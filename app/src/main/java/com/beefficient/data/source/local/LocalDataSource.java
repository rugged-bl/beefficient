package com.beefficient.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.beefficient.data.entity.DefaultTypes;
import com.beefficient.data.entity.Project;
import com.beefficient.data.entity.Task;
import com.beefficient.data.source.DataSource;
import com.beefficient.data.source.local.PersistenceContract.TaskEntry;
import com.beefficient.projects.Projects;
import com.beefficient.tasks.Tasks;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.beefficient.data.source.local.PersistenceContract.ProjectEntry;
import static com.beefficient.util.Objects.requireNonNull;

/**
 * Concrete implementation of a data source as a db
 */
public class LocalDataSource implements DataSource {

    public static final String TAG = "LocalDataSource";
    private static LocalDataSource INSTANCE;

    private final BriteDatabase databaseHelper;

    private Func1<Cursor, Task> taskMapperFunction;
    private Func1<Cursor, Project> projectMapperFunction;

    // Prevent direct instantiation
    private LocalDataSource(@NonNull Context context) {
        requireNonNull(context);

        DbHelper dbHelper = new DbHelper(context);
        SqlBrite sqlBrite = SqlBrite.create();
        databaseHelper = sqlBrite.wrapDatabaseHelper(dbHelper, Schedulers.io());

        taskMapperFunction = c -> {
            String taskId = c.getString(c.getColumnIndexOrThrow(TaskEntry.Column._id.name()));
            String title = c.getString(c.getColumnIndexOrThrow(TaskEntry.Column.title.name()));
            long time = c.getLong(c.getColumnIndexOrThrow(TaskEntry.Column.due_date.name()));
            int priorityIndex = c.getInt(c.getColumnIndexOrThrow(TaskEntry.Column.priority.name()));

            String projectId =
                    c.getString(c.getColumnIndexOrThrow(TaskEntry.Column.project_id.name()));
            String description =
                    c.getString(c.getColumnIndexOrThrow(TaskEntry.Column.description.name()));
            boolean completed =
                    c.getInt(c.getColumnIndexOrThrow(TaskEntry.Column.completed.name())) == 1;
            boolean withTime =
                    c.getInt(c.getColumnIndexOrThrow(TaskEntry.Column.with_time.name())) == 1;

            Task.Priority priority = Tasks.getPriorityFromIndex(priorityIndex);

            return new Task.Builder(title, taskId)
                    .setProjectId(projectId)
                    .setDescription(description)
                    .setCompleted(completed)
                    .setTime(time)
                    .setWithTime(withTime)
                    .setPriority(priority)
                    .build();
        };

        projectMapperFunction = c -> {
            String projectId = c.getString(c.getColumnIndexOrThrow(ProjectEntry.Column._id.name()));
            String name = c.getString(c.getColumnIndexOrThrow(ProjectEntry.Column.name.name()));
            int color = c.getInt(c.getColumnIndexOrThrow(ProjectEntry.Column.color.name()));

            return new Project(name, Projects.getColorFromIndex(color), projectId);
        };
        saveProject(DefaultTypes.PROJECT);
        /*getTasks()
                .subscribe(query -> {
                    for (Task task1 : query) {
                        Log.d("Test", task1.getId());
                    }
                });*/
    }

    public static LocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new LocalDataSource(context);
        }

        return INSTANCE;
    }

    @Override
    public Observable<List<Task>> getTasks() {
        Log.d(TAG, "getTasks");
        String sql = String.format("SELECT * FROM %s", TaskEntry.TABLE_NAME);

        return databaseHelper.createQuery(TaskEntry.TABLE_NAME, sql)
                .mapToList(taskMapperFunction);
    }

    @Override
    public Observable<Task> getTask(@NonNull String taskId) {
        Log.d(TAG, "getTask " + taskId);
        String sql = String.format("SELECT * FROM %s WHERE %s LIKE ?",
                TaskEntry.TABLE_NAME, TaskEntry.Column._id);

        return databaseHelper.createQuery(TaskEntry.TABLE_NAME, sql, taskId)
                .mapToOneOrDefault(taskMapperFunction, null);
    }

    @Override
    public void saveTask(@NonNull Task task) {
        Log.d(TAG, "saveTask " + task.getId());
        requireNonNull(task);

        ContentValues values = new ContentValues();
        values.put(TaskEntry.Column._id.name(), task.getId());
        values.put(TaskEntry.Column.project_id.name(), task.getProjectId());
        values.put(TaskEntry.Column.completed.name(), task.isCompleted());
        values.put(TaskEntry.Column.title.name(), task.getTitle());
        values.put(TaskEntry.Column.description.name(), task.getDescription());
        values.put(TaskEntry.Column.priority.name(), task.getPriority().ordinal());
        values.put(TaskEntry.Column.due_date.name(), task.getTime());
        values.put(TaskEntry.Column.with_time.name(), task.isWithTime());

        Observable<Task> taskObservable = getTask(task.getId());
        Subscription subscription = taskObservable.subscribe(t -> {
            if (t == null) {
                taskObservable.ignoreElements();
                databaseHelper.insert(TaskEntry.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE);
            } else {
                String selection = TaskEntry.Column._id + " = ?";
                String arg = t.getId();

                taskObservable.ignoreElements();
                databaseHelper.update(TaskEntry.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE, selection, arg);
            }
        }, Throwable::printStackTrace);

    }

    private void setTaskCompleted(@NonNull String taskId, boolean completed) {
        ContentValues values = new ContentValues();
        values.put(TaskEntry.Column.completed.name(), completed);

        String selection = TaskEntry.Column._id + " = ?";
        databaseHelper.update(TaskEntry.TABLE_NAME, values, selection, taskId);
    }

    @Override
    public void completeTask(@NonNull Task task) {
        completeTask(task.getId());
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        Log.d(TAG, "completeTask");
        setTaskCompleted(taskId, true);
    }

    @Override
    public void activateTask(@NonNull Task task) {
        activateTask(task.getId());
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        Log.d(TAG, "activateTask");
        setTaskCompleted(taskId, false);
    }

    @Override
    public void clearCompletedTasks() {
        Log.d(TAG, "clearCompletedTasks");
        String selection = TaskEntry.Column.completed + " = 1";
        databaseHelper.delete(TaskEntry.TABLE_NAME, selection);
    }

    @Override
    public void refreshTasks() {
        // Not required because the {@link DataRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    @Override
    public void deleteAllTasks() {
        Log.d(TAG, "deleteAllTasks");
        databaseHelper.delete(TaskEntry.TABLE_NAME, null);
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        Log.d(TAG, "deleteTask");
        String selection = TaskEntry.Column._id + " = ?";
        databaseHelper.delete(TaskEntry.TABLE_NAME, selection, taskId);
    }

    @Override
    public Observable<List<Project>> getProjects() {
        Log.d(TAG, "getProjects");
        String sql = String.format("SELECT * FROM %s", ProjectEntry.TABLE_NAME);

        return databaseHelper.createQuery(ProjectEntry.TABLE_NAME, sql)
                .mapToList(projectMapperFunction);
    }

    @Override
    public Observable<Project> getProject(@NonNull String projectId) {
        Log.d(TAG, "getProject " + projectId);
        String sql = String.format("SELECT * FROM %s WHERE %s = ?",
                ProjectEntry.TABLE_NAME, ProjectEntry.Column._id);

        return databaseHelper.createQuery(ProjectEntry.TABLE_NAME, sql, projectId)
                .mapToOneOrDefault(projectMapperFunction, null);
    }

    @Override
    public void saveProject(@NonNull Project project) {
        Log.d(TAG, "saveProject " + project.getId());
        requireNonNull(project);

        ContentValues values = new ContentValues();
        values.put(ProjectEntry.Column._id.name(), project.getId());
        values.put(ProjectEntry.Column.name.name(), project.getName());
        values.put(ProjectEntry.Column.color.name(), project.getColor().ordinal());

        Observable<Project> projectObservable = getProject(project.getId());
        Subscription subscription = projectObservable.subscribe(p -> {
            if (p == null) {
                projectObservable.ignoreElements();
                databaseHelper.insert(ProjectEntry.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE);
            } else {
                String selection = ProjectEntry.Column._id + " = ?";
                String[] selectionArgs = {p.getId()};

                projectObservable.ignoreElements();
                databaseHelper.update(ProjectEntry.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE, selection, selectionArgs);
            }
        }, Throwable::printStackTrace);
    }

    @Override
    public void refreshProjects() {
    }

    @Override
    public void deleteAllProjects() {
        Log.d(TAG, "deleteAllProjects");
        databaseHelper.delete(ProjectEntry.TABLE_NAME, null);
    }

    @Override
    public void deleteProject(@NonNull String projectId) {
        Log.d(TAG, "deleteProject " + projectId);
        String selection = ProjectEntry.Column._id + " = ?";
        databaseHelper.delete(ProjectEntry.TABLE_NAME, selection, projectId);
    }
}

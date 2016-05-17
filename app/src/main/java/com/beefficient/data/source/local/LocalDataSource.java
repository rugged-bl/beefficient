package com.beefficient.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.beefficient.data.entity.Project;
import com.beefficient.data.entity.Task;
import com.beefficient.data.source.DataSource;
import com.beefficient.data.source.local.PersistenceContract.TaskEntry;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.beefficient.data.source.local.PersistenceContract.ProjectEntry;
import static com.beefficient.util.Objects.requireNonNull;

/**
 * Concrete implementation of a data source as a db
 */
public class LocalDataSource implements DataSource {

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
            String itemId = c.getString(c.getColumnIndexOrThrow(TaskEntry.Column._id.name()));
            String projectId = c.getString(c.getColumnIndexOrThrow(TaskEntry.Column.project_id.name()));
            String title = c.getString(c.getColumnIndexOrThrow(TaskEntry.Column.title.name()));
            String description =
                    c.getString(c.getColumnIndexOrThrow(TaskEntry.Column.description.name()));
            boolean completed =
                    c.getInt(c.getColumnIndexOrThrow(TaskEntry.Column.completed.name())) == 1;
            boolean withTime =
                    c.getInt(c.getColumnIndexOrThrow(TaskEntry.Column.with_time.name())) == 1;
            long time = c.getLong(c.getColumnIndexOrThrow(TaskEntry.Column.due_date.name()));

            Task.Priority priority = Task.Priority.values()[
                    c.getInt(c.getColumnIndexOrThrow(TaskEntry.Column.priority.name()))];

            return new Task.Builder(title, itemId)
                    .setProjectId(projectId)
                    .setDescription(description)
                    .setCompleted(completed)
                    .setTime(time)
                    .setWithTime(withTime)
                    .setPriority(priority)
                    .build();
        };
        projectMapperFunction = c -> {
            String itemId = c.getString(c.getColumnIndexOrThrow(ProjectEntry.Column._id.name()));
            String name = c.getString(c.getColumnIndexOrThrow(ProjectEntry.Column.name.name()));
            int color = c.getInt(c.getColumnIndexOrThrow(ProjectEntry.Column.color.name()));

            return new Project(name, color, itemId);
        };
    }

    public static LocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new LocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public Observable<List<Task>> getTasks() {
        Log.d("LocalDataSource", "getTasks");
        String sql = String.format("SELECT %s FROM %s", TextUtils.join(",", TaskEntry.Column.values()), TaskEntry.TABLE_NAME);
        return databaseHelper.createQuery(TaskEntry.TABLE_NAME, sql)
                .mapToList(taskMapperFunction);
    }

    @Override
    public Observable<Task> getTask(@NonNull String taskId) {
        String sql = String.format("SELECT %s FROM %s WHERE %s LIKE ?",
                TextUtils.join(",", TaskEntry.Column.values()), TaskEntry.TABLE_NAME,
                TaskEntry.Column._id);
        return databaseHelper.createQuery(TaskEntry.TABLE_NAME, sql, taskId)
                .mapToOneOrDefault(taskMapperFunction, null);
    }

    @Override
    public void saveTask(@NonNull Task task) {
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

        deleteTask(task.getId());
        databaseHelper.insert(TaskEntry.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    @Override
    public void completeTask(@NonNull Task task) {
        completeTask(task.getId());
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        ContentValues values = new ContentValues();
        values.put(TaskEntry.Column.completed.name(), true);

        String selection = TaskEntry.Column._id + " LIKE ?";
        String[] selectionArgs = {taskId};
        databaseHelper.update(TaskEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    @Override
    public void activateTask(@NonNull Task task) {
        activateTask(task.getId());
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        ContentValues values = new ContentValues();
        values.put(TaskEntry.Column.completed.name(), false);

        String selection = TaskEntry.Column._id + " LIKE ?";
        String[] selectionArgs = {taskId};
        databaseHelper.update(TaskEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    @Override
    public void clearCompletedTasks() {
        String selection = TaskEntry.Column.completed + " LIKE ?";
        String[] selectionArgs = {"1"};
        databaseHelper.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public void refreshTasks() {
        // Not required because the {@link DataRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    @Override
    public void deleteAllTasks() {
        databaseHelper.delete(TaskEntry.TABLE_NAME, null);
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        String selection = TaskEntry.Column._id + " LIKE ?";
        String[] selectionArgs = {taskId};
        databaseHelper.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public Observable<List<Project>> getProjects() {
        String sql = String.format("SELECT %s FROM %s",
                TextUtils.join(",", ProjectEntry.Column.values()), ProjectEntry.TABLE_NAME);

        return databaseHelper.createQuery(ProjectEntry.TABLE_NAME, sql)
                .mapToList(projectMapperFunction);
    }

    @Override
    public Observable<Project> getProject(@NonNull String projectId) {
        String sql = String.format("SELECT %s FROM %s WHERE %s LIKE ?",
                TextUtils.join(",", ProjectEntry.Column.values()), ProjectEntry.TABLE_NAME,
                ProjectEntry.Column._id);

        return databaseHelper.createQuery(ProjectEntry.TABLE_NAME, sql, projectId)
                .mapToOneOrDefault(projectMapperFunction, null);
    }

    @Override
    public void saveProject(@NonNull Project project) {
        requireNonNull(project);
        ContentValues values = new ContentValues();
        values.put(ProjectEntry.Column._id.name(), project.getId());
        values.put(ProjectEntry.Column.name.name(), project.getName());
        values.put(ProjectEntry.Column.color.name(), project.getColor());

        deleteProject(project.getId());
        databaseHelper.insert(ProjectEntry.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    @Override
    public void refreshProjects() {
        //
    }

    @Override
    public void deleteAllProjects() {
        databaseHelper.delete(ProjectEntry.TABLE_NAME, null);
    }

    @Override
    public void deleteProject(@NonNull String projectId) {
        String selection = ProjectEntry.Column._id + " LIKE ?";
        String[] selectionArgs = {projectId};
        databaseHelper.delete(ProjectEntry.TABLE_NAME, selection, selectionArgs);
    }
}

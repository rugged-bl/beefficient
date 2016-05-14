package com.beefficient.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.beefficient.data.entity.Project;
import com.beefficient.data.entity.Task;
import com.beefficient.data.source.TasksDataSource;
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
 * Concrete implementation of a data source as a db.
 */
public class LocalDataSource implements TasksDataSource {

    private static LocalDataSource INSTANCE;
    private final BriteDatabase databaseHelper;
    private Func1<Cursor, Task> taskMapperFunction;
    private Func1<Cursor, Project> projectMapperFunction;

    // Prevent direct instantiation.
    private LocalDataSource(@NonNull Context context) {
        requireNonNull(context);
        DbHelper dbHelper = new DbHelper(context);
        SqlBrite sqlBrite = SqlBrite.create();
        databaseHelper = sqlBrite.wrapDatabaseHelper(dbHelper, Schedulers.io());
        taskMapperFunction = c -> {
            String itemId = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_ENTRY_ID));
            String title = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TITLE));
            String description =
                    c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_DESCRIPTION));
            boolean completed =
                    c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_COMPLETED)) == 1;
            boolean withTime =
                    c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_WITH_TIME)) == 1;
            long time = c.getLong(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_DUE_DATE));

            Task.Priority priority = Task.Priority.values()[
                    c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_PRIORITY))];

            return new Task.Builder(title, itemId)
                    .setDescription(description)
                    .setCompleted(completed)
                    .setTime(time)
                    .setWithTime(withTime)
                    .setPriority(priority)
                    .build();
        };
        projectMapperFunction = c -> {
            String itemId = c.getString(c.getColumnIndexOrThrow(ProjectEntry._ID));
            String name = c.getString(c.getColumnIndexOrThrow(ProjectEntry.COLUMN_NAME_NAME));
            int color = c.getInt(c.getColumnIndexOrThrow(ProjectEntry.COLUMN_NAME_COLOR));

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
        String[] projection = {
                TaskEntry.COLUMN_NAME_ENTRY_ID,
                TaskEntry.COLUMN_NAME_TITLE,
                TaskEntry.COLUMN_NAME_DESCRIPTION,
                TaskEntry.COLUMN_NAME_COMPLETED
        };
        String sql = String.format("SELECT %s FROM %s", TextUtils.join(",", projection), TaskEntry.TABLE_NAME);
        return databaseHelper.createQuery(TaskEntry.TABLE_NAME, sql)
                .mapToList(taskMapperFunction);
    }

    @Override
    public Observable<Task> getTask(@NonNull String taskId) {
        String[] projection = {
                TaskEntry.COLUMN_NAME_ENTRY_ID,
                TaskEntry.COLUMN_NAME_TITLE,
                TaskEntry.COLUMN_NAME_DESCRIPTION,
                TaskEntry.COLUMN_NAME_COMPLETED
        };
        String sql = String.format("SELECT %s FROM %s WHERE %s LIKE ?",
                TextUtils.join(",", projection), TaskEntry.TABLE_NAME, TaskEntry.COLUMN_NAME_ENTRY_ID);
        return databaseHelper.createQuery(TaskEntry.TABLE_NAME, sql, taskId)
                .mapToOneOrDefault(taskMapperFunction, null);
    }

    @Override
    public void saveTask(@NonNull Task task) {
        requireNonNull(task);
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_ENTRY_ID, task.getId());
        values.put(TaskEntry.COLUMN_NAME_TITLE, task.getTitle());
        values.put(TaskEntry.COLUMN_NAME_DESCRIPTION, task.getDescription());
        values.put(TaskEntry.COLUMN_NAME_COMPLETED, task.isCompleted());
        databaseHelper.insert(TaskEntry.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    @Override
    public void completeTask(@NonNull Task task) {
        completeTask(task.getId());
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_COMPLETED, true);

        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
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
        values.put(TaskEntry.COLUMN_NAME_COMPLETED, false);

        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {taskId};
        databaseHelper.update(TaskEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    @Override
    public void clearCompletedTasks() {
        String selection = TaskEntry.COLUMN_NAME_COMPLETED + " LIKE ?";
        String[] selectionArgs = {"1"};
        databaseHelper.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public void refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    @Override
    public void deleteAllTasks() {
        databaseHelper.delete(TaskEntry.TABLE_NAME, null);
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {taskId};
        databaseHelper.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public Observable<List<Project>> getProjects() {
        String[] projection = {
                ProjectEntry._ID,
                ProjectEntry.COLUMN_NAME_NAME,
                ProjectEntry.COLUMN_NAME_COLOR
        };
        String sql = String.format("SELECT %s FROM %s", TextUtils.join(",", projection), ProjectEntry.TABLE_NAME);
        return databaseHelper.createQuery(ProjectEntry.TABLE_NAME, sql)
                .mapToList(projectMapperFunction);
    }

    @Override
    public Observable<Project> getProject(@NonNull String projectId) {
        String[] projection = {
                ProjectEntry._ID,
                ProjectEntry.COLUMN_NAME_NAME,
                ProjectEntry.COLUMN_NAME_COLOR
        };
        String sql = String.format("SELECT %s FROM %s WHERE %s LIKE ?",
                TextUtils.join(",", projection), ProjectEntry.TABLE_NAME, ProjectEntry._ID);
        return databaseHelper.createQuery(ProjectEntry.TABLE_NAME, sql, projectId)
                .mapToOneOrDefault(projectMapperFunction, null);
    }

    @Override
    public void saveProject(@NonNull Project project) {
        requireNonNull(project);
        ContentValues values = new ContentValues();
        values.put(ProjectEntry._ID, project.getId());
        values.put(ProjectEntry.COLUMN_NAME_NAME, project.getName());
        values.put(ProjectEntry.COLUMN_NAME_COLOR, project.getColor());
        databaseHelper.insert(ProjectEntry.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    @Override
    public void refreshProjects() {
        //
    }

    @Override
    public void deleteProject(@NonNull String projectId) {
        String selection = ProjectEntry._ID + " LIKE ?";
        String[] selectionArgs = {projectId};
        databaseHelper.delete(ProjectEntry.TABLE_NAME, selection, selectionArgs);
    }
}

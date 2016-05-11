package com.beefficient.data.source;

import android.support.annotation.NonNull;

import com.beefficient.data.Task;

import static com.beefficient.util.Objects.requireNonNull;

import java.util.List;

import rx.Observable;

/**
 * Main entry point for accessing tasks data.
 * <p>
 */
public interface TasksDataSource {

    Observable<List<Task>> getTasks();

    Observable<Task> getTask(@NonNull String taskId);

    void saveTask(@NonNull Task task);

    void completeTask(@NonNull Task task);

    void completeTask(@NonNull String taskId);

    void activateTask(@NonNull Task task);

    void activateTask(@NonNull String taskId);

    void clearCompletedTasks();

    void refreshTasks();

    void deleteAllTasks();

    void deleteTask(@NonNull String taskId);
}

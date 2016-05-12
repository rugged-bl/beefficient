package com.beefficient.data.source;

import android.support.annotation.NonNull;

import com.beefficient.data.Project;
import com.beefficient.data.Task;

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

    Observable<List<Project>> getProjects();

    Observable<Project> getProject(@NonNull String projectId);

    void saveProject(@NonNull Project project);

    void refreshProjects();

    void deleteProject(@NonNull String projectId);
}

package com.beefficient.data.source.remote;

import android.support.annotation.NonNull;

import com.beefficient.data.entity.Project;
import com.beefficient.data.entity.Task;
import com.beefficient.data.source.TasksDataSource;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;

/**
 * Implementation of the data source that adds a latency simulating network.
 */
public class RemoteDataSource implements TasksDataSource {

    private static RemoteDataSource INSTANCE;

    private static final int SERVICE_LATENCY_IN_MILLIS = 5000;

    private final static Map<String, Task> TASKS_SERVICE_DATA;

    static {
        TASKS_SERVICE_DATA = new LinkedHashMap<>(2);
        for (int i = 0; i < 10; i++) {
            addTask("Build tower in Pisa", "Ground looks good, no foundation work required.");
            addTask("Finish bridge in Tacoma", "Found awesome girders at half the cost!");
        }
    }

    public static RemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RemoteDataSource();
        }
        return INSTANCE;
    }

    // Prevent direct instantiation.
    private RemoteDataSource() {
    }

    private static void addTask(String title, String description) {
        Task newTask = new Task.Builder(title)
                .setDescription(description)
                .build();
        TASKS_SERVICE_DATA.put(newTask.getId(), newTask);
    }

    @Override
    public Observable<List<Task>> getTasks() {
        return Observable
                .from(TASKS_SERVICE_DATA.values())
                .delay(SERVICE_LATENCY_IN_MILLIS, TimeUnit.MILLISECONDS)
                .toList();
    }

    @Override
    public Observable<Task> getTask(@NonNull String taskId) {
        final Task task = TASKS_SERVICE_DATA.get(taskId);
        if (task != null) {
            return Observable.just(task).delay(SERVICE_LATENCY_IN_MILLIS, TimeUnit.MILLISECONDS);
        } else {
            return Observable.empty();
        }
    }

    @Override
    public void saveTask(@NonNull Task task) {
        TASKS_SERVICE_DATA.put(task.getId(), task);
    }

    @Override
    public void completeTask(@NonNull Task task) {
        Task completedTask = new Task.Builder(task.getTitle(), task.getId())
                .setDescription(task.getDescription())
                .setCompleted(true)
                .build();
        TASKS_SERVICE_DATA.put(task.getId(), completedTask);
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    @Override
    public void activateTask(@NonNull Task task) {
        Task activeTask = new Task.Builder(task.getTitle(), task.getId())
                .setDescription(task.getDescription())
                .build();
        TASKS_SERVICE_DATA.put(task.getId(), activeTask);
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        // Not required for the remote data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    @Override
    public void clearCompletedTasks() {
        Iterator<Map.Entry<String, Task>> it = TASKS_SERVICE_DATA.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Task> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }

    @Override
    public void refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    @Override
    public void deleteAllTasks() {
        TASKS_SERVICE_DATA.clear();
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        TASKS_SERVICE_DATA.remove(taskId);
    }

    @Override
    public Observable<List<Project>> getProjects() {
        return null;
    }

    @Override
    public Observable<Project> getProject(@NonNull String projectId) {
        return null;
    }

    @Override
    public void saveProject(@NonNull Project project) {

    }

    @Override
    public void refreshProjects() {

    }

    @Override
    public void deleteProject(@NonNull String projectId) {

    }
}

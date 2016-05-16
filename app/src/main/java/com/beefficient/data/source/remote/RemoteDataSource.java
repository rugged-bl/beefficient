package com.beefficient.data.source.remote;

import android.support.annotation.NonNull;
import android.util.Log;

import com.beefficient.data.entity.Project;
import com.beefficient.data.entity.Task;
import com.beefficient.data.source.DataSource;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;

/**
 * Implementation of the data source that adds a latency simulating network.
 */
public class RemoteDataSource implements DataSource {

    private static RemoteDataSource INSTANCE;

    private static final int SERVICE_LATENCY_IN_MILLIS = 5000;

    private final static Map<String, Task> TASKS_SERVICE_DATA;
    private final static Map<String, Project> PROJECTS_SERVICE_DATA;

    static {
        TASKS_SERVICE_DATA = new LinkedHashMap<>(2);
        PROJECTS_SERVICE_DATA = new LinkedHashMap<>(2);

        for (int i = 0; i < 1; i++) {
            Project project1 = new Project("Project One", 0xffffff, "prid1");
            Project project2 = new Project("Project Two", 0xffffff, "prid2");
            Project project3 = new Project("Project Three", 0xffffff, "prid3");
            Project project4 = new Project("Project Four", 0xffffff, "prid4");
            addProject(project1);
            addProject(project2);
            addProject(project3);
            addProject(project4);

            for (int j = 0; j < 2; j++) {
                addTask("Title One " + j, "Desc One", project1, "taid1" + j);
                addTask("Title Two " + j, "Desc Two", project2, "taid2" + j);
                addTask("Title Three " + j, "Desc Three", project3, "taid3" + j);
                addTask("Title Four " + j, "Desc Four", project4, "taid4" + j);
            }
        }
    }

    public static RemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RemoteDataSource();
        }
        return INSTANCE;
    }

    // Prevent direct instantiation
    private RemoteDataSource() {
    }

    private static void addTask(String title, String description, Project project, String id) {
        Task newTask = new Task.Builder(title, id)
                .setDescription(description)
                .setProject(project)
                .build();
        TASKS_SERVICE_DATA.put(newTask.getId(), newTask);
    }

    @Override
    public Observable<List<Task>> getTasks() {
        Log.d("LocalDataSource", "getTasks");
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
        // Not required for the remote data source because the {@link DataRepository} handles
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
        // Not required for the remote data source because the {@link DataRepository} handles
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
        // Not required because the {@link DataRepository} handles the logic of refreshing the
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

    private static void addProject(Project project) {
        PROJECTS_SERVICE_DATA.put(project.getId(), project);
    }

    @Override
    public Observable<List<Project>> getProjects() {
        return Observable
                .from(PROJECTS_SERVICE_DATA.values())
                .delay(SERVICE_LATENCY_IN_MILLIS - 2000, TimeUnit.MILLISECONDS)
                .toList();
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
    public void deleteAllProjects()
    {
        PROJECTS_SERVICE_DATA.clear();
    }

    @Override
    public void deleteProject(@NonNull String projectId) {

    }
}

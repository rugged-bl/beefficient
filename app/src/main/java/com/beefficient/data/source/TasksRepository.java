package com.beefficient.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.beefficient.data.entity.Project;
import com.beefficient.data.entity.Task;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;

import static com.beefficient.util.Objects.requireNonNull;

/**
 * Concrete implementation to load tasks from the data sources into a cache.
 */
// TODO: придумать, куда засунуть проекты и метки
public class TasksRepository implements TasksDataSource {

    private static TasksRepository INSTANCE = null;

    private final TasksDataSource remoteDataSource;
    private final TasksDataSource localDataSource;

    Map<String, Project> cachedProjects;
    Map<String, Task> cachedTasks;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested.
     */
    boolean cacheIsDirty = false;

    // Prevent direct instantiation
    private TasksRepository(@NonNull TasksDataSource tasksRemoteDataSource,
                            @NonNull TasksDataSource tasksLocalDataSource) {
        remoteDataSource = requireNonNull(tasksRemoteDataSource);
        localDataSource = requireNonNull(tasksLocalDataSource);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param tasksRemoteDataSource the backend data source
     * @param tasksLocalDataSource  the device storage data source
     * @return the {@link TasksRepository} instance
     */
    public static TasksRepository getInstance(TasksDataSource tasksRemoteDataSource,
                                              TasksDataSource tasksLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new TasksRepository(tasksRemoteDataSource, tasksLocalDataSource);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(TasksDataSource, TasksDataSource)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    /**
     * Gets tasks from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     */
    @Override
    public Observable<List<Task>> getTasks() {
        // Respond immediately with cache if available and not dirty
        if (cachedTasks != null && !cacheIsDirty) {
            return Observable.from(cachedTasks.values()).toList();
        } else if (cachedTasks == null) {
            cachedTasks = new LinkedHashMap<>();
        }

        Observable<List<Task>> remoteTasks = remoteDataSource
                .getTasks()
                .flatMap(Observable::from)
                .doOnNext(task -> {
                    localDataSource.saveTask(task);
                    cachedTasks.put(task.getId(), task);
                })
                .toList()
                .doOnCompleted(() -> cacheIsDirty = false);
        if (cacheIsDirty) {
            return remoteTasks;
        } else {
            // Query the local storage if available. If not, query the network.
            Observable<List<Task>> localTasks = localDataSource.getTasks();
            return Observable.concat(localTasks, remoteTasks).first();
        }
    }

    @Override
    public Observable<List<Project>> getProjects() {
        // Respond immediately with cache if available and not dirty
        if (cachedProjects != null && !cacheIsDirty) {
            return Observable.from(cachedProjects.values()).toList();
        } else if (cachedProjects == null) {
            cachedProjects = new LinkedHashMap<>();
        }

        Observable<List<Project>> remoteProjects = remoteDataSource
                .getProjects()
                .flatMap(Observable::from)
                .doOnNext(project -> {
                    localDataSource.saveProject(project);
                    cachedProjects.put(project.getId(), project);
                })
                .toList()
                .doOnCompleted(() -> cacheIsDirty = false);
        if (cacheIsDirty) {
            return remoteProjects;
        } else {
            // Query the local storage if available. If not, query the network.
            Observable<List<Project>> localProjects = localDataSource.getProjects();
            return Observable.concat(localProjects, remoteProjects).first();
        }
    }

    @Override
    public Observable<Project> getProject(@NonNull String projectId) {
        requireNonNull(projectId);

        final Project cachedProject = getProjectWithId(projectId);

        // Respond immediately with cache if available
        if (cachedProject != null) {
            return Observable.just(cachedProject);
        }

        // Load from server/persisted if needed.
        // http://blog.danlew.net/2015/06/22/loading-data-from-multiple-sources-with-rxjava/
        // Is the task in the local data source? If not, query the network.
        Observable<Project> localProject = localDataSource
                .getProject(projectId)
                .doOnNext(project -> cachedProjects.put(projectId, project));
        Observable<Project> remoteProject = remoteDataSource
                .getProject(projectId)
                .doOnNext(project -> {
                    localDataSource.saveProject(project);
                    cachedProjects.put(project.getId(), project);
                });

        return Observable.concat(localProject, remoteProject).first();
    }

    @Override
    public void saveProject(@NonNull Project project) {
        requireNonNull(project);
        remoteDataSource.saveProject(project);
        localDataSource.saveProject(project);

        // Do in memory cache update to keep the app UI up to date
        if (cachedProjects == null) {
            cachedProjects = new LinkedHashMap<>();
        }
        cachedProjects.put(project.getId(), project);
    }

    @Override
    public void refreshProjects() {
        cacheIsDirty = true;
    }

    @Override
    public void deleteProject(@NonNull String projectId) {
        remoteDataSource.deleteProject(requireNonNull(projectId));
        localDataSource.deleteProject(requireNonNull(projectId));

        cachedProjects.remove(projectId);
    }

    @Override
    public void saveTask(@NonNull Task task) {
        requireNonNull(task);
        remoteDataSource.saveTask(task);
        localDataSource.saveTask(task);

        // Do in memory cache update to keep the app UI up to date
        if (cachedTasks == null) {
            cachedTasks = new LinkedHashMap<>();
        }
        cachedTasks.put(task.getId(), task);
    }

    @Override
    public void completeTask(@NonNull Task task) {
        requireNonNull(task);
        remoteDataSource.completeTask(task);
        localDataSource.completeTask(task);

        Task completedTask = new Task.Builder(task.getTitle(), task.getId())
                .setDescription(task.getDescription())
                .setCompleted(true)
                .build();

        // Do in memory cache update to keep the app UI up to date
        if (cachedTasks == null) {
            cachedTasks = new LinkedHashMap<>();
        }
        cachedTasks.put(task.getId(), completedTask);
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        requireNonNull(taskId);
        Task taskWithId = getTaskWithId(taskId);
        if (taskWithId != null) {
            completeTask(taskWithId);
        }
    }

    @Override
    public void activateTask(@NonNull Task task) {
        requireNonNull(task);
        remoteDataSource.activateTask(task);
        localDataSource.activateTask(task);

        Task activeTask = new Task.Builder(task.getTitle(), task.getId())
                .setDescription(task.getDescription())
                .build();

        // Do in memory cache update to keep the app UI up to date
        if (cachedTasks == null) {
            cachedTasks = new LinkedHashMap<>();
        }
        cachedTasks.put(task.getId(), activeTask);
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        requireNonNull(taskId);
        Task taskWithId = getTaskWithId(taskId);
        if (taskWithId != null) {
            activateTask(taskWithId);
        }
    }

    @Override
    public void clearCompletedTasks() {
        remoteDataSource.clearCompletedTasks();
        localDataSource.clearCompletedTasks();

        // Do in memory cache update to keep the app UI up to date
        if (cachedTasks == null) {
            cachedTasks = new LinkedHashMap<>();
        }
        Iterator<Map.Entry<String, Task>> it = cachedTasks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Task> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }

    /**
     * Gets tasks from local data source (sqlite) unless the table is new or empty. In that case it
     * uses the network data source. This is done to simplify the sample.
     */
    @Override
    public Observable<Task> getTask(@NonNull final String taskId) {
        requireNonNull(taskId);

        final Task cachedTask = getTaskWithId(taskId);

        // Respond immediately with cache if available
        if (cachedTask != null) {
            return Observable.just(cachedTask);
        }

        // Load from server/persisted if needed.
        // http://blog.danlew.net/2015/06/22/loading-data-from-multiple-sources-with-rxjava/
        // Is the task in the local data source? If not, query the network.
        Observable<Task> localTask = localDataSource
                .getTask(taskId)
                .doOnNext(task -> cachedTasks.put(taskId, task));
        Observable<Task> remoteTask = remoteDataSource
                .getTask(taskId)
                .doOnNext(task -> {
                    localDataSource.saveTask(task);
                    cachedTasks.put(task.getId(), task);
                });

        return Observable.concat(localTask, remoteTask).first();
    }

    @Override
    public void refreshTasks() {
        cacheIsDirty = true;
    }

    @Override
    public void deleteAllTasks() {
        remoteDataSource.deleteAllTasks();
        localDataSource.deleteAllTasks();

        if (cachedTasks == null) {
            cachedTasks = new LinkedHashMap<>();
        }
        cachedTasks.clear();
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        remoteDataSource.deleteTask(requireNonNull(taskId));
        localDataSource.deleteTask(requireNonNull(taskId));

        cachedTasks.remove(taskId);
    }

    @Nullable
    private Task getTaskWithId(@NonNull String id) {
        requireNonNull(id);
        if (cachedTasks == null || cachedTasks.isEmpty()) {
            return null;
        } else {
            return cachedTasks.get(id);
        }
    }

    @Nullable
    private Project getProjectWithId(@NonNull String id) {
        requireNonNull(id);
        if (cachedProjects == null || cachedProjects.isEmpty()) {
            return null;
        } else {
            return cachedProjects.get(id);
        }
    }
}

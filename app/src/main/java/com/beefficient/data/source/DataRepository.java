package com.beefficient.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.beefficient.data.entity.Project;
import com.beefficient.data.entity.Task;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;

import static com.beefficient.util.Objects.requireNonNull;

/**
 * Concrete implementation to load tasks from the data sources into a cache.
 */
public class DataRepository implements DataSource {

    private static DataRepository INSTANCE = null;

    private final DataSource remoteDataSource;
    private final DataSource localDataSource;

    Map<String, Project> cachedProjects;
    Map<String, Task> cachedTasks;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested.
     */
    AtomicBoolean forceCache = new AtomicBoolean();

    // Prevent direct instantiation
    private DataRepository(@NonNull DataSource tasksRemoteDataSource,
                           @NonNull DataSource tasksLocalDataSource) {
        remoteDataSource = requireNonNull(tasksRemoteDataSource);
        localDataSource = requireNonNull(tasksLocalDataSource);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param tasksRemoteDataSource the backend data source
     * @param tasksLocalDataSource  the device storage data source
     * @return the {@link DataRepository} instance
     */
    public static DataRepository getInstance(DataSource tasksRemoteDataSource,
                                             DataSource tasksLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new DataRepository(tasksRemoteDataSource, tasksLocalDataSource);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(DataSource, DataSource)} to create a new instance
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
        if (cachedTasks != null && !forceCache.get()) {
            return Observable.from(cachedTasks.values()).toList();
        } else {
            updateTasksCache();
        }

        Observable<List<Task>> remoteTasks = remoteDataSource
                .getTasks()
                .flatMap(Observable::from)
                .doOnNext(task -> {
                    localDataSource.saveTask(task);
                    cachedTasks.put(task.getId(), task);
                })
                .toList()
                .doOnCompleted(() -> forceCache.set(false));

        if (forceCache.get()) {
            return remoteTasks;
        } else {
            Observable<List<Task>> localTasks = localDataSource
                    .getTasks()
                    .doOnNext(tasks -> {
                        for (Task task : tasks) {
                            cachedTasks.put(task.getId(), task);
                        }
                    });
            return Observable.concat(localTasks, remoteTasks).first();
        }
    }

    @Override
    public Observable<List<Project>> getProjects() {
        // Respond immediately with cache if available and not dirty
        if (cachedProjects != null && !forceCache.get()) {
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
                .doOnCompleted(() -> forceCache.set(false));
        if (forceCache.get()) {
            //deleteAllTasks();
            //deleteAllProjects();
            /*localDataSource.deleteAllTasks();
            localDataSource.deleteAllProjects();*/
            return remoteProjects;
        } else {
            // Query the local storage if available. If not, query the network.
            Observable<List<Project>> localProjects = localDataSource
                    .getProjects()
                    .doOnNext(projects -> {
                        for (Project project : projects) {
                            cachedProjects.put(project.getId(), project);
                        }
                    });
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
        forceCache.set(true);
    }

    @Override
    public void deleteAllProjects() {
        remoteDataSource.deleteAllProjects();
        localDataSource.deleteAllProjects();

        if (cachedProjects == null) {
            cachedProjects = new LinkedHashMap<>();
        }

        cachedProjects.clear();
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

        updateTasksCache();
        cachedTasks.put(task.getId(), task);
    }

    private void updateTasksCache() {
        if (cachedTasks == null) {
            cachedTasks = new LinkedHashMap<>();
        }
    }

    @Override
    public void completeTask(@NonNull Task task) {
        requireNonNull(task);
        remoteDataSource.completeTask(task);
        localDataSource.completeTask(task);

        Task completedTask = new Task.Builder(task.getTitle(), task.getId())
                .setDescription(task.getDescription())
                .setProject(task.getProject())
                .setCompleted(true)
                .build();

        updateTasksCache();

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

        updateTasksCache();

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

        updateTasksCache();

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
        forceCache.set(true);
    }

    @Override
    public void deleteAllTasks() {
        remoteDataSource.deleteAllTasks();
        localDataSource.deleteAllTasks();

        updateTasksCache();
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

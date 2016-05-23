package com.beefficient.tasks;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.util.Log;

import com.beefficient.addedittask.AddEditTaskActivity;
import com.beefficient.data.entity.DefaultTypes;
import com.beefficient.data.entity.Project;
import com.beefficient.data.entity.Task;
import com.beefficient.data.source.DataRepository;
import com.beefficient.data.source.DataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.beefficient.util.Objects.requireNonNull;

public class TasksPresenter implements TasksContract.Presenter {

    private static final String TAG = "TasksPresenter";

    private final DataRepository dataRepository;
    private final TasksContract.View tasksView;

    private final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(100);
    private final ExecutorService executorService = new ThreadPoolExecutor(1, 8, 0L, TimeUnit.MILLISECONDS, queue);

    private TasksFilterType currentFiltering = TasksFilterType.ALL_TASKS;
    private TasksSortType currentSorting = TasksSortType.PROJECTS;

    // private boolean firstLoad = true;
    private CompositeSubscription subscriptions;

    public TasksPresenter(@NonNull DataRepository dataRepository, @NonNull TasksContract.View tasksView) {
        this.dataRepository = requireNonNull(dataRepository, "dataRepository cannot be null");
        this.tasksView = requireNonNull(tasksView, "tasksView cannot be null");
        this.subscriptions = new CompositeSubscription();
        tasksView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        loadTasks(false);
    }

    @Override
    public void unsubscribe() {
        subscriptions.clear();
    }

    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "result: " + requestCode + " " + resultCode + " " + data);
        String taskId = null;
        if (data != null) {
            taskId = data.getStringExtra(AddEditTaskActivity.EXTRA_TASK_ID);
        }

        if (taskId != null) {
            switch (requestCode) {
                case AddEditTaskActivity.REQUEST_ADD_TASK: {
                    if (Activity.RESULT_OK == resultCode) {
                        tasksView.showSavedMessage();
                        // TODO: scroll to added task
                    } else if (AddEditTaskActivity.RESULT_TASK_DELETED == resultCode) {
                        tasksView.showDeletedMessage();
                    }
                    break;
                }
                case AddEditTaskActivity.REQUEST_EDIT_TASK: {
                    if (Activity.RESULT_OK == resultCode) {
                        tasksView.showEditedMessage();
                        // TODO: scroll to edited task
                    } else if (AddEditTaskActivity.RESULT_TASK_DELETED == resultCode) {
                        tasksView.showDeletedMessage();
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void loadTasks(boolean forceUpdate) {
        loadTasks(forceUpdate, true);
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link DataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private void loadTasks(boolean forceUpdate, boolean showLoadingUI) {
        if (showLoadingUI) {
            tasksView.setLoadingIndicator(true);
        }
        if (forceUpdate) {
            dataRepository.refreshTasks();
        }

        Observable<List<Task>> tasks = dataRepository
                .getTasks()
                .flatMap(Observable::from)
                .filter(task -> {
                    switch (currentFiltering) {
                        case ACTIVE_TASKS:
                            return !task.isCompleted();
                        case COMPLETED_TASKS:
                            return task.isCompleted();
                        case ALL_TASKS:
                        default:
                            return true;
                    }
                }).toList();

        Observable<List<Project>> projects = dataRepository
                .getProjects()
                .flatMap(Observable::from)
                .filter(project -> {
                    switch (currentFiltering) {
                        default:
                            return true;
                    }
                }).toList();

        subscriptions.clear();
        Subscription subscription = Observable
                .zip(tasks, projects, Pair::new)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        // onNext
                        (Pair<List<Task>, List<Project>> pair) -> {
                            Log.d("TasksPresenter", "next");
                            processTasks(pair.first, pair.second);
                        },
                        // onError
                        throwable -> {
                            tasksView.showLoadingTasksError();
                            tasksView.setLoadingIndicator(false);
                            throwable.printStackTrace();
                        },
                        // onCompleted
                        () -> {
                            Log.d("TasksPresenter", "completed");
                            tasksView.setLoadingIndicator(false);
                        });

        Log.d("TasksPresenter", "subscribed");

        subscriptions.add(subscription);
    }

    private void processTasks(List<Task> tasks, List<Project> projects) {
        Log.d("TasksPresenter", "processTasks thread: " + Thread.currentThread().getName());
        if (projects.isEmpty()) {
            dataRepository.saveProject(DefaultTypes.PROJECT);
        }
        if (tasks.isEmpty()) {
            processEmptyTasks();
        } else {
            HashMap<Integer, TasksAdapter.SectionItem> sectionItems = new LinkedHashMap<>();
            ArrayList<TasksAdapter.TaskItem> taskItems = new ArrayList<>();

            TasksSort.requireAllTasksHaveProject(tasks, projects);

            switch (currentSorting) {
                case DATE:
                    TasksSort.Period.groupByDate(taskItems, sectionItems, tasks);
                    break;
                case PROJECTS:
                default:
                    TasksSort.groupByProject(taskItems, sectionItems, tasks, projects);
                    break;
            }

            // Show the list of tasks
            tasksView.showTasks(taskItems, sectionItems);
            // Set the filter label's text.
//            showFilterLabel();
        }
    }

//    private void showFilterLabel() {
//        switch (currentFiltering) {
//            case ACTIVE_TASKS:
//                tasksView.showActiveFilterLabel();
//                break;
//            case COMPLETED_TASKS:
//                tasksView.showCompletedFilterLabel();
//                break;
//            default:
//                tasksView.showAllFilterLabel();
//                break;
//        }
//    }

    private void processEmptyTasks() {
        switch (currentFiltering) {
            case ACTIVE_TASKS:
                tasksView.showNoActiveTasks();
                break;
            case COMPLETED_TASKS:
                tasksView.showNoCompletedTasks();
                break;
            default:
                tasksView.showNoTasks();
                break;
        }
    }

    @Override
    public void addNewTask() {
        tasksView.showAddTask();
    }

    @Override
    public void editTask(@NonNull Task task) {
        requireNonNull(task, "task cannot be null");
        tasksView.showEditTask(task.getId());
    }

    @Override
    public void completeTask(@NonNull Task task) {
        requireNonNull(task, "completedTask cannot be null");
        dataRepository.completeTask(task);
        tasksView.showTaskMarkedComplete();
        loadTasks(false, false);
    }

    @Override
    public void activateTask(@NonNull Task task) {
        requireNonNull(task, "activeTask cannot be null");
        dataRepository.activateTask(task);
        tasksView.showTaskMarkedActive();
        loadTasks(false, false);
    }

    @Override
    public void clearCompletedTasks() {
        dataRepository.clearCompletedTasks();
        tasksView.showCompletedTasksCleared();
        loadTasks(false, false);
    }

    @Override
    public void deleteAllData() {
        dataRepository.deleteAllTasks();
        dataRepository.deleteAllProjects();
    }

    /**
     * Sets the current task filtering type.
     *
     * @param requestType Can be {@link TasksFilterType#ALL_TASKS},
     *                    {@link TasksFilterType#COMPLETED_TASKS}, or
     *                    {@link TasksFilterType#ACTIVE_TASKS}
     */
    @Override
    public void setFiltering(TasksFilterType requestType) {
        currentFiltering = requestType;
    }

    @Override
    public TasksFilterType getFiltering() {
        return currentFiltering;
    }

    /**
     * Sets the current task filtering type.
     *
     * @param requestType Can be {@link TasksSortType#PROJECTS},
     *                    {@link TasksSortType#DATE}
     */
    @Override
    public void setSorting(TasksSortType requestType) {
        currentSorting = requestType;
    }

    @Override
    public TasksSortType getSorting() {
        return currentSorting;
    }
}

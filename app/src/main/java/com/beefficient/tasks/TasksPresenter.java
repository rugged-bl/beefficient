package com.beefficient.tasks;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.beefficient.data.Task;
import com.beefficient.data.source.TasksDataSource;
import com.beefficient.data.source.TasksRepository;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.beefficient.util.Objects.requireNonNull;

public class TasksPresenter implements TasksContract.Presenter {
    private final TasksRepository tasksRepository;

    private final TasksContract.View tasksView;

    private TasksFilterType mCurrentFiltering = TasksFilterType.ALL_TASKS;

    private boolean firstLoad = true;
    private CompositeSubscription subscriptions;

    public TasksPresenter(@NonNull TasksRepository tasksRepository, @NonNull TasksContract.View tasksView) {
        this.tasksRepository = requireNonNull(tasksRepository, "tasksRepository cannot be null");
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
    public void result(int requestCode, int resultCode) {
        // If a task was successfully added, show snackbar
        /*if (AddEditTaskActivity.REQUEST_ADD_TASK == requestCode && Activity.RESULT_OK == resultCode) {
            tasksView.showSuccessfullySavedMessage();
        }*/
    }

    @Override
    public void loadTasks(boolean forceUpdate) {
        // Simplification for sample: a network reload will be forced on first load.
        loadTasks(forceUpdate || firstLoad, true);
        firstLoad = false;
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link TasksDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private void loadTasks(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            tasksView.setLoadingIndicator(true);
        }
        if (forceUpdate) {
            tasksRepository.refreshTasks();
        }

        // The network request might be handled in a different thread so make sure Espresso knows
        // that the app is busy until the response is handled.
        //EspressoIdlingResource.increment(); // App is busy until further notice

        subscriptions.clear();
        Subscription subscription = tasksRepository
                .getTasks()
                .flatMap(Observable::from)
                .filter(task -> {
                    switch (mCurrentFiltering) {
                        case ACTIVE_TASKS:
                            //return task.isActive();
                            return true;
                        case COMPLETED_TASKS:
                            return task.isCompleted();
                        case ALL_TASKS:
                        default:
                            return true;
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Task>>() {
                    @Override
                    public void onCompleted() {
                        tasksView.setLoadingIndicator(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        tasksView.showLoadingTasksError();
                        tasksView.setLoadingIndicator(false);
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<Task> tasks) {
                        Log.d("TasksPresenter", "next");
                        processTasks(tasks);
                    }
                });
        
        subscriptions.add(subscription);
    }

    private void processTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            // Show a message indicating there are no tasks for that filter type.
            processEmptyTasks();
        } else {
            // Show the list of tasks
            tasksView.showTasks(tasks);
            // Set the filter label's text.
            showFilterLabel();
        }
    }

    private void showFilterLabel() {
        switch (mCurrentFiltering) {
            case ACTIVE_TASKS:
                tasksView.showActiveFilterLabel();
                break;
            case COMPLETED_TASKS:
                tasksView.showCompletedFilterLabel();
                break;
            default:
                tasksView.showAllFilterLabel();
                break;
        }
    }

    private void processEmptyTasks() {
        switch (mCurrentFiltering) {
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
    public void openTaskDetails(@NonNull Task requestedTask) {
        requireNonNull(requestedTask, "requestedTask cannot be null!");
        tasksView.showTaskDetailsUi(requestedTask.getId());
    }

    @Override
    public void completeTask(@NonNull Task completedTask) {
        requireNonNull(completedTask, "completedTask cannot be null!");
        tasksRepository.completeTask(completedTask);
        tasksView.showTaskMarkedComplete();
        loadTasks(false, false);
    }

    @Override
    public void activateTask(@NonNull Task activeTask) {
        requireNonNull(activeTask, "activeTask cannot be null!");
        tasksRepository.activateTask(activeTask);
        tasksView.showTaskMarkedActive();
        loadTasks(false, false);
    }

    @Override
    public void clearCompletedTasks() {
        tasksRepository.clearCompletedTasks();
        tasksView.showCompletedTasksCleared();
        loadTasks(false, false);
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
        mCurrentFiltering = requestType;
    }

    @Override
    public TasksFilterType getFiltering() {
        return mCurrentFiltering;
    }
}

package com.beefficient.tasks;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.util.Log;

import com.beefficient.data.entity.Project;
import com.beefficient.data.entity.Task;
import com.beefficient.data.source.DataRepository;
import com.beefficient.data.source.DataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.beefficient.util.Objects.requireNonNull;

public class TasksPresenter implements TasksContract.Presenter {
    private final DataRepository dataRepository;

    private final TasksContract.View tasksView;

    private TasksFilterType currentFiltering = TasksFilterType.ALL_TASKS;

    private boolean firstLoad = true;
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
                            //return task.isActive();
                            return true;
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
                .doOnCompleted(() -> tasksView.setLoadingIndicator(false))
                .doOnError(throwable -> {
                    tasksView.showLoadingTasksError();
                    tasksView.setLoadingIndicator(false);
                    throwable.printStackTrace();
                })
                .doOnNext(pair -> {
                    Log.d("TasksPresenter", "next");
                    processTasks(pair);
                })
                .subscribe();
                /*.subscribe(/*new Observer<List<Task>>()* new Observer<Pair<List<Task>, List<Project>>>() {
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
                    public void onNext(Pair<List<Task>, List<Project>> pair) {
                        Log.d("TasksPresenter", "next");
                        processTasks(tasks);
                    }
                });*/

        subscriptions.add(subscription);
    }

    private void processTasks(Pair<List<Task>, List<Project>> pair) {
        if (pair.first.isEmpty() || pair.second.isEmpty()) {
            // Show a message indicating there are no tasks for that filter type.
            processEmptyTasks();
        } else {
            //List<Task> sortedTasks = new ArrayList<>();
            ArrayList<TasksAdapter.SectionItem> testSectionItems = new ArrayList<>();
            ArrayList<TasksAdapter.TaskItem> taskItems = new ArrayList<>();
            HashMap<Integer, Integer> sortLinks = new HashMap<>();

            for (int projectIndex = 0, k = 0; projectIndex < pair.second.size(); projectIndex++) {
                TasksAdapter.SectionItem sectionItem =
                        new TasksAdapter.SectionItem(pair.second.get(projectIndex).getName());
                testSectionItems.add(sectionItem);

                sortLinks.put(k++, projectIndex);

                for (Task task : pair.first) {
                    if (task.getProject().getId().equals(pair.second.get(projectIndex).getId())) { //TODO: Add not-null check
                        //sortedTasks.add(pair.first.get(j));
                        taskItems.add(new TasksAdapter.TaskItem(task, null));
                        k++;
                    }
                }
            }

            // Show the list of tasks
            tasksView.showTasks(taskItems, testSectionItems, sortLinks);
            // Set the filter label's text.
            showFilterLabel();
        }
    }

    private void showFilterLabel() {
        switch (currentFiltering) {
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
    public void openTaskDetails(@NonNull Task requestedTask) {
        requireNonNull(requestedTask, "requestedTask cannot be null!");
        tasksView.showTaskDetails(requestedTask.getId());
    }

    @Override
    public void completeTask(@NonNull Task completedTask) {
        requireNonNull(completedTask, "completedTask cannot be null!");
        dataRepository.completeTask(completedTask);
        tasksView.showTaskMarkedComplete();
        loadTasks(false, false);
    }

    @Override
    public void activateTask(@NonNull Task activeTask) {
        requireNonNull(activeTask, "activeTask cannot be null!");
        dataRepository.activateTask(activeTask);
        tasksView.showTaskMarkedActive();
        loadTasks(false, false);
    }

    @Override
    public void clearCompletedTasks() {
        dataRepository.clearCompletedTasks();
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
        currentFiltering = requestType;
    }

    @Override
    public TasksFilterType getFiltering() {
        return currentFiltering;
    }
}

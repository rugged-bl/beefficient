package com.beefficient.tasks;

import android.support.annotation.NonNull;
import android.util.Log;

import com.beefficient.data.Project;
import com.beefficient.data.Task;
import com.beefficient.data.source.TasksRepository;

import org.ocpsoft.prettytime.shade.org.apache.commons.lang.math.RandomUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class TasksPresenter implements TasksContract.Presenter {

    private final TasksRepository tasksRepository;
    private final TasksContract.View tasksView;

    private CompositeSubscription subscriptions;

    public TasksPresenter(@NonNull TasksRepository tasksRepository, @NonNull TasksContract.View tasksView) {
        this.tasksRepository = tasksRepository;
        this.subscriptions = new CompositeSubscription();
        this.tasksView = tasksView;
        tasksView.setPresenter(this);
    }

    @Override
    public List<Task> getDummyTaskList() {
        List<Task> taskList = new ArrayList<>();
        Task.Builder task = new Task.Builder("Title")
                .setProject(new Project("Project"))
                .setTime(System.currentTimeMillis());

        for (int i = 0; i < 50; i++) {
            taskList.add(task.setCompleted(RandomUtils.nextBoolean())
                    .setPriority(Task.Priority.values()[RandomUtils.nextInt(3)]).build());
        }

        return taskList;
    }

    @Override
    public void subscribe() {
        loadTasks();
    }

    @Override
    public void loadTasks() {
        Log.d("TasksPresenter", "loadTasks");
//        if (showLoadingUI) {
//            mTasksView.setLoadingIndicator(true);
//        }
//        if (forceUpdate) {
//            mTasksRepository.refreshTasks();
//        }

        // The network request might be handled in a different thread so make sure Espresso knows
        // that the app is busy until the response is handled.
//        EspressoIdlingResource.increment(); // App is busy until further notice

        tasksRepository.refreshTasks();
        subscriptions.clear();
        Subscription subscription = tasksRepository.getTasks()
                .flatMap(Observable::from)
//                .filter(task -> {
//                    switch (currentFiltering) {
//                        case ACTIVE_TASKS:
//                            return task.isActive();
//                        case COMPLETED_TASKS:
//                            return task.isCompleted();
//                        case ALL_TASKS:
//                        default:
//                            return true;
//                    }
//                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Task>>() {
                    @Override
                    public void onCompleted() {
                        Log.d("TasksPresenter", "completed");
                        tasksView.setLoadingIndicator(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("TasksPresenter", "error");
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
//            showFilterLabel();
        }
    }

    private void processEmptyTasks() {
//        switch (mCurrentFiltering) {
//            case ACTIVE_TASKS:
//                mTasksView.showNoActiveTasks();
//                break;
//            case COMPLETED_TASKS:
//                mTasksView.showNoCompletedTasks();
//                break;
//            default:
//                mTasksView.showNoTasks();
//                break;
//        }
        tasksView.showNoTasks();
    }

    @Override
    public void unsubscribe() {
        subscriptions.clear();
    }
}

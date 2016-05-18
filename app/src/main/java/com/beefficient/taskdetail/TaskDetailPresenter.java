package com.beefficient.taskdetail;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.beefficient.data.entity.Task;
import com.beefficient.data.source.DataRepository;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.beefficient.util.Objects.requireNonNull;

/**
 * Listens to user actions from the UI ({@link TaskDetailFragment}), retrieves the data and updates
 * the UI as required.
 */
public class TaskDetailPresenter implements TaskDetailContract.Presenter {

    private final DataRepository dataRepository;

    private final TaskDetailContract.View taskDetailView;

    @Nullable
    private String taskId;
    private CompositeSubscription mSubscriptions;

    public TaskDetailPresenter(@Nullable String taskId,
                               @NonNull DataRepository dataRepository,
                               @NonNull TaskDetailContract.View taskDetailView) {
        this.taskId = taskId;
        this.dataRepository = requireNonNull(dataRepository, "tasksRepository cannot be null!");
        this.taskDetailView = requireNonNull(taskDetailView, "taskDetailView cannot be null!");
        mSubscriptions = new CompositeSubscription();
        this.taskDetailView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        openTask();
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    private void openTask() {
        if (null == taskId || taskId.isEmpty()) {
            taskDetailView.showMissingTask();
            return;
        }

        taskDetailView.setLoadingIndicator(true);
        Subscription subscription = dataRepository
                .getTask(taskId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Task>() {
                    @Override
                    public void onCompleted() {
                        taskDetailView.setLoadingIndicator(false);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Task task) {
                        showTask(task);
                    }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void editTask() {
        if (null == taskId || taskId.isEmpty()) {
            taskDetailView.showMissingTask();
            return;
        }
        taskDetailView.showEditTask(taskId);
    }

    @Override
    public void deleteTask() {
        dataRepository.deleteTask(taskId);
        taskDetailView.showTaskDeleted();
    }

    @Override
    public void completeTask() {
        if (null == taskId || taskId.isEmpty()) {
            taskDetailView.showMissingTask();
            return;
        }
        dataRepository.completeTask(taskId);
        taskDetailView.showTaskMarkedComplete();
    }

    @Override
    public void activateTask() {
        if (null == taskId || taskId.isEmpty()) {
            taskDetailView.showMissingTask();
            return;
        }
        dataRepository.activateTask(taskId);
        taskDetailView.showTaskMarkedActive();
    }

    private void showTask(Task task) {
        String title = task.getTitle();
        String description = task.getDescription();

        if (title != null && title.isEmpty()) {
            taskDetailView.hideTitle();
        } else {
            taskDetailView.showTitle(title);
        }

        if (description != null && description.isEmpty()) {
            taskDetailView.hideDescription();
        } else {
            taskDetailView.showDescription(description);
        }
        taskDetailView.showCompletionStatus(task.isCompleted());
    }
}

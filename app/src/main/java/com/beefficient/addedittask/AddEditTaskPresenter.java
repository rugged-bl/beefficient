package com.beefficient.addedittask;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.beefficient.data.entity.DefaultTypes;
import com.beefficient.data.entity.Task;
import com.beefficient.data.source.DataSource;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.beefficient.util.Objects.requireNonNull;

/**
 * Listens to user actions from the UI ({@link AddEditTaskFragment}), retrieves the data and updates
 * the UI as required.
 */
public class AddEditTaskPresenter implements AddEditTaskContract.Presenter {

    @NonNull
    private final DataSource dataRepository;

    @NonNull
    private final AddEditTaskContract.View addEditTaskView;

    @Nullable
    private String taskId;

    private CompositeSubscription subscriptions;

    /**
     * Creates a presenter for the add/edit view.
     *
     * @param taskId         ID of the task to edit or null for a new task
     * @param dataRepository a repository of data for tasks
     * @param addEditTaskView    the add/edit view
     */
    public AddEditTaskPresenter(@Nullable String taskId, @NonNull DataSource dataRepository,
                                @NonNull AddEditTaskContract.View addEditTaskView) {
        this.taskId = taskId;
        this.dataRepository = requireNonNull(dataRepository);
        this.addEditTaskView = requireNonNull(addEditTaskView);

        subscriptions = new CompositeSubscription();

        this.addEditTaskView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        if (taskId != null) {
            populateTask();
            openTask();
        }
    }

    @Override
    public void unsubscribe() {
        subscriptions.clear();
    }

    private void openTask() {
        if (null == taskId || taskId.isEmpty()) {
            return;
        }

        Subscription subscription = dataRepository
                .getTask(taskId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(task -> showTask(task));

        subscriptions.add(subscription);
    }

    private void showTask(Task task) {
        String title = task.getTitle();
        String description = task.getDescription();

        addEditTaskView.setTitle(title);
        addEditTaskView.setDescription(description);
        addEditTaskView.setCompleted(task.isCompleted());
    }

    @Override
    public void createTask(String title, String description, boolean completed) {
        if (title.isEmpty()) {
            addEditTaskView.showEmptyTaskError();
        } else {
            Task newTask = new Task.Builder(title)
                    .setDescription(description)
                    .setProject(DefaultTypes.PROJECT)
                    .setCompleted(completed)
                    .build();

            dataRepository.saveTask(newTask);
            addEditTaskView.showTasksList();
        }
    }

    @Override
    public void updateTask(String title, String description, boolean completed) {
        if (taskId == null) {
            throw new RuntimeException("updateTask() was called but task is new.");
        }

        if (title.isEmpty()) {
            addEditTaskView.showEmptyTaskError();
        } else {
            Task task = new Task.Builder(title, taskId)
                    .setDescription(description)
                    .setProject(DefaultTypes.PROJECT)
                    .setCompleted(completed)
                    .build();

            dataRepository.saveTask(task);
            addEditTaskView.showTasksList();
        }
    }

    @Override
    public void populateTask() {
        if (taskId == null) {
            throw new RuntimeException("populateTask() was called but task is new.");
        }
        dataRepository.getTask(taskId);
    }

    @Override
    public void deleteTask() {
        if (taskId != null) {
            dataRepository.deleteTask(taskId);
        }
        addEditTaskView.showTaskDeleted();
    }

/*    @Override
    public void onTaskLoaded(Task task) {
        // The view may not be able to handle UI updates anymore
        if (addEditTaskView.isActive()) {
            addEditTaskView.setTitle(task.getTitle());
            addEditTaskView.setDescription(task.getDescription());
        }
    }

    @Override
    public void onDataNotAvailable() {
        // The view may not be able to handle UI updates anymore
        if (addEditTaskView.isActive()) {
            addEditTaskView.showEmptyTaskError();
        }
    }*/
}

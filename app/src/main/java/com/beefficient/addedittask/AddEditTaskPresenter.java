package com.beefficient.addedittask;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.beefficient.data.entity.Project;
import com.beefficient.data.entity.Task;
import com.beefficient.data.source.DataSource;

import java.util.ArrayList;
import java.util.Arrays;

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

    private String title;
    private String description;
    private boolean completed;
    private Task.Priority priority;
    private Project project;

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
                .subscribe(this::showTask);

        subscriptions.add(subscription);
    }

    private void showTask(Task task) {
        setTitle(task.getTitle());
        setDescription(task.getDescription());
        setCompleted(task.isCompleted());
        setProject(task.getProject());
        setPriority(task.getPriority());
    }

    @Override
    public void saveTask() {
        if (title.isEmpty()) {
            addEditTaskView.showEmptyTaskError();
        } else {
            Task.Builder taskBuilder;
            if (isNewTask()) {
                taskBuilder = new Task.Builder(title);
            } else {
                taskBuilder = new Task.Builder(title, taskId);
            }

            Task task = taskBuilder
                    .setDescription(description)
                    .setProject(project)
                    .setPriority(priority)
                    .setCompleted(completed)
                    .build();

            dataRepository.saveTask(task);
            addEditTaskView.showTask();
        }
    }

    private boolean isNewTask() {
        return taskId == null;
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

    @Override
    public void setTitle(String title) {
        this.title = title;
        addEditTaskView.setTitle(title);
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
        addEditTaskView.setDescription(description);
    }

    @Override
    public void setCompleted(boolean completed) {
        this.completed = completed;
        addEditTaskView.setCompleted(completed);
    }

    @Override
    public void setProject(Project project) {
        this.project = project;
        addEditTaskView.setProject(project.getName());
    }

    @Override
    public void setPriority(Task.Priority priority) {
        this.priority = priority;
        addEditTaskView.setPriority(priority.priorityName());
    }

    @Override
    public void selectProject() {
        ArrayList<Project> projects =
                (ArrayList<Project>) dataRepository.getProjects().toBlocking().single();

        addEditTaskView.showSelectProjectDialog(projects);
    }

    @Override
    public void selectPriority() {
        addEditTaskView.showSelectPriorityDialog(Arrays.asList(Task.Priority.values()));
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

package com.beefficient.addedittask;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.beefficient.data.entity.DefaultTypes;
import com.beefficient.data.entity.Project;
import com.beefficient.data.entity.Task;
import com.beefficient.data.source.DataSource;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import rx.subscriptions.CompositeSubscription;

import static com.beefficient.util.Objects.requireNonNull;

/**
 * Listens to user actions from the UI ({@link AddEditTaskFragment}), retrieves the data and updates
 * the UI as required.
 */
public class AddEditTaskPresenter implements AddEditTaskContract.Presenter, Serializable {

    @NonNull
    private final DataSource dataRepository;

    @NonNull
    private final AddEditTaskContract.View view;

    @Nullable
    private Task task;

    private String title;
    private String description;
    private boolean completed;
    private Task.Priority priority;
    private Project project;
    private long time;
    private boolean withTime;

    private final CompositeSubscription subscriptions;

    /**
     * Creates a presenter for the add/edit view.
     *
     * @param task              the task to edit or null for a new task
     * @param dataRepository    a repository of data for tasks
     * @param view              the add/edit view
     */
    public AddEditTaskPresenter(@Nullable Task task, @NonNull DataSource dataRepository,
                                @NonNull AddEditTaskContract.View view) {
        setTask(task);
        this.dataRepository = requireNonNull(dataRepository);
        this.view = requireNonNull(view);

        subscriptions = new CompositeSubscription();

        this.view.setPresenter(this);
    }

    @Override
    public void subscribe() {
//        if (task != null) {
//            populateTask();
//        }
    }

    @Override
    public void unsubscribe() {
        subscriptions.clear();
    }

    @Nullable
    public Task getTask() {
        return task;
    }

    @Override
    public void saveTask() {
        if (title.isEmpty()) {
            view.showEmptyTitleError();
        } else {
            Task.Builder taskBuilder;
            if (isNewTask()) {
                taskBuilder = new Task.Builder(title);
            } else {
                // TODO
                dataRepository.deleteTask(task.getId());
                taskBuilder = new Task.Builder(task);
            }

            Task task = taskBuilder
                    .setTitle(title)
                    .setTime(time)
                    .setWithTime(withTime)
                    .setDescription(description)
                    .setProject(project)
                    .setPriority(priority)
                    .setCompleted(completed)
                    .build();

            dataRepository.saveTask(task);
            view.showTask();
        }
    }

    @Override
    public boolean isNewTask() {
        return task == null;
    }
//
//    @Override
//    public void populateTask() {
//        Log.d("AddEditTaskPresenter", "populateTask: ");
//        if (task == null) {
//            throw new RuntimeException("populateTask() was called but task is new.");
//        }
//
//        Subscription subscription = dataRepository
//                .getTask(task.getId())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(this::setTask);
//
//        subscriptions.add(subscription);
//    }

    @Override
    public void showTask() {
        Log.d("AddEditTaskPresenter", "showTask: ");
        view.setTitle(title);
        view.setDescription(description);
        view.setProject(project.getName());
        view.setPriority(priority.priorityName());
        view.setDueDate(time);
    }

    @Override
    public void deleteTask() {
        if (task != null) {
            dataRepository.deleteTask(task.getId());
        }
        view.showTaskDeleted();
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
        view.setTitle(title);
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
        view.setDescription(description);
    }

    @Override
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public void setProject(Project project) {
        this.project = project;
        view.setProject(project.getName());
    }

    @Override
    public void setPriority(Task.Priority priority) {
        this.priority = priority;
        view.setPriority(priority.priorityName());
    }

    @Override
    public void setWithTime(boolean withTime) {
        this.withTime = withTime;

    }

    @Override
    public void setDueDate(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minute) {
        DateTime dateTime = new DateTime(year, monthOfYear + 1, dayOfMonth, hourOfDay, minute);
        time = dateTime.getMillis();
        view.setDueDate(time);
    }

    @Override
    public void clearDueDate() {
        time = 0;
        view.setDueDate(time);
    }

    @Override
    public void selectProject() {
        ArrayList<Project> projects =
                (ArrayList<Project>) dataRepository.getProjects().toBlocking().single();

        view.showSelectProjectDialog(projects);
    }

    @Override
    public void selectPriority() {
        view.showSelectPriorityDialog(new ArrayList<>(Arrays.asList(Task.Priority.values())));
    }

    @Override
    public void selectDate() {
        DateTime dateTime = new DateTime((time == 0) ? (DateTimeUtils.currentTimeMillis()) : time);

        int hourOfDay;
        int minuteOfHour;

        if (!withTime) {
            DateTime now = new DateTime();
            hourOfDay = now.getHourOfDay();
            minuteOfHour = now.getMinuteOfHour();
        } else {
            hourOfDay = dateTime.getHourOfDay();
            minuteOfHour = dateTime.getMinuteOfHour();
        }

        view.showSelectDateDialog(dateTime.getYear(), dateTime.getMonthOfYear() - 1,
                dateTime.getDayOfMonth(), hourOfDay, minuteOfHour, withTime);
    }

    private void setTask(@Nullable Task task) {
        this.task = task;
        if (task == null) {
            project = DefaultTypes.PROJECT;
            priority = DefaultTypes.PRIORITY;
        } else {
            title = task.getTitle();
            description = task.getDescription();
            completed = task.isCompleted();
            priority = task.getPriority();
            project = task.getProject();
            time = task.getTime();
            withTime = task.isWithTime();
        }
    }
}

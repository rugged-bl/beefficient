package com.beefficient.addedittask;

import android.support.annotation.StringRes;

import com.beefficient.BasePresenter;
import com.beefficient.BaseView;
import com.beefficient.data.entity.Project;
import com.beefficient.data.entity.Task;

import java.util.List;

public interface AddEditTaskContract {

    interface View extends BaseView<Presenter> {

        void showEmptyTaskError();

        void showTasksList();

        void showTitle(String title);

        void showDescription(String description);

        void showCompleted(boolean completed);

        void showPriority(@StringRes int priorityName);

        void showProject(String name);

        void showTaskDeleted();

        void showTask();

        void showSelectProjectDialog(List<Project> projects);

        void showSelectPriorityDialog(List<Task.Priority> priorities);

        void showSelectDateDialog();
    }

    interface Presenter extends BasePresenter {

        void saveTask();

        void populateTask();

        void deleteTask();

        void setTitle(String title);

        void setDescription(String description);

        void setCompleted(boolean completed);

        void setProject(Project item);

        void setPriority(Task.Priority priority);

        void selectProject();

        void selectPriority();

        void selectDate();
    }
}
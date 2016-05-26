package com.beefficient.addedittask;

import android.support.annotation.StringRes;

import com.beefficient.BasePresenter;
import com.beefficient.BaseView;
import com.beefficient.data.entity.Project;
import com.beefficient.data.entity.Task;

import java.util.ArrayList;
import java.util.List;

public interface AddEditTaskContract {

    interface View extends BaseView<Presenter> {

        void showEmptyTitleError();

        void showTasksList();

        void setTitle(String title);

        void setDescription(String description);

        void setPriority(@StringRes int priorityName);

        void setProject(String name);

        void showTaskDeleted();

        void showTask();

        void showSelectProjectDialog(ArrayList<Project> projects);

        void showSelectPriorityDialog(List<Task.Priority> priorities);

        void showSelectDateDialog();
    }

    interface Presenter extends BasePresenter {

        Task getTask();

        boolean isNewTask();

        void saveTask();

//        void populateTask();

        void deleteTask();

        void showTask();

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
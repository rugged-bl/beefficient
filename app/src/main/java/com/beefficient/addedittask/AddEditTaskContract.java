package com.beefficient.addedittask;

import android.support.annotation.StringRes;

import com.beefficient.BasePresenter;
import com.beefficient.BaseView;
import com.beefficient.data.entity.Project;
import com.beefficient.data.entity.Task;

import java.util.ArrayList;

public interface AddEditTaskContract {

    interface View extends BaseView<Presenter> {

        void showEmptyTitleError();

        void showTasksList();

        void setTitle(String title);

        void setDescription(String description);

        void setPriority(@StringRes int priorityName);

        void setProject(String name);

        void setDueDate(long dueDate);

        void showTaskDeleted();

        void showTask();

        void showSelectProjectDialog(ArrayList<Project> projects);

        void showSelectPriorityDialog(ArrayList<Task.Priority> priorities);

        void showSelectDateDialog(int year, int monthOfYear, int dayOfMonth, int hourOfDay,
                                  int minute, boolean withTime);
    }

    interface Presenter extends BasePresenter {

        Task getTask();

        boolean isNewTask();

        void saveTask();

        void deleteTask();

        void showTask();

        void setTitle(String title);

        void setDescription(String description);

        void setCompleted(boolean completed);

        void setProject(Project item);

        void setPriority(Task.Priority priority);

        void setWithTime(boolean withTime);

        void setDueDate(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minute);

        void selectProject();

        void selectPriority();

        void selectDate();

        void clearDueDate();
    }
}
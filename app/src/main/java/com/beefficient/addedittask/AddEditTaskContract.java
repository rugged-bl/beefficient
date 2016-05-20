package com.beefficient.addedittask;

import android.support.annotation.StringRes;

import com.beefficient.BasePresenter;
import com.beefficient.BaseView;
import com.beefficient.data.entity.Project;

import java.util.List;

public interface AddEditTaskContract {

    interface View extends BaseView<Presenter> {

        void showEmptyTaskError();

        void showTasksList();

        void setTitle(String title);

        void setDescription(String description);

        void setCompleted(boolean completed);

        void showTaskDeleted();

        void showTask();

        void setPriority(@StringRes int priorityName);

        void setProject(String name);

        void showSelectProjectDialog(List<Project> projects);
    }

    interface Presenter extends BasePresenter {

        void saveTask(String title, String description, boolean completed, int time,
                      boolean withTime);

        void populateTask();

        void deleteTask();

        void setProject(Project item);

        void selectProject();
    }
}
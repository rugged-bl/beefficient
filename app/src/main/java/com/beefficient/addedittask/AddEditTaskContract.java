package com.beefficient.addedittask;

import com.beefficient.BasePresenter;
import com.beefficient.BaseView;

public interface AddEditTaskContract {

    interface View extends BaseView<Presenter> {

        void showEmptyTaskError();

        void showTasksList();

        void setTitle(String title);

        void setDescription(String description);

        void setCompleted(boolean completed);

        void showTaskDeleted();
    }

    interface Presenter extends BasePresenter {

        void createTask(String title, String description, boolean completed);

        void updateTask( String title, String description, boolean completed);

        void populateTask();

        void deleteTask();
    }
}
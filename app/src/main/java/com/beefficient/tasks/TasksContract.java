package com.beefficient.tasks;

import com.beefficient.BasePresenter;
import com.beefficient.BaseView;
import com.beefficient.data.Task;

import java.util.List;

public interface TasksContract {
    interface View extends BaseView<Presenter> {
        void setLoadingIndicator(boolean b);

        void showLoadingTasksError();

        void showNoTasks();

        void showTasks(List<Task> tasks);
    }

    interface Presenter extends BasePresenter {
        void loadTasks();
    }
}

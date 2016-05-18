package com.beefficient.tasks;

import android.support.annotation.NonNull;

import com.beefficient.BasePresenter;
import com.beefficient.BaseView;
import com.beefficient.data.entity.Task;

import java.util.ArrayList;
import java.util.HashMap;

public interface TasksContract {
    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showTasks(ArrayList<TasksAdapter.TaskItem> taskItems, HashMap<Integer, TasksAdapter.SectionItem> sectionItems);

        void showAddTask();

        void showEditTask(String taskId);

        void showTaskMarkedComplete();

        void showTaskMarkedActive();

        void showCompletedTasksCleared();

        void showLoadingTasksError();

        void showNoTasks();

        void showActiveFilterLabel();

        void showCompletedFilterLabel();

        void showAllFilterLabel();

        void showNoActiveTasks();

        void showNoCompletedTasks();

        void showSuccessfullySavedMessage();

        // TODO: для чего это?
        boolean isActive();

        void showFilteringPopUpMenu();
    }

    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode);

        void loadTasks(boolean forceUpdate);

        void addNewTask();

        void editTask(@NonNull Task task);

        void completeTask(@NonNull Task task);

        void activateTask(@NonNull Task task);

        void clearCompletedTasks();

        void setFiltering(TasksFilterType requestType);

        void deleteAllData();

        TasksFilterType getFiltering();
    }
}

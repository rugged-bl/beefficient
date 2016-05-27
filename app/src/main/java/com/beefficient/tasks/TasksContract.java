package com.beefficient.tasks;

import android.content.Intent;
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

        void showEditTask(Task task);

        void showTaskMarkedComplete();

        void showTaskMarkedActive();

        void showCompletedTasksCleared();

        void showLoadingTasksError();

        void showNoTasks();

        void showNoActiveTasks();

        void showNoCompletedTasks();

        void showSavedMessage();

        void showDeletedMessage();

        void showEditedMessage();
    }

    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode, Intent data);

        void loadTasks(boolean forceUpdate);

        void addNewTask();

        void editTask(@NonNull Task task);

        void completeTask(@NonNull Task task);

        void activateTask(@NonNull Task task);

        void clearCompletedTasks();

        void deleteAllData();

        void setFiltering(TasksFilterType requestType);

        TasksFilterType getFiltering();

        void setSorting(TasksSortType requestType);

        TasksSortType getSorting();
    }
}

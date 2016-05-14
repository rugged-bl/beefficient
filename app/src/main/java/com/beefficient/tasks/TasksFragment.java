package com.beefficient.tasks;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beefficient.R;
import com.beefficient.data.entity.Task;
import com.beefficient.data.source.TasksRepository;
import com.beefficient.data.source.local.LocalDataSource;
import com.beefficient.data.source.remote.RemoteDataSource;
import com.beefficient.main.MainContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TasksFragment extends Fragment implements TasksContract.View {

    private TasksContract.Presenter presenter;
    private TasksAdapter tasksAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    public TasksFragment() {
    }

    public static TasksFragment newInstance() {
        TasksFragment fragment = new TasksFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new TasksPresenter(TasksRepository.getInstance(RemoteDataSource.getInstance(),
                LocalDataSource.getInstance(getContext().getApplicationContext())), this);
        tasksAdapter = new TasksAdapter(Collections.EMPTY_LIST);
        tasksAdapter.setListener(task -> {
            Activity activity = getActivity();
            if (activity instanceof MainContract.View) {
                ((MainContract.View) activity).showSnackbar("Clicked task: " + task.getTitle(),
                        Snackbar.LENGTH_SHORT);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setAdapter(tasksAdapter);
        }

        Activity activity = getActivity();
        swipeRefreshLayout = (SwipeRefreshLayout) activity.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> presenter.loadTasks(true));

        FloatingActionButton addTaskButton = (FloatingActionButton) activity.findViewById(R.id.fab_add);
        addTaskButton.setOnClickListener(v -> {
            // TODO: start EditTaskActivity
            if (activity instanceof MainContract.View) {
                ((MainContract.View) activity).showSnackbar("Add task", Snackbar.LENGTH_SHORT);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.unsubscribe();
    }

    @Override
    public void setPresenter(@NonNull TasksContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(active));
    }

    @Override
    public void showLoadingTasksError() {
    }

    @Override
    public void showAddTask() {
    }

    @Override
    public void showTaskDetails(String taskId) {
    }

    @Override
    public void showTaskMarkedComplete() {
    }

    @Override
    public void showTaskMarkedActive() {
    }

    @Override
    public void showCompletedTasksCleared() {
    }

    @Override
    public void showActiveFilterLabel() {
    }

    @Override
    public void showCompletedFilterLabel() {
    }

    @Override
    public void showAllFilterLabel() {
    }

    @Override
    public void showNoActiveTasks() {
    }

    @Override
    public void showNoCompletedTasks() {
    }

    @Override
    public void showNoTasks() {
        Log.d("TasksFragment", "showNoTasks");
    }

    @Override
    public void showTasks(ArrayList<TasksAdapter.TaskItem> taskItems, ArrayList<TasksAdapter.SectionItem> sectionItems, HashMap<Integer, Integer> sortLinks) {
        Log.d("TasksFragment", "showTasks");

        tasksAdapter.setSectionItems(sectionItems);
        tasksAdapter.setTaskItems(taskItems);
        tasksAdapter.setSortLinks(sortLinks);
    }

    @Override
    public void showSuccessfullySavedMessage() {
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void showFilteringPopUpMenu() {
    }
}

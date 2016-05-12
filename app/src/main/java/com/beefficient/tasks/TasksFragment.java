package com.beefficient.tasks;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beefficient.R;
import com.beefficient.data.Task;
import com.beefficient.data.source.TasksRepository;
import com.beefficient.data.source.local.LocalDataSource;
import com.beefficient.data.source.remote.RemoteDataSource;

import java.util.ArrayList;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new TasksPresenter(TasksRepository.getInstance(RemoteDataSource.getInstance(),
                LocalDataSource.getInstance(getContext().getApplicationContext())), this);
        tasksAdapter = new TasksAdapter(new ArrayList<>(0));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setAdapter(tasksAdapter);
        }

        swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> presenter.loadTasks(true));

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
    public void showTasks(List<Task> tasks) {
        Log.d("TasksFragment", "showTasks");
        tasksAdapter.replaceData(tasks);
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

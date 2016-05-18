package com.beefficient.tasks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beefficient.R;
import com.beefficient.addedittask.AddEditTaskActivity;
import com.beefficient.data.entity.Task;
import com.beefficient.data.source.DataRepository;
import com.beefficient.data.source.local.LocalDataSource;
import com.beefficient.data.source.remote.RemoteDataSource;
import com.beefficient.main.MainContract;
import com.beefficient.taskdetail.TaskDetailActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static com.beefficient.util.Objects.requireNonNull;

public class TasksFragment extends Fragment implements TasksContract.View {

    private TasksContract.Presenter presenter;
    private TasksAdapter tasksAdapter;

    private RecyclerView tasksView;
    private View noTasksContainer;
    private TextView noTasksView;
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
        setHasOptionsMenu(true);

        presenter = new TasksPresenter(DataRepository.getInstance(RemoteDataSource.getInstance(),
                LocalDataSource.getInstance(getContext().getApplicationContext())), this);

        tasksAdapter = new TasksAdapter(Collections.EMPTY_LIST);
        tasksAdapter.setListener(new TasksAdapter.TaskItemListener() {
            @Override
            public void onTaskClick(Task task) {
                showTaskDetails(task.getId());
            }

            @Override
            public void onTaskLongClick(Task task) {
                Activity activity = getActivity();
                if (activity instanceof MainContract.View) {
                    // TODO: select task
                    showSnackbar("Long clicked task: " + task.getTitle());
                }
            }

            @Override
            public void onCompleteTaskClick(Task task) {
                presenter.completeTask(task);
            }

            @Override
            public void onActivateTaskClick(Task task) {
                presenter.activateTask(task);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        tasksView = (RecyclerView) view.findViewById(R.id.tasks_view);
        tasksView.setAdapter(tasksAdapter);

        noTasksContainer = view.findViewById(R.id.no_tasks_container);
        noTasksView = (TextView) view.findViewById(R.id.no_tasks);

        Activity activity = getActivity();
        swipeRefreshLayout = (SwipeRefreshLayout) activity.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> presenter.loadTasks(true));

        FloatingActionButton addTaskButton = (FloatingActionButton) activity.findViewById(R.id.fab_add);
        addTaskButton.setOnClickListener(v -> {
            // TODO: start EditTaskActivity
            if (activity instanceof MainContract.View) {
                showAddTask();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.result(requestCode, resultCode);
    }

    @Override
    public void setPresenter(@NonNull TasksContract.Presenter presenter) {
        this.presenter = requireNonNull(presenter);
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
        Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
        startActivityForResult(intent, AddEditTaskActivity.REQUEST_ADD_TASK);
    }

    @Override
    public void showTaskDetails(String taskId) {
        Intent intent = new Intent(getContext(), TaskDetailActivity.class);
        intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, taskId);
        startActivity(intent);
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

        showNoTasksView((String) getResources().getText(R.string.no_tasks));
    }

    private void showNoTasksView(String text) {
        tasksView.setVisibility(View.GONE);
        noTasksContainer.setVisibility(View.VISIBLE);
        noTasksView.setText(text);
    }

    @Override
    public void showTasks(ArrayList<TasksAdapter.TaskItem> taskItems, HashMap<Integer, TasksAdapter.SectionItem> sectionItems) {
        Log.d("TasksFragment", "showTasks");

        tasksView.setVisibility(View.VISIBLE);
        noTasksContainer.setVisibility(View.GONE);

        tasksAdapter.setContent(taskItems, sectionItems);
    }

    private void showSnackbar(String message) {
        View view = getView();
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void showSuccessfullySavedMessage() {
        showSnackbar("SuccessfullySaved");
        tasksAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void showFilteringPopUpMenu() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_tasks, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_search: {
                // TODO: show SearchView in toolbar
                return true;
            }
            case R.id.menu_item_toggle_completed: {
                // TODO: show/hide completed tasks in this view
                return true;
            }
            case R.id.menu_item_sort: {
                // TODO: открыть диалог с выбором сортировки
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
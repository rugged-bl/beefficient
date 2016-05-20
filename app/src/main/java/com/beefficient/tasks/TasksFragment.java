package com.beefficient.tasks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beefficient.Injection;
import com.beefficient.R;
import com.beefficient.addedittask.AddEditTaskActivity;
import com.beefficient.data.entity.Task;

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

    private TasksAdapter.TaskItemListener taskItemListener = new TasksAdapter.TaskItemListener() {
        @Override
        public void onTaskClick(Task task) {
            presenter.editTask(task);
        }

        @Override
        public void onTaskLongClick(Task task) {
            // TODO: select task
            showSnackbar("Long clicked task: " + task.getTitle());
        }

        @Override
        public void onCompleteTaskClick(Task task) {
            presenter.completeTask(task);
        }

        @Override
        public void onActivateTaskClick(Task task) {
            presenter.activateTask(task);
        }
    };

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        tasksAdapter = new TasksAdapter(Collections.EMPTY_LIST);
        tasksAdapter.setListener(taskItemListener);

        // Create the presenter
        new TasksPresenter(
                Injection.provideDataRepository(getContext().getApplicationContext()), this);
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

        FloatingActionButton addTaskButton =
                (FloatingActionButton) activity.findViewById(R.id.fab_add);
        addTaskButton.setOnClickListener(v -> presenter.addNewTask());

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
        presenter.result(requestCode, resultCode, data);
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
    public void showEditTask(String taskId) {
        Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
        intent.putExtra(AddEditTaskActivity.EXTRA_TASK_ID, taskId);
        startActivityForResult(intent, AddEditTaskActivity.REQUEST_EDIT_TASK);
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
    public void showNoActiveTasks() {
        showNoTasksView(getString(R.string.no_active_tasks));
    }

    @Override
    public void showNoCompletedTasks() {
        showNoTasksView(getString(R.string.no_completed_tasks));
    }

    @Override
    public void showNoTasks() {
        showNoTasksView(getString(R.string.no_tasks));
    }

    private void showNoTasksView(String text) {
        tasksView.setVisibility(View.GONE);
        noTasksContainer.setVisibility(View.VISIBLE);

        noTasksView.setText(text);
    }

    @Override
    public void showTasks(ArrayList<TasksAdapter.TaskItem> taskItems,
                          HashMap<Integer, TasksAdapter.SectionItem> sectionItems) {
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
    public void showSavedMessage() {
        showSnackbar(getString(R.string.task_saved));
        tasksAdapter.notifyDataSetChanged();
    }

    @Override
    public void showDeletedMessage() {
        showSnackbar(getString(R.string.task_deleted));
        tasksAdapter.notifyDataSetChanged();
    }

    @Override
    public void showFilteringPopUpMenu() {
        PopupMenu popup = new PopupMenu(getContext(), getActivity().findViewById(R.id.menu_filter));
        popup.getMenuInflater().inflate(R.menu.filter_tasks, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.active:
                    presenter.setFiltering(TasksFilterType.ACTIVE_TASKS);
                    break;
                case R.id.completed:
                    presenter.setFiltering(TasksFilterType.COMPLETED_TASKS);
                    break;
                default:
                    presenter.setFiltering(TasksFilterType.ALL_TASKS);
                    break;
            }
            presenter.loadTasks(false);
            return true;
        });

        popup.show();
    }

    @Override
    public void showSortingPopUpMenu() {
        PopupMenu popup = new PopupMenu(getContext(), getActivity().findViewById(R.id.menu_sort));
        popup.getMenuInflater().inflate(R.menu.sort_tasks, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.projects:
                    presenter.setSorting(TasksSortType.PROJECTS);
                    break;
                case R.id.date:
                    presenter.setSorting(TasksSortType.DATE);
                    break;
                default:
                    presenter.setSorting(TasksSortType.PROJECTS);
                    break;
            }
            presenter.loadTasks(false);
            return true;
        });

        popup.show();
    }

    @Override
    public void showEditedMessage() {
        showSnackbar(getString(R.string.task_edited));
        tasksAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_tasks, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_search: {
                break;
            }
            case R.id.menu_filter:
                showFilteringPopUpMenu();
                break;
            case R.id.menu_clear:
                presenter.clearCompletedTasks();
                break;
            case R.id.menu_sort: {
                showSortingPopUpMenu();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
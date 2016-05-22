package com.beefficient.addedittask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.beefficient.R;
import com.beefficient.data.entity.DefaultTypes;
import com.beefficient.data.entity.Project;
import com.beefficient.data.entity.Task;

import java.util.ArrayList;
import java.util.List;

import static com.beefficient.util.Objects.requireNonNull;

/**
 * Main UI for the add task screen. Users can enter a task title and description.
 */
public class AddEditTaskFragment extends Fragment implements AddEditTaskContract.View {

    private static final String TAG = "AddEditTaskFragment";

    public static final String ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID";

    private AddEditTaskContract.Presenter presenter;

    private String editedTaskId;

    private TextView titleView;
//    private TextView descriptionView;
    private CheckBox checkboxCompleted;

    private final TaskParam projectParam = new TaskParam(R.string.project, R.drawable.ic_project);
    private final TaskParam priorityParam = new TaskParam(R.string.priority, R.drawable.ic_priority);
    private final TaskParam dateParam = new TaskParam(R.string.date, R.drawable.ic_date_range);

    private TaskParamsAdapter paramsAdapter;

    public AddEditTaskFragment() {
    }

    public static AddEditTaskFragment newInstance(String taskId) {
        AddEditTaskFragment fragment = new AddEditTaskFragment();

        Bundle args = new Bundle();
        args.putString(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID, taskId);
        fragment.setArguments(args);

        return fragment;
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
    public void setPresenter(@NonNull AddEditTaskContract.Presenter presenter) {
        this.presenter = requireNonNull(presenter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        // Configure params adapter
        ArrayList<TaskParam> params = new ArrayList<>();
        params.add(projectParam);
        params.add(priorityParam);
        params.add(dateParam);

        paramsAdapter = new TaskParamsAdapter(params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addedittask, container, false);

        checkboxCompleted = (CheckBox) view.findViewById(R.id.task_completed);
        titleView = (TextView) view.findViewById(R.id.task_title);
//        descriptionView = (TextView) view.findViewById(R.id.task_description);

        ListView paramsView = (ListView) view.findViewById(R.id.task_params);
        paramsView.setAdapter(paramsAdapter);

        return view;
    }

    @Override
    public void showSelectProjectDialog(List<Project> projects) {

        // TODO
        ArrayAdapter<Project> adapter = new ArrayAdapter<Project>(getContext(),
                R.layout.dialog_item_project) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view;

                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    view = inflater.inflate(R.layout.dialog_item_project, parent, false);
                } else {
                    view = convertView;
                }

                TextView text = (TextView) view.findViewById(R.id.project_name);
                View indicator = view.findViewById(R.id.project_color);

                Project item = getItem(position);
                text.setText(item.getName());
                indicator.setBackgroundResource(item.getColor().color());

                return view;
            }
        };

        adapter.addAll(projects);

        new AlertDialog.Builder(getContext())
                .setNegativeButton(R.string.cancel, (dialog, which) -> {})
                .setTitle(R.string.select_project)
                .setAdapter(adapter, (dialog, which) -> {
                    Project project = adapter.getItem(which);
                    presenter.setProject(project);
                })
                .show();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setTaskIdIfAny();

        if (isNewTask()) {
            titleView.requestFocus();
            presenter.setProject(DefaultTypes.PROJECT);
            presenter.setPriority(DefaultTypes.PRIORITY);
        }

        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_task_done);

        fab.setOnClickListener(v -> {
            presenter.setTitle(titleView.getText().toString());
//            presenter.setDescription(descriptionView.getText().toString());
            presenter.setCompleted(checkboxCompleted.isChecked());
            presenter.saveTask();
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_addedittask, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete:
                presenter.deleteTask();
                return true;
        }
        return false;
    }

    @Override
    public void showSelectPriorityDialog(List<Task.Priority> priorities) {
        ArrayAdapter<Task.Priority> adapter = new ArrayAdapter<Task.Priority>(
                getContext(), R.layout.dialog_item_priority) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view;

                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    view = inflater.inflate(R.layout.dialog_item_priority, parent, false);
                } else {
                    view = convertView;
                }

                TextView text = (TextView) view.findViewById(R.id.priority_text);
                View indicator = view.findViewById(R.id.priority_indicator);

                Task.Priority item = getItem(position);
                text.setText(item.priorityName());
                indicator.setBackgroundResource(item.color());

                return view;
            }
        };
        adapter.addAll(priorities);

        new AlertDialog.Builder(getContext())
                .setNegativeButton(R.string.cancel, (dialog, which) -> {})
                .setTitle(R.string.select_priority)
                .setAdapter(adapter, (dialog, which) -> {
                    Task.Priority priority = adapter.getItem(which);
                    presenter.setPriority(priority);
                })
                .show();
    }

    @Override
    public void showSelectDateDialog() {
//        new AlertDialog.Builder(getContext())
//                .setNegativeButton(R.string.cancel, (dialog, which) -> {})
//                .setPositiveButton(R.string.select, (dialog, which) -> {})
//                .setView();
    }

    @Override
    public void showEmptyTaskError() {
        Snackbar.make(titleView, R.string.empty_task_message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showTasksList() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void showTitle(String title) {
        this.titleView.setText(title);
    }

    @Override
    public void showDescription(String description) {
//        this.descriptionView.setText(description);
    }

    @Override
    public void showCompleted(boolean completed) {
        checkboxCompleted.setChecked(completed);
    }

    @Override
    public void showPriority(@StringRes int priorityName) {
        priorityParam.setText(getString(priorityName));
        paramsAdapter.notifyDataSetChanged();
    }

    @Override
    public void showProject(String name) {
        projectParam.setText(name);
        paramsAdapter.notifyDataSetChanged();
    }

    @Override
    public void showTask() {
        Log.d(TAG, "showTask");
        Intent intent = new Intent();
        intent.putExtra(AddEditTaskActivity.EXTRA_TASK_ID, editedTaskId);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void showTaskDeleted() {
        Intent intent = new Intent();
        intent.putExtra(AddEditTaskActivity.EXTRA_TASK_ID, editedTaskId);
        getActivity().setResult(AddEditTaskActivity.RESULT_TASK_DELETED, intent);
        getActivity().finish();
    }

    private void setTaskIdIfAny() {
        if (getArguments() != null && getArguments().containsKey(ARGUMENT_EDIT_TASK_ID)) {
            editedTaskId = getArguments().getString(ARGUMENT_EDIT_TASK_ID);
        }
    }

    private boolean isNewTask() {
        return editedTaskId == null;
    }
}
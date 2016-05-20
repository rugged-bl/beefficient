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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.beefficient.R;
import com.beefficient.data.entity.DefaultTypes;

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
    private TextView descriptionView;
    private CheckBox checkboxCompleted;
    private TextView projectView;
    private TextView dueDateView;
    private TextView priorityView;

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
//        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addedittask, container, false);
        titleView = (TextView) view.findViewById(R.id.task_title);
        descriptionView = (TextView) view.findViewById(R.id.task_description);
        checkboxCompleted = (CheckBox) view.findViewById(R.id.checkbox_completed);
        priorityView = (TextView) view.findViewById(R.id.task_priority);
        projectView = (TextView) view.findViewById(R.id.task_project);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setTaskIdIfAny();

        if (isNewTask()) {
            titleView.requestFocus();
            setProject(getString(R.string.inbox));
            setPriority(R.string.low);
        }

        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_task_done);

        fab.setOnClickListener(v -> presenter.saveTask(titleView.getText().toString(),
                descriptionView.getText().toString(), checkboxCompleted.isChecked(),
                DefaultTypes.PROJECT, 0, false));
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
    public void showEmptyTaskError() {
        Snackbar.make(titleView, getString(R.string.empty_task_message), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showTasksList() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void setTitle(String title) {
        this.titleView.setText(title);
    }

    @Override
    public void setDescription(String description) {
        this.descriptionView.setText(description);
    }

    @Override
    public void setCompleted(boolean completed) {
        checkboxCompleted.setChecked(completed);
    }

    @Override
    public void setPriority(@StringRes int priorityName) {
        priorityView.setText(getString(priorityName));
    }

    @Override
    public void setProject(String name) {
        projectView.setText(name);
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
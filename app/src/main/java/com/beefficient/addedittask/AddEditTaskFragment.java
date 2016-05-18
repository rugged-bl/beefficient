package com.beefficient.addedittask;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.beefficient.R;

import static com.beefficient.util.Objects.requireNonNull;

/**
 * Main UI for the add task screen. Users can enter a task title and description.
 */
public class AddEditTaskFragment extends Fragment implements AddEditTaskContract.View {

    public static final String ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID";

    private AddEditTaskContract.Presenter presenter;

    private TextView title;
    private TextView description;

    private String editedTaskId;
    private CheckBox checkboxCompleted;

    public static AddEditTaskFragment newInstance(String taskId) {
        AddEditTaskFragment fragment =  new AddEditTaskFragment();

        Bundle args = new Bundle();
        args.putString(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID, taskId);
        fragment.setArguments(args);

        return fragment;
    }

    public AddEditTaskFragment() {
        // Required empty public constructor
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addedittask, container, false);
        title = (TextView) view.findViewById(R.id.task_title);
        description = (TextView) view.findViewById(R.id.task_description);
        checkboxCompleted = (CheckBox) view.findViewById(R.id.checkbox_completed);

        setHasOptionsMenu(true);
        setRetainInstance(true);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setTaskIdIfAny();

        if (isNewTask()) {
            title.requestFocus();
        }

        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_task_done);

        fab.setOnClickListener(v -> {
            if (isNewTask()) {
                presenter.createTask(title.getText().toString(),
                        description.getText().toString(), checkboxCompleted.isChecked());
            } else {
                presenter.updateTask(title.getText().toString(),
                        description.getText().toString(), checkboxCompleted.isChecked());
            }

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
    public void showEmptyTaskError() {
        Snackbar.make(title, getString(R.string.empty_task_message), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showTasksList() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void setTitle(String title) {
        this.title.setText(title);
    }

    @Override
    public void setDescription(String description) {
        this.description.setText(description);
    }

    @Override
    public void setCompleted(boolean completed) {
        checkboxCompleted.setChecked(completed);
    }

    @Override
    public void showTaskDeleted() {
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
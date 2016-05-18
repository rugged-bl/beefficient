package com.beefficient.addedittask;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beefficient.R;

import static com.beefficient.util.Objects.requireNonNull;

/**
 * Main UI for the add task screen. Users can enter a task title and description.
 */
public class AddEditTaskFragment extends Fragment implements AddEditTaskContract.View {

    public static final String ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID";

    private AddEditTaskContract.Presenter mPresenter;

    private TextView title;

    private TextView description;

    private String mEditedTaskId;

    public static AddEditTaskFragment newInstance() {
        return new AddEditTaskFragment();
    }

    public AddEditTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(@NonNull AddEditTaskContract.Presenter presenter) {
        mPresenter = requireNonNull(presenter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setTaskIdIfAny();

        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_task_done);
        fab.setImageResource(R.drawable.ic_done);
        fab.setOnClickListener(v -> {
            if (isNewTask()) {
                mPresenter.createTask(
                        title.getText().toString(),
                        description.getText().toString());
            } else {
                mPresenter.updateTask(
                        title.getText().toString(),
                        description.getText().toString());
            }

        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addtask, container, false);
        title = (TextView) view.findViewById(R.id.task_title);
        description = (TextView) view.findViewById(R.id.task_description);

        setHasOptionsMenu(true);
        setRetainInstance(true);

        return view;
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
    public boolean isActive() {
        return isAdded();
    }

    private void setTaskIdIfAny() {
        if (getArguments() != null && getArguments().containsKey(ARGUMENT_EDIT_TASK_ID)) {
            mEditedTaskId = getArguments().getString(ARGUMENT_EDIT_TASK_ID);
        }
    }

    private boolean isNewTask() {
        return mEditedTaskId == null;
    }
}

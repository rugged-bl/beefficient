package com.beefficient.addedittask;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.beefficient.Injection;
import com.beefficient.R;
import com.beefficient.data.entity.Project;
import com.beefficient.data.entity.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.beefficient.util.Objects.requireNonNull;

public class AddEditTaskFragment extends Fragment implements
        AddEditTaskContract.View, SelectProjectDialogFragment.OnProjectSelectedListener,
        SelectPriorityDialogFragment.OnPrioritySelectedListener {

    private static final String TAG = "AddEditTaskFragment";

    public static final String ARGUMENT_EDIT_TASK = "EDIT_TASK";

    private AddEditTaskContract.Presenter presenter;

    private TextView titleView;
    private TextView descriptionView;

    private final TaskParam projectParam = new TaskParam(R.string.project, R.drawable.ic_project);
    private final TaskParam priorityParam =
            new TaskParam(R.string.priority, R.drawable.ic_priority);
    private final TaskParam dateParam = new TaskParam(R.string.due_date, R.drawable.ic_date_range);
    private final TaskParam labelsParam = new TaskParam(R.string.labels, R.drawable.ic_label);
    private final TaskParam remindersParam =
            new TaskParam(R.string.reminders, R.drawable.ic_reminder);

    private TaskParamsAdapter paramsAdapter;

    public AddEditTaskFragment() {
    }

    public static AddEditTaskFragment newInstance(Task task) {
        AddEditTaskFragment fragment = new AddEditTaskFragment();

        Bundle args = new Bundle();
        args.putSerializable(AddEditTaskFragment.ARGUMENT_EDIT_TASK, task);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void setPresenter(@NonNull AddEditTaskContract.Presenter presenter) {
        this.presenter = requireNonNull(presenter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        if (presenter == null) {
            Bundle args = getArguments();
            Task task = null;

            if (args != null && args.containsKey(ARGUMENT_EDIT_TASK)) {
                task = (Task) args.getSerializable(ARGUMENT_EDIT_TASK);
            }

            new AddEditTaskPresenter(task,
                    Injection.provideDataRepository(getContext().getApplicationContext()), this);
        }

        presenter.subscribe();

        // Configure params adapter
        List<TaskParam> params = Arrays.asList(
                projectParam, priorityParam, dateParam, labelsParam, remindersParam);

        paramsAdapter = new TaskParamsAdapter(params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addedittask, container, false);

        titleView = (TextView) getActivity().findViewById(R.id.task_title);
        descriptionView = (TextView) getActivity().findViewById(R.id.task_description);

        ListView paramsView = (ListView) view.findViewById(R.id.task_params);
        Log.d(TAG, "onCreateView: " + paramsView.getAdapter());
        paramsView.setAdapter(paramsAdapter);
        paramsView.setOnItemClickListener((parent, view1, position, id) -> {
            TaskParam param = paramsAdapter.getItem(position);
            if (param == projectParam) {
                presenter.selectProject();
            } else if (param == priorityParam) {
                presenter.selectPriority();
            } else if (param == dateParam) {
                presenter.selectDate();
            } else if (param == labelsParam) {

            }
        });

        if (presenter.isNewTask()) {
            titleView.requestFocus();
        }

        presenter.showTask();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_task_done);

        fab.setOnClickListener(v -> {
            presenter.setTitle(titleView.getText().toString());
            presenter.setDescription(descriptionView.getText().toString());
            presenter.saveTask();
        });

        Log.d(TAG, "onActivityCreated: " + presenter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.unsubscribe();
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
            case R.id.menu_item_complete_task:
                presenter.setCompleted(true);
                return true;
            case R.id.menu_item_clear_date:
                presenter.clearDueDate();
                return true;
        }
        return false;
    }

    @Override
    public void showSelectProjectDialog(ArrayList<Project> projects) {
        DialogFragment dialog = SelectProjectDialogFragment.newInstance(projects);
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), "select_project_dialog");
    }

    @Override
    public void showSelectPriorityDialog(ArrayList<Task.Priority> priorities) {
        DialogFragment dialog = SelectPriorityDialogFragment.newInstance(priorities);
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), "select_priority_dialog");
    }

    @Override
    public void showSelectDateDialog(int year, int monthOfYear, int dayOfMonth, int hourOfDay,
                                     int minute, boolean withTime) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, pickedYear, pickedMonthOfYear, pickedDayOfMonth) -> {
                    if (withTime) {
                        presenter.setDueDate(pickedYear, pickedMonthOfYear, pickedDayOfMonth,
                                hourOfDay, minute);
                    } else {
                        presenter.setDueDate(pickedYear, pickedMonthOfYear, pickedDayOfMonth, 0, 0);
                    }
                },
                year, monthOfYear, dayOfMonth);


        datePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, getText(R.string.time),
                (dialog, which) -> {
                    DatePicker datePicker = datePickerDialog.getDatePicker();
                    int pickedYear = datePicker.getYear();
                    int pickedMonthOfYear = datePicker.getMonth();
                    int pickedDayOfMonth = datePicker.getDayOfMonth();

                    showSelectTimeDialog(pickedYear, pickedMonthOfYear, pickedDayOfMonth,
                            hourOfDay, minute);
                });
        datePickerDialog.show();
    }

    private void showSelectTimeDialog(int year, int monthOfYear, int dayOfMonth, int hourOfDay,
                                      int minute) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, pickedHourOfDay, pickedMinute) -> {
                    presenter.setDueDate(year, monthOfYear, dayOfMonth, pickedHourOfDay,
                            pickedMinute);
                    presenter.setWithTime(true);
                },
                hourOfDay, minute, true);

        timePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, getText(R.string.clear_time),
                (dialog, which) -> {
                    presenter.setDueDate(year, monthOfYear, dayOfMonth, 0, 0);
                    presenter.setWithTime(false);
                });
        timePickerDialog.show();
    }

    @Override
    public void showEmptyTitleError() {
        Snackbar.make(titleView, R.string.empty_title_message, Snackbar.LENGTH_LONG).show();
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
    public void setPriority(@StringRes int priorityName) {
        priorityParam.setText(getString(priorityName));
        paramsAdapter.notifyDataSetChanged();
    }

    @Override
    public void setProject(String name) {
        projectParam.setText(name);
        paramsAdapter.notifyDataSetChanged();
    }

    @Override
    public void setDueDate(long dueDate) {
        String dueDateText;
        if (dueDate == 0) {
            dueDateText = getString(R.string.date_not_set);
        } else {
            dueDateText = (String) DateUtils.getRelativeDateTimeString(getContext(), dueDate,
                    DateUtils.DAY_IN_MILLIS, DateUtils.DAY_IN_MILLIS, 0);
        }

        dateParam.setText(dueDateText);
        paramsAdapter.notifyDataSetChanged();
    }

    @Override
    public void showTask() {
        Intent intent = new Intent();
        intent.putExtra(AddEditTaskActivity.EXTRA_TASK, presenter.getTask());
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void showTaskDeleted() {
        Intent intent = new Intent();
        intent.putExtra(AddEditTaskActivity.EXTRA_TASK, presenter.getTask());
        getActivity().setResult(AddEditTaskActivity.RESULT_TASK_DELETED, intent);
        getActivity().finish();
    }

    @Override
    public void onProjectSelected(Project project) {
        presenter.setProject(project);
    }

    @Override
    public void onPrioritySelected(Task.Priority priority) {
        presenter.setPriority(priority);
    }
}
package com.beefficient.addedittask;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.beefficient.R;
import com.beefficient.data.entity.Task;
import com.beefficient.util.ActivityUtils;

/**
 * Displays an add or edit task screen.
 */
public class AddEditTaskActivity extends AppCompatActivity {

    private static final String TAG = "AddEditTaskActivity";

    public static final int REQUEST_ADD_TASK = 1;
    public static final int REQUEST_EDIT_TASK = 2;

    public static final int RESULT_TASK_DELETED = 1;

    public static final String EXTRA_TASK = "TASK";

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addedittask);

        initAppBar();

        AddEditTaskFragment addEditTaskFragment =
                (AddEditTaskFragment) getSupportFragmentManager().findFragmentById(R.id.container);

        Task task = null;
        if (addEditTaskFragment == null) {
            if (getIntent().hasExtra(EXTRA_TASK)) {
                task = (Task) getIntent().getSerializableExtra(EXTRA_TASK);
                actionBar.setTitle(R.string.edit_task);
            } else {
                actionBar.setTitle(R.string.add_task);
            }

            addEditTaskFragment = AddEditTaskFragment.newInstance(task);
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    addEditTaskFragment, R.id.container);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void initAppBar() {
//        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

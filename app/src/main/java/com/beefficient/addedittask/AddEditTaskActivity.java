package com.beefficient.addedittask;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.beefficient.Injection;
import com.beefficient.R;
import com.beefficient.util.ActivityUtils;

/**
 * Displays an add or edit task screen.
 */
public class AddEditTaskActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_TASK = 1;
    public static final int REQUEST_EDIT_TASK = 2;

    public static final int RESULT_TASK_DELETED = 1;

    public static final String EXTRA_TASK_ID = "TASK_ID";

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addedittask);

        initAppBar();

        AddEditTaskFragment addEditTaskFragment =
                (AddEditTaskFragment) getSupportFragmentManager().findFragmentById(R.id.container);

        String taskId = null;
        if (addEditTaskFragment == null) {
            if (getIntent().hasExtra(EXTRA_TASK_ID)) {
                taskId = getIntent().getStringExtra(EXTRA_TASK_ID);
                actionBar.setTitle(R.string.edit_task);
            } else {
                actionBar.setTitle(R.string.add_task);
            }

            addEditTaskFragment = AddEditTaskFragment.newInstance(taskId);
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    addEditTaskFragment, R.id.container);
        }

        // Create the presenter
        new AddEditTaskPresenter(taskId,
                Injection.provideDataRepository(getApplicationContext()), addEditTaskFragment);
    }

    private void initAppBar() {
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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

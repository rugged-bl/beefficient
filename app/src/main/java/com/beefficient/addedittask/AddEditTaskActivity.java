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

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtask);

        initAppBar();

        AddEditTaskFragment addEditTaskFragment =
                (AddEditTaskFragment) getSupportFragmentManager().findFragmentById(R.id.container);

        String taskId = null;
        if (addEditTaskFragment == null) {
            addEditTaskFragment = AddEditTaskFragment.newInstance();

            if (getIntent().hasExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID)) {
                taskId = getIntent().getStringExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID);
                if (toolbar != null) {
                    toolbar.setTitle(R.string.edit_task);
                }
                Bundle bundle = new Bundle();
                bundle.putString(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID, taskId);
                addEditTaskFragment.setArguments(bundle);
            } else {
                if (toolbar != null) {
                    toolbar.setTitle(R.string.add_task);
                }
            }

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    addEditTaskFragment, R.id.container);
        }

        // Create the presenter
        new AddEditTaskPresenter(
                taskId,
                Injection.provideDataRepository(getApplicationContext()),
                addEditTaskFragment);
    }

    private void initAppBar() {
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
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

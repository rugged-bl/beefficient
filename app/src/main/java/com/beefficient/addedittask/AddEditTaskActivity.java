package com.beefficient.addedittask;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.beefficient.Injection;
import com.beefficient.R;
import com.beefficient.util.ActivityUtils;

/**
 * Displays an add or edit task screen.
 */
public class AddEditTaskActivity extends AppCompatActivity {

    private static final String TAG = "AddEditTaskActivity";

    public static final int REQUEST_ADD_TASK = 1;
    public static final int REQUEST_EDIT_TASK = 2;

    public static final int RESULT_TASK_DELETED = 1;

    public static final String EXTRA_TASK_ID = "TASK_ID";

    private static final String KEY_PRESENTER = "PRESENTER";

    private ActionBar actionBar;
    private AddEditTaskPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addedittask);

        initAppBar();

        AddEditTaskFragment addEditTaskFragment =
                (AddEditTaskFragment) getSupportFragmentManager().findFragmentById(R.id.container);

        String taskId = null;
        if (addEditTaskFragment == null) {
            Log.d(TAG, "onCreate: null");
            if (getIntent().hasExtra(EXTRA_TASK_ID)) {
                taskId = getIntent().getStringExtra(EXTRA_TASK_ID);
                actionBar.setTitle(R.string.edit_task);
            } else {
                actionBar.setTitle(R.string.add_task);
            }

            addEditTaskFragment = AddEditTaskFragment.newInstance(taskId);
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    addEditTaskFragment, R.id.container);
        } else {
            Log.d(TAG, "onCreate: exist");
        }

        // Create the presenter
        if (savedInstanceState == null) {
            presenter = new AddEditTaskPresenter(taskId,
                    Injection.provideDataRepository(getApplicationContext()), addEditTaskFragment);
        } else if (savedInstanceState.containsKey(KEY_PRESENTER)) {
            presenter = (AddEditTaskPresenter) savedInstanceState.getSerializable(KEY_PRESENTER);
            if (presenter != null) {
                addEditTaskFragment.setPresenter(presenter);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putSerializable(KEY_PRESENTER, presenter);
    }

    private void initAppBar() {
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
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

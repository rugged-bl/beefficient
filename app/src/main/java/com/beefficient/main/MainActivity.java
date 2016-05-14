package com.beefficient.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.beefficient.R;
import com.beefficient.tasks.TasksFragment;

public class MainActivity extends AppCompatActivity implements
        MainContract.View, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private MainContract.Presenter presenter;

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle drawerToggle;
    private CoordinatorLayout coordinatorLayout;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new MainPresenter(this);

        initAppBar();
        initDrawer();
        initNavigationView();

        initSwipeRefreshLayout();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        loadTasksFragment();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            // Close drawer on back pressed
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private void initAppBar() {
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        if (drawer != null) {
            drawer.addDrawerListener(drawerToggle);
        }
    }

    private void initNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }
    }


    private void initSwipeRefreshLayout() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.orange_400),
                ContextCompat.getColor(this, R.color.light_blue_a700));
    }

    private void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_overview: {
                loadTasksFragment();
                break;
            }
            case R.id.nav_today: {
                // TODO: set TasksFragment with today tasks
                break;
            }
            case R.id.nav_next_week: {
                // TODO: set TasksFragment with next week tasks
                break;
            }
            case R.id.nav_calendar: {
                // TODO: set calendar fragment (TasksFragment?)
                break;
            }
            case R.id.nav_task_inbox: {
                // TODO: set TasksFragment with Inbox project tasks
                break;
            }
            case R.id.nav_projects: {
                // TODO: set ProjectsFragment (dialog?)
                break;
            }
            case R.id.nav_labels: {
                // TODO: set LabelsFragment (dialog?)
                break;
            }
            case R.id.nav_settings: {
                // TODO: start SettingsActivity
                break;
            }
        }

        appBarLayout.setExpanded(true);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void showSnackbar(CharSequence text, int duration) {
        Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void loadTasksFragment() {
        setFragment(TasksFragment.newInstance());
    }

    @Override
    public void loadProjectsFragment() {

    }

    @Override
    public void loadLabelsFragment() {

    }

    @Override
    public void loadTaskDetailsFragment() {

    }

    @Override
    public void setPresenter(@NonNull MainContract.Presenter presenter) {
        this.presenter = presenter;
    }


}
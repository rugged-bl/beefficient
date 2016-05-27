package com.beefficient.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.beefficient.BackgroundService;
import com.beefficient.R;
import com.beefficient.projects.ProjectsFragment;
import com.beefficient.tasks.TasksFragment;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intentService = new Intent(this, BackgroundService.class);
        startService(intentService);

        initAppBar();
        initDrawer();
        initNavigationView();

        initSwipeRefreshLayout();

        loadTasksFragment();
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
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
        SwipeRefreshLayout swipeRefreshLayout =
                (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setColorSchemeColors(
                    ContextCompat.getColor(this, R.color.accent),
                    ContextCompat.getColor(this, R.color.primary));
        }
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
                loadTasksFragment();
                break;
            }
            case R.id.nav_next_week: {
                // TODO: set TasksFragment with next week tasks
                loadTasksFragment();
                break;
            }
            case R.id.nav_calendar: {
                // TODO: set calendar fragment (TasksFragment?)
                break;
            }
            case R.id.nav_task_inbox: {
                // TODO: set TasksFragment with Inbox project tasks
                loadTasksFragment();
                break;
            }
            case R.id.nav_projects: {
                // TODO: set ProjectsFragment (dialog?)
                loadProjectsFragment();
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

    private void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    private void loadTasksFragment() {
        setFragment(TasksFragment.newInstance());
        setTitle(R.string.all_tasks);
    }

    private void loadProjectsFragment() {
        setFragment(ProjectsFragment.newInstance());
        setTitle(R.string.projects);
    }
}
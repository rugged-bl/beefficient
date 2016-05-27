package com.beefficient.projects;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beefficient.Injection;
import com.beefficient.R;

public class ProjectsFragment extends Fragment implements ProjectsContract.View {

    private ProjectsContract.Presenter presenter;
    private RecyclerView projectsView;
    private View noProjectsContainer;
    private TextView noProjectsView;
    private SwipeRefreshLayout swipeRefreshLayout;

    public ProjectsFragment() {
    }

    public static ProjectsFragment newInstance() {
        ProjectsFragment fragment = new ProjectsFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        // Create and bind presenter
        new ProjectsPresenter(
                Injection.provideDataRepository(getContext().getApplicationContext()), this);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        projectsView = (RecyclerView) view.findViewById(R.id.projects_view);
//        tasksView.setAdapter(tasksAdapter);

        noProjectsContainer = view.findViewById(R.id.no_projects_container);
        noProjectsView = (TextView) view.findViewById(R.id.no_projects);

        Activity activity = getActivity();
        swipeRefreshLayout = (SwipeRefreshLayout) activity.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> presenter.loadProjects(true));

        FloatingActionButton addTaskButton =
                (FloatingActionButton) activity.findViewById(R.id.fab_add);
        addTaskButton.setOnClickListener(v -> presenter.addNewProject());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void setPresenter(@NonNull ProjectsContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showAddProject() {
        // TODO
    }
}

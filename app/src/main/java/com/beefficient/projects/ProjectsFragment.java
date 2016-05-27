package com.beefficient.projects;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beefficient.Injection;
import com.beefficient.tasks.TasksPresenter;

public class ProjectsFragment extends Fragment implements ProjectsContract.View {

    private ProjectsContract.Presenter presenter;

    public ProjectsFragment() {
    }

    public static ProjectsFragment newInstance() {
        ProjectsFragment fragment = new ProjectsFragment();

        Bundle args = new Bundle();
//        args.putSerializable(AddEditTaskFragment.ARGUMENT_EDIT_TASK, task);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void setPresenter(@NonNull ProjectsContract.Presenter presenter) {
        this.presenter = presenter;
    }
}

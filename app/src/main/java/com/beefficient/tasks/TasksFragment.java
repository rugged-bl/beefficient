package com.beefficient.tasks;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beefficient.R;
import com.beefficient.data.Project;
import com.beefficient.data.Task;

import org.ocpsoft.prettytime.shade.org.apache.commons.lang.math.RandomUtils;

import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment implements TasksContract.View {

    private TasksContract.Presenter presenter;
//    private OnListFragmentInteractionListener listener;

    public TasksFragment() {
    }

    @SuppressWarnings("unused")
    public static TasksFragment newInstance() {
        TasksFragment fragment = new TasksFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (getArguments() != null) {
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            List<Task> taskList = new ArrayList<>();
            Task.Builder task = new Task.Builder("Title")
                    .setProject(new Project("Project"))
                    .setTime(System.currentTimeMillis());

            for (int i = 0; i < 50; i++) {
                taskList.add(task.setCompleted(RandomUtils.nextBoolean())
                        .setPriority(Task.Priority.values()[RandomUtils.nextInt(3)]).build());
            }
            recyclerView.setAdapter(new TasksAdapter(taskList));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnListFragmentInteractionListener) {
//            listener = (OnListFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        listener = null;
    }

    @Override
    public void setPresenter(@NonNull TasksContract.Presenter presenter) {
        this.presenter = presenter;
    }

//    public interface OnListFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onListFragmentInteraction(Task item);
//    }
}

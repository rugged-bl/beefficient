package com.beefficient.addedittask;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.beefficient.R;
import com.beefficient.data.entity.Project;

import java.util.ArrayList;

public class SelectProjectDialogFragment extends AppCompatDialogFragment {

    private static final String ARGUMENT_PROJECTS = "PROJECTS";

    public static SelectProjectDialogFragment newInstance(ArrayList<Project> projects) {
        SelectProjectDialogFragment f = new SelectProjectDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARGUMENT_PROJECTS, projects);
        f.setArguments(args);

        return f;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ArrayList<Project> projects = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            projects = (ArrayList<Project>) bundle.getSerializable(ARGUMENT_PROJECTS);
        }

        ArrayAdapter<Project> adapter = new ArrayAdapter<Project>(getContext(),
                R.layout.dialog_item_project) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view;

                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    view = inflater.inflate(R.layout.dialog_item_project, parent, false);
                } else {
                    view = convertView;
                }

                TextView text = (TextView) view.findViewById(R.id.project_name);
                AppCompatImageView color =
                        (AppCompatImageView) view.findViewById(R.id.project_color);

                Project project = getItem(position);
                text.setText(project.getName());

                // Set project color
                Drawable drawable = DrawableCompat.wrap(color.getDrawable());
                color.setImageDrawable(drawable);
                DrawableCompat.setTint(drawable,
                        ResourcesCompat.getColor(getResources(), project.getColor().color(), null));

                return view;
            }
        };

        adapter.addAll(projects);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.select_project)
                .setNegativeButton(R.string.cancel, (dialog1, which) -> {})
                .setAdapter(adapter, (dialog1, which) -> {
                    Project project = adapter.getItem(which);
                    OnProjectSelectedListener listener =
                            (OnProjectSelectedListener) getTargetFragment();

                    listener.onProjectSelected(project);
                })
                .create();

        return dialog;
    }

    public interface OnProjectSelectedListener {
        void onProjectSelected(Project project);
    }
}
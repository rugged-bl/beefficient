package com.beefficient.addedittask;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.beefficient.R;
import com.beefficient.data.entity.Task;

import java.util.ArrayList;

public class SelectPriorityDialogFragment extends AppCompatDialogFragment {

    private static final String ARGUMENT_PRIORITIES = "PRIORITIES";

    public static SelectPriorityDialogFragment newInstance(ArrayList<Task.Priority> priorities) {
        SelectPriorityDialogFragment f = new SelectPriorityDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARGUMENT_PRIORITIES, priorities);
        f.setArguments(args);

        return f;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ArrayList<Task.Priority> priorities = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            priorities = (ArrayList<Task.Priority>) bundle.getSerializable(ARGUMENT_PRIORITIES);
        }

        ArrayAdapter<Task.Priority> adapter = new ArrayAdapter<Task.Priority>(getContext(),
                R.layout.dialog_item_priority) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view;

                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    view = inflater.inflate(R.layout.dialog_item_priority, parent, false);
                } else {
                    view = convertView;
                }

                TextView text = (TextView) view.findViewById(R.id.priority_text);
                View indicator = view.findViewById(R.id.priority_indicator);

                Task.Priority item = getItem(position);
                text.setText(item.priorityName());
                indicator.setBackgroundResource(item.color());

                return view;
            }
        };

        adapter.addAll(priorities);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.select_priority)
                .setNegativeButton(R.string.cancel, (dialog1, which) -> {})
                .setAdapter(adapter, (dialog1, which) -> {
                    Task.Priority priority = adapter.getItem(which);
                    OnPrioritySelectedListener listener =
                            (OnPrioritySelectedListener) getTargetFragment();

                    listener.onPrioritySelected(priority);
                })
                .create();

        return dialog;
    }

    public interface OnPrioritySelectedListener {
        void onPrioritySelected(Task.Priority priority);
    }
}

package com.beefficient.tasks;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.beefficient.R;
import com.beefficient.data.Label;
import com.beefficient.data.Task;

import java.util.Date;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {

    private final List<Task> values;
//    private final OnListFragmentInteractionListener listener;

    public TasksAdapter(List<Task> items) {
        values = items;
//        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = values.get(position);

        Task t = holder.item;

        holder.title.setText(t.getTitle());
        holder.project.setText(t.getProject().getName());
        holder.done.setChecked(t.isCompleted());
        holder.priority.setBackgroundResource(t.getPriority().colorRes());

        Date dueDate = new Date(t.getTime());
        if (t.getTime() != 0) {
            holder.dueDate.setText(DateUtils.getRelativeDateTimeString(
                    holder.itemView.getContext(), dueDate.getTime(), DateUtils.DAY_IN_MILLIS,
                    DateUtils.DAY_IN_MILLIS, 0));

            holder.dueDate.setVisibility(View.VISIBLE);
        } else {
            holder.dueDate.setVisibility(View.GONE);
        }

        if (t.getLabelList().isEmpty()) {
            holder.labels.setVisibility(View.GONE);
        } else {
            StringBuilder labelBuilder = new StringBuilder();
            for (Label label : t.getLabelList()) {
                labelBuilder.append(label.getName()).append(" ");
            }
            holder.labels.setText(labelBuilder);
            holder.labels.setVisibility(View.VISIBLE);
        }

//        holder.itemView.setOnClickListener(v -> {
////            if (null != listener) {
////                // Notify the active callbacks interface (the activity, if the
////                // fragment is attached to one) that an item has been selected.
////                listener.onListFragmentInteraction(holder.item);
////            }
//        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Task item;
        public View priority;
        public CheckBox done;
        public TextView title;
        public TextView labels;
        public TextView dueDate;
        public TextView project;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            project = (TextView) view.findViewById(R.id.project);
            dueDate = (TextView) view.findViewById(R.id.due_date);
            labels = (TextView) view.findViewById(R.id.labels);
            done = (CheckBox) view.findViewById(R.id.checkbox_done);
            priority = view.findViewById(R.id.priority_indicator);
        }
    }
}

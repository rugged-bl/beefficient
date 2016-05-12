package com.beefficient.tasks;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.beefficient.R;
import com.beefficient.data.entity.Label;
import com.beefficient.data.entity.Project;
import com.beefficient.data.entity.Task;

import java.util.Date;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {

    private List<Task> tasks;
    private OnItemClickListener listener;

    public TasksAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(viewHolder.task);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.task = tasks.get(position);

        Task task = holder.task;
        Project project = task.getProject();

        if (project != null) {
            holder.project.setText(project.getName());
            holder.project.setVisibility(View.VISIBLE);
        } else {
            holder.project.setVisibility(View.GONE);
        }
        holder.title.setText(task.getTitle());
        holder.done.setChecked(task.isCompleted());
        holder.priority.setBackgroundResource(task.getPriority().colorRes());

        Date dueDate = new Date(task.getTime());
        if (task.getTime() != 0) {
            holder.dueDate.setText(DateUtils.getRelativeDateTimeString(
                    holder.itemView.getContext(), dueDate.getTime(), DateUtils.DAY_IN_MILLIS,
                    DateUtils.DAY_IN_MILLIS, 0));

            holder.dueDate.setVisibility(View.VISIBLE);
        } else {
            holder.dueDate.setVisibility(View.GONE);
        }

        if (task.getLabelList().isEmpty()) {
            holder.labels.setVisibility(View.GONE);
        } else {
            StringBuilder labelBuilder = new StringBuilder();
            for (Label label : task.getLabelList()) {
                labelBuilder.append(label.getName()).append(" ");
            }
            holder.labels.setText(labelBuilder);
            holder.labels.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Task task;
        public View priority;
        public CheckBox done;
        public TextView title;
        public TextView labels;
        public TextView dueDate;
        public TextView project;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            project = (TextView) itemView.findViewById(R.id.project);
            dueDate = (TextView) itemView.findViewById(R.id.due_date);
            labels = (TextView) itemView.findViewById(R.id.labels);
            done = (CheckBox) itemView.findViewById(R.id.checkbox_done);
            priority = itemView.findViewById(R.id.priority_indicator);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Task task);
    }
}

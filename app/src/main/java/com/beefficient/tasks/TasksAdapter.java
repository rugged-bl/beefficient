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
import java.util.HashMap;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_SECTION = 1;

    public static class SectionItem {
        CharSequence title;

        public SectionItem(CharSequence title) {
            this.title = title;
        }

        public CharSequence getTitle() {
            return title;
        }

        public void setTitle(CharSequence title) {
            this.title = title;
        }
    }

    public static class TaskItem {
        private SectionItem section;
        private Task task;

        public TaskItem(Task task, SectionItem section) {
            this.section = section;
            this.task = task;
        }

        public SectionItem getSection() {
            return section;
        }

        public void setSection(SectionItem section) {
            this.section = section;
        }

        public Task getTask() {
            return task;
        }

        public void setTask(Task task) {
            this.task = task;
        }
    }

    private List<TaskItem> taskItems;
    private HashMap<Integer, SectionItem> sectionItems;
    private OnItemClickListener listener;

    public TasksAdapter(List<TaskItem> taskItems) {
        this.taskItems = taskItems;
    }

    public void setTaskItems(List<TaskItem> taskItems) {
        this.taskItems = taskItems;
        notifyDataSetChanged();
    }

    public void setSectionItems(HashMap<Integer, SectionItem> sectionItems) {
        this.sectionItems = sectionItems;
    }

//    public void setSortLinks(HashMap<Integer, Integer> sortLinks) {
//        this.sortLinks = sortLinks;
//        keysSet = sortLinks.keySet();
//    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;

        if (viewType == VIEW_TYPE_ITEM) {
            final View view = inflater.inflate(R.layout.item_task, parent, false);

            TaskViewHolder taskViewHolder = new TaskViewHolder(view);
            taskViewHolder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(taskViewHolder.item.getTask());
                }
            });

            taskViewHolder.itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onLongItemClick(taskViewHolder.item.getTask());
                }
                return true;
            });

            viewHolder = taskViewHolder;
        } else if (viewType == VIEW_TYPE_SECTION) {
            final View view = inflater.inflate(R.layout.item_task_section, parent, false);
            viewHolder = new SectionViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TaskViewHolder) {
            TaskViewHolder taskHolder = (TaskViewHolder) holder;

            int itemOffset = 0;
            for (int sectionPosition : sectionItems.keySet()) {
                if (position >= sectionPosition) {
                    itemOffset--;
                }
            }

            taskHolder.item = taskItems.get(position + itemOffset);

            Task task = taskHolder.item.getTask();
            Project project = task.getProject();

            if (project != null) {
                taskHolder.project.setText(project.getName());
                taskHolder.project.setVisibility(View.VISIBLE);
            } else {
                taskHolder.project.setVisibility(View.GONE);
            }
            taskHolder.title.setText(task.getTitle());
            taskHolder.done.setChecked(task.isCompleted());
            taskHolder.priority.setBackgroundResource(task.getPriority().colorRes());

            Date dueDate = new Date(task.getTime());
            if (task.getTime() != 0) {
                taskHolder.dueDate.setText(DateUtils.getRelativeDateTimeString(
                        taskHolder.itemView.getContext(), dueDate.getTime(), DateUtils.DAY_IN_MILLIS,
                        DateUtils.DAY_IN_MILLIS, 0));

                taskHolder.dueDate.setVisibility(View.VISIBLE);
            } else {
                taskHolder.dueDate.setVisibility(View.GONE);
            }

            if (task.getLabelList().isEmpty()) {
                taskHolder.labels.setVisibility(View.GONE);
            } else {
                StringBuilder labelBuilder = new StringBuilder();
                for (Label label : task.getLabelList()) {
                    labelBuilder.append(label.getName()).append(" ");
                }
                taskHolder.labels.setText(labelBuilder);
                taskHolder.labels.setVisibility(View.VISIBLE);
            }
        } else if (holder instanceof SectionViewHolder) {
            SectionViewHolder sectionHolder = (SectionViewHolder) holder;
            sectionHolder.item = sectionItems.get(position);
            sectionHolder.title.setText(sectionHolder.item.getTitle());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (sectionItems.containsKey(position)) ? VIEW_TYPE_SECTION : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return taskItems.size() + sectionItems.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        public TaskItem item;
        public View priority;
        public CheckBox done;
        public TextView title;
        public TextView labels;
        public TextView dueDate;
        public TextView project;

        public TaskViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            project = (TextView) itemView.findViewById(R.id.project);
            dueDate = (TextView) itemView.findViewById(R.id.due_date);
            labels = (TextView) itemView.findViewById(R.id.labels);
            done = (CheckBox) itemView.findViewById(R.id.checkbox_done);
            priority = itemView.findViewById(R.id.priority_indicator);
        }
    }

    public class SectionViewHolder extends RecyclerView.ViewHolder {
        public SectionItem item;
        public TextView title;

        public SectionViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Task task);

        void onLongItemClick(Task task);
    }
}

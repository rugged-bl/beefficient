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

public class TasksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private static final int VIEW_TYPE_ITEM = 0;
	private static final int VIEW_TYPE_SECTION = 1;
	
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		RecyclerView.ViewHolder viewHolder = null;
		if (viewType == VIEW_TYPE_ITEM) {
			final View view = inflater.inflate(R.layout.item_task, parent, false);
			
        	viewHolder = new ViewHolder(view);
			viewHolder.itemView.setOnClickListener(v -> {
				if (listener != null) {
					listener.onItemClick(((ViewHolder) viewHolder).task);
				}
			});
		} else if (viewType == VIEW_TYPE_SECTION) {
			final View view = inflater.inflate(R.layout.item_task_section, parent, false);
        	viewHolder = new SectionViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
		int itemViewType = holder.getItemViewType();
		if (holder instanceof ViewHolder) {
			ViewHolder taskHolder = (ViewHolder) holder;
        	taskHolder.task = tasks.get(position);

        	Task task = taskHolder.task;
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
			sectionHolder.title.setText("test"); // TODO
		}
    }

	@Override
	public int getItemViewType(int position) {
		return VIEW_TYPE_ITEM; // TODO
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
	
	public class SectionViewHolder extends RecyclerView.ViewHolder {
		public TextView title;
		
		public SectionViewHolder(View itemView) {
			super(itemView);
			title = (TextView) itemView.findViewById(R.id.title);
		}
	}

    public interface OnItemClickListener {
        void onItemClick(Task task);
    }
}

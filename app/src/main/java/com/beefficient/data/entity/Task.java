package com.beefficient.data.entity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.beefficient.R;
import com.beefficient.util.Objects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Task {
    private Project project;
    private String projectId;

    private String id;

    private String title;
    @Nullable
    private String description;
    private Priority priority;
    private boolean completed;
    private boolean withTime;
    private long time;

    private List<Label> labelList;

    public Task(String title) {
        this.title = title;
        this.priority = Priority.LOW;
    }

    public static class Builder {
        private Project project;
        private String projectId;

        private String id;

        private String title;
        @Nullable
        private String description;
        private Priority priority = Priority.LOW;
        private boolean completed;
        private boolean withTime;
        private long time;

        private List<Label> labelList = new ArrayList<>();

        /**
         * Use this constructor to create a copy of another Task.
         *
         * @param task task
         */
        public Builder(Task task) {
            this.project = task.project;
            this.projectId = task.projectId;
            this.id = task.id;
            this.title = task.title;
            this.description = task.description;
            this.priority = task.priority;
            this.completed = task.completed;
            this.withTime = task.withTime;
            this.time = task.time;
            this.labelList = labelList;
        }

        /**
         * Use this constructor to create a new active Task.
         *
         * @param title title
         */
        public Builder(String title) {
            id = UUID.randomUUID().toString();
            this.title = title;
        }
        /**
         * Use this constructor to specify a Task if the Task already has an id (copy of
         * another Task).
         *
         * @param title title
         * @param id unique id
         */
        public Builder(String title, String id) {
            this.id = id;
            this.title = title;
        }

        public Builder setProject(@NonNull Project project) {
            this.project = project;
            return this;
        }

        public Builder setProjectId(@NonNull String projectId) {
            this.projectId = projectId;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setDescription(@Nullable String description) {
            this.description = description;
            return this;
        }

        public Builder setCompleted(boolean completed) {
            this.completed = completed;
            return this;
        }

        public Builder setPriority(Priority priority) {
            this.priority = priority;
            return this;
        }

        public Builder setTime(long time) {
            this.time = time;
            return this;
        }

        public Builder setWithTime(boolean withTime) {
            this.withTime = withTime;
            return this;
        }

        public Builder setLabelList(List<Label> labelList) {
            this.labelList = new ArrayList<>(labelList);
            return this;
        }

        public Task build() {
            Task task = new Task(title);
            task.id = id;
            task.description = description;
            task.priority = priority;
            task.completed = completed;
            task.withTime = withTime;
            task.time = time;
            task.labelList = labelList;
            task.project = project;
            task.projectId = projectId;

            if (project != null) {
                task.projectId = project.getId();
                project.addTask(task);
            }

            return task;
        }
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProject(@NonNull Project project) {
        this.project = project;
        projectId = project.getId();
        project.addTask(this);
    }

    public Project getProject() {
        return project;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public Priority getPriority() {
        return priority;
    }

    public long getTime() {
        return time;
    }

    public boolean isWithTime() {
        return withTime;
    }

    public boolean hasLabel(Label label) {
        return labelList.contains(label);
    }

    public List<Label> getLabelList() {
        return labelList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(task, task.title) &&
                Objects.equals(task, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, title, description);
    }

    @Override
    public String toString() {
        return title;
    }

    public enum Priority {
        LOW(R.color.colorLowPriority, R.string.low),
        MEDIUM(R.color.colorMediumPriority, R.string.medium),
        HIGH(R.color.colorHighPriority, R.string.high),
        VERY_HIGH(R.color.colorVeryHighPriority, R.string.very_high);

        private final int color;
        private final int priorityName;

        Priority(int color, int priorityName) {
            this.color = color;
            this.priorityName = priorityName;
        }

        public int color() {
            return color;
        }

        public int priorityName() {
            return priorityName;
        }
    }
}

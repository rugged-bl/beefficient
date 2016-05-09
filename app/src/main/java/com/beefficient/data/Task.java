package com.beefficient.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.beefficient.R;
import com.beefficient.util.ObjectUtils;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Task {
    private WeakReference<Project> project;

    private String id;

    private String title;
    @Nullable
    private String description;
    private Priority priority;
    private boolean completed;
    private boolean onlyDate;
    private long time;

    private List<Label> labelList;

    public Task(String title) {
        this.title = title;
        this.priority = Priority.LOW;
    }

    public static class Builder {
        private WeakReference<Project> project;

        private String id;

        private String title;
        @Nullable
        private String description;
        private Priority priority = Priority.LOW;
        private boolean completed;
        private boolean onlyDate;
        private long time;

        private List<Label> labelList = new ArrayList<>();

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
         * Use this builder to specify a Task if the Task already has an id (copy of
         * another Task).
         *
         * @param title title
         * @param id unique id
         */
        public Builder(String title, String id) {
            this.id = id;

            this.title = title;
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

        public Builder setOnlyDate(boolean onlyDate) {
            this.onlyDate = onlyDate;
            return this;
        }

        public Builder setLabelList(List<Label> labelList) {
            this.labelList = new ArrayList<>(labelList);
            return this;
        }

        public Builder setProject(@NonNull WeakReference<Project> project) {
            this.project = project;
            return this;
        }

        public Task build() {
            Task task = new Task(title);
            task.id = id;
            task.description = description;
            task.priority = priority;
            task.completed = completed;
            task.onlyDate = onlyDate;
            task.time = time;
            task.labelList = labelList;
            task.project = project;
            if (project != null)
                project.get().addTask(new SoftReference<>(task));

            return task;
        }
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

    public boolean isOnlyDate() {
        return onlyDate;
    }

    public boolean hasLabel(Label label) {
        return labelList.contains(label);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return ObjectUtils.equals(task, task.title) && ObjectUtils.equals(task, task.description);
    }

    @Override
    public int hashCode() {
        return ObjectUtils.hashCode(title, description);
    }

    @Override
    public String toString() {
        return title;
    }

    public enum Priority {
        LOW(R.color.colorLowPriority),
        MEDIUM(R.color.colorMediumPriority),
        HIGH(R.color.colorHighPriority),
        VERY_HIGH(R.color.colorVeryHighPriority);

        private final int color;

        Priority(int color) {
            this.color = color;
        }

        public int colorRes() {
            return color;
        }
    }
}

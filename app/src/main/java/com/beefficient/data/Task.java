package com.beefficient.data;

import android.support.annotation.Nullable;

import com.beefficient.R;
import com.beefficient.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

public class Task {
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
        private String title;

        @Nullable
        private String description;

        private Priority priority = Priority.LOW;
        private boolean completed;
        private boolean onlyDate;
        private long time;

        private List<Label> labelList = new ArrayList<>();

        public Builder(String title) {
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

        public Task build() {
            Task task = new Task(title);
            task.description = description;
            task.priority = priority;
            task.completed = completed;
            task.onlyDate = onlyDate;
            task.time = time;
            task.labelList = labelList;

            return task;
        }
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

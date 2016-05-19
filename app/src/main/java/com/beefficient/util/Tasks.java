package com.beefficient.util;

import com.beefficient.data.entity.Task;

public final class Tasks {

    private Tasks() {
    }

    public static Task.Priority getPriorityFromIndex(int priorityIndex) {
        int maxIndex = Task.Priority.values().length - 1;
        return Task.Priority.values()[Math.max(Math.min(priorityIndex, maxIndex), 0)];
    }
}

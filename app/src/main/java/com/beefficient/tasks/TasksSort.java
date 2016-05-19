package com.beefficient.tasks;

import android.text.format.DateFormat;
import android.text.format.DateUtils;

import com.beefficient.data.entity.DefaultTypes;
import com.beefficient.data.entity.Project;
import com.beefficient.data.entity.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public final class TasksSort {
    private TasksSort() {
    }

    static void groupByProject(ArrayList<TasksAdapter.TaskItem> taskItems, HashMap<Integer, TasksAdapter.SectionItem> sectionItems, List<Task> tasks, List<Project> projects) {
        int position = 0;

        Project projectsArr[] = new Project[projects.size()];
        projects.toArray(projectsArr);
        Arrays.sort(projectsArr, (lhs, rhs) -> {
            if (lhs.getId().compareTo(DefaultTypes.PROJECT.getId()) == 0)
                return -1;
            if (rhs.getId().compareTo(DefaultTypes.PROJECT.getId()) == 0)
                return 1;

            return lhs.getName().compareTo(rhs.getName());
        });

        for (Project project : projectsArr) {
            TasksAdapter.SectionItem sectionItem =
                    new TasksAdapter.SectionItem(project.getName());
            sectionItems.put(position++, sectionItem);

            for (Task task : tasks) {
                if (!task.getProjectId().isEmpty() && task.getProjectId().equals(project.getId())) {
                    task.setProject(project);
                    taskItems.add(new TasksAdapter.TaskItem(task, sectionItem));
                    position++;
                }
            }
        }
    }

    static void groupByDate(ArrayList<TasksAdapter.TaskItem> taskItems, HashMap<Integer, TasksAdapter.SectionItem> sectionItems, List<Task> tasks) {
        int position = 0;

        HashSet<Long> taskTimes = new HashSet<>();
        for (Task task : tasks) {
            taskTimes.add(task.getTime());
        }
        Long taskTimesSorted[] = new Long[taskTimes.size()];
        taskTimes.toArray(taskTimesSorted);
        Arrays.sort(taskTimesSorted);
        long curTime = System.currentTimeMillis() / 1000L;

        for (Long time : taskTimesSorted) {
            TasksAdapter.SectionItem sectionItem =
                    new TasksAdapter.SectionItem(time == 0 ?
                            "Inbox" :
                            DateUtils.formatSameDayTime(time, curTime, 2, 2));
                            /*DateUtils.getRelativeTimeSpanString(
                            time, curTime,
                            DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE));*/
            sectionItems.put(position++, sectionItem);

            for (Task task : tasks) {
                if (task.getTime() == time) {
                    taskItems.add(new TasksAdapter.TaskItem(task, sectionItem));
                    position++;
                }
            }
        }
    }

    static void requireAllTasksHaveProject(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getProjectId() == null || tasks.get(i).getProject() == null) {
                tasks.get(i).setProject(DefaultTypes.PROJECT);
            }
        }
    }

}

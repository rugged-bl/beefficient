package com.beefficient.tasks;

import android.content.Context;

import com.beefficient.Application;
import com.beefficient.R;
import com.beefficient.data.entity.DefaultTypes;
import com.beefficient.data.entity.Project;
import com.beefficient.data.entity.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public abstract class TasksSort {
    static Context context = Application.getContext();

    public static void groupByProject(ArrayList<TasksAdapter.TaskItem> taskItems, HashMap<Integer, TasksAdapter.SectionItem> sectionItems, List<Task> tasks, List<Project> projects) {
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
            if (project.getTaskList() == null || project.getTaskList().isEmpty()) {
                continue;
            }
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

    public static final class Period {
        private static int position;

        private Period() {
        }

        public static void groupByDate(ArrayList<TasksAdapter.TaskItem> taskItems,
                                       HashMap<Integer, TasksAdapter.SectionItem> sectionItems,
                                       List<Task> tasks) {
//        HashSet<Long> taskTimes = new HashSet<>();
//        for (Task task : tasks) {
//            taskTimes.add(task.getTime());
//        }
//        Long taskTimesSorted[] = new Long[taskTimes.size()];
//        taskTimes.toArray(taskTimesSorted);
//        Arrays.sort(taskTimesSorted);

            long curTime = System.currentTimeMillis();

            Calendar todayEnds = Calendar.getInstance();
            todayEnds.setTimeZone(TimeZone.getDefault());
            todayEnds.setTime(new Date(curTime));
            todayEnds.set(Calendar.HOUR, 23);
            todayEnds.set(Calendar.MINUTE, 59);
            todayEnds.set(Calendar.SECOND, 59);

            Calendar tomorrowEnds = Calendar.getInstance();
            tomorrowEnds.setTime(todayEnds.getTime());
            tomorrowEnds.add(Calendar.DAY_OF_MONTH, 1);

            Calendar weekEnds = Calendar.getInstance();
            weekEnds.setTime(todayEnds.getTime());
            weekEnds.add(Calendar.DAY_OF_MONTH, 7);

            Calendar nextWeekEnds = Calendar.getInstance();
            nextWeekEnds.setTime(weekEnds.getTime());
            nextWeekEnds.add(Calendar.DAY_OF_MONTH, 7);

        /*ArrayList<Long> timesArray = new ArrayList<>();
        timesArray.add(curTime);
        timesArray.add(todayEnds.getTimeInMillis());
        timesArray.add(tomorrowEnds.getTimeInMillis());
        timesArray.add(weekEnds.getTimeInMillis());
        timesArray.add(nextWeekEnds.getTimeInMillis());*/


            //date2.add(Calendar.HOUR, 12);

            position = 0;

            buildSectionsByPeriod(taskItems, sectionItems, context.getString(R.string.past),
                    tasks, -1, curTime); //Прошедшие
            buildSectionsByPeriod(taskItems, sectionItems, context.getString(R.string.today),
                    tasks, curTime, todayEnds.getTimeInMillis()); //Сегодня
            buildSectionsByPeriod(taskItems, sectionItems, context.getString(R.string.tomorrow),
                    tasks, todayEnds.getTimeInMillis(), tomorrowEnds.getTimeInMillis()); //Завтра
            buildSectionsByPeriod(taskItems, sectionItems, context.getString(R.string.week),
                    tasks, tomorrowEnds.getTimeInMillis(), weekEnds.getTimeInMillis()); //Неделя
            buildSectionsByPeriod(taskItems, sectionItems, context.getString(R.string.next_week),
                    tasks, weekEnds.getTimeInMillis(), nextWeekEnds.getTimeInMillis()); //Следующая неделя
            buildSectionsByPeriod(taskItems, sectionItems, context.getString(R.string.all_time),
                    tasks, nextWeekEnds.getTimeInMillis(), Long.MAX_VALUE); //Следующая неделя

        /*String sectionName = "";
        for (Long time : taskTimesSorted) {
            if (time == 0) {
                sectionName = "Inbox";
            } else if (time < curTime) {
                sectionName = "Прошедшие";
            }
            DateUtils.formatSameDayTime(time, curTime, 2, 2);

            TasksAdapter.SectionItem sectionItem = new TasksAdapter.SectionItem(sectionName);

            //DateUtils.getRelativeTimeSpanString(
            //                time, curTime,
            //                DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE));

            sectionItems.put(position++, sectionItem);

            for (Task task : tasks) {
                long taskTime = task.getTime();
                if (taskTime == time) {
                    taskItems.add(new TasksAdapter.TaskItem(task, sectionItem));
                    position++;
                }
            }
        }*/
        }

        private static int buildSectionsByPeriod(ArrayList<TasksAdapter.TaskItem> taskItems,
                                                 HashMap<Integer, TasksAdapter.SectionItem> sectionItems,
                                                 String sectionName,
                                                 List<Task> tasks,
                                                 long start, long end) {
            ArrayList<Task> tasksForPeriod = getTasksForPeriod(tasks, start, end);
            if (!tasksForPeriod.isEmpty()) {
                TasksAdapter.SectionItem sectionItem = new TasksAdapter.SectionItem(sectionName);
                sectionItems.put(position++, sectionItem);

                for (Task task : tasksForPeriod) {
                    taskItems.add(new TasksAdapter.TaskItem(task, sectionItem));
                }

                position += tasksForPeriod.size();
            }

            return position;
        }

        public static ArrayList<Task> getTasksForPeriod(List<Task> tasks, long start, long end) {
            ArrayList<Task> tasksForPeriod = new ArrayList<>();

            for (Task task : tasks) {
                long taskTime = task.getTime();
                if (taskTime > start && taskTime <= end) {
                    tasksForPeriod.add(task);
                }
            }

            return tasksForPeriod;
        }
    }

    public static void requireAllTasksHaveProject(List<Task> tasks, List<Project> projects) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getProjectId() == null && tasks.get(i).getProject() == null) {
                tasks.get(i).setProject(DefaultTypes.PROJECT);
            } else if (!tasks.get(i).getProjectId().isEmpty()) {
                for (int j = 0; j < projects.size(); j++) {
                    if (projects.get(j).getId().equals(tasks.get(i).getProjectId())) {
                        tasks.get(i).setProject(projects.get(j));
                    }
                }
            }
        }
    }

}

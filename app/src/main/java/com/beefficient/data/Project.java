package com.beefficient.data;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

// TODO: add color
public class Project {
    private ArrayList<SoftReference<Task>> taskList;

    private String name;

    public Project(String name) {
        taskList = new ArrayList<>();
        this.name = name;
    }

    public boolean addTask(SoftReference<Task> task) {
        taskList.add(task);
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<SoftReference<Task>> getTaskList() {
        return taskList;
    }
}
package com.beefficient.data.entity;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.UUID;

// TODO: add color
public class Project {
    private ArrayList<SoftReference<Task>> taskList;

    private String id;
    private String name;

    public Project(String name) {
        taskList = new ArrayList<>();
        id = UUID.randomUUID().toString();
        this.name = name;
    }

    public boolean addTask(SoftReference<Task> task) {
        taskList.add(task);
        return true;
    }

    public String getId() {
        return id;
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
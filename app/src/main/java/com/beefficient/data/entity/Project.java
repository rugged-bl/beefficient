package com.beefficient.data.entity;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.UUID;

// TODO: add color
public class Project {
    private ArrayList<Task> taskList;

    private String id;
    private String name;
    private int color = 0xffffff;

    public Project(String name, int color) {
        taskList = new ArrayList<>();
        id = UUID.randomUUID().toString();
        this.name = name;
        this.color = color;
    }

    public Project(String name, int color, String id) {
        taskList = new ArrayList<>();
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public boolean addTask(Task task) {
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public ArrayList<Task> getTaskList() {
        return taskList;
    }

}
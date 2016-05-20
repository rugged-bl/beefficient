package com.beefficient.data.entity;

import com.beefficient.R;

import java.util.ArrayList;
import java.util.UUID;

public class Project {
    private ArrayList<Task> taskList;

    private String id;
    private String name;
    private Color color = Color.BLACK;

    public Project(String name, Color color) {
        taskList = new ArrayList<>();
        id = UUID.randomUUID().toString();
        this.name = name;
        this.color = color;
    }

    public Project(String name, Color color, String id) {
        taskList = new ArrayList<>();
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public boolean addTask(Task task) {
        return taskList.add(task);
    }

    public boolean removeTask(Task task) {
        return taskList.remove(task);
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

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public ArrayList<Task> getTaskList() {
        return taskList;
    }

    @Override
    public String toString() {
        return name;
    }

    // TODO: add more colors
    public enum Color {
        BLUE(R.color.light_blue_a700, R.string.light_blue),
        ORANGE(R.color.orange_600, R.string.orange),
        GREEN(R.color.light_green_500, R.string.green),
        BLACK(android.R.color.black, R.string.black);

        private int color;
        private int colorName;

        Color(int color, int colorName) {
            this.color = color;
            this.colorName = colorName;
        }

        public int color() {
            return color;
        }

        public int colorName() {
            return colorName;
        }
    }

}
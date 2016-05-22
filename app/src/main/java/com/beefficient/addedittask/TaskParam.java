package com.beefficient.addedittask;

public class TaskParam {

    private int name;
    private int icon;
    private String text;

    public TaskParam(int name, int icon) {
        this.name = name;
        this.icon = icon;
    }

    public int getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

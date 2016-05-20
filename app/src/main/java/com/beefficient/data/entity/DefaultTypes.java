package com.beefficient.data.entity;

import com.beefficient.Application;
import com.beefficient.R;

public final class DefaultTypes {

    // TODO: сделать так, чтобы при переключении языка менялось название проекта
    private static final String defaultProjectTitle = Application.getContext().getString(R.string.inbox);

    public static final Project PROJECT =
            new Project(defaultProjectTitle, Project.Color.BLACK, defaultProjectTitle);

    public static final Task.Priority PRIORITY = Task.Priority.LOW;
}
package com.beefficient.data.entity;

import com.beefficient.Application;
import com.beefficient.R;

// TODO: сделать так, чтобы при переключении языка менялось название проекта
public final class DefaultTypes {
    private static final String defaultProjectTitle = Application.getContext().getString(R.string.inbox);

    public static final Project PROJECT =
            new Project(defaultProjectTitle, Project.Color.BLACK, defaultProjectTitle);
}
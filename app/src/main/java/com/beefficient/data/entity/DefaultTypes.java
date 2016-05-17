package com.beefficient.data.entity;

import com.beefficient.Application;
import com.beefficient.R;

public final class DefaultTypes {
    private static String defaultProjectTitle = Application.getContext().getString(R.string.inbox);

    public static final Project PROJECT = new Project(defaultProjectTitle, 0xffffff, defaultProjectTitle);
}

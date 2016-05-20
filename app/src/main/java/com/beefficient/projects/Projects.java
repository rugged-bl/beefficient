package com.beefficient.projects;

import com.beefficient.data.entity.Project;

public final class Projects {

    private Projects() {
    }

    public static Project.Color getColorFromIndex(int colorIndex) {
        int maxIndex = Project.Color.values().length - 1;
        return Project.Color.values()[Math.max(Math.min(colorIndex, maxIndex), 0)];
    }
}
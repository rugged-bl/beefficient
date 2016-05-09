package com.beefficient.data.source.local;

import android.provider.BaseColumns;

/**
 * The contract used for the db to save the tasks locally.
 */
public final class TasksPersistenceContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public TasksPersistenceContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class TaskEntry implements BaseColumns {
        public static final String PROJECTS_TABLE_NAME = "projects";
        public static final String COLUMN_NAME_PROJECT_NAME = "project_name";
        public static final String COLUMN_NAME_PROJECT_COLOR = "project_color";

        public static final String TASKS_TABLE_NAME = "tasks";
        public static final String COLUMN_NAME_ENTRY_ID = "entry_id";
        public static final String COLUMN_NAME_PROJECT_ID = "project_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_PRIORITY = "priority";
        public static final String COLUMN_NAME_COMPLETED = "completed";
        public static final String COLUMN_NAME_DUE_DATE = "due_date";
        public static final String COLUMN_NAME_ONLY_DATE = "only_date";

        public static final String TASKS_LABELS_TABLE_NAME = "task_labels";
        public static final String COLUMN_NAME_TASK_ID = "task_id";
        public static final String COLUMN_NAME_LABEL_ID = "label_id";

        public static final String LABELS_TABLE_NAME = "labels";
        public static final String COLUMN_NAME_LABEL_NAME = "label_name";
        public static final String COLUMN_NAME_LABEL_COLOR = "label_color";

    }
}

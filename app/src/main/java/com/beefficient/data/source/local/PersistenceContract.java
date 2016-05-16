package com.beefficient.data.source.local;

import android.provider.BaseColumns;

/**
 * The contract used for the db to save data locally.
 */
public final class PersistenceContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private PersistenceContract() {
    }

    public static abstract class ProjectEntry {
        public static final String TABLE_NAME = "project";

        public enum Column {
            _id, name, color
        }
    }

    public static abstract class TaskEntry {
        public static final String TABLE_NAME = "task";

        public enum Column {
            _id, project_id, completed, title, description, priority, due_date, with_time
        }
    }

    public static abstract class TaskLabelEntry {
        public static final String TABLE_NAME = "task_label";

        public enum Column {
            task_id, label_id
        }
    }

    public static abstract class LabelEntry {
        public static final String TABLE_NAME = "label";

        public enum Column {
            _id, name, color
        }
    }
}

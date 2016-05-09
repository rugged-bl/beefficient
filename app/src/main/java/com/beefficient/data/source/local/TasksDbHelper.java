package com.beefficient.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.beefficient.data.source.local.TasksPersistenceContract.TaskEntry;

public class TasksDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "Tasks.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String BOOLEAN_TYPE = " INTEGER";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    private static final String CREATE_PROJECTS_TABLE = "CREATE TABLE " +
            TaskEntry.PROJECTS_TABLE_NAME + " (" +
            TaskEntry._ID + TEXT_TYPE + " PRIMARY KEY," +
            TaskEntry.COLUMN_NAME_PROJECT_NAME + TEXT_TYPE + COMMA_SEP +
            TaskEntry.COLUMN_NAME_PROJECT_COLOR + TEXT_TYPE +
            " )";
    /*private String id;
    private String title;
    private String description;
    private Priority priority;
    private boolean completed;
    private boolean onlyDate;
    private long time;*/
    private static final String CREATE_TASKS_TABLE = "CREATE TABLE " +
            TaskEntry.TASKS_TABLE_NAME + " (" +
            TaskEntry._ID + TEXT_TYPE + " PRIMARY KEY," +
            TaskEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
            TaskEntry.COLUMN_NAME_PROJECT_ID + INTEGER_TYPE + COMMA_SEP +
            TaskEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
            TaskEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
            TaskEntry.COLUMN_NAME_PRIORITY + INTEGER_TYPE + COMMA_SEP +
            TaskEntry.COLUMN_NAME_COMPLETED + BOOLEAN_TYPE + COMMA_SEP +
            TaskEntry.COLUMN_NAME_DUE_DATE + INTEGER_TYPE + COMMA_SEP +
            TaskEntry.COLUMN_NAME_ONLY_DATE + BOOLEAN_TYPE + COMMA_SEP +
            "FOREIGN KEY(" + TaskEntry.COLUMN_NAME_PROJECT_ID + ") REFERENCES " +
            TaskEntry.PROJECTS_TABLE_NAME + "(" + TaskEntry._ID + ") ON DELETE CASCADE)";

    private static final String CREATE_TASK_LABELS_TABLE = "CREATE TABLE " +
            TaskEntry.TASKS_LABELS_TABLE_NAME + " (" +
            TaskEntry.COLUMN_NAME_TASK_ID + INTEGER_TYPE + COMMA_SEP +
            TaskEntry.COLUMN_NAME_LABEL_ID + INTEGER_TYPE + COMMA_SEP +
            "PRIMARY KEY(" + TaskEntry.COLUMN_NAME_TASK_ID + ", " + TaskEntry.COLUMN_NAME_LABEL_ID + "), " +
            "FOREIGN KEY(" + TaskEntry.COLUMN_NAME_TASK_ID + ") REFERENCES " + TaskEntry.TASKS_TABLE_NAME +
            "(" + TaskEntry.COLUMN_NAME_LABEL_ID + ") ON DELETE CASCADE," +
            "FOREIGN KEY(" + TaskEntry.COLUMN_NAME_LABEL_ID + ") REFERENCES " + TaskEntry.LABELS_TABLE_NAME +
            "(" + TaskEntry.COLUMN_NAME_LABEL_ID + ") ON DELETE CASCADE)";

    private static final String CREATE_LABELS_TABLE = "CREATE TABLE " +
            TaskEntry.LABELS_TABLE_NAME + " (" +
            TaskEntry._ID + TEXT_TYPE + " PRIMARY KEY," +
            TaskEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
            TaskEntry.COLUMN_NAME_LABEL_NAME + TEXT_TYPE + COMMA_SEP +
            TaskEntry.COLUMN_NAME_LABEL_COLOR + INTEGER_TYPE +
            " )";

    public TasksDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
        db.execSQL(CREATE_PROJECTS_TABLE);
        db.execSQL(CREATE_TASKS_TABLE);
        db.execSQL(CREATE_TASK_LABELS_TABLE);
        db.execSQL(CREATE_LABELS_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not required as at version 1
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not required as at version 1
    }
}

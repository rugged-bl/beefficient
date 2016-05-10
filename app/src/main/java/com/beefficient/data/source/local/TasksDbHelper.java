package com.beefficient.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.beefficient.data.source.local.TasksPersistenceContract.ProjectEntry;
import static com.beefficient.data.source.local.TasksPersistenceContract.TaskEntry;
import static com.beefficient.data.source.local.TasksPersistenceContract.TaskLabelEntry;
import static com.beefficient.data.source.local.TasksPersistenceContract.LabelEntry;

public class TasksDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "Tasks.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String BOOLEAN_TYPE = " INTEGER";
    private static final String INTEGER_TYPE = " INTEGER";

    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_PROJECT_TABLE = "CREATE TABLE " +
                ProjectEntry.TABLE_NAME + " (" +
                ProjectEntry._ID + TEXT_TYPE + " PRIMARY KEY," +
                ProjectEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                ProjectEntry.COLUMN_NAME_COLOR + TEXT_TYPE +
            ")";

    private static final String SQL_CREATE_TASK_TABLE = "CREATE TABLE " +
            TaskEntry.TABLE_NAME + " (" +
            TaskEntry._ID + TEXT_TYPE + " PRIMARY KEY," +
            TaskEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
            TaskEntry.COLUMN_NAME_PROJECT_ID + INTEGER_TYPE + COMMA_SEP +
            TaskEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
            TaskEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
            TaskEntry.COLUMN_NAME_PRIORITY + INTEGER_TYPE + COMMA_SEP +
            TaskEntry.COLUMN_NAME_COMPLETED + BOOLEAN_TYPE + COMMA_SEP +
            TaskEntry.COLUMN_NAME_DUE_DATE + INTEGER_TYPE + COMMA_SEP +
            TaskEntry.COLUMN_NAME_WITH_TIME + BOOLEAN_TYPE + COMMA_SEP +
            "FOREIGN KEY(" + TaskEntry.COLUMN_NAME_PROJECT_ID + ") REFERENCES " +
            ProjectEntry.TABLE_NAME + "(" + ProjectEntry._ID + ") ON DELETE CASCADE)";

    private static final String SQL_CREATE_LABEL_TABLE = "CREATE TABLE " +
                LabelEntry.TABLE_NAME + " (" +
                LabelEntry._ID + TEXT_TYPE + " PRIMARY KEY," +
                LabelEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                LabelEntry.COLUMN_NAME_COLOR + INTEGER_TYPE +
            ")";

    private static final String SQL_CREATE_TASK_LABEL_TABLE = "CREATE TABLE " +
            TaskLabelEntry.TABLE_NAME + " (" +
            TaskLabelEntry.COLUMN_NAME_TASK_ID + INTEGER_TYPE + COMMA_SEP +
            TaskLabelEntry.COLUMN_NAME_LABEL_ID + INTEGER_TYPE + COMMA_SEP +
            "PRIMARY KEY(" + TaskLabelEntry.COLUMN_NAME_TASK_ID + ", " + TaskLabelEntry.COLUMN_NAME_LABEL_ID + "), " +
            "FOREIGN KEY(" + TaskLabelEntry.COLUMN_NAME_TASK_ID + ") REFERENCES " + TaskEntry.TABLE_NAME +
            "(" + TaskEntry._ID + ") ON DELETE CASCADE," +
            "FOREIGN KEY(" + TaskLabelEntry.COLUMN_NAME_LABEL_ID + ") REFERENCES " + LabelEntry.TABLE_NAME +
            "(" + LabelEntry._ID + ") ON DELETE CASCADE)";

    public TasksDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
//        db.setForeignKeyConstraintsEnabled(true);
        db.execSQL(SQL_CREATE_PROJECT_TABLE);
        db.execSQL(SQL_CREATE_TASK_TABLE);
        db.execSQL(SQL_CREATE_LABEL_TABLE);
        db.execSQL(SQL_CREATE_TASK_LABEL_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not required as at version 1
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not required as at version 1
    }
}

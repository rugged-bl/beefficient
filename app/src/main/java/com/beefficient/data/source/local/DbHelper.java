package com.beefficient.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.beefficient.data.source.local.PersistenceContract.LabelEntry;
import static com.beefficient.data.source.local.PersistenceContract.ProjectEntry;
import static com.beefficient.data.source.local.PersistenceContract.TaskEntry;
import static com.beefficient.data.source.local.PersistenceContract.TaskLabelEntry;

public class DbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "beefficient.db";

    private static final String PRIMARY_KEY = " PRIMARY_KEY,";
    private static final String TEXT_TYPE = " TEXT";
    private static final String BOOLEAN_TYPE = " INTEGER";
    private static final String INTEGER_TYPE = " INTEGER";

    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_PROJECT_TABLE = "CREATE TABLE " +
            ProjectEntry.TABLE_NAME + " (" +
            ProjectEntry.Column._id + TEXT_TYPE + PRIMARY_KEY +
            ProjectEntry.Column.name + TEXT_TYPE + COMMA_SEP +
            ProjectEntry.Column.color + TEXT_TYPE +
            ")";

    private static final String SQL_CREATE_TASK_TABLE = "CREATE TABLE " +
            TaskEntry.TABLE_NAME + " (" +
            TaskEntry.Column._id + TEXT_TYPE + PRIMARY_KEY +
            TaskEntry.Column.project_id + INTEGER_TYPE + COMMA_SEP +
            TaskEntry.Column.completed + BOOLEAN_TYPE + COMMA_SEP +
            TaskEntry.Column.title + TEXT_TYPE + COMMA_SEP +
            TaskEntry.Column.description + TEXT_TYPE + COMMA_SEP +
            TaskEntry.Column.priority + INTEGER_TYPE + COMMA_SEP +
            TaskEntry.Column.due_date + INTEGER_TYPE + COMMA_SEP +
            TaskEntry.Column.with_time + BOOLEAN_TYPE +
            ")";

    private static final String SQL_CREATE_LABEL_TABLE = "CREATE TABLE " +
            LabelEntry.TABLE_NAME + " (" +
            LabelEntry.Column._id + TEXT_TYPE + " PRIMARY KEY," +
            LabelEntry.Column.name + TEXT_TYPE + COMMA_SEP +
            LabelEntry.Column.color + INTEGER_TYPE +
            ")";

    private static final String SQL_CREATE_TASK_LABEL_TABLE = "CREATE TABLE " +
            TaskLabelEntry.TABLE_NAME + " (" +
            TaskLabelEntry.Column.task_id + INTEGER_TYPE + COMMA_SEP +
            TaskLabelEntry.Column.label_id + INTEGER_TYPE + COMMA_SEP +
            "PRIMARY KEY(" + TaskLabelEntry.Column.task_id + ", " + TaskLabelEntry.Column.label_id + ")" +
            ")";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PROJECT_TABLE);
        db.execSQL(SQL_CREATE_TASK_TABLE);
        db.execSQL(SQL_CREATE_LABEL_TABLE);
        db.execSQL(SQL_CREATE_TASK_LABEL_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

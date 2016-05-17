package com.beefficient;

import android.database.Cursor;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.beefficient.data.entity.Project;
import com.beefficient.data.entity.Task;
import com.beefficient.data.source.local.DbHelper;
import com.beefficient.data.source.local.PersistenceContract;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import org.junit.Test;

import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TaskUnitTest {

    @Test
    public void taskBuilder() {
        Project project = new Project("Project", 0xFF000000);

		long time = System.currentTimeMillis();
        Task.Builder taskBuilder = new Task.Builder("Task")
                .setTitle("Title")
                .setPriority(Task.Priority.HIGH)
                .setCompleted(true)
                .setTime(time)
                .setDescription("Desc")
                .setProject(project);

        Task task = taskBuilder.build();

		assertEquals(task.getTitle(), "Title");
		assertEquals(task.getPriority(), Task.Priority.HIGH);
		assertTrue(task.isCompleted());
		assertEquals(task.getDescription(), "Desc");
		assertEquals(task.getTime(), time);
    }
}
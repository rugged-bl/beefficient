package com.beefficient;

import com.beefficient.data.Project;
import com.beefficient.data.Task;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TaskUnitTest {

    @Test
    public void taskBuilder() {
        Project project = new Project("Project");

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
        // System.out.println("title: " + task.getTitle() + "\ndescription: " + task.getDescription() +
        //         "\npriority: " + task.getPriority().name() + "\ntime: " + task.getTime() +
        //         "\nonlyDate: " + task.isOnlyDate() + "\ncompleted: " + task.isCompleted());
    }
}

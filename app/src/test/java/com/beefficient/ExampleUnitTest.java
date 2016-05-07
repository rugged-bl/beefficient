package com.beefficient;

import com.beefficient.data.Task;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void taskBuilder() {
        Task.Builder taskBuilder = new Task.Builder("Title")
                .setPriority(Task.Priority.HIGH)
                .setCompleted(true)
                .setTime(System.currentTimeMillis())
                .setDescription("Desc");

        Task task = taskBuilder.build();

        System.out.println("title: " + task.getTitle() + "\ndescription: " + task.getDescription() +
                "\npriority: " + task.getPriority().name() + "\ntime: " + task.getTime() +
                "\nonlyDate: " + task.isOnlyDate() + "\ncompleted: " + task.isCompleted());
    }
}
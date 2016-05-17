/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.beefficient.addedittask;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.beefficient.data.entity.Task;
import com.beefficient.data.source.DataSource;

import static com.beefficient.util.Objects.requireNonNull;

/**
 * Listens to user actions from the UI ({@link AddEditTaskFragment}), retrieves the data and updates
 * the UI as required.
 */
public class AddEditTaskPresenter implements AddEditTaskContract.Presenter {

    @NonNull
    private final DataSource dataRepository;

    @NonNull
    private final AddEditTaskContract.View addTaskView;

    @Nullable
    private String taskId;

    /**
     * Creates a presenter for the add/edit view.
     *
     * @param taskId         ID of the task to edit or null for a new task
     * @param dataRepository a repository of data for tasks
     * @param addTaskView    the add/edit view
     */
    public AddEditTaskPresenter(@Nullable String taskId, @NonNull DataSource dataRepository,
                                @NonNull AddEditTaskContract.View addTaskView) {
        this.taskId = taskId;
        this.dataRepository = requireNonNull(dataRepository);
        this.addTaskView = requireNonNull(addTaskView);

        this.addTaskView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        if (taskId != null) {
            populateTask();
        }
    }

    @Override
    public void unsubscribe() {

    }

    @Override
    public void createTask(String title, String description) {
        Task newTask = new Task
                .Builder(title)
                .setDescription(description)
                .setProjectId("prid1") //TODO:FIX
                .build();
        if (title.isEmpty()) {
            addTaskView.showEmptyTaskError();
        } else {
            dataRepository.saveTask(newTask);
            addTaskView.showTasksList();
        }
    }

    @Override
    public void updateTask(String title, String description) {
        if (taskId == null) {
            throw new RuntimeException("updateTask() was called but task is new.");
        }
        dataRepository.saveTask(new Task
                .Builder(title, taskId)
                .setDescription(description)
                .setProjectId("prid1") //TODO:FIX
                .build());
        addTaskView.showTasksList(); // After an edit, go back to the list.
    }

    @Override
    public void populateTask() {
        if (taskId == null) {
            throw new RuntimeException("populateTask() was called but task is new.");
        }
        dataRepository.getTask(taskId);
    }

/*    @Override
    public void onTaskLoaded(Task task) {
        // The view may not be able to handle UI updates anymore
        if (addTaskView.isActive()) {
            addTaskView.setTitle(task.getTitle());
            addTaskView.setDescription(task.getDescription());
        }
    }

    @Override
    public void onDataNotAvailable() {
        // The view may not be able to handle UI updates anymore
        if (addTaskView.isActive()) {
            addTaskView.showEmptyTaskError();
        }
    }*/
}

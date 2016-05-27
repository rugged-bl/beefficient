package com.beefficient.projects;

import com.beefficient.BasePresenter;
import com.beefficient.BaseView;

public class ProjectsContract {

    interface View extends BaseView<Presenter> {

        void showAddProject();
    }

    interface Presenter extends BasePresenter {

        void loadProjects(boolean force);

        void addNewProject();
    }
}

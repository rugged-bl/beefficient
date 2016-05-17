package com.beefficient.main;

import com.beefficient.BasePresenter;
import com.beefficient.BaseView;

public interface MainContract {

    interface View extends BaseView<Presenter> {
        void loadTasksFragment();

        void loadProjectsFragment();

        void loadLabelsFragment();
    }

    interface Presenter extends BasePresenter {
    }
}

package com.beefficient.main;

import com.beefficient.BasePresenter;
import com.beefficient.BaseView;

public interface MainContract {

    interface View extends BaseView<Presenter> {

        void showSnackbar(CharSequence text, int duration);

        void loadTasksFragment();

        void loadProjectsFragment();

        void loadLabelsFragment();

        void loadTaskDetailsFragment();
    }

    interface Presenter extends BasePresenter {
    }
}

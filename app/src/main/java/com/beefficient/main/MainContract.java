package com.beefficient.main;

import com.beefficient.BasePresenter;
import com.beefficient.BaseView;

public interface MainContract {
    interface View extends BaseView<Presenter> {
        void showSnackbar(CharSequence text, int duration);
    }

    interface Presenter extends BasePresenter {

    }
}

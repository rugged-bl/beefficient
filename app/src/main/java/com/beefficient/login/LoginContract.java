package com.beefficient.login;

import android.content.Intent;

import com.beefficient.BasePresenter;
import com.beefficient.BaseView;

public interface LoginContract {
    interface View extends BaseView<Presenter> {
        void showLoginDialog(Intent intent);
        void showToast(CharSequence text, int duration);
    }

    interface Presenter extends BasePresenter {
        void signIn();

        void result(int requestCode, int resultCode, Intent data);
    }
}

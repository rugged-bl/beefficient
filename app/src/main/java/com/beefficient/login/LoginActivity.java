package com.beefficient.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.beefficient.R;
import com.google.android.gms.common.SignInButton;

public class LoginActivity extends AppCompatActivity implements LoginContract.View {

    public static final String TAG = LoginActivity.class.getSimpleName();

    private SignInButton googleSignInButton;
    private LoginContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupGoogleSignInButton();

        new LoginPresenter(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.unsubscribe();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.result(requestCode, resultCode, data);
    }

    private void setupGoogleSignInButton() {
        googleSignInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        if (googleSignInButton != null) {
            googleSignInButton.setOnClickListener(v -> presenter.signIn());
        }
    }

    @Override
    public void showLoginDialog(Intent intent) {
        startActivityForResult(intent, LoginPresenter.REQUEST_CODE_SIGN_IN);
    }

    @Override
    public void showToast(CharSequence text, int duration) {
        Toast.makeText(this, text, duration).show();
    }

    @Override
    public void setPresenter(@NonNull LoginContract.Presenter presenter) {
        this.presenter = presenter;
    }
}

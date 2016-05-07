package com.beefficient.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class LoginPresenter implements LoginContract.Presenter,
        GoogleApiClient.OnConnectionFailedListener {

    public static final int REQUEST_CODE_SIGN_IN = 1;
    private static final String TAG = LoginPresenter.class.getSimpleName();

    GoogleApiClient googleApiClient;

    private final LoginContract.View loginView;

    public LoginPresenter(@NonNull LoginContract.View loginView) {
        this.loginView = loginView;

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        if (loginView instanceof FragmentActivity) {
            FragmentActivity activity = (FragmentActivity) loginView;

            // Build a GoogleApiClient with access to the Google Sign-In API and the
            // options specified by gso.
            googleApiClient = new GoogleApiClient.Builder(activity)
                    .enableAutoManage(activity, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

        }

        loginView.setPresenter(this);
    }

    @Override
    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        loginView.showLoginDialog(signInIntent);
    }

    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                loginView.showToast("Success. Logged in as: " + account.getDisplayName(), Toast.LENGTH_SHORT);
            } else {
                loginView.showToast("Fail", Toast.LENGTH_SHORT);
            }
        }
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

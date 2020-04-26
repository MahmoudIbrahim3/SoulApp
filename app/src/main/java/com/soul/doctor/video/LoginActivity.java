package com.soul.doctor.video;

import com.google.firebase.FirebaseApp;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.PushTokenRegistrationCallback;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.UserController;
import com.sinch.android.rtc.UserRegistrationCallback;
import com.soul.doctor.R;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.security.MessageDigest;

import static com.soul.doctor.video.SinchService.APP_KEY;
import static com.soul.doctor.video.SinchService.APP_SECRET;
import static com.soul.doctor.video.SinchService.ENVIRONMENT;
import static com.soul.doctor.video.SinchService.TAG;

public class LoginActivity extends BaseActivity implements SinchService.StartFailedListener,
        PushTokenRegistrationCallback, UserRegistrationCallback {

    private Button mLoginButton;
    private EditText mLoginName;
    private ProgressDialog mSpinner;
    static final String userName = "doctor"; //mLoginName.getText().toString();

    private String mUserId;
    private long mSigningSequence = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

        mLoginName = (EditText) findViewById(R.id.loginName);

        mLoginButton = (Button) findViewById(R.id.loginButton);
        mLoginButton.setEnabled(false);

        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loginClicked();
            }
        });
    }

    @Override
    protected void onServiceConnected() {
        mLoginButton.setEnabled(true);
        getSinchServiceInterface().setStartListener(this);

        new Handler().postDelayed(() -> {
            loginClicked();
        }, 1500);
    }

    @Override
    protected void onPause() {
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
        super.onPause();
    }

    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
    }

    @Override
    public void onStarted() {
//        openPlaceCallActivity();
    }

    private void startClientAndOpenPlaceCallActivity() {
        // start Sinch Client, it'll result onStarted() callback from where the place call activity will be started
        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient(userName);
            showSpinner();
        }

        openPlaceCallActivity();
    }

    private void loginClicked() {

        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
            return;
        }

        if (!userName.equals(getSinchServiceInterface().getUserName())) {
            getSinchServiceInterface().stopClient();
        }

//        if (!getSinchServiceInterface().isStarted()) {
//            getSinchServiceInterface().startClient(userName);
//            showSpinner();
//        } else {
//            openPlaceCallActivity();
//        }

        mUserId = userName;
        UserController uc = Sinch.getUserControllerBuilder()
                .context(getApplicationContext())
                .applicationKey(APP_KEY)
                .userId(mUserId)
                .environmentHost(ENVIRONMENT)
                .build();
        uc.registerUser(this, this);
    }

    private void openPlaceCallActivity() {
        Intent mainActivity = new Intent(this, PlaceCallActivity.class);
        startActivity(mainActivity);
        finish();
    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Logging in");
        mSpinner.setMessage("Please wait...");
//        mSpinner.show();
    }

    private void dismissSpinner() {
        if (mSpinner != null) {
            mSpinner.dismiss();
            mSpinner = null;
        }
    }

    @Override
    public void tokenRegistered() {
        dismissSpinner();
        startClientAndOpenPlaceCallActivity();
        Log.d(TAG, "tokenRegistered success");
    }

    @Override
    public void tokenRegistrationFailed(SinchError sinchError) {
        dismissSpinner();
        Toast.makeText(this, "Push token registration failed - incoming calls can't be received!", Toast.LENGTH_LONG).show();
    }

    // The most secure way is to obtain the signature from the backend,
    // since storing APP_SECRET in the app is not secure.
    // Following code demonstrates how the signature is obtained provided
    // the UserId and the APP_KEY and APP_SECRET.
    @Override
    public void onCredentialsRequired(ClientRegistration clientRegistration) {
        String toSign = mUserId + APP_KEY + mSigningSequence + APP_SECRET;
        String signature;
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-1");
            byte[] hash = messageDigest.digest(toSign.getBytes("UTF-8"));
            signature = Base64.encodeToString(hash, Base64.DEFAULT).trim();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }

        clientRegistration.register(signature, mSigningSequence++);
    }

    @Override
    public void onUserRegistered() {
        // Instance is registered, but we'll wait for another callback, assuring that the push token is
        // registered as well, meaning we can receive incoming calls.
    }

    @Override
    public void onUserRegistrationFailed(SinchError sinchError) {
        dismissSpinner();
        Toast.makeText(this, "Registration failed!", Toast.LENGTH_LONG).show();
    }
}

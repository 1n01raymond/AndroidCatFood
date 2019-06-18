package com.inha.androidcatfood;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    private Context mContext;
    private LoginButton btn_facebook_login;
    private LoginCallback mLoginCallback;
    private CallbackManager mCallbackManager;
    private Profile fbProfile;

    public APICallback onLoginSuccessCallback = new APICallback() {
        @Override
        public void run(Object arg) {

            Log.e("Test", "run start activity");
            Intent intent = new Intent(LoginActivity.this, MapActivity.class);
            intent.putExtra("fbProfile", fbProfile);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        mContext = getApplicationContext();

        mCallbackManager = CallbackManager.Factory.create();
        mLoginCallback = new LoginCallback(this);

        btn_facebook_login = (LoginButton) findViewById(R.id.btn_facebook_login);
        btn_facebook_login.setReadPermissions(Arrays.asList("public_profile", "email"));
        btn_facebook_login.registerCallback(mCallbackManager, mLoginCallback);


        fbProfile = Profile.getCurrentProfile().getCurrentProfile();
        if(fbProfile != null)
        {
            Log.e("Test", fbProfile.getId() + fbProfile.getName() + "Logged IN");

            APIClient.getInstance().login(fbProfile.getId(),
                    fbProfile.getName(), onLoginSuccessCallback);
        }
        else
        {
            Log.e("Test", "Not  Logged IN");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
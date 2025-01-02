package com.cofe.solution.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import javax.net.ssl.SNIHostName;

import com.cofe.solution.R;
import com.cofe.solution.ui.user.login.view.UserLoginActivity;

public class SplashScreen extends AppCompatActivity {

    Handler handler  = new Handler();
    Runnable r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        r = new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent (SplashScreen.this,  UserLoginActivity.class);
                startActivity(i);
            }
        };
        handler.postDelayed(r,2000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(r);
    }
}
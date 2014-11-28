package com.menil.rottenmovies;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;

/*
 * Created by menil on 24.10.2014.
 */
public class SplashScreen extends Activity {

    private void makeActionbar(){
        ActionBar actionBar = getActionBar();
        try{
            assert actionBar != null;
            actionBar.show();
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(R.string.app_name);
            //actionBar.setSubtitle("Home");
            actionBar.setBackgroundDrawable(new ColorDrawable(0xFF399322));//transparent
            actionBar.setIcon(R.drawable.actionbar_icon);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        makeActionbar();
        int SPLASH_TIME_OUT = 750;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start main activity
                Intent i = new Intent(SplashScreen.this, Main.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
package com.htl.flashair.fullscreenphoto;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;

public abstract class BaseActivity extends Activity {

    final int UPDATE_TIME_IN_MILLIS = 1000;
    final int HIDE_NAVIGATION_DELAY_TIME_IN_MILLIS = 5000;

    public abstract void findViews();
    public abstract void setListener();
    public abstract int getLayoutResId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        hideNavigationBar();
        setContentView(getLayoutResId());
        findViews();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fullScreenDelay();
    }

    private void hideStatusBar(){
        getWindow().setTitleColor(Color.rgb(65, 183, 216));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void hideNavigationBar(){
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void fullScreenDelay(){
        this.getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideNavigationBar();
            }
        },HIDE_NAVIGATION_DELAY_TIME_IN_MILLIS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}

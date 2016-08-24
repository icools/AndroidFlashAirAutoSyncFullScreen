package com.htl.flashair.fullscreenphoto;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

// TODO check wifi status
public class MainActivity extends Activity implements FlashAirHelper.FlashAirThumbnailCallBack {

    TextView mTextView;
    ImageView mImageView;
    final String mFilePath = "DCIM/14060825";
    final int UPDATE_TIME_IN_MILLIS = 1000;
    String mLastFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_main);
        findViews();
        setListener();
    }

    private void setFullScreen(){

        getWindow().setTitleColor(Color.rgb(65, 183, 216));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startDelayPost();
    }

    private void setListener() {
        mImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void downloadRawJpeg(){
        String downloadFile = "http://flashair/" + mFilePath + "/" + mLastFileName;
        Log.i(FlashAirHelper.TAG, "downloadFile:" + downloadFile);
        Picasso.with(MainActivity.this).load(downloadFile).fit().into(mImageView);
    }

    void findViews() {
        mTextView = (TextView) findViewById(R.id.txt_hint);
        mImageView = (ImageView) MainActivity.this.findViewById(R.id.imageView01);
        //mButton.getBackground().setColorFilter(Color.rgb(65, 183, 216), PorterDuff.Mode.SRC_IN);
    }

    private void getLastThumbnail() {
        FlashAirHelper.getFolderList(mFilePath, this);
    }

    private void checkHasNewFolder() {
        FlashAirHelper.checkHasNewFiles(this);
    }

    private void startDelayPost() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkHasNewFolder();
                startDelayPost();
            }
        }, UPDATE_TIME_IN_MILLIS);
    }

    public void getLastFileThumbnail(String filePath, String fileName) {
        FlashAirHelper.getPictureThumbnail(this, filePath, fileName, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void getThumbnail(BitmapDrawable bitmapDrawable) {
        if (bitmapDrawable == null) {
            setTitle("Result null");
        }
        ImageView imageView = (ImageView) MainActivity.this.findViewById(R.id.imageView01);
        imageView.setImageDrawable(bitmapDrawable);
        downloadRawJpeg();
    }

    @Override
    public void getFolderList(String[] files) {
        if (files == null) {
            setTitle("getFolderList null");
        }
        int lastFileIndex = files.length - 1;
        mLastFileName = files[lastFileIndex];
        //getLastFileThumbnail(mFilePath, mLastFileName);
        downloadRawJpeg();
        setTitle("Fetch Done:" + mLastFileName);
    }

    @Override
    public void checkNewFile(boolean hasNewFile) {
        if (hasNewFile) {
            Log.i(FlashAirHelper.TAG, "checkHasNewFolder");
            getLastThumbnail();
        }
    }

    public void setHint(String message) {
        mTextView.setText(message);
    }
}
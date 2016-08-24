package com.htl.flashair.fullscreenphoto;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

// TODO check wifi status
public class MainActivity extends Activity implements FlashAirHelper.FlashAirThumbnailCallBack {

    Button button ;
    ImageView imageView ;
    final String mFilePath = "DCIM/13960824";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setTitleColor(Color.rgb(65, 183, 216));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        findViews();
        setListener();
        button.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startDelayPost();
    }

    private void setListener() {
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    void findViews(){
        button = (Button)findViewById(R.id.button1);
        imageView = (ImageView) MainActivity.this.findViewById(R.id.imageView01);
        button.getBackground().setColorFilter(Color.rgb(65, 183, 216), PorterDuff.Mode.SRC_IN);
    }

    private void getLastThumbnail(){
        FlashAirHelper.getFolderList(mFilePath, this);
    }

    private void checkHasNewFolder(){
        FlashAirHelper.checkHasNewFiles(this);
    }

    private void startDelayPost(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkHasNewFolder();
                startDelayPost();
            }
        },1000);
    }

    public void getLastFileThumbnail(String filePath,String fileName){
        FlashAirHelper.getPictureThumbnail(MainActivity.this, filePath, fileName,this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void getThumbnail(BitmapDrawable bitmapDrawable) {
        if(bitmapDrawable == null){
            setTitle("Result null");
        }
        ImageView imageView = (ImageView) MainActivity.this.findViewById(R.id.imageView01);
        imageView.setImageDrawable(bitmapDrawable);
    }

    @Override
    public void getFolderList(String[] files) {
        int lastFileIndex = files.length-1 ;
        String lastFileName = files[lastFileIndex];
        getLastFileThumbnail(mFilePath,lastFileName);
        setTitle("Fetch Done:" + lastFileName);
    }

    @Override
    public void checkNewFile(boolean hasNewFile) {
        if(hasNewFile){
            getLastThumbnail();
        }
    }
}
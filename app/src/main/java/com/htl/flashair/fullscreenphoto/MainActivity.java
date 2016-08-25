package com.htl.flashair.fullscreenphoto;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

// TODO check wifi status
// TODO add Thumbnail or RawJpeg Mode
// TODO add Folder select function
public class MainActivity extends BaseActivity implements FlashAirCallBack {

    TextView mTextView;
    ImageView mImageView;
    String mFilePath = "DCIM/14160825";
    final int UPDATE_TIME_IN_MILLIS = 1000;
    String mLastFileName;
    Handler mHandler ;
    Runnable mRunnable ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
    }

    private void startDialogForSelectMonitorFolder(){
        SelectedFolderDialog dialog = new SelectedFolderDialog(this, new SelectedFolderDialog.SelectedCallback() {
            @Override
            public void onSelectedSuccess(String path) {
                mFilePath = path ;
                SettingManager.setLastSelectPath(MainActivity.this,mFilePath);
            }
        });
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFilePath = SettingManager.getLastSelectPath(this);
        if(mFilePath == null){
            startDialogForSelectMonitorFolder();
        }
        startDelayPost();
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeDelayPost();
    }

    @Override
    public void findViews() {
        mTextView = (TextView) findViewById(R.id.txt_hint);
        mImageView = (ImageView) MainActivity.this.findViewById(R.id.imageView01);
    }

    @Override
    public void setListener() {
        mImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO save image
                startDialogForSelectMonitorFolder();
            }
        });
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_main;
    }

    private void getLastThumbnail() {
        FlashAirHelper.getFolderList(mFilePath, this);
    }

    private void checkHasNewFolder() {
        FlashAirHelper.checkHasNewFiles(this);
    }

    private void startDelayPost() {
        mRunnable = new Runnable() {
            @Override
            public void run() {
                checkHasNewFolder();
                startDelayPost();
            }
        };
        mHandler.postDelayed(mRunnable, UPDATE_TIME_IN_MILLIS);
    }

    private void removeDelayPost(){
        mHandler.removeCallbacks(mRunnable);
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
    public void getThumbnail(BitmapDrawable bitmapDrawable,String fileName) {
        if (bitmapDrawable == null) {
            setHint("getThumbnail result null");
        }
        mImageView.setImageDrawable(bitmapDrawable);
        FlashAirHelper.downloadRawJpeg(mImageView,mFilePath,mLastFileName);
    }

    @Override
    public void getFolderList(String[] files) {
        if (files == null || files.length == 0) {
            setHint("getFolderList null or empty");
            return ;
        }
        int lastFileIndex = files.length - 1;
        mLastFileName = files[lastFileIndex];
        getLastFileThumbnail(mFilePath, mLastFileName);
        FlashAirHelper.downloadRawJpeg(mImageView,mFilePath,mLastFileName);
        setHint("Fetch Done:" + mLastFileName);
    }

    @Override
    public void checkNewFile(boolean hasNewFile) {
        if (hasNewFile) {
            setHint("found new file!");
            getLastThumbnail();
        }
    }

    @Override
    public void onError(String errorMessage) {
        setHint(errorMessage);
    }

    public void setHint(String message) {
        mTextView.setText(message);
    }
}
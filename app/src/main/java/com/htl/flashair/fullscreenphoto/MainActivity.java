package com.htl.flashair.fullscreenphoto;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

// TODO check wifi status
// TODO add Thumbnail or RawJpeg Mode
// TODO add Folder select function
// TODO add Exif info dashboard ?
// TODO lock screen mode
// TODO add  download progress
// TODO add get screen size
public class MainActivity extends BaseActivity implements FlashAirCallBack {

    TextView mTextView;
    TextView mTextCurrentPath ;
    TextView mTextScanNewPhotoStatus;
    ImageView mImageView;
    String mFilePath = "DCIM/14160825";
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
        if (mFilePath == null) {
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
        mTextView = (TextView) findViewById(R.id.text_status);
        mTextCurrentPath = (TextView) findViewById(R.id.text_current_path);
        mTextScanNewPhotoStatus = (TextView) findViewById(R.id.text_scan_new_photo);
        mImageView = (ImageView) findViewById(R.id.imageViewPhoto);
    }

    @Override
    public void setListener() {
        mImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO save image
                int visibility = findViewById(R.id.group_dashboard).getVisibility();
                int visibilityResult ;
                if(visibility == View.GONE){
                    visibilityResult = View.VISIBLE;
                }else{
                    visibilityResult = View.GONE;
                }
                findViewById(R.id.group_dashboard).setVisibility(visibilityResult);
            }
        });

        mImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                startDialogForSelectMonitorFolder();
                return false;
            }
        });
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_main;
    }

    private void getFolderList() {
        FlashAirHelper.getFolderList(mFilePath, this);
    }

    private void checkHasNewFolder() {
        FlashAirHelper.checkHasNewFiles(this);
    }

    private void startDelayPost() {
        mRunnable = new Runnable() {
            @Override
            public void run() {
                setScanPhotoStatus();
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
    public void getThumbnail(BitmapDrawable bitmapDrawable,String fileName) {
        if (bitmapDrawable == null) {
            showStatus("getThumbnail result null");
        }
        mImageView.setImageDrawable(bitmapDrawable);
    }

    @Override
    public void getFolderList(String[] files) {
        if (files == null || files.length == 0) {
            showStatus("Get folder list result is null or empty.");
            return ;
        }
        mLastFileName = findLatestJpegFile(files);
        if(mLastFileName == null){
            showStatus("Have no jpeg inside");
            return;
        }
        onFindLatestJpegFile(mLastFileName);
    }

    private void onFindLatestJpegFile(final String lastFileName){
        getLastFileThumbnail(mFilePath, lastFileName);
        FlashAirHelper.downloadRawJpeg(mImageView,mFilePath,lastFileName,new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                showStatus("Fetch:" + lastFileName);
            }
        });
        showStatus("Start to fetch:" + lastFileName,true);
    }

    public String findLatestJpegFile(String[] files){
        for(int i = files.length-1 ; i >= 0 ; i--){
            String fileName = files[i].toUpperCase();
            if(fileName.contains(".JPG")){
                return fileName;
            }
        }
        return null;
    }

    @Override
    public void checkNewFile(boolean hasNewFile) {
        if (hasNewFile) {
            showStatus("found new file!");
            getFolderList();
        }
    }

    @Override
    public void onError(String errorMessage) {
        showStatus(errorMessage);
    }

    public void showStatus(String message) {
        showStatus(message,false);
    }
    public void showStatus(String message,boolean bChangeColor) {
        mTextView.setText(message);
        int colorResId = bChangeColor ? Color.RED : Color.WHITE ;
        mTextView.setTextColor(colorResId);

        if(bChangeColor){
            mTextView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTextView.setTextColor(Color.WHITE);
                }
            },3000);
        }
    }

    public void setScanPhotoStatus(){
        mTextScanNewPhotoStatus.setText("Checking new photo : " + System.currentTimeMillis());
    }
}
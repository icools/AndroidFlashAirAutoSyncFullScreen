package com.example.android_tutorial_02;

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

import com.htl.flashair.fullscreenphoto.FlashAirHelper;
import com.htl.flashair.fullscreenphoto.R;

// TODO check wifi status
public class MainActivity extends Activity {

	Button button ;
	ImageView imageView ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setTitleColor(Color.rgb(65, 183, 216));
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		//hideActionBar();
		findViews();
		setListener();
	}

	private void hideActionBar() {
		getActionBar().hide();
	}

	private void setListener() {
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String fileName = "DSC02934.JPG";
				getLastThumbnail();
				button.setVisibility(View.GONE);
			}
		});
	}

	void findViews(){
		button = (Button)findViewById(R.id.button1);
		imageView = (ImageView) MainActivity.this.findViewById(R.id.imageView01);
		button.getBackground().setColorFilter(Color.rgb(65, 183, 216), PorterDuff.Mode.SRC_IN);
	}

	private void getLastThumbnail(){
		final String filePath = "DCIM/13960824";
		FlashAirHelper.getFolderList(filePath, new FlashAirHelper.FlashAirThumbnailCallBack() {
			@Override
			public void getThumbnail(BitmapDrawable bitmapDrawable) {

			}

			@Override
			public void getFolderList(String[] files) {
				int lastFileIndex = files.length-1 ;
				String lastFileName = files[lastFileIndex];
				getLastFileThumbnail(filePath,lastFileName);
				setTitle("Fetch Done:" + lastFileName);
				startDelayPost();
			}
		});
	}

	private void startDelayPost(){
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				getLastThumbnail();
			}
		},2000);
	}

	public void getLastFileThumbnail(String filePath,String fileName){
			FlashAirHelper.getPictureThumbnail(MainActivity.this, filePath, fileName, new FlashAirHelper.FlashAirThumbnailCallBack() {

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

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
/**
 *  ImageViewActivity.java
 *    An activity that displays and downloads 
 *    a selected image file from a FlashAir 
 *    card to an Android.
 *
 *  Created by Anisha Smith, Fixstars Corporation on 2013/06/03.
 * 
 *  Copyright (c) 2013, TOSHIBA CORPORATION
 *  All rights reserved.
 *  Released under the BSD 2-Clause license.
 *  http://flashair-developers.com/documents/license.html
 */
package com.example.android_tutorial_05;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

public class ImageViewActivity extends Activity {

	ImageView imageView;
	Button backButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_image_view);
//		getIntent();
//		imageView = (ImageView)findViewById(R.id.imageView1);
//		backButton = (Button)findViewById(R.id.button2);
//		getWindow().setTitleColor(Color.rgb(65, 183, 216));
//		backButton.getBackground().setColorFilter(Color.rgb(65, 183, 216), PorterDuff.Mode.SRC_IN);
//		Bundle extrasData = getIntent().getExtras();
//		String fileName = extrasData.getString("downloadFile");
//		String directory = extrasData.getString("directoryName");
//		downloadFile(fileName, directory);
	}

	
	void downloadFile(String downloadFile, String directory) {
		final ProgressDialog waitDialog;		
		// Setting ProgressDialog
	    waitDialog = new ProgressDialog(this);
	    waitDialog.setMessage("Now downloading...");
	    waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	    waitDialog.show();	    		
	    // Download file
		new AsyncTask<String, Void, Bitmap>(){
			@Override
			protected Bitmap doInBackground(String... params) {
				String fileName = params[0];
				return FlashAirRequest.getBitmap(fileName);
			}
			@Override
			protected void onPostExecute(Bitmap resultBitmap) {
		        waitDialog.dismiss();								
				viewImage(resultBitmap);
			}
		}.execute("http://flashair/" + directory + "/" + downloadFile.toString());	
	}


	void viewImage(Bitmap imageBitmap) {
		// Show image in ImageView
//		backButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				ImageViewActivity.this.finish();
//			}
//		});
//		if (imageBitmap == null) {
//			imageView.setImageResource(R.drawable.ic_launcher);
//		}
//		else {
//			imageView.setImageBitmap(imageBitmap);
//		}
	}
} // End ImageViewActivity class

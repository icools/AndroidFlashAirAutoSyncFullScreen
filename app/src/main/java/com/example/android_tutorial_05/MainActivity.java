/**
 *  MainActivity.java
 *    An activity that gets the unformatted contents of a FlashAir card
 *    and displays them in an automatically refreshing and 
 *    clickable list with thumbnail icons on the Android screen.
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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

import com.htl.flashair.fullscreenphoto.R;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {

	ListView listView;
	ImageView imageView;
	TextView currentDirText;
	TextView numFilesText;
	Button backButton;
	String rootDir = "DCIM";
	String directoryName = rootDir; // Initialize to rootDirectory
	SimpleAdapter listAdapter;
	int checkInterval = 5000;
	Handler updateHandler;
	boolean viewingList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		viewingList = true; // Start out viewing the list
		try {
			// Set buttons
			backButton = (Button)findViewById(R.id.button1);
			getWindow().setTitleColor(Color.rgb(65, 183, 216));
			backButton.getBackground().setColorFilter(Color.rgb(65, 183, 216), PorterDuff.Mode.SRC_IN);
			backButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(directoryName.equals(rootDir)) {
						listRootDirectory();
					}
					else {
						int index = directoryName.lastIndexOf("/");
						directoryName = directoryName.substring(0, index);
						listDirectory(directoryName);
					}
				}
			});
			backButton.setEnabled(false); // Disable in root directory
			listRootDirectory();
		} catch(Exception e) {
			Log.e("ERROR", "ERROR: " + e.toString());
			e.printStackTrace();
		}
		updateHandler = new Handler();
		startUpdate();
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus) {
			viewingList = true;
		}
		else {
			viewingList = false;
		}
	}
	
	
	public Runnable statusChecker = new Runnable() {
		@Override
		public void run() {
			if (checkIfListView() == true) {
				new AsyncTask<String, Void, String>(){
					@Override
					protected String doInBackground(String... params) {
						return FlashAirRequest.getString(params[0]);
					}
					@Override
					protected void onPostExecute(String status) {
						if(status.equals("1")) {
							// Fetch current contents of FlashAir and display list
							listDirectory(directoryName);
						}
					}
				}.execute("http://flashair/command.cgi?op=102");		
			}
			updateHandler.postDelayed(statusChecker, checkInterval);
		}
	};
	
	
	public void startUpdate() {
		statusChecker.run();	
	}
	
	
	public void stopUpdate() {
		updateHandler.removeCallbacks(statusChecker);
	}
	
	
	public boolean checkIfListView() {
		// Check if user is viewing a content list
		if(viewingList) {
			return true;
		}
		return false;
	}
	
	
	public void listRootDirectory() {
		directoryName = rootDir;
		listDirectory(directoryName);
	}

	
	public void listDirectory(String dir) {
		// Prepare command directory path
		if(dir.equals(rootDir)) {
			backButton.setEnabled(false);
		}
		else {
			backButton.setEnabled(true);
		}
		currentDirText = (TextView)findViewById(R.id.textView1);
		currentDirText.setText(dir + "/");
		dir = "/" + dir;
		ArrayList <NameValuePair> httpParams = new  ArrayList <NameValuePair> ();
		httpParams.add(new BasicNameValuePair("DIR", dir));
		dir = URLEncodedUtils.format (httpParams, "UTF-8" );
		numFilesText = (TextView)findViewById(R.id.textView2);
		// Fetch number of items in directory and display in a TextView
		new AsyncTask<String, Void, String>(){
			@Override
			protected String doInBackground(String... params) {
				String dir = params[0];
				String fileCount =  FlashAirRequest.getString("http://flashair/command.cgi?op=101&" + dir);				
				return fileCount;
			}
			@Override
			protected void onPostExecute(String fileCount) {
				numFilesText.setText("Items Found: " + fileCount);
			}
		}.execute(dir);	
		// Fetch list of items in directory and display in a ListView
		new AsyncTask<String, Void, ListAdapter>(){
			@Override
			protected ListAdapter doInBackground(String... params) {
				String dir = params[0];		
				ArrayList <String> fileNames = new ArrayList <String>();				
				String files = FlashAirRequest.getString("http://flashair/command.cgi?op=100&" + dir);
				String[] allFiles = files.split("([,\n])"); // split by newline or comma
				for(int i = 2; i < allFiles.length; i= i + 6) {
					if(allFiles[i].contains(".")) {
						// File
						fileNames.add(allFiles[i]);
					}
					else { // Directory, append "/"
						fileNames.add(allFiles[i] + "/");
					}
				}					
				
				// Get thumbnails
				ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
				for(int i = 0; i < fileNames.size(); i++) {
					String fileName = "";
					fileName = "http://flashair/thumbnail.cgi?" + directoryName + "/" + fileNames.get(i);
					Map<String, Object> entry = new HashMap<String, Object>();
					BitmapDrawable drawnIcon = null;
					if( (fileName.toLowerCase(Locale.getDefault()).endsWith(".jpg")) || (fileName.toLowerCase(Locale.getDefault()).endsWith(".jpeg")) ) {
						Bitmap thumbnail = FlashAirRequest.getBitmap(fileName);
						drawnIcon = new BitmapDrawable(getResources(), thumbnail);
					}
					if(drawnIcon == null) {
						entry.put("thmb", R.drawable.ic_launcher);
					}
					else {
						entry.put("thmb", drawnIcon);
					}
					entry.put("fname", fileNames.get(i)); // Put file name onto the map
					data.add(entry);
				}
				
				// Set the file list to a widget
				listAdapter = new SimpleAdapter(MainActivity.this,
						data,
						android.R.layout.activity_list_item,
						new String[]{"thmb", "fname"},
						new int[]{android.R.id.icon, android.R.id.text1});
				listAdapter.setViewBinder(new CustomViewBinder());

				return listAdapter;
			}
			@Override
			protected void onPostExecute(ListAdapter listAdapter) {
				listView = (ListView)findViewById(R.id.listView1);
				ColorDrawable divcolor = new ColorDrawable(Color.rgb(17, 19, 58));
				listView.setDivider(divcolor);
				listView.setDividerHeight(1);
				listView.setAdapter(listAdapter);
				listView.setOnItemClickListener(MainActivity.this);				
			}
		}.execute(dir);	
	}


	class CustomViewBinder implements ViewBinder {
		@Override
		public boolean setViewValue(View view, Object obj, String text) {
			if((view instanceof ImageView) && (obj instanceof Drawable)) {
				ImageView imageView = (ImageView) view;
				BitmapDrawable thumbnail = (BitmapDrawable) obj;
				imageView.setImageDrawable((Drawable)thumbnail);
				return true;
			}
			return false;
		}
	}


	@Override
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
		Object item = l.getItemAtPosition(position); // Get item at clicked position in list of files
		if(item instanceof Map<?, ?>) {
			Map<String, Object> mapItem = (Map<String, Object>) item;
			Object downloadFile = mapItem.get("fname");
			if(downloadFile.toString().endsWith("/")) {
				// Directory, remove "/" and show content list
				String dirName = downloadFile.toString().substring(0, downloadFile.toString().length()-1); // all but the "/"
				directoryName = directoryName + "/" + dirName;
				listDirectory(directoryName);
			}
			else if( (downloadFile.toString().toLowerCase(Locale.getDefault()).endsWith(".jpg")) || (downloadFile.toString().toLowerCase(Locale.getDefault()).endsWith(".jpeg"))
					|| (downloadFile.toString().toLowerCase(Locale.getDefault()).endsWith(".jpe")) || (downloadFile.toString().toLowerCase(Locale.getDefault()).endsWith(".png")) ) {
				// Image file, download using ImageViewActivity
				Intent viewImageIntent = new Intent(this, ImageViewActivity.class);
				viewImageIntent.putExtra("downloadFile", downloadFile.toString());
				viewImageIntent.putExtra("directoryName", directoryName);
				MainActivity.this.startActivity(viewImageIntent);
			} // Not an image file, do nothing
		}
	}
} // End MainActvitiy class
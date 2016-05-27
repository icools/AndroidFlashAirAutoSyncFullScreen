package com.example.android_tutorial_02;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.htl.flashair.fullscreenphoto.R;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button button = (Button)findViewById(R.id.button1);
		getWindow().setTitleColor(Color.rgb(65, 183, 216));
		button.getBackground().setColorFilter(Color.rgb(65, 183, 216), PorterDuff.Mode.SRC_IN);
		button.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View view) {
		switch ( view.getId() ) {
		case R.id.button1 :			
			// Fetch number of items in directory and display in a TextView
			new AsyncTask<String, Void, String>(){
				@Override
				protected String doInBackground(String... params) {
					return FlashAirRequest.getString(params[0]);				
				}
				@Override
				protected void onPostExecute(String fileCount) {
					TextView textView = (TextView)findViewById(R.id.textView0);					
					textView.setText("Items Found: " + fileCount);		
				}
			}.execute("http://flashair/command.cgi?op=101&DIR=/DCIM");		
		
			// Get a file list from FlashAir
			new AsyncTask<String, Void, String>(){
				@Override
				protected String doInBackground(String... params) {	
					return FlashAirRequest.getString(params[0]);
				}
				@Override
				protected void onPostExecute(String text) {
					TextView textView1 = (TextView)findViewById(R.id.textView1);
					textView1.setText(text);
				}
			}.execute("http://flashair/command.cgi?op=100&DIR=/DCIM");					
			break;
		}
	}
} // End MainActivity class

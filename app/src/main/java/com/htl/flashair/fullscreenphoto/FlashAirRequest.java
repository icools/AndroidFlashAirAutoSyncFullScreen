package com.htl.flashair.fullscreenphoto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

public class FlashAirRequest {	
	static public String getString(String command) {	
		String result = "";
		try{
			URL url = new URL(command);
			URLConnection urlCon = url.openConnection();
			urlCon.connect();
			InputStream inputStream = urlCon.getInputStream();		 
		    BufferedReader bufreader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
	        StringBuffer strbuf = new StringBuffer();
	        String str;
	        while ((str = bufreader.readLine()) != null) {
	        	if(strbuf.toString() != "") strbuf.append("\n");
	        	strbuf.append(str);
	        }
	        result =  strbuf.toString();												
		}catch(MalformedURLException e) {
			Log.e("ERROR", "ERROR: " + e.toString());
			e.printStackTrace();
		}
		catch(IOException e) {
			Log.e("ERROR", "ERROR: " + e.toString());
			e.printStackTrace();
		}
		return result;						
	}

	static public Bitmap getBitmap(String command) throws UnknownHostException {
		Bitmap resultBitmap = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		InputStream inputStream = null ;
		try {
			URL url = new URL(command);
			URLConnection urlCon = url.openConnection();
			urlCon.connect();
			inputStream = urlCon.getInputStream();
			byteArrayOutputStream = new ByteArrayOutputStream();
			byte[] byteChunk = new byte[1024];
			int bytesRead;
			while ((bytesRead = inputStream.read(byteChunk)) != -1) {
				byteArrayOutputStream.write(byteChunk, 0, bytesRead);
			}
			byte[] byteArray = byteArrayOutputStream.toByteArray();
			BitmapFactory.Options bfOptions = new BitmapFactory.Options();
			bfOptions.inPurgeable = true;
			resultBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, bfOptions);
		}catch(UnknownHostException e){
			e.printStackTrace();
			throw e;
		}catch(MalformedURLException e) {
			Log.e("ERROR", "ERROR: " + e.toString());
			e.printStackTrace();
		}
		catch(IOException e) {
			Log.e("ERROR", "ERROR: " + e.toString());
			e.printStackTrace();
		}finally {
			try {
				byteArrayOutputStream.close();
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resultBitmap;
	}
}
package com.htl.flashair.fullscreenphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android_tutorial_02.FlashAirRequest;

import java.util.ArrayList;
import java.util.Locale;

public class FlashAirHelper {

    public interface FlashAirThumbnailCallBack{
        void getThumbnail(BitmapDrawable bitmapDrawable);
        void getFolderList(String[] files);
    }

    static String TAG = "FlashAirHelper";

    final static String OP_GET_FOLDER_COUNT = "101" ;
    final static String OP_GET_FOLDER_LIST = "100" ;

    public static void getFolderCount(String folderName){
        new AsyncTask<String, Void, String>(){
            @Override
            protected String doInBackground(String... params) {
                return FlashAirRequest.getString(params[0]);
            }
            @Override
            protected void onPostExecute(String fileCount) {
                Log.i(TAG,"ListCount:" + fileCount);
            }
        }.execute(CommandOp.getCommand(OP_GET_FOLDER_COUNT) + folderName);
    }

    // Get a file list from FlashAir DCIM
    public static void getFolderList(String folderName,final FlashAirThumbnailCallBack callBack){
        new AsyncTask<String, Void, String>(){
            @Override
            protected String doInBackground(String... params) {
                return FlashAirRequest.getString(params[0]);
            }
            @Override
            protected void onPostExecute(String text) {
                if(callBack == null){
                    return ;
                }

                String[] result = text.split("\n");
                ArrayList<String> _fileNameList = new ArrayList<>();
                for(int i = 0 ; i < result.length ; i++){
                    String fileFullPath = result[i];
                    if(!fileFullPath.contains(",")){
                        continue;
                    }
                    String resultFileName = fileFullPath.split(",")[1];
                    _fileNameList.add(resultFileName);
                }

                String[] fileNameList = new String[_fileNameList.size()];
                _fileNameList.toArray(fileNameList);
                callBack.getFolderList(fileNameList);
            }
        }.execute(CommandOp.getCommand(OP_GET_FOLDER_LIST) + folderName);
    }

    public static void getPictureThumbnail(final Context context, String directoryName, String fileName,final FlashAirThumbnailCallBack callBack){
        new AsyncTask<String, Void, BitmapDrawable>(){
            @Override
            protected BitmapDrawable doInBackground(String... params) {
                String urlFileName = params[0] ;
                BitmapDrawable drawnIcon = null;
                if((urlFileName.toLowerCase(Locale.getDefault()).endsWith(".jpg")) || (urlFileName.toLowerCase(Locale.getDefault()).endsWith(".jpeg")) ) {
                    Bitmap thumbnail = com.example.android_tutorial_05.FlashAirRequest.getBitmap(urlFileName);
                    drawnIcon = new BitmapDrawable(context.getResources(), thumbnail);
                }
                return drawnIcon;
            }

            @Override
            protected void onPostExecute(BitmapDrawable drawnIcon) {
                if(callBack == null){
                    return ;
                }
                callBack.getThumbnail(drawnIcon);
            }
        }.execute(CommandOp.getFlashAirFilePath(directoryName,fileName));
    }

    private static class CommandOp{
        public static String getCommand(String opString){
            return "http://flashair/command.cgi?op=" + opString + "&DIR=/" ;
        }

        public static String getFlashAirFilePath(String directoryName,String fileName){
            return "http://flashair/thumbnail.cgi?" + directoryName + "/" + fileName;
        }
    }
}
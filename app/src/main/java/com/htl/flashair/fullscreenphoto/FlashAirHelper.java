package com.htl.flashair.fullscreenphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Locale;

public class FlashAirHelper {

    final static String RESULT_SUCCESS = "1";

    static String TAG = "FlashAirHelper";

    final static String OP_GET_FOLDER_COUNT = "101" ;
    final static String OP_GET_FOLDER_LIST = "100" ;
    final static String OP_CHECK_HAS_NEW_FILE = "102" ;

    public static void checkHasNewFiles(final FlashAirCallBack callback) {

        new AsyncTask<String, Void, String>(){
            @Override
            protected String doInBackground(String... params) {
                return FlashAirRequest.getString(params[0]);
            }
            @Override
            protected void onPostExecute(String result) {
                if(callback == null){
                    return ;
                }

                if(result == null){
                    callback.checkNewFile(false);
                }

                callback.checkNewFile(result.equals(RESULT_SUCCESS));
            }
        }.execute(CommandOp.getCommand(OP_CHECK_HAS_NEW_FILE));
    }

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
    public static void getFolderList(String folderName,final FlashAirCallBack callBack){
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

                if(text == null || text.isEmpty()){
                    callBack.onError("Folder not exist or Wifi is not available.");
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

    public static void getPictureThumbnail(final Context context, String directoryName, final String fileName, final FlashAirCallBack callBack){
        new AsyncTask<String, Void, BitmapDrawable>(){
            String _fileName = null;
            @Override
            protected BitmapDrawable doInBackground(String... params) {
                String urlFileName = params[0] ;
                BitmapDrawable drawnIcon = null;
                if((urlFileName.toLowerCase(Locale.getDefault()).endsWith(".jpg")) || (urlFileName.toLowerCase(Locale.getDefault()).endsWith(".jpeg")) ) {
                    _fileName = urlFileName;
                    try {
                        Bitmap thumbnail = FlashAirRequest.getBitmap(urlFileName);
                        drawnIcon = new BitmapDrawable(context.getResources(), thumbnail);
                    }catch(UnknownHostException e){
                        e.printStackTrace();
                    }
                }
                return drawnIcon;
            }

            @Override
            protected void onPostExecute(BitmapDrawable drawnIcon) {
                if(callBack == null){
                    return ;
                }
                callBack.getThumbnail(drawnIcon,_fileName);
            }
        }.execute(CommandOp.getFlashAirFilePath(directoryName,fileName));
    }

    public static void downloadRawJpeg(final ImageView imageView, String filePath, String fileName, final Handler callBackHandler){
        String downloadFile = CommandOp.getDownloadRawJpegPath(filePath ,fileName);
        Log.i(FlashAirHelper.TAG, "downloadFile:" + downloadFile);
        Picasso.with(imageView.getContext())
                .load(downloadFile)
                .resize(800,480)
                .noPlaceholder()
                .into(imageView);
//        new Target() {
//                    @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                        imageView.setImageBitmap(bitmap);
//                        if(callBackHandler != null) {
//                            callBackHandler.sendEmptyMessage(0);
//                        }
//                    }
//                    @Override public void onBitmapFailed(Drawable errorDrawable) { }
//                    @Override public void onPrepareLoad(Drawable placeHolderDrawable) { }
//                });
    }

    private static class CommandOp{

        public static String getCommand(String opString){
            return "http://flashair/command.cgi?op=" + opString + "&DIR=/" ;
        }

        public static String getFlashAirFilePath(String directoryName,String fileName){
            return "http://flashair/thumbnail.cgi?" + directoryName + "/" + fileName;
        }

        public static String getDownloadRawJpegPath(String filePath, String fileName){
            return "http://flashair/" + filePath + "/" + fileName;
        }
    }
}
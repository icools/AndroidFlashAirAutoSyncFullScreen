package com.htl.flashair.fullscreenphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Locale;

// TODO add auto find folder
public class FlashAirHelper {

    private final static String RESULT_SUCCESS = "1";

    private static String TAG = "FlashAirHelper";

    private final static String OP_GET_FOLDER_COUNT = "101" ;
    private final static String OP_GET_FOLDER_LIST = "100" ;
    private final static String OP_CHECK_HAS_NEW_FILE = "102" ;

    private final static int MAX_PHONE_RESIZE_WIDTH = 800;
    private final static int AUTO_FIT_PHOTO_HEIGHT_INDEX = 0;

    public static void checkHasNewFiles(final FlashAirCallBack callback) {

        new AsyncTask<String, Void, String>(){
            @Override
            protected String doInBackground(String... params) {
                try {
                    return FlashAirRequest.getString(params[0]);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if(callback == null){
                    return ;
                }

                if(result == null){
                    callback.checkNewFile(false);
                    callback.onUnknownHost();
                    return ;
                }
                callback.checkNewFile(result.equals(RESULT_SUCCESS));
            }
        }.execute(CommandOp.getCommand(OP_CHECK_HAS_NEW_FILE));
    }

    public static void getFolderCount(final String folderName){
        new AsyncTask<String, Void, String>(){
            @Override
            protected String doInBackground(String... params) {
                try {
                    return RequestHelper.getFolderCount(folderName);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(String fileCount) {
                Log.i(TAG,"ListCount:" + fileCount);
            }
        }.execute();
    }

    // Get a file list from FlashAir DCIM
    public static void getFolderList(final String folderName, final FlashAirCallBack callBack){
        new AsyncTask<String, Void, String>(){
            @Override
            protected String doInBackground(String... params) {
                try {
                    return RequestHelper.getFolderList(folderName);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String text) {
                if(callBack == null){
                    return ;
                }

                if(text == null){
                    callBack.onUnknownHost();
                    return ;
                }

                if(text == null || text.isEmpty()){
                    callBack.onError("Folder not exist or Wifi is not available.");
                    return ;
                }

                String[] result = text.split("\n");
                ArrayList<String> _fileNameList = new ArrayList<>();
                for(int i = 0 ; i < result.length ; i++){
                    Log.i(TAG,"getFolderList:" + result[i]);
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
        }.execute();
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
                .resize(MAX_PHONE_RESIZE_WIDTH,AUTO_FIT_PHOTO_HEIGHT_INDEX)
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

    public static class RequestHelper{
        public static String getFolderList(String folderName) throws UnknownHostException{
            return FlashAirRequest.getString(CommandOp.getCommand(OP_GET_FOLDER_LIST) + folderName);
        }

        public static String getFolderCount(String folderName)  throws UnknownHostException{
            return FlashAirRequest.getString(CommandOp.getCommand(OP_GET_FOLDER_COUNT) + folderName);
        }
    }
}
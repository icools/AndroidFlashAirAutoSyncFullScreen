package com.htl.flashair.fullscreenphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Locale;

public class FlashAirHelper {

    final static String RESULT_SUCCESS = "1";

    public static void checkHasNewFiles(final FlashAirThumbnailCallBack callback) {

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

    public interface FlashAirThumbnailCallBack{
        void getThumbnail(BitmapDrawable bitmapDrawable);
        void getFolderList(String[] files);
        void checkNewFile(boolean hasNewFile);
        void onError(String errorMessage);
    }

    static String TAG = "FlashAirHelper";

    final static String OP_GET_FOLDER_COUNT = "101" ;
    final static String OP_GET_FOLDER_LIST = "100" ;
    final static String OP_CHECK_HAS_NEW_FILE = "102" ;

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

                if(text == null || text.isEmpty()){
                    // TODO
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

    public static void getPictureThumbnail(final Context context, String directoryName, String fileName,final FlashAirThumbnailCallBack callBack){
        new AsyncTask<String, Void, BitmapDrawable>(){
            @Override
            protected BitmapDrawable doInBackground(String... params) {
                String urlFileName = params[0] ;
                BitmapDrawable drawnIcon = null;
                if((urlFileName.toLowerCase(Locale.getDefault()).endsWith(".jpg")) || (urlFileName.toLowerCase(Locale.getDefault()).endsWith(".jpeg")) ) {
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
                callBack.getThumbnail(drawnIcon);
            }
        }.execute(CommandOp.getFlashAirFilePath(directoryName,fileName));
    }

    public static void downloadRawJpeg(ImageView imageView, String filePath, String fileName){
        String downloadFile = CommandOp.getDownloadRawJpegPath(filePath ,fileName);
        Log.i(FlashAirHelper.TAG, "downloadFile:" + downloadFile);
        Picasso.with(imageView.getContext()).load(downloadFile).fit().into(imageView);
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
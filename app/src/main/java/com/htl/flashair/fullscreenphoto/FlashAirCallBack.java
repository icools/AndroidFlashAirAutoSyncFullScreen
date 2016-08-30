package com.htl.flashair.fullscreenphoto;

import android.graphics.drawable.BitmapDrawable;

public interface FlashAirCallBack {
    void getThumbnail(BitmapDrawable bitmapDrawable,String fileName);
    void getFolderList(String[] files);
    void checkNewFile(boolean hasNewFile);
    void onError(String errorMessage);
}
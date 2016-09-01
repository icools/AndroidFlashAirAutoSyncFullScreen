package com.htl.flashair.fullscreenphoto;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

public class MainMenuDialog extends AlertDialog.Builder {
    public MainMenuDialog(Context context) {
        super(context);
        setItems(getMenuList(),null);
    }

    CharSequence[] getMenuList(){
        return new CharSequence[]{"Select Folder","Auto Download"};
    }
}

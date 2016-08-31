package com.htl.flashair.fullscreenphoto;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ViewFlipper;

public class SelectedFolderDialog extends Dialog {

    interface SelectedCallback{
        void onSelectedSuccess(String path);
    }

    ViewFlipper mViewFlipper;
    ListView mListView ;
    EditText mEdtiText;
    Button mBtnSelected;
    String mPath = "DCIM" ;
    String[] mFiles ;

    public SelectedFolderDialog(final Context context,final SelectedCallback callback) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_selected_folder);
        findViews();
        setListener(context,callback);
        showFolder(context,mPath);
    }

    private void findViews(){
        mBtnSelected = (Button) findViewById(R.id.button_select);
        mListView = (ListView) findViewById(R.id.listView);
        mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        mEdtiText = (EditText) findViewById(R.id.editText);
    }

    private void showLoading(){
        mViewFlipper.setDisplayedChild(1);
    }

    private void showListView(){
        mViewFlipper.setDisplayedChild(0);
    }

    private void setListener(final Context context,final SelectedCallback callback){
        mBtnSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectedFolderDialog.this.dismiss();
                callback.onSelectedSuccess(mPath);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = mFiles[position];
                boolean isFolder = !name.contains(".");
                if(isFolder){
                    mPath = mPath + "/" +  name ;
                    mEdtiText.setText(mPath);
                    showFolder(context,mPath);
                }
            }
        });
    }

    private void showFolder(final Context context,final String path){
        showLoading();
        FlashAirHelper.getFolderList(path, new FlashAirCallBack() {
            @Override
            public void getThumbnail(BitmapDrawable bitmapDrawable, String fileName) {

            }

            @Override
            public void getFolderList(String[] files) {
                showListView();
                mFiles = files;
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(context
                        ,android.R.layout.simple_list_item_1
                        ,mFiles);
                mListView.setAdapter(adapter);
            }

            @Override
            public void checkNewFile(boolean hasNewFile) {

            }

            @Override
            public void onError(String errorMessage) {

            }

            @Override
            public void onUnknowHost() {
                // can ping the flashair , retry
                showFolder(context,path);
            }
        });
    }
}
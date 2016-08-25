package com.htl.flashair.fullscreenphoto;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class SelectedFolderDialog extends Dialog {

    interface SelectedCallback{
        void onSelectedSuccess(String path);
    }

    ListView mListView ;
    Button mBtnSelected;
    ArrayAdapter<String> mAapter;
    String mPath = "DCIM" ;
    String[] mFiles ;

    public SelectedFolderDialog(final Context context,final SelectedCallback callback) {
        super(context);
        setContentView(R.layout.activity_selected_folder);
        findViews();
        setListener(context,callback);
        showFolder(context,mPath);
    }

    private void findViews(){
        mBtnSelected = (Button) findViewById(R.id.button_select);
        mListView = (ListView) findViewById(R.id.listView);
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
                    showFolder(context,mPath);
                }

            }
        });
    }

    private void showFolder(final Context context,String path){
        FlashAirHelper.getFolderList(path, new FlashAirCallBack() {
            @Override
            public void getThumbnail(BitmapDrawable bitmapDrawable, String fileName) {

            }

            @Override
            public void getFolderList(String[] files) {
                mFiles = files;
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context
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
        });
    }
}
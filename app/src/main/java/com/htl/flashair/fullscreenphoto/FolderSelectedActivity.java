package com.htl.flashair.fullscreenphoto;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class FolderSelectedActivity extends Activity{

    Button mBtnSelect;
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_folder);
        findViews();
        setListener();
    }

    private void setListener() {

        mBtnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void findViews() {
        mBtnSelect = (Button) this.findViewById(R.id.button_select);
        mListView = (ListView) this.findViewById(R.id.listView);
    }
}

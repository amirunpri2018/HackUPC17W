package com.hackupc2017w.motocare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BluetoothHelper.BluetoothListener {

    private BluetoothHelper mBluetoothHelper;
    private ListView mListView;
    private Button mButton;
    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> mListData = new ArrayList<>();
    private Button mStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothHelper = new BluetoothHelper(getApplicationContext(),this);

        mListView = (ListView) findViewById(R.id.list_view);
        mButton = (Button) findViewById(R.id.search_button);
        mStop = (Button) findViewById(R.id.search_stop);

        mAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.simple_list_item_1,mListData);
        mListView.setAdapter(mAdapter);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListData.clear();
                mBluetoothHelper.getAviableDevices();
            }
        });

        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBluetoothHelper.stopSearch();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothHelper.stopSearch();
    }

    @Override
    public void onDeviceFound(String s) {
        mListData.add(s);
        mAdapter.notifyDataSetChanged();
    }
}

package com.hackupc2017w.motocare;

import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
    private ArrayList<BluetoothDevice> mBluetoothDevices = new ArrayList<>();
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

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mBluetoothHelper.pairDevice(mBluetoothDevices.get(i));
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                mBluetoothHelper.unpairDevice(mBluetoothDevices.get(i));
                return true;
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListData.clear();
                mBluetoothDevices.clear();
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
    public void onDeviceFound(String s,BluetoothDevice d) {
        mListData.add(s);
        mBluetoothDevices.add(d);
        mAdapter.notifyDataSetChanged();
    }
}

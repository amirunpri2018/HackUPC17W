package com.hackupc2017w.motocare;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;


public class BluetoothHelper_LE {

    private String myUUID;
    private Context context;
    private BluetoothListener mBluetoothListener;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mConnectedGatt;

    private Handler mHandler;
    private Runnable stopRunnable;
    private Runnable startRunnable;

    public interface BluetoothListener {
        public void onDeviceFound(String s, BluetoothDevice d);
    }

    public BluetoothHelper_LE(Context c, BluetoothListener l, BluetoothAdapter.LeScanCallback s) {
        myUUID = UUID.randomUUID().toString();
        context = c;
        mBluetoothListener = l;
        mBluetoothManager = (BluetoothManager) c.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mLeScanCallback = s;
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void getAviableDevices(){
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        mHandler.postDelayed(stopRunnable,2500);
    }


    public void stopSearch(){
    }

    public void pairDevice(BluetoothDevice bluetoothDevice){

    }

    public void unpairDevice(BluetoothDevice device) {

    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}

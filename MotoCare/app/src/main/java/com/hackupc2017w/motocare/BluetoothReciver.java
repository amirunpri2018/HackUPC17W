package com.hackupc2017w.motocare;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothReciver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            throw new Exception("Connection Lost");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

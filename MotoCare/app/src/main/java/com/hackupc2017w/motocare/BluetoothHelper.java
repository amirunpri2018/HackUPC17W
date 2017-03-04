package com.hackupc2017w.motocare;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;

import java.util.UUID;


public class BluetoothHelper{

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket btSocket;
    private String myUUID;
    private BroadcastReceiver discoveryMonitor;
    private Context context;

    private BroadcastReceiver startMonitor;
    private BroadcastReceiver stopMonitor;

    private BluetoothListener mListener;

    public interface BluetoothListener {
        public void onDeviceFound(String s);
    }

    public BluetoothHelper(Context c, BluetoothListener l) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        myUUID = UUID.randomUUID().toString();
        System.out.println(myUUID);
        context = c;
        mListener = l;
    }

    public void getAviableDevices(){
        dicoveryBluetooth();
    }

    private void dicoveryBluetooth() {
        System.out.println("Iniciando busqueda");
        // Aqui implementamos el BrodcastReceiver
        discoveryMonitor = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                System.out.println("ACCIO: "+action);
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    mListener.onDeviceFound(device.getName() + "\n" + device.getAddress());
                    System.out.println(device.getName() + "\n" + device.getAddress());
                }
            }
        };


        startMonitor = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("STARTING SEARCH");
            }
        };

        stopMonitor = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("STOPING SEARCH");
            }
        };

        context.registerReceiver(discoveryMonitor,new IntentFilter(BluetoothDevice.ACTION_FOUND));
        context.registerReceiver(startMonitor,new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        context.registerReceiver(stopMonitor, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        boolean b = mBluetoothAdapter.startDiscovery();
    }

    public void stopSearch(){
        mBluetoothAdapter.cancelDiscovery();
        if (discoveryMonitor != null){
            context.unregisterReceiver(discoveryMonitor);
        }
    }


    private class ConnectAsyncTask extends AsyncTask<BluetoothDevice, Integer, BluetoothSocket> {

        private BluetoothSocket mmSocket;
        private BluetoothDevice mmDevice;

        @Override
        protected BluetoothSocket doInBackground(BluetoothDevice... device) {

            mmDevice = device[0];

            try {
                mmSocket = mmDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(myUUID));
                mmSocket.connect();

            } catch (Exception e) {
            }

            return mmSocket;
        }

        @Override
        protected void onPostExecute(BluetoothSocket result) {

            btSocket = result;

        }

    }

}

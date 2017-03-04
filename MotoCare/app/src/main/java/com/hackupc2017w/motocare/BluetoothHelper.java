package com.hackupc2017w.motocare;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;


public class BluetoothHelper{

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket btSocket;
    private String myUUID;
    private BroadcastReceiver discoveryMonitor;
    private Context context;

    private BroadcastReceiver startMonitor;
    private BroadcastReceiver stopMonitor;
    private BroadcastReceiver mPairReceiver;

    private BluetoothListener mListener;

    private BluetoothDevice paired;

    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

    public interface BluetoothListener {
        public void onDeviceFound(String s, BluetoothDevice d);
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
                    mListener.onDeviceFound(device.getName() + "\n" + device.getAddress(),device);
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

        mPairReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                    final int state 		= intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                    final int prevState	= intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                    if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                        showToast("Paired");
                    } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
                        showToast("Unpaired");
                    }
                }
            }
        };

        context.registerReceiver(discoveryMonitor,new IntentFilter(BluetoothDevice.ACTION_FOUND));
        context.registerReceiver(startMonitor,new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        context.registerReceiver(stopMonitor, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        context.registerReceiver(mPairReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        boolean b = mBluetoothAdapter.startDiscovery();
    }

    public void stopSearch(){
        if (mBluetoothAdapter != null){
            mBluetoothAdapter.cancelDiscovery();
        }
        if (discoveryMonitor != null){
            context.unregisterReceiver(discoveryMonitor);
            context.unregisterReceiver(mPairReceiver);
            discoveryMonitor = null;
        }
    }

    public void pairDevice(BluetoothDevice bluetoothDevice){
        try {
            Method method = bluetoothDevice.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(bluetoothDevice, (Object[]) null);
            paired = bluetoothDevice;
            openBT();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unpairDevice(BluetoothDevice device) {
        try {
            closeBT();
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
            paired = null;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    //Codigo a mejorar
    void openBT() throws IOException
    {
        UUID uuid = UUID.fromString(myUUID); //Standard SerialPortService ID
        btSocket = paired.createRfcommSocketToServiceRecord(uuid);
        btSocket.connect();
        mmOutputStream = btSocket.getOutputStream();
        mmInputStream = btSocket.getInputStream();

        beginListenForData();

        showToast("Bluetooth Opened");
    }

    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            showToast(data);
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    void sendData() throws IOException
    {
        String msg = "Missatge de proba";
        msg += "\n";
        mmOutputStream.write(msg.getBytes());
        showToast("Data Sent");
    }

    void closeBT() throws IOException
    {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        btSocket.close();
        showToast("Bluetooth Closed");
    }


}

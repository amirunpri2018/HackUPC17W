package com.hackupc2017w.motocare;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BluetoothLEService extends Service {
    public BluetoothLEService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

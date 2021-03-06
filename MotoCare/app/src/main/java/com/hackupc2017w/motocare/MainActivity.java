package com.hackupc2017w.motocare;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.robotpajamas.blueteeth.BlueteethDevice;
import com.robotpajamas.blueteeth.BlueteethManager;
import com.robotpajamas.blueteeth.BlueteethResponse;
import com.robotpajamas.blueteeth.listeners.OnCharacteristicReadListener;
import com.robotpajamas.blueteeth.listeners.OnConnectionChangedListener;
import com.robotpajamas.blueteeth.listeners.OnScanCompletedListener;
import com.robotpajamas.blueteeth.listeners.OnServicesDiscoveredListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private Button mButton;
    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> mListData = new ArrayList<>();
    private ArrayList<BlueteethDevice> mBluetoothDevices = new ArrayList<>();
    private Button mStop;

    private static final int TRUE = 1;
    private static final int FALSE = 0;

    private BlueteethDevice currentDevice;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mListView = (ListView) findViewById(R.id.list_view);
        mButton = (Button) findViewById(R.id.search_button);
        mStop = (Button) findViewById(R.id.search_stop);

        mAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_list_item_1, mListData);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPreAccident();
            }
        });

        intent = new Intent(getApplicationContext(), PlayerService.class);


        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final BlueteethDevice device = mBluetoothDevices.get(i);
                device.connect(true, new OnConnectionChangedListener() {
                    @Override
                    public void call(final boolean isConnected) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Connection changed " + isConnected, Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
                        device.discoverServices(new OnServicesDiscoveredListener() {
                            @Override
                            public void call(BlueteethResponse response) {
                                Log.v("Main activity 2", response.name());
                            }
                        });

                        if (isConnected) {
                            currentDevice = device;
                            device.discoverServices(new OnServicesDiscoveredListener() {
                                @Override
                                public void call(BlueteethResponse response) {
                                    Log.v("Main activity", response.name());
                                    handler.post(runnable);
                                }
                            });
                        } else {
                            currentDevice = null;
                            handler.removeCallbacks(runnable);
                        }
                    }

                });
                return true;
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListData.clear();
                mBluetoothDevices.clear();

                BlueteethManager.with(getApplicationContext()).scanForPeripherals(5000, new OnScanCompletedListener() {
                    @Override
                    public void call(List<BlueteethDevice> blueteethDevices) {
                        for (BlueteethDevice device : blueteethDevices) {
                            if (!TextUtils.isEmpty(device.getBluetoothDevice().getName())) {
                                Log.v("MAIN ACTIVITY", device.getName() + " " + device.getMacAddress());
                                boolean trobat = false;
                                for (String s: mListData) {
                                    if(s.equals(device.getName())) trobat = true;
                                }
                                if(!trobat){
                                    mBluetoothDevices.add(device);
                                    mListData.add(device.getName());
                                }
                            }
                        }

                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

    }

    final Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            read();
            handler.postDelayed(runnable, 500);
        }
    };

    void read() {

        currentDevice.readCharacteristic(UUID.fromString("19B10012-E8F2-537E-4F6C-D104768A1214"),
                UUID.fromString("19B10010-E8F2-537E-4F6C-D104768A1214"), new OnCharacteristicReadListener() {
                    @Override
                    public void call(BlueteethResponse response, byte[] data) {
                        //String resposta = new String(data);
                        if (data.length > 0) {
                            Log.v("MAIN", "" + data[0]);

                            if (data[0] == TRUE) {
                                startPreAccident();
                            } else {
                                stopService(intent);
                            }
                        }
                    }
                });
    }

    private void startPreAccident() {
        scheduleNotification(getNotification("Are you okay?"), 5000);
        startService(intent);
    }

    private void scheduleNotification(Notification notification, int delay) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent = new Intent(this,NotificationPublisher.class);
        intent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        intent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        sendBroadcast(intent);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) getSystemService(getApplicationContext().ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("MotoCare");
        builder.setContentText(content);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Intent serviceIntent = new Intent(this, PlayerService.class);
        serviceIntent.putExtra("state", "stop");
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification.Action action = new Notification.Action(android.R.drawable.ic_input_delete, "STOP", pendingIntent);
        builder.addAction(action);
        return builder.build();
    }

}

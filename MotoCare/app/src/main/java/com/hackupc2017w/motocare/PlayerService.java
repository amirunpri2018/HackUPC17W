package com.hackupc2017w.motocare;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.IBinder;

import java.io.File;
import java.io.IOException;

public class PlayerService extends Service {
    private final MediaPlayer mediaPlayer;

    public PlayerService() {
        mediaPlayer = new MediaPlayer();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        File sdCard = Environment.getExternalStorageDirectory();
        File song = new File(sdCard.getAbsolutePath() + "/alarm.mp3");
        try {
            mediaPlayer.setDataSource(song.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            final AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            final int originalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.setLooping(false);
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer.reset();
    }
}

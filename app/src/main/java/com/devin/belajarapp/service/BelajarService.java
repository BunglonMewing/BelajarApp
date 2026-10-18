package com.devin.belajarapp.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.devin.belajarapp.R;
import com.devin.belajarapp.data.AlarmScheduler;
import com.devin.belajarapp.receiver.AlarmReceiver;

public class BelajarService extends Service {

    private static final int FOREGROUND_NOTIF_ID = 9999;
    private int jadwalId;
    private String namaSesi;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            jadwalId = intent.getIntExtra(AlarmScheduler.EXTRA_JADWAL_ID, -1);
            namaSesi = intent.getStringExtra(AlarmScheduler.EXTRA_NAMA_SESI);
            if (namaSesi == null) namaSesi = "Belajar";
        }

        startForeground(FOREGROUND_NOTIF_ID, buildForegroundNotif());

        // Set alarm tiap jam pertama (60 menit dari sekarang)
        scheduleNextHourAlarm();

        return START_STICKY;
    }

    private void scheduleNextHourAlarm() {
        long nextTrigger = System.currentTimeMillis() + (60 * 60 * 1000L);
        AlarmScheduler.setAlarmTiapJam(this, jadwalId, namaSesi, nextTrigger);
    }

    private Notification buildForegroundNotif() {
        return new NotificationCompat.Builder(this, AlarmReceiver.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle("Sesi Belajar Aktif")
                .setContentText("\"" + namaSesi + "\" sedang berlangsung. Tetap semangat!")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // Batalkan alarm tiap jam saat sesi selesai
        AlarmScheduler.batalkan(this, jadwalId);
        super.onDestroy();
    }
}

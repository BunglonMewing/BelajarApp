package com.devin.belajarapp.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.devin.belajarapp.R;
import com.devin.belajarapp.data.AlarmScheduler;
import com.devin.belajarapp.data.MotivasiProvider;
import com.devin.belajarapp.model.JadwalBelajar;
import com.devin.belajarapp.service.BelajarService;
import com.devin.belajarapp.ui.MainActivity;
import com.devin.belajarapp.data.JadwalRepository;

import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "belajar_channel";
    public static final String CHANNEL_ID_TIAP_JAM = "tiap_jam_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;

        int jadwalId = intent.getIntExtra(AlarmScheduler.EXTRA_JADWAL_ID, -1);
        String namaSesi = intent.getStringExtra(AlarmScheduler.EXTRA_NAMA_SESI);

        createChannels(context);

        switch (action) {
            case AlarmScheduler.ACTION_SESI_MULAI:
                tampilkanNotifMulai(context, jadwalId, namaSesi);
                startBelajarService(context, jadwalId, namaSesi);
                rescheduleNext(context, jadwalId);
                break;

            case AlarmScheduler.ACTION_SESI_SELESAI:
                tampilkanNotifSelesai(context, jadwalId, namaSesi);
                stopBelajarService(context);
                rescheduleNext(context, jadwalId);
                break;

            case AlarmScheduler.ACTION_TIAP_JAM:
                tampilkanNotifTiapJam(context, jadwalId, namaSesi);
                // BelajarService akan set ulang alarm jam berikutnya
                break;
        }
    }

    private void tampilkanNotifMulai(Context context, int id, String namaSesi) {
        String isi = MotivasiProvider.getMotivasiMulai(namaSesi != null ? namaSesi : "Belajar");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle("Waktunya Belajar!")
                .setContentText(isi)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(isi))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(getMainIntent(context));

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) nm.notify(id * 10, builder.build());
    }

    private void tampilkanNotifSelesai(Context context, int id, String namaSesi) {
        String isi = MotivasiProvider.getMotivasiSelesai(namaSesi != null ? namaSesi : "Belajar");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle("Sesi Selesai!")
                .setContentText(isi)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(isi))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(getMainIntent(context));

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) nm.notify(id * 10 + 1, builder.build());
    }

    private void tampilkanNotifTiapJam(Context context, int id, String namaSesi) {
        String isi = MotivasiProvider.getMotivasiTiapJam();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_TIAP_JAM)
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle("Tetap Semangat!")
                .setContentText(isi)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(isi))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(getMainIntent(context));

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) nm.notify(id * 10 + 2, builder.build());
    }

    private void startBelajarService(Context context, int jadwalId, String namaSesi) {
        Intent serviceIntent = new Intent(context, BelajarService.class);
        serviceIntent.putExtra(AlarmScheduler.EXTRA_JADWAL_ID, jadwalId);
        serviceIntent.putExtra(AlarmScheduler.EXTRA_NAMA_SESI, namaSesi);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }

    private void stopBelajarService(Context context) {
        context.stopService(new Intent(context, BelajarService.class));
    }

    private void rescheduleNext(Context context, int jadwalId) {
        JadwalRepository repo = new JadwalRepository(context);
        List<JadwalBelajar> list = repo.getAllJadwal();
        for (JadwalBelajar j : list) {
            if (j.getId() == jadwalId && j.isAktif()) {
                AlarmScheduler.jadwalkan(context, j);
                break;
            }
        }
    }

    private PendingIntent getMainIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private void createChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm == null) return;

            NotificationChannel ch1 = new NotificationChannel(
                    CHANNEL_ID, "Pengingat Belajar", NotificationManager.IMPORTANCE_HIGH);
            ch1.setDescription("Notifikasi saat sesi belajar mulai dan selesai");

            NotificationChannel ch2 = new NotificationChannel(
                    CHANNEL_ID_TIAP_JAM, "Motivasi Tiap Jam", NotificationManager.IMPORTANCE_DEFAULT);
            ch2.setDescription("Notifikasi motivasi tiap jam selama sesi berlangsung");

            nm.createNotificationChannel(ch1);
            nm.createNotificationChannel(ch2);
        }
    }
}

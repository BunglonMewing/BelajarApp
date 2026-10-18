package com.devin.belajarapp.data;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.devin.belajarapp.model.JadwalBelajar;
import com.devin.belajarapp.receiver.AlarmReceiver;

import java.util.Calendar;
import java.util.List;

public class AlarmScheduler {

    public static final String ACTION_SESI_MULAI = "ACTION_SESI_MULAI";
    public static final String ACTION_SESI_SELESAI = "ACTION_SESI_SELESAI";
    public static final String ACTION_TIAP_JAM = "ACTION_TIAP_JAM";

    public static final String EXTRA_JADWAL_ID = "EXTRA_JADWAL_ID";
    public static final String EXTRA_NAMA_SESI = "EXTRA_NAMA_SESI";

    // Mapping nama hari ke Calendar day of week
    public static int hariToCalendar(String hari) {
        switch (hari.toUpperCase()) {
            case "MINGGU":   return Calendar.SUNDAY;
            case "SENIN":    return Calendar.MONDAY;
            case "SELASA":   return Calendar.TUESDAY;
            case "RABU":     return Calendar.WEDNESDAY;
            case "KAMIS":    return Calendar.THURSDAY;
            case "JUMAT":    return Calendar.FRIDAY;
            case "SABTU":    return Calendar.SATURDAY;
            default:         return Calendar.MONDAY;
        }
    }

    public static void jadwalkanSemua(Context context) {
        JadwalRepository repo = new JadwalRepository(context);
        List<JadwalBelajar> list = repo.getAllJadwal();
        for (JadwalBelajar j : list) {
            if (j.isAktif()) {
                jadwalkan(context, j);
            } else {
                batalkan(context, j.getId());
            }
        }
    }

    public static void jadwalkan(Context context, JadwalBelajar jadwal) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        // Set alarm mingguan untuk sesi mulai
        Calendar calMulai = getNextCalendar(jadwal.getHari(), jadwal.getJamMulai(), jadwal.getMenitMulai());

        Intent intentMulai = new Intent(context, AlarmReceiver.class);
        intentMulai.setAction(ACTION_SESI_MULAI);
        intentMulai.putExtra(EXTRA_JADWAL_ID, jadwal.getId());
        intentMulai.putExtra(EXTRA_NAMA_SESI, jadwal.getNamaSesi());

        PendingIntent piMulai = PendingIntent.getBroadcast(
                context,
                jadwal.getId() * 100,
                intentMulai,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
            am.setWindow(AlarmManager.RTC_WAKEUP, calMulai.getTimeInMillis(),
                    60 * 1000L, piMulai);
        } else {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calMulai.getTimeInMillis(), piMulai);
        }

        // Set alarm untuk sesi selesai
        Calendar calSelesai = getNextCalendar(jadwal.getHari(), jadwal.getJamSelesai(), jadwal.getMenitSelesai());

        Intent intentSelesai = new Intent(context, AlarmReceiver.class);
        intentSelesai.setAction(ACTION_SESI_SELESAI);
        intentSelesai.putExtra(EXTRA_JADWAL_ID, jadwal.getId());
        intentSelesai.putExtra(EXTRA_NAMA_SESI, jadwal.getNamaSesi());

        PendingIntent piSelesai = PendingIntent.getBroadcast(
                context,
                jadwal.getId() * 100 + 1,
                intentSelesai,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
            am.setWindow(AlarmManager.RTC_WAKEUP, calSelesai.getTimeInMillis(),
                    60 * 1000L, piSelesai);
        } else {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calSelesai.getTimeInMillis(), piSelesai);
        }
    }

    // Dipanggil oleh BelajarService untuk set pengingat tiap jam
    public static void setAlarmTiapJam(Context context, int jadwalId, String namaSesi, long triggerTime) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(ACTION_TIAP_JAM);
        intent.putExtra(EXTRA_JADWAL_ID, jadwalId);
        intent.putExtra(EXTRA_NAMA_SESI, namaSesi);

        PendingIntent pi = PendingIntent.getBroadcast(
                context,
                jadwalId * 100 + 2,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
            am.setWindow(AlarmManager.RTC_WAKEUP, triggerTime, 60 * 1000L, pi);
        } else {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pi);
        }
    }

    public static void batalkan(Context context, int jadwalId) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        for (int suffix = 0; suffix <= 2; suffix++) {
            Intent intent = new Intent(context, AlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(
                    context,
                    jadwalId * 100 + suffix,
                    intent,
                    PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
            );
            if (pi != null) am.cancel(pi);
        }
    }

    private static Calendar getNextCalendar(String hari, int jam, int menit) {
        Calendar cal = Calendar.getInstance();
        int targetDay = hariToCalendar(hari);

        cal.set(Calendar.HOUR_OF_DAY, jam);
        cal.set(Calendar.MINUTE, menit);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        int todayDay = cal.get(Calendar.DAY_OF_WEEK);
        int daysUntil = (targetDay - todayDay + 7) % 7;

        if (daysUntil == 0 && cal.getTimeInMillis() <= System.currentTimeMillis()) {
            daysUntil = 7;
        }

        cal.add(Calendar.DAY_OF_YEAR, daysUntil);
        return cal;
    }
}

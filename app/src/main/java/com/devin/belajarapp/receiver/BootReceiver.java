package com.devin.belajarapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.devin.belajarapp.data.AlarmScheduler;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Jadwalkan ulang semua alarm setelah HP restart
            AlarmScheduler.jadwalkanSemua(context);
        }
    }
}

package com.example.ck_mobile.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import org.jspecify.annotations.NonNull;

public class SessionWatchService extends Service {

    public static void start(@NonNull Context context) {
        Intent intent = new Intent(context.getApplicationContext(), SessionWatchService.class);
        try {
            context.getApplicationContext().startService(intent);
        } catch (Exception ignored) {
        }
    }

    public static void stop(@NonNull Context context) {
        Intent intent = new Intent(context.getApplicationContext(), SessionWatchService.class);
        try {
            context.getApplicationContext().stopService(intent);
        } catch (Exception ignored) {
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        SessionManager.getInstance(this).onTaskRemovedFromRecents();
        stopSelf();
        super.onTaskRemoved(rootIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
package com.example.sanjay.canteen_clint.backgroundClass;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class Notification extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.O)

         startForeground(1,new android.app.Notification());
    }
}

package de.NeonSoft.neopowermenu.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import de.NeonSoft.neopowermenu.Receiver.ScreenOnReceiver;

public class ScreenOffService extends Service {
    private static boolean sticky = false;
    BroadcastReceiver mReceiver;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("NPM", "[ScreenOffService] service started");
        IntentFilter filter = new IntentFilter("android.intent.action.SCREEN_ON");
        this.mReceiver = new ScreenOnReceiver();
        registerReceiver(this.mReceiver, filter);
        registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                ScreenOffService.sticky = true;
            }
        }, new IntentFilter("de.NeonSoft.neopowermenu.FakePowerOffBroadcastSticky"));
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (((PowerManager) ScreenOffService.this.getSystemService(Context.POWER_SERVICE)).isScreenOn() && !ScreenOffService.sticky) {
                    ScreenOffService.this.stopSelf();
                }
            }
        }, 40000);
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        unregisterReceiver(this.mReceiver);
        Log.i("NPM", "[ScreenOffService] service stopped");
        super.onDestroy();
    }
}

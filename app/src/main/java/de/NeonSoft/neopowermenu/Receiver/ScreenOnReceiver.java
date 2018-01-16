package de.NeonSoft.neopowermenu.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import java.io.DataOutputStream;
import java.io.IOException;

import de.NeonSoft.neopowermenu.helpers.PreferenceNames;
import de.NeonSoft.neopowermenu.helpers.SettingsManager;

public class ScreenOnReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        SharedPreferences pref = SettingsManager.getInstance(context).getMainPrefs();
        Log.d("NPM", "[ScreenOnReceiver] Screen on");
        if (pref.getInt(PreferenceNames.pFakeOffStrategy, 0) == 2) {
            try {
                new DataOutputStream(Runtime.getRuntime().exec("su").getOutputStream()).writeBytes("echo 0 > /sys/class/graphics/fb0/blank\necho 1 > /sys/class/graphics/fb0/blank\n");
            } catch (IOException e) {
                Log.e("NPM", "[ScreenOnReceiver] IOException:", e);
            }
        }
    }
}

package de.NeonSoft.neopowermenu;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

public class deviceAdmin extends DeviceAdminReceiver {


    static SharedPreferences getSamplePreferences(Context context) {
        return context.getSharedPreferences(
                DeviceAdminReceiver.class.getName(), 0);
    }

    static String PREF_PASSWORD_QUALITY = "password_quality";
    static String PREF_PASSWORD_LENGTH = "password_length";
    static String PREF_MAX_FAILED_PW = "max_failed_pw";

    void showToast(Context context, CharSequence msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        //showToast(context, "Sample Device Admin: enabled");
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return context.getString(R.string.permissionsScreen_DeviceAdminDisable);
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        //showToast(context, "Sample Device Admin: disabled");
    }

}

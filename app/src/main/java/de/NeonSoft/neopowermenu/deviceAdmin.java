package de.NeonSoft.neopowermenu;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

public class deviceAdmin extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return context.getString(R.string.permissionsScreen_DeviceAdminDisable);
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
    }

}

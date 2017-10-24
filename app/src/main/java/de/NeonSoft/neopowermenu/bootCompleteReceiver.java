package de.NeonSoft.neopowermenu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.io.File;

import de.NeonSoft.neopowermenu.helpers.helper;

public class bootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            prepareAssets(context);
        }
    }

    // copies required files from assets to file system
    private void prepareAssets(Context context) {
        Log.d("NPM:bCR","Preparing assets...");
        File f;

        // prepare alternative screenrecord binary if doesn't exist yet
        f = new File(context.getFilesDir() + "/screenrecord");
        String assetName = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            assetName = Build.SUPPORTED_64_BIT_ABIS.length > 0 ?
                    "screenrecord_arm64" : "screenrecord";
        } else {
            assetName = "screenrecord_kk";
        }
        if (!f.exists()) {
            Log.d("NPM:bCR","Preparing "+assetName+"...");
            helper.writeAssetToFile(context, assetName, f);
            if (f.exists()) {
                f.setExecutable(true);
                Log.d("NPM:bCR","Prepared "+assetName+"!");
            } else {
                Log.e("NPM:bCR","Failed to prepare "+assetName+"...");
            }
        }
        Log.d("NPM:bCR","Assets prepared!");
    }
}
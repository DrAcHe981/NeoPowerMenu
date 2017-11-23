package de.NeonSoft.neopowermenu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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
        Log.d("NPM","Preparing assets...");
        File f;

        AssetManager asstmngr = context.getAssets();

        // prepare alternative screenrecord binary if doesn't exist yet
        f = new File(context.getFilesDir() + "/screenrecord");
        String assetName = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            assetName = Build.SUPPORTED_64_BIT_ABIS.length > 0 ?
                    "screenrecord_n_arm64" : "screenrecord_n";
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            assetName = Build.SUPPORTED_64_BIT_ABIS.length > 0 ?
                    "screenrecord_arm64" : "screenrecord";
        } else {
            assetName = "screenrecord_kk";
        }
        try {
            InputStream internalAsset = asstmngr.open(assetName);
            if (f.length() != internalAsset.available()) {
                f.delete();
            }
            if (!f.exists()) {
                Log.d("NPM","Preparing "+assetName+"...");
                helper.writeAssetToFile(context, assetName, f);
                if (f.exists()) {
                    f.setExecutable(true);
                    Log.d("NPM","Prepared "+assetName+"!");
                } else {
                    Log.e("NPM","Failed to prepare "+assetName+"...");
                }
            }
        } catch (IOException e) {
            Log.e("NPM","Failed to prepare "+assetName+"...", e);
        }
        Log.d("NPM","Assets prepared!");
    }
}
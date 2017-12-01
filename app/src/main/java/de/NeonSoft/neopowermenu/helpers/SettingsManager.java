/*
 * Copyright (C) 2017 Peter Gregus for GravityBox Project (C3C076@xda)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.NeonSoft.neopowermenu.helpers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.FileObserver;
import android.util.Log;
import android.widget.Toast;

import de.NeonSoft.neopowermenu.MainActivity;
import de.NeonSoft.neopowermenu.R;
import de.NeonSoft.neopowermenu.xposed.XposedUtils;

public class SettingsManager {

    public interface FileObserverListener {
        void onFileUpdated(String path);
        void onFileAttributesChanged(String path);
    }

    private static Context mContext;
    private static SettingsManager mInstance;
    private WorldReadablePrefs mPrefsMain;
    private FileObserver mFileObserver;
    private List<FileObserverListener> mFileObserverListeners;

    private SettingsManager(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mContext = (XposedUtils.USE_DEVICE_PROTECTED_STORAGE() && !context.isDeviceProtectedStorage() ? context.createDeviceProtectedStorageContext() : context);

            PackageManager m = context.getPackageManager();
            String s = context.getPackageName();
            try {
                PackageInfo p = m.getPackageInfo(s, 0);
                s = p.applicationInfo.dataDir;
            } catch (PackageManager.NameNotFoundException e) {
                Log.w("NPM", "Error Package name not found ", e);
            }
            if (XposedUtils.USE_DEVICE_PROTECTED_STORAGE() && new File(s + "/shared_prefs/" + MainActivity.class.getPackage().getName() + "_preferences.xml").exists() && !new File("/data/user_de/0/" + MainActivity.class.getPackage().getName() + "/shared_prefs/" + MainActivity.class.getPackage().getName() + "_preferences.xml").exists()) {
                File oldPref = new File(s + "/shared_prefs/" + MainActivity.class.getPackage().getName() + "_preferences.xml");
                Log.d("NPM","Found old preference file, trying to move.");
                if (oldPref.renameTo(new File("/data/user_de/0/" + MainActivity.class.getPackage().getName() + "/shared_prefs/" + MainActivity.class.getPackage().getName() + "_preferences.xml"))) {
                    Log.d("NPM","File moved!");
                } else {
                    Log.e("NPM","Moving failed.");
                }
            }
        } else {
            mContext = context;
        }
        mFileObserverListeners = new ArrayList<>();
        mPrefsMain =  new WorldReadablePrefs(mContext, MainActivity.class.getPackage().getName() + "_preferences");
        mFileObserverListeners.add(mPrefsMain);

        registerFileObserver();
    }

    public static synchronized SettingsManager getInstance(Context context) {
        if (context == null && mInstance == null)
            throw new IllegalArgumentException("Context cannot be null");

        if (mInstance == null) {
            mInstance = new SettingsManager(context.getApplicationContext() != null ? context.getApplicationContext() : context);
        }
        return mInstance;
    }

    public static String getSettingsFile(Context context) {
        if (XposedUtils.USE_DEVICE_PROTECTED_STORAGE()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return "/data/user_de/0/" + MainActivity.class.getPackage().getName() + "/shared_prefs/" + MainActivity.class.getPackage().getName() + "_preferences.xml";
            }
        }

        PackageManager m = context.getPackageManager();
        String s = context.getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("NPM", "Error Package name not found ", e);
        }
        return s + "/shared_prefs/" + MainActivity.class.getPackage().getName() + "_preferences.xml";
    }

    public String getOrCreateUuid() {
        String uuid = mPrefsMain.getString("settings_uuid", null);
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            mPrefsMain.edit().putString("settings_uuid", uuid).commit();
        }
        return uuid;
    }

    public void resetUuid(String uuid) {
        mPrefsMain.edit().putString("settings_uuid", uuid).commit();
    }

    public void resetUuid() {
        resetUuid(null);
    }

    public void fixFolderPermissionsAsync() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // main dir
                File pkgFolder = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    pkgFolder = mContext.getDataDir();
                } else {
                    PackageManager m = mContext.getPackageManager();
                    String s = mContext.getPackageName();
                    try {
                        PackageInfo p = m.getPackageInfo(s, 0);
                        pkgFolder = new File(p.applicationInfo.dataDir);
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.w("NPM", "Error Package name not found ", e);
                    }
                }
                if (pkgFolder.exists()) {
                    pkgFolder.setExecutable(true, false);
                    pkgFolder.setReadable(true, false);
                }
                // cache dir
                File cacheFolder = mContext.getCacheDir();
                if (cacheFolder.exists()) {
                    cacheFolder.setExecutable(true, false);
                    cacheFolder.setReadable(true, false);
                }
                // files dir
                File filesFolder = mContext.getFilesDir();
                if (filesFolder.exists()) {
                    filesFolder.setExecutable(true, false);
                    filesFolder.setReadable(true, false);
                    for (File f : filesFolder.listFiles()) {
                        f.setExecutable(true, false);
                        f.setReadable(true, false);
                    }
                }
                // app picker
                File appPickerFolder = new File(mContext.getFilesDir() + "/app_picker");
                if (appPickerFolder.exists()) {
                    appPickerFolder.setExecutable(true, false);
                    appPickerFolder.setReadable(true, false);
                    for (File f : appPickerFolder.listFiles()) {
                        f.setExecutable(true, false);
                        f.setReadable(true, false);
                    }
                }
            }
        });
    }

    public WorldReadablePrefs getMainPrefs() {
        return mPrefsMain;
    }

    private void registerFileObserver() {
        File pkgFolder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            pkgFolder = mContext.getDataDir();
        } else {
            PackageManager m = mContext.getPackageManager();
            String s = mContext.getPackageName();
            try {
                PackageInfo p = m.getPackageInfo(s, 0);
                pkgFolder = new File(p.applicationInfo.dataDir);
            } catch (PackageManager.NameNotFoundException e) {
                Log.w("NPM", "Error Package name not found ", e);
            }
        }
        if (pkgFolder != null) {
            mFileObserver = new FileObserver(pkgFolder.getPath() + "/shared_prefs",
                    FileObserver.ATTRIB | FileObserver.CLOSE_WRITE) {
                @Override
                public void onEvent(int event, String path) {
                    for (FileObserverListener l : mFileObserverListeners) {
                        if ((event & FileObserver.ATTRIB) != 0)
                            l.onFileAttributesChanged(path);
                        if ((event & FileObserver.CLOSE_WRITE) != 0)
                            l.onFileUpdated(path);
                    }
                }
            };
            mFileObserver.startWatching();
        } else {
            Log.e("NPM","Could not start FileObserver: pkgFolder is null.");
        }
    }
}
package de.NeonSoft.neopowermenu.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import de.NeonSoft.neopowermenu.R;
import de.NeonSoft.neopowermenu.helpers.PreferenceNames;
import de.NeonSoft.neopowermenu.helpers.SettingsManager;
import de.NeonSoft.neopowermenu.helpers.helper;
import de.NeonSoft.neopowermenu.xposed.*;

import android.app.*;

public class ScreenRecordingService extends Service {

    private static Context mContext;
    private static NotificationManager nm;

    private static final String TAG = "NPM";
    boolean DeepLogging = false;

    private static final int SCREENRECORD_NOTIFICATION_ID = 3;
    private static final int MSG_TASK_ENDED = 1;
    private static final int MSG_TASK_ERROR = 2;
    private static final String TMP_PATH = Environment.getExternalStorageDirectory() + "/__tmp_screenrecord.mp4";
    private static long iCountdown = 0;

    public static final String ACTION_SCREEN_RECORDING_START = "neopowermenu.intent.action.SCREEN_RECORDING_START";
    public static final String ACTION_SCREEN_RECORDING_STOP = "neopowermenu.intent.action.SCREEN_RECORDING_STOP";
    public static final String ACTION_TOGGLE_SCREEN_RECORDING = "neopowermenu.intent.action.TOGGLE_SCREEN_RECORDING";
    public static final String ACTION_SCREEN_RECORDING_STATUS_CHANGED = "neopowermenu.intent.action.SCREEN_RECORDING_STATUS_CHANGED";
    public static final String ACTION_TOGGLE_SHOW_TOUCHES = "neopowermenu.intent.action.SCREEN_RECORDING_TOGGLE_SHOW_TOUCHES";
    public static final String ACTION_SCREEN_RECORDING_OPEN = "neopowermenu.intent.action.SCREEN_RECORDING_OPEN";
    public static final String ACTION_SCREEN_RECORDING_DELETE = "neopowermenu.intent.action.SCREEN_RECORDING_DELETE";
    public static final String EXTRA_RECORDING_STATUS = "recordingStatus";
    public static final String EXTRA_STATUS_MESSAGE = "statusMessage";
    public static final String EXTRA_SHOW_TOUCHES = "showTouches";
    public static final String SETTING_SHOW_TOUCHES = "show_touches";

    public static final int STATUS_IDLE = 0;
    public static final int STATUS_RECORDING = 1;
    public static final int STATUS_PROCESSING = 2;
    public static final int STATUS_ERROR = -1;

    private static Handler mHandler;
    private Notification mRecordingNotif;
    private int mRecordingStatus;
    private int mShowTouchesDefault = 0;
    private SharedPreferences mPrefs;
    private boolean mUseStockBinary;

    private CaptureThread mCaptureThread;
    private String screenrecordReturnCode = "";
    private Toast tToast;
    private Context baseContext;

    private class CaptureThread extends Thread {
        public void run() {
            try {
                // Firstly, make sure we are able to get to pid field of ProcessImpl class
                final Class<?> classProcImpl;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    classProcImpl = Class.forName("java.lang.UNIXProcess");
                } else {
                    classProcImpl = Class.forName("java.lang.ProcessManager$ProcessImpl");
                }
                final Field fieldPid = classProcImpl.getDeclaredField("pid");
                fieldPid.setAccessible(true);

                // choose screenrecord binary and prepare command
                List<String> command = new ArrayList<>();
                //command.add("su");
                if(DeepLogging) Log.d(TAG, "Using binary located in: " + getBinaryPath());
                if (!new File(getBinaryPath()).exists() || !new File(getBinaryPath()).canRead() || !new File(getBinaryPath()).canExecute()) {
                    Log.e(TAG, "Binary not found!");
                    Toast.makeText(mContext, getString(R.string.screenrecord_notif_binarynotFound), Toast.LENGTH_LONG).show();
                    stopSelf();
                }
                command.add(getBinaryPath());
                if(DeepLogging) Log.d(TAG, "Setting screenrecorder configurations...");
                if (!mUseStockBinary && mPrefs.getBoolean(PreferenceNames.pScreenRecord_Microphone, true)) {
                    //command.add("--microphone");
                    //if(DeepLogging) Log.d(TAG, "> --microphone");
                }
                String prefVal = mPrefs.getString(PreferenceNames.pScreenRecord_Size, "default");
                if (!prefVal.equals("default")) {
                    command.add("--size");
                    command.add(prefVal);
                    if(DeepLogging) Log.d(TAG, "> --size " + prefVal);
                }
                prefVal = String.valueOf(mPrefs.getLong(PreferenceNames.pScreenRecord_BitRate, 4) * 1000000);
                command.add("--bit-rate");
                command.add(prefVal);
                if(DeepLogging) Log.d(TAG, "> --bit-rate " + prefVal);
                if (!mUseStockBinary) {
                    prefVal = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(mPrefs.getLong(PreferenceNames.pScreenRecord_TimeLimit, (1000 * 3) * 60)));
                    command.add("--time-limit");
                    command.add(prefVal);
                    if(DeepLogging) Log.d(TAG, "> --time-limit " + prefVal);
                }
                if (mPrefs.getBoolean(PreferenceNames.pScreenRecord_Rotate, false)) {
                    command.add("--rotate");
                    if(DeepLogging) Log.d(TAG, "> --rotate");
                }
                command.add(TMP_PATH);
                if(DeepLogging) Log.d(TAG, "> " + TMP_PATH);

                // construct and start the process
                ProcessBuilder pb = new ProcessBuilder();
                pb.command(command);
                pb.redirectErrorStream(true);
                if(DeepLogging) Log.d(TAG, "Starting capture thread.");
                Process proc = pb.start();

                // Get process PID to be used with native kill later
                final int pid = fieldPid.getInt(proc);
                if(DeepLogging) Log.d(TAG, "Screenrecord PID = " + pid);

                BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));

                while (!isInterrupted()) {
                    if (br.ready()) {
                        screenrecordReturnCode += br.readLine();
                        Log.e(TAG, "Error: " + br.readLine());
                    }

                    try {
                        int code = proc.exitValue();

                        // If the recording is still running, we won't reach here,
                        // but will land in the catch block below.
                        Message msg = Message.obtain(mHandler, MSG_TASK_ENDED, code, 0, null);
                        mHandler.sendMessage(msg);

                        // No need to stop the process, so we can exit this method early
                        return;
                    } catch (IllegalThreadStateException ignore) {
                        // ignored
                    }
                }

                // Terminate the recording process
                Runtime.getRuntime().exec(new String[]{"kill", "-2", String.valueOf(pid)});
            } catch (IOException | IllegalAccessException | ClassNotFoundException | IllegalArgumentException | NoSuchFieldException e) {
                // Notify something went wrong
                Message msg = Message.obtain(mHandler, MSG_TASK_ERROR, 0, 0, e.getMessage());
                mHandler.sendMessage(msg);

                // Log the error as well
                Log.e(TAG, "Error while starting the screenrecord process", e);
            }
        }
    }

    ;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();
        nm = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);

        final String prefsName = getPackageName() + "_preferences";
        mPrefs = SettingsManager.getInstance(this).getMainPrefs();
        DeepLogging = mPrefs.getBoolean(PreferenceNames.pDeepXposedLogging,false);

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == MSG_TASK_ENDED) {
                    // The screenrecord process stopped, act as if user
                    // requested the record to stop.
                    stopScreenrecord();
                } else if (msg.what == MSG_TASK_ERROR) {
                    mCaptureThread = null;
                    updateStatus(STATUS_ERROR, (String) msg.obj);
                    Toast.makeText(ScreenRecordingService.this,
                            R.string.screenrecord_toast_error, Toast.LENGTH_SHORT).show();
                }
            }
        };

        mRecordingStatus = STATUS_IDLE;

        Notification.Builder builder = new Notification.Builder(this)
                .setTicker(getString(R.string.screenrecord_notif_ticker))
                .setContentTitle(getString(R.string.screenrecord_notif_title))
                .setSmallIcon(R.drawable.ic_sysbar_camera)
                .setWhen(System.currentTimeMillis());

        Intent stopIntent = new Intent(this, ScreenRecordingService.class);
        stopIntent.setAction(ACTION_SCREEN_RECORDING_STOP);
        PendingIntent stopPendIntent = PendingIntent.getService(this, 0, stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pointerIntent = new Intent(this, ScreenRecordingService.class)
                .setAction(ACTION_TOGGLE_SHOW_TOUCHES);
        PendingIntent pointerPendIntent = PendingIntent.getService(this, 0, pointerIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            builder
                    .addAction(new Notification.Action.Builder(
                            Icon.createWithResource(this, R.drawable.ic_media_stop),
                            getString(R.string.screenrecord_notif_buttons).split("\\|")[0], stopPendIntent).build())
                    .addAction(new Notification.Action.Builder(
                            Icon.createWithResource(this, R.drawable.ic_text_dot),
                            getString(R.string.screenrecord_notif_buttons).split("\\|")[1], pointerPendIntent).build());
        } else {
            builder.addAction(R.drawable.ic_media_stop, getString(R.string.screenrecord_notif_buttons).split("\\|")[0], stopPendIntent)
                    .addAction(R.drawable.ic_text_dot, getString(R.string.screenrecord_notif_buttons).split("\\|")[1], pointerPendIntent);
        }


        mRecordingNotif = builder.build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        baseContext = getBaseContext();
        if(DeepLogging) Log.d(TAG, "Received command: " + intent.getAction());
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals(ACTION_SCREEN_RECORDING_START)) {
                startScreenrecord();
            } else if (intent.getAction().equals(ACTION_SCREEN_RECORDING_STOP)) {
                stopScreenrecord();
            } else if (intent.getAction().equals(ACTION_TOGGLE_SCREEN_RECORDING)) {
                toggleScreenrecord();
            } else if (intent.getAction().equals(ACTION_TOGGLE_SHOW_TOUCHES)) {
                toggleShowTouches();
            } else if (intent.getAction().equals(ACTION_SCREEN_RECORDING_OPEN)) {
                String path = intent.getStringExtra("path");

                Intent player = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
                player.setDataAndType(Uri.parse(path), "video/*");
                if (Build.VERSION.SDK_INT >= 19) {
                    player.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"video/*"});
                }
                player.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Intent chooser = Intent.createChooser(player, getString(R.string.screenrecord_notif_open));
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(chooser);

                stopSelf();

            } else if (intent.getAction().equals(ACTION_SCREEN_RECORDING_DELETE)) {
                String path = intent.getStringExtra("path");
                File file = new File(path);

                if (file.delete()) {
                    // Make it appear in gallery, run MediaScanner
                    MediaScannerConnection.scanFile(ScreenRecordingService.this,
                            new String[]{file.getAbsolutePath()}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                    if(DeepLogging) Log.i(TAG, "MediaScanner done scanning " + path);
                                }
                            });
                } else {
                    Log.e(TAG, "Failed to delete.");
                }

                nm.cancel(SCREENRECORD_NOTIFICATION_ID + 1);

                stopSelf();

            }
        } else {
            if(DeepLogging) Log.d(TAG, "Unknown action code.");
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (isRecording()) {
            stopScreenrecord();
        }
        super.onDestroy();
    }

    private boolean isIdle() {
        return (mRecordingStatus == STATUS_IDLE);
    }

    private boolean isRecording() {
        return (mRecordingStatus == STATUS_RECORDING);
    }

    private boolean isProcessing() {
        return (mRecordingStatus == STATUS_PROCESSING);
    }

    private void updateStatus(int status, String message) {
        mRecordingStatus = status;
        if (isRecording()) {
            startForeground(SCREENRECORD_NOTIFICATION_ID, mRecordingNotif);
        } else {
            stopForeground(true);
            resetShowTouches();
        }

        Intent intent = new Intent(ACTION_SCREEN_RECORDING_STATUS_CHANGED);
        intent.putExtra(EXTRA_RECORDING_STATUS, mRecordingStatus);
        if (message != null) {
            intent.putExtra(EXTRA_STATUS_MESSAGE, message);
        }
        sendBroadcast(intent);
    }

    private void updateStatus(int status) {
        updateStatus(status, null);
    }

    private void toggleShowTouches() {
        sendBroadcast(new Intent(ACTION_TOGGLE_SHOW_TOUCHES));
    }

    private void resetShowTouches() {
        Intent intent = new Intent(ACTION_TOGGLE_SHOW_TOUCHES);
        intent.putExtra(EXTRA_SHOW_TOUCHES, mShowTouchesDefault);
        sendBroadcast(intent);
    }

    private void toggleScreenrecord() {
        if (isRecording()) {
            stopScreenrecord();
        } else {
            startScreenrecord();
        }
    }

    private boolean isScreenrecordSupported() {
        // Exynos devices are currently known to have issues
        if (XposedUtils.isExynosDevice()) {
            Log.e(TAG, "isScreenrecordSupported: screen recording not supported on Exynos devices");
        }
        // check if screenrecord and kill binaries exist and are executable
        File f = new File(getBinaryPath());
        final boolean scrBinaryOk = f.exists() && f.canExecute();
        if (!scrBinaryOk) {
            Log.e(TAG, "isScreenrecordSupported: screenrecord binary doesn't exist or is not executable");
        }
        f = new File("/system/bin/kill");
        final boolean killBinaryOk = f.exists() && f.canExecute();
        if (!killBinaryOk) {
            Log.e(TAG, "isScreenrecordSupported: kill binary doesn't exist or is not executable");
        }
        return (!XposedUtils.isExynosDevice() && scrBinaryOk && killBinaryOk);
    }

    private void startScreenrecord() {
        mUseStockBinary = mPrefs.getBoolean(PreferenceNames.pScreenRecord_UseStockBinary, false);
        if (!isScreenrecordSupported()) {
            Log.e(TAG, "startScreenrecord: System does not support screen recording");
            Toast.makeText(this, getString(R.string.screenrecord_toast_nosupport), Toast.LENGTH_SHORT).show();
            return;
        }
        if (isRecording()) {
            Log.e(TAG, "startScreenrecord: Recording is already running, ignoring screenrecord start request");
            return;
        } else if (isProcessing()) {
            Log.e(TAG, "startScreenrecord: Previous recording is still being processed, " +
                    "ignoring screenrecord start request");
            Toast.makeText(this, R.string.screenrecord_toast_processing, Toast.LENGTH_SHORT).show();
            return;
        }

        if (Build.VERSION.SDK_INT >= 17) {
            try {
                mShowTouchesDefault = Settings.Global.getInt(getContentResolver(),
                        SETTING_SHOW_TOUCHES);
            } catch (SettingNotFoundException e) {
                //
            }
        } else
            try {
                mShowTouchesDefault = Settings.System.getInt(getContentResolver(),
                        SETTING_SHOW_TOUCHES);
            } catch (SettingNotFoundException e) {
                //
            }
        {
        }

        if (mShowTouchesDefault == 0)

        {
            toggleShowTouches();
        }

        mCaptureThread = new CaptureThread();

        if (mPrefs.getLong(PreferenceNames.pScreenRecord_Countdown, 0) > 0) {
            iCountdown = mPrefs.getLong(PreferenceNames.pScreenRecord_Countdown,0);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    final CountDownTimer cdt5  = new CountDownTimer(iCountdown,1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            if (tToast != null) tToast.cancel();
                            tToast = Toast.makeText(mContext, getString(R.string.screenrecord_toast_start_in).replace("[TIME]", "" + helper.getTimeString(mContext, millisUntilFinished, 0)), Toast.LENGTH_SHORT);
                            tToast.show();
                        }

                        @Override
                        public void onFinish() {
                            tToast.cancel();
                            mCaptureThread.start();
                        }
                    }.start();
                }
            });
        } else {
            mCaptureThread.start();
        }


        updateStatus(STATUS_RECORDING);

    }

    private void stopScreenrecord() {
        if (!isRecording()) {
            Log.e(TAG, "Cannot stop recording that's not active");
            return;
        }

        updateStatus(STATUS_PROCESSING);

        try {
            mCaptureThread.interrupt();
        } catch (Exception e) { /* ignore */ }

        // Wait a bit for capture thread to finish
        while (mCaptureThread.isAlive()) {
            // wait...
        }

        Notification.Builder builder = new Notification.Builder(mContext)
                .setContentText(getString(R.string.screenrecord_notif_saving))
                .setContentTitle(getString(R.string.screenrecord_notif_title))
                .setSmallIcon(R.drawable.ic_sysbar_camera)
                .setOngoing(true)
                .setWhen(System.currentTimeMillis());

        nm.notify(SCREENRECORD_NOTIFICATION_ID + 1, builder.build());

        // Give a second to screenrecord to process the file
        mHandler.postDelayed(new Runnable() {
            public void run() {
                mCaptureThread = null;

                String resultMsg = "";
                String fileName = "NPM_SCR_" + new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.US).format(new Date()) + ".mp4";

                File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                if (!picturesDir.exists()) {
                    if (!picturesDir.mkdir()) {
                        resultMsg = getString(R.string.screenrecord_notif_failedCreateDir);
                        Log.e(TAG, "Cannot create Pictures directory");
                        return;
                    }
                }

                File screenrecord = new File(picturesDir, "Screenrecord");
                if (!screenrecord.exists()) {
                    if (!screenrecord.mkdir()) {
                        resultMsg = getString(R.string.screenrecord_notif_failedCreateDir);
                        Log.e(TAG, "Cannot create Screenrecord directory");
                        return;
                    }
                }

                File input = new File(TMP_PATH);
                final File output = new File(screenrecord, fileName);

                if(DeepLogging) Log.d(TAG, "Copying file to " + output.getAbsolutePath());

                try {
                    copyFileUsingStream(input, output);
                    input.delete();
                    resultMsg = String.format(getString(R.string.screenrecord_toast_saved), output.getName());
                    //Toast.makeText(ScreenRecordingService.this,
                    //							 String.format(getString(R.string.screenrecord_toast_saved),
                    //														 output.getPath()), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    resultMsg = getString(R.string.screenrecord_notif_failedToSave);
                    Log.e(TAG, "Unable to copy output file", e);
                    Toast.makeText(ScreenRecordingService.this,
                            R.string.screenrecord_toast_save_error, Toast.LENGTH_SHORT).show();
                }

                // Make it appear in gallery, run MediaScanner
                MediaScannerConnection.scanFile(ScreenRecordingService.this,
                        new String[]{output.getAbsolutePath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i(TAG, "MediaScanner done scanning " + path);
                            }
                        });

                Intent openIntent = new Intent(mContext, ScreenRecordingService.class);
                openIntent.setAction(ACTION_SCREEN_RECORDING_OPEN);
                openIntent.putExtra("path", output.getPath());
                PendingIntent openPendIntent = PendingIntent.getService(mContext, 0, openIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                Notification.Builder builder = new Notification.Builder(mContext)
                        .setContentText(resultMsg)
                        .setTicker(resultMsg)
                        .setContentTitle(getString(R.string.screenrecord_notif_title))
                        .setSmallIcon(R.drawable.ic_sysbar_camera)
                        .setOngoing(false)
                        .setWhen(System.currentTimeMillis());

                if (resultMsg.equals(String.format(getString(R.string.screenrecord_toast_saved), output.getName()))) {
                    builder.setContentIntent(openPendIntent);
                    Intent deleteIntent = new Intent(mContext, ScreenRecordingService.class);
                    deleteIntent.setAction(ACTION_SCREEN_RECORDING_DELETE);
                    deleteIntent.putExtra("path", output.getPath());
                    PendingIntent deletePendIntent = PendingIntent.getService(mContext, 0, deleteIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        builder
                                .addAction(new Notification.Action.Builder(
                                        Icon.createWithResource(mContext, R.drawable.ic_action_trash),
                                        getString(R.string.presetsManager_Buttons).split("\\|")[2], deletePendIntent).build());
                    } else {
                        builder.addAction(R.drawable.ic_action_trash, getString(R.string.presetsManager_Buttons).split("\\|")[2], deletePendIntent);
                    }
                }

                nm.notify(SCREENRECORD_NOTIFICATION_ID + 1, builder.build());

                updateStatus(STATUS_IDLE);
            }
        }, 3000);
    }

    private String getBinaryPath() {
        return (mUseStockBinary ? "/system/bin/screenrecord" : getFilesDir() + "/screenrecord");
    }

    private static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }
}

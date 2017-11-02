package de.NeonSoft.neopowermenu.services;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.hardware.*;
import android.os.*;

import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.SettingsManager;

import android.os.PowerManager.WakeLock;

import android.hardware.Camera;

public class TorchService extends Service {
    private static final String TAG = "NPM:TorchService";

    public static final String ACTION_TOGGLE_TORCH = "neopowermenu.intent.action.TOGGLE_TORCH";
    public static final String ACTION_TORCH_STATUS_CHANGED = "neopowermenu.intent.action.TORCH_STATUS_CHANGED";
    public static final String ACTION_TORCH_GET_STATUS = "neopowermenu.intent.action.TORCH_GET_STATUS";
    public static final String EXTRA_TORCH_STATUS = "torchStatus";
    public static final String EXTRA_GO_TO_SLEEP = "goToSleep";
    public static final int TORCH_STATUS_OFF = 0;
    public static final int TORCH_STATUS_ON = 1;
    public static final int TORCH_STATUS_ERROR = -1;

    private Camera mCamera;
    private static int mTorchStatus = TORCH_STATUS_OFF;
    private Notification mTorchNotif;
    private PendingIntent mPendingIntent;
    private WakeLock mPartialWakeLock;
    private int mTorchTimeout;
    private boolean mTorchAutoOff = true;
    private Handler mHandler;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mTorchStatus = TORCH_STATUS_OFF;

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(getString(R.string.quick_settings_torch_on));
        builder.setContentText(getString(R.string.quick_settings_torch_off_notif));
        builder.setSmallIcon(R.drawable.ic_qs_torch_on);
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.ic_qs_torch_on);
        builder.setLargeIcon(b);
        Intent intent = new Intent(ACTION_TOGGLE_TORCH);
        mPendingIntent = PendingIntent.getService(this, 0, intent, 0);
        builder.setContentIntent(mPendingIntent);
        mTorchNotif = builder.build();

        SharedPreferences prefs = SettingsManager.getInstance(this).getMainPrefs();
        mTorchTimeout = (int) prefs.getLong("FlashlightAutoOffTime", 10 * 60 * 1000);
        mTorchAutoOff = prefs.getBoolean("FlashlightAutoOff", true);
        mHandler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            if (ACTION_TOGGLE_TORCH.equals(intent.getAction())) {
                toggleTorch(intent.getBooleanExtra(EXTRA_GO_TO_SLEEP, false));
                return START_REDELIVER_INTENT;
            }
            if (ACTION_TORCH_GET_STATUS.equals(intent.getAction())) {
                ResultReceiver receiver = intent.getParcelableExtra("receiver");
                Bundle data = new Bundle();
                data.putInt(EXTRA_TORCH_STATUS, mTorchStatus);
                receiver.send(0, data);
                return START_STICKY;
            }
        }

        stopSelf();
        return START_NOT_STICKY;
    }

    public static int getTorchState() {
        return mTorchStatus;
    }

    private synchronized void toggleTorch(boolean goToSleep) {
        if (mTorchStatus != TORCH_STATUS_ON) {
            setTorchOn();
        } else {
            setTorchOff();
        }
    }

    private synchronized void setTorchOn() {
        try {
            if (mCamera == null) {
                mCamera = Camera.open();
            }
            Camera.Parameters camParams = mCamera.getParameters();
            camParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(camParams);
            mCamera.setPreviewTexture(new SurfaceTexture(0));
            mCamera.startPreview();
            mTorchStatus = TORCH_STATUS_ON;
            startForeground(2, mTorchNotif);

            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mPartialWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
            mPartialWakeLock.acquire(mTorchTimeout > 0 ? mTorchTimeout : 3600000);
            mHandler.removeCallbacks(mTorchTimeoutRunnable);
            if (mTorchAutoOff && mTorchTimeout > 0) {
                mHandler.postDelayed(mTorchTimeoutRunnable, mTorchTimeout);
            }
        } catch (Exception e) {
            mTorchStatus = TORCH_STATUS_ERROR;
            e.printStackTrace();
        } finally {
            Intent i = new Intent(ACTION_TORCH_STATUS_CHANGED);
            i.putExtra(EXTRA_TORCH_STATUS, mTorchStatus);
            sendBroadcast(i);
            if (mTorchStatus == TORCH_STATUS_ERROR) {
                stopSelf();
            }
        }
    }

    private synchronized void setTorchOff() {
        try {
            mHandler.removeCallbacks(mTorchTimeoutRunnable);
            if (mPartialWakeLock != null && mPartialWakeLock.isHeld()) {
                mPartialWakeLock.release();
                mPartialWakeLock = null;
            }
            if (mCamera != null) {
                Camera.Parameters camParams = mCamera.getParameters();
                camParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(camParams);
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
            mTorchStatus = TORCH_STATUS_OFF;
        } catch (Exception e) {
            mTorchStatus = TORCH_STATUS_ERROR;
            e.printStackTrace();
        } finally {
            Intent i = new Intent(ACTION_TORCH_STATUS_CHANGED);
            i.putExtra(EXTRA_TORCH_STATUS, mTorchStatus);
            sendBroadcast(i);
            stopForeground(true);
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        setTorchOff();
        super.onDestroy();
    }

    private Runnable mTorchTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            setTorchOff();
        }
    };
}

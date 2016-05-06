package de.NeonSoft.neopowermenu.xposed;

import android.app.*;
import android.bluetooth.*;
import android.content.*;
import android.nfc.*;
import android.os.*;
import android.os.storage.*;
import android.util.*;
import android.widget.*;
import com.android.internal.telephony.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.xposed.service.*;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage.*;

/**
 * Created by naman on 20/03/15.
 */
public class XposedMain implements IXposedHookLoadPackage, IXposedHookZygoteInit
{

    private static final String TAG = TAG;
		private XSharedPreferences preferences;
		private boolean DeepXposedLogging = false;
		private boolean HookShutdownThread = false;
		
    public static final String PACKAGE_NAME = MainActivity.class.getPackage().getName();

    public static final String CLASS_GLOBAL_ACTIONS = "com.android.internal.policy.impl.GlobalActions";
		public static final String CLASS_GLOBAL_ACTIONS_MARSHMALLOW = "com.android.server.policy.GlobalActions";
    private static final String CLASS_PHONE_WINDOW_MANAGER = "com.android.internal.policy.impl.PhoneWindowManager";
    private static final String CLASS_PHONE_WINDOW_MANAGER_MARSHMALLOW = "com.android.server.policy.PhoneWindowManager";
    private static final String CLASS_SHUTDOWNTHREAD = "com.android.server.power.ShutdownThread";
    private static final String CLASS_SHUTDOWNTHREAD_MARSHMALLOW = "com.android.server.power.ShutdownThread";

		public static final String NPM_ACTION_BROADCAST_SHUTDOWN = "de.NeonSoft.neopowermenu.action.Shutdown";
		public static final String NPM_ACTION_BROADCAST_REBOOT = "de.NeonSoft.neopowermenu.action.Reboot";
		public static final String NPM_ACTION_BROADCAST_REBOOTRECOVERY = "de.NeonSoft.neopowermenu.action.RebootRecovery";
		public static final String NPM_ACTION_BROADCAST_REBOOTBOOTLOADER = "de.NeonSoft.neopowermenu.action.RebootBootloader";
		public static final String NPM_ACTION_BROADCAST_SCREENSHOT = "de.NeonSoft.neopowermenu.action.takeScreenshot";
		public static final String NPM_ACTION_BROADCAST_SCREENRECORD = "de.NeonSoft.neopowermenu.action.takeScreenrecord";

    private static final int MESSAGE_DISMISS = 0;
		
		Handler xHandler;
    private static final Object mScreenshotLock = new Object();
    private static ServiceConnection mScreenshotConnection = null;  

    static Context mContext;
		public Object mObjectHolder;

    private static Object sIsStartedGuard;
    private static boolean sIsStarted;
    // Provides shutdown assurance in case the system_server is killed
    public static final String SHUTDOWN_ACTION_PROPERTY = "sys.shutdown.requested";
    // Indicates whether we are rebooting into safe mode
    public static final String REBOOT_SAFEMODE_PROPERTY = "persist.sys.safemode";
		
    private static ShutdownThread sInstance = new ShutdownThread();
		
		/*<!-- Internal Hook version to check if reboot is needed --!>*/
		private static final int XposedHookVersion = 17;

		
		Object mPhoneWindowManager;

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable
		{
				XposedUtils.log("Zygote init...");
				XposedUtils.log("~~{ Module Infos }~~");
        preferences = new XSharedPreferences(PACKAGE_NAME);
        //preferences.makeWorldReadable();
				DeepXposedLogging = true;//preferences.getBoolean("DeepXposedLogging",false);
				XposedUtils.log("Module Path: " + startupParam.modulePath);
				XposedUtils.log("Hook version: " + XposedHookVersion);
				XposedUtils.log("Deep Logging: " + DeepXposedLogging);
				XposedUtils.log("~~{ Device Infos }~~");
        XposedUtils.log("Hardware: " + Build.HARDWARE);
        XposedUtils.log("Product: " + Build.PRODUCT);
				XposedUtils.log("Manufacturer: " + Build.MANUFACTURER);
				XposedUtils.log("Model: " + Build.MODEL);
				XposedUtils.log("Android Version: " + Build.VERSION.RELEASE);
				XposedUtils.log("SDK Version: " + Build.VERSION.SDK_INT);
        XposedUtils.log("ROM: " + Build.DISPLAY);
				XposedUtils.log("Zygote init complete.");

    }


    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable
		{
				if (lpparam.packageName.equals("android"))
				{
						if (DeepXposedLogging) XposedUtils.log("Loading Power Menu...");

						String usedGADClass;
						String usedPWMClass;
						String usedSDClass;
						if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
						{
								usedGADClass = CLASS_GLOBAL_ACTIONS_MARSHMALLOW;
								usedPWMClass = CLASS_PHONE_WINDOW_MANAGER_MARSHMALLOW;
								usedSDClass = CLASS_SHUTDOWNTHREAD_MARSHMALLOW;
						}
						else
						{
								usedGADClass = CLASS_GLOBAL_ACTIONS;
								usedPWMClass = CLASS_PHONE_WINDOW_MANAGER;
								usedSDClass = CLASS_SHUTDOWNTHREAD;
						}

						if (DeepXposedLogging) XposedUtils.log("Detected " + android.os.Build.VERSION.RELEASE + ", injecting to: ");
						if (DeepXposedLogging) XposedUtils.log(usedGADClass);
						if (DeepXposedLogging) XposedUtils.log(usedPWMClass);
						if (DeepXposedLogging) XposedUtils.log(usedSDClass);
						final Class<?> phoneWindowManagerClass = XposedHelpers.findClass(usedPWMClass, lpparam.classLoader);
						final Class<?> globalActionsClass = XposedHelpers.findClass(usedGADClass, lpparam.classLoader);
						final Class<?> ShutdownThreadClass = XposedHelpers.findClass(usedSDClass, lpparam.classLoader);

						if (DeepXposedLogging) XposedUtils.log("Hooking (replace) "+usedGADClass+" Constructor...");
						XposedBridge.hookAllConstructors(globalActionsClass, new XC_MethodReplacement() {
										@Override
										protected Object replaceHookedMethod(final MethodHookParam param) throws Throwable
										{
												mObjectHolder = param.thisObject;
												mContext = (Context) param.args[0];
												final Handler mHandler = new Handler(); 
												xHandler = mHandler; 
												BroadcastReceiver mNPMReceiver = new BroadcastReceiver() {

														@Override
														public void onReceive(Context p1, Intent p2)
														{
																// TODO: Implement this method
																//Toast.makeText(p1, "Received NPM Broadcast: " + p2.getAction(), Toast.LENGTH_LONG).show();
																if (p2.getAction().equalsIgnoreCase(NPM_ACTION_BROADCAST_SCREENSHOT))
																{
																		final Handler handler = xHandler;
																		if (handler == null) return;

																		synchronized (mScreenshotLock)
																		{  
																				if (mScreenshotConnection != null)
																				{  
																						return;  
																				}  
																				ComponentName cn = new ComponentName("com.android.systemui",  
																																						 "com.android.systemui.screenshot.TakeScreenshotService");  
																				Intent intent = new Intent();  
																				intent.setComponent(cn);  
																				ServiceConnection conn = new ServiceConnection() {  
																						@Override  
																						public void onServiceConnected(ComponentName name, IBinder service)
																						{  
																								synchronized (mScreenshotLock)
																								{  
																										if (mScreenshotConnection != this)
																										{  
																												return;  
																										}  
																										final Messenger messenger = new Messenger(service);  
																										final Message msg = Message.obtain(null, 1);  
																										final ServiceConnection myConn = this;  

																										Handler h = new Handler(handler.getLooper()) {  
																												@Override  
																												public void handleMessage(Message msg)
																												{  
																														synchronized (mScreenshotLock)
																														{  
																																if (mScreenshotConnection == myConn)
																																{  
																																		mContext.unbindService(mScreenshotConnection);  
																																		mScreenshotConnection = null;  
																																		handler.removeCallbacks(mScreenshotTimeout);  
																																}  
																														}  
																												}  
																										};  
																										msg.replyTo = new Messenger(h);  
																										msg.arg1 = msg.arg2 = 0;  
																										h.post(new Runnable() {
																														@Override
																														public void run()
																														{
																																try
																																{
																																		messenger.send(msg);
																																}
																																catch (RemoteException e)
																																{
																																		XposedBridge.log(e);
																																}
																														}
																												});
																								}  
																						}  
																						@Override  
																						public void onServiceDisconnected(ComponentName name)
																						{}  
																				};  
																				if (mContext.bindService(intent, conn, Context.BIND_AUTO_CREATE))
																				{  
																						mScreenshotConnection = conn;  
																						handler.postDelayed(mScreenshotTimeout, 10000);  
																				}  
																		} 
																}
																else if (p2.getAction().equalsIgnoreCase(NPM_ACTION_BROADCAST_SCREENRECORD))
																{
																		try
																		{
																				Context context = mContext.createPackageContext(PACKAGE_NAME , Context.CONTEXT_IGNORE_SECURITY);
																				Intent intent = new Intent(context, ScreenRecordingService.class);
																				intent.setAction(ScreenRecordingService.ACTION_TOGGLE_SCREEN_RECORDING);
																				context.startService(intent);
																		}
																		catch (Throwable t)
																		{
																				if (DeepXposedLogging) XposedUtils.log("Start Screenrecord service failed:" + t);
																		}
																}
																else if (p2.getAction().equalsIgnoreCase(NPM_ACTION_BROADCAST_SHUTDOWN))
																{
																		IPowerManager pm = IPowerManager.Stub.asInterface(ServiceManager.getService(Context.POWER_SERVICE));
																		try
																		{
																				pm.shutdown(false, false);
																		}
																		catch (RemoteException e)
																		{}
																}
																else if (p2.getAction().equalsIgnoreCase(NPM_ACTION_BROADCAST_REBOOT))
																{
																		IPowerManager pm = IPowerManager.Stub.asInterface(ServiceManager.getService(Context.POWER_SERVICE));
																		try
																		{
																				pm.reboot(false, null, false);
																		}
																		catch (RemoteException e)
																		{}
																}
																else if (p2.getAction().equalsIgnoreCase(NPM_ACTION_BROADCAST_REBOOTRECOVERY))
																{IPowerManager pm = IPowerManager.Stub.asInterface(ServiceManager.getService(Context.POWER_SERVICE));
																		try
																		{
																				pm.reboot(false, "recovery", false);
																		}
																		catch (RemoteException e)
																		{}
																}
																else if (p2.getAction().equalsIgnoreCase(NPM_ACTION_BROADCAST_REBOOTBOOTLOADER))
																{IPowerManager pm = IPowerManager.Stub.asInterface(ServiceManager.getService(Context.POWER_SERVICE));
																		try
																		{
																				pm.reboot(false, "bootloader", false);
																		}
																		catch (RemoteException e)
																		{}
																}
														}
												};
												IntentFilter filter = new IntentFilter();
												filter.addAction(NPM_ACTION_BROADCAST_SHUTDOWN);
												filter.addAction(NPM_ACTION_BROADCAST_REBOOT);
												filter.addAction(NPM_ACTION_BROADCAST_REBOOTRECOVERY);
												filter.addAction(NPM_ACTION_BROADCAST_REBOOTBOOTLOADER);
												filter.addAction(NPM_ACTION_BROADCAST_SCREENSHOT);
												filter.addAction(NPM_ACTION_BROADCAST_SCREENRECORD);
												filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
												mContext.registerReceiver(mNPMReceiver, filter, null, null);
												return null;
										}
								});
						if (DeepXposedLogging) XposedUtils.log("Registering Broadcast Receiver and setting other values...");
						if (DeepXposedLogging) XposedUtils.log("Hooking (replace) " + usedGADClass + "#showDialog...");
						XposedHelpers.findAndHookMethod(usedGADClass, lpparam.classLoader, "showDialog", boolean.class, boolean.class, new XC_MethodReplacement() {
										@Override
										protected Object replaceHookedMethod(final MethodHookParam methodHookParam) throws Throwable
										{
												showDialog();
												return null;
										}

								});
						if (DeepXposedLogging) XposedUtils.log("Replaced with showDialog(), just executing startActivity() to start my own dialog.");
						if (DeepXposedLogging) XposedUtils.log("Hooking (replace) " + usedGADClass + "#createDialog...");
						XposedHelpers.findAndHookMethod(usedGADClass, lpparam.classLoader, "createDialog", new XC_MethodReplacement() {
										@Override
										protected Object replaceHookedMethod(final MethodHookParam methodHookParam) throws Throwable
										{

												return null;
										}

								});
						if (DeepXposedLogging) XposedUtils.log("Replaced with empty method to prevent crashes, hopefully working...");
						if (DeepXposedLogging) XposedUtils.log("Hooking (replace) " + usedGADClass + "#onAirplaneModeChanged...");
						XposedHelpers.findAndHookMethod(usedGADClass, lpparam.classLoader, "onAirplaneModeChanged", new XC_MethodReplacement() {

										@Override
										protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable
										{
												// TODO: Implement this method
												return null;
										}
								});
						if (DeepXposedLogging) XposedUtils.log("Replaced with empty method to prevent crashes, hopefully working...");
						if (HookShutdownThread) {
						if (DeepXposedLogging) XposedUtils.log("Hooking (after) "+usedSDClass+" constructor...");
						XposedBridge.hookAllConstructors(ShutdownThreadClass, new XC_MethodHook() {
								@Override
								public void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
										sIsStartedGuard = XposedHelpers.getStaticObjectField(ShutdownThreadClass,"sIsStartedGuard");
										if(sIsStartedGuard==null) XposedUtils.log("sIsStartedGuard is null,reboot will crash...");
										sIsStarted = XposedHelpers.getStaticObjectField(ShutdownThreadClass,"sIsStarted");
										if(sIsStarted==true) XposedUtils.log("sIsStarted is true,thats not normal for fresh created...");
										sInstance = (ShutdownThread) XposedHelpers.getStaticObjectField(ShutdownThreadClass,"sInstance");
										if(sInstance==null) XposedUtils.log("sInstance is null,reboot will crash...");
								} 
						});
						if (DeepXposedLogging) XposedUtils.log("Getting needed values...");
						if (DeepXposedLogging) XposedUtils.log("Hooking (replace) "+usedSDClass+"#beginShutdownSequence...");
						XposedHelpers.findAndHookMethod(usedSDClass, lpparam.classLoader, "beginShutdownSequence", Context.class ,new XC_MethodReplacement() {
								@Override
								protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable
								{
										try{
										synchronized (sIsStartedGuard) {
												if (sIsStarted) {
														Log.d(TAG, "Shutdown sequence already running, returning.");
														return null;
												}
												sIsStarted = true;
										}
										// throw up an indeterminate system dialog to indicate radio is
										// shutting down.
										/*ProgressDialog pd = new ProgressDialog(context);
										pd.setTitle(context.getText(com.android.internal.R.string.power_off));
										pd.setMessage(context.getText(com.android.internal.R.string.shutdown_progress));
										pd.setIndeterminate(true);
										pd.setCancelable(false);
										pd.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
										pd.show();*/
										Context context = (Context) param.args[0];
										sInstance.mContext = context;
										sInstance.mPowerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
										// make sure we never fall asleep again
										sInstance.mCpuWakeLock = null;
										try {
												sInstance.mCpuWakeLock = sInstance.mPowerManager.newWakeLock(
														PowerManager.PARTIAL_WAKE_LOCK, TAG + "-cpu");
												sInstance.mCpuWakeLock.setReferenceCounted(false);
												sInstance.mCpuWakeLock.acquire();
										} catch (SecurityException e) {
												Log.w(TAG, "No permission to acquire wake lock", e);
												sInstance.mCpuWakeLock = null;
										}
										// also make sure the screen stays on for better user experience
										sInstance.mScreenWakeLock = null;
										if (sInstance.mPowerManager.isScreenOn()) {
												try {
														sInstance.mScreenWakeLock = sInstance.mPowerManager.newWakeLock(
																PowerManager.FULL_WAKE_LOCK, TAG + "-screen");
														sInstance.mScreenWakeLock.setReferenceCounted(false);
														sInstance.mScreenWakeLock.acquire();
												} catch (SecurityException e) {
														Log.w(TAG, "No permission to acquire wake lock", e);
														sInstance.mScreenWakeLock = null;
												}
										}
										// start the thread that initiates shutdown
										sInstance.mHandler = new Handler() {
										};
										sInstance.start();
										} catch (Throwable t) {
												XposedUtils.log("ShutdownThread Failed: "+t.toString());
										}
										return null;
								}
						});
						if (DeepXposedLogging) XposedUtils.log("Rebuild the function to stop displaying the default shutdown loading dialog...");
						}
				}
				else if (lpparam.packageName.equals("de.NeonSoft.neopowermenu"))
				{
						if (DeepXposedLogging) XposedUtils.log("Creating self inject...");
						XposedHelpers.findAndHookMethod("de.NeonSoft.neopowermenu.helpers.helper", lpparam.classLoader, "ModuleState", new XC_MethodReplacement() {
										@Override
										protected Object replaceHookedMethod(final MethodHookParam param) throws Throwable
										{
												int active = XposedHookVersion;
												return active;
										}
								});
						XposedHelpers.findAndHookMethod("de.NeonSoft.neopowermenu.xposed.XposedUtils", lpparam.classLoader, "performSoftReboot", new XC_MethodReplacement() {

										@Override
										protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable
										{
												try
												{
														//Class<?> classSm = XposedHelpers.findClass("android.os.ServiceManager", null);
														//Class<?> classIpm = XposedHelpers.findClass("android.os.IPowerManager.Stub", null);
														//IBinder b = (IBinder) XposedHelpers.callStaticMethod(
														//		classSm, "getService", Context.POWER_SERVICE);
														IPowerManager ipm = IPowerManager.Stub.asInterface( ServiceManager.getService(Context.POWER_SERVICE));
														ipm.crash("Hot reboot");
														//Object ipm = XposedHelpers.callStaticMethod(classIpm, "asInterface", b);
														//XposedHelpers.callMethod(ipm, "crash", "Hot reboot");
												}
												catch (Throwable t)
												{
														try
														{
																XposedUtils.SystemProp.set("ctl.restart", "surfaceflinger");
																XposedUtils.SystemProp.set("ctl.restart", "zygote");
														}
														catch (Throwable t2)
														{
																XposedBridge.log(t);
																XposedBridge.log(t2);
														}
												}
												return null;
										}
								});
						if (DeepXposedLogging) XposedUtils.log("Self inject done!");
				}
				return;
    }

    private void showDialog()
		{
        if (mContext == null)
				{

            return;
        }

        try
				{
						//boolean mKeyguardShowing = XposedHelpers.getObjectField(mObjectHolder, "mKeyguardShowing");
            Context context = mContext.createPackageContext(PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);
            Intent intent = new Intent(context, XposedMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
						//intent.putExtra("mKeyguardShowing", mKeyguardShowing);
            context.startActivity(intent);
        }
				catch (Exception e)
				{
            if (DeepXposedLogging) XposedUtils.log("Failed to show Power Menu (" + PACKAGE_NAME + "): " + e);
        }
    }

    private static final Runnable mScreenshotTimeout = new Runnable() {
        @Override
        public void run()
				{
            synchronized (mScreenshotLock)
						{
                if (mScreenshotConnection != null)
								{
                    mContext.unbindService(mScreenshotConnection);
                    mScreenshotConnection = null;
                }
            }
        }
    };
		
		public static class ShutdownThread extends Thread {
				private final Object mActionDoneSync = new Object();
				private boolean mActionDone;
				private Context mContext;
				private PowerManager mPowerManager;
				private PowerManager.WakeLock mCpuWakeLock;
				private PowerManager.WakeLock mScreenWakeLock;
				private Handler mHandler;
				
				private ShutdownThread() {
						
				}
				

				void actionDone() {
						synchronized (mActionDoneSync) {
								mActionDone = true;
								mActionDoneSync.notifyAll();
						}
				}
				/**
				 * Makes sure we handle the shutdown gracefully.
				 * Shuts off power regardless of radio and bluetooth state if the alloted time has passed.
				 */
				public void run() {
						BroadcastReceiver br = new BroadcastReceiver() {
								@Override public void onReceive(Context context, Intent intent) {
										// We don't allow apps to cancel this, so ignore the result.
										actionDone();
								}
						};
						Toast.makeText(sInstance.mContext,"ShutdownThread execute...",Toast.LENGTH_SHORT).show();
						// First send the high-level shut down broadcast.
						mActionDone = false;
						mContext.sendOrderedBroadcastAsUser(new Intent(Intent.ACTION_SHUTDOWN),
																								UserHandle.ALL, null, br, mHandler, 0, null, null);
				}
		}
}


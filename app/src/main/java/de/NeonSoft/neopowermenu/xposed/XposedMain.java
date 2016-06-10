package de.NeonSoft.neopowermenu.xposed;

import android.app.*;
import android.content.*;
import android.os.*;
import android.provider.*;
import android.util.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.services.*;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage.*;
import java.util.*;

/**
 * Created by naman on 20/03/15.
 */
public class XposedMain implements IXposedHookLoadPackage, IXposedHookZygoteInit
{

    private static final String TAG = "NPM";
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

    private static final String CLASS_PACKAGE_MANAGER_SERVICE = "com.android.server.pm.PackageManagerService";
    private static final String CLASS_PACKAGE_MANAGER_SERVICE_MARSHMALLOW = "com.android.server.pm.PackageManagerService";
    private static final String CLASS_PACKAGE_PARSER_PACKAGE = "android.content.pm.PackageParser.Package";
    private static final String PERM_ACCESS_SURFACE_FLINGER = "android.permission.ACCESS_SURFACE_FLINGER";

		private static final String CLASS_SYSTEMUI = "com.android.systemui.SystemUIApplication";

		public static final String NPM_ACTION_BROADCAST_SHUTDOWN = "de.NeonSoft.neopowermenu.action.Shutdown";
		public static final String NPM_ACTION_BROADCAST_REBOOT = "de.NeonSoft.neopowermenu.action.Reboot";
		public static final String NPM_ACTION_BROADCAST_REBOOTRECOVERY = "de.NeonSoft.neopowermenu.action.RebootRecovery";
		public static final String NPM_ACTION_BROADCAST_REBOOTBOOTLOADER = "de.NeonSoft.neopowermenu.action.RebootBootloader";
		public static final String NPM_ACTION_BROADCAST_SCREENSHOT = "de.NeonSoft.neopowermenu.action.takeScreenshot";
		public static final String NPM_ACTION_BROADCAST_SCREENRECORD = "de.NeonSoft.neopowermenu.action.takeScreenrecord";
		public static final String NPM_ACTION_BROADCAST_KILLSYSTEMUI = "de.NeonSoft.neopowermenu.action.killSystemUI";
		public static final String NPM_ACTION_BROADCAST_TOGGLEAIRPLANEMODE = "de.NeonSoft.neopowermenu.action.toggleAirplaneMode";

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
		Class<?> ShutdownThreadClass = null;


		/*<!-- Internal Hook version to check if reboot is needed --!>*/
		private static final int XposedHookVersion = 20;
		
		Object mPhoneWindowManager;

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable
		{
        preferences = new XSharedPreferences(PACKAGE_NAME);
        //preferences.makeWorldReadable();
				DeepXposedLogging = preferences.getBoolean("DeepXposedLogging", false);
				XposedUtils.log("Zygote init...");
				XposedUtils.log("~~{ Module Infos }~~");
				XposedUtils.log("Module Path: " + startupParam.modulePath);
				XposedUtils.log("Hook version: " + XposedHookVersion);
				XposedUtils.log("Deep Logging: " + (DeepXposedLogging ? "active, logging everything." : "not active, logging only fatal errors."));
				if(DeepXposedLogging) {
						XposedUtils.log("~~{ Device Infos }~~");
        XposedUtils.log("Hardware: " + Build.HARDWARE);
        XposedUtils.log("Product: " + Build.PRODUCT);
				XposedUtils.log("Manufacturer: " + Build.MANUFACTURER);
				XposedUtils.log("Model: " + Build.MODEL);
				XposedUtils.log("Android Version: " + Build.VERSION.RELEASE);
				XposedUtils.log("SDK Version: " + Build.VERSION.SDK_INT);
        XposedUtils.log("ROM: " + Build.DISPLAY);
				}
				XposedUtils.log("Zygote init complete.");
				//preferences.edit().remove("activeParts");
    }


    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable
		{
				if (lpparam.packageName.equalsIgnoreCase("com.android.systemui"))
				{
						if (DeepXposedLogging) XposedUtils.log("Hooking (after) " + CLASS_SYSTEMUI + "#onCreate...");
						XposedHelpers.findAndHookMethod(CLASS_SYSTEMUI, lpparam.classLoader, "onCreate", new XC_MethodHook() {
										@Override
										public void afterHookedMethod(MethodHookParam param) throws Throwable
										{

												Application mNPMApp = (Application) param.thisObject;
												final Handler mNPMHandler = new Handler(mNPMApp.getMainLooper());
												BroadcastReceiver mNPMReceiver = new BroadcastReceiver() {

														@Override
														public void onReceive(Context p1, Intent p2)
														{
																//Toast.makeText(p1,"Received NPM Broadcast: "+p2.getAction(),Toast.LENGTH_LONG).show();
																if (DeepXposedLogging) XposedUtils.log("Received broadcast: "+p2.getAction());
																switch (p2.getAction())
																{
																		case NPM_ACTION_BROADCAST_KILLSYSTEMUI:
																				mNPMHandler.postDelayed(new Runnable() {
																								@Override
																								public void run()
																								{
																										android.os.Process.sendSignal(android.os.Process.myPid(), android.os.Process.SIGNAL_KILL);
																								}
																						}, 100);
																				break;
																}
														}
												};
												IntentFilter filter = new IntentFilter();
												filter.addAction(NPM_ACTION_BROADCAST_KILLSYSTEMUI);
												filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
												mNPMApp.registerReceiver(mNPMReceiver, filter);
										}
								});
						if (DeepXposedLogging) XposedUtils.log("Registered receiver for UI events.");
				}
				else
				if (lpparam.packageName.equals("android") &&
						lpparam.processName.equals("android"))
				{
						//if (DeepXposedLogging) 
								XposedUtils.log("Loading Power Menu...");

						String usedGADClass;
						String usedPWMClass;
						String usedSDClass;
						String usedPMClass;
						if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
						{
								usedGADClass = CLASS_GLOBAL_ACTIONS_MARSHMALLOW;
								usedPWMClass = CLASS_PHONE_WINDOW_MANAGER_MARSHMALLOW;
								usedSDClass = CLASS_SHUTDOWNTHREAD_MARSHMALLOW;
								usedPMClass = CLASS_PACKAGE_MANAGER_SERVICE_MARSHMALLOW;
						}
						else
						{
								usedGADClass = CLASS_GLOBAL_ACTIONS;
								usedPWMClass = CLASS_PHONE_WINDOW_MANAGER;
								usedSDClass = CLASS_SHUTDOWNTHREAD;
								usedPMClass = CLASS_PACKAGE_MANAGER_SERVICE;
						}

						if (DeepXposedLogging) XposedUtils.log("Detected " + android.os.Build.VERSION.RELEASE + ", injecting to: ");
						if (DeepXposedLogging) XposedUtils.log(usedGADClass);
						if (DeepXposedLogging) XposedUtils.log(usedPWMClass);
						if (DeepXposedLogging && HookShutdownThread) XposedUtils.log(usedSDClass);
						if (DeepXposedLogging) XposedUtils.log(usedPMClass);
						//final Class<?> phoneWindowManagerClass = XposedHelpers.findClass(usedPWMClass, lpparam.classLoader);
						final Class<?> globalActionsClass = XposedHelpers.findClass(usedGADClass, lpparam.classLoader);
						if (HookShutdownThread)
						{
								ShutdownThreadClass = XposedHelpers.findClass(usedSDClass, lpparam.classLoader);
						}

            final Class<?> pmServiceClass = XposedHelpers.findClass(usedPMClass, lpparam.classLoader);

						if (DeepXposedLogging) XposedUtils.log("Getting permissions...");
            XposedHelpers.findAndHookMethod(pmServiceClass, "grantPermissionsLPw",
								CLASS_PACKAGE_PARSER_PACKAGE, boolean.class, String.class, new XC_MethodHook() {
										@SuppressWarnings("unchecked")
										@Override
										protected void afterHookedMethod(MethodHookParam param) throws Throwable
										{
												final String pkgName = (String) XposedHelpers.getObjectField(param.args[0], "packageName");

												// NeoPowerMenu
												if (PACKAGE_NAME.equals(pkgName))
												{
														if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
														{
																final Object extras = XposedHelpers.getObjectField(param.args[0], "mExtras");
																final Object ps = XposedHelpers.callMethod(extras, "getPermissionsState");
																final List<String> grantedPerms =
																		(List<String>) XposedHelpers.getObjectField(param.args[0], "requestedPermissions");
																final Object settings = XposedHelpers.getObjectField(param.thisObject, "mSettings");
																final Object permissions = XposedHelpers.getObjectField(settings, "mPermissions");

																// Add android.permission.ACCESS_SURFACE_FLINGER needed by screen recorder
																if (!(boolean)XposedHelpers.callMethod(ps, "hasInstallPermission", PERM_ACCESS_SURFACE_FLINGER))
																{
																		final Object pAccessSurfaceFlinger = XposedHelpers.callMethod(permissions, "get",
																																																	PERM_ACCESS_SURFACE_FLINGER);
																		int ret = (int) XposedHelpers.callMethod(ps, "grantInstallPermission", pAccessSurfaceFlinger);
																		if (DeepXposedLogging) XposedUtils.log("Permission added: " + pAccessSurfaceFlinger + "; ret=" + ret);
																}

														}
														else
														{
																final Object extras = XposedHelpers.getObjectField(param.args[0], "mExtras");
																final Set<String> grantedPerms =
																		(Set<String>) XposedHelpers.getObjectField(extras, "grantedPermissions");
																final Object settings = XposedHelpers.getObjectField(param.thisObject, "mSettings");
																final Object permissions = XposedHelpers.getObjectField(settings, "mPermissions");

																// Add android.permission.ACCESS_SURFACE_FLINGER needed by screen recorder
																if (!grantedPerms.contains(PERM_ACCESS_SURFACE_FLINGER))
																{
																		final Object pAccessSurfaceFlinger = XposedHelpers.callMethod(permissions, "get",
																																																	PERM_ACCESS_SURFACE_FLINGER);
																		grantedPerms.add(PERM_ACCESS_SURFACE_FLINGER);
																		int[] gpGids = (int[]) XposedHelpers.getObjectField(extras, "gids");
																		int[] bpGids = (int[]) XposedHelpers.getObjectField(pAccessSurfaceFlinger, "gids");
																		gpGids = (int[]) XposedHelpers.callStaticMethod(param.thisObject.getClass(), 
																																										"appendInts", gpGids, bpGids);

																		if (DeepXposedLogging) XposedUtils.log("Permission added: " + pAccessSurfaceFlinger);
																}
														}
												}
												//preferences.edit().putString("activeParts", preferences.getString("activeParts","") + "permissionGranter,").commit();
										}
								});
						if (DeepXposedLogging) XposedUtils.log("Permission request hooked.");
						if (DeepXposedLogging) XposedUtils.log("Hooking (replace) " + usedGADClass + " Constructor...");
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
																if (DeepXposedLogging) XposedUtils.log("Received broadcast: "+p2.getAction());
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
																else if (p2.getAction().equalsIgnoreCase(NPM_ACTION_BROADCAST_TOGGLEAIRPLANEMODE))
																{
																		// read the airplane mode setting
																		boolean isEnabled = Settings.Global.getInt(
																				mContext.getContentResolver(), 
																				Settings.Global.AIRPLANE_MODE_ON, 0) == 1;

// toggle airplane mode
																		Settings.Global.putInt(
																				mContext.getContentResolver(),
																				Settings.Global.AIRPLANE_MODE_ON, isEnabled ? 0 : 1);

// Post an intent to reload
																		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
																		intent.putExtra("state", !isEnabled);
																		mContext.sendBroadcast(intent);
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
												filter.addAction(NPM_ACTION_BROADCAST_TOGGLEAIRPLANEMODE);
												filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
												mContext.registerReceiver(mNPMReceiver, filter, null, null);
												//preferences.edit().putString("activeParts", preferences.getString("activeParts","") +  "GlobalActionsDialog#constructor,").commit();
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
												//preferences.edit().putString("activeParts", preferences.getString("activeParts","") +  "GlobalActionsDialog#showDialog,").commit();
												return null;
										}

								});
						if (DeepXposedLogging) XposedUtils.log("Replaced with showDialog(), just executing startActivity() to start my own dialog.");
						if (DeepXposedLogging) XposedUtils.log("Hooking (replace) " + usedGADClass + "#createDialog...");
						XposedHelpers.findAndHookMethod(usedGADClass, lpparam.classLoader, "createDialog", new XC_MethodReplacement() {
										@Override
										protected Object replaceHookedMethod(final MethodHookParam methodHookParam) throws Throwable
										{

												//preferences.edit().putString("activeParts", preferences.getString("activeParts","") +  "GlobalActionsDialog#createDialog,").commit();
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
												//preferences.edit().putString("activeParts", preferences.getString("activeParts","") +  "GlobalActionsDialog#onAiplaneModeChanged,").commit();
												return null;
										}
								});
						if (DeepXposedLogging) XposedUtils.log("Replaced with empty method to prevent crashes, hopefully working...");
						if (HookShutdownThread && ShutdownThreadClass != null)
						{
								if (DeepXposedLogging) XposedUtils.log("Hooking (after) " + usedSDClass + " constructor...");
								XposedBridge.hookAllConstructors(ShutdownThreadClass, new XC_MethodHook() {
												@Override
												public void afterHookedMethod(XC_MethodHook.MethodHookParam param)
												{
														sIsStartedGuard = XposedHelpers.getStaticObjectField(ShutdownThreadClass, "sIsStartedGuard");
														if (sIsStartedGuard == null) XposedUtils.log("sIsStartedGuard is null,reboot will crash...");
														sIsStarted = XposedHelpers.getStaticObjectField(ShutdownThreadClass, "sIsStarted");
														if (sIsStarted == true) XposedUtils.log("sIsStarted is true,thats not normal for fresh created...");
														sInstance = (ShutdownThread) XposedHelpers.getStaticObjectField(ShutdownThreadClass, "sInstance");
														if (sInstance == null) XposedUtils.log("sInstance is null,reboot will crash...");
														//preferences.edit().putString("activeParts", preferences.getString("activeParts","") +  "ShutdownThread#constructor,").commit();
												} 
										});
								if (DeepXposedLogging) XposedUtils.log("Getting needed values...");
								if (DeepXposedLogging) XposedUtils.log("Hooking (replace) " + usedSDClass + "#beginShutdownSequence...");
								XposedHelpers.findAndHookMethod(usedSDClass, lpparam.classLoader, "beginShutdownSequence", Context.class , new XC_MethodReplacement() {
												@Override
												protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable
												{
														try
														{
																synchronized (sIsStartedGuard)
																{
																		if (sIsStarted)
																		{
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
																try
																{
																		sInstance.mCpuWakeLock = sInstance.mPowerManager.newWakeLock(
																				PowerManager.PARTIAL_WAKE_LOCK, TAG + "-cpu");
																		sInstance.mCpuWakeLock.setReferenceCounted(false);
																		sInstance.mCpuWakeLock.acquire();
																}
																catch (SecurityException e)
																{
																		Log.w(TAG, "No permission to acquire wake lock", e);
																		sInstance.mCpuWakeLock = null;
																}
																// also make sure the screen stays on for better user experience
																sInstance.mScreenWakeLock = null;
																if (sInstance.mPowerManager.isScreenOn())
																{
																		try
																		{
																				sInstance.mScreenWakeLock = sInstance.mPowerManager.newWakeLock(
																						PowerManager.FULL_WAKE_LOCK, TAG + "-screen");
																				sInstance.mScreenWakeLock.setReferenceCounted(false);
																				sInstance.mScreenWakeLock.acquire();
																		}
																		catch (SecurityException e)
																		{
																				Log.w(TAG, "No permission to acquire wake lock", e);
																				sInstance.mScreenWakeLock = null;
																		}
																}
																// start the thread that initiates shutdown
																sInstance.mHandler = new Handler() {
																};
																sInstance.start();
														}
														catch (Throwable t)
														{
																XposedUtils.log("ShutdownThread Failed: " + t.toString());
														}
														//preferences.edit().putString("activeParts", preferences.getString("activeParts","") +  "ShutdownThread#beginShutdownSequence,").commit();
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
						if (DeepXposedLogging) XposedUtils.log("Injected active hook version info.");
						if (DeepXposedLogging) XposedUtils.log("Hooking soft reboot call...");
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
														IPowerManager ipm = IPowerManager.Stub.asInterface(ServiceManager.getService(Context.POWER_SERVICE));
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
						if (DeepXposedLogging) XposedUtils.log("Soft reboot method replaced.");
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

		public static class ShutdownThread extends Thread
		{
				private final Object mActionDoneSync = new Object();
				private boolean mActionDone;
				private Context mContext;
				private PowerManager mPowerManager;
				private PowerManager.WakeLock mCpuWakeLock;
				private PowerManager.WakeLock mScreenWakeLock;
				private Handler mHandler;

				private ShutdownThread()
				{

				}


				void actionDone()
				{
						synchronized (mActionDoneSync)
						{
								mActionDone = true;
								mActionDoneSync.notifyAll();
						}
				}
				/**
				 * Makes sure we handle the shutdown gracefully.
				 * Shuts off power regardless of radio and bluetooth state if the alloted time has passed.
				 */
				public void run()
				{
						BroadcastReceiver br = new BroadcastReceiver() {
								@Override public void onReceive(Context context, Intent intent)
								{
										// We don't allow apps to cancel this, so ignore the result.
										actionDone();
								}
						};
						Toast.makeText(sInstance.mContext, "ShutdownThread execute...", Toast.LENGTH_SHORT).show();
						// First send the high-level shut down broadcast.
						mActionDone = false;
						mContext.sendOrderedBroadcastAsUser(new Intent(Intent.ACTION_SHUTDOWN),
																								UserHandle.ALL, null, br, mHandler, 0, null, null);
				}
		}
		
}


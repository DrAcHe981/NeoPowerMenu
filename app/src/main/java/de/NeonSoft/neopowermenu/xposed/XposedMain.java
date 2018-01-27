package de.NeonSoft.neopowermenu.xposed;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.XResources;
import android.media.AudioManager;
import android.os.*;
import android.os.Process;
import android.os.storage.IMountService;
import android.os.storage.IMountShutdownObserver;
import android.provider.*;
import android.util.*;
import android.widget.*;

import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.PreferenceNames;
import de.NeonSoft.neopowermenu.helpers.helper;
import de.NeonSoft.neopowermenu.services.*;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import android.view.*;
import android.view.WindowManagerPolicy.*;

import java.lang.reflect.*;

import android.telecom.*;
import android.util.Log;
import android.telephony.*;

import com.android.internal.app.AlertController;

public class XposedMain implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    private static final String TAG = "NPM";
    private static XSharedPreferences preferences;
    public static boolean DeepXposedLogging = false;
    public boolean UseRoot = true;
    public boolean HookShutdownThread = false;

    private boolean ExperimentalPWMHook = false;

    public static String PACKAGE_NAME = MainActivity.class.getPackage().getName();
    private static final File prefsFileProt = new File("/data/user_de/0/" + MainActivity.class.getPackage().getName() + "/shared_prefs/" + MainActivity.class.getPackage().getName() + "_preferences.xml");

    private static final String SHUTDOWN_BROADCAST
            = "am broadcast android.intent.action.ACTION_SHUTDOWN";
    private static final String SHUTDOWN_CMD = "reboot -p";
    private static final String REBOOT_CMD = "reboot";
    private static final String REBOOT_SOFT_REBOOT_CMD = "setprop ctl.restart zygote";
    private static final String REBOOT_RECOVERY_CMD = "reboot recovery";
    private static final String REBOOT_BOOTLOADER_CMD = "reboot bootloader";
    private static final String[] REBOOT_SAFE_MODE
            = new String[]{"setprop persist.sys.safemode 1", REBOOT_SOFT_REBOOT_CMD};

    public static final String CLASS_GLOBAL_ACTIONS = "com.android.internal.policy.impl.GlobalActions";
    public static final String CLASS_GLOBAL_ACTIONS_MARSHMALLOW = "com.android.server.policy.GlobalActions";
    private static final String CLASS_PHONE_WINDOW_MANAGER = "com.android.internal.policy.impl.PhoneWindowManager";
    private static final String CLASS_PHONE_WINDOW_MANAGER_MARSHMALLOW = "com.android.server.policy.PhoneWindowManager";
    private static final String CLASS_PHONE_WINDOW_MANAGER_MARSHMALLOW_OEM = "com.android.server.policy.OemPhoneWindowManager";

    private static final String CLASS_PACKAGE_MANAGER_SERVICE = "com.android.server.pm.PackageManagerService";
    private static final String CLASS_PACKAGE_MANAGER_SERVICE_MARSHMALLOW = "com.android.server.pm.PackageManagerService";
    private static final String CLASS_PACKAGE_PARSER_PACKAGE = "android.content.pm.PackageParser.Package";
    private static final String CLASS_SHUTDOWN_THREAD = "com.android.server.power.ShutdownThread";

    private static final String[] XPOSEDPERMISSIONS = {"android.permission.ACCESS_SURFACE_FLINGER", "android.permission.READ_FRAME_BUFFER"};

    private static final String CLASS_SYSTEMUI = "com.android.systemui.SystemUIApplication";

    public static final String NPM_ACTION_BROADCAST_SHUTDOWN = "de.NeonSoft.neopowermenu.action.Shutdown";
    public static final String NPM_ACTION_BROADCAST_REBOOT = "de.NeonSoft.neopowermenu.action.Reboot";
    public static final String NPM_ACTION_BROADCAST_SOFTREBOOT = "de.NeonSoft.neopowermenu.action.SoftReboot";
    public static final String NPM_ACTION_BROADCAST_REBOOTRECOVERY = "de.NeonSoft.neopowermenu.action.RebootRecovery";
    public static final String NPM_ACTION_BROADCAST_REBOOTBOOTLOADER = "de.NeonSoft.neopowermenu.action.RebootBootloader";
    public static final String NPM_ACTION_BROADCAST_SCREENSHOT = "de.NeonSoft.neopowermenu.action.takeScreenshot";
    public static final String NPM_ACTION_BROADCAST_SCREENRECORD = "de.NeonSoft.neopowermenu.action.takeScreenrecord";
    public static final String NPM_ACTION_BROADCAST_KILLSYSTEMUI = "de.NeonSoft.neopowermenu.action.killSystemUI";
    public static final String NPM_ACTION_BROADCAST_TOGGLEAIRPLANEMODE = "de.NeonSoft.neopowermenu.action.toggleAirplaneMode";
    public static final String NPM_ACTION_BROADCAST_KILLAPP = "de.NeonSoft.neopowermenu.action.KillApp";
    public static final String NPM_ACTION_BROADCAST_TOGGLEROTATION = "de.NeonSoft.neopowermenu.action.toggleRotationMode";
    public static final String NPM_ACTION_BROADCAST_TOGGLEDATA = "de.NeonSoft.neopowermenu.action.toggleData";
    public static final String NPM_ACTION_BROADCAST_REBOOTFLASHMODE = "de.NeonSoft.neopowermenu.action.RebootFlashMode";
    public static final String NPM_ACTION_BROADCAST_FAKEPOWEROFF = "de.NeonSoft.neopowermenu.action.FakePowerOff";


    static Handler xHandler;
    private final static Object mScreenshotLock = new Object();
    private static ServiceConnection mScreenshotConnection = null;

    static Context mContext;
    public Object mObjectHolder;

    /*<!-- Internal Hook version to check if reboot is needed --!>*/
    private static final int XposedHookVersion = 28;


    private BroadcastReceiver mNPMReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context p1, Intent p2) {
            //Log.i(TAG, "Received broadcast: " + p2.getAction());
            if (DeepXposedLogging)
                XposedUtils.log("Received broadcast: " + p2.getAction());
            switch (p2.getAction()) {
                case NPM_ACTION_BROADCAST_SHUTDOWN:
                    XposedUtils.doPowerAction(p1, XposedUtils.NPM_POWERACTION_SHUTDOWN);
                    //XposedHelpers.callMethod(ShutdownThreadClass,"shutdown",mContext, false);
                    //performCMDCall(new String[]{SHUTDOWN_BROADCAST, SHUTDOWN_CMD});
                    break;
                case NPM_ACTION_BROADCAST_REBOOT:
                    XposedUtils.doPowerAction(p1, XposedUtils.NPM_POWERACTION_REBOOT);
                    //XposedHelpers.callMethod(ShutdownThreadClass,"reboot",mContext, null, false);
                    //performCMDCall(new String[]{SHUTDOWN_BROADCAST, REBOOT_CMD});
                    break;
                case NPM_ACTION_BROADCAST_SOFTREBOOT:
                    XposedUtils.doPowerAction(p1, XposedUtils.NPM_POWERACTION_SOFTREBOOT);
                    //XposedUtils.performSoftReboot();
                    //performCMDCall(new String[]{SHUTDOWN_BROADCAST, REBOOT_SOFT_REBOOT_CMD});
                    break;
                case NPM_ACTION_BROADCAST_REBOOTRECOVERY:
                    XposedUtils.doPowerAction(p1, XposedUtils.NPM_POWERACTION_RECOVERY);
                    //XposedHelpers.callMethod(ShutdownThreadClass,"reboot",mContext, "recovery", false);
                    //performCMDCall(new String[]{SHUTDOWN_BROADCAST, REBOOT_RECOVERY_CMD});
                    break;
                case NPM_ACTION_BROADCAST_REBOOTBOOTLOADER:
                    XposedUtils.doPowerAction(p1, XposedUtils.NPM_POWERACTION_BOOTLOADER);
                    //XposedHelpers.callMethod(ShutdownThreadClass,"reboot",mContext, "bootloader", false);
                    //performCMDCall(new String[]{SHUTDOWN_BROADCAST, REBOOT_BOOTLOADER_CMD});
                    break;
                case NPM_ACTION_BROADCAST_KILLSYSTEMUI:
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            android.os.Process.sendSignal(android.os.Process.myPid(), android.os.Process.SIGNAL_KILL);
                        }
                    }, 100);
                    break;
                case NPM_ACTION_BROADCAST_SCREENSHOT:
                    takeScreenshot(p1);
                    break;
                case NPM_ACTION_BROADCAST_SCREENRECORD:
                    toggleScreenRecord(p1);
                    break;
                case NPM_ACTION_BROADCAST_TOGGLEAIRPLANEMODE:
                    toggleAiplaneMode(p1);
                    break;
                case NPM_ACTION_BROADCAST_KILLAPP:
                    killLastApp(p1);
                    break;
                case NPM_ACTION_BROADCAST_TOGGLEROTATION:
                    toggleRotation(p1);
                    break;
                case ScreenRecordingService.ACTION_TOGGLE_SHOW_TOUCHES:
                    toggleShowTouches(p1, p2.getIntExtra(ScreenRecordingService.EXTRA_SHOW_TOUCHES, -1));
                    break;
                case NPM_ACTION_BROADCAST_TOGGLEDATA:
                    toggleData(p1, !isDataActive(p1));
                    break;
                case NPM_ACTION_BROADCAST_FAKEPOWEROFF:
                    fakePowerOff(p1);
                    break;
            }
            if (isOrderedBroadcast()) {
                if (DeepXposedLogging)
                    XposedUtils.log("Aborting broadcast, to prevent multiple calls!");
                abortBroadcast();
            }
        }
    };

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        if (XposedUtils.USE_DEVICE_PROTECTED_STORAGE()) {
            preferences = new XSharedPreferences(prefsFileProt);
        } else {
            preferences = new XSharedPreferences(PACKAGE_NAME);
        }
        preferences.makeWorldReadable();
        preferences.reload();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !startupParam.startsSystemServer)
            return;
        DeepXposedLogging = preferences.getBoolean("DeepXposedLogging", false);
        ExperimentalPWMHook = preferences.getBoolean("ExperimentalPWMHook", false);
        UseRoot = preferences.getBoolean("UseRoot", true);
        HookShutdownThread = preferences.getBoolean("HookShutdownThread", false);
        XposedUtils.log("/_Zygote init...");
        XposedUtils.log("|_____Module Info");
        XposedUtils.log("|-Module Path: " + startupParam.modulePath);
        XposedUtils.log("|-Hook version: " + XposedHookVersion);
        XposedUtils.log("|-Preferences: " + preferences.getFile().getAbsolutePath());
        XposedUtils.log("||-Found: " + (preferences.getFile().exists() ? "yes" : "no, using defaults."));
        XposedUtils.log("|\\-World readable: " + preferences.getFile().canRead());
        XposedUtils.log("|-Deep Logging: " + (DeepXposedLogging ? "active, logging everything." : "not active, logging only errors."));
        if (!DeepXposedLogging) {
            XposedUtils.log("|");
            XposedUtils.log("|--->> ATTENTION");
            XposedUtils.log("|--->> Before reporting a system crash or not working function, please activate deep logging and restart the device, else I wont be able to help!");
            XposedUtils.log("|");
        }
        if (XposedUtils.isOxygenOsRom()) {
            //ExperimentalPWMHook = true;
            //preferences.edit().putBoolean(PreferenceNames.pExperimentalPWMHook, true).commit();
            //XposedUtils.log("|---OxygenOS Device detected, enabling phone window manager hook by default.");
        }
        XposedUtils.log("|-ExperimentalPWMHook: " + ExperimentalPWMHook);
        XposedUtils.log("|-UseRootCommands: " + UseRoot);
        XposedUtils.log("|-Hook ShutdownThread: " + HookShutdownThread);
        if (DeepXposedLogging) {
            XposedUtils.log("|_____Device Infos");
            XposedUtils.log("|-Hardware: " + Build.HARDWARE);
            XposedUtils.log("|-Product: " + Build.PRODUCT);
            XposedUtils.log("|-Manufacturer: " + Build.MANUFACTURER);
            XposedUtils.log("|-Model: " + Build.MODEL);
            XposedUtils.log("|-Android Version: " + Build.VERSION.RELEASE);
            XposedUtils.log("|-SDK Version: " + Build.VERSION.SDK_INT);
            XposedUtils.log("|-ROM: " + Build.DISPLAY);
        }
        XposedUtils.log("\\_Zygote init complete.");
    }


    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("android") &&
                lpparam.processName.equals("android")) {
            XposedUtils.log("Loading Power Menu...");

            final String usedGADClass;
            final String usedPWMClass;
            String usedPMClass;
            String usedSDTClass;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                usedGADClass = CLASS_GLOBAL_ACTIONS_MARSHMALLOW;
                //if (XposedUtils.isOxygenOsRom()) {
                //    usedPWMClass = CLASS_PHONE_WINDOW_MANAGER_MARSHMALLOW_OEM;
                //} else {
                    usedPWMClass = CLASS_PHONE_WINDOW_MANAGER_MARSHMALLOW;
                //}
                usedPMClass = CLASS_PACKAGE_MANAGER_SERVICE_MARSHMALLOW;
            } else {
                usedGADClass = CLASS_GLOBAL_ACTIONS;
                usedPWMClass = CLASS_PHONE_WINDOW_MANAGER;
                usedPMClass = CLASS_PACKAGE_MANAGER_SERVICE;
            }
            usedSDTClass = CLASS_SHUTDOWN_THREAD;

            if (DeepXposedLogging)
                XposedUtils.log("Detected " + android.os.Build.VERSION.RELEASE + "(" + Build.VERSION.SDK_INT + "), injecting to: ");
            if (DeepXposedLogging) XposedUtils.log(usedGADClass);
            if (DeepXposedLogging) XposedUtils.log(usedPWMClass);
            if (DeepXposedLogging) XposedUtils.log(usedPMClass);
            final Class<?> phoneWindowManagerClass = XposedHelpers.findClass(usedPWMClass, lpparam.classLoader);
            final Class<?> globalActionsClass = XposedHelpers.findClass(usedGADClass, lpparam.classLoader);

            final Class<?> pmServiceClass = XposedHelpers.findClass(usedPMClass, lpparam.classLoader);

            final Class<?> ShutdownThreadClass = XposedHelpers.findClass(usedSDTClass, lpparam.classLoader);

            if (DeepXposedLogging)
                XposedUtils.log("Getting permissions, using method for " + ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) ? "lollipop and above" : "kitkat and below") + "...");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                XposedHelpers.findAndHookMethod(pmServiceClass, "grantPermissionsLPw",
                        CLASS_PACKAGE_PARSER_PACKAGE, boolean.class, String.class, new XC_MethodHook() {
                            @SuppressWarnings("unchecked")
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                final String pkgName = (String) XposedHelpers.getObjectField(param.args[0], "packageName");

                                if (PACKAGE_NAME.equals(pkgName)) {
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                        final Object extras = XposedHelpers.getObjectField(param.args[0], "mExtras");
                                        final Object ps = XposedHelpers.callMethod(extras, "getPermissionsState");
                                        final List<String> grantedPerms =
                                                (List<String>) XposedHelpers.getObjectField(param.args[0], "requestedPermissions");
                                        final Object settings = XposedHelpers.getObjectField(param.thisObject, "mSettings");
                                        final Object permissions = XposedHelpers.getObjectField(settings, "mPermissions");

                                        for (int i = 0; i < XPOSEDPERMISSIONS.length; i++) {
                                            if (!(boolean) XposedHelpers.callMethod(ps, "hasInstallPermission", XPOSEDPERMISSIONS[i])) {
                                                final Object pAccessSurfaceFlinger = XposedHelpers.callMethod(permissions, "get",
                                                        XPOSEDPERMISSIONS[i]);
                                                int ret = (int) XposedHelpers.callMethod(ps, "grantInstallPermission", pAccessSurfaceFlinger);
                                                if (DeepXposedLogging)
                                                    XposedUtils.log("Permission added: " + XPOSEDPERMISSIONS[i] + " (" + pAccessSurfaceFlinger + ") ; ret=" + ret);
                                            }
                                        }

                                    } else {
                                        final Object extras = XposedHelpers.getObjectField(param.args[0], "mExtras");
                                        final Set<String> grantedPerms =
                                                (Set<String>) XposedHelpers.getObjectField(extras, "grantedPermissions");
                                        final Object settings = XposedHelpers.getObjectField(param.thisObject, "mSettings");
                                        final Object permissions = XposedHelpers.getObjectField(settings, "mPermissions");

                                        for (int i = 0; i < XPOSEDPERMISSIONS.length; i++) {
                                            if (!grantedPerms.contains(XPOSEDPERMISSIONS[i])) {
                                                final Object pAccessSurfaceFlinger = XposedHelpers.callMethod(permissions, "get",
                                                        XPOSEDPERMISSIONS[i]);
                                                grantedPerms.add(XPOSEDPERMISSIONS[i]);
                                                int[] gpGids = (int[]) XposedHelpers.getObjectField(extras, "gids");
                                                int[] bpGids = (int[]) XposedHelpers.getObjectField(pAccessSurfaceFlinger, "gids");
                                                gpGids = (int[]) XposedHelpers.callStaticMethod(param.thisObject.getClass(),
                                                        "appendInts", gpGids, bpGids);

                                                if (DeepXposedLogging)
                                                    XposedUtils.log("Permission added: " + XPOSEDPERMISSIONS[i] + " (" + pAccessSurfaceFlinger + ")");
                                            }
                                        }
                                    }
                                }
                            }
                        });
            } else {
                XposedHelpers.findAndHookMethod(pmServiceClass, "grantPermissionsLPw",
                        CLASS_PACKAGE_PARSER_PACKAGE, boolean.class, new XC_MethodHook() {
                            @SuppressWarnings("unchecked")
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                final String pkgName = (String) XposedHelpers.getObjectField(param.args[0], "packageName");

                                // NeoPowerMenu
                                if (PACKAGE_NAME.equals(pkgName)) {
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                        final Object extras = XposedHelpers.getObjectField(param.args[0], "mExtras");
                                        final Object ps = XposedHelpers.callMethod(extras, "getPermissionsState");
                                        final List<String> grantedPerms =
                                                (List<String>) XposedHelpers.getObjectField(param.args[0], "requestedPermissions");
                                        final Object settings = XposedHelpers.getObjectField(param.thisObject, "mSettings");
                                        final Object permissions = XposedHelpers.getObjectField(settings, "mPermissions");

                                        for (int i = 0; i < XPOSEDPERMISSIONS.length; i++) {
                                            if (!(boolean) XposedHelpers.callMethod(ps, "hasInstallPermission", XPOSEDPERMISSIONS[i])) {
                                                final Object pAccessSurfaceFlinger = XposedHelpers.callMethod(permissions, "get",
                                                        XPOSEDPERMISSIONS[i]);
                                                int ret = (int) XposedHelpers.callMethod(ps, "grantInstallPermission", pAccessSurfaceFlinger);
                                                if (DeepXposedLogging)
                                                    XposedUtils.log("Permission added: " + XPOSEDPERMISSIONS[i] + " (" + pAccessSurfaceFlinger + ") ; ret=" + ret);
                                            }
                                        }

                                    } else {
                                        final Object extras = XposedHelpers.getObjectField(param.args[0], "mExtras");
                                        final Set<String> grantedPerms =
                                                (Set<String>) XposedHelpers.getObjectField(extras, "grantedPermissions");
                                        final Object settings = XposedHelpers.getObjectField(param.thisObject, "mSettings");
                                        final Object permissions = XposedHelpers.getObjectField(settings, "mPermissions");

                                        for (int i = 0; i < XPOSEDPERMISSIONS.length; i++) {
                                            if (!grantedPerms.contains(XPOSEDPERMISSIONS[i])) {
                                                final Object pAccessSurfaceFlinger = XposedHelpers.callMethod(permissions, "get",
                                                        XPOSEDPERMISSIONS[i]);
                                                grantedPerms.add(XPOSEDPERMISSIONS[i]);
                                                int[] gpGids = (int[]) XposedHelpers.getObjectField(extras, "gids");
                                                int[] bpGids = (int[]) XposedHelpers.getObjectField(pAccessSurfaceFlinger, "gids");
                                                gpGids = (int[]) XposedHelpers.callStaticMethod(param.thisObject.getClass(),
                                                        "appendInts", gpGids, bpGids);

                                                if (DeepXposedLogging)
                                                    XposedUtils.log("Permission added: " + XPOSEDPERMISSIONS[i] + " (" + pAccessSurfaceFlinger + ")");
                                            }
                                        }
                                    }
                                }
                            }
                        });
            }
            if (DeepXposedLogging) XposedUtils.log("Permission request hooked.");
            if (!ExperimentalPWMHook) {
                if (DeepXposedLogging)
                    XposedUtils.log("Hooking (replace) " + usedGADClass + " Constructor...");
                XposedBridge.hookAllConstructors(globalActionsClass, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(final MethodHookParam param) throws Throwable {
                        try {
                            mObjectHolder = param.thisObject;
                            final Context context = (Context) param.args[0];
                            mContext = context;
                            final Handler mHandler = new Handler();
                            xHandler = mHandler;
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                                if (DeepXposedLogging)
                                    XposedUtils.log("Creating Broadcast Receiver for KitKat and below...");
                                IntentFilter filter = new IntentFilter();
                                //filter.addAction(NPM_ACTION_BROADCAST_KILLSYSTEMUI);
                                filter.addAction(NPM_ACTION_BROADCAST_SHUTDOWN);
                                filter.addAction(NPM_ACTION_BROADCAST_REBOOT);
                                filter.addAction(NPM_ACTION_BROADCAST_SOFTREBOOT);
                                filter.addAction(NPM_ACTION_BROADCAST_REBOOTRECOVERY);
                                filter.addAction(NPM_ACTION_BROADCAST_REBOOTBOOTLOADER);
                                filter.addAction(NPM_ACTION_BROADCAST_SCREENSHOT);
                                filter.addAction(NPM_ACTION_BROADCAST_SCREENRECORD);
                                filter.addAction(NPM_ACTION_BROADCAST_TOGGLEAIRPLANEMODE);
                                filter.addAction(NPM_ACTION_BROADCAST_TOGGLEROTATION);
                                filter.addAction(NPM_ACTION_BROADCAST_KILLAPP);
                                filter.addAction(ScreenRecordingService.ACTION_TOGGLE_SHOW_TOUCHES);
                                filter.addAction(NPM_ACTION_BROADCAST_TOGGLEDATA);
                                filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
                                mContext.registerReceiver(mNPMReceiver, filter);
                            }
                        } catch (Throwable t) {
                            XposedUtils.log(t.toString());
                        }
                        return null;
                    }
                });
                if (DeepXposedLogging)
                    XposedUtils.log("Registering Broadcast Receiver and setting other values...");
                if (DeepXposedLogging)
                    XposedUtils.log("Hooking (replace) " + usedGADClass + "#showDialog..." + (XposedUtils.isParanoidRom() ? "(Paranoid ROM Method)" : ""));
                XC_MethodHook showDialogHook = new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(final MethodHookParam methodHookParam) throws Throwable {
                        final PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
                        try {
                            showDialog();
                        } catch (Throwable t) {
                            Log.e("NPM", "Failed to show dialog, showing info...", t);
                            AlertDialog.Builder adb = new AlertDialog.Builder(mContext, 0);
                            adb.setMessage(R.string.xposedMain_MenuActivityNotFound);
                            adb.setNegativeButton(R.string.xposedMain_MenuActivityNotFound_Cancel, null);
                            adb.setPositiveButton(R.string.xposedMain_MenuActivityNotFound_Restart, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    pm.reboot(null);
                                }
                            });
                            adb.show();
                        }
                        return null;
                    }

                };
                if (XposedUtils.isParanoidRom()) {
                    XposedHelpers.findAndHookMethod(globalActionsClass, "showDialog",
                            boolean.class, boolean.class, boolean.class, showDialogHook);
                } else {
                    XposedHelpers.findAndHookMethod(globalActionsClass, "showDialog",
                            boolean.class, boolean.class, showDialogHook);
                }
                if (DeepXposedLogging)
                    XposedUtils.log("Replaced with showDialog(), just executing startActivity() to start my own dialog.");
                if (DeepXposedLogging)
                    XposedUtils.log("Hooking (replace) " + usedGADClass + "#createDialog...");
                XposedHelpers.findAndHookMethod(usedGADClass, lpparam.classLoader, "createDialog", new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(final MethodHookParam methodHookParam) throws Throwable {
                        try {
                            mContext = (Context) methodHookParam.args[0];
                        } catch (Throwable t) {
                            XposedUtils.log(t.toString());
                        }
                        return null;
                    }

                });
                if (DeepXposedLogging)
                    XposedUtils.log("Replaced with empty method to prevent crashes.");
                if (DeepXposedLogging)
                    XposedUtils.log("Hooking (replace) " + usedGADClass + "#onAirplaneModeChanged...");
                XposedHelpers.findAndHookMethod(usedGADClass, lpparam.classLoader, "onAirplaneModeChanged", new XC_MethodReplacement() {

                    @Override
                    protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                        return null;
                    }
                });
                if (DeepXposedLogging)
                    XposedUtils.log("Replaced with empty method to prevent crashes.");
            } else {
                if (DeepXposedLogging)
                    XposedUtils.log("Hooking in the PhoneWindowManager");
                if (DeepXposedLogging)
                    XposedUtils.log("Hooking (after) " + usedPWMClass + "#init...");
                XposedHelpers.findAndHookMethod(usedPWMClass, lpparam.classLoader, "init", Context.class, IWindowManager.class, WindowManagerFuncs.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(final MethodHookParam methodHookParam) throws Throwable {
                        try {
                            mContext = (Context) methodHookParam.args[0];
                        } catch (Throwable t) {
                            XposedUtils.log(t.toString());
                        }
                    }

                });
                if (DeepXposedLogging) XposedUtils.log("Getting mContext...");
                if (DeepXposedLogging)
                    XposedUtils.log("Hooking (replace) " + usedPWMClass + "#showGlobalActionsInternal...");
                XposedHelpers.findAndHookMethod(usedPWMClass, lpparam.classLoader, "showGlobalActionsInternal", new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(final MethodHookParam methodHookParam) throws Throwable {
                        //Log.d(TAG, "GlobalActionsInternal - long Power press");
                        try {
                            XposedBridge.invokeOriginalMethod(XposedHelpers.findMethodExact(usedPWMClass, lpparam.classLoader, "sendCloseSystemWindows", String.class), methodHookParam.thisObject, new Object[]{"globalactions"});
                        } catch (Throwable t) {
                            XposedUtils.log("Failed to invoke sendCloseSystemWindows: " + t);
                        }
                        final PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
                        final KeyguardManager km = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
                        try {
                            showDialog();
                        } catch (Throwable t) {
                            Log.e("NPM", "Failed to show dialog, showing info...", t);
                            AlertDialog.Builder adb = new AlertDialog.Builder(mContext, 0);
                            adb.setMessage(R.string.xposedMain_MenuActivityNotFound);
                            adb.setNegativeButton(R.string.xposedMain_MenuActivityNotFound_Cancel, null);
                            adb.setPositiveButton(R.string.xposedMain_MenuActivityNotFound_Restart, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    pm.reboot(null);
                                }
                            });
                            adb.show();
                        }
                        return null;
                    }

                });
                if (DeepXposedLogging)
                    XposedUtils.log("Replaced with showDialog(), just executing startActivity() to start my own dialog.");
            }
            if (HookShutdownThread) {
                if (DeepXposedLogging)
                    XposedUtils.log("Hooking " + ProgressDialog.Builder.class.getName() + "#show...");
                XposedHelpers.findAndHookMethod(ProgressDialog.Builder.class, "show", Context.class, CharSequence.class,
                        CharSequence.class, boolean.class,
                boolean.class, DialogInterface.OnCancelListener.class, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(final MethodHookParam param) throws Throwable {
                        try {
                            AlertController.AlertParams aC = ((AlertController.AlertParams) XposedHelpers.getObjectField(param.thisObject, "P"));
                            Log.d("NPM", "[xposedMain] Dialog title: " + aC.mTitle);
                            Resources sysRes = XResources.getSystem();
                            AlertDialog dialog = (AlertDialog) XposedHelpers.callMethod(param.thisObject, "create");
                            if (!aC.mTitle.equals(sysRes.getString(sysRes.getIdentifier("power_off", "string", "android"))) && !aC.mTitle.equals(sysRes.getString(sysRes.getIdentifier("reboot", "string", "android"))) && !aC.mTitle.equals(sysRes.getString(sysRes.getIdentifier("reboot_to_reset_title", "string", "android")))) {
                               dialog.show();
                            }
                            return dialog;
                        } catch (Throwable t) {
                            Log.e("NPM", "[xposedMain] Failed to prevent shutdown dialog:", t);
                        }
                        return null;
                    }
                });
                if (DeepXposedLogging)
                    XposedUtils.log("Trying to prevent default shutdown dialog being shown.");
            }

            XposedUtils.log("Loading complete, all hooks executed.");
        }
        if (lpparam.packageName.equalsIgnoreCase("com.android.systemui")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (DeepXposedLogging)
                    XposedUtils.log("Hooking (after) " + CLASS_SYSTEMUI + "#onCreate...");
                XposedHelpers.findAndHookMethod(CLASS_SYSTEMUI, lpparam.classLoader, "onCreate", new XC_MethodHook() {
                    @Override
                    public void afterHookedMethod(MethodHookParam param) throws Throwable {

                        if (DeepXposedLogging)
                            XposedUtils.log("Creating Broadcast Receiver for Lollipop and above...");
                        Application mNPMApplication = (Application) param.thisObject;
                        try {
                            IntentFilter filter = new IntentFilter();
                            filter.addAction(NPM_ACTION_BROADCAST_SHUTDOWN);
                            filter.addAction(NPM_ACTION_BROADCAST_REBOOT);
                            filter.addAction(NPM_ACTION_BROADCAST_SOFTREBOOT);
                            filter.addAction(NPM_ACTION_BROADCAST_REBOOTRECOVERY);
                            filter.addAction(NPM_ACTION_BROADCAST_REBOOTBOOTLOADER);
                            filter.addAction(NPM_ACTION_BROADCAST_KILLSYSTEMUI);
                            filter.addAction(NPM_ACTION_BROADCAST_SCREENSHOT);
                            filter.addAction(NPM_ACTION_BROADCAST_SCREENRECORD);
                            filter.addAction(NPM_ACTION_BROADCAST_TOGGLEAIRPLANEMODE);
                            filter.addAction(NPM_ACTION_BROADCAST_TOGGLEROTATION);
                            filter.addAction(NPM_ACTION_BROADCAST_KILLAPP);
                            filter.addAction(ScreenRecordingService.ACTION_TOGGLE_SHOW_TOUCHES);
                            filter.addAction(NPM_ACTION_BROADCAST_TOGGLEDATA);
                            filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
                            mNPMApplication.registerReceiver(mNPMReceiver, filter);
                        } catch (Throwable t) {
                            XposedUtils.log("Failed to register BroadcastReceiver: " + t.toString());
                        }
                    }
                });
                if (DeepXposedLogging) XposedUtils.log("Registered receiver for UI events.");
            }
        }
        if (lpparam.packageName.equals("de.NeonSoft.neopowermenu")) {
            if (DeepXposedLogging) XposedUtils.log("Creating self inject...");
            XposedHelpers.findAndHookMethod("de.NeonSoft.neopowermenu.helpers.helper", lpparam.classLoader, "ModuleState", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(final MethodHookParam param) throws Throwable {
                    int active = XposedHookVersion;
                    return active;
                }
            });
            if (DeepXposedLogging) XposedUtils.log("Injected active hook version info.");
            if (DeepXposedLogging) XposedUtils.log("Hooking soft reboot call...");
            XposedHelpers.findAndHookMethod("de.NeonSoft.neopowermenu.xposed.XposedUtils", lpparam.classLoader, "performSoftReboot", new XC_MethodReplacement() {

                @Override
                protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    try {
                        IPowerManager ipm = IPowerManager.Stub.asInterface(ServiceManager.getService(Context.POWER_SERVICE));
                        ipm.crash("Hot reboot");
                    } catch (Throwable t) {
                        try {
                            XposedUtils.SystemProp.set("ctl.restart", "surfaceflinger");
                            XposedUtils.SystemProp.set("ctl.restart", "zygote");
                        } catch (Throwable t2) {
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

    private static void performCMDCall(String[] cmd) {
        XposedUtils.log("Trying to run '" + cmd[0] + "' using system process call");

        java.lang.Process suProcess = null;
        try {
            suProcess = Runtime.getRuntime().exec(cmd);
            DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
            os.writeBytes("" + "\n");
            os.flush();
            os.close();
        } catch (Throwable e) {
            XposedUtils.log("Failed to perform system process call: " + e.toString());
        }
    }

    private static void takeScreenshot(final Context p1) {
        final Handler handler = new Handler();

        mScreenshotConnection = null;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                synchronized (mScreenshotLock) {
                    if (mScreenshotConnection != null) {
                        XposedUtils.log("Screenshot failed: cant create connection.");
                        return;
                    }
                    ComponentName cn = new ComponentName("com.android.systemui",
                            "com.android.systemui.screenshot.TakeScreenshotService");
                    Intent intent = new Intent();
                    intent.setComponent(cn);
                    ServiceConnection conn = new ServiceConnection() {
                        @Override
                        public void onServiceConnected(ComponentName name, IBinder service) {
                            synchronized (mScreenshotLock) {
                                if (mScreenshotConnection != this) {
                                    XposedUtils.log("Screenshot failed: wrong connection.");
                                    return;
                                }
                                final Messenger messenger = new Messenger(service);
                                final Message msg = Message.obtain(null, 1);
                                final ServiceConnection myConn = this;

                                Handler h = new Handler(handler.getLooper()) {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        synchronized (mScreenshotLock) {
                                            if (mScreenshotConnection == myConn) {
                                                p1.unbindService(mScreenshotConnection);
                                                mScreenshotConnection = null;
                                                handler.removeCallbacks(mScreenshotTimeout);
                                                if (DeepXposedLogging)
                                                    XposedUtils.log("Screenshot message handled, unbinding and removing callbacks.");
                                            }
                                        }
                                    }
                                };
                                msg.replyTo = new Messenger(h);
                                msg.arg1 = msg.arg2 = 0;
                                h.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (DeepXposedLogging)
                                                XposedUtils.log("Sending screenshot message...");
                                            messenger.send(msg);
                                        } catch (RemoteException e) {
                                            //Log.e(TAG, e.toString());
                                            XposedUtils.log("Screenshot failed: " + e.toString());
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onServiceDisconnected(ComponentName name) {
                        }
                    };
                    if (p1.bindService(intent, conn, Context.BIND_AUTO_CREATE)) {
                        mScreenshotConnection = conn;
                        handler.postDelayed(mScreenshotTimeout, 10000);
                    }
                }
            }
        }, preferences.getLong(PreferenceNames.pScreenshotDelay, 1000));
    }

    private static void toggleScreenRecord(Context p1) {
        try {
            Context pcontext = p1.createPackageContext(PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);
            Intent intent = new Intent(pcontext, ScreenRecordingService.class);
            intent.setAction(ScreenRecordingService.ACTION_TOGGLE_SCREEN_RECORDING);
            p1.startService(intent);
        } catch (Throwable t) {
            XposedUtils.log("Start Screenrecord service failed:" + t);
        }
    }

    private static void toggleAiplaneMode(Context p1) {
        // read the airplane mode setting
        boolean isEnabled = false;
        if (Build.VERSION.SDK_INT >= 17) {
            isEnabled = Settings.Global.getInt(
                    p1.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) == 1;

            // toggle airplane mode
            Settings.Global.putInt(
                    p1.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, isEnabled ? 0 : 1);
        } else {
            isEnabled = Settings.System.getInt(
                    p1.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) == 1;

            // toggle airplane mode
            Settings.System.putInt(
                    p1.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, isEnabled ? 0 : 1);
        }
        // Post an intent to reload
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", !isEnabled);
        p1.sendBroadcast(intent);
    }

    private static void killLastApp(final Context p1) {
        try {
            final Intent intent = new Intent(Intent.ACTION_MAIN);
            final PackageManager pm = p1.getPackageManager();
            String defaultHomePackage = "com.android.launcher";
            intent.addCategory(Intent.CATEGORY_HOME);

            final ResolveInfo res = pm.resolveActivity(intent, 0);
            if (res.activityInfo != null && !res.activityInfo.packageName.equals("android")) {
                defaultHomePackage = res.activityInfo.packageName;
            }

            ActivityManager am = (ActivityManager) p1.getSystemService(Service.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> apps = am.getRunningAppProcesses();

            String targetKilled = null;
            for (ActivityManager.RunningAppProcessInfo appInfo : apps) {
                int uid = appInfo.uid;
                // Make sure it's a foreground user application (not system,
                // root, phone, etc.)
                if (uid >= Process.FIRST_APPLICATION_UID && uid <= Process.LAST_APPLICATION_UID
                        && appInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                        !appInfo.processName.startsWith(defaultHomePackage) &&
                        !appInfo.processName.startsWith(MainActivity.class.getPackage().getName()) &&
                        !appInfo.processName.startsWith("com.google.android.gms") &&
                        !appInfo.processName.startsWith("com.android.systemui")) {
                    if (appInfo.pkgList != null && appInfo.pkgList.length > 0) {
                        for (String pkg : appInfo.pkgList) {
                            if (DeepXposedLogging) XposedUtils.log("Force stopping: " + pkg);
                            XposedHelpers.callMethod(am, "forceStopPackage", pkg);
                        }
                    } else {
                        if (DeepXposedLogging)
                            XposedUtils.log("Killing process ID " + appInfo.pid + ": " + appInfo.processName);
                        Process.killProcess(appInfo.pid);
                    }
                    targetKilled = appInfo.processName;
                    break;
                }
            }

            if (targetKilled != null) {
                try {
                    targetKilled = (String) pm.getApplicationLabel(
                            pm.getApplicationInfo(targetKilled, 0));
                } catch (PackageManager.NameNotFoundException nfe) {
                    //
                }
                //Class<?>[] paramArgs = new Class<?>[3];
                //paramArgs[0] = XposedHelpers.findClass(CLASS_WINDOW_STATE, null);
                //paramArgs[1] = int.class;
                //paramArgs[2] = boolean.class;
                //XposedHelpers.callMethod(mPhoneWindowManager, "performHapticFeedbackLw", paramArgs, null, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING, true);
                Toast.makeText(p1, p1.createPackageContext(MainActivity.class.getPackage().getName(),0).getString(R.string.powerMenu_Task_Killed).replace("[TARGET]", targetKilled), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(p1, p1.createPackageContext(MainActivity.class.getPackage().getName(),0).getString(R.string.powerMenu_No_Task_To_Kill), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            XposedUtils.log("Failed to execute kill command: " + e);
        }
        /*try {
            ActivityManager am = (ActivityManager) p1.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = am.getRunningAppProcesses();

            int processNumber = 1;
            if (runningAppProcessInfo.get(processNumber).processName.equalsIgnoreCase("com.google.gms.persistent")) processNumber = 2;

            if (DeepXposedLogging) {
                XposedUtils.log("Trying to kill " + runningAppProcessInfo.get(processNumber).processName + " (" + runningAppProcessInfo.get(processNumber).pid + ")");
            }

            java.lang.Process suProcess = null;
            suProcess = Runtime.getRuntime().exec("am force-stop " + runningAppProcessInfo.get(processNumber).processName);
            DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
            os.writeBytes("" + "\n");
            os.flush();
            os.close();
        } catch (Throwable e) {
            XposedUtils.log("Failed to kill: " + e.toString());
        }*/
    }

    private static void toggleRotation(Context p1) {
        boolean isEnabled = false;
        isEnabled = Settings.System.getInt(
                p1.getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, 0) == 1;

        Settings.System.putInt(
                p1.getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, isEnabled ? 0 : 1);
    }

    private static void toggleShowTouches(Context p1, int showTouches) {
        try {
            if (showTouches == -1) {
                showTouches = Settings.System.getInt(p1.getContentResolver(),
                        ScreenRecordingService.SETTING_SHOW_TOUCHES) == 1 ? 0 : 1;
            }
            Settings.System.putInt(p1.getContentResolver(),
                    ScreenRecordingService.SETTING_SHOW_TOUCHES, showTouches);
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }

    private void toggleData(Context p1, boolean enable) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) p1.getSystemService(Context.TELEPHONY_SERVICE);
            //telephonyManager.setDataEnabled(enable);
            Method setEnabledMethod = telephonyManager.getClass().getDeclaredMethod("setDataEnabled", boolean.class);
            if (setEnabledMethod != null) {
                setEnabledMethod.invoke(telephonyManager, enable);
            }
        } catch (Throwable t) {
            XposedUtils.log("Error toggling data: " + t.toString());
        }
    }

    private boolean isDataActive(Context p1) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) p1.getSystemService(Context.TELEPHONY_SERVICE);
            //return telephonyManager.getDataEnabled();
            Method getEnabledMethod = telephonyManager.getClass().getDeclaredMethod("getDataEnabled");
            if (getEnabledMethod != null) {
                return (boolean) getEnabledMethod.invoke(telephonyManager);
            }
        } catch (Throwable t) {
            XposedUtils.log("Error getting data state: " + t.toString());
            return false;
        }
        return false;
    }

    private void fakePowerOff(final Context context) {

        boolean z;
        final int mfpoTime = preferences.getInt(PreferenceNames.pFakeOffTime, 2);
        final int screenOffStrategy = preferences.getInt(PreferenceNames.pFakeOffStrategy, 0);
        final int mOrigTimeOut = preferences.getInt("pref_orig_timeout", -1);
        boolean mOrigTimeOutAvailable;
        if (mOrigTimeOut >= 15000) {
            z = true;
        } else {
            z = false;
        }
        mOrigTimeOutAvailable = z;

        Handler handler = new Handler();
        if (mOrigTimeOutAvailable) {
            handler.postDelayed(new Runnable() {
                public void run() {
                    if (!preferences.getBoolean(PreferenceNames.pFakeOff, true) || screenOffStrategy != 0) {
                        return;
                    }
                    if (Settings.System.putInt(context.getContentResolver(), "screen_off_timeout", 1000)) {
                        Log.d("NPM", "[FakePowerOffAction] successfully set timeout to 1000");
                    } else {
                        Log.e("NPM", "[FakePowerOffAction] failed to set timeout");
                    }
                }
            }, (long) ((mfpoTime * 1000) - 2000));
            handler.postDelayed(new Runnable() {
                public void run() {
                    if (!preferences.getBoolean("_pref_fpo_screen_off", true) || screenOffStrategy != 0) {
                        return;
                    }
                    if (Settings.System.putInt(context.getContentResolver(), "screen_off_timeout", mOrigTimeOut)) {
                        Log.d("NPM", "[FakePowerOffAction] successfully reset timeout to" + mOrigTimeOut);
                    } else {
                        Log.e("NPM", "[FakePowerOffAction] failed to reset timeout");
                    }
                }
            }, (long) ((mfpoTime * 1000) + 5000));
        } else {
            Log.w("NPM", "[FakePowerOffAction] original time out NOT available, don't mess with it");
        }
        int mSimPowerBtnTime = preferences.getInt(PreferenceNames.pFakeOffSimPowerButtonTime, 0);
        if (screenOffStrategy == 1 || screenOffStrategy == 2) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (screenOffStrategy == 1 || screenOffStrategy == 2) {
                        try {
                            new DataOutputStream(Runtime.getRuntime().exec("su").getOutputStream()).writeBytes("input keyevent 26\n");
                        } catch (Throwable e) {
                            Log.e("NPM", "[FakePowerOffAction]", e);
                        }
                    }
                    if (screenOffStrategy == 2) {
                        Intent intent = new Intent();
                        intent.setAction("de.NeonSoft.neopowermenu.FakePowerOffBroadcastSticky");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.sendBroadcast(intent);
                        Intent intent2 = new Intent("de.NeonSoft.neopowermenu.FakePowerOffBroadcastSticky");
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent2.putExtra("isPresent", true);
                        context.sendStickyBroadcast(intent2);
                        Settings.System.putInt(context.getContentResolver(), "screen_brightness", 1);
                        try {
                            new DataOutputStream(Runtime.getRuntime().exec("su").getOutputStream()).writeBytes("echo 0 > /sys/class/graphics/fb0/blank\necho 1 > /sys/class/graphics/fb0/blank\n");
                        } catch (Throwable e2) {
                            Log.e("NPM", "[FakePowerOffAction]", e2);
                        }
                    }
                }
            }, (long) ((mfpoTime * 1000) - mSimPowerBtnTime));
        }
        if (screenOffStrategy == 3) {
            handler.postDelayed(new Runnable() {
                public void run() {
                    XposedHelpers.callMethod((PowerManager) context.getSystemService(Context.POWER_SERVICE), "goToSleep", new Object[]{Long.valueOf(SystemClock.uptimeMillis())});
                }
            }, (long) (mfpoTime * 1000));
        }
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent();
                intent.setAction("de.NeonSoft.neopowermenu.intent.FAKE_POWER_OFF");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.sendBroadcast(intent);
                if (preferences.getBoolean(PreferenceNames.pFakeOffSilent, true)) {
                    try {
                        ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).setRingerMode(0);
                        Log.d("NPM", "[FakePowerOffAction] Set device to silent");
                    } catch (Throwable throwable) {
                        Log.e("NPM", "[FakePowerOffAction] Failed to set device to silent mode.", throwable);
                    }
                }
                if (preferences.getBoolean(PreferenceNames.pFakeOffLed, true)) {
                    boolean success = Settings.System.putInt(context.getContentResolver(), "notification_pulse", 0);
                    if (Build.MANUFACTURER.toLowerCase().startsWith("lg")) {
                        Log.d("NPM", "[FakePowerOffAction] is lg phone");
                        if (Settings.System.putInt(context.getContentResolver(), "lge_notification_pulse", 0)) {
                            Log.d("NPM", "[FakePowerOffAction] successfully turn off LED notif for LG device");
                        } else {
                            Log.e("NPM", "[FakePowerOffAction] failed to turn off LED notif for LG device");
                        }
                    }
                    if (success) {
                        Log.e("NPM", "[FakePowerOffAction] successfully turn off LED notif");
                    } else {
                        Log.e("NPM", "[FakePowerOffAction] failed to turn off LED notif");
                    }
                }
                if (preferences.getBoolean(PreferenceNames.pFakeOffInSOD, false)) {
                    Log.d("NPM", "[FakePowerOffAction] setting SOD flag to true");
                    XSOD.isInSOD = true;
                    Log.d("NPM", "[FakePowerOffAction] preventing screen on");
                    XSOD.preventScreenOn();
                }
            }
        }, (long) ((mfpoTime * 1000) + 1000));
    }

    private boolean showDialog() {
        if (mContext == null) {
            XposedUtils.log("Failed to show Power Menu: mContext is null");
            return false;
        }

        try {
            //boolean mKeyguardShowing = XposedHelpers.getObjectField(mObjectHolder, "mKeyguardShowing");
            Context context = mContext.createPackageContext(PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);
            Intent intent = new Intent(context, XposedMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            //intent.putExtra("mKeyguardShowing", mKeyguardShowing);
            context.startActivity(intent);
        } catch (Exception e) {
            XposedUtils.log("Failed to show Power Menu (" + PACKAGE_NAME + "): " + e);
            return false;
        }
        return true;
    }

    private static final Runnable mScreenshotTimeout = new Runnable() {
        @Override
        public void run() {
            synchronized (mScreenshotLock) {
                if (mScreenshotConnection != null) {
                    mContext.unbindService(mScreenshotConnection);
                    XposedUtils.log("Screenshot connection timed out, closing.");
                    mScreenshotConnection = null;
                }
            }
        }
    };

}


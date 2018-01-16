package de.NeonSoft.neopowermenu.xposed;

import android.os.Build.VERSION;

import de.NeonSoft.neopowermenu.helpers.PreferenceNames;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XposedHelpers.ClassNotFoundError;

public class XSOD {
    public static boolean isInSOD = false;
    private static boolean isSODEnabled = false;
    private static Class<?> mPowerManagerServiceClass = null;
    private static Class<?> mPowerManagerServiceOldClass = null;
    private static Object mPowerManagerServiceOldClassInstance = null;
    private static Object screenOnBlocker;

    public static void init(XSharedPreferences pref, ClassLoader classLoader) {
        isSODEnabled = pref.getBoolean(PreferenceNames.pFakeOffInSOD, false);
        if (isSODEnabled) {
            XposedUtils.log("[XOSD] initializing");
            if (VERSION.SDK_INT < 17 || VERSION.SDK_INT > 19) {
                try {
                    mPowerManagerServiceOldClass = XposedHelpers.findClass("com.android.server.PowerManagerService", classLoader);
                } catch (ClassNotFoundError e) {
                    XposedUtils.log("[XOSD] Err: com.android.server.PowerManagerService not found!!!");
                }
            } else {
                try {
                    mPowerManagerServiceClass = XposedHelpers.findClass("com.android.server.power.PowerManagerService", classLoader);
                } catch (ClassNotFoundError e2) {
                    XposedUtils.log("[XOSD] Err: com.android.server.power.PowerManagerService not found!!!");
                }
            }
            if (mPowerManagerServiceClass != null) {
                try {
                    XposedBridge.hookAllConstructors(mPowerManagerServiceClass, new XC_MethodHook() {
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            XposedUtils.log("[XOSD] Getting an instance of ScreenOnBlocker");
                            XSOD.screenOnBlocker = XposedHelpers.getObjectField(param.thisObject, "mScreenOnBlocker");
                        }
                    });
                } catch (Throwable e3) {
                    XposedUtils.log("[XOSD] " + e3);
                }
            }
            if (mPowerManagerServiceOldClass != null) {
                try {
                    XposedBridge.hookAllConstructors(mPowerManagerServiceOldClass, new XC_MethodHook() {
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            XposedUtils.log("[XOSD] Getting an instance of PowerManagerService (pre-4.2)");
                            XSOD.mPowerManagerServiceOldClassInstance = param.thisObject;
                        }
                    });
                    XposedHelpers.findAndHookMethod(mPowerManagerServiceOldClass, "forceReenableScreen", new Object[]{new XC_MethodHook() {
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (XSOD.isInSOD) {
                                XposedUtils.log("[XOSD] prevent forceReenableScreen");
                                param.setResult(null);
                            }
                        }
                    }});
                    return;
                } catch (Throwable e322) {
                    XposedUtils.log("[XOSD] " + e322);
                    return;
                }
            }
            return;
        }
        XposedUtils.log("[XOSD] SOD is not enabled");
    }

    public static void preventScreenOn() {
        try {
            if (screenOnBlocker != null) {
                XposedUtils.log("[XOSD] acquiring a screenOnBlocker");
                XposedHelpers.callMethod(screenOnBlocker, "acquire", new Object[0]);
            } else if (mPowerManagerServiceOldClassInstance != null) {
                XposedUtils.log("[XOSD] calling preventScreenOn");
                XposedHelpers.callMethod(mPowerManagerServiceOldClassInstance, "preventScreenOn", new Object[]{Boolean.valueOf(true)});
            }
        } catch (Throwable t) {
            XposedUtils.log("[XOSD] " + t);
        }
    }
}

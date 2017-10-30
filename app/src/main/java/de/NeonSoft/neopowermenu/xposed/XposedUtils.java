package de.NeonSoft.neopowermenu.xposed;

import android.content.*;
import android.content.pm.*;
import android.content.res.*;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;
import de.NeonSoft.neopowermenu.*;
import de.robv.android.xposed.*;
import java.util.*;

import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class XposedUtils
{
		private static Boolean mHasFlash;
		private static Boolean mIsExynosDevice;
		
		public static void log(String message) {
				Log.i("NPM:xposed",message);
				try {
						XposedBridge.log("[NeoPowerMenu] "+message);
				} catch (Throwable t) {}
		}

    public static Point getLocationInView(View src, View target)
		{
        final int[] l0 = new int[2];
        src.getLocationOnScreen(l0);

        final int[] l1 = new int[2];
        target.getLocationOnScreen(l1);

        l1[0] = l1[0] - l0[0] + target.getWidth() / 2;
        l1[1] = l1[1] - l0[1] + target.getHeight() / 2;

        return new Point(l1[0], l1[1]);
    }

    public static boolean hasFlash(Context con) {
        if (mHasFlash != null) return mHasFlash;

        try {
            PackageManager pm = con.getPackageManager();
            mHasFlash = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
            return mHasFlash;
        } catch (Throwable t) {
            mHasFlash = null;
            return false;
        }
    }
		
    public static boolean isExynosDevice() {
        if (mIsExynosDevice != null) return mIsExynosDevice;

        mIsExynosDevice = Build.HARDWARE.toLowerCase(Locale.US).contains("smdk");
        return mIsExynosDevice;
    }
		
		public static void doShutdown(Context context, int mode) {
				Intent powerIntent = new Intent();
				switch (mode) {
						case 0:
								powerIntent.setAction(XposedMain.NPM_ACTION_BROADCAST_SHUTDOWN);
								break;
						default:
								break;
				}
				if(powerIntent.getAction()!=null&&!powerIntent.getAction().equalsIgnoreCase("")) {
						context.sendBroadcast(powerIntent);
				}
		}
		
		public static void doReboot(Context context, int mode) {
				Intent powerIntent = new Intent();
				switch (mode) {
						case 0:
								powerIntent.setAction(XposedMain.NPM_ACTION_BROADCAST_REBOOT);
								break;
						case 1:
								performSoftReboot();
								//powerIntent.setAction("de.NeonSoft.neopowermenu.action.SoftReboot");
								break;
						case 2:
								powerIntent.setAction(XposedMain.NPM_ACTION_BROADCAST_REBOOTRECOVERY);
								break;
						case 3:
								powerIntent.setAction(XposedMain.NPM_ACTION_BROADCAST_REBOOTBOOTLOADER);
								break;
						default:
								break;
						
				}
				if(powerIntent.getAction()!=null&&!powerIntent.getAction().equalsIgnoreCase("")) {
						context.sendBroadcast(powerIntent);
				}
		}
		
		private static void replaceRecoveryMessage(Context context) {
				try {
						Resources res = XResources.getSystem();
						XResources.setSystemWideReplacement(
								res.getIdentifier("reboot_to_reset_title", "string", "android"),
								context.getString(R.string.powerMenuBottom_Recovery));
				} catch (Throwable t) { /* ignore */ }
		}

    public static boolean isParanoidRom() {
        return (Build.DISPLAY != null && Build.DISPLAY.startsWith("pa_"));
    }


    public static void performSoftReboot() {
    }

    public static class SystemProp extends XposedUtils {

        private SystemProp() {

        }

        // Get the value for the given key
        // @param key: key to lookup
        // @return null if the key isn't found
        public static String get(String key) {
            String ret;

            try {
                Class<?> classSystemProperties = findClass("android.os.SystemProperties", null);
                ret = (String) callStaticMethod(classSystemProperties, "get", key);
            } catch (Throwable t) {
                log("SystemProp.get failed: " + t.getMessage());
                ret = null;
            }
            return ret;
        }

        // Get the value for the given key
        // @param key: key to lookup
        // @param def: default value to return
        // @return if the key isn't found, return def if it isn't null, or an empty string otherwise
        public static String get(String key, String def) {
            String ret = def;

            try {
                Class<?> classSystemProperties = findClass("android.os.SystemProperties", null);
                ret = (String) callStaticMethod(classSystemProperties, "get", key, def);
            } catch (Throwable t) {
                log("SystemProp.get failed: " + t.getMessage());
                ret = def;
            }
            return ret;
        }

        // Get the value for the given key, and return as an integer
        // @param key: key to lookup
        // @param def: default value to return
        // @return the key parsed as an integer, or def if the key isn't found or cannot be parsed
        public static Integer getInt(String key, Integer def) {
            Integer ret = def;

            try {
                Class<?> classSystemProperties = findClass("android.os.SystemProperties", null);
                ret = (Integer) callStaticMethod(classSystemProperties, "getInt", key, def);
            } catch (Throwable t) {
                log("SystemProp.getInt failed: " + t.getMessage());
                ret = def;
            }
            return ret;
        }

        // Get the value for the given key, and return as a long
        // @param key: key to lookup
        // @param def: default value to return
        // @return the key parsed as a long, or def if the key isn't found or cannot be parsed
        public static Long getLong(String key, Long def) {
            Long ret = def;

            try {
                Class<?> classSystemProperties = findClass("android.os.SystemProperties", null);
                ret = (Long) callStaticMethod(classSystemProperties, "getLong", key, def);
            } catch (Throwable t) {
                log("SystemProp.getLong failed: " + t.getMessage());
                ret = def;
            }
            return ret;
        }

        // Get the value (case insensitive) for the given key, returned as a boolean
        // Values 'n', 'no', '0', 'false' or 'off' are considered false
        // Values 'y', 'yes', '1', 'true' or 'on' are considered true
        // If the key does not exist, or has any other value, then the default result is returned
        // @param key: key to lookup
        // @param def: default value to return
        // @return the key parsed as a boolean, or def if the key isn't found or cannot be parsed
        public static Boolean getBoolean(String key, boolean def) {
            Boolean ret = def;

            try {
                Class<?> classSystemProperties = findClass("android.os.SystemProperties", null);
                ret = (Boolean) callStaticMethod(classSystemProperties, "getBoolean", key, def);
            } catch (Throwable t) {
                log("SystemProp.getBoolean failed: " + t.getMessage());
                ret = def;
            }
            return ret;
        }

        // Set the value for the given key
        public static void set(String key, String val) {
            try{
                Class<?> classSystemProperties = findClass("android.os.SystemProperties", null);
                callStaticMethod(classSystemProperties, "set", key, val);
            } catch (Throwable t) {
                log("SystemProp.set failed: " + t.getMessage());
            }
        }
    }
}

package de.NeonSoft.neopowermenu.helpers;

import android.annotation.SuppressLint;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.*;
import android.util.*;
import android.view.*;
import android.view.animation.*;
import android.widget.ImageView;
import android.widget.NumberPicker;

import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.Preferences.*;
import de.NeonSoft.neopowermenu.xposed.XposedUtils;

import java.io.*;
import java.lang.reflect.*;
import java.security.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import net.lingala.zip4j.core.*;
import net.lingala.zip4j.model.*;
import net.lingala.zip4j.util.*;

//import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class helper {

    private static boolean zipLogging = true;

    @SuppressWarnings("deprecation")
    public static void setBackground(View view, Drawable d) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(d);
        } else {
            view.setBackgroundDrawable(d);
        }
    }

    public static boolean isAppInstalled(Context context, String appUri) {
        try {
            PackageManager pm = context.getPackageManager();
            pm.getPackageInfo(appUri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static File getFilesDir(Context ctx) {
        if (XposedUtils.USE_DEVICE_PROTECTED_STORAGE()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return ctx.isDeviceProtectedStorage() ?
                        ctx.getFilesDir() : ctx.createDeviceProtectedStorageContext().getFilesDir();
            }
        }
        return ctx.getFilesDir();
    }

    public static void postInvalidateOnAnimation(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.postInvalidateOnAnimation();
        } else {
            view.invalidate();
        }
    }

    public static void setThreadPrio(int prio) {
        android.os.Process.setThreadPriority(prio);
    }

    public static int ModuleState() {
        int active = -1;
        return active;
    }

    public static boolean copyPreferences(Context context, SharedPreferences from, SharedPreferences to, boolean logging) {
        try {
            if (logging) Log.i("NPM", "Copying preferences...");
            Map<String, ?> oldPrefsAll = from.getAll();
            if (!oldPrefsAll.isEmpty()) {
                Object[] keys = oldPrefsAll.keySet().toArray();
                Object[] values = oldPrefsAll.values().toArray();
                String designSpace = "";
                for (int x = 0; x < (String.format(Locale.getDefault(), "%03d/%03d", oldPrefsAll.size(), oldPrefsAll.size())).length(); x++) {
                    designSpace = designSpace + " ";
                }
                for (int i = 0; i < oldPrefsAll.size(); i++) {
                    boolean unknown = false;
                    if (logging)
                        Log.i("NPM", String.format("%03d/%03d", (i + 1), oldPrefsAll.size()) + " | " + keys[i]);
                    if (values[i].getClass().equals(String.class)) {
                        if (logging) Log.i("NPM", designSpace + " | Writing String...");
                        to.edit().putString((String) keys[i], (String) values[i]).apply();
                    } else if (values[i].getClass().equals(Integer.class)) {
                        if (logging) Log.i("NPM", designSpace + " | Writing Integer...");
                        to.edit().putInt((String) keys[i], (int) values[i]).apply();
                    } else if (values[i].getClass().equals(Boolean.class)) {
                        if (logging) Log.i("NPM", designSpace + " | Writing Boolean...");
                        to.edit().putBoolean((String) keys[i], (boolean) values[i]).apply();
                    } else {
                        unknown = true;
                        if (logging)
                            Log.i("NPM", designSpace + " | Unknown type... (" + values[i].getClass() + ")");
                    }
                    if (!unknown) {
                        if (logging) Log.i("NPM", designSpace + " \\_Converted.");
                    } else {
                        if (logging) Log.i("NPM", designSpace + " \\_Failed.");
                    }
                }
            }
        } catch (Throwable t) {
            return false;
        }
        return true;
    }

    public static long splitMilliseconds(long input, String request) {
        long OutputMiliSeconds = input;
        long OutputSeconds = TimeUnit.MILLISECONDS.toSeconds(input);
        input -= (OutputSeconds * 1000);
        long OutputMinutes = TimeUnit.MILLISECONDS.toMinutes(input);
        input -= (OutputMinutes * (1000 * 60));
        long OutputHours = TimeUnit.MILLISECONDS.toHours(input);
        for (int i = 0; i < OutputMiliSeconds; i += 1000) {
            if (OutputSeconds > 59) {
                OutputSeconds -= 60;
                OutputMinutes++;
            } else if (OutputMinutes > 59) {
                OutputMinutes -= 60;
                OutputHours++;
            }
        }
        return (request.equalsIgnoreCase("h") ? OutputHours : (request.equalsIgnoreCase("m") ? OutputMinutes : (request.equalsIgnoreCase("s") ? OutputSeconds : OutputMiliSeconds)));
    }

    public static String getTimeString(Context context, long InputMilliSeconds, int visibleText) {
        long OutputMiliSeconds = InputMilliSeconds;
        long OutputSeconds = TimeUnit.MILLISECONDS.toSeconds(InputMilliSeconds);
        InputMilliSeconds -= (OutputSeconds * 1000);
        long OutputMinutes = TimeUnit.MILLISECONDS.toMinutes(InputMilliSeconds);
        InputMilliSeconds -= (OutputMinutes * (1000 * 60));
        long OutputHours = TimeUnit.MILLISECONDS.toHours(InputMilliSeconds);
        for (int i = 0; i < OutputMiliSeconds; i += 1000) {
            if (OutputSeconds > 59) {
                OutputSeconds -= 60;
                OutputMinutes++;
            } else if (OutputMinutes > 59) {
                OutputMinutes -= 60;
                OutputHours++;
            }
        }
        String duration_string = "";
        String hours = "", minutes = "", seconds = "", milliseconds = "";
        hours = String.format(Locale.getDefault(), "%02d", OutputHours);
        minutes = String.format(Locale.getDefault(), "%02d", OutputMinutes);
        seconds = String.format(Locale.getDefault(), "%02d", OutputSeconds);
        if (seconds.equalsIgnoreCase("00") && minutes.equalsIgnoreCase("00") && hours.equalsIgnoreCase("00")) {
            milliseconds = String.format(Locale.getDefault(), "%03d", InputMilliSeconds);
        }

        if (milliseconds.isEmpty()) {
            duration_string = hours + ":";
            duration_string += minutes + ":";
            duration_string += seconds;
        } else {
            duration_string = hours + ":";
            duration_string += minutes + ":";
            duration_string += seconds + ":";
            duration_string += milliseconds;
        }
        //duration_string = hours + (!hours.isEmpty() ? (visibleText > 0 ? context.getString(R.string.advancedPrefs_Hours).substring(0, visibleText) + " " : visibleText == -1 ? context.getString(R.string.advancedPrefs_Hours) : !minutes.isEmpty() ? ":" : "") : "");
        //duration_string += minutes + (!minutes.isEmpty() ? (visibleText > 0 ? context.getString(R.string.advancedPrefs_Minutes).substring(0, visibleText) + " " : visibleText == -1 ? context.getString(R.string.advancedPrefs_Minutes) : !seconds.isEmpty() ? ":" : "") : "");
        //duration_string += seconds + (!seconds.isEmpty() ? (visibleText > 0 ? context.getString(R.string.advancedPrefs_Seconds).substring(0, visibleText) + " " : visibleText == -1 ? context.getString(R.string.advancedPrefs_Seconds) : !milliseconds.isEmpty() ? ":" : "") : "");
        //duration_string += milliseconds + (!milliseconds.isEmpty() ? (visibleText > 0 ? context.getString(R.string.advancedPrefs_MilliSeconds).substring(0, visibleText) : visibleText == -1 ? context.getString(R.string.advancedPrefs_MilliSeconds) : "") : "");
        return duration_string;
    }

    public static String getSizeString(long bytes, boolean si) {
        if (bytes <= 0) return "0b";
        int unit = si ? 1024 : 1000;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format(Locale.getDefault(), "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String md5Crypto(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(input.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2) {
                    h = "0" + h;
                }
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "null";
    }

    public static boolean isValidEmail(String input) {
        return !TextUtils.isEmpty(input) && android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches();
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static boolean isDeviceHorizontal(Context mContext) {
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        boolean mHorizontal = false;

        try {
            if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.FROYO) {
                int rotation = display.getOrientation();
                mHorizontal = !(rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180);
            } else {
                int rotation = display.getRotation();
                mHorizontal = !(rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180);
            }
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        }

        return mHorizontal;
    }

    public static Point getNavigationBarSize(Context mContext) {
        Point appUsableScreenSize = getAppUsableScreenSize(mContext);
        Point realScreenSize = getRealScreenSize(mContext);

        // at side
        if (appUsableScreenSize.x < realScreenSize.x) {
            return new Point(realScreenSize.x - appUsableScreenSize.x, 0);
        }

        // at bottom
        if (appUsableScreenSize.y < realScreenSize.y) {
            return new Point(0, realScreenSize.y - appUsableScreenSize.y);
        }

        // none
        return new Point(0, 0);
    }
    public static boolean isNavigationBarOnLeft(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int rotation =  display.getRotation();

        String aVerReleaseStr = Build.VERSION.RELEASE;
        int dotInd = aVerReleaseStr.indexOf(".");
        if (dotInd >= 0) {
            aVerReleaseStr = aVerReleaseStr.replaceAll("\\.", "");
            aVerReleaseStr = new StringBuffer(aVerReleaseStr).insert(dotInd, ".").toString();
        }

        float androidVer = Float.parseFloat(aVerReleaseStr);
        return rotation == 3 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    public static Point getAppUsableScreenSize(Context mContext) {
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = (windowManager.getDefaultDisplay());
        Point size = new Point();
        display.getSize(size);

        return size;
    }

    public static Point getRealScreenSize(Context mContext) {
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size);
        } else if (Build.VERSION.SDK_INT >= 14) {
            try {
                size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (Throwable t) {
            }
        }

        return size;
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        if (context == null) return dp;
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        if (context == null) return px;
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }


    public static void zipLogging(boolean enabled) {
        zipLogging = enabled;
        //Log.d("NPM:zip","Logging "+(zipLogging ? "enabled" : "disabled")+"...");
    }

    public static String zipFile(String fileToZip, String zipFile, String password) {
        try {
            if (!new File(fileToZip).exists()) {
                throw new Throwable("File not found: " + fileToZip);
            }
            //Log.i("NPM:zipFile","Zipping file: "+fileToZip);
            ZipFile zip = new ZipFile(zipFile);
            ZipParameters params = new ZipParameters();

            params.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            params.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            params.setIncludeRootFolder(false);

            if (password != null && !password.isEmpty()) {
                params.setPassword(password);
            }

            zip.addFile(new File(fileToZip), params);
        } catch (Throwable t) {
            if (zipLogging) Log.e("NPM", "Failed to zip: " + t.toString());
            return t.toString();
        }
        return null;
    }

    public static String zipAll(String toZipFolder, String zipFile, String password) {
        try {
            if (!new File(toZipFolder).exists()) {
                throw new Throwable("Folder not found: " + toZipFolder);
            }
            //Log.i("NPM:zipAll","Zipping folder: "+toZipFolder);
            ZipFile zip = new ZipFile(zipFile);
            ZipParameters params = new ZipParameters();

            params.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            params.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            params.setIncludeRootFolder(false);

            if (password != null && !password.isEmpty()) {
                params.setPassword(password);
            }

            zip.addFolder(toZipFolder, params);
        } catch (Throwable t) {
            if (zipLogging) Log.e("NPM", "Failed to zip: " + t.toString());
            return t.toString();
        }
        return null;
    }

    public static String unzipFile(String zipFile, String outputFolder, String fileToUnzip, String password) {
        try {
            if (!new File(zipFile).exists()) {
                throw new Throwable("File not found: " + zipFile);
            }
            //Log.i("NPM:unzipFile","Unzipping from: "+zipFile);
            ZipFile zip = new ZipFile(zipFile);
            UnzipParameters params = new UnzipParameters();

            if (password != null && !password.isEmpty()) {
                zip.setPassword(password);
            }

            zip.extractFile(fileToUnzip, outputFolder, params, fileToUnzip);
        } catch (Throwable t) {
            if (zipLogging) Log.e("NPM", "Failed to unzip: " + t.toString());
            return t.toString();
        }
        return null;
    }

    public static String unzipAll(String zipFile, String outputFolder, String password) {
        try {
            if (!new File(zipFile).exists()) {
                throw new Throwable("File not found: " + zipFile);
            }
            //Log.i("NPM:unzipAll","Unzipping from: "+zipFile);
            ZipFile zip = new ZipFile(zipFile);

            if (password != null && !password.isEmpty()) {
                zip.setPassword(password);
            }

            zip.extractAll(outputFolder);
        } catch (Throwable t) {
            if (zipLogging) Log.e("NPM", "Failed to unzip: " + t.toString());
            return t.toString();
        }
        return null;
    }

    public static boolean isValidZip(String zipFile, String password) {
        try {
            if (!new File(zipFile).exists()) {
                throw new Throwable("File not found: " + zipFile);
            }
            //Log.i("NPM:isValidZip","Checking for: "+zipFile);
            ZipFile zip = new ZipFile(zipFile);

            if (password != null && !password.isEmpty()) {
                zip.setPassword(password);
            }

            return zip.isValidZipFile();

        } catch (Throwable t) {
            if (zipLogging) Log.e("NPM", "Failed to validate: " + t.toString());
            return false;
        }
    }

    public static String removeFromZip(String zipFile, String fileToRemove, String password) {
        try {
            if (!new File(zipFile).exists()) {
                throw new Throwable("File not found: " + zipFile);
            }
            //Log.i("NPM:removeFromZip","Removing from: "+zipFile);
            ZipFile zip = new ZipFile(zipFile);

            if (password != null && !password.isEmpty()) {
                zip.setPassword(password);
            }

            zip.removeFile(fileToRemove);

        } catch (Throwable t) {
            if (zipLogging) Log.e("NPM", "Failed to remove: " + t.toString());
            return t.toString();
        }
        return null;
    }

    public static boolean copyFile(String inputPath, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File(outputPath.replace("/" + outputPath.split("/")[outputPath.split("/").length - 1], ""));
            if (!dir.exists()) {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputPath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        } catch (FileNotFoundException fnfe1) {
            Log.e("NPM", fnfe1.getMessage());
            return false;
        } catch (Exception e) {
            Log.e("NPM", e.getMessage());
            return false;
        } finally {
            return true;
        }

    }

    public static Object[] getDisplaySize(Context context, boolean alwaysPortrait) {

        int width = 0;
        int height = 0;
        boolean mHorizontal = false;

        DisplayMetrics metrics = new DisplayMetrics();
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Method mGetRawH = null, mGetRawW = null;

        try {
            // For JellyBean 4.2 (API 17) and onward
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                display.getRealMetrics(metrics);

                width = metrics.widthPixels;
                height = metrics.heightPixels;
            } else {
                mGetRawH = Display.class.getMethod("getRawHeight");
                mGetRawW = Display.class.getMethod("getRawWidth");

                try {
                    width = (Integer) mGetRawW.invoke(display);
                    height = (Integer) mGetRawH.invoke(display);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
        }
        try {
            if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.FROYO) {
                int rotation = display.getOrientation();
                if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
                    mHorizontal = false;
                } else {
                    mHorizontal = true;
                }
            } else {
                int rotation = display.getRotation();
                if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
                    mHorizontal = false;
                } else {
                    mHorizontal = true;
                }
            }
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        }
        if (alwaysPortrait && mHorizontal) {
            int tmp = width;
            width = height;
            height = tmp;
        }
        //Log.i("mHorizontal=",""+mHorizontal);
        return new Object[]{width, height, mHorizontal};
    }

    public static Animation getAnimation(final Context context, SharedPreferences prefs, int forItem, boolean forOut) {
        return getAnimation(context, prefs, forItem, forOut, null);
    }
    public static Animation getAnimation(final Context context, SharedPreferences prefs, int forItem, boolean forOut, final View view) {
        int speed;
        speed = 700;
        int speedId = prefs.getInt(PreferencesAnimationsFragment.names[forItem + PreferencesAnimationsFragment.anim_Speed][1].toString(), PreferencesAnimationsFragment.defaultTypes[forItem + PreferencesAnimationsFragment.anim_Speed]);
        if (speedId == 0) {
            speed = 100;
        } else if (speedId == 1) {
            speed = 300;
        } else if (speedId == 2) {
            speed = 500;
        } else if (speedId == 3) {
            speed = 700;
        } else if (speedId == 4) {
            speed = 900;
        } else if (speedId == 5) {
            speed = 1100;
        } else if (speedId == 6) {
            speed = 1300;
        } else {
            speed = speedId;
        }
        Animation anim = null, anim2 = null;
        int animId = prefs.getInt(PreferencesAnimationsFragment.names[forItem + PreferencesAnimationsFragment.anim_Type][1].toString(), PreferencesAnimationsFragment.defaultTypes[forItem + PreferencesAnimationsFragment.anim_Type]);
        if (PreferencesAnimationsFragment.names[forItem][1].toString().contains("progressbar")) {
            if (animId == 0) {
                anim = AnimationUtils.loadAnimation(context, R.anim.rotate_clockwise);
            } else if (animId == 1) {
                anim = AnimationUtils.loadAnimation(context, R.anim.rotate_counter_clockwise);
            } else if (animId == 2) {
                anim = AnimationUtils.loadAnimation(context, R.anim.fade_out);
                anim2 = AnimationUtils.loadAnimation(context, R.anim.fade_in);
            } else if (animId == 3) {
                anim = AnimationUtils.loadAnimation(context, R.anim.scale_in_up);
                anim2 = AnimationUtils.loadAnimation(context, R.anim.scale_out_down);
            } else if (animId == 4) {
                anim = AnimationUtils.loadAnimation(context, R.anim.scale_in_down);
                anim2 = AnimationUtils.loadAnimation(context, R.anim.scale_out_up);
            } else {
                anim = AnimationUtils.loadAnimation(context, R.anim.fade_out);
                anim2 = AnimationUtils.loadAnimation(context, R.anim.fade_in);
            }
            speed += 400;
        } else {
            if (!forOut) {
                if (animId == 0) {
                    anim = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                } else if (animId == 1) {
                    anim = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                } else if (animId == 2) {
                    anim = AnimationUtils.loadAnimation(context, R.anim.anim_slide_in_bottom);
                } else if (animId == 3) {
                    anim = AnimationUtils.loadAnimation(context, R.anim.anim_slide_in_right);
                } else if (animId == 4) {
                    anim = AnimationUtils.loadAnimation(context, R.anim.anim_slide_in_left);
                } else if (animId == 5) {
                    anim = AnimationUtils.loadAnimation(context, R.anim.anim_slide_in_top);
                } else if (animId == 6) {
                    anim = AnimationUtils.loadAnimation(context, R.anim.scale_in_up);
                } else if (animId == 7) {
                    anim = AnimationUtils.loadAnimation(context, R.anim.scale_in_down);
                } else if (animId == 8) {
                    anim = AnimationUtils.loadAnimation(context, R.anim.anim_flip_vertical_in);
                } else if (animId == 9) {
                    anim = AnimationUtils.loadAnimation(context, R.anim.anim_flip_horizontal_in);
                } else {
                    anim = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                }
            } else {
                if (animId == 0) {
                    anim = AnimationUtils.loadAnimation(context, R.anim.fade_out);
                } else if (animId == 1) {
                    anim = AnimationUtils.loadAnimation(context, R.anim.fade_out);
                } else if (animId == 2) {
                    anim = AnimationUtils.loadAnimation(context, R.anim.anim_slide_out_bottom);
                } else if (animId == 3) {
                    anim = AnimationUtils.loadAnimation(context, R.anim.anim_slide_out_right);
                } else if (animId == 4) {
                    anim = AnimationUtils.loadAnimation(context, R.anim.anim_slide_out_left);
                } else if (animId == 5) {
                    anim = AnimationUtils.loadAnimation(context, R.anim.anim_slide_out_top);
                } else if (animId == 6) {
                    anim = AnimationUtils.loadAnimation(context, R.anim.scale_out_up);
                } else if (animId == 7) {
                    anim = AnimationUtils.loadAnimation(context, R.anim.scale_out_down);
                } else if (animId == 8) {
                    anim = AnimationUtils.loadAnimation(context, R.anim.anim_flip_vertical_out);
                } else if (animId == 9) {
                    anim = AnimationUtils.loadAnimation(context, R.anim.anim_flip_horizontal_out);
                } else {
                    anim = AnimationUtils.loadAnimation(context, R.anim.fade_out);
                }
            }
        }
        android.view.animation.Interpolator interpolator = null;
        int interpolatorId = prefs.getInt(PreferencesAnimationsFragment.names[forItem + PreferencesAnimationsFragment.anim_Interpolator][1].toString(), PreferencesAnimationsFragment.defaultTypes[forItem + PreferencesAnimationsFragment.anim_Interpolator]);
        if (interpolatorId == 0) {
            interpolator = AnimationUtils.loadInterpolator(context, android.R.interpolator.linear);
        } else if (interpolatorId == 1) {
            interpolator = AnimationUtils.loadInterpolator(context, android.R.interpolator.accelerate_quint);
        } else if (interpolatorId == 2) {
            interpolator = AnimationUtils.loadInterpolator(context, android.R.interpolator.decelerate_quint);
        } else if (interpolatorId == 3) {
            interpolator = new android.view.animation.Interpolator() {
                @Override
                public float getInterpolation(float input) {
                    float x = input * 2.0f;
                    if (input < 0.5f) return 0.5f * x * x * x * x * x;
                    x = (input - 0.5f) * 2 - 1;
                    return 0.5f * x * x * x * x * x + 1;
                }
            };
        } else if (interpolatorId == 4) {
            interpolator = AnimationUtils.loadInterpolator(context, android.R.interpolator.bounce);
        } else if (interpolatorId == 5) {
            interpolator = AnimationUtils.loadInterpolator(context, android.R.interpolator.anticipate);
        } else if (interpolatorId == 6) {
            interpolator = AnimationUtils.loadInterpolator(context, android.R.interpolator.overshoot);
        } else if (interpolatorId == 7) {
            interpolator = AnimationUtils.loadInterpolator(context, android.R.interpolator.anticipate_overshoot);
        }
        anim.setInterpolator(interpolator);
        anim.setDuration(speed);
        if (anim2!=null) {
            anim2.setInterpolator(interpolator);
            anim2.setDuration(speed);
        }
        if (PreferencesAnimationsFragment.names[forItem][1].toString().contains("progressbar")) {
            final Animation finalAnim = anim, finalAnim2 = anim2;
            final boolean[] first = {true};
            anim.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationEnd(Animation arg0) {
                    if (first[0]) {
                        finalAnim.setAnimationListener(this);
                        view.startAnimation(finalAnim);
                        if (finalAnim2 != null) first[0] = false;
                    } else if (finalAnim2 != null) {
                        finalAnim2.setAnimationListener(this);
                        view.startAnimation(finalAnim2);
                        first[0] = true;
                    }
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                }

                @Override
                public void onAnimationStart(Animation arg0) {
                }

            });
        }
        return anim;
    }

    @SafeVarargs
    public static <T> AsyncTask startAsyncTask(AsyncTask<T, ?, ?> asyncTask, T... params) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            return asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        else
            return asyncTask.execute(params);
    }

    public static boolean writeAssetToFile(Context context, String assetName, File outFile) {
        try {
            AssetManager am = context.getAssets();
            InputStream input = am.open(assetName);
            FileOutputStream output = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) > 0) {
                output.write(buffer, 0, len);
            }
            input.close();
            output.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static float getDegreesForRotation(int value) {
        switch (value) {
            case Surface.ROTATION_90:
                return 360f - 90f;
            case Surface.ROTATION_180:
                return 360f - 180f;
            case Surface.ROTATION_270:
                return 360f - 270f;
        }
        return 0f;
    }

    public static Bitmap takeScreenshot(Context context, boolean statusBarVisible, boolean navBarVisible) throws Throwable {
        // We need to orient the screenshot correctly (and the Surface api seems to take screenshots
        // only in the natural orientation of the device :!)
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        Matrix mDisplayMatrix = new Matrix();
        Display mDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mDisplay.getRealMetrics(mDisplayMetrics);
        } else {
            mDisplay.getMetrics(mDisplayMetrics);
        }
        float[] dims = {(int) getDisplaySize(context, false)[0], (int) getDisplaySize(context, false)[1]};//{mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels};
        float degrees = getDegreesForRotation(mDisplay.getRotation());
        //boolean requiresRotation = true;//(degrees > 0); // "Rotate" always to fix graphic problems when holding device normal...
        //if (requiresRotation) {
        // Get the dimensions of the device in its native orientation
        mDisplayMatrix.reset();
        mDisplayMatrix.preRotate(-degrees);
        mDisplayMatrix.mapPoints(dims);
        dims[0] = Math.abs(dims[0]);
        dims[1] = Math.abs(dims[1]);
        //}

        // Take the screenshot
        Bitmap mScreenBitmap = SurfaceControl.screenshot((int) dims[0], (int) dims[1]);
        if (mScreenBitmap == null) {
            throw new Throwable("Failed to take screenshot using SurfaceControl, mScreenBitmap is null");
            //return null;
        }

        //if (requiresRotation) {
        // Rotate the screenshot to the current orientation
        Bitmap ss = Bitmap.createBitmap(mDisplayMetrics.widthPixels,
                mDisplayMetrics.heightPixels, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(ss);
        c.translate(ss.getWidth() / 2, ss.getHeight() / 2);
        c.rotate(degrees);
        c.translate(-dims[0] / 2, -dims[1] / 2);
        c.drawBitmap(mScreenBitmap, 0, 0, null);
        c.setBitmap(null);
        // Recycle the previous bitmap
        mScreenBitmap.recycle();
        mScreenBitmap = ss;
        //}

        // Optimizations
        mScreenBitmap.setHasAlpha(false);
        mScreenBitmap.prepareToDraw();

        Log.i("NPM", "[takeScreenshot] Screenshot using SurfaceControl taken!");
        return mScreenBitmap;
    }

    public static Bitmap takeScreenshot(Context context) {
        Bitmap bitmap = null;

        try {
            Object[] displaySize = getDisplaySize(context, false);
            bitmap = SurfaceControl.screenshot((int) displaySize[0], (int) displaySize[1]);
        } catch (Throwable t) {
            Log.e("NPM", "[takeScreenshot] Failed to take screenshot using SurfaceControl...", t);
        }

        if (bitmap != null) {
            Log.i("NPM", "[takeScreenshot] Screenshot using SurfaceControl taken!");
        } else {
            Log.e("NPM", "[takeScreenshot] Failed to take screenshot using SurfaceControl...");
        }

        return bitmap;
    }

    public static Bitmap blurBitmap(Context context, Bitmap bmp) throws Throwable {
        return blurBitmap(context, bmp, 14);
    }

    public static Bitmap blurBitmap(Context context, Bitmap bmp, float radius) throws Throwable {
        if (bmp==null)
            throw new Throwable("Failed to blur, bmp is null.");
        Bitmap out = Bitmap.createBitmap(bmp);
        RenderScript rs = RenderScript.create(context);
        radius = Math.min(Math.max(radius, 0), 25);

        Allocation input = Allocation.createFromBitmap(
                rs, bmp, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptIntrinsicBlur script = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setInput(input);
            script.setRadius(radius);
            script.forEach(output);
        } else {
            Log.e("NPM", "[blurTask] Too low sdk version for blurTask, min SDK needed " + Build.VERSION_CODES.JELLY_BEAN_MR1);
        }

        output.copyTo(out);

        rs.destroy();
        return out;
    }

    @SuppressLint("UseSparseArrays")
    public static int getBitmapPredominantColor(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] pixels = new int[width * height];

        bmp.getPixels(pixels, 0, width, 0, 0, width, height);

        Map<Integer, Integer> tmpMap = new HashMap<Integer, Integer>();
        for (int i = 0; i < pixels.length; i++) {
            Integer counter = (Integer) tmpMap.get(pixels[i]);
            if (counter == null) counter = 0;
            counter++;
            tmpMap.put(pixels[i], counter);
        }

        Map.Entry<Integer, Integer> maxEntry = null;
        for (Map.Entry<Integer, Integer> entry : tmpMap.entrySet()) {
            // discard transparent pixels
            if (entry.getKey() == Color.TRANSPARENT) continue;

            if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                maxEntry = entry;
            }
        }

        return maxEntry.getKey();
    }

    public static void setDividerColor(NumberPicker picker, int color) {

        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (IllegalArgumentException | IllegalAccessException | Resources.NotFoundException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }


        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 96; // Replaced the 1 by a 96
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 96; // Replaced the 1 by a 96

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
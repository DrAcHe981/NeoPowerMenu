package de.NeonSoft.neopowermenu.helpers;

import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.view.animation.*;

import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.Preferences.*;

import java.io.*;
import java.lang.reflect.*;
import java.security.*;
import java.util.*;

import net.lingala.zip4j.core.*;
import net.lingala.zip4j.model.*;
import net.lingala.zip4j.util.*;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

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
            if (logging) Log.i("NPM:pC", "Copying preferences...");
            Map<String, ?> oldPrefsAll = from.getAll();
            if (!oldPrefsAll.isEmpty()) {
                Object[] keys = oldPrefsAll.keySet().toArray();
                Object[] values = oldPrefsAll.values().toArray();
                String designSpace = "";
                for (int x = 0; x < (String.format("%03d/%03d", oldPrefsAll.size(), oldPrefsAll.size())).length(); x++) {
                    designSpace = designSpace + " ";
                }
                for (int i = 0; i < oldPrefsAll.size(); i++) {
                    boolean unknown = false;
                    if (logging)
                        Log.i("NPM:pC", String.format("%03d/%03d", (i + 1), oldPrefsAll.size()) + " | " + keys[i]);
                    if (values[i].getClass().equals(String.class)) {
                        if (logging) Log.i("NPM:pC", designSpace + " | Writing String...");
                        to.edit().putString((String) keys[i], (String) values[i]).commit();
                    } else if (values[i].getClass().equals(Integer.class)) {
                        if (logging) Log.i("NPM:pC", designSpace + " | Writing Integer...");
                        to.edit().putInt((String) keys[i], (int) values[i]).commit();
                    } else if (values[i].getClass().equals(Boolean.class)) {
                        if (logging) Log.i("NPM:pC", designSpace + " | Writing Boolean...");
                        to.edit().putBoolean((String) keys[i], (boolean) values[i]).commit();
                    } else {
                        unknown = true;
                        if (logging)
                            Log.i("NPM:pC", designSpace + " | Unknown type... (" + values[i].getClass() + ")");
                    }
                    if (!unknown) {
                        if (logging) Log.i("NPM:pC", designSpace + " \\_Converted.");
                    } else {
                        if (logging) Log.i("NPM:pC", designSpace + " \\_Failed.");
                    }
                }
            }
        } catch (Throwable t) {
            return false;
        } finally {
            return true;
        }
    }

    public static String getTimeString(long InputMilliSeconds, boolean withTxt) {
        //long OutputMiliSeconds = 0;
        long OutputSeconds = 0;
        long OutputMinutes = 0;
        long OutputHours = 0;
        for (int i = 0; i < InputMilliSeconds; i += 1000) {
            if (OutputSeconds >= 59) {
                OutputSeconds = 0;
                OutputMinutes++;
            } else if (OutputMinutes >= 59) {
                OutputMinutes = 0;
                OutputHours++;
            } else {
                OutputSeconds++;
            }
        }
        String duration_string = "0ms";
                /*if (withMs) {
						OutputHours = OutputMinutes;
						OutputMinutes = OutputSeconds;
						OutputSeconds = Long.parseLong((""+InputMilliSeconds));
				}*/
        String hours = "", minutes = "", seconds = "", milliseconds = "";
        if (OutputHours > 0) {
            hours = String.format("%2d", OutputHours);
        }
        if (OutputMinutes > 0) {
            minutes = String.format("%2d", OutputMinutes);
        }
        if (OutputSeconds > 0) {
            seconds = String.format("%2d", OutputSeconds);
        }
        if (seconds.isEmpty() && minutes.isEmpty() && hours.isEmpty()) {
            milliseconds = String.format("%3d", InputMilliSeconds);
        }
        duration_string = hours + (!hours.isEmpty() ? (withTxt ? "h " : ":") : "");
        duration_string += minutes + (!minutes.isEmpty() ? (withTxt ? "m " : ":") : "");
        duration_string += seconds + (!seconds.isEmpty() ? (withTxt ? "s " : ":") : "");
        duration_string += milliseconds + (!milliseconds.isEmpty() ? (withTxt ? "ms" : "") : "");
        //duration_string = hours+(!hours.isEmpty() && withTxt ? "h" : ":")+(!hours.isEmpty() && !minutes.isEmpty() && withTxt ? " " : "")+minutes+(!minutes.isEmpty() && withTxt ? "m" : ":")+(!minutes.isEmpty() && !seconds.isEmpty() && withTxt ? " " : "")+seconds+(!seconds.isEmpty() && withTxt ? "s" : ":")+(!seconds.isEmpty() && !milliseconds.isEmpty() && withTxt ? " " : "")+milliseconds+(!milliseconds.isEmpty() && withTxt ? "ms" : "");
				/*if (withTxt) {
						if (OutputHours > 0)
						{
								duration_string +=  "(H:M:S)";
						}
						else if (OutputMinutes > 0)
						{
								duration_string +=  "(M:S)";
						} else if (OutputSeconds > 0) {
								duration_string +=  "(S)";
						} else {
								duration_string += "(MS)";
						}
				}*/
        return duration_string;
    }

    public static String getSizeString(long bytes, boolean si) {
        if (bytes <= 0) return "0b";
        int unit = si ? 1024 : 1000;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String md5Crypto(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(input.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
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

        return mHorizontal;
    }

    public static Point getNavigationBarSize(Context mContext) {
        Point appUsableScreenSize = getAppUsableScreenSize(mContext);
        Point realScreenSize = getRealScreenSize(mContext);

        // at right
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
            if (zipLogging) Log.e("NPM:zipFile", "Failed to zip: " + t.toString());
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
            if (zipLogging) Log.e("NPM:zipAll", "Failed to zip: " + t.toString());
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
            if (zipLogging) Log.e("NPM:unzipFile", "Failed to unzip: " + t.toString());
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
            if (zipLogging) Log.e("NPM:unzipAll", "Failed to unzip: " + t.toString());
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
            if (zipLogging) Log.e("NPM:isValidZip", "Failed to validate: " + t.toString());
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
            if (zipLogging) Log.e("NPM:removeFromZip", "Failed to remove: " + t.toString());
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
            Log.e("NPM:copyFile", fnfe1.getMessage());
            return false;
        } catch (Exception e) {
            Log.e("NPM:copyFile", e.getMessage());
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
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
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

    public static Animation getAnimation(Context context, SharedPreferences prefs, int forItem, boolean forOut) {
        int speed;
        speed = 700;
        if (prefs.getInt(PreferencesAnimationsFragment.names[forItem+2][1].toString(), 3) == 0) {
            speed = 100;
        } else if (prefs.getInt(PreferencesAnimationsFragment.names[forItem+2][1].toString(), 3) == 1) {
            speed = 300;
        } else if (prefs.getInt(PreferencesAnimationsFragment.names[forItem+2][1].toString(), 3) == 2) {
            speed = 500;
        } else if (prefs.getInt(PreferencesAnimationsFragment.names[forItem+2][1].toString(), 3) == 3) {
            speed = 700;
        } else if (prefs.getInt(PreferencesAnimationsFragment.names[forItem+2][1].toString(), 3) == 4) {
            speed = 900;
        } else if (prefs.getInt(PreferencesAnimationsFragment.names[forItem+2][1].toString(), 3) == 5) {
            speed = 1100;
        } else if (prefs.getInt(PreferencesAnimationsFragment.names[forItem+2][1].toString(), 3) == 6) {
            speed = 1300;
        }
        Animation anim = null;
        if (!forOut) {
            if (prefs.getInt(PreferencesAnimationsFragment.names[forItem+1][1].toString(), PreferencesAnimationsFragment.defaultTypes[forItem+1]) == 0) {
                anim = AnimationUtils.loadAnimation(context, R.anim.fade_in);
            } else if (prefs.getInt(PreferencesAnimationsFragment.names[forItem+1][1].toString(), PreferencesAnimationsFragment.defaultTypes[forItem+1]) == 1) {
                anim = AnimationUtils.loadAnimation(context, R.anim.fade_in);
            } else if (prefs.getInt(PreferencesAnimationsFragment.names[forItem+1][1].toString(), PreferencesAnimationsFragment.defaultTypes[forItem+1]) == 2) {
                anim = AnimationUtils.loadAnimation(context, R.anim.anim_slide_in_bottom);
            } else if (prefs.getInt(PreferencesAnimationsFragment.names[forItem+1][1].toString(), PreferencesAnimationsFragment.defaultTypes[forItem+1]) == 3) {
                anim = AnimationUtils.loadAnimation(context, R.anim.anim_slide_in_right);
            } else if (prefs.getInt(PreferencesAnimationsFragment.names[forItem+1][1].toString(), PreferencesAnimationsFragment.defaultTypes[forItem+1]) == 4) {
                anim = AnimationUtils.loadAnimation(context, R.anim.anim_slide_in_left);
            } else if (prefs.getInt(PreferencesAnimationsFragment.names[forItem+1][1].toString(), PreferencesAnimationsFragment.defaultTypes[forItem+1]) == 5) {
                anim = AnimationUtils.loadAnimation(context, R.anim.anim_slide_in_top);
            } else if (prefs.getInt(PreferencesAnimationsFragment.names[forItem+1][1].toString(), PreferencesAnimationsFragment.defaultTypes[forItem+1]) == 6) {
                anim = AnimationUtils.loadAnimation(context, R.anim.scale_in_up);
            } else if (prefs.getInt(PreferencesAnimationsFragment.names[forItem+1][1].toString(), PreferencesAnimationsFragment.defaultTypes[forItem+1]) == 7) {
                anim = AnimationUtils.loadAnimation(context, R.anim.scale_in_down);
            } else {
                anim = AnimationUtils.loadAnimation(context, R.anim.fade_in);
            }
        } else {
            if (prefs.getInt(PreferencesAnimationsFragment.names[forItem+1][1].toString(), PreferencesAnimationsFragment.defaultTypes[forItem+1]) == 0) {
                anim = AnimationUtils.loadAnimation(context, R.anim.fade_out);
            } else if (prefs.getInt(PreferencesAnimationsFragment.names[forItem+1][1].toString(), PreferencesAnimationsFragment.defaultTypes[forItem+1]) == 1) {
                anim = AnimationUtils.loadAnimation(context, R.anim.fade_out);
            } else if (prefs.getInt(PreferencesAnimationsFragment.names[forItem+1][1].toString(), PreferencesAnimationsFragment.defaultTypes[forItem+1]) == 2) {
                anim = AnimationUtils.loadAnimation(context, R.anim.anim_slide_out_bottom);
            } else if (prefs.getInt(PreferencesAnimationsFragment.names[forItem+1][1].toString(), PreferencesAnimationsFragment.defaultTypes[forItem+1]) == 3) {
                anim = AnimationUtils.loadAnimation(context, R.anim.anim_slide_out_right);
            } else if (prefs.getInt(PreferencesAnimationsFragment.names[forItem+1][1].toString(), PreferencesAnimationsFragment.defaultTypes[forItem+1]) == 4) {
                anim = AnimationUtils.loadAnimation(context, R.anim.anim_slide_out_left);
            } else if (prefs.getInt(PreferencesAnimationsFragment.names[forItem+1][1].toString(), PreferencesAnimationsFragment.defaultTypes[forItem+1]) == 5) {
                anim = AnimationUtils.loadAnimation(context, R.anim.anim_slide_out_top);
            } else if (prefs.getInt(PreferencesAnimationsFragment.names[forItem+1][1].toString(), PreferencesAnimationsFragment.defaultTypes[forItem+1]) == 6) {
                anim = AnimationUtils.loadAnimation(context, R.anim.scale_out_up);
            } else if (prefs.getInt(PreferencesAnimationsFragment.names[forItem+1][1].toString(), PreferencesAnimationsFragment.defaultTypes[forItem+1]) == 7) {
                anim = AnimationUtils.loadAnimation(context, R.anim.scale_out_down);
            } else {
                anim = AnimationUtils.loadAnimation(context, R.anim.fade_out);
            }
        }
        anim.setDuration(speed);
        return anim;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @SafeVarargs
    public static <T> AsyncTask startAsyncTask(AsyncTask<T ,?, ?> asyncTask, T... params) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            return asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        else
            return asyncTask.execute(params);
    }

}

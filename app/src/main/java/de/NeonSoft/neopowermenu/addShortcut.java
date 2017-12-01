package de.NeonSoft.neopowermenu;

import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.*;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;

import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.LimitedAgeMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import de.NeonSoft.neopowermenu.Preferences.AddShortcutList;
import de.NeonSoft.neopowermenu.Preferences.PreferencesGraphicsFragment;
import de.NeonSoft.neopowermenu.Preferences.PreferencesVisibilityOrderFragment;
import de.NeonSoft.neopowermenu.helpers.GraphicDrawable;
import de.NeonSoft.neopowermenu.helpers.SettingsManager;
import de.NeonSoft.neopowermenu.helpers.TextDrawable;
import de.NeonSoft.neopowermenu.helpers.URLFileNameGenerator;
import de.NeonSoft.neopowermenu.helpers.actionBar;
import de.NeonSoft.neopowermenu.helpers.helper;
import de.NeonSoft.neopowermenu.xposed.*;

import static de.NeonSoft.neopowermenu.MainActivity.visibleFragment;

public class addShortcut extends AppCompatActivity {

    public static String TAG = "NPM";

    public static SharedPreferences preferences;
    public static SharedPreferences colorPrefs;

    public static Context mActivity;

    public static actionBar mActionBar;
    public static boolean useGraphic = true;
    public static String color1 = "#ff000000";
    public static String color2 = "#ffffff";
    public static float padding = 5f;
    public static boolean useCustomGraphic = true;
    LinearLayout mActionBarHolder;

    public static android.support.v4.app.FragmentManager mFragmentManager;

    public static String versionName = "v1.0";
    public static int versionCode = -1;

    boolean LOCALTESTSERVER = false;

    public static ArrayList<String> items = new ArrayList<>();
    private static ImageLoader imageLoader;
    private boolean ImgLoader_Loaded;

    public static String ForcedLanguage = "System";

    @Override
    protected void onCreate(Bundle p1) {

        useGraphic = true;
        useCustomGraphic = false;
        padding = 10f;

        mActivity = getApplicationContext();

        preferences = SettingsManager.getInstance(this).getMainPrefs();
        colorPrefs = getSharedPreferences("colors", 0);

        ForcedLanguage = preferences.getString("ForcedLanguage","System");
        if(!ForcedLanguage.equalsIgnoreCase("system")) {
            Locale myLocale = new Locale(ForcedLanguage);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
        }

        setTheme(R.style.ThemeBaseDark);

        LOCALTESTSERVER = preferences.getBoolean("useLocalServer", false);

        super.onCreate(p1);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkDarkTheme));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.window_background_dark));
        }

        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (Throwable e) {
            versionName = "< unknown >";
            versionCode = -1;
            Log.e("NPM", "Failed to get Version info: ", e);
        }

        mActionBar = new actionBar(this);

        mActionBarHolder = (LinearLayout) this.findViewById(R.id.actionBar);
        mActionBar.addActionBar(mActionBarHolder);

        mActionBar.setAnimationsEnabled(false);
        mActionBar.setTitle("NeoPowerMenu");
        mActionBar.setSubTitle(getString(R.string.addShortcut_AddShortcut));
        mActionBar.setAnimationsEnabled(true);

        mFragmentManager = getSupportFragmentManager();

        initImageLoader();

        changePrefPage(new AddShortcutList(), "List", false);
    }

    @Override
    public void onBackPressed() {
        if (visibleFragment.equalsIgnoreCase("List")) {
            super.onBackPressed();
        } else {
            changePrefPage(new AddShortcutList(), "List", false);
        }
    }

    public interface CreateShortcutListener {
        void onShortcutCreated(Intent intent);

        void onShortcutFailed(String reason);
    }

    public static void createShortcut(CreateShortcutListener listener, String forItem) {
        try {
            Bitmap icon = null;
            Intent launchIntent = new Intent(mActivity, XposedMainActivity.class);
            //launchIntent.putExtra(ShortcutActivity.EXTRA_ACTION, getAction());
            //launchIntent.putExtra(ShortcutActivity.EXTRA_ACTION_TYPE, getActionType());
            launchIntent.setAction("Shortcut");
            launchIntent.putExtra("ShortcutAction", forItem);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            Intent intent = new Intent();
            String string = forItem;
            try {
                string = mActivity.getResources().getString(mActivity.getResources().getIdentifier("powerMenuMain_" + forItem, "string", MainActivity.class.getPackage().getName()));
            } catch (Throwable t) {
                try {
                    string = mActivity.getResources().getString(mActivity.getResources().getIdentifier("powerMenuBottom_" + forItem, "string", MainActivity.class.getPackage().getName()));
                } catch (Throwable ignored) {
                }
            }

            icon = createCircleIcon(forItem, string, color1, color2);

            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launchIntent);
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, string);
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);//Intent.ShortcutIconResource.fromContext(mActivity, R.mipmap.ic_launcher));

            // descendants can override this to supply additional data
            listener.onShortcutCreated(intent);
        } catch (Throwable t) {
            listener.onShortcutFailed(t.toString());
        }
    }



    public static void changePrefPage(android.support.v4.app.Fragment fragment, String title, boolean allowLoss) {
        try {
            android.support.v4.app.FragmentTransaction frag = mFragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container, fragment);
            if (allowLoss) {
                frag.commitAllowingStateLoss();
            } else {
                frag.commit();
            }
            visibleFragment = title;
        } catch (Throwable t) {
        }
    }

    public static Bitmap createCircleIcon(String text, String finalText, String color1, String color2) {
        Drawable fbd = null;
        try {
            if (addShortcut.useGraphic) {
                Bitmap bd = null;
                if (text.equalsIgnoreCase(mActivity.getString(R.string.shortcut_ShowPowerMenu))) {
                    bd = loadImage(0, "#FFFFFF");
                } else if (text.equalsIgnoreCase("Shutdown")) {
                    bd = loadImage(1, color2);
                } else if (text.equalsIgnoreCase("Reboot")) {
                    bd = loadImage(2, color2);
                } else if (text.equalsIgnoreCase("SoftReboot")) {
                    bd = loadImage(3, color2);
                } else if (text.equalsIgnoreCase("Screenshot")) {
                    bd = loadImage(4, color2);
                } else if (text.equalsIgnoreCase("Screenrecord")) {
                    bd = loadImage(5, color2);
                } else if (text.equalsIgnoreCase("Flashlight")) {
                    bd = loadImage(7, color2);
                } else if (text.equalsIgnoreCase("ExpandedDesktop")) {
                    bd = loadImage(8, color2);
                } else if (text.equalsIgnoreCase("AirplaneMode")) {
                    bd = loadImage(10, color2);
                } else if (text.equalsIgnoreCase("RestartUI")) {
                    bd = loadImage(11, color2);
                } else if (text.equalsIgnoreCase("SoundMode")) {
                    bd = loadImage(12, color2);
                } else if (text.equalsIgnoreCase("Recovery")) {
                    bd = loadImage(15, color2);
                } else if (text.equalsIgnoreCase("Bootloader")) {
                    bd = loadImage(16, color2);
                } else if (text.equalsIgnoreCase("SafeMode")) {
                    bd = loadImage(17, color2);
                } else if (text.equalsIgnoreCase("SoundVibrate")) {
                    bd = loadImage(14, color2);
                } else if (text.equalsIgnoreCase("SoundNormal")) {
                    bd = loadImage(12, color2);
                } else if (text.equalsIgnoreCase("SoundSilent")) {
                    bd = loadImage(13, color2);
                } else if (text.equalsIgnoreCase("KillApp")) {
                    bd = loadImage(18, color2);
                } else if (text.equalsIgnoreCase("ToggleRotate")) {
                    bd = loadImage(21, color2);
                } else if (text.equalsIgnoreCase("MediaPrevious")) {
                    bd = loadImage(22, color2);
                } else if (text.equalsIgnoreCase("MediaPlayPause")) {
                    bd = loadImage(23, color2);
                } else if (text.equalsIgnoreCase("MediaNext")) {
                    bd = loadImage(25, color2);
                } else if (text.equalsIgnoreCase("ToggleWifi")) {
                    bd = loadImage(27,  color2);
                } else if (text.equalsIgnoreCase("ToggleBluetooth")) {
                    bd = loadImage(29, color2);
                } else if (text.equalsIgnoreCase("ToggleData")) {
                    bd = loadImage(31, color2);
                } else if (text.equalsIgnoreCase("RebootFlashMode")) {
                    bd = loadImage(32, color2);
                } else if (text.equalsIgnoreCase("LockPhone")) {
                    bd = loadImage(33, color2);
                } else if (text.equalsIgnoreCase("SilentMode")) {
                    bd = loadImage(12, color2);
                }
                if (text.equalsIgnoreCase(mActivity.getString(R.string.shortcut_ShowPowerMenu))) {
                    fbd = GraphicDrawable.builder().beginConfig().textColor(Color.parseColor(color2)).endConfig().setPadding(0).buildRound(bd, mActivity.getResources().getColor(R.color.transparent));
                } else {
                    fbd = GraphicDrawable.builder().beginConfig().textColor(Color.parseColor(color2)).endConfig().setPadding((int) padding).buildRound(bd, Color.parseColor(color1));
                }
            } else {
                fbd = TextDrawable.builder().beginConfig().textColor(Color.parseColor(color2)).endConfig()
                        .buildRound(finalText.substring(0, 1), Color.parseColor(color1));
            }
        } catch (Throwable t) {
            Log.e("NPM", "Failed to create Circle Icon.", t);
        }
        return helper.drawableToBitmap(fbd);
    }

    private void initImageLoader() {
        try {
            String CACHE_DIR = getCacheDir().getPath() + "/.temp_tmp";
            new File(CACHE_DIR).mkdirs();

            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheOnDisc(true).cacheInMemory(true).imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565).build();

            LruMemoryCache memoryCacheCore = new LruMemoryCache(5 * 1024 * 1024);
            LimitedAgeMemoryCache memoryCache = new LimitedAgeMemoryCache(memoryCacheCore, 15 * 60);
            LruDiskCache discCache = new LruDiskCache(new File(CACHE_DIR), new URLFileNameGenerator(), 250 * 1024 * 1024);
            ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
                    getBaseContext())
                    .defaultDisplayImageOptions(defaultOptions)
                    .diskCache(discCache)
                    .memoryCache(memoryCache);

            ImageLoaderConfiguration config = builder.build();
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(config);
            ImgLoader_Loaded = true;
            Log.d("NPM", "ImageLoader Loaded!");
        } catch (Exception e) {
            Log.e("NPM", "ImageLoader loading failed", e);
        }
    }

    public static Bitmap loadImage(final int id, final String color) {
        final Bitmap[] image = {null};
        if(new File(mActivity.getFilesDir() + "/images/" + PreferencesGraphicsFragment.graphics[id][2].toString() + ".png").exists() && useCustomGraphic) {
            imageLoader.loadImage(mActivity.getFilesDir() + "/images/" + PreferencesGraphicsFragment.graphics[id][2].toString() + ".png", new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    image[0] = helper.drawableToBitmap(mActivity.getResources().getDrawable((int) PreferencesGraphicsFragment.graphics[id][1]));
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    image[0] = changeBitmapColor(loadedImage, Color.parseColor(color));
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    image[0] = helper.drawableToBitmap(mActivity.getResources().getDrawable((int) PreferencesGraphicsFragment.graphics[id][1]));
                }
            });
        } else {
            Drawable drawable;
            if (id == 0) {
                drawable = mActivity.getResources().getDrawable(R.mipmap.ic_launcher);
            } else {
                drawable = mActivity.getResources().getDrawable((int) PreferencesGraphicsFragment.graphics[id][1]);
            }
            drawable.setColorFilter(Color.parseColor(color),
                    android.graphics.PorterDuff.Mode.MULTIPLY);
            image[0] = changeBitmapColor(helper.drawableToBitmap(drawable), Color.parseColor(color));
        }
        return image[0];
    }
    private static Bitmap changeBitmapColor(Bitmap sourceBitmap, int color) {

        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
                sourceBitmap.getWidth() - 1, sourceBitmap.getHeight() - 1);
        Paint p = new Paint();
        ColorFilter filter = new LightingColorFilter(color, 1);
        p.setColorFilter(filter);

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);

        return resultBitmap;
    }
}

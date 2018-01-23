package de.NeonSoft.neopowermenu;

import android.app.*;
import android.app.admin.DevicePolicyManager;
import android.content.*;
import android.content.pm.*;
import android.content.res.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.preference.PreferenceManager;
import android.support.v4.app.*;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.widget.*;

import cat.ereza.customactivityoncrash.*;

import com.nostra13.universalimageloader.cache.disc.impl.ext.*;
import com.nostra13.universalimageloader.cache.memory.impl.*;
import com.nostra13.universalimageloader.core.*;
import com.nostra13.universalimageloader.core.assist.*;

import de.NeonSoft.neopowermenu.Preferences.*;
import de.NeonSoft.neopowermenu.helpers.*;
import de.NeonSoft.neopowermenu.tour.tourFragment;
import de.NeonSoft.neopowermenu.permissionsScreen.*;
import de.NeonSoft.neopowermenu.xposed.*;
import eu.chainfire.libsuperuser.Shell;

import java.io.*;
import java.util.*;

import org.acra.*;

import android.support.v7.app.AlertDialog;

public class MainActivity extends AppCompatActivity {

    public static boolean LOCALTESTSERVER = false; // use local server 127.0.0.1:8080 or online www.Neon-Soft.de
    public static int TIMEOUT_MILLISEC = 10000; // = 10 seconds, Timeout for various network scripts

    public static String deviceUniqeId = "none";

    public static SharedPreferences preferences;
    public static SharedPreferences colorPrefs;
    public static SharedPreferences orderPrefs;
    public static SharedPreferences animationPrefs;
    public static boolean DeepLogging = false;
    public static String ForcedLanguage = "System";
    public static Context context;
    public static Activity activity;
    public static DevicePolicyManager devicePolicyManager;
    public static NotificationManager notificationManager;
    public static IBinder windowToken;
    public static LayoutInflater inflater;
    public static boolean RootAvailable;
    public static android.support.v4.app.FragmentManager fragmentManager;
    public static String visibleFragment = "Main";

    public static final int BG_PRIO = android.os.Process.THREAD_PRIORITY_BACKGROUND;

    public static String versionName = "v1.0";
    public static int versionCode = -1;

    public static LinearLayout actionBarHolder;

    public static actionBar actionbar;

    /*<!-- Internal needed Hook version to check if reboot is needed --!>*/
    public static int neededModuleActiveVersion = 28;

    public static String ImportUrl = null;

    public static Animation anim_fade_out;
    public static Animation anim_fade_in;
    public static Animation anim_fade_slide_out_right;
    public static Animation anim_fade_slide_in_right;

    public static OnClickListener previewOnClickListener;

    // Session data
    public static boolean loggedIn = false;
    public static boolean loggingIn = false;
    public static String usernameemail = "";
    public static String password = "";
    public static String accountUniqeId = "";
    public static String userRank = "U";
    public static String userName = "";

    public static String imagesstorage;
    public static ImageLoader imageLoader;
    public static boolean ImgLoader_Loaded;

    private String[] requieredDirs = {"presets", "download", "images", "temp"};

    boolean saveSortingIsSaving = false;

    boolean freshInstall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        CustomActivityOnCrash.install(getApplicationContext());
        CustomActivityOnCrash.setRestartActivityClass(MainActivity.class);
        ACRA.init(getApplication());

        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        SettingsManager.getInstance(this).fixFolderPermissionsAsync();

        context = getApplicationContext();
        activity = getParent();
        preferences = SettingsManager.getInstance(this).getMainPrefs();//getSharedPreferences(MainActivity.class.getPackage().getName() + "_preferences", 0);
        colorPrefs = getSharedPreferences("colors",  0);
        orderPrefs = getSharedPreferences("visibilityOrder", 0);
        animationPrefs = getSharedPreferences("animations", 0);

        DeepLogging = preferences.getBoolean("DeepXposedLogging", false);

        PackageManager m = getPackageManager();
        String s = getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("NPM", "Error Package name not found ", e);
        }

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

        for (String requieredDir : requieredDirs) {
            File check = new File(context.getFilesDir().getPath() + "/" + requieredDir);
            if (!check.exists() && !check.isDirectory()) {
                if(!check.mkdir()) {
                    Log.e("NPM","Failed to create required directory: "+requieredDir);
                }
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkDarkTheme));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.window_background_dark));
        }
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        anim_fade_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        anim_fade_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        anim_fade_slide_out_right = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_fade_slide_out_right);
        anim_fade_slide_in_right = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_fade_slide_in_right);

        Uri intentfilterdata = getIntent().getData();
        if (intentfilterdata != null) {
            try {
                ImportUrl = intentfilterdata.getScheme() + "://" + intentfilterdata.getPath();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        context = getApplicationContext();
        inflater = getLayoutInflater();

        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (Throwable e) {
            versionName = "< unknown >";
            versionCode = -1;
            Log.e("NPM", "Failed to get Version info: ", e);
        }

        actionbar = new actionBar(this);

        actionBarHolder = (LinearLayout) this.findViewById(R.id.actionBar);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(0, helper.getStatusBarHeight(context), 0, 0);
        //actionBarHolder.setLayoutParams(layoutParams);

        actionbar.addActionBar(actionBarHolder);

        actionbar.setAnimationsEnabled(false);
        actionbar.setTitle("NeoPowerMenu");
        actionbar.setSubTitle("v" + versionName + " (" + versionCode + ")" + (LOCALTESTSERVER ? " | Using local test Server" : ""));

        fragmentManager = getSupportFragmentManager();

        initImageLoader();

        previewOnClickListener = new OnClickListener() {

            @Override
            public void onClick(View p1) {
                //actionbar.hideButton();
                if (visibleFragment.equalsIgnoreCase("VisibilityOrder")) {
                    if (!saveSortingIsSaving) {
                        saveSorting save = new saveSorting();
                        save.setFinish(1);
                        helper.startAsyncTask(save);
                    }
                } else {
                    launchPowerMenu();
                }
            }
        };

        if ((deviceUniqeId = preferences.getString("userUniqeId", "none")).equalsIgnoreCase("none")) {
            Date date = new Date();
            deviceUniqeId = helper.md5Crypto(Build.MANUFACTURER + "-" + Build.MODEL + "-" + date.getYear() + "." + date.getMonth() + "." + date.getDay() + "-" + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds());
            preferences.edit().putString("userUniqeId", deviceUniqeId).commit();
            freshInstall = true;
        }
        if (freshInstall) {
            fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).add(R.id.dialog_container, new tourFragment(), "tour").commit();
            changePrefPage(new PreferencesPartFragment(), false);
        } else {
            if (preferences.getBoolean("DontAskPermissionsAgain", false) || permissionsScreen.checkPermissions(MainActivity.this, permissionsScreen.permissions)) {
                android.support.v4.app.Fragment fragment = new PreferencesPartFragment();
                changePrefPage(fragment, false);
                if (ImportUrl != null) {
                    changePrefPage(new PreferencesPresetsFragment(), false);
                }
            } else {
                changePrefPage(new permissionsScreen(), false);
            }
        }
        actionbar.setAnimationsEnabled(true);
        //throw new RuntimeException("This is a test crash!");
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.findFragmentByTag(slideDownDialogFragment.dialogTag) != null) {
                        /*Intent intent = new Intent();
                         intent.setAction(slideDownDialogFragment.dialogCloseCall);
						 context.sendBroadcast(intent);*/
            //Toast.makeText(context,"Canceling sDDF "+slideDownDialogFragment.dialogs.size(),Toast.LENGTH_LONG).show();
            if (slideDownDialogFragment.dialogs.size() > 0 && slideDownDialogFragment.dialogs.get(slideDownDialogFragment.dialogs.size() - 1).cancelDialog() == null) {
                fragmentManager.beginTransaction().remove(slideDownDialogFragment.dialogs.get(slideDownDialogFragment.dialogs.size() - 1)).commitAllowingStateLoss();
            }
        } else if (visibleFragment.equalsIgnoreCase("Cropper")) {
            changePrefPage(new PreferencesGraphicsFragment(), false);
        } else if (visibleFragment.equalsIgnoreCase("VisibilityOrder")) {
            if (!saveSortingIsSaving) {
                saveSorting save = new saveSorting();
                save.setFinish(2);
                helper.startAsyncTask(save);
            }
        } else if (visibleFragment.equalsIgnoreCase("PresetsManager")) {
            for (int i = 0; i < PreferencesPresetsFragment.OnlinePresets.size(); i++) {
                if (PreferencesPresetsFragment.OnlinePresets.get(i).getDownloadHelper() != null && PreferencesPresetsFragment.OnlinePresets.get(i).getDownloadHelper().isRunning()) {
                    PreferencesPresetsFragment.OnlinePresets.get(i).getDownloadHelper().stopDownload(true);
                }
            }
						if(PreferencesPresetsFragment.listParser != null)
            		PreferencesPresetsFragment.listParser.cancel(true);
            actionbar.setButton(getString(R.string.PreviewPowerMenu), R.drawable.ic_action_launch, MainActivity.previewOnClickListener);
            changePrefPage(new PreferencesColorFragment(), false);
        } else if (visibleFragment.equalsIgnoreCase("PresetsManagerOnline") || (visibleFragment.equalsIgnoreCase("PresetsManagerAccount") && (LoginFragment.loginFragmentMode.equalsIgnoreCase("login") || LoginFragment.loginFragmentMode.equalsIgnoreCase("logout")))) {
            if (PreferencesPresetsFragment.onlineSearchBar.getVisibility() == View.VISIBLE) {
                PreferencesPresetsFragment.hideBars();
            } else {
                PreferencesPresetsFragment.vpPager.setCurrentItem(1, true);
            }
        } else if (visibleFragment.equalsIgnoreCase("PresetsManagerAccount") && (LoginFragment.loginFragmentMode.equalsIgnoreCase("register") || LoginFragment.loginFragmentMode.equalsIgnoreCase("recover"))) {
            LoginFragment.returnToLogin();
        } else if (visibleFragment.equalsIgnoreCase("Gravity") || visibleFragment.equalsIgnoreCase("BackupRestore") || visibleFragment.equalsIgnoreCase("tour") || visibleFragment.equalsIgnoreCase("about") || visibleFragment.equalsIgnoreCase("login") || visibleFragment.equalsIgnoreCase("permissions") || visibleFragment.equalsIgnoreCase("Advanced") || visibleFragment.equalsIgnoreCase("CustomColors") || visibleFragment.equalsIgnoreCase("Graphics") || visibleFragment.equalsIgnoreCase("Animations")) {
            if (visibleFragment.equalsIgnoreCase("tour")) {
                if (tourFragment.pager.getCurrentItem() == 0) {
                    tourFragment.finishTour();
                } else {
                    tourFragment.pager.setCurrentItem(tourFragment.pager.getCurrentItem() - 1, true);
                }
            } else {
                //actionBarHolder.setVisibility(View.VISIBLE);
                //actionBarHolder.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
                changePrefPage(new PreferencesPartFragment(), false);
                if (visibleFragment.equalsIgnoreCase("about") || visibleFragment.equalsIgnoreCase("login")) {
                    actionbar.setButton(getString(R.string.PreviewPowerMenu), R.drawable.ic_action_launch, MainActivity.previewOnClickListener);
                }
            }
        } else if (visibleFragment.equalsIgnoreCase("Main") || visibleFragment.equalsIgnoreCase("permissionsAutoStart")) {
            finish();
            super.onBackPressed();
        }
    }

    public static void launchPowerMenu() {
        Intent intent = new Intent(context, XposedMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.putExtra("previewmode", true);
				//Toast.makeText(context, "pre Preview mode: "+intent.getBooleanExtra("previewmode",false), Toast.LENGTH_SHORT).show();
        context.startActivity(intent);
				//context.startService(intent);
    }

    @Override
    protected void onPause() {
        PackageManager m = getPackageManager();
        String s = getPackageName();
        try {
            PackageInfo p = m.getPackageInfo(s, 0);
            s = p.applicationInfo.dataDir;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("NPM", "Error Package name not found ", e);
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (!visibleFragment.equalsIgnoreCase("tour") && !visibleFragment.equalsIgnoreCase("about") && !visibleFragment.equalsIgnoreCase("permissions") && !visibleFragment.equalsIgnoreCase("permissionsAutoStart") && !visibleFragment.equalsIgnoreCase("PresetsManagerOnline") && !visibleFragment.equalsIgnoreCase("PresetsManagerAccount") && !visibleFragment.equalsIgnoreCase("VisibilityOrder") && !visibleFragment.equalsIgnoreCase("Cropper")) {
            actionbar.setButton(getString(R.string.PreviewPowerMenu), R.drawable.ic_action_launch, previewOnClickListener);
        }
        CustomActivityOnCrash.setRestartActivityClass(MainActivity.class);
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (visibleFragment.equalsIgnoreCase("Graphics")) {
            PreferencesGraphicsFragment.GridView_Images.setNumColumns(getResources().getInteger(R.integer.ImageList_Columns));
        } else if (visibleFragment.equalsIgnoreCase("Gravity")) {
            GravityChooserDialog.changeGravity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PreferencesVisibilityOrderFragment.visibilityOrderPermissionRequest) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PreferencesVisibilityOrderFragment.receivedPermissionResult(PreferencesVisibilityOrderFragment.visibilityOrderPermissionRequest_ItemSpace, PreferencesVisibilityOrderFragment.visibilityOrderPermissionRequest_Id, notificationManager.isNotificationPolicyAccessGranted());
            }
        } else if(requestCode == permissionsScreen.RESULT_ENABLE_ADMIN || requestCode == permissionsScreen.MY_PERMISSIONS_REQUEST) {
            permissionsScreen.adapter.notifyDataSetChanged();
        } else if (requestCode == PreferencesVisibilityOrderFragment.REQ_OBTAIN_SHORTCUT && PreferencesVisibilityOrderFragment.mShortcutHandler != null) {
            if (resultCode == Activity.RESULT_OK) {
                String localIconResName = null;
                Bitmap b = null;
                Intent.ShortcutIconResource siRes = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
                Intent shortcutIntent = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
                if (siRes != null) {
                    if (shortcutIntent != null &&
                            "neopowermenu.intent.action.LAUNCH_ACTION".equals(
                                    shortcutIntent.getAction())) {
                        localIconResName = siRes.resourceName;
                    } else {
                        try {
                            final Context extContext = context.createPackageContext(
                                    siRes.packageName, Context.CONTEXT_IGNORE_SECURITY);
                            final Resources extRes = extContext.getResources();
                            final int drawableResId = extRes.getIdentifier(siRes.resourceName, "drawable", siRes.packageName);
                            b = BitmapFactory.decodeResource(extRes, drawableResId);
                        } catch (PackageManager.NameNotFoundException e) {
                            //
                        }
                    }
                }
                if (localIconResName == null && b == null) {
                    b = (Bitmap) data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON);
                }

                PreferencesVisibilityOrderFragment.mShortcutHandler.onHandleShortcut(shortcutIntent,
                        data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME),
                        localIconResName, b);
            } else {
                PreferencesVisibilityOrderFragment.mShortcutHandler.onShortcutCancelled();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == permissionsScreen.MY_PERMISSIONS_REQUEST) {
            permissionsScreen.adapter.notifyDataSetChanged();
        } else if (requestCode == PreferencesVisibilityOrderFragment.visibilityOrderPermissionRequest) {
            PreferencesVisibilityOrderFragment.receivedPermissionResult(PreferencesVisibilityOrderFragment.visibilityOrderPermissionRequest_ItemSpace, PreferencesVisibilityOrderFragment.visibilityOrderPermissionRequest_Id, grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
        }
    }

    class saveSorting extends AsyncTask<Object, String, String> {

        private int finishingAction = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            saveSortingIsSaving = true;
            PreferencesVisibilityOrderFragment.LinearLayout_Progress.setVisibility(View.VISIBLE);
            PreferencesVisibilityOrderFragment.LinearLayout_Progress.startAnimation(MainActivity.anim_fade_in);
        }

        @Override
        protected String doInBackground(Object[] p1) {
            PreferencesVisibilityOrderFragment.adapter.outputSorting();
            return null;
        }

        @Override
        protected void onPostExecute(String p1) {
            super.onPostExecute(p1);
            saveSortingIsSaving = false;
            PreferencesVisibilityOrderFragment.LinearLayout_Progress.startAnimation(MainActivity.anim_fade_out);
            PreferencesVisibilityOrderFragment.LinearLayout_Progress.setVisibility(View.GONE);
            actionbar.setButton(context.getString(R.string.PreviewPowerMenu), R.drawable.ic_action_launch, previewOnClickListener);
            if (finishingAction==1) {
                launchPowerMenu();
            } else if (finishingAction==2) {
                changePrefPage(new PreferencesPartFragment(), true);
            }
        }

        public void setFinish(int finish) {
            this.finishingAction = finish;
        }
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
            Log.e("NPM", "Failed to load ImageLoader", e);
        }
    }

    public static void changePrefPage(android.support.v4.app.Fragment fragment, boolean allowLoss) {
        try {
            android.support.v4.app.FragmentTransaction frag = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container, fragment);
            if (allowLoss) {
                frag.commitAllowingStateLoss();
            } else {
                frag.commit();
            }
        } catch (Throwable t) {
        }
    }

}

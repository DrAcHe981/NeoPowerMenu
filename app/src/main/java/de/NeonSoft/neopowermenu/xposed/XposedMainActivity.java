package de.NeonSoft.neopowermenu.xposed;

import android.app.*;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import cat.ereza.customactivityoncrash.*;

import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.*;

import org.acra.*;

import de.NeonSoft.neopowermenu.R;
import de.NeonSoft.neopowermenu.Preferences.*;

import android.view.animation.*;
import android.view.View.*;

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

public class XposedMainActivity extends Activity implements DialogInterface.OnDismissListener {

    SharedPreferences preferences;
    SharedPreferences colorPrefs;
    SharedPreferences orderPrefs;
    SharedPreferences animationPrefs;
    ImageView revealImageView;
    CircularRevealView revealView, revealView2;
    TextView PreviewLabel;
    boolean mKeyguardShowing = false;
    boolean previewMode = false;
    android.os.Handler handler;
    int maxX, maxY;
    Context mContext;
    boolean doubleToConfirm = false;

    static ImageLoader mImageLoader;
    boolean mImageLoaderLoaded = false;

    public static String ForcedLanguage = "System";

    boolean HookShutdownThread = false;
    boolean DeepXposedLogging = false;

    boolean mBlurBehind = false;
    float mBlurRadius = 14f;

    int sStyleName = 0;
    XposedDialog powerDialog;

    public BroadcastReceiver mReceiver;

    public static Activity mActivity;
    FrameLayout mActivityRootView;

    KeyguardManager mKeyguardManger;
    KeyguardManager.KeyguardLock mKeyguardLock;
    int backgroundColor;

    public static String action = null;

    static ArrayList<MenuItemHolder> mItems = new ArrayList<>();

    private int shortcutItem = -1;
    static boolean isShortcutWithVisibleContent = false;
    boolean mRoundedDialogCorners = false;
    int mRoundedDialogCornersRadius = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingsManager.getInstance(this).fixFolderPermissionsAsync();

        previewMode = getIntent().getBooleanExtra("previewmode", false);
        if (!previewMode) {
            CustomActivityOnCrash.install(getApplicationContext());
            CustomActivityOnCrash.setRestartActivityClass(XposedMainActivity.class);
            ACRA.init(getApplication());
        }

        isShortcutWithVisibleContent = false;
        mContext = getApplicationContext();

        initImageLoader();

        preferences = SettingsManager.getInstance(this).getMainPrefs();
        colorPrefs = getSharedPreferences("colors", 0);
        orderPrefs = getSharedPreferences("visibilityOrder", 0);
        animationPrefs = getSharedPreferences("animations", 0);

        mRoundedDialogCorners = preferences.getBoolean(PreferenceNames.pRoundedDialogCorners, false);
        mRoundedDialogCornersRadius = (mRoundedDialogCorners ? preferences.getInt(PreferenceNames.pRoundedDialogCornersRadius, 0) : 0);

        DeepXposedLogging = preferences.getBoolean(PreferenceNames.pDeepXposedLogging, false);

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

        mBlurBehind = preferences.getBoolean("BlurBehind", false);
        mBlurRadius = preferences.getFloat("BlurRadius", 14f);

        sStyleName = preferences.getInt("DialogThemeId", 0);

        action = null;

        if (getIntent().getAction() != null && getIntent().getAction().equalsIgnoreCase("Shortcut")) {
            action = getIntent().getStringExtra("ShortcutAction");
            //Toast.makeText(getApplicationContext(), "Received action: " + action, Toast.LENGTH_SHORT).show();
            if (action.equalsIgnoreCase("PowerMenu") ) {
                action = null;
            }
        }

        setTheme(R.style.TransparentApp);
        int TransFlag = 0;
        if (Build.VERSION.SDK_INT >= 19) {
            TransFlag = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
        }
        getWindow().addFlags(TransFlag |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

        //getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
        getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        //getWindow().getDecorView().setSystemUiVisibility(View.STATUS_BAR_DISABLE_EXPAND | View.STATUS_BAR_DISABLE_HOME | View.STATUS_BAR_DISABLE_RECENT);
        //mKeyguardShowing = getIntent().getBooleanExtra("mKeyguardShowing",false);
        mKeyguardManger = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        mKeyguardLock = mKeyguardManger.newKeyguardLock(KEYGUARD_SERVICE);
        mKeyguardShowing = mKeyguardManger.isKeyguardLocked();
        if (mKeyguardShowing) {
            //Log.d("NeoPowerMenu","Showing in Keyguard");
            if (!preferences.getBoolean("ShowOnLockScreen", true)) {
                finish();
            }
            //mKeyguardLock.disableKeyguard();
            getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        } else {
            //Log.d("NeoPowerMenu","Showing Normal");
            getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
        }
        //getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);

        if (orderPrefs.getAll().isEmpty()) {
            orderPrefs.edit().putInt("0_item_type", visibilityOrder_ListAdapter.TYPE_NORMAL).apply();;
            orderPrefs.edit().putString("0_item_title", "Shutdown").apply();;
            orderPrefs.edit().putInt("1_item_type", visibilityOrder_ListAdapter.TYPE_NORMAL).apply();;
            orderPrefs.edit().putString("1_item_title", "Reboot").apply();;
            orderPrefs.edit().putInt("2_item_type", visibilityOrder_ListAdapter.TYPE_NORMAL).apply();;
            orderPrefs.edit().putString("2_item_title", "SoftReboot").apply();;
            orderPrefs.edit().putInt("3_item_type", visibilityOrder_ListAdapter.TYPE_MULTI).apply();;
            orderPrefs.edit().putString("3_item1_title", "Recovery").apply();;
            orderPrefs.edit().putString("3_item2_title", "Bootloader").apply();;
            orderPrefs.edit().putString("3_item3_title", "SafeMode").apply();;
        }

        mItems = new ArrayList<>();
        final ArrayList<String> MultiPage = new ArrayList<>();
        boolean firstItemDrawn = false;
        for (int i = 0; i < orderPrefs.getAll().size(); i++) {
            int createItem = -1;
            MenuItemHolder item = new MenuItemHolder();
            ArrayList<String> titles = new ArrayList<>();
            ArrayList<String> texts = new ArrayList<>();
            ArrayList<String> uris = new ArrayList<>();
            if (orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", -1) != -1) {
                item.setOnPage((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) : ""));
                item.setType(orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", visibilityOrder_ListAdapter.TYPE_NORMAL));
                item.setHideDesc(orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideDesc", false));
                item.setHideOnLockScreen(orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideOnLockscreen", false));
                item.setFillEmpty(orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_fillEmpty", false));
                item.setLockedWithPassword(orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_lockedWithPassword", false));
                item.setHideText(orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideText", false));
                item.setHorizontal(orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_horizontal", true));
                if (item.getType() == visibilityOrder_ListAdapter.TYPE_NORMAL) {
                    titles.add(orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "Empty"));
                    texts.add(orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_text", ""));
                    uris.add(orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_shortcutUri", ""));
                    if (MultiPage.size() == 0 || (MultiPage.size() == 1 && !firstItemDrawn)) {
                        if (!mKeyguardShowing || (mKeyguardShowing && !item.getHideOnLockScreen())) {
                            createItem = visibilityOrder_ListAdapter.TYPE_NORMAL;
                        }
                        firstItemDrawn = true;
                    }
                } else if (item.getType() == visibilityOrder_ListAdapter.TYPE_MULTI) {
                    int x = 1;
                    do {
                        titles.add(orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item" + x + "_title", "Empty"));
                        texts.add(orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item" + x + "_text", ""));
                        uris.add(orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item" + x + "_shortcutUri", ""));
                        x++;
                    } while (!orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item" + x + "_title", "][EMPTY][").equals("][EMPTY]["));
                    if (MultiPage.size() == 0) {
                        if (!mKeyguardShowing || (mKeyguardShowing && !item.getHideOnLockScreen())) {
                            createItem = visibilityOrder_ListAdapter.TYPE_MULTI;
                        }
                    }
                } else if (item.getType() == visibilityOrder_ListAdapter.TYPE_MULTIPAGE_START) {
                    titles.add(orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "Empty"));
                    texts.add(orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_text", ""));
                    uris.add(orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_shortcutUri", ""));
                    item.setOnPage(orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null"));
                    MultiPage.add(orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null"));
                    if (!mKeyguardShowing || (mKeyguardShowing && !item.getHideOnLockScreen())) {
                        firstItemDrawn = false;
                    }
                } else if (item.getType() == visibilityOrder_ListAdapter.TYPE_MULTIPAGE_END) {
                    titles.add(orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "Empty"));
                    texts.add(orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_text", ""));
                    uris.add(orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_shortcutUri", ""));
                    item.setOnPage(orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null"));
                    if (MultiPage.size() > 0) MultiPage.remove(MultiPage.size() - 1);
                }
                item.setTitle(titles);
                item.setText(texts);
                item.setShortcutUri(uris);
                mItems.add(item);
            }
            // if (XposedMainActivity.action != null && item.getTitle(1).contains(XposedMainActivity.action)) {
            //     shortcutItem = i;
            // }
        }

        if (action == null ||
                action.equalsIgnoreCase("FakePowerOff") ||
                action.equalsIgnoreCase("Shutdown") ||
                action.equalsIgnoreCase("Reboot") ||
                action.equalsIgnoreCase("SoftReboot") ||
                action.equalsIgnoreCase("Recovery") ||
                action.equalsIgnoreCase("Bootloader") ||
                action.equalsIgnoreCase("SafeMode") ||
                action.equalsIgnoreCase("Flashmode") ||
                (shortcutItem > -1 && mItems.get(shortcutItem).getLockedWithPassword())) {
            isShortcutWithVisibleContent = true;
            //Toast.makeText(getApplicationContext(), "Shortcut is a shutdown/reboot action or locked with password!", Toast.LENGTH_SHORT).show();
            this.overridePendingTransition(R.anim.fade_in, 0);
        }
        super.onCreate(savedInstanceState);

        if(mBlurBehind || mKeyguardShowing) {
            try {
                Bitmap map = helper.takeScreenshot(mContext, false, false);
                if (map != null) {
                    if (mBlurBehind) map = helper.blurBitmap(mContext, map, mBlurRadius);
                    Drawable draw = new BitmapDrawable(getResources(), map);
                    getWindow().setBackgroundDrawable(draw);
                }
            } catch (Throwable t) {
                Log.e("NPM","[blurTask] Failed to capture or blur the background...", t);
            }
        }

        setContentView(R.layout.activity_main_xposed);

        mActivity = XposedMainActivity.this;
        mActivityRootView = (FrameLayout) findViewById(R.id.activitymainxposedFrameLayout_Root);

        FrameLayout mFragmentHolder = (FrameLayout) findViewById(R.id.powerfragment_holder);
        mFragmentHolder.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                if (powerDialog != null) {
                    powerDialog.SubDialogs.clear();
                    powerDialog.dismissThis();
                }
            }
        });
                /*mBlurUtils = new BlurUtils(getApplicationContext());
                 Bitmap lastBlurredBitmap = BlurTask.getLastBlurredBitmap();

				 if(lastBlurredBitmap!= null) {
				 //Toast.makeText(mContext,"Found previous blur, using it!",Toast.LENGTH_SHORT).show();
				 BitmapDrawable blurredDrawable = new BitmapDrawable(lastBlurredBitmap);
				 blurredDrawable.setColorFilter(mColorFilter);
				 mActivityRootView.setBackground(blurredDrawable);
				 } else {
				 //Toast.makeText(mContext,"Starting new blur task...",Toast.LENGTH_SHORT).show();
				 //startBlurTask();
				 }*/

        revealView = (CircularRevealView) findViewById(R.id.reveal);
        revealImageView = (ImageView) findViewById(R.id.revealImage);
        revealView2 = (CircularRevealView) findViewById(R.id.reveal2);

        PreviewLabel = (TextView) findViewById(R.id.PreviewLable);
        if (previewMode)
            PreviewLabel.setVisibility(View.VISIBLE);

        Display mdisp = getWindowManager().getDefaultDisplay();
        Point mdispSize = new Point();
        mdisp.getSize(mdispSize);
        maxX = mdispSize.x;
        maxY = mdispSize.y;

        final int color = Color.parseColor(colorPrefs.getString("Dialog_Revealcolor", "#8800bcd4"));
        final Point p = new Point(maxX / 2, maxY / 2);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context p1, Intent p2) {
                if (powerDialog != null) {
                    if (powerDialog.canDismiss || previewMode) {
                        if (p2.getAction().equalsIgnoreCase(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                            if (powerDialog.mContext != null) {
                                powerDialog.SubDialogs.clear();
                                powerDialog.dismissThis();
                            }
                        } else if (p2.getAction().equalsIgnoreCase(Intent.ACTION_SCREEN_OFF)) {
                            finish();
                        }
                    }
                }
            }
        };

        mContext.registerReceiver(mReceiver, filter);

        if(!isShortcutWithVisibleContent) {
            revealView.setVisibility(View.INVISIBLE);
            revealView2.setVisibility(View.INVISIBLE);
            findViewById(R.id.powerfragment_holder).setVisibility(View.INVISIBLE);
        } else {
            mActivity.overridePendingTransition(0, 0);
        }

        if((isShortcutWithVisibleContent) && animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[1]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
            final Animation anim = helper.getAnimation(mContext, animationPrefs, 0, false);
            final int speed = (int) anim.getDuration();
            if (new File(mContext.getFilesDir().getPath() + "/images/xposed_dialog_background.png").exists() && preferences.getBoolean(PreferenceNames.pUseGraphics, false)) {
                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mImageLoader.loadImage(mContext.getFilesDir().getPath() + "/images/xposed_dialog_background.png", new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {

                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                //Log.w("NPM","Failed to load background image:", failReason.getCause());
                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                revealImageView.setImageBitmap(loadedImage);
                                revealImageView.setVisibility(View.VISIBLE);
                                revealImageView.startAnimation(anim);
                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {

                            }
                        });
                    }
                }, 50);
            } else {
                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[1]) == 1) {
                            revealView.reveal(p.x, p.y, color, 0, speed, null);
                        } else {
                            revealView.reveal(p.x, p.y, color, 0, 0, null);
                            revealView.startAnimation(anim);
                        }
                    }
                }, 50);
            }

            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showPowerDialog();
                }
            }, Math.max(speed - 200, 0));
        } else {
            revealView.reveal(p.x, p.y, color, 0, 0, null);
            showPowerDialog();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().getAttributes().windowAnimations = R.style.PopUpDialogAnimation_Window;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //powerDialog.dismiss();
    }

    private void showPowerDialog() {
        try {
            FragmentManager fm = getFragmentManager();
            powerDialog = new XposedDialog();
            powerDialog.setHost(this);
            powerDialog.setPreviewMode(previewMode);
            //powerDialog.setPreferences(preferences, colorPrefs, orderPrefs, animationPrefs);
            //powerDialog.setConfiguration(previewMode, HideOnClick, mKeyguardShowing, LoadAppIcons, sStyleName, GraphicsPadding, ColorizeNonStockIcons, imageLoader, DeepXposedLogging);
            //if(sStyleName.equalsIgnoreCase("Material")) {
            //powerDialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ThemeDialogBaseLight);
            //} else if (sStyleName.equalsIgnoreCase("Material (Fullscreen)")) {
            //		powerDialog.setStyle(DialogFragment.STYLE_NO_TITLE,R.style.TransparentApp);
            //}
            fm.beginTransaction().add(R.id.powerfragment_holder, powerDialog).commitAllowingStateLoss();
            //powerDialog.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.ThemeDialogBaseLight);
            //powerDialog.show(fm, "fragment_power");
        } catch (Throwable t) {
            Log.e("NeoPowerMenu", "Failed to show power menu: " + t.toString());
        }
    }

    public void revealFromTop(String colorString) {
        final int color = Color.parseColor(colorString);

        final Point p = new Point(maxX / 2, maxY / 2);

        if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[1]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
            Animation anim = helper.getAnimation(mContext, animationPrefs, 0, false);
            int speed = (int) anim.getDuration();
            if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[1]) == 1) {
                revealView.reveal(p.x, p.y, color, 0, speed, null);
            } else {
                revealView2.reveal(p.x, p.y, color, 0, 0, null);
                Animation animOut = null;
                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[1]) == 0) {
                    animOut = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
                } else if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[1]) == 2) {
                    animOut = AnimationUtils.loadAnimation(mContext, R.anim.anim_slide_out_top);
                } else if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[1]) == 3) {
                    animOut = AnimationUtils.loadAnimation(mContext, R.anim.anim_slide_out_left);
                } else if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[1]) == 4) {
                    animOut = AnimationUtils.loadAnimation(mContext, R.anim.anim_slide_out_right);
                } else if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[1]) == 5) {
                    animOut = AnimationUtils.loadAnimation(mContext, R.anim.anim_slide_out_bottom);
                } else if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[1]) == 6) {
                    animOut = AnimationUtils.loadAnimation(mContext, R.anim.scale_out_down);
                } else if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[1]) == 7) {
                    animOut = AnimationUtils.loadAnimation(mContext, R.anim.scale_out_up);
                } else {
                    animOut = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
                }
                animOut.setDuration(anim.getDuration());
                revealView.startAnimation(animOut);
                revealView.setVisibility(View.GONE);
                revealView2.setVisibility(View.VISIBLE);
                revealView2.startAnimation(anim);
            }
        } else {
            revealView.reveal(p.x, p.y, color, 0, 0, null);
        }

    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        if (powerDialog.canDismiss || previewMode) {
        }
    }

    @Override
    public void onBackPressed() {
        if (powerDialog != null) {
            if (powerDialog.canDismiss || previewMode) {
                powerDialog.dismissThis();
                //powerDialog = null;
            }
        }
    }

    @Override
    public void finish() {
        if (mReceiver != null) {
            try {
                mContext.unregisterReceiver(mReceiver);
            } catch (Throwable t) {
                Log.e("NPM", "Failed to unregister broadcastreceiver.", t);
            }
        }
        super.finish();
    }

    public void dismissThis() {
        //if (powerDialog != null)
        //{
        //		if (powerDialog.canDismiss || previewMode)
        //		{
        final Point p = new Point(maxX / 2, maxY / 2);

        if (animationPrefs.getInt(PreferencesAnimationsFragment.names[PreferencesAnimationsFragment.anim_Reveal + PreferencesAnimationsFragment.anim_Type][1].toString(), PreferencesAnimationsFragment.defaultTypes[PreferencesAnimationsFragment.anim_Reveal + PreferencesAnimationsFragment.anim_Type]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
            final int speed;
            final Animation anim = helper.getAnimation(mContext, animationPrefs, PreferencesAnimationsFragment.anim_Reveal, true);
            speed = (int) anim.getDuration();
            if (new File(mContext.getFilesDir().getPath() + "/images/xposed_dialog_background.png").exists() && preferences.getBoolean(PreferenceNames.pUseGraphics, false)) {
                revealImageView.startAnimation(anim);
                revealImageView.setVisibility(View.GONE);
            } else {
                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[PreferencesAnimationsFragment.anim_Reveal + PreferencesAnimationsFragment.anim_Type][1].toString(), PreferencesAnimationsFragment.defaultTypes[PreferencesAnimationsFragment.anim_Reveal + PreferencesAnimationsFragment.anim_Type]) == 1) {
                    String color = colorPrefs.getString("Reveal_Backgroundcolor", "#8800bcd4");
                    if (Color.alpha(Color.parseColor(color)) > 0) {
                        if (revealView.getVisibility() == View.VISIBLE) {
                            revealView.hide(p.x, p.y, Color.parseColor("#00000000"), 0, speed, null);
                        }
                    }
                } else {
                    if (revealView2.getVisibility() == View.VISIBLE) {
                        revealView2.startAnimation(anim);
                        revealView2.setVisibility(View.GONE);
                    } else if (revealView.getVisibility() == View.VISIBLE) {
                        revealView.startAnimation(anim);
                        revealView.setVisibility(View.GONE);
                    }
                }
            }
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mKeyguardShowing) {
                        //mKeyguardLock.reenableKeyguard();
                    }
                    mActivity.finish();
                    if (isShortcutWithVisibleContent) {
                        mActivity.overridePendingTransition(0, R.anim.fade_out);
                    } else {
                        mActivity.overridePendingTransition(0, 0);
                    }
                }
            }, speed);
        } else {
            revealView.setVisibility(View.GONE);
            revealView2.setVisibility(View.GONE);
            mActivity.finish();
            if (isShortcutWithVisibleContent) {
                mActivity.overridePendingTransition(0, R.anim.fade_out);
            } else {
                mActivity.overridePendingTransition(0, 0);
            }
        }
        //		}
        //}
    }

    private void initImageLoader() {
        try {
            String CACHE_DIR = mContext.getCacheDir().getPath() + "/.temp_tmp";
            new File(CACHE_DIR).mkdirs();

            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheOnDisc(true).cacheInMemory(true).imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565).build();

            LruMemoryCache memoryCacheCore = new LruMemoryCache(5 * 1024 * 1024);
            LimitedAgeMemoryCache memoryCache = new LimitedAgeMemoryCache(memoryCacheCore, 15 * 60);
            LruDiskCache discCache = new LruDiskCache(new File(CACHE_DIR), new URLFileNameGenerator(), 250 * 1024 * 1024);
            ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
                    mContext)
                    .defaultDisplayImageOptions(defaultOptions)
                    .discCache(discCache)
                    .memoryCache(memoryCache);

            ImageLoaderConfiguration config = builder.build();
            mImageLoader = ImageLoader.getInstance();
            mImageLoader.init(config);
            mImageLoaderLoaded = true;
            Log.d("NPM", "ImageLoader Loaded!");
        } catch (Exception e) {
            Log.e("NPM", "Failed to load ImageLoader", e);
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        XposedDialog.initLayoutParams();
        XposedDialog.setGravity(sStyleName);
    }

}

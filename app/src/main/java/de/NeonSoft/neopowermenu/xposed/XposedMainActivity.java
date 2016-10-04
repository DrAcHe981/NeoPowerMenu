package de.NeonSoft.neopowermenu.xposed;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import cat.ereza.customactivityoncrash.*;

import com.nostra13.universalimageloader.cache.disc.impl.ext.*;
import com.nostra13.universalimageloader.cache.memory.impl.*;
import com.nostra13.universalimageloader.core.*;
import com.nostra13.universalimageloader.core.assist.*;

import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.*;
import de.NeonSoft.neopowermenu.helpers.BlurUtils.*;

import java.io.*;

import org.acra.*;

import de.NeonSoft.neopowermenu.R;
import de.NeonSoft.neopowermenu.Preferences.*;

import android.view.animation.*;
import android.view.View.*;

public class XposedMainActivity extends Activity implements DialogInterface.OnDismissListener {

    SharedPreferences preferences;
    SharedPreferences colorPrefs;
    SharedPreferences orderPrefs;
    SharedPreferences animationPrefs;
    CircularRevealView revealView, revealView2;
    TextView PreviewLabel;
    boolean mKeyguardShowing = false;
    boolean previewMode = false;
    android.os.Handler handler;
    int maxX, maxY;
    Context mContext;
    boolean doubleToConfirm = false;

    boolean HookShutdownThread = false;
    boolean DeepXposedLogging = false;
    boolean HideOnClick = false;
    boolean LoadAppIcons = true;
    boolean RoundAppIcons = false;
    boolean ColorizeNonStockIcons = false;
    float GraphicsPadding = 0;

    String sStyleName = "Material";
    XposedDialog powerDialog;

    public BroadcastReceiver mReceiver;

    Activity mActivity;
    FrameLayout mActivityRootView;

    ImageLoader imageLoader;
    boolean ImgLoader_Loaded;

    KeyguardManager mKeyguardManger;
    KeyguardManager.KeyguardLock mKeyguardLock;
    int backgroundColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        previewMode = getIntent().getBooleanExtra("previewmode", false);
        if (!previewMode) {
            CustomActivityOnCrash.install(getApplicationContext());
            CustomActivityOnCrash.setRestartActivityClass(XposedMainActivity.class);
            ACRA.init(getApplication());
        }

        mContext = getApplicationContext();
        preferences = getSharedPreferences(MainActivity.class.getPackage().getName() + "_preferences", 0);
        colorPrefs = getSharedPreferences("colors", 0);
        orderPrefs = getSharedPreferences("visibilityOrder", 0);
        animationPrefs = getSharedPreferences("animations", 0);

        DeepXposedLogging = preferences.getBoolean("DeepXposedLogging", false);
        HideOnClick = preferences.getBoolean("HideOnClick", false);
        LoadAppIcons = preferences.getBoolean("LoadAppIcons", true);
        RoundAppIcons = preferences.getBoolean("RoundAppIcons", false);
        ColorizeNonStockIcons = preferences.getBoolean("ColorizeNonStockIcons", false);
        GraphicsPadding = preferences.getFloat("GraphicsPadding", 0);

        sStyleName = preferences.getString("DialogTheme", "Material");

        setTheme(R.style.TransparentApp);
        int TransFlag = 0;
        if(Build.VERSION.SDK_INT >= 19) {
            TransFlag = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
        }
        getWindow().addFlags(TransFlag |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
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
        super.onCreate(savedInstanceState);

        //Bitmap map=helper.takeScreenShot(XposedMainActivity.this);

        //Bitmap fast=helper.fastblur(map, 10);
        //final Drawable draw=new BitmapDrawable(getResources(),fast);
        //getWindow().setBackgroundDrawable(draw);

        setContentView(R.layout.activity_main_xposed);

        mActivity = XposedMainActivity.this;
        mActivityRootView = (FrameLayout) findViewById(R.id.activitymainxposedFrameLayout_Root);

        FrameLayout mFragmentHolder = (FrameLayout) findViewById(R.id.powerfragment_holder);
        mFragmentHolder.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                powerDialog.SubDialogs.clear();
                powerDialog.dismissThis();
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
        revealView2 = (CircularRevealView) findViewById(R.id.reveal2);

        PreviewLabel = (TextView) findViewById(R.id.PreviewLable);
        if (previewMode)
            PreviewLabel.setVisibility(View.VISIBLE);

        Display mdisp = getWindowManager().getDefaultDisplay();
        Point mdispSize = new Point();
        mdisp.getSize(mdispSize);
        maxX = mdispSize.x;
        maxY = mdispSize.y;

        final int color = Color.parseColor(colorPrefs.getString("Reveal_Backgroundcolor", "#8800bcd4"));
        final Point p = new Point(maxX / 2, maxY / 2);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context p1, Intent p2) {
                // TODO: Implement this method
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
        };

        mContext.registerReceiver(mReceiver, filter);

        initImageLoader();

        if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[1]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
            final Animation anim = helper.getAnimation(mContext, animationPrefs, 0, false);
            final int speed = (int) anim.getDuration();
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


            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showPowerDialog();
                }
            }, Math.max(speed - 150, 0));
        } else {
            revealView.reveal(p.x, p.y, color, 0, 0, null);
            showPowerDialog();
        }
    }

    @Override
    protected void onStart() {
        // TODO: Implement this method
        super.onStart();
        getWindow().getAttributes().windowAnimations = R.style.PopUpDialogAnimation_Window;
    }

    @Override
    protected void onPause() {
        // TODO: Implement this method
        super.onPause();
        //powerDialog.dismiss();
    }

    private void showPowerDialog() {
        try {
            FragmentManager fm = getFragmentManager();
            powerDialog = new XposedDialog();
            powerDialog.setHost(this);
            powerDialog.setPreferences(preferences, colorPrefs, orderPrefs, animationPrefs);
            powerDialog.setConfiguration(previewMode,HideOnClick, mKeyguardShowing, LoadAppIcons, sStyleName, GraphicsPadding, ColorizeNonStockIcons, imageLoader, DeepXposedLogging);
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

    public void revealFromTop() {
        final int color = Color.parseColor(colorPrefs.getString("ActionReveal_Backgroundcolor", "#ffffffff"));

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

    public void revealToTop() {
        final int color = Color.parseColor("#8800bcd4");

        final Point p = new Point(maxX / 2, maxY / 2);

        revealView.reveal(p.x, p.y, color, 0, 340, null);


    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        if (powerDialog.canDismiss || previewMode) {
        }
    }

    @Override
    public void onBackPressed() {
        // TODO: Implement this method
        if (powerDialog.canDismiss || previewMode) {
            powerDialog.dismissThis();
            //super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        if (mReceiver != null) {
            try {
                mContext.unregisterReceiver(mReceiver);
            } catch (Throwable t) {
                Log.e("NPM:dialog", "Failed to unregister broadcastreceiver.", t);
            }
        }
        super.finish();
    }

    public void dismissThis() {
        if (powerDialog.canDismiss || previewMode) {
            final Point p = new Point(maxX / 2, maxY / 2);

            if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[1]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                final int speed;
                Animation anim = helper.getAnimation(mContext, animationPrefs, 0, true);
                speed = (int) anim.getDuration();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[1]) == 1) {
                            revealView.hide(p.x, p.y, backgroundColor, 0, speed, null);
                        } else {
                            //revealView.hide(p.x, p.y, backgroundColor, 0, 0, null);
                            Animation anim = helper.getAnimation(mContext, animationPrefs, 0, true);
                            if (revealView2.getVisibility() == View.VISIBLE) {
                                revealView2.startAnimation(anim);
                            } else {
                                revealView.startAnimation(anim);
                            }
                        }
                    }
                }, 0);
                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mKeyguardShowing) {
                            //mKeyguardLock.reenableKeyguard();
                        }
                        mActivity.finish();
                        mActivity.overridePendingTransition(0, 0);
                    }
                }, speed);
            } else {
                mActivity.finish();
                mActivity.overridePendingTransition(0, 0);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO: Implement this method
        super.onConfigurationChanged(newConfig);
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
                    .discCache(discCache)
                    .memoryCache(memoryCache);

            ImageLoaderConfiguration config = builder.build();
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(config);
            ImgLoader_Loaded = true;
            Log.d("ImageLoader", "Loaded!");
        } catch (Exception e) {
            Log.e("ImageLoader", "Load failed, code:" + e);
        }
    }

}

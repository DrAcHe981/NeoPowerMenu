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
import java.util.*;
import org.acra.*;

import de.NeonSoft.neopowermenu.R;
import de.NeonSoft.neopowermenu.Preferences.*;
import android.view.animation.*;
import android.view.View.*;

/**
 * Created by naman on 20/03/15.
 */

public class XposedMainActivity extends Activity implements DialogInterface.OnDismissListener {

		public static SharedPreferences preferences;
		public static SharedPreferences colorPrefs;
		public static SharedPreferences orderPrefs;
		public static SharedPreferences animationPrefs;
    private static CircularRevealView revealView, revealView2;
		private static TextView PreviewLabel;
    private static int backgroundColor;
		public static boolean mKeyguardShowing = false;
		public static boolean previewMode = false;
    android.os.Handler handler;
    static int maxX, maxY;
		public static Context mContext;
		public static boolean doubleToConfirm = false;

		public static boolean HookShutdownThread = false;

		public static String sStyleName = "Material";
		XposedDialog powerDialog;
		
		public static BroadcastReceiver mReceiver;
		
		private static boolean mBlurEnabled;
		private static int mBlurScale;
		private static int mBlurRadius;
		private static BlurUtils mBlurUtils;
		private static ColorFilter mColorFilter;
		private static int mBlurDarkColorFilter;
		private static int mBlurMixedColorFilter;
		private static int mBlurLightColorFilter;
		private static Activity mActivity;
		private static FrameLayout mActivityRootView;

		public static ImageLoader imageLoader;
		public static boolean ImgLoader_Loaded;
		
		KeyguardManager mKeyguardManger;
		KeyguardManager.KeyguardLock mKeyguardLock;
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
				previewMode = getIntent().getBooleanExtra("previewmode",false);
				if(!previewMode) {
						CustomActivityOnCrash.install(getApplicationContext());
						CustomActivityOnCrash.setRestartActivityClass(XposedMainActivity.class);
						ACRA.init(getApplication());
				}
				
				mContext =getApplicationContext();
				preferences = getSharedPreferences(MainActivity.class.getPackage().getName() + "_preferences",Context.MODE_WORLD_READABLE);
				colorPrefs = getSharedPreferences("colors", Context.MODE_WORLD_READABLE);
				orderPrefs = getSharedPreferences("visibilityOrder",Context.MODE_WORLD_READABLE);
				animationPrefs = getSharedPreferences("animations", Context.MODE_WORLD_READABLE);
				
				mBlurScale = preferences.getInt("blurScale",20);
				mBlurRadius = preferences.getInt("blurRadius",3);
				mBlurDarkColorFilter = preferences.getInt("blurDarkColorFilter",Color.LTGRAY);
				mBlurMixedColorFilter = preferences.getInt("blurMixedColorFilter",Color.GRAY);
				mBlurLightColorFilter = preferences.getInt("blurLightColorFilter",Color.DKGRAY);
				mBlurEnabled = preferences.getBoolean("blurEnabled",false);
				
				sStyleName = preferences.getString("DialogTheme","Material");
				
        setTheme(R.style.TransparentApp);
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | 
														WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION |
														WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
														WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM |
														WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
														WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
				
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
				//getWindow().getDecorView().setSystemUiVisibility(View.STATUS_BAR_DISABLE_EXPAND | View.STATUS_BAR_DISABLE_HOME | View.STATUS_BAR_DISABLE_RECENT);
				//mKeyguardShowing = getIntent().getBooleanExtra("mKeyguardShowing",false);
				mKeyguardManger = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
				mKeyguardLock = mKeyguardManger.newKeyguardLock(KEYGUARD_SERVICE);
				mKeyguardShowing = mKeyguardManger.isDeviceLocked();
        if (mKeyguardShowing) {
						//Log.d("NeoPowerMenu","Showing in Keyguard");
						if (!preferences.getBoolean("ShowOnLockScreen",true)) { 
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
								public void onClick(View p1)
								{
										// TODO: Implement this method
										XposedDialog.dismissThis();
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
				if(XposedMainActivity.previewMode)
						PreviewLabel.setVisibility(View.VISIBLE);
						
        Display mdisp = getWindowManager().getDefaultDisplay();
        Point mdispSize = new Point();
        mdisp.getSize(mdispSize);
        maxX = mdispSize.x;
        maxY = mdispSize.y;

        final int color = Color.parseColor(colorPrefs.getString("Reveal_Backgroundcolor","#8800bcd4"));
        final Point p = new Point(maxX / 2, maxY / 2);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
				
				mReceiver = new BroadcastReceiver() {

						@Override
						public void onReceive(Context p1, Intent p2)
						{
								// TODO: Implement this method
								if(XposedDialog.canDismiss || previewMode) {
								if(p2.getAction().equalsIgnoreCase(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
										powerDialog.dismissThis();
								} else if(p2.getAction().equalsIgnoreCase(Intent.ACTION_SCREEN_OFF)) {
										finish();
								}
								}
						}
				};
				
				mContext.registerReceiver(mReceiver,filter);
				
				initImageLoader();
				
				if(animationPrefs.getInt(PreferencesAnimationsFragment.names[0]+"_type",PreferencesAnimationsFragment.defaultTypes[0]) != mContext.getString(R.string.animations_Types).split("\\|").length-1) {
						final Animation anim = helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 0, false);
						final int speed = (int) anim.getDuration();
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
								if (animationPrefs.getInt(PreferencesAnimationsFragment.names[0]+"_type",PreferencesAnimationsFragment.defaultTypes[0]) == 1) {
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
        }, Math.max(speed-150,0));
				} else {
						revealView.reveal(p.x, p.y, color, 0, 0, null);
						showPowerDialog();
				}
    }
		
		public static void startBlurTask() {
				if (mActivityRootView != null)
						mActivityRootView.setBackground(null);
				
				BlurTask.setBlurTaskCallback(new BlurTaskCallback() {

								@Override
								public void blurTaskDone(final Bitmap blurredBitmap)
								{
										// TODO: Implement this method
										if (blurredBitmap != null) {
												if (mActivityRootView != null) {
														Toast.makeText(mContext,"Blurring done, setting...",Toast.LENGTH_SHORT).show();
														mActivityRootView.post(new Runnable() {
																
																@Override
																public void run() {
																		BitmapDrawable blurredDrawable = new BitmapDrawable(blurredBitmap);
																		blurredDrawable.setColorFilter(mColorFilter);
																		
																		mActivityRootView.setBackground(blurredDrawable);
																}
														});
												}
										} else {
												Toast.makeText(mContext,"Failed to blur image...",Toast.LENGTH_SHORT).show();
										}
								}

								@Override
								public void dominantColor(int color)
								{
										// TODO: Implement this method
										double lightness = DisplayUtils.getColorLightness(color);
										
										if(lightness >= 0.0 && color <= 1.0) {
												if(lightness <= 0.33) {
														mColorFilter = new PorterDuffColorFilter(mBlurLightColorFilter, PorterDuff.Mode.MULTIPLY);
												} else if (lightness >= 0.34 && lightness <= 0.66) {
														mColorFilter = new PorterDuffColorFilter(mBlurMixedColorFilter, PorterDuff.Mode.MULTIPLY);
												} else if (lightness >= 0.67 && lightness <= 1.0) {
														mColorFilter = new PorterDuffColorFilter(mBlurDarkColorFilter, PorterDuff.Mode.MULTIPLY);
												}
										} else {
												mColorFilter = new PorterDuffColorFilter(mBlurMixedColorFilter, PorterDuff.Mode.MULTIPLY);
										}
								}
						});
						
						BlurTask.setBlurEngine(BlurEngine.RenderScriptBlur);
						
						new BlurTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}

		@Override
		protected void onStart()
		{
				// TODO: Implement this method
				super.onStart();
				getWindow().getAttributes().windowAnimations = R.style.PopUpDialogAnimation_Window;
		}
		
		@Override
		protected void onPause()
		{
				// TODO: Implement this method
				if(mReceiver != null) {
				try {
						mContext.unregisterReceiver(mReceiver);
				} catch (Throwable t) {
						Log.e("NPM:dialog","Failed to unregister broadcastreceiver.",t);
				}
				}
				super.onPause();
				//powerDialog.dismiss();
		}
		
    private void showPowerDialog() {
				try {
        FragmentManager fm = getFragmentManager();
        powerDialog = new XposedDialog();
				//if(sStyleName.equalsIgnoreCase("Material")) {
						//powerDialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ThemeDialogBaseLight);
						//} else if (sStyleName.equalsIgnoreCase("Material (Fullscreen)")) {
						//		powerDialog.setStyle(DialogFragment.STYLE_NO_TITLE,R.style.TransparentApp);
						//}
						fm.beginTransaction().add(R.id.powerfragment_holder,powerDialog).commitAllowingStateLoss();
						//powerDialog.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.ThemeDialogBaseLight);
        //powerDialog.show(fm, "fragment_power");
				} catch (Throwable t) {
						Log.e("NeoPowerMenu","Failed to show power menu: "+t.toString());
				}
    }

    public static void revealFromTop() {
        final int color = Color.parseColor(colorPrefs.getString("ActionReveal_Backgroundcolor","#ffffffff"));

        final Point p = new Point(maxX / 2, maxY / 2);

				if(animationPrefs.getInt(PreferencesAnimationsFragment.names[0]+"_type",PreferencesAnimationsFragment.defaultTypes[0]) != mContext.getString(R.string.animations_Types).split("\\|").length-1) {
						Animation anim = helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 0, false);
						int speed = (int) anim.getDuration();
				if (animationPrefs.getInt(PreferencesAnimationsFragment.names[0]+"_type",PreferencesAnimationsFragment.defaultTypes[0]) == 1) {
						revealView.reveal(p.x, p.y, color, 0, speed, null);
				} else {
						//revealView.reveal(p.x, p.y, color, 0, 0, null);
						revealView2.reveal(p.x, p.y, color, 0, 0, null);
						Animation animOut = null;
						if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[0]+"_type",PreferencesAnimationsFragment.defaultTypes[0]) == 0) {
								animOut = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
						} else if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[0]+"_type",PreferencesAnimationsFragment.defaultTypes[0]) == 2) {
								animOut = AnimationUtils.loadAnimation(mContext, R.anim.anim_slide_out_top);
						} else if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[0]+"_type",PreferencesAnimationsFragment.defaultTypes[0]) == 3) {
								animOut = AnimationUtils.loadAnimation(mContext, R.anim.anim_slide_out_left);
						} else if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[0]+"_type",PreferencesAnimationsFragment.defaultTypes[0]) == 4) {
								animOut = AnimationUtils.loadAnimation(mContext, R.anim.anim_slide_out_right);
						} else if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[0]+"_type",PreferencesAnimationsFragment.defaultTypes[0]) == 5) {
								animOut = AnimationUtils.loadAnimation(mContext, R.anim.anim_slide_out_bottom);
						} else if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[0]+"_type",PreferencesAnimationsFragment.defaultTypes[0]) == 6) {
								animOut = AnimationUtils.loadAnimation(mContext, R.anim.scale_out_down);
						} else if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[0]+"_type",PreferencesAnimationsFragment.defaultTypes[0]) == 7) {
								animOut = AnimationUtils.loadAnimation(mContext, R.anim.scale_out_up);
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

    public static void revealToTop() {
        final int color = Color.parseColor("#8800bcd4");

        final Point p = new Point(maxX / 2, maxY / 2);

        revealView.reveal(p.x, p.y, color, 0, 340, null);


    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
				if(XposedDialog.canDismiss || previewMode) {
				}
    }

		@Override
		public void onBackPressed()
		{
				// TODO: Implement this method
				if(XposedDialog.canDismiss || previewMode) {
						XposedDialog.dismissThis();
						//super.onBackPressed();
				}
		}
		
		public static void dismissThis() {
				if(XposedDialog.canDismiss || previewMode) {
        final Point p = new Point(maxX / 2, maxY / 2);


				if (animationPrefs.getInt(PreferencesAnimationsFragment.names[0] + "_type", PreferencesAnimationsFragment.defaultTypes[0]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1)
				{
						final int speed;
						Animation anim = helper.getAnimation(mContext, animationPrefs, 0, true);
						speed = (int) anim.getDuration();
						Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
										@Override
										public void run()
										{

												if (animationPrefs.getInt(PreferencesAnimationsFragment.names[0] + "_type", PreferencesAnimationsFragment.defaultTypes[0]) == 1)
												{
														revealView.hide(p.x, p.y, backgroundColor, 0, speed, null);
												}
												else
												{
														//revealView.hide(p.x, p.y, backgroundColor, 0, 0, null);
														Animation anim = helper.getAnimation(mContext, animationPrefs, 0, true);
														if(revealView2.getVisibility()==View.VISIBLE) {
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
										public void run()
										{
												if (mKeyguardShowing)
												{
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
		public void onConfigurationChanged(Configuration newConfig)
		{
				// TODO: Implement this method
				super.onConfigurationChanged(newConfig);
		}
		
		public static class BlurTask extends AsyncTask<Void, Void, Bitmap> {


				private static int[] mScreenDimens;

				private static Bitmap mScreenBitmap;

				private static BlurEngine mBlurEngine;

				private static BlurTaskCallback mCallback;


				public static void setBlurEngine(BlurEngine blurEngine) {


						mBlurEngine = blurEngine;


				}


				public static void setBlurTaskCallback(BlurTaskCallback callBack) {


						mCallback = callBack;


				}


				public static int[] getRealScreenDimensions() {


						return mScreenDimens;


				}


				public static Bitmap getLastBlurredBitmap() {


						return mScreenBitmap;


				}


				@Override

				protected void onPreExecute() {


// obtém o tamamho real da tela

						mScreenDimens = DisplayUtils.getRealScreenDimensions(mContext);


// obtém a screenshot da tela com escala reduzida

						mScreenBitmap = DisplayUtils.takeSurfaceScreenshot(mContext, mBlurScale);


				}


				@Override

				protected Bitmap doInBackground(Void... arg0) {


						try {


// continua ?

								if (mScreenBitmap == null) {
										Log.e("NPM","mScreenBitmap == null");
										return null;
								}


// calback

								mCallback.dominantColor(DisplayUtils.getDominantColorByPixelsSampling(mScreenBitmap, 10, 10));


// blur engine

								if (mBlurEngine == BlurEngine.RenderScriptBlur) {


										mScreenBitmap = mBlurUtils.renderScriptBlur(mScreenBitmap, mBlurRadius);


								} else if (mBlurEngine == BlurEngine.StackBlur) {


										mScreenBitmap = mBlurUtils.stackBlur(mScreenBitmap, mBlurRadius);


								} else if (mBlurEngine == BlurEngine.FastBlur) {


										mBlurUtils.fastBlur(mScreenBitmap, mBlurRadius);


								}


								return mScreenBitmap;


						} catch (OutOfMemoryError e) {


// erro
								Log.e("NPM","OutOfMemoryError: "+ e);
								return null;
	


						}

				}


				@Override

				protected void onPostExecute(Bitmap bitmap) {


						if (bitmap != null) {


// -----------------------------

// bitmap criado com sucesso !!!

// -----------------------------


// callback

								mCallback.blurTaskDone(bitmap);


						} else {


// --------------------------

// erro ao criar o bitmap !!!

// --------------------------


// callback

								mCallback.blurTaskDone(null);


						}

				}

		}


		private void initImageLoader() {
				try {
						String CACHE_DIR = getCacheDir().getPath() + "/.temp_tmp";
						new File(CACHE_DIR).mkdirs();

						DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
								.cacheOnDisc(true).cacheInMemory(true).imageScaleType(ImageScaleType.EXACTLY)
								.bitmapConfig(Bitmap.Config.RGB_565).build();

						LruMemoryCache memoryCacheCore = new LruMemoryCache(5*1024*1024);
						LimitedAgeMemoryCache memoryCache = new LimitedAgeMemoryCache(memoryCacheCore, 15*60);
						LruDiskCache discCache = new LruDiskCache(new File(CACHE_DIR),new URLFileNameGenerator(),250*1024*1024);
						ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
								getBaseContext())
								.defaultDisplayImageOptions(defaultOptions)
								.discCache(discCache)
								.memoryCache(memoryCache);

						ImageLoaderConfiguration config = builder.build();
						imageLoader = ImageLoader.getInstance();
						imageLoader.init(config);
						ImgLoader_Loaded = true;
						Log.d("ImageLoader","Loaded!");
				} catch (Exception e) {
						Log.e("ImageLoader","Load failed, code:"+e);
				}
		}

}

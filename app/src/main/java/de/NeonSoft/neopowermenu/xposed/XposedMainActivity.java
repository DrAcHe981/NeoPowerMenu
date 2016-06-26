package de.NeonSoft.neopowermenu.xposed;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.*;
import android.util.*;
import android.animation.*;

import de.NeonSoft.neopowermenu.helpers.BlurUtils.BlurEngine;
import de.NeonSoft.neopowermenu.helpers.BlurUtils.BlurTaskCallback;

/**
 * Created by naman on 20/03/15.
 */
public class XposedMainActivity extends Activity implements DialogInterface.OnDismissListener {

		public static SharedPreferences preferences;
		public static SharedPreferences orderPrefs;
    private static CircularRevealView revealView;
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
		
		BroadcastReceiver mReceiver;
		
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
		
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {

				mContext =getApplicationContext();
				preferences = getSharedPreferences(MainActivity.class.getPackage().getName() + "_preferences",Context.MODE_WORLD_READABLE);
				orderPrefs = getSharedPreferences("visibilityOrder",Context.MODE_WORLD_READABLE);
				
				mBlurScale = preferences.getInt("blurScale",20);
				mBlurRadius = preferences.getInt("blurRadius",3);
				mBlurDarkColorFilter = preferences.getInt("blurDarkColorFilter",Color.LTGRAY);
				mBlurMixedColorFilter = preferences.getInt("blurMixedColorFilter",Color.GRAY);
				mBlurLightColorFilter = preferences.getInt("blurLightColorFilter",Color.DKGRAY);
				mBlurEnabled = preferences.getBoolean("blurEnabled",false);
				
				sStyleName = preferences.getString("DialogTheme","Material");
				
        setTheme(R.style.TransparentApp);
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
				
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
				previewMode = getIntent().getBooleanExtra("previewmode",false);
				//mKeyguardShowing = getIntent().getBooleanExtra("mKeyguardShowing",false);
				KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
				mKeyguardShowing = km.isKeyguardLocked();
        if (mKeyguardShowing) {
						//Log.d("NeoPowerMenu","Showing in Keyguard");
						if (!preferences.getBoolean("ShowOnLockScreen",true)) { 
								finish();
						}
            getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        } else {
						//Log.d("NeoPowerMenu","Showing Normal");
            getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
        }
				getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        super.onCreate(savedInstanceState);

				//Bitmap map=helper.takeScreenShot(XposedMainActivity.this);

				//Bitmap fast=helper.fastblur(map, 10);
				//final Drawable draw=new BitmapDrawable(getResources(),fast);
				//getWindow().setBackgroundDrawable(draw);
				
        setContentView(R.layout.activity_main_xposed);
				
				mActivity = XposedMainActivity.this;
				mActivityRootView = (FrameLayout) findViewById(R.id.activitymainxposedFrameLayout_Root);
				
				mBlurUtils = new BlurUtils(getApplicationContext());
				Bitmap lastBlurredBitmap = BlurTask.getLastBlurredBitmap();
				
				if(lastBlurredBitmap!= null) {
						//Toast.makeText(mContext,"Found previous blur, using it!",Toast.LENGTH_SHORT).show();
						BitmapDrawable blurredDrawable = new BitmapDrawable(lastBlurredBitmap);
						blurredDrawable.setColorFilter(mColorFilter);
						mActivityRootView.setBackground(blurredDrawable);
				} else {
						//Toast.makeText(mContext,"Starting new blur task...",Toast.LENGTH_SHORT).show();
						//startBlurTask();
				}
				
        revealView = (CircularRevealView) findViewById(R.id.reveal);

				PreviewLabel = (TextView) findViewById(R.id.PreviewLable);
				if(XposedMainActivity.previewMode)
						PreviewLabel.setVisibility(View.VISIBLE);
						
        Display mdisp = getWindowManager().getDefaultDisplay();
        Point mdispSize = new Point();
        mdisp.getSize(mdispSize);
        maxX = mdispSize.x;
        maxY = mdispSize.y;

        final int color = Color.parseColor(preferences.getString("Reveal_Backgroundcolor","#8800bcd4"));
        final Point p = new Point(maxX / 2, maxY / 2);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
				
				mReceiver = new BroadcastReceiver() {

						@Override
						public void onReceive(Context p1, Intent p2)
						{
								// TODO: Implement this method
								if(p2.getAction().equalsIgnoreCase(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
										powerDialog.dismiss();
								} else if(p2.getAction().equalsIgnoreCase(Intent.ACTION_SCREEN_OFF)) {
										finish();
								}
						}
				};
				
				registerReceiver(mReceiver,filter);
				
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                revealView.reveal(p.x, p.y, color, 0, 340, null);
            }
        }, 50);


        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showPowerDialog();
            }
        }, 240);


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
		protected void onPause()
		{
				// TODO: Implement this method
				super.onPause();
				//powerDialog.dismiss();
		}
		
    private void showPowerDialog() {
				try {
        FragmentManager fm = getFragmentManager();
        powerDialog = new XposedDialog();
				if(sStyleName.equalsIgnoreCase("Material")) {
						powerDialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ThemeDialogBaseLight);
						} else if (sStyleName.equalsIgnoreCase("Material (Fullscreen)")) {
								powerDialog.setStyle(DialogFragment.STYLE_NO_TITLE,R.style.TransparentApp);
						}
						//fm.beginTransaction().add(R.id.powerfragment_holder,powerDialog).commit();
						//powerDialog.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.ThemeDialogBaseLight);
        powerDialog.show(fm, "fragment_power");
				} catch (Throwable t) {
						Log.e("NeoPowerMenu","Failed to show power menu: "+t.toString());
				}
    }

    public static void revealFromTop() {
        final int color = Color.parseColor(preferences.getString("ActionReveal_Backgroundcolor","#ffffffff"));

        final Point p = new Point(maxX / 2, maxY / 2);

        revealView.reveal(p.x, p.y, color, 0, 340, null);


    }

    public static void revealToTop() {
        final int color = Color.parseColor("#8800bcd4");

        final Point p = new Point(maxX / 2, maxY / 2);

        revealView.reveal(p.x, p.y, color, 0, 340, null);


    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
				if(XposedDialog.canDismiss || previewMode) {
						mContext.unregisterReceiver(mReceiver);
        final Point p = new Point(maxX / 2, maxY / 2);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                revealView.hide(p.x, p.y, backgroundColor, 0, 340, null);
            }
        }, 300);
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                overridePendingTransition(0, 0);
            }
        }, 500);
				}
    }

		@Override
		public void onBackPressed()
		{
				// TODO: Implement this method
				if(XposedDialog.canDismiss || previewMode) {
						super.onBackPressed();
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

		
		
}

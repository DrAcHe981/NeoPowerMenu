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

/**
 * Created by naman on 20/03/15.
 */
public class XposedMainActivity extends Activity implements DialogInterface.OnDismissListener {

		public static SharedPreferences preferences;
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
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {

				mContext =getApplicationContext();
				preferences = getSharedPreferences(MainActivity.class.getPackage().getName() + "_preferences",Context.MODE_WORLD_READABLE);
				
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
				
        setContentView(R.layout.activity_main_xposed);
				
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
		
}

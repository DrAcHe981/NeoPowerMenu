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

/**
 * Created by naman on 20/03/15.
 */
public class XposedMainActivity extends Activity implements DialogInterface.OnDismissListener {

		public static SharedPreferences preferences;
    private CircularRevealView revealView;
		private TextView PreviewLabel;
    private int backgroundColor;
		public static boolean mKeyguardShowing = false;
		public static boolean previewMode = false;
    android.os.Handler handler;
    int maxX, maxY;
		public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

				mContext =getApplicationContext();
				preferences = getSharedPreferences(MainActivity.class.getPackage().getName() + "_preferences",Context.MODE_WORLD_READABLE);
				
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

        final int color = Color.parseColor(preferences.getString("RevealBackground","#8800bcd4"));
        final Point p = new Point(maxX / 2, maxY / 2);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                revealView.reveal(p.x, p.y, color, 2, 440, null);
            }
        }, 100);


        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showPowerDialog();
            }
        }, 440);


    }

    private void showPowerDialog() {
				try {
        FragmentManager fm = getFragmentManager();
        XposedDialog powerDialog = new XposedDialog();
						powerDialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ThemeDialogBaseLight);
						//fm.beginTransaction().add(R.id.powerfragment_holder,powerDialog).commit();
						//powerDialog.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.ThemeDialogBaseLight);
        powerDialog.show(fm, "fragment_power");
				} catch (Throwable t) {
						Log.e("NeoPowerMenu","Failed to show power menu: "+t.toString());
				}
    }

    public void revealFromTop() {
        final int color = Color.parseColor(preferences.getString("ActionRevealBackground","#ffffffff"));

        final Point p = new Point(maxX / 2, maxY / 2);

        revealView.reveal(p.x, p.y, color, 2, 440, null);


    }

    public void revealToTop() {
        final int color = Color.parseColor("#8800bcd4");

        final Point p = new Point(maxX / 2, maxY / 2);

        revealView.reveal(p.x, p.y, color, 2, 440, null);


    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
				if(XposedDialog.canDismiss || previewMode) {
        final Point p = new Point(maxX / 2, maxY / 2);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
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

package de.NeonSoft.neopowermenu;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.os.*;
import android.preference.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.Preferences.*;
import de.NeonSoft.neopowermenu.helpers.*;
import de.NeonSoft.neopowermenu.xposed.*;
import eu.chainfire.libsuperuser.*;
import java.io.*;

import android.support.v7.widget.Toolbar;


public class MainActivity extends AppCompatActivity {
		
		public static SharedPreferences preferences;
		public static Context context;
		public static LayoutInflater inflater;
		private LinearLayout LinearLayout_ShowPreview;
		public static boolean RootAvailable;
		public static android.app.FragmentManager fragmentManager;
		public static String visibleFragment = "Main";

    private static final int BG_PRIO = android.os.Process.THREAD_PRIORITY_BACKGROUND;

		String versionName = "1.0";
		int versionCode = -1;
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        setTheme(R.style.ThemeBaseDark);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
				
				File presetDir = new File(getFilesDir()+"/presets/");
				presetDir.mkdirs();
				
				context = getApplicationContext();
				inflater = getLayoutInflater();
				
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.app_name);
				try
				{
						versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
						versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
				}
				catch (Throwable e)
				{
						Log.e("Failed to get Version infos: ",e.getMessage());
				}
				getSupportActionBar().setSubtitle(versionName);

				LinearLayout_ShowPreview = (LinearLayout) findViewById(R.id.activitymainLinearLayout_ShowPreviw);
				
        Fragment fragment = new PreferencesPartFragment();
        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
						.replace(R.id.pref_container, fragment).commit();
						
        new Thread(new Runnable() {
								@Override
								public void run() {
										helper.setThreadPrio(BG_PRIO);

										if (Shell.SU.available()) {
												new Handler(Looper.getMainLooper()).post(new Runnable() {
																@Override
																public void run() {
																		PreferencesPartFragment.rootAvailable();
																}
														});
										}
								}
						}).start();

				LinearLayout_ShowPreview.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										launchPowerMenu();
								}
						});
    }

		@Override
		public void onBackPressed()
		{
				if (visibleFragment.equalsIgnoreCase("CustomColors")) {
						fragmentManager.beginTransaction().replace(R.id.pref_container,new PreferencesPartFragment()).commit();
				} else if (visibleFragment.equalsIgnoreCase("VisibilityOrder")) {
						fragmentManager.beginTransaction().replace(R.id.pref_container,new PreferencesPartFragment()).commit();
				} else if (visibleFragment.equalsIgnoreCase("Main")) {
						super.onBackPressed();
				}
		}

    private void launchPowerMenu() {
				context = getApplicationContext();
				Intent intent = new Intent(context, XposedMainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				intent.putExtra("previewmode",true);
				context.startActivity(intent);
    }

		@Override
		public void onConfigurationChanged(Configuration newConfig)
		{
				// TODO: Implement this method
				super.onConfigurationChanged(newConfig);
		}
		
}

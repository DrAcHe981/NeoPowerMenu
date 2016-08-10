package de.NeonSoft.neopowermenu;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.content.res.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
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
import com.theartofdev.edmodo.cropper.*;
import de.NeonSoft.neopowermenu.Preferences.*;
import de.NeonSoft.neopowermenu.helpers.*;
import de.NeonSoft.neopowermenu.permissionsScreen.*;
import de.NeonSoft.neopowermenu.xposed.*;
import java.io.*;
import java.util.*;
import org.acra.*;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;

public class MainActivity extends AppCompatActivity
{

		public static boolean LOCALTESTSERVER = false; // use local server 127.0.0.1:8080 or online www.Neon-Soft.de
		public static int TIMEOUT_MILLISEC = 10000; // = 10 seconds, Timeout for various network scripts

		public static String deviceUniqeId = "none";

		public static SharedPreferences preferences;
		public static SharedPreferences colorPrefs;
		public static SharedPreferences orderPrefs;
		public static Context context;
		public static Activity activity;
		public static IBinder windowToken;
		public static LayoutInflater inflater;
		public static boolean RootAvailable;
		public static android.support.v4.app.FragmentManager fragmentManager;
		public static String visibleFragment = "Main";

    public static final int BG_PRIO = android.os.Process.THREAD_PRIORITY_BACKGROUND;

		public static String versionName = "v1.0";
		public static int versionCode = -1;

		LinearLayout actionBarHolder;

		public static actionBar actionbar;

		/*<!-- Internal needed Hook version to check if reboot is needed --!>*/
		public static int neededModuleActiveVersion = 23;

		public static String ImportUrl = null;

		public static Animation anim_fade_out;
		public static Animation anim_fade_in;
		public static Animation anim_fade_slide_out_right;
		public static Animation anim_fade_slide_in_right;

		public static OnClickListener previewOnClickListener;

		AlertDialog.Builder adb;

		// Session data
		public static boolean loggedIn = false;
		public static String usernameemail = "";
		public static String password = "";
		public static String accountUniqeId = "";

		public static String imagesstorage;
		public static ImageLoader imageLoader;
		public static boolean ImgLoader_Loaded;

		private String[] requieredDirs = {"presets","download","images","temp"};

    @Override
    protected void onCreate(Bundle savedInstanceState)
		{

				CustomActivityOnCrash.install(getApplicationContext());
				CustomActivityOnCrash.setRestartActivityClass(MainActivity.class);
				ACRA.init(getApplication());

				context = getApplicationContext();
				activity = getParent();
        preferences = getSharedPreferences(MainActivity.class.getPackage().getName() + "_preferences", Context.MODE_WORLD_READABLE);
				colorPrefs = getSharedPreferences("colors", Context.MODE_WORLD_READABLE);
				orderPrefs = getSharedPreferences("visibilityOrder", Context.MODE_WORLD_READABLE);

				/*if(getSharedPrefsFile(MainActivity.class.getPackage().getName()+"_preferences").exists()) {
				 Log.i("NPM:pC","Detected old preferences, converting...");
				 SharedPreferences oldPrefs = getSharedPreferences(MainActivity.class.getPackage().getName()+"_preferences", Context.MODE_WORLD_READABLE);
				 Map<String, ?> oldPrefsAll = oldPrefs.getAll();
				 if(!oldPrefsAll.isEmpty()) {
				 Object[] keys = oldPrefsAll.keySet().toArray();
				 Object[] values = oldPrefsAll.values().toArray();
				 String designSpace = "";
				 for(int x = 0; x < (String.format("%03d/%03d",oldPrefsAll.size(),oldPrefsAll.size())).length(); x++) {
				 designSpace = designSpace + " ";
				 }
				 for(int i = 0; i < oldPrefsAll.size(); i++) {
				 boolean unknown = false;
				 Log.i("NPM:pC",String.format("%03d/%03d",(i+1),oldPrefsAll.size())+" | "+keys[i]);
				 if(values[i].getClass().equals(String.class)) {
				 Log.i("NPM:pC",designSpace+" | Writing String...");
				 preferences.edit().putString((String) keys[i],(String) values[i]).commit();
				 } else if (values[i].getClass().equals(Integer.class)) {
				 Log.i("NPM:pC",designSpace+" | Writing Integer...");
				 preferences.edit().putInt((String) keys[i],(int) values[i]).commit();
				 } else if (values[i].getClass().equals(Boolean.class)) {
				 Log.i("NPM:pC",designSpace+" | Writing Boolean...");
				 preferences.edit().putBoolean((String) keys[i],(boolean) values[i]).commit();
				 } else {
				 unknown = true;
				 Log.i("NPM:pC",designSpace+" | Unknown type... ("+values[i].getClass()+")");
				 }
				 if(!unknown) {
				 Log.i("NPM:pC",designSpace+" \\_Converted.");
				 } else {
				 Log.i("NPM:pC",designSpace+" \\_Failed.");
				 }
				 }
				 }
				 Log.i("NPM:pC","Preferences convert complete, deleting old file...");
				 oldPrefs.edit().clear().commit();
				 if(getSharedPrefsFile(MainActivity.class.getPackage().getName()+"_preferences").exists()) {
				 if(getSharedPrefsFile(MainActivity.class.getPackage().getName()+"_preferences").delete()) {
				 Log.i("NPM:pC","Deleted.");
				 } else {
				 Log.i("NPM:pC","Failed to delete...");
				 }
				 }
				 }*/


				int orderCheck = 0;
				while (orderPrefs.getInt(orderCheck + "_item_type", -1) != -1)
				{
						orderCheck++;
				}
				if (orderCheck == 0)
				{
						orderPrefs.edit().putInt("0_item_type", visibilityOrderNew_ListAdapter.TYPE_NORMAL).commit();
						orderPrefs.edit().putString("0_item_title", "Shutdown").commit();
						orderPrefs.edit().putInt("1_item_type", visibilityOrderNew_ListAdapter.TYPE_NORMAL).commit();
						orderPrefs.edit().putString("1_item_title", "Reboot").commit();
						orderPrefs.edit().putInt("2_item_type", visibilityOrderNew_ListAdapter.TYPE_NORMAL).commit();
						orderPrefs.edit().putString("2_item_title", "SoftReboot").commit();
						orderPrefs.edit().putInt("3_item_type", visibilityOrderNew_ListAdapter.TYPE_MULTI).commit();
						orderPrefs.edit().putString("3_item1_title", "Recovery").commit();
						orderPrefs.edit().putString("3_item2_title", "Bootloader").commit();
						orderPrefs.edit().putString("3_item3_title", "SafeMode").commit();
				}
        setTheme(R.style.ThemeBaseDark);

				for (int folderCheck = 0; folderCheck < requieredDirs.length; folderCheck ++)
				{
						File check = new File(context.getFilesDir().getPath() + "/" + requieredDirs[folderCheck]);
						if (!check.exists() && !check.isDirectory())
						{
								check.mkdir();
						}
				}

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
				{
						getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
						getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkDarkTheme));
						getWindow().setNavigationBarColor(getResources().getColor(R.color.window_background_dark));
				}
				//getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

				anim_fade_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
				anim_fade_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
				anim_fade_slide_out_right = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_fade_slide_out_right);
				anim_fade_slide_in_right = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_fade_slide_in_right);

				File presetDir = new File(getFilesDir() + "/presets/");
				presetDir.mkdirs();

				Uri intentfilterdata = getIntent().getData();
				if (intentfilterdata != null)
				{
						try
						{
								ImportUrl = intentfilterdata.getScheme() + "://" + intentfilterdata.getPath();
						}
						catch (Exception e)
						{
								e.printStackTrace();
						}
				}

				context = getApplicationContext();
				inflater = getLayoutInflater();

				try
				{
						versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
						versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
				}
				catch (Throwable e)
				{
						versionName = "< unknown >";
						versionCode = -1;
						Log.e("Failed to get Version infos: ", e.getMessage());
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

				if (preferences.getBoolean("DontAskPermissionsAgain", false) || permissionsScreen.checkPermissions(MainActivity.this, permissionsScreen.permissions))
				{
						android.support.v4.app.Fragment fragment = new PreferencesPartFragment();
						fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
								.replace(R.id.pref_container, fragment).commit();
						if (ImportUrl != null)
						{
								fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container, new PreferencesPresetsFragment()).commit();
						}
				}
				else
				{
						fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container, new permissionsScreen()).commit();
				}
				previewOnClickListener = new OnClickListener() {

						@Override
						public void onClick(View p1)
						{
								// TODO: Implement this method
								actionbar.hideButton();
								launchPowerMenu();
						}
				};
				for (int i = 0;i < PreferencesColorFragment.ColorNames.length; i++)
				{
						if (PreferencesColorFragment.ColorNames[i][0] == ColorsListAdapter.TYPE_ITEM)
						{
								if (colorPrefs.getString(PreferencesColorFragment.ColorNames[i][1].toString(), "").isEmpty())
								{
										colorPrefs.edit().putString(PreferencesColorFragment.ColorNames[i][1].toString(), preferences.getString(PreferencesColorFragment.ColorNames[i][1].toString(), PreferencesColorFragment.lightPreset[i])).commit();
										preferences.edit().remove(PreferencesColorFragment.ColorNames[i][1].toString()).commit();
								}
						}
				}


				if ((deviceUniqeId = preferences.getString("userUniqeId", "none")).equalsIgnoreCase("none"))
				{
						Date date = new Date();
						deviceUniqeId = helper.md5Crypto(Build.MANUFACTURER + "-" + Build.MODEL + "-" + date.getYear() + "." + date.getMonth() + "." + date.getDay() + "-" + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds());
						preferences.edit().putString("userUniqeId", deviceUniqeId).commit();
						slideDownDialogFragment dialogFragment = new slideDownDialogFragment(this, MainActivity.fragmentManager);
						dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

										@Override
										public void onListItemClick(int position, String text)
										{
												// TODO: Implement this method
										}

										@Override
										public void onNegativeClick()
										{
												// TODO: Implement this method
										}

										@Override
										public void onNeutralClick()
										{
												// TODO: Implement this method
										}

										@Override
										public void onPositiveClick(Bundle resultBundle)
										{
												// TODO: Implement this method
										}

										@Override
										public void onTouchOutside()
										{
												// TODO: Implement this method
										}
								});
						dialogFragment.setText(getString(R.string.welcomeMsg));
						dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[0]);
						dialogFragment.showDialog(R.id.dialog_container);
				}
				actionbar.setButton(getString(R.string.PreviewPowerMenu), R.drawable.ic_action_launch, previewOnClickListener);
				actionbar.setAnimationsEnabled(true);
				//throw new RuntimeException("This is a test crash!");
    }

		@Override
		public void onBackPressed()
		{
				if (fragmentManager.findFragmentByTag(slideDownDialogFragment.dialogTag) != null)
				{
						/*Intent intent = new Intent();
						 intent.setAction(slideDownDialogFragment.dialogCloseCall);
						 context.sendBroadcast(intent);*/
						//Toast.makeText(context,"Canceling sDDF "+slideDownDialogFragmentHelper.dialogs.size()+"/"+slideDownDialogFragmentHelper.dialogs.size(),Toast.LENGTH_LONG).show();
						slideDownDialogFragment.dialogs.get(slideDownDialogFragment.dialogs.size() - 1).cancelDialog();
						return;
				}
				if (visibleFragment.equalsIgnoreCase("CustomColors") || visibleFragment.equalsIgnoreCase("Graphics"))
				{
						fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container, new PreferencesPartFragment()).commit();
				}
				else if (visibleFragment.equalsIgnoreCase("Cropper"))
				{
						fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container, new PreferencesGraphicsFragment()).commit();
				}
				else if (visibleFragment.equalsIgnoreCase("VisibilityOrder"))
				{
						new saveSorting().execute();
				}
				else if (visibleFragment.equalsIgnoreCase("PresetsManager"))
				{
						for (int i = 0; i < PreferencesPresetsFragment.DownloadingActiveForHelper.length; i ++)
						{
								if (PreferencesPresetsFragment.DownloadingActiveForHelper[i] != null && PreferencesPresetsFragment.DownloadingActiveForHelper[i].isRunning())
								{
										PreferencesPresetsFragment.DownloadingActiveForHelper[i].stopDownload(true);
								}
						}
						actionbar.setButton(getString(R.string.PreviewPowerMenu), R.drawable.ic_action_launch, MainActivity.previewOnClickListener);
						fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container, new PreferencesColorFragment()).commit();
				}
				else if (visibleFragment.equalsIgnoreCase("PresetsManagerOnline") || (visibleFragment.equalsIgnoreCase("PresetsManagerAccount") && (LoginFragment.loginFragmentMode.equalsIgnoreCase("login") || LoginFragment.loginFragmentMode.equalsIgnoreCase("logout"))))
				{
						if (PreferencesPresetsFragment.onlineSearchBar.getVisibility() == View.VISIBLE)
						{
								PreferencesPresetsFragment.hideBars();
						}
						else
						{
								PreferencesPresetsFragment.vpPager.setCurrentItem(1, true);
						}
				}
				else if (visibleFragment.equalsIgnoreCase("PresetsManagerAccount") && (LoginFragment.loginFragmentMode.equalsIgnoreCase("register") || LoginFragment.loginFragmentMode.equalsIgnoreCase("recover")))
				{
						LoginFragment.returnToLogin();
				}
				else if (visibleFragment.equalsIgnoreCase("Advanced"))
				{
						fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container, new PreferencesPartFragment()).commit();
				}
				else if (visibleFragment.equalsIgnoreCase("Gravity"))
				{
						fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container, new PreferencesAdvancedFragment()).commit();
				}
				else if (visibleFragment.equalsIgnoreCase("permissions"))
				{
						fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container, new PreferencesPartFragment()).commit();
				}
				else if (visibleFragment.equalsIgnoreCase("about") || visibleFragment.equalsIgnoreCase("login"))
				{
						actionbar.setButton(getString(R.string.PreviewPowerMenu), R.drawable.ic_action_launch, MainActivity.previewOnClickListener);
						fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container, new PreferencesPartFragment()).commit();
				}
				else if (visibleFragment.equalsIgnoreCase("Main") || visibleFragment.equalsIgnoreCase("permissionsAutoStart"))
				{
						finish();
						super.onBackPressed();
				}
		}

		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data)
		{
				// TODO: Implement this method
				super.onActivityResult(requestCode, resultCode, data);
		}

    public static void launchPowerMenu()
		{
				Intent intent = new Intent(context, XposedMainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				intent.putExtra("previewmode", true);
				context.startActivity(intent);
    }

		@Override
		protected void onPause()
		{
				// TODO: Implement this method
				getSharedPrefsFile(MainActivity.class.getPackage().getName() + "_preferences").setReadable(true, false);
				super.onPause();
		}

		@Override
		public void onResume()
		{
				// TODO: Implement this method
				if (!visibleFragment.equalsIgnoreCase("about") && !visibleFragment.equalsIgnoreCase("permissions") && !visibleFragment.equalsIgnoreCase("permissionsAutoStart") && !visibleFragment.equalsIgnoreCase("PresetsManagerOnline") && !visibleFragment.equalsIgnoreCase("PresetsManagerAccount") && !visibleFragment.equalsIgnoreCase("VisibilityOrder") && !visibleFragment.equalsIgnoreCase("Cropper"))
				{
						actionbar.setButton(getString(R.string.PreviewPowerMenu), R.drawable.ic_action_launch, previewOnClickListener);
				}
				CustomActivityOnCrash.setRestartActivityClass(MainActivity.class);
				super.onResume();
		}

		@Override
		public void onConfigurationChanged(Configuration newConfig)
		{
				// TODO: Implement this method
				super.onConfigurationChanged(newConfig);
				if (visibleFragment.equalsIgnoreCase("Graphics"))
				{
						PreferencesGraphicsFragment.GridView_Images.setNumColumns(getResources().getInteger(R.integer.ImageList_Columns));
				}
		}

		@Override
		public void onRequestPermissionsResult(int requestCode,
																					 String permissions[], int[] grantResults)
		{
				switch (requestCode)
				{
						case permissionsScreen.MY_PERMISSIONS_REQUEST: {
										// If request is cancelled, the result arrays are empty.
										if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
										{

										}
										permissionsScreen.adapter.notifyDataSetChanged();
										return;
								}

								// other 'case' lines to check for other
								// permissions this app might request
				}
		}

		class saveSorting extends AsyncTask<String, String, String>
		{

				@Override
				protected void onPreExecute()
				{
						// TODO: Implement this method
						super.onPreExecute();
						PreferencesVisibilityOrderFragmentNew.LinearLayout_Progress.setVisibility(View.VISIBLE);
						PreferencesVisibilityOrderFragmentNew.LinearLayout_Progress.startAnimation(MainActivity.anim_fade_in);
				}

				@Override
				protected String doInBackground(String[] p1)
				{
						// TODO: Implement this method
						PreferencesVisibilityOrderFragmentNew.adapter.outputSorting();
						return null;
				}

				@Override
				protected void onPostExecute(String p1)
				{
						// TODO: Implement this method
						super.onPostExecute(p1);
						PreferencesVisibilityOrderFragmentNew.LinearLayout_Progress.startAnimation(MainActivity.anim_fade_out);
						PreferencesVisibilityOrderFragmentNew.LinearLayout_Progress.setVisibility(View.GONE);
						actionbar.setButton(context.getString(R.string.PreviewPowerMenu), R.drawable.ic_action_launch, previewOnClickListener);
						fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container, new PreferencesPartFragment()).commit();
				}

		}

		private void initImageLoader()
		{
				try
				{
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
						Log.d("NPM:imageLoader", "Loaded!");
				}
				catch (Exception e)
				{
						Log.e("NPM:imageLoader", "Load failed, code:" + e);
				}
		}

}

package de.NeonSoft.neopowermenu;

import android.content.*;
import android.content.pm.*;
import android.content.res.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.Preferences.*;
import de.NeonSoft.neopowermenu.helpers.*;
import de.NeonSoft.neopowermenu.permissionsScreen.*;
import de.NeonSoft.neopowermenu.xposed.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class MainActivity extends AppCompatActivity {

		public static boolean LOCALTESTSERVER = false; // use local server "127.0.0.1:8080 or online www.Neon-Soft.de
		public static int TIMEOUT_MILLISEC = 10000; // = 10 seconds
		
		public static SharedPreferences preferences;
		public static Context context;
		public static LayoutInflater inflater;
		public static boolean RootAvailable;
		public static android.support.v4.app.FragmentManager fragmentManager;
		public static String visibleFragment = "Main";

		private static TextView TextView_ActionBarTitle;
		private static TextView TextView_ActionBarVersion;

		private static LinearLayout LinearLayout_ActionBarButton;
		private static ImageView ImageView_ActionBarButton;
		private static int ImageView_ActionBarButton_Icon = 0;
		private static TextView TextView_ActionBarButton;
		private static String TextView_ActionBarButton_Text = "none";
		
    public static final int BG_PRIO = android.os.Process.THREAD_PRIORITY_BACKGROUND;

		public static String versionName = "v1.0";
		int versionCode = -1;
		
		/*<!-- Internal needed Hook version to check if reboot is needed --!>*/
		public static int neededModuleActiveVersion = 19;
		
		public static String ImportUrl = null;
		
		static Animation anim_fade_out;
		static Animation anim_fade_in;
		static Animation anim_fade_slide_out_right;
		static Animation anim_fade_slide_in_right;

		public static OnClickListener previewOnClickListener;
		
		AlertDialog.Builder adb;
		
		// Session data
		public static boolean loggedIn = false;
		public static String usernameemail = "";
		public static String password = "";
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {

				context = getApplicationContext();
        preferences = getSharedPreferences(MainActivity.class.getPackage().getName() + "_preferences",Context.MODE_WORLD_READABLE);

        setTheme(R.style.ThemeBaseDark);
				
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
				
				if(preferences.getString("userUniqeId","null").equalsIgnoreCase("null")) {
						Date date = new Date();
						preferences.edit().putString("userUniqeId",helper.md5Crypto(Build.MANUFACTURER+"-"+Build.MODEL+"-"+date.getYear()+"."+date.getMonth()+"."+date.getDay()+"-"+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds())).commit();
						Toast.makeText(getApplicationContext(),getString(R.string.welcomeMsg),Toast.LENGTH_LONG).show();
				}
				
				if(preferences.getBoolean("autoLogin",false)) {
						LoginFragment.performLogin(context,preferences.getString("ueel","null"),preferences.getString("pd","null"),true);
				}
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
				{
						getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
						getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkDarkTheme));
						getWindow().setNavigationBarColor(getResources().getColor(R.color.window_background_dark));
				}
				
				anim_fade_out = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
				anim_fade_in = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
				anim_fade_slide_out_right = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_fade_slide_out_right);
				anim_fade_slide_in_right = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_fade_slide_in_right);
				
				File presetDir = new File(getFilesDir()+"/presets/");
				presetDir.mkdirs();

				Uri intentfilterdata = getIntent().getData();
				if (intentfilterdata != null)
				{
						try
						{
								ImportUrl = intentfilterdata.getScheme()+ "://"+intentfilterdata.getPath();
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
						Log.e("Failed to get Version infos: ",e.getMessage());
				}
				TextView_ActionBarTitle = (TextView) this.findViewById(R.id.mainTextView_Title);
				TextView_ActionBarVersion = (TextView) this.findViewById(R.id.mainTextView_Version);
				TextView_ActionBarVersion.setSelected(true);

				LinearLayout_ActionBarButton = (LinearLayout) this.findViewById(R.id.mainLinearLayout_Button);
				ImageView_ActionBarButton = (ImageView) this.findViewById(R.id.mainImageView_Button);
				ImageView_ActionBarButton_Icon = 0;
				TextView_ActionBarButton = (TextView) this.findViewById(R.id.mainTextView_Button);
				TextView_ActionBarButton_Text = "none";
				LinearLayout_ActionBarButton.setVisibility(View.GONE);

				TextView_ActionBarVersion.setText(versionName+" ("+versionCode+")");

        fragmentManager = getSupportFragmentManager();
				if(preferences.getBoolean("DontAskPermissionsAgain",false) || permissionsScreen.checkPermissions(MainActivity.this,permissionsScreen.permissions)) {
        android.support.v4.app.Fragment fragment = new PreferencesPartFragment();
        fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
								.replace(R.id.pref_container, fragment).commit();
						if (ImportUrl!=null) {
								fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container,new PreferencesPresetsFragment()).commit();
						}
				} else {
						fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container,new permissionsScreen()).commit();
				}
						previewOnClickListener = new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										hideActionBarButton();
										launchPowerMenu();
								}
						};
						//MainActivity.setActionBarButton(getString(R.string.PreviewPowerMenu),R.drawable.ic_action_launch,previewOnClickListener);
    }
		
		public static void setActionBarButton(String sText,int iImgResId,OnClickListener onclkl) {
				if(LinearLayout_ActionBarButton.getVisibility()==View.GONE || (!sText.equalsIgnoreCase(TextView_ActionBarButton_Text) && ImageView_ActionBarButton_Icon!=iImgResId)) {
						if(LinearLayout_ActionBarButton.getVisibility()==View.VISIBLE) {
								LinearLayout_ActionBarButton.startAnimation(anim_fade_out);
								LinearLayout_ActionBarButton.setVisibility(View.INVISIBLE);
						}
						TextView_ActionBarButton.setText(sText);
						TextView_ActionBarButton_Text = sText;
						//Toast.makeText(context,"Showing ActionBarButton:\nIcon: "+iImgResId+"\nText: "+sText,Toast.LENGTH_SHORT).show();
						if (iImgResId>-1) {
								ImageView_ActionBarButton_Icon = iImgResId;
								ImageView_ActionBarButton.setImageResource(iImgResId);
								ImageView_ActionBarButton.setVisibility(View.VISIBLE);
						} else {
								ImageView_ActionBarButton_Icon = -1;
								ImageView_ActionBarButton.setVisibility(View.GONE);
						}
						LinearLayout_ActionBarButton.setOnClickListener(onclkl);
						if(LinearLayout_ActionBarButton.getVisibility()==View.INVISIBLE) {
								LinearLayout_ActionBarButton.setVisibility(View.VISIBLE);
								LinearLayout_ActionBarButton.startAnimation(anim_fade_in);
						} else {
								LinearLayout_ActionBarButton.setVisibility(View.VISIBLE);
								LinearLayout_ActionBarButton.startAnimation(anim_fade_slide_in_right);
						}
				} else {
						//Toast.makeText(context,"ActionBarButton already set with this data.",Toast.LENGTH_SHORT).show();
				}
		}
		
		public static void setActionBarButtonListener(OnClickListener listener) {
				LinearLayout_ActionBarButton.setOnClickListener(listener);
		}

		public static void setActionBarButtonText(String sText) {
				if(!sText.equalsIgnoreCase(TextView_ActionBarButton.getText().toString())) {
						if(LinearLayout_ActionBarButton.getVisibility()==View.VISIBLE) {
								LinearLayout_ActionBarButton.startAnimation(anim_fade_out);
								LinearLayout_ActionBarButton.setVisibility(View.INVISIBLE);
						}
						TextView_ActionBarButton.setText(sText);
						TextView_ActionBarButton_Text = sText;
						if(LinearLayout_ActionBarButton.getVisibility()==View.INVISIBLE) {
								LinearLayout_ActionBarButton.setVisibility(View.VISIBLE);
								LinearLayout_ActionBarButton.startAnimation(anim_fade_in);
						}
				}
		}

		public static void setActionBarButtonIcon(int iImgResId) {
				if(iImgResId!=ImageView_ActionBarButton_Icon) {
						if(LinearLayout_ActionBarButton.getVisibility()==View.VISIBLE && ImageView_ActionBarButton.getVisibility()==View.VISIBLE) {
								ImageView_ActionBarButton.startAnimation(anim_fade_out);
						}
						if(iImgResId>-1) {
								ImageView_ActionBarButton_Icon = iImgResId;
								ImageView_ActionBarButton.setImageResource(iImgResId);
								ImageView_ActionBarButton.setVisibility(View.VISIBLE);
								ImageView_ActionBarButton.startAnimation(anim_fade_in);
						} else {
								ImageView_ActionBarButton.setVisibility(View.GONE);
						}
				}
		}

		public static void hideActionBarButton() {
				if(LinearLayout_ActionBarButton.getVisibility()==View.VISIBLE) {
						LinearLayout_ActionBarButton.startAnimation(anim_fade_slide_out_right);
						LinearLayout_ActionBarButton.setVisibility(View.GONE);
						ImageView_ActionBarButton_Icon = 0;
						TextView_ActionBarButton_Text = "none";
				}
		}

		@Override
		public void onBackPressed()
		{
				if (visibleFragment.equalsIgnoreCase("CustomColors")) {
						fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container,new PreferencesPartFragment()).commit();
				} else if (visibleFragment.equalsIgnoreCase("VisibilityOrder")) {
						fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container,new PreferencesPartFragment()).commit();
				} else if (visibleFragment.equalsIgnoreCase("PresetsManager")) {
						MainActivity.setActionBarButton(getString(R.string.PreviewPowerMenu),R.drawable.ic_action_launch,MainActivity.previewOnClickListener);
						fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container,new PreferencesColorFragment()).commit();
				} else if (visibleFragment.equalsIgnoreCase("PresetsManagerOnline") || (visibleFragment.equalsIgnoreCase("PresetsManagerAccount") && (LoginFragment.loginFragmentMode.equalsIgnoreCase("login") || LoginFragment.loginFragmentMode.equalsIgnoreCase("logout")))) {
						PreferencesPresetsFragment.vpPager.setCurrentItem(1,true);
				} else if (visibleFragment.equalsIgnoreCase("PresetsManagerAccount") && LoginFragment.loginFragmentMode.equalsIgnoreCase("register")) {
						LoginFragment.returnToLogin();
				} else if (visibleFragment.equalsIgnoreCase("Advanced")) {
						fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container,new PreferencesPartFragment()).commit();
				} else if (visibleFragment.equalsIgnoreCase("permissions")) {
						fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container,new PreferencesPartFragment()).commit();
				} else if (visibleFragment.equalsIgnoreCase("about") || visibleFragment.equalsIgnoreCase("login")) {
						MainActivity.setActionBarButton(getString(R.string.PreviewPowerMenu),R.drawable.ic_action_launch,MainActivity.previewOnClickListener);
						fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container,new PreferencesPartFragment()).commit();
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
				intent.putExtra("previewmode",true);
				context.startActivity(intent);
    }

		@Override
		public void onResume()
		{
				// TODO: Implement this method
				if(!visibleFragment.equalsIgnoreCase("permissions") && !visibleFragment.equalsIgnoreCase("permissionsAutoStart") && !visibleFragment.equalsIgnoreCase("PresetsManagerOnline") && !visibleFragment.equalsIgnoreCase("PresetsManagerAccount")) {
						setActionBarButton(getString(R.string.PreviewPowerMenu),R.drawable.ic_action_launch,previewOnClickListener);
				}
				super.onResume();
		}
		@Override
		public void onConfigurationChanged(Configuration newConfig)
		{
				// TODO: Implement this method
				super.onConfigurationChanged(newConfig);
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
		
}

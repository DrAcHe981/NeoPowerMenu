package de.NeonSoft.neopowermenu;

import android.content.*;
import android.content.res.*;
import android.net.*;
import android.os.*;
import android.support.v7.app.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.Preferences.*;
import de.NeonSoft.neopowermenu.xposed.*;
import java.io.*;
import java.net.*;
import android.support.v4.app.*;

public class MainActivity extends AppCompatActivity {
		
		public static SharedPreferences preferences;
		public static Context context;
		public static LayoutInflater inflater;
		private LinearLayout LinearLayout_ShowPreview;
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

		String versionName = "1.0";
		int versionCode = -1;
		
		/*<!-- Internal needed Hook version to check if reboot is needed --!>*/
		public static int neededModuleActiveVersion = 17;
		
		public static URL ImportUrl = null;
		
		static Animation anim_fade_out;
		static Animation anim_fade_in;
		static Animation anim_fade_slide_out_right;
		static Animation anim_fade_slide_in_right;

		OnClickListener previewOnClickListener;
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {

				context = getApplicationContext();
        preferences = getSharedPreferences(MainActivity.class.getPackage().getName() + "_preferences",Context.MODE_WORLD_READABLE);

        setTheme(R.style.ThemeBaseDark);
				
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
				
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
								ImportUrl = new URL(intentfilterdata.getScheme(), intentfilterdata.getHost(), intentfilterdata.getPath());
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

        android.support.v4.app.Fragment fragment = new PreferencesPartFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
						.replace(R.id.pref_container, fragment).commit();

						
						if (ImportUrl!=null) {
								fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container,new PreferencesPresetsFragment()).commit();
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
						fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container,new PreferencesColorFragment()).commit();
				} else if (visibleFragment.equalsIgnoreCase("Advanced")) {
						fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container,new PreferencesPartFragment()).commit();
				} else if (visibleFragment.equalsIgnoreCase("Main")) {
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
				setActionBarButton(getString(R.string.PreviewPowerMenu),R.drawable.ic_action_launch,previewOnClickListener);
				super.onResume();
		}
		@Override
		public void onConfigurationChanged(Configuration newConfig)
		{
				// TODO: Implement this method
				super.onConfigurationChanged(newConfig);
		}
		
}

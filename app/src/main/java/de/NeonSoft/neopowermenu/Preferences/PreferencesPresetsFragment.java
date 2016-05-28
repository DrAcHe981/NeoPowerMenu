package de.NeonSoft.neopowermenu.Preferences;
import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.view.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import com.ogaclejapan.smarttablayout.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.*;
import de.robv.android.xposed.*;
import java.io.*;
import java.net.*;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import de.NeonSoft.neopowermenu.R;
import android.view.View.*;
import android.util.*;
import android.view.animation.*;

public class PreferencesPresetsFragment extends Fragment
{

		public static AlertDialog importad;
		public static String Filename;
		public static String sCreator = "< unknown >";
		public static String newUrl;
		public static boolean Importcancled = false;

		public static RelativeLayout progressHolder;
		public static ProgressBar progress;
		public static TextView LoadingMsg;
		
		public static Activity mContext;
		
		View InflatedView;
		public static ViewPager vpPager;
		public static MyPagerAdapter adapterViewPager;
		
		public static PresetsAdapter localAdapter;
		public static PresetsAdapter onlineAdapter;
		public static ListView onlineList;
		public static TextView onlineMSG;

		public static boolean onlineRequestIsRunning;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
				// TODO: Implement this method
				MainActivity.visibleFragment = "PresetsManager";

				mContext = getActivity();
				
				InflatedView = inflater.inflate(R.layout.activity_presetsmanager, container, false);
				
        vpPager = (ViewPager) InflatedView.findViewById(R.id.pager);
        adapterViewPager = new MyPagerAdapter(MainActivity.fragmentManager, new String[] {getString(R.string.presetsManager_TitleAccount),getString(R.string.presetsManager_TitleLocal),getString(R.string.presetsManager_TitleOnline)});
        vpPager.setAdapter(adapterViewPager);

				vpPager.setCurrentItem(1);
				
				vpPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

								@Override
								public void onPageScrolled(int p1, float p2, int p3)
								{
										// TODO: Implement this method
								}

								@Override
								public void onPageSelected(int p1)
								{
										// TODO: Implement this method
										if(adapterViewPager.getPageTitle(p1).toString().equalsIgnoreCase(getString(R.string.presetsManager_TitleOnline))) {
												MainActivity.visibleFragment = "PresetsManagerOnline";
												MainActivity.setActionBarButton("Refresh", R.drawable.ic_action_autorenew, new OnClickListener() {

																@Override
																public void onClick(View p1)
																{
																		// TODO: Implement this method
																		new getOnlinePresets().execute();
																}
														});
										} else if(adapterViewPager.getPageTitle(p1).toString().equalsIgnoreCase(getString(R.string.presetsManager_TitleAccount))) {
												MainActivity.visibleFragment = "PresetsManagerAccount";
												if(LoginFragment.loginFragmentMode.equalsIgnoreCase("login")) {
														MainActivity.setActionBarButton(getString(R.string.login_Title),R.drawable.ic_action_import,LoginFragment.loginOnClickListener);
												} else if (LoginFragment.loginFragmentMode.equalsIgnoreCase("register")) {
														MainActivity.setActionBarButton(getString(R.string.login_TitleRegister),R.drawable.ic_action_import,LoginFragment.registerOnClickListener);
												} else if (LoginFragment.loginFragmentMode.equalsIgnoreCase("logout")) {
														MainActivity.setActionBarButton(getString(R.string.login_TitleLogout),R.drawable.ic_action_export,LoginFragment.logoutOnClickListener);
												}
										} else {
												MainActivity.visibleFragment = "PresetsManager";
												MainActivity.setActionBarButton(getString(R.string.PreviewPowerMenu),R.drawable.ic_action_launch,MainActivity.previewOnClickListener);
										}
								}

								@Override
								public void onPageScrollStateChanged(int p1)
								{
										// TODO: Implement this method
								}
						});
				
        SmartTabLayout tabsStrip = (SmartTabLayout) InflatedView.findViewById(R.id.tabs);
				tabsStrip.setCustomTabView(R.layout.customtab,R.id.customTabText);
				
        tabsStrip.setViewPager(vpPager);

				progressHolder = (RelativeLayout) InflatedView.findViewById(R.id.presetsmanagerlistholderRelativeLayout_Progress);
				progressHolder.setVisibility(View.GONE);
				progress = (ProgressBar) InflatedView.findViewById(R.id.presetsmanagerlistholderProgressBar_Progress);
				LoadingMsg = (TextView) InflatedView.findViewById(R.id.presetsmanagerlistholderTextView_LoadMsg);
				
				progressHolder.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// Just prevent touch trough...
								}
						});
				
				return InflatedView;
		}

		public static boolean ImportPreset(final URL url,final PresetsAdapter adapter,String name, String creator) {
				Importcancled = false;
				sCreator = "< unknown >";
				try {
						String fUrl = url.getFile();
						if (!fUrl.endsWith(".nps")) {
								MainActivity.ImportUrl = null;
								Toast.makeText(mContext,"Import failed...\nUnknown file type!",Toast.LENGTH_LONG).show();
								return false;
						}
						if(fUrl.startsWith("file:")) {
								fUrl = fUrl.replace("file:","");
						}
						newUrl = fUrl;
						File prefile = new File(newUrl);
						Filename = (name!=null && !name.isEmpty()) ? name+".nps" : prefile.getName();
						FileInputStream fIn = new FileInputStream(prefile);
						BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
						String aDataRow = ""; 
				while ((aDataRow = myReader.readLine()) != null)
				{ 
						//aBuffer += aDataRow + "\n";
						String[] aData = aDataRow.split("=");
						if (aData.length<2) {
								MainActivity.ImportUrl = null;
								Toast.makeText(mContext,"Import failed...\nCorrupted or invalid preset!",Toast.LENGTH_LONG).show();
								return false;//presetInfo[1] = mContext.getString(R.string.presetsManager_Creator).replace("[CREATORNAME]",aData[1]);
						}
						if(aData[0].equalsIgnoreCase("Creator")) {
								sCreator = aData[1];
						}
				}
				AlertDialog.Builder adb = new AlertDialog.Builder(mContext);
				//adb.setTitle((name!=null && !name.isEmpty()) ? name : Filename.split(".nps")[0]);
				
						View view = mContext.getLayoutInflater().inflate(R.layout.inputdialog,null);
						final TextView Text = (TextView) view.findViewById(R.id.inputdialogTextView1);
						Text.setVisibility(View.VISIBLE);
						Text.setText(((name!=null && !name.isEmpty()) ? name : Filename.split(".nps")[0])+"\n"+mContext.getString(R.string.presetsManager_Creator).replace("[CREATORNAME]",(creator!=null && !creator.isEmpty()) ? creator : sCreator)+"\n\n"+mContext.getString(R.string.presetsManager_ImportMsg));
						final LinearLayout Name = (LinearLayout) view.findViewById(R.id.inputdialogLinearLayout_Name);
						Name.setVisibility(View.GONE);
						final EditText Input = (EditText) view.findViewById(R.id.inputdialogEditText1);
						final LinearLayout Creator = (LinearLayout) view.findViewById(R.id.inputdialogLinearLayout_Creator);
						Creator.setVisibility(View.GONE);
						final Button Rename = (Button) view.findViewById(R.id.inputdialogButton_Rename);
						Rename.setVisibility(View.VISIBLE);
						final TextView OverwriteInfo = (TextView) view.findViewById(R.id.inputdialogTextViewOverwrite);
						Input.addTextChangedListener(new TextWatcher() {

										@Override
										public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
										{
												// TODO: Implement this method
										}

										@Override
										public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
										{
												// TODO: Implement this method
												if (!p1.toString().equalsIgnoreCase("")) {
														File checkFile = new File(mContext.getFilesDir()+"/presets/"+p1+".nps");
														if (checkFile.exists()) {
																OverwriteInfo.setVisibility(View.VISIBLE);
														} else {
																OverwriteInfo.setVisibility(View.GONE);
														}
												}
										}

										@Override
										public void afterTextChanged(Editable p1)
										{
												// TODO: Implement this method
										}
								});
						Rename.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View p1)
										{
												// TODO: Implement this method
												if(Rename.getText().toString().equalsIgnoreCase(mContext.getString(R.string.Dialog_Rename))) {
														Name.setVisibility(View.VISIBLE);
														Rename.setText(R.string.Dialog_Save);
												} else {
														Filename = Input.getText().toString()+".nps";
														//importad.setTitle(Input.getText().toString());
														Text.setText(Input.getText().toString()+"\n"+mContext.getString(R.string.presetsManager_Creator).replace("[CREATORNAME]",sCreator)+"\n\n"+mContext.getString(R.string.presetsManager_ImportMsg));
														Name.setVisibility(View.GONE);
														Rename.setText(R.string.Dialog_Rename);
												}
										}
								});
				Input.setText((name!=null && !name.isEmpty()) ? name : Filename.split(".nps")[0]);
				
				adb.setView(view);
				
				adb.setPositiveButton(R.string.Dialog_Yes, new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface p1, int p2)
								{
										// TODO: Implement this method
										if(Rename.getText().toString().equalsIgnoreCase(mContext.getString(R.string.Dialog_Save))) {
												Filename = Input.getText().toString()+".nps";
												//importad.setTitle(Input.getText().toString());
												Text.setText(Input.getText().toString()+"\n"+mContext.getString(R.string.presetsManager_Creator).replace("[CREATORNAME]",sCreator)+"\n\n"+mContext.getString(R.string.presetsManager_ImportMsg));
												Name.setVisibility(View.GONE);
										}
										new ImportPreset().execute(newUrl,Filename,adapter);
								}
						});
						adb.setNegativeButton(R.string.Dialog_Cancel, new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface p1, int p2)
										{
												// TODO: Implement this method
												Importcancled = true;
										}
								});
								
						importad = adb.create();
						importad.show();
						
				} catch (Throwable e) {
						
				}
				MainActivity.ImportUrl = null;
				if (Importcancled) {
						return false;
				} else {
				return true;
				}
		}
		
		public static class ImportPreset extends AsyncTask<Object, String, String>
		{
				
				PresetsAdapter adapter;
				String[] presetInfo = new String[4];
				File tmpfile;
				boolean newPresetAdded = false;

				@Override
				protected void onPreExecute()
				{
						// TODO: Implement this method
						progressHolder.setVisibility(View.VISIBLE);
						progressHolder.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_in));
						super.onPreExecute();
				}
				
				@Override
				protected String doInBackground(Object[] p1)
				{
						// TODO: Implement this method
						try {
								adapter = (PresetsAdapter) p1[2];
								if(p1[0].toString().startsWith("file:")) {
										p1[0] = p1[0].toString().replace("file:","");
								}
						File prefile = new File(p1[0].toString());
						String Filename = (String) p1[1];
						 tmpfile = new File(mContext.getFilesDir().getPath()+"/presets/"+Filename);
						 if(!tmpfile.exists()) {
								 newPresetAdded = true;
						 }
						FileInputStream fIn = new FileInputStream(prefile);
						FileOutputStream fOut = new FileOutputStream(tmpfile);
						BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
						BufferedWriter myWriter = new BufferedWriter(new OutputStreamWriter(fOut));
						String aDataRow = ""; 
								//String aBuffer = "";
								presetInfo[0] = Filename.split(".nps")[0];
								presetInfo[1] = mContext.getString(R.string.presetsManager_Creator).replace("[CREATORNAME]","< unknown >");
								presetInfo[2] = "true";
								presetInfo[3] = "true";
						while ((aDataRow = myReader.readLine()) != null)
						{ 
								//aBuffer += aDataRow + "\n";
								String[] aData = aDataRow.split("=");
								if(p1.length>=4 && aData[0].equalsIgnoreCase("creator")) {
										myWriter.write(aData[0]+"="+p1[3]);
								} else {
										myWriter.write(aDataRow + "\n");
								}
								String[] loadColor = aData[0].split("_");
								publishProgress(loadColor[0] +": "+aData[1]);
								if (aData[0].equalsIgnoreCase("Creator")) {
										presetInfo[1] = mContext.getString(R.string.presetsManager_Creator).replace("[CREATORNAME]",aData[1]);
								}
						}
						myWriter.close();
						myReader.close();
						fOut.close();
						fIn.close();
						} catch (Throwable e) {
								Log.e("[NeoPowerMenu]"," Preset Import failed: " + e.toString());
								return e.toString();
						}
						return "true";
				}

				@Override
				protected void onProgressUpdate(String[] values)
				{
						// TODO: Implement this method
						super.onProgressUpdate(values);
						LoadingMsg.setText("Loading...\n"+values[0]);
				}
				
				@Override
				protected void onPostExecute(String result)
				{
						// TODO: Implement this method
						super.onPostExecute(result);
						progressHolder.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_out));
						progressHolder.setVisibility(View.GONE);
						if (result.equalsIgnoreCase("true")) {
								if(newPresetAdded) {
										adapter.insert(presetInfo);
								}
								Toast.makeText(mContext,mContext.getString(R.string.presetsManager_ImportSuccess).replace("[PRESETNAME]",presetInfo[0]),Toast.LENGTH_SHORT).show();
						} else {
								tmpfile.delete();
								Toast.makeText(mContext,mContext.getString(R.string.presetsManager_ImportFailed),Toast.LENGTH_LONG).show();
						}
						MainActivity.ImportUrl = null;
				}
				
		}
		
		private static class MyPagerAdapter extends FragmentStatePagerAdapter
		{
				private static String[] pageTitles;
				
				public MyPagerAdapter(FragmentManager fragmentManager, String[] titles) {
						super(fragmentManager);
						pageTitles = titles;
				}

				// Returns total number of pages
				@Override
				public int getCount() {
						return pageTitles.length;
				}

				// Returns the fragment to display for that page
				@Override
				public Fragment getItem(int position) {
						switch (position) {
								case 0:
										return new LoginFragment();
								case 1: // Fragment # 0 - This will show FirstFragment
										return new PresetsPage(0, "Local");
								case 2: // Fragment # 0 - This will show FirstFragment different title
										return new PresetsPage(1, "Online");
								default:
										return null;
						}
				}
				
				// Returns the page title for the top indicator
				@Override
				public CharSequence getPageTitle(int position) {
						return "" + pageTitles[position];
				}
				
		}
}

package de.NeonSoft.neopowermenu.Preferences;
import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.*;
import eu.chainfire.libsuperuser.*;

import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import de.NeonSoft.neopowermenu.R;

public class PreferencesPartFragment extends Fragment
{
		
		Context mContext;

    private String Urlgithub = "https://github.com/DrAcHe981/NeoPowerMenu";
    private String Urloriggithub = "https://github.com/naman14/MaterialPowerMenu";

		private String ActiveStyle = "Material";
		private int ActiveStyleId = 0;
		
		private boolean hideicon = false;
		private boolean DeepXposedLogging = false;

		private View InflatedView;

		private static TextView TextView_ModuleStateTitle;
		private static TextView TextView_ModuleStateDesc;

		private static LinearLayout LinearLayout_Style;
		private static TextView TextView_StyleTitle;
		private static TextView TextView_StyleDesc;

		private static LinearLayout LinearLayout_Theme;

		private static LinearLayout LinearLayout_VisibilityOrder;

		private static LinearLayout LinearLayout_Advanced;
		
		private static LinearLayout LinearLayout_HideLauncherIcon;
		private static Switch Switch_HideLauncherIcon;
		
		private static LinearLayout LinearLayout_DeepXposedLogging;
		private static Switch Switch_DeepXposedLogging;
		
		private static LinearLayout LinearLayout_Source;
		private static LinearLayout LinearLayout_OrigSource;

		private static LinearLayout LinearLayout_Share;

		private static LinearLayout LinearLayout_Translator;

		private static LinearLayout LinearLayout_About;
		

		private static AlertDialog.Builder adb;
		private static AlertDialog ad;
		
		private static ProgressDialog pd = null;

		private static final int MY_PERMISSIONS_REQUEST = 101;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
				// TODO: Implement this method
				MainActivity.visibleFragment = "Main";
	
				mContext = getActivity();
				
				ActiveStyle = MainActivity.preferences.getString("DialogTheme", "Material");
				hideicon = MainActivity.preferences.getBoolean("HideLauncherIcon",false);
				DeepXposedLogging = MainActivity.preferences.getBoolean("DeepXposedLogging",false);
				
				InflatedView = inflater.inflate(R.layout.activity_preferences, container, false);

				TextView_ModuleStateTitle = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_ModuleStateTitle);
				TextView_ModuleStateDesc = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_ModuleStateDesc);

				LinearLayout_Style = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_Style);
				TextView_StyleTitle = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_StyleTitle);
				TextView_StyleDesc = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_StyleDesc);
				TextView_StyleDesc.setText(getString(R.string.preferencesDesc_Style).replace("[STYLENAME]", ActiveStyle));

				LinearLayout_Theme = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_Theme);

				LinearLayout_VisibilityOrder = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_VisibilityOrder);

				LinearLayout_Advanced = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_Advanced);
				
				LinearLayout_HideLauncherIcon = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_HideLauncherIcon);
				Switch_HideLauncherIcon = (Switch) InflatedView.findViewById(R.id.activitypreferencesSwitch_HideLauncherIcon);
				Switch_HideLauncherIcon.setChecked(hideicon);
				Switch_HideLauncherIcon.setClickable(false);
				Switch_HideLauncherIcon.setFocusable(false);

				LinearLayout_DeepXposedLogging = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_DeepXposedLogging);
				Switch_DeepXposedLogging = (Switch) InflatedView.findViewById(R.id.activitypreferencesSwitch_DeepXposedLogging);
				Switch_DeepXposedLogging.setChecked(DeepXposedLogging);
				Switch_DeepXposedLogging.setClickable(false);
				Switch_DeepXposedLogging.setFocusable(false);
				
				LinearLayout_Source = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_Source);
				LinearLayout_OrigSource = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_OrigSource);
				
				LinearLayout_Share = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_Share);

				LinearLayout_Translator = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_Translator);

				LinearLayout_About = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_About);

				LinearLayout_Style.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										AlertDialog.Builder alertdb = new AlertDialog.Builder(getActivity());
										alertdb.setTitle(R.string.preferencesTitle_Style);
										String[] styleList = new String[1];
										styleList[0] = "Material";
										for (int i=0;i < styleList.length;i++)
										{
												if (styleList[i].equalsIgnoreCase(ActiveStyle))
												{
														ActiveStyleId = i;
														//presetsList[i] = "(Active) "+ presetsFiles[i].getName().split(".nps")[0];
												}
										}
										alertdb.setSingleChoiceItems(styleList, ActiveStyleId, null);
										alertdb.setNegativeButton(R.string.Dialog_Cancel, new AlertDialog.OnClickListener() {

														@Override
														public void onClick(DialogInterface p1, int p2)
														{
																// TODO: Implement this method
														}
												});
										alertdb.setPositiveButton(R.string.Dialog_Ok, new AlertDialog.OnClickListener() {

														@Override
														public void onClick(DialogInterface p1, int p2)
														{
																// TODO: Implement this method
																try
																{
																		int selectedPosition = (ad).getListView().getCheckedItemPosition();
																		String selectedName = (ad).getListView().getItemAtPosition(selectedPosition).toString();
																		MainActivity.preferences.edit().putString("DialogTheme", selectedName).commit();
																		ActiveStyle = selectedName;
																		TextView_StyleDesc.setText(getString(R.string.preferencesDesc_Style).replace("[STYLENAME]", ActiveStyle));
																}
																catch (Throwable t)
																{
																}
														}
												});
										ad = alertdb.create();
										ad.show();
								}
						});

				LinearLayout_Theme.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										MainActivity.fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container, new PreferencesColorFragment()).commit();
								}
						});

				LinearLayout_VisibilityOrder.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										MainActivity.fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container, new PreferencesVisibilityOrderFragment()).commit();
								}
						});

				LinearLayout_Advanced.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										MainActivity.fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container,new PreferencesAdvancedFragment()).commit();
								}
						});
						
				LinearLayout_HideLauncherIcon.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										hideicon = !hideicon;
										String packageName = getActivity().getPackageName();
										ComponentName componentSettings = new ComponentName(packageName, packageName + ".SettingsActivity");
										if (hideicon)
										{
												getActivity().getPackageManager().setComponentEnabledSetting(componentSettings, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
										}
										else
										{
												getActivity().getPackageManager().setComponentEnabledSetting(componentSettings, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
										}
										Switch_HideLauncherIcon.setChecked(hideicon);
										MainActivity.preferences.edit().putBoolean("HideLauncherIcon",hideicon).commit();
								}
						});
						
				LinearLayout_DeepXposedLogging.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										DeepXposedLogging = !DeepXposedLogging;
										Switch_DeepXposedLogging.setChecked(DeepXposedLogging);
										MainActivity.preferences.edit().putBoolean("DeepXposedLogging",DeepXposedLogging).commit();
								}
						});

				LinearLayout_Source.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										Intent i = new Intent(Intent.ACTION_VIEW);
										i.setData(Uri.parse(Urlgithub));
										startActivity(i);
								}
						});
						
				LinearLayout_OrigSource.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										Intent i = new Intent(Intent.ACTION_VIEW);
										i.setData(Uri.parse(Urloriggithub));
										startActivity(i);
								}
						});

				LinearLayout_Share.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										Intent i = new Intent(Intent.ACTION_SEND);
										i.setType("text/plain");
										i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
										String sAux = getString(R.string.ShareMessage);
										sAux = sAux + "repo.xposed.info/module/de.NeonSoft.neopowermenu \n\n";
										i.putExtra(Intent.EXTRA_TEXT, sAux);
										startActivity(Intent.createChooser(i, getString(R.string.preferencesTitle_Share)));
								}
						});

				LinearLayout_About.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										adb = new AlertDialog.Builder(getActivity());
										adb.setTitle("About");

										adb.setMessage("NeoPowerMenu by Neon-Soft / DrAcHe981\n" + 
																	 "based on a Source from Naman Dwivedi (naman14)\n\n" +
																	 "< Used Librarys >\n" +
																	 "> HoloColorPicker from Lars Werkman\n" +
																	 "An Android Holo themed colorpicker designed by Marie Schweiz\n\n" +
																	 "Licensed under the Apache License, Version 2.0\n\n" +
																	 "> DragSortListView from Bauerca\n" +
																	 "DragSortListView (DSLV) is an extension of the Android ListView that enables drag-and-drop reordering of list items.\n\n" +
																	 "Licensed under the Apache License, Version 2.0\n\n" +
																	 "> libsuperuser from Chainfire / ChainsDD\n\n" +
																	 "Licensed under the Apache License, Version 2.0\n\n" +
																	 "");

										adb.setPositiveButton(R.string.Dialog_Ok, null);

										ad = adb.create();
										ad.show();
								}
						});

				checkState();
				if (!MainActivity.RootAvailable)
				{
						pd = new ProgressDialog(getActivity());
						pd.setMessage(getString(R.string.Dialog_WaitForRoot));
						pd.setIndeterminate(true);
						pd.setCancelable(false);
						pd.setCanceledOnTouchOutside(false);
						pd.setButton(pd.BUTTON_NEGATIVE, getString(R.string.Dialog_Cancel), new DialogInterface.OnClickListener() {

																 @Override
																 public void onClick(DialogInterface p1, int p2)
																 {
																		 pd.dismiss();
																		 getActivity().finish();
																 }
														 });
						pd.setButton(pd.BUTTON_NEUTRAL, getString(R.string.Dialog_Ignore), new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface p1, int p2)
										{
												pd.dismiss();
										}
								});
						pd.show();
				}
				else if (MainActivity.RootAvailable)
				{
						rootAvailable();
				}

				getPermissions();
				return InflatedView;
		}



		public void getPermissions()
		{
				try {
						if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
						{
// Here, thisActivity is the current activity
								if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
								{
// Should we show an explanation?
										if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE))
										{

// Show an expanation to the user *asynchronously* -- don't block
// this thread waiting for the user's response! After the user
// sees the explanation, try again to request the permission.
												showPermissionDialog(MY_PERMISSIONS_REQUEST);
												return;
										}
										else
										{

// No explanation needed, we can request the permission.

												requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST);
												return;
												// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
												// app-defined int constant. The callback method gets the
												// result of the request.
										}
								}
						}
				} catch (Throwable t) {
						Toast.makeText(getActivity(),"getPermission failed: "+t,Toast.LENGTH_LONG).show();
				}
		}


		@Override
		public void onRequestPermissionsResult(int requestCode,
																					 String permissions[], int[] grantResults)
		{
				switch (requestCode)
				{
						case MY_PERMISSIONS_REQUEST: {
										// If request is cancelled, the result arrays are empty.
										if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
										{

												// permission was granted, yay! Do the
												// contacts-related task you need to do.
												getPermissions();

										}
										else
										{
												if (adb==null) {
														showPermissionDialog(MY_PERMISSIONS_REQUEST);
												}
												// permission denied, boo! Disable the
												// functionality that depends on this permission.

										}
										return;
								}

								// other 'case' lines to check for other
								// permissions this app might request
				}
		}

		public void showPermissionDialog(int permission) {
				switch (permission) {
						case MY_PERMISSIONS_REQUEST:
								adb = new AlertDialog.Builder(getActivity());
								adb.setTitle(R.string.permissionRequestTitle);
								adb.setMessage(R.string.permissionRequestMsg);
								adb.setPositiveButton(R.string.Dialog_Ok, new DialogInterface.OnClickListener() {

												@Override
												public void onClick(DialogInterface p1, int p2)
												{
														// TODO: Implement this method
														adb = null;
														requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST);
												}
										});
								adb.setNegativeButton(R.string.Dialog_Cancel, new DialogInterface.OnClickListener() {

												@Override
												public void onClick(DialogInterface p1, int p2)
												{
														// TODO: Implement this method
													getActivity().finish();
												}
										});
								adb.show();
								break;
				}
		}
		
		private void checkState() {
				try{
						if (helper.ModuleState()>=MainActivity.neededModuleActiveVersion)
						{
								if(!MainActivity.RootAvailable) {
										TextView_ModuleStateTitle.setText(R.string.preferencesTitle_RootXposed2);
										TextView_ModuleStateDesc.setText(R.string.preferencesDesc_RootXposed2);
								} else {
										TextView_ModuleStateTitle.setText(R.string.preferencesTitle_RootXposed4);
										TextView_ModuleStateDesc.setText(R.string.preferencesDesc_RootXposed4);
								}
						} else if (helper.ModuleState()==-1) {
								TextView_ModuleStateTitle.setText(R.string.preferencesTitle_RootXposed3);
								TextView_ModuleStateDesc.setText(R.string.preferencesDesc_RootXposed3);
						} else {
								TextView_ModuleStateTitle.setText(R.string.preferencesTitle_RootXposed5);
								TextView_ModuleStateDesc.setText(R.string.preferencesDesc_RootXposed5);
								TextView_ModuleStateTitle.setTextColor(getResources().getColor(R.color.colorAccentDarkTheme));
								TextView_ModuleStateDesc.setTextColor(getResources().getColor(R.color.colorAccentDarkTheme));
						}
				} catch (Throwable t) {
						TextView_ModuleStateTitle.setText(R.string.preferencesTitle_RootXposed5);
						TextView_ModuleStateDesc.setText(R.string.preferencesDesc_RootXposed5);
						TextView_ModuleStateTitle.setTextColor(getResources().getColor(R.color.colorAccentDarkTheme));
						TextView_ModuleStateDesc.setTextColor(getResources().getColor(R.color.colorAccentDarkTheme));
				}
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState)
		{
				// TODO: Implement this method
				super.onActivityCreated(savedInstanceState);
				if(!MainActivity.RootAvailable) {
						new Thread(new Runnable() {
										@Override
										public void run() {
												helper.setThreadPrio(MainActivity.BG_PRIO);

												if (Shell.SU.available()) {
														new Handler(Looper.getMainLooper()).post(new Runnable() {
																		@Override
																		public void run() {
																				rootAvailable();
																		}
																});
												}
										}
								}).start();
				}
				
		}
		
		private void rootAvailable()
		{
				if (TextView_ModuleStateTitle != null)
				{
						if (pd != null)
						{
								MainActivity.RootAvailable = true;
								pd.dismiss();
								pd = null;
						}
						checkState();
				}
		}

}

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
import de.NeonSoft.neopowermenu.permissionsScreen.*;

public class PreferencesPartFragment extends Fragment
{
		
		Context mContext;

    private String Urlgithub = "https://github.com/DrAcHe981/NeoPowerMenu";
    
		private String ActiveStyle = "Material";
		private int ActiveStyleId = 0;
		
		private boolean hideicon = false;
		private boolean DeepXposedLogging = false;

		private View InflatedView;

		private static ProgressBar ProgressBar_RootWait;
		private static TextView TextView_ModuleStateTitle;
		private static TextView TextView_ModuleStateDesc;

		private static LinearLayout LinearLayout_Style;
		private static TextView TextView_StyleTitle;
		private static TextView TextView_StyleDesc;

		private static LinearLayout LinearLayout_Theme;

		private static LinearLayout LinearLayout_VisibilityOrder;

		private static LinearLayout LinearLayout_Advanced;
		
		private static LinearLayout LinearLayout_Permissions;
		
		private static LinearLayout LinearLayout_HideLauncherIcon;
		private static Switch Switch_HideLauncherIcon;
		
		private static LinearLayout LinearLayout_DeepXposedLogging;
		private static Switch Switch_DeepXposedLogging;
		
		private static LinearLayout LinearLayout_Source;

		private static LinearLayout LinearLayout_Share;

		private static LinearLayout LinearLayout_About;
		

		private static AlertDialog.Builder adb;
		private static AlertDialog ad;
		
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

				ProgressBar_RootWait = (ProgressBar) InflatedView.findViewById(R.id.activitypreferencesProgressBar_ModuleState);
				TextView_ModuleStateTitle = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_ModuleStateTitle);
				TextView_ModuleStateDesc = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_ModuleStateDesc);

				LinearLayout_Style = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_Style);
				TextView_StyleTitle = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_StyleTitle);
				TextView_StyleDesc = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_StyleDesc);
				TextView_StyleDesc.setText(ActiveStyle);

				LinearLayout_Theme = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_Theme);

				LinearLayout_VisibilityOrder = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_VisibilityOrder);

				LinearLayout_Advanced = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_Advanced);
				
				LinearLayout_Permissions = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_Permissions);
				
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
				
				LinearLayout_Share = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_Share);

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
										//styleList[1] = "Material (Fullscreen)";
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
																		TextView_StyleDesc.setText(ActiveStyle);
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

				LinearLayout_Permissions.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										Bundle args = new Bundle();
										args.putBoolean("AutoStart",false);
										permissionsScreen permScreen = new permissionsScreen();
										permScreen.setArguments(args);
										MainActivity.fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container,permScreen).commit();
								}
						});
						
				LinearLayout_HideLauncherIcon.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										hideicon = !hideicon;
										String packageName = getActivity().getPackageName();
										ComponentName componentSettings = new ComponentName(packageName, packageName + ".SettingsActivity");
										if (!hideicon)
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
										MainActivity.fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container,new AboutFragment()).commit();
								}
						});

				checkState();
				
				MainActivity.setActionBarButton(getString(R.string.PreviewPowerMenu),R.drawable.ic_action_launch,MainActivity.previewOnClickListener);
				
				return InflatedView;
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
										ProgressBar_RootWait.setVisibility(View.GONE);
								}
						} else if (helper.ModuleState()==-1) {
								if (!MainActivity.RootAvailable) {
										TextView_ModuleStateTitle.setText(R.string.preferencesTitle_RootXposed1);
										TextView_ModuleStateDesc.setText(R.string.preferencesDesc_RootXposed1);
								} else {
										TextView_ModuleStateTitle.setText(R.string.preferencesTitle_RootXposed3);
										TextView_ModuleStateDesc.setText(R.string.preferencesDesc_RootXposed3);
										ProgressBar_RootWait.setVisibility(View.GONE);
								}
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
						MainActivity.RootAvailable = true;
						checkState();
				}
		}

}

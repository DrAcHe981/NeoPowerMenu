package de.NeonSoft.neopowermenu.Preferences;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.text.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.*;
import de.NeonSoft.neopowermenu.permissionsScreen.*;
import eu.chainfire.libsuperuser.*;
import java.io.*;
import java.util.*;

import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.view.View.OnClickListener;

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

		private static LinearLayout LinearLayout_Graphics;
		
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

				LinearLayout_Graphics =(LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_Graphics);
				LinearLayout_Graphics.setAlpha((float) .3);
				LinearLayout_Graphics.setEnabled(false);
				
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
										slideDownDialogFragment dialogFragment = new slideDownDialogFragment(getActivity(), new slideDownDialogFragment.slideDownDialogInterface() {

														@Override
														public void onListItemClick(int position, String text)
														{
																// TODO: Implement this method
																MainActivity.preferences.edit().putString("DialogTheme", text).commit();
																ActiveStyle = text;
																TextView_StyleDesc.setText(ActiveStyle);
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
														public void onPositiveClick(ArrayList<String> resultData)
														{
																// TODO: Implement this method
														}

														@Override
														public void onTouchOutside()
														{
																// TODO: Implement this method
														}
												});
										dialogFragment.setDialogList(ListView.CHOICE_MODE_SINGLE, styleList, ActiveStyleId,true);
										dialogFragment.setDialogPositiveButton(mContext.getString(R.string.Dialog_Ok));
										MainActivity.fragmentManager.beginTransaction().add(R.id.dialog_container,dialogFragment,slideDownDialogFragment.dialogTag).commit();
								}
						});

				LinearLayout_Theme.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										MainActivity.fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container, new PreferencesColorFragment()).commit();
								}
						});

				LinearLayout_Graphics.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										MainActivity.fragmentManager.beginTransaction().setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container, new PreferencesGraphicsFragment()).commit();
								}
						});
						
				LinearLayout_VisibilityOrder.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										MainActivity.fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container, new PreferencesVisibilityOrderFragmentNew()).commit();
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
				
				/*final slideDownDialogFragment dialogFragment = new slideDownDialogFragment(getActivity(), new slideDownDialogFragment.slideDownDialogInterface() {

								@Override
								public void onListItemClick(int position, String text)
								{
										// TODO: Implement this method
										Toast.makeText(mContext,"onListItemClick: "+position+" / "+ text,Toast.LENGTH_SHORT).show();
								}

								@Override
								public void onNegativeClick()
								{
										// TODO: Implement this method
										Toast.makeText(mContext,"onNegativeClick",Toast.LENGTH_SHORT).show();
								}

								@Override
								public void onNeutralClick()
								{
										// TODO: Implement this method
										Toast.makeText(mContext,"onNeutralClick",Toast.LENGTH_SHORT).show();
								}

								@Override
								public void onPositiveClick(ArrayList<String> resultData)
								{
										// TODO: Implement this method
										Toast.makeText(mContext,"onPositiveClick: "+resultData.toString(),Toast.LENGTH_SHORT).show();
								}

								@Override
								public void onTouchOutside()
								{
										// TODO: Implement this method
										Toast.makeText(mContext,"onTouchOutside",Toast.LENGTH_SHORT).show();
								}
						});*/
				//dialogFragment.setDialogText("This is a dialog Test\nThis one will not close on touch outside");
				//dialogFragment.setDialogList(ListView.CHOICE_MODE_SINGLE,new String[] {"Item 1","Item 2","Item 3"},1,false);
				/*dialogFragment.setDialogInput1(mContext.getString(R.string.presetSaveDialog_InfoText), "Test", new TextWatcher() {

								@Override
								public void afterTextChanged(Editable p1)
								{
										// TODO: Implement this method
								}

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
												if (!checkFile.exists()) {
														dialogFragment.setDialogText("This is a dialog Test\nThis one will not close on touch outside");
												} else {
														dialogFragment.setDialogText("This is a dialog Test\nThis one will not close on touch outside\n\n"+mContext.getString(R.string.presetSaveDialog_OverwriteText));
												}
										}
								}
						});
				dialogFragment.setDialogInput2(mContext.getString(R.string.presetSaveDialog_CreatorNameInfo),"Test 2",null);*/
				//dialogFragment.setDialogColorPicker("#cc123456",true);
				//dialogFragment.setDialogCloseOnTouchOutside(false);
				//MainActivity.fragmentManager.beginTransaction().add(R.id.pref_container,dialogFragment,"TestDialog").commit();
				
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

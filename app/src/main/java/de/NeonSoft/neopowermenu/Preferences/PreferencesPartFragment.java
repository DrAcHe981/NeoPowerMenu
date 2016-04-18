package de.NeonSoft.neopowermenu.Preferences;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.*;

public class PreferencesPartFragment extends Fragment
{

    private String Urlgithub = "https://github.com/naman14/MaterialPowerMenu";

		private String ActiveStyle = "Material";
		private int ActiveStyleId = 0;
		
		private boolean hideicon = false;

		private View InflatedView;

		private static TextView TextView_ModuleStateTitle;
		private static TextView TextView_ModuleStateDesc;

		private static LinearLayout LinearLayout_Style;
		private static TextView TextView_StyleTitle;
		private static TextView TextView_StyleDesc;

		private static LinearLayout LinearLayout_Theme;
		private static TextView TextView_ThemeTitle;
		private static TextView TextView_ThemeDesc;

		private static LinearLayout LinearLayout_VisibilityOrder;
		private static TextView TextView_VisibilityOrderTitle;
		private static TextView TextView_VisibilityOrderDesc;

		private static LinearLayout LinearLayout_HideLauncherIcon;
		
		private static LinearLayout LinearLayout_Source;
		private static TextView TextView_SourceTitle;
		private static TextView TextView_SourceDesc;

		private static LinearLayout LinearLayout_Share;
		private static TextView TextView_ShareTitle;
		private static TextView TextView_ShareDesc;

		private static LinearLayout LinearLayout_Translator;

		private static LinearLayout LinearLayout_About;
		private static Switch Switch_HideLauncherIcon;

		private static AlertDialog.Builder adb;
		private static AlertDialog ad;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
				// TODO: Implement this method
				MainActivity.visibleFragment = "Main";

				ActiveStyle = MainActivity.preferences.getString("DialogTheme", "Material");
				hideicon = MainActivity.preferences.getBoolean("HideLauncherIcon",false);
				
				InflatedView = inflater.inflate(R.layout.activity_preferences, container, false);

				TextView_ModuleStateTitle = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_ModuleStateTitle);
				TextView_ModuleStateDesc = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_ModuleStateDesc);

				LinearLayout_Style = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_Style);
				TextView_StyleTitle = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_StyleTitle);
				TextView_StyleDesc = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_StyleDesc);
				TextView_StyleDesc.setText(getString(R.string.preferencesDesc_Style).replace("[STYLENAME]", ActiveStyle));

				LinearLayout_Theme = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_Theme);

				LinearLayout_VisibilityOrder = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_VisibilityOrder);

				LinearLayout_HideLauncherIcon = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_HideLauncherIcon);
				Switch_HideLauncherIcon = (Switch) InflatedView.findViewById(R.id.activitypreferencesSwitch_HideLauncherIcon);
				Switch_HideLauncherIcon.setChecked(hideicon);
				Switch_HideLauncherIcon.setClickable(false);
				Switch_HideLauncherIcon.setFocusable(false);
				
				LinearLayout_Source = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_Source);
				
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
										// TODO: Implement this method
										MainActivity.fragmentManager.beginTransaction().replace(R.id.pref_container, new PreferencesColorFragment()).commit();
								}
						});

				LinearLayout_VisibilityOrder.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										MainActivity.fragmentManager.beginTransaction().replace(R.id.pref_container, new PreferencesVisibilityOrderFragment()).commit();
								}
						});

				LinearLayout_HideLauncherIcon.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										if (hideicon)
										{
												hideicon = false;
												String packageName = getActivity().getPackageName();
												ComponentName componentSettings = new ComponentName(packageName, packageName + ".SettingsActivity");
												getActivity().getPackageManager().setComponentEnabledSetting(componentSettings, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
										}
										else
										{
												hideicon = true;
												String packageName = getActivity().getPackageName();
												ComponentName componentSettings = new ComponentName(packageName, packageName + ".SettingsActivity");
												getActivity().getPackageManager().setComponentEnabledSetting(componentSettings, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
										}
										Switch_HideLauncherIcon.setChecked(hideicon);
										MainActivity.preferences.edit().putBoolean("HideLauncherIcon",hideicon).commit();
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

				LinearLayout_Translator.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										Intent i = new Intent(Intent.ACTION_VIEW);
										i.setData(Uri.parse(getString(R.string.TranslatorWebsite)));
										startActivity(i);
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

										adb.setPositiveButton(R.string.Dialog_Ok, new DialogInterface.OnClickListener() {

														@Override
														public void onClick(DialogInterface p1, int p2)
														{
																// TODO: Implement this method
														}
												});

										ad = adb.create();
										ad.show();
								}
						});

				if (helper.ModuleState().equalsIgnoreCase("active"))
				{
						if (TextView_ModuleStateTitle != null)
						{
								TextView_ModuleStateTitle.setText(R.string.preferencesTitle_RootXposed2);
								TextView_ModuleStateDesc.setText(R.string.preferencesDesc_RootXposed2);
						}
				}
				if (!MainActivity.RootAvailable)
				{
						adb = new AlertDialog.Builder(getActivity());
						adb.setMessage(R.string.Dialog_WaitForRoot);
						adb.setNegativeButton(R.string.Dialog_Cancel, new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface p1, int p2)
										{
												// TODO: Implement this method
												getActivity().finish();
										}
								});
						adb.setCancelable(false);
						ad = adb.create();
						ad.show();
				}
				else if (MainActivity.RootAvailable)
				{
						rootAvailable();
				}

				return InflatedView;
		}

		public static void rootAvailable()
		{
				if (TextView_ModuleStateTitle != null)
				{
						//rootstatus.setTitle("Root available");

						//rootstatus.setSummary("Root is available.");
						//if(appstatus != null) {
						if (helper.ModuleState().equalsIgnoreCase("active"))
						{
								TextView_ModuleStateTitle.setText(R.string.preferencesTitle_RootXposed4);
								TextView_ModuleStateDesc.setText(R.string.preferencesDesc_RootXposed4);
						}
						else if (helper.ModuleState().equalsIgnoreCase("activenohook"))
						{
								TextView_ModuleStateTitle.setText("Root working,Xposed partly");
								TextView_ModuleStateDesc.setText("Active but there is an unknown problem..,please post your logs to the support thread");
						}
						else
						{
								TextView_ModuleStateTitle.setText(R.string.preferencesTitle_RootXposed3);
								TextView_ModuleStateDesc.setText(R.string.preferencesDesc_RootXposed3);
						}
						if (ad != null)
						{
								MainActivity.RootAvailable = true;
								ad.dismiss();
								ad = null;
						}
				}
		}

}

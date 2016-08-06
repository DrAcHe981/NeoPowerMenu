package de.NeonSoft.neopowermenu.helpers;
import android.app.*;
import android.content.pm.*;
import android.graphics.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.Preferences.*;
import java.io.*;
import java.util.*;

public class ColorsListAdapter extends ArrayAdapter<String>
{
		public static Activity context;
		LayoutInflater mInflater;
		public static Object[][] colorNamesArray;
		public static String[] defaultColors;
		
		public static final int TYPE_EMPTY = 0, TYPE_HEADER = 1, TYPE_ITEM = 2, TYPE_LOAD = 10, TYPE_SAVE = 11;
		
		public ColorsListAdapter(Activity context,Object[][] colorNames,String[] defaultColors) {
				super(context, R.layout.colorslistitem, colorNames);
				this.context = context;
				this.mInflater = context.getLayoutInflater();
				this.colorNamesArray = colorNames;
				this.defaultColors = defaultColors;
		}
		
		public int getItemType(int p1)
		{
				// TODO: Implement this method
				return colorNamesArray[p1][0];
		}
		
		@Override
		public View getView(final int p1, View InflatedView, ViewGroup p3)
		{
				// TODO: Implement this method
				LinearLayout root, previewLayout, Texts;
				final TextView Title,Preview,Desc,Line;
				final String[] loadColor;
				final String colorType;
				InflatedView = mInflater.inflate(R.layout.colorslistitem,null);

				root = (LinearLayout) InflatedView.findViewById(R.id.colorslistitemLinearLayout_Root);
				previewLayout = (LinearLayout) InflatedView.findViewById(R.id.colorslistitemLinearLayout_Preview);
				Preview = (TextView) InflatedView.findViewById(R.id.colorslistitemTextView_Preview);
				Texts = (LinearLayout) InflatedView.findViewById(R.id.colorslistitemLinearLayout_Texts);
				Title = (TextView) InflatedView.findViewById(R.id.colorslistitemTextView_Text);
				Desc = (TextView) InflatedView.findViewById(R.id.colorslistitemTextView_Desc);
				Line = (TextView) InflatedView.findViewById(R.id.colorslistitem_Line);
				
				int rowType = getItemType(p1);
						switch (rowType) {
								case TYPE_EMPTY:
										previewLayout.setVisibility(View.GONE);
										Texts.setVisibility(View.GONE);
										Line.setVisibility(View.GONE);
										InflatedView.setEnabled(false);
										InflatedView.setClickable(false);
										LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
												LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

										layoutParams.setMargins(0, helper.getNavigationBarHeight(context), 0, 0);
										root.setLayoutParams(layoutParams);
										break;
								case TYPE_LOAD:
										previewLayout.setVisibility(View.GONE);
										Title.setText(R.string.preset_Load);
										Desc.setText(R.string.preset_LoadDesc);
										root.setOnClickListener(new OnClickListener() {

														@Override
														public void onClick(View p1)
														{
																// TODO: Implement this method
																MainActivity.fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container, new PreferencesPresetsFragment()).commit();
														}
												});
										break;
								case TYPE_SAVE:
										previewLayout.setVisibility(View.GONE);
										Title.setText(R.string.preset_Save);
										Desc.setText(R.string.preset_SaveDesc);
										root.setOnClickListener(new OnClickListener() {

														@Override
														public void onClick(View p1)
														{
																// TODO: Implement this method
																final slideDownDialogFragment dialogFragment = new slideDownDialogFragment(context, MainActivity.fragmentManager);
																dialogFragment.setDialogListener(new slideDownDialogFragment.slideDownDialogInterface() {

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
																				public void onPositiveClick(ArrayList<String> resultData)
																				{
																						// TODO: Implement this method
																						try
																						{
																								File presetFile = new File(context.getFilesDir().getPath()+"/presets/"+resultData.get(0).replace("/","")+".nps");
																								presetFile.createNewFile();
																								FileWriter fw = new FileWriter(presetFile);
																								String versionName = "1.0";
																								try
																								{
																										versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
																								}
																								catch (PackageManager.NameNotFoundException e)
																								{
																										Log.e("tag", e.getMessage());
																								}
																								fw.append("AppVersion="+versionName+"\n");
																								if (!resultData.get(1).equalsIgnoreCase("")){
																										MainActivity.preferences.edit().putString("lastPresetCreatedBy",resultData.get(1)).commit();
																										fw.append("Creator="+resultData.get(1).replace("/","")+"\n");
																								} else {
																										fw.append("Creator=a "+android.os.Build.MANUFACTURER+" " +android.os.Build.MODEL+ " user\n");
																								}
																								for (int i=0;i<colorNamesArray.length;i++) {
																										String[] loadColor = colorNamesArray[i][1].toString().split("_");
																										if(loadColor.length>1) {
																												if (loadColor[1].contains("Background"))
																												{
																														fw.append(loadColor[0]+"_Backgroundcolor="+MainActivity.colorPrefs.getString(loadColor[0]+"_Backgroundcolor","#ffffffff")+"\n");
																												} else if (loadColor[1].contains("Text")) {
																														fw.append(loadColor[0]+"_Textcolor="+MainActivity.colorPrefs.getString(loadColor[0]+"_Textcolor","#ffffff")+"\n");
																												}
																										}
																								}
																								fw.close();
																								MainActivity.preferences.edit().putString("lastUsedPreset",resultData.get(0).replace("/","")).commit();
																								if(resultData.get(2).equalsIgnoreCase("true")) {
																										helper.zipAll(context.getFilesDir().getPath()+"/images/",context.getFilesDir().getPath()+"/temp/"+resultData.get(0).replace("/","")+".zip",null);
																										helper.zipFile(context.getFilesDir().getPath()+"/presets/"+resultData.get(0).replace("/","")+".nps",context.getFilesDir().getPath()+"/temp/"+resultData.get(0).replace("/","")+".zip",null);
																										new File(context.getFilesDir().getPath()+"/presets/"+resultData.get(0).replace("/","")+".nps").delete();
																										if (new File(context.getFilesDir().getPath()+"/temp/"+resultData.get(0).replace("/","")+".zip").renameTo(new File(context.getFilesDir().getPath()+"/presets/"+resultData.get(0).replace("/","")+".nps"))) {
																												Toast.makeText(context.getApplicationContext(),context.getString(R.string.presetSave_PresetSaved).replace("[PRESETNAME]",resultData.get(0).replace("/","")),Toast.LENGTH_SHORT).show();
																										}
																								} else {
																										Toast.makeText(context.getApplicationContext(),context.getString(R.string.presetSave_PresetSaved).replace("[PRESETNAME]",resultData.get(0).replace("/","")),Toast.LENGTH_SHORT).show();
																								}
																						}
																						catch (IOException e)
																						{}
																				}

																				@Override
																				public void onTouchOutside()
																				{
																						// TODO: Implement this method
																				}
																		});
																		dialogFragment.setDialogText("");
																dialogFragment.setDialogInput1(context.getString(R.string.presetSaveDialog_InfoText),MainActivity.preferences.getString("lastUsedPreset",""),false,new TextWatcher() {

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
																								File checkFile = new File(context.getFilesDir()+"/presets/"+p1.toString().replace("/","")+".nps");
																								if (!checkFile.exists()) {
																										dialogFragment.showInputAssistInfo(false);
																										//dialogFragment.setDialogText("");
																								} else {
																										dialogFragment.showInputAssistInfo(true);
																										//dialogFragment.setDialogText(context.getString(R.string.presetSaveDialog_OverwriteText));
																								}
																						}
																				}

																				@Override
																				public void afterTextChanged(Editable p1)
																				{
																						// TODO: Implement this method
																				}}
																);
																dialogFragment.setDialogInput2(context.getString(R.string.presetSaveDialog_InfoText),MainActivity.preferences.getString("lastPresetCreatedBy",""),true,null);
																dialogFragment.setDialogInputAssistInfo(context.getString(R.string.presetSaveDialog_OverwriteText));
																dialogFragment.setDialogCheckBox(context.getString(R.string.graphics_GraphicsSaveLoad).split("\\|")[0]);
																dialogFragment.setDialogNegativeButton(context.getString(R.string.Dialog_Buttons).split("\\|")[4]);
																dialogFragment.setDialogPositiveButton(context.getString(R.string.Dialog_Buttons).split("\\|")[7]);
																dialogFragment.showDialog(R.id.dialog_container);
														}
												});
										break;
								case TYPE_ITEM:
										loadColor = colorNamesArray[p1][1].toString().split("_");
										
										Desc.setVisibility(View.GONE);
										String currentColor = "#ff0000";
										colorType = "";
										try {
												String title = "";
												if(loadColor[1].contains("Background")) {
														colorType = "_Backgroundcolor";
														title = context.getString(R.string.colorsType_Background);
														if(loadColor[0].contains("Reveal")) {
																try {
																		String descCode = "Desc";
																		if(loadColor[0].contains("Action")) {
																				descCode = "Desc1";
																		}
																String Description = context.getResources().getString(context.getResources().getIdentifier("colorsPartReveal"+descCode,"string",MainActivity.class.getPackage().getName()));
																Desc.setText(Description);
																Desc.setVisibility(View.VISIBLE);
														} catch (Throwable t) {
																Desc.setText("String Resource for colorsPart" + loadColor[0]+" not found.");
														}
														}
												} else {
														colorType = "_Textcolor";
														title = context.getString(R.string.colorsType_Text);//context.getResources().getString(context.getResources().getIdentifier(colorNamesArray[p1][1].toString(),"string",MainActivity.class.getPackage().getName()));
												}
												Title.setText(title);
										} catch (Throwable t) {
												Title.setText("String Resource for "+ loadColor[0]+" not found.");
										}
										try {
												currentColor = MainActivity.colorPrefs.getString(loadColor[0]+colorType,defaultColors[p1]);
										} catch (Throwable t) {
												Preview.setText("Error");
										}
										Preview.setBackgroundColor(Color.parseColor(currentColor));

										root.setOnClickListener(new OnClickListener() {

														@Override
														public void onClick(View view)
														{
																// TODO: Implement this method
																final slideDownDialogFragment dialogFragment = new slideDownDialogFragment(context, MainActivity.fragmentManager);
																dialogFragment.setDialogListener(new slideDownDialogFragment.slideDownDialogInterface() {

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
																				public void onPositiveClick(ArrayList<String> resultData)
																				{
																						// TODO: Implement this method
																						MainActivity.colorPrefs.edit().putString(loadColor[0]+colorType,resultData.get(0)).commit();
																						Preview.setBackgroundColor(Color.parseColor(resultData.get(0)));
																				}

																				@Override
																				public void onTouchOutside()
																				{
																						// TODO: Implement this method
																				}
																		});
																dialogFragment.setDialogText("");
																dialogFragment.setDialogColorPicker(MainActivity.colorPrefs.getString(loadColor[0]+colorType,defaultColors[p1]),(defaultColors[p1].length()==7) ? false : true);
																dialogFragment.setDialogNegativeButton(context.getString(R.string.Dialog_Buttons).split("\\|")[4]);
																dialogFragment.setDialogPositiveButton(context.getString(R.string.Dialog_Buttons).split("\\|")[7]);
																dialogFragment.showDialog(R.id.dialog_container);
														}
												});
										break;
								case TYPE_HEADER:
										InflatedView = mInflater.inflate(R.layout.listheader,null);
										Title = (TextView) InflatedView.findViewById(R.id.listheaderTextView_Title);
										Desc = (TextView) InflatedView.findViewById(R.id.listheaderTextView_Desc);
										String mainString = "";
										if(colorNamesArray[p1][1].toString().equalsIgnoreCase("Presets") || colorNamesArray[p1][1].toString().equalsIgnoreCase("Reveal") || colorNamesArray[p1][1].toString().equalsIgnoreCase("Main")) {
												try {
														mainString = context.getResources().getString(context.getResources().getIdentifier("colorsPart"+colorNamesArray[p1][1],"string",MainActivity.class.getPackage().getName()));
														String title = (colorNamesArray[p1][1].toString().equalsIgnoreCase("Presets") ? mainString : context.getString(R.string.colorsPartTitle).replace("[PART]",mainString));//getResources().getString(context.getResources().getIdentifier("colorsCategory_"+colorNamesArray[p1][1].toString(),"string",MainActivity.class.getPackage().getName()));
														Title.setText(title);
												} catch (Throwable t1) {
														Title.setText("String Resource for colorsPart"+ colorNamesArray[p1][1]+" not found.");
												}
										} else {
										try {
												mainString = context.getResources().getString(context.getResources().getIdentifier("powerMenuMain_"+colorNamesArray[p1][1],"string",MainActivity.class.getPackage().getName()));
												String title = context.getString(R.string.colorsPartTitle).replace("[PART]",mainString);//getResources().getString(context.getResources().getIdentifier("colorsCategory_"+colorNamesArray[p1][1].toString(),"string",MainActivity.class.getPackage().getName()));
												Title.setText(title);
										} catch (Throwable t) {
												try {
														mainString = context.getResources().getString(context.getResources().getIdentifier("powerMenuBottom_"+colorNamesArray[p1][1],"string",MainActivity.class.getPackage().getName()));
														String title = context.getString(R.string.colorsPartTitle).replace("[PART]",mainString);//getResources().getString(context.getResources().getIdentifier("colorsCategory_"+colorNamesArray[p1][1].toString(),"string",MainActivity.class.getPackage().getName()));
														Title.setText(title);
												} catch (Throwable t1) {
														Title.setText("String Resource for "+ colorNamesArray[p1][1]+" not found.");
												}
										}
										}
										try {
												String Description = "";
												mainString = context.getResources().getString(context.getResources().getIdentifier("powerMenuMain_"+colorNamesArray[p1][1],"string",MainActivity.class.getPackage().getName()));
												if(mainString.equalsIgnoreCase(context.getString(R.string.powerMenuMain_Shutdown)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuMain_Reboot)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuMain_SoftReboot)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuBottom_Recovery)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuBottom_Bootloader)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuBottom_SafeMode))) {
												Description = context.getString(R.string.colorsPartDescDialog).replace("[BUTTON]",mainString);
												} else {
														Description = context.getString(R.string.colorsPartDescButton).replace("[BUTTON]",mainString);
												}
												//String Description = context.getResources().getString(context.getResources().getIdentifier("colorsDesc_Dialog"+colorNamesArray[p1][1],"string",MainActivity.class.getPackage().getName()));
												Desc.setText(Description);
												Desc.setVisibility(View.VISIBLE);
										} catch (Throwable t) {
												try {
														String Description = "";
														mainString = context.getResources().getString(context.getResources().getIdentifier("powerMenuBottom_"+colorNamesArray[p1][1],"string",MainActivity.class.getPackage().getName()));
														if(mainString.equalsIgnoreCase(context.getString(R.string.powerMenuMain_Shutdown)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuMain_Reboot)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuMain_SoftReboot)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuBottom_Recovery)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuBottom_Bootloader)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuBottom_SafeMode))) {
																Description = context.getString(R.string.colorsPartDescDialog).replace("[BUTTON]",mainString);
														} else {
																Description = context.getString(R.string.colorsPartDescButton).replace("[BUTTON]",mainString);
														}
														//String Description = context.getResources().getString(context.getResources().getIdentifier("colorsDesc_Dialog"+colorNamesArray[p1][1],"string",MainActivity.class.getPackage().getName()));
														Desc.setText(Description);
														Desc.setVisibility(View.VISIBLE);
												} catch (Throwable t1) {
														Desc.setVisibility(View.GONE);
														Desc.setText("String Resource for " + colorNamesArray[p1][1]+" not found.");
												}
										}
										InflatedView.setEnabled(false);
										break;
						}
				return InflatedView;
		}
		
}

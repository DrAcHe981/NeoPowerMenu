package de.NeonSoft.neopowermenu.helpers;
import android.app.*;
import android.content.*;
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

public class ColorsListAdapter extends ArrayAdapter<String>
{
		public static Activity context;
		LayoutInflater mInflater;
		public static String[][] colorNamesArray;
		public static String[] defaultColors;
		
		AlertDialog savePresetDialog;
		
		public static final String TYPE_ITEM = "item", TYPE_HEADER = "header", TYPE_LOAD = "load", TYPE_SAVE = "save";
		
		public ColorsListAdapter(Activity context,String[][] colorNames,String[] defaultColors) {
				super(context, R.layout.colorslistitem, colorNames);
				this.context = context;
				this.mInflater = context.getLayoutInflater();
				this.colorNamesArray = colorNames;
				this.defaultColors = defaultColors;
		}
		
		public String getItemType(int p1)
		{
				// TODO: Implement this method
				return colorNamesArray[p1][0];
		}
		
		@Override
		public View getView(final int p1, View InflatedView, ViewGroup p3)
		{
				// TODO: Implement this method
				LinearLayout root, previewLayout;
				final TextView Title,Preview,Desc;
				final String[] loadColor;
				final String colorType;
				InflatedView = mInflater.inflate(R.layout.colorslistitem,null);

				root = (LinearLayout) InflatedView.findViewById(R.id.colorslistitemLinearLayout_Root);
				previewLayout = (LinearLayout) InflatedView.findViewById(R.id.colorslistitemLinearLayout_Preview);
				Preview = (TextView) InflatedView.findViewById(R.id.colorslistitemTextView_Preview);
				Title = (TextView) InflatedView.findViewById(R.id.colorslistitemTextView_Text);
				Desc = (TextView) InflatedView.findViewById(R.id.colorslistitemTextView_Desc);
				
				String rowType = getItemType(p1);
						switch (rowType) {
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
																final AlertDialog.Builder alertdb = new AlertDialog.Builder(context);

																//alertdb.setTitle(R.string.preset_Save);
																View inflatedView = MainActivity.inflater.inflate(R.layout.inputdialog,null);
																final EditText Input = (EditText) inflatedView.findViewById(R.id.inputdialogEditText1);
																final EditText Input2 = (EditText) inflatedView.findViewById(R.id.inputdialogEditText2);
																final TextView OverwriteInfo = (TextView) inflatedView.findViewById(R.id.inputdialogTextViewOverwrite);
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
																								File checkFile = new File(context.getFilesDir()+"/presets/"+p1+".nps");
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
																Input.setText(MainActivity.preferences.getString("lastUsedPreset",""));
																Input2.setText(MainActivity.preferences.getString("lastPresetCreatedBy",""));
																alertdb.setView(inflatedView);
																alertdb.setCancelable(true);
																alertdb.setNegativeButton(R.string.Dialog_Cancel, new AlertDialog.OnClickListener() {

																				@Override
																				public void onClick(DialogInterface p1, int p2)
																				{
																						// TODO: Implement this method
																				}

																		});
																alertdb.setPositiveButton(R.string.Dialog_Save,new AlertDialog.OnClickListener() {

																				@Override
																				public void onClick(DialogInterface p1, int p2)
																				{
																						// TODO: Implement this method
																						try
																						{
																								File presetFile = new File(context.getFilesDir().getPath()+"/presets/"+Input.getText().toString()+".nps");
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
																								if (!Input2.getText().toString().equalsIgnoreCase("")){
																										MainActivity.preferences.edit().putString("lastPresetCreatedBy",Input2.getText().toString()).commit();
																										fw.append("Creator="+Input2.getText().toString()+"\n");
																								} else {
																										fw.append("Creator=a "+android.os.Build.MANUFACTURER+" " +android.os.Build.MODEL+ " user\n");
																								}
																								for (int i=0;i<colorNamesArray.length;i++) {
																										String[] loadColor = colorNamesArray[i][1].split("_");
																										if(loadColor.length>1) {
																												if (loadColor[1].contains("Background"))
																												{
																														fw.append(loadColor[0]+"_Backgroundcolor="+MainActivity.preferences.getString(loadColor[0]+"_Backgroundcolor","#ffffffff")+"\n");
																												} else if (loadColor[1].contains("Text")) {
																														fw.append(loadColor[0]+"_Textcolor="+MainActivity.preferences.getString(loadColor[0]+"_Textcolor","#ffffff")+"\n");
																												}
																										}
																								}
																								fw.close();
																								MainActivity.preferences.edit().putString("lastUsedPreset",Input.getText().toString()).commit();
																								Toast.makeText(context.getApplicationContext(),context.getString(R.string.presetSave_PresetSaved).replace("[PRESETNAME]",Input.getText().toString()),Toast.LENGTH_SHORT).show();
																						}
																						catch (IOException e)
																						{}
																				}
																		});
																savePresetDialog = alertdb.create();

																savePresetDialog.show();
														}
												});
										break;
								case TYPE_ITEM:
										loadColor = colorNamesArray[p1][1].split("_");
										
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
																String Description = context.getResources().getString(context.getResources().getIdentifier("colorsDesc_"+loadColor[0],"string",MainActivity.class.getPackage().getName()));
																Desc.setText(Description);
																Desc.setVisibility(View.VISIBLE);
														} catch (Throwable t) {
																Desc.setText("String Resource for colorsDesc_" + loadColor[0]+" not found.");
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
												currentColor = MainActivity.preferences.getString(loadColor[0]+colorType,defaultColors[p1]);
										} catch (Throwable t) {
												Preview.setText("Error");
										}
										Preview.setBackgroundColor(Color.parseColor(currentColor));

										root.setOnClickListener(new OnClickListener() {

														@Override
														public void onClick(View view)
														{
																// TODO: Implement this method
																//Toast.makeText(context,"Loaded color: "+loadColor[0]+colorType,Toast.LENGTH_LONG).show();
																PreferencesColorFragment.showColorPicker(Preview,loadColor[0]+colorType,MainActivity.preferences.getString(loadColor[0]+colorType,defaultColors[p1]),(defaultColors[p1].length()==7) ? false : true);
														}
												});
										break;
								case TYPE_HEADER:
										InflatedView = mInflater.inflate(R.layout.listheader,null);
										Title = (TextView) InflatedView.findViewById(R.id.listheaderTextView_Title);
										Desc = (TextView) InflatedView.findViewById(R.id.listheaderTextView_Desc);
										try {
												String title = context.getResources().getString(context.getResources().getIdentifier("colorsCategory_"+colorNamesArray[p1][1].toString(),"string",MainActivity.class.getPackage().getName()));
												Title.setText(title);
										} catch (Throwable t) {
												Title.setText("String Resource for colorsCategory_"+ colorNamesArray[p1][1]+" not found.");
										}
										try {
												String Description = context.getResources().getString(context.getResources().getIdentifier("colorsDesc_Dialog"+colorNamesArray[p1][1],"string",MainActivity.class.getPackage().getName()));
												Desc.setText(Description);
												Desc.setVisibility(View.VISIBLE);
										} catch (Throwable t) {
												Desc.setVisibility(View.GONE);
												Desc.setText("String Resource for colorsDesc_Dialog" + colorNamesArray[p1][1]+" not found.");
										}
										InflatedView.setEnabled(false);
										break;
						}
				return InflatedView;
		}
		
}

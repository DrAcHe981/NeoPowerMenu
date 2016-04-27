package de.NeonSoft.neopowermenu.Preferences;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.os.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.larswerkman.holocolorpicker.*;
import de.NeonSoft.neopowermenu.*;
import java.io.*;
import android.support.v4.app.Fragment;

import de.NeonSoft.neopowermenu.R;

public class PreferencesColorFragment extends Fragment
{

		private static LinearLayout LinearLayout_LoadPreset;
		
		private static LinearLayout LinearLayout_SavePreset;

		private static LinearLayout LinearLayout_RevealBackgroundColor;
		private static TextView TextView_RevealBackgroundColorPreview;
		
		private static LinearLayout LinearLayout_ActionRevealBackgroundColor;
		private static TextView TextView_ActionRevealBackgroundColorPreview;

		private static LinearLayout LinearLayout_DialogBackgroundColor;
		private static TextView TextView_DialogBackgroundColorPreview;

		private static LinearLayout LinearLayout_DialogShutdownBackgroundColor;
		private static TextView TextView_DialogShutdownBackgroundColorPreview;

		private static LinearLayout LinearLayout_DialogRebootBackgroundColor;
		private static TextView TextView_DialogRebootBackgroundColorPreview;

		private static LinearLayout LinearLayout_DialogSoftRebootBackgroundColor;
		private static TextView TextView_DialogSoftRebootBackgroundColorPreview;

		private static LinearLayout LinearLayout_DialogScreenshotBackgroundColor;
		private static TextView TextView_DialogScreenshotBackgroundColorPreview;

		private static LinearLayout LinearLayout_DialogRecoveryBackgroundColor;
		private static TextView TextView_DialogRecoveryBackgroundColorPreview;

		private static LinearLayout LinearLayout_DialogBootloaderBackgroundColor;
		private static TextView TextView_DialogBootloaderBackgroundColorPreview;

		private static LinearLayout LinearLayout_DialogSafeModeBackgroundColor;
		private static TextView TextView_DialogSafeModeBackgroundColorPreview;

		private static LinearLayout LinearLayout_DialogTextColor;
		private static TextView TextView_DialogTextColorPreview;

		private static LinearLayout LinearLayout_DialogShutdownTextColor;
		private static TextView TextView_DialogShutdownTextColorPreview;

		private static LinearLayout LinearLayout_DialogRebootTextColor;
		private static TextView TextView_DialogRebootTextColorPreview;

		private static LinearLayout LinearLayout_DialogSoftRebootTextColor;
		private static TextView TextView_DialogSoftRebootTextColorPreview;

		private static LinearLayout LinearLayout_DialogScreenshotTextColor;
		private static TextView TextView_DialogScreenshotTextColorPreview;
		
		private static LinearLayout LinearLayout_DialogRecoveryTextColor;
		private static TextView TextView_DialogRecoveryTextColorPreview;

		private static LinearLayout LinearLayout_DialogBootloaderTextColor;
		private static TextView TextView_DialogBootloaderTextColorPreview;

		private static LinearLayout LinearLayout_DialogSafeModeTextColor;
		private static TextView TextView_DialogSafeModeTextColorPreview;
		
		AlertDialog loadpresetsDialog;
		AlertDialog savePresetDialog;
		File[] presetsFiles;
		String[] presetsList;
		String[] lightPreset = {"#8800bcd4","#880097a7",
				"#fff5f5f5","#ffd32f2f","#ff3f51b5","#ffe91e63","#ff3f51b5","#ff8bc34a","#ff277b71","#ff009688",
				"#000000","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff"};
		String[] darkPreset = {"#88121212","#8821272b",
				"#ff212121","#ffd32f2f","#ff3f51b5","#ffe91e63","#ff3f51b5","#ff8bc34a","#ff277b71","#ff009688",
				"#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff"};
		String[] blackPreset = {"#88000000","#88000000",
				"#ff000000","#ff000000","#ff000000","#ff000000","#ff000000","#ff000000","#ff000000","#ff000000",
				"#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff"};
		String[] ColorNames = {"RevealBackground","ActionRevealBackground",
				"Dialog_Backgroundcolor","DialogShutdown_Backgroundcolor","DialogReboot_Backgroundcolor","DialogSoftReboot_Backgroundcolor","DialogScreenshot_Backgroundcolor","DialogRecovery_Backgroundcolor","DialogBootloader_Backgroundcolor","DialogSafeMode_Backgroundcolor",
				"Dialog_Textcolor","DialogShutdown_Textcolor","DialogReboot_Textcolor","DialogSoftReboot_Textcolor","DialogScreenshot_Textcolor","DialogRecovery_Textcolor","DialogBootloader_Textcolor","DialogSafeMode_Textcolor"};

		ColorPicker picker;
		boolean hexChangeViaWheel;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
				// TODO: Implement this method
				MainActivity.visibleFragment = "CustomColors";
				View InflatedView = inflater.inflate(R.layout.activity_colorpreferences,container,false);
				
				LinearLayout_LoadPreset = (LinearLayout) InflatedView.findViewById(R.id.activitycolorpreferencesLinearLayout_PresetLoad);
			
				LinearLayout_SavePreset = (LinearLayout) InflatedView.findViewById(R.id.activitycolorpreferencesLinearLayout_PresetSave);
				
				LinearLayout_RevealBackgroundColor = (LinearLayout) InflatedView.findViewById(R.id.activitycolorpreferencesLinearLayout_RevealBackgroundColor);
				TextView_RevealBackgroundColorPreview = (TextView) InflatedView.findViewById(R.id.activitycolorpreferencesTextView_RevealBackgroundColorPreview);
				
				LinearLayout_ActionRevealBackgroundColor = (LinearLayout) InflatedView.findViewById(R.id.activitycolorpreferencesLinearLayout_ActionRevealBackgroundColor);
				TextView_ActionRevealBackgroundColorPreview = (TextView) InflatedView.findViewById(R.id.activitycolorpreferencesTextView_ActionRevealBackgroundColorPreview);

				LinearLayout_DialogBackgroundColor = (LinearLayout) InflatedView.findViewById(R.id.activitycolorpreferencesLinearLayout_DialogBackgroundcolor);
				TextView_DialogBackgroundColorPreview = (TextView) InflatedView.findViewById(R.id.activitycolorpreferencesTextView_DialogBackgroundcolorPreview);

				LinearLayout_DialogShutdownBackgroundColor = (LinearLayout) InflatedView.findViewById(R.id.activitycolorpreferencesLinearLayout_DialogShutdownBackgroundcolor);
				TextView_DialogShutdownBackgroundColorPreview = (TextView) InflatedView.findViewById(R.id.activitycolorpreferencesTextView_DialogShutdownBackgroundcolorPreview);

				LinearLayout_DialogRebootBackgroundColor = (LinearLayout) InflatedView.findViewById(R.id.activitycolorpreferencesLinearLayout_DialogRebootBackgroundcolor);
				TextView_DialogRebootBackgroundColorPreview = (TextView) InflatedView.findViewById(R.id.activitycolorpreferencesTextView_DialogRebootBackgroundcolorPreview);

				LinearLayout_DialogSoftRebootBackgroundColor = (LinearLayout) InflatedView.findViewById(R.id.activitycolorpreferencesLinearLayout_DialogSoftRebootBackgroundcolor);
				TextView_DialogSoftRebootBackgroundColorPreview = (TextView) InflatedView.findViewById(R.id.activitycolorpreferencesTextView_DialogSoftRebootBackgroundcolorPreview);

				LinearLayout_DialogScreenshotBackgroundColor = (LinearLayout) InflatedView.findViewById(R.id.activitycolorpreferencesLinearLayout_DialogScreenshotBackgroundcolor);
				TextView_DialogScreenshotBackgroundColorPreview = (TextView) InflatedView.findViewById(R.id.activitycolorpreferencesTextView_DialogScreenshotBackgroundcolorPreview);

				LinearLayout_DialogRecoveryBackgroundColor = (LinearLayout) InflatedView.findViewById(R.id.activitycolorpreferencesLinearLayout_DialogRecoveryBackgroundcolor);
				TextView_DialogRecoveryBackgroundColorPreview = (TextView) InflatedView.findViewById(R.id.activitycolorpreferencesTextView_DialogRecoveryBackgroundcolorPreview);

				LinearLayout_DialogBootloaderBackgroundColor = (LinearLayout) InflatedView.findViewById(R.id.activitycolorpreferencesLinearLayout_DialogBootloaderBackgroundcolor);
				TextView_DialogBootloaderBackgroundColorPreview = (TextView) InflatedView.findViewById(R.id.activitycolorpreferencesTextView_DialogBootloaderBackgroundcolorPreview);

				LinearLayout_DialogSafeModeBackgroundColor = (LinearLayout) InflatedView.findViewById(R.id.activitycolorpreferencesLinearLayout_DialogSafeModeBackgroundcolor);
				TextView_DialogSafeModeBackgroundColorPreview = (TextView) InflatedView.findViewById(R.id.activitycolorpreferencesTextView_DialogSafeModeBackgroundcolorPreview);

				
				LinearLayout_DialogTextColor = (LinearLayout) InflatedView.findViewById(R.id.activitycolorpreferencesLinearLayout_DialogTextcolor);
				TextView_DialogTextColorPreview = (TextView) InflatedView.findViewById(R.id.activitycolorpreferencesTextView_DialogTextcolorPreview);

				LinearLayout_DialogShutdownTextColor = (LinearLayout) InflatedView.findViewById(R.id.activitycolorpreferencesLinearLayout_DialogShutdownTextcolor);
				TextView_DialogShutdownTextColorPreview = (TextView) InflatedView.findViewById(R.id.activitycolorpreferencesTextView_DialogShutdownTextcolorPreview);

				LinearLayout_DialogRebootTextColor = (LinearLayout) InflatedView.findViewById(R.id.activitycolorpreferencesLinearLayout_DialogRebootTextcolor);
				TextView_DialogRebootTextColorPreview = (TextView) InflatedView.findViewById(R.id.activitycolorpreferencesTextView_DialogRebootTextcolorPreview);

				LinearLayout_DialogSoftRebootTextColor = (LinearLayout) InflatedView.findViewById(R.id.activitycolorpreferencesLinearLayout_DialogSoftRebootTextcolor);
				TextView_DialogSoftRebootTextColorPreview = (TextView) InflatedView.findViewById(R.id.activitycolorpreferencesTextView_DialogSoftRebootTextcolorPreview);

				LinearLayout_DialogScreenshotTextColor = (LinearLayout) InflatedView.findViewById(R.id.activitycolorpreferencesLinearLayout_DialogScreenshotTextcolor);
				TextView_DialogScreenshotTextColorPreview = (TextView) InflatedView.findViewById(R.id.activitycolorpreferencesTextView_DialogScreenshotTextcolorPreview);

				LinearLayout_DialogRecoveryTextColor = (LinearLayout) InflatedView.findViewById(R.id.activitycolorpreferencesLinearLayout_DialogRecoveryTextcolor);
				TextView_DialogRecoveryTextColorPreview = (TextView) InflatedView.findViewById(R.id.activitycolorpreferencesTextView_DialogRecoveryTextcolorPreview);

				LinearLayout_DialogBootloaderTextColor = (LinearLayout) InflatedView.findViewById(R.id.activitycolorpreferencesLinearLayout_DialogBootloaderTextcolor);
				TextView_DialogBootloaderTextColorPreview = (TextView) InflatedView.findViewById(R.id.activitycolorpreferencesTextView_DialogBootloaderTextcolorPreview);

				LinearLayout_DialogSafeModeTextColor = (LinearLayout) InflatedView.findViewById(R.id.activitycolorpreferencesLinearLayout_DialogSafeModeTextcolor);
				TextView_DialogSafeModeTextColorPreview = (TextView) InflatedView.findViewById(R.id.activitycolorpreferencesTextView_DialogSafeModeTextcolorPreview);
				
				TextView_RevealBackgroundColorPreview.setBackgroundColor(Color.parseColor(MainActivity.preferences.getString(ColorNames[0],lightPreset[0])));
				TextView_ActionRevealBackgroundColorPreview.setBackgroundColor(Color.parseColor(MainActivity.preferences.getString(ColorNames[1],lightPreset[1])));
				TextView_DialogBackgroundColorPreview.setBackgroundColor(Color.parseColor(MainActivity.preferences.getString(ColorNames[2],lightPreset[2])));
				TextView_DialogShutdownBackgroundColorPreview.setBackgroundColor(Color.parseColor(MainActivity.preferences.getString(ColorNames[3],lightPreset[3])));
				TextView_DialogRebootBackgroundColorPreview.setBackgroundColor(Color.parseColor(MainActivity.preferences.getString(ColorNames[4],lightPreset[4])));
				TextView_DialogSoftRebootBackgroundColorPreview.setBackgroundColor(Color.parseColor(MainActivity.preferences.getString(ColorNames[5],lightPreset[5])));
				TextView_DialogScreenshotBackgroundColorPreview.setBackgroundColor(Color.parseColor(MainActivity.preferences.getString(ColorNames[6],lightPreset[6])));
				TextView_DialogRecoveryBackgroundColorPreview.setBackgroundColor(Color.parseColor(MainActivity.preferences.getString(ColorNames[7],lightPreset[7])));
				TextView_DialogBootloaderBackgroundColorPreview.setBackgroundColor(Color.parseColor(MainActivity.preferences.getString(ColorNames[8],lightPreset[8])));
				TextView_DialogSafeModeBackgroundColorPreview.setBackgroundColor(Color.parseColor(MainActivity.preferences.getString(ColorNames[9],lightPreset[9])));
			
				TextView_DialogTextColorPreview.setBackgroundColor(Color.parseColor(MainActivity.preferences.getString(ColorNames[10],lightPreset[10])));
				TextView_DialogShutdownTextColorPreview.setBackgroundColor(Color.parseColor(MainActivity.preferences.getString(ColorNames[11],lightPreset[11])));
				TextView_DialogRebootTextColorPreview.setBackgroundColor(Color.parseColor(MainActivity.preferences.getString(ColorNames[12],lightPreset[12])));
				TextView_DialogSoftRebootTextColorPreview.setBackgroundColor(Color.parseColor(MainActivity.preferences.getString(ColorNames[13],lightPreset[13])));
				TextView_DialogScreenshotTextColorPreview.setBackgroundColor(Color.parseColor(MainActivity.preferences.getString(ColorNames[14],lightPreset[14])));
				TextView_DialogRecoveryTextColorPreview.setBackgroundColor(Color.parseColor(MainActivity.preferences.getString(ColorNames[15],lightPreset[15])));
				TextView_DialogBootloaderTextColorPreview.setBackgroundColor(Color.parseColor(MainActivity.preferences.getString(ColorNames[16],lightPreset[16])));
				TextView_DialogSafeModeTextColorPreview.setBackgroundColor(Color.parseColor(MainActivity.preferences.getString(ColorNames[17],lightPreset[17])));
				
				LinearLayout_LoadPreset.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										MainActivity.fragmentManager.beginTransaction().replace(R.id.pref_container, new PreferencesPresetsFragment()).commit();
								}
						});
						
				LinearLayout_SavePreset.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										final AlertDialog.Builder alertdb = new AlertDialog.Builder(getActivity());

										alertdb.setTitle(R.string.preset_Save);
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
																		File checkFile = new File(getActivity().getFilesDir()+"/presets/"+p1+".nps");
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
																		File presetFile = new File(getActivity().getFilesDir().getPath()+"/presets/"+Input.getText().toString()+".nps");
																		presetFile.createNewFile();
																		FileWriter fw = new FileWriter(presetFile);
																		String versionName = "1.0";
																		try
																		{
																				versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
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
																		for (int i=0;i<ColorNames.length;i++) {
																				fw.append(ColorNames[i]+"="+MainActivity.preferences.getString(ColorNames[i],"#ffffffff")+"\n");
																		}
																		fw.close();
																		MainActivity.preferences.edit().putString("lastUsedPreset",Input.getText().toString()).commit();
																		Toast.makeText(getActivity().getApplicationContext(),getString(R.string.presetSave_PresetSaved).replace("[PRESETNAME]",Input.getText().toString()),Toast.LENGTH_SHORT).show();
																}
																catch (IOException e)
																{}
														}
												});
										savePresetDialog = alertdb.create();

										savePresetDialog.show();
								}
						});
				
				LinearLayout_RevealBackgroundColor.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										showColorPicker(TextView_RevealBackgroundColorPreview,ColorNames[0],MainActivity.preferences.getString(ColorNames[0],lightPreset[0]),true);
								}
						});
				LinearLayout_ActionRevealBackgroundColor.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										showColorPicker(TextView_ActionRevealBackgroundColorPreview,ColorNames[1],MainActivity.preferences.getString(ColorNames[1],lightPreset[1]),true);
								}
						});

				LinearLayout_DialogBackgroundColor.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										showColorPicker(TextView_DialogBackgroundColorPreview,ColorNames[2],MainActivity.preferences.getString(ColorNames[2],lightPreset[2]),true);
								}
						});
				LinearLayout_DialogShutdownBackgroundColor.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										showColorPicker(TextView_DialogShutdownBackgroundColorPreview,ColorNames[3],MainActivity.preferences.getString(ColorNames[3],lightPreset[3]),true);
								}
						});
				LinearLayout_DialogRebootBackgroundColor.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										showColorPicker(TextView_DialogRebootBackgroundColorPreview,ColorNames[4],MainActivity.preferences.getString(ColorNames[4],lightPreset[4]),true);
								}
						});
				LinearLayout_DialogSoftRebootBackgroundColor.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										showColorPicker(TextView_DialogSoftRebootBackgroundColorPreview,ColorNames[5],MainActivity.preferences.getString(ColorNames[5],lightPreset[5]),true);
								}
						});
				LinearLayout_DialogScreenshotBackgroundColor.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										showColorPicker(TextView_DialogScreenshotBackgroundColorPreview,ColorNames[6],MainActivity.preferences.getString(ColorNames[6],lightPreset[6]),true);
								}
						});
				LinearLayout_DialogRecoveryBackgroundColor.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										showColorPicker(TextView_DialogRecoveryBackgroundColorPreview,ColorNames[7],MainActivity.preferences.getString(ColorNames[7],lightPreset[7]),true);
								}
						});
				LinearLayout_DialogBootloaderBackgroundColor.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										showColorPicker(TextView_DialogBootloaderBackgroundColorPreview,ColorNames[8],MainActivity.preferences.getString(ColorNames[8],lightPreset[8]),true);
								}
						});
				LinearLayout_DialogSafeModeBackgroundColor.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										showColorPicker(TextView_DialogSafeModeBackgroundColorPreview,ColorNames[9],MainActivity.preferences.getString(ColorNames[9],lightPreset[9]),true);
								}
						});

				LinearLayout_DialogTextColor.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										showColorPicker(TextView_DialogTextColorPreview,ColorNames[10],MainActivity.preferences.getString(ColorNames[10],lightPreset[10]),false);
								}
						});
				LinearLayout_DialogShutdownTextColor.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										showColorPicker(TextView_DialogShutdownTextColorPreview,ColorNames[11],MainActivity.preferences.getString(ColorNames[11],lightPreset[11]),false);
								}
						});
				LinearLayout_DialogRebootTextColor.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										showColorPicker(TextView_DialogRebootTextColorPreview,ColorNames[12],MainActivity.preferences.getString(ColorNames[12],lightPreset[12]),false);
								}
						});
				LinearLayout_DialogSoftRebootTextColor.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										showColorPicker(TextView_DialogSoftRebootTextColorPreview,ColorNames[13],MainActivity.preferences.getString(ColorNames[13],lightPreset[13]),false);
								}
						});
				LinearLayout_DialogScreenshotTextColor.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										showColorPicker(TextView_DialogScreenshotTextColorPreview,ColorNames[14],MainActivity.preferences.getString(ColorNames[14],lightPreset[14]),false);
								}
						});
				LinearLayout_DialogRecoveryTextColor.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										showColorPicker(TextView_DialogRecoveryTextColorPreview,ColorNames[15],MainActivity.preferences.getString(ColorNames[15],lightPreset[15]),false);
								}
						});
				LinearLayout_DialogBootloaderTextColor.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										showColorPicker(TextView_DialogBootloaderTextColorPreview,ColorNames[16],MainActivity.preferences.getString(ColorNames[16],lightPreset[16]),false);
								}
						});
				LinearLayout_DialogSafeModeTextColor.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										showColorPicker(TextView_DialogSafeModeTextColorPreview,ColorNames[17],MainActivity.preferences.getString(ColorNames[17],lightPreset[17]),false);
								}
						});
				
				return InflatedView;
		}

		private void showColorPicker(final TextView previewTextView,final String forColor,final String defaultColor,final boolean showOpacityBar) {

				AlertDialog.Builder alertdb = new AlertDialog.Builder(getActivity());
				View inflatedLayout = MainActivity.inflater.inflate(R.layout.dialogcolorpicker,null);
				picker = (ColorPicker) inflatedLayout.findViewById(R.id.picker);
				TextView opacityText = (TextView) inflatedLayout.findViewById(R.id.opacitybarText);
				OpacityBar opacityBar = (OpacityBar) inflatedLayout.findViewById(R.id.opacitybar);
				SVBar SVBar = (SVBar) inflatedLayout.findViewById(R.id.SVBar);
				ValueBar valueBar = (ValueBar) inflatedLayout.findViewById(R.id.Valuebar);
				SaturationBar saturationBar = (SaturationBar) inflatedLayout.findViewById(R.id.Saturationbar);
				final TextView hexInput = (TextView) inflatedLayout.findViewById(R.id.hexInput);

				if(showOpacityBar) {
						opacityText.setVisibility(View.VISIBLE);
						opacityBar.setVisibility(View.VISIBLE);
						picker.addOpacityBar(opacityBar);
				}

				picker.addSVBar(SVBar);
				picker.addValueBar(valueBar);
				picker.addSaturationBar(saturationBar);
				picker.setOldCenterColor(Color.parseColor(MainActivity.preferences.getString(forColor,defaultColor)));
				picker.setColor(Color.parseColor(MainActivity.preferences.getString(forColor,defaultColor)));
				if(showOpacityBar) {
						hexInput.setText(""+String.format("#%08X", (0xFFFFFFFF & picker.getColor())));
				} else {
						hexInput.setText(""+String.format("#%06X", (0xFFFFFF & picker.getColor())));
				}
				/*picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {

				 @Override
				 public void onColorChanged(int p1)
				 {
				 // TODO: Implement this method
				 hexChangeViaWheel = true;
				 hexInput.setText(""+String.format("#%08X", (0xFFFFFFFF & p1)));
				 }

				 });*/
				picker.setOnTouchListener(new OnTouchListener() {

								@Override
								public boolean onTouch(View p1, MotionEvent p2)
								{
										// TODO: Implement this method
										hexChangeViaWheel = true;
										if(showOpacityBar) {
												hexInput.setText(""+String.format("#%08X", (0xFFFFFFFF & picker.getColor())));
										} else {
												hexInput.setText(""+String.format("#%06X", (0xFFFFFF & picker.getColor())));
										}
										return false;
								}
						});
				SVBar.setOnTouchListener(new OnTouchListener() {

								@Override
								public boolean onTouch(View p1, MotionEvent p2)
								{
										// TODO: Implement this method
										hexChangeViaWheel = true;
										if(showOpacityBar) {
												hexInput.setText(""+String.format("#%08X", (0xFFFFFFFF & picker.getColor())));
										} else {
												hexInput.setText(""+String.format("#%06X", (0xFFFFFF & picker.getColor())));
										}
										return false;
								}
						});
				valueBar.setOnTouchListener(new OnTouchListener() {

								@Override
								public boolean onTouch(View p1, MotionEvent p2)
								{
										// TODO: Implement this method
										hexChangeViaWheel = true;
										if(showOpacityBar) {
												hexInput.setText(""+String.format("#%08X", (0xFFFFFFFF & picker.getColor())));
										} else {
												hexInput.setText(""+String.format("#%06X", (0xFFFFFF & picker.getColor())));
										}
										return false;
								}
						});
				saturationBar.setOnTouchListener(new OnTouchListener() {

								@Override
								public boolean onTouch(View p1, MotionEvent p2)
								{
										// TODO: Implement this method
										hexChangeViaWheel = true;
										if(showOpacityBar) {
												hexInput.setText(""+String.format("#%08X", (0xFFFFFFFF & picker.getColor())));
										} else {
												hexInput.setText(""+String.format("#%06X", (0xFFFFFF & picker.getColor())));
										}
										return false;
								}
						});
				opacityBar.setOnTouchListener(new OnTouchListener() {

								@Override
								public boolean onTouch(View p1, MotionEvent p2)
								{
										// TODO: Implement this method
										hexChangeViaWheel = true;
										if(showOpacityBar) {
												hexInput.setText(""+String.format("#%08X", (0xFFFFFFFF & picker.getColor())));
										} else {
												hexInput.setText(""+String.format("#%06X", (0xFFFFFF & picker.getColor())));
										}
										return false;
								}
						});
				hexInput.addTextChangedListener(new TextWatcher() {

								@Override
								public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
								{
										// TODO: Implement this method
								}

								@Override
								public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
								{
										// TODO: Implement this method
								}

								@Override
								public void afterTextChanged(Editable p1)
								{
										// TODO: Implement this method
										if(!hexChangeViaWheel) {
												try {
														hexInput.setTextColor(Color.parseColor("#FFFFFF"));
														picker.setColor(Color.parseColor(hexInput.getText().toString()));
														//picker.invalidate();
												} catch (Throwable e) {
														hexInput.setTextColor(Color.parseColor("#FF0000"));
												}
										} else {
												hexChangeViaWheel = false;
										}
								}
						});
				alertdb.setView(inflatedLayout);
				alertdb.setTitle(R.string.Dialog_SelectColor);
				alertdb.setNegativeButton(R.string.Dialog_Cancel, new AlertDialog.OnClickListener() {

								@Override
								public void onClick(DialogInterface p1, int p2)
								{
										// TODO: Implement this method
								}
						});
				alertdb.setPositiveButton(R.string.Dialog_Save, new AlertDialog.OnClickListener() {

								@Override
								public void onClick(DialogInterface p1, int p2)
								{
										//Toast.makeText(MainActivity.context,"Color: "+picker.getColor(),Toast.LENGTH_SHORT).show();
										try {
												//String[] prefOld = TextUtils.split(forPref.getTitle().toString(),"Current: *");
												if(showOpacityBar) {
														//forPref.setTitle(prefOld[0]+"Current: "+String.format("#%08X", (0xFFFFFFFF & picker.getColor()))+")");
														MainActivity.preferences.edit().putString(forColor,String.format("#%08X", (0xFFFFFFFF & picker.getColor()))).commit();
												} else {
														//forPref.setTitle(prefOld[0]+"Current: "+String.format("#%06X", (0xFFFFFF & picker.getColor()))+")");
														MainActivity.preferences.edit().putString(forColor,String.format("#%06X", (0xFFFFFF & picker.getColor()))).commit();
												}
												previewTextView.setBackgroundColor(picker.getColor());
										} catch (Throwable t) {
												Log.e("NeoPowerMenu",t.toString());
										}
										// TODO: Implement this method
								}
						});
				alertdb.show();
		}
		
}

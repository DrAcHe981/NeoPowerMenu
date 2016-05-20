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
import de.NeonSoft.neopowermenu.helpers.*;
import java.util.*;

public class PreferencesColorFragment extends Fragment
{

		static Context mContext;
		
		private static ListView ListView_ColorsList;
		private static ColorsListAdapter adapter;
		
		AlertDialog loadpresetsDialog;
		AlertDialog savePresetDialog;
		File[] presetsFiles;
		String[] presetsList;
		
		public static String[] lightPreset = {
				"Presets","Load","Save",
				"Reveal","#8800bcd4","#ff0097a7",
				"Main","#fff5f5f5","#000000",
				"Shutdown","#ffd32f2f","#ffffff",
				"Reboot","#ff3f51b5","#ffffff",
				"SoftReboot","#ffe91e63","#ffffff",
				"Screenshot","#ff3f51b5","#ffffff",
				"Screenrecord","#ff3f51b5","#ffffff",
				"Flashlight","#ff3f51b5","#ffffff",
				"ExpandedDesktop","#ff3f51b5","#ffffff",
				"AirplaneMode","#ff3f51b5","#ffffff",
				"RestartUI","#ff3f51b5","#ffffff",
				"SoundMode","#ff3f51b5","#ffffff",
				"Recovery","#ff8bc34a","#ffffff",
				"Bootloader","#ff277b71","#ffffff",
				"SafeMode","#ff009698","#ffffff"};

		public static String[] darkPreset = {
				"Presets","Load","Save",
				"Reveal","#88121212","#ff21272b",
				"Main","#ff212121","#ffffff",
				"Shutdown","#ffd32f2f","#ffffff",
				"Reboot","#ff3f51b5","#ffffff",
				"SoftReboot","#ffe91e63","#ffffff",
				"Screenshot","#ff3f51b5","#ffffff",
				"Screenrecord","#ff3f51b5","#ffffff",
				"Flashlight","#ff3f51b5","#ffffff",
				"ExpandedDesktop","#ff3f51b5","#ffffff",
				"AirplaneMode","#ff3f51b5","#ffffff",
				"RestartUI","#ff3f51b5","#ffffff",
				"SoundMode","#ff3f51b5","#ffffff",
				"Recovery","#ff8bc34a","#ffffff",
				"Bootloader","#ff277b71","#ffffff",
				"SafeMode","#ff009698","#ffffff"};

		public static String[] blackPreset = {
				"Presets","Load","Save",
				"Reveal","#88000000","#ff000000",
				"Main","#ff000000","#ffffff",
				"Shutdown","#ff000000","#ffffff",
				"Reboot","#ff000000","#ffffff",
				"SoftReboot","#ff000000","#ffffff",
				"Screenshot","#ff000000","#ffffff",
				"Screenrecord","#ff000000","#ffffff",
				"Flashlight","#ff000000","#ffffff",
				"ExpandedDesktop","#ff000000","#ffffff",
				"AirplaneMode","#ff000000","#ffffff",
				"RestartUI","#ff000000","#ffffff",
				"SoundMode","#ff000000","#ffffff",
				"Recovery","#ff000000","#ffffff",
				"Bootloader","#ff000000","#ffffff",
				"SafeMode","#ff000000","#ffffff"};
				
		public static String[][] ColorNames = {
				{ColorsListAdapter.TYPE_HEADER,"Presets"},{ColorsListAdapter.TYPE_LOAD,"Load"},{ColorsListAdapter.TYPE_SAVE,"Save"},
				{ColorsListAdapter.TYPE_HEADER,"Reveal"},{ColorsListAdapter.TYPE_ITEM,"Reveal_Background"},{ColorsListAdapter.TYPE_ITEM,"ActionReveal_Background"},
				{ColorsListAdapter.TYPE_HEADER,"Main"},{ColorsListAdapter.TYPE_ITEM,"Dialog_Backgroundcolor"},{ColorsListAdapter.TYPE_ITEM,"Dialog_Textcolor"},
				{ColorsListAdapter.TYPE_HEADER,"Shutdown"},{ColorsListAdapter.TYPE_ITEM,"DialogShutdown_Backgroundcolor"},{ColorsListAdapter.TYPE_ITEM,"DialogShutdown_Textcolor"},
				{ColorsListAdapter.TYPE_HEADER,"Reboot"},{ColorsListAdapter.TYPE_ITEM,"DialogReboot_Backgroundcolor"},{ColorsListAdapter.TYPE_ITEM,"DialogReboot_Textcolor"},
				{ColorsListAdapter.TYPE_HEADER,"SoftReboot"},{ColorsListAdapter.TYPE_ITEM,"DialogSoftReboot_Backgroundcolor"},{ColorsListAdapter.TYPE_ITEM,"DialogSoftReboot_Textcolor"},
				{ColorsListAdapter.TYPE_HEADER,"Screenshot"},{ColorsListAdapter.TYPE_ITEM,"DialogScreenshot_Backgroundcolor"},{ColorsListAdapter.TYPE_ITEM,"DialogScreenshot_Textcolor"},
				{ColorsListAdapter.TYPE_HEADER,"Screenrecord"},{ColorsListAdapter.TYPE_ITEM,"DialogScreenrecord_Backgroundcolor"},{ColorsListAdapter.TYPE_ITEM,"DialogScreenrecord_Textcolor"},
				{ColorsListAdapter.TYPE_HEADER,"Flashlight"},{ColorsListAdapter.TYPE_ITEM,"DialogFlashlight_Backgroundcolor"},{ColorsListAdapter.TYPE_ITEM,"DialogFlashlight_Textcolor"},
				{ColorsListAdapter.TYPE_HEADER,"ExpandedDesktop"},{ColorsListAdapter.TYPE_ITEM,"DialogExpandedDesktop_Backgroundcolor"},{ColorsListAdapter.TYPE_ITEM,"DialogExpandedDesktop_Textcolor"},
				{ColorsListAdapter.TYPE_HEADER,"AirplaneMode"},{ColorsListAdapter.TYPE_ITEM,"DialogAirplaneMode_Backgroundcolor"},{ColorsListAdapter.TYPE_ITEM,"DialogAirplaneMode_Textcolor"},
				{ColorsListAdapter.TYPE_HEADER,"RestartUI"},{ColorsListAdapter.TYPE_ITEM,"DialogRestartUI_Backgroundcolor"},{ColorsListAdapter.TYPE_ITEM,"DialogRestartUI_Textcolor"},
				{ColorsListAdapter.TYPE_HEADER,"SoundMode"},{ColorsListAdapter.TYPE_ITEM,"DialogSoundMode_Backgroundcolor"},{ColorsListAdapter.TYPE_ITEM,"DialogSoundMode_Textcolor"},
				{ColorsListAdapter.TYPE_HEADER,"Recovery"},{ColorsListAdapter.TYPE_ITEM,"DialogRecovery_Backgroundcolor"},{ColorsListAdapter.TYPE_ITEM,"DialogRecovery_Textcolor"},
				{ColorsListAdapter.TYPE_HEADER,"Bootloader"},{ColorsListAdapter.TYPE_ITEM,"DialogBootloader_Backgroundcolor"},{ColorsListAdapter.TYPE_ITEM,"DialogBootloader_Textcolor"},
				{ColorsListAdapter.TYPE_HEADER,"SafeMode"},{ColorsListAdapter.TYPE_ITEM,"DialogSafeMode_Backgroundcolor"},{ColorsListAdapter.TYPE_ITEM,"DialogSafeMode_Textcolor"}};
				
		static ColorPicker picker;
		static boolean hexChangeViaWheel;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
				// TODO: Implement this method
				MainActivity.visibleFragment = "CustomColors";
				View InflatedView = inflater.inflate(R.layout.activity_colorpreferences,container,false);
				
				mContext = getActivity();
				
				ListView_ColorsList = (ListView) InflatedView.findViewById(R.id.activitycolorpreferencesListView_Colors);
				
				adapter = new ColorsListAdapter(getActivity(),ColorNames,lightPreset);
				ListView_ColorsList.setFastScrollEnabled(true);
				ListView_ColorsList.setAdapter(adapter);
				
				
				return InflatedView;
		}

		public static void showColorPicker(final TextView previewTextView,final String forColor,final String defaultColor,final boolean showOpacityBar) {

				AlertDialog.Builder alertdb = new AlertDialog.Builder(mContext);
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
										adapter.notifyDataSetChanged();
										// TODO: Implement this method
								}
						});
				alertdb.show();
		}
		
}

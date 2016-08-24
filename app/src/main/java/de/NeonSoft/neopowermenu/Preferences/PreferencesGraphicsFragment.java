package de.NeonSoft.neopowermenu.Preferences;
import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.nostra13.universalimageloader.core.listener.*;
import com.theartofdev.edmodo.cropper.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.*;
import java.io.*;
import java.util.*;

import android.support.v4.app.Fragment;
import de.NeonSoft.neopowermenu.R;

public class PreferencesGraphicsFragment extends Fragment
{
		
		public static Activity mContext;
		
		public static GridView GridView_Images;
		static graphicsAdapter graphicsAdapter;

		public static int SELECT_PICTURE_RESULT = 1;
		
		static int selected = -1;
		
		//String activeGraphics = "internal1";
		
		public static Object[][] defaultGraphics = {
				{"Progress", "stock","Progress"},
				{"Shutdown",R.drawable.poweroff1,"Shutdown"},
				{"Reboot",R.drawable.ic_av_loop,"Reboot"},
				{"SoftReboot",R.drawable.ic_image_rotate_left,"SoftReboot"},
				{"Screenshot",R.drawable.ic_device_now_wallpaper,"Screenshot"},
				{"Screenrecord",R.drawable.ic_image_center_focus_weak,"Screenrecord"},
				{"Flashlight",R.drawable.ic_qs_torch_off,"FlashlightOff"},
				{"Flashlight",R.drawable.ic_qs_torch_on,"FlashlightOn"},
				{"ExpandedDesktop",R.drawable.ic_device_developer_mode,"ExpandedDesktop"},
				{"AirplaneMode",R.drawable.ic_device_airplanemode_off,"AirplaneModeOff"},
				{"AirplaneMode",R.drawable.ic_device_airplanemode_on,"AirplaneModeOn"},
				{"RestartUI",R.drawable.ic_alert_error,"RestartUI"},
				{"SoundNormal",R.drawable.ic_av_volume_up,"SoundModeOn"},
				{"SoundSilent",R.drawable.ic_av_volume_off,"SoundModeOff"},
				{"SoundVibrate",R.drawable.ic_notification_vibration,"SoundModeVibrate"},
				{"Recovery",R.drawable.ic_hardware_memory,"Recovery"},
				{"Bootloader",R.drawable.ic_action_settings_backup_restore,"Bootloader"},
				{"SafeMode",R.drawable.ic_notification_sync_problem,"SafeMode"}};
		
		Object[][] loadGraphics = {
				{"Progress", "stock","Progress"},
				{"Shutdown",R.drawable.poweroff1,"Shutdown"},
				{"Reboot",R.drawable.ic_av_loop,"Reboot"},
				{"SoftReboot",R.drawable.ic_image_rotate_left,"SoftReboot"},
				{"Screenshot",R.drawable.ic_device_now_wallpaper,"Screenshot"},
				{"Screenrecord",R.drawable.ic_image_center_focus_weak,"Screenrecord"},
				{"Flashlight",R.drawable.ic_qs_torch_off,"FlashlightOff"},
				{"Flashlight",R.drawable.ic_qs_torch_on,"FlashlightOn"},
				{"ExpandedDesktop",R.drawable.ic_device_developer_mode,"ExpandedDesktop"},
				{"AirplaneMode",R.drawable.ic_device_airplanemode_off,"AirplaneModeOff"},
				{"AirplaneMode",R.drawable.ic_device_airplanemode_on,"AirplaneModeOn"},
				{"RestartUI",R.drawable.ic_alert_error,"RestartUI"},
				{"SoundNormal",R.drawable.ic_av_volume_up,"SoundModeOn"},
				{"SoundSilent",R.drawable.ic_av_volume_off,"SoundModeOff"},
				{"SoundVibrate",R.drawable.ic_notification_vibration,"SoundModeVibrate"},
				{"Recovery",R.drawable.ic_hardware_memory,"Recovery"},
				{"Bootloader",R.drawable.ic_action_settings_backup_restore,"Bootloader"},
				{"SafeMode",R.drawable.ic_notification_sync_problem,"SafeMode"}};
				
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
				// TODO: Implement this method
				MainActivity.visibleFragment = "Graphics";
				MainActivity.actionbar.setTitle(getString(R.string.preferences_Graphics).split("\\|")[0]);
				MainActivity.actionbar.setSubTitle(getString(R.string.preferences_Graphics).split("\\|")[1]);
				
				MainActivity.actionbar.setButton(getString(R.string.PreviewPowerMenu),R.drawable.ic_action_launch,MainActivity.previewOnClickListener);
				
				mContext = getActivity();
				
				View InflatedView = inflater.inflate(R.layout.activity_graphics, container, false);
				
				GridView_Images = (GridView) InflatedView.findViewById(R.id.activitygraphicsGridView1);
				
				graphicsAdapter = new graphicsAdapter(getActivity(),MainActivity.imageLoader);
				GridView_Images.setFastScrollEnabled(true);
				PauseOnScrollListener listener = new PauseOnScrollListener(MainActivity.imageLoader,true, true);
				GridView_Images.setOnScrollListener(listener);
				GridView_Images.setAdapter(graphicsAdapter);
				
				for( int i = 0; i < defaultGraphics.length; i++) {
						File checkFile = new File(mContext.getFilesDir().getPath()+"/images/"+defaultGraphics[i][2]+".png");
						if (checkFile.exists()) {
								loadGraphics[i][1] = mContext.getFilesDir().getPath()+"/images/"+defaultGraphics[i][2]+".png";
						} else if (defaultGraphics[i][0].toString().equalsIgnoreCase("Progress")) {
								loadGraphics[i][1] = MainActivity.preferences.getString("ProgressDrawable","stock");
						}
				}
				
				if(loadGraphics[0][1].toString().equalsIgnoreCase("pb/dr")) {
						loadGraphics[0][1] = R.drawable.progress_pitchblack_darkred_cm13;
				} else if(loadGraphics[0][1].toString().equalsIgnoreCase("WeaReOne")) {
						loadGraphics[0][1] = R.drawable.progress_weareone;
				}
				
				graphicsAdapter.addFallbackGraphics(defaultGraphics);
				graphicsAdapter.addAll(loadGraphics);
				
				GridView_Images.setOnItemClickListener(new AdapterView.OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
								{
										// TODO: Implement this method
										selected = p3;
										slideDownDialogFragment dialogFragment = new slideDownDialogFragment(getActivity(),MainActivity.fragmentManager);
										dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

														@Override
														public void onListItemClick(int position, String text)
														{
																// TODO: Implement this method
																graphicsAdapter.removeFromCache(defaultGraphics[selected][2].toString());
																if(position == 0) {
																		if(defaultGraphics[selected][0].toString().equalsIgnoreCase("Progress")) {
																				MainActivity.preferences.edit().putString("ProgressDrawable","Stock").commit();
																		}
																		new File(loadGraphics[selected][1].toString()).delete();
																		loadGraphics[selected][1] = defaultGraphics[selected][1];
																		graphicsAdapter.remove(selected);
																		Object[] item = {defaultGraphics[selected][0],defaultGraphics[selected][1],defaultGraphics[selected][2]};
																		graphicsAdapter.addAt(selected,item);
																} else if (position == 1) {
																		//Toast.makeText(getActivity(),getString(R.string.presetsManager_NJI),Toast.LENGTH_SHORT).show();
																		slideDownDialogFragment presetsListDialog = new slideDownDialogFragment(getActivity(),MainActivity.fragmentManager);
																		presetsListDialog.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

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
																						public void onPositiveClick(Bundle resultBundle)
																						{
																								// TODO: Implement this method
																								new File(loadGraphics[selected][1].toString()).delete();
																								if(helper.unzipFile(mContext.getFilesDir().getPath()+"/presets/"+resultBundle.getString(slideDownDialogFragment.RESULT_LIST)+".nps",mContext.getFilesDir().getPath()+"/images/",defaultGraphics[selected][2]+".png",null) == null) {
																										loadGraphics[selected][1] = mContext.getFilesDir().getPath()+"/images/"+defaultGraphics[selected][2]+".png";
																										graphicsAdapter.remove(selected);
																										Object[] item = {defaultGraphics[selected][0],loadGraphics[selected][1],defaultGraphics[selected][2]};
																										graphicsAdapter.addAt(selected,item);
																								}
																						}

																						@Override
																						public void onTouchOutside()
																						{
																								// TODO: Implement this method
																						}
																				});
																				helper.zipLogging(false);
																		File presetsFolder = new File(getActivity().getFilesDir().getPath() + "/presets/");
																		File[] presetsFiles = presetsFolder.listFiles(new FilenameFilter() {
																						public boolean accept(File dir, String name)
																						{
																								boolean supported = helper.isValidZip(dir+"/"+name,null) && helper.unzipFile(dir+"/"+name,mContext.getFilesDir().getAbsolutePath()+"/temp/",defaultGraphics[selected][2]+".png",null) == null;
																								return (supported && name.toLowerCase().endsWith(".nps"));
																						}});
																						helper.zipLogging(true);
																		if(presetsFiles.length>0) {
																				String[] presetsListTitles = new String[presetsFiles.length];
																				for (int i=0;i < presetsFiles.length;i++)
																				{
																						presetsListTitles[i] = presetsFiles[i].getName().split(".nps")[0];
																				}
																				presetsListDialog.setList(ListView.CHOICE_MODE_SINGLE,presetsListTitles, -1, false);
																				presetsListDialog.setNegativeButton(getString(R.string.Dialog_Buttons).split("\\|")[4]);
																				presetsListDialog.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[6]);
																		} else {
																				presetsListDialog.setText(getString(R.string.graphics_NoPresetsFound));
																				presetsListDialog.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[0]);
																		}
																		presetsListDialog.showDialog(R.id.dialog_container);
																} else if (position == 2) {
																		Intent intent = new Intent();
																		intent.setType("image/*");
																		intent.setAction(Intent.ACTION_GET_CONTENT);
																		startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE_RESULT);
																} else if (position == 3) {
																		MainActivity.preferences.edit().putString("ProgressDrawable","pb/dr").commit();
																		new File(loadGraphics[selected][1].toString()).delete();
																		loadGraphics[selected][1] = R.drawable.progress_pitchblack_darkred_cm13;
																		graphicsAdapter.remove(selected);
																		Object[] item = {defaultGraphics[selected][0],R.drawable.progress_pitchblack_darkred_cm13,defaultGraphics[selected][2]};
																		graphicsAdapter.addAt(selected,item);
																} else if (position == 4) {
																		MainActivity.preferences.edit().putString("ProgressDrawable","WeaReOne").commit();
																		new File(loadGraphics[selected][1].toString()).delete();
																		loadGraphics[selected][1] = R.drawable.progress_weareone;
																		graphicsAdapter.remove(selected);
																		Object[] item = {defaultGraphics[selected][0],R.drawable.progress_weareone,defaultGraphics[selected][2]};
																		graphicsAdapter.addAt(selected,item);
																}
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
														public void onPositiveClick(Bundle resultBundle)
														{
																// TODO: Implement this method
														}

														@Override
														public void onTouchOutside()
														{
																// TODO: Implement this method
														}
												});
										String[] choose = (getString(R.string.graphics_Choose)+(defaultGraphics[selected][0].toString().equalsIgnoreCase("Progress") ? "|PitchBlack / DarkRed CM13|We aRe One" : "")).split("\\|");
										dialogFragment.setList(ListView.CHOICE_MODE_NONE, choose, -1, true);
										dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[4]);
										dialogFragment.showDialog(R.id.dialog_container);
								}
						});
				
				return InflatedView;
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data)
		{
				// TODO: Implement this method
				super.onActivityResult(requestCode, resultCode, data);
				if (requestCode == SELECT_PICTURE_RESULT) {
						if (resultCode == mContext.RESULT_OK) {
								startCrop(data.getData());
						}
				}
		}
		
		public void startCrop(Uri selectedImage) {
				//Intent intent = 
				/*CropImage.activity(selectedImage)
						.setGuidelines(CropImageView.Guidelines.ON)
						.setActivityTitle(mContext.getString(R.string.app_name))
						.setAllowRotation(true)
						.setCropShape(CropImageView.CropShape.OVAL)
						.setFixAspectRatio(true)
						.setShowCropOverlay(true)
						.setBorderCornerColor(mContext.getResources().getColor(R.color.colorAccentDarkThemeTrans))
						.setBorderLineColor(mContext.getResources().getColor(R.color.colorAccentDarkTheme))
						.setGuidelinesColor(mContext.getResources().getColor(R.color.colorAccentDarkThemeTrans))
						.start(mContext);*/
				//mContext.startActivityForResult(intent,CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
				
				MainActivity.changePrefPage(new Cropper(defaultGraphics[selected][0].toString(),selectedImage,defaultGraphics[selected][2].toString()+".png"), false);
				
		}
		
		public void cropComplete(String croppedImage) {
				graphicsAdapter.remove(selected);
				Object[] item = {defaultGraphics[selected][0],croppedImage,defaultGraphics[selected][2]};
				graphicsAdapter.addAt(selected,item);
		}
		
}

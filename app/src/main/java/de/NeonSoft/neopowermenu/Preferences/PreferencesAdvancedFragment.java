package de.NeonSoft.neopowermenu.Preferences;
import android.support.v4.app.*;
import android.view.*;
import android.os.*;
import de.NeonSoft.neopowermenu.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.helpers.*;
import android.widget.SeekBar.*;

public class PreferencesAdvancedFragment extends Fragment
{

		LinearLayout LinearLayout_UseGraphics;
		Switch Switch_UseGraphics;
		boolean boolean_UseGraphics;
		
		LinearLayout LinearLayout_DialogGravity;
		
		LinearLayout LinearLayout_ShowOnLockScreen;
		Switch Switch_ShowOnLockScreen;
		boolean boolean_ShowOnLockScreen;
		
		LinearLayout LinearLayout_ScreenshotDelay;
		SeekBar SeekBar_ScreenshotDelay;
		TextView TextView_ScreenshotDelayTime;
		long Long_ScreenshotDelay;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
				// TODO: Implement this method
				MainActivity.visibleFragment = "Advanced";
				View InflatedView = inflater.inflate(R.layout.activity_advanced,null);

				boolean_UseGraphics = MainActivity.preferences.getBoolean("UseGraphics",false);
				LinearLayout_UseGraphics = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_UseGraphics);
				Switch_UseGraphics = (Switch) InflatedView.findViewById(R.id.activityadvancedSwitch_UseGraphics);

				Switch_UseGraphics.setChecked(boolean_UseGraphics);
				Switch_UseGraphics.setClickable(false);
				Switch_UseGraphics.setFocusable(false);

				LinearLayout_UseGraphics.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										boolean_UseGraphics = !boolean_UseGraphics;
										MainActivity.preferences.edit().putBoolean("UseGraphics",boolean_UseGraphics).commit();
										Switch_UseGraphics.setChecked(boolean_UseGraphics);
								}
						});
						
				LinearLayout_DialogGravity = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_DialogGravity);
						
				LinearLayout_DialogGravity.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										GravityChooserDialog gcd = new GravityChooserDialog();
										//MainActivity.fragmentManager.beginTransaction().replace(R.id.pref_container,new GravityChooserDialog()).commit();
										gcd.setStyle(gcd.STYLE_NO_FRAME,R.style.TransparentApp);
										gcd.show(MainActivity.fragmentManager,"gravity_chooser");
								}
						});
				
				boolean_ShowOnLockScreen = MainActivity.preferences.getBoolean("ShowOnLockScreen",true);
				LinearLayout_ShowOnLockScreen = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_ShowOnLockscreen);
				Switch_ShowOnLockScreen = (Switch) InflatedView.findViewById(R.id.activityadvancedSwitch_ShowOnLockScreen);
				
				Switch_ShowOnLockScreen.setChecked(boolean_ShowOnLockScreen);
				Switch_ShowOnLockScreen.setClickable(false);
				Switch_ShowOnLockScreen.setFocusable(false);
				
				LinearLayout_ShowOnLockScreen.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										boolean_ShowOnLockScreen = !boolean_ShowOnLockScreen;
										MainActivity.preferences.edit().putBoolean("ShowOnLockScreen",boolean_ShowOnLockScreen).commit();
										Switch_ShowOnLockScreen.setChecked(boolean_ShowOnLockScreen);
								}
						});
						
				Long_ScreenshotDelay = MainActivity.preferences.getLong("ScreenshotDelay",1000);
				LinearLayout_ScreenshotDelay = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_ScreenshotDelay);
				SeekBar_ScreenshotDelay = (SeekBar) InflatedView.findViewById(R.id.activityadvancedSeekBar_ScreenshotDelay);
				TextView_ScreenshotDelayTime = (TextView) InflatedView.findViewById(R.id.activityadvancedTextView_ScreenshotDelayTime);
				
				SeekBar_ScreenshotDelay.setMax(150);
				SeekBar_ScreenshotDelay.setProgress((int) (Long_ScreenshotDelay/1000));
				if(Long_ScreenshotDelay == 0 ) {
						TextView_ScreenshotDelayTime.setText(R.string.advancedPrefs_DelayZero);
				} else {
				TextView_ScreenshotDelayTime.setText(helper.getTimeString(Long_ScreenshotDelay,true));
				}
				SeekBar_ScreenshotDelay.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

								@Override
								public void onProgressChanged(SeekBar p1, int p2, boolean p3)
								{
										// TODO: Implement this method
										Long_ScreenshotDelay = (p2*1000);
										if(Long_ScreenshotDelay == 0 ) {
												TextView_ScreenshotDelayTime.setText(R.string.advancedPrefs_DelayZero);
										} else {
										TextView_ScreenshotDelayTime.setText(helper.getTimeString(Long_ScreenshotDelay,true));
										}
								}

								@Override
								public void onStartTrackingTouch(SeekBar p1)
								{
										// TODO: Implement this method
								}

								@Override
								public void onStopTrackingTouch(SeekBar p1)
								{
										// TODO: Implement this method
										MainActivity.preferences.edit().putLong("ScreenshotDelay",Long_ScreenshotDelay).commit();
								}
						});
						
				return InflatedView;
		}
		
}

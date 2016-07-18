package de.NeonSoft.neopowermenu.Preferences;

import android.view.*;
import android.os.*;
import de.NeonSoft.neopowermenu.*;
import android.widget.*;
import android.view.View.*;
import android.support.v4.app.*;

public class GravityChooserDialog extends DialogFragment
{
		
		LinearLayout LinearLayout_ImageHolder;

		boolean boolean_DialogGravityTop = false;
		LinearLayout LinearLayout_DialogGravityTop;
		Switch Switch_DialogGravityTop;

		boolean boolean_DialogGravityLeft = false;
		LinearLayout LinearLayout_DialogGravityLeft;
		Switch Switch_DialogGravityLeft;

		//boolean boolean_DialogGravityCenter = false;
		//LinearLayout LinearLayout_DialogGravityCenter;
		//Switch Switch_DialogGravityCenter;

		boolean boolean_DialogGravityRight = false;
		LinearLayout LinearLayout_DialogGravityRight;
		Switch Switch_DialogGravityRight;

		boolean boolean_DialogGravityBottom = false;
		LinearLayout LinearLayout_DialogGravityBottom;
		Switch Switch_DialogGravityBottom;
		
		@Override
		public View onCreateView(LayoutInflater p1, ViewGroup p2, Bundle p3)
		{
				MainActivity.visibleFragment = "Gravity";
				
				MainActivity.actionbar.setTitle(getString(R.string.advancedPrefsTitle_DialogGravity));
				MainActivity.actionbar.setSubTitle(getString(R.string.advancedPrefsDesc_DialogGravity));
				
				View InflatedView = p1.inflate(R.layout.dialoggravitychooser,p2,false);
				
				LinearLayout_ImageHolder = (LinearLayout) InflatedView.findViewById(R.id.dialoggravitychooserLinearLayout_ImageHolder);
				
				boolean_DialogGravityTop = MainActivity.preferences.getBoolean("DialogGravityTop",false);
				LinearLayout_DialogGravityTop = (LinearLayout) InflatedView.findViewById(R.id.dialoggravitychooserLinearLayout_DialogGravityTop);
				Switch_DialogGravityTop = (Switch) InflatedView.findViewById(R.id.dialoggravitychooserSwitch_DialogGravityTop);
				Switch_DialogGravityTop.setClickable(false);
				Switch_DialogGravityTop.setFocusable(false);
				Switch_DialogGravityTop.setChecked(boolean_DialogGravityTop);

				boolean_DialogGravityLeft = MainActivity.preferences.getBoolean("DialogGravityLeft",false);
				LinearLayout_DialogGravityLeft = (LinearLayout) InflatedView.findViewById(R.id.dialoggravitychooserLinearLayout_DialogGravityLeft);
				Switch_DialogGravityLeft = (Switch) InflatedView.findViewById(R.id.dialoggravitychooserSwitch_DialogGravityLeft);
				Switch_DialogGravityLeft.setClickable(false);
				Switch_DialogGravityLeft.setFocusable(false);
				Switch_DialogGravityLeft.setChecked(boolean_DialogGravityLeft);

				//boolean_DialogGravityCenter = MainActivity.preferences.getBoolean("DialogGravityCenter",false);
				//LinearLayout_DialogGravityCenter = (LinearLayout) InflatedView.findViewById(R.id.dialoggravitychooserLinearLayout_DialogGravityCenter);
				//Switch_DialogGravityCenter = (Switch) InflatedView.findViewById(R.id.dialoggravitychooserSwitch_DialogGravityCenter);
				//Switch_DialogGravityCenter.setClickable(false);
				//Switch_DialogGravityCenter.setFocusable(false);
				//Switch_DialogGravityCenter.setChecked(boolean_DialogGravityCenter);

				boolean_DialogGravityRight = MainActivity.preferences.getBoolean("DialogGravityRight",false);
				LinearLayout_DialogGravityRight = (LinearLayout) InflatedView.findViewById(R.id.dialoggravitychooserLinearLayout_DialogGravityRight);
				Switch_DialogGravityRight = (Switch) InflatedView.findViewById(R.id.dialoggravitychooserSwitch_DialogGravityRight);
				Switch_DialogGravityRight.setClickable(false);
				Switch_DialogGravityRight.setFocusable(false);
				Switch_DialogGravityRight.setChecked(boolean_DialogGravityRight);

				boolean_DialogGravityBottom = MainActivity.preferences.getBoolean("DialogGravityBottom",false);
				LinearLayout_DialogGravityBottom = (LinearLayout) InflatedView.findViewById(R.id.dialoggravitychooserLinearLayout_DialogGravityBottom);
				Switch_DialogGravityBottom = (Switch) InflatedView.findViewById(R.id.dialoggravitychooserSwitch_DialogGravityBottom);
				Switch_DialogGravityBottom.setClickable(false);
				Switch_DialogGravityBottom.setFocusable(false);
				Switch_DialogGravityBottom.setChecked(boolean_DialogGravityBottom);
				
				LinearLayout_DialogGravityTop.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										boolean_DialogGravityTop = !boolean_DialogGravityTop;
										Switch_DialogGravityTop.setChecked(boolean_DialogGravityTop);
										if(boolean_DialogGravityTop) {
												boolean_DialogGravityBottom = false;
												Switch_DialogGravityBottom.setChecked(boolean_DialogGravityBottom);
												MainActivity.preferences.edit().putBoolean("DialogGravityBottom",boolean_DialogGravityBottom).commit();
										}
										MainActivity.preferences.edit().putBoolean("DialogGravityTop",boolean_DialogGravityTop).commit();
										changeGravity();
								}
						});
						
				LinearLayout_DialogGravityLeft.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										boolean_DialogGravityLeft = !boolean_DialogGravityLeft;
										Switch_DialogGravityLeft.setChecked(boolean_DialogGravityLeft);
										if(boolean_DialogGravityLeft) {
												boolean_DialogGravityRight = false;
												Switch_DialogGravityRight.setChecked(boolean_DialogGravityRight);
												MainActivity.preferences.edit().putBoolean("DialogGravityRight",boolean_DialogGravityRight).commit();
										}
										MainActivity.preferences.edit().putBoolean("DialogGravityLeft",boolean_DialogGravityLeft).commit();
										changeGravity();
								}
						});

				LinearLayout_DialogGravityRight.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										boolean_DialogGravityRight = !boolean_DialogGravityRight;
										Switch_DialogGravityRight.setChecked(boolean_DialogGravityRight);
										if(boolean_DialogGravityRight) {
												boolean_DialogGravityLeft = false;
												Switch_DialogGravityLeft.setChecked(boolean_DialogGravityLeft);
												MainActivity.preferences.edit().putBoolean("DialogGravityLeft",boolean_DialogGravityLeft).commit();
										}
										MainActivity.preferences.edit().putBoolean("DialogGravityRight",boolean_DialogGravityRight).commit();
										changeGravity();
								}
						});

				LinearLayout_DialogGravityBottom.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										boolean_DialogGravityBottom = !boolean_DialogGravityBottom;
										Switch_DialogGravityBottom.setChecked(boolean_DialogGravityBottom);
										if(boolean_DialogGravityBottom) {
												boolean_DialogGravityTop = false;
												Switch_DialogGravityTop.setChecked(boolean_DialogGravityTop);
												MainActivity.preferences.edit().putBoolean("DialogGravityTop",boolean_DialogGravityTop).commit();
										}
										MainActivity.preferences.edit().putBoolean("DialogGravityBottom",boolean_DialogGravityBottom).commit();
										changeGravity();
								}
						});
				
						changeGravity();
						
				return InflatedView;
		}

		void changeGravity() {
				int gravity = 0;
				if(boolean_DialogGravityTop) {
						gravity |= Gravity.TOP;
				} else if(boolean_DialogGravityBottom) {
						gravity |= Gravity.BOTTOM;
				} else {
						gravity |= Gravity.CENTER_VERTICAL;
				}
				if(boolean_DialogGravityLeft) {
						gravity |= Gravity.LEFT;
				} else if(boolean_DialogGravityRight) {
						gravity |= Gravity.RIGHT;
				} else {
						gravity |= Gravity.CENTER_HORIZONTAL;
				}
				LinearLayout_ImageHolder.setGravity(gravity);
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState)
		{
				super.onActivityCreated(savedInstanceState);
				if(getShowsDialog()) {
        getDialog().getWindow()
						.getAttributes().windowAnimations = R.style.DialogAnimation;
				}
		}
		
}

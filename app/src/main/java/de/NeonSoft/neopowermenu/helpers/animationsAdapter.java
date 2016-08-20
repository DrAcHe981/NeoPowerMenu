package de.NeonSoft.neopowermenu.helpers;

import android.app.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.*;

public class animationsAdapter extends ArrayAdapter<Integer>
{
		
		Activity mContext;
		String[] items;
		int[] defaultTypes;
		//int[] types;
		//int[] speeds;
		
		public animationsAdapter(Activity context,String[] items, int[] defaultTypes) {//, int[] types, int[] speeds) {
				super(context,R.layout.animations_item,items);
				this.mContext = context;
				this.items = items;
				this.defaultTypes = defaultTypes;
				//this.types = types;
				//this.speeds = speeds;
		}

		@Override
		public View getView(final int p1, View p2, ViewGroup p3)
		{
				// TODO: Implement this method
				View InflatedView = mContext.getLayoutInflater().inflate(R.layout.animations_item,p3,false);
				
				LinearLayout Root = (LinearLayout) InflatedView.findViewById(R.id.animationsitemLinearLayout_Root);

				TextView Text = (TextView) InflatedView.findViewById(R.id.animationsitemTextView_Title);
				final TextView Desc = (TextView) InflatedView.findViewById(R.id.animationsitemTextView_Desc);

				Text.setText(mContext.getString(R.string.animations_Items).split("\\|")[p1]);
				if(MainActivity.animationPrefs.getInt(items[p1]+"_type",defaultTypes[p1])<mContext.getString(R.string.animations_Types).split("\\|").length-1) {
				Desc.setText(mContext.getString(R.string.animations_Desc)
						.replace("[NAME]",mContext.getString(R.string.animations_Types).split("\\|")[MainActivity.animationPrefs.getInt(items[p1]+"_type",defaultTypes[p1])])
										 .replace("[SPEED]",mContext.getString(R.string.animations_Speeds).split("\\|")[MainActivity.animationPrefs.getInt(items[p1]+"_speed",2)]));
		} else {
				Desc.setText(mContext.getString(R.string.animations_Types).split("\\|")[mContext.getString(R.string.animations_Types).split("\\|").length-1]);
		}
				//Desc.setText(mContext.getString(R.string.animations_Desc).replace("[NAME]",types[MainActivity.animationsPrefs.getInt(items[p1]+"_type",defaultTypes[p1])]).replace("[SPEED]",speeds[MainActivity.animationsPrefs.getInt(names[p1]+"_speed",2)]));

				Root.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View view)
								{
										// TODO: Implement this method
										slideDownDialogFragment dialogFragment = new slideDownDialogFragment(mContext, MainActivity.fragmentManager);
										dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

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
																MainActivity.animationPrefs.edit().putInt(items[p1]+"_type",resultBundle.getInt(slideDownDialogFragment.RESULT_LIST)).commit();
																if(resultBundle.getInt(slideDownDialogFragment.RESULT_LIST)<mContext.getString(R.string.animations_Types).split("\\|").length-1) {
																Desc.setText(mContext.getString(R.string.animations_Desc)
																						 .replace("[NAME]",mContext.getString(R.string.animations_Types).split("\\|")[MainActivity.animationPrefs.getInt(items[p1]+"_type",defaultTypes[p1])])
																						 .replace("[SPEED]",mContext.getString(R.string.animations_Speeds).split("\\|")[MainActivity.animationPrefs.getInt(items[p1]+"_speed",2)]));
																slideDownDialogFragment dialogFragment = new slideDownDialogFragment(mContext, MainActivity.fragmentManager);
																dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

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
																						MainActivity.animationPrefs.edit().putInt(items[p1]+"_speed",resultBundle.getInt(slideDownDialogFragment.RESULT_LIST)).commit();
																						Desc.setText(mContext.getString(R.string.animations_Desc)
																												 .replace("[NAME]",mContext.getString(R.string.animations_Types).split("\\|")[MainActivity.animationPrefs.getInt(items[p1]+"_type",defaultTypes[p1])])
																												 .replace("[SPEED]",mContext.getString(R.string.animations_Speeds).split("\\|")[MainActivity.animationPrefs.getInt(items[p1]+"_speed",2)]));
																				}

																				@Override
																				public void onTouchOutside()
																				{
																						// TODO: Implement this method
																				}
																		});
																dialogFragment.setList(ListView.CHOICE_MODE_SINGLE, mContext.getString(R.string.animations_Speeds).split("\\|"), MainActivity.animationPrefs.getInt(items[p1]+"_speed",2), false);
																dialogFragment.setNegativeButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
																dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_OK]);
																dialogFragment.showDialog(R.id.dialog_container);
																} else {
																		Desc.setText(mContext.getString(R.string.animations_Types).split("\\|")[resultBundle.getInt(slideDownDialogFragment.RESULT_LIST)]);
																}
														}

														@Override
														public void onTouchOutside()
														{
																// TODO: Implement this method
														}
												});
										dialogFragment.setList(ListView.CHOICE_MODE_SINGLE, mContext.getString(R.string.animations_Types).split("\\|"), MainActivity.animationPrefs.getInt(items[p1]+"_type",defaultTypes[p1]), false);
										dialogFragment.setNegativeButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
										dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_OK]);
										dialogFragment.showDialog(R.id.dialog_container);
								}
						});
				
				return InflatedView;
		}
		
}

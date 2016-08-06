package de.NeonSoft.neopowermenu.helpers;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;
import java.util.*;

public class aboutAdapter extends ArrayAdapter<String>
{
		private Activity mContext;
		
		private ArrayList<String> itemsTitles;
		private ArrayList<String> itemsTexts;
		
		private int IdClickCount = 20;
		private Toast lastToast;
		private Timer timer = new Timer();
		private boolean timerRunning = false;
		
		public aboutAdapter(Activity context,ArrayList<String> itemTitles,ArrayList<String> itemTexts) {
				super(context,R.layout.aboutlistitem,itemTitles);
				this.mContext = context;
				this.itemsTitles = itemTitles;
				this.itemsTexts = itemTexts;
		}

		@Override
		public View getView(final int p1, View p2, ViewGroup p3)
		{
				// TODO: Implement this method
				View InflatedView = mContext.getLayoutInflater().inflate(R.layout.aboutlistitem,p3,false);
				
				LinearLayout root = (LinearLayout) InflatedView.findViewById(R.id.aboutlistitemLinearLayout);
				TextView Title = (TextView) InflatedView.findViewById(R.id.aboutlistitemTextView1);
				TextView Text = (TextView) InflatedView.findViewById(R.id.aboutlistitemTextView2);
				
				Title.setText(this.itemsTitles.get(p1));
				Text.setText(this.itemsTexts.get(p1));
				
				if(this.itemsTexts.get(p1).contains("Your Device Id")) {
						Text.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View view)
										{
												if(IdClickCount > 1) {
														if(lastToast!=null) lastToast.cancel();
														IdClickCount--;
														if(IdClickCount > 10) {
												try {
												ClipboardManager cpm = new ClipboardManager(mContext, new Handler());
												cpm.setText(MainActivity.deviceUniqeId);
												if(cpm.getText().toString().equals(MainActivity.deviceUniqeId)) {
														lastToast = Toast.makeText(mContext,MainActivity.deviceUniqeId+" copied to clipboard.",Toast.LENGTH_SHORT);
														lastToast.show();
												} else {
														lastToast = Toast.makeText(mContext,"Failed to put to clipboard...",Toast.LENGTH_SHORT);
														lastToast.show();
												}
												}
												catch (Throwable t) {
														Log.e("NPM","Failed to put in clipboard: "+t.toString());
														lastToast = Toast.makeText(mContext,"Failed to put to clipboard...",Toast.LENGTH_SHORT);
														lastToast.show();
												}
														} else {
																lastToast = Toast.makeText(mContext,"Click "+IdClickCount+" times to edit your deviceId.\nBe carefull with this!",Toast.LENGTH_LONG);
																lastToast.show();
														}
														/*if(timerRunning) timer.cancel();
														timerRunning = true;
														timer.schedule(new TimerTask() {

																		@Override
																		public void run()
																		{
																				timerRunning = false;
																				IdClickCount = 0;
																				//Toast.makeText(mContext,"idClickCount reset",Toast.LENGTH_SHORT).show();
																		}
																}, 1000);*/
												} else {
														IdClickCount = 20;
														final slideDownDialogFragment dialogFragment = new slideDownDialogFragment(mContext,MainActivity.fragmentManager);
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
																				
																		}

																		@Override
																		public void onPositiveClick(ArrayList<String> resultData)
																		{
																				MainActivity.deviceUniqeId = (resultData.get(1).equalsIgnoreCase("true") ? helper.md5Crypto(resultData.get(0)) : resultData.get(0));
																				MainActivity.preferences.edit().putString("userUniqeId",MainActivity.deviceUniqeId).commit();
																				itemsTexts.set(p1,"Your Device Id:\n" + ((MainActivity.deviceUniqeId.isEmpty() || MainActivity.deviceUniqeId.equalsIgnoreCase("none")) ? "Not generated. (this is not normal...)" : MainActivity.deviceUniqeId) + "\nYour Account Id:\n" + ((MainActivity.accountUniqeId.isEmpty() || MainActivity.accountUniqeId.equalsIgnoreCase("none")) ? "Not logged in." : MainActivity.accountUniqeId) + "\nThe Id's are used by the Preset Sever to verify your identity.");
																				notifyDataSetChanged();
																		}

																		@Override
																		public void onTouchOutside()
																		{
																				// TODO: Implement this method
																		}
																});
																dialogFragment.setDialogCloseOnTouchOutside(false);
														dialogFragment.setDialogText("Use this at your own risk!\nThis is to change your device Id, when changing this keep in mind that your uploaded presets won't be 'yours' anymore, if not logged in with an account!");
														dialogFragment.setDialogInput1("Device Id:",MainActivity.deviceUniqeId,false,null);
														dialogFragment.setDialogCheckBox("Encrypt with md5");
														dialogFragment.setDialogNegativeButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[4]);
														//dialogFragment.setDialogNeutralButton("Reset");
														dialogFragment.setDialogPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[7]);
														dialogFragment.showDialog(R.id.dialog_container);
												}
										}
								});
						Text.setOnLongClickListener(new OnLongClickListener() {

										@Override
										public boolean onLongClick(View p1)
										{
												// TODO: Implement this method
												try {
														ClipboardManager cpm = new ClipboardManager(mContext, new Handler());
														cpm.setText(MainActivity.accountUniqeId);
														if(cpm.getText().toString().equals(MainActivity.accountUniqeId)) {
																Toast.makeText(mContext,MainActivity.accountUniqeId+" copied to clipboard.",Toast.LENGTH_SHORT).show();
														} else {
																Toast.makeText(mContext,"Failed to put to clipboard...",Toast.LENGTH_SHORT).show();
														}
												}
												catch (Throwable t) {
														Log.e("NPM","Failed to put in clipboard: "+t.toString());
														Toast.makeText(mContext,"Failed to put to clipboard...",Toast.LENGTH_SHORT).show();
												}
												return true;
										}
								});
				} else {
						root.setClickable(false);
						root.setEnabled(false);
				}
				
				return InflatedView;
		}
		
}

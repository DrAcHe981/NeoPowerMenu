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
import com.nostra13.universalimageloader.utils.*;
import java.io.*;
import de.NeonSoft.neopowermenu.Preferences.*;
import android.view.animation.*;

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
				if(this.itemsTexts.get(p1).contains("Click here to reset the settings.")) {
						Text.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View p1)
										{
												// TODO: Implement this method
												slideDownDialogFragment dialogFragment = new slideDownDialogFragment(mContext, MainActivity.fragmentManager);
												dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

																@Override
																public void onListItemClick(final int position, String text)
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
																								if(position==0) {
																										MainActivity.preferences.edit().clear().commit();
																										MainActivity.colorPrefs.edit().clear().commit();
																										MainActivity.orderPrefs.edit().clear().commit();
																										MainActivity.animationPrefs.edit().clear().commit();
																										MainActivity.imageLoader.clearMemoryCache();
																										MainActivity.imageLoader.clearDiskCache();
																										File[] cacheFiles = mContext.getCacheDir().listFiles(new FileFilter() {

																														@Override
																														public boolean accept(File p1)
																														{
																																// TODO: Implement this method
																																return true;
																														}
																												});
																										for(int i=0; i<cacheFiles.length; i++) {
																												cacheFiles[i].delete();
																										}
																										File[] tempFiles = new File(mContext.getFilesDir().getPath()+"/temp/").listFiles(new FileFilter() {

																														@Override
																														public boolean accept(File p1)
																														{
																																// TODO: Implement this method
																																return true;
																														}
																												});
																										for(int i=0; i<tempFiles.length; i++) {
																												tempFiles[i].delete();
																										}
																										File[] downloadFiles = new File(mContext.getFilesDir().getPath()+"/download/").listFiles(new FileFilter() {

																														@Override
																														public boolean accept(File p1)
																														{
																																// TODO: Implement this method
																																return true;
																														}
																												});
																										for(int i=0; i<downloadFiles.length; i++) {
																												downloadFiles[i].delete();
																										}
																										File[] presetsFiles = new File(mContext.getFilesDir().getPath()+"/presets/").listFiles(new FileFilter() {

																														@Override
																														public boolean accept(File p1)
																														{
																																// TODO: Implement this method
																																return true;
																														}
																												});
																										for(int i=0; i<presetsFiles.length; i++) {
																												presetsFiles[i].delete();
																										}
																										File[] graphicFiles = new File(mContext.getFilesDir().getPath()+"/images/").listFiles(new FileFilter() {

																														@Override
																														public boolean accept(File p1)
																														{
																																// TODO: Implement this method
																																return true;
																														}
																												});
																										for(int i=0; i<graphicFiles.length; i++) {
																												graphicFiles[i].delete();
																										}
																								} else if(position==1) {
																										File[] presetsFiles = new File(mContext.getFilesDir().getPath()+"/presets/").listFiles(new FileFilter() {

																														@Override
																														public boolean accept(File p1)
																														{
																																// TODO: Implement this method
																																return true;
																														}
																												});
																										for(int i=0; i<presetsFiles.length; i++) {
																												presetsFiles[i].delete();
																										}
																								} else if(position==2) {
																										MainActivity.colorPrefs.edit().clear().commit();
																								} else if(position==3) {
																										MainActivity.orderPrefs.edit().clear().commit();
																								} else if(position==4) {
																										MainActivity.preferences.edit().putString("ProgressDrawable","Stock").commit();
																										File[] graphicFiles = new File(mContext.getFilesDir().getPath()+"/images/").listFiles(new FileFilter() {

																														@Override
																														public boolean accept(File p1)
																														{
																																// TODO: Implement this method
																																return true;
																														}
																												});
																										for(int i=0; i<graphicFiles.length; i++) {
																												graphicFiles[i].delete();
																										}
																								} else if(position==5) {
																										MainActivity.imageLoader.clearMemoryCache();
																										MainActivity.imageLoader.clearDiskCache();
																										File[] cacheFiles = mContext.getCacheDir().listFiles(new FileFilter() {

																														@Override
																														public boolean accept(File p1)
																														{
																																// TODO: Implement this method
																																return true;
																														}
																												});
																										for(int i=0; i<cacheFiles.length; i++) {
																												cacheFiles[i].delete();
																										}
																										File[] tempFiles = new File(mContext.getFilesDir().getPath()+"/temp/").listFiles(new FileFilter() {

																														@Override
																														public boolean accept(File p1)
																														{
																																// TODO: Implement this method
																																return true;
																														}
																												});
																										for(int i=0; i<tempFiles.length; i++) {
																												tempFiles[i].delete();
																										}
																										File[] downloadFiles = new File(mContext.getFilesDir().getPath()+"/download/").listFiles(new FileFilter() {

																														@Override
																														public boolean accept(File p1)
																														{
																																// TODO: Implement this method
																																return true;
																														}
																												});
																										for(int i=0; i<downloadFiles.length; i++) {
																												downloadFiles[i].delete();
																										}
																								} else if (position==6) {
																										MainActivity.animationPrefs.edit().clear().commit();
																								}
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
																														AboutFragment.LoadingLayout.setVisibility(View.VISIBLE);
																														AboutFragment.LoadingLayout.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
																														new Handler().postDelayed(new Runnable() {

																																		@Override
																																		public void run()
																																		{
																																				mContext.runOnUiThread(new Runnable() {

																																								@Override
																																								public void run()
																																								{
																																										// TODO: Implement this method
																																										mContext.recreate();
																																								}
																																						});
																																		}
																																},1000L);
																												}

																												@Override
																												public void onTouchOutside()
																												{
																														// TODO: Implement this method
																												}
																										});
																										dialogFragment.setCloseOnTouchOutside(false);
																								dialogFragment.setText("Files deleted!\n\nRestart the app to complete the reset.");
																								dialogFragment.setPositiveButton("Restart");
																								dialogFragment.showDialog(R.id.dialog_container);
																						}

																						@Override
																						public void onTouchOutside()
																						{
																								// TODO: Implement this method
																						}
																				});
																		dialogFragment.setText("Are you sure to "+text.toLowerCase()+"?\nThis can't be undone!");
																		dialogFragment.setNegativeButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
																		dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_DELETE]);
																		dialogFragment.showDialog(R.id.dialog_container);
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
												dialogFragment.setList(ListView.CHOICE_MODE_NONE, new String[] {"Delete all data","Delete saved presets","Delete color settings","Delete visibility and order settings","Delete all graphics","Delete all cached files","Delete animation settings"}, -1, true);
												dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[4]);
												dialogFragment.showDialog(R.id.dialog_container);
										}
								});
				}
				else if (this.itemsTexts.get(p1).contains("This Project uses some public librarys")) {
						Text.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View p1)
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
																		MainActivity.preferences.edit().putBoolean("useLocalServer",resultBundle.getBoolean(slideDownDialogFragment.RESULT_CHECKBOX)).commit();
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
																								AboutFragment.LoadingLayout.setVisibility(View.VISIBLE);
																								AboutFragment.LoadingLayout.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
																								new Handler().postDelayed(new Runnable() {

																												@Override
																												public void run()
																												{
																														mContext.runOnUiThread(new Runnable() {

																																		@Override
																																		public void run()
																																		{
																																				// TODO: Implement this method
																																				mContext.recreate();
																																		}
																																});
																												}
																										},1000L);
																						}

																						@Override
																						public void onTouchOutside()
																						{
																								// TODO: Implement this method
																						}
																				});
																		dialogFragment.setCloseOnTouchOutside(false);
																		dialogFragment.setText("Restart the app to let the change take effect.");
																		dialogFragment.setPositiveButton("Restart");
																		dialogFragment.showDialog(R.id.dialog_container);
																}

																@Override
																public void onTouchOutside()
																{
																		// TODO: Implement this method
																}
														});
												dialogFragment.setText("Disable or enable the use of the local server via the checkbox below.\nLocal means 127.0.0.1, online means www.Neon-Soft.de");
												dialogFragment.setCheckBox("Use local server",MainActivity.preferences.getBoolean("useLocalServer",false));
												dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_SAVE]);
												dialogFragment.showDialog(R.id.dialog_container);
										}
								});
						
				}
				else if(this.itemsTexts.get(p1).contains("Your Device Id")) {
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
																				
																		}

																		@Override
																		public void onPositiveClick(Bundle resultBundle)
																		{
																				MainActivity.deviceUniqeId = (resultBundle.getBoolean(slideDownDialogFragment.RESULT_CHECKBOX) ? helper.md5Crypto(resultBundle.getString(slideDownDialogFragment.RESULT_INPUT+"0")) : resultBundle.getString(slideDownDialogFragment.RESULT_INPUT+"0"));
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
																dialogFragment.setCloseOnTouchOutside(false);
														dialogFragment.setText("Use this at your own risk!\nThis is to change your device Id, when changing this keep in mind that your uploaded presets won't be 'yours' anymore, if not logged in with an account!");
														dialogFragment.addInput("Device Id:",MainActivity.deviceUniqeId,false,null);
														dialogFragment.setCheckBox("Encrypt with md5",true);
														dialogFragment.setNegativeButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[4]);
														//dialogFragment.setDialogNeutralButton("Reset");
														dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[7]);
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

package de.NeonSoft.neopowermenu.helpers;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.Preferences.*;
import java.io.*;
import java.net.*;
import java.util.*;
import android.text.*;

public class PresetsAdapter extends ArrayAdapter<String>
{

		public static String selectedName = "";
		public static AlertDialog uploadad;

		private final Activity context;
		private final ArrayList<String> itemsTitle;
		private final ArrayList<String> itemsDesc;
		private final ArrayList<String> itemsEnabled;
		private final ArrayList<String> itemsLocal;

		public PresetsAdapter(Activity context,
													ArrayList<String> itemsTitle,
													ArrayList<String> itemsDesc,
													ArrayList<String> itemsEnabled,
													ArrayList<String> itemsLocal)
		{
				super(context, R.layout.list_item_handle_left, itemsTitle);
				this.context = context;
				this.itemsTitle = itemsTitle;
				this.itemsDesc = itemsDesc;
				this.itemsEnabled = itemsEnabled;
				this.itemsLocal = itemsLocal;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
				// TODO: Implement this method
				final LayoutInflater inflater = context.getLayoutInflater();
				View rowView;
				//final String prefname = this.itemsSwitchPrefName.get(position);
				rowView = inflater.inflate(R.layout.presetmanager_listitem, null, true);

				LinearLayout root = (LinearLayout) rowView.findViewById(R.id.root);
				TextView ItemTitle = (TextView) rowView.findViewById(R.id.title);
				final TextView ItemDesc = (TextView) rowView.findViewById(R.id.text);
				LinearLayout LocalButton = (LinearLayout) rowView.findViewById(R.id.presetmanagerlistitemLinearLayout_Local);
				LocalButton.setVisibility(View.VISIBLE);
				RadioButton ItemSwitch = (RadioButton) rowView.findViewById(R.id.Active);
				ItemSwitch.setClickable(false);
				ItemSwitch.setFocusable(false);
				final TextView StarsCount = (TextView) rowView.findViewById(R.id.presetmanagerlistitemTextView_Stars);
				final LinearLayout OnlineButton = (LinearLayout) rowView.findViewById(R.id.presetmanagerlistitemLinearLayout_Online);
				OnlineButton.setVisibility(View.GONE);
				final ProgressBar Progress = (ProgressBar) rowView.findViewById(R.id.presetmanagerlistitemProgressBar_Download);
				Progress.setProgress(0);
				LinearLayout BottomBar = (LinearLayout) rowView.findViewById(R.id.presetmanagerlistitemLinearLayout_BottomBar);
				BottomBar.setVisibility(View.GONE);
				final LinearLayout Upload = (LinearLayout) rowView.findViewById(R.id.presetmanagerlistitemLinearLayout_Upload);
				LinearLayout Share = (LinearLayout) rowView.findViewById(R.id.presetmanagerlistitemLinearLayout_Share);
				final LinearLayout Star = (LinearLayout) rowView.findViewById(R.id.presetmanagerlistitemLinearLayout_Star);
				Star.setVisibility(View.GONE);
				final ImageView StarImage = (ImageView) rowView.findViewById(R.id.presetmanagerlistitemImageView_Star);
				final TextView StarText = (TextView) rowView.findViewById(R.id.presetmanagerlistitemTextView_StarText);
				LinearLayout Delete = (LinearLayout) rowView.findViewById(R.id.presetmanagerlistitemLinearLayout_Delete);

				ItemTitle.setText(this.itemsTitle.get(position));
				final String[] split = this.itemsDesc.get(position).split(",=,");
				String desc = context.getString(R.string.presetsManager_Creator).replace("[CREATORNAME]", itemsDesc.get(position));
				if (split.length > 1)
				{
						desc = context.getString(R.string.presetsManager_Creator).replace("[CREATORNAME]", split[0]) + "\n" + context.getString(R.string.app_name) + " " + split[1];
						StarsCount.setText(context.getString(R.string.presetsManager_Stars).replace("[STARS]",split[2]));
				}
				ItemDesc.setText(desc);
				if (itemsLocal.get(position).equalsIgnoreCase("true") || itemsLocal.get(position).equalsIgnoreCase("pre"))
				{
						if (!itemsLocal.get(position).equalsIgnoreCase("pre"))
						{
								BottomBar.setVisibility(View.VISIBLE);
								Upload.setOnClickListener(new OnClickListener() {

												@Override
												public void onClick(View p1)
												{
														// TODO: Implement this method
														slideDownDialogFragment dialogFragment = new slideDownDialogFragment(context, new slideDownDialogFragment.slideDownDialogInterface() {

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
																				// TODO: Implement this method{
																				final String appVersion;
																				final String presetCreator = resultData.get(1);
																				appVersion = MainActivity.versionName.replace("v", "");
																				try
																				{
																						FileInputStream fIn = new FileInputStream(context.getFilesDir().getPath() + "/presets/" + itemsTitle.get(position) + ".nps");
																						BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
																						String aDataRow = ""; 
																						while ((aDataRow = myReader.readLine()) != null)
																						{ 
																								String[] aData = aDataRow.split("=");
																								if (aData[0].equalsIgnoreCase("AppVersion"))
																								{
																										appVersion = aData[1];
																								}
																						}
																						myReader.close();
																						fIn.close();
																				}
																				catch (Throwable t)
																				{
																				}
																				uploadHelper uH = new uploadHelper(context, new uploadHelper.uploadHelperInterface() {

																								@Override
																								public void onUploadStarted(boolean state)
																								{
																										// TODO: Implement this method
																										Progress.setProgress(0);
																										Upload.setEnabled(false);
																										Upload.setAlpha((float) .5);
																								}

																								@Override
																								public void onPublishUploadProgress(long nowSize, long totalSize)
																								{
																										// TODO: Implement this method
																										Progress.setProgress(((int) nowSize / (int) totalSize) * 100);
																								}

																								@Override
																								public void onUploadComplete()
																								{
																										// TODO: Implement this method
																										Progress.setProgress(0);
																										Upload.setEnabled(true);
																										Upload.setAlpha((float) 1);
																										new getOnlinePresets().execute((PreferencesPresetsFragment.onlineOrderSelectedString.isEmpty() ? "" : "order=" + PreferencesPresetsFragment.onlineOrderSelectedString));
																										Toast.makeText(context, context.getString(R.string.presetsManager_UploadComplete), Toast.LENGTH_SHORT).show();
																								}

																								@Override
																								public void onUploadFailed(String reason)
																								{
																										// TODO: Implement this method
																										Progress.setProgress(0);
																										Upload.setEnabled(true);
																										Upload.setAlpha((float) 1);
																										if (reason.contains("no access")) {
																												slideDownDialogFragment dialogFragment = new slideDownDialogFragment(context, new slideDownDialogFragment.slideDownDialogInterface() {

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
																																		if(!MainActivity.loggedIn) {
																																		slideDownDialogFragment dialogFragment = new slideDownDialogFragment(context, new slideDownDialogFragment.slideDownDialogInterface(){

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
																																								PreferencesPresetsFragment.vpPager.setCurrentItem(0,true);
																																						}

																																						@Override
																																						public void onPositiveClick(ArrayList<String> resultData)
																																						{
																																								// TODO: Implement this method
																																								LoginFragment.performLogin(context,resultData.get(0),helper.md5Crypto(resultData.get(1)),resultData.get(2).equalsIgnoreCase("true") ? true : false,false);
																																						}

																																						@Override
																																						public void onTouchOutside()
																																						{
																																								// TODO: Implement this method
																																						}
																																				});
																																		dialogFragment.setDialogText("");
																																		dialogFragment.setDialogInput1(context.getString(R.string.login_UsernameEmail), presetCreator,false,null);
																																		dialogFragment.setDialogInput2(context.getString(R.string.login_Password),"",false,null);
																																		dialogFragment.setDialogInputMode(2,InputType.TYPE_TEXT_VARIATION_PASSWORD);
																																		dialogFragment.setDialogCheckBox(context.getString(R.string.login_KeepLogin));
																																		dialogFragment.setDialogNegativeButton(context.getString(R.string.Dialog_Cancel));
																																		//dialogFragment.setDialogNeutralButton(context.getString(R.string.login_TitleRegister));
																																		dialogFragment.setDialogPositiveButton(context.getString(R.string.login_Title));
																																		MainActivity.fragmentManager.beginTransaction().add(R.id.dialog_container, dialogFragment, slideDownDialogFragment.dialogTag).commit();
																																		} else {
																																				PreferencesPresetsFragment.vpPager.setCurrentItem(0,true);
																																		}
																																}

																																@Override
																																public void onTouchOutside()
																																{
																																		// TODO: Implement this method
																																}
																														});
																												dialogFragment.setDialogText(context.getString(R.string.presetsManager_UploadFailedNoAccess));
																												dialogFragment.setDialogNegativeButton(context.getString(R.string.Dialog_Cancel));
																												dialogFragment.setDialogPositiveButton(context.getString(R.string.login_Title));
																												MainActivity.fragmentManager.beginTransaction().add(R.id.dialog_container,dialogFragment,slideDownDialogFragment.dialogTag).commit();
																										}
																										else if (reason.contains("Preset name exists."))
																										{
																												slideDownDialogFragment dialogFragment = new slideDownDialogFragment(context, new slideDownDialogFragment.slideDownDialogInterface() {

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
																																}

																																@Override
																																public void onTouchOutside()
																																{
																																		// TODO: Implement this method
																																}
																														});
																												dialogFragment.setDialogText(context.getString(R.string.presetsManager_UploadFailedSameName));
																												dialogFragment.setDialogPositiveButton(context.getString(R.string.Dialog_Ok));
																												MainActivity.fragmentManager.beginTransaction().add(R.id.dialog_container,dialogFragment,slideDownDialogFragment.dialogTag).commit();
																										}
																										else if (reason.contains("Cannot connect to the DB"))
																										{
																												Toast.makeText(context, context.getString(R.string.presetsManager_CantConnecttoServer), Toast.LENGTH_LONG).show();
																										}
																										else if (reason.contains("Connection refused"))
																										{
																												Toast.makeText(context, context.getString(R.string.presetsManager_CantConnecttoServer), Toast.LENGTH_LONG).show();
																										}
																										else
																										{
																												slideDownDialogFragment dialogFragment = new slideDownDialogFragment(context, new slideDownDialogFragment.slideDownDialogInterface() {

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
																																}

																																@Override
																																public void onTouchOutside()
																																{
																																		// TODO: Implement this method
																																}
																														});
																												dialogFragment.setDialogText(context.getString(R.string.presetsManager_UploadFailed)+"\n"+reason);
																												dialogFragment.setDialogPositiveButton(context.getString(R.string.Dialog_Ok));
																												MainActivity.fragmentManager.beginTransaction().add(R.id.dialog_container,dialogFragment,slideDownDialogFragment.dialogTag).commit();
																										}
																								}
																						});
																				uH.setServerUrl("http://" + (MainActivity.LOCALTESTSERVER ? "127.0.0.1:8080" : "www.Neon-Soft.de") + "/page/NeoPowerMenu/phpWebservice/webservice2.php");
																				uH.setLocalUrl(context.getFilesDir().getPath() + "/presets/" + itemsTitle.get(position) + ".nps");
																				uH.uploadAs(resultData.get(0) + ".nps");
																				uH.setAdditionalUploadPosts(new String[][] {{"presetName",resultData.get(0)},{"presetCreator",resultData.get(1)},{"presetAppVersion","v" + appVersion},{MainActivity.usernameemail.contains("@") ? "userEmail" : "userName",MainActivity.usernameemail},{"userId",MainActivity.preferences.getString("userUniqeId", "null")}});
																				uH.startUpload();
																		}

																		@Override
																		public void onTouchOutside()
																		{
																				// TODO: Implement this method
																		}
																});
														dialogFragment.setDialogText(context.getString(R.string.presetsManager_UploadMsg));
														dialogFragment.setDialogInput1(context.getString(R.string.presetSaveDialog_InfoText),itemsTitle.get(position),false,null);
														dialogFragment.setDialogInput2(context.getString(R.string.presetSaveDialog_CreatorNameInfo),itemsDesc.get(position),false,null);
														dialogFragment.setDialogNegativeButton(context.getString(R.string.Dialog_Cancel));
														dialogFragment.setDialogPositiveButton(context.getString(R.string.Dialog_Ok));
														MainActivity.fragmentManager.beginTransaction().add(R.id.dialog_container,dialogFragment,slideDownDialogFragment.dialogTag).commit();
												}
										});
								Delete.setOnClickListener(new OnClickListener() {

												@Override
												public void onClick(View p1)
												{
														// TODO: Implement this method
														slideDownDialogFragment dialogFragment = new slideDownDialogFragment(context, new slideDownDialogFragment.slideDownDialogInterface() {

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
																						String selectedName = itemsTitle.get(position);
																						File presetFile = new File(context.getFilesDir().getPath() + "/presets/" + selectedName + ".nps");
																						if (presetFile.delete())
																						{
																								removeAt(position);
																								Toast.makeText(context.getApplicationContext(), context.getString(R.string.presetLoad_PresetDeleted).replace("[PRESETNAME]", selectedName), Toast.LENGTH_SHORT).show();
																						}

																				}
																				catch (Throwable t)
																				{
																						Toast.makeText(context.getApplicationContext(), "Failed to delete: " + t, Toast.LENGTH_LONG).show();
																				}
																				notifyDataSetChanged();
																		}

																		@Override
																		public void onTouchOutside()
																		{
																				// TODO: Implement this method
																		}
																});
																dialogFragment.setDialogText(context.getString(R.string.presetsManager_SureToDelete).replace("[PRESETNAME]", itemsTitle.get(position)));
														dialogFragment.setDialogNegativeButton(context.getString(R.string.Dialog_Cancel));
														dialogFragment.setDialogPositiveButton(context.getString(R.string.Dialog_Delete));
														MainActivity.fragmentManager.beginTransaction().add(R.id.dialog_container,dialogFragment,slideDownDialogFragment.dialogTag).commit();
												}
										});
								Share.setOnClickListener(new OnClickListener() {

												@Override
												public void onClick(View p1)
												{
														// TODO: Implement this method
														try
														{
																PreferencesPresetsFragment.progress.setVisibility(View.VISIBLE);
																Intent shareIntent = new Intent(Intent.ACTION_SEND);
																shareIntent.setType("text/rtf");
																File prefile = new File(context.getFilesDir().getPath() + "/presets/" + itemsTitle.get(position) + ".nps");
																File sharedfolder = new File(context.getExternalFilesDir(null) + "/NeoPowerMenu/sharedpresets");
																File tmpfile = new File(context.getExternalFilesDir(null) + "/NeoPowerMenu/sharedpresets/" + itemsTitle.get(position) + ".nps");
																sharedfolder.mkdirs();
																tmpfile.delete();
																FileInputStream fIn = new FileInputStream(prefile);
																FileOutputStream fOut = new FileOutputStream(tmpfile);
																BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
																BufferedWriter myWriter = new BufferedWriter(new OutputStreamWriter(fOut));
																String aDataRow = "";
																while ((aDataRow = myReader.readLine()) != null)
																{ 
																		myWriter.write(aDataRow + "\n");
																}
																myWriter.close();
																myReader.close();
																fOut.close();
																fIn.close();

																shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tmpfile));
																context.startActivity(shareIntent.createChooser(shareIntent, itemsTitle.get(position)));
																PreferencesPresetsFragment.progress.setVisibility(View.GONE);
														}
														catch (Throwable e)
														{}
												}
										});
						}
						if (this.itemsEnabled.get(position).equalsIgnoreCase("false"))
						{
								ItemSwitch.setEnabled(false);
								ItemSwitch.setChecked(false);
						}
						else
						{
								ItemSwitch.setChecked(MainActivity.preferences.getString("lastUsedPreset", getContext().getString(R.string.presetLoadDialog_BuiltInLight)).equalsIgnoreCase(itemsTitle.get(position)) ? true : false);
						}
						root.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View p1)
										{
												// TODO: Implement this method
												selectedName = itemsTitle.get(position);
												new loadPreset().execute(selectedName);
										}
								});
				}
				else if (itemsLocal.get(position).equalsIgnoreCase("false"))
				{
						LocalButton.setVisibility(View.GONE);
						OnlineButton.setVisibility(View.VISIBLE);
						try
						{
								BottomBar.setVisibility(View.VISIBLE);
								Upload.setVisibility(View.GONE);
								Share.setVisibility(View.GONE);
								if(MainActivity.preferences.getString("ratedFor","").contains(itemsTitle.get(position)+",")) {
										StarImage.setImageResource(R.drawable.ic_action_star_0);
										StarText.setText(context.getString(R.string.presetsManager_removeStar));
								}
								if (PresetsPage.onlineIds[position].equalsIgnoreCase(MainActivity.preferences.getString("userUniqeId", "null")))
								{
										Delete.setOnClickListener(new OnClickListener() {

														@Override
														public void onClick(View p1)
														{
																// TODO: Implement this method
																slideDownDialogFragment dialogFragment = new slideDownDialogFragment(context, new slideDownDialogFragment.slideDownDialogInterface() {

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
																						uploadHelper uH = new uploadHelper(context, new uploadHelper.uploadHelperInterface() {

																										@Override
																										public void onUploadStarted(boolean state)
																										{
																												// TODO: Implement this method
																												PreferencesPresetsFragment.LoadingMsg.setText(context.getString(R.string.login_Processing));
																												PreferencesPresetsFragment.progressHolder.setVisibility(View.VISIBLE);
																												PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
																										}

																										@Override
																										public void onPublishUploadProgress(long nowSize, long totalSize)
																										{
																												// TODO: Implement this method
																										}

																										@Override
																										public void onUploadComplete()
																										{
																												// TODO: Implement this method
																												PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
																												PreferencesPresetsFragment.progressHolder.setVisibility(View.GONE);
																												Toast.makeText(context, context.getString(R.string.presetLoad_PresetDeleted).replace("[PRESETNAME]", itemsTitle.get(position)), Toast.LENGTH_SHORT).show();
																												new getOnlinePresets().execute((PreferencesPresetsFragment.onlineOrderSelectedString.isEmpty() ? "" : "order=" + PreferencesPresetsFragment.onlineOrderSelectedString));
																										}

																										@Override
																										public void onUploadFailed(String reason)
																										{
																												// TODO: Implement this method
																												PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
																												PreferencesPresetsFragment.progressHolder.setVisibility(View.GONE);
																												Toast.makeText(context, "Failed to delete.\n" + reason, Toast.LENGTH_LONG).show();
																										}
																								});
																						uH.setServerUrl("http://" + (MainActivity.LOCALTESTSERVER ? "127.0.0.1:8080" : "www.Neon-Soft.de") + "/page/NeoPowerMenu/phpWebservice/webservice1.php");
																						uH.setAdditionalUploadPosts(new String[][] {{"action","delete"},{"presetName",itemsTitle.get(position)},{"userId",MainActivity.preferences.getString("userUniqeId", "null")}});
																						try
																						{
																								new File(context.getFilesDir().getPath() + "/tmp").createNewFile();
																						}
																						catch (IOException e)
																						{}
																						uH.setLocalUrl(context.getFilesDir().getPath() + "/tmp");
																						uH.startUpload();
																				}

																				@Override
																				public void onTouchOutside()
																				{
																						// TODO: Implement this method
																				}
																		});
																dialogFragment.setDialogText(context.getString(R.string.presetsManager_SureToDelete).replace("[PRESETNAME]", itemsTitle.get(position)));
																dialogFragment.setDialogNegativeButton(context.getString(R.string.Dialog_Cancel));
																dialogFragment.setDialogPositiveButton(context.getString(R.string.Dialog_Delete));
																MainActivity.fragmentManager.beginTransaction().add(R.id.dialog_container,dialogFragment,slideDownDialogFragment.dialogTag).commit();
														}
												});
								} else {
										Star.setVisibility(View.VISIBLE);
										Delete.setVisibility(View.GONE);
										Star.setOnClickListener(new OnClickListener() {

														@Override
														public void onClick(View p1)
														{
																// TODO: Implement this method
																uploadHelper uH = new uploadHelper(context, new uploadHelper.uploadHelperInterface() {

																				@Override
																				public void onUploadStarted(boolean state)
																				{
																						// TODO: Implement this method
																						PreferencesPresetsFragment.LoadingMsg.setText(context.getString(R.string.login_Processing));
																						PreferencesPresetsFragment.progressHolder.setVisibility(View.VISIBLE);
																						PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
																				}

																				@Override
																				public void onPublishUploadProgress(long nowSize, long totalSize)
																				{
																						// TODO: Implement this method
																				}

																				@Override
																				public void onUploadComplete()
																				{
																						// TODO: Implement this method
																						if (StarText.getText().toString().equalsIgnoreCase(context.getString(R.string.presetsManager_giveStar))) {
																								StarsCount.setText(context.getString(R.string.presetsManager_Stars).replace("[STARS]",""+(Integer.parseInt(StarsCount.getText().toString().split(": ")[1])+1)));
																								StarImage.setImageResource(R.drawable.ic_action_star_0);
																								StarText.setText(context.getString(R.string.presetsManager_removeStar));
																								MainActivity.preferences.edit().putString("ratedFor",MainActivity.preferences.getString("ratedFor","")+itemsTitle.get(position)+",").commit();
																						} else {
																								StarsCount.setText(context.getString(R.string.presetsManager_Stars).replace("[STARS]",""+(Integer.parseInt(StarsCount.getText().toString().split(": ")[1])-1)));
																								StarImage.setImageResource(R.drawable.ic_action_star_10);
																								StarText.setText(context.getString(R.string.presetsManager_giveStar));
																								MainActivity.preferences.edit().putString("ratedFor",MainActivity.preferences.getString("ratedFor","").replace(itemsTitle.get(position)+",","")).commit();
																						}
																						LoginFragment.getStatistics();
																						PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
																						PreferencesPresetsFragment.progressHolder.setVisibility(View.GONE);
																				}

																				@Override
																				public void onUploadFailed(String reason)
																				{
																						// TODO: Implement this method
																						PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
																						PreferencesPresetsFragment.progressHolder.setVisibility(View.GONE);
																						if (reason.contains("Cannot connect to the DB"))
																						{
																								Toast.makeText(context, context.getString(R.string.presetsManager_CantConnecttoServer), Toast.LENGTH_LONG).show();
																						}
																						else if (reason.contains("Connection refused"))
																						{
																								Toast.makeText(context, context.getString(R.string.presetsManager_CantConnecttoServer), Toast.LENGTH_LONG).show();
																						}
																						else
																						{
																								Toast.makeText(context, context.getString(R.string.login_LoginFailedWithReason)+"\n" + reason, Toast.LENGTH_LONG).show();
																						}
																				}
																		});
																uH.setServerUrl("http://" + (MainActivity.LOCALTESTSERVER ? "127.0.0.1:8080" : "www.Neon-Soft.de") + "/page/NeoPowerMenu/phpWebservice/webservice3.php");
																uH.setAdditionalUploadPosts(new String[][] {{"action",(StarText.getText().toString().equalsIgnoreCase(context.getString(R.string.presetsManager_giveStar)) ? "givestar" : "removestar")},{(MainActivity.usernameemail.contains("@") ? "userEmail" : "userName"),MainActivity.usernameemail},{"name",itemsTitle.get(position)}});
																try
																{
																		new File(context.getFilesDir().getPath() + "/tmp").createNewFile();
																}
																catch (IOException e)
																{}
																uH.setLocalUrl(context.getFilesDir().getPath() + "/tmp");
																uH.startUpload();
														}
												});
								}
						}
						catch (Throwable t)
						{}
						if(PreferencesPresetsFragment.DownloadingActiveFor.contains(itemsTitle.get(position) + ",")) {
								//oldText = ItemDesc.getText().toString();
								ItemDesc.setText(context.getString(R.string.presetsManager_Downloading));
								Progress.setProgress(0);
								OnlineButton.setEnabled(false);
								OnlineButton.setAlpha((float) .3);
						}
						root.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View p1)
										{
												// TODO: Implement this method
												if (OnlineButton.isEnabled())
												{
														downloadHelper dH = new downloadHelper(context, new downloadHelper.downloadHelperInterface() {

																		String oldText;

																		@Override
																		public void onDownloadStarted(boolean state)
																		{
																				// TODO: Implement this method
																				oldText = ItemDesc.getText().toString();
																				ItemDesc.setText(context.getString(R.string.presetsManager_Downloading));
																				Progress.setProgress(0);
																				OnlineButton.setEnabled(false);
																				OnlineButton.setAlpha((float) .3);
																				PreferencesPresetsFragment.DownloadingActiveFor = PreferencesPresetsFragment.DownloadingActiveFor + itemsTitle.get(position) + ",";
																		}

																		@Override
																		public void onPublishDownloadProgress(long nowSize, long totalSize)
																		{
																				// TODO: Implement this method
																				ItemDesc.setText(context.getString(R.string.presetsManager_Downloading) + " (" + helper.getSizeString(nowSize, true) + "/" + helper.getSizeString(totalSize, true) + ")");
																				Progress.setProgress((int) (nowSize / totalSize) * 100);
																		}

																		@Override
																		public void onDownloadComplete()
																		{
																				// TODO: Implement this method
																				Progress.setProgress(0);
																				ItemDesc.setText(oldText);
																				OnlineButton.setEnabled(true);
																				OnlineButton.setAlpha((float) 1);
																				PreferencesPresetsFragment.DownloadingActiveFor = PreferencesPresetsFragment.DownloadingActiveFor.replace(itemsTitle.get(position) + ",","");
																				try
																				{
																						PreferencesPresetsFragment.ImportPreset("file://" + context.getFilesDir().getPath() + "/download/" + split[0] + "_" + itemsTitle.get(position).replace("'","\\\'").replace("\"","\\\"") + ".nps", PreferencesPresetsFragment.localAdapter, itemsTitle.get(position), null);
																				}
																				catch (Exception e)
																				{
																						new File(context.getFilesDir().getPath() + "/download/" + itemsTitle.get(position).replace("'","\\'").replace("\"","\\\"") + ".nps").delete();
																						Toast.makeText(context, context.getString(R.string.presetsManager_ImportFailed) + "\n" + e.toString(), Toast.LENGTH_LONG).show();
																						Log.e("NPM", e.toString());
																				}
																		}

																		@Override
																		public void onDownloadFailed(String reason)
																		{
																				// TODO: Implement this method
																				Progress.setProgress(0);
																				ItemDesc.setText(oldText);
																				OnlineButton.setEnabled(true);
																				OnlineButton.setAlpha((float) 1);
																				Toast.makeText(context, context.getString(R.string.presetsManager_DownloadFailed) + "\n" + reason, Toast.LENGTH_LONG).show();
																				PreferencesPresetsFragment.DownloadingActiveFor = PreferencesPresetsFragment.DownloadingActiveFor.replace(itemsTitle.get(position) + ",","");
																		}
																});
														dH.setUrl("http://" + (MainActivity.LOCALTESTSERVER ? "127.0.0.1:8080" : "www.Neon-Soft.de") + "/page/NeoPowerMenu/Presets/" + split[0] + "_" + itemsTitle.get(position).replace("'","\\'").replace("\"","\\\"") + ".nps");
														dH.setLocalUrl(context.getFilesDir().getPath() + "/download");
														dH.startDownload();
												}
										}
								});
				}


				return rowView;
		}

		public String[] getItemAt(int position)
		{
				String[] string = new String[4];

				string[0] = this.itemsTitle.get(position);
				string[1] = this.itemsDesc.get(position);
				string[2] = this.itemsEnabled.get(position);
				string[3] = this.itemsLocal.get(position);

				return string;
		}

		public boolean findItem(String searchTerm)
		{
				for (int i=0;i < itemsTitle.size() - 1;i++)
				{
						if (itemsTitle.get(i).equals(searchTerm))
						{
								return true;
						}
				}
				return false;
		}

		public void insert(String[] string)
		{
				itemsTitle.add(string[0]);
				itemsDesc.add(string[1]);
				itemsEnabled.add(string[2]);
				itemsLocal.add(string[3]);
				notifyDataSetChanged();
		}

		public void insertAt(String[] string, int to)
		{
				itemsTitle.add(to, string[0]);
				itemsDesc.add(to, string[1]);
				itemsEnabled.add(to, string[2]);
				itemsLocal.add(to, string[3]);
				notifyDataSetChanged();
		}

		public void removeAt(int position)
		{
				itemsTitle.remove(position);
				itemsDesc.remove(position);
				itemsEnabled.remove(position);
				itemsLocal.remove(position);
				notifyDataSetChanged();
		}

		public void removeAll()
		{
				itemsTitle.clear();
				itemsDesc.clear();
				itemsEnabled.clear();
				itemsLocal.clear();
				notifyDataSetChanged();
		}

		class loadPreset extends AsyncTask<String, String, String>
		{

				boolean oldPreset = false;
				
				@Override
				protected void onPreExecute()
				{
						// TODO: Implement this method
						PreferencesPresetsFragment.progressHolder.setVisibility(View.VISIBLE);
						PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
						super.onPreExecute();
				}

				@Override
				protected String doInBackground(String[] p1)
				{
						// TODO: Implement this method
						try
						{
								if (!p1[0].equalsIgnoreCase(context.getString(R.string.presetLoadDialog_BuiltInLight)) && !p1[0].equalsIgnoreCase(context.getString(R.string.presetLoadDialog_BuiltInDark)) && !p1[0].equalsIgnoreCase(context.getString(R.string.presetLoadDialog_BuiltInBlack)))
								{
										File presetFile = new File(context.getFilesDir().getPath() + "/presets/" + p1[0] + ".nps");
										FileInputStream fIn = new FileInputStream(presetFile);
										BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
										String aDataRow = ""; 
										String aBuffer = ""; 
										for (int i = 0;i < PreferencesColorFragment.ColorNames.length;i++)
										{
												String[] loadColor = PreferencesColorFragment.ColorNames[i][1].split("_");
												if (loadColor.length > 1)
												{
														if (loadColor[1].contains("Background"))
														{
																MainActivity.preferences.edit().putString(loadColor[0] + "_Backgroundcolor", PreferencesColorFragment.lightPreset[i]).commit();
														}
														else if (loadColor[1].contains("Text"))
														{
																MainActivity.preferences.edit().putString(loadColor[0] + "_Textcolor", PreferencesColorFragment.lightPreset[i]).commit();
														}
														publishProgress("Reset to default...");
												}
										}

										while ((aDataRow = myReader.readLine()) != null)
										{ 
												aBuffer += aDataRow + "\n";
												String aData[] = aDataRow.split("=");
												String[] loadColor = aData[0].split("_");
												if (aData[0].equalsIgnoreCase("AppVersion")) {
														if(aData[1].equalsIgnoreCase("1.4.2")) {
																//Toast.makeText(MainActivity.context,"Converting old Save names...",Toast.LENGTH_SHORT).show();
																oldPreset = true;
														}
												}
												else if (!aData[0].equalsIgnoreCase("AppVersion") && !aData[0].equalsIgnoreCase("Creator"))
												{
														if(oldPreset && aData[0].equalsIgnoreCase("RevealBackground")) {
																aData[0] = "Reveal_Background";
														} else if (oldPreset && aData[0].equalsIgnoreCase("ActionRevealBackground")) {
																aData[0] = "ActionReveal_Background";
														}
														MainActivity.preferences.edit().putString(aData[0], aData[1]).commit();
														publishProgress(loadColor[0] + ": " + aData[1]);
												}
										}
								}
								else
								{
										String[] preset = PreferencesColorFragment.lightPreset;
										if (p1[0].equalsIgnoreCase(context.getString(R.string.presetLoadDialog_BuiltInLight)))
										{
												preset = PreferencesColorFragment.lightPreset;
										}
										else if (p1[0].equalsIgnoreCase(context.getString(R.string.presetLoadDialog_BuiltInDark)))
										{
												preset = PreferencesColorFragment.darkPreset;
										}
										else if (p1[0].equalsIgnoreCase(context.getString(R.string.presetLoadDialog_BuiltInBlack)))
										{
												preset = PreferencesColorFragment.blackPreset;
										}
										for (int i = 0;i < PreferencesColorFragment.ColorNames.length;i++)
										{
												String[] loadColor = PreferencesColorFragment.ColorNames[i][1].split("_");
												if (loadColor.length > 1)
												{
														if (loadColor[1].contains("Background"))
														{
																MainActivity.preferences.edit().putString(loadColor[0] + "_Backgroundcolor", preset[i]).commit();
														}
														else if (loadColor[1].contains("Text"))
														{
																MainActivity.preferences.edit().putString(loadColor[0] + "_Textcolor", preset[i]).commit();
														}
														publishProgress(loadColor[0] + ": " + preset[i]);
												}
										}
								}
								return "success";
						}
						catch (Throwable e)
						{
								return e.toString();
						}
				}

				@Override
				protected void onProgressUpdate(String[] p1)
				{
						// TODO: Implement this method
						super.onProgressUpdate(p1);
						PreferencesPresetsFragment.LoadingMsg.setText("Loading...\n" + p1[0]);
				}

				@Override
				protected void onPostExecute(String p1)
				{
						// TODO: Implement this method
						super.onPostExecute(p1);
						if (p1.equalsIgnoreCase("success"))
						{
								MainActivity.preferences.edit().putString("lastUsedPreset", selectedName).commit();
								Toast.makeText(context.getApplicationContext(), context.getString(R.string.presetLoad_PresetLoaded).replace("[PRESETNAME]", selectedName), Toast.LENGTH_SHORT).show();
						}
						else
						{
								Toast.makeText(context.getApplicationContext(), context.getString(R.string.presetsManager_ImportFailed) + "\n" + p1, Toast.LENGTH_SHORT).show();
						}
						PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
						PreferencesPresetsFragment.progressHolder.setVisibility(View.GONE);
						notifyDataSetChanged();
				}

		}

}

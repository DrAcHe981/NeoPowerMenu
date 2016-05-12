package de.NeonSoft.neopowermenu.helpers;

import android.app.*;
import android.view.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;
import java.io.*;
import java.util.*;
import de.NeonSoft.neopowermenu.Preferences.*;
import android.view.View.*;
import android.content.*;
import android.net.*;
import java.nio.*;
import android.os.*;
import android.view.animation.*;

public class PresetsAdapter extends ArrayAdapter<String>
{
		
		public static String selectedName = "";
		
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
				LayoutInflater inflater = context.getLayoutInflater();
				View rowView;
				//final String prefname = this.itemsSwitchPrefName.get(position);
				rowView = inflater.inflate(R.layout.presetmanager_listitem, null, true);

				TextView ItemTitle = (TextView) rowView.findViewById(R.id.title);
				TextView ItemDesc = (TextView) rowView.findViewById(R.id.text);
				LinearLayout LocalButton = (LinearLayout) rowView.findViewById(R.id.presetmanagerlistitemLinearLayout_Local);
				LocalButton.setVisibility(View.VISIBLE);
				RadioButton ItemSwitch = (RadioButton) rowView.findViewById(R.id.Active);
				ItemSwitch.setClickable(false);
				ItemSwitch.setFocusable(false);
				LinearLayout OnlineButton = (LinearLayout) rowView.findViewById(R.id.presetmanagerlistitemLinearLayout_Online);
				OnlineButton.setVisibility(View.GONE);
				ProgressBar Progress = (ProgressBar) rowView.findViewById(R.id.presetmanagerlistitemProgressBar_Download);
				Progress.setProgress(0);
				LinearLayout BottomBar = (LinearLayout) rowView.findViewById(R.id.presetmanagerlistitemLinearLayout_BottomBar);
				BottomBar.setVisibility(View.GONE);
				LinearLayout Share = (LinearLayout) rowView.findViewById(R.id.presetmanagerlistitemLinearLayout_Share);
				LinearLayout Delete = (LinearLayout) rowView.findViewById(R.id.presetmanagerlistitemLinearLayout_Delete);
				
				ItemTitle.setText(this.itemsTitle.get(position));
				ItemDesc.setText(this.itemsDesc.get(position));
				if (itemsLocal.get(position).equalsIgnoreCase("true") || itemsLocal.get(position).equalsIgnoreCase("pre")) {
						if(!itemsLocal.get(position).equalsIgnoreCase("pre")) {
								BottomBar.setVisibility(View.VISIBLE);
								Delete.setOnClickListener(new OnClickListener() {

												@Override
												public void onClick(View p1)
												{
														// TODO: Implement this method
														AlertDialog.Builder adb = new AlertDialog.Builder(context);
														adb.setTitle(itemsTitle.get(position));
														adb.setMessage(context.getString(R.string.presetsManager_SureToDelete).replace("[PRESETNAME]",itemsTitle.get(position)));
														adb.setPositiveButton(R.string.Dialog_Delete, new DialogInterface.OnClickListener() {

																		@Override
																		public void onClick(DialogInterface p1, int p2)
																		{
																				// TODO: Implement this method
																				try
																				{
																						String selectedName = itemsTitle.get(position);
																						File presetFile = new File(context.getFilesDir().getPath()+"/presets/"+selectedName+".nps");
																						if (presetFile.delete()) {
																								removeAt(position);
																								Toast.makeText(context.getApplicationContext(),context.getString(R.string.presetLoad_PresetDeleted).replace("[PRESETNAME]",selectedName),Toast.LENGTH_SHORT).show();
																						}

																				} catch (Throwable t) {
																						Toast.makeText(context.getApplicationContext(),"Failed to delete: "+t,Toast.LENGTH_LONG).show();
																				}
																				notifyDataSetChanged();
																		}
																});
														adb.setNegativeButton(R.string.Dialog_Cancel, new DialogInterface.OnClickListener() {

																		@Override
																		public void onClick(DialogInterface p1, int p2)
																		{
																				// TODO: Implement this method
																		}
																});
																
																adb.show();
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
																File prefile = new File(context.getFilesDir().getPath()+"/presets/"+itemsTitle.get(position)+".nps");
																File sharedfolder = new File(context.getExternalFilesDir(null)+"/NeoPowerMenu/sharedpresets");
														File tmpfile = new File(context.getExternalFilesDir(null)+"/NeoPowerMenu/sharedpresets/"+itemsTitle.get(position)+".nps");
																sharedfolder.mkdirs();
																tmpfile.delete();
																FileInputStream fIn = new FileInputStream(prefile);
																FileOutputStream fOut = new FileOutputStream(tmpfile);
																BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
																BufferedWriter myWriter = new BufferedWriter(new OutputStreamWriter(fOut));
																String aDataRow = ""; 
																//String aBuffer = "";
																while ((aDataRow = myReader.readLine()) != null)
																{ 
																		//aBuffer += aDataRow + "\n";
																		myWriter.write(aDataRow + "\n");
																}
																myWriter.close();
																myReader.close();
																fOut.close();
																fIn.close();
																
														shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tmpfile));
																context.startActivity(shareIntent.createChooser(shareIntent,itemsTitle.get(position)));
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
				rowView.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										selectedName = itemsTitle.get(position);
										new loadPreset().execute(selectedName);
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

		public void insert(String[] string) {
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

		
		class loadPreset extends AsyncTask<String, String, String>
		{

				@Override
				protected void onPreExecute()
				{
						// TODO: Implement this method
						PreferencesPresetsFragment.progressHolder.setVisibility(View.VISIBLE);
						PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_in));
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
										File presetFile = new File(context.getFilesDir().getPath()+"/presets/"+p1[0]+".nps");
										FileInputStream fIn = new FileInputStream(presetFile);
										BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
										String aDataRow = ""; 
										String aBuffer = ""; 
										for (int i = 0;i < PreferencesColorFragment.ColorNames.length;i++)
										{
												String[] loadColor = PreferencesColorFragment.ColorNames[i][1].split("_");
												if(loadColor.length>1) {
														if (loadColor[1].contains("Background"))
														{
																MainActivity.preferences.edit().putString(loadColor[0]+"_Backgroundcolor", PreferencesColorFragment.lightPreset[i]).commit();
														} else if (loadColor[1].contains("Text")) {
																MainActivity.preferences.edit().putString(loadColor[0]+"_Textcolor", PreferencesColorFragment.lightPreset[i]).commit();
														}
														publishProgress("Reset to default...");
												}
										}

										while ((aDataRow = myReader.readLine()) != null)
										{ 
												aBuffer += aDataRow + "\n";
												String aData[] = aDataRow.split("=");
												String[] loadColor = aData[0].split("_");
												if (!aData[0].equalsIgnoreCase("AppVersion") && !aData[0].equalsIgnoreCase("Creator"))
												{
														MainActivity.preferences.edit().putString(aData[0], aData[1]).commit();
														publishProgress(loadColor[0] +": "+aData[1]);
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
												if(loadColor.length>1) {
														if (loadColor[1].contains("Background"))
														{
																MainActivity.preferences.edit().putString(loadColor[0]+"_Backgroundcolor", preset[i]).commit();
														} else if (loadColor[1].contains("Text")) {
																MainActivity.preferences.edit().putString(loadColor[0]+"_Textcolor", preset[i]).commit();
														}
														publishProgress(loadColor[0] +": "+preset[i]);
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
						PreferencesPresetsFragment.LoadingMsg.setText("Loading...\n"+p1[0]);
				}
				
				@Override
				protected void onPostExecute(String p1)
				{
						// TODO: Implement this method
						super.onPostExecute(p1);
						if(p1.equalsIgnoreCase("success")) {
								MainActivity.preferences.edit().putString("lastUsedPreset", selectedName).commit();
								Toast.makeText(context.getApplicationContext(),context.getString(R.string.presetLoad_PresetLoaded).replace("[PRESETNAME]",selectedName),Toast.LENGTH_SHORT).show();
						} else {
								Toast.makeText(context.getApplicationContext(),context.getString(R.string.presetsManager_ImportFailed)+"\n"+p1,Toast.LENGTH_SHORT).show();
						}
						PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_out));
						PreferencesPresetsFragment.progressHolder.setVisibility(View.GONE);
						notifyDataSetChanged();
				}
				
		}
		
}

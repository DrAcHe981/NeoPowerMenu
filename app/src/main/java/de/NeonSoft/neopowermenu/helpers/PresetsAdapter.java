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

public class PresetsAdapter extends ArrayAdapter<String>
{

		String[] lightPreset = {"#8800bcd4","#880097a7",
				"#fff5f5f5","#ffd32f2f","#ff3f51b5","#ffe91e63","#ff3f51b5","#ff8bc34a","#ff277b71","#ff009688",
				"#000000","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff"};
		String[] darkPreset = {"#88121212","#8821272b",
				"#ff212121","#ffd32f2f","#ff3f51b5","#ffe91e63","#ff3f51b5","#ff8bc34a","#ff277b71","#ff009688",
				"#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff"};
		String[] blackPreset = {"#88000000","#88000000",
				"#ff000000","#ff000000","#ff000000","#ff000000","#ff000000","#ff000000","#ff000000","#ff000000",
				"#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff"};
		String[] ColorNames = {"RevealBackground","ActionRevealBackground",
				"Dialog_Backgroundcolor","DialogShutdown_Backgroundcolor","DialogReboot_Backgroundcolor","DialogSoftReboot_Backgroundcolor","DialogScreenshot_Backgroundcolor","DialogRecovery_Backgroundcolor","DialogBootloader_Backgroundcolor","DialogSafeMode_Backgroundcolor",
				"Dialog_Textcolor","DialogShutdown_Textcolor","DialogReboot_Textcolor","DialogSoftReboot_Textcolor","DialogRecovery_Textcolor","DialogBootloader_Textcolor","DialogSafeMode_Textcolor"};

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
																PresetsPage.progress.setVisibility(View.VISIBLE);
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
																PresetsPage.progress.setVisibility(View.GONE);
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
										try
										{
										String selectedName = itemsTitle.get(position);
										if (position >= 3)
										{
														File presetFile = new File(context.getFilesDir().getPath()+"/presets/"+itemsTitle.get(position)+".nps");
														FileInputStream fIn = new FileInputStream(presetFile);
														BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
														String aDataRow = ""; 
														String aBuffer = ""; 
														for (int i = 0;i < lightPreset.length;i++)
														{
																MainActivity.preferences.edit().putString(ColorNames[i], lightPreset[i]).commit();
														}
														while ((aDataRow = myReader.readLine()) != null)
														{ 
																aBuffer += aDataRow + "\n";
																String aData[] = aDataRow.split("=");
																if (!aData[0].equalsIgnoreCase("AppVersion"))
																{
																		MainActivity.preferences.edit().putString(aData[0], aData[1]).commit();
																}
														}
										}
										else
										{
												String[] preset = lightPreset;
												if (position == 0)
												{
														preset = lightPreset;
												}
												else if (position == 1)
												{
														preset = darkPreset;
												}
												else if (position == 2)
												{
														preset = blackPreset;
												}
												for (int i = 0;i < preset.length;i++)
												{
														MainActivity.preferences.edit().putString(ColorNames[i], preset[i]).commit();
												}
										}
										MainActivity.preferences.edit().putString("lastUsedPreset", itemsTitle.get(position)).commit();
												Toast.makeText(context.getApplicationContext(),context.getString(R.string.presetLoad_PresetLoaded).replace("[PRESETNAME]",selectedName),Toast.LENGTH_SHORT).show();
										}
										catch (Throwable e)
										{
												Toast.makeText(context.getApplicationContext(),"Failed to load: "+e,Toast.LENGTH_SHORT).show();
										}
										notifyDataSetChanged();
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

}

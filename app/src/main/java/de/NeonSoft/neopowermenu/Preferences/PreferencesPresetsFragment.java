package de.NeonSoft.neopowermenu.Preferences;
import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.view.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.widget.AdapterView.*;
import android.widget.TextView.*;
import com.ogaclejapan.smarttablayout.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.*;
import java.io.*;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View.OnClickListener;
import de.NeonSoft.neopowermenu.R;
import java.util.*;

public class PreferencesPresetsFragment extends Fragment
{

		public static AlertDialog importad;
		public static String Filename;
		public static String sCreator = "< unknown >";
		public static String newUrl;
		public static boolean Importcancled = false;

		public static RelativeLayout progressHolder;
		public static ProgressBar progress;
		public static TextView LoadingMsg;

		public static Activity mContext;

		View InflatedView;
		public static ViewPager vpPager;
		public static MyPagerAdapter adapterViewPager;

		public static PresetsAdapter localAdapter;
		public static PresetsAdapter onlineAdapter;
		public static ListView onlineList;
		public static TextView onlineMSG;
		
		public static LinearLayout onlineSearch;
		
		public static LinearLayout onlineSearchBar;
		public static EditText onlineSearchEdit;
		public static ImageView onlineStartSearch;
		
		public static LinearLayout onlineOrder;
		
		public static String onlineSearchTerm = "";
		
		private static int onlineOrderSelected = 0;
		public static String onlineOrderSelectedString = "";

		public static boolean onlineRequestIsRunning;

		public static String DownloadingActiveFor = "";
		public static downloadHelper[] DownloadingActiveForHelper;
		public static LinearLayout[] DownloadingActiveForLayout;
		public static String[] DownloadingActiveForOldText;
		public static TextView[] DownloadingActiveForLabel;
		public static ProgressBar[] DownloadingActiveForProgress;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
				// TODO: Implement this method
				MainActivity.visibleFragment = "PresetsManager";

				mContext = getActivity();
				onlineSearchTerm = "";
				onlineOrderSelected = 0;
				onlineOrderSelectedString = "";

				InflatedView = inflater.inflate(R.layout.activity_presetsmanager, container, false);


				onlineSearch = (LinearLayout) InflatedView.findViewById(R.id.activitypresetsmanagerLinearLayout_SearchIcon);
				onlineSearch.setVisibility(View.GONE);

				onlineSearchBar = (LinearLayout) InflatedView.findViewById(R.id.activitypresetsmanagerLinearLayout_SearchBar);
				onlineSearchEdit = (EditText) InflatedView.findViewById(R.id.activitypresetsmanagerEditText_Search);
				onlineStartSearch = (ImageView) InflatedView.findViewById(R.id.activitypresetsmanagerImageView_StartSearch);
				onlineSearchBar.setVisibility(View.GONE);

				onlineOrder = (LinearLayout) InflatedView.findViewById(R.id.activitypresetsmanagerLinearLayout_Order);
				onlineOrder.setVisibility(View.GONE);
				
        vpPager = (ViewPager) InflatedView.findViewById(R.id.pager);
        adapterViewPager = new MyPagerAdapter(MainActivity.fragmentManager, new String[] {getString(R.string.presetsManager_TitleAccount),getString(R.string.presetsManager_TitleLocal),getString(R.string.presetsManager_TitleOnline)});
        vpPager.setAdapter(adapterViewPager);

				vpPager.setCurrentItem(1);

				vpPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

								@Override
								public void onPageScrolled(int p1, float p2, int p3)
								{
										// TODO: Implement this method
								}

								@Override
								public void onPageSelected(int p1)
								{
										// TODO: Implement this method
										if (adapterViewPager.getPageTitle(p1).toString().equalsIgnoreCase(getString(R.string.presetsManager_TitleOnline)))
										{
												MainActivity.visibleFragment = "PresetsManagerOnline";
												MainActivity.actionbar.setActionBarButton(getString(R.string.presetsManager_Refresh), R.drawable.ic_action_autorenew, new OnClickListener() {

																@Override
																public void onClick(View p1)
																{
																		// TODO: Implement this method
																		new getOnlinePresets().execute((onlineOrderSelectedString.isEmpty() ? "" : "order=" + PreferencesPresetsFragment.onlineOrderSelectedString));
																}
														});
												onlineSearch.setOnClickListener(new OnClickListener() {

																@Override
																public void onClick(View p1)
																{
																		// TODO: Implement this method
																		if(onlineSearch.getVisibility() == View.VISIBLE) {
																				onlineSearch.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_out_top));
																				onlineSearch.setVisibility(View.GONE);
																		}
																		if(onlineSearchBar.getVisibility() == View.GONE) {
																				onlineStartSearch.setOnClickListener(new OnClickListener() {

																								@Override
																								public void onClick(View p1)
																								{
																										// TODO: Implement this method
																										if(!onlineSearchEdit.getText().toString().isEmpty()) {
																												InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
																												IBinder windowToken = onlineSearchEdit.getWindowToken();
																												inputManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);
																												onlineSearchTerm = onlineSearchEdit.getText().toString();
																												new getOnlinePresets().execute((onlineOrderSelectedString.isEmpty() ? "" : "order="+ onlineOrderSelectedString),(onlineSearchTerm.isEmpty() ? "" : "search="+onlineSearchTerm));
																										}
																								}
																						});
																				onlineSearchEdit.setOnEditorActionListener(new OnEditorActionListener() {

																								@Override
																								public boolean onEditorAction(TextView p1, int p2, KeyEvent p3)
																								{
																										// TODO: Implement this method
																										onlineStartSearch.callOnClick();
																										return false;
																								}
																						});
																				onlineSearchEdit.setText("");
																				onlineSearchBar.setVisibility(View.VISIBLE);
																				onlineSearchBar.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_in_top));
																		}
																		if (onlineOrder.getVisibility() == View.VISIBLE)
																		{
																				onlineOrder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_out_top));
																				onlineOrder.setVisibility(View.GONE);
																		}
																}
														});
												onlineOrder.setOnClickListener(new OnClickListener() {

																@Override
																public void onClick(View p1)
																{
																		// TODO: Implement this method
																		if(onlineSearch.getVisibility() == View.VISIBLE) {
																				onlineSearch.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_out_top));
																				onlineSearch.setVisibility(View.GONE);
																		}
																		if (onlineOrder.getVisibility() == View.VISIBLE)
																		{
																				onlineOrder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_out_top));
																				onlineOrder.setVisibility(View.GONE);
																		}
																		slideDownDialogFragment dialogFragment = new slideDownDialogFragment(getActivity(), MainActivity.fragmentManager);
																		dialogFragment.setDialogListener(new slideDownDialogFragment.slideDownDialogInterface() {

																						@Override
																						public void onListItemClick(int position, String text)
																						{
																								// TODO: Implement this method
																								onlineOrderSelected = position;
																								onlineOrderSelectedString = text;
																								MainActivity.actionbar.setActionBarButtonListener(new OnClickListener() {

																												@Override
																												public void onClick(View p1)
																												{
																														// TODO: Implement this method
																														new getOnlinePresets().execute("order=" + onlineOrderSelectedString);
																												}
																										});
																								hideBars();
																								new getOnlinePresets().execute("order=" + onlineOrderSelectedString);
																						}

																						@Override
																						public void onNegativeClick()
																						{
																								// TODO: Implement this method
																								hideBars();
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
																								hideBars();
																						}

																						@Override
																						public void onTouchOutside()
																						{
																								// TODO: Implement this method
																								hideBars();
																						}
																				});
																				dialogFragment.setDialogText(mContext.getString(R.string.presetsManager_OrderBy));
																		dialogFragment.setDialogList(ListView.CHOICE_MODE_SINGLE, new String[] {getString(R.string.presetsManager_OrderNames).split("/")[0] + " (" + getString(R.string.presetsManager_OrderAscDesc).split("/")[0] + ")",
																																		 getString(R.string.presetsManager_OrderNames).split("/")[0] + " (" + getString(R.string.presetsManager_OrderAscDesc).split("/")[1] + ")",
																																		 getString(R.string.presetsManager_OrderNames).split("/")[1] + " (" + getString(R.string.presetsManager_OrderAscDesc).split("/")[0] + ")",
																																		 getString(R.string.presetsManager_OrderNames).split("/")[1] + " (" + getString(R.string.presetsManager_OrderAscDesc).split("/")[1] + ")",
																																		 getString(R.string.presetsManager_OrderNames).split("/")[2] + " (" + getString(R.string.presetsManager_OrderAscDesc).split("/")[0] + ")",
																																		 getString(R.string.presetsManager_OrderNames).split("/")[2] + " (" + getString(R.string.presetsManager_OrderAscDesc).split("/")[1] + ")",
																																		 getString(R.string.presetsManager_OrderNames).split("/")[3] + " (" + getString(R.string.presetsManager_OrderAscDesc).split("/")[0] + ")",
																																		 getString(R.string.presetsManager_OrderNames).split("/")[3] + " (" + getString(R.string.presetsManager_OrderAscDesc).split("/")[1] + ")",
																																		 getString(R.string.presetsManager_OrderNames).split("/")[4] + " (" + getString(R.string.presetsManager_OrderAscDesc).split("/")[0] + ")",
																																		 getString(R.string.presetsManager_OrderNames).split("/")[4] + " (" + getString(R.string.presetsManager_OrderAscDesc).split("/")[1] + ")"}, onlineOrderSelected,true);
																		dialogFragment.setDialogPositiveButton(mContext.getString(R.string.Dialog_Ok));
																		dialogFragment.showDialog();
																		if (onlineOrder.getVisibility() == View.VISIBLE)
																		{
																				onlineOrder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_out_top));
																				onlineOrder.setVisibility(View.GONE);
																		}
																		/*
																		AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
																		adb.setTitle(R.string.presetsManager_OrderBy);
																		adb.setSingleChoiceItems(new String[] {getString(R.string.presetsManager_OrderNames).split("/")[0] + " (" + getString(R.string.presetsManager_OrderAscDesc).split("/")[0] + ")",
																						getString(R.string.presetsManager_OrderNames).split("/")[0] + " (" + getString(R.string.presetsManager_OrderAscDesc).split("/")[1] + ")",
																						getString(R.string.presetsManager_OrderNames).split("/")[1] + " (" + getString(R.string.presetsManager_OrderAscDesc).split("/")[0] + ")",
																						getString(R.string.presetsManager_OrderNames).split("/")[1] + " (" + getString(R.string.presetsManager_OrderAscDesc).split("/")[1] + ")",
																						getString(R.string.presetsManager_OrderNames).split("/")[2] + " (" + getString(R.string.presetsManager_OrderAscDesc).split("/")[0] + ")",
																						getString(R.string.presetsManager_OrderNames).split("/")[2] + " (" + getString(R.string.presetsManager_OrderAscDesc).split("/")[1] + ")",
																						getString(R.string.presetsManager_OrderNames).split("/")[3] + " (" + getString(R.string.presetsManager_OrderAscDesc).split("/")[0] + ")",
																						getString(R.string.presetsManager_OrderNames).split("/")[3] + " (" + getString(R.string.presetsManager_OrderAscDesc).split("/")[1] + ")"}, onlineOrderSelected, new DialogInterface.OnClickListener() {

																						@Override
																						public void onClick(DialogInterface p1, int p2)
																						{
																								// TODO: Implement this method
																								ad.dismiss();
																								onlineOrderSelected = p2;
																								onlineOrderSelectedString = (ad).getListView().getItemAtPosition(p2).toString();
																								MainActivity.actionbar.setActionBarButtonListener(new OnClickListener() {

																												@Override
																												public void onClick(View p1)
																												{
																														// TODO: Implement this method
																														new getOnlinePresets().execute("order=" + onlineOrderSelectedString);
																												}
																										});
																								new getOnlinePresets().execute("order=" + (ad).getListView().getItemAtPosition(p2));
																						}
																				});

																		ad = adb.create();
																		ad.show();
																		*/
																}
														});
												onlineSearch.setVisibility(View.VISIBLE);
												onlineSearch.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_in_top));
												onlineOrder.setVisibility(View.VISIBLE);
												onlineOrder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_in_top));
										}
										else if (adapterViewPager.getPageTitle(p1).toString().equalsIgnoreCase(getString(R.string.presetsManager_TitleAccount)))
										{
												MainActivity.visibleFragment = "PresetsManagerAccount";
												if (LoginFragment.loginFragmentMode.equalsIgnoreCase("login"))
												{
														MainActivity.actionbar.setActionBarButton(getString(R.string.login_Title), R.drawable.ic_action_import, LoginFragment.loginOnClickListener);
												}
												else if (LoginFragment.loginFragmentMode.equalsIgnoreCase("register"))
												{
														MainActivity.actionbar.setActionBarButton(getString(R.string.login_TitleRegister), R.drawable.ic_action_import, LoginFragment.registerOnClickListener);
												}
												else if (LoginFragment.loginFragmentMode.equalsIgnoreCase("logout"))
												{
														MainActivity.actionbar.setActionBarButton(getString(R.string.login_TitleLogout), R.drawable.ic_action_export, LoginFragment.logoutOnClickListener);
												} else if (LoginFragment.loginFragmentMode.equalsIgnoreCase("recover")) {
														MainActivity.actionbar.setActionBarButton(getString(R.string.login_Recover),R.drawable.ic_action_settings_backup_restore ,LoginFragment.recoverOnClickListener);
												}
												if(onlineSearch.getVisibility() == View.VISIBLE) {
														onlineSearch.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_out_top));
														onlineSearch.setVisibility(View.GONE);
												}
												if(onlineSearchBar.getVisibility() == View.VISIBLE) {
														onlineSearchBar.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_out_top));
														onlineSearchBar.setVisibility(View.GONE);
														//onlineSearchEdit.setText("");
												}
												if (onlineOrder.getVisibility() == View.VISIBLE)
												{
														onlineOrder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_out_top));
														onlineOrder.setVisibility(View.GONE);
												}
												LoginFragment.checkState();
										}
										else
										{
												MainActivity.visibleFragment = "PresetsManager";
												MainActivity.actionbar.setActionBarButton(getString(R.string.PreviewPowerMenu), R.drawable.ic_action_launch, MainActivity.previewOnClickListener);
												if(onlineSearch.getVisibility() == View.VISIBLE) {
														onlineSearch.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_out_top));
														onlineSearch.setVisibility(View.GONE);
												}
												if(onlineSearchBar.getVisibility() == View.VISIBLE) {
														onlineSearchBar.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_out_top));
														onlineSearchBar.setVisibility(View.GONE);
														//onlineSearchEdit.setText("");
												}
												if (onlineOrder.getVisibility() == View.VISIBLE)
												{
														onlineOrder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_out_top));
														onlineOrder.setVisibility(View.GONE);
												}
										}
								}

								@Override
								public void onPageScrollStateChanged(int p1)
								{
										// TODO: Implement this method
								}
						});

        SmartTabLayout tabsStrip = (SmartTabLayout) InflatedView.findViewById(R.id.tabs);
				tabsStrip.setCustomTabView(R.layout.customtab, R.id.customTabText);

        tabsStrip.setViewPager(vpPager);

				progressHolder = (RelativeLayout) InflatedView.findViewById(R.id.presetsmanagerlistholderRelativeLayout_Progress);
				progressHolder.setVisibility(View.GONE);
				progress = (ProgressBar) InflatedView.findViewById(R.id.presetsmanagerlistholderProgressBar_Progress);
				LoadingMsg = (TextView) InflatedView.findViewById(R.id.presetsmanagerlistholderTextView_LoadMsg);

				progressHolder.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// Just prevent touch trough...
								}
						});
				return InflatedView;
		}

		public static void hideBars() {
				if(onlineSearchBar.getVisibility() == View.VISIBLE) {
						onlineSearchBar.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_out_top));
						onlineSearchBar.setVisibility(View.GONE);
				}
				onlineSearchTerm = "";
				onlineSearch.setVisibility(View.VISIBLE);
				onlineSearch.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_in_top));
				onlineOrder.setVisibility(View.VISIBLE);
				onlineOrder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_in_top));
		}
		
		public static boolean ImportPreset(final String surl, final PresetsAdapter adapter, String name, final String creator)
		{
				Importcancled = false;
				sCreator = "< unknown >";
				try
				{
						//Log.i("NPM","Showing import dialog for: "+surl);
						String fUrl = surl;
						if (!surl.endsWith(".nps"))
						{
								MainActivity.ImportUrl = null;
								Toast.makeText(mContext, "Import failed...\nUnknown file type!", Toast.LENGTH_LONG).show();
								return false;
						}
						if (surl.startsWith("file:"))
						{
								fUrl = surl.replace("file:", "");
						}
						newUrl = fUrl;
						File prefile = new File(newUrl);
						Filename = (name != null && !name.isEmpty()) ? name + ".nps" : prefile.getName();
						FileInputStream fIn = new FileInputStream(prefile);
						BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
						String aDataRow = ""; 
						while ((aDataRow = myReader.readLine()) != null)
						{ 
								//aBuffer += aDataRow + "\n";
								String[] aData = aDataRow.split("=");
								if (aData.length < 2)
								{
										MainActivity.ImportUrl = null;
										Toast.makeText(mContext, "Import failed...\nCorrupted or invalid preset!", Toast.LENGTH_LONG).show();
										return false;//presetInfo[1] = mContext.getString(R.string.presetsManager_Creator).replace("[CREATORNAME]",aData[1]);
								}
								if (aData[0].equalsIgnoreCase("Creator"))
								{
										sCreator = aData[1];
								}
						}
						final slideDownDialogFragment dialogFragment = new slideDownDialogFragment(mContext, MainActivity.fragmentManager);
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
												Importcancled = true;
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
												Filename = resultData.get(0) + ".nps";
												new ImportPreset().execute(newUrl, Filename, adapter);
										}

										@Override
										public void onTouchOutside()
										{
												// TODO: Implement this method
												Importcancled = true;
										}
								});
						dialogFragment.setDialogText(mContext.getString(R.string.presetsManager_Creator).replace("[CREATORNAME]", (creator != null && !creator.isEmpty()) ? creator : sCreator) + "\n\n" + mContext.getString(R.string.presetsManager_ImportMsg));
						dialogFragment.setDialogInput1(mContext.getString(R.string.presetSaveDialog_InfoText),Filename.replace(".nps",""),false,new TextWatcher() {

										@Override
										public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
										{
												// TODO: Implement this method
										}

										@Override
										public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
										{
												// TODO: Implement this method
												if (!p1.toString().equalsIgnoreCase("")) {
														File checkFile = new File(mContext.getFilesDir()+"/presets/"+p1.toString().replace("/","")+".nps");
														if (!checkFile.exists()) {
																dialogFragment.setOverwriteInfo(false);
																//dialogFragment.setDialogText("");
														} else {
																dialogFragment.setOverwriteInfo(true);
																//dialogFragment.setDialogText(context.getString(R.string.presetSaveDialog_OverwriteText));
														}
												}
										}

										@Override
										public void afterTextChanged(Editable p1)
										{
												// TODO: Implement this method
										}}
						);
						dialogFragment.setDialogNegativeButton(mContext.getString(R.string.Dialog_Cancel));
						dialogFragment.setDialogPositiveButton(mContext.getString(R.string.Dialog_Save));
						dialogFragment.showDialog();

				}
				catch (Throwable e)
				{
						Log.e("NPM", "Import failed!\n" + e.toString());
				}
				MainActivity.ImportUrl = null;
				if (Importcancled)
				{
						return false;
				}
				else
				{
						return true;
				}
		}

		public static class ImportPreset extends AsyncTask<Object, String, String>
		{

				PresetsAdapter adapter;
				String[] presetInfo = new String[4];
				File tmpfile;
				boolean newPresetAdded = false;
				boolean oldPreset = false;

				@Override
				protected void onPreExecute()
				{
						// TODO: Implement this method
						progressHolder.setVisibility(View.VISIBLE);
						progressHolder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
						super.onPreExecute();
				}

				@Override
				protected String doInBackground(Object[] p1)
				{
						// TODO: Implement this method
						try
						{
								adapter = (PresetsAdapter) p1[2];
								if (p1[0].toString().startsWith("file:"))
								{
										p1[0] = p1[0].toString().replace("file:", "");
								}
								Log.i("NPM", "Starting import for: " + p1[0]);
								File prefile = new File(p1[0].toString());
								String Filename = (String) p1[1];
								tmpfile = new File(mContext.getFilesDir().getPath() + "/presets/" + Filename);
								if (!tmpfile.exists())
								{
										newPresetAdded = true;
								}
								FileInputStream fIn = new FileInputStream(prefile);
								FileOutputStream fOut = new FileOutputStream(tmpfile);
								BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
								BufferedWriter myWriter = new BufferedWriter(new OutputStreamWriter(fOut));
								String aDataRow = ""; 
								//String aBuffer = "";
								presetInfo[0] = Filename.split(".nps")[0];
								presetInfo[1] = "< unknown >";
								presetInfo[2] = "true";
								presetInfo[3] = "true";
								while ((aDataRow = myReader.readLine()) != null)
								{ 
										//aBuffer += aDataRow + "\n";
										String[] aData = aDataRow.split("=");
										if (aData[0].equalsIgnoreCase("AppVersion"))
										{
												if (aData[1].equalsIgnoreCase("1.4.2"))
												{
														//Toast.makeText(MainActivity.context,"Converting old Save names...",Toast.LENGTH_SHORT).show();
														oldPreset = true;
												}
										}
										else if (p1.length >= 4 && aData[0].equalsIgnoreCase("creator"))
										{
												myWriter.write(aData[0] + "=" + p1[3]);
										}
										else
										{
												if (oldPreset && aData[0].equalsIgnoreCase("RevealBackground"))
												{
														aData[0] = "Reveal_Background";
												}
												else if (oldPreset && aData[0].equalsIgnoreCase("ActionRevealBackground"))
												{
														aData[0] = "ActionReveal_Background";
												}
												myWriter.write(aData[0] + "=" + aData[1] + "\n");
										}
										String[] loadColor = aData[0].split("_");
										publishProgress(loadColor[0] + ": " + aData[1]);
										if (aData[0].equalsIgnoreCase("Creator"))
										{
												presetInfo[1] = aData[1];
										}
								}
								myWriter.close();
								myReader.close();
								fOut.close();
								fIn.close();
						}
						catch (Throwable e)
						{
								Log.e("NPM", " Preset Import failed: " + e.toString());
								return e.toString();
						}
						return "true";
				}

				@Override
				protected void onProgressUpdate(String[] values)
				{
						// TODO: Implement this method
						super.onProgressUpdate(values);
						LoadingMsg.setText("Loading...\n" + values[0]);
				}

				@Override
				protected void onPostExecute(String result)
				{
						// TODO: Implement this method
						super.onPostExecute(result);
						progressHolder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
						progressHolder.setVisibility(View.GONE);
						if (result.equalsIgnoreCase("true"))
						{
								if (newPresetAdded)
								{
										adapter.insert(presetInfo);
								}
								Toast.makeText(mContext, mContext.getString(R.string.presetsManager_ImportSuccess).replace("[PRESETNAME]", presetInfo[0]), Toast.LENGTH_SHORT).show();
						}
						else
						{
								tmpfile.delete();
								Toast.makeText(mContext, mContext.getString(R.string.presetsManager_ImportFailed), Toast.LENGTH_LONG).show();
						}
						MainActivity.ImportUrl = null;
				}

		}

		private static class MyPagerAdapter extends FragmentStatePagerAdapter
		{
				private static String[] pageTitles;

				public MyPagerAdapter(FragmentManager fragmentManager, String[] titles)
				{
						super(fragmentManager);
						pageTitles = titles;
				}

				// Returns total number of pages
				@Override
				public int getCount()
				{
						return pageTitles.length;
				}

				// Returns the fragment to display for that page
				@Override
				public Fragment getItem(int position)
				{
						switch (position)
						{
								case 0:
										return new LoginFragment();
								case 1: // Fragment # 0 - This will show FirstFragment
										return new PresetsPage(0, "Local");
								case 2: // Fragment # 0 - This will show FirstFragment different title
										return new PresetsPage(1, "Online");
								default:
										return null;
						}
				}

				// Returns the page title for the top indicator
				@Override
				public CharSequence getPageTitle(int position)
				{
						return "" + pageTitles[position];
				}

		}
}

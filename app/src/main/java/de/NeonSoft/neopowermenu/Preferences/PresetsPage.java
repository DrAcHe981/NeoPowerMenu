package de.NeonSoft.neopowermenu.Preferences;

import android.app.*;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.Preferences.*;
import de.NeonSoft.neopowermenu.helpers.*;
import java.io.*;
import java.util.*;

import android.support.v4.app.Fragment;

public class PresetsPage extends Fragment
{
    // Store instance variables
		public static Activity mContext;
    private String title;
    private int page;
		
		public static ArrayList<String> onlineIds = new ArrayList<String>();
		
		public PresetsPage()
		{
				this.page = -1;
				this.title = null;
		}
    // newInstance constructor for creating fragment with arguments
    public PresetsPage(int page, String title)
		{
				this.page = page;
				this.title = title;
    }
		
    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState)
		{
        super.onCreate(savedInstanceState);
				mContext = getActivity();
        //page = getArguments().getInt("iPage", 0);
        //title = getArguments().getString("sTitle");
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
														 Bundle savedInstanceState)
		{
        View view = inflater.inflate(R.layout.presetsmanager_listholder, container, false);
				if(page!=-1 && title != null) {
				ListView list = (ListView) view.findViewById(R.id.presetsmanagerlistholderListView_Presets);

				RelativeLayout messageHolder = (RelativeLayout) view.findViewById(R.id.presetsmanagerlistholderRelativeLayout_Message);
						messageHolder.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View p1)
										{
												// TODO: Implement this method
										}
								});
				TextView message = (TextView) view.findViewById(R.id.presetsmanagerlistholderTextView_Message);
				messageHolder.setVisibility(View.GONE);

				if (title.equalsIgnoreCase("Local")) {
						message.setText(MainActivity.context.getString(R.string.presetsManager_LoadMore).split("\\|")[0]);
						messageHolder.setVisibility(View.VISIBLE);
						messageHolder.startAnimation(AnimationUtils.loadAnimation(PreferencesPresetsFragment.mContext,R.anim.fade_in));
						list.setVisibility(View.GONE);
						new loadLocal().execute(list,messageHolder,message);
				}
				else
				{
						PreferencesPresetsFragment.onlineAdapter = new PresetsAdapter(PreferencesPresetsFragment.mContext, PreferencesPresetsFragment.OnlineListTitles, PreferencesPresetsFragment.OnlineListDescs, PreferencesPresetsFragment.OnlineListEnabled, PreferencesPresetsFragment.OnlineListLocal);
						list.setAdapter(PreferencesPresetsFragment.onlineAdapter);
						list.setFastScrollEnabled(true);
						list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
						PreferencesPresetsFragment.onlineList = list;
						PreferencesPresetsFragment.onlineMSG = message;
						PreferencesPresetsFragment.onlineMSGHolder = messageHolder;
						PresetsPage.onlineIds.clear();
						PreferencesPresetsFragment.OnlineListTitles.clear();
						PreferencesPresetsFragment.OnlineListDescs.clear();
						PreferencesPresetsFragment.OnlineListEnabled.clear();
						PreferencesPresetsFragment.OnlineListLocal.clear();
						new getOnlinePresets().execute((PreferencesPresetsFragment.onlineOrderSelectedString.isEmpty() ? "" : "order=" + PreferencesPresetsFragment.onlineOrderSelectedString));
						//message.setVisibility(View.VISIBLE);
				}
				}
        return view;
    }
		
		class loadLocal extends AsyncTask<Object, String, String>
		{

				ListView list;
				RelativeLayout messageHolder;
				TextView message;
				File[] presetsFiles;
				String[] presetsListTitles;
				String[] presetsListDesc;
				String[] presetsListEnabled;
				String[] presetsListLocal;
				
				@Override
				protected void onPreExecute()
				{
						// TODO: Implement this method
						super.onPreExecute();
						File presetsFolder = new File(getActivity().getFilesDir().getPath() + "/presets/");
						presetsFiles = presetsFolder.listFiles(new FilenameFilter() {
										public boolean accept(File dir, String name)
										{
												return name.toLowerCase().endsWith(".nps");
										}});
						presetsListTitles = new String[presetsFiles.length + 3];
						presetsListDesc = new String[presetsFiles.length + 3];
						presetsListEnabled = new String[presetsFiles.length + 3];
						presetsListLocal = new String[presetsFiles.length + 3];
						String[] builtIn = getString(R.string.presetLoadDialog_BuiltIn).split("\\|");
						presetsListTitles[0] = builtIn[0];
						presetsListDesc[0] = "Neon-Soft.de" + " (" + getString(R.string.presetsManager_BuiltIn) + ")";
						presetsListEnabled[0] = "true";
						presetsListLocal[0] = "pre";
						presetsListTitles[1] = builtIn[1];
						presetsListDesc[1] = "Neon-Soft.de" + " (" + getString(R.string.presetsManager_BuiltIn) + ")";
						presetsListEnabled[1] = "true";
						presetsListLocal[1] = "pre";
						presetsListTitles[2] = builtIn[2];
						presetsListDesc[2] = "Neon-Soft.de" + " (" + getString(R.string.presetsManager_BuiltIn) + ")";
						presetsListEnabled[2] = "true";
						presetsListLocal[2] = "pre";
				}

				@Override
				protected String doInBackground(Object[] p1)
				{
						// TODO: Implement this method
						list = (ListView) p1[0];
						messageHolder = (RelativeLayout) p1[1];
						message = (TextView) p1[2];
						for (int i=0;i < presetsFiles.length;i++)
						{
								presetsListDesc[i + 3] = getString(R.string.presetsManager_Creator).replace("[CREATORNAME]", "<unknown>");
								presetsListTitles[i + 3] = presetsFiles[i].getName().split(".nps")[0];
								File tmpfile = new File(getActivity().getFilesDir().getPath() + "/presets/" + presetsFiles[i].getName());
								if(helper.isValidZip(tmpfile.getPath(),null)) {
										helper.unzipFile(tmpfile.getPath(),mContext.getFilesDir().getPath()+"/temp/",presetsFiles[i].getName(),null);
										tmpfile = new File(mContext.getFilesDir().getPath()+"/temp/"+presetsFiles[i].getName());
								}
								try
								{
										FileInputStream tmpread = new FileInputStream(tmpfile);
										BufferedReader myReader = new BufferedReader(new InputStreamReader(tmpread));
										String aDataRow = ""; 
										String aBuffer = "";
										while ((aDataRow = myReader.readLine()) != null)
										{ 
												String aData[] = aDataRow.split("=");
												if (aData[0].equalsIgnoreCase("Creator"))
												{
														aBuffer += aData[1];
														break;
												}
										}
										if (!aBuffer.equalsIgnoreCase(""))
										{
												presetsListDesc[i + 3] = aBuffer;
										}
										tmpread.close();
										myReader.close();
								}
								catch (Throwable e)
								{

								}
								if(tmpfile.getPath().startsWith(mContext.getFilesDir().getPath()+"/temp/")) {
										tmpfile.delete();
								}
								presetsListEnabled[i + 3] = "true";
								presetsListLocal[i + 3] = "true";
						}
						return null;
				}

				@Override
				protected void onPostExecute(String p1)
				{
						// TODO: Implement this method
						super.onPostExecute(p1);
						messageHolder.startAnimation(AnimationUtils.loadAnimation(PreferencesPresetsFragment.mContext,R.anim.fade_out));
						messageHolder.setVisibility(View.GONE);
						ArrayList<String> ListTitles = new ArrayList<String>(Arrays.asList(presetsListTitles));
						ArrayList<String> ListDescs = new ArrayList<String>(Arrays.asList(presetsListDesc));
						ArrayList<String> ListEnabled = new ArrayList<String>(Arrays.asList(presetsListEnabled));
						ArrayList<String> ListLocal = new ArrayList<String>(Arrays.asList(presetsListLocal));
						PreferencesPresetsFragment.localAdapter = new PresetsAdapter(getActivity(), ListTitles, ListDescs, ListEnabled, ListLocal);
						list.setAdapter(PreferencesPresetsFragment.localAdapter);
						list.setFastScrollEnabled(true);
						list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
						list.setVisibility(View.VISIBLE);
						list.startAnimation(AnimationUtils.loadAnimation(PreferencesPresetsFragment.mContext, R.anim.fade_in));
						if (MainActivity.ImportUrl != null)
						{
								PreferencesPresetsFragment.ImportPreset(MainActivity.ImportUrl, PreferencesPresetsFragment.localAdapter,null,null);
						}
				}
				
		}
		
}

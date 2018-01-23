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
		
		//public static ArrayList<String> onlineIds = new ArrayList<String>();
		
		public PresetsPage()
		{
				this.page = -1;
				this.title = null;
		}
    // newInstance constructor for creating fragment with arguments


	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
		this.page = args.getInt("page");
		this.title = args.getString("title");
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
						list.setFastScrollEnabled(true);
						list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

				RelativeLayout messageHolder = (RelativeLayout) view.findViewById(R.id.presetsmanagerlistholderRelativeLayout_Message);
						messageHolder.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View p1)
										{

										}
								});
				TextView message = (TextView) view.findViewById(R.id.presetsmanagerlistholderTextView_Message);
				messageHolder.setVisibility(View.GONE);

				if (title.equalsIgnoreCase("Local")) {
						message.setText(MainActivity.context.getString(R.string.presetsManager_LoadMore).split("\\|")[0]);
						messageHolder.setVisibility(View.VISIBLE);
						messageHolder.startAnimation(AnimationUtils.loadAnimation(PreferencesPresetsFragment.mContext,R.anim.fade_in));
						list.setVisibility(View.GONE);
						helper.startAsyncTask(new loadLocal(),list,messageHolder,message);
				}
				else
				{
					if (!MainActivity.visibleFragment.equalsIgnoreCase("tour")) {
						PreferencesPresetsFragment.onlineAdapter = new PresetsAdapter(PreferencesPresetsFragment.mContext, PreferencesPresetsFragment.OnlinePresets);
						list.setAdapter(PreferencesPresetsFragment.onlineAdapter);
						PreferencesPresetsFragment.onlineList = list;
						PreferencesPresetsFragment.onlineMSG = message;
						PreferencesPresetsFragment.onlineMSGHolder = messageHolder;
						PreferencesPresetsFragment.OnlinePresets.clear();
						PreferencesPresetsFragment.listParser = helper.startAsyncTask(new getOnlinePresets(), (PreferencesPresetsFragment.onlineOrderSelectedString.isEmpty() ? "" : "order=" + PreferencesPresetsFragment.onlineOrderSelectedString));
						//message.setVisibility(View.VISIBLE);
					}
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
				PresetsHolder[] presetsList;
				
				@Override
				protected void onPreExecute()
				{

						super.onPreExecute();
						File presetsFolder = new File(getActivity().getFilesDir().getPath() + "/presets/");
						presetsFiles = presetsFolder.listFiles(new FilenameFilter() {
										public boolean accept(File dir, String name)
										{
												return name.toLowerCase().endsWith(".nps");
										}});
						presetsList = new PresetsHolder[presetsFiles.length + 3];
						String[] builtIn = getString(R.string.presetLoadDialog_BuiltIn).split("\\|");
						PresetsHolder preset = new PresetsHolder();
						preset.setType(PresetsHolder.TYPE_INTERNAL);
						preset.setName(builtIn[0]);
						preset.setDescription("Neon-Soft.de" + " (" + getString(R.string.presetsManager_BuiltIn) + ")");
						presetsList[0] = preset;
						preset = new PresetsHolder();
						preset.setType(PresetsHolder.TYPE_INTERNAL);
						preset.setName(builtIn[1]);
						preset.setDescription("Neon-Soft.de" + " (" + getString(R.string.presetsManager_BuiltIn) + ")");
						presetsList[1] = preset;
						preset = new PresetsHolder();
						preset.setType(PresetsHolder.TYPE_INTERNAL);
						preset.setName(builtIn[2]);
						preset.setDescription("Neon-Soft.de" + " (" + getString(R.string.presetsManager_BuiltIn) + ")");
						presetsList[2] = preset;
				}

				@Override
				protected String doInBackground(Object[] p1)
				{

						list = (ListView) p1[0];
						messageHolder = (RelativeLayout) p1[1];
						message = (TextView) p1[2];
						for (int i=0;i < presetsFiles.length;i++)
						{
								PresetsHolder preset = new PresetsHolder();
								preset.setType(PresetsHolder.TYPE_INTERNAL);
								preset.setName(presetsFiles[i].getName().split(".nps")[0]);
								preset.setDescription(getString(R.string.presetsManager_Creator).replace("[CREATORNAME]", "<unknown>"));
								File tmpfile = new File(getActivity().getFilesDir().getPath() + "/presets/" + presetsFiles[i].getName());
								if(helper.isValidZip(tmpfile.getPath(),null)) {
										preset.setHasGraphics(true);
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
												preset.setDescription(aBuffer);
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
								presetsList[i + 3] = preset;
						}
						return null;
				}

				@Override
				protected void onPostExecute(String p1)
				{

						super.onPostExecute(p1);
						messageHolder.startAnimation(AnimationUtils.loadAnimation(PreferencesPresetsFragment.mContext,R.anim.fade_out));
						messageHolder.setVisibility(View.GONE);
						ArrayList<PresetsHolder> localpresets = new ArrayList<>(Arrays.asList(presetsList));
						PreferencesPresetsFragment.localAdapter = new PresetsAdapter(getActivity(), localpresets);
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

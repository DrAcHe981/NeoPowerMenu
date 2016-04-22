package de.NeonSoft.neopowermenu.Preferences;

import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.*;
import java.io.*;
import java.util.*;

public class PresetsPage extends Fragment
{
    // Store instance variables
    private String title;
    private int page;
		public static LinearLayout progress;

    // newInstance constructor for creating fragment with arguments
    public PresetsPage (int page, String title) {
				this.page = page;
				this.title = title;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //page = getArguments().getInt("iPage", 0);
        //title = getArguments().getString("sTitle");
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
														 Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.presetsmanager_listholder, container, false);
				ListView list = (ListView) view.findViewById(R.id.presetsmanagerlistholderListView_Presets);
				
				TextView message = (TextView) view.findViewById(R.id.presetsmanagerlistholderTextView_Message);
				message.setVisibility(View.GONE);
				
				progress = (LinearLayout) view.findViewById(R.id.presetsmanagerlistholderLinearLayout_Progress);
				progress.setVisibility(View.GONE);
				
				if(title.equalsIgnoreCase("Local")) {
				File presetsFolder = new File(getActivity().getFilesDir().getPath()+"/presets/");
				File[] presetsFiles = presetsFolder.listFiles(new FilenameFilter() {
								public boolean accept(File dir, String name) {
										return name.toLowerCase().endsWith(".nps");
								}});
				String[] presetsListTitles = new String[presetsFiles.length+3];
				String[] presetsListDesc = new String[presetsFiles.length+3];
				String[] presetsListEnabled = new String[presetsFiles.length+3];
				String[] presetsListLocal = new String[presetsFiles.length+3];
						presetsListTitles[0] = getString(R.string.presetLoadDialog_BuiltInLight);
						presetsListDesc[0] = getString(R.string.presetsManager_Creator).replace("[CREATORNAME]","Neon-Soft.de")+" ("+ getString(R.string.presetsManager_BuiltIn)+")";
				presetsListEnabled[0] = "true";
				presetsListLocal[0] = "pre";
						presetsListTitles[1] = getString(R.string.presetLoadDialog_BuiltInDark);
						presetsListDesc[1] = getString(R.string.presetsManager_Creator).replace("[CREATORNAME]","Neon-Soft.de")+" ("+ getString(R.string.presetsManager_BuiltIn)+")";
				presetsListEnabled[1] = "true";
				presetsListLocal[1] = "pre";
						presetsListTitles[2] = getString(R.string.presetLoadDialog_BuiltInBlack);
						presetsListDesc[2] = getString(R.string.presetsManager_Creator).replace("[CREATORNAME]","Neon-Soft.de")+" ("+ getString(R.string.presetsManager_BuiltIn)+")";
				presetsListEnabled[2] = "true";
				presetsListLocal[2] = "pre";
						for (int i=0;i<presetsFiles.length;i++) {
								presetsListDesc[i + 3] = getString(R.string.presetsManager_Creator).replace("[CREATORNAME]","<unknown>");
						presetsListTitles[i+3] = presetsFiles[i].getName().split(".nps")[0];
						File tmpfile = new File(getActivity().getFilesDir().getPath()+"/presets/"+ presetsFiles[i].getName());
						try
						{
								FileInputStream tmpread = new FileInputStream(tmpfile);
								BufferedReader myReader = new BufferedReader( new InputStreamReader(tmpread));
								String aDataRow = ""; 
								String aBuffer = "";
								while ((aDataRow = myReader.readLine()) != null) { 
										String aData[] = aDataRow.split("=");
										if(aData[0].equalsIgnoreCase("Creator")) {
												aBuffer += aData[1];
												break;
										}
								}
								if (!aBuffer.equalsIgnoreCase("")) {
										presetsListDesc[i + 3] = getString(R.string.presetsManager_Creator).replace("[CREATORNAME]",aBuffer);
								}
								
						}
						catch (Throwable e)
						{
								
						}
						presetsListEnabled[i+3] = "true";
						presetsListLocal[i+3] = "true";
				}
				ArrayList<String> ListTitles = new ArrayList<String>( Arrays.asList(presetsListTitles));
				ArrayList<String> ListDescs = new ArrayList<String>( Arrays.asList(presetsListDesc));
				ArrayList<String> ListEnabled = new ArrayList<String>( Arrays.asList(presetsListEnabled));
				ArrayList<String> ListLocal = new ArrayList<String>( Arrays.asList(presetsListLocal));
				PresetsAdapter adapter = new PresetsAdapter(getActivity(),ListTitles,ListDescs,ListEnabled,ListLocal);
				list.setAdapter(adapter);
						list.setFastScrollEnabled(true);
						list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				} else {
						message.setVisibility(View.VISIBLE);
				}
        return view;
    }
}

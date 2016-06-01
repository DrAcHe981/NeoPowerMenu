package de.NeonSoft.neopowermenu.Preferences;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.*;
import java.util.*;

public class AboutFragment extends Fragment
{

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
				// TODO: Implement this method
				MainActivity.visibleFragment = "about";
				MainActivity.hideActionBarButton();
				View InflatedView = inflater.inflate(R.layout.activity_about,container,false);
				
				ListView list = (ListView) InflatedView.findViewById(R.id.activityaboutListView1);
				
				String[] titles = new String[] {"About",
																				"User Id",
																				"Hooked Components",
																				"Used Librarys",
																				"HoloColorPicker",
																				"DragSortListView",
																				"libsuperuser"};
				ArrayList<String> titlesList = new ArrayList<String>(Arrays.asList(titles));

				String[] texts = new String[] {"NeoPowerMenu by Neon-Soft / DrAcHe981\nbased on a Source from Naman Dwivedi (naman14).\n\nTranslators:\n English: Robin G. (DrAcHe981), mrwasdennnoch\n German: Robin G. (DrAcHe981), mrwasdennnoch\n Polish: \n Portuguese: \n Russia: \n\nSpecial Thanks:\n You for using my Module.\n Naman Dwivedi (naman14) for the original source.\n rovo89 and Tungstwenty for Xposed.\n Igor Da Silva for the concept.",
																				"Your User Id:\n" + MainActivity.preferences.getString("userUniqeId","null") + "\nThis Id is used for the Preset Sever to verify your identity.",
																				helper.activeParts(getActivity()),
																				"This Project uses some public librarys, all (maybe i have forgot some...) used librarys are listed below.",
																				"HoloColorPicker from Lars Werkman.\nAn Android Holo themed colorpicker designed by Marie Schweiz.\nLicensed under the Apache License, Version 2.0",
																				"DragSortListView from Bauerca (DSLV) is an extension of the Android ListView that enables drag-and-drop reordering of list items.\nLicensed under the Apache License, Version 2.0",
																				"libsupersu from Chainfire / ChainsDD.\nLicensed under the Apache License, Version 2.0"};
				ArrayList<String> textsList = new ArrayList<String>(Arrays.asList(texts));
				
				aboutAdapter aa = new aboutAdapter(getActivity(),titlesList,textsList);
				
				list.setFastScrollEnabled(true);
				list.setAdapter(aa);
				
				return InflatedView;
		}
		
}

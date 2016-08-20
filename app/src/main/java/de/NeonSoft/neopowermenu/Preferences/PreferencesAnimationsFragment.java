package de.NeonSoft.neopowermenu.Preferences;
import android.os.*;
import android.view.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;
import android.view.View.*;
import de.NeonSoft.neopowermenu.helpers.*;

public class PreferencesAnimationsFragment extends android.support.v4.app.Fragment
{

		public static int[] defaultTypes = {1,2,0,2,0};
		public static String[] names = {
				"dialog",
				"reveal",
				"icons",
				"singlelineitems",
				"multilineitems"};
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
				// TODO: Implement this method
				MainActivity.visibleFragment = "Animations";
				MainActivity.actionbar.setTitle(getString(R.string.preferences_Animations).split("\\|")[0]);
				MainActivity.actionbar.setSubTitle(getString(R.string.preferences_Animations).split("\\|")[1]);
				
				View InflatedView = inflater.inflate(R.layout.activity_animations, container, false);
				
				ListView holder = (ListView) InflatedView.findViewById(R.id.activityanimationsListView);
				
				String[] items = getString(R.string.animations_Items).split("\\|");
				String[] types = getString(R.string.animations_Types).split("\\|");
				String[] speeds = getString(R.string.animations_Speeds).split("\\|");
				
				animationsAdapter adapter = new animationsAdapter(getActivity(), names, defaultTypes);
				
				holder.setAdapter(adapter);
				
				return InflatedView;
		}
		
}

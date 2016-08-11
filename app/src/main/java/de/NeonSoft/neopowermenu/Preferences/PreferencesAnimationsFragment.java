package de.NeonSoft.neopowermenu.Preferences;
import android.view.*;
import android.os.*;
import de.NeonSoft.neopowermenu.*;

public class PreferencesAnimationsFragment extends android.support.v4.app.Fragment
{

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
				// TODO: Implement this method
				MainActivity.visibleFragment = "Animations";
				MainActivity.actionbar.setTitle(getString(R.string.preferences_Animations).split("\\|")[0]);
				MainActivity.actionbar.setSubTitle(getString(R.string.preferences_Animations).split("\\|")[1]);
				
				View InflatedView = inflater.inflate(R.layout.activity_animations, container, false);
				
				
				
				return InflatedView;
		}
		
}

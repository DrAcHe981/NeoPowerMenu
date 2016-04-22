package de.NeonSoft.neopowermenu.Preferences;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.view.*;
import android.view.*;
import com.ogaclejapan.smarttablayout.*;
import de.NeonSoft.neopowermenu.*;

import de.NeonSoft.neopowermenu.R;

public class PreferencesPresetsFragment extends Fragment
{

		View InflatedView;
		MyPagerAdapter adapterViewPager;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
				// TODO: Implement this method
				MainActivity.visibleFragment = "PresetsManager";

				InflatedView = inflater.inflate(R.layout.activity_presetsmanager, container, false);
				
        ViewPager vpPager = (ViewPager) InflatedView.findViewById(R.id.pager);
        adapterViewPager = new MyPagerAdapter(MainActivity.fragmentManager, new String[] {getString(R.string.presetsManager_TitleLocal),getString(R.string.presetsManager_TitleOnline)});
        vpPager.setAdapter(adapterViewPager);

        SmartTabLayout tabsStrip = (SmartTabLayout) InflatedView.findViewById(R.id.tabs);
				tabsStrip.setCustomTabView(R.layout.customtab,R.id.customTabText);
				
        tabsStrip.setViewPager(vpPager);
				
				return InflatedView;
		}

		private static class MyPagerAdapter extends FragmentStatePagerAdapter
		{
				private static String[] pageTitles;
				
				public MyPagerAdapter(FragmentManager fragmentManager, String[] titles) {
						super(fragmentManager);
						pageTitles = titles;
				}

				// Returns total number of pages
				@Override
				public int getCount() {
						return pageTitles.length;
				}

				// Returns the fragment to display for that page
				@Override
				public Fragment getItem(int position) {
						switch (position) {
								case 0: // Fragment # 0 - This will show FirstFragment
										return new PresetsPage(0, "Local");
								case 1: // Fragment # 0 - This will show FirstFragment different title
										return new PresetsPage(1, "Online");
								default:
										return null;
						}
				}

				// Returns the page title for the top indicator
				@Override
				public CharSequence getPageTitle(int position) {
						return "" + pageTitles[position];
				}
				
		}
}

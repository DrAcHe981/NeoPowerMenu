package de.NeonSoft.neopowermenu.Preferences;
import android.app.*;
import android.os.*;
import android.view.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.DSLV.*;
import de.NeonSoft.neopowermenu.helpers.*;
import java.util.*;
import android.support.v4.app.Fragment;
import de.NeonSoft.neopowermenu.xposed.*;

public class PreferencesVisibilityOrderFragment extends Fragment
{
		

    private visibilityOrder_ListAdapter adapter;
		
		private String[][] items;
    private String[] arrayTitles;
    private ArrayList<String> listTitles;
		private String[] arrayEnabledStates;
		private ArrayList<String> listEnabledStates;
		
    private DragSortListView.DropListener onDrop =
		new DragSortListView.DropListener() {
				@Override
				public void drop(int from, int to) {
						if (from != to) {
								String[] item=adapter.getItemAt(from);
								
								adapter.removeAt(from);
								adapter.insertAt(item, to);
								adapter.OutputSorting();
								adapter.notifyDataSetChanged();
						}
				}
		};

    private DragSortListView.RemoveListener onRemove = 
		new DragSortListView.RemoveListener() {
				@Override
				public void remove(int which) {
						adapter.remove(adapter.getItem(which));
				}
		};

    private DragSortListView.DragScrollProfile ssProfile =
		new DragSortListView.DragScrollProfile() {
				@Override
				public float getSpeed(float w, long t) {
						if (w > 0.8f) {
								// Traverse all views in a millisecond
								return ((float) adapter.getCount()) / 0.001f;
						} else {
								return 10.0f * w;
						}
				}
		};
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
				
				MainActivity.visibleFragment = "VisibilityOrder";
				View InflatedView = inflater.inflate(R.layout.activity_visibilityorder,container,false);
				
        DragSortListView lv = (DragSortListView) InflatedView.findViewById(R.id.activityvisibilityorderDSLV_List); 
				
        lv.setDropListener(onDrop);
        lv.setRemoveListener(onRemove);
        lv.setDragEnabled(true);
        lv.setDragScrollProfile(ssProfile);
				lv.setFastScrollEnabled(true);
				
				items = new String[][] {
						{"Shutdown","true"},
						{"Reboot","true"},
						{"SoftReboot","true"},
						{"Screenshot","true"},
						{"Screenrecord",XposedUtils.isExynosDevice() ? "false" : "true"},
						{"Flashlight",XposedUtils.hasFlash(getActivity()) ? "true" : "false"},
						{"ExpandedDesktop",(XposedUtils.isAppInstalled(getActivity(),"com.ceco.kitkat.gravitybox") || XposedUtils.isAppInstalled(getActivity(),"com.ceco.lollipop.gravitybox") || XposedUtils.isAppInstalled(getActivity(),"com.ceco.marshmallow.gravitybox")) ? "true" : "false"},
						{"AirplaneMode","true"},
						{"RestartUI","true"},
						{"SoundMode","true"}};

        arrayTitles = new String[items.length];
				arrayEnabledStates = new String[items.length];
				
				for (int i=0;i<items.length;i++) {
				if (MainActivity.preferences.getInt(items[i][0]+"Position",-1)==-1) {
						if (items[i][0].equalsIgnoreCase("Shutdown") || items[i][0].equalsIgnoreCase("Reboot") || items[i][0].equalsIgnoreCase("SoftReboot")) {
								MainActivity.preferences.edit().putBoolean(items[i][0]+"Enabled",true).commit();
						} else {
								MainActivity.preferences.edit().putBoolean(items[i][0]+"Enabled",false).commit();
						}
						MainActivity.preferences.edit().putInt(items[i][0]+"Position",i).commit();
				}
				arrayTitles[MainActivity.preferences.getInt(items[i][0]+"Position",i)] = items[i][0];
				arrayEnabledStates[MainActivity.preferences.getInt(items[i][0]+"Position",i)] = items[i][1];
				}
				
        listTitles = new ArrayList<String>(Arrays.asList(arrayTitles));
				listEnabledStates = new ArrayList<String>(Arrays.asList(arrayEnabledStates));
        adapter = new visibilityOrder_ListAdapter(getActivity(),listTitles,listEnabledStates);
				
        lv.setAdapter(adapter);
						
				return InflatedView;
		}
		
}

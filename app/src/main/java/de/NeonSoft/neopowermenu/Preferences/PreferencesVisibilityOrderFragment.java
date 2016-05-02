package de.NeonSoft.neopowermenu.Preferences;
import android.app.*;
import android.os.*;
import android.view.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.DSLV.*;
import de.NeonSoft.neopowermenu.helpers.*;
import java.util.*;
import android.support.v4.app.Fragment;

public class PreferencesVisibilityOrderFragment extends Fragment
{
		

    private visibilityOrder_ListAdapter adapter;
		
    private String[] arrayTitles;
    private ArrayList<String> listTitles;
		private String[] arrayPrefPositions;
		private ArrayList<String> listPrefPositions;
		private String[] arrayDescs;
		private ArrayList<String> listDescs;
    private String[] arrayPrefNames;
    private ArrayList<String> listPrefNames;
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
				
				
				if (MainActivity.preferences.getInt("ShutdownPosition",-1)==-1) {
						MainActivity.preferences.edit().putBoolean("ShutdownEnabled",true).commit();
						MainActivity.preferences.edit().putInt("ShutdownPosition",0).commit();
				}
				if (MainActivity.preferences.getInt("RebootPosition",-1)==-1) {
						MainActivity.preferences.edit().putBoolean("RebootEnabled",true).commit();
						MainActivity.preferences.edit().putInt("RebootPosition",1).commit();
				}
				if (MainActivity.preferences.getInt("SoftRebootPosition",-1)==-1) {
						MainActivity.preferences.edit().putBoolean("SoftRebootEnabled",true).commit();
						MainActivity.preferences.edit().putInt("SoftRebootPosition",2).commit();
				}
				if (MainActivity.preferences.getInt("ScreenshotPosition",-1)==-1) {
						MainActivity.preferences.edit().putBoolean("ScreenshotEnabled",false).commit();
						MainActivity.preferences.edit().putInt("ScreenshotPosition",3).commit();
				}
				if (MainActivity.preferences.getInt("ScreenrecordPosition",-1)==-1) {
						MainActivity.preferences.edit().putBoolean("ScreenrecordEnabled",false).commit();
						MainActivity.preferences.edit().putInt("ScreenrecordPosition",4).commit();
				}
				
        arrayTitles = new String[5];
				arrayTitles[MainActivity.preferences.getInt("ShutdownPosition",0)] = getString(R.string.visibilityOrderTitle_Shutdown);
				arrayTitles[MainActivity.preferences.getInt("RebootPosition",1)] = getString(R.string.visibilityOrderTitle_Reboot);
				arrayTitles[MainActivity.preferences.getInt("SoftRebootPosition",2)] = getString(R.string.visibilityOrderTitle_SoftReboot);
				arrayTitles[MainActivity.preferences.getInt("ScreenshotPosition",3)] = getString(R.string.visibilityOrderTitle_Screenshot);
				arrayTitles[MainActivity.preferences.getInt("ScreenrecordPosition",4)] = getString(R.string.visibilityOrderTitle_Screenrecord);
        listTitles = new ArrayList<String>(Arrays.asList(arrayTitles));

        arrayPrefPositions = new String[5];
				arrayPrefPositions[MainActivity.preferences.getInt("ShutdownPosition",0)] = "ShutdownPosition";
				arrayPrefPositions[MainActivity.preferences.getInt("RebootPosition",1)] = "RebootPosition";
				arrayPrefPositions[MainActivity.preferences.getInt("SoftRebootPosition",2)] = "SoftRebootPosition";
				arrayPrefPositions[MainActivity.preferences.getInt("ScreenshotPosition",3)] = "ScreenshotPosition";
				arrayPrefPositions[MainActivity.preferences.getInt("ScreenrecordPosition",4)] = "ScreenrecordPosition";
				listPrefPositions = new ArrayList<String>(Arrays.asList(arrayPrefPositions));
				
				arrayDescs = new String[5];
				arrayDescs[MainActivity.preferences.getInt("ShutdownPosition",0)] = getString(R.string.visibilityOrderDesc_Shutdown);
				arrayDescs[MainActivity.preferences.getInt("RebootPosition",1)] = getString(R.string.visibilityOrderDesc_Reboot);
				arrayDescs[MainActivity.preferences.getInt("SoftRebootPosition",2)] = getString(R.string.visibilityOrderDesc_SoftReboot);
				arrayDescs[MainActivity.preferences.getInt("ScreenshotPosition",3)] = getString(R.string.visibilityOrderDesc_Screenshot);
				arrayDescs[MainActivity.preferences.getInt("ScreenrecordPosition",4)] = getString(R.string.visibilityOrderDesc_Screenrecord);
				listDescs = new ArrayList<String>(Arrays.asList(arrayDescs));
				
				arrayPrefNames = new String[5];
				arrayPrefNames[MainActivity.preferences.getInt("ShutdownPosition",0)] = "ShutdownEnabled";
				arrayPrefNames[MainActivity.preferences.getInt("RebootPosition",1)] = "RebootEnabled";
				arrayPrefNames[MainActivity.preferences.getInt("SoftRebootPosition",2)] = "SoftRebootEnabled";
				arrayPrefNames[MainActivity.preferences.getInt("ScreenshotPosition",3)] = "ScreenshotEnabled";
				arrayPrefNames[MainActivity.preferences.getInt("ScreenrecordPosition",4)] = "ScreenrecordEnabled";
				listPrefNames = new ArrayList<String>(Arrays.asList(arrayPrefNames));
				
				arrayEnabledStates = new String[5];
				arrayEnabledStates[MainActivity.preferences.getInt("ShutdownPosition",0)] = "true";
				arrayEnabledStates[MainActivity.preferences.getInt("RebootPosition",1)] = "true";
				arrayEnabledStates[MainActivity.preferences.getInt("SoftRebootPosition",2)] = "true";
				arrayEnabledStates[MainActivity.preferences.getInt("ScreenshotPosition",3)] = "true";
				arrayEnabledStates[MainActivity.preferences.getInt("ScreenrecordPosition",4)] = "true";
				listEnabledStates = new ArrayList<String>(Arrays.asList(arrayEnabledStates));
				
        adapter = new visibilityOrder_ListAdapter(getActivity(),listTitles,listPrefPositions,listDescs,listPrefNames,listEnabledStates);
				
        lv.setAdapter(adapter);
						
				return InflatedView;
		}
		
}

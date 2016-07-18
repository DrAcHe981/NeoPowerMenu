package de.NeonSoft.neopowermenu.Preferences;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.DSLV.*;
import de.NeonSoft.neopowermenu.helpers.*;
import java.util.*;
import android.view.View.*;
import com.android.internal.os.*;

public class PreferencesVisibilityOrderFragmentNew extends Fragment
{

		TextView TextView_AddNormalItem;
		TextView TextView_AddMultiItem;
		
		DragSortListView DSLV_List;
		public static visibilityOrderNew_ListAdapter adapter;

		public static LinearLayout LinearLayout_Progress;
		
		public static String[] PowerMenuItems = new String[] {
				"Empty",
				"Shutdown",
				"Reboot",
				"SoftReboot",
				"Screenshot",
				"Screenrecord",
				"Flashlight",
				"ExpandedDesktop",
				"AirplaneMode",
				"RestartUI",
				"SoundMode",
				"Recovery",
				"Bootloader",
				"SafeMode"};
		public static String[] PowerMenuItemsTexts;
		
    private DragSortListView.DropListener onDrop =
		new DragSortListView.DropListener() {
				@Override
				public void drop(int from, int to) {
						if (from != to) {
								Object[] item=adapter.getItemAt(from);

								adapter.removeAt(from);
								adapter.insertAt(to,item);
								//adapter.notifyDataSetChanged();
						}
				}
		};

    private DragSortListView.RemoveListener onRemove = 
		new DragSortListView.RemoveListener() {
				@Override
				public void remove(int which) {
						adapter.removeAt(which);
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
				// TODO: Implement this method
				MainActivity.visibleFragment = "VisibilityOrder";

				MainActivity.actionbar.setTitle(getString(R.string.preferencesTitle_VisibilityOrder));
				MainActivity.actionbar.setSubTitle(getString(R.string.preferencesDesc_VisibilityOrder));
				
				PowerMenuItemsTexts = new String[] {
						getString(R.string.powerMenuMain_Empty),
						getString(R.string.powerMenuMain_Shutdown),
						getString(R.string.powerMenuMain_Reboot),
						getString(R.string.powerMenuMain_SoftReboot),
						getString(R.string.powerMenuMain_Screenshot),
						getString(R.string.powerMenuMain_Screenrecord),
						getString(R.string.powerMenuMain_Flashlight),
						getString(R.string.powerMenuMain_ExpandedDesktop),
						getString(R.string.powerMenuMain_AirplaneMode),
						getString(R.string.powerMenuMain_RestartUI),
						getString(R.string.powerMenuMain_SoundMode),
						getString(R.string.powerMenuBottom_Recovery),
						getString(R.string.powerMenuBottom_Bootloader),
						getString(R.string.powerMenuBottom_SafeMode)};
				
				View InflatedView = inflater.inflate(R.layout.visibilityorder, container, false);
				
				TextView_AddNormalItem = (TextView) InflatedView.findViewById(R.id.visibilityorderTextView_AddNormal);
				TextView_AddMultiItem = (TextView) InflatedView.findViewById(R.id.visibilityorderTextView_AddMulti);
				
				TextView_AddNormalItem.setText(getString(R.string.visibilityOrder_Add).split("/")[0]);
				TextView_AddMultiItem.setText(getString(R.string.visibilityOrder_Add).split("/")[1]);
				
				DSLV_List = (DragSortListView) InflatedView.findViewById(R.id.visibilityorderDSLV_List);

        DSLV_List.setDropListener(onDrop);
        DSLV_List.setRemoveListener(onRemove);
        DSLV_List.setDragEnabled(true);
        DSLV_List.setDragScrollProfile(ssProfile);
				DSLV_List.setFastScrollEnabled(true);

				ArrayList<Integer> types = new ArrayList<Integer>(Arrays.asList(new Integer[] {}));
				ArrayList<String> items = new ArrayList<String>(Arrays.asList(new String[] {}));
				int i = 0;
				while (MainActivity.orderPrefs.getInt(i+"_item_type",-1) != -1) {
						types.add(MainActivity.orderPrefs.getInt(i+"_item_type",adapter.TYPE_NORMAL));
						if(types.get(i) == adapter.TYPE_NORMAL) {
								items.add(MainActivity.orderPrefs.getString(i+"_item_title","null"));
						} else if(types.get(i) == adapter.TYPE_MULTI) {
								items.add(MainActivity.orderPrefs.getString(i+"_item1_title","null") + "," +
										MainActivity.orderPrefs.getString(i+"_item2_title","null") + "," +
										MainActivity.orderPrefs.getString(i+"_item3_title","null"));
						}
						i++;
				}
				
				adapter = new visibilityOrderNew_ListAdapter(getActivity(),types,items);
				
				DSLV_List.setAdapter(adapter);
				
				LinearLayout_Progress = (LinearLayout) InflatedView.findViewById(R.id.activityvisibilityorderLinearLayout_Saving);
				LinearLayout_Progress.setVisibility(View.GONE);
				
				LinearLayout_Progress.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
								}
						});
				
				TextView_AddNormalItem.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										slideDownDialogFragment dialogFragment = new slideDownDialogFragment(getActivity(), MainActivity.fragmentManager);
										dialogFragment.setDialogListener(new slideDownDialogFragment.slideDownDialogInterface() {

														@Override
														public void onListItemClick(int position, String text)
														{
																// TODO: Implement this method
																adapter.addItem(visibilityOrderNew_ListAdapter.TYPE_NORMAL,PowerMenuItems[position]);
														}

														@Override
														public void onNegativeClick()
														{
																// TODO: Implement this method
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
														}

														@Override
														public void onTouchOutside()
														{
																// TODO: Implement this method
														}
												});
										dialogFragment.setDialogList(ListView.CHOICE_MODE_NONE,PreferencesVisibilityOrderFragmentNew.PowerMenuItemsTexts,0,true);
										dialogFragment.setDialogPositiveButton(getString(R.string.Dialog_Cancel));
										dialogFragment.showDialog(R.id.dialog_container);
								}
						});
						
				TextView_AddMultiItem.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										slideDownDialogFragment dialogFragment = new slideDownDialogFragment(getActivity(), MainActivity.fragmentManager);
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
																String[] split = resultData.get(0).split(",");
																String splitStr = "";
																for(int i = 0;i < split.length;i++) {
																		splitStr = splitStr + PowerMenuItems[Integer.parseInt(split[i])] + (i >= split.length ? "" : ",");
																}
																//Toast.makeText(getActivity(),"Adding "+split.length+" items: "+splitStr,Toast.LENGTH_LONG).show();
																adapter.addItem(visibilityOrderNew_ListAdapter.TYPE_MULTI,splitStr);
														}

														@Override
														public void onTouchOutside()
														{
																// TODO: Implement this method
														}
												});
												dialogFragment.setDialogText(getString(R.string.visibilityOrder_SelectMulti));
										dialogFragment.setDialogList(ListView.CHOICE_MODE_MULTIPLE,PreferencesVisibilityOrderFragmentNew.PowerMenuItemsTexts,-1,false);
										dialogFragment.setDialogListLimit(3,false);
										dialogFragment.setDialogNegativeButton(getString(R.string.Dialog_Cancel));
										dialogFragment.setDialogPositiveButton(getString(R.string.Dialog_Ok));
										dialogFragment.showDialog(R.id.dialog_container);
								}
						});
				
						MainActivity.actionbar.hideButton();
						
				return InflatedView;
		}
		
}

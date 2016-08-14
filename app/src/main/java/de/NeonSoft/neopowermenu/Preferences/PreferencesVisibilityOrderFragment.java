package de.NeonSoft.neopowermenu.Preferences;
import android.os.*;
import android.support.v4.app.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.DSLV.*;
import de.NeonSoft.neopowermenu.helpers.*;
import java.util.*;

public class PreferencesVisibilityOrderFragment extends Fragment
{

		//TextView TextView_AddNormalItem;
		//TextView TextView_AddMultiItem;
		
		LinearLayout LinearLayout_Add;
		
		DragSortListView DSLV_List;
		public static visibilityOrder_ListAdapter adapter;

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
				"SafeMode",
				"SoundVibrate",
				"SoundNormal",
				"SoundSilent"};
		public static String[] PowerMenuItemsTexts;
		
    private DragSortListView.DropListener onDrop =
		new DragSortListView.DropListener() {
				@Override
				public void drop(int from, int to) {
						adapter.move(from, to);
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
						getString(R.string.powerMenuMain_ExpandedDesktop)+" (GravityBox)",
						getString(R.string.powerMenuMain_AirplaneMode),
						getString(R.string.powerMenuMain_RestartUI),
						getString(R.string.powerMenuMain_SoundMode),
						getString(R.string.powerMenuBottom_Recovery),
						getString(R.string.powerMenuBottom_Bootloader),
						getString(R.string.powerMenuBottom_SafeMode),
						getString(R.string.powerMenuMain_SoundVibrate),
						getString(R.string.powerMenuMain_SoundNormal),
						getString(R.string.powerMenuMain_SoundSilent)};
				
				View InflatedView = inflater.inflate(R.layout.visibilityorder, container, false);
				
				/*TextView_AddNormalItem = (TextView) InflatedView.findViewById(R.id.visibilityorderTextView_AddNormal);
				TextView_AddMultiItem = (TextView) InflatedView.findViewById(R.id.visibilityorderTextView_AddMulti);
				
				TextView_AddNormalItem.setText(getString(R.string.visibilityOrder_Add).split("\\|")[0]);
				TextView_AddMultiItem.setText(getString(R.string.visibilityOrder_Add).split("\\|")[1]);*/
				
				LinearLayout_Add = (LinearLayout) InflatedView.findViewById(R.id.visibilityorderLinearLayout_Add);
				
				DSLV_List = (DragSortListView) InflatedView.findViewById(R.id.visibilityorderDSLV_List);

        DSLV_List.setDropListener(onDrop);
        DSLV_List.setRemoveListener(onRemove);
        DSLV_List.setDragEnabled(true);
        DSLV_List.setDragScrollProfile(ssProfile);
				DSLV_List.setFastScrollEnabled(true);

				ArrayList<Integer> types = new ArrayList<Integer>(Arrays.asList(new Integer[] {}));
				ArrayList<String> items = new ArrayList<String>(Arrays.asList(new String[] {}));
				//int i = 0;
				ArrayList<String> MultiPage = new ArrayList<String>();
				for(int i = 0; i < MainActivity.orderPrefs.getAll().keySet().size(); i++) {
						//Log.i("NPM:visibilityorder","Loading item "+(MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+i+
						//				"\nType: "+MainActivity.orderPrefs.getInt((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+ i+"_item_type",-1));
						if(MainActivity.orderPrefs.getInt((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+ i+"_item_type",-1)!=-1) {
						types.add(MainActivity.orderPrefs.getInt((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+ i+"_item_type",adapter.TYPE_NORMAL));
						if(MainActivity.orderPrefs.getInt((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+ i+"_item_type",adapter.TYPE_NORMAL) == adapter.TYPE_NORMAL) {
								items.add(MainActivity.orderPrefs.getString((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+ i+"_item_title","null"));
								//Log.i("NPM:visibilityorder","Content(single): "+items.get(i));
						} else if(MainActivity.orderPrefs.getInt((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+ i+"_item_type",adapter.TYPE_NORMAL) == adapter.TYPE_MULTI) {
								items.add(MainActivity.orderPrefs.getString((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+ i+"_item1_title","null") + "|" +
													MainActivity.orderPrefs.getString((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+ i+"_item2_title","null") + "|" +
													MainActivity.orderPrefs.getString((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+ i+"_item3_title","null"));
								//Log.i("NPM:visibilityorder","Content(multi): "+items.get(i));
						} else if (MainActivity.orderPrefs.getInt((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+i+"_item_type",-1)==visibilityOrder_ListAdapter.TYPE_MULTIPAGE_START) {
								//types.add(MainActivity.orderPrefs.getInt((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+i+"_item_type",adapter.TYPE_NORMAL));
								items.add(MainActivity.orderPrefs.getString((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+i+"_item_title","null"));
								MultiPage.add(MainActivity.orderPrefs.getString((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+i+"_item_title","null"));
								//Log.i("NPM:visibilityorder","Content: open multi page");
						} else if (MainActivity.orderPrefs.getInt((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+i+"_item_type",-1)==visibilityOrder_ListAdapter.TYPE_MULTIPAGE_END) {
								//types.add(visibilityOrder_ListAdapter.TYPE_MULTIPAGE_END);
								items.add(MainActivity.orderPrefs.getString((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+i+"_item_title","null"));
								MultiPage.remove(MultiPage.size()-1);
								//Log.i("NPM:visibilityorder","Content: close multi page");
						}
				}	
				}
				/*while (MainActivity.orderPrefs.getInt(i+"_item_type",-1) != -1) {
						types.add(MainActivity.orderPrefs.getInt(i+"_item_type",adapter.TYPE_NORMAL));
						if(types.get(i) == adapter.TYPE_NORMAL) {
								items.add(MainActivity.orderPrefs.getString(i+"_item_title","null"));
						} else if(types.get(i) == adapter.TYPE_MULTI) {
								items.add(MainActivity.orderPrefs.getString(i+"_item1_title","null") + "|" +
										MainActivity.orderPrefs.getString(i+"_item2_title","null") + "|" +
										MainActivity.orderPrefs.getString(i+"_item3_title","null"));
						}
						i++;
				}*/
				
				adapter = new visibilityOrder_ListAdapter(getActivity(),types,items);
				
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
				
				LinearLayout_Add.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										slideDownDialogFragment dialogFragment = new slideDownDialogFragment(getActivity(), MainActivity.fragmentManager);
										dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

														@Override
														public void onListItemClick(int position, String text)
														{
																// TODO: Implement this method
																if(position==0) {
																		slideDownDialogFragment dialogFragment = new slideDownDialogFragment(getActivity(), MainActivity.fragmentManager);
																		dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

																						@Override
																						public void onListItemClick(int position, String text)
																						{
																								// TODO: Implement this method
																								adapter.addItem(visibilityOrder_ListAdapter.TYPE_NORMAL,PowerMenuItems[position]);
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
																						public void onPositiveClick(Bundle resultBundle)
																						{
																								// TODO: Implement this method
																						}

																						@Override
																						public void onTouchOutside()
																						{
																								// TODO: Implement this method
																						}
																				});
																		dialogFragment.setList(ListView.CHOICE_MODE_NONE,PreferencesVisibilityOrderFragment.PowerMenuItemsTexts,0,true);
																		dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[4]);
																		dialogFragment.showDialog(R.id.dialog_container);
																} else if (position==1) {
																		slideDownDialogFragment dialogFragment = new slideDownDialogFragment(getActivity(), MainActivity.fragmentManager);
																		dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

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
																						public void onPositiveClick(Bundle resultBundle)
																						{
																								// TODO: Implement this method
																								String[] split = resultBundle.getString(slideDownDialogFragment.RESULT_LIST).split(",");
																								String splitStr = "";
																								int type = visibilityOrder_ListAdapter.TYPE_MULTI;
																								for(int i = 0;i < split.length;i++) {
																										splitStr = splitStr + PowerMenuItems[Integer.parseInt(split[i])];
																										splitStr = splitStr + (i >= split.length ? "" : "|");
																								}
																								//Toast.makeText(getActivity(),"Adding "+split.length+" items: "+splitStr,Toast.LENGTH_LONG).show();
																								adapter.addItem(type,splitStr);
																						}

																						@Override
																						public void onTouchOutside()
																						{
																								// TODO: Implement this method
																						}
																				});
																		dialogFragment.setText(getString(R.string.visibilityOrder_SelectMulti));
																		dialogFragment.setList(ListView.CHOICE_MODE_MULTIPLE,PreferencesVisibilityOrderFragment.PowerMenuItemsTexts,-1,false);
																		dialogFragment.setListLimit(3,false);
																		dialogFragment.setNegativeButton(getString(R.string.Dialog_Buttons).split("\\|")[4]);
																		dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[0]);
																		dialogFragment.showDialog(R.id.dialog_container);
																} else if (position==2) {
																		slideDownDialogFragment dialogFragment = new slideDownDialogFragment(getActivity(), MainActivity.fragmentManager);
																		dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

																						@Override
																						public void onListItemClick(int position, String text)
																						{
																								// TODO: Implement this method
																								Date date = new Date();
																								String groupName = helper.md5Crypto(date.getDay()+"."+date.getMonth()+"."+date.getYear()+"/"+date.getHours()+":"+date.getMinutes()+":"+date.getMinutes());
																								adapter.addItem(visibilityOrder_ListAdapter.TYPE_MULTIPAGE_START,groupName);
																								adapter.addItem(visibilityOrder_ListAdapter.TYPE_NORMAL,PowerMenuItems[position]);
																								adapter.addItem(visibilityOrder_ListAdapter.TYPE_MULTIPAGE_END,groupName);
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
																						public void onPositiveClick(Bundle resultBundle)
																						{
																								// TODO: Implement this method
																						}

																						@Override
																						public void onTouchOutside()
																						{
																								// TODO: Implement this method
																						}
																				});
																		dialogFragment.setList(ListView.CHOICE_MODE_NONE,PreferencesVisibilityOrderFragment.PowerMenuItemsTexts,0,true);
																		dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[4]);
																		dialogFragment.showDialog(R.id.dialog_container);
																}
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
														public void onPositiveClick(Bundle resultBundle)
														{
																// TODO: Implement this method
														}

														@Override
														public void onTouchOutside()
														{
																// TODO: Implement this method
														}
												});
										dialogFragment.setList(ListView.CHOICE_MODE_NONE,getString(R.string.visibilityOrder_AddItem).split("\\|"), -1, true);
										dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[4]);
										dialogFragment.showDialog(R.id.dialog_container);
								}
						});
						
				/*TextView_AddNormalItem.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										slideDownDialogFragment dialogFragment = new slideDownDialogFragment(getActivity(), MainActivity.fragmentManager);
										dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

														@Override
														public void onListItemClick(int position, String text)
														{
																// TODO: Implement this method
																adapter.addItem(visibilityOrder_ListAdapter.TYPE_NORMAL,PowerMenuItems[position]);
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
														public void onPositiveClick(Bundle resultBundle)
														{
																// TODO: Implement this method
														}

														@Override
														public void onTouchOutside()
														{
																// TODO: Implement this method
														}
												});
										dialogFragment.setList(ListView.CHOICE_MODE_NONE,PreferencesVisibilityOrderFragment.PowerMenuItemsTexts,0,true);
										dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[4]);
										dialogFragment.showDialog(R.id.dialog_container);
								}
						});
						
				TextView_AddMultiItem.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										slideDownDialogFragment dialogFragment = new slideDownDialogFragment(getActivity(), MainActivity.fragmentManager);
										dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

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
														public void onPositiveClick(Bundle resultBundle)
														{
																// TODO: Implement this method
																String[] split = resultBundle.getString(slideDownDialogFragment.RESULT_LIST).split(",");
																String splitStr = "";
																int type = visibilityOrder_ListAdapter.TYPE_MULTI;
																for(int i = 0;i < split.length;i++) {
																		splitStr = splitStr + PowerMenuItems[Integer.parseInt(split[i])];
																		splitStr = splitStr + (i >= split.length ? "" : "|");
																}
																//Toast.makeText(getActivity(),"Adding "+split.length+" items: "+splitStr,Toast.LENGTH_LONG).show();
																adapter.addItem(type,splitStr);
														}

														@Override
														public void onTouchOutside()
														{
																// TODO: Implement this method
														}
												});
												dialogFragment.setText(getString(R.string.visibilityOrder_SelectMulti));
										dialogFragment.setList(ListView.CHOICE_MODE_MULTIPLE,PreferencesVisibilityOrderFragment.PowerMenuItemsTexts,-1,false);
										dialogFragment.setListLimit(3,false);
										dialogFragment.setNegativeButton(getString(R.string.Dialog_Buttons).split("\\|")[4]);
										dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[0]);
										dialogFragment.showDialog(R.id.dialog_container);
								}
						});*/
				
						MainActivity.actionbar.hideButton();
						
				return InflatedView;
		}
		
}

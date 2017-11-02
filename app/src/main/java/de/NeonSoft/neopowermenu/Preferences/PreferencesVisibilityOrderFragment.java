package de.NeonSoft.neopowermenu.Preferences;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.*;
import android.support.v4.app.*;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.DSLV.*;
import de.NeonSoft.neopowermenu.helpers.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class PreferencesVisibilityOrderFragment extends Fragment {

    public static Activity mContext;
    public static ShortcutHandler mShortcutHandler;

    PackageManager pm;

    public static slideDownDialogFragment loadDialog;

    public static boolean appsListFullyParsed;
    static ArrayList<String> appsNames = new ArrayList<String>();
    static ArrayList<String> appsPackages = new ArrayList<String>();
    static ArrayList<String> appsNamesFiltered = new ArrayList<String>();
    static ArrayList<String> appsPackagesFiltered = new ArrayList<String>();
    public static AsyncTask loadAppsTask;

    public static final int REQ_OBTAIN_SHORTCUT = 1028;
    static ArrayList<String> shortcutNames = new ArrayList<>();
    static ArrayList<ResolveInfo> shortcutPackages = new ArrayList<>();
    static ArrayList<String> shortcutsNamesFiltered = new ArrayList<String>();
    static ArrayList<ResolveInfo> shortcutsPackagesFiltered = new ArrayList<>();
    public static boolean shortcutsListFullyParsed;
    public static AsyncTask loadShortcutsTask;

    LinearLayout LinearLayout_Add;

    DragSortListView DSLV_List;
    public static visibilityOrder_ListAdapter adapter;

    public static LinearLayout LinearLayout_Progress;

    public static ArrayList<String> FilteredPowerMenuItems = new ArrayList<>();
    public static ArrayList<String> FilteredPowerMenuItemsTexts = new ArrayList<>();
    public static ArrayList<Boolean> FilteredCheckedItems = new ArrayList<>();
    public static ArrayList<String> SelectedPowerMenuItems = new ArrayList<>();
    public static ArrayList<String> SelectedPowerMenuItemsTexts = new ArrayList<>();
    public static String[] PowerMenuItems = new String[]{
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
            "SoundSilent",
            "KillApp",
            "AppShortcut",
            "ToggleRotate",
            "MediaPrevious",
            "MediaPlayPause",
            "MediaNext",
            "ToggleWifi",
            "ToggleBluetooth",
            "ToggleData",
            "RebootFlashMode",
            "LockPhone",
            "SilentMode",
            "Shortcut"};
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
                        return 0.25f;//((float) adapter.getCount()) / 0.001f;
                    } else {
                        return 10.0f * w;
                    }
                }
            };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (!MainActivity.visibleFragment.equalsIgnoreCase("tour")) {
            MainActivity.visibleFragment = "VisibilityOrder";
        }

        mContext = getActivity();

        pm = mContext.getPackageManager();

        MainActivity.actionbar.setTitle(getString(R.string.preferences_VisibilityOrder).split("\\|")[0]);
        MainActivity.actionbar.setSubTitle(getString(R.string.preferences_VisibilityOrder).split("\\|")[1]);

        PowerMenuItemsTexts = new String[]{
                getString(R.string.powerMenuMain_Empty),
                getString(R.string.powerMenuMain_Shutdown),
                getString(R.string.powerMenuMain_Reboot),
                getString(R.string.powerMenuMain_SoftReboot),
                getString(R.string.powerMenuMain_Screenshot),
                getString(R.string.powerMenuMain_Screenrecord),
                getString(R.string.powerMenuMain_Flashlight),
                getString(R.string.powerMenuMain_ExpandedDesktop) + " (GravityBox)",
                getString(R.string.powerMenuMain_AirplaneMode),
                getString(R.string.powerMenuMain_RestartUI),
                getString(R.string.powerMenuMain_SoundMode),
                getString(R.string.powerMenuBottom_Recovery),
                getString(R.string.powerMenuBottom_Bootloader),
                getString(R.string.powerMenuBottom_SafeMode),
                getString(R.string.powerMenuMain_SoundVibrate),
                getString(R.string.powerMenuMain_SoundNormal),
                getString(R.string.powerMenuMain_SoundSilent),
                getString(R.string.powerMenuMain_KillApp),
                getString(R.string.powerMenuMain_AppShortcut),
                getString(R.string.powerMenuMain_ToggleRotate),
                getString(R.string.powerMenuMain_MediaPrevious),
                getString(R.string.powerMenuMain_MediaPlayPause),
                getString(R.string.powerMenuMain_MediaNext),
                getString(R.string.powerMenuMain_ToggleWifi),
                getString(R.string.powerMenuMain_ToggleBluetooth),
                getString(R.string.powerMenuMain_ToggleData),
                getString(R.string.powerMenuMain_RebootFlashMode),
                getString(R.string.powerMenuMain_LockPhone),
                getString(R.string.powerMenuMain_SilentMode),
                getString(R.string.powerMenuMain_Shortcut)};

        View InflatedView = inflater.inflate(R.layout.visibilityorder, container, false);

        LinearLayout_Add = (LinearLayout) InflatedView.findViewById(R.id.visibilityorderLinearLayout_Add);

        DSLV_List = (DragSortListView) InflatedView.findViewById(R.id.visibilityorderDSLV_List);

        DSLV_List.setDropListener(onDrop);
        DSLV_List.setRemoveListener(onRemove);
        DSLV_List.setDragEnabled(true);
        DSLV_List.setDragScrollProfile(ssProfile);
        DSLV_List.setFastScrollEnabled(true);

        ArrayList<MenuItemHolder> items = new ArrayList<>();
        ArrayList<String> MultiPage = new ArrayList<>();
        for (int i = 0; i < MainActivity.orderPrefs.getAll().keySet().size(); i++) {
            MenuItemHolder item = new MenuItemHolder();
            if (MainActivity.orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", -1) != -1) {
                item.setLockedWithPassword((!MainActivity.preferences.getString(PreferenceNames.pItemPWL, "").isEmpty() && MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_lockedWithPassword", false)));
                item.setHideText((!MainActivity.preferences.getString(PreferenceNames.pItemPWL, "").isEmpty() && MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideText", false)));
                if (MainActivity.orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", visibilityOrder_ListAdapter.TYPE_NORMAL) == visibilityOrder_ListAdapter.TYPE_NORMAL) {
                    item.setType(visibilityOrder_ListAdapter.TYPE_NORMAL);
                    item.setHideDesc(MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideDesc", false));
                    item.setHideOnLockScreen(MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideOnLockscreen", false));
                    item.setFillEmpty(MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_fillEmpty", false));
                    item.setTitle(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", ""), "","");
                    item.setText(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_text", ""),"","");
                    item.setShortcutUri(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_shortcutUri", ""),"","");
                } else if (MainActivity.orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", visibilityOrder_ListAdapter.TYPE_NORMAL) == visibilityOrder_ListAdapter.TYPE_MULTI) {
                    item.setType(visibilityOrder_ListAdapter.TYPE_MULTI);
                    item.setHideDesc(MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideDesc", false));
                    item.setHideOnLockScreen(MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideOnLockscreen", false));
                    item.setFillEmpty(MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_fillEmpty", false));
                    item.setTitle(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item1_title", ""),
                            MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item2_title", ""),
                            MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item3_title", ""));
                    item.setText(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item1_text", "").replace("< default >", ""),
                            MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item2_text", "").replace("< default >", ""),
                            MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item3_text", "").replace("< default >", ""));
                    item.setShortcutUri(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item1_shortcutUri", ""),
                            MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item2_shortcutUri", ""),
                            MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item3_shortcutUri", ""));
                } else if (MainActivity.orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", -1) == visibilityOrder_ListAdapter.TYPE_MULTIPAGE_START) {
                    item.setType(visibilityOrder_ListAdapter.TYPE_MULTIPAGE_START);
                    item.setHideDesc(MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideDesc", false));
                    item.setHideOnLockScreen(MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideOnLockscreen", false));
                    item.setFillEmpty(MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_fillEmpty", false));
                    item.setTitle(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", ""),"","");
                    item.setText("","","");
                    MultiPage.add(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", ""));
                } else if (MultiPage.size() > 0 && MainActivity.orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", -1) == visibilityOrder_ListAdapter.TYPE_MULTIPAGE_END) {
                    item.setType(visibilityOrder_ListAdapter.TYPE_MULTIPAGE_END);
                    item.setHideDesc(MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideDesc", false));
                    item.setHideOnLockScreen(MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideOnLockscreen", false));
                    item.setFillEmpty(MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_fillEmpty", false));
                    item.setTitle(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", ""),"","");
                    item.setText("","","");
                    MultiPage.remove(MultiPage.size() - 1);
                }
            }
            if (item.getType() != -1) {
                items.add(item);
            }
        }
        adapter = new visibilityOrder_ListAdapter(getActivity(), items);

        DSLV_List.setAdapter(adapter);

        LinearLayout_Progress = (LinearLayout) InflatedView.findViewById(R.id.activityvisibilityorderLinearLayout_Saving);
        LinearLayout_Progress.setVisibility(View.GONE);

        LinearLayout_Progress.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {

            }
        });

        LinearLayout_Add.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                dialogFragment.setContext(getActivity());
                dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

                    @Override
                    public void onListItemClick(int position, String text) {

                        if (position == 0) {
                            final slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                            dialogFragment.setContext(getActivity());
                            dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                            dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

                                @Override
                                public void onListItemClick(int position, String text) {
                                    String[] FinalPowerMenuItems;
                                    if (FilteredPowerMenuItems.isEmpty()) {
                                        FinalPowerMenuItems = PowerMenuItems;
                                    } else {
                                        FinalPowerMenuItems = FilteredPowerMenuItems.toArray(new String[]{});
                                    }
                                    if (FinalPowerMenuItems[position].equals("AppShortcut")) {
                                        showLoadingDialog("apps");
                                        MenuItemHolder item = new MenuItemHolder();
                                        item.setType(visibilityOrder_ListAdapter.TYPE_NORMAL);
                                        loadAppsTask = new loadApps().execute(item, -1);
                                    } else if (FinalPowerMenuItems[position].equals("Shortcut")) {
                                        showLoadingDialog("shortcuts");
                                        MenuItemHolder item = new MenuItemHolder();
                                        item.setType(visibilityOrder_ListAdapter.TYPE_NORMAL);
                                        loadShortcutsTask = new loadShortcuts().execute(item, -1);
                                    } else {
                                        MenuItemHolder item = new MenuItemHolder();
                                        item.setType(visibilityOrder_ListAdapter.TYPE_NORMAL);
                                        item.setTitle(FinalPowerMenuItems[position], "","");
                                        item.setText("","","");
                                        if (FinalPowerMenuItems[position].equalsIgnoreCase(PowerMenuItems[7])) {
                                            if (!helper.isAppInstalled(mContext, "com.ceco.gm2.gravitybox") && !helper.isAppInstalled(mContext, "com.ceco.kitkat.gravitybox") && !helper.isAppInstalled(mContext, "com.ceco.lollipop.gravitybox") && !helper.isAppInstalled(mContext, "com.ceco.marshmallow.gravitybox") && !helper.isAppInstalled(mContext, "com.ceco.nougat.gravitybox")) {

                                                slideDownDialogFragment NoGravityBox = new slideDownDialogFragment();
                                                NoGravityBox.setContext(getActivity());
                                                NoGravityBox.setFragmentManager(MainActivity.fragmentManager);
                                                NoGravityBox.setText(getString(R.string.visibilityOrder_NoGravityBoxFound));
                                                NoGravityBox.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[0]);
                                                NoGravityBox.showDialog(R.id.dialog_container);
                                            }
                                        }
                                        adapter.addItem(item);
                                    }
                                }

                                @Override
                                public void onNegativeClick() {

                                }

                                @Override
                                public void onNeutralClick() {

                                }

                                @Override
                                public void onPositiveClick(Bundle resultBundle) {

                                }

                                @Override
                                public void onTouchOutside() {

                                }
                            });
                            dialogFragment.addInput(getString(R.string.visibilityOrder_FilterItemsList), "", true, new TextWatcher() {

                                @Override
                                public void afterTextChanged(Editable p1) {

                                }

                                @Override
                                public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {

                                }

                                @Override
                                public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
                                    FilteredPowerMenuItems.clear();
                                    FilteredPowerMenuItemsTexts.clear();
                                    if (p1.toString().isEmpty()) {
                                        dialogFragment.setList(ListView.CHOICE_MODE_NONE, PowerMenuItemsTexts, -1, true);
                                    } else {
                                        for (int x = 0; x < PowerMenuItems.length; x++) {
                                            if (PowerMenuItemsTexts[x].toLowerCase().contains(p1.toString().toLowerCase())) {
                                                FilteredPowerMenuItems.add(PowerMenuItems[x]);
                                                FilteredPowerMenuItemsTexts.add(PowerMenuItemsTexts[x]);
                                            }
                                        }
                                        dialogFragment.setList(ListView.CHOICE_MODE_NONE, FilteredPowerMenuItemsTexts, -1, true);
                                    }
                                }
                            });
                            dialogFragment.setList(ListView.CHOICE_MODE_NONE, PowerMenuItemsTexts, 0, true);
                            dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[4]);
                            dialogFragment.showDialog(R.id.dialog_container);
                        } else if (position == 1) {
                            final ArrayList<String> AddPowerMenuItems = new ArrayList<>();
                            final slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                            dialogFragment.setContext(getActivity());
                            dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                            dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

                                @Override
                                public void onListItemClick(int position, String text) {
                                    if (FilteredPowerMenuItems.isEmpty()) {
                                        if (dialogFragment.getListItemChecked(position)) {
                                            AddPowerMenuItems.add(PowerMenuItems[position]);
                                        } else {
                                            AddPowerMenuItems.remove(PowerMenuItems[position]);
                                        }
                                    } else {
                                        if (dialogFragment.getListItemChecked(position)) {
                                            AddPowerMenuItems.add(FilteredPowerMenuItems.get(position));
                                        } else {
                                            AddPowerMenuItems.remove(FilteredPowerMenuItems.get(position));
                                        }
                                    }
                                }

                                @Override
                                public void onNegativeClick() {

                                }

                                @Override
                                public void onNeutralClick() {

                                }

                                @Override
                                public void onPositiveClick(Bundle resultBundle) {
                                    if (resultBundle != null) {
                                        MenuItemHolder item = new MenuItemHolder();
                                        item.setType(visibilityOrder_ListAdapter.TYPE_MULTI);
                                        item.setTitle(AddPowerMenuItems.get(0), (AddPowerMenuItems.size() > 1 ? AddPowerMenuItems.get(1) : ""), (AddPowerMenuItems.size() > 2 ? AddPowerMenuItems.get(2) : ""));
                                        item.setText("","","");
                                        adapter.addItem(item);
                                    }
                                }

                                @Override
                                public void onTouchOutside() {

                                }
                            });
                            dialogFragment.addInput(getString(R.string.visibilityOrder_FilterItemsList), "", true, new TextWatcher() {

                                @Override
                                public void afterTextChanged(Editable p1) {

                                }

                                @Override
                                public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {

                                }

                                @Override
                                public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
                                    FilteredPowerMenuItems.clear();
                                    FilteredPowerMenuItemsTexts.clear();
                                    FilteredCheckedItems.clear();
                                    if (p1.toString().isEmpty()) {
                                        for (int x = 0; x < PowerMenuItems.length; x++) {
                                            for (int z = 0; z < AddPowerMenuItems.size(); z++) {
                                                FilteredCheckedItems.add(false);
                                                if (AddPowerMenuItems.get(z).equalsIgnoreCase(PowerMenuItems[x])) {
                                                    FilteredCheckedItems.set(x, true);
                                                }
                                            }
                                        }
                                        dialogFragment.setList(ListView.CHOICE_MODE_MULTIPLE, PowerMenuItemsTexts, -1, false);
                                        dialogFragment.setListChecks(FilteredCheckedItems);
                                    } else {
                                        for (int x = 0; x < PowerMenuItems.length; x++) {
                                            if (PowerMenuItemsTexts[x].toLowerCase().contains(p1.toString().toLowerCase())) {
                                                FilteredPowerMenuItems.add(PowerMenuItems[x]);
                                                FilteredPowerMenuItemsTexts.add(PowerMenuItemsTexts[x]);
                                                FilteredCheckedItems.add(false);
                                                for (int z = 0; z < AddPowerMenuItems.size(); z++) {
                                                    if (AddPowerMenuItems.get(z).equalsIgnoreCase(PowerMenuItems[x])) {
                                                        FilteredCheckedItems.set(FilteredCheckedItems.size() - 1, true);
                                                    }
                                                }
                                            }
                                        }
                                        dialogFragment.setList(ListView.CHOICE_MODE_MULTIPLE, FilteredPowerMenuItemsTexts, -1, false);
                                        dialogFragment.setListChecks(FilteredCheckedItems);
                                    }
                                }
                            });
                            //dialogFragment.setText(getString(R.string.visibilityOrder_SelectMulti));
                            dialogFragment.setList(ListView.CHOICE_MODE_MULTIPLE, PreferencesVisibilityOrderFragment.PowerMenuItemsTexts, -1, false);
                            dialogFragment.setListLimit(3, false);
                            dialogFragment.setNegativeButton(getString(R.string.Dialog_Buttons).split("\\|")[4]);
                            dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[0]);
                            dialogFragment.showDialog(R.id.dialog_container);
                        } else if (position == 2) {
                            final slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                            dialogFragment.setContext(getActivity());
                            dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                            dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

                                @Override
                                public void onListItemClick(int position, String text) {

                                    String[] FinalPowerMenuItems;
                                    if (FilteredPowerMenuItems.isEmpty()) {
                                        FinalPowerMenuItems = PowerMenuItems;
                                    } else {
                                        FinalPowerMenuItems = FilteredPowerMenuItems.toArray(new String[]{});
                                    }
                                    Date date = new Date();
                                    String groupName = helper.md5Crypto(date.getDay() + "." + date.getMonth() + "." + date.getYear() + "/" + date.getHours() + ":" + date.getMinutes() + ":" + date.getMinutes() + ":" + date.getSeconds());
                                    MenuItemHolder item = new MenuItemHolder();
                                    item.setType(visibilityOrder_ListAdapter.TYPE_MULTIPAGE_START);
                                    item.setTitle(groupName,"","");
                                    adapter.addItem(item);
                                    item = new MenuItemHolder();
                                    item.setType(visibilityOrder_ListAdapter.TYPE_NORMAL);
                                    item.setTitle(FinalPowerMenuItems[position],"","");
                                    adapter.addItem(item);
                                    item = new MenuItemHolder();
                                    item.setType(visibilityOrder_ListAdapter.TYPE_MULTIPAGE_END);
                                    item.setTitle(groupName,"","");
                                    adapter.addItem(item);
                                }

                                @Override
                                public void onNegativeClick() {

                                }

                                @Override
                                public void onNeutralClick() {

                                }

                                @Override
                                public void onPositiveClick(Bundle resultBundle) {

                                }

                                @Override
                                public void onTouchOutside() {

                                }
                            });
                            dialogFragment.addInput(getString(R.string.visibilityOrder_FilterItemsList), "", true, new TextWatcher() {

                                @Override
                                public void afterTextChanged(Editable p1) {

                                }

                                @Override
                                public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {

                                }

                                @Override
                                public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
                                    FilteredPowerMenuItems.clear();
                                    FilteredPowerMenuItemsTexts.clear();
                                    if (p1.toString().isEmpty()) {
                                        dialogFragment.setList(ListView.CHOICE_MODE_NONE, PowerMenuItemsTexts, -1, true);
                                    } else {
                                        for (int x = 0; x < PowerMenuItems.length; x++) {
                                            if (PowerMenuItemsTexts[x].toLowerCase().contains(p1.toString().toLowerCase())) {
                                                FilteredPowerMenuItems.add(PowerMenuItems[x]);
                                                FilteredPowerMenuItemsTexts.add(PowerMenuItemsTexts[x]);
                                            }
                                        }
                                        dialogFragment.setList(ListView.CHOICE_MODE_NONE, FilteredPowerMenuItemsTexts, -1, true);
                                    }
                                }
                            });
                            dialogFragment.setList(ListView.CHOICE_MODE_NONE, PreferencesVisibilityOrderFragment.PowerMenuItemsTexts, 0, true);
                            dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[4]);
                            dialogFragment.showDialog(R.id.dialog_container);
                        }
                    }

                    @Override
                    public void onNegativeClick() {

                    }

                    @Override
                    public void onNeutralClick() {

                    }

                    @Override
                    public void onPositiveClick(Bundle resultBundle) {

                    }

                    @Override
                    public void onTouchOutside() {

                    }
                });
                dialogFragment.setList(ListView.CHOICE_MODE_NONE, getString(R.string.visibilityOrder_AddItem).split("\\|"), -1, true);
                dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[4]);
                dialogFragment.showDialog(R.id.dialog_container);
            }
        });
        if (!MainActivity.visibleFragment.equalsIgnoreCase("tour")) {
            //MainActivity.actionbar.hideButton();
        }

        return InflatedView;
    }

    public static void showLoadingDialog(final String mode) {
        if ((mode.equalsIgnoreCase("shortcuts") && !shortcutsListFullyParsed) || (mode.equalsIgnoreCase("apps") && !appsListFullyParsed)) {
            loadDialog = new slideDownDialogFragment();
            loadDialog.setContext(mContext);
            loadDialog.setFragmentManager(MainActivity.fragmentManager);
            loadDialog.setListener(new slideDownDialogFragment.slideDownDialogInterface() {
                @Override
                public void onListItemClick(int position, String text) {

                }

                @Override
                public void onNegativeClick() {

                }

                @Override
                public void onNeutralClick() {

                }

                @Override
                public void onPositiveClick(Bundle resultBundle) {
                    if (mode.equalsIgnoreCase("shortcuts")) {
                        loadShortcutsTask.cancel(true);
                    } else {
                        loadAppsTask.cancel(true);
                    }
                    loadDialog.setPositiveButton("");
                }

                @Override
                public void onTouchOutside() {

                }
            });
            loadDialog.setCloseOnTouchOutside(false);
            loadDialog.setCloseOnButtonClick(false);
            loadDialog.setText(mContext.getString(R.string.login_Processing));
            loadDialog.addProgressBar(true, false);
            loadDialog.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
            loadDialog.showDialog(R.id.dialog_container);
        } else {
            loadDialog = null;
        }
    }

    public static class loadApps extends AsyncTask<Object, String, String> {

        PackageManager pm;
        MenuItemHolder item;
        //int type;
        int rechoice = -1;
        String result;

        @Override
        protected void onPreExecute() {
            appsPackages.clear();
            appsNames.clear();
            pm = mContext.getPackageManager();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Object... strings) {
            item = (MenuItemHolder) strings[0];
            result = item.getTitle(1) + "|" + item.getTitle(2) + "|" + item.getTitle(3);
            rechoice = (int) strings[1];
            //get a list of installed apps.
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            List<ResolveInfo> packages = pm.queryIntentActivities(intent, 0);
            Collections.sort(packages, new Comparator<ResolveInfo>() {
                @Override
                public int compare(ResolveInfo resolveInfo, ResolveInfo t1) {
                    return resolveInfo.loadLabel(pm).toString().compareToIgnoreCase(t1.loadLabel(pm).toString());
                }
            });

            //List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

            int i = 0;
            for (ResolveInfo packageInfo : packages) {
                if (isCancelled()) {
                    return "canceled";
                }
                i++;
                if (loadDialog != null)
                    loadDialog.setProgressBar((i * 100) / packages.size());
                if (!packageInfo.activityInfo.packageName.equals(packageInfo.loadLabel(pm).toString())) {
                    appsPackages.add(packageInfo.activityInfo.packageName + "/" + packageInfo.activityInfo.name);
                    appsNames.add(packageInfo.loadLabel(pm).toString());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null) {
                appsListFullyParsed = true;
                if (loadDialog != null) loadDialog.cancelDialog();
                final slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                dialogFragment.setContext(mContext);
                dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {
                    @Override
                    public void onListItemClick(int position, String text) {

                    }

                    @Override
                    public void onNegativeClick() {

                    }

                    @Override
                    public void onNeutralClick() {

                    }

                    @Override
                    public void onPositiveClick(Bundle resultBundle) {
                        String string;
                        if (item.getType() == visibilityOrder_ListAdapter.TYPE_NORMAL) {
                            if (appsPackagesFiltered.isEmpty()) {
                                string = appsPackages.get(resultBundle.getInt(slideDownDialogFragment.RESULT_LIST));
                            } else {
                                string = appsPackagesFiltered.get(resultBundle.getInt(slideDownDialogFragment.RESULT_LIST));
                            }
                        } else {
                            if (appsPackagesFiltered.isEmpty()) {
                                string = result.replace("[THIS]", appsPackages.get(resultBundle.getInt(slideDownDialogFragment.RESULT_LIST)));
                            } else {
                                string = result.replace("[THIS]", appsPackagesFiltered.get(resultBundle.getInt(slideDownDialogFragment.RESULT_LIST)));
                            }
                        }
                        MenuItemHolder newItem = new MenuItemHolder();
                        newItem.setType(item.getType());
                        if (item.getType() == visibilityOrder_ListAdapter.TYPE_NORMAL) {
                            newItem.setTitle(string,"","");
                        } else {
                            newItem.setTitle(string.split("\\|")[0],string.split("\\|")[1],string.split("\\|")[2]);
                        }
                        newItem.setHideDesc(false);
                        newItem.setHideOnLockScreen(false);
                        newItem.setText( "","","");
                        if (rechoice != -1) {
                            adapter.removeAt(rechoice);
                            adapter.insertAt(rechoice, newItem);
                        } else {
                            adapter.addItem(newItem);
                        }
                    }

                    @Override
                    public void onTouchOutside() {

                    }
                });
                dialogFragment.addInput(mContext.getString(R.string.visibilityOrder_SearchApp), "", true, new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        appsNamesFiltered.clear();
                        appsPackagesFiltered.clear();
                        if (charSequence.toString().isEmpty()) {
                            dialogFragment.setList(ListView.CHOICE_MODE_SINGLE, appsNames, -1, false);
                        } else {
                            for (int x = 0; x < appsNames.size(); x++) {
                                if (appsNames.get(x).toLowerCase().contains(charSequence.toString().toLowerCase())) {
                                    appsNamesFiltered.add(appsNames.get(x));
                                    appsPackagesFiltered.add(appsPackages.get(x));
                                }
                            }
                            dialogFragment.setList(ListView.CHOICE_MODE_SINGLE, appsNamesFiltered, -1, false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                dialogFragment.setList(ListView.CHOICE_MODE_SINGLE, appsNames, -1, false);
                dialogFragment.setNegativeButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_OK]);
                dialogFragment.showDialog(R.id.dialog_container);
            } else if (s.equalsIgnoreCase("canceled")) {
                appsListFullyParsed = false;
                appsNames.clear();
                appsPackages.clear();
            }
        }

    }

    public static class loadShortcuts extends AsyncTask<Object, String, String> {

        PackageManager pm;
        MenuItemHolder item;
        //int type;
        int ThisEditID;
        int rechoice = -1;
        String result;

        @Override
        protected void onPreExecute() {
            shortcutPackages.clear();
            shortcutNames.clear();
            pm = mContext.getPackageManager();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Object... strings) {
            item = (MenuItemHolder) strings[0];
            result = item.getTitle(1) + "|" + item.getTitle(2) + "|" +item.getTitle(3);
            rechoice = (int) strings[1];

            List<PackageInfo> packages = pm.getInstalledPackages(0);
            List<ResolveInfo> shortcuts = new ArrayList<>();
            Intent mainIntent = new Intent();
            mainIntent.setAction(Intent.ACTION_CREATE_SHORTCUT);
            for(PackageInfo pi : packages) {
                if (this.isCancelled()) break;
                if (pi.packageName.equals(mContext.getPackageName()))
                    continue;
                mainIntent.setPackage(pi.packageName);
                List<ResolveInfo> activityList = pm.queryIntentActivities(mainIntent, 0);
                for(ResolveInfo ri : activityList) {
                    shortcuts.add(ri);
                }
            }
            //get a list of installed shortcuts.
            //Intent shortcutsIntent = new Intent();
            //shortcutsIntent.setAction(Intent.ACTION_CREATE_SHORTCUT);
            //List<ResolveInfo> shortcuts = pm.queryIntentActivities(shortcutsIntent, 0);
            Collections.sort(shortcuts, new Comparator<ResolveInfo>() {
                @Override
                public int compare(ResolveInfo resolveInfo, ResolveInfo t1) {
                    return resolveInfo.loadLabel(pm).toString().compareToIgnoreCase(t1.loadLabel(pm).toString());
                }
            });

            //List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

            int i = 0;
            for (ResolveInfo packageInfo : shortcuts) {
                if (isCancelled()) {
                    return "canceled";
                }
                i++;
                if (loadDialog != null)
                    loadDialog.setProgressBar((i * 100) / shortcuts.size());
                //if (!packageInfo.activityInfo.packageName.equals(packageInfo.loadLabel(pm).toString())) {
                    //appsPackages.add(packageInfo.activityInfo.packageName + "/" + packageInfo.activityInfo.name);
                shortcutPackages.add(packageInfo);
                shortcutNames.add(packageInfo.loadLabel(pm).toString());
                //Log.d("NPM:sL", "Adding " + packageInfo.loadLabel(pm) + " to the list.");
                //}
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null) {
                shortcutsListFullyParsed = true;
                if (loadDialog != null) loadDialog.cancelDialog();
                final slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                dialogFragment.setContext(mContext);
                dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {
                    @Override
                    public void onListItemClick(int position, String text) {

                    }

                    @Override
                    public void onNegativeClick() {

                    }

                    @Override
                    public void onNeutralClick() {

                    }

                    @Override
                    public void onPositiveClick(final Bundle resultBundle) {
                        final ResolveInfo thisRI;
                        if (item.getType() == visibilityOrder_ListAdapter.TYPE_NORMAL) {
                            if (shortcutsNamesFiltered.isEmpty()) {
                                thisRI = shortcutPackages.get(resultBundle.getInt(slideDownDialogFragment.RESULT_LIST));
                            } else {
                                thisRI = shortcutsPackagesFiltered.get(resultBundle.getInt(slideDownDialogFragment.RESULT_LIST));
                            }
                        } else {
                            String[] split = result.split("\\|");
                            for (int i = 0;  i < split.length; i ++) {
                                if (split[i].equalsIgnoreCase("[THIS]")) {
                                    ThisEditID = i+1;
                                }
                            }
                            if (shortcutsNamesFiltered.isEmpty()) {
                                thisRI = shortcutPackages.get(resultBundle.getInt(slideDownDialogFragment.RESULT_LIST));
                            } else {
                                thisRI = shortcutsPackagesFiltered.get(resultBundle.getInt(slideDownDialogFragment.RESULT_LIST));
                            }
                        }
                        ShortcutItem si = new ShortcutItem(item.getTitle(ThisEditID), thisRI);
                        if (si.getCreateShortcutIntent() == null) {
                            Toast.makeText(mContext, "ShortcutIntent is null", Toast.LENGTH_SHORT).show();
                            //setValue(null);
                            //getDialog().dismiss();
                        } else {
                            si.setShortcutCreatedListener(new ShortcutCreatedListener() {
                                @Override
                                public void onShortcutCreated(ShortcutItem sir) {
                                    MenuItemHolder newItem = new MenuItemHolder();
                                    newItem.setType(item.getType());
                                    if (item.getType() == visibilityOrder_ListAdapter.TYPE_NORMAL) {
                                        newItem.setTitle(sir.getIntent().getStringExtra("label"),"","");
                                        newItem.setShortcutUri(sir.getIntent().toUri(Intent.URI_INTENT_SCHEME), "","");
                                    } else {
                                        newItem.setTitle((ThisEditID == 1 ? sir.getIntent().getStringExtra("label") : item.getTitle(1)), (ThisEditID == 2 ? sir.getIntent().getStringExtra("label") : item.getTitle(2)), (ThisEditID == 3 ? sir.getIntent().getStringExtra("label") : item.getTitle(3)));
                                        newItem.setShortcutUri((ThisEditID == 1 ? sir.getIntent().toUri(Intent.URI_INTENT_SCHEME) : item.getShortcutUri(1)), (ThisEditID == 2 ? sir.getIntent().toUri(Intent.URI_INTENT_SCHEME) : item.getShortcutUri(2)), (ThisEditID == 3 ? sir.getIntent().toUri(Intent.URI_INTENT_SCHEME) : item.getShortcutUri(3)));
                                    }
                                    newItem.setHideDesc(false);
                                    newItem.setHideOnLockScreen(false);
                                    newItem.setText( item.getText(1), item.getText(2), item.getText(3));
                                    if (rechoice != -1) {
                                        adapter.removeAt(rechoice);
                                        adapter.insertAt(rechoice, newItem);
                                    } else {
                                        adapter.addItem(newItem);
                                    }
                                    //setValue(sir.getValue());
                                    // we have to call this explicitly for some yet unknown reason...
                                    //sPrefsFragment.onSharedPreferenceChanged(
                                    //        SettingsManager.getInstance(mContext).getMainPrefs(), getKey());
                                    //getDialog().dismiss();
                                }
                            });
                            obtainShortcut(si);
                        }
                        /*MenuItemHolder newItem = new MenuItemHolder();
                        newItem.setType(item.getType());
                        newItem.setTitle(string);
                        newItem.setHideDesc(false);
                        newItem.setHideOnLockScreen(false);
                        newItem.setText((item.getType() == visibilityOrder_ListAdapter.TYPE_NORMAL ? "" : "< default >|< default >|< default >"));
                        if (rechoice != -1) {
                            adapter.removeAt(rechoice);
                            adapter.insertAt(rechoice, newItem);
                        } else {
                            adapter.addItem(newItem);
                        }*/
                    }

                    @Override
                    public void onTouchOutside() {

                    }
                });
                dialogFragment.addInput(mContext.getString(R.string.visibilityOrder_SearchShortcut), "", true, new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        shortcutsNamesFiltered.clear();
                        shortcutsPackagesFiltered.clear();
                        if (charSequence.toString().isEmpty()) {
                            dialogFragment.setList(ListView.CHOICE_MODE_SINGLE, shortcutNames, -1, false);
                        } else {
                            for (int x = 0; x < shortcutNames.size(); x++) {
                                if (shortcutNames.get(x).toLowerCase().contains(charSequence.toString().toLowerCase())) {
                                    shortcutsNamesFiltered.add(shortcutNames.get(x));
                                    shortcutsPackagesFiltered.add(shortcutsPackagesFiltered.get(x));
                                }
                            }
                            dialogFragment.setList(ListView.CHOICE_MODE_SINGLE, shortcutsNamesFiltered, -1, false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                dialogFragment.setList(ListView.CHOICE_MODE_SINGLE, shortcutNames, -1, false);
                dialogFragment.setNegativeButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_OK]);
                dialogFragment.showDialog(R.id.dialog_container);
            } else if (s.equalsIgnoreCase("canceled")) {
                shortcutsListFullyParsed = false;
                shortcutNames.clear();
            }
        }
    }

    public static void obtainShortcut(ShortcutHandler handler) {
        if (handler == null) return;

        mShortcutHandler = handler;
        mContext.startActivityForResult(mShortcutHandler.getCreateShortcutIntent(), REQ_OBTAIN_SHORTCUT);
    }

    static class AppItem implements IIconListAdapterItem {
        protected String mAppName;
        protected BitmapDrawable mAppIcon;
        protected ResolveInfo mResolveInfo;
        protected Intent mIntent;

        private AppItem() { }

        public AppItem(String appName, ResolveInfo ri) {
            mAppName = appName;
            mResolveInfo = ri;
            if (mResolveInfo != null) {
                mIntent = new Intent(Intent.ACTION_MAIN);
                mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                ComponentName cn = new ComponentName(mResolveInfo.activityInfo.packageName,
                        mResolveInfo.activityInfo.name);
                mIntent.setComponent(cn);
            }
        }

        public String getAppName() {
            return mAppName;
        }

        public String getValue() {
            return (mIntent == null ? null : mIntent.toUri(0));
        }

        public Intent getIntent() {
            return mIntent;
        }

        @Override
        public String getText() {
            return mAppName;
        }

        @Override
        public String getSubText() {
            return null;
        }

        protected String getKey() {
            return getValue();
        }

        @Override
        public Drawable getIconLeft() {
            if (mResolveInfo == null) return null;

            if (mAppIcon == null) {
                final String key = getKey();
            }
            return mAppIcon;
        }

        @Override
        public Drawable getIconRight() {
            return null;
        }
    }

    interface ShortcutCreatedListener {
        void onShortcutCreated(ShortcutItem item);
    }
    public interface ShortcutHandler {
        Intent getCreateShortcutIntent();
        void onHandleShortcut(Intent intent, String name,
                              String localIconResName, Bitmap icon);
        void onShortcutCancelled();
    }

    static class ShortcutItem extends AppItem implements ShortcutHandler {
        private Intent mCreateShortcutIntent;
        private ShortcutCreatedListener mShortcutCreatedListener;

        public ShortcutItem(String appName, ResolveInfo ri) {
            mAppName = appName;
            mResolveInfo = ri;
            if (mResolveInfo != null) {
                mCreateShortcutIntent = new Intent(Intent.ACTION_CREATE_SHORTCUT);
                ComponentName cn = new ComponentName(mResolveInfo.activityInfo.packageName,
                        mResolveInfo.activityInfo.name);
                mCreateShortcutIntent.setComponent(cn);
                // mark intent so we can later identify it comes from NeoPowerMenu
                mCreateShortcutIntent.putExtra("neopowermenu", true);
            }
        }

        public void setShortcutCreatedListener(ShortcutCreatedListener listener) {
            mShortcutCreatedListener = listener;
        }

        @Override
        protected String getKey() {
            return mCreateShortcutIntent.toUri(0);
        }

        @Override
        public Intent getCreateShortcutIntent() {
            return mCreateShortcutIntent;
        }

        @Override
        public void onHandleShortcut(Intent intent, String name, String localIconResName, Bitmap icon) {
            if (intent == null) {
                Toast.makeText(mContext, "Failed to load, intent is null", Toast.LENGTH_LONG).show();
                return;
            }

            mIntent = intent;
            mIntent.putExtra("mode", 1);

            // generate label
            if (name != null) {
                mIntent.putExtra("label", name);
                mIntent.putExtra("prefLabel", mAppName + ": " + name);
            } else {
                mIntent.putExtra("label", mAppName);
                mIntent.putExtra("prefLabel", mAppName);
            }

            // process icon
            if (localIconResName != null) {
                mIntent.putExtra("iconResName", localIconResName);
            } else if (icon != null) {
                try {
                    final String dir = mContext.getFilesDir() + "/app_picker";
                    final String fileName = dir + "/" + name + ".png";
                    File d = new File(dir);
                    d.mkdirs();
                    d.setReadable(true, false);
                    d.setExecutable(true, false);
                    File f = new File(fileName);
                    FileOutputStream fos = new FileOutputStream(f);
                    final boolean iconSaved = icon.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    if (iconSaved) {
                        mIntent.putExtra("icon", f.getAbsolutePath());
                        f.setReadable(true, false);
                    }
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // callback to shortcut created listener if set
            if (mShortcutCreatedListener != null) {
                mShortcutCreatedListener.onShortcutCreated(this);
            }
        }

        @Override
        public void onShortcutCancelled() {
            Toast.makeText(mContext, "Shortcut creation cancelled", Toast.LENGTH_SHORT).show();
        }
    }

}

package de.NeonSoft.neopowermenu.Preferences;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.DSLV.*;
import de.NeonSoft.neopowermenu.helpers.*;
import de.NeonSoft.neopowermenu.xposed.XposedUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class PreferencesVisibilityOrderFragment extends Fragment {

    public static Activity mContext;
    public static int visibilityOrderPermissionRequest_Id = -1;
    public static int visibilityOrderPermissionRequest_ItemSpace = 1;
    public static int visibilityOrderPermissionRequest = 103;
    public static int visibilityOrderPermissionRequest_Count = 0;
    public static int AddItemMode_NEW = 1;
    public static int AddItemMode_REPLACE = 2;
    public static int AddItemMode_AddToMulti = 3;
    public static ShortcutHandler mShortcutHandler;

    static PackageManager pm;
    private static NotificationManager nm;
    private static DevicePolicyManager devicePolicyManager;

    public static slideDownDialogFragment appsLoadDialog;
    public static slideDownDialogFragment shortcutsLoadDialog;

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
    static ArrayList<ResolveInfo> shortcutPackagesFiltered = new ArrayList<>();
    public static boolean shortcutsListFullyParsed;
    public static AsyncTask loadShortcutsTask;

    LinearLayout LinearLayout_Add;

    public static DragSortListView DSLV_List;
    public static visibilityOrder_ListAdapter adapter;

    public static LinearLayout LinearLayout_Progress;

    public static ArrayList<String> FilteredPowerMenuItems = new ArrayList<>();
    public static ArrayList<String> FilteredPowerMenuItemsTexts = new ArrayList<>();
    public static ArrayList<Boolean> FilteredCheckedItems = new ArrayList<>();
    public static ArrayList<String> SelectedPowerMenuItems = new ArrayList<>();
    public static ArrayList<String> SelectedPowerMenuItemsTexts = new ArrayList<>();
    public static String[] PowerMenuItems = new String[]{
            "Empty", // 0
            "Shutdown", // 1
            "Reboot", // 2
            "SoftReboot", // 3
            "Screenshot", // 4
            "Screenrecord", // 5
            "Flashlight", // 6
            "ExpandedDesktop", // 7
            "AirplaneMode", // 8
            "RestartUI", // 9
            "SoundMode", // 10
            "Recovery", // 11
            "Bootloader", // 12
            "SafeMode", // 13
            "SoundVibrate", // 14
            "SoundNormal", // 15
            "SoundSilent", // 16
            "KillApp", // 17
            "AppShortcut", // 18
            "ToggleRotate", // 19
            "MediaPrevious", // 20
            "MediaPlayPause", // 21
            "MediaNext", // 22
            "ToggleWifi", // 23
            "ToggleBluetooth", // 24
            "ToggleData", // 25
            "RebootFlashMode", // 26
            "LockPhone", // 27
            "SilentMode", // 28
            "Shortcut"}; //, //29
            // "FakePowerOff"}; // 30
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
        nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        devicePolicyManager = (DevicePolicyManager) mContext.getSystemService(Context.DEVICE_POLICY_SERVICE);

        MainActivity.actionbar.setTitle(getString(R.string.preferences_VisibilityOrderTitle));
        MainActivity.actionbar.setSubTitle(getString(R.string.preferences_VisibilityOrderDesc));

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
                getString(R.string.powerMenuMain_Shortcut)}; //,
                // getString(R.string.powerMenuMain_FakePowerOff)};

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
            ArrayList<String> titles = new ArrayList<>();
            ArrayList<String> texts = new ArrayList<>();
            ArrayList<String> uris = new ArrayList<>();
            if (MainActivity.orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", -1) != -1) {
                item.setLockedWithPassword((!MainActivity.preferences.getString(PreferenceNames.pItemPWL, "").isEmpty() && MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_lockedWithPassword", false)));
                item.setHideText((!MainActivity.preferences.getString(PreferenceNames.pItemPWL, "").isEmpty() && MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideText", false)));
                if (MainActivity.orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", visibilityOrder_ListAdapter.TYPE_NORMAL) == visibilityOrder_ListAdapter.TYPE_NORMAL) {
                    item.setType(visibilityOrder_ListAdapter.TYPE_NORMAL);
                    item.setHideDesc(MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideDesc", false));
                    item.setHideOnLockScreen(MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideOnLockscreen", false));
                    item.setFillEmpty(MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_fillEmpty", false));
                    titles.add(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "Empty"));
                    texts.add(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_text", ""));
                    uris.add(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_shortcutUri", ""));
                } else if (MainActivity.orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", visibilityOrder_ListAdapter.TYPE_NORMAL) == visibilityOrder_ListAdapter.TYPE_MULTI) {
                    item.setType(visibilityOrder_ListAdapter.TYPE_MULTI);
                    item.setHideDesc(MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideDesc", false));
                    item.setHideOnLockScreen(MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideOnLockscreen", false));
                    item.setFillEmpty(MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_fillEmpty", false));
                    item.setHorizontal(MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_horizontal", true));
                    int x = 1;
                    do {
                        titles.add(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item" + x + "_title", "Empty"));
                        texts.add(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item" + x + "_text", ""));
                        uris.add(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item" + x + "_shortcutUri", ""));
                        x++;
                    } while (!MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item" + x + "_title", "][EMPTY][").equals("][EMPTY]["));
                } else if (MainActivity.orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", -1) == visibilityOrder_ListAdapter.TYPE_MULTIPAGE_START) {
                    item.setType(visibilityOrder_ListAdapter.TYPE_MULTIPAGE_START);
                    item.setHideDesc(MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideDesc", false));
                    item.setHideOnLockScreen(MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideOnLockscreen", false));
                    item.setFillEmpty(MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_fillEmpty", false));
                    item.setHideFirstItemInFolder(MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideFirst", false));
                    item.setPageId(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_pageId", ""));
                    titles.add(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "Empty"));
                    texts.add(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_text", ""));
                    uris.add(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_shortcutUri", ""));
                    MultiPage.add(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", ""));
                } else if (MultiPage.size() > 0 && MainActivity.orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", -1) == visibilityOrder_ListAdapter.TYPE_MULTIPAGE_END) {
                    item.setType(visibilityOrder_ListAdapter.TYPE_MULTIPAGE_END);
                    item.setHideDesc(MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideDesc", false));
                    item.setHideOnLockScreen(MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideOnLockscreen", false));
                    item.setFillEmpty(MainActivity.orderPrefs.getBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_fillEmpty", false));
                    titles.add(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "Empty"));
                    texts.add(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_text", ""));
                    uris.add(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_shortcutUri", ""));
                    MultiPage.remove(MultiPage.size() - 1);
                }
                item.setTitle(titles);
                item.setText(texts);
                item.setShortcutUri(uris);
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
                showAddItemDialog(AddItemMode_NEW, 1, -1);
            }
        });
        if (!MainActivity.visibleFragment.equalsIgnoreCase("tour")) {
            //MainActivity.actionbar.hideButton();
        }

        return InflatedView;
    }

    public static void showAddItemDialog(final int addItemMode, final int itemSpace, final int addItemPosition) {
        slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
        dialogFragment.setContext(mContext);
        dialogFragment.setFragmentManager(MainActivity.fragmentManager);
        dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

            @Override
            public void onListItemClick(int position, String text) {
                if (position == 0) {
                    showAddSingleItemDialog(addItemMode, itemSpace, addItemPosition);
                } else if (position == 1) {
                    showAddMultiItemDialog(addItemMode, itemSpace, addItemPosition);
                } else if (position == 2) {
                    final slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                    dialogFragment.setContext(mContext);
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
                            item.getTitles().add(groupName);
                            item.getTexts().add("");
                            item.getShortcutUris().add("");
                            adapter.addItem(item);
                            item = new MenuItemHolder();
                            item.setType(visibilityOrder_ListAdapter.TYPE_NORMAL);
                            item.getTitles().add(FinalPowerMenuItems[position]);
                            item.getTexts().add("");
                            item.getShortcutUris().add("");
                            adapter.addItem(item);
                            if (FinalPowerMenuItems[position].equals("AppShortcut")) {
                                showLoadingDialog("apps");
                                item.getTitles().set(0, "AppShortcut");
                                item.getTexts().set(0, "");
                                item.getShortcutUris().set(0, "");
                                loadAppsTask = helper.startAsyncTask(new loadApps(), item, adapter.getCount()-1);
                            } else if (FinalPowerMenuItems[position].equals("Shortcut")) {
                                showLoadingDialog("shortcuts");
                                item.getTitles().set(0, "Shortcut");
                                item.getTexts().set(0, "");
                                item.getShortcutUris().set(0, "");
                                loadShortcutsTask = helper.startAsyncTask(new loadShortcuts(), item, adapter.getCount()-1);
                            } else {
                                item.getTitles().set(0, FinalPowerMenuItems[position]);
                                item.getTexts().set(0, "");
                                item.getShortcutUris().set(0, "");
                                visibilityOrderPermissionRequest_Id = adapter.getCount()-1;
                                checkItemAfterAdd(item, adapter.getCount()-1);
                            }
                            item = new MenuItemHolder();
                            item.setType(visibilityOrder_ListAdapter.TYPE_MULTIPAGE_END);
                            item.getTitles().add(groupName);
                            item.getTexts().add("");
                            item.getShortcutUris().add("");
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
                    dialogFragment.addInput(mContext.getString(R.string.visibilityOrder_FilterItemsList), "", true, new TextWatcher() {

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
                    dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[4]);
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
        dialogFragment.setList(ListView.CHOICE_MODE_NONE, mContext.getString(R.string.visibilityOrder_AddItem).split("\\|"), -1, true);
        dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[4]);
        dialogFragment.showDialog(R.id.dialog_container);
    }
    public static void showAddSingleItemDialog(final int addItemMode, final int itemSpace, final int addItemPosition) {
        final slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
        dialogFragment.setContext(mContext);
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
                    MenuItemHolder item;
                    if (addItemMode==AddItemMode_NEW) {
                        item = new MenuItemHolder();
                        item.setType(visibilityOrder_ListAdapter.TYPE_NORMAL);
                        item.getTitles().add("AppShortcut");
                        item.getTexts().add("");
                        item.getShortcutUris().add("");
                    } else if (addItemMode == AddItemMode_AddToMulti) {
                        item = adapter.getItem(addItemPosition);
                        item.getTitles().add("AppShortcut");
                        item.getTexts().add("");
                        item.getShortcutUris().add("");
                    } else {
                        item = adapter.getItem(addItemPosition);
                        item.getTitles().set(itemSpace-1, "AppShortcut");
                        item.getTexts().set(itemSpace-1, "");
                        item.getShortcutUris().set(itemSpace-1, "");
                    }
                    loadAppsTask = helper.startAsyncTask(new loadApps(), item, addItemMode==AddItemMode_NEW ? -1 : addItemPosition);
                } else if (FinalPowerMenuItems[position].equals("Shortcut")) {
                    showLoadingDialog("shortcuts");
                    MenuItemHolder item;
                    if (addItemMode==AddItemMode_NEW) {
                        item = new MenuItemHolder();
                        item.setType(visibilityOrder_ListAdapter.TYPE_NORMAL);
                        item.getTitles().add("Shortcut");
                        item.getTexts().add("");
                        item.getShortcutUris().add("");
                    } else if (addItemMode == AddItemMode_AddToMulti) {
                        item = adapter.getItem(addItemPosition);
                        item.getTitles().add("Shortcut");
                        item.getTexts().add("");
                        item.getShortcutUris().add("");
                    } else {
                        item = adapter.getItem(addItemPosition);
                        item.getTitles().set(itemSpace-1, "Shortcut");
                        item.getTexts().set(itemSpace-1, "");
                        item.getShortcutUris().set(itemSpace-1, "");
                    }
                    loadShortcutsTask = helper.startAsyncTask(new loadShortcuts(), item, addItemMode==AddItemMode_NEW ? -1 : addItemPosition);
                } else {
                    MenuItemHolder item;
                    if (addItemMode==AddItemMode_NEW) {
                        item = new MenuItemHolder();
                        item.setType(visibilityOrder_ListAdapter.TYPE_NORMAL);
                        item.getTitles().add(FinalPowerMenuItems[position]);
                        item.getTexts().add("");
                        item.getShortcutUris().add("");
                    } else if (addItemMode == AddItemMode_AddToMulti) {
                        item = adapter.getItem(addItemPosition);
                        item.getTitles().add(FinalPowerMenuItems[position]);
                        item.getTexts().add("");
                        item.getShortcutUris().add("");
                    } else {
                        item = adapter.getItemAt(addItemPosition);
                        item.getTitles().set(itemSpace-1, FinalPowerMenuItems[position]);
                        item.getTexts().set(itemSpace-1, "");
                        item.getShortcutUris().set(itemSpace-1, "");
                    }
                    if (addItemMode==AddItemMode_NEW) {
                        adapter.addItem(item);
                    } else {
                        adapter.removeAt(addItemPosition);
                        adapter.insertAt(addItemPosition, item);
                    }
                    visibilityOrderPermissionRequest_Id = addItemPosition;
                    checkItemAfterAdd(item, addItemPosition);
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
        dialogFragment.addInput(mContext.getString(R.string.visibilityOrder_FilterItemsList), "", true, new TextWatcher() {

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
        dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[4]);
        dialogFragment.showDialog(R.id.dialog_container);
    }
    public static void showAddMultiItemDialog(final int addItemMode, final int itemSpace, final int addItemPosition) {
        final ArrayList<String> AddPowerMenuItems = new ArrayList<>();
        final slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
        dialogFragment.setContext(mContext);
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
                    for (int i = 0; i < AddPowerMenuItems.size(); i++) {
                        item.getTitles().add(AddPowerMenuItems.get(i));
                        item.getTexts().add("");
                        item.getShortcutUris().add("");
                    }
                    adapter.addItem(item);
                    visibilityOrderPermissionRequest_Id = addItemPosition;
                    checkItemAfterAdd(item, addItemPosition);
                    for (int i = 0; i < AddPowerMenuItems.size(); i++) {
                        if (item.getTitle(i+1).equalsIgnoreCase("AppShortcut")) {
                            showLoadingDialog("apps");
                            loadAppsTask = helper.startAsyncTask(new loadApps(), item, adapter.getCount()-1);
                        } else if (item.getTitle(i+1).equalsIgnoreCase("Shortcut")){
                            showLoadingDialog("shortcuts");
                            loadShortcutsTask = helper.startAsyncTask(new loadShortcuts(), item, adapter.getCount()-1);
                        }
                    }
                }
            }

            @Override
            public void onTouchOutside() {

            }
        });
        dialogFragment.addInput(mContext.getString(R.string.visibilityOrder_FilterItemsList), "", true, new TextWatcher() {

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
        dialogFragment.setListLimit(10, false);
        dialogFragment.setNegativeButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[4]);
        dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[0]);
        dialogFragment.showDialog(R.id.dialog_container);
    }

    public static void checkItemAfterAdd(final MenuItemHolder item, final int id) {
        boolean doNotDisturbRequest = false;
        visibilityOrderPermissionRequest_Count = 0;
        for (int i = 1; i <= item.getTitles().size(); i++) {
            final int finalI = i;
            if (item.getTitle(i).equalsIgnoreCase(PowerMenuItems[7])) {
                if (!helper.isAppInstalled(mContext, "com.ceco.gm2.gravitybox") && !helper.isAppInstalled(mContext, "com.ceco.kitkat.gravitybox") && !helper.isAppInstalled(mContext, "com.ceco.lollipop.gravitybox") && !helper.isAppInstalled(mContext, "com.ceco.marshmallow.gravitybox") && !helper.isAppInstalled(mContext, "com.ceco.nougat.gravitybox")) {
                    visibilityOrderPermissionRequest_Count++;
                    slideDownDialogFragment NoGravityBox = new slideDownDialogFragment();
                    NoGravityBox.setContext(mContext);
                    NoGravityBox.setFragmentManager(MainActivity.fragmentManager);
                    NoGravityBox.setText(mContext.getString(R.string.visibilityOrder_NoGravityBoxFound));
                    NoGravityBox.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[0]);
                    NoGravityBox.showDialog(R.id.dialog_container);
                    receivedPermissionResult(finalI, id, false);
                }
            } else if (item.getTitle(i).equalsIgnoreCase(PowerMenuItems[10]) ||
                        item.getTitle(i).equalsIgnoreCase(PowerMenuItems[14]) ||
                        item.getTitle(i).equalsIgnoreCase(PowerMenuItems[15]) ||
                        item.getTitle(i).equalsIgnoreCase(PowerMenuItems[16]) ||
                        item.getTitle(i).equalsIgnoreCase(PowerMenuItems[28])) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !MainActivity.notificationManager.isNotificationPolicyAccessGranted() && !doNotDisturbRequest) {
                    visibilityOrderPermissionRequest_Count++;
                    visibilityOrderPermissionRequest_ItemSpace = i;
                    doNotDisturbRequest = true;
                    slideDownDialogFragment MissingPermission = new slideDownDialogFragment();
                    MissingPermission.setContext(mContext);
                    MissingPermission.setFragmentManager(MainActivity.fragmentManager);
                    MissingPermission.setText(mContext.getString(R.string.visibilityOrder_MissingPermissionForNotiPolicy));
                    MissingPermission.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[0]);
                    MissingPermission.setNegativeButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[4]);
                    MissingPermission.setListener(new slideDownDialogFragment.slideDownDialogInterface() {
                        @Override
                        public void onListItemClick(int position, String text) {

                        }

                        @Override
                        public void onNegativeClick() {
                            receivedPermissionResult(finalI, id, false);
                        }

                        @Override
                        public void onNeutralClick() {

                        }

                        @Override
                        public void onPositiveClick(Bundle resultBundle) {
                            try {
                                Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                                mContext.startActivityForResult(intent, visibilityOrderPermissionRequest);
                            } catch (Throwable t) {
                                slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                                dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                                dialogFragment.setContext(mContext);
                                dialogFragment.setText("Failed to launch the \"notification policy access\" settings:\n" + t.toString());
                                dialogFragment.showDialog(R.id.dialog_container);
                            }
                        }

                        @Override
                        public void onTouchOutside() {
                            onNegativeClick();
                        }
                    });
                    MissingPermission.showDialog(R.id.dialog_container);
                }
            } else if (item.getTitle(i).equalsIgnoreCase(PowerMenuItems[6])) {
                if (!XposedUtils.hasFlash(mContext)) {
                    visibilityOrderPermissionRequest_Count++;
                    slideDownDialogFragment MissingPermission = new slideDownDialogFragment();
                    MissingPermission.setContext(mContext);
                    String string = item.getTitle(i);
                    if (item.getText(i).isEmpty()) {
                        if (string.contains(".")) {
                            try {
                                string = pm.getApplicationInfo(string.split("/")[0], 0).loadLabel(pm).toString();
                            } catch (PackageManager.NameNotFoundException ignored) {
                            }
                        } else {
                            try {
                                string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + item.getTitle(i), "string", MainActivity.class.getPackage().getName()));
                            } catch (Throwable t) {
                                try {
                                    string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_" + item.getTitle(i), "string", MainActivity.class.getPackage().getName()));
                                } catch (Throwable ignored) {
                                }
                            }
                        }
                    } else {
                        string = item.getText(i);
                    }
                    MissingPermission.setFragmentManager(MainActivity.fragmentManager);
                    MissingPermission.setText(mContext.getString(R.string.visibilityOrder_NotSupportedbyDevice).replace("[TITLE]", string));
                    MissingPermission.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[0]);
                    MissingPermission.showDialog(R.id.dialog_container);
                    receivedPermissionResult(finalI, id, false);
                } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    visibilityOrderPermissionRequest_Count++;
                    visibilityOrderPermissionRequest_ItemSpace = i;
                    slideDownDialogFragment MissingPermission = new slideDownDialogFragment();
                    MissingPermission.setContext(mContext);
                    MissingPermission.setFragmentManager(MainActivity.fragmentManager);
                    MissingPermission.setText(mContext.getString(R.string.visibilityOrder_MissingPermissionForCamera));
                    MissingPermission.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[0]);
                    MissingPermission.setNegativeButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[4]);
                    MissingPermission.setListener(new slideDownDialogFragment.slideDownDialogInterface() {
                        @Override
                        public void onListItemClick(int position, String text) {

                        }

                        @Override
                        public void onNegativeClick() {
                            receivedPermissionResult(finalI, id, false);
                        }

                        @Override
                        public void onNeutralClick() {

                        }

                        @Override
                        public void onPositiveClick(Bundle resultBundle) {
                            ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.CAMERA}, visibilityOrderPermissionRequest);
                        }

                        @Override
                        public void onTouchOutside() {
                            onNegativeClick();
                        }
                    });
                    MissingPermission.showDialog(R.id.dialog_container);
                }
            } else if (item.getTitle(i).equalsIgnoreCase(PowerMenuItems[27])) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !devicePolicyManager.isAdminActive(new ComponentName(mContext, deviceAdmin.class))) {
                    visibilityOrderPermissionRequest_Count++;
                    visibilityOrderPermissionRequest_ItemSpace = i;
                    slideDownDialogFragment MissingPermission = new slideDownDialogFragment();
                    MissingPermission.setContext(mContext);
                    MissingPermission.setFragmentManager(MainActivity.fragmentManager);
                    MissingPermission.setText(mContext.getString(R.string.visibilityOrder_MissingDeviceAdmin));
                    MissingPermission.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[0]);
                    MissingPermission.setNegativeButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[4]);
                    MissingPermission.setListener(new slideDownDialogFragment.slideDownDialogInterface() {
                        @Override
                        public void onListItemClick(int position, String text) {

                        }

                        @Override
                        public void onNegativeClick() {
                            receivedPermissionResult(finalI, id, false);
                        }

                        @Override
                        public void onNeutralClick() {

                        }

                        @Override
                        public void onPositiveClick(Bundle resultBundle) {
                            try {
                                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, new ComponentName(mContext, deviceAdmin.class));
                                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, mContext.getString(R.string.permissionsScreenDesc_DeviceAdmin));
                                mContext.startActivityForResult(intent, visibilityOrderPermissionRequest);
                            } catch (Throwable t) {
                                slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                                dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                                dialogFragment.setContext(mContext);
                                dialogFragment.setText("Failed to launch the \"device admin\" settings:\n" + t.toString());
                                dialogFragment.showDialog(R.id.dialog_container);
                            }
                        }

                        @Override
                        public void onTouchOutside() {
                            onNegativeClick();
                        }
                    });
                    MissingPermission.showDialog(R.id.dialog_container);
                }
            }
        }
    }

    public static void showLoadingDialog(final String mode) {
        if ((mode.equalsIgnoreCase("shortcuts") && !shortcutsListFullyParsed) || (mode.equalsIgnoreCase("apps") && !appsListFullyParsed)) {
            slideDownDialogFragment loadDialog = new slideDownDialogFragment();
            if (mode.equalsIgnoreCase("shortcuts")) shortcutsLoadDialog = loadDialog;
            if (mode.equalsIgnoreCase("apps")) appsLoadDialog = loadDialog;
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
                        shortcutsLoadDialog.setPositiveButton("");
                    } else {
                        loadAppsTask.cancel(true);
                        appsLoadDialog.setPositiveButton("");
                    }
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
            //loadDialog = null;
        }
    }

    public static void receivedPermissionResult(int itemSpace, int id, boolean granted) {
        if (!granted) {
            if (id == -1) id = adapter.getCount()-1;
            if (adapter.getItemAt(id).getType() == visibilityOrder_ListAdapter.TYPE_NORMAL) {
                adapter.removeAt(id);
            } else if (adapter.getItemAt(id).getType() == visibilityOrder_ListAdapter.TYPE_MULTI) {
                MenuItemHolder item = adapter.getItemAt(id);
                int finalItemSpace = itemSpace + visibilityOrderPermissionRequest_Count;
                for (int i = itemSpace; i < finalItemSpace; i++) {
                    item.getTitles().set(itemSpace-1, "Empty");
                    item.getTexts().set(itemSpace-1, "");
                    item.getShortcutUris().set(itemSpace-1, "");
                    itemSpace++;
                }
                if (itemSpace == item.getTitles().size()) {
                    adapter.removeAt(id);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    public static class loadApps extends AsyncTask<Object, String, String> {

        PackageManager pm;
        MenuItemHolder item;
        //int type;
        int rechoice = -1;
        int itemSpace = 0;
        ArrayList<String> result;

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
            result = item.getTitles();
            if (item.getType() == visibilityOrder_ListAdapter.TYPE_MULTI) {
                for (int i = 0;  i < result.size(); i ++) {
                    if (result.get(i).equalsIgnoreCase("AppShortcut")) {
                        itemSpace = i;
                    }
                }
            }
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
                if (appsLoadDialog != null)
                    appsLoadDialog.setProgressBar((i * 100) / packages.size());
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
                if (appsLoadDialog != null) appsLoadDialog.cancelDialog();
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
                        if (appsPackagesFiltered.isEmpty()) {
                            string = appsPackages.get(resultBundle.getInt(slideDownDialogFragment.RESULT_LIST));
                        } else {
                            string = appsPackagesFiltered.get(resultBundle.getInt(slideDownDialogFragment.RESULT_LIST));
                        }
                        MenuItemHolder newItem;
                        if (rechoice != -1) {
                            newItem = adapter.getItemAt(rechoice);
                        } else {
                            newItem = new MenuItemHolder();
                            newItem.setType(item.getType());
                            newItem.setHideDesc(false);
                            newItem.setHideOnLockScreen(false);
                            newItem.setTitle(item.getTitles());
                            newItem.setText(item.getTexts());
                            newItem.setShortcutUri(item.getShortcutUris());
                        }
                        if (item.getType() == visibilityOrder_ListAdapter.TYPE_NORMAL) {
                            newItem.getTitles().set(0, string);
                            newItem.getTexts().set(0, "");
                            newItem.getShortcutUris().set(0, "");
                        } else {
                            newItem.getTitles().set(itemSpace, string);
                            newItem.getTexts().set(itemSpace, "");
                            newItem.getShortcutUris().set(itemSpace, "");
                        }
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
        int ThisEditID = 1;
        int rechoice = -1;
        ArrayList<String> result;

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
            result = item.getTitles();
            for (int i = 0;  i < result.size(); i ++) {
                if (result.get(i).equalsIgnoreCase("Shortcut")) {
                    ThisEditID = i+1;
                }
            }
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
            Collections.sort(shortcuts, new Comparator<ResolveInfo>() {
                @Override
                public int compare(ResolveInfo resolveInfo, ResolveInfo t1) {
                    return resolveInfo.loadLabel(pm).toString().compareToIgnoreCase(t1.loadLabel(pm).toString());
                }
            });

            int i = 0;
            for (ResolveInfo packageInfo : shortcuts) {
                if (isCancelled()) {
                    return "canceled";
                }
                i++;
                if (shortcutsLoadDialog != null)
                    shortcutsLoadDialog.setProgressBar((i * 100) / shortcuts.size());
                shortcutPackages.add(packageInfo);
                shortcutNames.add(packageInfo.loadLabel(pm).toString());
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
                if (shortcutsLoadDialog != null) shortcutsLoadDialog.cancelDialog();
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
                                thisRI = shortcutPackagesFiltered.get(resultBundle.getInt(slideDownDialogFragment.RESULT_LIST));
                            }
                        } else {
                            if (shortcutsNamesFiltered.isEmpty()) {
                                thisRI = shortcutPackages.get(resultBundle.getInt(slideDownDialogFragment.RESULT_LIST));
                            } else {
                                thisRI = shortcutPackagesFiltered.get(resultBundle.getInt(slideDownDialogFragment.RESULT_LIST));
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
                                    MenuItemHolder newItem;
                                    if (rechoice != -1) {
                                        newItem = adapter.getItemAt(rechoice);
                                    } else {
                                        newItem = new MenuItemHolder();
                                        newItem.setType(item.getType());
                                        newItem.setHideDesc(false);
                                        newItem.setHideOnLockScreen(false);
                                        newItem.setTitle(item.getTitles());
                                        newItem.setText(item.getTexts());
                                        newItem.setShortcutUri(item.getShortcutUris());
                                    }
                                    if (newItem.getType() == visibilityOrder_ListAdapter.TYPE_NORMAL) {
                                        newItem.getTitles().set(0, sir.getIntent().getStringExtra("label"));
                                        newItem.getShortcutUris().set(0, sir.getIntent().toUri(Intent.URI_INTENT_SCHEME));
                                    } else {
                                        newItem.getTitles().set(ThisEditID-1, sir.getIntent().getStringExtra("label"));
                                        newItem.getShortcutUris().set(ThisEditID-1, sir.getIntent().toUri(Intent.URI_INTENT_SCHEME));
                                    }
                                    if (rechoice != -1) {
                                        adapter.removeAt(rechoice);
                                        adapter.insertAt(rechoice, newItem);
                                    } else {
                                        adapter.addItem(newItem);
                                    }
                                }
                            });
                            obtainShortcut(si);
                        }
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
                        shortcutPackagesFiltered.clear();
                        if (charSequence.toString().isEmpty()) {
                            dialogFragment.setList(ListView.CHOICE_MODE_SINGLE, shortcutNames, -1, false);
                        } else {
                            for (int x = 0; x < shortcutNames.size(); x++) {
                                if (shortcutNames.get(x).toLowerCase().contains(charSequence.toString().toLowerCase())) {
                                    shortcutsNamesFiltered.add(shortcutNames.get(x));
                                    shortcutPackagesFiltered.add(shortcutPackages.get(x));
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
            //Toast.makeText(mContext, "Shortcut creation cancelled", Toast.LENGTH_SHORT).show();
        }
    }

}

package de.NeonSoft.neopowermenu.helpers;

import android.app.*;
import android.content.pm.*;
import android.os.*;
import android.support.annotation.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.Preferences.*;

import java.util.*;

public class visibilityOrder_ListAdapter extends ArrayAdapter<MenuItemHolder> {

    private Activity mContext;
    private LayoutInflater inflater;
    private ArrayList<MenuItemHolder> items;
    PackageManager pm;

    public static int TYPE_NORMAL = 0;
    public static int TYPE_MULTI = 1;
    public static int TYPE_MULTIPAGE_START = 2;
    public static int TYPE_MULTIPAGE_END = 3;

    public visibilityOrder_ListAdapter(Activity context,
                                       ArrayList<MenuItemHolder> items) {
        super(context, R.layout.visibilityorder_normal, items);
        this.mContext = context;
        this.inflater = context.getLayoutInflater();
        this.items = items;
        pm = mContext.getPackageManager();
    }

    @NonNull
    @Override
    public View getView(final int position, View p2, @NonNull ViewGroup p3) {

        View InflatedView = p2;

        if (items.get(position).getType() == TYPE_NORMAL) {
            boolean hasDescription = false;
            InflatedView = inflater.inflate(R.layout.visibilityorder_normal, p3, false);

            LinearLayout MenuItemHolder = (LinearLayout) InflatedView.findViewById(R.id.visibilityordernormalLinearLayout_item);
            TextView item = (TextView) InflatedView.findViewById(R.id.visibilityordernormal_item);
            String string = items.get(position).getTitle();
            if (items.get(position).getText().isEmpty()) {
                if (string.contains(".")) {
                    try {
                        string = pm.getApplicationInfo(string.split("/")[0], 0).loadLabel(pm).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                    }
                } else {
                    try {
                        string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + items.get(position).getTitle(), "string", MainActivity.class.getPackage().getName()));
                    } catch (Throwable t) {
                        try {
                            string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_" + items.get(position).getTitle(), "string", MainActivity.class.getPackage().getName()));
                        } catch (Throwable t1) {
                        }
                    }
                }
            } else {
                string = items.get(position).getText();
            }
            item.setText(string);

            ImageView HideDescription = (ImageView) InflatedView.findViewById(R.id.visibilityordernormalImageView_HideDescription);
            ImageView HideOnLockscreen = (ImageView) InflatedView.findViewById(R.id.visibilityordernormalImageView_HideOnLockscreen);

            try {
                if (!mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + this.items.get(position).getTitle() + "Desc", "string", MainActivity.class.getPackage().getName())).equalsIgnoreCase("")) {
                    HideDescription.setVisibility(items.get(position).getHideDesc() ? View.GONE : View.VISIBLE);
                    hasDescription = true;
                }
            } catch (Throwable t) {
            }
            HideOnLockscreen.setVisibility(items.get(position).getHideOnLockScreen() ? View.GONE : View.VISIBLE);

            MenuItemHolder.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View p1) {
                    final slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                    dialogFragment.setContext(mContext);
                    dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                    dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

                        @Override
                        public void onListItemClick(int listpos, String text) {
                            String[] FinalPowerMenuItems;
                            if (PreferencesVisibilityOrderFragment.FilteredPowerMenuItems.isEmpty()) {
                                FinalPowerMenuItems = PreferencesVisibilityOrderFragment.PowerMenuItems;
                            } else {
                                FinalPowerMenuItems = PreferencesVisibilityOrderFragment.FilteredPowerMenuItems.toArray(new String[]{});
                            }
                            if (FinalPowerMenuItems[listpos].equals("AppShortcut")) {
                                if (!PreferencesVisibilityOrderFragment.appsListFullyParsed) {
                                    PreferencesVisibilityOrderFragment.loadAppsDialog = new slideDownDialogFragment();
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.setContext(mContext);
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.setFragmentManager(MainActivity.fragmentManager);
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.setListener(new slideDownDialogFragment.slideDownDialogInterface() {
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
                                            PreferencesVisibilityOrderFragment.loadAppsDialog.setPositiveButton("");
                                            PreferencesVisibilityOrderFragment.loadAppsTask.cancel(true);
                                        }

                                        @Override
                                        public void onTouchOutside() {

                                        }
                                    });
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.setCloseOnTouchOutside(false);
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.setCloseOnButtonClick(false);
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.setText(mContext.getString(R.string.login_Processing));
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.addProgressBar(true, false);
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.showDialog(R.id.dialog_container);
                                } else {
                                    PreferencesVisibilityOrderFragment.loadAppsDialog = null;
                                }
                                PreferencesVisibilityOrderFragment.loadAppsTask = helper.startAsyncTask(new PreferencesVisibilityOrderFragment.loadApps(), items.get(position), position);
                            } else {
                                MenuItemHolder item = getItemAt(position);
                                item.setTitle(FinalPowerMenuItems[listpos]);
                                removeAt(position);
                                insertAt(position, item);
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
                            PreferencesVisibilityOrderFragment.FilteredPowerMenuItems.clear();
                            PreferencesVisibilityOrderFragment.FilteredPowerMenuItemsTexts.clear();
                            if (p1.toString().isEmpty()) {
                                dialogFragment.setList(ListView.CHOICE_MODE_NONE, PreferencesVisibilityOrderFragment.PowerMenuItemsTexts, -1, true);
                            } else {
                                for (int x = 0; x < PreferencesVisibilityOrderFragment.PowerMenuItems.length; x++) {
                                    if (PreferencesVisibilityOrderFragment.PowerMenuItemsTexts[x].toLowerCase().contains(p1.toString().toLowerCase())) {
                                        PreferencesVisibilityOrderFragment.FilteredPowerMenuItems.add(PreferencesVisibilityOrderFragment.PowerMenuItems[x]);
                                        PreferencesVisibilityOrderFragment.FilteredPowerMenuItemsTexts.add(PreferencesVisibilityOrderFragment.PowerMenuItemsTexts[x]);
                                    }
                                }
                                dialogFragment.setList(ListView.CHOICE_MODE_NONE, PreferencesVisibilityOrderFragment.FilteredPowerMenuItemsTexts, -1, true);
                            }
                        }
                    });
                    dialogFragment.setList(ListView.CHOICE_MODE_NONE, PreferencesVisibilityOrderFragment.PowerMenuItemsTexts, -1, true);
                    dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[4]);
                    dialogFragment.showDialog(R.id.dialog_container);
                }
            });

            LinearLayout EditAppearanceBehaviour = (LinearLayout) InflatedView.findViewById(R.id.visibilityordernormalLinearLayout_EditBehaviour);
            EditAppearanceBehaviour.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditAppearanceBehaviourFor(position);
                }
            });

        } else if (items.get(position).getType() == TYPE_MULTI) {
            InflatedView = inflater.inflate(R.layout.visibilityorder_multi, p3, false);

            final String[] item = items.get(position).getTitle().split("\\|");

            final TextView item1 = (TextView) InflatedView.findViewById(R.id.visibilityordermulti_item1);
            String string = "";
            if (!items.get(position).getText().split("\\|")[0].equalsIgnoreCase("< default >")) {
                string = items.get(position).getText().split("\\|")[0];
            }
            if (string.isEmpty()) {
                string = item[0];
                if (string.contains(".")) {
                    try {
                        string = pm.getApplicationInfo(string.split("/")[0], 0).loadLabel(pm).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                    }
                } else {
                    try {
                        string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + item[0], "string", MainActivity.class.getPackage().getName()));
                    } catch (Throwable t) {
                        try {
                            string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_" + item[0], "string", MainActivity.class.getPackage().getName()));
                        } catch (Throwable t1) {
                        }
                    }
                }
            }
            item1.setText(string);

            TextView item2 = (TextView) InflatedView.findViewById(R.id.visibilityordermulti_item2);
            String string2 = "";
            if (item.length >= 2) {
                if (items.get(position).getText().split("\\|").length >= 2 && !items.get(position).getText().split("\\|")[1].equalsIgnoreCase("< default >")) {
                    string2 = items.get(position).getText().split("\\|")[1];
                }
                if (string2.isEmpty()) {
                    string2 = item[1];
                    if (string2.contains(".")) {
                        try {
                            string2 = pm.getApplicationInfo(string2.split("/")[0], 0).loadLabel(pm).toString();
                        } catch (PackageManager.NameNotFoundException e) {
                        }
                    } else {
                        try {
                            string2 = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + item[1], "string", MainActivity.class.getPackage().getName()));
                        } catch (Throwable t) {
                            try {
                                string2 = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_" + item[1], "string", MainActivity.class.getPackage().getName()));
                            } catch (Throwable t1) {
                            }
                        }
                    }
                }
                item2.setText(string2);
            } else {
                item2.setText(PreferencesVisibilityOrderFragment.PowerMenuItemsTexts[0]);
            }

            TextView item3 = (TextView) InflatedView.findViewById(R.id.visibilityordermulti_item3);
            String string3 = "";
            if (item.length == 3) {
                if (items.get(position).getText().split("\\|").length == 3 && !items.get(position).getText().split("\\|")[2].equalsIgnoreCase("< default >")) {
                    string3 = items.get(position).getText().split("\\|")[2];
                }
                if (string3.isEmpty()) {
                    string3 = item[2];
                    if (string3.contains(".")) {
                        try {
                            string3 = pm.getApplicationInfo(string3.split("/")[0], 0).loadLabel(pm).toString();
                        } catch (PackageManager.NameNotFoundException e) {
                        }
                    } else {
                        try {
                            string3 = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + item[2], "string", MainActivity.class.getPackage().getName()));
                        } catch (Throwable t) {
                            try {
                                string3 = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_" + item[2], "string", MainActivity.class.getPackage().getName()));
                            } catch (Throwable t1) {
                            }
                        }
                    }
                }
                item3.setText(string3);
            } else {
                item3.setText(PreferencesVisibilityOrderFragment.PowerMenuItemsTexts[0]);
            }

            item1.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View p1) {
                    final slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                    dialogFragment.setContext(mContext);
                    dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                    dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

                        @Override
                        public void onListItemClick(int listpos, String text) {
                            String[] FinalPowerMenuItems;
                            if (PreferencesVisibilityOrderFragment.FilteredPowerMenuItems.isEmpty()) {
                                FinalPowerMenuItems = PreferencesVisibilityOrderFragment.PowerMenuItems;
                            } else {
                                FinalPowerMenuItems = PreferencesVisibilityOrderFragment.FilteredPowerMenuItems.toArray(new String[]{});
                            }
                            if (FinalPowerMenuItems[listpos].equals("AppShortcut")) {
                                if (!PreferencesVisibilityOrderFragment.appsListFullyParsed) {
                                    PreferencesVisibilityOrderFragment.loadAppsDialog = new slideDownDialogFragment();
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.setContext(mContext);
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.setFragmentManager(MainActivity.fragmentManager);
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.setListener(new slideDownDialogFragment.slideDownDialogInterface() {
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
                                            PreferencesVisibilityOrderFragment.loadAppsDialog.setPositiveButton("");
                                            PreferencesVisibilityOrderFragment.loadAppsTask.cancel(true);
                                        }

                                        @Override
                                        public void onTouchOutside() {

                                        }
                                    });
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.setCloseOnTouchOutside(false);
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.setCloseOnButtonClick(false);
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.setText(mContext.getString(R.string.login_Processing));
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.addProgressBar(true, false);
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.showDialog(R.id.dialog_container);
                                } else {
                                    PreferencesVisibilityOrderFragment.loadAppsDialog = null;
                                }
                                MenuItemHolder newItem = items.get(position);
                                newItem.setType(TYPE_MULTI);
                                newItem.setTitle("[THIS]" + "|" + item[1] + "|" + item[2]);
                                PreferencesVisibilityOrderFragment.loadAppsTask = helper.startAsyncTask(new PreferencesVisibilityOrderFragment.loadApps(), newItem, position);
                            } else {
                                MenuItemHolder newItem = items.get(position);
                                newItem.setType(TYPE_MULTI);
                                newItem.setTitle(PreferencesVisibilityOrderFragment.PowerMenuItems[listpos] + "|" + item[1] + "|" + item[2]);
                                removeAt(position);
                                insertAt(position, newItem);
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
                            PreferencesVisibilityOrderFragment.FilteredPowerMenuItems.clear();
                            PreferencesVisibilityOrderFragment.FilteredPowerMenuItemsTexts.clear();
                            if (p1.toString().isEmpty()) {
                                dialogFragment.setList(ListView.CHOICE_MODE_NONE, PreferencesVisibilityOrderFragment.PowerMenuItemsTexts, -1, true);
                            } else {
                                for (int x = 0; x < PreferencesVisibilityOrderFragment.PowerMenuItems.length; x++) {
                                    if (PreferencesVisibilityOrderFragment.PowerMenuItemsTexts[x].toLowerCase().contains(p1.toString().toLowerCase())) {
                                        PreferencesVisibilityOrderFragment.FilteredPowerMenuItems.add(PreferencesVisibilityOrderFragment.PowerMenuItems[x]);
                                        PreferencesVisibilityOrderFragment.FilteredPowerMenuItemsTexts.add(PreferencesVisibilityOrderFragment.PowerMenuItemsTexts[x]);
                                    }
                                }
                                dialogFragment.setList(ListView.CHOICE_MODE_NONE, PreferencesVisibilityOrderFragment.FilteredPowerMenuItemsTexts, -1, true);
                            }
                        }
                    });
                    dialogFragment.setList(ListView.CHOICE_MODE_NONE, PreferencesVisibilityOrderFragment.PowerMenuItemsTexts, -1, true);
                    dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[4]);
                    dialogFragment.showDialog(R.id.dialog_container);
                }
            });
            item2.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View p1) {
                    final slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                    dialogFragment.setContext(mContext);
                    dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                    dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

                        @Override
                        public void onListItemClick(int listpos, String text) {
                            String[] FinalPowerMenuItems;
                            if (PreferencesVisibilityOrderFragment.FilteredPowerMenuItems.isEmpty()) {
                                FinalPowerMenuItems = PreferencesVisibilityOrderFragment.PowerMenuItems;
                            } else {
                                FinalPowerMenuItems = PreferencesVisibilityOrderFragment.FilteredPowerMenuItems.toArray(new String[]{});
                            }
                            if (FinalPowerMenuItems[listpos].equals("AppShortcut")) {
                                if (!PreferencesVisibilityOrderFragment.appsListFullyParsed) {
                                    PreferencesVisibilityOrderFragment.loadAppsDialog = new slideDownDialogFragment();
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.setContext(mContext);
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.setFragmentManager(MainActivity.fragmentManager);
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.setListener(new slideDownDialogFragment.slideDownDialogInterface() {
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
                                            PreferencesVisibilityOrderFragment.loadAppsDialog.setPositiveButton("");
                                            PreferencesVisibilityOrderFragment.loadAppsTask.cancel(true);
                                        }

                                        @Override
                                        public void onTouchOutside() {

                                        }
                                    });
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.setCloseOnTouchOutside(false);
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.setCloseOnButtonClick(false);
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.setText(mContext.getString(R.string.login_Processing));
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.addProgressBar(true, false);
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.showDialog(R.id.dialog_container);
                                } else {
                                    PreferencesVisibilityOrderFragment.loadAppsDialog = null;
                                }
                                MenuItemHolder newItem = items.get(position);
                                newItem.setType(TYPE_MULTI);
                                newItem.setTitle(item[0] + "|" + "[THIS]" + "|" + item[2]);
                                PreferencesVisibilityOrderFragment.loadAppsTask = helper.startAsyncTask(new PreferencesVisibilityOrderFragment.loadApps(), newItem, position);
                            } else {
                                MenuItemHolder newItem = items.get(position);
                                newItem.setType(TYPE_MULTI);
                                newItem.setTitle(item[0] + "|" + PreferencesVisibilityOrderFragment.PowerMenuItems[listpos] + "|" + item[2]);
                                removeAt(position);
                                insertAt(position, newItem);
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
                            PreferencesVisibilityOrderFragment.FilteredPowerMenuItems.clear();
                            PreferencesVisibilityOrderFragment.FilteredPowerMenuItemsTexts.clear();
                            if (p1.toString().isEmpty()) {
                                dialogFragment.setList(ListView.CHOICE_MODE_NONE, PreferencesVisibilityOrderFragment.PowerMenuItemsTexts, -1, true);
                            } else {
                                for (int x = 0; x < PreferencesVisibilityOrderFragment.PowerMenuItems.length; x++) {
                                    if (PreferencesVisibilityOrderFragment.PowerMenuItemsTexts[x].toLowerCase().contains(p1.toString().toLowerCase())) {
                                        PreferencesVisibilityOrderFragment.FilteredPowerMenuItems.add(PreferencesVisibilityOrderFragment.PowerMenuItems[x]);
                                        PreferencesVisibilityOrderFragment.FilteredPowerMenuItemsTexts.add(PreferencesVisibilityOrderFragment.PowerMenuItemsTexts[x]);
                                    }
                                }
                                dialogFragment.setList(ListView.CHOICE_MODE_NONE, PreferencesVisibilityOrderFragment.FilteredPowerMenuItemsTexts, -1, true);
                            }
                        }
                    });
                    dialogFragment.setList(ListView.CHOICE_MODE_NONE, PreferencesVisibilityOrderFragment.PowerMenuItemsTexts, -1, true);
                    dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[4]);
                    dialogFragment.showDialog(R.id.dialog_container);
                }
            });
            item3.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View p1) {
                    final slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                    dialogFragment.setContext(mContext);
                    dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                    dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

                        @Override
                        public void onListItemClick(int listpos, String text) {
                            String[] FinalPowerMenuItems;
                            if (PreferencesVisibilityOrderFragment.FilteredPowerMenuItems.isEmpty()) {
                                FinalPowerMenuItems = PreferencesVisibilityOrderFragment.PowerMenuItems;
                            } else {
                                FinalPowerMenuItems = PreferencesVisibilityOrderFragment.FilteredPowerMenuItems.toArray(new String[]{});
                            }
                            if (FinalPowerMenuItems[listpos].equals("AppShortcut")) {
                                if (!PreferencesVisibilityOrderFragment.appsListFullyParsed) {
                                    PreferencesVisibilityOrderFragment.loadAppsDialog = new slideDownDialogFragment();
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.setContext(mContext);
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.setFragmentManager(MainActivity.fragmentManager);
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.setListener(new slideDownDialogFragment.slideDownDialogInterface() {
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
                                            PreferencesVisibilityOrderFragment.loadAppsDialog.setPositiveButton("");
                                            PreferencesVisibilityOrderFragment.loadAppsTask.cancel(true);
                                        }

                                        @Override
                                        public void onTouchOutside() {

                                        }
                                    });
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.setCloseOnTouchOutside(false);
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.setCloseOnButtonClick(false);
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.setText(mContext.getString(R.string.login_Processing));
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.addProgressBar(true, false);
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                                    PreferencesVisibilityOrderFragment.loadAppsDialog.showDialog(R.id.dialog_container);
                                } else {
                                    PreferencesVisibilityOrderFragment.loadAppsDialog = null;
                                }
                                MenuItemHolder newItem = items.get(position);
                                newItem.setType(TYPE_MULTI);
                                newItem.setTitle(item[0] + "|" + item[1] + "|" + "[THIS]");
                                PreferencesVisibilityOrderFragment.loadAppsTask = helper.startAsyncTask(new PreferencesVisibilityOrderFragment.loadApps(), newItem, position);
                            } else {
                                MenuItemHolder newItem = items.get(position);
                                newItem.setType(TYPE_MULTI);
                                newItem.setTitle(item[0] + "|" + item[1] + "|" + PreferencesVisibilityOrderFragment.PowerMenuItems[listpos]);
                                removeAt(position);
                                insertAt(position, newItem);
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
                            PreferencesVisibilityOrderFragment.FilteredPowerMenuItems.clear();
                            PreferencesVisibilityOrderFragment.FilteredPowerMenuItemsTexts.clear();
                            if (p1.toString().isEmpty()) {
                                dialogFragment.setList(ListView.CHOICE_MODE_NONE, PreferencesVisibilityOrderFragment.PowerMenuItemsTexts, -1, true);
                            } else {
                                for (int x = 0; x < PreferencesVisibilityOrderFragment.PowerMenuItems.length; x++) {
                                    if (PreferencesVisibilityOrderFragment.PowerMenuItemsTexts[x].toLowerCase().contains(p1.toString().toLowerCase())) {
                                        PreferencesVisibilityOrderFragment.FilteredPowerMenuItems.add(PreferencesVisibilityOrderFragment.PowerMenuItems[x]);
                                        PreferencesVisibilityOrderFragment.FilteredPowerMenuItemsTexts.add(PreferencesVisibilityOrderFragment.PowerMenuItemsTexts[x]);
                                    }
                                }
                                dialogFragment.setList(ListView.CHOICE_MODE_NONE, PreferencesVisibilityOrderFragment.FilteredPowerMenuItemsTexts, -1, true);
                            }
                        }
                    });
                    dialogFragment.setList(ListView.CHOICE_MODE_NONE, PreferencesVisibilityOrderFragment.PowerMenuItemsTexts, -1, true);
                    dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[4]);
                    dialogFragment.showDialog(R.id.dialog_container);
                }
            });

            ImageView HideOnLockscreen = (ImageView) InflatedView.findViewById(R.id.visibilityordermultiImageView_HideOnLockscreen);
            HideOnLockscreen.setVisibility(items.get(position).getHideOnLockScreen() ? View.GONE : View.VISIBLE);

            LinearLayout EditAppearanceBehaviour = (LinearLayout) InflatedView.findViewById(R.id.visibilityordermultiLinearLayout_EditBehaviour);
            EditAppearanceBehaviour.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditAppearanceBehaviourFor(position);
                }
            });

        } else if (items.get(position).getType() == TYPE_MULTIPAGE_START) {
            InflatedView = inflater.inflate(R.layout.visibilityorder_normal, p3, false);

            TextView item = (TextView) InflatedView.findViewById(R.id.visibilityordernormal_item);
            item.setClickable(false);
            item.setText(mContext.getString(R.string.visibilityOrder_MultiPage).split("\\|")[0]);

            ImageView HideOnLockscreen = (ImageView) InflatedView.findViewById(R.id.visibilityordernormalImageView_HideOnLockscreen);
            HideOnLockscreen.setVisibility(items.get(position).getHideOnLockScreen() ? View.GONE : View.VISIBLE);

            LinearLayout EditAppearanceBehaviour = (LinearLayout) InflatedView.findViewById(R.id.visibilityordernormalLinearLayout_EditBehaviour);
            EditAppearanceBehaviour.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditAppearanceBehaviourFor(position);
                }
            });
        } else if (items.get(position).getType() == TYPE_MULTIPAGE_END) {
            InflatedView = inflater.inflate(R.layout.visibilityorder_normal, p3, false);

            TextView item = (TextView) InflatedView.findViewById(R.id.visibilityordernormal_item);
            LinearLayout dragHandle = (LinearLayout) InflatedView.findViewById(R.id.drag_handle);
            //dragHandle.setVisibility(View.GONE);
            item.setClickable(false);
            item.setText(mContext.getString(R.string.visibilityOrder_MultiPage).split("\\|")[1]);

            LinearLayout BottomBar = (LinearLayout) InflatedView.findViewById(R.id.visibilityordernormal_BottomBar);
            BottomBar.setVisibility(View.GONE);
        }
        InflatedView.setClickable(false);
        InflatedView.setEnabled(false);

        return InflatedView;
    }

    public void addItem(MenuItemHolder item) {
        items.add(item);
        notifyDataSetChanged();
    }

    public MenuItemHolder getItemAt(int position) {
        return items.get(position);
    }

    public void insertAt(int position, MenuItemHolder item) {
        items.add(position, item);
        notifyDataSetChanged();
    }

    public void move(int from, int to) {
        if (from != to) {
            MenuItemHolder item = getItemAt(from);
            if (item.getType() == TYPE_MULTIPAGE_START || item.getType() == TYPE_MULTIPAGE_END) {
                boolean validMove = true;
                items.remove(from);
                items.add(to, item);
                ArrayList<String> pages = new ArrayList<>();
                for (int i = 0; i < items.size(); i++) {
                    try {
                        MenuItemHolder checkItem = getItemAt(i);
                        if (MainActivity.DeepLogging)
                            Log.i("NPM:vOPC", "(" + String.format("%02d", i) + ")> " + checkItem.getType() + " | " + checkItem.getTitle());
                        if (checkItem.getType() == TYPE_MULTIPAGE_START) {
                            pages.add(checkItem.getTitle());
                        } else if (checkItem.getType() == TYPE_MULTIPAGE_END) {
                            pages.remove(pages.size() - 1);
                        }
                    } catch (Throwable t) {
                        Log.e("NPM:vOPC", "Invalid move operation:", t);
                        validMove = false;
                        break;
                    }
                }
                if (!validMove) {
                    Toast.makeText(mContext, mContext.getString(R.string.visibilityOrder_InvalidMultiPageMove), Toast.LENGTH_LONG).show();
                    items.remove(to);
                    items.add(from, item);
                }
                notifyDataSetChanged();
            } else {
                //Toast.makeText(mContext, "normal move from "+from+" to "+to, Toast.LENGTH_SHORT).show();
                items.remove(from);
                items.add(to, item);
                notifyDataSetChanged();
            }
        }

    }

    public void removeAt(int position) {
        if (items.get(position).getType() == TYPE_MULTIPAGE_START) {
            MenuItemHolder item = getItemAt(position);
            int removingLayers = 0;
            while (items.size() > position) {
                MenuItemHolder checkItem = getItemAt(position);
                if (checkItem.getType() == TYPE_MULTIPAGE_END) {
                    if (removingLayers > 0) {
                        items.remove(position);
                        removingLayers--;
                        //Toast.makeText(mContext,"- Setting layer count to "+removingLayers,Toast.LENGTH_SHORT).show();
                        if (removingLayers == 0) {
                            break;
                        }
                    }
                } else {
                    if (checkItem.getType() == TYPE_MULTIPAGE_START) {
                        removingLayers++;
                        //Toast.makeText(mContext,"+ Setting layer count to "+removingLayers,Toast.LENGTH_SHORT).show();
                    }
                    items.remove(position);
                }
            }
        } else if (items.get(position).getType() == TYPE_MULTIPAGE_END) {
        } else if (items.get(position).getType() == TYPE_NORMAL || (int) items.get(position).getType() == TYPE_MULTI) {
            items.remove(position);
        }
        notifyDataSetChanged();
        //outputSorting();
    }


    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    private void showEditAppearanceBehaviourFor(final int position) {

        final MenuItemHolder thisItem = items.get(position);
        boolean hasDescription = false;
        try {
            if (!mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + thisItem.getTitle() + "Desc", "string", MainActivity.class.getPackage().getName())).equalsIgnoreCase("")) {
                hasDescription = true;
            }
        } catch (Throwable t) {
        }
        final ArrayList<String> options = new ArrayList<String>();
        ArrayList<Boolean> checked = new ArrayList<Boolean>();
        if (hasDescription) {
            options.add(mContext.getString(R.string.visibilityOrder_HideDesc));
            checked.add(thisItem.getHideDesc());
        }
        if (thisItem.getType() == TYPE_MULTI) {
            options.add(mContext.getString(R.string.visibilityOrder_FillEmpty));
            checked.add(thisItem.getFillEmpty());
        }
        options.add(mContext.getString(R.string.visibilityOrder_HideOnLockscreen));
        checked.add(thisItem.getHideOnLockScreen());
        slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
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
                                           String inputResult = resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0", "");
                                           String inputResult2 = resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "1", "");
                                           String inputResult3 = resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "2", "");
                                           String listResult = resultBundle.getString(slideDownDialogFragment.RESULT_LIST, "");

                                           MenuItemHolder item = items.get(position);
                                           if (thisItem.getType() == TYPE_NORMAL || thisItem.getType() == TYPE_MULTIPAGE_START) {
                                               item.setText((inputResult.isEmpty() ? "" : inputResult));

                                               if (item.getType() == TYPE_NORMAL) {
                                                   item.setHideDesc(false);
                                                   item.setHideOnLockScreen(false);
                                               } else if (item.getType() == TYPE_MULTI) {
                                                   item.setFillEmpty(false);
                                                   item.setHideOnLockScreen(false);
                                               }

                                               if (!listResult.isEmpty()) {
                                                   for (String result : listResult.split(",")) {
                                                           if (options.size() >= 2) {
                                                               if (Integer.parseInt(result) == 0) {
                                                                   item.setHideDesc(true);
                                                               } else if (Integer.parseInt(result) == 1) {
                                                                   item.setHideOnLockScreen(true);
                                                               }
                                                           } else {
                                                               if (Integer.parseInt(result) == 0) {
                                                                   item.setHideOnLockScreen(true);
                                                               }
                                                           }
                                                   }
                                               }
                                           } else if (thisItem.getType() == TYPE_MULTI)

                                           {
                                               item.setText((inputResult.isEmpty() ? "< default >" : inputResult) + "|" + (inputResult2.isEmpty() ? "< default >" : inputResult2) + "|" + (inputResult3.isEmpty() ? "< default >" : inputResult3));

                                               item.setHideOnLockScreen(false);

                                               if (!listResult.isEmpty()) {
                                                   for (String result : listResult.split(",")) {
                                                       if (options.size() >= 2) {
                                                           if (Integer.parseInt(result) == 0) {
                                                               item.setFillEmpty(true);
                                                           } else if (Integer.parseInt(result) == 1) {
                                                               item.setHideOnLockScreen(true);
                                                           }
                                                       } else {
                                                           if (Integer.parseInt(result) == 0) {
                                                               item.setHideOnLockScreen(true);
                                                           }
                                                       }
                                                   }
                                               }
                                           }

                                           items.set(position, item);

                                           notifyDataSetChanged();

                                       }

                                       @Override
                                       public void onTouchOutside() {

                                       }
                                   }

        );
        dialogFragment.setText(mContext.getString(R.string.visibilityOrder_EditAppearanceBehaviourDesc));
        if (thisItem.getType() == TYPE_NORMAL)

        {
            dialogFragment.addInput(mContext.getString(R.string.visibilityOrder_EditAppearanceBehaviourText), thisItem.getText(), true, null);
        } else if (thisItem.getType() == TYPE_MULTI)

        {
            dialogFragment.addInput(mContext.getString(R.string.visibilityOrder_EditAppearanceBehaviourText1), thisItem.getText().split("\\|")[0].replace("< default >", ""), true, null);
            dialogFragment.addInput(mContext.getString(R.string.visibilityOrder_EditAppearanceBehaviourText2), (thisItem.getText().split("\\|").length >= 2 ? thisItem.getText().split("\\|")[1].replace("< default >", "") : ""), true, null);
            dialogFragment.addInput(mContext.getString(R.string.visibilityOrder_EditAppearanceBehaviourText3), (thisItem.getText().split("\\|").length == 3 ? thisItem.getText().split("\\|")[2].replace("< default >", "") : ""), true, null);
        }

        dialogFragment.setList(ListView.CHOICE_MODE_MULTIPLE, options, -1, false);
        dialogFragment.setListChecks(checked);
        dialogFragment.setListAllowEmpty(true);
        dialogFragment.setNegativeButton(mContext.getString(R.string.Dialog_Buttons).

                split("\\|")[slideDownDialogFragment

                .BUTTON_CANCEL]);
        dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).

                split("\\|")[slideDownDialogFragment

                .BUTTON_SAVE]);
        dialogFragment.showDialog(R.id.dialog_container);
    }

    public void outputSorting() {
        ArrayList<String> MultiPage = new ArrayList<String>();
        MainActivity.orderPrefs.edit().clear().apply();
        for (int i = 0; i < items.size(); i++) {
            MainActivity.orderPrefs.edit().putInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", items.get(i).getType()).apply();
            MainActivity.orderPrefs.edit().putBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideDesc", items.get(i).getHideDesc()).apply();
            MainActivity.orderPrefs.edit().putBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideOnLockscreen", items.get(i).getHideOnLockScreen()).apply();
            MainActivity.orderPrefs.edit().putBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_fillEmpty", items.get(i).getFillEmpty()).apply();
            if (items.get(i).getType() == TYPE_MULTIPAGE_START) {
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", items.get(i).getTitle()).apply();
                MultiPage.add(items.get(i).getTitle());
            } else if (items.get(i).getType() == TYPE_MULTIPAGE_END) {
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", MultiPage.get(MultiPage.size() - 1)).apply();
                MultiPage.remove(MultiPage.size() - 1);
            } else if (items.get(i).getType() == TYPE_NORMAL) {
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", items.get(i).getTitle()).apply();
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_text", items.get(i).getText()).apply();
            } else if (items.get(i).getType() == TYPE_MULTI) {
                String[] split = items.get(i).getTitle().split("\\|");
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item1_title", split[0]).apply();
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item2_title", split[1]).apply();
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item3_title", split[2]).apply();
                String[] split2 = items.get(i).getText().split("\\|");
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item1_text", split2[0]).apply();
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item2_text", split2[1]).apply();
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item3_text", split2[2]).apply();
            }
        }
    }

}

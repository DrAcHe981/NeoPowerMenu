package de.NeonSoft.neopowermenu.helpers;

import android.app.*;
import android.content.ClipData;
import android.content.Context;
import android.content.pm.PackageManager;
import android.nfc.INfcAdapter;
import android.os.*;
import android.support.annotation.BoolRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.util.Log;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.Preferences.*;

import java.util.*;

public class visibilityOrder_ListAdapter extends ArrayAdapter<String> {

    private Activity mContext;
    private LayoutInflater inflater;
    private ArrayList<Integer> itemsType;
    private ArrayList<String> itemsTitle;
    private ArrayList<Boolean> itemsHideDesc;
    private ArrayList<Boolean> itemsHideOnLockscreen;
    private ArrayList<String> itemsTexts;
    PackageManager pm;

    public static int TYPE_NORMAL = 0;
    public static int TYPE_MULTI = 1;
    public static int TYPE_MULTIPAGE_START = 2;
    public static int TYPE_MULTIPAGE_END = 3;

    public visibilityOrder_ListAdapter(Activity context,
                                       ArrayList<Integer> itemsType,
                                       ArrayList<String> itemsTitle,
                                       ArrayList<Boolean> itemsHideDesc,
                                       ArrayList<Boolean> itemsHideOnLockscreen,
                                       ArrayList<String> itemsTexts) {
        super(context, R.layout.visibilityorder_normal, itemsTitle);
        this.mContext = context;
        this.inflater = context.getLayoutInflater();
        this.itemsType = itemsType;
        this.itemsTitle = itemsTitle;
        this.itemsHideDesc = itemsHideDesc;
        this.itemsTexts = itemsTexts;
        this.itemsHideOnLockscreen = itemsHideOnLockscreen;
        pm = mContext.getPackageManager();
    }

    @NonNull
    @Override
    public View getView(final int position, View p2, @NonNull ViewGroup p3) {

        View InflatedView = p2;

        if (itemsType.get(position) == TYPE_NORMAL) {
            boolean hasDescription = false;
            InflatedView = inflater.inflate(R.layout.visibilityorder_normal, p3, false);

            LinearLayout itemHolder = (LinearLayout) InflatedView.findViewById(R.id.visibilityordernormalLinearLayout_item);
            TextView item = (TextView) InflatedView.findViewById(R.id.visibilityordernormal_item);
            String string = itemsTitle.get(position);
            if (itemsTexts.get(position).isEmpty()) {
                if (string.contains(".")) {
                    try {
                        string = pm.getApplicationInfo(string.split("/")[0], 0).loadLabel(pm).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                    }
                } else {
                    try {
                        string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + itemsTitle.get(position), "string", MainActivity.class.getPackage().getName()));
                    } catch (Throwable t) {
                        try {
                            string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_" + itemsTitle.get(position), "string", MainActivity.class.getPackage().getName()));
                        } catch (Throwable t1) {
                        }
                    }
                }
            } else {
                string = itemsTexts.get(position);
            }
            item.setText(string);

            ImageView HideDescription = (ImageView) InflatedView.findViewById(R.id.visibilityordernormalImageView_HideDescription);
            ImageView HideOnLockscreen = (ImageView) InflatedView.findViewById(R.id.visibilityordernormalImageView_HideOnLockscreen);

            try {
                if (!mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + this.itemsTitle.get(position) + "Desc", "string", MainActivity.class.getPackage().getName())).equalsIgnoreCase("")) {
                    HideDescription.setVisibility(itemsHideDesc.get(position) ? View.GONE : View.VISIBLE);
                    hasDescription = true;
                }
            } catch (Throwable t) {
            }
            HideOnLockscreen.setVisibility(itemsHideOnLockscreen.get(position) ? View.GONE : View.VISIBLE);

            itemHolder.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View p1) {
                    slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                    dialogFragment.setContext(mContext);
                    dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                    dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

                        @Override
                        public void onListItemClick(int listpos, String text) {
                            if (PreferencesVisibilityOrderFragment.PowerMenuItems[listpos].equals("AppShortcut")) {
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
                                PreferencesVisibilityOrderFragment.loadAppsTask = new PreferencesVisibilityOrderFragment.loadApps().execute(TYPE_NORMAL, position);
                            } else {
                                boolean hideDesc = itemsHideDesc.get(position);
                                boolean hideOnLockscreen = itemsHideOnLockscreen.get(position);
                                removeAt(position);
                                insertAt(position, new Object[]{TYPE_NORMAL, PreferencesVisibilityOrderFragment.PowerMenuItems[listpos], hideDesc, hideOnLockscreen, ""});
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
                    int selItem = 0;
                    if (itemsTitle.get(position).contains(".")) {
                        selItem = 18;
                    } else {
                        for (int i = 0; i < PreferencesVisibilityOrderFragment.PowerMenuItems.length; i++) {
                            if (PreferencesVisibilityOrderFragment.PowerMenuItems[i].equalsIgnoreCase(itemsTitle.get(position))) {
                                selItem = i;
                                break;
                            }
                        }
                    }
                    dialogFragment.setList(ListView.CHOICE_MODE_SINGLE, PreferencesVisibilityOrderFragment.PowerMenuItemsTexts, selItem, true);
                    dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[4]);
                    dialogFragment.showDialog(R.id.dialog_container);
                }
            });

            LinearLayout EditAppearanceBehaviour = (LinearLayout) InflatedView.findViewById(R.id.visibilityordernormalLinearLayout_EditBehaviour);
            final boolean finalHasDescription = hasDescription;
            EditAppearanceBehaviour.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                    dialogFragment.setContext(mContext);
                    dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                    final ArrayList<String> options = new ArrayList<String>();
                    ArrayList<Boolean> checked = new ArrayList<Boolean>();
                    if (finalHasDescription) {
                        options.add(mContext.getString(R.string.visibilityOrder_HideDesc));
                        checked.add(itemsHideDesc.get(position));
                    }
                    options.add(mContext.getString(R.string.visibilityOrder_HideOnLockscreen));
                    checked.add(itemsHideOnLockscreen.get(position));
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
                            String listResult = resultBundle.getString(slideDownDialogFragment.RESULT_LIST, "");

                            itemsTexts.set(position, (inputResult.isEmpty() ? "" : inputResult));

                            if(options.size() >= 2) {
                                itemsHideDesc.set(position, false);
                                itemsHideOnLockscreen.set(position, false);
                            } else {
                                itemsHideOnLockscreen.set(position, false);
                            }

                            if (!listResult.isEmpty()) {
                                for (String result : listResult.split(",")) {
                                    if(options.size() >= 2) {
                                        if (Integer.parseInt(result) == 0) {
                                            itemsHideDesc.set(position, true);
                                        } else if (Integer.parseInt(result) == 1) {
                                            itemsHideOnLockscreen.set(position, true);
                                        }
                                    } else {
                                        if (Integer.parseInt(result) == 0) {
                                            itemsHideOnLockscreen.set(position, true);
                                        }
                                    }
                                }
                            }

                            notifyDataSetChanged();

                        }

                        @Override
                        public void onTouchOutside() {

                        }
                    });
                    dialogFragment.setText(mContext.getString(R.string.visibilityOrder_EditAppearanceBehaviourDesc));
                    dialogFragment.addInput(mContext.getString(R.string.visibilityOrder_EditAppearanceBehaviourText), itemsTexts.get(position), true, null);
                    dialogFragment.setList(ListView.CHOICE_MODE_MULTIPLE, options, -1, false);
                    dialogFragment.setListChecks(checked);
                    dialogFragment.setListAllowEmpty(true);
                    dialogFragment.setNegativeButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                    dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_SAVE]);
                    dialogFragment.showDialog(R.id.dialog_container);
                }
            });

        } else if (itemsType.get(position) == TYPE_MULTI) {
            InflatedView = inflater.inflate(R.layout.visibilityorder_multi, p3, false);

            final String[] items = itemsTitle.get(position).split("\\|");

            final TextView item1 = (TextView) InflatedView.findViewById(R.id.visibilityordermulti_item1);
            String string = "";
            if (!itemsTexts.get(position).split("\\|")[0].equalsIgnoreCase("< default >")) {
                string = itemsTexts.get(position).split("\\|")[0];
            }
            if(string.isEmpty()) {
                string = items[0];
                if (string.contains(".")) {
                    try {
                        string = pm.getApplicationInfo(string.split("/")[0], 0).loadLabel(pm).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                    }
                } else {
                    try {
                        string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + items[0], "string", MainActivity.class.getPackage().getName()));
                    } catch (Throwable t) {
                        try {
                            string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_" + items[0], "string", MainActivity.class.getPackage().getName()));
                        } catch (Throwable t1) {
                        }
                    }
                }
            }
            item1.setText(string);

            TextView item2 = (TextView) InflatedView.findViewById(R.id.visibilityordermulti_item2);
            String string2 = "";
            if (items.length >= 2) {
                if (itemsTexts.get(position).split("\\|").length >= 2 && !itemsTexts.get(position).split("\\|")[1].equalsIgnoreCase("< default >")) {
                    string2 = itemsTexts.get(position).split("\\|")[1];
                }
                if(string2.isEmpty()) {
                    string2 = items[1];
                    if (string2.contains(".")) {
                        try {
                            string2 = pm.getApplicationInfo(string2.split("/")[0], 0).loadLabel(pm).toString();
                        } catch (PackageManager.NameNotFoundException e) {
                        }
                    } else {
                        try {
                            string2 = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + items[1], "string", MainActivity.class.getPackage().getName()));
                        } catch (Throwable t) {
                            try {
                                string2 = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_" + items[1], "string", MainActivity.class.getPackage().getName()));
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
            if (items.length == 3) {
                if (itemsTexts.get(position).split("\\|").length == 3 && !itemsTexts.get(position).split("\\|")[2].equalsIgnoreCase("< default >")) {
                    string3 = itemsTexts.get(position).split("\\|")[2];
                }
                if(string3.isEmpty()) {
                    string3 = items[2];
                    if (string3.contains(".")) {
                        try {
                            string3 = pm.getApplicationInfo(string3.split("/")[0], 0).loadLabel(pm).toString();
                        } catch (PackageManager.NameNotFoundException e) {
                        }
                    } else {
                        try {
                            string3 = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + items[2], "string", MainActivity.class.getPackage().getName()));
                        } catch (Throwable t) {
                            try {
                                string3 = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_" + items[2], "string", MainActivity.class.getPackage().getName()));
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
                    slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                    dialogFragment.setContext(mContext);
                    dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                    dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

                        @Override
                        public void onListItemClick(int listpos, String text) {

                            if (PreferencesVisibilityOrderFragment.PowerMenuItems[listpos].equals("AppShortcut")) {
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
                                PreferencesVisibilityOrderFragment.loadAppsTask = new PreferencesVisibilityOrderFragment.loadApps().execute(TYPE_MULTI, position, "[THIS]|" + (items.length >= 2 ? items[1] : "Empty") + "|" + (items.length == 3 ? items[2] : "Empty"));
                            } else {
                                boolean hideDesc = itemsHideDesc.get(position);
                                boolean hideOnLockscreen = itemsHideOnLockscreen.get(position);
                                String[] texts = itemsTexts.get(position).split("\\|");
                                removeAt(position);
                                insertAt(position, new Object[]{TYPE_MULTI, PreferencesVisibilityOrderFragment.PowerMenuItems[listpos] + "|" + (items.length >= 2 ? items[1] : "Empty") + "|" + (items.length == 3 ? items[2] : "Empty"), hideDesc, hideOnLockscreen, "< default >|"+(texts.length >= 2 ? texts[1] : "< default >")+"|"+(texts.length == 3 ? texts[2] : "< default >")});
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
                    int selItem = 0;
                    if (items[0].contains(".")) {
                        selItem = 18;
                    } else {
                        for (int i = 0; i < PreferencesVisibilityOrderFragment.PowerMenuItems.length; i++) {
                            if (PreferencesVisibilityOrderFragment.PowerMenuItems[i].equalsIgnoreCase(items[0])) {
                                selItem = i;
                                break;
                            }
                        }
                    }
                    dialogFragment.setList(ListView.CHOICE_MODE_SINGLE, PreferencesVisibilityOrderFragment.PowerMenuItemsTexts, selItem, true);
                    dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[4]);
                    dialogFragment.showDialog(R.id.dialog_container);
                }
            });
            item2.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View p1) {
                    slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                    dialogFragment.setContext(mContext);
                    dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                    dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

                        @Override
                        public void onListItemClick(int listpos, String text) {

                            if (PreferencesVisibilityOrderFragment.PowerMenuItems[listpos].equals("AppShortcut")) {
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
                                PreferencesVisibilityOrderFragment.loadAppsTask = new PreferencesVisibilityOrderFragment.loadApps().execute(TYPE_MULTI, position, items[0] + "|[THIS]|" + (items.length == 3 ? items[2] : "Empty"));
                            } else {
                                boolean hideDesc = itemsHideDesc.get(position);
                                boolean hideOnLockscreen = itemsHideOnLockscreen.get(position);
                                String[] texts = itemsTexts.get(position).split("\\|");
                                removeAt(position);
                                insertAt(position, new Object[]{TYPE_MULTI, items[0] + "|" + PreferencesVisibilityOrderFragment.PowerMenuItems[listpos] + "|" + (items.length == 3 ? items[2] : "Empty"), hideDesc, hideOnLockscreen, texts[0]+"|"+texts[1]+"|"+(texts.length == 3 ? texts[2] : "< default >")});
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
                    int selItem = 0;
                    if (items.length >= 2) {
                        if (items[1].contains(".")) {
                            selItem = 18;
                        } else {
                            for (int i = 0; i < PreferencesVisibilityOrderFragment.PowerMenuItems.length; i++) {
                                if (PreferencesVisibilityOrderFragment.PowerMenuItems[i].equalsIgnoreCase(items[1])) {
                                    selItem = i;
                                    break;
                                }
                            }
                        }
                    }
                    dialogFragment.setList(ListView.CHOICE_MODE_SINGLE, PreferencesVisibilityOrderFragment.PowerMenuItemsTexts, selItem, true);
                    dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[4]);
                    dialogFragment.showDialog(R.id.dialog_container);
                }
            });
            item3.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View p1) {
                    slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                    dialogFragment.setContext(mContext);
                    dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                    dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

                        @Override
                        public void onListItemClick(int listpos, String text) {
                            if (PreferencesVisibilityOrderFragment.PowerMenuItems[listpos].equals("AppShortcut")) {
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
                                PreferencesVisibilityOrderFragment.loadAppsTask = new PreferencesVisibilityOrderFragment.loadApps().execute(TYPE_MULTI, position, items[0] + "|" + items[1] + "|[THIS]");
                            } else {
                                boolean hideDesc = itemsHideDesc.get(position);
                                boolean hideOnLockscreen = itemsHideOnLockscreen.get(position);
                                String[] texts = itemsTexts.get(position).split("\\|");
                                removeAt(position);
                                insertAt(position, new Object[]{TYPE_MULTI, items[0] + "|" + items[1] + "|" + PreferencesVisibilityOrderFragment.PowerMenuItems[listpos], hideDesc, hideOnLockscreen, texts[0]+"|"+texts[1]+"|"+texts[2]});
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
                    int selItem = 0;
                    if (items.length == 3) {
                        if (items[0].contains(".")) {
                            selItem = 18;
                        } else {
                            for (int i = 0; i < PreferencesVisibilityOrderFragment.PowerMenuItems.length; i++) {
                                if (PreferencesVisibilityOrderFragment.PowerMenuItems[i].equalsIgnoreCase(items[2])) {
                                    selItem = i;
                                    break;
                                }
                            }
                        }
                    }
                    dialogFragment.setList(ListView.CHOICE_MODE_SINGLE, PreferencesVisibilityOrderFragment.PowerMenuItemsTexts, selItem, true);
                    dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[4]);
                    dialogFragment.showDialog(R.id.dialog_container);
                }
            });

            ImageView HideOnLockscreen = (ImageView) InflatedView.findViewById(R.id.visibilityordermultiImageView_HideOnLockscreen);
            HideOnLockscreen.setVisibility(itemsHideOnLockscreen.get(position) ? View.GONE : View.VISIBLE);

            LinearLayout EditAppearanceBehaviour = (LinearLayout) InflatedView.findViewById(R.id.visibilityordermultiLinearLayout_EditBehaviour);
            EditAppearanceBehaviour.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
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

                            itemsTexts.set(position, (inputResult.isEmpty() ? "< default >" : inputResult) + "|" + (inputResult2.isEmpty() ? "< default >" : inputResult2) + "|" + (inputResult3.isEmpty() ? "< default >" : inputResult3));

                            itemsHideOnLockscreen.set(position, false);

                            if (!listResult.isEmpty()) {
                                for (String result : listResult.split(",")) {
                                    if (Integer.parseInt(result) == 0) {
                                        itemsHideOnLockscreen.set(position, true);
                                    }
                                }
                            }

                            notifyDataSetChanged();

                        }

                        @Override
                        public void onTouchOutside() {

                        }
                    });
                    dialogFragment.setText(mContext.getString(R.string.visibilityOrder_EditAppearanceBehaviourDesc));
                    dialogFragment.addInput(mContext.getString(R.string.visibilityOrder_EditAppearanceBehaviourText1), itemsTexts.get(position).split("\\|")[0].replace("< default >",""), true, null);
                    dialogFragment.addInput(mContext.getString(R.string.visibilityOrder_EditAppearanceBehaviourText2), (itemsTexts.get(position).split("\\|").length >= 2 ? itemsTexts.get(position).split("\\|")[1].replace("< default >","") : ""), true, null);
                    dialogFragment.addInput(mContext.getString(R.string.visibilityOrder_EditAppearanceBehaviourText3), (itemsTexts.get(position).split("\\|").length == 3 ? itemsTexts.get(position).split("\\|")[2].replace("< default >","") : ""), true, null);
                    ArrayList<String> options = new ArrayList<String>();
                    ArrayList<Boolean> checked = new ArrayList<Boolean>();
                    options.add(mContext.getString(R.string.visibilityOrder_HideOnLockscreen));
                    checked.add(itemsHideOnLockscreen.get(position));
                    dialogFragment.setList(ListView.CHOICE_MODE_MULTIPLE, options, -1, false);
                    dialogFragment.setListChecks(checked);
                    dialogFragment.setListAllowEmpty(true);
                    dialogFragment.setNegativeButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                    dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_SAVE]);
                    dialogFragment.showDialog(R.id.dialog_container);
                }
            });

        } else if (itemsType.get(position) == TYPE_MULTIPAGE_START) {
            InflatedView = inflater.inflate(R.layout.visibilityorder_normal, p3, false);

            TextView item = (TextView) InflatedView.findViewById(R.id.visibilityordernormal_item);
            item.setClickable(false);
            item.setText(mContext.getString(R.string.visibilityOrder_MultiPage).split("\\|")[0]);

            ImageView HideOnLockscreen = (ImageView) InflatedView.findViewById(R.id.visibilityordernormalImageView_HideOnLockscreen);
            HideOnLockscreen.setVisibility(itemsHideOnLockscreen.get(position) ? View.GONE : View.VISIBLE);

            LinearLayout EditAppearanceBehaviour = (LinearLayout) InflatedView.findViewById(R.id.visibilityordernormalLinearLayout_EditBehaviour);
            EditAppearanceBehaviour.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
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
                            String listResult = resultBundle.getString(slideDownDialogFragment.RESULT_LIST, "");

                            itemsHideOnLockscreen.set(position, false);

                            if (!listResult.isEmpty()) {
                                for (String result : listResult.split(",")) {
                                    if (Integer.parseInt(result) == 0) {
                                        itemsHideOnLockscreen.set(position, true);
                                    }
                                }
                            }

                            notifyDataSetChanged();

                        }

                        @Override
                        public void onTouchOutside() {

                        }
                    });
                    dialogFragment.setText(mContext.getString(R.string.visibilityOrder_EditAppearanceBehaviourDesc));
                    ArrayList<String> options = new ArrayList<String>();
                    ArrayList<Boolean> checked = new ArrayList<Boolean>();
                    options.add(mContext.getString(R.string.visibilityOrder_HideOnLockscreen));
                    checked.add(itemsHideOnLockscreen.get(position));
                    dialogFragment.setList(ListView.CHOICE_MODE_MULTIPLE, options, -1, false);
                    dialogFragment.setListChecks(checked);
                    dialogFragment.setListAllowEmpty(true);
                    dialogFragment.setNegativeButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                    dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_SAVE]);
                    dialogFragment.showDialog(R.id.dialog_container);
                }
            });
        } else if (itemsType.get(position) == TYPE_MULTIPAGE_END) {
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

    public void addItem(int type, String item, boolean hideDesc, boolean hideOnLockscreen, String text) {
        itemsType.add(type);
        itemsTitle.add(item);
        itemsHideDesc.add(hideDesc);
        itemsHideOnLockscreen.add(hideOnLockscreen);
        itemsTexts.add(text);
        notifyDataSetChanged();
        //outputSorting();
    }

    public Object[] getItemAt(int position) {
        return new Object[]{itemsType.get(position), itemsTitle.get(position), itemsHideDesc.get(position), itemsHideOnLockscreen.get(position), itemsTexts.get(position)};
    }

    public void insertAt(int position, Object[] item) {
        itemsType.add(position, Integer.parseInt(item[0].toString()));
        itemsTitle.add(position, item[1].toString());
        itemsHideDesc.add(position, (boolean) item[2]);
        itemsHideOnLockscreen.add(position, (boolean) item[3]);
        itemsTexts.add(position, item[4].toString());
        notifyDataSetChanged();
        //outputSorting();
    }

    public void move(int from, int to) {
        if (from != to) {
            Object[] item = getItemAt(from);
            if ((int) item[0] == TYPE_MULTIPAGE_START || (int) item[0] == TYPE_MULTIPAGE_END) {
                boolean validMove = true;
                itemsType.remove(from);
                itemsTitle.remove(from);
                itemsHideDesc.remove(from);
                itemsHideOnLockscreen.remove(from);
                itemsTexts.remove(from);
                itemsType.add(to, (int) item[0]);
                itemsTitle.add(to, item[1].toString());
                itemsHideDesc.add(to, (boolean) item[2]);
                itemsHideOnLockscreen.add(to, (boolean) item[3]);
                itemsTexts.add(to, item[4].toString());
                ArrayList<String> pages = new ArrayList<>();
                for (int i = 0; i < itemsType.size(); i++) {
                    try {
                        Object[] checkItem = getItemAt(i);
                        if (MainActivity.DeepLogging)
                            Log.i("NPM:vOPC", "(" + String.format("%02d", i) + ")> " + checkItem[0] + " | " + checkItem[1]);
                        if ((int) checkItem[0] == TYPE_MULTIPAGE_START) {
                            pages.add(checkItem[1].toString());
                        } else if ((int) checkItem[0] == TYPE_MULTIPAGE_END) {
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
                    itemsType.remove(to);
                    itemsTitle.remove(to);
                    itemsHideDesc.remove(to);
                    itemsHideOnLockscreen.remove(to);
                    itemsTexts.remove(to);
                    itemsType.add(from, (int) item[0]);
                    itemsTitle.add(from, item[1].toString());
                    itemsHideDesc.add(from, (boolean) item[2]);
                    itemsHideOnLockscreen.add(from, (boolean) item[3]);
                    itemsTexts.add(from, item[4].toString());
                }
                notifyDataSetChanged();
            } else {
                //Toast.makeText(mContext, "normal move from "+from+" to "+to, Toast.LENGTH_SHORT).show();
                itemsType.remove(from);
                itemsTitle.remove(from);
                itemsHideDesc.remove(from);
                itemsHideOnLockscreen.remove(from);
                itemsTexts.remove(from);
                itemsType.add(to, (int) item[0]);
                itemsTitle.add(to, item[1].toString());
                itemsHideDesc.add(to, (boolean) item[2]);
                itemsHideOnLockscreen.add(to, (boolean) item[3]);
                itemsTexts.add(to, item[4].toString());
                notifyDataSetChanged();
            }
        }

    }

    public void removeAt(int position) {
        if ((int) itemsType.get(position) == TYPE_MULTIPAGE_START) {
            Object[] item = getItemAt(position);
            int removingLayers = 0;
            while (itemsType.size() > position) {
                Object[] checkItem = getItemAt(position);
                if ((int) checkItem[0] == TYPE_MULTIPAGE_END) {
                    if (removingLayers > 0) {
                        itemsType.remove(position);
                        itemsTitle.remove(position);
                        itemsHideDesc.remove(position);
                        itemsHideOnLockscreen.remove(position);
                        itemsTexts.remove(position);
                        removingLayers--;
                        //Toast.makeText(mContext,"- Setting layer count to "+removingLayers,Toast.LENGTH_SHORT).show();
                        if (removingLayers == 0) {
                            break;
                        }
                    }
                } else {
                    if ((int) checkItem[0] == TYPE_MULTIPAGE_START) {
                        removingLayers++;
                        //Toast.makeText(mContext,"+ Setting layer count to "+removingLayers,Toast.LENGTH_SHORT).show();
                    }
                    itemsType.remove(position);
                    itemsTitle.remove(position);
                    itemsHideDesc.remove(position);
                    itemsHideOnLockscreen.remove(position);
                    itemsTexts.remove(position);
                }
            }
        } else if ((int) itemsType.get(position) == TYPE_MULTIPAGE_END) {
        } else if ((int) itemsType.get(position) == TYPE_NORMAL || (int) itemsType.get(position) == TYPE_MULTI) {
            itemsType.remove(position);
            itemsTitle.remove(position);
            itemsHideDesc.remove(position);
            itemsHideOnLockscreen.remove(position);
            itemsTexts.remove(position);
        }
        notifyDataSetChanged();
        //outputSorting();
    }


    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    public void outputSorting() {
        ArrayList<String> MultiPage = new ArrayList<String>();
        MainActivity.orderPrefs.edit().clear().apply();
        for (int i = 0; i < itemsTitle.size(); i++) {
            MainActivity.orderPrefs.edit().putInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", itemsType.get(i)).apply();
            MainActivity.orderPrefs.edit().putBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideDesc", itemsHideDesc.get(i)).apply();
            MainActivity.orderPrefs.edit().putBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideOnLockscreen", itemsHideOnLockscreen.get(i)).apply();
            if (itemsType.get(i) == TYPE_MULTIPAGE_START) {
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", itemsTitle.get(i)).apply();
                MultiPage.add(itemsTitle.get(i));
            } else if (itemsType.get(i) == TYPE_MULTIPAGE_END) {
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", MultiPage.get(MultiPage.size() - 1)).apply();
                MultiPage.remove(MultiPage.size() - 1);
            } else if (itemsType.get(i) == TYPE_NORMAL) {
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", itemsTitle.get(i)).apply();
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_text", itemsTexts.get(i)).apply();
            } else if (itemsType.get(i) == TYPE_MULTI) {
                String[] split = itemsTitle.get(i).split("\\|");
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item1_title", split[0]).apply();
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item2_title", (split.length >= 2 ? split[1] : "Empty")).apply();
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item3_title", (split.length == 3 ? split[2] : "Empty")).apply();
                String[] split2 = itemsTexts.get(i).split("\\|");
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item1_text", split2[0]).apply();
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item2_text", (split2.length >= 2 ? split2[1] : "")).apply();
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item3_text", (split2.length == 3 ? split2[2] : "")).apply();
            }
        }
    }

}

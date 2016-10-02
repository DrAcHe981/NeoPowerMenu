package de.NeonSoft.neopowermenu.helpers;

import android.app.*;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.*;
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
    PackageManager pm;

    public static int TYPE_NORMAL = 0;
    public static int TYPE_MULTI = 1;
    public static int TYPE_MULTIPAGE_START = 2;
    public static int TYPE_MULTIPAGE_END = 3;

    public visibilityOrder_ListAdapter(Activity context,
                                       ArrayList<Integer> itemsType,
                                       ArrayList<String> itemsTitle) {
        super(context, R.layout.visibilityorder_normal, itemsTitle);
        this.mContext = context;
        this.inflater = context.getLayoutInflater();
        this.itemsType = itemsType;
        this.itemsTitle = itemsTitle;
        pm = mContext.getPackageManager();
    }

    @Override
    public View getView(final int position, View p2, ViewGroup p3) {

        View InflatedView = null;

        if (itemsType.get(position) == TYPE_NORMAL) {
            InflatedView = inflater.inflate(R.layout.visibilityorder_normal, p3, false);

            TextView item = (TextView) InflatedView.findViewById(R.id.visibilityordernormal_item);
            String string = itemsTitle.get(position);
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
            item.setText(string);

            LinearLayout ItemCheckBoxHolder = (LinearLayout) InflatedView.findViewById(R.id.visibilityordernormal_LinearLayout_HideDesc);
            final CheckBox ItemCheckBox = (CheckBox) InflatedView.findViewById(R.id.visibilityordernormal_hideDescCheckBox);
            ItemCheckBox.setClickable(false);
            ItemCheckBox.setFocusable(false);
            try {
                if (!mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + this.itemsTitle.get(position) + "Desc", "string", MainActivity.class.getPackage().getName())).equalsIgnoreCase("")) {
                    ItemCheckBox.setChecked(MainActivity.preferences.getBoolean(this.itemsTitle.get(position) + "_HideDesc", false));
                    ItemCheckBoxHolder.setVisibility(View.VISIBLE);
                    ItemCheckBoxHolder.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View p1) {
                            ItemCheckBox.setChecked(!ItemCheckBox.isChecked());
                            MainActivity.preferences.edit().putBoolean(itemsTitle.get(position) + "_HideDesc", ItemCheckBox.isChecked()).apply();
                        }
                    });
                }
            } catch (Throwable t) {
            }

            item.setOnClickListener(new OnClickListener() {

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
                                removeAt(position);
                                insertAt(position, new Object[]{TYPE_NORMAL, PreferencesVisibilityOrderFragment.PowerMenuItems[listpos]});
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

        } else if (itemsType.get(position) == TYPE_MULTI) {
            InflatedView = inflater.inflate(R.layout.visibilityorder_multi, p3, false);

            final String[] items = itemsTitle.get(position).split("\\|");

            final TextView item1 = (TextView) InflatedView.findViewById(R.id.visibilityordermulti_item1);
            String string = items[0];
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
            item1.setText(string);

            TextView item2 = (TextView) InflatedView.findViewById(R.id.visibilityordermulti_item2);
            if (items.length >= 2) {
                string = items[1];
                if (string.contains(".")) {
                    try {
                        string = pm.getApplicationInfo(string.split("/")[0], 0).loadLabel(pm).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                    }
                } else {
                    try {
                        string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + items[1], "string", MainActivity.class.getPackage().getName()));
                    } catch (Throwable t) {
                        try {
                            string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_" + items[1], "string", MainActivity.class.getPackage().getName()));
                        } catch (Throwable t1) {
                        }
                    }
                }
                item2.setText(string);
            } else {
                item2.setText(PreferencesVisibilityOrderFragment.PowerMenuItemsTexts[0]);
            }

            TextView item3 = (TextView) InflatedView.findViewById(R.id.visibilityordermulti_item3);
            if (items.length == 3) {
                string = items[2];
                if (string.contains(".")) {
                    try {
                        string = pm.getApplicationInfo(string.split("/")[0], 0).loadLabel(pm).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                    }
                } else {
                    try {
                        string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + items[2], "string", MainActivity.class.getPackage().getName()));
                    } catch (Throwable t) {
                        try {
                            string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_" + items[2], "string", MainActivity.class.getPackage().getName()));
                        } catch (Throwable t1) {
                        }
                    }
                }
                item3.setText(string);
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
                                removeAt(position);
                                insertAt(position, new Object[]{TYPE_MULTI, PreferencesVisibilityOrderFragment.PowerMenuItems[listpos] + "|" + (items.length >= 2 ? items[1] : "Empty") + "|" + (items.length == 3 ? items[2] : "Empty")});
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
                                removeAt(position);
                                insertAt(position, new Object[]{TYPE_MULTI, items[0] + "|" + PreferencesVisibilityOrderFragment.PowerMenuItems[listpos] + "|" + (items.length == 3 ? items[2] : "Empty")});
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
                                removeAt(position);
                                insertAt(position, new Object[]{TYPE_MULTI, items[0] + "|" + (items.length >= 2 ? items[1] : "Empty") + "|" + PreferencesVisibilityOrderFragment.PowerMenuItems[listpos]});
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
        } else if (itemsType.get(position) == TYPE_MULTIPAGE_START) {
            InflatedView = inflater.inflate(R.layout.visibilityorder_normal, p3, false);

            TextView item = (TextView) InflatedView.findViewById(R.id.visibilityordernormal_item);
            item.setClickable(false);
            item.setText(mContext.getString(R.string.visibilityOrder_MultiPage).split("\\|")[0]);
        } else if (itemsType.get(position) == TYPE_MULTIPAGE_END) {
            InflatedView = inflater.inflate(R.layout.visibilityorder_normal, p3, false);

            TextView item = (TextView) InflatedView.findViewById(R.id.visibilityordernormal_item);
            LinearLayout dragHandle = (LinearLayout) InflatedView.findViewById(R.id.drag_handle);
            //dragHandle.setVisibility(View.GONE);
            item.setClickable(false);
            item.setText(mContext.getString(R.string.visibilityOrder_MultiPage).split("\\|")[1]);
        }
        InflatedView.setClickable(false);

        return InflatedView;
    }

    public void addItem(int type, String item) {
        itemsType.add(type);
        itemsTitle.add(item);
        notifyDataSetChanged();
        //outputSorting();
    }

    public Object[] getItemAt(int position) {
        return new Object[]{itemsType.get(position), itemsTitle.get(position)};
    }

    public void insertAt(int position, Object[] item) {
        itemsType.add(position, Integer.parseInt(item[0].toString()));
        itemsTitle.add(position, item[1].toString());
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
                itemsType.add(to, (int) item[0]);
                itemsTitle.add(to, item[1].toString());
                ArrayList<String> pages = new ArrayList<>();
                for (int i = 0; i < itemsType.size(); i++) {
                    try {
                        Object[] checkItem = getItemAt(i);
                        if(MainActivity.DeepLogging) Log.i("NPM:vOPC","("+String.format("%02d",i)+")> "+checkItem[0] + " | " + checkItem[1]);
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
                    Toast.makeText(mContext,mContext.getString(R.string.visibilityOrder_InvalidMultiPageMove),Toast.LENGTH_LONG).show();
                    itemsType.remove(to);
                    itemsTitle.remove(to);
                    itemsType.add(from, (int) item[0]);
                    itemsTitle.add(from, item[1].toString());
                }
                notifyDataSetChanged();
            } else {
                //Toast.makeText(mContext, "normal move from "+from+" to "+to, Toast.LENGTH_SHORT).show();
                itemsType.remove(from);
                itemsTitle.remove(from);
                itemsType.add(to, (int) item[0]);
                itemsTitle.add(to, item[1].toString());
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
                }
            }
        } else if ((int) itemsType.get(position) == TYPE_MULTIPAGE_END) {
        } else if ((int) itemsType.get(position) == TYPE_NORMAL || (int) itemsType.get(position) == TYPE_MULTI) {
            itemsType.remove(position);
            itemsTitle.remove(position);
        }
        notifyDataSetChanged();
        //outputSorting();
    }

    public void outputSorting() {
        ArrayList<String> MultiPage = new ArrayList<String>();
        MainActivity.orderPrefs.edit().clear().apply();
        for (int i = 0; i < itemsTitle.size(); i++) {
            if (itemsType.get(i) == TYPE_MULTIPAGE_START) {
                MainActivity.orderPrefs.edit().putInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", TYPE_MULTIPAGE_START).apply();
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", itemsTitle.get(i)).apply();
                MultiPage.add(itemsTitle.get(i));
            } else if (itemsType.get(i) == TYPE_MULTIPAGE_END) {
                MainActivity.orderPrefs.edit().putInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", TYPE_MULTIPAGE_END).apply();
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", MultiPage.get(MultiPage.size() - 1)).apply();
                MultiPage.remove(MultiPage.size() - 1);
            } else if (itemsType.get(i) == TYPE_NORMAL) {
                MainActivity.orderPrefs.edit().putInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", itemsType.get(i)).apply();
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", itemsTitle.get(i)).apply();
            } else if (itemsType.get(i) == TYPE_MULTI) {
                String[] split = itemsTitle.get(i).split("\\|");
                MainActivity.orderPrefs.edit().putInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", itemsType.get(i)).apply();
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item1_title", split[0]).apply();
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item2_title", (split.length >= 2 ? split[1] : "Empty")).apply();
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item3_title", (split.length == 3 ? split[2] : "Empty")).apply();
            }
        }
    }

}

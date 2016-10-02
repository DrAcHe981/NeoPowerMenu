package de.NeonSoft.neopowermenu.Preferences;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser;
import android.content.pm.ResolveInfo;
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

import java.util.*;

public class PreferencesVisibilityOrderFragment extends Fragment {

    public static Activity mContext;

    public static boolean appsListFullyParsed;
    static ArrayList<String> appsNames = new ArrayList<String>();
    static ArrayList<String> appsPackages = new ArrayList<String>();
    static ArrayList<String> appsNamesFiltered = new ArrayList<String>();
    static ArrayList<String> appsPackagesFiltered = new ArrayList<String>();
    public static AsyncTask loadAppsTask;
    public static slideDownDialogFragment loadAppsDialog;

    LinearLayout LinearLayout_Add;

    DragSortListView DSLV_List;
    public static visibilityOrder_ListAdapter adapter;

    public static LinearLayout LinearLayout_Progress;

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
            "MediaNext"};
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(!MainActivity.visibleFragment.equalsIgnoreCase("tour")) {
            MainActivity.visibleFragment = "VisibilityOrder";
        }

        mContext = getActivity();

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
                getString(R.string.powerMenuMain_MediaNext)};

        View InflatedView = inflater.inflate(R.layout.visibilityorder, container, false);

        LinearLayout_Add = (LinearLayout) InflatedView.findViewById(R.id.visibilityorderLinearLayout_Add);

        DSLV_List = (DragSortListView) InflatedView.findViewById(R.id.visibilityorderDSLV_List);

        DSLV_List.setDropListener(onDrop);
        DSLV_List.setRemoveListener(onRemove);
        DSLV_List.setDragEnabled(true);
        DSLV_List.setDragScrollProfile(ssProfile);
        DSLV_List.setFastScrollEnabled(true);

        ArrayList<Integer> types = new ArrayList<Integer>(Arrays.asList(new Integer[]{}));
        ArrayList<String> items = new ArrayList<String>(Arrays.asList(new String[]{}));
        //int i = 0;
        ArrayList<String> MultiPage = new ArrayList<String>();
        for (int i = 0; i < MainActivity.orderPrefs.getAll().keySet().size(); i++) {
            //Log.i("NPM:visibilityorder","Loading item "+(MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+i+
            //				"\nType: "+MainActivity.orderPrefs.getInt((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+ i+"_item_type",-1));
            if (MainActivity.orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", -1) != -1) {
                types.add(MainActivity.orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", adapter.TYPE_NORMAL));
                if (MainActivity.orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", adapter.TYPE_NORMAL) == adapter.TYPE_NORMAL) {
                    items.add(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null"));
                    //Log.i("NPM:visibilityorder","Content(single): "+items.get(i));
                } else if (MainActivity.orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", adapter.TYPE_NORMAL) == adapter.TYPE_MULTI) {
                    items.add(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item1_title", "null") + "|" +
                            MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item2_title", "null") + "|" +
                            MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item3_title", "null"));
                    //Log.i("NPM:visibilityorder","Content(multi): "+items.get(i));
                } else if (MainActivity.orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", -1) == visibilityOrder_ListAdapter.TYPE_MULTIPAGE_START) {
                    //types.add(MainActivity.orderPrefs.getInt((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+i+"_item_type",adapter.TYPE_NORMAL));
                    items.add(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null"));
                    MultiPage.add(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null"));
                    //Log.i("NPM:visibilityorder","Content: open multi page");
                } else if (MultiPage.size() > 0 && MainActivity.orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", -1) == visibilityOrder_ListAdapter.TYPE_MULTIPAGE_END) {
                    //types.add(visibilityOrder_ListAdapter.TYPE_MULTIPAGE_END);
                    items.add(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null"));
                    MultiPage.remove(MultiPage.size() - 1);
                    //Log.i("NPM:visibilityorder","Content: close multi page");
                }
            }
        }
        adapter = new visibilityOrder_ListAdapter(getActivity(), types, items);

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
                            slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                            dialogFragment.setContext(getActivity());
                            dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                            dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

                                @Override
                                public void onListItemClick(int position, String text) {
                                    if(PowerMenuItems[position].equals("AppShortcut")) {
                                        if(!appsListFullyParsed) {
                                            loadAppsDialog = new slideDownDialogFragment();
                                            loadAppsDialog.setContext(getActivity());
                                            loadAppsDialog.setFragmentManager(MainActivity.fragmentManager);
                                            loadAppsDialog.setListener(new slideDownDialogFragment.slideDownDialogInterface() {
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
                                                    loadAppsDialog.setPositiveButton("");
                                                    loadAppsTask.cancel(true);
                                                }

                                                @Override
                                                public void onTouchOutside() {

                                                }
                                            });
                                            loadAppsDialog.setCloseOnTouchOutside(false);
                                            loadAppsDialog.setCloseOnButtonClick(false);
                                            loadAppsDialog.setText(getString(R.string.login_Processing));
                                            loadAppsDialog.addProgressBar(true, false);
                                            loadAppsDialog.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                                            loadAppsDialog.showDialog(R.id.dialog_container);
                                        } else {
                                            loadAppsDialog = null;
                                        }
                                        loadAppsTask = new loadApps().execute(adapter.TYPE_NORMAL,-1);
                                    } else {
                                        adapter.addItem(visibilityOrder_ListAdapter.TYPE_NORMAL, PowerMenuItems[position]);
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
                            dialogFragment.setList(ListView.CHOICE_MODE_NONE, PreferencesVisibilityOrderFragment.PowerMenuItemsTexts, 0, true);
                            dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[4]);
                            dialogFragment.showDialog(R.id.dialog_container);
                        } else if (position == 1) {
                            slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                            dialogFragment.setContext(getActivity());
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
                                    if (resultBundle != null) {
                                            String[] split = resultBundle.getString(slideDownDialogFragment.RESULT_LIST).split(",");
                                            String splitStr = "";
                                            int type = visibilityOrder_ListAdapter.TYPE_MULTI;
                                            for (int i = 0; i < split.length; i++) {
                                                splitStr = splitStr + PowerMenuItems[Integer.parseInt(split[i])];
                                                splitStr = splitStr + (i >= split.length ? "" : "|");
                                            }
                                            //Toast.makeText(getActivity(),"Adding "+split.length+" items: "+splitStr,Toast.LENGTH_LONG).show();
                                            adapter.addItem(type, splitStr);
                                        }
                                }

                                @Override
                                public void onTouchOutside() {
                                    
                                }
                            });
                            dialogFragment.setText(getString(R.string.visibilityOrder_SelectMulti));
                            dialogFragment.setList(ListView.CHOICE_MODE_MULTIPLE, PreferencesVisibilityOrderFragment.PowerMenuItemsTexts, -1, false);
                            dialogFragment.setListLimit(3, false);
                            dialogFragment.setNegativeButton(getString(R.string.Dialog_Buttons).split("\\|")[4]);
                            dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[0]);
                            dialogFragment.showDialog(R.id.dialog_container);
                        } else if (position == 2) {
                            slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                            dialogFragment.setContext(getActivity());
                            dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                            dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

                                @Override
                                public void onListItemClick(int position, String text) {
                                    
                                    Date date = new Date();
                                    String groupName = helper.md5Crypto(date.getDay() + "." + date.getMonth() + "." + date.getYear() + "/" + date.getHours() + ":" + date.getMinutes() + ":" + date.getMinutes()+ ":" + date.getSeconds());
                                    adapter.addItem(visibilityOrder_ListAdapter.TYPE_MULTIPAGE_START, groupName);
                                    adapter.addItem(visibilityOrder_ListAdapter.TYPE_NORMAL, PowerMenuItems[position]);
                                    adapter.addItem(visibilityOrder_ListAdapter.TYPE_MULTIPAGE_END, groupName);
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
        if(!MainActivity.visibleFragment.equalsIgnoreCase("tour")) {
            MainActivity.actionbar.hideButton();
        }

        return InflatedView;
    }

    public static class loadApps extends AsyncTask<Object, String, String> {

        PackageManager pm;
        int type;
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
            type = (int) strings[0];
            rechoice = (int) strings[1];
            if(strings.length == 3) {
                result = strings[2].toString();
            }
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
                if(isCancelled()) {
                    return "canceled";
                }
                i++;
                if(loadAppsDialog != null) loadAppsDialog.setProgressBar((i * 100) / packages.size());
                if(!packageInfo.activityInfo.packageName.equals(packageInfo.loadLabel(pm).toString())) {
                    appsPackages.add(packageInfo.activityInfo.packageName+"/"+packageInfo.activityInfo.name);
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
                if (loadAppsDialog != null) loadAppsDialog.cancelDialog();
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
                        if (type == adapter.TYPE_NORMAL) {
                            if(appsPackagesFiltered.isEmpty()) {
                                string = appsPackages.get(resultBundle.getInt(slideDownDialogFragment.RESULT_LIST));
                            } else {
                                string = appsPackagesFiltered.get(resultBundle.getInt(slideDownDialogFragment.RESULT_LIST));
                            }
                        } else {
                            if(appsPackagesFiltered.isEmpty()) {
                                string = result.replace("[THIS]", appsPackages.get(resultBundle.getInt(slideDownDialogFragment.RESULT_LIST)));
                            } else {
                                string = result.replace("[THIS]", appsPackagesFiltered.get(resultBundle.getInt(slideDownDialogFragment.RESULT_LIST)));
                            }
                        }
                        if (rechoice != -1) {
                            adapter.removeAt(rechoice);
                            adapter.insertAt(rechoice, new Object[]{type, string});
                        } else {
                            adapter.addItem(type, string);
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
                        if(charSequence.toString().isEmpty()) {
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

}

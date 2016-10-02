package de.NeonSoft.neopowermenu.helpers;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.widget.*;

import com.nostra13.universalimageloader.utils.*;

import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.Preferences.*;

import java.io.*;
import java.util.*;

import android.widget.AbsListView.*;

public class PresetsAdapter extends ArrayAdapter<String> {

    public static String selectedName = "";
    public static AlertDialog uploadad;

    private final Activity context;
    private final ArrayList<String> itemsTitle;
    private final ArrayList<String> itemsDesc;
    private final ArrayList<String> itemsEnabled;
    private final ArrayList<String> itemsLocal;

    public PresetsAdapter(Activity context,
                          ArrayList<String> itemsTitle,
                          ArrayList<String> itemsDesc,
                          ArrayList<String> itemsEnabled,
                          ArrayList<String> itemsLocal) {
        super(context, R.layout.presetmanager_listitem, itemsTitle);
        this.context = context;
        this.itemsTitle = itemsTitle;
        this.itemsDesc = itemsDesc;
        this.itemsEnabled = itemsEnabled;
        this.itemsLocal = itemsLocal;
        //if(itemsLocal.get(0).equalsIgnoreCase("false")) {
        //PreferencesPresetsFragment.DownloadingActiveForRoot = new LinearLayout[itemsTitle.size()];
        //PreferencesPresetsFragment.DownloadingActiveForHelper = new downloadHelper[itemsTitle.size()];
        //PreferencesPresetsFragment.DownloadingActiveForLayout = new LinearLayout[itemsTitle.size()];
        //PreferencesPresetsFragment.DownloadingActiveForImageView = new ImageView[itemsTitle.size()];
        //PreferencesPresetsFragment.DownloadingActiveForOldText = new String[itemsTitle.size()];
        //PreferencesPresetsFragment.DownloadingActiveForLabel = new TextView[itemsTitle.size()];
        //PreferencesPresetsFragment.DownloadingActiveForProgress = new ProgressBar[itemsTitle.size()];
        //}
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        
        final LayoutInflater inflater = context.getLayoutInflater();
        View rowView;
        //final String prefname = this.itemsSwitchPrefName.get(position);
        rowView = inflater.inflate(R.layout.presetmanager_listitem, null, true);

        final LinearLayout root = (LinearLayout) rowView.findViewById(R.id.root);
        final TextView ItemTitle = (TextView) rowView.findViewById(R.id.title);
        ItemTitle.setGravity(Gravity.NO_GRAVITY);
        final TextView ItemDesc = (TextView) rowView.findViewById(R.id.text);
        ItemDesc.setGravity(Gravity.NO_GRAVITY);
        final ImageView hasGraphics = (ImageView) rowView.findViewById(R.id.presetmanagerlistitemImageView_hasGraphics);
        hasGraphics.setVisibility(View.GONE);
        LinearLayout LocalButton = (LinearLayout) rowView.findViewById(R.id.presetmanagerlistitemLinearLayout_Local);
        LocalButton.setVisibility(View.VISIBLE);
        RadioButton ItemSwitch = (RadioButton) rowView.findViewById(R.id.Active);
        ItemSwitch.setClickable(false);
        ItemSwitch.setFocusable(false);
        final TextView StarsCount = (TextView) rowView.findViewById(R.id.presetmanagerlistitemTextView_Stars);
        final LinearLayout OnlineButton = (LinearLayout) rowView.findViewById(R.id.presetmanagerlistitemLinearLayout_Online);
        final ImageView OnlineButtonImage = (ImageView) rowView.findViewById(R.id.presetmanagerlistitemImageView_Online);
        OnlineButton.setVisibility(View.GONE);
        final ProgressBar Progress = (ProgressBar) rowView.findViewById(R.id.presetmanagerlistitemProgressBar_Download);
        Progress.setProgress(0);
        LinearLayout BottomBar = (LinearLayout) rowView.findViewById(R.id.presetmanagerlistitemLinearLayout_BottomBar);
        BottomBar.setVisibility(View.GONE);
        final LinearLayout Upload = (LinearLayout) rowView.findViewById(R.id.presetmanagerlistitemLinearLayout_Upload);
        TextView UploadText = (TextView) rowView.findViewById(R.id.presetmanagerlistitemTextView_Upload);
        UploadText.setText(context.getString(R.string.presetsManager_Buttons).split("\\|")[4]);
        LinearLayout Share = (LinearLayout) rowView.findViewById(R.id.presetmanagerlistitemLinearLayout_Share);
        TextView ShareText = (TextView) rowView.findViewById(R.id.presetmanagerlistitemTextView_Share);
        ShareText.setText(context.getString(R.string.presetsManager_Buttons).split("\\|")[5]);
        final LinearLayout Star = (LinearLayout) rowView.findViewById(R.id.presetmanagerlistitemLinearLayout_Star);
        Star.setVisibility(View.GONE);
        final ImageView StarImage = (ImageView) rowView.findViewById(R.id.presetmanagerlistitemImageView_Star);
        final TextView StarText = (TextView) rowView.findViewById(R.id.presetmanagerlistitemTextView_StarText);
        TextView StarLine = (TextView) rowView.findViewById(R.id.presetmanagerlistitemTextView_StarLine);
        StarLine.setVisibility(View.GONE);
        LinearLayout Delete = (LinearLayout) rowView.findViewById(R.id.presetmanagerlistitemLinearLayout_Delete);
        ImageView DeleteImage = (ImageView) rowView.findViewById(R.id.presetmanagerlistitemImageView_Delete);
        TextView DeleteText = (TextView) rowView.findViewById(R.id.presetmanagerlistitemTextView_Delete);
        DeleteText.setText(context.getString(R.string.presetsManager_Buttons).split("\\|")[2]);
        Delete.setVisibility(View.GONE);

        ItemTitle.setText(this.itemsTitle.get(position));
        final String[] split = this.itemsDesc.get(position).split(",=,");
        String desc = context.getString(R.string.presetsManager_Creator).replace("[CREATORNAME]", itemsDesc.get(position));
        if (split.length > 1) {
            desc = context.getString(R.string.presetsManager_Creator).replace("[CREATORNAME]", split[0]) + "\nNeoPowerMenu " + split[1];
            StarsCount.setText(context.getString(R.string.presetsManager_Stars).replace("[STARS]", split[2]));
        }
        ItemDesc.setText(desc);
        if (itemsLocal.get(position).equalsIgnoreCase("true") || itemsLocal.get(position).equalsIgnoreCase("pre")) {
            root.setEnabled(true);
            if (!itemsLocal.get(position).equalsIgnoreCase("pre")) {
                if (helper.isValidZip(context.getFilesDir().getPath() + "/presets/" + itemsTitle.get(position) + ".nps", null)) {
                    hasGraphics.setVisibility(View.VISIBLE);
                }
                BottomBar.setVisibility(View.VISIBLE);
                Upload.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View p1) {
                        
                        slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                        dialogFragment.setContext(context);
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
                                
                                String appVersion = MainActivity.versionName.replace("v", "");
                                final String presetName = resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0");
                                final String presetCreator = resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "1");
                                boolean hasGraphics = false;
                                String path = "";
                                try {
                                    FileInputStream fIn;
                                    if (helper.isValidZip(context.getFilesDir().getPath() + "/presets/" + itemsTitle.get(position) + ".nps", null)) {
                                        hasGraphics = true;
                                        helper.copyFile(context.getFilesDir().getPath() + "/presets/" + itemsTitle.get(position) + ".nps", context.getFilesDir().getPath() + "/temp/" + presetName + ".nps.tmp");
                                        helper.unzipFile(context.getFilesDir().getPath() + "/temp/" + presetName + ".nps.tmp", context.getFilesDir().getPath() + "/temp/", itemsTitle.get(position) + ".nps", null);
                                        new File(context.getFilesDir().getPath() + "/temp/" + itemsTitle.get(position) + ".nps").renameTo(new File(context.getFilesDir().getPath() + "/temp/" + presetName + ".nps"));
                                        helper.removeFromZip(context.getFilesDir().getPath() + "/temp/" + presetName + ".nps.tmp", itemsTitle.get(position) + ".nps", null);
                                        helper.zipFile(context.getFilesDir().getPath() + "/temp/" + presetName + ".nps", context.getFilesDir().getPath() + "/temp/" + presetName + ".nps.tmp", null);
                                        path = context.getFilesDir().getPath() + "/temp/" + presetName + ".nps";
                                        fIn = new FileInputStream(context.getFilesDir().getPath() + "/temp/" + presetName + ".nps");
                                    } else {
                                        path = context.getFilesDir().getPath() + "/presets/" + itemsTitle.get(position) + ".nps";
                                        fIn = new FileInputStream(context.getFilesDir().getPath() + "/presets/" + itemsTitle.get(position) + ".nps");
                                    }
                                    BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
                                    String aDataRow = "";
                                    while ((aDataRow = myReader.readLine()) != null) {
                                        String[] aData = aDataRow.split("=");
                                        if (aData[0].equalsIgnoreCase("AppVersion")) {
                                            appVersion = aData[1];
                                        }
                                    }
                                    myReader.close();
                                    fIn.close();
                                } catch (Throwable t) {
                                }
                                if (helper.isValidZip(context.getFilesDir().getPath() + "/temp/" + presetName + ".nps.tmp", null)) {
                                    new File(context.getFilesDir().getPath() + "/temp/" + presetName + ".nps").delete();
                                    new File(context.getFilesDir().getPath() + "/temp/" + presetName + ".nps.tmp").renameTo(new File(context.getFilesDir().getPath() + "/temp/" + presetName + ".nps"));
                                }
                                final slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                                dialogFragment.setContext(context);
                                dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                                final uploadHelper uH = new uploadHelper(context);
                                uH.setInterface(new uploadHelper.uploadHelperInterface() {

                                    @Override
                                    public void onStateChanged(int state) {
                                        
                                        try {
                                            if (state == uploadHelper.STATE_WAITING) {
                                                //Progress.setProgress(100);
                                                //Animation BlinkAnim = AnimationUtils.loadAnimation(context, R.anim.progress_blink);
                                                //Progress.setAlpha((float) 1);
                                                //Progress.startAnimation(BlinkAnim);
                                                //Upload.setEnabled(false);
                                                //Upload.setAlpha((float) .5);
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
                                                        
                                                        dialogFragment.setText(context.getString(R.string.uploadHelper_States).split("\\|")[uploadHelper.STATE_CANCELLING] + "\n\n");
                                                        dialogFragment.setPositiveButton(null);
                                                        dialogFragment.setProgressBarBlink(true);
                                                        uH.stopUpload(true);
                                                    }

                                                    @Override
                                                    public void onTouchOutside() {
                                                        
                                                    }
                                                });
                                                dialogFragment.setCloseOnButtonClick(false);
                                                dialogFragment.setText(context.getString(R.string.uploadHelper_States).split("\\|")[state] + "\n\n");
                                                dialogFragment.addProgressBar(true, true);
                                                dialogFragment.setPositiveButton(context.getString(R.string.Dialog_Buttons).split("\\|")[4]);
                                                dialogFragment.setCloseOnTouchOutside(false);
                                                dialogFragment.showDialog(R.id.dialog_container);
                                            }
                                            dialogFragment.setText(context.getString(R.string.uploadHelper_States).split("\\|")[state] + "\n\n");
                                        } catch (Throwable t) {
                                        }
                                    }

                                    @Override
                                    public void onPublishUploadProgress(long nowSize, long totalSize) {
                                        try {
                                            
                                            //ItemDesc.setText((int) (( nowSize * 100) / totalSize));
                                            //Progress.setProgress(uH.getProgress());
                                            if (dialogFragment != null && !dialogFragment.getText().equalsIgnoreCase(context.getString(R.string.uploadHelper_States).split("\\|")[uploadHelper.STATE_CANCELLING] + "\n\n")) {
                                                dialogFragment.setText(context.getString(R.string.uploadHelper_States).split("\\|")[uH.getState()] + ((uH.getSizes()[0] > 0 && uH.getState() != uploadHelper.STATE_CANCELLING) ? "\n" + helper.getSizeString(uH.getSizes()[0], true) + "/" + helper.getSizeString(uH.getSizes()[1], true) + "\neta: " + helper.getTimeString(uH.getETA(), true) + ", speed: " + helper.getSizeString(uH.getAvgSpeed(), true) + "/s" : "\n\n"));
                                                if (uH.getSizes()[0] > 0) {
                                                    dialogFragment.setProgressBar(uH.getProgress());
                                                    dialogFragment.setProgressBarBlink(false);
                                                    //Progress.clearAnimation();
                                                    //Progress.setAlpha((float) 0.2);
                                                }
                                            }
                                        } catch (Throwable t) {
                                        }
                                    }

                                    @Override
                                    public void onUploadComplete(String response) {

                                        dialogFragment.closeDialog();
                                        //Progress.setProgress(0);
                                        //Progress.clearAnimation();
                                        //Progress.setAlpha((float) 0.2);
                                        //Upload.setEnabled(true);
                                        //Upload.setAlpha((float) 1);
                                        //if(helper.isValidZip(context.getFilesDir().getPath() + "/temp/" + resultData.get(0) + ".nps",null)) {
                                        new File(context.getFilesDir().getPath() + "/temp/" + presetName + ".nps").delete();
                                        //}
                                        new getOnlinePresets().execute((PreferencesPresetsFragment.onlineOrderSelectedString.isEmpty() ? "" : "order=" + PreferencesPresetsFragment.onlineOrderSelectedString));
                                        Toast.makeText(context, context.getString(R.string.presetsManager_UploadComplete), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onUploadFailed(String reason) {

                                        dialogFragment.closeDialog();
                                        //Progress.setProgress(0);
                                        //Progress.clearAnimation();
                                        //Progress.setAlpha((float) 0.2);
                                        //Upload.setEnabled(true);
                                        //Upload.setAlpha((float) 1);
                                        if (reason.equalsIgnoreCase("canceled")) {
                                            //Toast.makeText(context,"Download canceled",Toast.LENGTH_SHORT).show();
                                        } else if (reason.contains("no access")) {
                                            slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                                            dialogFragment.setContext(context);
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

                                                    if (!MainActivity.loggedIn) {
                                                        slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                                                        dialogFragment.setContext(context);
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

                                                                PreferencesPresetsFragment.vpPager.setCurrentItem(0, true);
                                                            }

                                                            @Override
                                                            public void onPositiveClick(Bundle resultBundle) {

                                                                LoginFragment.performLogin(context, resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0"), helper.md5Crypto(resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "1")), resultBundle.getBoolean(slideDownDialogFragment.RESULT_CHECKBOX), false);
                                                            }

                                                            @Override
                                                            public void onTouchOutside() {

                                                            }
                                                        });
                                                        dialogFragment.setText("");
                                                        dialogFragment.addInput(context.getString(R.string.login_UsernameEmail), presetCreator, false, null);
                                                        dialogFragment.addInput(context.getString(R.string.login_Password), "", false, null);
                                                        dialogFragment.setInputMode(2, InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                                        dialogFragment.setCheckBox(context.getString(R.string.login_KeepLogin), MainActivity.preferences.getBoolean("autoLogin", false));
                                                        dialogFragment.setNegativeButton(context.getString(R.string.Dialog_Buttons).split("\\|")[4]);
                                                        //dialogFragment.setDialogNeutralButton(context.getString(R.string.login_TitleRegister));
                                                        dialogFragment.setPositiveButton(context.getString(R.string.login_Title));
                                                        dialogFragment.showDialog(R.id.dialog_container);
                                                    } else {
                                                        PreferencesPresetsFragment.vpPager.setCurrentItem(0, true);
                                                    }
                                                }

                                                @Override
                                                public void onTouchOutside() {

                                                }
                                            });
                                            dialogFragment.setText(context.getString(R.string.presetsManager_UploadFailedNoAccess));
                                            dialogFragment.setNegativeButton(context.getString(R.string.Dialog_Buttons).split("\\|")[4]);
                                            dialogFragment.setPositiveButton(context.getString(R.string.login_Title));
                                            dialogFragment.showDialog(R.id.dialog_container);
                                        } else if (reason.contains("Preset name exists.")) {
                                            slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                                            dialogFragment.setContext(context);
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

                                                }

                                                @Override
                                                public void onTouchOutside() {

                                                }
                                            });
                                            dialogFragment.setText(context.getString(R.string.presetsManager_UploadFailedSameName));
                                            dialogFragment.setPositiveButton(context.getString(R.string.Dialog_Buttons).split("\\|")[0]);
                                            dialogFragment.showDialog(R.id.dialog_container);
                                        } else if (reason.contains("Cannot connect to the DB")) {
                                            Toast.makeText(context, context.getString(R.string.presetsManager_CantConnecttoServer), Toast.LENGTH_LONG).show();
                                        } else if (reason.contains("Connection refused")) {
                                            Toast.makeText(context, context.getString(R.string.presetsManager_CantConnecttoServer), Toast.LENGTH_LONG).show();
                                        } else {
                                            slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                                            dialogFragment.setContext(context);
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

                                                }

                                                @Override
                                                public void onTouchOutside() {

                                                }
                                            });
                                            dialogFragment.setText(reason);
                                            dialogFragment.setPositiveButton(context.getString(R.string.Dialog_Buttons).split("\\|")[0]);
                                            dialogFragment.showDialog(R.id.dialog_container);
                                        }
                                    }
                                });
                                uH.setServerUrl("http" + (MainActivity.LOCALTESTSERVER ? "" : "s") + "://" + (MainActivity.LOCALTESTSERVER ? "127.0.0.1:8080" : "www.Neon-Soft.de") + "/page/NeoPowerMenu/phpWebservice/webservice2.php");
                                uH.setLocalUrl(path);
                                uH.uploadAs(resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0") + ".nps");
                                uH.setAdditionalUploadPosts(new String[][]{{"presetName", resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0")}, {"presetCreator", resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "1")}, {"presetAppVersion", "v" + appVersion}, {"presetHasGraphics", hasGraphics ? "true" : "false"}, {MainActivity.userName.contains("@") ? "userEmail" : "userName", MainActivity.userName}, {"userId", MainActivity.deviceUniqeId}});
                                uH.startUpload();
                            }

                            @Override
                            public void onTouchOutside() {

                            }
                        });
                        dialogFragment.setText(context.getString(R.string.presetsManager_UploadMsg));
                        dialogFragment.addInput(context.getString(R.string.presetSaveDialog_InfoText), itemsTitle.get(position), false, null);
                        dialogFragment.addInput(context.getString(R.string.presetSaveDialog_CreatorNameInfo), itemsDesc.get(position), false, null);
                        dialogFragment.setNegativeButton(context.getString(R.string.Dialog_Buttons).split("\\|")[4]);
                        dialogFragment.setPositiveButton(context.getString(R.string.Dialog_Buttons).split("\\|")[0]);
                        dialogFragment.showDialog(R.id.dialog_container);
                    }
                });
                Delete.setVisibility(View.VISIBLE);
                Delete.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View p1) {
                        slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                        dialogFragment.setContext(context);
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

                                try {
                                    String selectedName = itemsTitle.get(position);
                                    File presetFile = new File(context.getFilesDir().getPath() + "/presets/" + selectedName + ".nps");
                                    if (presetFile.delete()) {
                                        removeAt(position);
                                        Toast.makeText(context.getApplicationContext(), context.getString(R.string.presetLoad_PresetDeleted).replace("[PRESETNAME]", selectedName), Toast.LENGTH_SHORT).show();
                                    }

                                } catch (Throwable t) {
                                    Toast.makeText(context.getApplicationContext(), "Failed to delete: " + t, Toast.LENGTH_LONG).show();
                                }
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onTouchOutside() {

                            }
                        });
                        dialogFragment.setText(context.getString(R.string.presetsManager_SureToDelete).replace("[PRESETNAME]", itemsTitle.get(position)));
                        dialogFragment.setNegativeButton(context.getString(R.string.Dialog_Buttons).split("\\|")[4]);
                        dialogFragment.setPositiveButton(context.getString(R.string.Dialog_Buttons).split("\\|")[5]);
                        dialogFragment.showDialog(R.id.dialog_container);
                    }
                });
                Share.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View p1) {

                        try {
                            PreferencesPresetsFragment.progress.setVisibility(View.VISIBLE);
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/rtf");
                            File sharedfolder = new File(context.getExternalFilesDir(null) + "/NeoPowerMenu/sharedpresets");
                            File tmpfile = new File(context.getExternalFilesDir(null) + "/NeoPowerMenu/sharedpresets/" + itemsTitle.get(position) + ".nps");
                            sharedfolder.mkdirs();
                            tmpfile.delete();
                            helper.copyFile(context.getFilesDir().getPath() + "/presets/" + itemsTitle.get(position) + ".nps", context.getExternalFilesDir(null) + "/NeoPowerMenu/sharedpresets/" + itemsTitle.get(position) + ".nps");

                            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(context.getExternalFilesDir(null) + "/NeoPowerMenu/sharedpresets/" + itemsTitle.get(position) + ".nps")));
                            context.startActivity(shareIntent.createChooser(shareIntent, itemsTitle.get(position)));
                            PreferencesPresetsFragment.progress.setVisibility(View.GONE);
                        } catch (Throwable e) {
                        }
                    }
                });
            }
            if (this.itemsEnabled.get(position).equalsIgnoreCase("false")) {
                ItemSwitch.setEnabled(false);
                ItemSwitch.setChecked(false);
            } else {
                String[] builtIn = context.getString(R.string.presetsManager_BuiltIn).split("/");
                ItemSwitch.setChecked(MainActivity.preferences.getString("lastUsedPreset", builtIn[0]).equalsIgnoreCase(itemsTitle.get(position)) ? true : false);
            }
            root.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View p1) {

                    selectedName = itemsTitle.get(position);
                    if (helper.isValidZip(context.getFilesDir().getPath() + "/presets/" + selectedName + ".nps", null)) {
                        slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                        dialogFragment.setContext(context);
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

                                helper.unzipFile(context.getFilesDir().getPath() + "/presets/" + selectedName + ".nps", context.getFilesDir().getPath() + "/temp/", selectedName + ".nps", null);
                                new loadPreset().execute(context.getFilesDir().getPath() + "/temp/" + selectedName + ".nps");
                            }

                            @Override
                            public void onPositiveClick(Bundle resultBundle) {

                                helper.unzipAll(context.getFilesDir().getPath() + "/presets/" + selectedName + ".nps", context.getFilesDir().getPath() + "/temp/", null);
                                for (int i = 0; i < PreferencesGraphicsFragment.defaultGraphics.length; i++) {
                                    try {
                                        new File(context.getFilesDir().getPath() + "/images/" + PreferencesGraphicsFragment.defaultGraphics[i][2] + ".png").delete();
                                        new File(context.getFilesDir().getPath() + "/temp/" + PreferencesGraphicsFragment.defaultGraphics[i][2] + ".png").renameTo(new File(context.getFilesDir().getPath() + "/images/" + PreferencesGraphicsFragment.defaultGraphics[i][2] + ".png"));
                                        MemoryCacheUtils.removeFromCache(((context.getFilesDir().getPath() + "/images/" + PreferencesGraphicsFragment.defaultGraphics[i][2] + ".png").startsWith("file://") ? "" : "file://") + context.getFilesDir().getPath() + "/images/" + PreferencesGraphicsFragment.defaultGraphics[i][2] + ".png", MainActivity.imageLoader.getMemoryCache());
                                        DiskCacheUtils.removeFromCache(((context.getFilesDir().getPath() + "/images/" + PreferencesGraphicsFragment.defaultGraphics[i][2] + ".png").startsWith("file://") ? "" : "file://") + context.getFilesDir().getPath() + "/images/" + PreferencesGraphicsFragment.defaultGraphics[i][2] + ".png", MainActivity.imageLoader.getDiscCache());
                                        if (PreferencesGraphicsFragment.defaultGraphics[i][2].toString().equalsIgnoreCase("Progress") && new File(context.getFilesDir().getPath() + "/images/" + PreferencesGraphicsFragment.defaultGraphics[i][2] + ".png").exists()) {
                                            MainActivity.preferences.edit().putString("ProgressDrawable", "file").apply();
                                        } else {
                                            MainActivity.preferences.edit().putString("ProgressDrawable", "stock").apply();
                                        }
                                    } catch (Throwable t) {
                                        Log.d("NPM:presetLoad", "Cant move graphic " + PreferencesGraphicsFragment.defaultGraphics[i][0] + "-" + PreferencesGraphicsFragment.defaultGraphics[i][1] + ".png, reason: " + t.toString());
                                    }
                                }
                                if (!resultBundle.getBoolean(slideDownDialogFragment.RESULT_CHECKBOX)) {
                                    new loadPreset().execute(context.getFilesDir().getPath() + "/temp/" + selectedName + ".nps");
                                } else {
                                    Toast.makeText(context,context.getString(R.string.presetLoad_PresetLoaded).replace("[PRESETNAME]",selectedName),Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onTouchOutside() {

                            }
                        });
                        dialogFragment.setText(context.getString(R.string.graphics_GraphicsSaveLoad).split("\\|")[1]);
                        dialogFragment.setCheckBox(context.getString(R.string.presetsManager_LoadOnlyGraphics),false);
                        dialogFragment.setNegativeButton(context.getString(R.string.Dialog_Buttons).split("\\|")[4]);
                        dialogFragment.setNeutralButton(context.getString(R.string.Dialog_Buttons).split("\\|")[2]);
                        dialogFragment.setPositiveButton(context.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_LOAD]);
                        dialogFragment.showDialog(R.id.dialog_container);
                    } else {
                        new loadPreset().execute((position >= 3 ? context.getFilesDir().getPath() + "/presets/" : "") + selectedName + (position >= 3 ? ".nps" : ""));
                    }
                }
            });
        } else if (itemsLocal.get(position).equalsIgnoreCase("false")) {
            root.setEnabled(true);
            LocalButton.setVisibility(View.GONE);
            OnlineButton.setVisibility(View.VISIBLE);
            try {
                BottomBar.setVisibility(View.VISIBLE);
                Upload.setVisibility(View.GONE);
                Share.setVisibility(View.GONE);
                if (MainActivity.preferences.getString("ratedFor", "").contains("&" + itemsTitle.get(position) + ",")) {
                    StarImage.setImageResource(R.drawable.ic_action_star_0);
                    StarText.setText(context.getString(R.string.presetsManager_Buttons).split("\\|")[1]);
                } else {
                    StarImage.setImageResource(R.drawable.ic_action_star_10);
                    StarText.setText(context.getString(R.string.presetsManager_Buttons).split("\\|")[0]);
                }
                if (MainActivity.userRank.equals("A") || PresetsPage.onlineIds.get(position).equals(MainActivity.deviceUniqeId) || PresetsPage.onlineIds.get(position).equals(MainActivity.accountUniqeId)) {
                    StarLine.setVisibility(View.VISIBLE);
                    Delete.setVisibility(View.VISIBLE);
                    Delete.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View p1) {
                            slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                            dialogFragment.setContext(context);
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

                                    uploadHelper uH = new uploadHelper(context);
                                    uH.setInterface(new uploadHelper.uploadHelperInterface() {

                                        @Override
                                        public void onStateChanged(int state) {

                                            if (state == uploadHelper.STATE_WAITING) {
                                                PreferencesPresetsFragment.LoadingMsg.setText(context.getString(R.string.login_Processing));
                                                PreferencesPresetsFragment.progressHolder.setVisibility(View.VISIBLE);
                                                PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
                                            }
                                        }

                                        @Override
                                        public void onPublishUploadProgress(long nowSize, long totalSize) {

                                        }

                                        @Override
                                        public void onUploadComplete(String response) {

                                            PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
                                            PreferencesPresetsFragment.progressHolder.setVisibility(View.GONE);
                                            Toast.makeText(context, context.getString(R.string.presetLoad_PresetDeleted).replace("[PRESETNAME]", itemsTitle.get(position)), Toast.LENGTH_SHORT).show();
                                            new getOnlinePresets().execute((PreferencesPresetsFragment.onlineOrderSelectedString.isEmpty() ? "" : "order=" + PreferencesPresetsFragment.onlineOrderSelectedString));
                                        }

                                        @Override
                                        public void onUploadFailed(String reason) {

                                            PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
                                            PreferencesPresetsFragment.progressHolder.setVisibility(View.GONE);
                                            Toast.makeText(context, "Failed to delete.\n" + reason, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    uH.setServerUrl("http" + (MainActivity.LOCALTESTSERVER ? "" : "s") + "://" + (MainActivity.LOCALTESTSERVER ? "127.0.0.1:8080" : "www.Neon-Soft.de") + "/page/NeoPowerMenu/phpWebservice/webservice1.php");
                                    uH.setAdditionalUploadPosts(new String[][]{{"action", "delete"}, {"presetName", itemsTitle.get(position)}, {"userId", (MainActivity.userRank.equalsIgnoreCase("A") ? PresetsPage.onlineIds.get(position) : PresetsPage.onlineIds.get(position).equals(MainActivity.deviceUniqeId) ? MainActivity.deviceUniqeId : MainActivity.accountUniqeId)}});
                                    try {
                                        new File(context.getFilesDir().getPath() + "/tmp").createNewFile();
                                    } catch (IOException e) {
                                    }
                                    uH.setLocalUrl(context.getFilesDir().getPath() + "/tmp");
                                    uH.startUpload();
                                }

                                @Override
                                public void onTouchOutside() {

                                }
                            });
                            dialogFragment.setText(context.getString(R.string.presetsManager_SureToDelete).replace("[PRESETNAME]", itemsTitle.get(position)));
                            dialogFragment.setNegativeButton(context.getString(R.string.Dialog_Buttons).split("\\|")[4]);
                            dialogFragment.setPositiveButton(context.getString(R.string.Dialog_Buttons).split("\\|")[5]);
                            dialogFragment.showDialog(R.id.dialog_container);
                        }
                    });
                }
                if (!PresetsPage.onlineIds.get(position).equals(MainActivity.deviceUniqeId) && !PresetsPage.onlineIds.get(position).equals(MainActivity.accountUniqeId)) {
                    Star.setVisibility(View.VISIBLE);
                    if (!MainActivity.userRank.equals("A") && !MainActivity.accountUniqeId.isEmpty() && !MainActivity.accountUniqeId.equalsIgnoreCase("none")) {
                        Delete.setVisibility(View.VISIBLE);
                        StarLine.setVisibility(View.VISIBLE);
                        DeleteImage.setImageResource(R.drawable.ic_action_warning);
                        DeleteText.setText(context.getString(R.string.presetsManager_Buttons).split("\\|")[3]);
                        Delete.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View p1) {
                                slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                                dialogFragment.setContext(context);
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

                                        uploadHelper uH = new uploadHelper(context);
                                        uH.setInterface(new uploadHelper.uploadHelperInterface() {

                                            @Override
                                            public void onStateChanged(int state) {

                                                if (state == uploadHelper.STATE_WAITING) {
                                                    PreferencesPresetsFragment.LoadingMsg.setText(context.getString(R.string.login_Processing));
                                                    PreferencesPresetsFragment.progressHolder.setVisibility(View.VISIBLE);
                                                    PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
                                                }
                                            }

                                            @Override
                                            public void onPublishUploadProgress(long nowSize, long totalSize) {

                                            }

                                            @Override
                                            public void onUploadComplete(String response) {

                                                PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
                                                PreferencesPresetsFragment.progressHolder.setVisibility(View.GONE);
                                                Toast.makeText(context, context.getString(R.string.presetsManager_ReportResult).split("\\|")[0].replace("[PRESETNAME]", itemsTitle.get(position)), Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onUploadFailed(String reason) {

                                                PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
                                                PreferencesPresetsFragment.progressHolder.setVisibility(View.GONE);
                                                slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                                                dialogFragment.setContext(context);
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

                                                    }

                                                    @Override
                                                    public void onTouchOutside() {

                                                    }
                                                });
                                                dialogFragment.setText(context.getString(R.string.presetsManager_ReportResult).split("\\|")[1] + reason);
                                                dialogFragment.setPositiveButton(context.getString(R.string.Dialog_Buttons).split("\\|")[0]);
                                                dialogFragment.showDialog(R.id.dialog_container);
                                            }
                                        });
                                        uH.setServerUrl("http" + (MainActivity.LOCALTESTSERVER ? "" : "s") + "://" + (MainActivity.LOCALTESTSERVER ? "127.0.0.1:8080" : "www.Neon-Soft.de") + "/page/NeoPowerMenu/phpWebservice/webservice3.php");
                                        uH.setAdditionalUploadPosts(new String[][]{{"action", "report"}, {"presetName", itemsTitle.get(position)}, {"reason", resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0").replace("\n", "<br>")}, {"accountId", MainActivity.accountUniqeId}});
                                        try {
                                            new File(context.getFilesDir().getPath() + "/tmp").createNewFile();
                                        } catch (IOException e) {
                                        }
                                        uH.setLocalUrl(context.getFilesDir().getPath() + "/tmp");
                                        uH.startUpload();
                                    }

                                    @Override
                                    public void onTouchOutside() {

                                    }
                                });
                                dialogFragment.setText(context.getString(R.string.presetsManager_ReportDialog).split("\\|")[0].replace("[PRESETNAME]", itemsTitle.get(position)));
                                dialogFragment.addInput(context.getString(R.string.presetsManager_ReportDialog).split("\\|")[1], "", false, null);
                                dialogFragment.setInputSingleLine(0, false);
                                dialogFragment.setNegativeButton(context.getString(R.string.Dialog_Buttons).split("\\|")[4]);
                                dialogFragment.setPositiveButton(context.getString(R.string.presetsManager_Buttons).split("\\|")[3]);
                                dialogFragment.showDialog(R.id.dialog_container);
                            }
                        });
                    }
                    Star.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View p1) {

                            uploadHelper uH = new uploadHelper(context);
                            uH.setInterface(new uploadHelper.uploadHelperInterface() {

                                @Override
                                public void onStateChanged(int state) {

                                    if (state == uploadHelper.STATE_WAITING) {
                                        PreferencesPresetsFragment.LoadingMsg.setText(context.getString(R.string.login_Processing));
                                        PreferencesPresetsFragment.progressHolder.setVisibility(View.VISIBLE);
                                        PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
                                    }
                                }

                                @Override
                                public void onPublishUploadProgress(long nowSize, long totalSize) {

                                }

                                @Override
                                public void onUploadComplete(String response) {

                                    if (StarText.getText().toString().equalsIgnoreCase(context.getString(R.string.presetsManager_Buttons).split("\\|")[0])) {
                                        StarsCount.setText(context.getString(R.string.presetsManager_Stars).replace("[STARS]", "" + (Integer.parseInt(StarsCount.getText().toString().split(": ")[1]) + 1)));
                                        StarImage.setImageResource(R.drawable.ic_action_star_0);
                                        StarText.setText(context.getString(R.string.presetsManager_Buttons).split("\\|")[1]);
                                        MainActivity.preferences.edit().putString("ratedFor", MainActivity.preferences.getString("ratedFor", "") + "&" + itemsTitle.get(position) + ",").apply();
                                    } else {
                                        StarsCount.setText(context.getString(R.string.presetsManager_Stars).replace("[STARS]", "" + (Integer.parseInt(StarsCount.getText().toString().split(": ")[1]) - 1)));
                                        StarImage.setImageResource(R.drawable.ic_action_star_10);
                                        StarText.setText(context.getString(R.string.presetsManager_Buttons).split("\\|")[0]);
                                        MainActivity.preferences.edit().putString("ratedFor", MainActivity.preferences.getString("ratedFor", "").replace("&" + itemsTitle.get(position) + ",", "")).apply();
                                    }
                                    String[] split = itemsDesc.get(position).split(",=,");
                                    itemsDesc.set(position, split[0] + ",=," + split[1] + ",=," + (Integer.parseInt(StarsCount.getText().toString().split(": ")[1])));
                                    LoginFragment.getStatistics();
                                    PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
                                    PreferencesPresetsFragment.progressHolder.setVisibility(View.GONE);
                                }

                                @Override
                                public void onUploadFailed(String reason) {

                                    PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
                                    PreferencesPresetsFragment.progressHolder.setVisibility(View.GONE);
                                    if (reason.contains("Cannot connect to the DB")) {
                                        Toast.makeText(context, context.getString(R.string.presetsManager_CantConnecttoServer), Toast.LENGTH_LONG).show();
                                    } else if (reason.contains("Connection refused")) {
                                        Toast.makeText(context, context.getString(R.string.presetsManager_CantConnecttoServer), Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(context, context.getString(R.string.login_LoginFailedWithReason) + "\n" + reason, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            uH.setServerUrl("http" + (MainActivity.LOCALTESTSERVER ? "" : "s") + "://" + (MainActivity.LOCALTESTSERVER ? "127.0.0.1:8080" : "www.Neon-Soft.de") + "/page/NeoPowerMenu/phpWebservice/webservice3.php");
                            uH.setAdditionalUploadPosts(new String[][]{{"action", (StarText.getText().toString().equalsIgnoreCase(context.getString(R.string.presetsManager_Buttons).split("\\|")[0]) ? "givestar" : "removestar")}, {(MainActivity.usernameemail.contains("@") ? "userEmail" : "userName"), MainActivity.usernameemail}, {"name", itemsTitle.get(position)}});
                            try {
                                new File(context.getFilesDir().getPath() + "/tmp").createNewFile();
                            } catch (IOException e) {
                            }
                            uH.setLocalUrl(context.getFilesDir().getPath() + "/tmp");
                            uH.startUpload();
                        }
                    });
                }
            } catch (Throwable t) {
            }
            if (position < (PreferencesPresetsFragment.DownloadingActiveForHelper.size()) && PreferencesPresetsFragment.DownloadingActiveForHelper.get(position) != null) {
                //oldText = ItemDesc.getText().toString();
                Progress.setProgress(PreferencesPresetsFragment.DownloadingActiveForHelper.get(position).getProgress());
                //OnlineButton.setEnabled(false);
                //OnlineButton.setAlpha((float) .3);
                OnlineButtonImage.setImageResource(R.drawable.ic_action_cancel);
                PreferencesPresetsFragment.DownloadingActiveForRoot.set(position, root);
                PreferencesPresetsFragment.DownloadingActiveForLayout.set(position, OnlineButton);
                PreferencesPresetsFragment.DownloadingActiveForImageView.set(position, OnlineButtonImage);
                PreferencesPresetsFragment.DownloadingActiveForLabel.set(position, ItemDesc);
                PreferencesPresetsFragment.DownloadingActiveForProgress.set(position, Progress);
                long[] sizes = PreferencesPresetsFragment.DownloadingActiveForHelper.get(position).getSizes();
                PreferencesPresetsFragment.DownloadingActiveForLabel.get(position).setText(context.getString(R.string.downloadHelper_States).split("\\|")[PreferencesPresetsFragment.DownloadingActiveForHelper.get(position).getState()] + (PreferencesPresetsFragment.DownloadingActiveForHelper.get(position).getSizes()[0] > 0 ? " - eta: " + helper.getTimeString(PreferencesPresetsFragment.DownloadingActiveForHelper.get(position).getETA(), true) + " | speed: " + helper.getSizeString(PreferencesPresetsFragment.DownloadingActiveForHelper.get(position).getAvgSpeed(), true) + "/s\n" + helper.getSizeString(sizes[0], true) + "/" + helper.getSizeString(sizes[1], true) + " | " + PreferencesPresetsFragment.DownloadingActiveForHelper.get(position).getProgress() + "%" : "\n"));
                if (PreferencesPresetsFragment.DownloadingActiveForHelper.get(position).getProgress() <= 0) {
                    Progress.setProgress(100);
                    Animation BlinkAnim = AnimationUtils.loadAnimation(context, R.anim.progress_blink);
                    PreferencesPresetsFragment.DownloadingActiveForProgress.get(position).setAlpha((float) 1);
                    Progress.startAnimation(BlinkAnim);
                }
            }
            root.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View p1) {

                    if (position < (PreferencesPresetsFragment.DownloadingActiveForHelper.size()) && PreferencesPresetsFragment.DownloadingActiveForHelper.get(position) == null) {
                        final downloadHelper dH = new downloadHelper(context);
                        dH.setInterface(new downloadHelper.downloadHelperInterface() {

                            @Override
                            public void onStateChanged(int state) {

                                try {
                                    if (state == downloadHelper.STATE_WAITING) {
                                        PreferencesPresetsFragment.DownloadingActiveForHelper.set(position, dH);
                                        PreferencesPresetsFragment.DownloadingActiveForRoot.set(position, root);
                                        PreferencesPresetsFragment.DownloadingActiveForLayout.set(position, OnlineButton);
                                        PreferencesPresetsFragment.DownloadingActiveForImageView.set(position, OnlineButtonImage);
                                        PreferencesPresetsFragment.DownloadingActiveForLabel.set(position, ItemDesc);
                                        PreferencesPresetsFragment.DownloadingActiveForProgress.set(position, Progress);
                                        PreferencesPresetsFragment.DownloadingActiveForOldText.set(position, ItemDesc.getText().toString());
                                        if (PreferencesPresetsFragment.DownloadingActiveFor.equals("")) {
                                            PreferencesPresetsFragment.DownloadingActiveFor = itemsTitle.get(position);
                                        }
                                        //PreferencesPresetsFragment.DownloadingActiveForLabel[position].setText(context.getString(R.string.downloadHelper_States).split("\\|")[state] + "\n");
                                        PreferencesPresetsFragment.DownloadingActiveForProgress.get(position).setProgress(100);
                                        Animation BlinkAnim = AnimationUtils.loadAnimation(context, R.anim.progress_blink);
                                        PreferencesPresetsFragment.DownloadingActiveForProgress.get(position).setAlpha((float) 1);
                                        PreferencesPresetsFragment.DownloadingActiveForProgress.get(position).startAnimation(BlinkAnim);
                                        //OnlineButton.setEnabled(false);
                                        //OnlineButton.setAlpha((float) .3);
                                        PreferencesPresetsFragment.DownloadingActiveForImageView.get(position).setImageResource(R.drawable.ic_action_cancel);
                                    }
                                    PreferencesPresetsFragment.DownloadingActiveForLabel.get(position).setText(context.getString(R.string.downloadHelper_States).split("\\|")[state] + "\n");
                                } catch (Throwable t) {
                                    Log.e("NPM", "Failed to start download: " + t.toString());
                                }
                            }

                            @Override
                            public void onPublishDownloadProgress(long nowSize, long totalSize) {

                                try {
                                    if(PreferencesPresetsFragment.DownloadingActiveForHelper.get(position).getState()!=downloadHelper.STATE_CANCELLING) {
                                        PreferencesPresetsFragment.DownloadingActiveForLabel.get(position).setText(context.getString(R.string.downloadHelper_States).split("\\|")[PreferencesPresetsFragment.DownloadingActiveForHelper.get(position).getState()] + " - eta: " + helper.getTimeString(dH.getETA(), true) + " | speed: " + helper.getSizeString(dH.getAvgSpeed(), true) + "/s\n" + helper.getSizeString(nowSize, true) + "/" + helper.getSizeString(totalSize, true) + " | " + PreferencesPresetsFragment.DownloadingActiveForHelper.get(position).getProgress() + "%");
                                        if (PreferencesPresetsFragment.DownloadingActiveForHelper.get(position).getSizes()[0] > 0) {
                                            PreferencesPresetsFragment.DownloadingActiveForProgress.get(position).clearAnimation();
                                            PreferencesPresetsFragment.DownloadingActiveForProgress.get(position).setAlpha((float) 0.2);
                                        }
                                        PreferencesPresetsFragment.DownloadingActiveForProgress.get(position).setProgress((int) ((nowSize * 100) / totalSize));
                                        PreferencesPresetsFragment.DownloadingActiveForRoot.get(position).postInvalidate();
                                    }
                                } catch (Throwable t) {
                                    Log.e("NPM", "Failed to update progress: " + t.toString());
                                }
                            }

                            @Override
                            public void onDownloadComplete() {
                                
                                try {
                                    PreferencesPresetsFragment.DownloadingActiveForProgress.get(position).setProgress(0);
                                    PreferencesPresetsFragment.DownloadingActiveForProgress.get(position).clearAnimation();
                                    PreferencesPresetsFragment.DownloadingActiveForProgress.get(position).setAlpha((float) 0.2);
                                    PreferencesPresetsFragment.DownloadingActiveForLabel.get(position).setText(PreferencesPresetsFragment.DownloadingActiveForOldText.get(position));
                                    PreferencesPresetsFragment.DownloadingActiveForLayout.get(position).setEnabled(true);
                                    PreferencesPresetsFragment.DownloadingActiveForLayout.get(position).setAlpha((float) 1);
                                    PreferencesPresetsFragment.DownloadingActiveForImageView.get(position).setImageResource(R.drawable.ic_file_file_download);
                                    PreferencesPresetsFragment.DownloadingActiveFor = "";
                                    PreferencesPresetsFragment.DownloadingActiveForHelper.set(position, null);
                                    PreferencesPresetsFragment.DownloadingActiveForRoot.set(position, null);
                                    PreferencesPresetsFragment.DownloadingActiveForLayout.set(position, null);
                                    PreferencesPresetsFragment.DownloadingActiveForImageView.set(position, null);
                                    PreferencesPresetsFragment.DownloadingActiveForLabel.set(position, null);
                                    PreferencesPresetsFragment.DownloadingActiveForProgress.set(position, null);
                                    try {
                                        PreferencesPresetsFragment.ImportPreset("file://" + context.getFilesDir().getPath() + "/download/" + split[0] + "_" + itemsTitle.get(position).replace("'", "\\\'").replace("\"", "\\\"") + ".nps", PreferencesPresetsFragment.localAdapter, itemsTitle.get(position), null);
                                    } catch (Exception e) {
                                        new File(context.getFilesDir().getPath() + "/download/" + itemsTitle.get(position).replace("'", "\\'").replace("\"", "\\\"") + ".nps").delete();
                                        Toast.makeText(context, context.getString(R.string.presetsManager_ImportFailed) + "\n" + e.toString(), Toast.LENGTH_LONG).show();
                                        Log.e("NPM", e.toString());
                                    }
                                } catch (Throwable t) {
                                }
                            }

                            @Override
                            public void onDownloadFailed(String reason) {
                                
                                try {
                                    PreferencesPresetsFragment.DownloadingActiveForProgress.get(position).setProgress(0);
                                    PreferencesPresetsFragment.DownloadingActiveForProgress.get(position).clearAnimation();
                                    PreferencesPresetsFragment.DownloadingActiveForProgress.get(position).setAlpha((float) 0.2);
                                    PreferencesPresetsFragment.DownloadingActiveForLabel.get(position).setText(PreferencesPresetsFragment.DownloadingActiveForOldText.get(position));
                                    PreferencesPresetsFragment.DownloadingActiveForLayout.get(position).setEnabled(true);
                                    PreferencesPresetsFragment.DownloadingActiveForLayout.get(position).setAlpha((float) 1);
                                    PreferencesPresetsFragment.DownloadingActiveForImageView.get(position).setImageResource(R.drawable.ic_file_file_download);
                                    if (!reason.equalsIgnoreCase("canceled"))
                                        Toast.makeText(context, reason, Toast.LENGTH_LONG).show();
                                    PreferencesPresetsFragment.DownloadingActiveFor = "";
                                    PreferencesPresetsFragment.DownloadingActiveForHelper.set(position, null);
                                    PreferencesPresetsFragment.DownloadingActiveForRoot.set(position, null);
                                    PreferencesPresetsFragment.DownloadingActiveForLayout.set(position, null);
                                    PreferencesPresetsFragment.DownloadingActiveForImageView.set(position, null);
                                    PreferencesPresetsFragment.DownloadingActiveForLabel.set(position, null);
                                    PreferencesPresetsFragment.DownloadingActiveForProgress.set(position, null);
                                } catch (Throwable t) {
                                }
                            }
                        });
                        dH.setUrl("http" + (MainActivity.LOCALTESTSERVER ? "" : "s") + "://" + (MainActivity.LOCALTESTSERVER ? "127.0.0.1:8080" : "www.Neon-Soft.de") + "/page/NeoPowerMenu/Presets/" + split[0] + "_" + itemsTitle.get(position).replace("'", "\\'").replace("\"", "\\\"") + ".nps");
                        dH.setLocalUrl(context.getFilesDir().getPath() + "/download");
                        dH.startDownload();
                    } else {
                        PreferencesPresetsFragment.DownloadingActiveForLabel.get(position).setText(context.getString(R.string.downloadHelper_States).split("\\|")[downloadHelper.STATE_CANCELLING] + "\n");
                        PreferencesPresetsFragment.DownloadingActiveForLayout.get(position).setEnabled(false);
                        PreferencesPresetsFragment.DownloadingActiveForLayout.get(position).setAlpha((float) .3);
                        PreferencesPresetsFragment.DownloadingActiveForHelper.get(position).stopDownload(true);
                        PreferencesPresetsFragment.DownloadingActiveForProgress.get(position).setProgress(100);
                        Animation BlinkAnim = AnimationUtils.loadAnimation(context, R.anim.progress_blink);
                        PreferencesPresetsFragment.DownloadingActiveForProgress.get(position).setAlpha((float) 1);
                        PreferencesPresetsFragment.DownloadingActiveForProgress.get(position).startAnimation(BlinkAnim);
                    }
                }
            });
            if (PreferencesPresetsFragment.OnlineHasGraphics.get(position)) {
                hasGraphics.setVisibility(View.VISIBLE);
            }
        } else if (itemsLocal.get(position).equalsIgnoreCase("LoadMore")) {
            BottomBar.setVisibility(View.GONE);
            LocalButton.setVisibility(View.GONE);
            OnlineButton.setVisibility(View.GONE);
            ItemTitle.setGravity(Gravity.CENTER);
            ItemDesc.setGravity(Gravity.CENTER);
            ItemDesc.setText(itemsDesc.get(position));
            Progress.setProgress(100);
            Animation BlinkAnim = AnimationUtils.loadAnimation(context, R.anim.progress_blink);
            Progress.setAlpha((float) 1);
            Progress.startAnimation(BlinkAnim);
            ItemTitle.setText(MainActivity.context.getString(R.string.presetsManager_LoadMore).split("\\|")[0]);
            ItemDesc.setText(MainActivity.context.getString(R.string.presetsManager_LoadMore).split("\\|")[1]);
            new getOnlinePresets(getOnlinePresets.MODE_OFFSET).execute((PreferencesPresetsFragment.onlineOrderSelectedString.isEmpty() ? "" : "order=" + PreferencesPresetsFragment.onlineOrderSelectedString), (PreferencesPresetsFragment.onlineSearchTerm.isEmpty() ? "" : "search=" + PreferencesPresetsFragment.onlineSearchTerm), "offset=" + (PreferencesPresetsFragment.OnlineListLocal.size() - 1));
            root.setEnabled(false);
        } else if (itemsLocal.get(position).equalsIgnoreCase("error")) {
            BottomBar.setVisibility(View.GONE);
            LocalButton.setVisibility(View.GONE);
            OnlineButton.setVisibility(View.GONE);
            ItemTitle.setGravity(Gravity.CENTER);
            ItemDesc.setGravity(Gravity.CENTER);
            ItemDesc.setText(itemsDesc.get(position));
            root.setEnabled(true);
            root.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View p1) {
                    
                    root.setEnabled(false);
                    Progress.setProgress(100);
                    Animation BlinkAnim = AnimationUtils.loadAnimation(context, R.anim.progress_blink);
                    Progress.setAlpha((float) 1);
                    Progress.startAnimation(BlinkAnim);
                    ItemTitle.setText(MainActivity.context.getString(R.string.presetsManager_LoadMore).split("\\|")[0]);
                    ItemDesc.setText(MainActivity.context.getString(R.string.presetsManager_LoadMore).split("\\|")[1]);
                    new getOnlinePresets(getOnlinePresets.MODE_OFFSET).execute((PreferencesPresetsFragment.onlineOrderSelectedString.isEmpty() ? "" : "order=" + PreferencesPresetsFragment.onlineOrderSelectedString), (PreferencesPresetsFragment.onlineSearchTerm.isEmpty() ? "" : "search=" + PreferencesPresetsFragment.onlineSearchTerm), "offset=" + (PreferencesPresetsFragment.OnlineListLocal.size() - 1));
                }
            });
        } else if (itemsLocal.get(position).equalsIgnoreCase("NoMore")) {
            BottomBar.setVisibility(View.GONE);
            LocalButton.setVisibility(View.GONE);
            OnlineButton.setVisibility(View.GONE);
            ItemTitle.setGravity(Gravity.CENTER);
            ItemDesc.setGravity(Gravity.CENTER);
            ItemDesc.setText(itemsDesc.get(position));
            root.setEnabled(false);
        }


        return rowView;
    }

    public String[] getItemAt(int position) {
        String[] string = new String[4];

        string[0] = this.itemsTitle.get(position);
        string[1] = this.itemsDesc.get(position);
        string[2] = this.itemsEnabled.get(position);
        string[3] = this.itemsLocal.get(position);

        return string;
    }

    public boolean findItem(String searchTerm) {
        for (int i = 0; i < itemsTitle.size() - 1; i++) {
            if (itemsTitle.get(i).equals(searchTerm)) {
                return true;
            }
        }
        return false;
    }

    public void insert(String[] string) {
        itemsTitle.add(string[0]);
        itemsDesc.add(string[1]);
        itemsEnabled.add(string[2]);
        itemsLocal.add(string[3]);
        notifyDataSetChanged();
    }

    public void insertAt(String[] string, int to) {
        itemsTitle.add(to, string[0]);
        itemsDesc.add(to, string[1]);
        itemsEnabled.add(to, string[2]);
        itemsLocal.add(to, string[3]);
        notifyDataSetChanged();
    }

    public void removeAt(int position) {
        itemsTitle.remove(position);
        itemsDesc.remove(position);
        itemsEnabled.remove(position);
        itemsLocal.remove(position);
        notifyDataSetChanged();
    }

    public void removeAll() {
        itemsTitle.clear();
        itemsDesc.clear();
        itemsEnabled.clear();
        itemsLocal.clear();
        notifyDataSetChanged();
    }

    class loadPreset extends AsyncTask<String, String, String> {

        boolean oldPreset = false;
        String presetVersionName = "1.0";
        int presetVersionCode = 0;

        @Override
        protected void onPreExecute() {
            
            PreferencesPresetsFragment.progressHolder.setVisibility(View.VISIBLE);
            PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String[] p1) {
            
            try {
                String[] builtIn = context.getString(R.string.presetLoadDialog_BuiltIn).split("\\|");
                if (p1[0].startsWith(context.getFilesDir().getPath())) {
                    File presetFile = new File(p1[0]);
                    FileInputStream fIn = new FileInputStream(presetFile);
                    BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
                    String aDataRow = "";
                    String aBuffer = "";
                    for (int i = 0; i < PreferencesColorFragment.ColorNames.length; i++) {
                        String[] loadColor = PreferencesColorFragment.ColorNames[i][1].toString().split("_");
                        if (loadColor.length > 1) {
                            if (loadColor[1].contains("Background")) {
                                MainActivity.colorPrefs.edit().putString(loadColor[0] + "_Backgroundcolor", PreferencesColorFragment.lightPreset[i]).apply();
                            } else if (loadColor[1].contains("Text")) {
                                MainActivity.colorPrefs.edit().putString(loadColor[0] + "_Textcolor", PreferencesColorFragment.lightPreset[i]).apply();
                            } else if (loadColor[1].contains("Circle")) {
                                MainActivity.colorPrefs.edit().putString(loadColor[0] + "_Circlecolor", PreferencesColorFragment.lightPreset[i]).apply();
                            }
                            publishProgress("Resetting to default...");
                        }
                    }

                    while ((aDataRow = myReader.readLine()) != null) {
                        aBuffer += aDataRow + "\n";
                        String aData[] = aDataRow.split("=");
                        String[] loadColor = aData[0].split("_");
                        if (aData[0].equalsIgnoreCase("AppVersion")) {
                            presetVersionName = aData[1];
                        } else if (aData[0].equals("AppVersionCode")) {
                            presetVersionCode = Integer.parseInt(aData[1]);
                        } else if (aData[0].equals("GraphicsPadding")) {
                            MainActivity.preferences.edit().putFloat("GraphicsPadding",Integer.parseInt(aData[1])).apply();
                        } else {
                            if (presetVersionName.equals("1.4.2")) {
                                if(aData[0].equalsIgnoreCase("RevealBackground")) {
                                    aData[0] = "Reveal_Backgroundcolor";
                                } else if (aData[0].equalsIgnoreCase("ActionRevealBackground")) {
                                    aData[0] = "ActionReveal_Backgroundcolor";
                                }
                            }
                            if (presetVersionCode < 34 && aData[0].contains("Background") && !aData[0].contains("Reveal")) {
                                String[] split = aData[0].split("_");
                                MainActivity.colorPrefs.edit().putString(split[0] + "_Circlecolor",aData[1]).apply();
                            }
                            for (int check = 0; check < PreferencesColorFragment.ColorNames.length; check++) {
                                if (PreferencesColorFragment.ColorNames[check][1].equals(aData[0]) && aData[0].contains("color")) {
                                    MainActivity.colorPrefs.edit().putString(aData[0], aData[1]).apply();
                                    publishProgress(loadColor[0] + ": " + aData[1]);
                                }
                            }
                        }
                    }
                    if (presetFile.getPath().startsWith(context.getFilesDir().getPath() + "/temp/")) {
                        File presetsFolder = new File(context.getFilesDir().getPath() + "/temp/");
                        File[] presetsFiles = presetsFolder.listFiles(new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                return true;
                            }
                        });
                        for (int i = 0; i < presetsFiles.length; i++) {
                            presetsFiles[i].delete();
                        }
                    }
                } else {
                    String[] preset = PreferencesColorFragment.lightPreset;
                    if (p1[0].equalsIgnoreCase(builtIn[0])) {
                        preset = PreferencesColorFragment.lightPreset;
                    } else if (p1[0].equalsIgnoreCase(builtIn[1])) {
                        preset = PreferencesColorFragment.darkPreset;
                    } else if (p1[0].equalsIgnoreCase(builtIn[2])) {
                        preset = PreferencesColorFragment.blackPreset;
                    }
                    for (int i = 0; i < PreferencesColorFragment.ColorNames.length; i++) {
                        String[] loadColor = PreferencesColorFragment.ColorNames[i][1].toString().split("_");
                        if (loadColor.length > 1) {
                            if (loadColor[1].contains("Background")) {
                                MainActivity.colorPrefs.edit().putString(loadColor[0] + "_Backgroundcolor", preset[i]).apply();
                            } else if (loadColor[1].contains("Text")) {
                                MainActivity.colorPrefs.edit().putString(loadColor[0] + "_Textcolor", preset[i]).apply();
                            } else if (loadColor[1].contains("Circle")) {
                                MainActivity.colorPrefs.edit().putString(loadColor[0] + "_Circletcolor", preset[i]).apply();
                            }
                            publishProgress(loadColor[0] + ": " + preset[i]);
                        }
                    }
                }
                return "success";
            } catch (Throwable e) {
                Log.e("NPM:import", "Failed to import: ", e);
                return e.toString();
            }
        }

        @Override
        protected void onProgressUpdate(String[] p1) {
            
            super.onProgressUpdate(p1);
            PreferencesPresetsFragment.LoadingMsg.setText(context.getString(R.string.presetsManager_Loading) + "\n" + p1[0]);
        }

        @Override
        protected void onPostExecute(String p1) {
            
            super.onPostExecute(p1);
            if (p1.equalsIgnoreCase("success")) {
                MainActivity.preferences.edit().putString("lastUsedPreset", selectedName).apply();
                Toast.makeText(context.getApplicationContext(), context.getString(R.string.presetLoad_PresetLoaded).replace("[PRESETNAME]", selectedName), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context.getApplicationContext(), context.getString(R.string.presetsManager_ImportFailed) + "\n" + p1, Toast.LENGTH_SHORT).show();
            }
            PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
            PreferencesPresetsFragment.progressHolder.setVisibility(View.GONE);
            notifyDataSetChanged();
        }

    }

}

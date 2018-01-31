package de.NeonSoft.neopowermenu.helpers;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.content.FileProvider;
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

public class PresetsAdapter extends ArrayAdapter<PresetsHolder> {

    public static String selectedName = "";
    public static AlertDialog uploadad;

    private final Activity context;
    private final ArrayList<PresetsHolder> mItems;

    public PresetsAdapter(Activity context,
                          ArrayList<PresetsHolder> items) {
        super(context, R.layout.presetmanager_listitem, items);
        this.context = context;
        this.mItems = items;
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

        final PresetsHolder preset = mItems.get(position);
        final LayoutInflater inflater = context.getLayoutInflater();
        View rowView;
        //final String prefname = this.itemsSwitchPrefName.get(position);
        rowView = inflater.inflate(R.layout.presetmanager_listitem, null, true);

        final LinearLayout root = (LinearLayout) rowView.findViewById(R.id.root);
        final TextView ItemTitle = (TextView) rowView.findViewById(R.id.title);
        ItemTitle.setGravity(Gravity.NO_GRAVITY);
        final TextView ItemDesc = (TextView) rowView.findViewById(R.id.text);
        ItemDesc.setGravity(Gravity.NO_GRAVITY);
        final LinearLayout presetContent = (LinearLayout) rowView.findViewById(R.id.presetmanagerlistitemLinearLayout_Content);
        presetContent.setVisibility(View.GONE);
        final ImageView hasColors = (ImageView) rowView.findViewById(R.id.presetmanagerlistitemImageView_hasColors);
        hasColors.setAlpha((float) .2);
        final ImageView hasGraphics = (ImageView) rowView.findViewById(R.id.presetmanagerlistitemImageView_hasGraphics);
        hasGraphics.setAlpha((float) .2);
        final ImageView hasAnimations = (ImageView) rowView.findViewById(R.id.presetmanagerlistitemImageView_hasAnimations);
        hasAnimations.setAlpha((float) .2);
        final ImageView hasRoundedCorners = (ImageView) rowView.findViewById(R.id.presetmanagerlistitemImageView_hasRoundedCorners);
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

        ItemTitle.setText(this.mItems.get(position).getName());
        final String[] split = this.mItems.get(position).getDescription().split(",=,");
        String desc = context.getString(R.string.presetsManager_Creator).replace("[CREATORNAME]", mItems.get(position).getDescription());
        if (split.length > 1) {
            desc = context.getString(R.string.presetsManager_Creator).replace("[CREATORNAME]", split[0]) + "\nNeoPowerMenu " + split[1];
            StarsCount.setText(context.getString(R.string.presetsManager_Stars).replace("[STARS]", split[2]));
        }
        ItemDesc.setText(desc);
        preset.setRoot(root);
        preset.setLayout(OnlineButton);
        preset.setImageView(OnlineButtonImage);
        preset.setTexView(ItemDesc);
        preset.setProgressBar(Progress);
        if (preset.getType() == PresetsHolder.TYPE_INTERNAL) {
            root.setEnabled(true);
            if (position >= 3) {
                presetContent.setVisibility(View.VISIBLE);
                if (preset.getHasColors()) {
                    hasColors.setAlpha((float) 1);
                }
                if (preset.getHasGraphics()) {
                    hasGraphics.setAlpha((float) 1);
                }
                if (preset.getHasAnimations()) {
                    hasAnimations.setAlpha((float) 1);
                }
                if (preset.getHasRoundCorners()) {
                    hasRoundedCorners.setImageResource(R.drawable.ic_rounded_corner);
                } else {
                    hasRoundedCorners.setImageResource(R.drawable.ic_crop_square);
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
                                final String presetName = resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0").replace("/", "").trim();
                                final String presetCreator = resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "1").replace("/", "").trim();
                                File presetFile = new File(context.getFilesDir().getPath() + "/presets/" + mItems.get(position).getName() + ".nps");
                                String features = "";
                                String path = "";
                                try {
                                    FileInputStream fIn;
                                    if (helper.isValidZip(context.getFilesDir().getPath() + "/presets/" + mItems.get(position).getName() + ".nps", null)) {
                                        helper.copyFile(context.getFilesDir().getPath() + "/presets/" + mItems.get(position).getName() + ".nps", context.getFilesDir().getPath() + "/temp/" + presetName + ".nps.tmp");
                                        helper.unzipFile(context.getFilesDir().getPath() + "/temp/" + presetName + ".nps.tmp", context.getFilesDir().getPath() + "/temp/", mItems.get(position).getName() + ".nps", null);
                                        new File(context.getFilesDir().getPath() + "/temp/" + mItems.get(position).getName() + ".nps").renameTo(new File(context.getFilesDir().getPath() + "/temp/" + presetName + ".nps"));
                                        helper.removeFromZip(context.getFilesDir().getPath() + "/temp/" + presetName + ".nps.tmp", mItems.get(position).getName() + ".nps", null);
                                        helper.zipFile(context.getFilesDir().getPath() + "/temp/" + presetName + ".nps", context.getFilesDir().getPath() + "/temp/" + presetName + ".nps.tmp", null);
                                        path = context.getFilesDir().getPath() + "/temp/" + presetName + ".nps";
                                        presetFile = new File(context.getFilesDir().getPath() + "/temp/" + presetName + ".nps");
                                    } else {
                                        path = context.getFilesDir().getPath() + "/presets/" + mItems.get(position).getName() + ".nps";
                                    }
                                    fIn = new FileInputStream(presetFile);
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
                                if (mItems.get(position).getHasGraphics()) {
                                    new File(context.getFilesDir().getPath() + "/temp/" + presetName + ".nps").delete();
                                    new File(context.getFilesDir().getPath() + "/temp/" + presetName + ".nps.tmp").renameTo(new File(context.getFilesDir().getPath() + "/temp/" + presetName + ".nps"));
                                }
                                if (mItems.get(position).getHasColors()) {
                                    features += (features.isEmpty() ? "" : ",") + "colors";
                                }
                                if (mItems.get(position).getHasAnimations()) {
                                    features += (features.isEmpty() ? "" : ",") + "animations";
                                }
                                if (mItems.get(position).getHasGraphics()) {
                                    features += (features.isEmpty() ? "" : ",") + "graphics";
                                }
                                if (mItems.get(position).getHasRoundCorners()) {
                                    features += (features.isEmpty() ? "" : ",") + "roundcorners";
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
                                                dialogFragment.setText(context.getString(R.string.uploadHelper_States).split("\\|")[uH.getState()] + ((uH.getSizes()[0] > 0 && uH.getState() != uploadHelper.STATE_CANCELLING) ? "\n" + helper.getSizeString(uH.getSizes()[0], true) + "/" + helper.getSizeString(uH.getSizes()[1], true) + "\neta: " + helper.getTimeString(context, uH.getETA(), 1) + ", speed: " + helper.getSizeString(uH.getAvgSpeed(), true) + "/s" : "\n\n"));
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
                                        File presetsFolder = new File(context.getFilesDir().getPath() + "/temp/");
                                        File[] presetsFiles = presetsFolder.listFiles(new FilenameFilter() {
                                            public boolean accept(File dir, String name) {
                                                return true;
                                            }
                                        });
                                        for (int i = 0; i < presetsFiles.length; i++) {
                                            presetsFiles[i].delete();
                                        }
                                        //}
                                        PreferencesPresetsFragment.listParser = helper.startAsyncTask(new getOnlinePresets(), (PreferencesPresetsFragment.onlineOrderSelectedString.isEmpty() ? "" : "order=" + PreferencesPresetsFragment.onlineOrderSelectedString));
                                        Toast.makeText(context, context.getString(R.string.presetsManager_UploadComplete), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onUploadFailed(String reason) {

                                        dialogFragment.closeDialog();
                                        File presetsFolder = new File(context.getFilesDir().getPath() + "/temp/");
                                        File[] presetsFiles = presetsFolder.listFiles(new FilenameFilter() {
                                            public boolean accept(File dir, String name) {
                                                return true;
                                            }
                                        });
                                        for (int i = 0; i < presetsFiles.length; i++) {
                                            presetsFiles[i].delete();
                                        }
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
                                                        dialogFragment.setInputMode(1, InputType.TYPE_TEXT_VARIATION_PASSWORD);
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
                                uH.setServerUrl("http" + (MainActivity.LOCALTESTSERVER ? "" : "s") + "://" + (MainActivity.LOCALTESTSERVER ? "127.0.0.1:8080" : "www.neon-soft.de") + "/page/NeoPowerMenu/phpWebservice/webservice2.php");
                                uH.setLocalUrl(path);
                                uH.uploadAs(resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0") + ".nps");
                                uH.setAdditionalUploadPosts(new String[][]{{"presetName", resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0")}, {"presetCreator", resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "1")}, {"presetAppVersion", "v" + appVersion}, {MainActivity.usernameemail.contains("@") ? "userEmail" : "userName", "" + MainActivity.usernameemail}, {"userId", "" + MainActivity.deviceUniqeId}, {"accId", "" + MainActivity.accountUniqeId}, {"presetContent", features}});
                                uH.startUpload();
                            }

                            @Override
                            public void onTouchOutside() {

                            }
                        });
                        dialogFragment.setText(context.getString(R.string.presetsManager_UploadMsg));
                        dialogFragment.addInput(context.getString(R.string.presetSaveDialog_InfoText), mItems.get(position).getName(), false, null);
                        dialogFragment.addInput(context.getString(R.string.presetSaveDialog_CreatorNameInfo), mItems.get(position).getDescription(), false, null);
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
                                    String selectedName = mItems.get(position).getName();
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
                        dialogFragment.setText(context.getString(R.string.presetsManager_SureToDelete).replace("[PRESETNAME]", mItems.get(position).getName()));
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
                            File sharedfolder = new File(context.getExternalFilesDir(null) + "/sharedpresets");
                            File tmpfile = new File(context.getExternalFilesDir(null) + "/sharedpresets/" + mItems.get(position).getName() + ".nps");
                            sharedfolder.mkdirs();
                            tmpfile.delete();
                            helper.copyFile(context.getFilesDir().getPath() + "/presets/" + mItems.get(position).getName() + ".nps", context.getExternalFilesDir(null) + "/sharedpresets/" + mItems.get(position).getName() + ".nps");

                            shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, MainActivity.class.getPackage().getName()+".fileprovider", new File(context.getExternalFilesDir(null) + "/sharedpresets/" + mItems.get(position).getName() + ".nps")));
                            context.startActivity(Intent.createChooser(shareIntent, mItems.get(position).getName()));
                            PreferencesPresetsFragment.progress.setVisibility(View.GONE);
                        } catch (Throwable e) {
                            Log.e("NPM", "Failed to share preset:", e);
                        }
                    }
                });
            }
            if (MainActivity.preferences.getString("lastUsedPreset", "").equalsIgnoreCase(mItems.get(position).getName())) {
                //ItemSwitch.setEnabled(false);
                ItemSwitch.setChecked(true);
            } else {
                String[] builtIn = context.getString(R.string.presetsManager_BuiltIn).split("/");
                ItemSwitch.setChecked(MainActivity.preferences.getString("lastUsedPreset", builtIn[0]).equalsIgnoreCase(mItems.get(position).getName()));
            }
            root.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View p1) {

                    selectedName = mItems.get(position).getName();
                    if (position > 2) {
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
                                new loadPreset().execute(context.getFilesDir().getPath() + "/presets/" + selectedName + ".nps", resultBundle.getString(slideDownDialogFragment.RESULT_LIST));
                            }

                            @Override
                            public void onTouchOutside() {

                            }
                        });
                        dialogFragment.setText(context.getString(R.string.presetsManager_LoadPreset));
                        ArrayList<String> options = new ArrayList<>();
                        ArrayList<Boolean> checked = new ArrayList<>();
                        if (mItems.get(position).getHasGraphics()) {
                            options.add(context.getString(R.string.loadPreset_Graphics));
                            checked.add(false);
                        }
                        if (mItems.get(position).getHasColors()) {
                            options.add(context.getString(R.string.loadPreset_Colors));
                            checked.add(true);
                        }
                        if (mItems.get(position).getHasAnimations()) {
                            options.add(context.getString(R.string.loadPreset_Animations));
                            checked.add(false);
                        }
                        if (options.size() == 1) {
                            new loadPreset().execute(context.getFilesDir().getPath() + "/presets/" + selectedName + ".nps", options.get(0));
                        } else {
                            dialogFragment.setList(ListView.CHOICE_MODE_MULTIPLE, options, -1, false);
                            dialogFragment.setListChecks(checked);
                            dialogFragment.setListReturnMode(slideDownDialogFragment.LIST_RETURN_MODE_TEXT);
                            dialogFragment.setNegativeButton(context.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                            dialogFragment.setPositiveButton(context.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_LOAD]);
                            dialogFragment.showDialog(R.id.dialog_container);
                        }
                    } else {
                        new loadPreset().execute(selectedName, context.getString(R.string.loadPreset_Colors));
                    }
                }
            });
        } else if (preset.getType() == PresetsHolder.TYPE_ONLINE) {
            root.setEnabled(true);
            LocalButton.setVisibility(View.GONE);
            OnlineButton.setVisibility(View.VISIBLE);
            try {
                BottomBar.setVisibility(View.VISIBLE);
                Upload.setVisibility(View.GONE);
                Share.setVisibility(View.GONE);
                if (MainActivity.preferences.getString("ratedFor", "").contains("&" + mItems.get(position).getName() + ",")) {
                    StarImage.setImageResource(R.drawable.ic_action_star_0);
                    StarText.setText(context.getString(R.string.presetsManager_Buttons).split("\\|")[1]);
                } else {
                    StarImage.setImageResource(R.drawable.ic_action_star_10);
                    StarText.setText(context.getString(R.string.presetsManager_Buttons).split("\\|")[0]);
                }
                if (MainActivity.userRank.equals("A") || preset.getId().equals(MainActivity.deviceUniqeId) || preset.getId().equals(MainActivity.accountUniqeId)) {
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
                                            Toast.makeText(context, context.getString(R.string.presetLoad_PresetDeleted).replace("[PRESETNAME]", mItems.get(position).getName()), Toast.LENGTH_SHORT).show();
                                            PreferencesPresetsFragment.listParser = helper.startAsyncTask(new getOnlinePresets(), (PreferencesPresetsFragment.onlineOrderSelectedString.isEmpty() ? "" : "order=" + PreferencesPresetsFragment.onlineOrderSelectedString));
                                        }

                                        @Override
                                        public void onUploadFailed(String reason) {

                                            PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
                                            PreferencesPresetsFragment.progressHolder.setVisibility(View.GONE);
                                            Toast.makeText(context, "Failed to delete.\n" + reason, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    uH.setServerUrl("http" + (MainActivity.LOCALTESTSERVER ? "" : "s") + "://" + (MainActivity.LOCALTESTSERVER ? "127.0.0.1:8080" : "www.neon-soft.de") + "/page/NeoPowerMenu/phpWebservice/webservice1.php");
                                    uH.setAdditionalUploadPosts(new String[][]{{"action", "delete"}, {"presetName", mItems.get(position).getName()}, {"userId", (MainActivity.userRank.equalsIgnoreCase("A") ? preset.getId() : preset.getId().equals(MainActivity.deviceUniqeId) ? MainActivity.deviceUniqeId : MainActivity.accountUniqeId)}});
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
                            dialogFragment.setText(context.getString(R.string.presetsManager_SureToDelete).replace("[PRESETNAME]", mItems.get(position).getName()));
                            dialogFragment.setNegativeButton(context.getString(R.string.Dialog_Buttons).split("\\|")[4]);
                            dialogFragment.setPositiveButton(context.getString(R.string.Dialog_Buttons).split("\\|")[5]);
                            dialogFragment.showDialog(R.id.dialog_container);
                        }
                    });
                }
                if (!preset.getId().equals(MainActivity.deviceUniqeId) && !preset.getId().equals(MainActivity.accountUniqeId)) {
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
                                                Toast.makeText(context, context.getString(R.string.presetsManager_ReportResultSuccess).replace("[PRESETNAME]", mItems.get(position).getName()), Toast.LENGTH_SHORT).show();
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
                                                dialogFragment.setText(context.getString(R.string.presetsManager_ReportResultFailed) + reason);
                                                dialogFragment.setPositiveButton(context.getString(R.string.Dialog_Buttons).split("\\|")[0]);
                                                dialogFragment.showDialog(R.id.dialog_container);
                                            }
                                        });
                                        uH.setServerUrl("http" + (MainActivity.LOCALTESTSERVER ? "" : "s") + "://" + (MainActivity.LOCALTESTSERVER ? "127.0.0.1:8080" : "www.neon-soft.de") + "/page/NeoPowerMenu/phpWebservice/webservice3.php");
                                        uH.setAdditionalUploadPosts(new String[][]{{"action", "report"}, {"presetName", mItems.get(position).getName()}, {"reason", resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0").replace("\n", "<br>")}, {"accountId", MainActivity.accountUniqeId}});
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
                                dialogFragment.setText(context.getString(R.string.presetsManager_ReportDialogText).replace("[PRESETNAME]", mItems.get(position).getName()));
                                dialogFragment.addInput(context.getString(R.string.presetsManager_ReportDialogReason), "", false, null);
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
                                        MainActivity.preferences.edit().putString("ratedFor", MainActivity.preferences.getString("ratedFor", "") + "&" + mItems.get(position).getName() + ",").commit();
                                    } else {
                                        StarsCount.setText(context.getString(R.string.presetsManager_Stars).replace("[STARS]", "" + (Integer.parseInt(StarsCount.getText().toString().split(": ")[1]) - 1)));
                                        StarImage.setImageResource(R.drawable.ic_action_star_10);
                                        StarText.setText(context.getString(R.string.presetsManager_Buttons).split("\\|")[0]);
                                        MainActivity.preferences.edit().putString("ratedFor", MainActivity.preferences.getString("ratedFor", "").replace("&" + mItems.get(position).getName() + ",", "")).commit();
                                    }
                                    String[] split = mItems.get(position).getDescription().split(",=,");
                                    preset.setDescription(split[0] + ",=," + split[1] + ",=," + (Integer.parseInt(StarsCount.getText().toString().split(": ")[1])));
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
                            uH.setServerUrl("http" + (MainActivity.LOCALTESTSERVER ? "" : "s") + "://" + (MainActivity.LOCALTESTSERVER ? "127.0.0.1:8080" : "www.neon-soft.de") + "/page/NeoPowerMenu/phpWebservice/webservice3.php");
                            uH.setAdditionalUploadPosts(new String[][]{{"action", (StarText.getText().toString().equalsIgnoreCase(context.getString(R.string.presetsManager_Buttons).split("\\|")[0]) ? "givestar" : "removestar")}, {(MainActivity.usernameemail.contains("@") ? "userEmail" : "userName"), MainActivity.usernameemail}, {"name", mItems.get(position).getName()}});
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
            if (preset.getDownloadHelper() != null) {
                Progress.setProgress(PreferencesPresetsFragment.OnlinePresets.get(position).getDownloadHelper().getProgress());
                OnlineButtonImage.setImageResource(R.drawable.ic_action_cancel);
                long[] sizes = preset.getDownloadHelper().getSizes();
                preset.getTextView().setText(context.getString(R.string.downloadHelper_States).split("\\|")[preset.getDownloadHelper().getState()] + (preset.getDownloadHelper().getState() == downloadHelper.STATE_DOWNLOADING ? " - " + helper.getSizeString(sizes[0], true) + "/" + helper.getSizeString(sizes[1], true) + " | " + preset.getDownloadHelper().getProgress() + "%\n" + context.getString(R.string.downloadHelper_RemainingTime).replace("[TIME]", helper.getTimeString(context, preset.getDownloadHelper().getETA(), 1)) + " | " + context.getString(R.string.downloadHelper_Speed).replace("[SPEED]", helper.getSizeString(preset.getDownloadHelper().getAvgSpeed(), true)) : "\n"));
                if (preset.getDownloadHelper().getState() != downloadHelper.STATE_DOWNLOADING) {
                    preset.getProgessBar().setProgress(100);
                    Animation BlinkAnim = AnimationUtils.loadAnimation(context, R.anim.progress_blink);
                    preset.getProgessBar().startAnimation(BlinkAnim);
                } else {
                    preset.getProgessBar().setAlpha((float) .2);
                }
                PreferencesPresetsFragment.OnlinePresets.set(position, preset);
            }
            root.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View p1) {

                    if (position < (PreferencesPresetsFragment.OnlinePresets.size()) && PreferencesPresetsFragment.OnlinePresets.get(position).getDownloadHelper() == null) {
                        final downloadHelper dH = new downloadHelper(context);
                        dH.setInterface(new downloadHelper.downloadHelperInterface() {

                            @Override
                            public void onStateChanged(int state) {

                                try {
                                    if (state == downloadHelper.STATE_WAITING) {
                                        preset.setDownloadHelper(dH);
                                        //PreferencesPresetsFragment.DownloadingActiveForRoot.set(position, root);
                                        //PreferencesPresetsFragment.DownloadingActiveForLayout.set(position, OnlineButton);
                                        //PreferencesPresetsFragment.DownloadingActiveForImageView.set(position, OnlineButtonImage);
                                        //PreferencesPresetsFragment.DownloadingActiveForLabel.set(position, ItemDesc);
                                        //PreferencesPresetsFragment.DownloadingActiveForProgress.set(position, Progress);
                                        preset.setOldDescription(ItemDesc.getText().toString());
                                        //if (PreferencesPresetsFragment.DownloadingActiveFor.equals("")) {
                                        //    PreferencesPresetsFragment.DownloadingActiveFor = mItems.get(position).getName();
                                        //}
                                        //PreferencesPresetsFragment.DownloadingActiveForLabel[position].setText(context.getString(R.string.downloadHelper_States).split("\\|")[state] + "\n");
                                        preset.getProgessBar().setProgress(100);
                                        Animation BlinkAnim = AnimationUtils.loadAnimation(context, R.anim.progress_blink);
                                        preset.getProgessBar().setAlpha((float) 1);
                                        preset.getProgessBar().startAnimation(BlinkAnim);
                                        //OnlineButton.setEnabled(false);
                                        //OnlineButton.setAlpha((float) .3);
                                        preset.getImageView().setImageResource(R.drawable.ic_action_cancel);
                                    }
                                    preset.getTextView().setText(context.getString(R.string.downloadHelper_States).split("\\|")[state] + "\n");
                                    PreferencesPresetsFragment.OnlinePresets.set(position, preset);
                                } catch (Throwable t) {
                                    Log.e("NPM", "Failed to start download: " + t.toString());
                                }
                            }

                            @Override
                            public void onPublishDownloadProgress(long nowSize, long totalSize) {
                                try {
                                    if (preset.getDownloadHelper().getState() != downloadHelper.STATE_CANCELLING) {
                                        preset.getTextView().setText(context.getString(R.string.downloadHelper_States).split("\\|")[preset.getDownloadHelper().getState()] + " - " + helper.getSizeString(nowSize, true) + "/" + helper.getSizeString(totalSize, true) + " | " + preset.getDownloadHelper().getProgress() + "%\n" + context.getString(R.string.downloadHelper_RemainingTime).replace("[TIME]", helper.getTimeString(context, preset.getDownloadHelper().getETA(), 1)) + " | " + context.getString(R.string.downloadHelper_Speed).replace("[SPEED]", helper.getSizeString(preset.getDownloadHelper().getAvgSpeed(), true)));
                                        if (preset.getDownloadHelper().getSizes()[0] > 0) {
                                            preset.getProgessBar().clearAnimation();
                                            preset.getProgessBar().setAlpha((float) 0.2);
                                        }
                                        preset.getProgessBar().setProgress((int) ((nowSize * 100) / totalSize));
                                        preset.getRoot().postInvalidate();
                                    }
                                } catch (Throwable t) {
                                    Log.e("NPM", "Failed to update progress: " + t.toString());
                                }
                            }

                            @Override
                            public void onDownloadComplete() {

                                try {
                                    preset.getProgessBar().setProgress(0);
                                    preset.getProgessBar().clearAnimation();
                                    preset.getProgessBar().setAlpha((float) 0.2);
                                    preset.getTextView().setText(preset.getOldDescription());
                                    preset.getLayout().setEnabled(true);
                                    preset.getLayout().setAlpha((float) 1);
                                    preset.getImageView().setImageResource(R.drawable.ic_file_file_download);
                                    //PreferencesPresetsFragment.DownloadingActiveFor = "";
                                    preset.setDownloadHelper(null);
                                    PreferencesPresetsFragment.OnlinePresets.set(position, preset);
                                    try {
                                        PreferencesPresetsFragment.ImportPreset("file://" + context.getFilesDir().getPath() + "/download/" + split[0] + "_" + mItems.get(position).getName().replace("'", "\\\'").replace("\"", "\\\"") + ".nps", PreferencesPresetsFragment.localAdapter, mItems.get(position).getName(), null);
                                    } catch (Exception e) {
                                        new File(context.getFilesDir().getPath() + "/download/" + mItems.get(position).getName().replace("'", "\\'").replace("\"", "\\\"") + ".nps").delete();
                                        Toast.makeText(context, context.getString(R.string.presetsManager_ImportFailed) + "\n" + e.toString(), Toast.LENGTH_LONG).show();
                                        Log.e("NPM", e.toString());
                                    }
                                } catch (Throwable t) {
                                }
                            }

                            @Override
                            public void onDownloadFailed(String reason) {

                                try {
                                    preset.getProgessBar().setProgress(0);
                                    preset.getProgessBar().clearAnimation();
                                    preset.getProgessBar().setAlpha((float) 0.2);
                                    preset.getTextView().setText(preset.getOldDescription());
                                    preset.getLayout().setEnabled(true);
                                    preset.getLayout().setAlpha((float) 1);
                                    preset.getImageView().setImageResource(R.drawable.ic_file_file_download);
                                    //PreferencesPresetsFragment.DownloadingActiveFor = "";
                                    preset.setDownloadHelper(null);
                                    if (!reason.equalsIgnoreCase("canceled"))
                                        Toast.makeText(context, reason, Toast.LENGTH_LONG).show();
                                    PreferencesPresetsFragment.OnlinePresets.set(position, preset);
                                } catch (Throwable t) {
                                }
                            }
                        });
                        dH.setUrl("http" + (MainActivity.LOCALTESTSERVER ? "" : "s") + "://" + (MainActivity.LOCALTESTSERVER ? "127.0.0.1:8080" : "www.neon-soft.de") + "/page/NeoPowerMenu/Presets/" + split[0] + "_" + mItems.get(position).getName().replace("'", "\\'").replace("\"", "\\\"") + ".nps");
                        dH.setLocalUrl(context.getFilesDir().getPath() + "/download");
                        dH.setAllowMultiple(true);
                        dH.startDownload();
                    } else {
                        preset.getTextView().setText(context.getString(R.string.downloadHelper_States).split("\\|")[downloadHelper.STATE_CANCELLING] + "\n");
                        preset.getLayout().setEnabled(false);
                        preset.getLayout().setAlpha((float) .3);
                        preset.getDownloadHelper().stopDownload(true);
                        preset.getProgessBar().setProgress(100);
                        Animation BlinkAnim = AnimationUtils.loadAnimation(context, R.anim.progress_blink);
                        preset.getProgessBar().setAlpha((float) 1);
                        preset.getProgessBar().startAnimation(BlinkAnim);
                    }
                }
            });
            presetContent.setVisibility(View.VISIBLE);
            if (preset.getHasColors()) {
                hasColors.setAlpha((float) 1);
            }
            if (preset.getHasGraphics()) {
                hasGraphics.setAlpha((float) 1);
            }
            if (preset.getHasAnimations()) {
                hasAnimations.setAlpha((float) 1);
            }
            if (preset.getHasRoundCorners()) {
                hasRoundedCorners.setImageResource(R.drawable.ic_rounded_corner);
            } else {
                hasRoundedCorners.setImageResource(R.drawable.ic_crop_square);
            }
        } else if (preset.getType() == PresetsHolder.TYPE_LOADMORE) {
            BottomBar.setVisibility(View.GONE);
            LocalButton.setVisibility(View.GONE);
            OnlineButton.setVisibility(View.GONE);
            ItemTitle.setGravity(Gravity.CENTER);
            ItemDesc.setGravity(Gravity.CENTER);
            ItemDesc.setText(mItems.get(position).getDescription());
            Progress.setProgress(100);
            Animation BlinkAnim = AnimationUtils.loadAnimation(context, R.anim.progress_blink);
            Progress.setAlpha((float) 1);
            Progress.startAnimation(BlinkAnim);
            ItemTitle.setText(MainActivity.context.getString(R.string.presetsManager_LoadMore).split("\\|")[0]);
            ItemDesc.setText(MainActivity.context.getString(R.string.presetsManager_LoadMore).split("\\|")[1]);
            PreferencesPresetsFragment.listParser = helper.startAsyncTask(new getOnlinePresets(getOnlinePresets.MODE_OFFSET), (PreferencesPresetsFragment.onlineOrderSelectedString.isEmpty() ? "" : "order=" + PreferencesPresetsFragment.onlineOrderSelectedString), (PreferencesPresetsFragment.onlineSearchTerm.isEmpty() ? "" : "search=" + PreferencesPresetsFragment.onlineSearchTerm), "offset=" + (PreferencesPresetsFragment.OnlinePresets.size() - 1));
            root.setEnabled(false);
        } else if (preset.getType() == PresetsHolder.TYPE_ERROR) {
            BottomBar.setVisibility(View.GONE);
            LocalButton.setVisibility(View.GONE);
            OnlineButton.setVisibility(View.GONE);
            ItemTitle.setGravity(Gravity.CENTER);
            ItemDesc.setGravity(Gravity.CENTER);
            ItemDesc.setText(mItems.get(position).getDescription());
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
                    PreferencesPresetsFragment.listParser = helper.startAsyncTask(new getOnlinePresets(getOnlinePresets.MODE_OFFSET), (PreferencesPresetsFragment.onlineOrderSelectedString.isEmpty() ? "" : "order=" + PreferencesPresetsFragment.onlineOrderSelectedString), (PreferencesPresetsFragment.onlineSearchTerm.isEmpty() ? "" : "search=" + PreferencesPresetsFragment.onlineSearchTerm), "offset=" + (PreferencesPresetsFragment.OnlinePresets.size() - 1));
                }
            });
        } else if (preset.getType() == PresetsHolder.TYPE_NOMORE) {
            BottomBar.setVisibility(View.GONE);
            LocalButton.setVisibility(View.GONE);
            OnlineButton.setVisibility(View.GONE);
            ItemTitle.setGravity(Gravity.CENTER);
            ItemDesc.setGravity(Gravity.CENTER);
            ItemDesc.setText(mItems.get(position).getDescription());
            root.setEnabled(false);
        }

        mItems.set(position, preset);

        return rowView;
    }

    public PresetsHolder getItemAt(int position) {
        return mItems.get(position);
    }

    public boolean findItem(String searchTerm) {
        for (int i = 0; i < mItems.size() - 1; i++) {
            if (mItems.get(i).getName().equals(searchTerm)) {
                return true;
            }
        }
        return false;
    }

    public void insert(PresetsHolder item) {
        mItems.add(item);
        notifyDataSetChanged();
    }

    public void insertAt(PresetsHolder item, int to) {
        mItems.add(to, item);
        notifyDataSetChanged();
    }

    public void removeAt(int position) {
        mItems.remove(position);
        notifyDataSetChanged();
    }

    public void removeAll() {
        mItems.clear();
        notifyDataSetChanged();
    }

    class loadPreset extends AsyncTask<String, String, String> {

        boolean oldPreset = false;
        String presetVersionName = "1.0";
        int presetVersionCode = 0;

        String oldActionReveal = "";

        @Override
        protected void onPreExecute() {

            PreferencesPresetsFragment.progressHolder.setVisibility(View.VISIBLE);
            PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String[] p1) {
            String selectedOptions = p1[1];
            Log.i("NPM","Trying to load preset: " + p1[0] + "\nLoading the components: " + p1[1].split(" ")[1]);
            try {
                String[] builtIn = context.getString(R.string.presetLoadDialog_BuiltIn).split("\\|");
                if (p1[0].startsWith(context.getFilesDir().getPath())) {
                    if (selectedOptions.contains(context.getString(R.string.loadPreset_Colors))) {
                        for (int i = 0; i < neopowermenu.colors.size(); i++) {
                            for (int x = 2; x < neopowermenu.colors.get(i).length; x++) {
                                MainActivity.colorPrefs.edit().putString(neopowermenu.colors.get(i)[x].toString(), neopowermenu.defaultColors.get(i)[x-1]).apply();
                            }
                            publishProgress("Resetting to default...");
                        }
                    }

                    if (helper.isValidZip(p1[0], null)) {
                        helper.unzipAll(context.getFilesDir().getPath() + "/presets/" + selectedName + ".nps", context.getFilesDir().getPath() + "/temp/", null);
                        p1[0] = context.getFilesDir().getPath() + "/temp/" + selectedName + ".nps";
                    }
                    File presetFile = new File(p1[0]);
                    FileInputStream fIn = new FileInputStream(presetFile);
                    BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
                    String aDataRow = "";
                    String aBuffer = "";

                    if (selectedOptions.contains(context.getString(R.string.loadPreset_Graphics))) {
                        for (int i = 0; i < PreferencesGraphicsFragment.graphics.length; i++) {
                            try {
                                publishProgress("Loading graphic \"" + PreferencesGraphicsFragment.graphics[i][2] + "\"");
                                new File(context.getFilesDir().getPath() + "/images/" + PreferencesGraphicsFragment.graphics[i][2] + ".png").delete();
                                new File(context.getFilesDir().getPath() + "/temp/" + PreferencesGraphicsFragment.graphics[i][2] + ".png").renameTo(new File(context.getFilesDir().getPath() + "/images/" + PreferencesGraphicsFragment.graphics[i][2] + ".png"));
                                MemoryCacheUtils.removeFromCache(((context.getFilesDir().getPath() + "/images/" + PreferencesGraphicsFragment.graphics[i][2] + ".png").startsWith("file://") ? "" : "file://") + context.getFilesDir().getPath() + "/images/" + PreferencesGraphicsFragment.graphics[i][2] + ".png", MainActivity.imageLoader.getMemoryCache());
                                DiskCacheUtils.removeFromCache(((context.getFilesDir().getPath() + "/images/" + PreferencesGraphicsFragment.graphics[i][2] + ".png").startsWith("file://") ? "" : "file://") + context.getFilesDir().getPath() + "/images/" + PreferencesGraphicsFragment.graphics[i][2] + ".png", MainActivity.imageLoader.getDiscCache());
                            } catch (Throwable t) {
                                Log.d("NPM", "Cant move graphic " + PreferencesGraphicsFragment.graphics[i][0] + "-" + PreferencesGraphicsFragment.graphics[i][1] + ".png, reason: " + t.toString());
                            }
                        }
                        if (PreferencesGraphicsFragment.graphics[0][2].toString().equalsIgnoreCase("Progress") && new File(context.getFilesDir().getPath() + "/images/" + PreferencesGraphicsFragment.graphics[0][2] + ".png").exists()) {
                            MainActivity.preferences.edit().putString("ProgressDrawable", "file").commit();
                        } else {
                            MainActivity.preferences.edit().putString("ProgressDrawable", "stock").commit();
                        }
                    }
                    String currentSection = "main";
                    while ((aDataRow = myReader.readLine()) != null) {
                        aBuffer += aDataRow + "\n";
                        String aData[] = aDataRow.split("=");
                        String[] loadColor = aData[0].split("_");
                        if (aData[0].equalsIgnoreCase("AppVersion")) {
                            presetVersionName = aData[1];
                        } else if (aData[0].equals("AppVersionCode")) {
                            presetVersionCode = Integer.parseInt(aData[1]);
                        } else if (aData[0].equals("GraphicsPadding")) {
                            MainActivity.preferences.edit().putFloat(PreferenceNames.pGraphicsPadding, Integer.parseInt(aData[1])).commit();
                        } else if (aData[0].equals("ColorizeNonStockIcons")) {
                            MainActivity.preferences.edit().putBoolean(PreferenceNames.pColorizeNonStockIcons, (aData[1].equals("true") ? true : false)).commit();
                        } else if (aData[0].equals("CircleRadius")) {
                            MainActivity.preferences.edit().putInt(PreferenceNames.pCircleRadius, Integer.parseInt(aData[1])).commit();
                        } else if (aData[0].equals("DialogCornersRadius")) {
                            MainActivity.preferences.edit().putInt(PreferenceNames.pRoundedDialogCornersRadius, Integer.parseInt(aData[1])).commit();
                        } else {
                            if (aDataRow.contains("[COLORS]") || aDataRow.contains("_Textcolor") || aDataRow.contains("_Circlecolor") || aDataRow.contains("_Revealcolor") || aDataRow.contains("_Backgroundcolor")) {
                                currentSection = "colors";
                            } else if (aDataRow.contains("[ANIMATIONS]")) {
                                currentSection = "animations";
                            }
                            if (selectedOptions.contains(context.getString(R.string.loadPreset_Colors)) && currentSection.equals("colors")) {
                                if (presetVersionName.equals("1.4.2")) {
                                    if (aData[0].equalsIgnoreCase("RevealBackground")) {
                                        aData[0] = "Reveal_Backgroundcolor";
                                    } else if (aData[0].equalsIgnoreCase("ActionRevealBackground")) {
                                        aData[0] = "ActionReveal_Backgroundcolor";
                                    }
                                }
                                if (presetVersionCode < 34 && aData[0].contains("Background") && !aData[0].contains("Reveal")) {
                                    String[] split = aData[0].split("_");
                                    MainActivity.colorPrefs.edit().putString(split[0] + "_Circlecolor", aData[1]).apply();
                                }
                                if (presetVersionCode < 43 && aData[0].contains("ActionReveal")) {
                                    //String[] split = aData[0].split("=");
                                    oldActionReveal = aData[1];
                                    //publishProgress("importingOldAR");
                                }
                                if (presetVersionCode < 44) {
                                    if (aData[0].equalsIgnoreCase("Reveal_Backgroundcolor")) {
                                        aData[0] = "Dialog_Revealcolor";
                                    }
                                }
                                if (!oldActionReveal.isEmpty() && aData[0].contains("Background") && !aData[0].contains("Reveal")) {
                                    String[] split = aData[0].split("_");
                                    //publishProgress("convertedOldAR");
                                    MainActivity.colorPrefs.edit().putString(split[0] + "_Revealcolor", oldActionReveal).apply();
                                }
                                for (int check = 0; check < neopowermenu.colors.size(); check++) {
                                    for (int x = 2; x <= neopowermenu.colors.get(check).length; x++) {
                                        if (neopowermenu.colors.get(check)[x-1].toString().equals(aData[0]) && aData[0].contains("color")) {
                                            MainActivity.colorPrefs.edit().putString(aData[0], aData[1]).apply();
                                            publishProgress(loadColor[0] + ": " + aData[1]);
                                        }
                                    }
                                }
                            }

                            if (selectedOptions.contains(context.getString(R.string.loadPreset_Animations)) && currentSection.equals("animations")) {
                                if (aData[0].contains("_type") || aData[0].contains("_interpolator") || aData[0].contains("_speed")) {
                                    MainActivity.animationPrefs.edit().putInt(aData[0], Integer.parseInt(aData[1])).apply();
                                    publishProgress(aData[0] + ": " + aData[1]);
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
                    String[][] preset = neopowermenu.lightPresetArray;
                    if (p1[0].equalsIgnoreCase(builtIn[0])) {
                        preset = neopowermenu.lightPresetArray;
                    } else if (p1[0].equalsIgnoreCase(builtIn[1])) {
                        preset = neopowermenu.darkPresetArray;
                    } else if (p1[0].equalsIgnoreCase(builtIn[2])) {
                        preset = neopowermenu.blackPresetArray;
                    }
                    for (int i = 0; i < neopowermenu.colors.size(); i++) {
                        for (int x = 2; x < neopowermenu.colors.get(i).length; x++) {
                            String[] loadColor = neopowermenu.colors.get(i)[x].toString().split("_");
                            if (loadColor.length > 1) {
                                MainActivity.colorPrefs.edit().putString(neopowermenu.colors.get(i)[x].toString(), (i < preset.length ? preset[i][x - 1] : neopowermenu.defaultColors.get(i)[x -1])).apply();
                                publishProgress(loadColor[0] + ": " + (i < preset.length ? preset[i][x - 1] : neopowermenu.defaultColors.get(i)[x -1]));
                            }
                        }
                    }
                }
                return "success";
            } catch (Throwable e) {
                Log.e("NPM", "Failed to load preset: ", e);
                return e.toString();
            }
        }

        @Override
        protected void onProgressUpdate(String[] p1) {

            super.onProgressUpdate(p1);
            if (p1[0].equalsIgnoreCase("importingOldAR")) {
                Toast.makeText(context, "Found old ActionReveal, starting convert process...", Toast.LENGTH_SHORT).show();
            } else if (p1[0].equalsIgnoreCase("convertedOldAR")) {
                Toast.makeText(context, "Converted an old ActionReveal!", Toast.LENGTH_SHORT).show();
            } else {
                PreferencesPresetsFragment.LoadingMsg.setText(context.getString(R.string.presetsManager_Loading) + "\n" + p1[0]);
            }
        }

        @Override
        protected void onPostExecute(String p1) {

            super.onPostExecute(p1);
            if (p1.equalsIgnoreCase("success")) {
                MainActivity.preferences.edit().putString("lastUsedPreset", selectedName).commit();
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

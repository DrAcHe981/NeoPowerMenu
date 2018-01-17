package de.NeonSoft.neopowermenu.Preferences;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.widget.*;

import com.nostra13.universalimageloader.core.listener.*;

import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.*;

import java.io.*;
import java.util.*;

import android.support.v4.app.Fragment;

import de.NeonSoft.neopowermenu.R;

import android.widget.AdapterView.*;

public class PreferencesGraphicsFragment extends Fragment {

    public static Activity mContext;

    public static boolean boolean_UseGraphics = false;
    public static boolean boolean_LoadAppIcons = true;
    public static boolean boolean_ColorizeNonStockIcons = false;
    public static float float_padding = 0;

    LinearLayout LinearLayout_Padding;
    TextView TextView_PaddingValue;
    SeekBar SeekBar_Padding;

    private LinearLayout LinearLayout_Behavior;

    public static GridView GridView_Images;
    static GraphicsAdapter mGraphicsAdapter;

    TextView graphicsDisabled;

    public static int SELECT_PICTURE_RESULT = 1;

    static int selected = -1;

    //String activeGraphics = "internal1";

    ArrayList<GraphicItemHolder> GraphicsList = new ArrayList<>();
    public static Object[][] graphics = {
            {"Progress", "stock", "Progress"}, //0
            {"Shutdown", R.drawable.poweroff1, "Shutdown"}, //1
            {"Reboot", R.drawable.ic_av_loop, "Reboot"}, //2
            {"SoftReboot", R.drawable.ic_image_rotate_left, "SoftReboot"}, //3
            {"Screenshot", R.drawable.ic_device_now_wallpaper, "Screenshot"}, //4
            {"Screenrecord", R.drawable.ic_image_center_focus_weak, "Screenrecord"}, //5
            {"Flashlight", R.drawable.ic_qs_torch_off, "FlashlightOff"}, //6
            {"Flashlight", R.drawable.ic_qs_torch_on, "FlashlightOn"}, //7
            {"ExpandedDesktop", R.drawable.ic_device_developer_mode, "ExpandedDesktop"}, //8
            {"AirplaneMode", R.drawable.ic_device_airplanemode_off, "AirplaneModeOff"}, //9
            {"AirplaneMode", R.drawable.ic_device_airplanemode_on, "AirplaneModeOn"}, //10
            {"RestartUI", R.drawable.ic_alert_error, "RestartUI"}, //11
            {"SoundNormal", R.drawable.ic_av_volume_up, "SoundModeOn"}, //12
            {"SoundSilent", R.drawable.ic_av_volume_off, "SoundModeOff"}, //13
            {"SoundVibrate", R.drawable.ic_notification_vibration, "SoundModeVibrate"}, //14
            {"Recovery", R.drawable.ic_hardware_memory, "Recovery"}, //15
            {"Bootloader", R.drawable.ic_action_settings_backup_restore, "Bootloader"}, //16
            {"SafeMode", R.drawable.ic_notification_sync_problem, "SafeMode"}, //17
            {"KillApp", R.drawable.ic_action_highlight_remove, "KillApp"}, //18
            {"AppShortcut", R.drawable.ic_action_android, "AppShortcut"}, //19
            {"ToggleRotate", R.drawable.ic_device_screen_lock_rotation, "ToggleRotationOff"}, //20
            {"ToggleRotate", R.drawable.ic_device_screen_rotation, "ToggleRotationOn"}, //21
            {"MediaPrevious", R.drawable.ic_action_playback_prev, "MediaPrevious"}, //22
            {"MediaPlayPause", R.drawable.ic_action_playback_play, "MediaPlay"}, //23
            {"MediaPlayPause", R.drawable.ic_action_playback_pause, "MediaPause"}, //24
            {"MediaNext", R.drawable.ic_action_playback_next, "MediaNext"}, //25
            {"ToggleWifi", R.drawable.ic_device_signal_wifi_off, "ToggleWifiOff"}, // 26
            {"ToggleWifi", R.drawable.ic_device_signal_wifi_4_bar, "ToggleWifiOn"}, // 27
            {"ToggleBluetooth", R.drawable.ic_device_bluetooth_disabled, "ToggleBluetoothOff"}, // 28
            {"ToggleBluetooth", R.drawable.ic_device_bluetooth, "ToggleBluetoothOn"}, // 29
            {"ToggleData", R.drawable.ic_device_signal_cellular_off, "ToggleDataOff"}, // 30
            {"ToggleData", R.drawable.ic_device_signal_cellular_4_bar, "ToggleDataOn"}, // 31
            {"RebootFlashMode", R.drawable.ic_image_flash_on, "RebootFlashMode"}, // 32
            {"LockPhone", R.drawable.ic_action_lock_outline, "LockPhone"}, // 33
            {"Shortcut", R.drawable.ic_action_android, "Shortcut"}, // 34
    };

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (!MainActivity.visibleFragment.equalsIgnoreCase("tour")) {
            MainActivity.visibleFragment = "Graphics";
            MainActivity.actionbar.setButton(getString(R.string.PreviewPowerMenu), R.drawable.ic_action_launch, MainActivity.previewOnClickListener);
        }
        MainActivity.actionbar.setTitle(getString(R.string.preferences_Graphics).split("\\|")[0]);
        MainActivity.actionbar.setSubTitle(getString(R.string.preferences_Graphics).split("\\|")[1]);

        mContext = getActivity();

        boolean_UseGraphics = MainActivity.preferences.getBoolean(PreferenceNames.pUseGraphics, false);
        boolean_LoadAppIcons = MainActivity.preferences.getBoolean(PreferenceNames.pLoadAppIcons, true);
        boolean_ColorizeNonStockIcons = MainActivity.preferences.getBoolean(PreferenceNames.pColorizeNonStockIcons, false);
        float_padding = MainActivity.preferences.getFloat(PreferenceNames.pGraphicsPadding, 0);

        View InflatedView = inflater.inflate(R.layout.activity_graphics, container, false);

        LinearLayout_Behavior = (LinearLayout) InflatedView.findViewById(R.id.activitygraphicsLinealLayout_Behavior);
        LinearLayout_Behavior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                dialogFragment.setContext(mContext);
                dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                ArrayList<String> options = new ArrayList<>();
                ArrayList<Boolean> checked = new ArrayList<>();
                options.add(getString(R.string.advancedPrefsTitle_UseGraphics));
                checked.add(boolean_UseGraphics);
                options.add(getString(R.string.advancedPrefs_LoadAppIcons));
                checked.add(boolean_LoadAppIcons);
                options.add(getString(R.string.advancedPrefs_ColorizeNonStockIcons));
                checked.add(boolean_ColorizeNonStockIcons);
                dialogFragment.setList(ListView.CHOICE_MODE_MULTIPLE, options, -1, false);
                dialogFragment.setListChecks(checked);
                View customView = inflater.inflate(R.layout.seekbardialog, null, false);
                final TextView PaddingTitle = (TextView) customView.findViewById(R.id.seekbardialog_Title);
                final TextView PaddingValue = (TextView) customView.findViewById(R.id.seekbardialog_Value);
                final TextView PaddingDesc = (TextView) customView.findViewById(R.id.seekbardialog_Desc);
                final SeekBar PaddingSeekbar = (SeekBar) customView.findViewById(R.id.seekbardialog_Seekbar);
                PaddingTitle.setText(R.string.graphics_PaddingTitle);
                PaddingDesc.setText(R.string.graphics_PaddingDesc);
                PaddingSeekbar.setMax(20);
                PaddingSeekbar.setProgress((int) helper.convertPixelsToDp(float_padding, mContext));
                PaddingValue.setText((int) helper.convertPixelsToDp(float_padding, mContext) + "dp");
                PaddingSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        float_padding = helper.convertDpToPixel(i, mContext);
                        PaddingValue.setText((int) helper.convertPixelsToDp(float_padding, mContext) + "dp");
                        //MainActivity.preferences.edit().putFloat(PreferenceNames.pGraphicsPadding, float_padding).commit();
                        mGraphicsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
                dialogFragment.setCustomView(customView);
                dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {
                    @Override
                    public void onListItemClick(int position, String text) {

                    }

                    @Override
                    public void onNegativeClick() {
                        float_padding = MainActivity.preferences.getFloat(PreferenceNames.pGraphicsPadding, 0);
                        mGraphicsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNeutralClick() {

                    }

                    @Override
                    public void onPositiveClick(Bundle resultBundle) {
                        String result = resultBundle.getString(slideDownDialogFragment.RESULT_LIST, "");
                        String splitResult[] = result.split(",");
                        boolean_UseGraphics = false;
                        boolean_LoadAppIcons = false;
                        boolean_ColorizeNonStockIcons = false;
                        for (String p : splitResult) {
                            if (Integer.parseInt(p) == 0) {
                                boolean_UseGraphics = true;
                            } else if (Integer.parseInt(p) == 1) {
                                boolean_LoadAppIcons = true;
                            } else if (Integer.parseInt(p) == 2) {
                                boolean_ColorizeNonStockIcons = true;
                            }
                        }
                        if (!boolean_UseGraphics && graphicsDisabled.getVisibility() == View.GONE) {
                            graphicsDisabled.setVisibility(View.VISIBLE);
                            graphicsDisabled.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
                        } else if (boolean_UseGraphics && graphicsDisabled.getVisibility() == View.VISIBLE) {
                            graphicsDisabled.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                            graphicsDisabled.setVisibility(View.GONE);
                        }
                        float_padding = helper.convertDpToPixel(PaddingSeekbar.getProgress(), mContext);
                        MainActivity.preferences.edit()
                                .putFloat(PreferenceNames.pGraphicsPadding, float_padding)
                                .putBoolean(PreferenceNames.pUseGraphics, boolean_UseGraphics)
                                .putBoolean(PreferenceNames.pLoadAppIcons, boolean_LoadAppIcons)
                                .putBoolean(PreferenceNames.pColorizeNonStockIcons, boolean_ColorizeNonStockIcons).commit();
                        mGraphicsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onTouchOutside() {

                    }
                });
                dialogFragment.setNegativeButton(getString(R.string.Dialog_Buttons).split("\\|")[4]);
                dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[7]);
                dialogFragment.showDialog(R.id.dialog_container);
            }
        });

        GridView_Images = (GridView) InflatedView.findViewById(R.id.activitygraphicsGridView1);
        GridView_Images.setNumColumns(getResources().getInteger(R.integer.ImageList_Columns));

        GraphicItemHolder backgroundGraphic = new GraphicItemHolder();
        backgroundGraphic.setName(getString(R.string.powerMenuMain_MenuBackground));
        backgroundGraphic.setFile(mContext.getFilesDir().getPath() + "/images/" + "xposed_dialog_background.png");
        backgroundGraphic.setFileName("xposed_dialog_background");
        GraphicsList.add(backgroundGraphic);

        for (int i = 0; i < graphics.length; i++) {
            GraphicItemHolder graphic = new GraphicItemHolder();
            graphic.setName(graphics[i][0].toString());
            if (graphics[i][0].toString().equalsIgnoreCase("Progress")) {
                if (MainActivity.preferences.getString("ProgressDrawable", "stock").equalsIgnoreCase("pb/dr")) {
                    graphic.setRessource(R.drawable.progress_pitchblack_darkred_cm13);
                } else if (MainActivity.preferences.getString("ProgressDrawable", "stock").equalsIgnoreCase("WeaReOne")) {
                    graphic.setRessource(R.drawable.progress_weareone);
                } else {
                    graphic.setFile("stock");
                }
            }
            File checkFile = new File(mContext.getFilesDir().getPath() + "/images/" + graphics[i][2] + ".png");
            if (checkFile.exists()) {
                graphic.setFile(mContext.getFilesDir().getPath() + "/images/" + graphics[i][2] + ".png");
            } else if (!checkFile.exists() && !graphics[i][0].toString().equalsIgnoreCase("Progress")) {
                graphic.setRessource((int) graphics[i][1]);
            }
            graphic.setFileName(graphics[i][2].toString());
            GraphicsList.add(graphic);
        }

        ArrayList<String> MultiPage = new ArrayList<String>();
        for (int i = 0; i < MainActivity.orderPrefs.getAll().keySet().size(); i++) {
            if (MainActivity.orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", -1) != -1) {
                if (MainActivity.orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", visibilityOrder_ListAdapter.TYPE_NORMAL) == visibilityOrder_ListAdapter.TYPE_NORMAL) {
                    if (MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null").contains(".") || !MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_shortcutUri", "").isEmpty()) {
                        GraphicItemHolder graphic = new GraphicItemHolder();
                        graphic.setName(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null").split("/")[0]);
                        if (new File(mContext.getFilesDir().getPath() + "/images/" + MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null").split("/")[0] + ".png").exists()) {
                            graphic.setFile(mContext.getFilesDir().getPath() + "/images/" + MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null").split("/")[0] + ".png");
                        } else if (new File(mContext.getFilesDir().getPath() + "/app_picker/" + MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null") + ".png").exists()) {
                            graphic.setFile(mContext.getFilesDir().getPath() + "/app_picker/" + MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null") + ".png");
                        } else {
                            graphic.setRessource(R.drawable.ic_action_android);
                        }
                        graphic.setFileName(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null").split("/")[0]);
                        GraphicsList.add(graphic);
                    }
                } else if (MainActivity.orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", visibilityOrder_ListAdapter.TYPE_NORMAL) == visibilityOrder_ListAdapter.TYPE_MULTI) {
                    int x = 1;
                    do {
                        GraphicItemHolder graphic = new GraphicItemHolder();
                        graphic.setName(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item" + x + "_title", "null").split("/")[0]);
                        if (new File(mContext.getFilesDir().getPath() + "/images/" + MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item" + x + "_title", "null").split("/")[0] + ".png").exists()) {
                            graphic.setFile(mContext.getFilesDir().getPath() + "/images/" + MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item" + x + "_title", "null").split("/")[0] + ".png");
                        } else if (new File(mContext.getFilesDir().getPath() + "/app_picker/" + MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item" + x + "_title", "null") + ".png").exists()) {
                            graphic.setFile(mContext.getFilesDir().getPath() + "/app_picker/" + MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item" + x + "_title", "null") + ".png");
                        } else {
                            graphic.setRessource(R.drawable.ic_action_android);
                        }
                        graphic.setFileName(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item" + x + "_title", "null").split("/")[0]);
                        GraphicsList.add(graphic);
                        x++;
                    } while (!MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "][EMPTY][").equals("][EMPTY]["));
                } else if (MainActivity.orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", -1) == visibilityOrder_ListAdapter.TYPE_MULTIPAGE_START) {
                    MultiPage.add(MainActivity.orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null"));
                } else if (MultiPage.size() > 0 && MainActivity.orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", -1) == visibilityOrder_ListAdapter.TYPE_MULTIPAGE_END) {
                    MultiPage.remove(MultiPage.size() - 1);
                }
            }
        }

        mGraphicsAdapter = new GraphicsAdapter(getActivity(), MainActivity.imageLoader, GraphicsList);
        GridView_Images.setFastScrollEnabled(true);
        PauseOnScrollListener listener = new PauseOnScrollListener(MainActivity.imageLoader, true, true);
        GridView_Images.setOnScrollListener(listener);
        GridView_Images.setAdapter(mGraphicsAdapter);
        //mGraphicsAdapter.addFallbackGraphics(defaultGraphics);
        //mGraphicsAdapter.addAll(GraphicsList);

        GridView_Images.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                selected = p3;
                slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                dialogFragment.setContext(mContext);
                dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

                    @Override
                    public void onListItemClick(int position, String text) {
                        if (position == 0) {
                            if (GraphicsList.get(selected).getName().equalsIgnoreCase("Progress")) {
                                MainActivity.preferences.edit().putString("ProgressDrawable", "Stock").commit();
                            }
                            if (new File(GraphicsList.get(selected).getFile()).exists()) {
                                mGraphicsAdapter.removeFromCache(GraphicsList.get(selected).getFile());
                                new File(GraphicsList.get(selected).getFile()).delete();
                            }
                            GraphicItemHolder item = new GraphicItemHolder();
                            item.setName(GraphicsList.get(selected).getName());
                            Log.d("NPM", "Graphic file: " + GraphicsList.get(selected).getFile());
                            if (selected >= graphics.length) {
                                item.setRessource(R.drawable.ic_action_android);
                                if (new File(mContext.getFilesDir().getPath() + "/app_picker/" + GraphicsList.get(selected).getName() + ".png").exists()) {
                                    item.setFile(mContext.getFilesDir().getPath() + "/app_picker/" + GraphicsList.get(selected).getName() + ".png");
                                }
                            } else {
                                if (GraphicsList.get(selected).getName().equalsIgnoreCase("Progress")) {
                                    item.setFile("stock");
                                } else if (GraphicsList.get(selected).getFileName().equalsIgnoreCase("xposed_dialog_background")) {
                                    item.setFile(mContext.getFilesDir().getPath() + "/images/" + "xposed_dialog_background.png");
                                } else {
                                    item.setRessource((int) graphics[selected][1]);
                                }
                            }
                            item.setFileName(GraphicsList.get(selected).getFileName());
                            mGraphicsAdapter.set(selected, item);
                            GraphicsList.set(selected, item);
                        } else if (position == 1) {
                            helper.startAsyncTask(new ScanPresetsForGraphic(), selected);
                        } else if (position == 2) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE_RESULT);
                        } else if (position == 3) {
                            MainActivity.preferences.edit().putString("ProgressDrawable", "pb/dr").commit();
                            GraphicItemHolder item = new GraphicItemHolder();
                            item.setName(GraphicsList.get(selected).getName());
                            item.setRessource(R.drawable.progress_pitchblack_darkred_cm13);
                            item.setFileName(GraphicsList.get(selected).getFileName());
                            mGraphicsAdapter.set(selected, item);
                            GraphicsList.set(selected, item);
                        } else if (position == 4) {
                            MainActivity.preferences.edit().putString("ProgressDrawable", "WeaReOne").commit();
                            GraphicItemHolder item = new GraphicItemHolder();
                            item.setName(GraphicsList.get(selected).getName());
                            item.setRessource(R.drawable.progress_weareone);
                            item.setFileName(GraphicsList.get(selected).getFileName());
                            mGraphicsAdapter.set(selected, item);
                            GraphicsList.set(selected, item);
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
                String[] choose = (getString(R.string.graphics_Choose) + (GraphicsList.get(selected).getName().equalsIgnoreCase("Progress") ? "|PitchBlack / DarkRed CM13|We aRe One" : "")).split("\\|");
                dialogFragment.setList(ListView.CHOICE_MODE_NONE, choose, -1, true);
                dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[4]);
                dialogFragment.showDialog(R.id.dialog_container);
            }
        });


        graphicsDisabled = (TextView) InflatedView.findViewById(R.id.activitygraphicsTextView_Disabled);
        graphicsDisabled.setVisibility(boolean_UseGraphics ? View.GONE : View.VISIBLE);
        graphicsDisabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean_UseGraphics = true;
                MainActivity.preferences.edit().putBoolean(PreferenceNames.pUseGraphics, true).commit();
                graphicsDisabled.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                graphicsDisabled.setVisibility(View.GONE);
            }
        });

        return InflatedView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE_RESULT) {
            if (resultCode == mContext.RESULT_OK) {
                startCrop(data.getData());
            }
        }
    }

    public void startCrop(Uri selectedImage) {
        Cropper cropper = new Cropper();
        Bundle cropperArgs = new Bundle();
        cropperArgs.putString("mItem", GraphicsList.get(selected).getName());
        cropperArgs.putParcelable("mUri", selectedImage);
        cropperArgs.putString("mSaveAs", GraphicsList.get(selected).getFileName() + ".png");
        cropper.setArguments(cropperArgs);

        MainActivity.changePrefPage(cropper, false);

    }

    class ScanPresetsForGraphic extends AsyncTask<Integer, Integer, String> {

        slideDownDialogFragment loadDialog;
        File presetsFolder;
        File[] presetsFiles;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                    cancel(true);
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
            helper.zipLogging(false);
            presetsFolder = new File(getActivity().getFilesDir().getPath() + "/presets/");
        }

        @Override
        protected String doInBackground(final Integer... selected) {
            final int[] i = {0};
            presetsFiles = presetsFolder.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    boolean supported = helper.isValidZip(dir + "/" + name, null) && helper.unzipFile(dir + "/" + name, mContext.getFilesDir().getAbsolutePath() + "/temp/", GraphicsList.get(selected[0]).getName() + ".png", null) == null;
                    onProgressUpdate((i[0] * 100) / presetsFolder.listFiles().length);
                    i[0]++;
                    return (supported && name.toLowerCase().endsWith(".nps"));
                }
            });
            if (isCancelled()) {
                return "canceled";
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            loadDialog.setProgressBar(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null) {
                helper.zipLogging(true);
                loadDialog.cancelDialog();
                slideDownDialogFragment presetsListDialog = new slideDownDialogFragment();
                presetsListDialog.setContext(mContext);
                presetsListDialog.setFragmentManager(MainActivity.fragmentManager);
                presetsListDialog.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

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
                        if (presetsFiles.length > 0) {
                            if (GraphicsList.get(selected).getFile() != null) {
                                mGraphicsAdapter.removeFromCache(GraphicsList.get(selected).getFile());
                                new File(GraphicsList.get(selected).getFile()).delete();
                            }
                            if (GraphicsList.get(selected).getName().equalsIgnoreCase("Progress")) {
                                MainActivity.preferences.edit().putString("ProgressDrawable", "file").commit();
                            }
                            Log.d("NPM", "Extracting from " + presetsFiles[resultBundle.getInt(slideDownDialogFragment.RESULT_LIST)]);
                            if (helper.unzipFile(presetsFiles[resultBundle.getInt(slideDownDialogFragment.RESULT_LIST)].toString(), mContext.getFilesDir().getPath() + "/images/", GraphicsList.get(selected).getFileName() + ".png", null) == null) {
                                GraphicItemHolder item = new GraphicItemHolder();
                                item.setName(GraphicsList.get(selected).getName());
                                item.setFile(mContext.getFilesDir().getPath() + "/images/" + GraphicsList.get(selected).getFileName() + ".png");
                                item.setFileName(GraphicsList.get(selected).getFileName());
                                mGraphicsAdapter.set(selected, item);
                                GraphicsList.set(selected, item);
                            }
                        }
                    }

                    @Override
                    public void onTouchOutside() {

                    }
                });
                if (presetsFiles.length > 0) {
                    String[] presetsListTitles = new String[presetsFiles.length];
                    for (int i = 0; i < presetsFiles.length; i++) {
                        presetsListTitles[i] = presetsFiles[i].getName().split(".nps")[0];
                    }
                    presetsListDialog.setList(ListView.CHOICE_MODE_SINGLE, presetsListTitles, -1, false);
                    presetsListDialog.setNegativeButton(getString(R.string.Dialog_Buttons).split("\\|")[4]);
                    presetsListDialog.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[6]);
                } else {
                    presetsListDialog.setText(getString(R.string.graphics_NoPresetsFound));
                    presetsListDialog.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[0]);
                }
                presetsListDialog.showDialog(R.id.dialog_container);
            }
        }
    }

}

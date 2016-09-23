package de.NeonSoft.neopowermenu.Preferences;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.nostra13.universalimageloader.core.listener.*;
import com.theartofdev.edmodo.cropper.*;

import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.*;

import java.io.*;
import java.util.*;

import android.support.v4.app.Fragment;

import de.NeonSoft.neopowermenu.R;

public class PreferencesGraphicsFragment extends Fragment {

    public static Activity mContext;

    public static float float_padding = 0;
    LinearLayout LinearLayout_Padding;
    TextView TextView_PaddingValue;
    SeekBar SeekBar_Padding;
    
    public static GridView GridView_Images;
    static graphicsAdapter graphicsAdapter;

    public static int SELECT_PICTURE_RESULT = 1;

    static int selected = -1;

    //String activeGraphics = "internal1";

    public static Object[][] defaultGraphics = {
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
            {"MediaNext", R.drawable.ic_action_playback_next, "MediaNext"} //25
    };

    Object[][] loadGraphics = {
            {"Progress", "stock", "Progress"},
            {"Shutdown", R.drawable.poweroff1, "Shutdown"},
            {"Reboot", R.drawable.ic_av_loop, "Reboot"},
            {"SoftReboot", R.drawable.ic_image_rotate_left, "SoftReboot"},
            {"Screenshot", R.drawable.ic_device_now_wallpaper, "Screenshot"},
            {"Screenrecord", R.drawable.ic_image_center_focus_weak, "Screenrecord"},
            {"Flashlight", R.drawable.ic_qs_torch_off, "FlashlightOff"},
            {"Flashlight", R.drawable.ic_qs_torch_on, "FlashlightOn"},
            {"ExpandedDesktop", R.drawable.ic_device_developer_mode, "ExpandedDesktop"},
            {"AirplaneMode", R.drawable.ic_device_airplanemode_off, "AirplaneModeOff"},
            {"AirplaneMode", R.drawable.ic_device_airplanemode_on, "AirplaneModeOn"},
            {"RestartUI", R.drawable.ic_alert_error, "RestartUI"},
            {"SoundNormal", R.drawable.ic_av_volume_up, "SoundModeOn"},
            {"SoundSilent", R.drawable.ic_av_volume_off, "SoundModeOff"},
            {"SoundVibrate", R.drawable.ic_notification_vibration, "SoundModeVibrate"},
            {"Recovery", R.drawable.ic_hardware_memory, "Recovery"},
            {"Bootloader", R.drawable.ic_action_settings_backup_restore, "Bootloader"},
            {"SafeMode", R.drawable.ic_notification_sync_problem, "SafeMode"},
            {"KillApp", R.drawable.ic_action_highlight_remove, "KillApp"},
            {"AppShortcut", R.drawable.ic_action_android, "AppShortcut"},
            {"ToggleRotate", R.drawable.ic_device_screen_lock_rotation, "ToggleRotationOff"},
            {"ToggleRotate", R.drawable.ic_device_screen_rotation, "ToggleRotationOn"},
            {"MediaPrevious", R.drawable.ic_action_playback_prev, "MediaPrevious"},
            {"MediaPlayPause", R.drawable.ic_action_playback_play, "MediaPlay"},
            {"MediaPlayPause", R.drawable.ic_action_playback_pause, "MediaPause"},
            {"MediaNext", R.drawable.ic_action_playback_next, "MediaNext"}};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        if(!MainActivity.visibleFragment.equalsIgnoreCase("tour")) {
            MainActivity.visibleFragment = "Graphics";
            MainActivity.actionbar.setButton(getString(R.string.PreviewPowerMenu), R.drawable.ic_action_launch, MainActivity.previewOnClickListener);
        }
        MainActivity.actionbar.setTitle(getString(R.string.preferences_Graphics).split("\\|")[0]);
        MainActivity.actionbar.setSubTitle(getString(R.string.preferences_Graphics).split("\\|")[1]);

        mContext = getActivity();

        float_padding = MainActivity.preferences.getFloat("GraphicsPadding",0);

        View InflatedView = inflater.inflate(R.layout.activity_graphics, container, false);

        LinearLayout_Padding = (LinearLayout) InflatedView.findViewById(R.id.activitygraphicsLinealLayout_Padding);
        TextView_PaddingValue = (TextView) InflatedView.findViewById(R.id.activitygraphicsTextView_PaddingValue);
        SeekBar_Padding = (SeekBar) InflatedView.findViewById(R.id.activitygraphicsSeekBar_Padding);
        SeekBar_Padding.setMax(20);
        SeekBar_Padding.setProgress((int) helper.convertPixelsToDp(float_padding,mContext));
        TextView_PaddingValue.setText((int) helper.convertPixelsToDp(float_padding,mContext)+"dp");

        SeekBar_Padding.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float_padding = helper.convertDpToPixel(i, mContext);
                TextView_PaddingValue.setText((int) helper.convertPixelsToDp(float_padding,mContext)+"dp");
                MainActivity.preferences.edit().putFloat("GraphicsPadding",float_padding).apply();
                graphicsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        GridView_Images = (GridView) InflatedView.findViewById(R.id.activitygraphicsGridView1);

        graphicsAdapter = new graphicsAdapter(getActivity(), MainActivity.imageLoader);
        GridView_Images.setFastScrollEnabled(true);
        PauseOnScrollListener listener = new PauseOnScrollListener(MainActivity.imageLoader, true, true);
        GridView_Images.setOnScrollListener(listener);
        GridView_Images.setAdapter(graphicsAdapter);

        for (int i = 0; i < defaultGraphics.length; i++) {
            File checkFile = new File(mContext.getFilesDir().getPath() + "/images/" + defaultGraphics[i][2] + ".png");
            if (checkFile.exists()) {
                loadGraphics[i][1] = mContext.getFilesDir().getPath() + "/images/" + defaultGraphics[i][2] + ".png";
            } else if (defaultGraphics[i][0].toString().equalsIgnoreCase("Progress")) {
                loadGraphics[i][1] = MainActivity.preferences.getString("ProgressDrawable", "stock");
            }
        }

        if (loadGraphics[0][1].toString().equalsIgnoreCase("pb/dr")) {
            loadGraphics[0][1] = R.drawable.progress_pitchblack_darkred_cm13;
        } else if (loadGraphics[0][1].toString().equalsIgnoreCase("WeaReOne")) {
            loadGraphics[0][1] = R.drawable.progress_weareone;
        }

        graphicsAdapter.addFallbackGraphics(defaultGraphics);
        graphicsAdapter.addAll(loadGraphics);

        GridView_Images.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4) {
                
                selected = p3;
                slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                dialogFragment.setContext(mContext);
                dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

                    @Override
                    public void onListItemClick(int position, String text) {
                        
                        graphicsAdapter.removeFromCache(defaultGraphics[selected][2].toString());
                        if (position == 0) {
                            if (defaultGraphics[selected][0].toString().equalsIgnoreCase("Progress")) {
                                MainActivity.preferences.edit().putString("ProgressDrawable", "Stock").apply();
                            }
                            new File(loadGraphics[selected][1].toString()).delete();
                            loadGraphics[selected][1] = defaultGraphics[selected][1];
                            graphicsAdapter.remove(selected);
                            Object[] item = {defaultGraphics[selected][0], defaultGraphics[selected][1], defaultGraphics[selected][2]};
                            graphicsAdapter.addAt(selected, item);
                        } else if (position == 1) {
                            helper.zipLogging(false);
                            //Toast.makeText(getActivity(),getString(R.string.presetsManager_NJI),Toast.LENGTH_SHORT).show();
                            File presetsFolder = new File(getActivity().getFilesDir().getPath() + "/presets/");
                            final File[] presetsFiles = presetsFolder.listFiles(new FilenameFilter() {
                                public boolean accept(File dir, String name) {
                                    boolean supported = helper.isValidZip(dir + "/" + name, null) && helper.unzipFile(dir + "/" + name, mContext.getFilesDir().getAbsolutePath() + "/temp/", defaultGraphics[selected][2] + ".png", null) == null;
                                    return (supported && name.toLowerCase().endsWith(".nps"));
                                }
                            });
                            helper.zipLogging(true);
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
                                        if (defaultGraphics[selected][0].toString().equalsIgnoreCase("Progress")) {
                                            MainActivity.preferences.edit().putString("ProgressDrawable", "file").apply();
                                        }
                                        new File(loadGraphics[selected][1].toString()).delete();
                                        Log.d("NPM", "Extracting from " + presetsFiles[resultBundle.getInt(slideDownDialogFragment.RESULT_LIST)]);
                                        if (helper.unzipFile(presetsFiles[resultBundle.getInt(slideDownDialogFragment.RESULT_LIST)].toString(), mContext.getFilesDir().getPath() + "/images/", defaultGraphics[selected][2] + ".png", null) == null) {
                                            loadGraphics[selected][1] = mContext.getFilesDir().getPath() + "/images/" + defaultGraphics[selected][2] + ".png";
                                            graphicsAdapter.remove(selected);
                                            Object[] item = {defaultGraphics[selected][0], loadGraphics[selected][1], defaultGraphics[selected][2]};
                                            graphicsAdapter.addAt(selected, item);
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
                        } else if (position == 2) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE_RESULT);
                        } else if (position == 3) {
                            MainActivity.preferences.edit().putString("ProgressDrawable", "pb/dr").apply();
                            new File(loadGraphics[selected][1].toString()).delete();
                            loadGraphics[selected][1] = R.drawable.progress_pitchblack_darkred_cm13;
                            graphicsAdapter.remove(selected);
                            Object[] item = {defaultGraphics[selected][0], R.drawable.progress_pitchblack_darkred_cm13, defaultGraphics[selected][2]};
                            graphicsAdapter.addAt(selected, item);
                        } else if (position == 4) {
                            MainActivity.preferences.edit().putString("ProgressDrawable", "WeaReOne").apply();
                            new File(loadGraphics[selected][1].toString()).delete();
                            loadGraphics[selected][1] = R.drawable.progress_weareone;
                            graphicsAdapter.remove(selected);
                            Object[] item = {defaultGraphics[selected][0], R.drawable.progress_weareone, defaultGraphics[selected][2]};
                            graphicsAdapter.addAt(selected, item);
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
                String[] choose = (getString(R.string.graphics_Choose) + (defaultGraphics[selected][0].toString().equalsIgnoreCase("Progress") ? "|PitchBlack / DarkRed CM13|We aRe One" : "")).split("\\|");
                dialogFragment.setList(ListView.CHOICE_MODE_NONE, choose, -1, true);
                dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[4]);
                dialogFragment.showDialog(R.id.dialog_container);
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
        cropperArgs.putString("mItem", defaultGraphics[selected][0].toString());
        cropperArgs.putParcelable("mUri", selectedImage);
        cropperArgs.putString("mSaveAs", defaultGraphics[selected][2].toString() + ".png");
        cropper.setArguments(cropperArgs);

        MainActivity.changePrefPage(cropper, false);

    }

}

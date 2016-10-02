package de.NeonSoft.neopowermenu.Preferences;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.os.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import com.larswerkman.holocolorpicker.*;

import de.NeonSoft.neopowermenu.*;

import java.io.*;

import android.support.v4.app.Fragment;

import de.NeonSoft.neopowermenu.R;
import de.NeonSoft.neopowermenu.helpers.*;

import java.util.*;

public class PreferencesColorFragment extends Fragment {

    Context mContext;

    ListView ListView_ColorsList;
    ColorsListAdapter adapter;

    public static String[] lightPreset = {
            "Presets", "Load", "Save",
            "Reveal", "#8800bcd4", "#ff0097a7",
            "Main", "#fff5f5f5", "#000000",
            "Shutdown", "#ffd32f2f", "#ffd32f2f", "#ffffff",
            "Reboot", "#ff3f51b5", "#ff3f51b5", "#ffffff",
            "SoftReboot", "#ffe91e63", "#ffe91e63", "#ffffff",
            "Screenshot", "#ff3f51b5", "#ffffff",
            "Screenrecord", "#ff3f51b5", "#ffffff",
            "Flashlight", "#ff3f51b5", "#ffffff",
            "ExpandedDesktop", "#ff3f51b5", "#ffffff",
            "AirplaneMode", "#ff3f51b5", "#ffffff",
            "RestartUI", "#ff3f51b5", "#ffffff",
            "SoundMode", "#ff3f51b5", "#ffffff",
            "Recovery", "#ff8bc34a", "#ff8bc34a", "#ffffff",
            "Bootloader", "#ff277b71", "#ff277b71", "#ffffff",
            "SafeMode", "#ff009698", "#ff009698", "#ffffff",
            "SoundVibrate", "#ff3f51b5", "#ffffff",
            "SoundNormal", "#ff3f51b5", "#ffffff",
            "SoundSilent", "#ff3f51b5", "#ffffff",
            "KillApp", "#ff3f51b5", "#ffffff",
            "AppShortcut", "#ff3f51b5", "#ffffff",
            "ActivityShortcut", "#ff3f51b5", "#ffffff",
            "ToggleRotate", "#ff3f51b5", "#ffffff",
            "MediaPrevious", "#ff3f51b5", "#ffffff",
            "MediaPlayPause", "#ff3f51b5", "#ffffff",
            "MediaNext", "#ff3f51b5", "#ffffff"};

    public static String[] darkPreset = {
            "Presets", "Load", "Save",
            "Reveal", "#88121212", "#ff21272b",
            "Main", "#ff212121", "#ffffff",
            "Shutdown", "#ffd32f2f", "#ffd32f2f", "#ffffff",
            "Reboot", "#ff3f51b5", "#ff3f51b5", "#ffffff",
            "SoftReboot", "#ffe91e63", "#ffe91e63", "#ffffff",
            "Screenshot", "#ff3f51b5", "#ffffff",
            "Screenrecord", "#ff3f51b5", "#ffffff",
            "Flashlight", "#ff3f51b5", "#ffffff",
            "ExpandedDesktop", "#ff3f51b5", "#ffffff",
            "AirplaneMode", "#ff3f51b5", "#ffffff",
            "RestartUI", "#ff3f51b5", "#ffffff",
            "SoundMode", "#ff3f51b5", "#ffffff",
            "Recovery", "#ff8bc34a", "#ff8bc34a", "#ffffff",
            "Bootloader", "#ff277b71", "#ff277b71", "#ffffff",
            "SafeMode", "#ff009698", "#ff009698", "#ffffff",
            "SoundVibrate", "#ff3f51b5", "#ffffff",
            "SoundNormal", "#ff3f51b5", "#ffffff",
            "SoundSilent", "#ff3f51b5", "#ffffff",
            "KillApp", "#ff3f51b5", "#ffffff",
            "AppShortcut", "#ff3f51b5", "#ffffff",
            "ToggleRotate", "#ff3f51b5", "#ffffff",
            "MediaPrevious", "#ff3f51b5", "#ffffff",
            "MediaPlayPause", "#ff3f51b5", "#ffffff",
            "MediaNext", "#ff3f51b5", "#ffffff"};

    public static String[] blackPreset = {
            "Presets", "Load", "Save",
            "Reveal", "#88000000", "#ff000000",
            "Main", "#ff000000", "#ffffff",
            "Shutdown", "#ff000000", "#ff000000", "#ffffff",
            "Reboot", "#ff000000", "#ff000000", "#ffffff",
            "SoftReboot", "#ff000000", "#ff000000", "#ffffff",
            "Screenshot", "#ff000000", "#ffffff",
            "Screenrecord", "#ff000000", "#ffffff",
            "Flashlight", "#ff000000", "#ffffff",
            "ExpandedDesktop", "#ff000000", "#ffffff",
            "AirplaneMode", "#ff000000", "#ffffff",
            "RestartUI", "#ff000000", "#ffffff",
            "SoundMode", "#ff000000", "#ffffff",
            "Recovery", "#ff000000", "#ff000000", "#ffffff",
            "Bootloader", "#ff000000", "#ff000000", "#ffffff",
            "SafeMode", "#ff000000", "#ff000000", "#ffffff",
            "SoundVibrate", "#ff000000", "#ffffff",
            "SoundNormal", "#ff000000", "#ffffff",
            "SoundSilent", "#ff000000", "#ffffff",
            "KillApp", "#ff000000", "#ffffff",
            "AppShortcut", "#ff000000", "#ffffff",
            "ToggleRotate", "#ff000000", "#ffffff",
            "MediaPrevious", "#ff000000", "#ffffff",
            "MediaPlayPause", "#ff000000", "#ffffff",
            "MediaNext", "#ff000000", "#ffffff"};

    public static Object[][] ColorNames = {
            {ColorsListAdapter.TYPE_HEADER, "Presets"}, {ColorsListAdapter.TYPE_LOAD, "Load"}, {ColorsListAdapter.TYPE_SAVE, "Save"},
            {ColorsListAdapter.TYPE_HEADER, "Reveal"}, {ColorsListAdapter.TYPE_ITEM, "Reveal_Backgroundcolor"}, {ColorsListAdapter.TYPE_ITEM, "ActionReveal_Backgroundcolor"},
            {ColorsListAdapter.TYPE_HEADER, "Main"}, {ColorsListAdapter.TYPE_ITEM, "Dialog_Backgroundcolor"}, {ColorsListAdapter.TYPE_ITEM, "Dialog_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "Shutdown"}, {ColorsListAdapter.TYPE_ITEM, "DialogShutdown_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogShutdown_Backgroundcolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogShutdown_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "Reboot"}, {ColorsListAdapter.TYPE_ITEM, "DialogReboot_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogReboot_Backgroundcolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogReboot_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "SoftReboot"}, {ColorsListAdapter.TYPE_ITEM, "DialogSoftReboot_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogSoftReboot_Backgroundcolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogSoftReboot_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "Screenshot"}, {ColorsListAdapter.TYPE_ITEM, "DialogScreenshot_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogScreenshot_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "Screenrecord"}, {ColorsListAdapter.TYPE_ITEM, "DialogScreenrecord_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogScreenrecord_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "Flashlight"}, {ColorsListAdapter.TYPE_ITEM, "DialogFlashlight_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogFlashlight_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "ExpandedDesktop"}, {ColorsListAdapter.TYPE_ITEM, "DialogExpandedDesktop_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogExpandedDesktop_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "AirplaneMode"}, {ColorsListAdapter.TYPE_ITEM, "DialogAirplaneMode_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogAirplaneMode_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "RestartUI"}, {ColorsListAdapter.TYPE_ITEM, "DialogRestartUI_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogRestartUI_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "SoundMode"}, {ColorsListAdapter.TYPE_ITEM, "DialogSoundMode_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogSoundMode_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "Recovery"}, {ColorsListAdapter.TYPE_ITEM, "DialogRecovery_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogRecovery_Backgroundcolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogRecovery_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "Bootloader"}, {ColorsListAdapter.TYPE_ITEM, "DialogBootloader_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogBootloader_Backgroundcolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogBootloader_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "SafeMode"}, {ColorsListAdapter.TYPE_ITEM, "DialogSafeMode_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogSafeMode_Backgroundcolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogSafeMode_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "SoundVibrate"}, {ColorsListAdapter.TYPE_ITEM, "DialogSoundVibrate_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogSoundVibrate_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "SoundNormal"}, {ColorsListAdapter.TYPE_ITEM, "DialogSoundNormal_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogSoundNormal_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "SoundSilent"}, {ColorsListAdapter.TYPE_ITEM, "DialogSoundSilent_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogSoundSilent_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "KillApp"}, {ColorsListAdapter.TYPE_ITEM, "DialogKillApp_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogKillApp_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "AppShortcut"}, {ColorsListAdapter.TYPE_ITEM, "DialogAppShortcut_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogAppShortcut_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "ToggleRotate"}, {ColorsListAdapter.TYPE_ITEM, "DialogToggleRotate_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogToggleRotate_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "MediaPrevious"}, {ColorsListAdapter.TYPE_ITEM, "DialogMediaPrevious_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogMediaPrevious_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "MediaPlayPause"}, {ColorsListAdapter.TYPE_ITEM, "DialogMediaPlayPause_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogMediaPlayPause_Textcolor"},
            {ColorsListAdapter.TYPE_HEADER, "MediaNext"}, {ColorsListAdapter.TYPE_ITEM, "DialogMediaNext_Circlecolor"}, {ColorsListAdapter.TYPE_ITEM, "DialogMediaNext_Textcolor"}};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!MainActivity.visibleFragment.equalsIgnoreCase("tour")) {
            MainActivity.visibleFragment = "CustomColors";
        }
        View InflatedView = inflater.inflate(R.layout.activity_colorpreferences, container, false);

        mContext = getActivity();

        MainActivity.actionbar.setTitle(getString(R.string.preferences_Theme).split("\\|")[0]);
        MainActivity.actionbar.setSubTitle(getString(R.string.preferences_Theme).split("\\|")[1]);

        ListView_ColorsList = (ListView) InflatedView.findViewById(R.id.activitycolorpreferencesListView_Colors);

        adapter = new ColorsListAdapter(getActivity(), ColorNames, lightPreset);
        ListView_ColorsList.setFastScrollEnabled(true);
        ListView_ColorsList.setAdapter(adapter);


        return InflatedView;
    }

}

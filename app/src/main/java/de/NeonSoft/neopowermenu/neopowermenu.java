package de.NeonSoft.neopowermenu;
import android.app.*;
import android.content.SharedPreferences;

import org.acra.*;
import org.acra.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;

import de.NeonSoft.neopowermenu.helpers.ColorsListAdapter;
import de.NeonSoft.neopowermenu.helpers.visibilityOrder_ListAdapter;


@ReportsCrashes(
		formUri = "https://www.neon-soft.de/inc/acra/acra.php",
		formUriBasicAuthLogin = "acra",
		formUriBasicAuthPassword = "acraerrormailer",
		reportType = org.acra.sender.HttpSender.Type.JSON,
		mode = ReportingInteractionMode.SILENT,
		sendReportsAtShutdown = true,
		socketTimeout = 1000 * 20
)

public class neopowermenu extends Application
{

	public static ArrayList<String[]> defaultColors;
    public static ArrayList<Object[]> colors;

    static SharedPreferences orderPrefs;
    static SharedPreferences colorPrefs;

	public static String[][] lightPresetArray = {
            {"Presets"},
            {"Load"},
            {"Save"},
            {"Main", "#8800bcd4", "#fff5f5f5", "#000000"},
            {"Shutdown", "#ff0097a7", "#ffd32f2f", "#ffd32f2f", "#ffffff"},
            {"Reboot", "#ff0097a7", "#ff3f51b5", "#ff3f51b5", "#ffffff"},
            {"SoftReboot", "#ff0097a7", "#ffe91e63", "#ffe91e63", "#ffffff"},
            {"Screenshot", "#ff3f51b5", "#ffffff"},
            {"Screenrecord", "#ff3f51b5", "#ffffff"},
            {"Flashlight", "#ff3f51b5", "#ffffff"},
            {"ExpandedDesktop", "#ff3f51b5", "#ffffff"},
            {"AirplaneMode", "#ff3f51b5", "#ffffff"},
            {"RestartUI", "#ff3f51b5", "#ffffff"},
            {"SoundMode", "#ff3f51b5", "#ffffff"},
            {"Recovery", "#ff0097a7", "#ff8bc34a", "#ff8bc34a", "#ffffff"},
            {"Bootloader", "#ff0097a7", "#ff277b71", "#ff277b71", "#ffffff"},
            {"SafeMode", "#ff0097a7", "#ff009698", "#ff009698", "#ffffff"},
            {"SoundVibrate", "#ff3f51b5", "#ffffff"},
            {"SoundNormal", "#ff3f51b5", "#ffffff"},
            {"SoundSilent", "#ff3f51b5", "#ffffff"},
            {"KillApp", "#ff3f51b5", "#ffffff"},
            {"ToggleRotate", "#ff3f51b5", "#ffffff"},
            {"MediaPrevious", "#ff3f51b5", "#ffffff"},
            {"MediaPlayPause", "#ff3f51b5", "#ffffff"},
            {"MediaNext", "#ff3f51b5", "#ffffff"},
            {"ToggleWifi", "#ff3f51b5", "#ffffff"},
            {"ToggleBluetooth", "#ff3f51b5", "#ffffff"},
            {"ToggleData", "#ff3f51b5", "#ffffff"},
            {"RebootFlashMode", "#ff0097a7", "#ff3f51b5", "#ff3f51b5", "#ffffff"},
            {"LockPhone", "#ff3f51b5", "#ffffff"},
            {"SilentMode", "#ff3f51b5", "#ffffff"}};

    public static String[][] darkPresetArray = {
            {"Presets"},
            {"Load"},
            {"Save"},
            {"Main", "#88121212", "#ff212121", "#ffffff"},
            {"Shutdown", "#ff0097a7", "#ffd32f2f", "#ffd32f2f", "#ffffff"},
            {"Reboot", "#ff0097a7", "#ff3f51b5", "#ff3f51b5", "#ffffff"},
            {"SoftReboot", "#ff0097a7", "#ffe91e63", "#ffe91e63", "#ffffff"},
            {"Screenshot", "#ff3f51b5", "#ffffff"},
            {"Screenrecord", "#ff3f51b5", "#ffffff"},
            {"Flashlight", "#ff3f51b5", "#ffffff"},
            {"ExpandedDesktop", "#ff3f51b5", "#ffffff"},
            {"AirplaneMode", "#ff3f51b5", "#ffffff"},
            {"RestartUI", "#ff3f51b5", "#ffffff"},
            {"SoundMode", "#ff3f51b5", "#ffffff"},
            {"Recovery", "#ff0097a7", "#ff8bc34a", "#ff8bc34a", "#ffffff"},
            {"Bootloader", "#ff0097a7", "#ff277b71", "#ff277b71", "#ffffff"},
            {"SafeMode", "#ff0097a7", "#ff009698", "#ff009698", "#ffffff"},
            {"SoundVibrate", "#ff3f51b5", "#ffffff"},
            {"SoundNormal", "#ff3f51b5", "#ffffff"},
            {"SoundSilent", "#ff3f51b5", "#ffffff"},
            {"KillApp", "#ff3f51b5", "#ffffff"},
            {"ToggleRotate", "#ff3f51b5", "#ffffff"},
            {"MediaPrevious", "#ff3f51b5", "#ffffff"},
            {"MediaPlayPause", "#ff3f51b5", "#ffffff"},
            {"MediaNext", "#ff3f51b5", "#ffffff"},
            {"ToggleWifi", "#ff3f51b5", "#ffffff"},
            {"ToggleBluetooth", "#ff3f51b5", "#ffffff"},
            {"ToggleData", "#ff3f51b5", "#ffffff"},
            {"RebootFlashMode", "#ff0097a7", "#ff3f51b5", "#ff3f51b5", "#ffffff"},
            {"LockPhone", "#ff3f51b5", "#ffffff"},
            {"SilentMode", "#ff3f51b5", "#ffffff"}};

    public static String[][] blackPresetArray = {
            {"Presets"},
            {"Load"},
            {"Save"},
            {"Main", "#88000000", "#ff000000", "#ffffff"},
            {"Shutdown", "#ff000000", "#ff000000", "#ff000000", "#ffffff"},
            {"Reboot", "#ff000000", "#ff000000", "#ff000000", "#ffffff"},
            {"SoftReboot", "#ff000000", "#ff000000", "#ff000000", "#ffffff"},
            {"Screenshot", "#ff000000", "#ffffff"},
            {"Screenrecord", "#ff000000", "#ffffff"},
            {"Flashlight", "#ff000000", "#ffffff"},
            {"ExpandedDesktop", "#ff000000", "#ffffff"},
            {"AirplaneMode", "#ff000000", "#ffffff"},
            {"RestartUI", "#ff000000", "#ffffff"},
            {"SoundMode", "#ff000000", "#ffffff"},
            {"Recovery", "#ff000000", "#ff000000", "#ff000000", "#ffffff"},
            {"Bootloader", "#ff000000", "#ff000000", "#ff000000", "#ffffff"},
            {"SafeMode", "#ff000000", "#ff000000", "#ff000000", "#ffffff"},
            {"SoundVibrate", "#ff000000", "#ffffff"},
            {"SoundNormal", "#ff000000", "#ffffff"},
            {"SoundSilent", "#ff000000", "#ffffff"},
            {"KillApp", "#ff000000", "#ffffff"},
            {"ToggleRotate", "#ff000000", "#ffffff"},
            {"MediaPrevious", "#ff000000", "#ffffff"},
            {"MediaPlayPause", "#ff000000", "#ffffff"},
            {"MediaNext", "#ff000000", "#ffffff"},
            {"ToggleWifi", "#ff000000", "#ffffff"},
            {"ToggleBluetooth", "#ff000000", "#ffffff"},
            {"ToggleData", "#ff000000", "#ffffff"},
            {"RebootFlashMode", "#ff000000", "#ff000000", "#ff000000", "#ffffff"},
            {"LockPhone", "#ff000000", "#ffffff"},
            {"SilentMode", "#ff000000", "#ffffff"}};

    public static Object[][] ColorNamesArray = {
            {ColorsListAdapter.TYPE_HEADER, "Presets"},
            {ColorsListAdapter.TYPE_LOAD, "Load"},
            {ColorsListAdapter.TYPE_SAVE, "Save"},

            {ColorsListAdapter.TYPE_ITEM, "Main",
                    "Dialog_Revealcolor",
                    "Dialog_Backgroundcolor",
                    "Dialog_Textcolor"},

            {ColorsListAdapter.TYPE_ITEM, "Shutdown",
                    "DialogShutdown_Revealcolor",
                    "DialogShutdown_Backgroundcolor",
                    "DialogShutdown_Circlecolor",
                    "DialogShutdown_Textcolor"},

            {ColorsListAdapter.TYPE_ITEM, "Reboot",
                    "DialogReboot_Revealcolor",
                    "DialogReboot_Backgroundcolor",
                    "DialogReboot_Circlecolor",
                    "DialogReboot_Textcolor"},

            {ColorsListAdapter.TYPE_ITEM, "SoftReboot",
                    "DialogSoftReboot_Revealcolor",
                    "DialogSoftReboot_Backgroundcolor",
                    "DialogSoftReboot_Circlecolor",
                    "DialogSoftReboot_Textcolor"},

            {ColorsListAdapter.TYPE_ITEM, "Screenshot",
                    "DialogScreenshot_Circlecolor",
                    "DialogScreenshot_Textcolor"},

            {ColorsListAdapter.TYPE_ITEM, "Screenrecord",
                    "DialogScreenrecord_Circlecolor",
                    "DialogScreenrecord_Textcolor"},

            {ColorsListAdapter.TYPE_ITEM, "Flashlight",
                    "DialogFlashlight_Circlecolor",
                    "DialogFlashlight_Textcolor"},

            {ColorsListAdapter.TYPE_ITEM, "ExpandedDesktop",
                    "DialogExpandedDesktop_Circlecolor",
                    "DialogExpandedDesktop_Textcolor"},

            {ColorsListAdapter.TYPE_ITEM, "AirplaneMode",
                    "DialogAirplaneMode_Circlecolor",
                    "DialogAirplaneMode_Textcolor"},

            {ColorsListAdapter.TYPE_ITEM, "RestartUI",
                    "DialogRestartUI_Circlecolor",
                    "DialogRestartUI_Textcolor"},

            {ColorsListAdapter.TYPE_ITEM, "SoundMode",
                    "DialogSoundMode_Circlecolor",
                    "DialogSoundMode_Textcolor"},

            {ColorsListAdapter.TYPE_ITEM, "Recovery",
                    "DialogRecovery_Revealcolor",
                    "DialogRecovery_Backgroundcolor",
                    "DialogRecovery_Circlecolor",
                    "DialogRecovery_Textcolor"},

            {ColorsListAdapter.TYPE_ITEM, "Bootloader",
                    "DialogBootloader_Revealcolor",
                    "DialogBootloader_Backgroundcolor",
                    "DialogBootloader_Circlecolor",
                    "DialogBootloader_Textcolor"},

            {ColorsListAdapter.TYPE_ITEM, "SafeMode",
                    "DialogSafeMode_Revealcolor",
                    "DialogSafeMode_Backgroundcolor",
                    "DialogSafeMode_Circlecolor",
                    "DialogSafeMode_Textcolor"},

            {ColorsListAdapter.TYPE_ITEM, "SoundVibrate",
                    "DialogSoundVibrate_Circlecolor",
                    "DialogSoundVibrate_Textcolor"},

            {ColorsListAdapter.TYPE_ITEM, "SoundNormal",
                    "DialogSoundNormal_Circlecolor",
                    "DialogSoundNormal_Textcolor"},

            {ColorsListAdapter.TYPE_ITEM, "SoundSilent",
                    "DialogSoundSilent_Circlecolor",
                    "DialogSoundSilent_Textcolor"},

            {ColorsListAdapter.TYPE_ITEM, "KillApp",
                    "DialogKillApp_Circlecolor",
                    "DialogKillApp_Textcolor"},

            {ColorsListAdapter.TYPE_ITEM, "ToggleRotate",
                    "DialogToggleRotate_Circlecolor",
                    "DialogToggleRotate_Textcolor"},

            {ColorsListAdapter.TYPE_ITEM, "MediaPrevious",
                    "DialogMediaPrevious_Circlecolor",
                    "DialogMediaPrevious_Textcolor"},

            {ColorsListAdapter.TYPE_ITEM, "MediaPlayPause",
                    "DialogMediaPlayPause_Circlecolor",
                    "DialogMediaPlayPause_Textcolor"},

            {ColorsListAdapter.TYPE_ITEM, "MediaNext",
                    "DialogMediaNext_Circlecolor",
                    "DialogMediaNext_Textcolor"},

            {ColorsListAdapter.TYPE_ITEM, "ToggleWifi",
                    "DialogToggleWifi_Circlecolor",
                    "DialogToggleWifi_Textcolor"},

            {ColorsListAdapter.TYPE_ITEM, "ToggleBluetooth",
                    "DialogToggleBluetooth_Circlecolor",
                    "DialogToggleBluetooth_Textcolor"},

            {ColorsListAdapter.TYPE_ITEM, "ToggleData",
                    "DialogToggleData_Circlecolor",
                    "DialogToggleData_Textcolor"},

            {ColorsListAdapter.TYPE_ITEM, "RebootFlashMode",
                    "DialogRebootFlashMode_Revealcolor",
                    "DialogRebootFlashMode_Backgroundcolor",
                    "DialogRebootFlashMode_Circlecolor",
                    "DialogRebootFlashMode_Textcolor"},

            {ColorsListAdapter.TYPE_ITEM, "LockPhone",
                    "DialogLockPhone_Circlecolor",
                    "DialogLockPhone_Textcolor"},

            {ColorsListAdapter.TYPE_ITEM, "SilentMode",
                    "DialogSilentMode_Circlecolor",
                    "DialogSilentMode_Textcolor"}};

		@Override
		public void onCreate()
		{
            super.onCreate();

            orderPrefs = getSharedPreferences("visibilityOrder", 0);
            colorPrefs = getSharedPreferences("colors", 0);
            parseColors();
		}

		public static void parseColors() {
            defaultColors = new ArrayList<>(Arrays.asList(lightPresetArray));
            colors = new ArrayList<>(Arrays.asList(ColorNamesArray));
            ArrayList<String> MultiPage = new ArrayList<String>();
            for (int i = 0; i < orderPrefs.getAll().keySet().size(); i++) {
                if (orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", -1) != -1) {
                    if (orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", visibilityOrder_ListAdapter.TYPE_NORMAL) == visibilityOrder_ListAdapter.TYPE_NORMAL) {
                        if (orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null").contains(".") || !orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_shortcutUri", "").isEmpty()) {
                            Object[] newElement = new Object[] {ColorsListAdapter.TYPE_ITEM,
                                    orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null").split("/")[0],
                                    "Dialog" + (orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null").contains(".") ? "AppShortcut" : "Shortcut") + orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null").split("/")[0] + "_Circlecolor",
                                    "Dialog" + (orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null").contains(".") ? "AppShortcut" : "Shortcut") + orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null").split("/")[0] + "_Textcolor"};
                            if (!isInArray(colors, newElement)) {
                                colors.add(newElement);
                                defaultColors.add(new String[]{"", "#ff3f51b5", "#ffffff"});
                            }
                            for (int y = 2; y < neopowermenu.colors.get(neopowermenu.colors.size()-1).length; y++) {
                                if (colorPrefs.getString(neopowermenu.colors.get(neopowermenu.colors.size()-1)[y].toString(), "").isEmpty()) {
                                    colorPrefs.edit().putString(neopowermenu.colors.get(neopowermenu.colors.size()-1)[y].toString(), neopowermenu.defaultColors.get(neopowermenu.defaultColors.size()-1)[y - 1]).apply();
                                    //MainActivity.preferences.edit().remove(neopowermenu.colors.get(i)[1].toString()).commit();
                                }
                            }
                        }
                    } else if (orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", visibilityOrder_ListAdapter.TYPE_NORMAL) == visibilityOrder_ListAdapter.TYPE_MULTI) {
                        int x = 1;
                        do {
                            if (orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item" + x + "_title", "null").contains(".") || !orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item" + x + "_shortcutUri", "").isEmpty()) {
                                Object[] newElement = new Object[] {ColorsListAdapter.TYPE_ITEM,
                                            orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item" + x + "_title", "null").split("/")[0],
                                            "Dialog" + (orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item" + x + "_title", "null").contains(".") ? "AppShortcut" : "Shortcut") + orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item" + x + "_title", "null").split("/")[0] + "_Circlecolor",
                                            "Dialog" + (orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item" + x + "_title", "null").contains(".") ? "AppShortcut" : "Shortcut") + orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item" + x + "_title", "null").split("/")[0] + "_Textcolor"};
                                if (!isInArray(colors, newElement)) {
                                    colors.add(newElement);
                                    defaultColors.add(new String[]{"", "#ff3f51b5", "#ffffff"});
                                }
                                for (int y = 2; y < neopowermenu.colors.get(neopowermenu.colors.size()-1).length; y++) {
                                    if (colorPrefs.getString(neopowermenu.colors.get(neopowermenu.colors.size()-1)[y].toString(), "").isEmpty()) {
                                        colorPrefs.edit().putString(neopowermenu.colors.get(neopowermenu.colors.size()-1)[y].toString(), neopowermenu.defaultColors.get(neopowermenu.defaultColors.size()-1)[y - 1]).apply();
                                        //MainActivity.preferences.edit().remove(neopowermenu.colors.get(i)[1].toString()).commit();
                                    }
                                }
                            }
                            x++;
                        } while (!orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item" + x + "_title", "][EMPTY][").equals("][EMPTY]["));
                    } else if (orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", -1) == visibilityOrder_ListAdapter.TYPE_MULTIPAGE_START) {
                        MultiPage.add(orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null"));
                    } else if (MultiPage.size() > 0 && orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", -1) == visibilityOrder_ListAdapter.TYPE_MULTIPAGE_END) {
                        MultiPage.remove(MultiPage.size() - 1);
                    }
                }
            }
        }

    static boolean isInArray(ArrayList<Object[]> array, Object[] search) {
        for (int x = 0; x < array.size(); x++) {
            int matches = 0;
            for (int y = 0; y < array.get(x).length; y++) {
                if (array.get(x)[1].equals(search[1])) matches++;
            }
            if (matches == array.get(x).length) {
                return true;
            }
        }
        return false;
    }

}

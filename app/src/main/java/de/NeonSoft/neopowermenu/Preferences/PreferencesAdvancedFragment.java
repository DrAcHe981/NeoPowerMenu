package de.NeonSoft.neopowermenu.Preferences;

import android.support.v4.app.*;
import android.view.*;
import android.os.*;

import de.NeonSoft.neopowermenu.*;

import android.widget.*;

import de.NeonSoft.neopowermenu.helpers.*;

import android.widget.SeekBar.*;

public class PreferencesAdvancedFragment extends Fragment {

    LinearLayout LinearLayout_UseGraphics;
    Switch Switch_UseGraphics;
    boolean boolean_UseGraphics;
    boolean ExperimentalPWMHook = false;

    LinearLayout LinearLayout_DialogGravity;

    LinearLayout LinearLayout_Confirmation;
    TextView TextView_ConfirmationTitle;
    TextView TextView_ConfirmationDesc;
    Switch Switch_Confirmation;
    boolean boolean_Confirmation;

    LinearLayout LinearLayout_HideOnClick;
    Switch Switch_HideOnClick;
    boolean boolean_HideOnClick;

    LinearLayout LinearLayout_LoadAppIcons;
    Switch Switch_LoadAppIcons;
    boolean boolean_LoadAppIcons;

    LinearLayout LinearLayout_RoundAppIcons;
    Switch Switch_RoundAppIcons;
    boolean boolean_RoundAppIcons;

    LinearLayout LinearLayout_ColorizeNonStockIcons;
    Switch Switch_ColorizeNonStockIcons;
    boolean boolean_ColorizeNonStockIcons;

    LinearLayout LinearLayout_ShowOnLockScreen;
    Switch Switch_ShowOnLockScreen;
    boolean boolean_ShowOnLockScreen;

    LinearLayout LinearLayout_UseRoot;
    Switch Switch_UseRoot;
    boolean boolean_UseRoot;

    LinearLayout LinearLayout_ExperimentalPWMHook;
    Switch Switch_ExperimentalPWMHook;

    LinearLayout LinearLayout_ScreenshotDelay;
    SeekBar SeekBar_ScreenshotDelay;
    TextView TextView_ScreenshotDelayTime;
    long Long_ScreenshotDelay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: Implement this method
        if(!MainActivity.visibleFragment.equalsIgnoreCase("tour")) {
            MainActivity.visibleFragment = "Advanced";
        }

        MainActivity.actionbar.setTitle(getString(R.string.preferences_Advanced).split("\\|")[0]);
        MainActivity.actionbar.setSubTitle(getString(R.string.preferences_Advanced).split("\\|")[1]);

        View InflatedView = inflater.inflate(R.layout.activity_advanced, null);

        boolean_UseGraphics = MainActivity.preferences.getBoolean("UseGraphics", false);
        LinearLayout_UseGraphics = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_UseGraphics);
        Switch_UseGraphics = (Switch) InflatedView.findViewById(R.id.activityadvancedSwitch_UseGraphics);

        Switch_UseGraphics.setChecked(boolean_UseGraphics);
        Switch_UseGraphics.setClickable(false);
        Switch_UseGraphics.setFocusable(false);

        LinearLayout_UseGraphics.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                // TODO: Implement this method
                boolean_UseGraphics = !boolean_UseGraphics;
                MainActivity.preferences.edit().putBoolean("UseGraphics", boolean_UseGraphics).apply();
                Switch_UseGraphics.setChecked(boolean_UseGraphics);
            }
        });

        LinearLayout_DialogGravity = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_DialogGravity);

        LinearLayout_DialogGravity.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                //GravityChooserDialog gcd = new GravityChooserDialog();
                MainActivity.changePrefPage(new GravityChooserDialog(), false);
                //gcd.setStyle(gcd.STYLE_NO_FRAME,R.style.TransparentApp);
                //gcd.show(MainActivity.fragmentManager,"gravity_chooser");
            }
        });

        boolean_Confirmation = MainActivity.preferences.getBoolean("RequireConfirmation", false);
        LinearLayout_Confirmation = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_Confirmation);
        TextView_ConfirmationTitle = (TextView) InflatedView.findViewById(R.id.activityadvancedTextView_ConfirmationTitle);
        TextView_ConfirmationDesc = (TextView) InflatedView.findViewById(R.id.activityadvancedTextView_ConfirmationDesc);
        TextView_ConfirmationTitle.setText(getString(R.string.advancedPrefs_Confirmation).split("\\|")[0]);
        TextView_ConfirmationDesc.setText(getString(R.string.advancedPrefs_Confirmation).split("\\|")[1]);
        Switch_Confirmation = (Switch) InflatedView.findViewById(R.id.activityadvancedSwitch_Confirmation);
        Switch_Confirmation.setChecked(boolean_Confirmation);
        Switch_Confirmation.setClickable(false);
        Switch_Confirmation.setFocusable(false);

        LinearLayout_Confirmation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                boolean_Confirmation = !boolean_Confirmation;
                Switch_Confirmation.setChecked(boolean_Confirmation);
                MainActivity.preferences.edit().putBoolean("RequireConfirmation", boolean_Confirmation).apply();
            }
        });

        boolean_HideOnClick = MainActivity.preferences.getBoolean("HideOnClick", false);
        LinearLayout_HideOnClick = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_HideOnClick);
        Switch_HideOnClick = (Switch) InflatedView.findViewById(R.id.activityadvancedSwitch_HideOnClick);

        Switch_HideOnClick.setChecked(boolean_HideOnClick);
        Switch_HideOnClick.setClickable(false);
        Switch_HideOnClick.setFocusable(false);

        LinearLayout_HideOnClick.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                boolean_HideOnClick = !boolean_HideOnClick;
                Switch_HideOnClick.setChecked(boolean_HideOnClick);
                MainActivity.preferences.edit().putBoolean("HideOnClick", boolean_HideOnClick).apply();
            }
        });

        boolean_LoadAppIcons = MainActivity.preferences.getBoolean("LoadAppIcons", true);
        LinearLayout_LoadAppIcons = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_LoadAppIcons);
        Switch_LoadAppIcons = (Switch) InflatedView.findViewById(R.id.activityadvancedSwitch_LoadAppIcons);

        Switch_LoadAppIcons.setChecked(boolean_LoadAppIcons);
        Switch_LoadAppIcons.setClickable(false);
        Switch_LoadAppIcons.setFocusable(false);

        LinearLayout_LoadAppIcons.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                boolean_LoadAppIcons = !boolean_LoadAppIcons;
                Switch_LoadAppIcons.setChecked(boolean_LoadAppIcons);
                MainActivity.preferences.edit().putBoolean("LoadAppIcons", boolean_LoadAppIcons).apply();
            }
        });

        boolean_RoundAppIcons = MainActivity.preferences.getBoolean("RoundAppIcons", false);
        LinearLayout_RoundAppIcons = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_RoundAppIcons);
        Switch_RoundAppIcons = (Switch) InflatedView.findViewById(R.id.activityadvancedSwitch_RoundAppIcons);

        Switch_RoundAppIcons.setChecked(boolean_RoundAppIcons);
        Switch_RoundAppIcons.setClickable(false);
        Switch_RoundAppIcons.setFocusable(false);

        LinearLayout_RoundAppIcons.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                boolean_RoundAppIcons = !boolean_RoundAppIcons;
                Switch_RoundAppIcons.setChecked(boolean_RoundAppIcons);
                MainActivity.preferences.edit().putBoolean("RoundAppIcons", boolean_RoundAppIcons).apply();
            }
        });

        boolean_ColorizeNonStockIcons = MainActivity.preferences.getBoolean("ColorizeNonStockIcons", false);
        LinearLayout_ColorizeNonStockIcons = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_ColorizeNonStockIcons);
        Switch_ColorizeNonStockIcons = (Switch) InflatedView.findViewById(R.id.activityadvancedSwitch_ColorizeNonStockIcons);

        Switch_ColorizeNonStockIcons.setChecked(boolean_ColorizeNonStockIcons);
        Switch_ColorizeNonStockIcons.setClickable(false);
        Switch_ColorizeNonStockIcons.setFocusable(false);

        LinearLayout_ColorizeNonStockIcons.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                boolean_ColorizeNonStockIcons = !boolean_ColorizeNonStockIcons;
                Switch_ColorizeNonStockIcons.setChecked(boolean_ColorizeNonStockIcons);
                MainActivity.preferences.edit().putBoolean("ColorizeNonStockIcons", boolean_ColorizeNonStockIcons).apply();
            }
        });

        boolean_UseRoot = MainActivity.preferences.getBoolean("UseRoot", true);
        LinearLayout_UseRoot = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_UseRoot);
        Switch_UseRoot = (Switch) InflatedView.findViewById(R.id.activityadvancedSwitch_UseRoot);

        Switch_UseRoot.setChecked(boolean_UseRoot);
        Switch_UseRoot.setClickable(false);
        Switch_UseRoot.setFocusable(false);

        LinearLayout_UseRoot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                boolean_UseRoot = !boolean_UseRoot;
                Switch_UseRoot.setChecked(boolean_UseRoot);
                MainActivity.preferences.edit().putBoolean("UseRoot", boolean_UseRoot).apply();
            }
        });

        ExperimentalPWMHook = MainActivity.preferences.getBoolean("ExperimentalPWMHook", false);
        LinearLayout_ExperimentalPWMHook = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_ExperimentalPWMHook);
        Switch_ExperimentalPWMHook = (Switch) InflatedView.findViewById(R.id.activityadvancedSwitch_ExperimentalPWMHook);

        Switch_ExperimentalPWMHook.setChecked(ExperimentalPWMHook);
        Switch_ExperimentalPWMHook.setClickable(false);
        Switch_ExperimentalPWMHook.setFocusable(false);

        LinearLayout_ExperimentalPWMHook.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                ExperimentalPWMHook = !ExperimentalPWMHook;
                MainActivity.preferences.edit().putBoolean("ExperimentalPWMHook", ExperimentalPWMHook).apply();
                Switch_ExperimentalPWMHook.setChecked(ExperimentalPWMHook);
            }
        });

        boolean_ShowOnLockScreen = MainActivity.preferences.getBoolean("ShowOnLockScreen", true);
        LinearLayout_ShowOnLockScreen = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_ShowOnLockscreen);
        Switch_ShowOnLockScreen = (Switch) InflatedView.findViewById(R.id.activityadvancedSwitch_ShowOnLockScreen);

        Switch_ShowOnLockScreen.setChecked(boolean_ShowOnLockScreen);
        Switch_ShowOnLockScreen.setClickable(false);
        Switch_ShowOnLockScreen.setFocusable(false);

        LinearLayout_ShowOnLockScreen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                // TODO: Implement this method
                boolean_ShowOnLockScreen = !boolean_ShowOnLockScreen;
                MainActivity.preferences.edit().putBoolean("ShowOnLockScreen", boolean_ShowOnLockScreen).apply();
                Switch_ShowOnLockScreen.setChecked(boolean_ShowOnLockScreen);
            }
        });

        Long_ScreenshotDelay = MainActivity.preferences.getLong("ScreenshotDelay", 1000);
        LinearLayout_ScreenshotDelay = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_ScreenshotDelay);
        SeekBar_ScreenshotDelay = (SeekBar) InflatedView.findViewById(R.id.activityadvancedSeekBar_ScreenshotDelay);
        TextView_ScreenshotDelayTime = (TextView) InflatedView.findViewById(R.id.activityadvancedTextView_ScreenshotDelayTime);

        SeekBar_ScreenshotDelay.setMax(150);
        SeekBar_ScreenshotDelay.setProgress((int) (Long_ScreenshotDelay / 1000));
        if (Long_ScreenshotDelay == 0) {
            TextView_ScreenshotDelayTime.setText(R.string.advancedPrefs_DelayZero);
        } else {
            TextView_ScreenshotDelayTime.setText(helper.getTimeString(Long_ScreenshotDelay, true));
        }
        SeekBar_ScreenshotDelay.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar p1, int p2, boolean p3) {
                // TODO: Implement this method
                Long_ScreenshotDelay = (p2 * 1000);
                if (Long_ScreenshotDelay == 0) {
                    TextView_ScreenshotDelayTime.setText(R.string.advancedPrefs_DelayZero);
                } else {
                    TextView_ScreenshotDelayTime.setText(helper.getTimeString(Long_ScreenshotDelay, true));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar p1) {
                // TODO: Implement this method
            }

            @Override
            public void onStopTrackingTouch(SeekBar p1) {
                // TODO: Implement this method
                MainActivity.preferences.edit().putLong("ScreenshotDelay", Long_ScreenshotDelay).apply();
            }
        });

        return InflatedView;
    }

}

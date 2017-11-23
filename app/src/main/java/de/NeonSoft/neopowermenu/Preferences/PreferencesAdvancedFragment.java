package de.NeonSoft.neopowermenu.Preferences;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.support.v4.app.*;
import android.text.InputType;
import android.util.Log;
import android.view.*;
import android.os.*;

import de.NeonSoft.neopowermenu.*;

import android.widget.*;

import java.util.concurrent.TimeUnit;

import de.NeonSoft.neopowermenu.helpers.*;

public class PreferencesAdvancedFragment extends Fragment {

    boolean ExperimentalPWMHook = false;

    LinearLayout LinearLayout_FlashlightAutoOff;
    Switch Switch_FlashlightAutoOff;
    boolean boolean_FlashlightAutoOff;

    LinearLayout LinearLayout_FlashlightAutoOffTime;
    TextView TextView_FlashlightAutoOffTime;
    long Long_FlashlightAutoOffTime;

    LinearLayout LinearLayout_ScreenRecordStockBinary;
    Switch Switch_ScreenRecordStockBinary;
    boolean boolean_ScreenRecordStockBinary;

    LinearLayout LinearLayout_ScreenRecordMicrophone;
    Switch Switch_ScreenRecordMicrophone;
    boolean boolean_ScreenRecordMicrophone;

    LinearLayout LinearLayout_ScreenRecordSize;
    TextView TextView_ScreenRecordSize;
    long Long_ScreenRecordSize;

    LinearLayout LinearLayout_ScreenRecordBitRate;
    TextView TextView_ScreenRecordBitRate;
    long Long_ScreenRecordBitRate;

    LinearLayout LinearLayout_ScreenRecordTimeLimit;
    TextView TextView_ScreenRecordTimeLimit;
    long Long_ScreenRecordTimeLimit;

    LinearLayout LinearLayout_ScreenRecordRotate;
    Switch Switch_ScreenRecordRotate;
    boolean boolean_ScreenRecordRotate;

    LinearLayout LinearLayout_ScreenRecordCountdown;
    TextView TextView_ScreenRecordCountdown;
    long Long_ScreenRecordCountdown;

    LinearLayout LinearLayout_Password;

    LinearLayout LinearLayout_LockWithFingerprint;
    Switch Switch_LockWithFingerprint;
    boolean boolean_LockWithFingerprint;

    LinearLayout LinearLayout_BlurBehind;
    Switch Switch_BlurBehind;
    boolean boolean_BlurBehind;

    LinearLayout LinearLayout_BlurRadius;
    TextView TextView_BlurRadius;
    float Float_BlurRadius;

    LinearLayout LinearLayout_Confirmation;
    TextView TextView_ConfirmationTitle;
    TextView TextView_ConfirmationDesc;
    Switch Switch_Confirmation;
    boolean boolean_Confirmation;

    LinearLayout LinearLayout_HideOnClick;
    Switch Switch_HideOnClick;
    boolean boolean_HideOnClick;

    LinearLayout LinearLayout_ShowOnLockScreen;
    Switch Switch_ShowOnLockScreen;
    boolean boolean_ShowOnLockScreen;

    LinearLayout LinearLayout_UseRoot;
    Switch Switch_UseRoot;
    boolean boolean_UseRoot;

    LinearLayout LinearLayout_ExperimentalPWMHook;
    Switch Switch_ExperimentalPWMHook;

    LinearLayout LinearLayout_ScreenshotDelay;
    TextView TextView_ScreenshotDelayTime;
    long Long_ScreenshotDelay;

    KeyguardManager mKeyguardManager;
    FingerprintManager mFingerprintManager;

    FingerprintHandler mFingerprintHandler;
    slideDownDialogFragment fingerprintDialog;
    private Toast fingerprintErrorToast;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!MainActivity.visibleFragment.equalsIgnoreCase("tour")) {
            MainActivity.visibleFragment = "Advanced";
        }

        mKeyguardManager = (KeyguardManager) getActivity().getSystemService(Context.KEYGUARD_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
        }

        MainActivity.actionbar.setTitle(getString(R.string.preferences_Advanced).split("\\|")[0]);
        MainActivity.actionbar.setSubTitle(getString(R.string.preferences_Advanced).split("\\|")[1]);

        View InflatedView = inflater.inflate(R.layout.activity_advanced, container, false);

        boolean_FlashlightAutoOff = MainActivity.preferences.getBoolean(PreferenceNames.pTorchAutoOff, true);
        LinearLayout_FlashlightAutoOff = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_FlashlightAutoOff);
        Switch_FlashlightAutoOff = (Switch) InflatedView.findViewById(R.id.activityadvancedSwitch_FlashlightAutoOff);

        Switch_FlashlightAutoOff.setChecked(boolean_FlashlightAutoOff);
        Switch_FlashlightAutoOff.setClickable(false);
        Switch_FlashlightAutoOff.setFocusable(false);

        LinearLayout_FlashlightAutoOff.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                boolean_FlashlightAutoOff = !boolean_FlashlightAutoOff;
                MainActivity.preferences.edit().putBoolean(PreferenceNames.pTorchAutoOff, boolean_FlashlightAutoOff).commit();
                Switch_FlashlightAutoOff.setChecked(boolean_FlashlightAutoOff);
                if (boolean_FlashlightAutoOff) {
                    LinearLayout_FlashlightAutoOffTime.setEnabled(true);
                    LinearLayout_FlashlightAutoOffTime.setAlpha((float) 1);
                } else {
                    LinearLayout_FlashlightAutoOffTime.setEnabled(false);
                    LinearLayout_FlashlightAutoOffTime.setAlpha((float) .3);
                }
            }
        });

        Long_FlashlightAutoOffTime = MainActivity.preferences.getLong(PreferenceNames.pTorchAutoOffTime, 1000 * 60 * 10);
        LinearLayout_FlashlightAutoOffTime = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_FlashlightAutoOffTime);
        TextView_FlashlightAutoOffTime = (TextView) InflatedView.findViewById(R.id.activityadvancedTextView_FlashlightAutoOffTime);
        if (Long_FlashlightAutoOffTime == 0) {
            TextView_FlashlightAutoOffTime.setText(R.string.advancedPrefs_DelayZero);
        } else {
            TextView_FlashlightAutoOffTime.setText(helper.getTimeString(getActivity(), Long_FlashlightAutoOffTime, 1));
        }
        if (boolean_FlashlightAutoOff) {
            LinearLayout_FlashlightAutoOffTime.setEnabled(true);
            LinearLayout_FlashlightAutoOffTime.setAlpha((float) 1);
        } else {
            LinearLayout_FlashlightAutoOffTime.setEnabled(false);
            LinearLayout_FlashlightAutoOffTime.setAlpha((float) .3);
        }

        LinearLayout_FlashlightAutoOffTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                dialogFragment.setContext(getActivity());
                dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                dialogFragment.setText(getString(R.string.advancedPrefs_SelectTime));
                View customView = inflater.inflate(R.layout.timepickerdialog, null, false);
                final NumberPicker timepickerdialog_PickerHours = (NumberPicker) customView.findViewById(R.id.timepickerdialog_Hours);
                final NumberPicker timepickerdialog_PickerMinutes = (NumberPicker) customView.findViewById(R.id.timepickerdialog_Minutes);
                final NumberPicker timepickerdialog_PickerSeconds = (NumberPicker) customView.findViewById(R.id.timepickerdialog_Secondss);
                String[] vals = new String[25];
                for (int i = 0; i <= 24; i++) {
                    vals[i] = i + " " + getString(R.string.advancedPrefs_Hours);
                }
                timepickerdialog_PickerHours.setMinValue(0);
                timepickerdialog_PickerHours.setMaxValue(24);
                timepickerdialog_PickerHours.setWrapSelectorWheel(false);
                timepickerdialog_PickerHours.setDisplayedValues(vals);
                timepickerdialog_PickerHours.setValue((int) helper.splitMilliseconds(Long_FlashlightAutoOffTime, "h"));
                helper.setDividerColor(timepickerdialog_PickerHours, getResources().getColor(R.color.colorAccentDarkThemeTrans));
                vals = new String[61];
                for (int i = 0; i <= 60; i++) {
                    vals[i] = i + " " + getString(R.string.advancedPrefs_Minutes);
                }
                timepickerdialog_PickerMinutes.setMinValue(0);
                timepickerdialog_PickerMinutes.setMaxValue(59);
                timepickerdialog_PickerMinutes.setWrapSelectorWheel(false);
                timepickerdialog_PickerMinutes.setDisplayedValues(vals);
                timepickerdialog_PickerMinutes.setValue((int) helper.splitMilliseconds(Long_FlashlightAutoOffTime, "m"));
                helper.setDividerColor(timepickerdialog_PickerMinutes, getResources().getColor(R.color.colorAccentDarkThemeTrans));
                vals = new String[61];
                for (int i = 0; i <= 60; i++) {
                    vals[i] = i + " " + getString(R.string.advancedPrefs_Seconds);
                }
                timepickerdialog_PickerSeconds.setMinValue(0);
                timepickerdialog_PickerSeconds.setMaxValue(59);
                timepickerdialog_PickerSeconds.setWrapSelectorWheel(false);
                timepickerdialog_PickerSeconds.setDisplayedValues(vals);
                timepickerdialog_PickerSeconds.setValue((int) helper.splitMilliseconds(Long_FlashlightAutoOffTime, "s"));
                helper.setDividerColor(timepickerdialog_PickerSeconds, getResources().getColor(R.color.colorAccentDarkThemeTrans));
                dialogFragment.setCustomView(customView);
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
                        long milliseconds = 0;
                        milliseconds += java.util.concurrent.TimeUnit.HOURS.toMillis(timepickerdialog_PickerHours.getValue());//timepickerdialog_PickerHours.getValue() * (1000 * 60);
                        milliseconds += java.util.concurrent.TimeUnit.MINUTES.toMillis(timepickerdialog_PickerMinutes.getValue());//timepickerdialog_PickerMinutes.getValue() * (1000 * 60);
                        milliseconds += java.util.concurrent.TimeUnit.SECONDS.toMillis(timepickerdialog_PickerSeconds.getValue());//timepickerdialog_PickerSeconds.getValue() * 1000;
                        Long_FlashlightAutoOffTime = milliseconds;
                        if (Long_FlashlightAutoOffTime == 0) {
                            TextView_FlashlightAutoOffTime.setText(R.string.advancedPrefs_DelayZero);
                        } else {
                            TextView_FlashlightAutoOffTime.setText(helper.getTimeString(getActivity(), Long_FlashlightAutoOffTime, 1));
                        }
                        MainActivity.preferences.edit().putLong(PreferenceNames.pTorchAutoOffTime, Long_FlashlightAutoOffTime).commit();
                    }

                    @Override
                    public void onTouchOutside() {

                    }
                });
                dialogFragment.setNegativeButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_OK]);
                dialogFragment.showDialog(R.id.dialog_container);
            }
        });

        boolean_ScreenRecordStockBinary = MainActivity.preferences.getBoolean(PreferenceNames.pScreenRecord_UseStockBinary, false);
        LinearLayout_ScreenRecordStockBinary = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_ScreenRecordUseStockBinary);
        Switch_ScreenRecordStockBinary = (Switch) InflatedView.findViewById(R.id.activityadvancedSwitch_ScreenRecordUseStockBinary);

        Switch_ScreenRecordStockBinary.setChecked(boolean_ScreenRecordStockBinary);
        Switch_ScreenRecordStockBinary.setClickable(false);
        Switch_ScreenRecordStockBinary.setFocusable(false);

        LinearLayout_ScreenRecordStockBinary.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                boolean_ScreenRecordStockBinary = !boolean_ScreenRecordStockBinary;
                MainActivity.preferences.edit().putBoolean(PreferenceNames.pScreenRecord_UseStockBinary, boolean_ScreenRecordStockBinary).commit();
                Switch_ScreenRecordStockBinary.setChecked(boolean_ScreenRecordStockBinary);
                if (!boolean_ScreenRecordStockBinary) {
                    LinearLayout_ScreenRecordMicrophone.setEnabled(true);
                    LinearLayout_ScreenRecordMicrophone.setAlpha((float) 1);
                    LinearLayout_ScreenRecordTimeLimit.setEnabled(true);
                    LinearLayout_ScreenRecordTimeLimit.setAlpha((float) 1);
                } else {
                    LinearLayout_ScreenRecordMicrophone.setEnabled(false);
                    LinearLayout_ScreenRecordMicrophone.setAlpha((float) .3);
                    LinearLayout_ScreenRecordTimeLimit.setEnabled(false);
                    LinearLayout_ScreenRecordTimeLimit.setAlpha((float) .3);
                }
            }
        });

        boolean_ScreenRecordMicrophone = MainActivity.preferences.getBoolean(PreferenceNames.pScreenRecord_Microphone, true);
        LinearLayout_ScreenRecordMicrophone = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_ScreenRecordMicrophone);
        Switch_ScreenRecordMicrophone = (Switch) InflatedView.findViewById(R.id.activityadvancedSwitch_ScreenRecordMicrophone);

        Switch_ScreenRecordMicrophone.setChecked(boolean_ScreenRecordMicrophone);
        Switch_ScreenRecordMicrophone.setClickable(false);
        Switch_ScreenRecordMicrophone.setFocusable(false);
        if (!boolean_ScreenRecordStockBinary) {
            LinearLayout_ScreenRecordMicrophone.setEnabled(true);
            LinearLayout_ScreenRecordMicrophone.setAlpha((float) 1);
        } else {
            LinearLayout_ScreenRecordMicrophone.setEnabled(false);
            LinearLayout_ScreenRecordMicrophone.setAlpha((float) .3);
        }

        LinearLayout_ScreenRecordMicrophone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                boolean_ScreenRecordMicrophone = !boolean_ScreenRecordMicrophone;
                MainActivity.preferences.edit().putBoolean(PreferenceNames.pScreenRecord_Microphone, boolean_ScreenRecordMicrophone).commit();
                Switch_ScreenRecordMicrophone.setChecked(boolean_ScreenRecordMicrophone);
            }
        });

        final String[] ScreenRecordingSizes = new String[]{"Device Default", "SD (360x640px)", "HD (720x1280px)", "Full HD (1080x1920px)"};

        Long_ScreenRecordSize = MainActivity.preferences.getLong(PreferenceNames.pScreenRecord_Size + "Long", 0);
        LinearLayout_ScreenRecordSize = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_ScreenRecordSize);
        TextView_ScreenRecordSize = (TextView) InflatedView.findViewById(R.id.activityadvancedTextView_ScreenRecordSize);
        TextView_ScreenRecordSize.setText(ScreenRecordingSizes[(int) Long_ScreenRecordSize]);

        LinearLayout_ScreenRecordSize.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                dialogFragment.setContext(getActivity());
                dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                dialogFragment.setText(getString(R.string.advancedPrefs_SelectSize));
                View customView = inflater.inflate(R.layout.timepickerdialog, null, false);
                final NumberPicker timepickerdialog_PickerHours = (NumberPicker) customView.findViewById(R.id.timepickerdialog_Hours);
                final NumberPicker timepickerdialog_PickerMinutes = (NumberPicker) customView.findViewById(R.id.timepickerdialog_Minutes);
                timepickerdialog_PickerMinutes.setVisibility(View.GONE);
                final NumberPicker timepickerdialog_PickerSeconds = (NumberPicker) customView.findViewById(R.id.timepickerdialog_Secondss);
                timepickerdialog_PickerSeconds.setVisibility(View.GONE);
                timepickerdialog_PickerHours.setMinValue(0);
                timepickerdialog_PickerHours.setMaxValue(3);
                timepickerdialog_PickerHours.setWrapSelectorWheel(false);
                timepickerdialog_PickerHours.setDisplayedValues(ScreenRecordingSizes);
                timepickerdialog_PickerHours.setValue((int) Long_ScreenRecordSize);
                helper.setDividerColor(timepickerdialog_PickerHours, getResources().getColor(R.color.colorAccentDarkThemeTrans));
                dialogFragment.setCustomView(customView);
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
                        Long_ScreenRecordSize = timepickerdialog_PickerHours.getValue();
                        TextView_ScreenRecordSize.setText(ScreenRecordingSizes[(int) Long_ScreenRecordSize]);
                        String size = "default";
                        if (ScreenRecordingSizes[(int) Long_ScreenRecordSize].split("\\(").length > 1) {
                            size = ScreenRecordingSizes[(int) Long_ScreenRecordSize].split("\\(")[1];
                        }
                        MainActivity.preferences.edit().putLong(PreferenceNames.pScreenRecord_Size + "Long", Long_ScreenRecordSize)
                                .putString(PreferenceNames.pScreenRecord_Size, size.replace("px)", "")).commit();
                    }

                    @Override
                    public void onTouchOutside() {

                    }
                });
                dialogFragment.setNegativeButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_OK]);
                dialogFragment.showDialog(R.id.dialog_container);
            }
        });

        Long_ScreenRecordBitRate = MainActivity.preferences.getLong(PreferenceNames.pScreenRecord_BitRate, 4);
        LinearLayout_ScreenRecordBitRate = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_ScreenRecordBitRate);
        TextView_ScreenRecordBitRate = (TextView) InflatedView.findViewById(R.id.activityadvancedTextView_ScreenRecordBitRate);
        TextView_ScreenRecordBitRate.setText(Long_ScreenRecordBitRate + "MBps");

        LinearLayout_ScreenRecordBitRate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                dialogFragment.setContext(getActivity());
                dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                dialogFragment.setText(getString(R.string.advancedPrefs_SelectBitRate));
                View customView = inflater.inflate(R.layout.timepickerdialog, null, false);
                final NumberPicker timepickerdialog_PickerHours = (NumberPicker) customView.findViewById(R.id.timepickerdialog_Hours);
                final NumberPicker timepickerdialog_PickerMinutes = (NumberPicker) customView.findViewById(R.id.timepickerdialog_Minutes);
                timepickerdialog_PickerMinutes.setVisibility(View.GONE);
                final NumberPicker timepickerdialog_PickerSeconds = (NumberPicker) customView.findViewById(R.id.timepickerdialog_Secondss);
                timepickerdialog_PickerSeconds.setVisibility(View.GONE);
                timepickerdialog_PickerHours.setMinValue(0);
                timepickerdialog_PickerHours.setMaxValue(19);
                timepickerdialog_PickerHours.setWrapSelectorWheel(false);
                String[] mbps = new String[20];
                for (int i = 0; i < 20; i++) {
                    mbps[i] = (i + 1) + "MBps";
                }
                timepickerdialog_PickerHours.setDisplayedValues(mbps);
                timepickerdialog_PickerHours.setValue((int) Long_ScreenRecordBitRate - 1);
                helper.setDividerColor(timepickerdialog_PickerHours, getResources().getColor(R.color.colorAccentDarkThemeTrans));
                dialogFragment.setCustomView(customView);
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
                        Long_ScreenRecordBitRate = timepickerdialog_PickerHours.getValue() + 1;
                        TextView_ScreenRecordBitRate.setText(Long_ScreenRecordBitRate + "MBps");
                        MainActivity.preferences.edit().putLong(PreferenceNames.pScreenRecord_BitRate, Long_ScreenRecordBitRate).commit();
                    }

                    @Override
                    public void onTouchOutside() {

                    }
                });
                dialogFragment.setNegativeButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_OK]);
                dialogFragment.showDialog(R.id.dialog_container);
            }
        });

        Long_ScreenRecordTimeLimit = MainActivity.preferences.getLong(PreferenceNames.pScreenRecord_TimeLimit, (1000 * 3) * 60);
        LinearLayout_ScreenRecordTimeLimit = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_ScreenRecordTime);
        TextView_ScreenRecordTimeLimit = (TextView) InflatedView.findViewById(R.id.activityadvancedTextView_ScreenRecordTime);
        if (Long_ScreenRecordTimeLimit == 0) {
            TextView_ScreenRecordTimeLimit.setText(R.string.advancedPrefs_DelayZero);
        } else {
            TextView_ScreenRecordTimeLimit.setText(helper.getTimeString(getActivity(), Long_ScreenRecordTimeLimit, 1));
        }
        if (!boolean_ScreenRecordStockBinary) {
            LinearLayout_ScreenRecordTimeLimit.setEnabled(true);
            LinearLayout_ScreenRecordTimeLimit.setAlpha((float) 1);
        } else {
            LinearLayout_ScreenRecordTimeLimit.setEnabled(false);
            LinearLayout_ScreenRecordTimeLimit.setAlpha((float) .3);
        }

        LinearLayout_ScreenRecordTimeLimit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                dialogFragment.setContext(getActivity());
                dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                dialogFragment.setText(getString(R.string.advancedPrefs_SelectTime));
                View customView = inflater.inflate(R.layout.timepickerdialog, null, false);
                final NumberPicker timepickerdialog_PickerHours = (NumberPicker) customView.findViewById(R.id.timepickerdialog_Hours);
                final NumberPicker timepickerdialog_PickerMinutes = (NumberPicker) customView.findViewById(R.id.timepickerdialog_Minutes);
                final NumberPicker timepickerdialog_PickerSeconds = (NumberPicker) customView.findViewById(R.id.timepickerdialog_Secondss);
                String[] vals = new String[25];
                for (int i = 0; i <= 24; i++) {
                    vals[i] = i + " " + getString(R.string.advancedPrefs_Hours);
                }
                timepickerdialog_PickerHours.setMinValue(0);
                timepickerdialog_PickerHours.setMaxValue(24);
                timepickerdialog_PickerHours.setWrapSelectorWheel(false);
                timepickerdialog_PickerHours.setDisplayedValues(vals);
                timepickerdialog_PickerHours.setValue((int) helper.splitMilliseconds(Long_ScreenRecordTimeLimit, "h"));
                helper.setDividerColor(timepickerdialog_PickerHours, getResources().getColor(R.color.colorAccentDarkThemeTrans));
                vals = new String[61];
                for (int i = 0; i <= 60; i++) {
                    vals[i] = i + " " + getString(R.string.advancedPrefs_Minutes);
                }
                timepickerdialog_PickerMinutes.setMinValue(0);
                timepickerdialog_PickerMinutes.setMaxValue(59);
                timepickerdialog_PickerMinutes.setDisplayedValues(vals);
                timepickerdialog_PickerMinutes.setWrapSelectorWheel(false);
                timepickerdialog_PickerMinutes.setValue((int) helper.splitMilliseconds(Long_ScreenRecordTimeLimit, "m"));
                helper.setDividerColor(timepickerdialog_PickerMinutes, getResources().getColor(R.color.colorAccentDarkThemeTrans));
                vals = new String[61];
                for (int i = 0; i <= 60; i++) {
                    vals[i] = i + " " + getString(R.string.advancedPrefs_Seconds);
                }
                timepickerdialog_PickerSeconds.setMinValue(0);
                timepickerdialog_PickerSeconds.setMaxValue(59);
                timepickerdialog_PickerSeconds.setDisplayedValues(vals);
                timepickerdialog_PickerSeconds.setWrapSelectorWheel(false);
                timepickerdialog_PickerSeconds.setValue((int) helper.splitMilliseconds(Long_ScreenRecordTimeLimit, "s"));
                helper.setDividerColor(timepickerdialog_PickerSeconds, getResources().getColor(R.color.colorAccentDarkThemeTrans));
                dialogFragment.setCustomView(customView);
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
                        long milliseconds = 0;
                        milliseconds += java.util.concurrent.TimeUnit.HOURS.toMillis(timepickerdialog_PickerHours.getValue());//timepickerdialog_PickerHours.getValue() * (1000 * 60);
                        milliseconds += java.util.concurrent.TimeUnit.MINUTES.toMillis(timepickerdialog_PickerMinutes.getValue());//timepickerdialog_PickerMinutes.getValue() * (1000 * 60);
                        milliseconds += java.util.concurrent.TimeUnit.SECONDS.toMillis(timepickerdialog_PickerSeconds.getValue());//timepickerdialog_PickerSeconds.getValue() * 1000;
                        Long_ScreenRecordTimeLimit = milliseconds;
                        if (Long_ScreenRecordTimeLimit == 0) {
                            TextView_ScreenRecordTimeLimit.setText(R.string.advancedPrefs_DelayZero);
                        } else {
                            TextView_ScreenRecordTimeLimit.setText(helper.getTimeString(getActivity(), Long_ScreenRecordTimeLimit, 1));
                        }
                        MainActivity.preferences.edit().putLong(PreferenceNames.pScreenRecord_TimeLimit, Long_ScreenRecordTimeLimit).commit();
                    }

                    @Override
                    public void onTouchOutside() {

                    }
                });
                dialogFragment.setNegativeButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_OK]);
                dialogFragment.showDialog(R.id.dialog_container);
            }
        });

        boolean_ScreenRecordRotate = MainActivity.preferences.getBoolean(PreferenceNames.pScreenRecord_Rotate, false);
        LinearLayout_ScreenRecordRotate = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_ScreenRecordRotate);
        Switch_ScreenRecordRotate = (Switch) InflatedView.findViewById(R.id.activityadvancedSwitch_ScreenRecordRotate);

        Switch_ScreenRecordRotate.setChecked(boolean_ScreenRecordRotate);
        Switch_ScreenRecordRotate.setClickable(false);
        Switch_ScreenRecordRotate.setFocusable(false);

        LinearLayout_ScreenRecordRotate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                boolean_ScreenRecordRotate = !boolean_ScreenRecordRotate;
                MainActivity.preferences.edit().putBoolean(PreferenceNames.pScreenRecord_Rotate, boolean_ScreenRecordRotate).commit();
                Switch_ScreenRecordRotate.setChecked(boolean_ScreenRecordRotate);
            }
        });

        Long_ScreenRecordCountdown = MainActivity.preferences.getLong(PreferenceNames.pScreenRecord_Countdown, 0);
        LinearLayout_ScreenRecordCountdown = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_ScreenRecordCountdown);
        TextView_ScreenRecordCountdown = (TextView) InflatedView.findViewById(R.id.activityadvancedTextView_ScreenRecordCountdown);
        if (Long_ScreenRecordCountdown == 0) {
            TextView_ScreenRecordCountdown.setText(R.string.advancedPrefs_DelayZero);
        } else {
            TextView_ScreenRecordCountdown.setText(helper.getTimeString(getActivity(), Long_ScreenRecordCountdown, 1));
        }

        LinearLayout_ScreenRecordCountdown.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                dialogFragment.setContext(getActivity());
                dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                dialogFragment.setText(getString(R.string.advancedPrefs_SelectTime));
                View customView = inflater.inflate(R.layout.timepickerdialog, null, false);
                final NumberPicker timepickerdialog_PickerHours = (NumberPicker) customView.findViewById(R.id.timepickerdialog_Hours);
                final NumberPicker timepickerdialog_PickerMinutes = (NumberPicker) customView.findViewById(R.id.timepickerdialog_Minutes);
                final NumberPicker timepickerdialog_PickerSeconds = (NumberPicker) customView.findViewById(R.id.timepickerdialog_Secondss);
                String[] vals = new String[25];
                for (int i = 0; i <= 24; i++) {
                    vals[i] = i + " " + getString(R.string.advancedPrefs_Hours);
                }
                timepickerdialog_PickerHours.setMinValue(0);
                timepickerdialog_PickerHours.setMaxValue(24);
                timepickerdialog_PickerHours.setWrapSelectorWheel(false);
                timepickerdialog_PickerHours.setDisplayedValues(vals);
                timepickerdialog_PickerHours.setValue((int) helper.splitMilliseconds(Long_ScreenRecordCountdown, "h"));
                helper.setDividerColor(timepickerdialog_PickerHours, getResources().getColor(R.color.colorAccentDarkThemeTrans));
                vals = new String[61];
                for (int i = 0; i <= 60; i++) {
                    vals[i] = i + " " + getString(R.string.advancedPrefs_Minutes);
                }
                timepickerdialog_PickerMinutes.setMinValue(0);
                timepickerdialog_PickerMinutes.setMaxValue(59);
                timepickerdialog_PickerMinutes.setDisplayedValues(vals);
                timepickerdialog_PickerMinutes.setWrapSelectorWheel(false);
                timepickerdialog_PickerMinutes.setValue((int) helper.splitMilliseconds(Long_ScreenRecordCountdown, "m"));
                helper.setDividerColor(timepickerdialog_PickerMinutes, getResources().getColor(R.color.colorAccentDarkThemeTrans));
                vals = new String[61];
                for (int i = 0; i <= 60; i++) {
                    vals[i] = i + " " + getString(R.string.advancedPrefs_Seconds);
                }
                timepickerdialog_PickerSeconds.setMinValue(0);
                timepickerdialog_PickerSeconds.setMaxValue(59);
                timepickerdialog_PickerSeconds.setDisplayedValues(vals);
                timepickerdialog_PickerSeconds.setWrapSelectorWheel(false);
                timepickerdialog_PickerSeconds.setValue((int) helper.splitMilliseconds(Long_ScreenRecordCountdown, "s"));
                helper.setDividerColor(timepickerdialog_PickerSeconds, getResources().getColor(R.color.colorAccentDarkThemeTrans));
                dialogFragment.setCustomView(customView);
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
                        long milliseconds = 0;
                        milliseconds += java.util.concurrent.TimeUnit.HOURS.toMillis(timepickerdialog_PickerHours.getValue());//timepickerdialog_PickerHours.getValue() * (1000 * 60);
                        milliseconds += java.util.concurrent.TimeUnit.MINUTES.toMillis(timepickerdialog_PickerMinutes.getValue());//timepickerdialog_PickerMinutes.getValue() * (1000 * 60);
                        milliseconds += java.util.concurrent.TimeUnit.SECONDS.toMillis(timepickerdialog_PickerSeconds.getValue());//timepickerdialog_PickerSeconds.getValue() * 1000;
                        Long_ScreenRecordCountdown = milliseconds;
                        if (Long_ScreenRecordCountdown == 0) {
                            TextView_ScreenRecordCountdown.setText(R.string.advancedPrefs_DelayZero);
                        } else {
                            TextView_ScreenRecordCountdown.setText(helper.getTimeString(getActivity(), Long_ScreenRecordCountdown, 1));
                        }
                        MainActivity.preferences.edit().putLong(PreferenceNames.pScreenRecord_Countdown, Long_ScreenRecordCountdown).commit();
                    }

                    @Override
                    public void onTouchOutside() {

                    }
                });
                dialogFragment.setNegativeButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_OK]);
                dialogFragment.showDialog(R.id.dialog_container);
            }
        });

        LinearLayout_Password = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_Password);

        LinearLayout_Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                dialogFragment.setContext(getActivity());
                dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {
                    @Override
                    public void onListItemClick(int position, String text) {

                    }

                    @Override
                    public void onNegativeClick() {
                        dialogFragment.closeDialog();
                    }

                    @Override
                    public void onNeutralClick() {
                    }

                    @Override
                    public void onPositiveClick(Bundle resultBundle) {
                        if (!MainActivity.preferences.getString(PreferenceNames.pItemPWL, "").isEmpty()) {
                            if (helper.md5Crypto(resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0")).equals(MainActivity.preferences.getString(PreferenceNames.pItemPWL, ""))) {
                                MainActivity.preferences.edit().putString(PreferenceNames.pItemPWL, (resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "1", "").isEmpty() ? "" : helper.md5Crypto(resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "1", "")))).commit();
                                dialogFragment.closeDialog();
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.powerMenu_WrongPassword), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            MainActivity.preferences.edit().putString(PreferenceNames.pItemPWL, helper.md5Crypto(resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0", ""))).commit();
                            dialogFragment.closeDialog();
                        }
                        if (mFingerprintManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED) {
                                if (mFingerprintManager.hasEnrolledFingerprints()) {
                                    if (!MainActivity.preferences.getString(PreferenceNames.pItemPWL, "").isEmpty()) {
                                        LinearLayout_LockWithFingerprint.setEnabled(true);
                                        LinearLayout_LockWithFingerprint.setAlpha((float) 1);
                                    } else {
                                        LinearLayout_LockWithFingerprint.setEnabled(false);
                                        LinearLayout_LockWithFingerprint.setAlpha((float) .3);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onTouchOutside() {

                    }
                });
                dialogFragment.setText(getString(R.string.advancedPrefs_ChangePassword));
                int inputCount = 0;
                if (!MainActivity.preferences.getString(PreferenceNames.pItemPWL, "").isEmpty()) {
                    dialogFragment.addInput(getString(R.string.advancedPrefs_OldPassword), "", true, null);
                    dialogFragment.setInputMode(0, InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    inputCount++;
                }
                dialogFragment.addInput(getString(R.string.advancedPrefs_Password), "", true, null);
                dialogFragment.setInputMode(inputCount, InputType.TYPE_TEXT_VARIATION_PASSWORD);
                dialogFragment.setNegativeButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_SAVE]);
                dialogFragment.setCloseOnButtonClick(false);
                dialogFragment.showDialog(R.id.dialog_container);
            }
        });

        boolean_LockWithFingerprint = MainActivity.preferences.getBoolean(PreferenceNames.pLockWithFingerprint, false);
        LinearLayout_LockWithFingerprint = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_LockWithFingerprint);
        Switch_LockWithFingerprint = (Switch) InflatedView.findViewById(R.id.activityadvancedSwitch_LockWithFingerprint);

        Switch_LockWithFingerprint.setChecked(boolean_LockWithFingerprint);
        Switch_LockWithFingerprint.setClickable(false);
        Switch_LockWithFingerprint.setFocusable(false);
        LinearLayout_LockWithFingerprint.setEnabled(false);
        LinearLayout_LockWithFingerprint.setAlpha((float) .3);
        if (mFingerprintManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!MainActivity.preferences.getString(PreferenceNames.pItemPWL, "").isEmpty()) {
                LinearLayout_LockWithFingerprint.setEnabled(true);
                LinearLayout_LockWithFingerprint.setAlpha((float) 1);
            }
        }

        LinearLayout_LockWithFingerprint.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED || !mKeyguardManager.isKeyguardSecure() || !mFingerprintManager.hasEnrolledFingerprints()) {
                        final slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
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
                                try {
                                    Intent intent = new Intent();
                                    intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.SecuritySettings"));
                                    startActivity(intent);
                                } catch (Throwable t) {
                                    Log.e("NPM", "Failed to start settings activity...", t);
                                }
                            }

                            @Override
                            public void onTouchOutside() {
                            }
                        });
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                            dialogFragment.setText(getString(R.string.advancedPrefs_MissingFingerprintPermission) + "\n\n" + getString(R.string.advancedPrefs_ClickOkToOpenSettings));
                        } else if (!mKeyguardManager.isKeyguardSecure()) {
                            dialogFragment.setText(getString(R.string.advancedPrefs_KeyguardNotSecured) + "\n\n" + getString(R.string.advancedPrefs_ClickOkToOpenSettings));
                        } else if (!mFingerprintManager.hasEnrolledFingerprints()) {
                            dialogFragment.setText(getString(R.string.advancedPrefs_NoFingerprintsAdded) + "\n\n" + getString(R.string.advancedPrefs_ClickOkToOpenSettings));
                        }
                        dialogFragment.setNegativeButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                        dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_OK]);
                        dialogFragment.showDialog(R.id.dialog_container);
                        return;
                    }
                    if (boolean_LockWithFingerprint) {
                        final slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                        dialogFragment.setContext(getActivity());
                        dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                        dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {
                            @Override
                            public void onListItemClick(int position, String text) {

                            }

                            @Override
                            public void onNegativeClick() {
                                dialogFragment.closeDialog();
                            }

                            @Override
                            public void onNeutralClick() {
                            }

                            @Override
                            public void onPositiveClick(Bundle resultBundle) {
                                if (helper.md5Crypto(resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0")).equals(MainActivity.preferences.getString(PreferenceNames.pItemPWL, ""))) {
                                    dialogFragment.closeDialog();
                                    boolean_LockWithFingerprint = !boolean_LockWithFingerprint;
                                    MainActivity.preferences.edit().putBoolean(PreferenceNames.pLockWithFingerprint, boolean_LockWithFingerprint).commit();
                                    Switch_LockWithFingerprint.setChecked(boolean_LockWithFingerprint);
                                } else {
                                    Toast.makeText(getActivity(), getString(R.string.powerMenu_WrongPassword), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onTouchOutside() {
                            }
                        });
                        dialogFragment.setText(getString(R.string.visibilityOrder_RemovePWLock));
                        dialogFragment.addInput(getString(R.string.advancedPrefs_Password), "", true, null);
                        dialogFragment.setInputMode(0, InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        dialogFragment.setNegativeButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                        dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_OK]);
                        dialogFragment.setCloseOnButtonClick(false);
                        dialogFragment.showDialog(R.id.dialog_container);
                    } else {
                        fingerprintDialog = new slideDownDialogFragment();
                        fingerprintDialog.setContext(getActivity());
                        fingerprintDialog.setFragmentManager(MainActivity.fragmentManager);
                        fingerprintDialog.setListener(new slideDownDialogFragment.slideDownDialogInterface() {
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
                                mFingerprintHandler.stopAuth();
                                fingerprintDialog.closeDialog();
                            }

                            @Override
                            public void onTouchOutside() {
                                mFingerprintHandler.stopAuth();
                            }
                        });
                        fingerprintDialog.setText(getString(R.string.visibilityOrder_ConfirmFingerprint));
                        ImageView fingerprintImage = new ImageView(getActivity());
                        fingerprintImage.setImageResource(R.drawable.fingerprint);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) helper.convertDpToPixel(55, getActivity()));
                        fingerprintImage.setLayoutParams(params);
                        fingerprintDialog.setCustomView(fingerprintImage);
                        fingerprintDialog.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                        fingerprintDialog.setCloseOnButtonClick(false);

                        mFingerprintHandler = new FingerprintHandler(getActivity());
                        if (mFingerprintHandler.isCipherReady()) {
                            mFingerprintHandler.addInterface(new FingerprintHandler.FingerprintInterface() {
                                @Override
                                public void onFingerprintSuccess(FingerprintManager.AuthenticationResult result) {
                                    fingerprintDialog.closeDialog();
                                    boolean_LockWithFingerprint = true;
                                    MainActivity.preferences.edit().putBoolean(PreferenceNames.pLockWithFingerprint, boolean_LockWithFingerprint).commit();
                                    Switch_LockWithFingerprint.setChecked(boolean_LockWithFingerprint);
                                }

                                @Override
                                public void onFingerprintFailed(int errorCode, String errorMsg) {
                                    if (errorCode == -1) {
                                        if (fingerprintErrorToast != null)
                                            fingerprintErrorToast.cancel();
                                        fingerprintErrorToast = Toast.makeText(getActivity(), "Unknown problem: " + errorMsg, Toast.LENGTH_LONG);
                                        fingerprintErrorToast.show();
                                    } else if (errorCode == FingerprintManager.FINGERPRINT_ERROR_LOCKOUT) {
                                        if (fingerprintErrorToast != null)
                                            fingerprintErrorToast.cancel();
                                        fingerprintErrorToast = Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG);
                                        fingerprintErrorToast.show();
                                        mFingerprintHandler.stopAuth();
                                        fingerprintDialog.cancelDialog();
                                    } else if (errorCode != FingerprintManager.FINGERPRINT_ERROR_CANCELED) {
                                        if (fingerprintErrorToast != null)
                                            fingerprintErrorToast.cancel();
                                        fingerprintErrorToast = Toast.makeText(getActivity(), getString(R.string.powerMenu_FingerprintFailed), Toast.LENGTH_LONG);
                                        fingerprintErrorToast.show();
                                    }
                                }

                                @Override
                                public void onFingerprintHelp(int helpId, String helpString) {
                                    Toast.makeText(getActivity(), helpString, Toast.LENGTH_LONG).show();
                                }
                            });
                            mFingerprintHandler.startAuth(mFingerprintManager);
                        }
                        fingerprintDialog.showDialog(R.id.dialog_container);
                    }
                }
            }
        });

        boolean_BlurBehind = MainActivity.preferences.getBoolean(PreferenceNames.pBlurBehind, false);
        LinearLayout_BlurBehind = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_BlurBehind);
        Switch_BlurBehind = (Switch) InflatedView.findViewById(R.id.activityadvancedSwitch_BlurBehind);

        Switch_BlurBehind.setChecked(boolean_BlurBehind);
        Switch_BlurBehind.setClickable(false);
        Switch_BlurBehind.setFocusable(false);

        LinearLayout_BlurBehind.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                boolean_BlurBehind = !boolean_BlurBehind;
                MainActivity.preferences.edit().putBoolean(PreferenceNames.pBlurBehind, boolean_BlurBehind).commit();
                Switch_BlurBehind.setChecked(boolean_BlurBehind);
                if (boolean_BlurBehind) {
                    LinearLayout_BlurRadius.setEnabled(true);
                    LinearLayout_BlurRadius.setAlpha((float) 1);
                } else {
                    LinearLayout_BlurRadius.setEnabled(false);
                    LinearLayout_BlurRadius.setAlpha((float) .3);
                }
            }
        });

        Float_BlurRadius = MainActivity.preferences.getFloat(PreferenceNames.pBlurRadius, 14f);
        LinearLayout_BlurRadius = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_BlurRadius);
        TextView_BlurRadius = (TextView) InflatedView.findViewById(R.id.activityadvancedTextView_BlurRadius);
        TextView_BlurRadius.setText(((int) Float_BlurRadius) + "");
        if (boolean_BlurBehind) {
            LinearLayout_BlurRadius.setEnabled(true);
            LinearLayout_BlurRadius.setAlpha((float) 1);
        } else {
            LinearLayout_BlurRadius.setEnabled(false);
            LinearLayout_BlurRadius.setAlpha((float) .3);
        }

        LinearLayout_BlurRadius.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                dialogFragment.setContext(getActivity());
                dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                dialogFragment.setText(getString(R.string.advancedPrefs_SelectBlurRadius));
                View customView = inflater.inflate(R.layout.timepickerdialog, null, false);
                final NumberPicker timepickerdialog_PickerHours = (NumberPicker) customView.findViewById(R.id.timepickerdialog_Hours);
                final NumberPicker timepickerdialog_PickerMinutes = (NumberPicker) customView.findViewById(R.id.timepickerdialog_Minutes);
                timepickerdialog_PickerMinutes.setVisibility(View.GONE);
                final NumberPicker timepickerdialog_PickerSeconds = (NumberPicker) customView.findViewById(R.id.timepickerdialog_Secondss);
                timepickerdialog_PickerSeconds.setVisibility(View.GONE);
                timepickerdialog_PickerHours.setMinValue(0);
                timepickerdialog_PickerHours.setMaxValue(25);
                timepickerdialog_PickerHours.setWrapSelectorWheel(false);
                timepickerdialog_PickerHours.setValue((int) Float_BlurRadius);
                helper.setDividerColor(timepickerdialog_PickerHours, getResources().getColor(R.color.colorAccentDarkThemeTrans));
                dialogFragment.setCustomView(customView);
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
                        Float_BlurRadius = timepickerdialog_PickerHours.getValue();
                        TextView_BlurRadius.setText(((int) Float_BlurRadius) + "");
                        MainActivity.preferences.edit().putFloat(PreferenceNames.pBlurRadius, Float_BlurRadius).commit();
                    }

                    @Override
                    public void onTouchOutside() {

                    }
                });
                dialogFragment.setNegativeButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_OK]);
                dialogFragment.showDialog(R.id.dialog_container);
            }
        });

        boolean_Confirmation = MainActivity.preferences.getBoolean(PreferenceNames.pRequireConfirmation, false);
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
                MainActivity.preferences.edit().putBoolean(PreferenceNames.pRequireConfirmation, boolean_Confirmation).commit();
            }
        });

        boolean_HideOnClick = MainActivity.preferences.getBoolean(PreferenceNames.pHideOnClick, false);
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
                MainActivity.preferences.edit().putBoolean(PreferenceNames.pHideOnClick, boolean_HideOnClick).commit();
            }
        });

        boolean_UseRoot = MainActivity.preferences.getBoolean(PreferenceNames.pUseRoot, true);
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
                MainActivity.preferences.edit().putBoolean(PreferenceNames.pUseRoot, boolean_UseRoot).commit();
            }
        });

        ExperimentalPWMHook = MainActivity.preferences.getBoolean(PreferenceNames.pExperimentalPWMHook, false);
        LinearLayout_ExperimentalPWMHook = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_ExperimentalPWMHook);
        Switch_ExperimentalPWMHook = (Switch) InflatedView.findViewById(R.id.activityadvancedSwitch_ExperimentalPWMHook);

        Switch_ExperimentalPWMHook.setChecked(ExperimentalPWMHook);
        Switch_ExperimentalPWMHook.setClickable(false);
        Switch_ExperimentalPWMHook.setFocusable(false);

        LinearLayout_ExperimentalPWMHook.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                ExperimentalPWMHook = !ExperimentalPWMHook;
                MainActivity.preferences.edit().putBoolean(PreferenceNames.pExperimentalPWMHook, ExperimentalPWMHook).commit();
                Switch_ExperimentalPWMHook.setChecked(ExperimentalPWMHook);
            }
        });

        boolean_ShowOnLockScreen = MainActivity.preferences.getBoolean(PreferenceNames.pShowOnLockscreen, true);
        LinearLayout_ShowOnLockScreen = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_ShowOnLockscreen);
        Switch_ShowOnLockScreen = (Switch) InflatedView.findViewById(R.id.activityadvancedSwitch_ShowOnLockScreen);

        Switch_ShowOnLockScreen.setChecked(boolean_ShowOnLockScreen);
        Switch_ShowOnLockScreen.setClickable(false);
        Switch_ShowOnLockScreen.setFocusable(false);

        LinearLayout_ShowOnLockScreen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                boolean_ShowOnLockScreen = !boolean_ShowOnLockScreen;
                MainActivity.preferences.edit().putBoolean(PreferenceNames.pShowOnLockscreen, boolean_ShowOnLockScreen).commit();
                Switch_ShowOnLockScreen.setChecked(boolean_ShowOnLockScreen);
            }
        });

        Long_ScreenshotDelay = MainActivity.preferences.getLong(PreferenceNames.pScreenshotDelay, 1000);
        LinearLayout_ScreenshotDelay = (LinearLayout) InflatedView.findViewById(R.id.activityadvancedLinearLayout_ScreenshotDelay);
        TextView_ScreenshotDelayTime = (TextView) InflatedView.findViewById(R.id.activityadvancedTextView_ScreenshotDelayTime);
        if (Long_ScreenshotDelay == 0) {
            TextView_ScreenshotDelayTime.setText(R.string.advancedPrefs_DelayZero);
        } else {
            TextView_ScreenshotDelayTime.setText(helper.getTimeString(getActivity(), Long_ScreenshotDelay, 1));
        }

        LinearLayout_ScreenshotDelay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                dialogFragment.setContext(getActivity());
                dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                dialogFragment.setText(getString(R.string.advancedPrefs_SelectTime));
                View customView = inflater.inflate(R.layout.timepickerdialog, null, false);
                final NumberPicker timepickerdialog_PickerHours = (NumberPicker) customView.findViewById(R.id.timepickerdialog_Hours);
                final NumberPicker timepickerdialog_PickerMinutes = (NumberPicker) customView.findViewById(R.id.timepickerdialog_Minutes);
                final NumberPicker timepickerdialog_PickerSeconds = (NumberPicker) customView.findViewById(R.id.timepickerdialog_Secondss);
                String[] vals = new String[25];
                for (int i = 0; i <= 24; i++) {
                    vals[i] = i + " " + getString(R.string.advancedPrefs_Hours);
                }
                timepickerdialog_PickerHours.setMinValue(0);
                timepickerdialog_PickerHours.setMaxValue(24);
                timepickerdialog_PickerHours.setWrapSelectorWheel(false);
                timepickerdialog_PickerHours.setDisplayedValues(vals);
                timepickerdialog_PickerHours.setValue((int) helper.splitMilliseconds(Long_ScreenshotDelay, "h"));
                helper.setDividerColor(timepickerdialog_PickerHours, getResources().getColor(R.color.colorAccentDarkThemeTrans));
                vals = new String[61];
                for (int i = 0; i <= 60; i++) {
                    vals[i] = i + " " + getString(R.string.advancedPrefs_Minutes);
                }
                timepickerdialog_PickerMinutes.setMinValue(0);
                timepickerdialog_PickerMinutes.setMaxValue(59);
                timepickerdialog_PickerMinutes.setDisplayedValues(vals);
                timepickerdialog_PickerMinutes.setWrapSelectorWheel(false);
                timepickerdialog_PickerMinutes.setValue((int) helper.splitMilliseconds(Long_ScreenshotDelay, "m"));
                helper.setDividerColor(timepickerdialog_PickerMinutes, getResources().getColor(R.color.colorAccentDarkThemeTrans));
                vals = new String[61];
                for (int i = 0; i <= 60; i++) {
                    vals[i] = i + " " + getString(R.string.advancedPrefs_Seconds);
                }
                timepickerdialog_PickerSeconds.setMinValue(0);
                timepickerdialog_PickerSeconds.setMaxValue(59);
                timepickerdialog_PickerSeconds.setDisplayedValues(vals);
                timepickerdialog_PickerSeconds.setWrapSelectorWheel(false);
                timepickerdialog_PickerSeconds.setValue((int) helper.splitMilliseconds(Long_ScreenshotDelay, "s"));
                helper.setDividerColor(timepickerdialog_PickerSeconds, getResources().getColor(R.color.colorAccentDarkThemeTrans));
                dialogFragment.setCustomView(customView);
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
                        long milliseconds = 0;
                        milliseconds += java.util.concurrent.TimeUnit.HOURS.toMillis(timepickerdialog_PickerHours.getValue());//timepickerdialog_PickerHours.getValue() * (1000 * 60);
                        milliseconds += java.util.concurrent.TimeUnit.MINUTES.toMillis(timepickerdialog_PickerMinutes.getValue());//timepickerdialog_PickerMinutes.getValue() * (1000 * 60);
                        milliseconds += java.util.concurrent.TimeUnit.SECONDS.toMillis(timepickerdialog_PickerSeconds.getValue());//timepickerdialog_PickerSeconds.getValue() * 1000;
                        Long_ScreenshotDelay = milliseconds;
                        if (Long_ScreenshotDelay == 0) {
                            TextView_ScreenshotDelayTime.setText(R.string.advancedPrefs_DelayZero);
                        } else {
                            TextView_ScreenshotDelayTime.setText(helper.getTimeString(getActivity(), Long_ScreenshotDelay, 1));
                        }
                        MainActivity.preferences.edit().putLong(PreferenceNames.pScreenshotDelay, Long_ScreenshotDelay).commit();
                    }

                    @Override
                    public void onTouchOutside() {

                    }
                });
                dialogFragment.setNegativeButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_OK]);
                dialogFragment.showDialog(R.id.dialog_container);
            }
        });

        return InflatedView;
    }

}

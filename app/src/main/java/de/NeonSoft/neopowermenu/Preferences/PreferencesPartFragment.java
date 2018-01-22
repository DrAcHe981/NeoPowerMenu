package de.NeonSoft.neopowermenu.Preferences;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.R;
import de.NeonSoft.neopowermenu.helpers.*;
import de.NeonSoft.neopowermenu.permissionsScreen.*;
import eu.chainfire.libsuperuser.*;

import android.support.v4.app.Fragment;

public class PreferencesPartFragment extends Fragment {

    /**
     * PayPal
     */
    final String PAYPAL_USER = "drache981@neon-soft.de";
    final String PAYPAL_CURRENCY_CODE = "EUR";

    Context mContext;
    Activity mActivity;

    String Urlgithub = "https://github.com/DrAcHe981/NeoPowerMenu";

    int ActiveStyleId = 0;

    boolean hideicon = false;
    boolean DeepXposedLogging = false;

    View InflatedView;

    public static ScrollView scrollView;

    LinearLayout LinearLayout_ModuleState;
    ProgressBar ProgressBar_RootWait;
    TextView TextView_ModuleStateTitle;
    TextView TextView_ModuleStateDesc;

    LinearLayout LinearLayout_Style;
    TextView TextView_StyleTitle;
    TextView TextView_StyleDesc;

    LinearLayout LinearLayout_Theme;
    TextView TextView_ThemeTitle;
    TextView TextView_ThemeDesc;

    LinearLayout LinearLayout_Graphics;
    TextView TextView_GraphicsTitle;
    TextView TextView_GraphicsDesc;

    LinearLayout LinearLayout_VisibilityOrder;
    TextView TextView_VisibilityOrderTitle;
    TextView TextView_VisibilityOrderDesc;

    LinearLayout LinearLayout_Animations;
    TextView TextView_AnimationsTitle;
    TextView TextView_AnimationsDesc;

    LinearLayout LinearLayout_DialogGravity;

    LinearLayout LinearLayout_Account;
    TextView TextView_AccountTitle;
    TextView TextView_AccountDesc;

    LinearLayout LinearLayout_Advanced;
    TextView TextView_AdvancedTitle;
    TextView TextView_AdvancedDesc;

    LinearLayout LinearLayout_Permissions;
    TextView TextView_PermissionsTitle;
    TextView TextView_PermissionsDesc;

    LinearLayout LinearLayout_BackupRestore;

    LinearLayout LinearLayout_HideLauncherIcon;
    TextView TextView_HideLauncherIconTitle;
    TextView TextView_HideLauncherIconDesc;
    Switch Switch_HideLauncherIcon;

    LinearLayout LinearLayout_DeepXposedLogging;
    TextView TextView_DeepXposedLoggingTitle;
    TextView TextView_DeepXposedLoggingDesc;
    Switch Switch_DeepXposedLogging;

    LinearLayout LinearLayout_Donate;
    TextView TextView_DonateTitle;
    TextView TextView_DonateDesc;

    LinearLayout LinearLayout_Source;
    TextView TextView_SourceTitle;
    TextView TextView_SourceDesc;

    LinearLayout LinearLayout_Translate;

    LinearLayout LinearLayout_Share;
    TextView TextView_ShareTitle;
    TextView TextView_ShareDesc;

    LinearLayout LinearLayout_About;
    TextView TextView_AboutTitle;
    TextView TextView_AboutDesc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!MainActivity.visibleFragment.equalsIgnoreCase("tour")) {
            MainActivity.visibleFragment = "Main";
        }
        final String[] styleList = {getString(R.string.style_Material),
            getString(R.string.style_MaterialFullscreen),
            getString(R.string.style_MaterialFullHorizontal)//,
            //getString(R.string.style_MaterialDynamicWidth)
        };

        mContext = getActivity();
        mActivity = getActivity();

        if (MainActivity.preferences.getBoolean("autoLogin", false) && !MainActivity.loggedIn) {
            LoginFragment.performLogin(getActivity(), MainActivity.preferences.getString("ueel", "null"), MainActivity.preferences.getString("pd", "null"), true, true);
        }

        MainActivity.actionbar.setTitle("NeoPowerMenu");
        MainActivity.actionbar.setSubTitle("v" + MainActivity.versionName + " (" + MainActivity.versionCode + ")" + (MainActivity.LOCALTESTSERVER ? " | Using local test Server" : ""));

        if (MainActivity.orderPrefs.getAll().isEmpty()) {
            MainActivity.orderPrefs.edit().putInt("0_item_type", visibilityOrder_ListAdapter.TYPE_NORMAL).apply();
            MainActivity.orderPrefs.edit().putString("0_item_title", "Shutdown").apply();
            MainActivity.orderPrefs.edit().putInt("1_item_type", visibilityOrder_ListAdapter.TYPE_NORMAL).apply();
            MainActivity.orderPrefs.edit().putString("1_item_title", "Reboot").apply();
            MainActivity.orderPrefs.edit().putInt("2_item_type", visibilityOrder_ListAdapter.TYPE_NORMAL).apply();
            MainActivity.orderPrefs.edit().putString("2_item_title", "SoftReboot").apply();
            MainActivity.orderPrefs.edit().putInt("3_item_type", visibilityOrder_ListAdapter.TYPE_MULTI).apply();
            MainActivity.orderPrefs.edit().putString("3_item1_title", "Recovery").apply();
            MainActivity.orderPrefs.edit().putString("3_item2_title", "Bootloader").apply();
            MainActivity.orderPrefs.edit().putString("3_item3_title", "SafeMode").apply();
        }
        for (int i = 0; i < PreferencesColorFragment.ColorNames.length; i++) {
            if ((int) PreferencesColorFragment.ColorNames[i][0] == ColorsListAdapter.TYPE_ITEM) {
                if (MainActivity.colorPrefs.getString(PreferencesColorFragment.ColorNames[i][1].toString(), "").isEmpty()) {
                    //Log.d("NPM","["+i+"]> Setting initial color for "+PreferencesColorFragment.ColorNames[i][1].toString()+" with the value "+PreferencesColorFragment.lightPreset[i]);
                    MainActivity.colorPrefs.edit().putString(PreferencesColorFragment.ColorNames[i][1].toString(), MainActivity.preferences.getString(PreferencesColorFragment.ColorNames[i][1].toString(), PreferencesColorFragment.lightPreset[i])).apply();
                    MainActivity.preferences.edit().remove(PreferencesColorFragment.ColorNames[i][1].toString()).commit();
                }
            }
        }
        for (int i = 0; i < PreferencesAnimationsFragment.names.length; i++) {
            if ((int) PreferencesAnimationsFragment.names[i][0] == animationsAdapter.TYPE_HEADER) {
                if (PreferencesAnimationsFragment.names[i + PreferencesAnimationsFragment.anim_Type][1].toString().contains("type") && MainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[i + PreferencesAnimationsFragment.anim_Type][1].toString(), -1) == -1) {
                    //Log.d("NPM","["+i+"]> Setting initial animation for "+PreferencesAnimationsFragment.names[i][1].toString()+" with the value "+PreferencesAnimationsFragment.defaultTypes[i + PreferencesAnimationsFragment.anim_Type]);
                    MainActivity.animationPrefs.edit().putInt(PreferencesAnimationsFragment.names[i + PreferencesAnimationsFragment.anim_Type][1].toString(), PreferencesAnimationsFragment.defaultTypes[i + PreferencesAnimationsFragment.anim_Type]).apply();
                    MainActivity.animationPrefs.edit().putInt(PreferencesAnimationsFragment.names[i + PreferencesAnimationsFragment.anim_Interpolator][1].toString(), PreferencesAnimationsFragment.defaultTypes[i + PreferencesAnimationsFragment.anim_Interpolator]).apply();
                    MainActivity.animationPrefs.edit().putInt(PreferencesAnimationsFragment.names[i + PreferencesAnimationsFragment.anim_Speed][1].toString(), PreferencesAnimationsFragment.defaultTypes[i + PreferencesAnimationsFragment.anim_Speed]).apply();
                }
            }
        }

        ActiveStyleId = MainActivity.preferences.getInt("DialogThemeId", 0);
        hideicon = MainActivity.preferences.getBoolean("HideLauncherIcon", false);
        DeepXposedLogging = MainActivity.preferences.getBoolean("DeepXposedLogging", false);

        InflatedView = inflater.inflate(R.layout.activity_preferences, container, false);

        scrollView = (ScrollView) InflatedView.findViewById(R.id.scrollView);

        LinearLayout_ModuleState = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_ModuleState);
        ProgressBar_RootWait = (ProgressBar) InflatedView.findViewById(R.id.activitypreferencesProgressBar_ModuleState);
        if(!MainActivity.RootAvailable) {
            ProgressBar_RootWait.setProgress(100);
            Animation BlinkAnim = AnimationUtils.loadAnimation(mActivity, R.anim.progress_blink);
            ProgressBar_RootWait.setAlpha((float) 1);
            ProgressBar_RootWait.startAnimation(BlinkAnim);
        } else {
            ProgressBar_RootWait.setVisibility(View.GONE);
        }
        TextView_ModuleStateTitle = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_ModuleStateTitle);
        TextView_ModuleStateDesc = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_ModuleStateDesc);

        LinearLayout_Style = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_Style);
        TextView_StyleTitle = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_StyleTitle);
        TextView_StyleDesc = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_StyleDesc);
        TextView_StyleDesc.setText(styleList[Math.min(ActiveStyleId, styleList.length-1)]);

        LinearLayout_Theme = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_Theme);
        TextView_ThemeTitle = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_ThemeTitle);
        TextView_ThemeDesc = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_ThemeDesc);
        TextView_ThemeTitle.setText(getString(R.string.preferences_Theme).split("\\|")[0]);
        TextView_ThemeDesc.setText(getString(R.string.preferences_Theme).split("\\|")[1]);

        LinearLayout_Graphics = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_Graphics);
        TextView_GraphicsTitle = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_GraphicsTitle);
        TextView_GraphicsDesc = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_GraphicsDesc);
        TextView_GraphicsTitle.setText(getString(R.string.preferences_Graphics).split("\\|")[0]);
        TextView_GraphicsDesc.setText(getString(R.string.preferences_Graphics).split("\\|")[1]);

        LinearLayout_VisibilityOrder = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_VisibilityOrder);
        TextView_VisibilityOrderTitle = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_VisibilityOrderTitle);
        TextView_VisibilityOrderDesc = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_VisibilityOrderDesc);
        TextView_VisibilityOrderTitle.setText(getString(R.string.preferences_VisibilityOrder).split("\\|")[0]);
        TextView_VisibilityOrderDesc.setText(getString(R.string.preferences_VisibilityOrder).split("\\|")[1]);

        LinearLayout_Animations = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_Animations);
        TextView_AnimationsTitle = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_AnimationsTitle);
        TextView_AnimationsDesc = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_AnimationsDesc);
        TextView_AnimationsTitle.setText(getString(R.string.preferences_Animations).split("\\|")[0]);
        TextView_AnimationsDesc.setText(getString(R.string.preferences_Animations).split("\\|")[1]);
        //inearLayout_Animations.setAlpha((float) .3);
        //LinearLayout_Animations.setEnabled(false);

        LinearLayout_DialogGravity = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_DialogPosition);

        LinearLayout_Account = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_Account);
        TextView_AccountTitle = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_AccountTitle);
        TextView_AccountDesc = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_AccountDesc);
        TextView_AccountTitle.setText(getString(R.string.preferences_Account).split("\\|")[0]);
        TextView_AccountDesc.setText(getString(R.string.preferences_Account).split("\\|")[1]);

        LinearLayout_Advanced = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_Advanced);
        TextView_AdvancedTitle = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_AdvancedTitle);
        TextView_AdvancedDesc = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_AdvancedDesc);
        TextView_AdvancedTitle.setText(getString(R.string.preferences_Advanced).split("\\|")[0]);
        TextView_AdvancedDesc.setText(getString(R.string.preferences_Advanced).split("\\|")[1]);

        LinearLayout_Permissions = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_Permissions);
        TextView_PermissionsTitle = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_PermissionsTitle);
        TextView_PermissionsDesc = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_PermissionsDesc);
        TextView_PermissionsTitle.setText(getString(R.string.preferences_Permissions).split("\\|")[0]);
        TextView_PermissionsDesc.setText(getString(R.string.preferences_Permissions).split("\\|")[1]);

        LinearLayout_BackupRestore = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_BackupRestore);

        LinearLayout_HideLauncherIcon = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_HideLauncherIcon);
        TextView_HideLauncherIconTitle = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_HideLauncherIconTitle);
        TextView_HideLauncherIconDesc = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_HideLauncherIconDesc);
        TextView_HideLauncherIconTitle.setText(getString(R.string.preferences_HideLauncherIcon).split("\\|")[0]);
        TextView_HideLauncherIconDesc.setText(getString(R.string.preferences_HideLauncherIcon).split("\\|")[1]);
        Switch_HideLauncherIcon = (Switch) InflatedView.findViewById(R.id.activitypreferencesSwitch_HideLauncherIcon);
        Switch_HideLauncherIcon.setChecked(hideicon);
        Switch_HideLauncherIcon.setClickable(false);
        Switch_HideLauncherIcon.setFocusable(false);

        LinearLayout_DeepXposedLogging = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_DeepXposedLogging);
        TextView_DeepXposedLoggingTitle = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_DeepXposedLoggingTitle);
        TextView_DeepXposedLoggingDesc = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_DeepXposedLoggingDesc);
        TextView_DeepXposedLoggingTitle.setText(getString(R.string.preferences_DeepXposedLogging).split("\\|")[0]);
        TextView_DeepXposedLoggingDesc.setText(getString(R.string.preferences_DeepXposedLogging).split("\\|")[1]);
        Switch_DeepXposedLogging = (Switch) InflatedView.findViewById(R.id.activitypreferencesSwitch_DeepXposedLogging);
        Switch_DeepXposedLogging.setChecked(DeepXposedLogging);
        Switch_DeepXposedLogging.setClickable(false);
        Switch_DeepXposedLogging.setFocusable(false);

        LinearLayout_Donate = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_Donate);
        TextView_DonateTitle = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_DonateTitle);
        TextView_DonateDesc = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_DonateDesc);
        TextView_DonateTitle.setText(getString(R.string.preferences_Donate).split("\\|")[0]);
        TextView_DonateDesc.setText(getString(R.string.preferences_Donate).split("\\|")[1]);

        LinearLayout_Source = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_Source);
        TextView_SourceTitle = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_SourceTitle);
        TextView_SourceDesc = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_SourceDesc);
        TextView_SourceTitle.setText(getString(R.string.preferences_NeoSource).split("\\|")[0]);
        TextView_SourceDesc.setText(getString(R.string.preferences_NeoSource).split("\\|")[1]);

        LinearLayout_Translate = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_Translate);

        LinearLayout_Share = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_Share);
        TextView_ShareTitle = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_ShareTitle);
        TextView_ShareDesc = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_ShareDesc);
        TextView_ShareTitle.setText(getString(R.string.preferences_Share).split("\\|")[0]);
        TextView_ShareDesc.setText(getString(R.string.preferences_Share).split("\\|")[1]);

        LinearLayout_About = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_About);
        TextView_AboutTitle = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_AboutTitle);
        TextView_AboutDesc = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_AboutDesc);
        TextView_AboutTitle.setText(getString(R.string.preferences_About).split("\\|")[0]);
        TextView_AboutDesc.setText(getString(R.string.preferences_About).split("\\|")[1]);

        LinearLayout_ModuleState.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (helper.ModuleState()==-1) {
                    try {
                        Intent viewIntent = new Intent();
                        viewIntent.setClassName("de.robv.android.xposed.installer", "de.robv.android.xposed.installer.WelcomeActivity");
                        viewIntent.putExtra("fragment", 1);
                        viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(viewIntent);
                    } catch (Throwable e) {
                        slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                        dialogFragment.setContext(mActivity);
                        dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                        dialogFragment.setText("Failed to open the Xposed installer, application not found.");
                        dialogFragment.showDialog(R.id.dialog_container);
                    }
                } else if (!MainActivity.RootAvailable) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            helper.setThreadPrio(MainActivity.BG_PRIO);

                            if (!Shell.SU.available()) {
                                slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                                dialogFragment.setContext(mActivity);
                                dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                                dialogFragment.setText("Failed to execute root request, device is not rooted or the request got rejected.");
                                dialogFragment.showDialog(R.id.dialog_container);
                            } else {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mActivity != null) {
                                            mActivity.runOnUiThread(new Runnable() {

                                                @Override
                                                public void run() {
                                                    rootAvailable();
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    }).start();
                }
            }
        });

        LinearLayout_Style.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {

                slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                dialogFragment.setContext(mActivity);
                dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

                    @Override
                    public void onListItemClick(int position, String text) {

                        MainActivity.preferences.edit().putInt("DialogThemeId", position).commit();
                        TextView_StyleDesc.setText(styleList[position]);
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
                dialogFragment.setList(ListView.CHOICE_MODE_SINGLE, styleList, MainActivity.preferences.getInt("DialogThemeId",0), true);
                dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[0]);
                dialogFragment.showDialog(R.id.dialog_container);
            }
        });

        LinearLayout_Theme.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                MainActivity.changePrefPage(new PreferencesColorFragment(), false);
            }
        });

        LinearLayout_Graphics.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                MainActivity.changePrefPage(new PreferencesGraphicsFragment(), false);
            }
        });

        LinearLayout_VisibilityOrder.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                MainActivity.changePrefPage(new PreferencesVisibilityOrderFragment(), false);
            }
        });

        LinearLayout_Animations.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                MainActivity.changePrefPage(new PreferencesAnimationsFragment(), false);
            }
        });

        LinearLayout_DialogGravity.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                MainActivity.changePrefPage(new GravityChooserDialog(), false);
            }
        });

        LinearLayout_Account.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {

                PreferencesPresetsFragment ppf = new PreferencesPresetsFragment();
                ppf.setStartTab(0);
                MainActivity.changePrefPage(ppf, false);
                MainActivity.visibleFragment = "PresetsManagerAccount";
                if (LoginFragment.loginFragmentMode.equalsIgnoreCase("login")) {
                    MainActivity.actionbar.setButton(getString(R.string.login_Title), R.drawable.ic_action_import, LoginFragment.loginOnClickListener);
                } else if (LoginFragment.loginFragmentMode.equalsIgnoreCase("register")) {
                    MainActivity.actionbar.setButton(getString(R.string.login_TitleRegister), R.drawable.ic_action_import, LoginFragment.registerOnClickListener);
                } else if (MainActivity.loggedIn) {
                    MainActivity.actionbar.setButton(getString(R.string.login_TitleLogout), R.drawable.ic_action_export, LoginFragment.logoutOnClickListener);
                } else if (LoginFragment.loginFragmentMode.equalsIgnoreCase("recover")) {
                    MainActivity.actionbar.setButton(getString(R.string.login_Recover), R.drawable.ic_action_settings_backup_restore, LoginFragment.recoverOnClickListener);
                }
            }
        });

        LinearLayout_Advanced.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                MainActivity.changePrefPage(new PreferencesAdvancedFragment(), false);
            }
        });

        LinearLayout_Permissions.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                Bundle args = new Bundle();
                args.putBoolean("AutoStart", false);
                permissionsScreen permScreen = new permissionsScreen();
                permScreen.setArguments(args);
                MainActivity.changePrefPage(permScreen, false);
            }
        });

        LinearLayout_BackupRestore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.changePrefPage(new PreferencesBackupRestore(), false);
            }
        });

        LinearLayout_HideLauncherIcon.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                hideicon = !hideicon;
                String packageName = getActivity().getPackageName();
                ComponentName componentSettings = new ComponentName(packageName, packageName + ".SettingsActivity");
                if (!hideicon) {
                    getActivity().getPackageManager().setComponentEnabledSetting(componentSettings, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                } else {
                    getActivity().getPackageManager().setComponentEnabledSetting(componentSettings, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                }
                Switch_HideLauncherIcon.setChecked(hideicon);
                MainActivity.preferences.edit().putBoolean("HideLauncherIcon", hideicon).commit();
            }
        });

        LinearLayout_DeepXposedLogging.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                DeepXposedLogging = !DeepXposedLogging;
                Switch_DeepXposedLogging.setChecked(DeepXposedLogging);
                MainActivity.preferences.edit().putBoolean("DeepXposedLogging", DeepXposedLogging).commit();
            }
        });

        LinearLayout_Donate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                dialogFragment.setContext(mActivity);
                dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

                    @Override
                    public void onListItemClick(int position, String text) {

                        if (position == 0) {
                            Uri.Builder uriBuilder = new Uri.Builder();
                            uriBuilder.scheme("https").authority("www.paypal.com").path("cgi-bin/webscr");
                            uriBuilder.appendQueryParameter("cmd", "_donations");

                            uriBuilder.appendQueryParameter("business", PAYPAL_USER);
                            uriBuilder.appendQueryParameter("lc", "US");
                            uriBuilder.appendQueryParameter("item_name", "NeoPowerMenu - Create your own Power Menu!");
                            uriBuilder.appendQueryParameter("no_note", "1");
                            // uriBuilder.appendQueryParameter("no_note", "0");
                            // uriBuilder.appendQueryParameter("cn", "Note to the developer");
                            uriBuilder.appendQueryParameter("no_shipping", "1");
                            uriBuilder.appendQueryParameter("currency_code", PAYPAL_CURRENCY_CODE);
                            Uri payPalUri = uriBuilder.build();

                            try {
                                Intent viewIntent = new Intent(Intent.ACTION_VIEW, payPalUri);
                                startActivity(viewIntent);
                            } catch (ActivityNotFoundException e) {
                                slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                                dialogFragment.setContext(mActivity);
                                dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                                dialogFragment.setText("Failed to donate, no application found to handle the request.");
                                dialogFragment.showDialog(R.id.dialog_container);
                            }
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
                dialogFragment.setList(ListView.CHOICE_MODE_NONE, new String[]{"PayPal"}, -1, true);
                dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                dialogFragment.showDialog(R.id.dialog_container);
            }
        });

        LinearLayout_Translate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://crowdin.com/project/neopowermenu"));
                try {
                    startActivity(i);
                } catch (ActivityNotFoundException e) {
                    slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                    dialogFragment.setContext(mActivity);
                    dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                    dialogFragment.setText("Failed, no application found to handle the request.");
                    dialogFragment.showDialog(R.id.dialog_container);
                }
            }
        });

        LinearLayout_Source.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(Urlgithub));
                try {
                    startActivity(i);
                } catch (ActivityNotFoundException e) {
                    slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                    dialogFragment.setContext(mActivity);
                    dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                    dialogFragment.setText("Failed, no application found to handle the request.");
                    dialogFragment.showDialog(R.id.dialog_container);
                }
            }
        });

        LinearLayout_Share.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "NeoPowerMenu");
                String sAux = getString(R.string.ShareMessage);
                sAux = sAux + "repo.xposed.info/module/de.NeonSoft.neopowermenu \n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                try {
                    startActivity(Intent.createChooser(i, getString(R.string.preferences_Share).split("\\|")[0]));
                } catch (ActivityNotFoundException e) {
                    slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                    dialogFragment.setContext(mActivity);
                    dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                    dialogFragment.setText("Failed, no application found to handle the request.");
                    dialogFragment.showDialog(R.id.dialog_container);
                }
            }
        });

        LinearLayout_About.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {

                MainActivity.changePrefPage(new AboutFragment(), false);
            }
        });

        checkState();

        if (!MainActivity.visibleFragment.equalsIgnoreCase("tour")) {
            MainActivity.actionbar.setButton(getString(R.string.PreviewPowerMenu), R.drawable.ic_action_launch, MainActivity.previewOnClickListener);
        }

        return InflatedView;
    }

    void checkState() {
        if (isAdded()) {
            if (helper.ModuleState() >= MainActivity.neededModuleActiveVersion) {
                if (!MainActivity.RootAvailable) {
                    TextView_ModuleStateTitle.setText(getString(R.string.preferences_RootXposed2).split("\\|")[0]);
                    TextView_ModuleStateDesc.setText(getString(R.string.preferences_RootXposed2).split("\\|")[1]);
                } else {
                    TextView_ModuleStateTitle.setText(getString(R.string.preferences_RootXposed4).split("\\|")[0]);
                    TextView_ModuleStateDesc.setText(getString(R.string.preferences_RootXposed4).split("\\|")[1]);
                    //ProgressBar_RootWait.startAnimation(MainActivity.anim_fade_out);
                    ProgressBar_RootWait.clearAnimation();
                    ProgressBar_RootWait.setVisibility(View.GONE);
                }
            } else if (helper.ModuleState() == -1) {
                if (!MainActivity.RootAvailable) {
                    TextView_ModuleStateTitle.setText(getString(R.string.preferences_RootXposed1).split("\\|")[0]);
                    TextView_ModuleStateDesc.setText(getString(R.string.preferences_RootXposed1).split("\\|")[1]);
                } else {
                    TextView_ModuleStateTitle.setText(getString(R.string.preferences_RootXposed3).split("\\|")[0]);
                    TextView_ModuleStateDesc.setText(getString(R.string.preferences_RootXposed3).split("\\|")[1]);
                    //ProgressBar_RootWait.startAnimation(MainActivity.anim_fade_out);
                    ProgressBar_RootWait.clearAnimation();
                    ProgressBar_RootWait.setVisibility(View.GONE);
                }
            } else {
                TextView_ModuleStateTitle.setText(getString(R.string.preferences_RootXposed5).split("\\|")[0]);
                TextView_ModuleStateDesc.setText(getString(R.string.preferences_RootXposed5).split("\\|")[1]);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    TextView_ModuleStateTitle.setTextColor(getResources().getColor(R.color.colorAccentDarkTheme, null));
                    TextView_ModuleStateDesc.setTextColor(getResources().getColor(R.color.colorAccentDarkTheme, null));
                } else {
                    TextView_ModuleStateTitle.setTextColor(getResources().getColor(R.color.colorAccentDarkTheme));
                    TextView_ModuleStateDesc.setTextColor(getResources().getColor(R.color.colorAccentDarkTheme));
                }
                //ProgressBar_RootWait.startAnimation(MainActivity.anim_fade_out);
                ProgressBar_RootWait.clearAnimation();
                ProgressBar_RootWait.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        if (!MainActivity.RootAvailable) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    helper.setThreadPrio(MainActivity.BG_PRIO);

                    if (Shell.SU.available()) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (mActivity != null) {
                                    mActivity.runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            rootAvailable();
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }).start();
        }

    }

    void rootAvailable() {
        MainActivity.RootAvailable = true;
        checkState();
    }

}

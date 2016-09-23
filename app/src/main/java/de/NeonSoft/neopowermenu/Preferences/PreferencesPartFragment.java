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
import android.widget.*;

import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.*;
import de.NeonSoft.neopowermenu.permissionsScreen.*;
import eu.chainfire.libsuperuser.*;

import android.support.v4.app.Fragment;

public class PreferencesPartFragment extends Fragment {

    /**
     * PayPal
     */
    public static final String PAYPAL_USER = "drache981@gmail.com";
    public static final String PAYPAL_CURRENCY_CODE = "EUR";

    Context mContext;
    public static Activity mActivity;

    private String Urlgithub = "https://github.com/DrAcHe981/NeoPowerMenu";

    private String ActiveStyle = "Material";
    private int ActiveStyleId = 0;

    private boolean hideicon = false;
    private boolean DeepXposedLogging = false;

    private View InflatedView;

    private static ProgressBar ProgressBar_RootWait;
    private static TextView TextView_ModuleStateTitle;
    private static TextView TextView_ModuleStateDesc;

    private static LinearLayout LinearLayout_Style;
    private static TextView TextView_StyleTitle;
    private static TextView TextView_StyleDesc;

    private static LinearLayout LinearLayout_Theme;
    private static TextView TextView_ThemeTitle;
    private static TextView TextView_ThemeDesc;

    private static LinearLayout LinearLayout_Graphics;
    private static TextView TextView_GraphicsTitle;
    private static TextView TextView_GraphicsDesc;

    private static LinearLayout LinearLayout_VisibilityOrder;
    private static TextView TextView_VisibilityOrderTitle;
    private static TextView TextView_VisibilityOrderDesc;

    private static LinearLayout LinearLayout_Animations;
    private static TextView TextView_AnimationsTitle;
    private static TextView TextView_AnimationsDesc;

    private static LinearLayout LinearLayout_Account;
    private static TextView TextView_AccountTitle;
    private static TextView TextView_AccountDesc;

    private static LinearLayout LinearLayout_Advanced;
    private static TextView TextView_AdvancedTitle;
    private static TextView TextView_AdvancedDesc;

    private static LinearLayout LinearLayout_Permissions;
    private static TextView TextView_PermissionsTitle;
    private static TextView TextView_PermissionsDesc;

    private static LinearLayout LinearLayout_HideLauncherIcon;
    private static TextView TextView_HideLauncherIconTitle;
    private static TextView TextView_HideLauncherIconDesc;
    private static Switch Switch_HideLauncherIcon;

    private static LinearLayout LinearLayout_DeepXposedLogging;
    private static TextView TextView_DeepXposedLoggingTitle;
    private static TextView TextView_DeepXposedLoggingDesc;
    private static Switch Switch_DeepXposedLogging;

    private static LinearLayout LinearLayout_Donate;
    private static TextView TextView_DonateTitle;
    private static TextView TextView_DonateDesc;

    private static LinearLayout LinearLayout_Source;
    private static TextView TextView_SourceTitle;
    private static TextView TextView_SourceDesc;

    private static LinearLayout LinearLayout_Share;
    private static TextView TextView_ShareTitle;
    private static TextView TextView_ShareDesc;

    private static LinearLayout LinearLayout_About;
    private static TextView TextView_AboutTitle;
    private static TextView TextView_AboutDesc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: Implement this method
        if(!MainActivity.visibleFragment.equalsIgnoreCase("tour")) {
            MainActivity.visibleFragment = "Main";
        }

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
                    MainActivity.colorPrefs.edit().putString(PreferencesColorFragment.ColorNames[i][1].toString(), MainActivity.preferences.getString(PreferencesColorFragment.ColorNames[i][1].toString(), PreferencesColorFragment.lightPreset[i])).apply();
                    MainActivity.preferences.edit().remove(PreferencesColorFragment.ColorNames[i][1].toString()).apply();
                }
            }
        }
        for (int i = 0; i < PreferencesAnimationsFragment.names.length; i++) {
            if ((int) PreferencesAnimationsFragment.names[i][0] == animationsAdapter.TYPE_ITEM) {
                if(PreferencesAnimationsFragment.names[i][1].toString().contains("type") && MainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[i][1].toString(), -1) == -1) {
                    MainActivity.animationPrefs.edit().putInt(PreferencesAnimationsFragment.names[i][1].toString(), PreferencesAnimationsFragment.defaultTypes[i]).apply();
                    MainActivity.animationPrefs.edit().putInt(PreferencesAnimationsFragment.names[i+1][1].toString(), 3).apply();
                }
            }
        }

        ActiveStyle = MainActivity.preferences.getString("DialogTheme", "Material");
        hideicon = MainActivity.preferences.getBoolean("HideLauncherIcon", false);
        DeepXposedLogging = MainActivity.preferences.getBoolean("DeepXposedLogging", false);

        InflatedView = inflater.inflate(R.layout.activity_preferences, container, false);

        ProgressBar_RootWait = (ProgressBar) InflatedView.findViewById(R.id.activitypreferencesProgressBar_ModuleState);
        TextView_ModuleStateTitle = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_ModuleStateTitle);
        TextView_ModuleStateDesc = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_ModuleStateDesc);

        LinearLayout_Style = (LinearLayout) InflatedView.findViewById(R.id.activitypreferencesLinearLayout_Style);
        TextView_StyleTitle = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_StyleTitle);
        TextView_StyleDesc = (TextView) InflatedView.findViewById(R.id.activitypreferencesTextView_StyleDesc);
        TextView_StyleDesc.setText(ActiveStyle);

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

        LinearLayout_Style.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                // TODO: Implement this method
                String[] styleList = new String[3];
                styleList[0] = getString(R.string.style_Material);
                styleList[1] = getString(R.string.style_MaterialFullscreen);
                styleList[2] = getString(R.string.style_MaterialFullHorizontal);
                for (int i = 0; i < styleList.length; i++) {
                    if (styleList[i].equalsIgnoreCase(ActiveStyle)) {
                        ActiveStyleId = i;
                        //presetsList[i] = "(Active) "+ presetsFiles[i].getName().split(".nps")[0];
                    }
                }
                slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                dialogFragment.setContext(mActivity);
                dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

                    @Override
                    public void onListItemClick(int position, String text) {
                        // TODO: Implement this method
                        MainActivity.preferences.edit().putString("DialogTheme", text).apply();
                        ActiveStyle = text;
                        TextView_StyleDesc.setText(ActiveStyle);
                    }

                    @Override
                    public void onNegativeClick() {
                        // TODO: Implement this method
                    }

                    @Override
                    public void onNeutralClick() {
                        // TODO: Implement this method
                    }

                    @Override
                    public void onPositiveClick(Bundle resultBundle) {
                        // TODO: Implement this method
                    }

                    @Override
                    public void onTouchOutside() {
                        // TODO: Implement this method
                    }
                });
                dialogFragment.setList(ListView.CHOICE_MODE_SINGLE, styleList, ActiveStyleId, true);
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

        LinearLayout_Account.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                // TODO: Implement this method
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
                MainActivity.preferences.edit().putBoolean("HideLauncherIcon", hideicon).apply();
            }
        });

        LinearLayout_DeepXposedLogging.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                DeepXposedLogging = !DeepXposedLogging;
                Switch_DeepXposedLogging.setChecked(DeepXposedLogging);
                MainActivity.preferences.edit().putBoolean("DeepXposedLogging", DeepXposedLogging).apply();
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
                        // TODO: Implement this method
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
                        // TODO: Implement this method
                    }

                    @Override
                    public void onNeutralClick() {
                        // TODO: Implement this method
                    }

                    @Override
                    public void onPositiveClick(Bundle resultBundle) {
                        // TODO: Implement this method
                    }

                    @Override
                    public void onTouchOutside() {
                        // TODO: Implement this method
                    }
                });
                dialogFragment.setList(ListView.CHOICE_MODE_NONE, new String[]{"PayPal"}, -1, true);
                dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                dialogFragment.showDialog(R.id.dialog_container);
            }
        });

        LinearLayout_Source.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                // TODO: Implement this method
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(Urlgithub));
                startActivity(i);
            }
        });

        LinearLayout_Share.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                // TODO: Implement this method
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "NeoPowerMenu");
                String sAux = getString(R.string.ShareMessage);
                sAux = sAux + "repo.xposed.info/module/de.NeonSoft.neopowermenu \n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, getString(R.string.preferences_Share).split("\\|")[0]));
            }
        });

        LinearLayout_About.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                // TODO: Implement this method
                MainActivity.changePrefPage(new AboutFragment(), false);
            }
        });

        checkState();

        if(!MainActivity.visibleFragment.equalsIgnoreCase("tour")) {
            MainActivity.actionbar.setButton(getString(R.string.PreviewPowerMenu), R.drawable.ic_action_launch, MainActivity.previewOnClickListener);
        }

        return InflatedView;
    }

    private void checkState() {
        if (isAdded()) {
            try {
                if (helper.ModuleState() >= MainActivity.neededModuleActiveVersion) {
                    if (!MainActivity.RootAvailable) {
                        TextView_ModuleStateTitle.setText(getString(R.string.preferences_RootXposed2).split("\\|")[0]);
                        TextView_ModuleStateDesc.setText(getString(R.string.preferences_RootXposed2).split("\\|")[1]);
                    } else {
                        TextView_ModuleStateTitle.setText(getString(R.string.preferences_RootXposed4).split("\\|")[0]);
                        TextView_ModuleStateDesc.setText(getString(R.string.preferences_RootXposed4).split("\\|")[1]);
                        //ProgressBar_RootWait.startAnimation(MainActivity.anim_fade_out);
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
                        ProgressBar_RootWait.setVisibility(View.GONE);
                                        /*slideDownDialogFragment dialogFragment = new slideDownDialogFragment(getActivity(), MainActivity.fragmentManager);
										//dialogFragment.set
										dialogFragment.setDialogText(getString(R.string.preferencesDesc_RootXposed3));
										dialogFragment.setDialogNegativeButton(getString(R.string.Dialog_Ignore));
										dialogFragment.setDialogPositiveButton(getString(R.string.Dialog_Ok));
										dialogFragment.showDialog();*/
                    }
                } else {
                    TextView_ModuleStateTitle.setText(getString(R.string.preferences_RootXposed5).split("\\|")[0]);
                    TextView_ModuleStateDesc.setText(getString(R.string.preferences_RootXposed5).split("\\|")[1]);
                    TextView_ModuleStateTitle.setTextColor(getResources().getColor(R.color.colorAccentDarkTheme));
                    TextView_ModuleStateDesc.setTextColor(getResources().getColor(R.color.colorAccentDarkTheme));
                    //ProgressBar_RootWait.startAnimation(MainActivity.anim_fade_out);
                    ProgressBar_RootWait.setVisibility(View.GONE);
                }
            } catch (Throwable t) {
                TextView_ModuleStateTitle.setText(getString(R.string.preferences_RootXposed5).split("\\|")[0]);
                TextView_ModuleStateDesc.setText(getString(R.string.preferences_RootXposed5).split("\\|")[1]);
                TextView_ModuleStateTitle.setTextColor(getResources().getColor(R.color.colorAccentDarkTheme));
                TextView_ModuleStateDesc.setTextColor(getResources().getColor(R.color.colorAccentDarkTheme));
                //ProgressBar_RootWait.startAnimation(MainActivity.anim_fade_out);
                ProgressBar_RootWait.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO: Implement this method
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

    private void rootAvailable() {
        if (TextView_ModuleStateTitle != null) {
            MainActivity.RootAvailable = true;
            checkState();
        }
    }

}

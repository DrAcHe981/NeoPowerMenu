package de.NeonSoft.neopowermenu.Preferences;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.widget.*;

import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.*;

import java.io.*;
import java.util.*;

import android.support.v4.app.Fragment;

import org.acra.*;

import android.text.*;

public class LoginFragment extends Fragment {

    static Activity mContext;
    public static String loginFragmentMode = "login";

    static TextView TextView_AccountInfo;

    // LoginContainer
    static LinearLayout LinearLayout_LoginContainer;
    static EditText EditText_UsernameEmail, EditText_Password;
    boolean boolean_ShowPW = false;
    static ImageView ImageView_ShowHidePw;
    static CheckBox CheckBox_KeepLogin;
    static LinearLayout LinearLayout_CreateAccount;
    static LinearLayout LinearLayout_RecoverButton;

    // RegisterContainer
    static LinearLayout LinearLayout_RegisterContainer;
    static EditText EditText_Username, EditText_Email, EditText_RetypeEmail;

    // RecoverAccountContainer
    static LinearLayout LinearLayout_RecoverAccountContainer;
    static EditText EditText_RecoverUsername;
    static EditText EditText_RecoverEmail;

    // LoggedInContainer
    static LinearLayout LinearLayout_LoggedInContainer;
    static TextView TextView_TitleStatistics, TextView_Statistics, TextView_ReloadStatistics;

    public static OnClickListener loginOnClickListener, registerOnClickListener, recoverOnClickListener, logoutOnClickListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        mContext = getActivity();

        //loginFragmentMode = "login";
        View InflatedView = inflater.inflate(R.layout.activity_login, container, false);

        TextView_AccountInfo = (TextView) InflatedView.findViewById(R.id.activityloginTextView_AccountInfo);

        // LoginContainer
        LinearLayout_LoginContainer = (LinearLayout) InflatedView.findViewById(R.id.activityloginLinearLayout_LoginContainer);
        LinearLayout_LoginContainer.setVisibility(View.VISIBLE);
        EditText_UsernameEmail = (EditText) InflatedView.findViewById(R.id.activityloginEditText_UsernameEmail);
        EditText_Password = (EditText) InflatedView.findViewById(R.id.activityloginEditText_Password);
        ImageView_ShowHidePw = (ImageView) InflatedView.findViewById(R.id.activityloginImageView_ShowHidePW);
        CheckBox_KeepLogin = (CheckBox) InflatedView.findViewById(R.id.activityloginCheckBox_KeepLogin);
        LinearLayout_CreateAccount = (LinearLayout) InflatedView.findViewById(R.id.activityloginLinearLayout_CreateAccount);
        LinearLayout_RecoverButton = (LinearLayout) InflatedView.findViewById(R.id.activityloginLinearLayout_RecoverButton);

        // RegisterContainer
        LinearLayout_RegisterContainer = (LinearLayout) InflatedView.findViewById(R.id.activityloginLinearLayout_RegisterContainer);
        LinearLayout_RegisterContainer.setVisibility(View.GONE);
        EditText_Username = (EditText) InflatedView.findViewById(R.id.activityloginEditText_Username);
        EditText_Email = (EditText) InflatedView.findViewById(R.id.activityloginEditText_Email);
        EditText_RetypeEmail = (EditText) InflatedView.findViewById(R.id.activityloginEditText_RetypeEmail);

        // RecoverAccountContainer
        LinearLayout_RecoverAccountContainer = (LinearLayout) InflatedView.findViewById(R.id.activityloginLinearLayout_RecoverAccount);
        LinearLayout_RecoverAccountContainer.setVisibility(View.GONE);
        EditText_RecoverUsername = (EditText) InflatedView.findViewById(R.id.activityloginEditText_RecoverUsername);
        EditText_RecoverEmail = (EditText) InflatedView.findViewById(R.id.activityloginEditText_RecoverEmail);

        // LoggedInContainer
        LinearLayout_LoggedInContainer = (LinearLayout) InflatedView.findViewById(R.id.activityloginLinearLayout_LoggedInContainer);
        LinearLayout_LoggedInContainer.setVisibility(View.GONE);
        TextView_TitleStatistics = (TextView) InflatedView.findViewById(R.id.activityloginTextView_TitleStatistics);
        TextView_Statistics = (TextView) InflatedView.findViewById(R.id.activityloginTextView_Statistics);
        TextView_ReloadStatistics = (TextView) InflatedView.findViewById(R.id.activityloginTextView_ReloadStatistics);

        loginOnClickListener = new OnClickListener() {

            @Override
            public void onClick(View p1) {
                
                if (EditText_UsernameEmail.getText().toString().isEmpty() || EditText_Password.getText().toString().isEmpty()) {
                    Toast.makeText(mContext, getString(R.string.login_pleaseCheckInput), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (EditText_UsernameEmail.getText().toString().contains("@")) {
                    if (!helper.isValidEmail(EditText_UsernameEmail.getText().toString())) {
                        Toast.makeText(mContext, getString(R.string.login_pleaseCheckInput), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                performLogin(mContext, EditText_UsernameEmail.getText().toString(), helper.md5Crypto(EditText_Password.getText().toString()), CheckBox_KeepLogin.isChecked(), false);
            }
        };

        registerOnClickListener = new OnClickListener() {

            @Override
            public void onClick(View p1) {
                
                if (EditText_Username.getText().toString().isEmpty() || (!helper.isValidEmail(EditText_Email.getText().toString()) || !helper.isValidEmail(EditText_RetypeEmail.getText().toString())) || !EditText_Email.getText().toString().equals(EditText_RetypeEmail.getText().toString())) {
                    Toast.makeText(getActivity(), getString(R.string.login_pleaseCheckInput), Toast.LENGTH_SHORT).show();
                    return;
                }
                uploadHelper uH = new uploadHelper(getActivity());
                uH.setInterface(new uploadHelper.uploadHelperInterface() {

                    @Override
                    public void onStateChanged(int state) {
                        
                        if (state == uploadHelper.STATE_WAITING) {
                            PreferencesPresetsFragment.LoadingMsg.setText(getString(R.string.login_Processing));
                            PreferencesPresetsFragment.progressHolder.setVisibility(View.VISIBLE);
                            PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
                        }
                    }

                    @Override
                    public void onPublishUploadProgress(long nowSize, long totalSize) {
                        
                    }

                    @Override
                    public void onUploadComplete(String response) {
                        
                        PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                        PreferencesPresetsFragment.progressHolder.setVisibility(View.GONE);
                        Toast.makeText(mContext, getString(R.string.login_RegisterSuccess), Toast.LENGTH_SHORT).show();
                        LinearLayout_RegisterContainer.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                        LinearLayout_RegisterContainer.setVisibility(View.GONE);
                        EditText_UsernameEmail.setText(EditText_Username.getText().toString());
                        LinearLayout_LoginContainer.setVisibility(View.VISIBLE);
                        LinearLayout_LoginContainer.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
                        MainActivity.actionbar.setButtonText(getString(R.string.login_Title));
                        MainActivity.actionbar.setButtonListener(loginOnClickListener);
                        loginFragmentMode = "login";
                    }

                    @Override
                    public void onUploadFailed(String reason) {
                        
                        PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                        PreferencesPresetsFragment.progressHolder.setVisibility(View.GONE);
                        if (reason.contains("this username or email is already in use")) {
                            Toast.makeText(mContext, getString(R.string.login_RegisterFailedNameUsed), Toast.LENGTH_LONG).show();
                        } else if (reason.contains("Cannot connect to the DB")) {
                            Toast.makeText(mContext, getString(R.string.presetsManager_CantConnecttoDB), Toast.LENGTH_LONG).show();
                        } else if (reason.contains("Connection refused") || reason.contains("Unable to resolve host")) {
                            Toast.makeText(mContext, getString(R.string.presetsManager_CantConnecttoServer), Toast.LENGTH_LONG).show();
                        } else {
                            slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                            dialogFragment.setContext(mContext);
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
                            dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[0]);
                            dialogFragment.showDialog(R.id.dialog_container);
                        }
                    }
                });
                uH.setServerUrl("http" + (MainActivity.LOCALTESTSERVER ? "" : "s") + "://" + (MainActivity.LOCALTESTSERVER ? "127.0.0.1:8080" : "www.Neon-Soft.de") + "/page/NeoPowerMenu/phpWebservice/webservice3.php");
                uH.setAdditionalUploadPosts(new String[][]{{"action", "register"}, {"name", EditText_Username.getText().toString()}, {"email", EditText_Email.getText().toString()}});
                try {
                    new File(mContext.getFilesDir().getPath() + "/tmp").createNewFile();
                } catch (IOException e) {
                }
                uH.setLocalUrl(mContext.getFilesDir().getPath() + "/tmp");
                uH.setAllowMultiple(true);
                uH.startUpload();
            }
        };

        recoverOnClickListener = new OnClickListener() {

            @Override
            public void onClick(View p1) {
                
                if (EditText_RecoverUsername.getText().toString().isEmpty() || !helper.isValidEmail(EditText_RecoverEmail.getText().toString())) {
                    Toast.makeText(getActivity(), getString(R.string.login_pleaseCheckInput), Toast.LENGTH_SHORT).show();
                    return;
                }
                uploadHelper uH = new uploadHelper(getActivity());
                uH.setInterface(new uploadHelper.uploadHelperInterface() {

                    @Override
                    public void onStateChanged(int state) {
                        
                        if (state == uploadHelper.STATE_WAITING) {
                            PreferencesPresetsFragment.LoadingMsg.setText(getString(R.string.login_Processing));
                            PreferencesPresetsFragment.progressHolder.setVisibility(View.VISIBLE);
                            PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
                        }
                    }

                    @Override
                    public void onPublishUploadProgress(long nowSize, long totalSize) {
                        
                    }

                    @Override
                    public void onUploadComplete(String response) {
                        
                        PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                        PreferencesPresetsFragment.progressHolder.setVisibility(View.GONE);
                        Toast.makeText(mContext, getString(R.string.login_RecoverSuccess), Toast.LENGTH_SHORT).show();
                        LinearLayout_RecoverAccountContainer.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                        LinearLayout_RecoverAccountContainer.setVisibility(View.GONE);
                        EditText_UsernameEmail.setText(EditText_RecoverUsername.getText().toString());
                        LinearLayout_LoginContainer.setVisibility(View.VISIBLE);
                        LinearLayout_LoginContainer.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
                        MainActivity.actionbar.setButtonText(getString(R.string.login_Title));
                        MainActivity.actionbar.setButtonListener(loginOnClickListener);
                        MainActivity.actionbar.setButtonIcon(R.drawable.ic_action_import);
                        loginFragmentMode = "login";
                    }

                    @Override
                    public void onUploadFailed(String reason) {
                        
                        PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                        PreferencesPresetsFragment.progressHolder.setVisibility(View.GONE);
                        if (reason.contains("cant find any user with this data")) {
                            Toast.makeText(mContext, getString(R.string.login_RecoverNotFound), Toast.LENGTH_LONG).show();
                        } else if (reason.contains("Cannot connect to the DB")) {
                            Toast.makeText(mContext, getString(R.string.presetsManager_CantConnecttoDB), Toast.LENGTH_LONG).show();
                        } else if (reason.contains("Connection refused") || reason.contains("Unable to resolve host")) {
                            Toast.makeText(mContext, getString(R.string.presetsManager_CantConnecttoServer), Toast.LENGTH_LONG).show();
                        } else {
                            slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                            dialogFragment.setContext(mContext);
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
                            dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[0]);
                            dialogFragment.showDialog(R.id.dialog_container);
                        }
                    }
                });
                uH.setServerUrl("http" + (MainActivity.LOCALTESTSERVER ? "" : "s") + "://" + (MainActivity.LOCALTESTSERVER ? "127.0.0.1:8080" : "www.Neon-Soft.de") + "/page/NeoPowerMenu/phpWebservice/webservice3.php");
                uH.setAdditionalUploadPosts(new String[][]{{"action", "resetpw"}, {"name", EditText_RecoverUsername.getText().toString()}, {"email", EditText_RecoverEmail.getText().toString()}});
                try {
                    new File(mContext.getFilesDir().getPath() + "/tmp").createNewFile();
                } catch (IOException e) {
                }
                uH.setLocalUrl(mContext.getFilesDir().getPath() + "/tmp");
                uH.setAllowMultiple(true);
                uH.startUpload();
            }

        };

        logoutOnClickListener = new OnClickListener() {

            @Override
            public void onClick(View p1) {
                
                MainActivity.loggedIn = false;
                MainActivity.usernameemail = "null";
                MainActivity.password = "null";
                MainActivity.accountUniqeId = "none";
                MainActivity.userRank = "U";
                MainActivity.preferences.edit().putBoolean("autoLogin", false)
                        .remove("ueel")
                        .remove("pd")
                        .remove("auid").apply();
                LinearLayout_LoggedInContainer.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                LinearLayout_LoggedInContainer.setVisibility(View.GONE);
                LinearLayout_LoginContainer.setVisibility(View.VISIBLE);
                LinearLayout_LoginContainer.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
                TextView_AccountInfo.setVisibility(View.VISIBLE);
                TextView_AccountInfo.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
                MainActivity.actionbar.setButton(mContext.getString(R.string.login_Title), R.drawable.ic_action_import, loginOnClickListener);
                loginFragmentMode = "login";
            }
        };

        //checkState();

        if (MainActivity.preferences.getBoolean("autoLogin", false)) {
            EditText_UsernameEmail.setText(MainActivity.preferences.getString("ueel", "null"));
            CheckBox_KeepLogin.setChecked(true);
        }

        ImageView_ShowHidePw.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                
                int cursorpos = EditText_Password.getSelectionStart();
                if (boolean_ShowPW) {
                    boolean_ShowPW = false;
                    ImageView_ShowHidePw.setImageResource(R.drawable.ic_action_eye_closed);
                    EditText_Password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    boolean_ShowPW = true;
                    ImageView_ShowHidePw.setImageResource(R.drawable.ic_action_eye_open);
                    EditText_Password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                }
                EditText_Password.setSelection(cursorpos);
            }
        });

        LinearLayout_CreateAccount.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                
                loginFragmentMode = "register";
                MainActivity.actionbar.setButtonText(getString(R.string.login_TitleRegister));
                MainActivity.actionbar.setButtonListener(registerOnClickListener);
                LinearLayout_LoginContainer.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                LinearLayout_LoginContainer.setVisibility(View.GONE);
                EditText_Username.setText("");
                EditText_Email.setText("");
                EditText_RetypeEmail.setText("");
                if (!EditText_UsernameEmail.getText().toString().isEmpty() && helper.isValidEmail(EditText_UsernameEmail.getText().toString())) {
                    EditText_Email.setText(EditText_UsernameEmail.getText().toString());
                } else if (!EditText_UsernameEmail.getText().toString().isEmpty()) {
                    EditText_Username.setText(EditText_UsernameEmail.getText().toString());
                }
                LinearLayout_RegisterContainer.setVisibility(View.VISIBLE);
                LinearLayout_RegisterContainer.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
            }
        });

        LinearLayout_RecoverButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                
                loginFragmentMode = "recover";
                MainActivity.actionbar.setButton(getString(R.string.login_Recover), R.drawable.ic_action_settings_backup_restore, recoverOnClickListener);
                LinearLayout_LoginContainer.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                LinearLayout_LoginContainer.setVisibility(View.GONE);
                EditText_RecoverUsername.setText("");
                EditText_RecoverEmail.setText("");
                if (!EditText_UsernameEmail.getText().toString().isEmpty() && helper.isValidEmail(EditText_UsernameEmail.getText().toString())) {
                    EditText_RecoverEmail.setText(EditText_UsernameEmail.getText().toString());
                } else if (!EditText_UsernameEmail.getText().toString().isEmpty()) {
                    EditText_RecoverUsername.setText(EditText_UsernameEmail.getText().toString());
                }
                LinearLayout_RecoverAccountContainer.setVisibility(View.VISIBLE);
                LinearLayout_RecoverAccountContainer.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
            }
        });

        TextView_ReloadStatistics.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                
                getStatistics();
            }
        });

        if (MainActivity.loggedIn) {
            loginFragmentMode = "logout";
        }

        if (PreferencesPresetsFragment.startTab == 0) PreferencesPresetsFragment.checkPage();

        return InflatedView;
    }

    public static void checkState() {
        if (MainActivity.loggedIn) {
            loginFragmentMode = "logout";
            if (TextView_AccountInfo != null && TextView_AccountInfo.getVisibility() == View.VISIBLE)
                TextView_AccountInfo.setVisibility(View.GONE);
            if (LinearLayout_LoginContainer != null && LinearLayout_LoginContainer.getVisibility() == View.VISIBLE)
                LinearLayout_LoginContainer.setVisibility(View.GONE);
            TextView_TitleStatistics.setText(mContext.getString(R.string.login_TitleStatistics).replace("[USERNAMEEMAIL]", MainActivity.userName));
            getStatistics();
            if (LinearLayout_LoggedInContainer.getVisibility() == View.GONE)
                LinearLayout_LoggedInContainer.setVisibility(View.VISIBLE);
            //MainActivity.actionbar.setActionBarButton(getString(R.string.login_TitleLogout), R.drawable.ic_content_send, logoutOnClickListener);
        } else if (loginFragmentMode.equalsIgnoreCase("register")) {
            loginFragmentMode = "register";
            if (TextView_AccountInfo != null && TextView_AccountInfo.getVisibility() == View.GONE)
                TextView_AccountInfo.setVisibility(View.VISIBLE);
            if (LinearLayout_RegisterContainer != null && LinearLayout_RegisterContainer.getVisibility() == View.GONE)
                LinearLayout_RegisterContainer.setVisibility(View.VISIBLE);
            if (LinearLayout_LoginContainer != null && LinearLayout_LoginContainer.getVisibility() == View.VISIBLE)
                LinearLayout_LoginContainer.setVisibility(View.GONE);
            //MainActivity.actionbar.setActionBarButton(getString(R.string.login_Title), R.drawable.ic_content_send, loginOnClickListener);
        } else if (loginFragmentMode.equalsIgnoreCase("recover")) {
            loginFragmentMode = "recover";
            if (TextView_AccountInfo != null && TextView_AccountInfo.getVisibility() == View.GONE)
                TextView_AccountInfo.setVisibility(View.VISIBLE);
            if (LinearLayout_RecoverAccountContainer != null && LinearLayout_RecoverAccountContainer.getVisibility() == View.GONE)
                LinearLayout_RecoverAccountContainer.setVisibility(View.VISIBLE);
            if (LinearLayout_LoginContainer != null && LinearLayout_LoginContainer.getVisibility() == View.VISIBLE)
                LinearLayout_LoginContainer.setVisibility(View.GONE);
            //MainActivity.actionbar.setActionBarButton(getString(R.string.login_Title), R.drawable.ic_content_send, loginOnClickListener);
        } else {
            loginFragmentMode = "login";
            if (TextView_AccountInfo != null && TextView_AccountInfo.getVisibility() == View.GONE)
                TextView_AccountInfo.setVisibility(View.VISIBLE);
            if (LinearLayout_LoginContainer != null && LinearLayout_LoginContainer.getVisibility() == View.GONE)
                LinearLayout_LoginContainer.setVisibility(View.VISIBLE);
            if (LinearLayout_LoggedInContainer != null && LinearLayout_LoggedInContainer.getVisibility() == View.VISIBLE)
                LinearLayout_LoggedInContainer.setVisibility(View.GONE);
            //MainActivity.actionbar.setActionBarButton(getString(R.string.login_Title), R.drawable.ic_content_send, loginOnClickListener);
        }
    }

    public static void performLogin(final Activity context, final String usernameemail, final String password, final boolean keeplogin, final boolean background) {

        uploadHelper uH = new uploadHelper(context);
        uH.setInterface(new uploadHelper.uploadHelperInterface() {

            @Override
            public void onStateChanged(int state) {
                
                if (!background) {
                    if (state == uploadHelper.STATE_WAITING) {
                        MainActivity.loggingIn = true;
                        PreferencesPresetsFragment.LoadingMsg.setText(context.getString(R.string.login_Processing));
                        PreferencesPresetsFragment.progressHolder.setVisibility(View.VISIBLE);
                        PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
                    }
                }
            }

            @Override
            public void onPublishUploadProgress(long nowSize, long totalSize) {
                
            }

            @Override
            public void onUploadComplete(String response) {
                
                MainActivity.loggingIn = false;
                String[] responseSplit = response.split(",");
                if (!background) {
                    PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                    PreferencesPresetsFragment.progressHolder.setVisibility(View.GONE);
                }
                Toast.makeText(context, context.getString(R.string.login_LoggedIn), Toast.LENGTH_SHORT).show();
                MainActivity.loggedIn = true;
                MainActivity.usernameemail = usernameemail;
                MainActivity.password = password;
                MainActivity.accountUniqeId = (responseSplit.length >= 2 && !responseSplit[3].isEmpty() ? responseSplit[1] : "none");
                MainActivity.userRank = (responseSplit.length >= 3 && !responseSplit[3].isEmpty() ? responseSplit[2] : "U");
                MainActivity.userName = (responseSplit.length >= 4 && !responseSplit[3].isEmpty() ? responseSplit[3] : "< unknown >");
                if (keeplogin) {
                    MainActivity.preferences.edit().putBoolean("autoLogin", true)
                            .putString("ueel", MainActivity.usernameemail)
                            .putString("pd", MainActivity.password)
                            .putString("auid", MainActivity.accountUniqeId).apply();
                }
                if (LinearLayout_LoginContainer != null) {
                    TextView_AccountInfo.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                    TextView_AccountInfo.setVisibility(View.GONE);
                    LinearLayout_LoginContainer.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                    LinearLayout_LoginContainer.setVisibility(View.GONE);
                    TextView_TitleStatistics.setText(context.getString(R.string.login_TitleStatistics).replace("[USERNAMEEMAIL]", MainActivity.userName));
                    getStatistics();
                    LinearLayout_LoggedInContainer.setVisibility(View.VISIBLE);
                    LinearLayout_LoggedInContainer.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
                    loginFragmentMode = "logout";
                }
            }

            @Override
            public void onUploadFailed(String reason) {
                
                MainActivity.loggingIn = false;
                if (!background) {
                    PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                    PreferencesPresetsFragment.progressHolder.setVisibility(View.GONE);
                    if (reason.contains("user with this data not found")) {
                        Toast.makeText(context, context.getString(R.string.login_LoginFailedWrongData), Toast.LENGTH_LONG).show();
                    } else if (reason.contains("Cannot connect to the DB")) {
                        Toast.makeText(context, context.getString(R.string.presetsManager_CantConnecttoDB), Toast.LENGTH_LONG).show();
                    } else if (reason.contains("Connection refused") || reason.contains("Unable to resolve host")) {
                        Toast.makeText(context, context.getString(R.string.presetsManager_CantConnecttoServer), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, context.getString(R.string.login_LoginFailedWithReason) + "\n" + reason, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        uH.setServerUrl("http" + (MainActivity.LOCALTESTSERVER ? "" : "s") + "://" + (MainActivity.LOCALTESTSERVER ? "127.0.0.1:8080" : "www.Neon-Soft.de") + "/page/NeoPowerMenu/phpWebservice/webservice3.php");
        uH.setAdditionalUploadPosts(new String[][]{{"action", "login"}, {(helper.isValidEmail(usernameemail) ? "email" : "name"), usernameemail}, {"password", password}});
        try {
            new File(context.getFilesDir().getPath() + "/tmp").createNewFile();
        } catch (IOException e) {
        }
        uH.setLocalUrl(context.getFilesDir().getPath() + "/tmp");
        uH.setAllowMultiple(true);
        uH.startUpload();
    }

    public static void getStatistics() {
        uploadHelper uH = new uploadHelper(mContext);
        uH.setInterface(new uploadHelper.uploadHelperInterface() {

            @Override
            public void onStateChanged(int state) {
                
                if (state == uploadHelper.STATE_WAITING) {
                    TextView_Statistics.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                    TextView_Statistics.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPublishUploadProgress(long nowSize, long totalSize) {
                
            }

            @Override
            public void onUploadComplete(String response) {
                
                String[] stats = response.split(",");
                TextView_Statistics.setText(mContext.getString(R.string.login_Statistics).replace("[UPLOADCOUNT]", stats[1]).replace("[STARSGIVEN]", stats[2]).replace("[STARSRECEIVED]", stats[3]).replace("[TOP5PRESETS]", stats[4]));
                TextView_Statistics.setVisibility(View.VISIBLE);
                TextView_Statistics.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
            }

            @Override
            public void onUploadFailed(String reason) {
                
                TextView_Statistics.setText(reason);
                TextView_Statistics.setVisibility(View.VISIBLE);
                TextView_Statistics.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
            }
        });
        uH.setServerUrl("http" + (MainActivity.LOCALTESTSERVER ? "" : "s") + "://" + (MainActivity.LOCALTESTSERVER ? "127.0.0.1:8080" : "www.Neon-Soft.de") + "/page/NeoPowerMenu/phpWebservice/webservice3.php");
        uH.setAdditionalUploadPosts(new String[][]{{"action", "statistics"}, {(MainActivity.usernameemail.contains("@") ? "email" : "name"), MainActivity.usernameemail}, {"deviceId", MainActivity.deviceUniqeId}, {"accountId", MainActivity.accountUniqeId}});
        try {
            new File(mContext.getFilesDir().getPath() + "/tmp").createNewFile();
        } catch (IOException e) {
        }
        uH.setLocalUrl(mContext.getFilesDir().getPath() + "/tmp");
        uH.setAllowMultiple(true);
        uH.startUpload();
        if (MainActivity.visibleFragment.equalsIgnoreCase("PresetsManagerAccount")) {
            MainActivity.actionbar.setButtonText(mContext.getString(R.string.login_TitleLogout));
            MainActivity.actionbar.setButtonIcon(R.drawable.ic_action_export);
            MainActivity.actionbar.setButtonListener(logoutOnClickListener);
        }
    }

    public static void returnToLogin() {
        loginFragmentMode = "login";
        MainActivity.actionbar.setButtonText(mContext.getString(R.string.login_Title));
        MainActivity.actionbar.setButtonListener(loginOnClickListener);
        MainActivity.actionbar.setButtonIcon(R.drawable.ic_action_import);
        if (LinearLayout_RegisterContainer.getVisibility() == View.VISIBLE) {
            LinearLayout_RegisterContainer.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
            LinearLayout_RegisterContainer.setVisibility(View.GONE);
            if (!EditText_Username.getText().toString().isEmpty()) {
                EditText_UsernameEmail.setText(EditText_Username.getText().toString());
            } else if (!EditText_Email.getText().toString().isEmpty()) {
                EditText_UsernameEmail.setText(EditText_Email.getText().toString());
            }
        } else if (LinearLayout_RecoverAccountContainer.getVisibility() == View.VISIBLE) {
            LinearLayout_RecoverAccountContainer.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
            LinearLayout_RecoverAccountContainer.setVisibility(View.GONE);
            if (!EditText_RecoverUsername.getText().toString().isEmpty()) {
                EditText_UsernameEmail.setText(EditText_RecoverUsername.getText().toString());
            } else if (!EditText_RecoverEmail.getText().toString().isEmpty()) {
                EditText_UsernameEmail.setText(EditText_RecoverEmail.getText().toString());
            }
        }
        LinearLayout_LoginContainer.setVisibility(View.VISIBLE);
        LinearLayout_LoginContainer.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
    }

}

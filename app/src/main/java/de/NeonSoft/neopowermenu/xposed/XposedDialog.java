package de.NeonSoft.neopowermenu.xposed;

import android.Manifest;
import android.animation.*;
import android.annotation.TargetApi;
import android.app.*;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.*;
import android.content.*;
import android.content.pm.*;
import android.filterfw.core.Frame;
import android.graphics.*;
import android.graphics.drawable.*;
import android.hardware.fingerprint.FingerprintManager;
import android.media.*;
import android.net.Uri;
import android.net.wifi.*;
import android.os.*;
import android.provider.*;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.telephony.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.nostra13.universalimageloader.cache.disc.impl.ext.*;
import com.nostra13.universalimageloader.cache.memory.impl.*;
import com.nostra13.universalimageloader.core.*;
import com.nostra13.universalimageloader.core.assist.*;
import com.nostra13.universalimageloader.core.listener.*;

import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.Preferences.*;
import de.NeonSoft.neopowermenu.R;
import de.NeonSoft.neopowermenu.helpers.*;
import de.NeonSoft.neopowermenu.services.*;
import eu.chainfire.libsuperuser.*;

import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.*;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class XposedDialog extends DialogFragment {

    SharedPreferences preferences;
    SharedPreferences colorPrefs;
    SharedPreferences orderPrefs;
    SharedPreferences animationPrefs;

    private XposedMainActivity menuHost;

    private View PowerDialog;

    public XposedDialog() {

    }

    static PackageManager pm;
    KeyguardManager mKeyguardManager;
    KeyguardManager.KeyguardLock mKeyguardLock;
    FingerprintManager mFingerprintManager;

    public Context mContext;
    public Handler mHandler;
    public LayoutInflater mInflater;

    boolean mPreviewMode = false;
    boolean mHideOnClick = false;
    boolean mKeyguardShowing = false;
    boolean mLoadAppIcons = true;
    int sStyleName;
    float mGraphicsPadding = 0;
    boolean mColorizeNonStockIcons = false;
    boolean mDeepXposedLogging = false;

    boolean RequireConfirmation = false;
    String confirmDialog = "";
    boolean isDismissing = false;
    public ArrayList<String> SubDialogs = new ArrayList<>();

    boolean GraphicBehindProgress = false;

    boolean HookShutdownThread = false;
    boolean UseRootCommands = false;

    String mPasswordLock = "";
    boolean mUseFingerprint = false;
    FingerprintHandler mFingerprintHandler;
    private Runnable hidePasswordErrorRunnable;

    LinearLayout dialogMain;
    LinearLayout dialogPadding;
    FrameLayout dialogContent;
    LinearLayout ListContainer;
    LinearLayout ListContainer2;

    FrameLayout frame;
    FrameLayout frame2;
    FrameLayout frame3;
    FrameLayout frameConfirm;
    FrameLayout frameEnterPassword;
    LinearLayout frameLinear, frame2Linear, frame3Linear, frameConfirmLinear, frameEnterPasswordLinear;
    ScrollView frameScroll, frame3Scroll;
    private CircularRevealView revealView;
    private View selectedView;
    int backgroundColor;
    ImageView progressbg;
    ProgressBar progress;
    TextView status;
    TextView status_detail;
    TextView confirmAction;
    TextView confirmNo;
    TextView confirmYes;
    TextView EnterPasswordAction;
    EditText EnterPasswordInput;
    ImageView EnterPasswordFingerprint;
    TextView EnterPasswordError;
    TextView EnterPasswordCancel;
    TextView EnterPasswordOk;

    private final String SHUTDOWN_BROADCAST = "am broadcast android.intent.action.ACTION_SHUTDOWN";
    private final String SHUTDOWN = "reboot -p";
    private final String REBOOT_CMD = "reboot";
    private final String REBOOT_SOFT_REBOOT_CMD = "setprop ctl.restart zygote";
    private final String REBOOT_RECOVERY_CMD = "reboot recovery";
    private final String REBOOT_BOOTLOADER_CMD = "reboot bootloader";
    private final String[] REBOOT_SAFE_MODE = new String[]{"setprop persist.sys.safemode 1", REBOOT_SOFT_REBOOT_CMD};
    private final String REBOOT_FLASHMODE_CMD = "reboot oem-53";

    private final int BG_PRIO = android.os.Process.THREAD_PRIORITY_BACKGROUND;
    private final int RUNNABLE_DELAY_MS = 1000;

    public boolean canDismiss = true;

    int int_Vertical = 0;
    int int_Horizontal = 0;
    Object[] DisplaySize;

    AudioManager am;
    public static int amRingerMode;

    Runnable mRun;
    ArrayList<ImageView> soundModeIcon_Image = new ArrayList<>();
    ArrayList<String> soundModeIcon_Color = new ArrayList<>();
    ArrayList<TextView> soundModeIcon_Text = new ArrayList<>();
    int airplaneMode = 0;
    ArrayList<ImageView> airplaneModeIcon_Image = new ArrayList<>();
    ArrayList<String> airplaneModeIcon_Color = new ArrayList<>();
    boolean flashlightOn = false;
    ArrayList<ImageView> flashlightIcon_Image = new ArrayList<>();
    ArrayList<String> flashlightIcon_Color = new ArrayList<>();
    int rotate = 0;
    ArrayList<ImageView> rotateIcon_Image = new ArrayList<>();
    ArrayList<String> rotateIcon_Color = new ArrayList<>();
    boolean mediaPlaying = false;
    ArrayList<ImageView> playPauseIcon_Image = new ArrayList<>();
    ArrayList<String> playPauseIcon_Color = new ArrayList<>();
    boolean wifiActive = false;
    WifiManager wifiManager;
    ArrayList<ImageView> toggleWifi_Image = new ArrayList<>();
    ArrayList<String> toggleWifi_Color = new ArrayList<>();
    boolean bluetoothActive = false;
    BluetoothAdapter bluetoothAdapter;
    ArrayList<ImageView> toggleBluetooth_Image = new ArrayList<>();
    ArrayList<String> toggleBluetooth_Color = new ArrayList<>();
    boolean dataActive = false;
    TelephonyManager telephonyManager;
    ArrayList<ImageView> toggleData_Image = new ArrayList<>();
    ArrayList<String> toggleData_Color = new ArrayList<>();
    ArrayList<ImageView> silentModeIcon_Image = new ArrayList<>();
    ArrayList<String> silentModeIcon_Color = new ArrayList<>();

    int shortcutItem = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getActivity();

        pm = mContext.getPackageManager();
        mKeyguardManager = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
        mKeyguardLock = mKeyguardManager.newKeyguardLock(Context.KEYGUARD_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFingerprintManager = (FingerprintManager) mContext.getSystemService(Context.FINGERPRINT_SERVICE);
        }
        hidePasswordErrorRunnable = new Runnable() {
            @Override
            public void run() {
                EnterPasswordError.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
                EnterPasswordError.setVisibility(View.GONE);
            }
        };
        mKeyguardShowing = mKeyguardManager.isKeyguardLocked();

        mInflater = inflater;
        isDismissing = false;
        canDismiss = true;
        mHandler = new Handler();
        preferences = SettingsManager.getInstance(mContext).getMainPrefs();//mContext.getSharedPreferences(MainActivity.class.getPackage().getName() + "_preferences", 0);
        colorPrefs = mContext.getSharedPreferences("colors", 0);
        orderPrefs = mContext.getSharedPreferences("visibilityOrder", 0);
        animationPrefs = mContext.getSharedPreferences("animations", 0);
        HookShutdownThread = preferences.getBoolean("HookShutdownThread", false);
        mDeepXposedLogging = preferences.getBoolean("DeepXposedLogging", false);
        mHideOnClick = preferences.getBoolean("HideOnClick", false);
        mLoadAppIcons = preferences.getBoolean("LoadAppIcons", true);
        mColorizeNonStockIcons = preferences.getBoolean("ColorizeNonStockIcons", false);
        mGraphicsPadding = preferences.getFloat("GraphicsPadding", 0);

        mPasswordLock = preferences.getString(PreferenceNames.pItemPWL, "");
        mUseFingerprint = preferences.getBoolean(PreferenceNames.pLockWithFingerprint, false);

        sStyleName = preferences.getInt("DialogThemeId", 0);
        if (Build.VERSION.SDK_INT >= 17) {
            try {
                airplaneMode = Settings.Global.getInt(mContext.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON);
            } catch (Throwable e) {
            }
        } else {
            try {
                airplaneMode = Settings.System.getInt(mContext.getContentResolver(), Settings.System.AIRPLANE_MODE_ON);
            } catch (Throwable e) {
            }
        }
        try {
            rotate = Settings.System.getInt(mContext.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
        } catch (Throwable e) {
        }
        PowerDialog = inflater.inflate(R.layout.fragment_power, container, false);


        am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        amRingerMode = am.getRingerMode();
        mediaPlaying = am.isMusicActive();

        wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        wifiActive = wifiManager.isWifiEnabled();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            bluetoothActive = bluetoothAdapter.isEnabled();
        }

        telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        dataActive = isDataActive();

        RequireConfirmation = preferences.getBoolean("RequireConfirmation", false);
        UseRootCommands = preferences.getBoolean("UseRoot", true);

        int_Vertical = preferences.getInt("DialogPosition_Vertical",25);
        int_Horizontal = preferences.getInt("DialogPosition_Horizontal",25);
        DisplaySize = helper.getDisplaySize(mContext, false);

        if (!helper.isDeviceHorizontal(mContext)) {
            DisplaySize[1] = (int) DisplaySize[1] - helper.getNavigationBarSize(mContext).y - helper.getStatusBarHeight(mContext);
        } else {
            DisplaySize[0] = (int) DisplaySize[0] - helper.getNavigationBarSize(mContext).x;
            DisplaySize[1] = (int) DisplaySize[1] - helper.getStatusBarHeight(mContext);
        }

        dialogMain = (LinearLayout) PowerDialog.findViewById(R.id.fragmentpowerFrameLayout_Main);

        dialogPadding = (LinearLayout) PowerDialog.findViewById(R.id.fragmentpowerPadding);

        dialogContent = (FrameLayout) PowerDialog.findViewById(R.id.fragmentpowerFrameLayout1);

        revealView = (CircularRevealView) PowerDialog.findViewById(R.id.reveal);
        backgroundColor = Color.parseColor(colorPrefs.getString("Dialog_Backgroundcolor", "#ffffff"));
        ListContainer = (LinearLayout) PowerDialog.findViewById(R.id.ListContainer);

        frame = (FrameLayout) PowerDialog.findViewById(R.id.frame);
        dialogContent.setBackgroundColor(backgroundColor);
        frameLinear = (LinearLayout) PowerDialog.findViewById(R.id.frameLinear);
        frameScroll = (ScrollView) PowerDialog.findViewById(R.id.frameScroll);
        frame2 = (FrameLayout) PowerDialog.findViewById(R.id.frame2);
        frame2.setVisibility(View.GONE);
        frame2Linear = (LinearLayout) PowerDialog.findViewById(R.id.frame2Linear);
        frame3 = (FrameLayout) PowerDialog.findViewById(R.id.frame3);
        frame3.setVisibility(View.GONE);
        frame3Linear = (LinearLayout) PowerDialog.findViewById(R.id.frame3Linear);
        frame3Scroll = (ScrollView) PowerDialog.findViewById(R.id.frame3Scroll);
        ListContainer2 = (LinearLayout) PowerDialog.findViewById(R.id.ListContainer2);

        frameConfirm = (FrameLayout) PowerDialog.findViewById(R.id.frameConfirm);
        frameConfirm.setVisibility(View.GONE);
        frameConfirm.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        frameConfirmLinear = (LinearLayout) PowerDialog.findViewById(R.id.frameConfirmLinear);
        confirmAction = (TextView) PowerDialog.findViewById(R.id.fragmentpowerTextView_ConfirmAction);
        confirmAction.setTextColor(Color.parseColor(colorPrefs.getString("Dialog_Textcolor", "#000000")));
        confirmNo = (TextView) PowerDialog.findViewById(R.id.fragmentpowerTextView_ConfirmNo);
        confirmNo.setTextColor(Color.parseColor(colorPrefs.getString("Dialog_Textcolor", "#000000")));
        confirmYes = (TextView) PowerDialog.findViewById(R.id.fragmentpowerTextView_ConfirmYes);
        confirmYes.setTextColor(Color.parseColor(colorPrefs.getString("Dialog_Textcolor", "#000000")));

        frameEnterPassword = (FrameLayout) PowerDialog.findViewById(R.id.frameEnterPassword);
        frameEnterPassword.setVisibility(View.GONE);
        frameEnterPassword.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        frameEnterPasswordLinear = (LinearLayout) PowerDialog.findViewById(R.id.frameEnterPasswordLinear);
        EnterPasswordAction = (TextView) PowerDialog.findViewById(R.id.fragmentpowerTextView_EnterPasswordAction);
        EnterPasswordAction.setTextColor(Color.parseColor(colorPrefs.getString("Dialog_Textcolor", "#000000")));
        EnterPasswordCancel = (TextView) PowerDialog.findViewById(R.id.fragmentpowerTextView_EnterPasswordCancel);
        EnterPasswordFingerprint = (ImageView) PowerDialog.findViewById(R.id.fragmentpowerImageView_EnterPasswordFingerPrint);
        EnterPasswordFingerprint.setColorFilter(Color.parseColor(colorPrefs.getString("Dialog_Textcolor", "#000000")), PorterDuff.Mode.MULTIPLY);
        EnterPasswordInput = (EditText) PowerDialog.findViewById(R.id.fragmentpowerEditText_EnterPassword);
        EnterPasswordError = (TextView) PowerDialog.findViewById(R.id.fragmentpowerTextView_EnterPasswordError);
        EnterPasswordError.setTextColor(Color.parseColor(colorPrefs.getString("Dialog_Textcolor", "#000000")));
        EnterPasswordError.setVisibility(View.GONE);
        EnterPasswordCancel.setTextColor(Color.parseColor(colorPrefs.getString("Dialog_Textcolor", "#000000")));
        EnterPasswordCancel.setText(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
        EnterPasswordOk = (TextView) PowerDialog.findViewById(R.id.fragmentpowerTextView_EnterPasswordOk);
        EnterPasswordOk.setTextColor(Color.parseColor(colorPrefs.getString("Dialog_Textcolor", "#000000")));
        EnterPasswordOk.setText(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_OK]);

        if (sStyleName == 1) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dialogContent.getLayoutParams());
            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            params.height = LinearLayout.LayoutParams.MATCH_PARENT;
            dialogContent.setLayoutParams(params);
            FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(frame.getLayoutParams());
            params2.width = FrameLayout.LayoutParams.MATCH_PARENT;
            params2.topMargin = helper.getStatusBarHeight(mContext);
            if (!helper.isDeviceHorizontal(mContext)) {
                params2.bottomMargin = helper.getNavigationBarSize(mContext).y;
            } else if (helper.isDeviceHorizontal(mContext)) {
                params2.rightMargin = helper.getNavigationBarSize(mContext).x;
            }
            params2.height = FrameLayout.LayoutParams.MATCH_PARENT;
            frame.setLayoutParams(params2);
            frame3.setLayoutParams(params2);
            FrameLayout.LayoutParams crparams = new FrameLayout.LayoutParams(revealView.getLayoutParams());
            crparams.width = FrameLayout.LayoutParams.MATCH_PARENT;
            crparams.height = FrameLayout.LayoutParams.MATCH_PARENT;
            revealView.setLayoutParams(crparams);
        } else if (sStyleName == 2) {
            LinearLayout.LayoutParams frameScrollParams = new LinearLayout.LayoutParams(frameScroll.getLayoutParams());
            frameScrollParams.width = ((int) helper.convertDpToPixel(340, mContext));
            frameScrollParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            frameScroll.setLayoutParams(frameScrollParams);
            frame3Scroll.setLayoutParams(frameScrollParams);
            FrameLayout.LayoutParams crparams = new FrameLayout.LayoutParams(revealView.getLayoutParams());
            crparams.width = FrameLayout.LayoutParams.MATCH_PARENT;
            crparams.height = ((int) helper.convertDpToPixel(150, mContext));// + (boolean_DialogGravityBottom ? helper.getNavigationBarSize(mContext).y : (boolean_DialogGravityTop ? helper.getStatusBarHeight(mContext) : 0));
            revealView.setLayoutParams(crparams);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dialogContent.getLayoutParams());
            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            dialogContent.setLayoutParams(params);
            FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(frame.getLayoutParams());
            params2.width = FrameLayout.LayoutParams.MATCH_PARENT;
            frame.setLayoutParams(params2);
            frame3.setLayoutParams(params2);
        } else if (sStyleName == 3) {
            LinearLayout.LayoutParams frameScrollParams = new LinearLayout.LayoutParams(frameScroll.getLayoutParams());
            frameScrollParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            frameScrollParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            frameScroll.setLayoutParams(frameScrollParams);
            frame3Scroll.setLayoutParams(frameScrollParams);
            FrameLayout.LayoutParams crparams = new FrameLayout.LayoutParams(revealView.getLayoutParams());
            crparams.width = FrameLayout.LayoutParams.MATCH_PARENT;
            crparams.height = ((int) helper.convertDpToPixel(150, mContext));// + (boolean_DialogGravityBottom ? helper.getNavigationBarSize(mContext).y : (boolean_DialogGravityTop ? helper.getStatusBarHeight(mContext) : 0));
            revealView.setLayoutParams(crparams);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dialogContent.getLayoutParams());
            params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialogContent.setLayoutParams(params);
            FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(frame.getLayoutParams());
            params2.width = FrameLayout.LayoutParams.WRAP_CONTENT;
            frame.setLayoutParams(params2);
            frame3.setLayoutParams(params2);
            frameLinear.setLayoutParams(params2);
            frame3Linear.setLayoutParams(params2);
        } else {
            FrameLayout.LayoutParams crparams = new FrameLayout.LayoutParams(revealView.getLayoutParams());
            crparams.width = ((int) helper.convertDpToPixel(340, mContext));// + (boolean_DialogGravityRight ? helper.getNavigationBarSize(mContext).x : 0);
            crparams.height = ((int) helper.convertDpToPixel(150, mContext));// + (boolean_DialogGravityBottom ? helper.getNavigationBarSize(mContext).y : (boolean_DialogGravityTop ? helper.getStatusBarHeight(mContext) : 0));
            revealView.setLayoutParams(crparams);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dialogContent.getLayoutParams());
            params.width = ((int) helper.convertDpToPixel(340, mContext));// + (boolean_DialogGravityRight ? helper.getNavigationBarSize(mContext).x : 0);
            dialogContent.setLayoutParams(params);
            FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(frame.getLayoutParams());
            params2.width = FrameLayout.LayoutParams.MATCH_PARENT;
            frame.setLayoutParams(params2);
            frame3.setLayoutParams(params2);
        }
        FrameLayout.LayoutParams paramsFrame2 = new FrameLayout.LayoutParams(frame2.getLayoutParams());
        FrameLayout.LayoutParams paramsConfirm = new FrameLayout.LayoutParams(frameConfirm.getLayoutParams());
        FrameLayout.LayoutParams paramsEnterPassword = new FrameLayout.LayoutParams(frameEnterPassword.getLayoutParams());
        paramsFrame2.width = FrameLayout.LayoutParams.MATCH_PARENT;
        paramsConfirm.width = FrameLayout.LayoutParams.MATCH_PARENT;
        paramsEnterPassword.width = FrameLayout.LayoutParams.MATCH_PARENT;
        paramsConfirm.height = ((int) helper.convertDpToPixel(150, mContext));// + (boolean_DialogGravityBottom ? helper.getNavigationBarSize(mContext).y : (boolean_DialogGravityTop ? helper.getStatusBarHeight(mContext) : 0));

        frame2.setLayoutParams(paramsFrame2);
        frameConfirm.setLayoutParams(paramsConfirm);
        frameEnterPassword.setLayoutParams(paramsEnterPassword);

        if (animationPrefs.getInt(PreferencesAnimationsFragment.names[4][1].toString(), PreferencesAnimationsFragment.defaultTypes[1]) < mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
            LayoutTransition lt = new LayoutTransition();
            dialogMain.setLayoutTransition(lt);
            dialogContent.setLayoutTransition(lt);
        }

        frame2Linear.setGravity(Gravity.CENTER);

        status = (TextView) PowerDialog.findViewById(R.id.status);
        status_detail = (TextView) PowerDialog.findViewById(R.id.status_detail);

        progressbg = (ImageView) PowerDialog.findViewById(R.id.progressbg);
        progressbg.setVisibility((GraphicBehindProgress ? View.VISIBLE : View.INVISIBLE));
        progress = (ProgressBar) PowerDialog.findViewById(R.id.progress);

        progress.getIndeterminateDrawable().setColorFilter(
                Color.parseColor("#ffffff"),
                PorterDuff.Mode.SRC_IN);

        if (orderPrefs.getAll().isEmpty()) {
            orderPrefs.edit().putInt("0_item_type", visibilityOrder_ListAdapter.TYPE_NORMAL).commit();;
            orderPrefs.edit().putString("0_item_title", "Shutdown").commit();;
            orderPrefs.edit().putInt("1_item_type", visibilityOrder_ListAdapter.TYPE_NORMAL).commit();;
            orderPrefs.edit().putString("1_item_title", "Reboot").commit();;
            orderPrefs.edit().putInt("2_item_type", visibilityOrder_ListAdapter.TYPE_NORMAL).commit();;
            orderPrefs.edit().putString("2_item_title", "SoftReboot").commit();;
            orderPrefs.edit().putInt("3_item_type", visibilityOrder_ListAdapter.TYPE_MULTI).commit();;
            orderPrefs.edit().putString("3_item1_title", "Recovery").commit();;
            orderPrefs.edit().putString("3_item2_title", "Bootloader").commit();;
            orderPrefs.edit().putString("3_item3_title", "SafeMode").commit();;
        }

        final ArrayList<String> MultiPage = new ArrayList<>();
        boolean firstItemDrawn = false;
        for (int i = 0; i < XposedMainActivity.mItems.size(); i++) {
            int createItem = -1;
            MenuItemHolder item = XposedMainActivity.mItems.get(i);
            if (orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", -1) != -1) {
                if (item.getType() == visibilityOrder_ListAdapter.TYPE_NORMAL) {
                    if (MultiPage.size() == 0 || (MultiPage.size() == 1 && !firstItemDrawn)) {
                        if (!mKeyguardShowing || (mKeyguardShowing && !item.getHideOnLockScreen())) {
                            createItem = visibilityOrder_ListAdapter.TYPE_NORMAL;
                        }
                        firstItemDrawn = true;
                    }
                } else if (item.getType() == visibilityOrder_ListAdapter.TYPE_MULTI) {
                    if (MultiPage.size() == 0) {
                        if (!mKeyguardShowing || (mKeyguardShowing && !item.getHideOnLockScreen())) {
                            createItem = visibilityOrder_ListAdapter.TYPE_MULTI;
                        }
                    }
                } else if (item.getType() == visibilityOrder_ListAdapter.TYPE_MULTIPAGE_START) {
                    MultiPage.add(orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null"));
                    if (!mKeyguardShowing || (mKeyguardShowing && !item.getHideOnLockScreen())) {
                        firstItemDrawn = false;
                    }
                } else if (item.getType() == visibilityOrder_ListAdapter.TYPE_MULTIPAGE_END) {
                    if (MultiPage.size() > 0) MultiPage.remove(MultiPage.size() - 1);
                }
            }
            if (XposedMainActivity.action != null && (item.getTitle(1).contains(XposedMainActivity.action) || item.getTitle(2).contains(XposedMainActivity.action) || item.getTitle(3).contains(XposedMainActivity.action))) {
                shortcutItem = i;
            }
            if (createItem == visibilityOrder_ListAdapter.TYPE_NORMAL) {
                ListContainer.addView(createNormalItem(i, item.getTitle(1), (MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) : null), item.getHideDesc(), item.getText(1)));
            } else if (createItem == visibilityOrder_ListAdapter.TYPE_MULTI) {
                ListContainer.addView(createMultiItem(i, item.getTitle(1)));
            }
        }
        if (XposedMainActivity.action != null) {
            mHideOnClick = true;
            performMenuClick(shortcutItem, XposedMainActivity.action, null);
        }

        mRun = new Runnable() {

            @Override
            public void run() {
                refreshIcons();
            }
        };
        mHandler.postDelayed(mRun, 250L);

        if (XposedMainActivity.isShortcutWithVisibleContent) {
            if (animationPrefs.getInt(PreferencesAnimationsFragment.names[4][1].toString(), PreferencesAnimationsFragment.defaultTypes[4]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                dialogMain.startAnimation(helper.getAnimation(mContext, animationPrefs, 3, false));
            }
        } else {
            dialogMain.setVisibility(View.INVISIBLE);
        }

        ViewTreeObserver vto = dialogContent.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                setGravity();
                ViewTreeObserver obs = dialogContent.getViewTreeObserver();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }
            }
        });

        dialogPadding.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int widthWas = oldRight - oldLeft; // right exclusive, left inclusive
                int heightWas = oldBottom - oldTop; // bottom exclusive, top inclusive
                if( v.getWidth() != widthWas || v.getHeight() != heightWas )
                {
                    setGravity();
                }
            }
        });

        return PowerDialog;

    }

    private View createNormalItem(final int id, String title, String pageItem, boolean hideDesc, String customText) {
        final String mTitle = title;
        View inflated = mInflater.inflate(R.layout.powermenu_normal, null, false);

        LinearLayout root = (LinearLayout) inflated.findViewById(R.id.powermenunormal_root);
        ImageView icon = (ImageView) inflated.findViewById(R.id.powermenunormal_icon);
        ImageView icon2 = (ImageView) inflated.findViewById(R.id.powermenunormal_icon2);
        icon2.setVisibility(View.GONE);
        TextView text = (TextView) inflated.findViewById(R.id.powermenunormal_text1);
        text.setVisibility(View.GONE);
        TextView desc = (TextView) inflated.findViewById(R.id.powermenunormal_text2);
        desc.setVisibility(View.GONE);

        if (!title.equalsIgnoreCase("Empty")) {
            String string = title;
            if (customText.isEmpty()) {
                if (string.contains(".")) {
                    PackageManager pm = mContext.getPackageManager();
                    try {
                        string = pm.getApplicationInfo(string.split("/")[0], 0).loadLabel(pm).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                    }
                } else {
                    try {
                        string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + title, "string", MainActivity.class.getPackage().getName()));
                    } catch (Throwable t) {
                        try {
                            string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_" + title, "string", MainActivity.class.getPackage().getName()));
                        } catch (Throwable t1) {
                        }
                    }
                }
            } else {
                string = customText;
            }
            text.setText(string);
            text.setVisibility((XposedMainActivity.mItems.get(id).getHideText() ? View.GONE : View.VISIBLE));
            text.setTextColor(Color.parseColor(colorPrefs.getString("Dialog_Textcolor", "#000000")));

            if (!hideDesc) {
                String descString = title;
                try {
                    descString = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + title + "Desc", "string", MainActivity.class.getPackage().getName()));
                    desc.setVisibility(View.VISIBLE);
                } catch (Throwable t) {
                    try {
                        descString = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_" + title + "Desc", "string", MainActivity.class.getPackage().getName()));
                        desc.setVisibility(View.VISIBLE);
                    } catch (Throwable t1) {
                    }
                }
                if (preferences.getLong("ScreenshotDelay", 1000) == 0) {
                    descString = descString.replace("[SCREENSHOTDELAY]", mContext.getString(R.string.advancedPrefs_DelayZero));
                } else {
                    descString = descString.replace("[SCREENSHOTDELAY]", helper.getTimeString(mContext, preferences.getLong("ScreenshotDelay", 1000), 1));
                }
                descString = (preferences.getBoolean("FlashlightAutoOff", true) ? descString.replace("[AUTOOFF]", helper.getTimeString(mContext, preferences.getLong("FlashlightAutoOffTime", 1000 * 60 * 10), 1)) : getString(R.string.powerMenuMain_FlashlightDescDisabled));
                String descText = mContext.getString(R.string.SoundMode_Normal);
                if (amRingerMode == AudioManager.RINGER_MODE_SILENT) {
                    descText = mContext.getString(R.string.SoundMode_Silent);
                } else if (amRingerMode == AudioManager.RINGER_MODE_VIBRATE) {
                    descText = mContext.getString(R.string.SoundMode_Vibrate);
                }
                if (descString.contains("[SOUNDMODE]")) {
                    soundModeIcon_Text.add(desc);
                }
                descString = descString.replace("[SOUNDMODE]", descText);
                desc.setText(descString);
            }

            desc.setTextColor(Color.parseColor(colorPrefs.getString("Dialog_Textcolor", "#000000")));

            createCircleIcon(id, icon, icon2, title, string, colorPrefs.getString("Dialog" + (!XposedMainActivity.mItems.get(id).getShortcutUri(1).isEmpty() ? "Shortcut" : (title.contains(".") ? "AppShortcut" : title)) + "_Circlecolor", "#ff000000"), colorPrefs.getString("Dialog" + (!XposedMainActivity.mItems.get(id).getShortcutUri(1).isEmpty() ? "Shortcut" : (title.contains(".") ? "AppShortcut" : title)) + "_Textcolor", "#ffffff"));

            if (pageItem == null || pageItem.isEmpty()) {
                root.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View p1) {

                        performMenuClick(id, mTitle, p1);
                    }
                });
            } else {
                final String thisGroup = pageItem;
                root.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View p1) {

                        performMenuClick(id, "multipage:" + thisGroup, p1);
                    }
                });
            }
        } else {
            root.setVisibility(View.INVISIBLE);
        }
        if (animationPrefs.getInt(PreferencesAnimationsFragment.names[10][1].toString(), PreferencesAnimationsFragment.defaultTypes[10]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
            root.startAnimation(helper.getAnimation(mContext, animationPrefs, 9, false));
        }
        return inflated;
    }

    private View createMultiItem(final int id, String title) {
        View inflated = mInflater.inflate(R.layout.powermenu_multi, null, false);

        LinearLayout root = (LinearLayout) inflated.findViewById(R.id.powermenumulti_item1);
        ImageView icon = (ImageView) inflated.findViewById(R.id.powermenumulti_item1icon);
        ImageView icon2 = (ImageView) inflated.findViewById(R.id.powermenumulti_item1icon2);
        icon2.setVisibility(View.GONE);
        TextView text = (TextView) inflated.findViewById(R.id.powermenumulti_item1text);
        text.setVisibility(View.GONE);

        if (!XposedMainActivity.mItems.get(id).getTitle(1).equalsIgnoreCase("Empty")) {
            String string = XposedMainActivity.mItems.get(id).getTitle(1);
            if (!XposedMainActivity.mItems.get(id).getText(1).isEmpty()) {
                string = XposedMainActivity.mItems.get(id).getText(1);
            }
            if (string.isEmpty()) {
                //string = XposedMainActivity.mItems.get(id).getTitle(1);
                if (string.contains(".")) {
                    PackageManager pm = mContext.getPackageManager();
                    try {
                        string = pm.getApplicationInfo(string.split("/")[0], 0).loadLabel(pm).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                    }
                } else {
                    try {
                        string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + XposedMainActivity.mItems.get(id).getTitle(1), "string", MainActivity.class.getPackage().getName()));
                    } catch (Throwable t) {
                        try {
                            string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_" + XposedMainActivity.mItems.get(id).getTitle(1), "string", MainActivity.class.getPackage().getName()));
                        } catch (Throwable t1) {
                            string = "Failed to get String resource for powerMenuBottom_" + XposedMainActivity.mItems.get(id).getTitle(1);
                            Log.w("NPM", "Failed to get String resource for powerMenuBottom_" + XposedMainActivity.mItems.get(id).getTitle(1), t);
                        }
                    }
                }
            }
            text.setText(string);
            text.setVisibility((XposedMainActivity.mItems.get(id).getHideText() ? View.GONE : View.VISIBLE));
            text.setTextColor(Color.parseColor(colorPrefs.getString("Dialog_Textcolor", "#000000")));
            if (XposedMainActivity.mItems.get(id).getTitle(1).equalsIgnoreCase("SoundMode")) {
                soundModeIcon_Text.add(null);
            }

            Log.d("NPM", "ShortcutUri(1): " + XposedMainActivity.mItems.get(id).getShortcutUri(1));

            String IDENTIFIER = (!XposedMainActivity.mItems.get(id).getShortcutUri(1).isEmpty() ? "Shortcut" : (XposedMainActivity.mItems.get(id).getTitle(1).contains(".") ? "AppShortcut" : XposedMainActivity.mItems.get(id).getTitle(1)));
            createCircleIcon(id, icon, icon2, XposedMainActivity.mItems.get(id).getTitle(1), string, colorPrefs.getString("Dialog" + IDENTIFIER + "_Circlecolor", "#ff000000"), colorPrefs.getString("Dialog" + IDENTIFIER + "_Textcolor", "#ffffff"));

            root.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View p1) {

                    performMenuClick(id, XposedMainActivity.mItems.get(id).getTitle(1), p1);
                }
            });
        } else {
            root.setVisibility((XposedMainActivity.mItems.get(id).getFillEmpty() ? View.INVISIBLE : View.GONE));
        }
        if (animationPrefs.getInt(PreferencesAnimationsFragment.names[13][1].toString(), PreferencesAnimationsFragment.defaultTypes[13]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
            root.startAnimation(helper.getAnimation(mContext, animationPrefs, 12, false));
        }

        LinearLayout root2 = (LinearLayout) inflated.findViewById(R.id.powermenumulti_item2);
        ImageView iconitem2 = (ImageView) inflated.findViewById(R.id.powermenumulti_item2icon);
        ImageView icon2item2 = (ImageView) inflated.findViewById(R.id.powermenumulti_item2icon2);
        icon2item2.setVisibility(View.GONE);
        TextView text2 = (TextView) inflated.findViewById(R.id.powermenumulti_item2text);
        text2.setVisibility(View.GONE);

        if (!XposedMainActivity.mItems.get(id).getTitle(2).equalsIgnoreCase("Empty")) {
            String string2 = XposedMainActivity.mItems.get(id).getTitle(2);
            if (!XposedMainActivity.mItems.get(id).getText(2).isEmpty()) {
                string2 = XposedMainActivity.mItems.get(id).getText(2);
            }
            if (string2.isEmpty()) {
                //string2 = XposedMainActivity.mItems.get(id).getTitle(2);
                if (string2.contains(".")) {
                    PackageManager pm = mContext.getPackageManager();
                    try {
                        string2 = pm.getApplicationInfo(string2.split("/")[0], 0).loadLabel(pm).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                    }
                } else {
                    try {
                        string2 = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + XposedMainActivity.mItems.get(id).getTitle(2), "string", MainActivity.class.getPackage().getName()));
                    } catch (Throwable t) {
                        try {
                            string2 = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_" + XposedMainActivity.mItems.get(id).getTitle(2), "string", MainActivity.class.getPackage().getName()));
                        } catch (Throwable t1) {
                            Log.w("NPM", "Failed to get String resource for powerMenuBottom_" + XposedMainActivity.mItems.get(id).getTitle(2), t);
                        }
                    }
                }
            }
            text2.setText(string2);
            text2.setVisibility((XposedMainActivity.mItems.get(id).getHideText() ? View.GONE : View.VISIBLE));
            text2.setTextColor(Color.parseColor(colorPrefs.getString("Dialog_Textcolor", "#000000")));
            if (XposedMainActivity.mItems.get(id).getTitle(2).equalsIgnoreCase("SoundMode")) {
                soundModeIcon_Text.add(null);
            }

            String IDENTIFIER = (!XposedMainActivity.mItems.get(id).getShortcutUri(2).isEmpty() ? "Shortcut" : (XposedMainActivity.mItems.get(id).getTitle(2).contains(".") ? "AppShortcut" : XposedMainActivity.mItems.get(id).getTitle(2)));
            createCircleIcon(id, iconitem2, icon2item2, XposedMainActivity.mItems.get(id).getTitle(2), string2, colorPrefs.getString("Dialog" + IDENTIFIER + "_Circlecolor", "#ff000000"), colorPrefs.getString("Dialog" + IDENTIFIER + "_Textcolor", "#ffffff"));

            root2.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View p1) {

                    performMenuClick(id, XposedMainActivity.mItems.get(id).getTitle(2), p1);
                }
            });
        } else {
            root2.setVisibility((XposedMainActivity.mItems.get(id).getFillEmpty() ? View.INVISIBLE : View.GONE));
        }
        if (animationPrefs.getInt(PreferencesAnimationsFragment.names[13][1].toString(), PreferencesAnimationsFragment.defaultTypes[13]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
            Animation anim = helper.getAnimation(mContext, animationPrefs, 12, false);
            anim.setStartOffset((anim.getDuration() / 30) * 3);
            root2.startAnimation(anim);
        }

        LinearLayout root3 = (LinearLayout) inflated.findViewById(R.id.powermenumulti_item3);
        ImageView iconitem3 = (ImageView) inflated.findViewById(R.id.powermenumulti_item3icon);
        ImageView icon2item3 = (ImageView) inflated.findViewById(R.id.powermenumulti_item3icon2);
        icon2item3.setVisibility(View.GONE);
        TextView text3 = (TextView) inflated.findViewById(R.id.powermenumulti_item3text);
        text3.setVisibility(View.GONE);

        if (!XposedMainActivity.mItems.get(id).getTitle(3).equalsIgnoreCase("Empty")) {
            String string3 = XposedMainActivity.mItems.get(id).getTitle(3);
            if (!XposedMainActivity.mItems.get(id).getText(3).isEmpty()) {
                string3 = XposedMainActivity.mItems.get(id).getText(3);
            }
            if (string3.isEmpty()) {
                //string3 = XposedMainActivity.mItems.get(id).getTitle(3);
                if (string3.contains(".")) {
                    PackageManager pm = mContext.getPackageManager();
                    try {
                        string3 = pm.getApplicationInfo(string3.split("/")[0], 0).loadLabel(pm).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                    }
                } else {
                    try {
                        string3 = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + XposedMainActivity.mItems.get(id).getTitle(3), "string", MainActivity.class.getPackage().getName()));
                    } catch (Throwable t) {
                        try {
                            string3 = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_" + XposedMainActivity.mItems.get(id).getTitle(3), "string", MainActivity.class.getPackage().getName()));
                        } catch (Throwable t1) {
                            Log.w("NPM", "Failed to get String resource for powerMenuBottom_" + XposedMainActivity.mItems.get(id).getTitle(3), t);
                        }
                    }
                }
            }
            text3.setText(string3);
            text3.setVisibility((XposedMainActivity.mItems.get(id).getHideText() ? View.GONE : View.VISIBLE));
            text3.setTextColor(Color.parseColor(colorPrefs.getString("Dialog_Textcolor", "#000000")));
            if (XposedMainActivity.mItems.get(id).getTitle(3).equalsIgnoreCase("SoundMode")) {
                soundModeIcon_Text.add(null);
            }

            String IDENTIFIER = (!XposedMainActivity.mItems.get(id).getShortcutUri(3).isEmpty() ? "Shortcut" : (XposedMainActivity.mItems.get(id).getTitle(3).contains(".") ? "AppShortcut" : XposedMainActivity.mItems.get(id).getTitle(3)));
            createCircleIcon(id, iconitem3, icon2item3, XposedMainActivity.mItems.get(id).getTitle(3), string3, colorPrefs.getString("Dialog" + IDENTIFIER + "_Circlecolor", "#ff000000"), colorPrefs.getString("Dialog" + IDENTIFIER + "_Textcolor", "#ffffff"));

            root3.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View p1) {

                    performMenuClick(id, XposedMainActivity.mItems.get(id).getTitle(3), p1);
                }
            });
        } else {
            root3.setVisibility((XposedMainActivity.mItems.get(id).getFillEmpty() ? View.INVISIBLE : View.GONE));
        }
        if (animationPrefs.getInt(PreferencesAnimationsFragment.names[13][1].toString(), PreferencesAnimationsFragment.defaultTypes[13]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
            Animation anim = helper.getAnimation(mContext, animationPrefs, 12, false);
            anim.setStartOffset((anim.getDuration() / 30) * 6);
            root3.startAnimation(anim);
        }

        return inflated;
    }

    public void refreshIcons() {
        if (isAdded()) {
            if (!soundModeIcon_Image.isEmpty() && amRingerMode != am.getRingerMode()) {
                amRingerMode = am.getRingerMode();
                for (int i = 0; i < soundModeIcon_Image.size(); i++) {
                    if (amRingerMode == AudioManager.RINGER_MODE_VIBRATE) {
                        loadImage(soundModeIcon_Image.get(i), 14, PreferencesGraphicsFragment.graphics[14][2].toString(), soundModeIcon_Color.get(i));
                    } else if (amRingerMode == AudioManager.RINGER_MODE_SILENT) {
                        loadImage(soundModeIcon_Image.get(i), 13, PreferencesGraphicsFragment.graphics[13][2].toString(), soundModeIcon_Color.get(i));
                    } else {
                        loadImage(soundModeIcon_Image.get(i), 12, PreferencesGraphicsFragment.graphics[12][2].toString(), soundModeIcon_Color.get(i));
                    }
                }
                if (!soundModeIcon_Text.isEmpty()) {
                    for (int i = 0; i < soundModeIcon_Text.size(); i++) {
                        if (soundModeIcon_Text.get(i) != null) {
                            String descText = getString(R.string.SoundMode_Normal);
                            if (amRingerMode == AudioManager.RINGER_MODE_SILENT) {
                                descText = getString(R.string.SoundMode_Silent);
                            } else if (amRingerMode == AudioManager.RINGER_MODE_VIBRATE) {
                                descText = getString(R.string.SoundMode_Vibrate);
                            }
                            String descString = mContext.getString(R.string.powerMenuMain_SoundModeDesc).replace("[SOUNDMODE]", descText);
                            soundModeIcon_Text.get(i).setText(descString);
                        }
                    }
                }
            }

            if (!airplaneModeIcon_Image.isEmpty()) {
                for (int i = 0; i < airplaneModeIcon_Image.size(); i++) {
                    if (Build.VERSION.SDK_INT >= 17) {
                        try {
                            if (airplaneMode != android.provider.Settings.Global.getInt(mContext.getContentResolver(), android.provider.Settings.Global.AIRPLANE_MODE_ON)) {
                                if (android.provider.Settings.Global.getInt(mContext.getContentResolver(), android.provider.Settings.Global.AIRPLANE_MODE_ON) == 0) {
                                    loadImage(airplaneModeIcon_Image.get(i), 10, PreferencesGraphicsFragment.graphics[10][2].toString(), airplaneModeIcon_Color.get(i));
                                } else {
                                    loadImage(airplaneModeIcon_Image.get(i), 9, PreferencesGraphicsFragment.graphics[9][2].toString(), airplaneModeIcon_Color.get(i));
                                }
                                airplaneMode = android.provider.Settings.Global.getInt(mContext.getContentResolver(), android.provider.Settings.Global.AIRPLANE_MODE_ON);
                            }
                        } catch (Throwable e) {
                            Log.e("NPM:rI", "Failed to refresh airplane icon:", e);
                            loadImage(airplaneModeIcon_Image.get(i), 10, PreferencesGraphicsFragment.graphics[10][2].toString(), airplaneModeIcon_Color.get(i));
                        }
                    } else {
                        try {
                            if (airplaneMode != android.provider.Settings.System.getInt(mContext.getContentResolver(), android.provider.Settings.System.AIRPLANE_MODE_ON)) {
                                if (android.provider.Settings.System.getInt(mContext.getContentResolver(), android.provider.Settings.System.AIRPLANE_MODE_ON) == 0) {
                                    loadImage(airplaneModeIcon_Image.get(i), 10, PreferencesGraphicsFragment.graphics[10][2].toString(), airplaneModeIcon_Color.get(i));
                                } else {
                                    loadImage(airplaneModeIcon_Image.get(i), 9, PreferencesGraphicsFragment.graphics[9][2].toString(), airplaneModeIcon_Color.get(i));
                                }
                                airplaneMode = android.provider.Settings.System.getInt(mContext.getContentResolver(), android.provider.Settings.System.AIRPLANE_MODE_ON);
                            }
                        } catch (Throwable e) {
                            Log.e("NPM:rI", "Failed to refresh airplane icon:", e);
                            loadImage(airplaneModeIcon_Image.get(i), 10, PreferencesGraphicsFragment.graphics[10][2].toString(), airplaneModeIcon_Color.get(i));
                        }
                    }
                }
            }

            if (!flashlightIcon_Image.isEmpty()) {
                for (int i = 0; i < flashlightIcon_Image.size(); i++) {
                    try {
                        if (flashlightOn != (TorchService.getTorchState() == TorchService.TORCH_STATUS_ON)) {
                            if (TorchService.getTorchState() == TorchService.TORCH_STATUS_ON) {
                                loadImage(flashlightIcon_Image.get(i), 6, PreferencesGraphicsFragment.graphics[6][2].toString(), flashlightIcon_Color.get(i));
                            } else {
                                loadImage(flashlightIcon_Image.get(i), 7, PreferencesGraphicsFragment.graphics[7][2].toString(), flashlightIcon_Color.get(i));
                            }
                            flashlightOn = TorchService.getTorchState() == TorchService.TORCH_STATUS_ON;
                        }
                    } catch (Throwable t) {
                        Log.e("NPM:rI", "Failed to refresh torch icon:", t);
                    }
                }
            }

            if (!rotateIcon_Image.isEmpty()) {
                for (int i = 0; i < rotateIcon_Image.size(); i++) {
                    try {
                        if (rotate != android.provider.Settings.System.getInt(mContext.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION)) {
                            if (android.provider.Settings.System.getInt(mContext.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION) == 0) {
                                loadImage(rotateIcon_Image.get(i), 20, PreferencesGraphicsFragment.graphics[20][2].toString(), rotateIcon_Color.get(i));
                            } else {
                                loadImage(rotateIcon_Image.get(i), 21, PreferencesGraphicsFragment.graphics[21][2].toString(), rotateIcon_Color.get(i));
                            }
                            rotate = android.provider.Settings.System.getInt(mContext.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
                        }
                    } catch (Throwable e) {
                        Log.e("NPM:rI", "Failed to refresh rotate icon:", e);
                    }
                }
            }

            if (!playPauseIcon_Image.isEmpty()) {
                for (int i = 0; i < playPauseIcon_Image.size(); i++) {
                    try {
                        if (mediaPlaying != am.isMusicActive()) {
                            if (!am.isMusicActive()) {
                                loadImage(playPauseIcon_Image.get(i), 23, PreferencesGraphicsFragment.graphics[23][2].toString(), playPauseIcon_Color.get(i));
                            } else {
                                loadImage(playPauseIcon_Image.get(i), 24, PreferencesGraphicsFragment.graphics[24][2].toString(), playPauseIcon_Color.get(i));
                            }
                            mediaPlaying = am.isMusicActive();
                        }
                    } catch (Throwable e) {
                        loadImage(playPauseIcon_Image.get(i), 23, PreferencesGraphicsFragment.graphics[23][2].toString(), playPauseIcon_Color.get(i));
                        Log.e("NPM:rI", "Failed to refresh media icon:", e);
                    }
                }
            }

            if (!toggleWifi_Image.isEmpty() && wifiManager != null) {
                for (int i = 0; i < toggleWifi_Image.size(); i++) {
                    try {
                        if (wifiActive != wifiManager.isWifiEnabled()) {
                            if (!wifiManager.isWifiEnabled()) {
                                loadImage(toggleWifi_Image.get(i), 26, PreferencesGraphicsFragment.graphics[26][2].toString(), toggleWifi_Color.get(i));
                            } else {
                                loadImage(toggleWifi_Image.get(i), 27, PreferencesGraphicsFragment.graphics[27][2].toString(), toggleWifi_Color.get(i));
                            }
                            wifiActive = wifiManager.isWifiEnabled();
                        }
                    } catch (Throwable e) {
                        loadImage(toggleWifi_Image.get(i), 26, PreferencesGraphicsFragment.graphics[26][2].toString(), toggleWifi_Color.get(i));
                        Log.e("NPM:rI", "Failed to refresh wifi icon:", e);
                    }
                }
            }

            if (!toggleBluetooth_Image.isEmpty() && bluetoothAdapter != null) {
                for (int i = 0; i < toggleBluetooth_Image.size(); i++) {
                    try {
                        if (bluetoothActive != bluetoothAdapter.isEnabled()) {
                            if (!bluetoothAdapter.isEnabled()) {
                                loadImage(toggleBluetooth_Image.get(i), 28, PreferencesGraphicsFragment.graphics[28][2].toString(), toggleBluetooth_Color.get(i));
                            } else {
                                loadImage(toggleBluetooth_Image.get(i), 29, PreferencesGraphicsFragment.graphics[29][2].toString(), toggleBluetooth_Color.get(i));
                            }
                            bluetoothActive = bluetoothAdapter.isEnabled();
                        }
                    } catch (Throwable e) {
                        loadImage(toggleBluetooth_Image.get(i), 28, PreferencesGraphicsFragment.graphics[28][2].toString(), toggleBluetooth_Color.get(i));
                        Log.e("NPM:rI", "Failed to refresh bluetooth icon:", e);
                    }
                }
            }

            if (!toggleData_Image.isEmpty()) {
                for (int i = 0; i < toggleData_Image.size(); i++) {
                    try {
                        if (dataActive != isDataActive()) {
                            if (!isDataActive()) {
                                loadImage(toggleData_Image.get(i), 30, PreferencesGraphicsFragment.graphics[30][2].toString(), toggleData_Color.get(i));
                            } else {
                                loadImage(toggleData_Image.get(i), 31, PreferencesGraphicsFragment.graphics[31][2].toString(), toggleData_Color.get(i));
                            }
                            dataActive = isDataActive();
                        }
                    } catch (Throwable e) {
                        loadImage(toggleData_Image.get(i), 30, PreferencesGraphicsFragment.graphics[30][2].toString(), toggleData_Color.get(i));
                        Log.e("NPM:rI", "Failed to refresh data icon:", e);
                    }
                }
            }

            if (!silentModeIcon_Image.isEmpty()) {
                for (int i = 0; i < silentModeIcon_Image.size(); i++) {
                    try {
                        if (amRingerMode != am.getRingerMode()) {
                            if (amRingerMode == AudioManager.RINGER_MODE_SILENT) {
                                loadImage(silentModeIcon_Image.get(i), 12, PreferencesGraphicsFragment.graphics[12][2].toString(), silentModeIcon_Color.get(i));
                            } else {
                                loadImage(silentModeIcon_Image.get(i), 13, PreferencesGraphicsFragment.graphics[13][2].toString(), silentModeIcon_Color.get(i));
                            }
                            amRingerMode = am.getRingerMode();
                        }
                    } catch (Throwable t) {
                        loadImage(silentModeIcon_Image.get(i), 13, PreferencesGraphicsFragment.graphics[12][2].toString(), silentModeIcon_Color.get(i));
                        Log.e("NPM:rI", "Failed to refresh silent mode icon:", t);
                    }
                }
            }
            mHandler.postDelayed(mRun, 250L);
        }
    }

    public void createCircleIcon(final int id, ImageView background, final ImageView foreground, String text, String finalText, final String color1, String color2) {
        try {
            if (preferences.getBoolean("UseGraphics", false)) {
                GraphicDrawable drawable = GraphicDrawable.builder().buildRound((Bitmap) null, Color.parseColor(color1));
                background.setImageDrawable(drawable);
                foreground.setVisibility(View.VISIBLE);
                if (!XposedMainActivity.mItems.get(id).getShortcutUri(1).isEmpty() || !XposedMainActivity.mItems.get(id).getShortcutUri(2).isEmpty() || !XposedMainActivity.mItems.get(id).getShortcutUri(3).isEmpty()) {
                    final String finalText1 = text;
                    SimpleImageLoadingListener listener = new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            foreground.setImageBitmap(null);
                            foreground.setPadding((int) helper.convertDpToPixel(5, mContext), (int) helper.convertDpToPixel(5, mContext), (int) helper.convertDpToPixel(5, mContext), (int) helper.convertDpToPixel(5, mContext));
                            foreground.setColorFilter(Color.parseColor("#ffffff"),
                                    android.graphics.PorterDuff.Mode.DST);
                            if (animationPrefs.getInt(PreferencesAnimationsFragment.names[7][1].toString(), PreferencesAnimationsFragment.defaultTypes[7]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                                foreground.startAnimation(helper.getAnimation(mContext, animationPrefs, 6, true));
                            }
                            foreground.setVisibility(View.INVISIBLE);
                            super.onLoadingStarted(imageUri, view);
                        }

                        @Override
                        public void onLoadingComplete(final String imageUri, final View view, Bitmap loadedImage) {
                            foreground.setPadding((int) mGraphicsPadding, (int) mGraphicsPadding, (int) mGraphicsPadding, (int) mGraphicsPadding);
                            foreground.setImageBitmap(loadedImage);
                            if (mColorizeNonStockIcons) {
                                foreground.setColorFilter(Color.parseColor(color1),
                                        android.graphics.PorterDuff.Mode.MULTIPLY);
                            }
                            foreground.setVisibility(View.VISIBLE);
                            if (animationPrefs.getInt(PreferencesAnimationsFragment.names[7][1].toString(), PreferencesAnimationsFragment.defaultTypes[7]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                                foreground.startAnimation(helper.getAnimation(mContext, animationPrefs, 6, false));
                            }
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            Log.w("NPM:xposedDialog", "Failed to load image '" + imageUri + "': " + failReason.getCause());
                            foreground.setImageDrawable(mContext.getResources().getDrawable((int) PreferencesGraphicsFragment.graphics[33][1]));
                            foreground.setColorFilter(Color.parseColor(color1),
                                    android.graphics.PorterDuff.Mode.MULTIPLY);
                            foreground.setVisibility(View.VISIBLE);
                            if (animationPrefs.getInt(PreferencesAnimationsFragment.names[7][1].toString(), PreferencesAnimationsFragment.defaultTypes[7]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                                foreground.startAnimation(helper.getAnimation(mContext, animationPrefs, 6, false));
                            }
                            if (imageUri.contains("/images/")) {
                                XposedMainActivity.mImageLoader.loadImage("file://" + mContext.getFilesDir().getPath() + "/app_picker/" + finalText1 + ".png", this);
                            }
                        }
                    };
                    XposedMainActivity.mImageLoader.loadImage("file://" + mContext.getFilesDir().getPath() + "/images/" + text + ".png", listener);
                } else if (text.equalsIgnoreCase("Shutdown")) {
                    loadImage(foreground, 1, PreferencesGraphicsFragment.graphics[1][2].toString(), color2);
                } else if (text.equalsIgnoreCase("Reboot")) {
                    loadImage(foreground, 2, PreferencesGraphicsFragment.graphics[2][2].toString(), color2);
                } else if (text.equalsIgnoreCase("SoftReboot")) {
                    loadImage(foreground, 3, PreferencesGraphicsFragment.graphics[3][2].toString(), color2);
                } else if (text.equalsIgnoreCase("Screenshot")) {
                    loadImage(foreground, 4, PreferencesGraphicsFragment.graphics[4][2].toString(), color2);
                } else if (text.equalsIgnoreCase("Screenrecord")) {
                    loadImage(foreground, 5, PreferencesGraphicsFragment.graphics[5][2].toString(), color2);
                } else if (text.equalsIgnoreCase("Flashlight")) {
                    if (TorchService.getTorchState() == TorchService.TORCH_STATUS_OFF) {
                        loadImage(foreground, 7, PreferencesGraphicsFragment.graphics[7][2].toString(), color2);
                    } else {
                        loadImage(foreground, 6, PreferencesGraphicsFragment.graphics[6][2].toString(), color2);
                    }
                    flashlightIcon_Image.add(foreground);
                    flashlightIcon_Color.add(color2);
                } else if (text.equalsIgnoreCase("ExpandedDesktop")) {
                    loadImage(foreground, 8, PreferencesGraphicsFragment.graphics[8][2].toString(), color2);
                } else if (text.equalsIgnoreCase("AirplaneMode")) {
                    if (Build.VERSION.SDK_INT >= 17) {
                        try {
                            if (android.provider.Settings.Global.getInt(mContext.getContentResolver(), android.provider.Settings.Global.AIRPLANE_MODE_ON) == 0) {
                                loadImage(foreground, 10, PreferencesGraphicsFragment.graphics[10][2].toString(), color2);
                            } else {
                                loadImage(foreground, 9, PreferencesGraphicsFragment.graphics[9][2].toString(), color2);
                            }
                        } catch (Throwable e) {
                            loadImage(foreground, 10, PreferencesGraphicsFragment.graphics[10][2].toString(), color2);
                        }
                    } else {
                        try {
                            if (android.provider.Settings.System.getInt(mContext.getContentResolver(), android.provider.Settings.System.AIRPLANE_MODE_ON) == 0) {
                                loadImage(foreground, 10, PreferencesGraphicsFragment.graphics[10][2].toString(), color2);
                            } else {
                                loadImage(foreground, 9, PreferencesGraphicsFragment.graphics[9][2].toString(), color2);
                            }
                        } catch (Throwable e) {
                            loadImage(foreground, 10, PreferencesGraphicsFragment.graphics[10][2].toString(), color2);
                        }
                    }
                    airplaneModeIcon_Image.add(foreground);
                    airplaneModeIcon_Color.add(color2);
                } else if (text.equalsIgnoreCase("RestartUI")) {
                    loadImage(foreground, 11, PreferencesGraphicsFragment.graphics[11][2].toString(), color2);
                } else if (text.equalsIgnoreCase("SoundMode")) {
                    if (amRingerMode == AudioManager.RINGER_MODE_VIBRATE) {
                        loadImage(foreground, 14, PreferencesGraphicsFragment.graphics[14][2].toString(), color2);
                    } else if (amRingerMode == AudioManager.RINGER_MODE_SILENT) {
                        loadImage(foreground, 13, PreferencesGraphicsFragment.graphics[13][2].toString(), color2);
                    } else {
                        loadImage(foreground, 12, PreferencesGraphicsFragment.graphics[12][2].toString(), color2);
                    }
                    soundModeIcon_Image.add(foreground);
                    soundModeIcon_Color.add(color2);
                } else if (text.equalsIgnoreCase("Recovery")) {
                    loadImage(foreground, 15, PreferencesGraphicsFragment.graphics[15][2].toString(), color2);
                } else if (text.equalsIgnoreCase("Bootloader")) {
                    loadImage(foreground, 16, PreferencesGraphicsFragment.graphics[16][2].toString(), color2);
                } else if (text.equalsIgnoreCase("SafeMode")) {
                    loadImage(foreground, 17, PreferencesGraphicsFragment.graphics[17][2].toString(), color2);
                } else if (text.equalsIgnoreCase("SoundVibrate")) {
                    loadImage(foreground, 14, PreferencesGraphicsFragment.graphics[14][2].toString(), color2);
                } else if (text.equalsIgnoreCase("SoundNormal")) {
                    loadImage(foreground, 12, PreferencesGraphicsFragment.graphics[12][2].toString(), color2);
                } else if (text.equalsIgnoreCase("SoundSilent")) {
                    loadImage(foreground, 13, PreferencesGraphicsFragment.graphics[13][2].toString(), color2);
                } else if (text.equalsIgnoreCase("KillApp")) {
                    loadImage(foreground, 18, PreferencesGraphicsFragment.graphics[18][2].toString(), color2);
                } else if (text.contains(".")) {
                    //Log.d("NPM:appIcon","Checking: "+id+"_"+text.split("/")[0]);
                    if (new File(mContext.getFilesDir().getPath() + "/images/" + text.split("/")[0] + ".png").exists()) {
                        loadImage(foreground, 19, text.split("/")[0], color2);
                    } else {
                        if (mLoadAppIcons) {
                            try {
                                Drawable d = pm.getApplicationIcon(text.split("/")[0]);
                                foreground.setPadding((int) mGraphicsPadding, (int) mGraphicsPadding, (int) mGraphicsPadding, (int) mGraphicsPadding);
                                foreground.setImageDrawable(d);
                                if (mColorizeNonStockIcons) {
                                    foreground.setColorFilter(Color.parseColor(color2),
                                            android.graphics.PorterDuff.Mode.MULTIPLY);
                                }
                                foreground.setVisibility(View.VISIBLE);
                                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[7][1].toString(), PreferencesAnimationsFragment.defaultTypes[7]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                                    foreground.startAnimation(helper.getAnimation(mContext, animationPrefs, 6, false));
                                }
                            } catch (PackageManager.NameNotFoundException e) {
                                foreground.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            loadImage(foreground, 19, PreferencesGraphicsFragment.graphics[19][2].toString(), color2);
                        }
                    }
                } else if (text.equalsIgnoreCase("ToggleRotate")) {
                    try {
                        if (android.provider.Settings.System.getInt(mContext.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION) == 0) {
                            loadImage(foreground, 20, PreferencesGraphicsFragment.graphics[20][2].toString(), color2);
                        } else {
                            loadImage(foreground, 21, PreferencesGraphicsFragment.graphics[21][2].toString(), color2);
                        }
                    } catch (Throwable e) {
                        loadImage(foreground, 21, PreferencesGraphicsFragment.graphics[21][2].toString(), color2);
                    }
                    rotateIcon_Image.add(foreground);
                    rotateIcon_Color.add(color2);
                } else if (text.equalsIgnoreCase("MediaPrevious")) {
                    loadImage(foreground, 22, PreferencesGraphicsFragment.graphics[22][2].toString(), color2);
                } else if (text.equalsIgnoreCase("MediaPlayPause")) {
                    try {
                        if (!mediaPlaying) {
                            loadImage(foreground, 23, PreferencesGraphicsFragment.graphics[23][2].toString(), color2);
                        } else {
                            loadImage(foreground, 24, PreferencesGraphicsFragment.graphics[24][2].toString(), color2);
                        }
                    } catch (Throwable t) {
                        loadImage(foreground, 23, PreferencesGraphicsFragment.graphics[23][2].toString(), color2);
                    }
                    playPauseIcon_Image.add(foreground);
                    playPauseIcon_Color.add(color2);
                } else if (text.equalsIgnoreCase("MediaNext")) {
                    loadImage(foreground, 25, PreferencesGraphicsFragment.graphics[25][2].toString(), color2);
                } else if (text.equalsIgnoreCase("ToggleWifi")) {
                    try {
                        if (!wifiActive) {
                            loadImage(foreground, 26, PreferencesGraphicsFragment.graphics[26][2].toString(), color2);
                        } else {
                            loadImage(foreground, 27, PreferencesGraphicsFragment.graphics[27][2].toString(), color2);
                        }
                    } catch (Throwable t) {
                        loadImage(foreground, 26, PreferencesGraphicsFragment.graphics[26][2].toString(), color2);
                    }
                    toggleWifi_Image.add(foreground);
                    toggleWifi_Color.add(color2);
                } else if (text.equalsIgnoreCase("ToggleBluetooth")) {
                    try {
                        if (!bluetoothActive) {
                            loadImage(foreground, 28, PreferencesGraphicsFragment.graphics[28][2].toString(), color2);
                        } else {
                            loadImage(foreground, 29, PreferencesGraphicsFragment.graphics[29][2].toString(), color2);
                        }
                    } catch (Throwable t) {
                        loadImage(foreground, 28, PreferencesGraphicsFragment.graphics[28][2].toString(), color2);
                    }
                    toggleBluetooth_Image.add(foreground);
                    toggleBluetooth_Color.add(color2);
                } else if (text.equalsIgnoreCase("ToggleData")) {
                    try {
                        if (!dataActive) {
                            loadImage(foreground, 30, PreferencesGraphicsFragment.graphics[30][2].toString(), color2);
                        } else {
                            loadImage(foreground, 31, PreferencesGraphicsFragment.graphics[31][2].toString(), color2);
                        }
                    } catch (Throwable t) {
                        loadImage(foreground, 30, PreferencesGraphicsFragment.graphics[30][2].toString(), color2);
                    }
                    toggleData_Image.add(foreground);
                    toggleData_Color.add(color2);
                } else if (text.equalsIgnoreCase("RebootFlashMode")) {
                    loadImage(foreground, 32, PreferencesGraphicsFragment.graphics[32][2].toString(), color2);
                } else if (text.equalsIgnoreCase("LockPhone")) {
                    loadImage(foreground, 33, PreferencesGraphicsFragment.graphics[33][2].toString(), color2);
                } else if (text.equalsIgnoreCase("SilentMode")) {
                    if (amRingerMode == AudioManager.RINGER_MODE_SILENT) {
                        loadImage(foreground, 13, PreferencesGraphicsFragment.graphics[13][2].toString(), color2);
                    } else {
                        loadImage(foreground, 12, PreferencesGraphicsFragment.graphics[12][2].toString(), color2);
                    }
                    silentModeIcon_Image.add(foreground);
                    silentModeIcon_Color.add(color2);
                }
            } else {
                if (text.contains(".")) {
                    PackageManager pm = mContext.getPackageManager();
                    try {
                        text = pm.getApplicationInfo(text.split("/")[0], 0).loadLabel(pm).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                    }
                }
                TextDrawable drawable = TextDrawable.builder().beginConfig().textColor(Color.parseColor(color2)).endConfig()
                        .buildRound(finalText.substring(0, 1), Color.parseColor(color1));
                background.setImageDrawable(drawable);
                foreground.setVisibility(View.GONE);
            }
        } catch (Throwable t) {
            Log.e("NPM", "Failed to create Circle Icon.", t);
        }
    }

    private void loadImage(final ImageView image, final int id, final String fileName, final String color) {
        if (new File(mContext.getFilesDir().getPath() + "/images/" + fileName + ".png").exists()) {
            XposedMainActivity.mImageLoader.displayImage("file://" + mContext.getFilesDir().getPath() + "/images/" + fileName + ".png",
                    image, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            image.setImageBitmap(null);
                            image.setPadding((int) helper.convertDpToPixel(5, mContext), (int) helper.convertDpToPixel(5, mContext), (int) helper.convertDpToPixel(5, mContext), (int) helper.convertDpToPixel(5, mContext));
                            image.setColorFilter(Color.parseColor("#ffffff"),
                                    android.graphics.PorterDuff.Mode.DST);
                            if (animationPrefs.getInt(PreferencesAnimationsFragment.names[7][1].toString(), PreferencesAnimationsFragment.defaultTypes[7]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                                image.startAnimation(helper.getAnimation(mContext, animationPrefs, 6, true));
                            }
                            image.setVisibility(View.INVISIBLE);
                            super.onLoadingStarted(imageUri, view);
                        }

                        @Override
                        public void onLoadingComplete(final String imageUri, final View view, Bitmap loadedImage) {
                            image.setPadding((int) mGraphicsPadding, (int) mGraphicsPadding, (int) mGraphicsPadding, (int) mGraphicsPadding);
                            image.setImageBitmap(loadedImage);
                            if (mColorizeNonStockIcons) {
                                image.setColorFilter(Color.parseColor(color),
                                        android.graphics.PorterDuff.Mode.MULTIPLY);
                            }
                            image.setVisibility(View.VISIBLE);
                            if (animationPrefs.getInt(PreferencesAnimationsFragment.names[7][1].toString(), PreferencesAnimationsFragment.defaultTypes[7]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                                image.startAnimation(helper.getAnimation(mContext, animationPrefs, 6, false));
                            }
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            Log.w("NPM:xposedDialog", "Failed to load image '" + imageUri + "': " + failReason.getCause());
                            image.setImageDrawable(mContext.getResources().getDrawable((int) PreferencesGraphicsFragment.graphics[id][1]));
                            image.setColorFilter(Color.parseColor(color),
                                    android.graphics.PorterDuff.Mode.MULTIPLY);
                            image.setVisibility(View.VISIBLE);
                            if (animationPrefs.getInt(PreferencesAnimationsFragment.names[7][1].toString(), PreferencesAnimationsFragment.defaultTypes[7]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                                image.startAnimation(helper.getAnimation(mContext, animationPrefs, 6, false));
                            }
                        }
                    });
        } else {
            image.setPadding((int) mGraphicsPadding, (int) mGraphicsPadding, (int) mGraphicsPadding, (int) mGraphicsPadding);
            image.setImageDrawable(mContext.getResources().getDrawable((int) PreferencesGraphicsFragment.graphics[id][1]));
            image.setColorFilter(Color.parseColor(color),
                    android.graphics.PorterDuff.Mode.MULTIPLY);
            image.setVisibility(View.VISIBLE);
            if (animationPrefs.getInt(PreferencesAnimationsFragment.names[7][1].toString(), PreferencesAnimationsFragment.defaultTypes[7]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                image.startAnimation(helper.getAnimation(mContext, animationPrefs, 6, false));
            }
        }
    }

    private void performMenuClick(final int id, final String name, final View v) {
        if (name.contains("multipage:")) {
            if (frame3.getVisibility() == View.GONE) {
                ListContainer2.removeAllViews();
            } else {
                ListContainer.removeAllViews();
            }

            String page = name.split("\\:")[1];
            if (!page.equalsIgnoreCase("Root")) {
                if (confirmDialog.isEmpty()) SubDialogs.add(page);
                boolean firstItemDrawn = false;
                boolean inRightSpot = false;
                ArrayList<String> MultiPage = new ArrayList<String>();
                //Log.i("NPM:itemLoader","Searching for multi item with the code "+page);
                //Log.i("NPM:itemLoader","Total entries to check: "+XposedMainActivity.mItems.size());
                for (int i = 0; i < XposedMainActivity.mItems.size(); i++) {
                    if (XposedMainActivity.mItems.get(i).getType() == visibilityOrder_ListAdapter.TYPE_MULTIPAGE_START) {
                        if (XposedMainActivity.mItems.get(i).getOnPage().equals(page)) {
                            firstItemDrawn = false;
                            //MultiPage.add(XposedMainActivity.mItems.get(i));
                            inRightSpot = true;
                            //Log.i("NPM:itemLoader","Got the right spot!");
                        } else {
                            if (inRightSpot && MultiPage.size() == 0) {
                                firstItemDrawn = false;
                                MultiPage.add(XposedMainActivity.mItems.get(i).getTitle(1));
                                page = XposedMainActivity.mItems.get(i).getTitle(1);
                            }
                            //Log.i("NPM:itemLoader","Found multi item with another code "+XposedMainActivity.mItems.get(i).getTitle()+(inRightSpot ? "" : ", but ignoring."));
                        }
                    } else if (XposedMainActivity.mItems.get(i).getType() == visibilityOrder_ListAdapter.TYPE_MULTIPAGE_END) {
                        if (XposedMainActivity.mItems.get(i).getOnPage().equals(page)) {
                            inRightSpot = false;
                            if (MultiPage.size() > 0) {
                                //page = MultiPage.get(MultiPage.size()-1);
                                MultiPage.remove(MultiPage.size() - 1);
                            }
                            //Log.i("NPM:itemLoader","Left the right spot.");
                        }
                    } else if (inRightSpot) {
                        if (XposedMainActivity.mItems.get(i).getType() == visibilityOrder_ListAdapter.TYPE_NORMAL) {
                            if (XposedMainActivity.mItems.get(i).getOnPage().contains(page)) {
                                if (MultiPage.size() == 0 || !firstItemDrawn) {
                                    if (!mKeyguardShowing || (mKeyguardShowing && !XposedMainActivity.mItems.get(i).getHideOnLockScreen())) {
                                        if (frame3.getVisibility() == View.GONE) {
                                            ListContainer2.addView(createNormalItem(i, XposedMainActivity.mItems.get(i).getTitle(1), (MultiPage.size() > 0 ? page : null), XposedMainActivity.mItems.get(i).getHideDesc(), XposedMainActivity.mItems.get(i).getText(1)));
                                        } else {
                                            ListContainer.addView(createNormalItem(i, XposedMainActivity.mItems.get(i).getTitle(1), (MultiPage.size() > 0 ? page : null), XposedMainActivity.mItems.get(i).getHideDesc(), XposedMainActivity.mItems.get(i).getText(1)));
                                        }
                                    }
                                }
                                if (!firstItemDrawn) {
                                    firstItemDrawn = true;
                                    if (MultiPage.size() > 0) {
                                        page = MultiPage.get(MultiPage.size() - 1);
                                        //MultiPage.remove(MultiPage.size()-1);
                                    }
                                }
                                //Log.i("NPM:itemLoader","Added "+XposedMainActivity.mItems.get(i).getTitle()+" in "+page);
                                //if(MultiPage.size()>0) MultiPage.remove(MultiPage.size()-1);
                            }
                        } else if (XposedMainActivity.mItems.get(i).getType() == visibilityOrder_ListAdapter.TYPE_MULTI) {
                            if (MultiPage.size() == 0 && XposedMainActivity.mItems.get(i).getOnPage().contains(page)) {
                                if (!mKeyguardShowing || (mKeyguardShowing && !XposedMainActivity.mItems.get(i).getHideOnLockScreen())) {
                                    if (frame3.getVisibility() == View.GONE) {
                                        ListContainer2.addView(createMultiItem(i, XposedMainActivity.mItems.get(i).getTitle(1)));
                                    } else {
                                        ListContainer.addView(createMultiItem(i, XposedMainActivity.mItems.get(i).getTitle(1)));
                                    }
                                }
                                //Log.i("NPM:itemLoader","Added "+XposedMainActivity.mItems.get(i).getTitle() +" in "+page);
                            }
                        }
                    }
                }
            } else {
                final ArrayList<String> MultiPage = new ArrayList<String>();
                boolean firstItemDrawn = false;
                for (int i = 0; i < XposedMainActivity.mItems.size(); i++) {
                    if (XposedMainActivity.mItems.get(i).getType() != -1) {
                        if (XposedMainActivity.mItems.get(i).getType() == visibilityOrder_ListAdapter.TYPE_NORMAL) {
                            if (MultiPage.size() == 0 || (MultiPage.size() == 1 && !firstItemDrawn)) {
                                if (!mKeyguardShowing || (mKeyguardShowing && !XposedMainActivity.mItems.get(i).getHideOnLockScreen())) {
                                    if (frame3.getVisibility() == View.GONE) {
                                        ListContainer2.addView(createNormalItem(i, XposedMainActivity.mItems.get(i).getTitle(1), (MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) : null), XposedMainActivity.mItems.get(i).getHideDesc(), XposedMainActivity.mItems.get(i).getText(1)));
                                    } else {
                                        ListContainer.addView(createNormalItem(i, XposedMainActivity.mItems.get(i).getTitle(1), (MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) : null), XposedMainActivity.mItems.get(i).getHideDesc(), XposedMainActivity.mItems.get(i).getText(1)));
                                    }
                                }
                                firstItemDrawn = true;
                            }
                        } else if (XposedMainActivity.mItems.get(i).getType() == visibilityOrder_ListAdapter.TYPE_MULTI) {
                            if (MultiPage.size() == 0) {
                                if (!mKeyguardShowing || (mKeyguardShowing && !XposedMainActivity.mItems.get(i).getHideOnLockScreen())) {
                                    if (frame3.getVisibility() == View.GONE) {
                                        ListContainer2.addView(createMultiItem(i, XposedMainActivity.mItems.get(i).getTitle(1)));
                                    } else {
                                        ListContainer.addView(createMultiItem(i, XposedMainActivity.mItems.get(i).getTitle(1)));
                                    }
                                }
                            }
                        } else if (XposedMainActivity.mItems.get(i).getType() == visibilityOrder_ListAdapter.TYPE_MULTIPAGE_START) {
                            MultiPage.add(orderPrefs.getString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", "null"));
                            firstItemDrawn = false;
                        } else if (orderPrefs.getInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", -1) == visibilityOrder_ListAdapter.TYPE_MULTIPAGE_END) {
                            if (MultiPage.size() > 0) MultiPage.remove(MultiPage.size() - 1);
                        }
                    }
                }
            }
            if (frame3.getVisibility() == View.GONE) {
                frame3.setVisibility(View.VISIBLE);
                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[4][1].toString(), PreferencesAnimationsFragment.defaultTypes[1]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                    if (confirmDialog.isEmpty()) {
                        frame.startAnimation(helper.getAnimation(mContext, animationPrefs, 3, true));
                    }
                    frame3.startAnimation(helper.getAnimation(mContext, animationPrefs, 3, false));
                }
                if (!confirmDialog.isEmpty()) {
                    confirmDialog = "";
                }
                frame.setVisibility(View.GONE);
            } else {
                frame.setVisibility(View.VISIBLE);
                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[4][1].toString(), PreferencesAnimationsFragment.defaultTypes[1]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                    frame3.startAnimation(helper.getAnimation(mContext, animationPrefs, 3, true));
                    frame.startAnimation(helper.getAnimation(mContext, animationPrefs, 3, false));
                }
                frame3.setVisibility(View.GONE);
            }
        } else {
            if (id > -1 && XposedMainActivity.mItems.get(id).getLockedWithPassword() && !mPasswordLock.isEmpty()) {
                if (!confirmDialog.equalsIgnoreCase(name)) {
                    confirmDialog = name;
                    SubDialogs.add("EnterPassword");
                    EnterPasswordInput.setVisibility(View.VISIBLE);
                    EnterPasswordFingerprint.setVisibility(View.GONE);
                    EnterPasswordAction.setText(getString(R.string.powerMenu_LockedWithPassword));
                    EnterPasswordError.setText("");
                    EnterPasswordError.setVisibility(View.GONE);
                    EnterPasswordOk.setText(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_OK]);
                    if (mFingerprintHandler != null) mFingerprintHandler.stopAuth();
                    if (mFingerprintManager != null && mUseFingerprint && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED) {
                            if (mFingerprintManager.hasEnrolledFingerprints()) {
                                EnterPasswordInput.setVisibility(View.GONE);
                                EnterPasswordFingerprint.setVisibility(View.VISIBLE);
                                EnterPasswordAction.setText(getString(R.string.powerMenu_PasswordLockedWithFingerprint));
                                EnterPasswordOk.setText(getString(R.string.powerMenu_PasswordUsePassword));

                                mFingerprintHandler = new FingerprintHandler(mContext);
                                if (mFingerprintHandler.isCipherReady()) {
                                    mFingerprintHandler.addInterface(new FingerprintHandler.FingerprintInterface() {
                                        @Override
                                        public void onFingerprintSuccess(FingerprintManager.AuthenticationResult result) {
                                            SubDialogs.clear();
                                            performMenuClick(id, name, v);
                                        }

                                        @Override
                                        public void onFingerprintFailed(int errorCode, String errorMsg) {
                                            if (isAdded()) {
                                                if (errorCode == -1) {
                                                    EnterPasswordError.setText("Unknown problem: " + errorMsg);
                                                    if(EnterPasswordError.getVisibility() == View.GONE) {
                                                        EnterPasswordError.setVisibility(View.VISIBLE);
                                                        //EnterPasswordError.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
                                                    }
                                                    mHandler.removeCallbacks(hidePasswordErrorRunnable);
                                                    mHandler.postDelayed(hidePasswordErrorRunnable, 4000L);
                                                } else if (errorCode == FingerprintManager.FINGERPRINT_ERROR_LOCKOUT) {
                                                    EnterPasswordError.setText(errorMsg);
                                                    if(EnterPasswordError.getVisibility() == View.GONE) {
                                                        EnterPasswordError.setVisibility(View.VISIBLE);
                                                        //EnterPasswordError.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
                                                    }
                                                    mHandler.removeCallbacks(hidePasswordErrorRunnable);
                                                    mHandler.postDelayed(hidePasswordErrorRunnable, 4000L);
                                                    EnterPasswordAction.setText(getString(R.string.powerMenu_LockedWithPassword));
                                                    EnterPasswordFingerprint.setVisibility(View.GONE);
                                                    EnterPasswordInput.setVisibility(View.VISIBLE);
                                                    mFingerprintHandler.stopAuth();
                                                    EnterPasswordOk.setText(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_OK]);
                                                    InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                                                    EnterPasswordInput.requestFocus();
                                                    IBinder windowToken = EnterPasswordInput.getWindowToken();
                                                    inputManager.showSoftInput(EnterPasswordInput, InputMethodManager.SHOW_IMPLICIT);
                                                } else if (errorCode != FingerprintManager.FINGERPRINT_ERROR_CANCELED) {
                                                    EnterPasswordError.setText(getString(R.string.powerMenu_FingerprintFailed));
                                                    if(EnterPasswordError.getVisibility() == View.GONE) {
                                                        EnterPasswordError.setVisibility(View.VISIBLE);
                                                        //EnterPasswordError.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
                                                    }
                                                    mHandler.removeCallbacks(hidePasswordErrorRunnable);
                                                    mHandler.postDelayed(hidePasswordErrorRunnable, 4000L);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFingerprintHelp(int helpId, String helpString) {
                                            EnterPasswordError.setText(helpString);
                                            if(EnterPasswordError.getVisibility() == View.GONE) {
                                                EnterPasswordError.setVisibility(View.VISIBLE);
                                                //EnterPasswordError.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
                                            }
                                            mHandler.removeCallbacks(hidePasswordErrorRunnable);
                                            mHandler.postDelayed(hidePasswordErrorRunnable, 4000L);
                                        }
                                    });
                                    mFingerprintHandler.startAuth(mFingerprintManager);
                                }
                            }
                        }
                    } else {
                        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        EnterPasswordInput.requestFocus();
                        IBinder windowToken = EnterPasswordInput.getWindowToken();
                        inputManager.showSoftInput(EnterPasswordInput, InputMethodManager.SHOW_IMPLICIT);
                    }
                    EnterPasswordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            checkPassword(id, name, v);
                            return true;
                        }
                    });
                    EnterPasswordCancel.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View p1) {
                            try {
                                InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                                IBinder windowToken = EnterPasswordInput.getWindowToken();
                                inputManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);
                            } catch (Throwable t) {
                            }
                            dismissThis();
                        }
                    });
                    EnterPasswordOk.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View p1) {
                            if (EnterPasswordOk.getText().toString().equalsIgnoreCase(getString(R.string.powerMenu_PasswordUsePassword))) {
                                EnterPasswordAction.setText(getString(R.string.powerMenu_LockedWithPassword));
                                EnterPasswordFingerprint.setVisibility(View.GONE);
                                EnterPasswordInput.setVisibility(View.VISIBLE);
                                mFingerprintHandler.stopAuth();
                                EnterPasswordOk.setText(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_OK]);
                                InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                                EnterPasswordInput.requestFocus();
                                IBinder windowToken = EnterPasswordInput.getWindowToken();
                                inputManager.showSoftInput(EnterPasswordInput, InputMethodManager.SHOW_IMPLICIT);
                            } else {
                                checkPassword(id, name, v);
                            }
                        }
                    });
                    if (frame3.getVisibility() == View.GONE) {
                        frameEnterPassword.setVisibility(View.VISIBLE);
                        if (animationPrefs.getInt(PreferencesAnimationsFragment.names[4][1].toString(), PreferencesAnimationsFragment.defaultTypes[1]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                            frame.startAnimation(helper.getAnimation(mContext, animationPrefs, 3, true));
                            frameEnterPassword.startAnimation(helper.getAnimation(mContext, animationPrefs, 3, false));
                        }
                        frame.setVisibility(View.GONE);
                    } else {
                        frameEnterPassword.setVisibility(View.VISIBLE);
                        if (animationPrefs.getInt(PreferencesAnimationsFragment.names[4][1].toString(), PreferencesAnimationsFragment.defaultTypes[1]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                            frame3.startAnimation(helper.getAnimation(mContext, animationPrefs, 3, true));
                            frameEnterPassword.startAnimation(helper.getAnimation(mContext, animationPrefs, 3, false));
                        }
                        frame3.setVisibility(View.GONE);
                    }
                    return;
                }
            }
            if (RequireConfirmation && (name.equalsIgnoreCase("Shutdown") ||
                    name.equalsIgnoreCase("Reboot") ||
                    name.equalsIgnoreCase("SoftReboot") ||
                    name.equalsIgnoreCase("Recovery") ||
                    name.equalsIgnoreCase("Bootloader") ||
                    name.equalsIgnoreCase("SafeMode") ||
                    name.equalsIgnoreCase("RebootFlashMode"))) {
                if (!confirmDialog.equalsIgnoreCase(name)) {
                    confirmDialog = name;
                    SubDialogs.add("Confirm");
                    confirmAction.setText(mContext.getString(R.string.powerMenu_SureToRebootPowerOff).split("\\|")[(name.equalsIgnoreCase("Shutdown") ? 1 : 0)]);
                    confirmNo.setText(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_NO]);
                    confirmNo.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View p1) {

                            dismissThis();
                        }
                    });
                    confirmYes.setText(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_YES]);
                    confirmYes.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View p1) {
                            SubDialogs.clear();
                            performMenuClick(id, name, v);
                        }
                    });
                    if (frame3.getVisibility() == View.GONE) {
                        frameConfirm.setVisibility(View.VISIBLE);
                        if (animationPrefs.getInt(PreferencesAnimationsFragment.names[4][1].toString(), PreferencesAnimationsFragment.defaultTypes[1]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                            frame.startAnimation(helper.getAnimation(mContext, animationPrefs, 3, true));
                            frameConfirm.startAnimation(helper.getAnimation(mContext, animationPrefs, 3, false));
                        }
                        frame.setVisibility(View.GONE);
                    } else {
                        frameConfirm.setVisibility(View.VISIBLE);
                        if (animationPrefs.getInt(PreferencesAnimationsFragment.names[4][1].toString(), PreferencesAnimationsFragment.defaultTypes[1]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                            frame3.startAnimation(helper.getAnimation(mContext, animationPrefs, 3, true));
                            frameConfirm.startAnimation(helper.getAnimation(mContext, animationPrefs, 3, false));
                        }
                        frame3.setVisibility(View.GONE);
                    }
                    return;
                }
            }
            //SubDialogs.clear();
            if (!XposedMainActivity.mItems.get(id).getShortcutUri(1).isEmpty() || !XposedMainActivity.mItems.get(id).getShortcutUri(2).isEmpty() || !XposedMainActivity.mItems.get(id).getShortcutUri(3).isEmpty()) {
                if (!mPreviewMode) {
                    SubDialogs.clear();
                    dismissThis();
                    Intent intent = null;
                    String uri = "";
                    try {
                        if (XposedMainActivity.mItems.get(id).getType() == visibilityOrder_ListAdapter.TYPE_NORMAL) {
                            uri = XposedMainActivity.mItems.get(id).getShortcutUri(1);
                            intent = Intent.parseUri(XposedMainActivity.mItems.get(id).getShortcutUri(1), Intent.URI_INTENT_SCHEME);
                        } else {
                            for (int i = 1; i <= 3; i++) {
                                if (name.equalsIgnoreCase(XposedMainActivity.mItems.get(id).getTitle(i))) {
                                    uri = XposedMainActivity.mItems.get(id).getShortcutUri(i);
                                    intent = Intent.parseUri(XposedMainActivity.mItems.get(id).getShortcutUri(i), Intent.URI_INTENT_SCHEME);
                                }
                            }
                        }
                        mContext.startActivity(intent);
                    } catch (Throwable e) {
                        Log.e("NPM:xposedDialog", "No package with uri '" + uri + "' found...", e);
                    }
                }
            } else if (name.equalsIgnoreCase("Shutdown")) {
                canDismiss = false;

                //revealView.setVisibility(View.VISIBLE);
                final int color = Color.parseColor(colorPrefs.getString("DialogShutdown_Backgroundcolor", "#d32f2f"));
                final Point p = getLocationInView(revealView, v);
                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[0]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                    Animation anim = helper.getAnimation(mContext, animationPrefs, 0, false);
                    if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[0]) == 1) {
                        revealView.reveal(p.x, p.y, color, 0, anim.getDuration(), null);
                    } else {
                        revealView.reveal(p.x, p.y, color, 0, 0, null);
                        if (frame3.getVisibility() == View.VISIBLE) {
                            frame3.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else if (frame.getVisibility() == View.VISIBLE) {
                            frame.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else if (frameConfirm.getVisibility() == View.VISIBLE) {
                            frameConfirm.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else if (frameEnterPassword.getVisibility() == View.VISIBLE) {
                            frameEnterPassword.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        }
                        revealView.startAnimation(anim);
                        frame2.startAnimation(anim);
                    }
                } else {
                    revealView.reveal(p.x, p.y, color, 0, 0, null);
                }

                if (selectedView == v) {
                    //revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
                    selectedView = null;
                } else {
                    //revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
                    selectedView = v;
                }

                ((XposedMainActivity) mContext).revealFromTop(colorPrefs.getString("DialogShutdown_Revealcolor", "#ff0097a7"));
                frame.setVisibility(View.GONE);
                frame3.setVisibility(View.GONE);
                frameConfirm.setVisibility(View.GONE);
                frameEnterPassword.setVisibility(View.GONE);
                frame2.setVisibility(View.VISIBLE);

                status.setText(R.string.powerMenuMain_Shutdown);
                status_detail.setText(R.string.powerMenu_Shuttingdown);

                setProgressScreen("Shutdown");
                if (!mPreviewMode) {
                    if (HookShutdownThread) {
                        XposedUtils.doShutdown(mContext, 0);
                    } else if (UseRootCommands) {
                        new BackgroundThread(SHUTDOWN).start();
                    } else {
                        Intent launchIntent = new Intent(XposedMain.NPM_ACTION_BROADCAST_SHUTDOWN);
                        mContext.sendOrderedBroadcast(launchIntent, null);
                    }
                }
            } else if (name.equalsIgnoreCase("Reboot")) {
                canDismiss = false;

                //revealView.setVisibility(View.VISIBLE);
                final int color = Color.parseColor(colorPrefs.getString("DialogReboot_Backgroundcolor", "#3f51b5"));
                final Point p = getLocationInView(revealView, v);
                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[0]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                    Animation anim = helper.getAnimation(mContext, animationPrefs, 0, false);
                    if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[0]) == 1) {
                        revealView.reveal(p.x, p.y, color, 0, anim.getDuration(), null);
                    } else {
                        revealView.reveal(p.x, p.y, color, 0, 0, null);
                        if (frame3.getVisibility() == View.VISIBLE) {
                            frame3.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else if (frame.getVisibility() == View.VISIBLE) {
                            frame.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else if (frameConfirm.getVisibility() == View.VISIBLE) {
                            frameConfirm.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else if (frameEnterPassword.getVisibility() == View.VISIBLE) {
                            frameEnterPassword.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        }
                        revealView.startAnimation(anim);
                        frame2.startAnimation(anim);
                    }
                } else {
                    revealView.reveal(p.x, p.y, color, 0, 0, null);
                }

                if (selectedView == v) {
                    //revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
                    selectedView = null;
                } else {
                    //revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
                    selectedView = v;
                }

                ((XposedMainActivity) mContext).revealFromTop(colorPrefs.getString("DialogReboot_Revealcolor", "#ff0097a7"));
                frame.setVisibility(View.GONE);
                frame3.setVisibility(View.GONE);
                frameConfirm.setVisibility(View.GONE);
                frameEnterPassword.setVisibility(View.GONE);
                frame2.setVisibility(View.VISIBLE);

                status.setText(R.string.powerMenuMain_Reboot);
                status_detail.setText(R.string.powerMenu_Rebooting);

                setProgressScreen("Reboot");
                if (!mPreviewMode) {
                    if (HookShutdownThread) {
                        XposedUtils.doReboot(mContext, 0);
                    } else if (UseRootCommands) {
                        new BackgroundThread(REBOOT_CMD).start();
                    } else {
                        Intent launchIntent = new Intent(XposedMain.NPM_ACTION_BROADCAST_REBOOT);
                        mContext.sendOrderedBroadcast(launchIntent, null);
                    }
                }
            } else if (name.equalsIgnoreCase("SoftReboot")) {
                canDismiss = false;

                //revealView.setVisibility(View.VISIBLE);
                final int color = Color.parseColor(colorPrefs.getString("DialogSoftReboot_Backgroundcolor", "#e91e63"));
                final Point p = getLocationInView(revealView, v);
                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[0]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                    Animation anim = helper.getAnimation(mContext, animationPrefs, 0, false);
                    if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[0]) == 1) {
                        revealView.reveal(p.x, p.y, color, 0, anim.getDuration(), null);
                    } else {
                        revealView.reveal(p.x, p.y, color, 0, 0, null);
                        if (frame3.getVisibility() == View.VISIBLE) {
                            frame3.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else if (frame.getVisibility() == View.VISIBLE) {
                            frame.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else if (frameConfirm.getVisibility() == View.VISIBLE) {
                            frameConfirm.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else if (frameEnterPassword.getVisibility() == View.VISIBLE) {
                            frameEnterPassword.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        }
                        revealView.startAnimation(anim);
                        frame2.startAnimation(anim);
                    }
                } else {
                    revealView.reveal(p.x, p.y, color, 0, 0, null);
                }

                if (selectedView == v) {
                    //revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
                    selectedView = null;
                } else {
                    //revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
                    selectedView = v;
                }

                ((XposedMainActivity) mContext).revealFromTop(colorPrefs.getString("DialogSoftReboot_Revealcolor", "#ff0097a7"));
                frame.setVisibility(View.GONE);
                frame3.setVisibility(View.GONE);
                frameConfirm.setVisibility(View.GONE);
                frameEnterPassword.setVisibility(View.GONE);
                frame2.setVisibility(View.VISIBLE);

                status.setText(R.string.powerMenuMain_SoftReboot);
                status_detail.setText(R.string.powerMenu_Rebooting);

                setProgressScreen("SoftReboot");
                if (!mPreviewMode) {
                    if (HookShutdownThread) {
                        XposedUtils.doReboot(mContext, 1);
                    } else if (UseRootCommands) {
                        new BackgroundThread(REBOOT_SOFT_REBOOT_CMD).start();
                    } else {
                        Intent launchIntent = new Intent(XposedMain.NPM_ACTION_BROADCAST_SOFTREBOOT);
                        mContext.sendOrderedBroadcast(launchIntent, null);
                    }
                }
            } else if (name.equalsIgnoreCase("Screenshot")) {
                if (!mPreviewMode) {
                    SubDialogs.clear();
                    dismissThis();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {

                            Intent takeScreenshotBC = new Intent();
                            takeScreenshotBC.setAction(XposedMain.NPM_ACTION_BROADCAST_SCREENSHOT);
                            mContext.sendOrderedBroadcast(takeScreenshotBC, null);
                            //Toast.makeText(mContext, "Taking screenshot...",Toast.LENGTH_SHORT).show();
                        }
                    }, preferences.getLong("ScreenshotDelay", 1000));
                }
            } else if (name.equalsIgnoreCase("Screenrecord")) {
                if (!mPreviewMode) {
                    SubDialogs.clear();
                    dismissThis();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {

                            Intent takeScreenrecordBC = new Intent();
                            takeScreenrecordBC.setAction(XposedMain.NPM_ACTION_BROADCAST_SCREENRECORD);
                            mContext.sendOrderedBroadcast(takeScreenrecordBC, null);
                            //Toast.makeText(mContext, "Taking screenrecord...",Toast.LENGTH_SHORT).show();
                        }
                    }, 1000);
                }
            } else if (name.equalsIgnoreCase("Flashlight")) {
                if (!mPreviewMode) {
                    if (mHideOnClick) {
                        SubDialogs.clear();
                        dismissThis();
                    }
                    final Handler handler = new Handler();
                    new Thread() {
                        @Override
                        public void run() {
                            handler.post(new Runnable() {

                                @Override
                                public void run() {
                                    toggleTorch(false);
                                }
                            });
                        }
                    }.start();
                }
            } else if (name.equalsIgnoreCase("ExpandedDesktop")) {
                if (!mPreviewMode) {
                    if (mHideOnClick) {
                        SubDialogs.clear();
                        dismissThis();
                    }
                    Intent launchIntent = new Intent("gravitybox.intent.action.TOGGLE_EXPANDED_DESKTOP");
                    mContext.sendOrderedBroadcast(launchIntent, null);
                }
            } else if (name.equalsIgnoreCase("AirplaneMode")) {
                if (!mPreviewMode) {
                    if (mHideOnClick) {
                        SubDialogs.clear();
                        dismissThis();
                    }
                    Intent launchIntent = new Intent(XposedMain.NPM_ACTION_BROADCAST_TOGGLEAIRPLANEMODE);
                    mContext.sendOrderedBroadcast(launchIntent, null);
                }
            } else if (name.equalsIgnoreCase("RestartUI")) {
                if (!mPreviewMode) {
                    SubDialogs.clear();
                    dismissThis();
                    Intent launchIntent = new Intent(XposedMain.NPM_ACTION_BROADCAST_KILLSYSTEMUI);
                    mContext.sendOrderedBroadcast(launchIntent, null);
                }
            } else if (name.equalsIgnoreCase("SoundMode")) {
                if (!mPreviewMode) {
                    if (mHideOnClick) {
                        SubDialogs.clear();
                        dismissThis();
                    }
                    if (amRingerMode == AudioManager.RINGER_MODE_NORMAL) {
                        am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    } else if (amRingerMode == AudioManager.RINGER_MODE_SILENT) {
                        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    } else if (amRingerMode == AudioManager.RINGER_MODE_VIBRATE) {
                        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    }
                }
            } else if (name.equalsIgnoreCase("Recovery")) {
                canDismiss = false;

                //revealView.setVisibility(View.VISIBLE);
                final int color = Color.parseColor(colorPrefs.getString("DialogRecovery_Backgroundcolor", "#8bc34a"));
                final Point p = getLocationInView(revealView, v);
                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[0]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                    Animation anim = helper.getAnimation(mContext, animationPrefs, 0, false);
                    if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[0]) == 1) {
                        revealView.reveal(p.x, p.y, color, 0, anim.getDuration(), null);
                    } else {
                        revealView.reveal(p.x, p.y, color, 0, 0, null);
                        if (frame3.getVisibility() == View.VISIBLE) {
                            frame3.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else if (frame.getVisibility() == View.VISIBLE) {
                            frame.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else if (frameConfirm.getVisibility() == View.VISIBLE) {
                            frameConfirm.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else if (frameEnterPassword.getVisibility() == View.VISIBLE) {
                            frameEnterPassword.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        }
                        revealView.startAnimation(anim);
                        frame2.startAnimation(anim);
                    }
                } else {
                    revealView.reveal(p.x, p.y, color, 0, 0, null);
                }

                if (selectedView == v) {
                    //revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
                    selectedView = null;
                } else {
                    //revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
                    selectedView = v;
                }

                ((XposedMainActivity) mContext).revealFromTop(colorPrefs.getString("DialogRecovery_Revealcolor", "#ff0097a7"));
                frame.setVisibility(View.GONE);
                frame3.setVisibility(View.GONE);
                frameConfirm.setVisibility(View.GONE);
                frameEnterPassword.setVisibility(View.GONE);
                frame2.setVisibility(View.VISIBLE);

                status.setText(R.string.powerMenuBottom_Recovery);
                status_detail.setText(R.string.powerMenu_Rebooting);

                setProgressScreen("Recovery");
                if (!mPreviewMode) {
                    if (HookShutdownThread) {
                        XposedUtils.doReboot(mContext, 2);
                    } else if (UseRootCommands) {
                        new BackgroundThread(REBOOT_RECOVERY_CMD).start();
                    } else {
                        Intent launchIntent = new Intent(XposedMain.NPM_ACTION_BROADCAST_REBOOTRECOVERY);
                        mContext.sendOrderedBroadcast(launchIntent, null);
                    }
                }
            } else if (name.equalsIgnoreCase("Bootloader")) {
                canDismiss = false;

                //revealView.setVisibility(View.VISIBLE);
                final int color = Color.parseColor(colorPrefs.getString("DialogBootloader_Backgroundcolor", "#277b71"));
                final Point p = getLocationInView(revealView, v);
                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[0]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                    Animation anim = helper.getAnimation(mContext, animationPrefs, 0, false);
                    if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[0]) == 1) {
                        revealView.reveal(p.x, p.y, color, 0, anim.getDuration(), null);
                    } else {
                        revealView.reveal(p.x, p.y, color, 0, 0, null);
                        if (frame3.getVisibility() == View.VISIBLE) {
                            frame3.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else if (frame.getVisibility() == View.VISIBLE) {
                            frame.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else if (frameConfirm.getVisibility() == View.VISIBLE) {
                            frameConfirm.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else if (frameEnterPassword.getVisibility() == View.VISIBLE) {
                            frameEnterPassword.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        }
                        revealView.startAnimation(anim);
                        frame2.startAnimation(anim);
                    }
                } else {
                    revealView.reveal(p.x, p.y, color, 0, 0, null);
                }

                if (selectedView == v) {
                    //revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
                    selectedView = null;
                } else {
                    //revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
                    selectedView = v;
                }

                ((XposedMainActivity) mContext).revealFromTop(colorPrefs.getString("DialogBootloader_Revealcolor", "#ff0097a7"));
                frame.setVisibility(View.GONE);
                frame3.setVisibility(View.GONE);
                frameConfirm.setVisibility(View.GONE);
                frameEnterPassword.setVisibility(View.GONE);
                frame2.setVisibility(View.VISIBLE);

                status.setText(R.string.powerMenuBottom_Bootloader);
                status_detail.setText(R.string.powerMenu_Rebooting);

                setProgressScreen("Bootloader");
                if (!mPreviewMode) {
                    if (HookShutdownThread) {
                        XposedUtils.doReboot(mContext, 3);
                    } else if (UseRootCommands) {
                        new BackgroundThread(REBOOT_BOOTLOADER_CMD).start();
                    } else {
                        Intent launchIntent = new Intent(XposedMain.NPM_ACTION_BROADCAST_REBOOTBOOTLOADER);
                        mContext.sendOrderedBroadcast(launchIntent, null);
                    }
                }
            } else if (name.equalsIgnoreCase("SafeMode")) {
                canDismiss = false;

                //revealView.setVisibility(View.VISIBLE);
                final int color = Color.parseColor(colorPrefs.getString("DialogSafeMode_Backgroundcolor", "#009688"));
                final Point p = getLocationInView(revealView, v);
                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[0]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                    Animation anim = helper.getAnimation(mContext, animationPrefs, 0, false);
                    if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[0]) == 1) {
                        revealView.reveal(p.x, p.y, color, 0, anim.getDuration(), null);
                    } else {
                        revealView.reveal(p.x, p.y, color, 0, 0, null);
                        if (frame3.getVisibility() == View.VISIBLE) {
                            frame3.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else if (frame.getVisibility() == View.VISIBLE) {
                            frame.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else if (frameConfirm.getVisibility() == View.VISIBLE) {
                            frameConfirm.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else if (frameEnterPassword.getVisibility() == View.VISIBLE) {
                            frameEnterPassword.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        }
                        revealView.startAnimation(anim);
                        frame2.startAnimation(anim);
                    }
                } else {
                    revealView.reveal(p.x, p.y, color, 0, 0, null);
                }

                if (selectedView == v) {
                    //revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
                    selectedView = null;
                } else {
                    //revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
                    selectedView = v;
                }

                ((XposedMainActivity) mContext).revealFromTop(colorPrefs.getString("DialogSafeMode_Revealcolor", "#ff0097a7"));
                frame.setVisibility(View.GONE);
                frame3.setVisibility(View.GONE);
                frameConfirm.setVisibility(View.GONE);
                frameEnterPassword.setVisibility(View.GONE);
                frame2.setVisibility(View.VISIBLE);

                status.setText(R.string.powerMenuBottom_SafeMode);
                status_detail.setText(R.string.powerMenu_Rebooting);

                setProgressScreen("SafeMode");
                if (!mPreviewMode) {
                    new BackgroundThread(REBOOT_SAFE_MODE).start();
                }
            } else if (name.equalsIgnoreCase("SoundVibrate")) {
                if (!mPreviewMode) {
                    if (mHideOnClick) {
                        SubDialogs.clear();
                        dismissThis();
                    }
                    am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                }
            } else if (name.equalsIgnoreCase("SoundNormal")) {
                if (!mPreviewMode) {
                    if (mHideOnClick) {
                        SubDialogs.clear();
                        dismissThis();
                    }
                    am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
            } else if (name.equalsIgnoreCase("SoundSilent")) {
                if (!mPreviewMode) {
                    if (mHideOnClick) {
                        SubDialogs.clear();
                        dismissThis();
                    }
                    am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                }
            } else if (name.contains(".")) {
                if (!mPreviewMode) {
                    SubDialogs.clear();
                    dismissThis();
                    try {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(name.split("/")[0], name.split("/")[1]));
                        mContext.startActivity(intent);
                    } catch (Throwable e) {
                        Log.e("NPM:xposedDialog", "No package with name '" + name + "' found...", e);
                    }
                }
            } else if (name.equalsIgnoreCase("ActivityShortcut")) {
                if (mHideOnClick) {
                    SubDialogs.clear();
                    dismissThis();
                }
            } else if (name.equalsIgnoreCase("KillApp")) {
                if (!mPreviewMode) {
                    SubDialogs.clear();
                    dismissThis();
                    Intent launchIntent = new Intent(XposedMain.NPM_ACTION_BROADCAST_KILLAPP);
                    mContext.sendOrderedBroadcast(launchIntent, null);
                }
            } else if (name.equalsIgnoreCase("ToggleRotate")) {
                if (!mPreviewMode) {
                    if (mHideOnClick) {
                        SubDialogs.clear();
                        dismissThis();
                    }
                    Intent launchIntent = new Intent(XposedMain.NPM_ACTION_BROADCAST_TOGGLEROTATION);
                    mContext.sendOrderedBroadcast(launchIntent, null);
                }
            } else if (name.equalsIgnoreCase("MediaPrevious")) {
                if (!mPreviewMode) {
                    if (mHideOnClick) {
                        SubDialogs.clear();
                        dismissThis();
                    }
                    long eventtime = SystemClock.uptimeMillis();
                    Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                    KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS, 0);
                    downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
                    mContext.sendOrderedBroadcast(downIntent, null);
                }
            } else if (name.equalsIgnoreCase("MediaPlayPause")) {
                if (!mPreviewMode) {
                    if (mHideOnClick) {
                        SubDialogs.clear();
                        dismissThis();
                    }
                    long eventtime = SystemClock.uptimeMillis();
                    Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                    KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
                    downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
                    mContext.sendOrderedBroadcast(downIntent, null);
                }
            } else if (name.equalsIgnoreCase("MediaNext")) {
                if (!mPreviewMode) {
                    if (mHideOnClick) {
                        SubDialogs.clear();
                        dismissThis();
                    }
                    long eventtime = SystemClock.uptimeMillis();
                    Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                    KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT, 0);
                    downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
                    mContext.sendOrderedBroadcast(downIntent, null);
                }
            } else if (name.equalsIgnoreCase("ToggleWifi")) {
                if (!mPreviewMode) {
                    if (mHideOnClick) {
                        SubDialogs.clear();
                        dismissThis();
                    }
                    toggleWifi(!wifiActive);
                }
            } else if (name.equalsIgnoreCase("ToggleBluetooth")) {
                if (!mPreviewMode) {
                    if (mHideOnClick) {
                        SubDialogs.clear();
                        dismissThis();
                    }
                    toggleBluetooth(!bluetoothActive);
                }
            } else if (name.equalsIgnoreCase("ToggleData")) {
                if (!mPreviewMode) {
                    if (mHideOnClick) {
                        SubDialogs.clear();
                        dismissThis();
                    }
                    Intent launchIntent = new Intent(XposedMain.NPM_ACTION_BROADCAST_TOGGLEDATA);
                    mContext.sendOrderedBroadcast(launchIntent, null);
                }
            } else if (name.equalsIgnoreCase("RebootFlashMode")) {
                canDismiss = false;

                //revealView.setVisibility(View.VISIBLE);
                final int color = Color.parseColor(colorPrefs.getString("DialogRebootFlashMode_Backgroundcolor", "#3f51b5"));
                final Point p = getLocationInView(revealView, v);
                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[0]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                    Animation anim = helper.getAnimation(mContext, animationPrefs, 0, false);
                    if (animationPrefs.getInt(PreferencesAnimationsFragment.names[1][1].toString(), PreferencesAnimationsFragment.defaultTypes[0]) == 1) {
                        revealView.reveal(p.x, p.y, color, 0, anim.getDuration(), null);
                    } else {
                        revealView.reveal(p.x, p.y, color, 0, 0, null);
                        if (frame3.getVisibility() == View.VISIBLE) {
                            frame3.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else if (frame.getVisibility() == View.VISIBLE) {
                            frame.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else if (frameConfirm.getVisibility() == View.VISIBLE) {
                            frameConfirm.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        } else if (frameEnterPassword.getVisibility() == View.VISIBLE) {
                            frameEnterPassword.startAnimation(helper.getAnimation(mContext, animationPrefs, 0, true));
                        }
                        revealView.startAnimation(anim);
                        frame2.startAnimation(anim);
                    }
                } else {
                    revealView.reveal(p.x, p.y, color, 0, 0, null);
                }

                if (selectedView == v) {
                    //revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
                    selectedView = null;
                } else {
                    //revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
                    selectedView = v;
                }

                ((XposedMainActivity) mContext).revealFromTop(colorPrefs.getString("DialogRebootFlashMode_Revealcolor", "#ff0097a7"));
                frame.setVisibility(View.GONE);
                frame3.setVisibility(View.GONE);
                frameConfirm.setVisibility(View.GONE);
                frameEnterPassword.setVisibility(View.GONE);
                frame2.setVisibility(View.VISIBLE);

                status.setText(R.string.powerMenuMain_RebootFlashMode);
                status_detail.setText(R.string.powerMenu_Rebooting);

                setProgressScreen("RebootFlashMode");
                if (!mPreviewMode) {
                    if (HookShutdownThread) {
                        XposedUtils.doReboot(mContext, 0);
                    } else if (UseRootCommands) {
                        new BackgroundThread(REBOOT_FLASHMODE_CMD).start();
                    } else {
                        Intent launchIntent = new Intent(XposedMain.NPM_ACTION_BROADCAST_REBOOTFLASHMODE);
                        mContext.sendOrderedBroadcast(launchIntent, null);
                    }
                }
            } else if (name.equalsIgnoreCase("LockPhone")) {
                if (!mPreviewMode) {
                    SubDialogs.clear();
                    dismissThis();
                    DevicePolicyManager devicePolicyManager = (DevicePolicyManager) mContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
                    if (!devicePolicyManager.isAdminActive(new ComponentName(mContext, deviceAdmin.class))) {
                        Intent intent = new Intent(DevicePolicyManager
                                .ACTION_ADD_DEVICE_ADMIN);
                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                                new ComponentName(mContext, deviceAdmin.class));
                        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                                getString(R.string.permissionsScreenDesc_DeviceAdmin));
                        startActivity(intent);
                    } else {
                        devicePolicyManager.lockNow();
                    }
                }
            } else if (name.equalsIgnoreCase("SilentMode")) {
                if (!mPreviewMode) {
                    if (mHideOnClick) {
                        SubDialogs.clear();
                        dismissThis();
                    }
                    if (amRingerMode == AudioManager.RINGER_MODE_SILENT) {
                        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    } else if (amRingerMode == AudioManager.RINGER_MODE_NORMAL) {
                        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    }
                }
            }
        }
    }

    public void checkPassword(int id, String name, View v) {
        if (helper.md5Crypto(EnterPasswordInput.getText().toString()).equalsIgnoreCase(mPasswordLock)) {
            try {
                InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                IBinder windowToken = EnterPasswordInput.getWindowToken();
                inputManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Throwable t) {
            }
            SubDialogs.clear();
            performMenuClick(id, name, v);
        } else {
            EnterPasswordInput.setText("");
            EnterPasswordError.setText(getString(R.string.powerMenu_WrongPassword));
            if(EnterPasswordError.getVisibility() == View.GONE) {
                EnterPasswordError.setVisibility(View.VISIBLE);
                //EnterPasswordError.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
            }
            mHandler.removeCallbacks(hidePasswordErrorRunnable);
            mHandler.postDelayed(hidePasswordErrorRunnable, 4000L);
        }
    }

    public void setProgressScreen(final String showingFor) {
        status.setTextColor(Color.parseColor(colorPrefs.getString("Dialog" + showingFor + "_Textcolor", "#ffffff")));
        status_detail.setTextColor(Color.parseColor(colorPrefs.getString("Dialog" + showingFor + "_Textcolor", "#ffffff")));

        //if(GraphicBehindProgress) loadImage(progressbg, 0, colorPrefs.getString("DialogShutdown_Textcolor", "#ffffff"));
        if (preferences.getString("ProgressDrawable", "Stock").equalsIgnoreCase("file")) {
            if (new File(mContext.getFilesDir().getPath() + "/images/" + PreferencesGraphicsFragment.graphics[0][2] + ".png").exists()) {
                XposedMainActivity.mImageLoader.displayImage("file://" + mContext.getFilesDir().getPath() + "/images/" + PreferencesGraphicsFragment.graphics[0][2] + ".png",
                        progressbg, new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {
                                progress.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onLoadingComplete(final String imageUri, final View view, Bitmap loadedImage) {
                                progressbg.setPadding((int) mGraphicsPadding, (int) mGraphicsPadding, (int) mGraphicsPadding, (int) mGraphicsPadding);
                                progressbg.setVisibility(View.VISIBLE);
                                progressbg.setImageBitmap(loadedImage);
                                if (mColorizeNonStockIcons) {
                                    progressbg.setColorFilter(Color.parseColor(colorPrefs.getString("Dialog" + showingFor + "_Textcolor", "#ffffff")),
                                            android.graphics.PorterDuff.Mode.MULTIPLY);
                                }
                                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[7][1].toString(), PreferencesAnimationsFragment.defaultTypes[2]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                                    Animation blendIn = helper.getAnimation(mContext, animationPrefs, 6, false);
                                    blendIn.setAnimationListener(new Animation.AnimationListener() {

                                        @Override
                                        public void onAnimationEnd(Animation p1) {

                                            Animation progressAnim = AnimationUtils.loadAnimation(mContext, R.anim.rotate_right);
                                            progressAnim.setRepeatMode(Animation.RESTART);
                                            progressAnim.setRepeatCount(Animation.INFINITE);
                                            progressbg.startAnimation(progressAnim);
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation p1) {

                                        }

                                        @Override
                                        public void onAnimationStart(Animation p1) {

                                        }
                                    });
                                    progressbg.startAnimation(blendIn);
                                } else {
                                    Animation progressAnim = AnimationUtils.loadAnimation(mContext, R.anim.rotate_right);
                                    progressAnim.setRepeatMode(Animation.RESTART);
                                    progressAnim.setRepeatCount(Animation.INFINITE);
                                    progressbg.startAnimation(progressAnim);
                                }
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                Log.w("NPM:xposedDialog", "Failed to load image '" + imageUri + "': " + failReason.getCause().toString());
                                progress.getIndeterminateDrawable().setColorFilter(Color.parseColor(colorPrefs.getString("Dialog" + showingFor + "_Textcolor", "#ffffff")), android.graphics.PorterDuff.Mode.MULTIPLY);
                                progress.setVisibility(View.VISIBLE);
                                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[7][1].toString(), PreferencesAnimationsFragment.defaultTypes[2]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                                    progress.startAnimation(helper.getAnimation(mContext, animationPrefs, 6, false));
                                }
                            }
                        });
            }
        } else {
            if (preferences.getString("ProgressDrawable", "Stock").equalsIgnoreCase("Stock")) {
                progress.getIndeterminateDrawable().setColorFilter(Color.parseColor(colorPrefs.getString("Dialog" + showingFor + "_Textcolor", "#ffffff")), android.graphics.PorterDuff.Mode.MULTIPLY);
                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[7][1].toString(), PreferencesAnimationsFragment.defaultTypes[2]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                    progress.startAnimation(helper.getAnimation(mContext, animationPrefs, 6, false));
                }
            } else {
                progress.setVisibility(View.GONE);
                progressbg.setColorFilter(Color.parseColor(colorPrefs.getString("Dialog" + showingFor + "_Textcolor", "#ffffff")), android.graphics.PorterDuff.Mode.MULTIPLY);
                new loadProgressResource().execute(showingFor);
            }
        }
    }

    public void setHost(XposedMainActivity host) {
        this.menuHost = host;
    }

    public void setPreviewMode(boolean mode) {
        this.mPreviewMode = mode;
    }

    class loadProgressResource extends AsyncTask<Object, String, String> {

        Drawable image = null;
        String showingFor = "";

        @Override
        protected String doInBackground(Object[] p1) {

            showingFor = p1[0].toString();
            try {
                if (preferences.getString("ProgressDrawable", "Stock").equalsIgnoreCase("pb/dr")) {
                    image = mContext.getResources().getDrawable(R.drawable.progress_pitchblack_darkred_cm13);
                } else if (preferences.getString("ProgressDrawable", "Stock").equalsIgnoreCase("WeaReOne")) {
                    image = mContext.getResources().getDrawable(R.drawable.progress_weareone);
                }
            } catch (OutOfMemoryError t) {
                return t.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String p1) {

            super.onPostExecute(p1);
            if (p1 == null) {
                progressbg.setImageDrawable(image);
                progressbg.setVisibility(View.VISIBLE);
                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[7][1].toString(), PreferencesAnimationsFragment.defaultTypes[2]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                    progressbg.startAnimation(helper.getAnimation(mContext, animationPrefs, 6, false));
                }
                ((AnimationDrawable) progressbg.getDrawable()).start();
            } else {
                Log.w("NPM:lPR", "Failed to load progress drawable: " + p1);
                progressbg.setVisibility(View.INVISIBLE);
                progress.setVisibility(View.VISIBLE);
                progress.getIndeterminateDrawable().setColorFilter(Color.parseColor(colorPrefs.getString("Dialog" + showingFor + "_Textcolor", "#ffffff")), android.graphics.PorterDuff.Mode.MULTIPLY);
                if (animationPrefs.getInt(PreferencesAnimationsFragment.names[7][1].toString(), PreferencesAnimationsFragment.defaultTypes[2]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                    progress.startAnimation(helper.getAnimation(mContext, animationPrefs, 6, false));
                }
            }
        }

    }

    private void setThreadPrio(int prio) {
        android.os.Process.setThreadPriority(prio);
    }

    private class BackgroundThread extends Thread {
        private Object sCmd;

        private BackgroundThread(Object cmd) {
            this.sCmd = cmd;
            if (mDeepXposedLogging)
                XposedUtils.log("Starting background task with command: " + cmd);
        }

        @Override
        public void run() {
            super.run();
            if (!mPreviewMode) {
                setThreadPrio(BG_PRIO);

                if (sCmd == null) {
                    return;
                }
                if (mDeepXposedLogging)
                    XposedUtils.log("Sending " + SHUTDOWN_BROADCAST);
                Shell.SU.run(SHUTDOWN_BROADCAST);

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (sCmd instanceof String) {
                            if (HookShutdownThread) {
                                if (sCmd.toString().equalsIgnoreCase(SHUTDOWN)) {
                                    Intent broadcast = new Intent();
                                    broadcast.setAction(XposedMain.NPM_ACTION_BROADCAST_SHUTDOWN);
                                    mContext.sendOrderedBroadcast(broadcast, null);
                                } else if (sCmd.toString().equalsIgnoreCase(REBOOT_CMD)) {
                                    Intent broadcast = new Intent();
                                    broadcast.setAction(XposedMain.NPM_ACTION_BROADCAST_REBOOT);
                                    mContext.sendOrderedBroadcast(broadcast, null);
                                }
                            } else {
                                if (mDeepXposedLogging)
                                    XposedUtils.log("Running su command " + sCmd.toString());
                                Shell.SU.run((String) sCmd);
                            }
                        } else if (sCmd instanceof String[]) {
                            if (mDeepXposedLogging)
                                XposedUtils.log("Running su commands " + sCmd);
                            Shell.SU.run((String[]) sCmd);
                        }
                    }
                }, RUNNABLE_DELAY_MS);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getShowsDialog()) {
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams windowParams = window.getAttributes();
            windowParams.dimAmount = 0.0f;
            //windowParams.type = windowParams.TYPE_KEYGUARD_DIALOG;

            window.setAttributes(windowParams);
        }
    }

    public void dismissThis() {
        if(XposedMainActivity.action != null) {
            SubDialogs.clear();
            confirmDialog = "";
        }
        if (SubDialogs.isEmpty()) {
            if (canDismiss || mPreviewMode) {
                if (!isDismissing) {
                    isDismissing = true;
                    int speed = 0;
                    if (mContext != null && animationPrefs.getInt(PreferencesAnimationsFragment.names[4][1].toString(), PreferencesAnimationsFragment.defaultTypes[4]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                        Animation anim = helper.getAnimation(mContext, animationPrefs, 3, true);
                        dialogMain.startAnimation(anim);
                        speed = (int) anim.getDuration();
                    }
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            if (dialogMain != null) dialogMain.setVisibility(View.GONE);
                            menuHost.dismissThis();
                        }
                    }, Math.max(speed - 100, 0));
                }
            }
        } else {
            if (!confirmDialog.isEmpty()) {
                if (SubDialogs.get(SubDialogs.size() - 1).equalsIgnoreCase("EnterPassword")) {
                    if (animationPrefs.getInt(PreferencesAnimationsFragment.names[4][1].toString(), PreferencesAnimationsFragment.defaultTypes[4]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                        frameEnterPassword.startAnimation(helper.getAnimation(mContext, animationPrefs, 3, true));
                    }
                    if (mFingerprintHandler != null) mFingerprintHandler.stopAuth();
                    frameEnterPassword.setVisibility(View.GONE);
                } else {
                    if (animationPrefs.getInt(PreferencesAnimationsFragment.names[4][1].toString(), PreferencesAnimationsFragment.defaultTypes[4]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                        frameConfirm.startAnimation(helper.getAnimation(mContext, animationPrefs, 3, true));
                    }
                    frameConfirm.setVisibility(View.GONE);
                }
            }
            if (mDeepXposedLogging) {
                Log.i("NPM:dT", "Total entries: " + SubDialogs.size());
            }
            SubDialogs.remove(SubDialogs.size() - 1);
            String parent = "root";
            if (!SubDialogs.isEmpty()) {
                parent = SubDialogs.get(SubDialogs.size() - 1);
            }
            if (mDeepXposedLogging) Log.i("NPM:dT", "Performing menu back to: " + parent);
            performMenuClick(0, "multipage:" + parent, null);
        }
    }

    private Point getLocationInView(View src, View target) {
        if (target != null) {
            final int[] l0 = new int[2];
            src.getLocationOnScreen(l0);

            final int[] l1 = new int[2];
            target.getLocationOnScreen(l1);

            l1[0] = l1[0] - l0[0] + target.getWidth() / 2;
            l1[1] = l1[1] - l0[1] + target.getHeight() / 2;

            return new Point(l1[0], l1[1]);
        } else {
            return new Point(0, 0);
        }
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        if (getShowsDialog()) {
            /*
            int gravity = 0;
            if (boolean_DialogGravityTop) {
                gravity |= Gravity.TOP;
            } else if (boolean_DialogGravityBottom) {
                gravity |= Gravity.BOTTOM;
            } else {
                gravity |= Gravity.CENTER_VERTICAL;
            }
            if (boolean_DialogGravityLeft) {
                gravity |= Gravity.LEFT;
            } else if (boolean_DialogGravityRight) {
                gravity |= Gravity.RIGHT;
            } else {
                gravity |= Gravity.CENTER_HORIZONTAL;
            }
            //getDialog().getWindow()
            //		.getAttributes().windowAnimations = R.style.DialogAnimation;
            getDialog().getWindow()
                    .getAttributes().gravity = gravity;
            */
        }
    }

    private void setGravity() {
        int left = 0, top = 0, right = 0, bottom = 0;
        try {
            bottom = ((int_Vertical * ((int) DisplaySize[1] - dialogContent.getHeight())) / 100);
            //top = ((int) DisplaySize[0] % (int) helper.convertDpToPixel(int_Vertical, mContext));
        } catch (Exception e) {
            Log.d("NPM:GRAV","Calculation error.", e);
        }
        try {
            right = ((int_Horizontal * ((int) DisplaySize[0] - dialogContent.getWidth())) / 100);
            //left = ((int) DisplaySize[1] % (int) helper.convertDpToPixel(int_Horizontal, mContext));
        } catch (Exception e) {
            Log.d("NPM:GRAV","Calculation error.", e);
        }
        dialogPadding.setPadding(left,top,right,bottom);
        try {
            if (!helper.isDeviceHorizontal(mContext)) {
                LinearLayout.LayoutParams dialogContentParams = new LinearLayout.LayoutParams(dialogContent.getLayoutParams());
                dialogContentParams.topMargin = helper.getStatusBarHeight(mContext);
                dialogContentParams.bottomMargin = helper.getNavigationBarSize(mContext).y;
                dialogContent.setLayoutParams(dialogContentParams);
            } else {
                LinearLayout.LayoutParams dialogContentParams = new LinearLayout.LayoutParams(dialogContent.getLayoutParams());
                dialogContentParams.topMargin = helper.getStatusBarHeight(mContext);
                dialogContentParams.rightMargin = helper.getNavigationBarSize(mContext).x;
                dialogContent.setLayoutParams(dialogContentParams);
            }
        } catch (Exception e) {
            Log.e("NPM:GRAV","Failed to set layout params.", e);
        }
    }

    private void toggleTorch(boolean goToSleep) {
        try {
            Intent intent = new Intent(mContext, TorchService.class);
            intent.setAction(TorchService.ACTION_TOGGLE_TORCH);
            intent.putExtra(TorchService.EXTRA_GO_TO_SLEEP, goToSleep);
            mContext.startService(intent);
        } catch (Throwable t) {
            Log.e("TorchService", "Error toggling Torch: " + t.toString());
        }
    }

    private void toggleWifi(boolean enable) {
        try {
            wifiManager.setWifiEnabled(enable);
        } catch (Throwable t) {
            Log.e("NPM:tW", "Error toggling wifi: " + t.toString());
        }
    }

    private void toggleBluetooth(boolean enable) {
        try {
            if (enable) {
                bluetoothAdapter.enable();
            } else {
                bluetoothAdapter.disable();
            }
        } catch (Throwable t) {
            Log.e("NPM:tB", "Error toggling bluetooth: ", t);
        }
    }

    private boolean isDataActive() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                return Settings.Global.getInt(mContext.getContentResolver(), "mobile_data", 0) == 1;
            } else {
                return Settings.Secure.getInt(mContext.getContentResolver(), "mobile_data", 0) == 1;
            }
        } catch (Throwable t) {
            Log.e("NPM:gD", "Error getting data state: ", t);
            return false;
        }
    }
}


package de.NeonSoft.neopowermenu.Preferences;

import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.media.*;
import android.os.*;
import android.support.annotation.Nullable;
import android.support.v4.app.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.widget.*;

import com.nostra13.universalimageloader.core.assist.*;
import com.nostra13.universalimageloader.core.listener.*;

import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.*;
import de.NeonSoft.neopowermenu.services.*;
import de.NeonSoft.neopowermenu.xposed.XposedMainActivity;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import android.support.v4.app.DialogFragment;

public class GravityChooserDialog extends DialogFragment {

    public static Activity mContext;

    static LinearLayout LinearLayout_ImageHolder;

    private static SeekBar SeekBar_Vertical;
    private static SeekBar SeekBar_Horizontal;
    private SeekBar SeekBar_Size;
    private ImageView ImageView_Reset;
    AudioManager am;
    public static int amRingerMode;

    static float float_padding = 0;
    int mGraphicsRadius;
    static int int_Vertical = 0;
    static int int_Horizontal = 0;
    static int int_Size = 0;
    boolean boolean_roundedCorners;
    int int_Radius = 0;
    static Object[] DisplaySize;

    static View DummyPowerDialog;
    static int DummyPowerDialogHeight;
    static FrameLayout PowerDialogFrame;

    @Override
    public View onCreateView(LayoutInflater p1, ViewGroup p2, Bundle p3) {

        mContext = getActivity();

        am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        amRingerMode = am.getRingerMode();

        DisplaySize = helper.getDisplaySize(mContext, false);
        if (!helper.isDeviceHorizontal(mContext)) {
            DisplaySize[1] = (int) DisplaySize[1] - helper.getNavigationBarSize(mContext).y - helper.getStatusBarHeight(mContext) - MainActivity.actionBarHolder.getHeight();
        } else {
            DisplaySize[0] = (int) DisplaySize[0] - helper.getNavigationBarSize(mContext).x;
            DisplaySize[1] = (int) DisplaySize[1] - helper.getStatusBarHeight(mContext);
        }

        float_padding = MainActivity.preferences.getFloat(PreferenceNames.pGraphicsPadding,0);
        mGraphicsRadius = MainActivity.preferences.getInt(PreferenceNames.pCircleRadius, 100);

        int_Vertical = MainActivity.preferences.getInt(PreferenceNames.pDialogPosition_Vertical,50);
        int_Horizontal = MainActivity.preferences.getInt(PreferenceNames.pDialogPosition_Horizontal,50);
        int_Size = MainActivity.preferences.getInt(PreferenceNames.pDialogPosition_Size,60);
        boolean_roundedCorners = MainActivity.preferences.getBoolean(PreferenceNames.pRoundedDialogCorners, false);
        int_Radius = (boolean_roundedCorners ? MainActivity.preferences.getInt(PreferenceNames.pRoundedDialogCornersRadius, 0) : 0);

        MainActivity.visibleFragment = "Gravity";

        //MainActivity.actionBarHolder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
        //MainActivity.actionBarHolder.setVisibility(View.GONE);

        MainActivity.actionbar.setTitle(getString(R.string.advancedPrefsTitle_DialogGravity));
        MainActivity.actionbar.setSubTitle(getString(R.string.advancedPrefsDesc_DialogGravity));

        View InflatedView = p1.inflate(R.layout.activity_dialogposition, p2, false);

        LinearLayout_ImageHolder = (LinearLayout) InflatedView.findViewById(R.id.dialoggravitychooserLinearLayout_ImageHolder);

        SeekBar_Vertical = (SeekBar) InflatedView.findViewById(R.id.activitydialogpositionSeekBar_Vertical);
        SeekBar_Vertical.setMax(100);
        SeekBar_Vertical.setProgress(int_Vertical);

        SeekBar_Vertical.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int_Vertical = progress;
                MainActivity.preferences.edit().putInt("DialogPosition_Vertical", int_Vertical).commit();
                changeGravity();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        SeekBar_Horizontal = (SeekBar) InflatedView.findViewById(R.id.activitydialogpositionSeekBar_Horitontal);
        SeekBar_Horizontal.setMax(100);
        SeekBar_Horizontal.setProgress(int_Horizontal);

        SeekBar_Horizontal.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int_Horizontal = progress;
                MainActivity.preferences.edit().putInt("DialogPosition_Horizontal", int_Horizontal).commit();
                changeGravity();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        ImageView_Reset = (ImageView) InflatedView.findViewById(R.id.activitydialogpositionImageView_Reset);
        ImageView_Reset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int_Horizontal = 50;
                int_Vertical = 50;
                int_Size = 60;
                MainActivity.preferences.edit().putInt("DialogPosition_Vertical", int_Vertical).putInt("DialogPosition_Horizontal", int_Horizontal).putInt("DialogPosition_Size", int_Size).commit();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    SeekBar_Vertical.setProgress(50,true);
                    SeekBar_Horizontal.setProgress(50,true);
                    SeekBar_Size.setProgress(50,true);
                } else {
                    SeekBar_Vertical.setProgress(50);
                    SeekBar_Horizontal.setProgress(50);
                    SeekBar_Size.setProgress(50);
                }
                SeekBar_Vertical.invalidate();
                changeGravity();
            }
        });

        DummyPowerDialog = p1.inflate(R.layout.fragment_power, LinearLayout_ImageHolder, false);
        PowerDialogFrame = (FrameLayout) DummyPowerDialog.findViewById(R.id.fragmentpowerFrameLayout1);
        PowerDialogFrame.setPadding(2,2,2,2);
        GradientDrawable bgDrawable = (GradientDrawable) PowerDialogFrame.getBackground();
        bgDrawable.setColor(Color.parseColor(MainActivity.colorPrefs.getString("Dialog_Backgroundcolor", "#ffffff")));
        bgDrawable.setStroke(2, mContext.getResources().getColor(R.color.colorAccentDarkTheme));
        bgDrawable.setCornerRadius(int_Radius);
        PowerDialogFrame.setBackground(bgDrawable);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DummyPowerDialog.getLayoutParams());
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = (int) (((int) DisplaySize[0])*(0.01*int_Size));
        DummyPowerDialog.setLayoutParams(params);
        PowerDialogFrame.setLayoutParams(params);

        SeekBar_Size = (SeekBar) InflatedView.findViewById(R.id.activitydialogpositionSeekBar_Size);
        SeekBar_Size.setMax(80);
        SeekBar_Size.setProgress(int_Size-10);

        SeekBar_Size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int_Size = progress+10;
                MainActivity.preferences.edit().putInt("DialogPosition_Size", int_Size).commit();
                changeGravity();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        LinearLayout Main = (LinearLayout) DummyPowerDialog.findViewById(R.id.fragmentpowerFrameLayout_Main);
        FrameLayout Content = (FrameLayout) DummyPowerDialog.findViewById(R.id.fragmentpowerFrameLayout1);

        LinearLayout ListContainer = (LinearLayout) DummyPowerDialog.findViewById(R.id.ListContainer);

        FrameLayout frame = (FrameLayout) DummyPowerDialog.findViewById(R.id.frame);
        //frame.setBackgroundColor(Color.parseColor(MainActivity.colorPrefs.getString("Dialog_Backgroundcolor", "#ffffff")));

        ((FrameLayout) DummyPowerDialog.findViewById(R.id.frame2)).setVisibility(View.GONE);
        ((FrameLayout) DummyPowerDialog.findViewById(R.id.frameConfirm)).setVisibility(View.GONE);
        ((FrameLayout) DummyPowerDialog.findViewById(R.id.frameEnterPassword)).setVisibility(View.GONE);

        for (int i = 0; i <= 3; i++) {
            String mTitle = "";
            if (i == 0) {
                mTitle = "Shutdown";
            } else if (i == 1) {
                mTitle = "Reboot";
            } else if (i == 2) {
                mTitle = "SoftReboot";
            } else if (i == 3) {
                mTitle = "Recovery|Bootloader|SafeMode";
            }
            View inflated = null;
            if (i != 3) {
                inflated = p1.inflate(R.layout.powermenu_normal, null, false);

                LinearLayout root = (LinearLayout) inflated.findViewById(R.id.powermenunormal_root);
                ImageView icon = (ImageView) inflated.findViewById(R.id.powermenunormal_icon);
                ImageView icon2 = (ImageView) inflated.findViewById(R.id.powermenunormal_icon2);
                icon2.setVisibility(View.GONE);
                TextView text = (TextView) inflated.findViewById(R.id.powermenunormal_text1);
                TextView desc = (TextView) inflated.findViewById(R.id.powermenunormal_text2);
                desc.setVisibility(View.GONE);

                if (!mTitle.equalsIgnoreCase("Empty")) {
                    String string = "Failed to get String resource for " + mTitle;
                    try {
                        string = getResources().getString(getResources().getIdentifier("powerMenuMain_" + mTitle, "string", MainActivity.class.getPackage().getName()));
                    } catch (Throwable t) {
                        try {
                            string = getResources().getString(getResources().getIdentifier("powerMenuBottom_" + mTitle, "string", MainActivity.class.getPackage().getName()));
                        } catch (Throwable t1) {
                        }
                    }
                    text.setText(string);
                    text.setTextColor(Color.parseColor(MainActivity.colorPrefs.getString("Dialog_Textcolor", "#000000")));

                    if (!MainActivity.preferences.getBoolean(mTitle + "_HideDesc", false)) {
                        String descString = "Failed to get String resource for " + mTitle;
                        try {
                            descString = getResources().getString(getResources().getIdentifier("powerMenuMain_" + mTitle + "Desc", "string", MainActivity.class.getPackage().getName()));
                            desc.setVisibility(View.VISIBLE);
                        } catch (Throwable t) {
                            try {
                                descString = getResources().getString(getResources().getIdentifier("powerMenuBottom_" + mTitle + "Desc", "string", MainActivity.class.getPackage().getName()));
                                desc.setVisibility(View.VISIBLE);
                            } catch (Throwable t1) {
                            }
                        }
                        desc.setText(descString);
                    }

                    desc.setTextColor(Color.parseColor(MainActivity.colorPrefs.getString("Dialog_Textcolor", "#000000")));

                    createCircleIcon(icon, icon2, string, MainActivity.colorPrefs.getString("Dialog" + mTitle + "_Circlecolor", "#ff000000"), MainActivity.colorPrefs.getString("Dialog" + mTitle + "_Textcolor", "#ffffff"));
                } else {
                    root.setVisibility(View.INVISIBLE);
                }
                if (MainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[PreferencesAnimationsFragment.anim_SingleLine+PreferencesAnimationsFragment.anim_Type][1].toString(), PreferencesAnimationsFragment.defaultTypes[PreferencesAnimationsFragment.anim_SingleLine+PreferencesAnimationsFragment.anim_Type]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                    root.startAnimation(helper.getAnimation(mContext, MainActivity.animationPrefs, PreferencesAnimationsFragment.anim_SingleLine, false));
                }
            } else {

                inflated = p1.inflate(R.layout.powermenu_multi, (ViewGroup) DummyPowerDialog, false);

                LinearLayout itemHolder = (LinearLayout) inflated.findViewById(R.id.powermenumulti_itemHolder);

                ArrayList<String> mItems = new ArrayList<>(Arrays.asList(mTitle.split("\\|")));

                for (int x = 0; x < mItems.size(); x++) {
                    final View view = (View) p1.inflate(R.layout.powermenu_multiitem, (ViewGroup) inflated, false);

                    LinearLayout root = (LinearLayout) view.findViewById(R.id.powermenumulti_item);
                    ImageView icon = (ImageView) view.findViewById(R.id.powermenumulti_itemicon);
                    ImageView icon2 = (ImageView) view.findViewById(R.id.powermenumulti_itemicon2);
                    icon2.setVisibility(View.GONE);
                    TextView text = (TextView) view.findViewById(R.id.powermenumulti_itemtext);
                    String string;
                    string = mItems.get(x);
                    try {
                        string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + mItems.get(x), "string", MainActivity.class.getPackage().getName()));
                    } catch (Throwable t) {
                        try {
                            string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_" + mItems.get(x), "string", MainActivity.class.getPackage().getName()));
                        } catch (Throwable t1) {
                        }
                    }
                    string = string.replace("(Fake)", "");
                    text.setText(string);
                    text.setTextColor(Color.parseColor(MainActivity.colorPrefs.getString("Dialog_Textcolor", "#000000")));

                    String IDENTIFIER = mItems.get(x);
                    createCircleIcon(icon, icon2, string, MainActivity.colorPrefs.getString("Dialog" + IDENTIFIER + "_Circlecolor", "#ff000000"), MainActivity.colorPrefs.getString("Dialog" + IDENTIFIER + "_Textcolor", "#ffffff"));
                    if (MainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[PreferencesAnimationsFragment.anim_MultiLine+PreferencesAnimationsFragment.anim_Type][1].toString(), PreferencesAnimationsFragment.defaultTypes[PreferencesAnimationsFragment.anim_MultiLine+PreferencesAnimationsFragment.anim_Type]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                        Animation anim = helper.getAnimation(mContext, MainActivity.animationPrefs, PreferencesAnimationsFragment.anim_MultiLine, false);
                        anim.setStartOffset((anim.getDuration() / 30) * ((i - 1) * 3));
                        root.startAnimation(anim);
                    }

                    itemHolder.addView(view);

                }
            }
            ListContainer.addView(inflated);
        }

        LinearLayout_ImageHolder.addView(DummyPowerDialog);
        LinearLayout.LayoutParams MainParams = new LinearLayout.LayoutParams(Main.getLayoutParams());
        MainParams.width = (int) helper.getDisplaySize(mContext, true)[0] - 200;
        //MainParams.height = (int) helper.convertDpToPixel((float) 250, mContext);
        //Main.setLayoutParams(MainParams);

        int animationId = MainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[PreferencesAnimationsFragment.anim_Dialog+PreferencesAnimationsFragment.anim_Type][1].toString(), PreferencesAnimationsFragment.defaultTypes[PreferencesAnimationsFragment.anim_Dialog+PreferencesAnimationsFragment.anim_Type]);
        if (animationId != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
            if (animationId >= 6 && animationId <= 9) {
                Content.startAnimation(helper.getAnimation(mContext, MainActivity.animationPrefs, PreferencesAnimationsFragment.anim_Dialog, false));
            } else {
                LinearLayout_ImageHolder.startAnimation(helper.getAnimation(mContext, MainActivity.animationPrefs, PreferencesAnimationsFragment.anim_Dialog, false));
            }
        }

        ViewTreeObserver vto = DummyPowerDialog.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                DummyPowerDialogHeight = DummyPowerDialog.getHeight() + 25;
                changeGravity();
                ViewTreeObserver obs = DummyPowerDialog.getViewTreeObserver();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }
            }
        });
        return InflatedView;
    }

    public void createCircleIcon(ImageView background, final ImageView foreground, String text, String color1, String color2) {
        try {
            if (MainActivity.preferences.getBoolean("UseGraphics", false)) {
                GraphicDrawable drawable = GraphicDrawable.builder().buildRoundRect((Bitmap) null, Color.parseColor(color1), mGraphicsRadius/2);
                background.setImageDrawable(drawable);
                foreground.setVisibility(View.VISIBLE);
                if (text.equalsIgnoreCase(mContext.getString(R.string.powerMenuMain_Shutdown))) {
                    loadImage(foreground, 1, color2);
                } else if (text.equalsIgnoreCase(mContext.getString(R.string.powerMenuMain_Reboot))) {
                    loadImage(foreground, 2, color2);
                } else if (text.equalsIgnoreCase(mContext.getString(R.string.powerMenuMain_SoftReboot))) {
                    loadImage(foreground, 3, color2);
                } else if (text.equalsIgnoreCase(mContext.getString(R.string.powerMenuBottom_Recovery))) {
                    loadImage(foreground, 15, color2);
                } else if (text.equalsIgnoreCase(mContext.getString(R.string.powerMenuBottom_Bootloader))) {
                    loadImage(foreground, 16, color2);
                } else if (text.equalsIgnoreCase(mContext.getString(R.string.powerMenuBottom_SafeMode))) {
                    loadImage(foreground, 17, color2);
                }
            } else {
                TextDrawable drawable = TextDrawable.builder().beginConfig().textColor(Color.parseColor(color2)).endConfig()
                        .buildRound(text.substring(0, 1), Color.parseColor(color1));
                background.setImageDrawable(drawable);
                foreground.setVisibility(View.GONE);
            }
        } catch (Throwable t) {
            Log.e("NPM", "Failed to create Circle Icon.", t);
        }
    }

    private static void loadImage(final ImageView image, final int id, final String color) {
        if (new File(mContext.getFilesDir().getPath() + "/images/" + PreferencesGraphicsFragment.graphics[id][2] + ".png").exists()) {
            MainActivity.imageLoader.displayImage("file://" + mContext.getFilesDir().getPath() + "/images/" + PreferencesGraphicsFragment.graphics[id][2] + ".png",
                    image, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            image.setImageBitmap(null);
                            image.setPadding((int) helper.convertDpToPixel(5,mContext), (int) helper.convertDpToPixel(5,mContext), (int) helper.convertDpToPixel(5,mContext), (int) helper.convertDpToPixel(5,mContext));
                            image.setColorFilter(Color.parseColor("#ffffff"),
                                    android.graphics.PorterDuff.Mode.DST);
                            image.setVisibility(View.INVISIBLE);
                            super.onLoadingStarted(imageUri, view);
                        }

                        @Override
                        public void onLoadingComplete(final String imageUri, final View view, Bitmap loadedImage) {
                            image.setPadding((int) float_padding,(int) float_padding,(int) float_padding,(int) float_padding);
                            image.setImageBitmap(loadedImage);
                            if(MainActivity.preferences.getBoolean("ColorizeNonStockIcons",false)) {
                                image.setColorFilter(Color.parseColor(color),
                                        android.graphics.PorterDuff.Mode.MULTIPLY);
                            }
                            image.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            Log.e("NPM", "Failed to load image '" + imageUri + "': " + failReason.getCause().toString());
                            image.setImageDrawable(mContext.getResources().getDrawable((int) PreferencesGraphicsFragment.graphics[id][1]));
                            image.setColorFilter(Color.parseColor(color),
                                    android.graphics.PorterDuff.Mode.SRC_IN);
                            image.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            image.setPadding((int) Math.max(float_padding,helper.convertDpToPixel(5,mContext)),(int) Math.max(float_padding,helper.convertDpToPixel(5,mContext)),(int) Math.max(float_padding,helper.convertDpToPixel(5,mContext)),(int) Math.max(float_padding,helper.convertDpToPixel(5,mContext)));
            image.setImageDrawable(mContext.getResources().getDrawable((int) PreferencesGraphicsFragment.graphics[id][1]));
            image.setColorFilter(Color.parseColor(color),
                    android.graphics.PorterDuff.Mode.SRC_IN);
            image.setVisibility(View.VISIBLE);
            //image.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
        }
    }

    public static void changeGravity() {
        DisplaySize = helper.getDisplaySize(mContext, false);
        if (!helper.isDeviceHorizontal(mContext) || helper.getNavigationBarSize(mContext).x == 0) {
            DisplaySize[1] = (int) DisplaySize[1] - helper.getNavigationBarSize(mContext).y - helper.getStatusBarHeight(mContext) - MainActivity.actionBarHolder.getHeight();
        } else {
            DisplaySize[0] = (int) DisplaySize[0] - helper.getNavigationBarSize(mContext).x;
            DisplaySize[1] = (int) DisplaySize[1] - helper.getStatusBarHeight(mContext) - MainActivity.actionBarHolder.getHeight();
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DummyPowerDialog.getLayoutParams());
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = (int) (((int) DisplaySize[0]) * (0.01 * int_Size));
        DummyPowerDialog.setLayoutParams(params);
        PowerDialogFrame.setLayoutParams(params);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int left = 0, top = 0, right = 0, bottom = 0;
                try {
                    bottom = ((int_Vertical * ((int) DisplaySize[1] - DummyPowerDialog.getHeight())) / SeekBar_Vertical.getMax());
                    //top = ((int) DisplaySize[0] % (int) helper.convertDpToPixel(int_Vertical, mContext));
                } catch (Exception e) {
                    Log.d("NPM", "Calculation error.", e);
                }
                try {
                    right = ((int_Horizontal * ((int) DisplaySize[0] - DummyPowerDialog.getWidth())) / SeekBar_Horizontal.getMax());
                    //left = ((int) DisplaySize[1] % (int) helper.convertDpToPixel(int_Horizontal, mContext));
                } catch (Exception e) {
                    Log.d("NPM", "Calculation error.", e);
                }
                LinearLayout_ImageHolder.setPadding(left, top, right, bottom);
            }
        }, 1L);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}

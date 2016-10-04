package de.NeonSoft.neopowermenu.Preferences;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.media.*;
import android.os.*;
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

import java.io.*;

import android.support.v4.app.DialogFragment;

public class GravityChooserDialog extends DialogFragment {

    public static Activity mContext;

    LinearLayout LinearLayout_ImageHolder;

    boolean boolean_DialogGravityTop = false;
    LinearLayout LinearLayout_DialogGravityTop;
    TextView TextView_DialogGravityTop;
    Switch Switch_DialogGravityTop;

    boolean boolean_DialogGravityLeft = false;
    LinearLayout LinearLayout_DialogGravityLeft;
    TextView TextView_DialogGravityLeft;
    Switch Switch_DialogGravityLeft;

    //boolean boolean_DialogGravityCenter = false;
    //LinearLayout LinearLayout_DialogGravityCenter;
    //Switch Switch_DialogGravityCenter;

    boolean boolean_DialogGravityRight = false;
    LinearLayout LinearLayout_DialogGravityRight;
    TextView TextView_DialogGravityRight;
    Switch Switch_DialogGravityRight;

    boolean boolean_DialogGravityBottom = false;
    LinearLayout LinearLayout_DialogGravityBottom;
    TextView TextView_DialogGravityBottom;
    Switch Switch_DialogGravityBottom;

    AudioManager am;
    public static int amRingerMode;

    static float float_padding = 0;

    @Override
    public View onCreateView(LayoutInflater p1, ViewGroup p2, Bundle p3) {

        mContext = getActivity();

        am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        amRingerMode = am.getRingerMode();

        float_padding = MainActivity.preferences.getFloat("GraphicsPadding",0);

        MainActivity.visibleFragment = "Gravity";

        MainActivity.actionbar.setTitle(getString(R.string.advancedPrefsTitle_DialogGravity));
        MainActivity.actionbar.setSubTitle(getString(R.string.advancedPrefsDesc_DialogGravity));

        View InflatedView = p1.inflate(R.layout.dialoggravitychooser, p2, false);

        LinearLayout_ImageHolder = (LinearLayout) InflatedView.findViewById(R.id.dialoggravitychooserLinearLayout_ImageHolder);

        View DummyPowerDialog = p1.inflate(R.layout.fragment_power, null, false);
        LinearLayout Main = (LinearLayout) DummyPowerDialog.findViewById(R.id.fragmentpowerFrameLayout_Main);

        LinearLayout ListContainer = (LinearLayout) DummyPowerDialog.findViewById(R.id.ListContainer);

        FrameLayout frame = (FrameLayout) DummyPowerDialog.findViewById(R.id.frame);
        frame.setBackgroundColor(Color.parseColor(MainActivity.colorPrefs.getString("Dialog_Backgroundcolor", "#ffffff")));

        ((FrameLayout) DummyPowerDialog.findViewById(R.id.frame2)).setVisibility(View.GONE);
        ((FrameLayout) DummyPowerDialog.findViewById(R.id.frameConfirm)).setVisibility(View.GONE);

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
                if (MainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[10][1].toString(), PreferencesAnimationsFragment.defaultTypes[10]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                    root.startAnimation(helper.getAnimation(mContext, MainActivity.animationPrefs, 9, false));
                }
            } else {

                inflated = p1.inflate(R.layout.powermenu_multi, null, false);

                final String[] titles = mTitle.split("\\|");
                LinearLayout root = (LinearLayout) inflated.findViewById(R.id.powermenumulti_item1);
                ImageView icon = (ImageView) inflated.findViewById(R.id.powermenumulti_item1icon);
                ImageView icon2 = (ImageView) inflated.findViewById(R.id.powermenumulti_item1icon2);
                icon2.setVisibility(View.GONE);
                TextView text = (TextView) inflated.findViewById(R.id.powermenumulti_item1text);

                if (!titles[0].equalsIgnoreCase("Empty")) {
                    String string = "Failed to get String resource for powerMenuMain_" + titles[0];
                    try {
                        string = getResources().getString(getResources().getIdentifier("powerMenuMain_" + titles[0], "string", MainActivity.class.getPackage().getName()));
                    } catch (Throwable t) {
                        try {
                            string = getResources().getString(getResources().getIdentifier("powerMenuBottom_" + titles[0], "string", MainActivity.class.getPackage().getName()));
                        } catch (Throwable t1) {
                            string = "Failed to get String resource for powerMenuBottom_" + titles[0];
                            Log.e("NPM", "Failed to get String resource for powerMenuBottom_" + titles[0], t);
                        }
                    }
                    text.setText(string);
                    text.setTextColor(Color.parseColor(MainActivity.colorPrefs.getString("Dialog_Textcolor", "#000000")));

                    createCircleIcon(icon, icon2, string, MainActivity.colorPrefs.getString("Dialog" + titles[0] + "_Circlecolor", "#ff000000"), MainActivity.colorPrefs.getString("Dialog" + titles[0] + "_Textcolor", "#ffffff"));
                } else {
                    root.setVisibility(View.GONE);
                }
                if (MainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[13][1].toString(), PreferencesAnimationsFragment.defaultTypes[13]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                    root.startAnimation(helper.getAnimation(mContext, MainActivity.animationPrefs, 12, false));
                }

                LinearLayout root2 = (LinearLayout) inflated.findViewById(R.id.powermenumulti_item2);
                ImageView iconitem2 = (ImageView) inflated.findViewById(R.id.powermenumulti_item2icon);
                ImageView icon2item2 = (ImageView) inflated.findViewById(R.id.powermenumulti_item2icon2);
                icon2item2.setVisibility(View.GONE);
                TextView text2 = (TextView) inflated.findViewById(R.id.powermenumulti_item2text);
                if (!titles[1].equalsIgnoreCase("Empty")) {
                    String string2 = "Failed to get String resource for powerMenuMain_" + titles[1];
                    try {
                        string2 = getResources().getString(getResources().getIdentifier("powerMenuMain_" + titles[1], "string", MainActivity.class.getPackage().getName()));
                    } catch (Throwable t) {
                        try {
                            string2 = "Failed to get String resource for powerMenuBottom_" + titles[1];
                            string2 = getResources().getString(getResources().getIdentifier("powerMenuBottom_" + titles[1], "string", MainActivity.class.getPackage().getName()));
                        } catch (Throwable t1) {
                            Log.e("NPM", "Failed to get String resource for powerMenuBottom_" + titles[1], t);
                        }
                    }
                    text2.setText(string2);
                    text2.setTextColor(Color.parseColor(MainActivity.colorPrefs.getString("Dialog_Textcolor", "#000000")));

                    createCircleIcon(iconitem2, icon2item2, string2, MainActivity.colorPrefs.getString("Dialog" + titles[1] + "_Circlecolor", "#ff000000"), MainActivity.colorPrefs.getString("Dialog" + titles[1] + "_Textcolor", "#ffffff"));
                } else {
                    root2.setVisibility(View.GONE);
                }
                if (MainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[13][1].toString(), PreferencesAnimationsFragment.defaultTypes[13]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                    Animation anim = helper.getAnimation(mContext, MainActivity.animationPrefs, 12, false);
                    anim.setStartOffset((anim.getDuration() / 30) * 3);
                    root2.startAnimation(anim);
                }

                LinearLayout root3 = (LinearLayout) inflated.findViewById(R.id.powermenumulti_item3);
                ImageView iconitem3 = (ImageView) inflated.findViewById(R.id.powermenumulti_item3icon);
                ImageView icon2item3 = (ImageView) inflated.findViewById(R.id.powermenumulti_item3icon2);
                icon2item3.setVisibility(View.GONE);
                TextView text3 = (TextView) inflated.findViewById(R.id.powermenumulti_item3text);

                if (!titles[2].equalsIgnoreCase("Empty")) {
                    String string3 = "Failed to get String resource for powerMenuMain_" + titles[2];
                    try {
                        string3 = getResources().getString(getResources().getIdentifier("powerMenuMain_" + titles[2], "string", MainActivity.class.getPackage().getName()));
                    } catch (Throwable t) {
                        try {
                            string3 = "Failed to get String resource for powerMenuBottom_" + titles[2];
                            string3 = getResources().getString(getResources().getIdentifier("powerMenuBottom_" + titles[2], "string", MainActivity.class.getPackage().getName()));
                        } catch (Throwable t1) {
                            Log.e("NPM", "Failed to get String resource for powerMenuBottom_" + titles[2], t);
                        }
                    }
                    text3.setText(string3);
                    text3.setTextColor(Color.parseColor(MainActivity.colorPrefs.getString("Dialog_Textcolor", "#000000")));

                    createCircleIcon(iconitem3, icon2item3, string3, MainActivity.colorPrefs.getString("Dialog" + titles[2] + "_Circlecolor", "#ff000000"), MainActivity.colorPrefs.getString("Dialog" + titles[2] + "_Textcolor", "#ffffff"));
                } else {
                    root3.setVisibility(View.GONE);
                }
                if (MainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[13][1].toString(), PreferencesAnimationsFragment.defaultTypes[13]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                    Animation anim = helper.getAnimation(mContext, MainActivity.animationPrefs, 12, false);
                    anim.setStartOffset((anim.getDuration() / 30) * 6);
                    root3.startAnimation(anim);
                }
            }
            if (inflated != null) ListContainer.addView(inflated);

        }

        LinearLayout_ImageHolder.addView(DummyPowerDialog);
        LinearLayout.LayoutParams MainParams = new LinearLayout.LayoutParams(Main.getLayoutParams());
        MainParams.width = (int) helper.getDisplaySize(mContext, true)[0] - (60 * 2);
        //MainParams.height = (int) helper.convertDpToPixel((float) 250,mContext.getApplicationContext());
        Main.setLayoutParams(MainParams);

        String[] gravitys = getString(R.string.advancedPrefs_DialogGravity).split("\\|");

        boolean_DialogGravityTop = MainActivity.preferences.getBoolean("DialogGravityTop", false);
        LinearLayout_DialogGravityTop = (LinearLayout) InflatedView.findViewById(R.id.dialoggravitychooserLinearLayout_DialogGravityTop);
        Switch_DialogGravityTop = (Switch) InflatedView.findViewById(R.id.dialoggravitychooserSwitch_DialogGravityTop);
        TextView_DialogGravityTop = (TextView) InflatedView.findViewById(R.id.dialoggravitychooserText_DialogGravityTop);
        Switch_DialogGravityTop.setClickable(false);
        Switch_DialogGravityTop.setFocusable(false);
        Switch_DialogGravityTop.setChecked(boolean_DialogGravityTop);
        TextView_DialogGravityTop.setText(gravitys[0]);

        boolean_DialogGravityLeft = MainActivity.preferences.getBoolean("DialogGravityLeft", false);
        LinearLayout_DialogGravityLeft = (LinearLayout) InflatedView.findViewById(R.id.dialoggravitychooserLinearLayout_DialogGravityLeft);
        Switch_DialogGravityLeft = (Switch) InflatedView.findViewById(R.id.dialoggravitychooserSwitch_DialogGravityLeft);
        TextView_DialogGravityLeft = (TextView) InflatedView.findViewById(R.id.dialoggravitychooserText_DialogGravityLeft);
        Switch_DialogGravityLeft.setClickable(false);
        Switch_DialogGravityLeft.setFocusable(false);
        Switch_DialogGravityLeft.setChecked(boolean_DialogGravityLeft);
        TextView_DialogGravityLeft.setText(gravitys[1]);

        //boolean_DialogGravityCenter = MainActivity.preferences.getBoolean("DialogGravityCenter",false);
        //LinearLayout_DialogGravityCenter = (LinearLayout) InflatedView.findViewById(R.id.dialoggravitychooserLinearLayout_DialogGravityCenter);
        //Switch_DialogGravityCenter = (Switch) InflatedView.findViewById(R.id.dialoggravitychooserSwitch_DialogGravityCenter);
        //Switch_DialogGravityCenter.setClickable(false);
        //Switch_DialogGravityCenter.setFocusable(false);
        //Switch_DialogGravityCenter.setChecked(boolean_DialogGravityCenter);

        boolean_DialogGravityRight = MainActivity.preferences.getBoolean("DialogGravityRight", false);
        LinearLayout_DialogGravityRight = (LinearLayout) InflatedView.findViewById(R.id.dialoggravitychooserLinearLayout_DialogGravityRight);
        Switch_DialogGravityRight = (Switch) InflatedView.findViewById(R.id.dialoggravitychooserSwitch_DialogGravityRight);
        TextView_DialogGravityRight = (TextView) InflatedView.findViewById(R.id.dialoggravitychooserText_DialogGravityRight);
        Switch_DialogGravityRight.setClickable(false);
        Switch_DialogGravityRight.setFocusable(false);
        Switch_DialogGravityRight.setChecked(boolean_DialogGravityRight);
        TextView_DialogGravityRight.setText(gravitys[2]);

        boolean_DialogGravityBottom = MainActivity.preferences.getBoolean("DialogGravityBottom", false);
        LinearLayout_DialogGravityBottom = (LinearLayout) InflatedView.findViewById(R.id.dialoggravitychooserLinearLayout_DialogGravityBottom);
        Switch_DialogGravityBottom = (Switch) InflatedView.findViewById(R.id.dialoggravitychooserSwitch_DialogGravityBottom);
        TextView_DialogGravityBottom = (TextView) InflatedView.findViewById(R.id.dialoggravitychooserText_DialogGravityBottom);
        Switch_DialogGravityBottom.setClickable(false);
        Switch_DialogGravityBottom.setFocusable(false);
        Switch_DialogGravityBottom.setChecked(boolean_DialogGravityBottom);
        TextView_DialogGravityBottom.setText(gravitys[3]);

        LinearLayout_DialogGravityTop.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                boolean_DialogGravityTop = !boolean_DialogGravityTop;
                Switch_DialogGravityTop.setChecked(boolean_DialogGravityTop);
                if (boolean_DialogGravityTop) {
                    boolean_DialogGravityBottom = false;
                    Switch_DialogGravityBottom.setChecked(boolean_DialogGravityBottom);
                    MainActivity.preferences.edit().putBoolean("DialogGravityBottom", boolean_DialogGravityBottom).apply();
                }
                MainActivity.preferences.edit().putBoolean("DialogGravityTop", boolean_DialogGravityTop).apply();
                changeGravity();
            }
        });

        LinearLayout_DialogGravityLeft.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                boolean_DialogGravityLeft = !boolean_DialogGravityLeft;
                Switch_DialogGravityLeft.setChecked(boolean_DialogGravityLeft);
                if (boolean_DialogGravityLeft) {
                    boolean_DialogGravityRight = false;
                    Switch_DialogGravityRight.setChecked(boolean_DialogGravityRight);
                    MainActivity.preferences.edit().putBoolean("DialogGravityRight", boolean_DialogGravityRight).apply();
                }
                MainActivity.preferences.edit().putBoolean("DialogGravityLeft", boolean_DialogGravityLeft).apply();
                changeGravity();
            }
        });

        LinearLayout_DialogGravityRight.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                boolean_DialogGravityRight = !boolean_DialogGravityRight;
                Switch_DialogGravityRight.setChecked(boolean_DialogGravityRight);
                if (boolean_DialogGravityRight) {
                    boolean_DialogGravityLeft = false;
                    Switch_DialogGravityLeft.setChecked(boolean_DialogGravityLeft);
                    MainActivity.preferences.edit().putBoolean("DialogGravityLeft", boolean_DialogGravityLeft).apply();
                }
                MainActivity.preferences.edit().putBoolean("DialogGravityRight", boolean_DialogGravityRight).apply();
                changeGravity();
            }
        });

        LinearLayout_DialogGravityBottom.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                boolean_DialogGravityBottom = !boolean_DialogGravityBottom;
                Switch_DialogGravityBottom.setChecked(boolean_DialogGravityBottom);
                if (boolean_DialogGravityBottom) {
                    boolean_DialogGravityTop = false;
                    Switch_DialogGravityTop.setChecked(boolean_DialogGravityTop);
                    MainActivity.preferences.edit().putBoolean("DialogGravityTop", boolean_DialogGravityTop).apply();
                }
                MainActivity.preferences.edit().putBoolean("DialogGravityBottom", boolean_DialogGravityBottom).apply();
                changeGravity();
            }
        });

        changeGravity();

        return InflatedView;
    }


    public void createCircleIcon(ImageView background, final ImageView foreground, String text, String color1, String color2) {
        try {
            if (MainActivity.preferences.getBoolean("UseGraphics", false)) {
                GraphicDrawable drawable = GraphicDrawable.builder().buildRound((Bitmap) null, Color.parseColor(color1));
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
        if (new File(mContext.getFilesDir().getPath() + "/images/" + PreferencesGraphicsFragment.defaultGraphics[id][2] + ".png").exists()) {
            MainActivity.imageLoader.displayImage("file://" + mContext.getFilesDir().getPath() + "/images/" + PreferencesGraphicsFragment.defaultGraphics[id][2] + ".png",
                    image, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            image.setImageBitmap(null);
                            image.setPadding((int) helper.convertDpToPixel(5,mContext), (int) helper.convertDpToPixel(5,mContext), (int) helper.convertDpToPixel(5,mContext), (int) helper.convertDpToPixel(5,mContext));
                            image.setColorFilter(Color.parseColor("#ffffff"),
                                    android.graphics.PorterDuff.Mode.DST);
                            if (MainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[7][1].toString(), PreferencesAnimationsFragment.defaultTypes[7]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                                image.startAnimation(helper.getAnimation(mContext, MainActivity.animationPrefs, 6, true));
                            }
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
                            if (MainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[7][1].toString(), PreferencesAnimationsFragment.defaultTypes[7]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                                image.startAnimation(helper.getAnimation(mContext, MainActivity.animationPrefs, 6, false));
                            }
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            Log.e("NPM:xposedDialog", "Failed to load image '" + imageUri + "': " + failReason.getCause().toString());
                            image.setImageDrawable(mContext.getResources().getDrawable((int) PreferencesGraphicsFragment.defaultGraphics[id][1]));
                            image.setColorFilter(Color.parseColor(color),
                                    android.graphics.PorterDuff.Mode.SRC_IN);
                            image.setVisibility(View.VISIBLE);
                            if (MainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[7][1].toString(), PreferencesAnimationsFragment.defaultTypes[7]) != mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
                                image.startAnimation(helper.getAnimation(mContext, MainActivity.animationPrefs, 6, false));
                            }
                        }
                    });
        } else {
            image.setPadding((int) Math.max(float_padding,helper.convertDpToPixel(5,mContext)),(int) Math.max(float_padding,helper.convertDpToPixel(5,mContext)),(int) Math.max(float_padding,helper.convertDpToPixel(5,mContext)),(int) Math.max(float_padding,helper.convertDpToPixel(5,mContext)));
            image.setImageDrawable(mContext.getResources().getDrawable((int) PreferencesGraphicsFragment.defaultGraphics[id][1]));
            image.setColorFilter(Color.parseColor(color),
                    android.graphics.PorterDuff.Mode.SRC_IN);
            image.setVisibility(View.VISIBLE);
            image.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
        }
    }

    void changeGravity() {
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
        LinearLayout_ImageHolder.setGravity(gravity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}

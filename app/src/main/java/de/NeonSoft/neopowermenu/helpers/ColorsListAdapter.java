package de.NeonSoft.neopowermenu.helpers;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import de.NeonSoft.neopowermenu.MainActivity;
import de.NeonSoft.neopowermenu.Preferences.PreferencesAnimationsFragment;
import de.NeonSoft.neopowermenu.Preferences.PreferencesColorFragment;
import de.NeonSoft.neopowermenu.Preferences.PreferencesPresetsFragment;
import de.NeonSoft.neopowermenu.R;
import de.NeonSoft.neopowermenu.neopowermenu;

public class ColorsListAdapter extends ArrayAdapter {

    public static Activity context;
    LayoutInflater mInflater;
    public static ArrayList<Object[]> colorNamesArray;
    public static ArrayList<String[]> defaultColors;
    PackageManager mPackageManager;

    public static final int TYPE_EMPTY = 0, TYPE_HEADER = 1, TYPE_ITEM = 2, TYPE_LOAD = 10, TYPE_SAVE = 11;

    public ColorsListAdapter(Activity context, ArrayList<Object[]> colorNames, ArrayList<String[]> defaultColors) {
        super(context, R.layout.colorslistitem, colorNames);
        this.context = context;
        this.mInflater = context.getLayoutInflater();
        this.colorNamesArray = colorNames;
        this.defaultColors = defaultColors;
        mPackageManager = context.getPackageManager();
    }

    public int getItemType(int p1) {

        return (int) colorNamesArray.get(p1)[0];
    }

    @NonNull
    @Override
    public View getView(final int p1, @Nullable View InflatedView, @NonNull ViewGroup parent) {

        final LinearLayout root, headerHolder, itemHolder1, itemHolder2, Legacy_Layout, Legacy_previewLayout;
        final TextView Legacy_Title, Legacy_Desc;
        String[] loadColor;
        String colorType;
        InflatedView = mInflater.inflate(R.layout.colorslistitem, null);

        root = (LinearLayout) InflatedView.findViewById(R.id.colorslistitemLinearLayout_Root);
        headerHolder = (LinearLayout) InflatedView.findViewById(R.id.colorslistitemLinearLayout_HeaderHolder);
        itemHolder1 = (LinearLayout) InflatedView.findViewById(R.id.colorslistitemLinearLayout_ItemHolder1);
        itemHolder2 = (LinearLayout) InflatedView.findViewById(R.id.colorslistitemLinearLayout_ItemHolder2);
        Legacy_Layout = (LinearLayout) InflatedView.findViewById(R.id.colorslistitemLinearLayout_Legacy);
        Legacy_previewLayout = (LinearLayout) InflatedView.findViewById(R.id.colorslistitemLinearLayout_Preview);
        Legacy_Title = (TextView) InflatedView.findViewById(R.id.colorslistitemTextView_Text);
        Legacy_Desc = (TextView) InflatedView.findViewById(R.id.colorslistitemTextView_Desc);

        int rowType = getItemType(p1);
        switch (rowType) {
            case TYPE_LOAD:
                Legacy_Layout.setVisibility(View.VISIBLE);
                Legacy_previewLayout.setVisibility(View.GONE);
                Legacy_Title.setText(R.string.preset_Load);
                Legacy_Desc.setText(R.string.preset_LoadDesc);
                root.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View p1) {

                        MainActivity.changePrefPage(new PreferencesPresetsFragment(), false);
                    }
                });
                break;
            case TYPE_SAVE:
                Legacy_Layout.setVisibility(View.VISIBLE);
                Legacy_previewLayout.setVisibility(View.GONE);
                Legacy_Title.setText(R.string.preset_Save);
                Legacy_Desc.setText(R.string.preset_SaveDesc);
                root.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View p1) {

                        final slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                        dialogFragment.setContext(context);
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

                                String selectedOptions = resultBundle.getString(slideDownDialogFragment.RESULT_LIST);

                                try {
                                    File presetFile = new File(context.getFilesDir().getPath() + "/presets/" + resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0").replace("/", "").trim() + ".nps");
                                    presetFile.createNewFile();
                                    FileWriter fw = new FileWriter(presetFile);
                                    fw.append("[INFO]" + "\n");
                                    fw.append("AppVersion=" + MainActivity.versionName + "\n");
                                    fw.append("AppVersionCode=" + MainActivity.versionCode + "\n");
                                    if (!resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "1").equalsIgnoreCase("")) {
                                        MainActivity.preferences.edit().putString("lastPresetCreatedBy", resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "1")).commit();
                                        fw.append("Creator=" + resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "1").replace("/", "").trim() + "\n");
                                    } else {
                                        fw.append("Creator=a " + Build.BRAND + " " + Build.MODEL + " user\n");
                                    }
                                    fw.append("GraphicsScale=" + MainActivity.preferences.getFloat("GraphicsPadding", 0) + "\n");
                                    fw.append("ColorizeNonStockIcons=" + MainActivity.preferences.getBoolean("ColorizeNonStockIcons", false) + "\n");
                                    fw.append("CircleRadius=" + MainActivity.preferences.getInt(PreferenceNames.pCircleRadius, 100) + "\n");
                                    fw.append("DialogCornersRadius=" + MainActivity.preferences.getInt(PreferenceNames.pRoundedDialogCornersRadius, 0) + "\n");
                                    if (selectedOptions.contains(context.getString(R.string.savePreset_Colors))) {
                                        fw.append("[COLORS]" + "\n");
                                        for (int i = 0; i < colorNamesArray.size(); i++) {
                                            for (int x = 2; x < colorNamesArray.get(i).length; x++) {
                                                String[] loadColor = colorNamesArray.get(i)[x].toString().split("_");
                                                if (loadColor.length > 1) {
                                                    if (loadColor[1].contains("Reveal")) {
                                                        fw.append(loadColor[0] + "_Revealcolor=" + MainActivity.colorPrefs.getString(loadColor[0] + "_Revealcolor", defaultColors.get(i)[x-1]) + "\n");
                                                    } else if (loadColor[1].contains("Background")) {
                                                        fw.append(loadColor[0] + "_Backgroundcolor=" + MainActivity.colorPrefs.getString(loadColor[0] + "_Backgroundcolor", defaultColors.get(i)[x-1]) + "\n");
                                                    } else if (loadColor[1].contains("Text")) {
                                                        fw.append(loadColor[0] + "_Textcolor=" + MainActivity.colorPrefs.getString(loadColor[0] + "_Textcolor", defaultColors.get(i)[x-1]) + "\n");
                                                    } else if (loadColor[1].contains("Circle")) {
                                                        fw.append(loadColor[0] + "_Circlecolor=" + MainActivity.colorPrefs.getString(loadColor[0] + "_Circlecolor", defaultColors.get(i)[x-1]) + "\n");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (selectedOptions.contains(context.getString(R.string.savePreset_Animations))) {
                                        fw.append("[ANIMATIONS]" + "\n");
                                        for (int i = 0; i < PreferencesAnimationsFragment.names.length; i++) {
                                            String[] loadAnimation = PreferencesAnimationsFragment.names[i][1].toString().split("_");
                                            if (loadAnimation.length > 1) {
                                                fw.append(PreferencesAnimationsFragment.names[i][1].toString() + "=" + MainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[i][1].toString(), PreferencesAnimationsFragment.defaultTypes[i]) + "\n");
                                            }
                                        }
                                    }
                                    fw.close();
                                    if (selectedOptions.contains(context.getString(R.string.savePreset_Graphics))) {
                                        helper.zipAll(context.getFilesDir().getPath() + "/images/", context.getFilesDir().getPath() + "/temp/" + resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0").replace("/", "").trim() + ".zip", null);
                                        helper.zipFile(context.getFilesDir().getPath() + "/presets/" + resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0").replace("/", "").trim() + ".nps", context.getFilesDir().getPath() + "/temp/" + resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0").replace("/", "").trim() + ".zip", null);
                                        new File(context.getFilesDir().getPath() + "/presets/" + resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0").replace("/", "").trim() + ".nps").delete();
                                        if (new File(context.getFilesDir().getPath() + "/temp/" + resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0").replace("/", "").trim() + ".zip").renameTo(new File(context.getFilesDir().getPath() + "/presets/" + resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0").replace("/", "").trim() + ".nps"))) {
                                            Toast.makeText(context.getApplicationContext(), context.getString(R.string.presetSave_PresetSaved).replace("[PRESETNAME]", resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0").replace("/", "").trim()), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(context.getApplicationContext(), context.getString(R.string.presetSave_PresetSaved).replace("[PRESETNAME]", resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0").replace("/", "").trim()), Toast.LENGTH_SHORT).show();
                                    }
                                    MainActivity.preferences.edit().putString("lastUsedPreset", resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0").replace("/", "").trim()).commit();
                                } catch (IOException e) {
                                }
                            }

                            @Override
                            public void onTouchOutside() {

                            }
                        });
                        dialogFragment.setText("");
                        dialogFragment.addInput(context.getString(R.string.presetSaveDialog_InfoText), MainActivity.preferences.getString("lastUsedPreset", ""), false, new TextWatcher() {

                                    @Override
                                    public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {

                                        if (!p1.toString().equalsIgnoreCase("")) {
                                            File checkFile = new File(context.getFilesDir() + "/presets/" + p1.toString().replace("/", "").trim() + ".nps");
                                            if (!checkFile.exists()) {
                                                dialogFragment.showAssistInfo(false);
                                                //dialogFragment.setDialogText("");
                                            } else {
                                                dialogFragment.showAssistInfo(true);
                                                //dialogFragment.setDialogText(context.getString(R.string.presetSaveDialog_OverwriteText));
                                            }
                                        }
                                    }

                                    @Override
                                    public void afterTextChanged(Editable p1) {

                                    }
                                }
                        );
                        dialogFragment.addInput(context.getString(R.string.presetSaveDialog_CreatorNameInfo), MainActivity.preferences.getString("lastPresetCreatedBy", ""), true, null);
                        dialogFragment.setInputAssistInfo(context.getString(R.string.presetSaveDialog_OverwriteText));
                        File graphicsDir = new File(context.getFilesDir().getPath() + "/images/");
                        File[] graphics = graphicsDir.listFiles();
                        ArrayList<String> options = new ArrayList<>();
                        ArrayList<Boolean> checked = new ArrayList<>();
                        options.add(context.getString(R.string.savePreset_Colors));
                        checked.add(true);
                        options.add(context.getString(R.string.savePreset_Animations));
                        checked.add(false);
                        if (graphics.length > 0)
                            options.add(context.getString(R.string.savePreset_Graphics));
                        if (graphics.length > 0) checked.add(false);
                        dialogFragment.setList(ListView.CHOICE_MODE_MULTIPLE, options, -1, false);
                        dialogFragment.setListChecks(checked);
                        dialogFragment.setListReturnMode(slideDownDialogFragment.LIST_RETURN_MODE_TEXT);
                        dialogFragment.setNegativeButton(context.getString(R.string.Dialog_Buttons).split("\\|")[4]);
                        dialogFragment.setPositiveButton(context.getString(R.string.Dialog_Buttons).split("\\|")[7]);
                        dialogFragment.showDialog(R.id.dialog_container);
                    }
                });
                break;
            case TYPE_ITEM:
                View header = mInflater.inflate(R.layout.listheader, headerHolder);
                TextView HeaderTitle = (TextView) header.findViewById(R.id.listheaderTextView_Title);
                TextView HeaderDesc = (TextView) header.findViewById(R.id.listheaderTextView_Desc);
                String mainString = "";
                if (colorNamesArray.get(p1)[1].toString().contains(".")) {
                    try {
                        HeaderTitle.setText(mPackageManager.getApplicationInfo(colorNamesArray.get(p1)[1].toString(), 0).loadLabel(mPackageManager).toString());
                    } catch (PackageManager.NameNotFoundException e) {
                        HeaderTitle.setText(colorNamesArray.get(p1)[1].toString());
                        Log.e("NPM", "No package found for resource " + colorNamesArray.get(p1)[1].toString(), e);
                    }
                } else if (colorNamesArray.get(p1)[1].toString().equalsIgnoreCase("Presets") || colorNamesArray.get(p1)[1].toString().equalsIgnoreCase("Reveal") || colorNamesArray.get(p1)[1].toString().equalsIgnoreCase("Main")) {
                    try {
                        mainString = context.getResources().getString(context.getResources().getIdentifier("colorsPart" + colorNamesArray.get(p1)[1], "string", MainActivity.class.getPackage().getName()));
                        String title = (colorNamesArray.get(p1)[1].toString().equalsIgnoreCase("Presets") ? mainString : context.getString(R.string.colorsPartTitle).replace("[PART]", mainString));//getResources().getString(context.getResources().getIdentifier("colorsCategory_"+colorNamesArray[p1][1].toString(),"string",MainActivity.class.getPackage().getName()));
                        HeaderTitle.setText(title);
                    } catch (Throwable t1) {
                        HeaderTitle.setText("String Resource for colorsPart" + colorNamesArray.get(p1)[1] + " not found.");
                    }
                } else {
                    try {
                        mainString = context.getResources().getString(context.getResources().getIdentifier("powerMenuMain_" + colorNamesArray.get(p1)[1], "string", MainActivity.class.getPackage().getName()));
                        String title = context.getString(R.string.colorsPartTitle).replace("[PART]", mainString);//getResources().getString(context.getResources().getIdentifier("colorsCategory_"+colorNamesArray[p1][1].toString(),"string",MainActivity.class.getPackage().getName()));
                        HeaderTitle.setText(title);
                    } catch (Throwable t) {
                        try {
                            mainString = context.getResources().getString(context.getResources().getIdentifier("powerMenuBottom_" + colorNamesArray.get(p1)[1], "string", MainActivity.class.getPackage().getName()));
                            String title = context.getString(R.string.colorsPartTitle).replace("[PART]", mainString);//getResources().getString(context.getResources().getIdentifier("colorsCategory_"+colorNamesArray[p1][1].toString(),"string",MainActivity.class.getPackage().getName()));
                            HeaderTitle.setText(title);
                        } catch (Throwable t1) {
                            HeaderTitle.setText(colorNamesArray.get(p1)[1].toString());
                        }
                    }
                }
                if (colorNamesArray.get(p1)[1].toString().contains(".")) {
                    HeaderDesc.setText(context.getString(R.string.powerMenuMain_AppShortcut));
                    HeaderDesc.setVisibility(View.VISIBLE);
                } else {
                    try {
                        String Description = "";
                        mainString = context.getResources().getString(context.getResources().getIdentifier("powerMenuMain_" + colorNamesArray.get(p1)[1], "string", MainActivity.class.getPackage().getName()));
                        if (mainString.equalsIgnoreCase(context.getString(R.string.powerMenuMain_Shutdown)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuMain_Reboot)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuMain_SoftReboot)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuBottom_Recovery)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuBottom_Bootloader)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuBottom_SafeMode))) {
                            Description = context.getString(R.string.colorsPartDescDialog).replace("[BUTTON]", mainString);
                        } else {
                            Description = context.getString(R.string.colorsPartDescButton).replace("[BUTTON]", mainString);
                        }
                        //String Description = context.getResources().getString(context.getResources().getIdentifier("colorsDesc_Dialog"+colorNamesArray[p1][1],"string",MainActivity.class.getPackage().getName()));
                        HeaderDesc.setText(Description);
                        HeaderDesc.setVisibility(View.VISIBLE);
                    } catch (Throwable t) {
                        try {
                            String Description = "";
                            mainString = context.getResources().getString(context.getResources().getIdentifier("powerMenuBottom_" + colorNamesArray.get(p1)[1], "string", MainActivity.class.getPackage().getName()));
                            if (mainString.equalsIgnoreCase(context.getString(R.string.powerMenuMain_Shutdown)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuMain_Reboot)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuMain_SoftReboot)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuBottom_Recovery)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuBottom_Bootloader)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuBottom_SafeMode))) {
                                Description = context.getString(R.string.colorsPartDescDialog).replace("[BUTTON]", mainString);
                            } else {
                                Description = context.getString(R.string.colorsPartDescButton).replace("[BUTTON]", mainString);
                            }
                            //String Description = context.getResources().getString(context.getResources().getIdentifier("colorsDesc_Dialog"+colorNamesArray[p1][1],"string",MainActivity.class.getPackage().getName()));
                            HeaderDesc.setText(Description);
                            HeaderDesc.setVisibility(View.VISIBLE);
                        } catch (Throwable t1) {
                            if (defaultColors.get(p1)[0].isEmpty()) {
                                HeaderDesc.setVisibility(View.VISIBLE);
                                HeaderDesc.setText(context.getString(R.string.powerMenuMain_Shortcut));
                            } else {
                                HeaderDesc.setVisibility(View.GONE);
                            }
                        }
                    }
                }
                header.setEnabled(false);
                root.setEnabled(false);
                for (int i = 2; i < colorNamesArray.get(p1).length; i++) {
                    final int finalI = i-1;
                    int limitPerRow = 2 + 2;
                    if (colorNamesArray.get(p1).length-2 == 3) {
                        limitPerRow = 2 + 3;
                    }
                    View item = mInflater.inflate(R.layout.colorlistitemholder, (i < limitPerRow ? itemHolder1 : itemHolder2), false);
                    LinearLayout rootView = (LinearLayout) item.findViewById(R.id.colorslistitemholder_Root);
                    final TextView previewView = (TextView) item.findViewById(R.id.colorslistitemholder_Preview);
                    TextView titleView = (TextView) item.findViewById(R.id.colorslistitemholder_Title);


                    loadColor = colorNamesArray.get(p1)[i].toString().split("_");

                    final String currentColor;
                    if (loadColor[1].contains("Reveal")) {
                        colorType = "_Revealcolor";
                    } else if (loadColor[1].contains("Background")) {
                        colorType = "_Backgroundcolor";
                    } else if (loadColor[1].contains("Circle")) {
                        colorType = "_Circlecolor";
                    } else if (loadColor[1].contains("Text")) {
                        colorType = "_Textcolor";
                    } else {
                        colorType = "null";
                    }
                    String title = "";
                    if (loadColor[1].contains("Reveal")) {
                        title = context.getString(R.string.colorsType_Reveal);
                    } else if (loadColor[1].contains("Background")) {
                        title = context.getString(R.string.colorsType_Background);
                        if (loadColor[0].contains("Reveal")) {
                            title = context.getString(R.string.colorsType_Reveal);
                        }
                    } else if (loadColor[1].contains("Circle")) {
                        title = context.getString(R.string.colorsType_Circle);
                    } else if (loadColor[1].contains("Text")) {
                        title = context.getString(R.string.colorsType_Text);
                    }
                    titleView.setText(title);
                    String loadedColor = "#ff0000";
                    try {
                        loadedColor = MainActivity.colorPrefs.getString(loadColor[0] + colorType, defaultColors.get(p1)[finalI]);
                    } catch (Throwable t) {
                        loadedColor = "#ff0000";
                        previewView.setText("Error: " + loadColor[0] + colorType);
                    }
                    currentColor = loadedColor;
                    try {
                        previewView.setBackgroundColor(Color.parseColor(currentColor));
                    } catch (Throwable t) {
                        Log.e("NPM", "Failed to load color '" + currentColor + "'", t);
                    }

                    final String[] finalLoadColor = loadColor;
                    final String finalColorType = colorType;

                    rootView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            final slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                            dialogFragment.setContext(context);
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

                                    MainActivity.colorPrefs.edit().putString(finalLoadColor[0] + finalColorType, resultBundle.getString(slideDownDialogFragment.RESULT_COLORPICKER)).apply();
                                    notifyDataSetChanged();
                                }

                                @Override
                                public void onTouchOutside() {

                                }
                            });
                            dialogFragment.setText("");
                            dialogFragment.setColorPicker(MainActivity.colorPrefs.getString(finalLoadColor[0] + finalColorType, defaultColors.get(p1)[finalI]), defaultColors.get(p1)[finalI].length() != 7);
                            dialogFragment.setNegativeButton(context.getString(R.string.Dialog_Buttons).split("\\|")[4]);
                            dialogFragment.setPositiveButton(context.getString(R.string.Dialog_Buttons).split("\\|")[7]);
                            dialogFragment.showDialog(R.id.dialog_container);
                        }
                    });
                    rootView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                            dialogFragment.setContext(context);
                            dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                            dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {
                                @Override
                                public void onListItemClick(int position, String text) {
                                    if (position == 0) {
                                        for (int i = 0; i < colorNamesArray.size(); i++) {
                                            for (int x = 2; x < colorNamesArray.get(i).length; x++) {
                                                if (colorNamesArray.get(i)[x].toString().contains(finalColorType)) {
                                                    MainActivity.colorPrefs.edit().putString(colorNamesArray.get(i)[x].toString(), currentColor).apply();
                                                    notifyDataSetChanged();
                                                }
                                            }
                                        }
                                    } else if (position == 1) {
                                        MainActivity.colorPrefs.edit().putString(finalLoadColor[0] + finalColorType, (p1 > colorNamesArray.size() ? "#ffffff" : defaultColors.get(p1)[finalI])).apply();
                                        previewView.setBackgroundColor(Color.parseColor((p1 > colorNamesArray.size() ? "#ffffff" : defaultColors.get(p1)[finalI])));
                                        notifyDataSetChanged();
                                    } else if (position == 2) {
                                        ClipData clip = ClipData.newPlainText("ColorCopy", currentColor);
                                        PreferencesColorFragment.cbM.setPrimaryClip(clip);
                                        Toast.makeText(context, context.getString(R.string.colorsCopied), Toast.LENGTH_SHORT).show();
                                    } else if (position == 3) {
                                        if (PreferencesColorFragment.cbM.hasPrimaryClip() && PreferencesColorFragment.cbM.getPrimaryClip().getItemAt(0).getText().toString().matches("#[0-9a-fA-F]{6,8}")) {
                                            String color = PreferencesColorFragment.cbM.getPrimaryClip().getItemAt(0).getText().toString();
                                            if (color.length() < defaultColors.get(p1)[finalI].length()) {
                                                color = color.replace("#", "#FF");
                                            } else if (color.length() > defaultColors.get(p1)[finalI].length()) {
                                                color = "#" + color.substring(3, color.length());
                                            }
                                            MainActivity.colorPrefs.edit().putString(finalLoadColor[0] + finalColorType, color).apply();
                                            previewView.setBackgroundColor(Color.parseColor(defaultColors.get(p1)[finalI]));
                                            notifyDataSetChanged();
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
                            ArrayList<String> options = new ArrayList<>();
                            options.add(context.getString(R.string.colorsApplyToAll));
                            options.add(context.getString(R.string.colorsResetToDefault));
                            options.add(context.getString(R.string.colorsCopyColor));
                            if (PreferencesColorFragment.cbM.hasPrimaryClip() && PreferencesColorFragment.cbM.getPrimaryClip().getItemAt(0).getText().toString().matches("#[0-9a-fA-F]{6,8}")) {
                                options.add(context.getString(R.string.colorsInsertColor));
                            }
                            dialogFragment.setList(ListView.CHOICE_MODE_NONE, options, -1, true);
                            dialogFragment.setPositiveButton(context.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                            dialogFragment.showDialog(R.id.dialog_container);
                            return true;
                        }
                    });

                    if (i < limitPerRow) {
                        itemHolder1.addView(item);
                    } else {
                        itemHolder2.addView(item);
                    }
                }
                break;
            case TYPE_HEADER:
                View xheader = mInflater.inflate(R.layout.listheader, null);
                TextView xHeaderTitle = (TextView) xheader.findViewById(R.id.listheaderTextView_Title);
                TextView xHeaderDesc = (TextView) xheader.findViewById(R.id.listheaderTextView_Desc);
                String xmainString = "";
                if (colorNamesArray.get(p1)[1].toString().contains(".")) {
                    try {
                        xHeaderTitle.setText(mPackageManager.getApplicationInfo(colorNamesArray.get(p1)[1].toString(), 0).loadLabel(mPackageManager).toString());
                    } catch (PackageManager.NameNotFoundException e) {
                        xHeaderTitle.setText(colorNamesArray.get(p1)[1].toString());
                        Log.e("NPM", "No package found for resource " + colorNamesArray.get(p1)[1].toString(), e);
                    }
                } else if (colorNamesArray.get(p1)[1].toString().equalsIgnoreCase("Presets") || colorNamesArray.get(p1)[1].toString().equalsIgnoreCase("Reveal") || colorNamesArray.get(p1)[1].toString().equalsIgnoreCase("Main")) {
                    try {
                        mainString = context.getResources().getString(context.getResources().getIdentifier("colorsPart" + colorNamesArray.get(p1)[1], "string", MainActivity.class.getPackage().getName()));
                        String title = (colorNamesArray.get(p1)[1].toString().equalsIgnoreCase("Presets") ? mainString : context.getString(R.string.colorsPartTitle).replace("[PART]", mainString));//getResources().getString(context.getResources().getIdentifier("colorsCategory_"+colorNamesArray[p1][1].toString(),"string",MainActivity.class.getPackage().getName()));
                        xHeaderTitle.setText(title);
                    } catch (Throwable t1) {
                        xHeaderTitle.setText("String Resource for colorsPart" + colorNamesArray.get(p1)[1] + " not found.");
                    }
                } else {
                    try {
                        mainString = context.getResources().getString(context.getResources().getIdentifier("powerMenuMain_" + colorNamesArray.get(p1)[1], "string", MainActivity.class.getPackage().getName()));
                        String title = context.getString(R.string.colorsPartTitle).replace("[PART]", mainString);//getResources().getString(context.getResources().getIdentifier("colorsCategory_"+colorNamesArray[p1][1].toString(),"string",MainActivity.class.getPackage().getName()));
                        xHeaderTitle.setText(title);
                    } catch (Throwable t) {
                        try {
                            mainString = context.getResources().getString(context.getResources().getIdentifier("powerMenuBottom_" + colorNamesArray.get(p1)[1], "string", MainActivity.class.getPackage().getName()));
                            String title = context.getString(R.string.colorsPartTitle).replace("[PART]", mainString);//getResources().getString(context.getResources().getIdentifier("colorsCategory_"+colorNamesArray[p1][1].toString(),"string",MainActivity.class.getPackage().getName()));
                            xHeaderTitle.setText(title);
                        } catch (Throwable t1) {
                            xHeaderTitle.setText(colorNamesArray.get(p1)[1].toString());
                        }
                    }
                }
                if (colorNamesArray.get(p1)[1].toString().contains(".")) {
                    xHeaderDesc.setText(context.getString(R.string.powerMenuMain_AppShortcut));
                    xHeaderDesc.setVisibility(View.VISIBLE);
                } else {
                    try {
                        String Description = "";
                        mainString = context.getResources().getString(context.getResources().getIdentifier("powerMenuMain_" + colorNamesArray.get(p1)[1], "string", MainActivity.class.getPackage().getName()));
                        if (mainString.equalsIgnoreCase(context.getString(R.string.powerMenuMain_Shutdown)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuMain_Reboot)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuMain_SoftReboot)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuBottom_Recovery)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuBottom_Bootloader)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuBottom_SafeMode))) {
                            Description = context.getString(R.string.colorsPartDescDialog).replace("[BUTTON]", mainString);
                        } else {
                            Description = context.getString(R.string.colorsPartDescButton).replace("[BUTTON]", mainString);
                        }
                        //String Description = context.getResources().getString(context.getResources().getIdentifier("colorsDesc_Dialog"+colorNamesArray[p1][1],"string",MainActivity.class.getPackage().getName()));
                        xHeaderDesc.setText(Description);
                        xHeaderDesc.setVisibility(View.VISIBLE);
                    } catch (Throwable t) {
                        try {
                            String Description = "";
                            mainString = context.getResources().getString(context.getResources().getIdentifier("powerMenuBottom_" + colorNamesArray.get(p1)[1], "string", MainActivity.class.getPackage().getName()));
                            if (mainString.equalsIgnoreCase(context.getString(R.string.powerMenuMain_Shutdown)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuMain_Reboot)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuMain_SoftReboot)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuBottom_Recovery)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuBottom_Bootloader)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuBottom_SafeMode))) {
                                Description = context.getString(R.string.colorsPartDescDialog).replace("[BUTTON]", mainString);
                            } else {
                                Description = context.getString(R.string.colorsPartDescButton).replace("[BUTTON]", mainString);
                            }
                            //String Description = context.getResources().getString(context.getResources().getIdentifier("colorsDesc_Dialog"+colorNamesArray[p1][1],"string",MainActivity.class.getPackage().getName()));
                            xHeaderDesc.setText(Description);
                            xHeaderDesc.setVisibility(View.VISIBLE);
                        } catch (Throwable t1) {
                            if (defaultColors.get(p1)[0].isEmpty()) {
                                xHeaderDesc.setVisibility(View.VISIBLE);
                                xHeaderDesc.setText(context.getString(R.string.powerMenuMain_Shortcut));
                            } else {
                                xHeaderDesc.setVisibility(View.GONE);
                            }
                        }
                    }
                }
                xheader.setEnabled(false);
                InflatedView = xheader;
                break;
        }
        return InflatedView;
    }
}

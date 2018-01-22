package de.NeonSoft.neopowermenu.helpers;

import android.app.*;
import android.content.pm.*;
import android.graphics.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.Preferences.*;

import java.io.*;
import java.util.*;

import android.os.*;

public class ColorsListAdapter extends ArrayAdapter<Object> {
    public static Activity context;
    LayoutInflater mInflater;
    public static Object[][] colorNamesArray;
    public static String[] defaultColors;

    public static final int TYPE_EMPTY = 0, TYPE_HEADER = 1, TYPE_ITEM = 2, TYPE_LOAD = 10, TYPE_SAVE = 11;

    public ColorsListAdapter(Activity context, Object[][] colorNames, String[] defaultColors) {
        super(context, R.layout.colorslistitem, colorNames);
        this.context = context;
        this.mInflater = context.getLayoutInflater();
        this.colorNamesArray = colorNames;
        this.defaultColors = defaultColors;
    }

    public int getItemType(int p1) {

        return (int) colorNamesArray[p1][0];
    }

    @Override
    public View getView(final int p1, View InflatedView, ViewGroup p3) {

        final LinearLayout root, previewLayout, Texts;
        final TextView Title, Preview, Desc, Line;
        final String[] loadColor;
        final String colorType;
        InflatedView = mInflater.inflate(R.layout.colorslistitem, null);

        root = (LinearLayout) InflatedView.findViewById(R.id.colorslistitemLinearLayout_Root);
        previewLayout = (LinearLayout) InflatedView.findViewById(R.id.colorslistitemLinearLayout_Preview);
        Preview = (TextView) InflatedView.findViewById(R.id.colorslistitemTextView_Preview);
        Texts = (LinearLayout) InflatedView.findViewById(R.id.colorslistitemLinearLayout_Texts);
        Title = (TextView) InflatedView.findViewById(R.id.colorslistitemTextView_Text);
        Desc = (TextView) InflatedView.findViewById(R.id.colorslistitemTextView_Desc);
        Line = (TextView) InflatedView.findViewById(R.id.colorslistitem_Line);

        int rowType = getItemType(p1);
        switch (rowType) {
            case TYPE_EMPTY:
                previewLayout.setVisibility(View.GONE);
                Texts.setVisibility(View.GONE);
                Line.setVisibility(View.GONE);
                InflatedView.setEnabled(false);
                InflatedView.setClickable(false);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                layoutParams.setMargins(0, (helper.isDeviceHorizontal(context) ? helper.getNavigationBarSize(context).x : helper.getNavigationBarSize(context).y), 0, 0);
                root.setLayoutParams(layoutParams);
                break;
            case TYPE_LOAD:
                previewLayout.setVisibility(View.GONE);
                Title.setText(R.string.preset_Load);
                Desc.setText(R.string.preset_LoadDesc);
                root.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View p1) {

                        MainActivity.changePrefPage(new PreferencesPresetsFragment(), false);
                    }
                });
                break;
            case TYPE_SAVE:
                previewLayout.setVisibility(View.GONE);
                Title.setText(R.string.preset_Save);
                Desc.setText(R.string.preset_SaveDesc);
                root.setOnClickListener(new OnClickListener() {

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
									fw.append("ColorizeNonStockIcons=" + MainActivity.preferences.getBoolean("ColorizeNonStockIcons",false) + "\n");
                                    fw.append("CircleRadius=" + MainActivity.preferences.getInt(PreferenceNames.pCircleRadius, 100) + "\n");
                                    fw.append("DialogCornersRadius=" + MainActivity.preferences.getInt(PreferenceNames.pRoundedDialogCornersRadius, 0) + "\n");
                                    if (selectedOptions.contains(context.getString(R.string.savePreset_Colors))) {
                                        fw.append("[COLORS]" + "\n");
                                        for (int i = 0; i < colorNamesArray.length; i++) {
                                            String[] loadColor = colorNamesArray[i][1].toString().split("_");
                                            if (loadColor.length > 1) {
                                                if (loadColor[1].contains("Reveal")) {
                                                    fw.append(loadColor[0] + "_Revealcolor=" + MainActivity.colorPrefs.getString(loadColor[0] + "_Revealcolor", "#ffffffff") + "\n");
                                                } else if (loadColor[1].contains("Background")) {
                                                    fw.append(loadColor[0] + "_Backgroundcolor=" + MainActivity.colorPrefs.getString(loadColor[0] + "_Backgroundcolor", "#ffffffff") + "\n");
                                                } else if (loadColor[1].contains("Text")) {
                                                    fw.append(loadColor[0] + "_Textcolor=" + MainActivity.colorPrefs.getString(loadColor[0] + "_Textcolor", "#ffffff") + "\n");
                                                } else if (loadColor[1].contains("Circle")) {
                                                    fw.append(loadColor[0] + "_Circlecolor=" + MainActivity.colorPrefs.getString(loadColor[0] + "_Circlecolor", "#ffffff") + "\n");
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
                        if (graphics.length > 0) options.add(context.getString(R.string.savePreset_Graphics));
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
                loadColor = colorNamesArray[p1][1].toString().split("_");

                Desc.setVisibility(View.GONE);
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
                try {
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
                    Title.setText(title);
                } catch (Throwable t) {
                    Title.setText("String Resource for " + loadColor[0] + " not found.");
                }
                String loadedColor = "#ff0000";
                try {
                    loadedColor = MainActivity.colorPrefs.getString(loadColor[0] + colorType, defaultColors[p1]);
                } catch (Throwable t) {
                    loadedColor = "#ff0000";
                    Preview.setText("Error");
                }
                currentColor = loadedColor;
                Preview.setBackgroundColor(Color.parseColor(currentColor));

                root.setOnClickListener(new OnClickListener() {

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

                                MainActivity.colorPrefs.edit().putString(loadColor[0] + colorType, resultBundle.getString(slideDownDialogFragment.RESULT_COLORPICKER)).apply();
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onTouchOutside() {

                            }
                        });
                        dialogFragment.setText("");
                        dialogFragment.setColorPicker(MainActivity.colorPrefs.getString(loadColor[0] + colorType, defaultColors[p1]), defaultColors[p1].length() != 7);
                        dialogFragment.setNegativeButton(context.getString(R.string.Dialog_Buttons).split("\\|")[4]);
                        dialogFragment.setPositiveButton(context.getString(R.string.Dialog_Buttons).split("\\|")[7]);
                        dialogFragment.showDialog(R.id.dialog_container);
                    }
                });
                root.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                        dialogFragment.setContext(context);
                        dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                        dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {
                            @Override
                            public void onListItemClick(int position, String text) {
                                if (position == 0) {
                                    for (int i = 0; i < PreferencesColorFragment.ColorNames.length; i++) {
                                            if (PreferencesColorFragment.ColorNames[i][1].toString().contains(colorType)) {
                                                MainActivity.colorPrefs.edit().putString(PreferencesColorFragment.ColorNames[i][1].toString(), currentColor).apply();
                                                notifyDataSetChanged();
                                            }
                                    }
                                } else if (position == 1) {
                                    MainActivity.colorPrefs.edit().putString(loadColor[0] + colorType, defaultColors[p1]).apply();
                                    Preview.setBackgroundColor(Color.parseColor(defaultColors[p1]));
                                    notifyDataSetChanged();
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
                        dialogFragment.setList(ListView.CHOICE_MODE_NONE, new String[]{context.getString(R.string.colorsApplyToAll), context.getString(R.string.colorsResetToDefault)}, -1, true);
                        dialogFragment.setPositiveButton(context.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                        dialogFragment.showDialog(R.id.dialog_container);
                        return true;
                    }
                });
                break;
            case TYPE_HEADER:
                InflatedView = mInflater.inflate(R.layout.listheader, null);
                TextView HeaderTitle = (TextView) InflatedView.findViewById(R.id.listheaderTextView_Title);
                TextView HeaderDesc = (TextView) InflatedView.findViewById(R.id.listheaderTextView_Desc);
                String mainString = "";
                if (colorNamesArray[p1][1].toString().equalsIgnoreCase("Presets") || colorNamesArray[p1][1].toString().equalsIgnoreCase("Reveal") || colorNamesArray[p1][1].toString().equalsIgnoreCase("Main")) {
                    try {
                        mainString = context.getResources().getString(context.getResources().getIdentifier("colorsPart" + colorNamesArray[p1][1], "string", MainActivity.class.getPackage().getName()));
                        String title = (colorNamesArray[p1][1].toString().equalsIgnoreCase("Presets") ? mainString : context.getString(R.string.colorsPartTitle).replace("[PART]", mainString));//getResources().getString(context.getResources().getIdentifier("colorsCategory_"+colorNamesArray[p1][1].toString(),"string",MainActivity.class.getPackage().getName()));
                        HeaderTitle.setText(title);
                    } catch (Throwable t1) {
                        HeaderTitle.setText("String Resource for colorsPart" + colorNamesArray[p1][1] + " not found.");
                    }
                } else {
                    try {
                        mainString = context.getResources().getString(context.getResources().getIdentifier("powerMenuMain_" + colorNamesArray[p1][1], "string", MainActivity.class.getPackage().getName()));
                        String title = context.getString(R.string.colorsPartTitle).replace("[PART]", mainString);//getResources().getString(context.getResources().getIdentifier("colorsCategory_"+colorNamesArray[p1][1].toString(),"string",MainActivity.class.getPackage().getName()));
                        HeaderTitle.setText(title);
                    } catch (Throwable t) {
                        try {
                            mainString = context.getResources().getString(context.getResources().getIdentifier("powerMenuBottom_" + colorNamesArray[p1][1], "string", MainActivity.class.getPackage().getName()));
                            String title = context.getString(R.string.colorsPartTitle).replace("[PART]", mainString);//getResources().getString(context.getResources().getIdentifier("colorsCategory_"+colorNamesArray[p1][1].toString(),"string",MainActivity.class.getPackage().getName()));
                            HeaderTitle.setText(title);
                        } catch (Throwable t1) {
                            HeaderTitle.setText("String Resource for " + colorNamesArray[p1][1] + " not found.");
                        }
                    }
                }
                try {
                    String Description = "";
                    mainString = context.getResources().getString(context.getResources().getIdentifier("powerMenuMain_" + colorNamesArray[p1][1], "string", MainActivity.class.getPackage().getName()));
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
                        mainString = context.getResources().getString(context.getResources().getIdentifier("powerMenuBottom_" + colorNamesArray[p1][1], "string", MainActivity.class.getPackage().getName()));
                        if (mainString.equalsIgnoreCase(context.getString(R.string.powerMenuMain_Shutdown)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuMain_Reboot)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuMain_SoftReboot)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuBottom_Recovery)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuBottom_Bootloader)) || mainString.equalsIgnoreCase(context.getString(R.string.powerMenuBottom_SafeMode))) {
                            Description = context.getString(R.string.colorsPartDescDialog).replace("[BUTTON]", mainString);
                        } else {
                            Description = context.getString(R.string.colorsPartDescButton).replace("[BUTTON]", mainString);
                        }
                        //String Description = context.getResources().getString(context.getResources().getIdentifier("colorsDesc_Dialog"+colorNamesArray[p1][1],"string",MainActivity.class.getPackage().getName()));
                        HeaderDesc.setText(Description);
                        HeaderDesc.setVisibility(View.VISIBLE);
                    } catch (Throwable t1) {
                        HeaderDesc.setVisibility(View.GONE);
                        HeaderDesc.setText("String Resource for " + colorNamesArray[p1][1] + " not found.");
                    }
                }
                InflatedView.setEnabled(false);
                break;
        }
        return InflatedView;
    }

}

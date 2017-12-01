package de.NeonSoft.neopowermenu.Preferences;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;

import de.NeonSoft.neopowermenu.MainActivity;
import de.NeonSoft.neopowermenu.R;
import de.NeonSoft.neopowermenu.addShortcut;
import de.NeonSoft.neopowermenu.helpers.GraphicItemHolder;
import de.NeonSoft.neopowermenu.helpers.PreferenceNames;
import de.NeonSoft.neopowermenu.helpers.helper;
import de.NeonSoft.neopowermenu.helpers.slideDownDialogFragment;

public class AddShortcutSettings extends Fragment {

    Activity mActivity;

    String selectedItem = null;
    String finalString = "";

    ImageView image;
    TextView title;
    private LinearLayout LinearLayout_UseGraphic;
    private Switch Switch_UseGraphic;

    private LinearLayout LinearLayout_UseCustomGraphic;
    private Switch Switch_UseCustomGraphic;

    private LinearLayout LinearLayout_Padding;
    private TextView TextView_Padding;
    private SeekBar SeekBar_Padding;

    private LinearLayout LinearLayout_CircleColor;
    private TextView TextView_CircleColorPreview;

    private LinearLayout LinearLayout_TextColor;
    private TextView TextView_TextColorPreview;

    public static int SELECT_PICTURE_RESULT = 1;

    public AddShortcutSettings() {

    }

    public void setItem(String item) {
        selectedItem = item;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mActivity = getActivity();

        View InflatedView = inflater.inflate(R.layout.activity_addshortcutsettings, container, false);

        image = (ImageView) InflatedView.findViewById(R.id.itemIcon);
        title = (TextView) InflatedView.findViewById(R.id.itemText);

        finalString = selectedItem;
        try {
            finalString = mActivity.getResources().getString(mActivity.getResources().getIdentifier("powerMenuMain_" + selectedItem, "string", MainActivity.class.getPackage().getName()));
        } catch (Throwable t) {
            try {
                finalString = mActivity.getResources().getString(mActivity.getResources().getIdentifier("powerMenuBottom_" + selectedItem, "string", MainActivity.class.getPackage().getName()));
            } catch (Throwable ignored) {
            }
        }
        image.setImageBitmap(addShortcut.createCircleIcon(selectedItem, finalString, addShortcut.color1, addShortcut.color2));
        title.setText(finalString);

        LinearLayout_UseGraphic = (LinearLayout) InflatedView.findViewById(R.id.addShortcut_UseGraphic);
        Switch_UseGraphic = (Switch) InflatedView.findViewById(R.id.addShortcutSwitch_UseGraphic);

        Switch_UseGraphic.setChecked(addShortcut.useGraphic);
        Switch_UseGraphic.setClickable(false);
        Switch_UseGraphic.setFocusable(false);

        LinearLayout_UseGraphic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                addShortcut.useGraphic = !addShortcut.useGraphic;
                Switch_UseGraphic.setChecked(addShortcut.useGraphic);
                image.setImageBitmap(addShortcut.createCircleIcon(selectedItem, finalString, addShortcut.color1, addShortcut.color2));
                LinearLayout_Padding.setEnabled(addShortcut.useGraphic);
                SeekBar_Padding.setEnabled(addShortcut.useGraphic);
                LinearLayout_Padding.setAlpha(addShortcut.useGraphic ? 1f : .3f);
                    LinearLayout_UseCustomGraphic.setEnabled(addShortcut.useGraphic);
                    LinearLayout_UseCustomGraphic.setAlpha(addShortcut.useGraphic ? 1f : .3f);
            }
        });

        LinearLayout_UseCustomGraphic = (LinearLayout) InflatedView.findViewById(R.id.addShortcut_UseCustomGraphic);
        Switch_UseCustomGraphic = (Switch) InflatedView.findViewById(R.id.addShortcutSwitch_UseCustomGraphic);

        Switch_UseCustomGraphic.setChecked(addShortcut.useCustomGraphic);
        Switch_UseCustomGraphic.setClickable(false);
        Switch_UseCustomGraphic.setFocusable(false);

        LinearLayout_UseCustomGraphic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View p1) {
                addShortcut.useCustomGraphic = !addShortcut.useCustomGraphic;
                Switch_UseCustomGraphic.setChecked(addShortcut.useCustomGraphic);
                image.setImageBitmap(addShortcut.createCircleIcon(selectedItem, finalString, addShortcut.color1, addShortcut.color2));
            }
        });

        LinearLayout_CircleColor = (LinearLayout) InflatedView.findViewById(R.id.addShortcut_CircleColorRoot);
        TextView_CircleColorPreview = (TextView) InflatedView.findViewById(R.id.addShortcut_CircleColorPreview);
        TextView_CircleColorPreview.setBackgroundColor(Color.parseColor(addShortcut.color1));

        LinearLayout_CircleColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                dialogFragment.setContext(mActivity);
                dialogFragment.setFragmentManager(addShortcut.mFragmentManager);
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
                        addShortcut.color1 = resultBundle.getString(slideDownDialogFragment.RESULT_COLORPICKER);
                        image.setImageBitmap(addShortcut.createCircleIcon(selectedItem, finalString, addShortcut.color1, addShortcut.color2));
                        TextView_CircleColorPreview.setBackgroundColor(Color.parseColor(addShortcut.color1));
                    }

                    @Override
                    public void onTouchOutside() {

                    }
                });
                dialogFragment.setText("");
                dialogFragment.setColorPicker(addShortcut.color1, true);
                dialogFragment.setNegativeButton(getString(R.string.Dialog_Buttons).split("\\|")[4]);
                dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[7]);
                dialogFragment.showDialog(R.id.dialog_container);
            }
        });

        LinearLayout_TextColor = (LinearLayout) InflatedView.findViewById(R.id.addShortcut_TextColorRoot);
        TextView_TextColorPreview = (TextView) InflatedView.findViewById(R.id.addShortcut_TextColorPreview);
        TextView_TextColorPreview.setBackgroundColor(Color.parseColor(addShortcut.color2));

        LinearLayout_TextColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                dialogFragment.setContext(mActivity);
                dialogFragment.setFragmentManager(addShortcut.mFragmentManager);
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
                        addShortcut.color2 = resultBundle.getString(slideDownDialogFragment.RESULT_COLORPICKER);
                        image.setImageBitmap(addShortcut.createCircleIcon(selectedItem, finalString, addShortcut.color1, addShortcut.color2));
                        TextView_TextColorPreview.setBackgroundColor(Color.parseColor(addShortcut.color2));
                    }

                    @Override
                    public void onTouchOutside() {

                    }
                });
                dialogFragment.setText("");
                dialogFragment.setColorPicker(addShortcut.color2, true);
                dialogFragment.setNegativeButton(getString(R.string.Dialog_Buttons).split("\\|")[4]);
                dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[7]);
                dialogFragment.showDialog(R.id.dialog_container);
            }
        });

        LinearLayout_Padding = (LinearLayout) InflatedView.findViewById(R.id.addShortcut_PaddingRoot);
        TextView_Padding = (TextView) InflatedView.findViewById(R.id.addShortcut_PaddingValue);
        SeekBar_Padding = (SeekBar) InflatedView.findViewById(R.id.addShortcut_Padding);
        SeekBar_Padding.setMax(50);
        SeekBar_Padding.setProgress((int) addShortcut.padding);
        TextView_Padding.setText((int) addShortcut.padding + "dp");

        SeekBar_Padding.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                addShortcut.padding = i;
                TextView_Padding.setText(i + "dp");
                image.setImageBitmap(addShortcut.createCircleIcon(selectedItem, finalString, addShortcut.color1, addShortcut.color2));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        addShortcut.mActionBar.setButton(getString(R.string.addShortcut_Add), R.drawable.ic_action_add, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(addShortcut.TAG, "Adding shortcut...");
                addShortcut.createShortcut(new addShortcut.CreateShortcutListener() {

                    @Override
                    public void onShortcutCreated(Intent intent) {
                        Log.i(addShortcut.TAG, "Shortcut added.");
                        if (intent != null) {
                            mActivity.setResult(Activity.RESULT_OK, intent);
                        } else {
                            mActivity.setResult(Activity.RESULT_OK);
                        }
                        mActivity.finish();
                    }

                    @Override
                    public void onShortcutFailed(String reason) {
                        Log.e(addShortcut.TAG, "Failed to create Shortcut: " + reason);
                        Toast.makeText(mActivity, "Failed to create Shortcut, more info in the logs under the tag '" + addShortcut.TAG + "'.", Toast.LENGTH_LONG).show();
                        mActivity.setResult(Activity.RESULT_CANCELED);
                        mActivity.finish();
                    }

                }, selectedItem);
            }
        });

        return InflatedView;
    }
}

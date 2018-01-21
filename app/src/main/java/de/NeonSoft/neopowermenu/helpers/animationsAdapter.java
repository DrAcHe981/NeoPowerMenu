package de.NeonSoft.neopowermenu.helpers;

import android.app.*;
import android.os.*;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import java.util.ArrayList;
import java.util.Arrays;

import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.*;
import de.NeonSoft.neopowermenu.Preferences.*;

public class animationsAdapter extends ArrayAdapter<Object> {

    public static final int TYPE_EMPTY = 0, TYPE_HEADER = 1, TYPE_ITEM = 2;

    Activity mContext;
    Object[][] items;
    int[] defaultTypes;
    int item = -1;
    //int[] types;
    //int[] speeds;

    public animationsAdapter(Activity mContext, Object[][] items, int[] defaultTypes) {//, int[] types, int[] speeds) {
        super(mContext, R.layout.animations_item, items);
        this.mContext = mContext;
        this.items = items;
        this.defaultTypes = defaultTypes;
        //this.types = types;
        //this.speeds = speeds;
    }

    public int getItemType(int p1) {

        return (int) items[p1][0];
    }

    @Override
    public View getView(final int p1, View p2, ViewGroup p3) {

        View InflatedView;
        InflatedView = mContext.getLayoutInflater().inflate(R.layout.animations_item, p3, false);
        if(items[p1][1].toString().contains("reveal")) {
            item = 0;
        } else if(items[p1][1].toString().contains("dialog")) {
            item = 1;
        } else if(items[p1][1].toString().contains("icons")) {
            item = 2;
        } else if(items[p1][1].toString().contains("singleline")) {
            item = 3;
        } else if(items[p1][1].toString().contains("multiline")) {
            item = 4;
        } else if(items[p1][1].toString().contains("progressbar")) {
            item = 5;
        }
        int rowType = getItemType(p1);
        switch (rowType) {
            case TYPE_ITEM:
                LinearLayout Root = (LinearLayout) InflatedView.findViewById(R.id.animationsitemLinearLayout_Root);

                TextView Text = (TextView) InflatedView.findViewById(R.id.animationsitemTextView_Title);
                final TextView Desc = (TextView) InflatedView.findViewById(R.id.animationsitemTextView_Desc);
                final ArrayList<String> animations = new ArrayList<>(Arrays.asList( mContext.getString((items[p1-1][1].toString().contains("progressbar") ? R.string.animations_Types_Progressbar : R.string.animations_Types)).split("\\|")));
                if(items[p1][1].toString().contains("type")) {
                    Text.setText(mContext.getString(R.string.animations_Type));
                    Desc.setText(animations.get(MainActivity.animationPrefs.getInt(items[p1][1].toString(), defaultTypes[p1])));

                    Root.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                            dialogFragment.setContext(mContext);
                            dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                            dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

                                @Override
                                public void onListItemClick(int position, String text) {
                                    if (!items[p1-1][1].toString().contains("reveal") && !items[p1-1][1].toString().contains("progressbar") && position > 0) {
                                        position++;
                                    }
                                    MainActivity.animationPrefs.edit().putInt(items[p1][1].toString(), position).apply();
                                    notifyDataSetChanged();
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
                            int selItem = MainActivity.animationPrefs.getInt(items[p1][1].toString(), defaultTypes[p1]);
                            if (!items[p1-1][1].toString().contains("reveal") && !items[p1-1][1].toString().contains("progressbar")) {
                                animations.remove(1);
                                selItem = (selItem > 0 ? selItem-1 : selItem);
                            }
                            dialogFragment.setList(ListView.CHOICE_MODE_SINGLE, animations, selItem, true);
                            dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                            dialogFragment.showDialog(R.id.dialog_container);
                        }
                    });
                } else if(items[p1][1].toString().contains("interpolator")) {
                    final ArrayList<String> interpolators = new ArrayList<>(Arrays.asList(mContext.getString(R.string.animations_Interpolators).split("\\|")));
                    Text.setText(mContext.getString(R.string.animations_Interpolator));
                    Desc.setText(mContext.getString(R.string.animations_Interpolators).split("\\|")[MainActivity.animationPrefs.getInt(items[p1][1].toString(), PreferencesAnimationsFragment.defaultTypes[p1])]);
                    if (MainActivity.animationPrefs.getInt(items[p1-1][1].toString(), defaultTypes[p1-1]) < animations.size() - 1) {
                        Root.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                                dialogFragment.setContext(mContext);
                                dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                                dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

                                    @Override
                                    public void onListItemClick(int position, String text) {

                                        MainActivity.animationPrefs.edit().putInt(items[p1][1].toString(), position).apply();
                                        notifyDataSetChanged();
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
                                dialogFragment.setList(ListView.CHOICE_MODE_SINGLE, interpolators, MainActivity.animationPrefs.getInt(items[p1][1].toString(), PreferencesAnimationsFragment.defaultTypes[p1]), true);
                                dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                                dialogFragment.showDialog(R.id.dialog_container);
                            }
                        });
                    } else {
                        Root.setAlpha((float) .3);
                        Root.setEnabled(false);
                    }
                } else if(items[p1][1].toString().contains("speed")) {
                    final ArrayList<String> speeds = new ArrayList<>(Arrays.asList(mContext.getString(R.string.animations_Speeds).split("\\|")));
                    int speed = 100 + (items[p1-1][1].toString().contains("progressbar") ? 400 : 0);
                    for (int i = 0; i < speeds.size(); i++) {
                        speeds.set(i, speeds.get(i) + (i == speeds.size()-1 ? (MainActivity.animationPrefs.getInt(items[p1][1].toString(), defaultTypes[p1]) > speeds.size() ? " (" + MainActivity.animationPrefs.getInt(items[p1][1].toString(), defaultTypes[p1]) + "ms)" : "") : " (" + speed + "ms)"));
                        speed += 200;
                    }
                    Text.setText(mContext.getString(R.string.animations_Speed));
                    Desc.setText(speeds.get(Math.min(MainActivity.animationPrefs.getInt(items[p1][1].toString(), PreferencesAnimationsFragment.defaultTypes[p1]), speeds.size()-1)));
                    if (MainActivity.animationPrefs.getInt(items[p1-2][1].toString(), defaultTypes[p1-2]) < animations.size() - 1) {
                        Root.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                                dialogFragment.setContext(mContext);
                                dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                                dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

                                    @Override
                                    public void onListItemClick(int position, String text) {
                                        if (position == speeds.size()-1) {
                                            final slideDownDialogFragment customSpeed = new slideDownDialogFragment();
                                            customSpeed.setContext(mContext);
                                            customSpeed.setFragmentManager(MainActivity.fragmentManager);
                                            customSpeed.setListener(new slideDownDialogFragment.slideDownDialogInterface() {
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
                                                    MainActivity.animationPrefs.edit().putInt(items[p1][1].toString(), Integer.parseInt(resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0"))).apply();
                                                    notifyDataSetChanged();
                                                }

                                                @Override
                                                public void onTouchOutside() {

                                                }
                                            });
                                            customSpeed.addInput(mContext.getString(R.string.animations_CustomSpeed), MainActivity.animationPrefs.getInt(items[p1][1].toString(), defaultTypes[p1]) == speeds.size()-1 ? "" + MainActivity.animationPrefs.getInt(items[p1][1].toString(), defaultTypes[p1 - 2]) : "700", false, new TextWatcher() {
                                                @Override
                                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                                }

                                                @Override
                                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                    if (s.toString().isEmpty() || Integer.parseInt(s.toString()) < 100) {
                                                        customSpeed.setInputAssistInfo(mContext.getString(R.string.animations_CustomSpeedTooSmall).replace("[VALUE]", "100"));
                                                        customSpeed.showAssistInfo(true);
                                                    } else {
                                                        customSpeed.showAssistInfo(false);
                                                    }
                                                }

                                                @Override
                                                public void afterTextChanged(Editable s) {

                                                }
                                            });
                                            customSpeed.setInputMode(0, InputType.TYPE_CLASS_NUMBER);
                                            customSpeed.setNeutralButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                                            customSpeed.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_SAVE]);
                                            customSpeed.showDialog(R.id.dialog_container);
                                        } else {
                                            MainActivity.animationPrefs.edit().putInt(items[p1][1].toString(), position).apply();
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
                                dialogFragment.setList(ListView.CHOICE_MODE_SINGLE, speeds, MainActivity.animationPrefs.getInt(items[p1][1].toString(), PreferencesAnimationsFragment.defaultTypes[p1]), true);
                                dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                                dialogFragment.showDialog(R.id.dialog_container);
                            }
                        });
                    } else {
                        Root.setAlpha((float) .3);
                        Root.setEnabled(false);
                    }
                }
                break;
            case TYPE_HEADER:
                InflatedView = mContext.getLayoutInflater().inflate(R.layout.listheader, null);
                TextView HeaderTitle = (TextView) InflatedView.findViewById(R.id.listheaderTextView_Title);
                TextView HeaderDesc = (TextView) InflatedView.findViewById(R.id.listheaderTextView_Desc);
                HeaderTitle.setText(mContext.getString(R.string.animations_Items).split("\\|")[item]);

                InflatedView.setEnabled(false);
        }
        return InflatedView;
    }

}

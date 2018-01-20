package de.NeonSoft.neopowermenu.helpers;

import android.app.*;
import android.os.*;
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
        }
        int rowType = getItemType(p1);
        switch (rowType) {
            case TYPE_ITEM:
                LinearLayout Root = (LinearLayout) InflatedView.findViewById(R.id.animationsitemLinearLayout_Root);

                TextView Text = (TextView) InflatedView.findViewById(R.id.animationsitemTextView_Title);
                final TextView Desc = (TextView) InflatedView.findViewById(R.id.animationsitemTextView_Desc);
                if(items[p1][1].toString().contains("type")) {
                    Text.setText(mContext.getString(R.string.animations_Type));
                    Desc.setText(mContext.getString(R.string.animations_Types).split("\\|")[MainActivity.animationPrefs.getInt(items[p1][1].toString(), defaultTypes[p1])]);

                    Root.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                            dialogFragment.setContext(mContext);
                            dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                            dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

                                @Override
                                public void onListItemClick(int position, String text) {
                                    if (!items[p1-1][1].toString().contains("reveal") && position > 0) {
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
                            ArrayList<String> animations = new ArrayList<>(Arrays.asList(mContext.getString(R.string.animations_Types).split("\\|")));
                            int selItem = MainActivity.animationPrefs.getInt(items[p1][1].toString(), defaultTypes[p1]);
                            if (!items[p1-1][1].toString().contains("reveal")) {
                                animations.remove(1);
                                selItem = (selItem > 0 ? selItem-1 : selItem);
                            }
                            dialogFragment.setList(ListView.CHOICE_MODE_SINGLE, animations, selItem, true);
                            dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                            dialogFragment.showDialog(R.id.dialog_container);
                        }
                    });
                } else if(items[p1][1].toString().contains("speed")) {
                    Text.setText(mContext.getString(R.string.animations_Speed));
                    Desc.setText(mContext.getString(R.string.animations_Speeds).split("\\|")[MainActivity.animationPrefs.getInt(items[p1][1].toString(), 3)]);
                    if (MainActivity.animationPrefs.getInt(items[p1-1][1].toString(), defaultTypes[p1-1]) < mContext.getString(R.string.animations_Types).split("\\|").length - 1) {
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
                                dialogFragment.setList(ListView.CHOICE_MODE_SINGLE, mContext.getString(R.string.animations_Speeds).split("\\|"), MainActivity.animationPrefs.getInt(items[p1][1].toString(), 3), true);
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

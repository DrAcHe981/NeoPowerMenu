package de.NeonSoft.neopowermenu.helpers;

import android.app.*;
import android.content.pm.*;
import android.os.*;
import android.support.annotation.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.Preferences.*;

import java.util.*;

public class visibilityOrder_ListAdapter extends ArrayAdapter<MenuItemHolder> {

    private Activity mContext;
    private LayoutInflater inflater;
    private ArrayList<MenuItemHolder> items;
    PackageManager pm;

    public static int TYPE_NORMAL = 0;
    public static int TYPE_MULTI = 1;
    public static int TYPE_MULTIPAGE_START = 2;
    public static int TYPE_MULTIPAGE_END = 3;

    public visibilityOrder_ListAdapter(Activity context,
                                       ArrayList<MenuItemHolder> items) {
        super(context, R.layout.visibilityorder_normal, items);
        this.mContext = context;
        this.inflater = context.getLayoutInflater();
        this.items = items;
        pm = mContext.getPackageManager();
    }

    @NonNull
    @Override
    public View getView(final int position, View p2, @NonNull ViewGroup p3) {

        View InflatedView = p2;

        if (items.get(position).getType() == TYPE_NORMAL) {
            boolean hasDescription = false;
            InflatedView = inflater.inflate(R.layout.visibilityorder_normal, p3, false);

            LinearLayout MenuItemHolder = (LinearLayout) InflatedView.findViewById(R.id.visibilityordernormalLinearLayout_item);
            TextView item = (TextView) InflatedView.findViewById(R.id.visibilityordernormal_item);
            String string = items.get(position).getTitle(1);
            if (items.get(position).getText(1).isEmpty()) {
                if (string.contains(".")) {
                    try {
                        string = pm.getApplicationInfo(string.split("/")[0], 0).loadLabel(pm).toString();
                    } catch (PackageManager.NameNotFoundException ignored) {
                    }
                } else {
                    try {
                        string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + items.get(position).getTitle(1), "string", MainActivity.class.getPackage().getName()));
                    } catch (Throwable t) {
                        try {
                            string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_" + items.get(position).getTitle(1), "string", MainActivity.class.getPackage().getName()));
                        } catch (Throwable ignored) {
                        }
                    }
                }
            } else {
                string = items.get(position).getText(1);
            }
            item.setText(string);

            ImageView HideDescription = (ImageView) InflatedView.findViewById(R.id.visibilityordernormalImageView_HideDescription);
            ImageView HideOnLockscreen = (ImageView) InflatedView.findViewById(R.id.visibilityordernormalImageView_HideOnLockscreen);
            ImageView LockedWithPassword = (ImageView) InflatedView.findViewById(R.id.visibilityordernormalImageView_LockedWithPassword);

            try {
                if (!mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + this.items.get(position).getTitle(1) + "Desc", "string", MainActivity.class.getPackage().getName())).equalsIgnoreCase("")) {
                    HideDescription.setVisibility(items.get(position).getHideDesc() ? View.GONE : View.VISIBLE);
                    hasDescription = true;
                }
            } catch (Throwable ignored) {
            }
            HideOnLockscreen.setVisibility(items.get(position).getHideOnLockScreen() ? View.GONE : View.VISIBLE);
            LockedWithPassword.setVisibility(items.get(position).getLockedWithPassword() ? View.VISIBLE : View.GONE);

            item.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View p1) {
                    PreferencesVisibilityOrderFragment.showAddSingleItemDialog(PreferencesVisibilityOrderFragment.AddItemMode_REPLACE, 1, position);
                }
            });

            item.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showEditTitleFor(position, 1);
                    return true;
                }
            });

            LinearLayout EditAppearanceBehaviour = (LinearLayout) InflatedView.findViewById(R.id.visibilityordernormalLinearLayout_EditBehaviour);
            EditAppearanceBehaviour.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditAppearanceBehaviourFor(position);
                }
            });

        } else if (items.get(position).getType() == TYPE_MULTI) {
            InflatedView = inflater.inflate(R.layout.visibilityorder_multi, p3, false);

            LinearLayout itemHolder = (LinearLayout) InflatedView.findViewById(R.id.visibilityordermulti_items);

            for (int i = 1; i <= items.get(position).getTitles().size(); i++) {
                final View view = (View) inflater.inflate(R.layout.visibilityorder_multiitem, null, false);

                final TextView item = (TextView) view.findViewById(R.id.visibilityordermulti_item);
                String string = "";
                if (!items.get(position).getText(i).isEmpty()) {
                    string = items.get(position).getText(i);
                }
                if (string.isEmpty()) {
                    string = items.get(position).getTitle(i);
                    if (string.contains(".")) {
                        try {
                            string = pm.getApplicationInfo(string.split("/")[0], 0).loadLabel(pm).toString();
                        } catch (PackageManager.NameNotFoundException ignored) {
                        }
                    } else {
                        try {
                            string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + items.get(position).getTitle(i), "string", MainActivity.class.getPackage().getName()));
                        } catch (Throwable t) {
                            try {
                                string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_" + items.get(position).getTitle(i), "string", MainActivity.class.getPackage().getName()));
                            } catch (Throwable ignored) {
                            }
                        }
                    }
                }
                item.setText(string);

                final int finalI = i;
                item.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View p1) {
                        PreferencesVisibilityOrderFragment.showAddSingleItemDialog(PreferencesVisibilityOrderFragment.AddItemMode_REPLACE, finalI, position);
                    }
                });

                item.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                        dialogFragment.setContext(mContext);
                        dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                        dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {
                                                       @Override
                                                       public void onListItemClick(int pos, String text) {
                                                           if (pos==0) {
                                                               showEditTitleFor(position, finalI);
                                                           } else if (pos==1) {
                                                                items.get(position).getTitles().remove(finalI-1);
                                                                items.get(position).getTexts().remove(finalI-1);
                                                                items.get(position).getShortcutUtis().remove(finalI-1);
                                                                if (items.get(position).getTitles().size() == 0) {
                                                                    items.remove(position);
                                                                }
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
                                                   }

                        );
                        dialogFragment.setList(ListView.CHOICE_MODE_NONE, mContext.getString(R.string.visibilityOrder_EditMultiItemChoices).split("\\|"), -1, true);
                        dialogFragment.setNegativeButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                        dialogFragment.showDialog(R.id.dialog_container);
                        return true;
                    }
                });

                itemHolder.addView(view);

            }

            ImageView HideOnLockscreen = (ImageView) InflatedView.findViewById(R.id.visibilityordermultiImageView_HideOnLockscreen);
            ImageView LockedWithPassword = (ImageView) InflatedView.findViewById(R.id.visibilityordermultiImageView_LockedWithPassword);
            ImageView Orientation = (ImageView) InflatedView.findViewById(R.id.visibilityordermultiImageView_Orientation);
            HideOnLockscreen.setVisibility(items.get(position).getHideOnLockScreen() ? View.GONE : View.VISIBLE);
            LockedWithPassword.setVisibility(items.get(position).getLockedWithPassword() ? View.VISIBLE : View.GONE);
            Orientation.setImageResource(items.get(position).getHorizontal() ? R.drawable.ic_swap_horiz : R.drawable.ic_swap_vert);

            LinearLayout EditAppearanceBehaviour = (LinearLayout) InflatedView.findViewById(R.id.visibilityordermultiLinearLayout_EditBehaviour);
            EditAppearanceBehaviour.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditAppearanceBehaviourFor(position);
                }
            });

            LinearLayout AddItem = (LinearLayout) InflatedView.findViewById(R.id.visibilityordermultiLinearLayout_Add);
            AddItem.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    PreferencesVisibilityOrderFragment.showAddSingleItemDialog(PreferencesVisibilityOrderFragment.AddItemMode_AddToMulti, -1, position);
                }
            });

        } else if (items.get(position).getType() == TYPE_MULTIPAGE_START) {
            InflatedView = inflater.inflate(R.layout.visibilityorder_normal, p3, false);

            TextView item = (TextView) InflatedView.findViewById(R.id.visibilityordernormal_item);
            item.setClickable(false);
            item.setText(mContext.getString(R.string.visibilityOrder_MultiPage).split("\\|")[0]);

            ImageView HideOnLockscreen = (ImageView) InflatedView.findViewById(R.id.visibilityordernormalImageView_HideOnLockscreen);
            ImageView LockedWithPassword = (ImageView) InflatedView.findViewById(R.id.visibilityordernormalImageView_LockedWithPassword);
            HideOnLockscreen.setVisibility(items.get(position).getHideOnLockScreen() ? View.GONE : View.VISIBLE);
            LockedWithPassword.setVisibility(items.get(position).getLockedWithPassword() ? View.VISIBLE : View.GONE);

            LinearLayout EditAppearanceBehaviour = (LinearLayout) InflatedView.findViewById(R.id.visibilityordernormalLinearLayout_EditBehaviour);
            EditAppearanceBehaviour.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditAppearanceBehaviourFor(position);
                }
            });
            item.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showEditTitleFor(position, 1);
                    return true;
                }
            });
        } else if (items.get(position).getType() == TYPE_MULTIPAGE_END) {
            InflatedView = inflater.inflate(R.layout.visibilityorder_normal, p3, false);

            TextView item = (TextView) InflatedView.findViewById(R.id.visibilityordernormal_item);
            LinearLayout dragHandle = (LinearLayout) InflatedView.findViewById(R.id.drag_handle);
            //dragHandle.setVisibility(View.GONE);
            item.setClickable(false);
            item.setText(mContext.getString(R.string.visibilityOrder_MultiPage).split("\\|")[1]);

            LinearLayout BottomBar = (LinearLayout) InflatedView.findViewById(R.id.visibilityordernormal_BottomBar);
            BottomBar.setVisibility(View.GONE);
        }
        InflatedView.setClickable(false);
        InflatedView.setEnabled(false);

        return InflatedView;
    }

    public void addItem(MenuItemHolder item) {
        items.add(item);
        notifyDataSetChanged();
        PreferencesVisibilityOrderFragment.DSLV_List.smoothScrollToPosition(PreferencesVisibilityOrderFragment.DSLV_List.getCount());
    }

    public MenuItemHolder getItemAt(int position) {
        return items.get(position);
    }

    public void insertAt(int position, MenuItemHolder item) {
        items.add(position, item);
        notifyDataSetChanged();
    }

    public void move(int from, int to) {
        if (from != to) {
            MenuItemHolder item = getItemAt(from);
            if (item.getType() == TYPE_MULTIPAGE_START || item.getType() == TYPE_MULTIPAGE_END) {
                boolean validMove = true;
                items.remove(from);
                items.add(to, item);
                ArrayList<String> pages = new ArrayList<>();
                for (int i = 0; i < items.size(); i++) {
                    try {
                        MenuItemHolder checkItem = getItemAt(i);
                        if (MainActivity.DeepLogging)
                            Log.i("NPM", "(" + String.format("%02d", i) + ")> " + checkItem.getType() + " | " + checkItem.getTitle(1));
                        if (checkItem.getType() == TYPE_MULTIPAGE_START) {
                            pages.add(checkItem.getTitle(1));
                        } else if (checkItem.getType() == TYPE_MULTIPAGE_END) {
                            pages.remove(pages.size() - 1);
                        }
                    } catch (Throwable t) {
                        Log.e("NPM", "Invalid move operation:", t);
                        validMove = false;
                        break;
                    }
                }
                if (!validMove) {
                    Toast.makeText(mContext, mContext.getString(R.string.visibilityOrder_InvalidMultiPageMove), Toast.LENGTH_LONG).show();
                    items.remove(to);
                    items.add(from, item);
                }
                notifyDataSetChanged();
            } else {
                //Toast.makeText(mContext, "normal move from "+from+" to "+to, Toast.LENGTH_SHORT).show();
                items.remove(from);
                items.add(to, item);
                notifyDataSetChanged();
            }
        }

    }

    public void removeAt(int position) {
        if (items.get(position).getType() == TYPE_MULTIPAGE_START) {
            MenuItemHolder item = getItemAt(position);
            int removingLayers = 0;
            while (items.size() > position) {
                MenuItemHolder checkItem = getItemAt(position);
                if (checkItem.getType() == TYPE_MULTIPAGE_END) {
                    if (removingLayers > 0) {
                        items.remove(position);
                        removingLayers--;
                        //Toast.makeText(mContext,"- Setting layer count to "+removingLayers,Toast.LENGTH_SHORT).show();
                        if (removingLayers == 0) {
                            break;
                        }
                    }
                } else {
                    if (checkItem.getType() == TYPE_MULTIPAGE_START) {
                        removingLayers++;
                        //Toast.makeText(mContext,"+ Setting layer count to "+removingLayers,Toast.LENGTH_SHORT).show();
                    }
                    items.remove(position);
                }
            }
        } else if (items.get(position).getType() == TYPE_MULTIPAGE_END) {
        } else if (items.get(position).getType() == TYPE_NORMAL || (int) items.get(position).getType() == TYPE_MULTI) {
            items.remove(position);
        }
        notifyDataSetChanged();
        //outputSorting();
    }


    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    private void showEditTitleFor(final int position, final int itemSpace) {
        final MenuItemHolder thisItem = items.get(position);

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
                                           thisItem.getTexts().set(itemSpace-1, "");
                                           items.set(position, thisItem);

                                           notifyDataSetChanged();
                                       }

                                       @Override
                                       public void onPositiveClick(Bundle resultBundle) {
                                           String inputResult = (String) resultBundle.get(slideDownDialogFragment.RESULT_INPUT + "0");

                                           thisItem.getTexts().set(itemSpace-1, inputResult);
                                           items.set(position, thisItem);

                                           notifyDataSetChanged();

                                       }

                                       @Override
                                       public void onTouchOutside() {

                                       }
                                   }

        );
        dialogFragment.addInput(mContext.getString(R.string.visibilityOrder_EditAppearanceBehaviourText), thisItem.getText(itemSpace), true, null);
        dialogFragment.setNegativeButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
        dialogFragment.setNeutralButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_DELETE]);
        dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_SAVE]);
        dialogFragment.showDialog(R.id.dialog_container);
    }

    private void showEditAppearanceBehaviourFor(final int position) {

        final MenuItemHolder thisItem = items.get(position);
        boolean hasDescription = false;
        try {
            if (!mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + thisItem.getTitle(1) + "Desc", "string", MainActivity.class.getPackage().getName())).equalsIgnoreCase("")) {
                hasDescription = true;
            }
        } catch (Throwable t) {
        }
        final ArrayList<String> options = new ArrayList<String>();
        ArrayList<Boolean> checked = new ArrayList<Boolean>();
        if (hasDescription) {
            options.add(mContext.getString(R.string.visibilityOrder_HideDesc));
            checked.add(thisItem.getHideDesc());
        }
        if (thisItem.getType() == TYPE_MULTI) {
            options.add(mContext.getString(R.string.visibilityOrder_FillEmpty));
            checked.add(thisItem.getFillEmpty());
            options.add(mContext.getString(R.string.visibilityOrder_Horizontal));
            checked.add(thisItem.getHorizontal());
        }
        options.add(mContext.getString(R.string.visibilityOrder_HideOnLockscreen));
        checked.add(thisItem.getHideOnLockScreen());
        if (thisItem.getType() != TYPE_MULTIPAGE_START) {
            if (thisItem.getType() == TYPE_MULTI) {
                options.add(mContext.getString(R.string.visibilityOrder_HideText));
                checked.add(thisItem.getHideText());
            }
            options.add(mContext.getString(R.string.visibilityOrder_LockWithPassword));
            checked.add(thisItem.getLockedWithPassword());
        }
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
                                           String listResult = resultBundle.getString(slideDownDialogFragment.RESULT_LIST, "");

                                           final MenuItemHolder item = items.get(position);
                                           final boolean oldLockedWithPassword = item.getLockedWithPassword();
                                           if (item.getType() == TYPE_NORMAL) {
                                               item.setHideDesc(false);
                                               item.setHideOnLockScreen(false);
                                           } else if (item.getType() == TYPE_MULTI) {
                                               item.setFillEmpty(false);
                                               item.setHideOnLockScreen(false);
                                               item.setHorizontal(false);
                                           }
                                           item.setLockedWithPassword(false);
                                           item.setHideText(false);
                                           if (thisItem.getType() == TYPE_NORMAL || thisItem.getType() == TYPE_MULTIPAGE_START) {

                                               if (!listResult.isEmpty()) {
                                                   for (final String result : listResult.split(",")) {
                                                       if (result.equalsIgnoreCase(mContext.getString(R.string.visibilityOrder_HideDesc))) {
                                                           item.setHideDesc(true);
                                                       } else if (result.equalsIgnoreCase(mContext.getString(R.string.visibilityOrder_HideOnLockscreen))) {
                                                           item.setHideOnLockScreen(true);
                                                       } else if (result.equalsIgnoreCase(mContext.getString(R.string.visibilityOrder_HideText))) {
                                                           item.setHideText(true);
                                                       } else if (result.equalsIgnoreCase(mContext.getString(R.string.visibilityOrder_LockWithPassword))) {
                                                           if (MainActivity.preferences.getString(PreferenceNames.pItemPWL, "").isEmpty()) {
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
                                                                       MainActivity.preferences.edit().putString(PreferenceNames.pItemPWL, helper.md5Crypto(resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0", ""))).commit();
                                                                       item.setLockedWithPassword(true);

                                                                       items.set(position, item);

                                                                       notifyDataSetChanged();
                                                                   }

                                                                   @Override
                                                                   public void onTouchOutside() {

                                                                   }
                                                               });
                                                               dialogFragment.setText(mContext.getString(R.string.visibilityOrder_NoPasswordSet));
                                                               dialogFragment.addInput(mContext.getString(R.string.advancedPrefs_Password), "", false, null);
                                                               dialogFragment.setInputMode(0, InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                                               dialogFragment.setNegativeButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                                                               dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_SAVE]);
                                                               dialogFragment.showDialog(R.id.dialog_container);
                                                           } else {
                                                               item.setLockedWithPassword(true);
                                                           }
                                                       }
                                                   }
                                               }
                                               if (!MainActivity.preferences.getString(PreferenceNames.pItemPWL, "").isEmpty() && !item.getLockedWithPassword() && oldLockedWithPassword) {
                                                   final slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                                                   dialogFragment.setContext(mContext);
                                                   dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                                                   dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {
                                                       @Override
                                                       public void onListItemClick(int position, String text) {

                                                       }

                                                       @Override
                                                       public void onNegativeClick() {
                                                           item.setLockedWithPassword(oldLockedWithPassword);
                                                           dialogFragment.closeDialog();

                                                           items.set(position, item);

                                                           notifyDataSetChanged();
                                                       }

                                                       @Override
                                                       public void onNeutralClick() {
                                                       }

                                                       @Override
                                                       public void onPositiveClick(Bundle resultBundle) {
                                                           if (helper.md5Crypto(resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0")).equals(MainActivity.preferences.getString(PreferenceNames.pItemPWL, ""))) {
                                                               dialogFragment.closeDialog();

                                                               items.set(position, item);

                                                               notifyDataSetChanged();
                                                           } else {
                                                               Toast.makeText(mContext, mContext.getString(R.string.powerMenu_WrongPassword), Toast.LENGTH_LONG).show();
                                                           }
                                                       }

                                                       @Override
                                                       public void onTouchOutside() {
                                                           item.setLockedWithPassword(oldLockedWithPassword);
                                                       }
                                                   });
                                                   dialogFragment.setText(mContext.getString(R.string.visibilityOrder_RemovePWLock));
                                                   dialogFragment.addInput(mContext.getString(R.string.advancedPrefs_Password), "", true, null);
                                                   dialogFragment.setInputMode(0, InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                                   dialogFragment.setNegativeButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                                                   dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_OK]);
                                                   dialogFragment.setCloseOnButtonClick(false);
                                                   dialogFragment.showDialog(R.id.dialog_container);
                                               }
                                           } else if (thisItem.getType() == TYPE_MULTI) {
                                               if (!listResult.isEmpty()) {
                                                   for (String result : listResult.split(",")) {
                                                       if (result.equalsIgnoreCase(mContext.getString(R.string.visibilityOrder_FillEmpty))) {
                                                           item.setFillEmpty(true);
                                                       } else if (result.equalsIgnoreCase(mContext.getString(R.string.visibilityOrder_Horizontal))) {
                                                           item.setHorizontal(true);
                                                       } else if (result.equalsIgnoreCase(mContext.getString(R.string.visibilityOrder_HideOnLockscreen))) {
                                                           item.setHideOnLockScreen(true);
                                                       } else if (result.equalsIgnoreCase(mContext.getString(R.string.visibilityOrder_HideText))) {
                                                           item.setHideText(true);
                                                       } else if (result.equalsIgnoreCase(mContext.getString(R.string.visibilityOrder_LockWithPassword))) {
                                                           if (MainActivity.preferences.getString(PreferenceNames.pItemPWL, "").isEmpty()) {
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
                                                                       MainActivity.preferences.edit().putString(PreferenceNames.pItemPWL, helper.md5Crypto(resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0", ""))).commit();
                                                                       item.setLockedWithPassword(true);

                                                                       items.set(position, item);

                                                                       notifyDataSetChanged();
                                                                   }

                                                                   @Override
                                                                   public void onTouchOutside() {

                                                                   }
                                                               });
                                                               dialogFragment.setText(mContext.getString(R.string.visibilityOrder_NoPasswordSet));
                                                               dialogFragment.addInput(mContext.getString(R.string.advancedPrefs_Password), "", false, null);
                                                               dialogFragment.setInputMode(0, InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                                               dialogFragment.setNegativeButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                                                               dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_SAVE]);
                                                               dialogFragment.showDialog(R.id.dialog_container);
                                                           } else {
                                                               item.setLockedWithPassword(true);
                                                           }
                                                       }
                                                   }
                                               }
                                               if (!MainActivity.preferences.getString(PreferenceNames.pItemPWL, "").isEmpty() && !item.getLockedWithPassword() && oldLockedWithPassword) {
                                                   final slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                                                   dialogFragment.setContext(mContext);
                                                   dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                                                   dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {
                                                       @Override
                                                       public void onListItemClick(int position, String text) {

                                                       }

                                                       @Override
                                                       public void onNegativeClick() {
                                                           item.setLockedWithPassword(oldLockedWithPassword);
                                                           dialogFragment.closeDialog();

                                                           items.set(position, item);

                                                           notifyDataSetChanged();
                                                       }

                                                       @Override
                                                       public void onNeutralClick() {
                                                       }

                                                       @Override
                                                       public void onPositiveClick(Bundle resultBundle) {
                                                           if (helper.md5Crypto(resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0")).equals(MainActivity.preferences.getString(PreferenceNames.pItemPWL, ""))) {
                                                               dialogFragment.closeDialog();

                                                               items.set(position, item);

                                                               notifyDataSetChanged();
                                                           } else {
                                                               Toast.makeText(mContext, mContext.getString(R.string.powerMenu_WrongPassword), Toast.LENGTH_LONG).show();
                                                           }
                                                       }

                                                       @Override
                                                       public void onTouchOutside() {
                                                           item.setLockedWithPassword(oldLockedWithPassword);
                                                       }
                                                   });
                                                   dialogFragment.setText(mContext.getString(R.string.visibilityOrder_RemovePWLock));
                                                   dialogFragment.addInput(mContext.getString(R.string.advancedPrefs_Password), "", true, null);
                                                   dialogFragment.setInputMode(0, InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                                   dialogFragment.setNegativeButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                                                   dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_OK]);
                                                   dialogFragment.setCloseOnButtonClick(false);
                                                   dialogFragment.showDialog(R.id.dialog_container);
                                               }
                                           }

                                           items.set(position, item);

                                           notifyDataSetChanged();

                                       }

                                       @Override
                                       public void onTouchOutside() {

                                       }
                                   }

        );
        dialogFragment.setText(mContext.getString(R.string.visibilityOrder_EditAppearanceBehaviourDesc));
        dialogFragment.setList(ListView.CHOICE_MODE_MULTIPLE, options, -1, false);
        dialogFragment.setListReturnMode(slideDownDialogFragment.LIST_RETURN_MODE_TEXT);
        dialogFragment.setListChecks(checked);
        dialogFragment.setListAllowEmpty(true);
        dialogFragment.setNegativeButton(mContext.getString(R.string.Dialog_Buttons).

                split("\\|")[slideDownDialogFragment

                .BUTTON_CANCEL]);
        dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).

                split("\\|")[slideDownDialogFragment

                .BUTTON_SAVE]);
        dialogFragment.showDialog(R.id.dialog_container);
    }

    public void outputSorting() {
        ArrayList<String> MultiPage = new ArrayList<String>();
        MainActivity.orderPrefs.edit().clear().apply();
        for (int i = 0; i < items.size(); i++) {
            MainActivity.orderPrefs.edit().putInt((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_type", items.get(i).getType()).apply();
            MainActivity.orderPrefs.edit().putBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideDesc", items.get(i).getHideDesc()).apply();
            MainActivity.orderPrefs.edit().putBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideOnLockscreen", items.get(i).getHideOnLockScreen()).apply();
            MainActivity.orderPrefs.edit().putBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_fillEmpty", items.get(i).getFillEmpty()).apply();
            MainActivity.orderPrefs.edit().putBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_lockedWithPassword", items.get(i).getLockedWithPassword()).apply();
            MainActivity.orderPrefs.edit().putBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_hideText", items.get(i).getHideText()).apply();
            MainActivity.orderPrefs.edit().putBoolean((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_horizontal", items.get(i).getHorizontal()).apply();
            if (items.get(i).getType() == TYPE_MULTIPAGE_START) {
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", items.get(i).getTitle(1)).apply();
                MultiPage.add(items.get(i).getTitle(1));
            } else if (items.get(i).getType() == TYPE_MULTIPAGE_END) {
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", MultiPage.get(MultiPage.size() - 1)).apply();
                MultiPage.remove(MultiPage.size() - 1);
            } else if (items.get(i).getType() == TYPE_NORMAL) {
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_title", items.get(i).getTitle(1)).apply();
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_text", items.get(i).getText(1)).apply();
                MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item_shortcutUri", items.get(i).getShortcutUri(1)).apply();
            } else if (items.get(i).getType() == TYPE_MULTI) {
                for (int x = 1; x <= items.get(i).getTitles().size(); x++) {
                    MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item" + x + "_title", items.get(i).getTitle(x)).apply();
                    MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item" + x + "_text", items.get(i).getText(x)).apply();
                    MainActivity.orderPrefs.edit().putString((MultiPage.size() > 0 ? MultiPage.get(MultiPage.size() - 1) + "_" : "") + i + "_item" + x + "_shortcutUri", items.get(i).getShortcutUri(x)).apply();
                }
            }
        }
    }

}

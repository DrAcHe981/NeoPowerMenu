package de.NeonSoft.neopowermenu.helpers;

import android.annotation.NonNull;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.util.ArrayList;

import de.NeonSoft.neopowermenu.MainActivity;
import de.NeonSoft.neopowermenu.Preferences.AddShortcutList;
import de.NeonSoft.neopowermenu.Preferences.PreferencesAnimationsFragment;
import de.NeonSoft.neopowermenu.Preferences.PreferencesGraphicsFragment;
import de.NeonSoft.neopowermenu.R;
import de.NeonSoft.neopowermenu.addShortcut;
import de.NeonSoft.neopowermenu.services.TorchService;

import static com.android.internal.R.styleable.TextView;

public class ShortcutListAdapter extends ArrayAdapter<String> {

    Activity mActivity;
    LayoutInflater mInflater;
    ArrayList<String> mItems = new ArrayList<>();
    PackageManager pm;

    int selectedItem = 0;

    public ShortcutListAdapter(Activity activity, ArrayList<String> items) {
        super(activity, R.layout.shortcutlistitem, items);

        mActivity = activity;
        mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mItems = items;
        pm = mActivity.getPackageManager();
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        View InflatedView = mInflater.inflate(R.layout.shortcutlistitem, parent, false);

        LinearLayout root = (LinearLayout) InflatedView.findViewById(R.id.shortcutlistitem_root);

        ImageView imageBG = (ImageView) InflatedView.findViewById(R.id.shortcutlistitem_icon);
        imageBG.setVisibility(View.GONE);
        ImageView image = (ImageView) InflatedView.findViewById(R.id.shortcutlistitem_icon2);

        TextView title = (TextView) InflatedView.findViewById(R.id.shortcutlistitem_text1);
        TextView desc = (TextView) InflatedView.findViewById(R.id.shortcutlistitem_text2);
        desc.setVisibility(View.GONE);
        RadioButton check = (RadioButton) InflatedView.findViewById(R.id.shortcutlistitem_selector);
        check.setClickable(false);
        check.setFocusable(false);
        if (selectedItem == position) {
            check.setChecked(true);
        }

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelection(position);
            }
        });

        String string = mItems.get(position);
        if (string.equalsIgnoreCase("PowerMenu")) {
            string = mActivity.getString(R.string.shortcut_ShowPowerMenu);
        } else {
            if (string.contains(".")) {
                try {
                    string = pm.getApplicationInfo(string.split("/")[0], 0).loadLabel(pm).toString();
                } catch (PackageManager.NameNotFoundException e) {
                }
            } else {
                try {
                    string = mActivity.getResources().getString(mActivity.getResources().getIdentifier("powerMenuMain_" + mItems.get(position), "string", MainActivity.class.getPackage().getName()));
                } catch (Throwable t) {
                    try {
                        string = mActivity.getResources().getString(mActivity.getResources().getIdentifier("powerMenuBottom_" + mItems.get(position), "string", MainActivity.class.getPackage().getName()));
                    } catch (Throwable t1) {
                    }
                }
            }
        }
        title.setText(string);

        String color1 = "#ff000000", color2 = "#ffffffff";

        if (position==0) {
            color1 = "#ff404040";
            color2 = "#ffffffff";
        } else {
            color1 = addShortcut.colorPrefs.getString("Dialog" + addShortcut.items.get(position) + "_Circlecolor", "#ff000000");
            color2 = addShortcut.colorPrefs.getString("Dialog" + addShortcut.items.get(position) + "_Textcolor", "#ff000000");
        }

        image.setImageBitmap(addShortcut.createCircleIcon(mItems.get(position), string, color1, color2));
        return InflatedView;
    }

    public void setSelection(int position) {
        selectedItem = position;
        notifyDataSetChanged();
    }
    public int getSelectedPosition() {
        return selectedItem;
    }

}

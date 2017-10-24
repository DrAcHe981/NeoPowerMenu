package de.NeonSoft.neopowermenu.Preferences;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import de.NeonSoft.neopowermenu.R;
import de.NeonSoft.neopowermenu.addShortcut;
import de.NeonSoft.neopowermenu.helpers.ShortcutListAdapter;

public class AddShortcutList extends Fragment {

    Activity mActivity;

    ListView mShortcutList;
    public static ShortcutListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View InflatedView = inflater.inflate(R.layout.activity_addshortcutlist, container, false);

        addShortcut.useGraphic = true;
        addShortcut.useCustomGraphic = false;
        addShortcut.padding = 10f;

        mActivity = getActivity();

        mShortcutList = (ListView) InflatedView.findViewById(R.id.activityaddshortcutlistListView_List);

        addShortcut.items = new ArrayList<>(Arrays.asList(PreferencesVisibilityOrderFragment.PowerMenuItems));
        addShortcut.items.remove(0);
        addShortcut.items.add(0,getString(R.string.shortcut_ShowPowerMenu));

        adapter = new ShortcutListAdapter(mActivity, addShortcut.items);

        mShortcutList.setAdapter(adapter);
        addShortcut.mActionBar.setButton(getString(R.string.addShortcut_Next), R.drawable.ic_content_send, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addShortcut.color1 = addShortcut.colorPrefs.getString("Dialog" + addShortcut.items.get(adapter.getSelectedPosition()) + "_Circlecolor", "#ff000000");
                addShortcut.color2 = addShortcut.colorPrefs.getString("Dialog" + addShortcut.items.get(adapter.getSelectedPosition()) + "_Textcolor", "#ff000000");
                AddShortcutSettings settingsFragment = new AddShortcutSettings();
                settingsFragment.setItem(addShortcut.items.get(adapter.getSelectedPosition()));
                addShortcut.changePrefPage(settingsFragment, "Settings", false);
            }
        });

        return InflatedView;
    }
}

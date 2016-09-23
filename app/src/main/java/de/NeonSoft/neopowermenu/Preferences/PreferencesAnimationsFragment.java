package de.NeonSoft.neopowermenu.Preferences;

import android.os.*;
import android.view.*;
import android.widget.*;

import de.NeonSoft.neopowermenu.*;

import android.view.View.*;

import de.NeonSoft.neopowermenu.helpers.*;

public class PreferencesAnimationsFragment extends android.support.v4.app.Fragment {

    public static int[] defaultTypes = {
            0, 1, 0,
            0, 2, 0,
            0, 0, 0,
            0, 2, 0,
            0, 0, 0};
    public static Object[][] names = {
            {animationsAdapter.TYPE_HEADER, "reveal"},{animationsAdapter.TYPE_ITEM, "reveal_type"},{animationsAdapter.TYPE_ITEM, "reveal_speed"}, //0
            {animationsAdapter.TYPE_HEADER, "dialog"},{animationsAdapter.TYPE_ITEM, "dialog_type"},{animationsAdapter.TYPE_ITEM, "dialog_speed"}, //3
            {animationsAdapter.TYPE_HEADER, "icons"},{animationsAdapter.TYPE_ITEM, "icons_type"},{animationsAdapter.TYPE_ITEM, "icons_speed"}, //6
            {animationsAdapter.TYPE_HEADER, "singleline"},{animationsAdapter.TYPE_ITEM, "singleline_type"},{animationsAdapter.TYPE_ITEM, "singleline_speed"}, //9
            {animationsAdapter.TYPE_HEADER, "multiline"},{animationsAdapter.TYPE_ITEM, "multiline_type"},{animationsAdapter.TYPE_ITEM, "multiline_speed"}}; //12

    public static String[] customPrefs = {
            "Alpha",
            "XDelta",
            "YDelta",
            "XScale",
            "YScale",
            "PivotX",
            "PivotY",
            "XRotation",
            "YRotation",
            "Duration"};

    public static ListView holder;
    public static animationsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: Implement this method
        if (!MainActivity.visibleFragment.equalsIgnoreCase("tour")) {
            MainActivity.visibleFragment = "Animations";
        }
        MainActivity.actionbar.setTitle(getString(R.string.preferences_Animations).split("\\|")[0]);
        MainActivity.actionbar.setSubTitle(getString(R.string.preferences_Animations).split("\\|")[1]);

        View InflatedView = inflater.inflate(R.layout.activity_animations, container, false);

        holder = (ListView) InflatedView.findViewById(R.id.activityanimationsListView);

        adapter = new animationsAdapter(getActivity(), names, defaultTypes);

        holder.setAdapter(adapter);

        return InflatedView;
    }

}

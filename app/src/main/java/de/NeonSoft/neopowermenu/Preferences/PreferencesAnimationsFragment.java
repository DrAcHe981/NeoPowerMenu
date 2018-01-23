package de.NeonSoft.neopowermenu.Preferences;

import android.os.*;
import android.view.*;
import android.widget.*;

import de.NeonSoft.neopowermenu.*;

import android.view.View.*;

import de.NeonSoft.neopowermenu.helpers.*;

public class PreferencesAnimationsFragment extends android.support.v4.app.Fragment {


    public static int anim_Reveal = 0;
    public static int anim_Dialog = 4;
    public static int anim_Icons = 8;
    public static int anim_SingleLine = 12;
    public static int anim_MultiLine = 16;
    public static int anim_Progressbar = 20;

    public static int anim_Type = 1;
    public static int anim_Interpolator = 2;
    public static int anim_Speed = 3;

    public static int[] defaultTypes = {
            0, 1, 0, 3,
            0, 2, 0, 3,
            0, 0, 0, 3,
            0, 2, 0, 3,
            0, 0, 0, 3,
            0, 0, 3, 3};
    public static Object[][] names = {
            {animationsAdapter.TYPE_HEADER, "reveal"},{animationsAdapter.TYPE_ITEM, "reveal_type"},{animationsAdapter.TYPE_ITEM, "reveal_interpolator"},{animationsAdapter.TYPE_ITEM, "reveal_speed"}, //0
            {animationsAdapter.TYPE_HEADER, "dialog"},{animationsAdapter.TYPE_ITEM, "dialog_type"},{animationsAdapter.TYPE_ITEM, "dialog_interpolator"},{animationsAdapter.TYPE_ITEM, "dialog_speed"}, //4
            {animationsAdapter.TYPE_HEADER, "icons"},{animationsAdapter.TYPE_ITEM, "icons_type"},{animationsAdapter.TYPE_ITEM, "icons_interpolator"},{animationsAdapter.TYPE_ITEM, "icons_speed"}, //8
            {animationsAdapter.TYPE_HEADER, "singleline"},{animationsAdapter.TYPE_ITEM, "singleline_type"},{animationsAdapter.TYPE_ITEM, "singleline_interpolator"},{animationsAdapter.TYPE_ITEM, "singleline_speed"}, //12
            {animationsAdapter.TYPE_HEADER, "multiline"},{animationsAdapter.TYPE_ITEM, "multiline_type"},{animationsAdapter.TYPE_ITEM, "multiline_interpolator"},{animationsAdapter.TYPE_ITEM, "multiline_speed"}, //16
            {animationsAdapter.TYPE_HEADER, "progressbar"},{animationsAdapter.TYPE_ITEM, "progressbar_type"},{animationsAdapter.TYPE_ITEM, "progressbar_interpolator"},{animationsAdapter.TYPE_ITEM, "progressbar_speed"}}; //20

    public static String[] customPrefs = {
            "fromAlpha",
            "toAlpha",
            "fromXDelta",
            "toXDelta",
            "fromYDelta",
            "toYDelta",
            "fromXScale",
            "toXScale",
            "fromYScale",
            "toYScale",
            "PivotX",
            "PivotY",
            "fromXRotation",
            "toXRotation",
            "fromYRotation",
            "toYRotation"};

    public static ListView holder;
    public static animationsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (!MainActivity.visibleFragment.equalsIgnoreCase("tour")) {
            MainActivity.visibleFragment = "Animations";
        }
        MainActivity.actionbar.setTitle(getString(R.string.preferences_AnimationsTitle));
        MainActivity.actionbar.setSubTitle(getString(R.string.preferences_AnimationsDesc));

        View InflatedView = inflater.inflate(R.layout.activity_animations, container, false);

        holder = (ListView) InflatedView.findViewById(R.id.activityanimationsListView);

        adapter = new animationsAdapter(getActivity(), names, defaultTypes);

        holder.setAdapter(adapter);

        return InflatedView;
    }

}

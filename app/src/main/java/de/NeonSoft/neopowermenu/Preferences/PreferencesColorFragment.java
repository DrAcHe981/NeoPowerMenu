package de.NeonSoft.neopowermenu.Preferences;

import android.content.*;
import android.content.ClipboardManager;
import android.os.*;
import android.view.*;
import android.widget.*;

import de.NeonSoft.neopowermenu.*;

import android.support.v4.app.Fragment;

import de.NeonSoft.neopowermenu.R;
import de.NeonSoft.neopowermenu.helpers.*;

public class PreferencesColorFragment extends Fragment {

    Context mContext;

    public static android.content.ClipboardManager cbM;

    ListView ListView_ColorsList;
    ColorsListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!MainActivity.visibleFragment.equalsIgnoreCase("tour")) {
            MainActivity.visibleFragment = "CustomColors";
        }
        View InflatedView = inflater.inflate(R.layout.activity_colorpreferences, container, false);

        mContext = getActivity();
        cbM = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);

        MainActivity.actionbar.setTitle(getString(R.string.preferences_ThemeTitle));
        MainActivity.actionbar.setSubTitle(getString(R.string.preferences_ThemeDesc));

        ListView_ColorsList = (ListView) InflatedView.findViewById(R.id.activitycolorpreferencesListView_Colors);

        neopowermenu.parseColors();

        adapter = new ColorsListAdapter(getActivity(), neopowermenu.colors, neopowermenu.defaultColors);
        ListView_ColorsList.setFastScrollEnabled(true);
        ListView_ColorsList.setAdapter(adapter);


        return InflatedView;
    }

}

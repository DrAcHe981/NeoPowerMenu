package de.NeonSoft.neopowermenu.Preferences;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.*;
import android.support.v4.app.*;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.*;

import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.*;

import java.util.*;

public class AboutFragment extends Fragment {


    public static RelativeLayout LoadingLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: Implement this method
        MainActivity.visibleFragment = "about";

        MainActivity.actionbar.hideButton();
        View InflatedView = inflater.inflate(R.layout.activity_about, container, false);

        ListView list = (ListView) InflatedView.findViewById(R.id.activityaboutListView1);

        LoadingLayout = (RelativeLayout) InflatedView.findViewById(R.id.activityaboutRelativeLayout_Progress);
        LoadingLayout.setVisibility(View.GONE);

        String[] titles = new String[]{"About",
                getString(R.string.tourReview_Title),
                "User Id",
                "Force Language",
                "Used Libraries",
                "CustomActivityOnCrash",
                "ACRA",
                "SmartTabLayout",
                "HoloColorPicker",
                "DragSortListView",
                "libsuperuser"};
        ArrayList<String> titlesList = new ArrayList<String>(Arrays.asList(titles));

        Resources res = getResources();
        Configuration conf = res.getConfiguration();

        String[] texts = new String[]{"NeoPowerMenu by Neon-Soft / DrAcHe981\n" +
                "based on the Source from Naman Dwivedi (naman14).\n\n" +
                "Translators (I update them from time to time so don't be angry if you translated, but don't got listed here ;D):\n" +
                "> German:\n" +
                " Robing G. (DrAcHe981), MrWasdennnoch\n" +
                "> Dutch:\n" +
                " Hakan Ilbas (hakanilbas)\n" +
                "> French:\n" +
                " Joël COSTAMAGNA (Joel-Costamagna), Raziel65, Rom (RomXDA)\n" +
                "> Italian:\n" +
                " Lucas Rey (LordSpectre)\n" +
                "> Japanese:\n" +
                " AndroPlus (Noumi)\n" +
                "> Polish:\n" +
                " Kasia Suchecka (kejciaq)\n" +
                "> Portuguese:\n" +
                " Joel Bastos (burns8888)\n" +
                "> Portuguese (BR):\n" +
                " Marcio Andrade (marciozomb13), Rhaysf15\n" +
                "> Romanian:\n" +
                " \n" +
                "> Russian:\n" +
                " Smirnaff, Артем Пичугин (leonardo906), Дмитрий Никифорян (pekjiamiiiuk2014)\n" +
                "> Spanish (MX):\n" +
                " CricketBlueSide, Daniel Carrero (danielcr), Joseahfer, WarLion\n" +
                "> Turkish:\n" +
                " Fatih Fırıncı (Fatih_BaKeR)\n\n" +
                "Special Thanks:\n" +
                " You for using my Module.\n" +
                " C3C0 for all the code I took out of GravityBox.\n" +
                " Naman Dwivedi (naman14) for the original source.\n" +
                " rovo89 and Tungstwenty for Xposed.\n" +
                " Igor Da Silva for the concept.",
                getString(R.string.tourReview_Desc),
                "Your Device Id:\n" + ((MainActivity.deviceUniqeId.isEmpty() || MainActivity.deviceUniqeId.equalsIgnoreCase("none")) ? "Not generated. (this is not normal...)" : MainActivity.deviceUniqeId) + "\nYour Account Id:\n" + ((MainActivity.accountUniqeId.isEmpty() || MainActivity.accountUniqeId.equalsIgnoreCase("none")) ? "Not logged in." : MainActivity.accountUniqeId) + "\nThis Id's are used by the Preset Sever to verify your identity.",
                "Click here to force the language, current:\n" +
                        conf.locale.getDisplayName() + " (" + conf.locale.getLanguage() + (conf.locale.getCountry().isEmpty() ? "" : "_" + conf.locale.getCountry().toUpperCase()) + ")",
                "This Project uses some public libraries, all (maybe i have forgot some...) used libraries are listed below.",
                "Copyright 2014 Eduard Ereza Martinez.\nLicensed under the Apache License, Version 2.0",
                "Licensed under the Apache License, Version 2.0",
                "Copyright Oraclejapan\nLicensed under the Apache License, Version 2.0",
                "Copyright Lars Werkman.\nAn Android Holo themed colorpicker designed by Marie Schweiz.\nLicensed under the Apache License, Version 2.0",
                "Copyright Bauerca\nDragSortListView is an extension of the Android ListView that enables drag-and-drop reordering of list items.\nLicensed under the Apache License, Version 2.0",
                "Copyright 2012-2015 Jorrit Chainfire Jongma.\nLicensed under the Apache License, Version 2.0"};
        ArrayList<String> textsList = new ArrayList<String>(Arrays.asList(texts));

        aboutAdapter aa = new aboutAdapter(getActivity(), titlesList, textsList);

        list.setFastScrollEnabled(true);
        list.setAdapter(aa);

        return InflatedView;
    }

}

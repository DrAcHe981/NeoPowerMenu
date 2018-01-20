/*
 * Copyright 2015 Eduard Ereza Martínez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.NeonSoft.neopowermenu;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import cat.ereza.customactivityoncrash.*;
import de.NeonSoft.neopowermenu.helpers.*;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

import android.support.v4.app.*;
import android.support.v7.app.*;

import java.util.*;

import android.content.res.*;

public final class ErrorActivity extends AppCompatActivity {

    public static android.support.v4.app.FragmentManager fragmentManager;

    public static ErrorActivity thisActivity;

    public static String versionName = "v1.0";
    public static int versionCode = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.ThemeBaseDark);
        super.onCreate(savedInstanceState);

        thisActivity = this;

        fragmentManager = getSupportFragmentManager();


        setContentView(de.NeonSoft.neopowermenu.R.layout.customactivityoncrash_error_activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkDarkTheme));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.window_background_dark));
        }
        LinearLayout actionBar = (LinearLayout) findViewById(R.id.actionbar);
        actionBar actionbar = new actionBar(this);
        actionbar.addActionBar(actionBar);
        actionbar.setAnimationsEnabled(false);
        actionbar.setTitle("NeoPowerMenu");
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (Throwable e) {
            Log.e("NPM", "Failed to get Version info: ", e);
        }
        actionbar.setSubTitle("v" + versionName + "(" + versionCode + ")");
        actionbar.setAnimationsEnabled(true);

        fragmentManager.beginTransaction().replace(R.id.error_container, new errorFragment()).commit();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.findFragmentByTag(slideDownDialogFragment.dialogTag) != null) {
                        /*Intent intent = new Intent();
						intent.setAction("de.NeonSoft.neopowermenu.closeDialogs");
						thisActivity.sendBroadcast(intent);*/
            slideDownDialogFragment.dialogs.get(slideDownDialogFragment.dialogs.size() - 1).cancelDialog();
            return;
        }
        super.onBackPressed();
    }
}

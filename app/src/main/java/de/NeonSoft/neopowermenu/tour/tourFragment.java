package de.NeonSoft.neopowermenu.tour;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import de.NeonSoft.neopowermenu.MainActivity;
import de.NeonSoft.neopowermenu.Preferences.PreferencesAdvancedFragment;
import de.NeonSoft.neopowermenu.Preferences.PreferencesAnimationsFragment;
import de.NeonSoft.neopowermenu.Preferences.PreferencesColorFragment;
import de.NeonSoft.neopowermenu.Preferences.PreferencesGraphicsFragment;
import de.NeonSoft.neopowermenu.Preferences.PreferencesPartFragment;
import de.NeonSoft.neopowermenu.Preferences.PreferencesPresetsFragment;
import de.NeonSoft.neopowermenu.Preferences.PreferencesVisibilityOrderFragment;
import de.NeonSoft.neopowermenu.R;
import de.NeonSoft.neopowermenu.helpers.NonSwipeableViewPager;
import de.NeonSoft.neopowermenu.permissionsScreen.permissionsScreen;

public class tourFragment extends Fragment {

    static Activity mActivity;
    static Fragment thisFragment;

    public static NonSwipeableViewPager pager;
    MyPagerAdapter pagerAdapter;

    public static View.OnClickListener finishOnClick;
    View.OnClickListener nexttourPage;

    public static ArrayList<ImageView> pageImages = new ArrayList<>();
    public static ArrayList<LinearLayout> pageTextHolders = new ArrayList<>();

    Animation slideIn;
    Animation fadeIn;

    public tourFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        thisFragment = this;
        mActivity = getActivity();
        MainActivity.actionbar.setAnimationsEnabled(true);

        slideIn = AnimationUtils.loadAnimation(mActivity,R.anim.anim_slide_in_bottom);
        fadeIn = AnimationUtils.loadAnimation(mActivity,R.anim.fade_in);

        MainActivity.visibleFragment = "tour";
        MainActivity.actionbar.setTitle("NeoPowerMenu");
        MainActivity.actionbar.hideSubTitle();
        MainActivity.actionbar.setButton(getString(R.string.tourPage_Next), R.drawable.ic_content_send, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    pager.setCurrentItem(pager.getCurrentItem() + 1, true);
            }
        });

        View InflatedView = inflater.inflate(R.layout.tourfragment, container, false);

        LinearLayout root = (LinearLayout) InflatedView.findViewById(R.id.tourPageRoot);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        pager = (NonSwipeableViewPager) InflatedView.findViewById(R.id.pager);
        pagerAdapter = new MyPagerAdapter(MainActivity.fragmentManager, new String[]{"0", "1", "2", "3", "4", "5", "6", "7"});
        pager.setAdapter(pagerAdapter);

        finishOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishTour();
            }
        };

        nexttourPage = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pager.setCurrentItem(pager.getCurrentItem() + 1, true);
            }
        };

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pageTextHolders.get(position).setVisibility(View.VISIBLE);
                pageTextHolders.get(position).startAnimation(slideIn);
                if (position == 0) {
                    pageImages.get(position).setVisibility(View.VISIBLE);
                    pageImages.get(position).startAnimation(fadeIn);
                    MainActivity.changePrefPage(new PreferencesPartFragment(), false);
                    MainActivity.actionbar.setButton(getString(R.string.tourPage_Next), R.drawable.ic_content_send, nexttourPage);
                } else if (position == 1) {
                    MainActivity.changePrefPage(new PreferencesPartFragment(), false);
                    MainActivity.actionbar.setButton(getString(R.string.tourPage_Next), R.drawable.ic_content_send, nexttourPage);
                } else if (position == 2) {
                    MainActivity.changePrefPage(new PreferencesColorFragment(), false);
                    MainActivity.actionbar.setButton(getString(R.string.tourPage_Next), R.drawable.ic_content_send, nexttourPage);
                } else if (position == 3) {
                    MainActivity.changePrefPage(new PreferencesGraphicsFragment(), false);
                    MainActivity.actionbar.setButton(getString(R.string.tourPage_Next), R.drawable.ic_content_send, nexttourPage);
                } else if (position == 4) {
                    MainActivity.changePrefPage(new PreferencesVisibilityOrderFragment(), false);
                    MainActivity.actionbar.setButton(getString(R.string.tourPage_Next), R.drawable.ic_content_send, nexttourPage);
                } else if (position == 5) {
                    MainActivity.changePrefPage(new PreferencesAnimationsFragment(), false);
                    MainActivity.actionbar.setButton(getString(R.string.tourPage_Next), R.drawable.ic_content_send, nexttourPage);
                } else if (position == 6) {
                    MainActivity.changePrefPage(new PreferencesAdvancedFragment(), false);
                    MainActivity.actionbar.setButton(getString(R.string.tourPage_Next), R.drawable.ic_content_send, nexttourPage);
                } else if (position == 7) {
                    pageImages.get(position).setVisibility(View.VISIBLE);
                    pageImages.get(position).startAnimation(fadeIn);
                    MainActivity.changePrefPage(new PreferencesPartFragment(), false);
                    MainActivity.actionbar.setButton(getString(R.string.tourPage_Enjoy), R.drawable.ic_action_launch, finishOnClick);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        return InflatedView;
    }

    public static void finishTour() {
        MainActivity.visibleFragment = "main";
        MainActivity.fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).remove(MainActivity.fragmentManager.findFragmentByTag("tour")).commit();
        if (MainActivity.preferences.getBoolean("DontAskPermissionsAgain", false) || permissionsScreen.checkPermissions(mActivity, permissionsScreen.permissions)) {
            if (MainActivity.ImportUrl != null) {
                MainActivity.changePrefPage(new PreferencesPresetsFragment(), false);
            } else {
                android.support.v4.app.Fragment fragment = new PreferencesPartFragment();
                MainActivity.changePrefPage(fragment, false);
            }
            MainActivity.actionbar.setButtonText(mActivity.getString(R.string.PreviewPowerMenu));
            MainActivity.actionbar.setButtonIcon(R.drawable.ic_action_launch);
            MainActivity.actionbar.setButtonListener(MainActivity.previewOnClickListener);
        } else {
            MainActivity.changePrefPage(new permissionsScreen(), false);
        }
    }

    private static class MyPagerAdapter extends FragmentStatePagerAdapter {
        private static String[] pageTitles;

        public MyPagerAdapter(FragmentManager fragmentManager, String[] titles) {
            super(fragmentManager);
            pageTitles = titles;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return pageTitles.length;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            tourPage page = new tourPage();
            Bundle pageBundle = new Bundle();
            switch (position) {
                case 0:
                    pageBundle.putInt("page", 0);
                    pageBundle.putString("title", "0");
                    page.setArguments(pageBundle);
                    return page;
                case 1:
                    pageBundle.putInt("page", 1);
                    pageBundle.putString("title", "1");
                    page.setArguments(pageBundle);
                    return page;
                case 2:
                    pageBundle.putInt("page", 2);
                    pageBundle.putString("title", "2");
                    page.setArguments(pageBundle);
                    return page;
                case 3:
                    pageBundle.putInt("page", 3);
                    pageBundle.putString("title", "3");
                    page.setArguments(pageBundle);
                    return page;
                case 4:
                    pageBundle.putInt("page", 4);
                    pageBundle.putString("title", "4");
                    page.setArguments(pageBundle);
                    return page;
                case 5:
                    pageBundle.putInt("page", 5);
                    pageBundle.putString("title", "5");
                    page.setArguments(pageBundle);
                    return page;
                case 6:
                    pageBundle.putInt("page", 6);
                    pageBundle.putString("title", "6");
                    page.setArguments(pageBundle);
                    return page;
                case 7:
                    pageBundle.putInt("page", 7);
                    pageBundle.putString("title", "7");
                    page.setArguments(pageBundle);
                    return page;
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "" + pageTitles[position];
        }

    }
}

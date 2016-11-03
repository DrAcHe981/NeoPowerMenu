package de.NeonSoft.neopowermenu.Preferences;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.view.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.widget.AdapterView.*;
import android.widget.TextView.*;

import com.ogaclejapan.smarttablayout.*;

import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.*;

import java.io.*;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View.OnClickListener;

import de.NeonSoft.neopowermenu.R;

import java.util.*;

public class PreferencesPresetsFragment extends Fragment {

    public static AlertDialog importad;
    public static String Filename;
    public static String sCreator = "< unknown >";
    public static String newUrl;
    public static boolean Importcancled = false;

    public static RelativeLayout progressHolder;
    public static ProgressBar progress;
    public static TextView LoadingMsg;

    public static Activity mContext;

    View InflatedView;
    public static ViewPager vpPager;
    public static MyPagerAdapter adapterViewPager;

    public static PresetsAdapter localAdapter;
    public static PresetsAdapter onlineAdapter;
    public static ListView onlineList;
    public static TextView onlineMSG;
    public static RelativeLayout onlineMSGHolder;

    public static LinearLayout onlineSearch;

    public static LinearLayout onlineSearchBar;
    public static EditText onlineSearchEdit;
    public static ImageView onlineStartSearch;

    public static LinearLayout onlineOrder;

    public static String onlineSearchTerm = "";

    private static int onlineOrderSelected = 0;
    public static String onlineOrderSelectedString = "";

    public static boolean onlineRequestIsRunning;

    public static String DownloadingActiveFor = "";
    /*public static ArrayList<LinearLayout> DownloadingActiveForRoot = new ArrayList<LinearLayout>();
    public static ArrayList<downloadHelper> DownloadingActiveForHelper = new ArrayList<downloadHelper>();
    public static ArrayList<LinearLayout> DownloadingActiveForLayout = new ArrayList<LinearLayout>();
    public static ArrayList<ImageView> DownloadingActiveForImageView = new ArrayList<ImageView>();
    public static ArrayList<String> DownloadingActiveForOldText = new ArrayList<String>();
    public static ArrayList<TextView> DownloadingActiveForLabel = new ArrayList<TextView>();
    public static ArrayList<ProgressBar> DownloadingActiveForProgress = new ArrayList<ProgressBar>();*/
		public static ArrayList<PresetsHolder> OnlinePresets = new ArrayList<>();

    //public static ArrayList<String> OnlineListTitles = new ArrayList<String>();
    //public static ArrayList<String> OnlineListDescs = new ArrayList<String>();
    //public static ArrayList<String> OnlineListEnabled = new ArrayList<String>();
    //public static ArrayList<String> OnlineListLocal = new ArrayList<String>();
    //public static ArrayList<Boolean> OnlineHasGraphics = new ArrayList<Boolean>();

    public static AsyncTask listParser;

    //public static String oldUploadText = "";

    public static int startTab = 1;

    public void setStartTab(int tab) {
        startTab = tab;
    }

    public PreferencesPresetsFragment() {
        startTab = 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        MainActivity.visibleFragment = "PresetsManager";

        MainActivity.actionbar.setTitle(getString(R.string.preset_Load));
        MainActivity.actionbar.setSubTitle(getString(R.string.preset_LoadDesc));

        mContext = getActivity();
        onlineSearchTerm = "";
        onlineOrderSelected = 0;
        onlineOrderSelectedString = "";

        InflatedView = inflater.inflate(R.layout.activity_presetsmanager, container, false);


        onlineSearch = (LinearLayout) InflatedView.findViewById(R.id.activitypresetsmanagerLinearLayout_SearchIcon);
        onlineSearch.setVisibility(View.GONE);

        onlineSearchBar = (LinearLayout) InflatedView.findViewById(R.id.activitypresetsmanagerLinearLayout_SearchBar);
        onlineSearchEdit = (EditText) InflatedView.findViewById(R.id.activitypresetsmanagerEditText_Search);
        onlineStartSearch = (ImageView) InflatedView.findViewById(R.id.activitypresetsmanagerImageView_StartSearch);
        onlineSearchBar.setVisibility(View.GONE);

        onlineOrder = (LinearLayout) InflatedView.findViewById(R.id.activitypresetsmanagerLinearLayout_Order);
        onlineOrder.setVisibility(View.GONE);

        vpPager = (ViewPager) InflatedView.findViewById(R.id.pager);
        adapterViewPager = new MyPagerAdapter(MainActivity.fragmentManager, new String[]{getString(R.string.presetsManager_TitleAccount), getString(R.string.presetsManager_TitleLocal), getString(R.string.presetsManager_TitleOnline)});
        vpPager.setAdapter(adapterViewPager);

        vpPager.setCurrentItem(startTab);

        if (startTab == 0) {
            MainActivity.visibleFragment = "PresetsManagerAccount";
        } else if (startTab == 1) {
            MainActivity.visibleFragment = "PresetsManager";
        } else if (startTab == 2) {
            MainActivity.visibleFragment = "PresetsManagerOnline";
        }

        vpPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int p1, float p2, int p3) {

            }

            @Override
            public void onPageSelected(int p1) {
                checkPage();
            }

            @Override
            public void onPageScrollStateChanged(int p1) {

            }
        });


        SmartTabLayout tabsStrip = (SmartTabLayout) InflatedView.findViewById(R.id.tabs);
        tabsStrip.setCustomTabView(R.layout.customtab, R.id.customTabText);

        tabsStrip.setViewPager(vpPager);

        progressHolder = (RelativeLayout) InflatedView.findViewById(R.id.presetsmanagerlistholderRelativeLayout_Progress);
        progressHolder.setVisibility(View.GONE);
        progress = (ProgressBar) InflatedView.findViewById(R.id.presetsmanagerlistholderProgressBar_Progress);
        LoadingMsg = (TextView) InflatedView.findViewById(R.id.presetsmanagerlistholderTextView_LoadMsg);

        progressHolder.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View p1) {
                // Just prevent touch trough...
            }
        });
        return InflatedView;
    }

    public static void checkPage() {
        if (adapterViewPager.getPageTitle(vpPager.getCurrentItem()).toString().equalsIgnoreCase(mContext.getString(R.string.presetsManager_TitleOnline))) {
            MainActivity.visibleFragment = "PresetsManagerOnline";
            MainActivity.actionbar.setButton(mContext.getString(R.string.presetsManager_Refresh), R.drawable.ic_action_autorenew, new OnClickListener() {

                @Override
                public void onClick(View p1) {

                    listParser = helper.startAsyncTask(new getOnlinePresets(),(onlineOrderSelectedString.isEmpty() ? "" : "order=" + PreferencesPresetsFragment.onlineOrderSelectedString));
                }
            });
            onlineSearch.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View p1) {

                    if (onlineSearch.getVisibility() == View.VISIBLE) {
                        onlineSearch.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_out_top));
                        onlineSearch.setVisibility(View.GONE);
                    }
                    if (onlineSearchBar.getVisibility() == View.GONE) {
                        onlineStartSearch.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View p1) {

                                if (!onlineSearchEdit.getText().toString().isEmpty()) {
                                    try {
                                        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                                        IBinder windowToken = onlineSearchEdit.getWindowToken();
                                        inputManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);
                                    } catch (Throwable t) {
                                    }
                                    onlineSearchTerm = onlineSearchEdit.getText().toString();
                                    listParser = helper.startAsyncTask(new getOnlinePresets(),(onlineOrderSelectedString.isEmpty() ? "" : "order=" + onlineOrderSelectedString), (onlineSearchTerm.isEmpty() ? "" : "search=" + onlineSearchTerm));
                                }
                            }
                        });
                        onlineSearchEdit.setOnEditorActionListener(new OnEditorActionListener() {

                            @Override
                            public boolean onEditorAction(TextView p1, int p2, KeyEvent p3) {

                                onlineStartSearch.callOnClick();
                                return false;
                            }
                        });
                        onlineSearchEdit.setText("");
                        onlineSearchBar.setVisibility(View.VISIBLE);
                        onlineSearchBar.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_in_top));
                        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        onlineSearchEdit.requestFocus();
                        IBinder windowToken = onlineSearchEdit.getWindowToken();
                        inputManager.showSoftInput(onlineSearchEdit, InputMethodManager.SHOW_IMPLICIT);
                    }
                    if (onlineOrder.getVisibility() == View.VISIBLE) {
                        onlineOrder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_out_top));
                        onlineOrder.setVisibility(View.GONE);
                    }
                }
            });
            onlineOrder.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View p1) {

                    if (onlineSearch.getVisibility() == View.VISIBLE) {
                        onlineSearch.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_out_top));
                        onlineSearch.setVisibility(View.GONE);
                    }
                    if (onlineOrder.getVisibility() == View.VISIBLE) {
                        onlineOrder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_out_top));
                        onlineOrder.setVisibility(View.GONE);
                    }
                    slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                    dialogFragment.setContext(mContext);
                    dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                    dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

                        @Override
                        public void onListItemClick(int position, String text) {

                            onlineOrderSelected = position;
                            onlineOrderSelectedString = text;
                            MainActivity.actionbar.setButtonListener(new OnClickListener() {

                                @Override
                                public void onClick(View p1) {

                                    listParser = helper.startAsyncTask(new getOnlinePresets(),"order=" + onlineOrderSelectedString);
                                }
                            });
                            hideBars();
                            listParser = helper.startAsyncTask(new getOnlinePresets(),"order=" + onlineOrderSelectedString);
                        }

                        @Override
                        public void onNegativeClick() {

                            hideBars();
                        }

                        @Override
                        public void onNeutralClick() {

                        }

                        @Override
                        public void onPositiveClick(Bundle resultBundle) {

                            hideBars();
                        }

                        @Override
                        public void onTouchOutside() {

                            hideBars();
                        }
                    });
                    dialogFragment.setText(mContext.getString(R.string.presetsManager_OrderBy));
                    dialogFragment.setList(ListView.CHOICE_MODE_SINGLE, new String[]{
                            mContext.getString(R.string.presetsManager_OrderNames).split("\\|")[0] + " (" + mContext.getString(R.string.presetsManager_OrderAscDesc).split("\\|")[0] + ")",
                            mContext.getString(R.string.presetsManager_OrderNames).split("\\|")[0] + " (" + mContext.getString(R.string.presetsManager_OrderAscDesc).split("\\|")[1] + ")",
                            mContext.getString(R.string.presetsManager_OrderNames).split("\\|")[1] + " (" + mContext.getString(R.string.presetsManager_OrderAscDesc).split("\\|")[0] + ")",
                            mContext.getString(R.string.presetsManager_OrderNames).split("\\|")[1] + " (" + mContext.getString(R.string.presetsManager_OrderAscDesc).split("\\|")[1] + ")",
                            mContext.getString(R.string.presetsManager_OrderNames).split("\\|")[2] + " (" + mContext.getString(R.string.presetsManager_OrderAscDesc).split("\\|")[0] + ")",
                            mContext.getString(R.string.presetsManager_OrderNames).split("\\|")[2] + " (" + mContext.getString(R.string.presetsManager_OrderAscDesc).split("\\|")[1] + ")",
                            mContext.getString(R.string.presetsManager_OrderNames).split("\\|")[3] + " (" + mContext.getString(R.string.presetsManager_OrderAscDesc).split("\\|")[0] + ")",
                            mContext.getString(R.string.presetsManager_OrderNames).split("\\|")[3] + " (" + mContext.getString(R.string.presetsManager_OrderAscDesc).split("\\|")[1] + ")",
                            mContext.getString(R.string.presetsManager_OrderNames).split("\\|")[4] + " (" + mContext.getString(R.string.presetsManager_OrderAscDesc).split("\\|")[0] + ")",
                            mContext.getString(R.string.presetsManager_OrderNames).split("\\|")[4] + " (" + mContext.getString(R.string.presetsManager_OrderAscDesc).split("\\|")[1] + ")"}, onlineOrderSelected, true);
                    dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[0]);
                    dialogFragment.showDialog(R.id.dialog_container);
                    if (onlineOrder.getVisibility() == View.VISIBLE) {
                        onlineOrder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_out_top));
                        onlineOrder.setVisibility(View.GONE);
                    }
                }
            });
            onlineSearch.setVisibility(View.VISIBLE);
            onlineSearch.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_in_top));
            onlineOrder.setVisibility(View.VISIBLE);
            onlineOrder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_in_top));
        } else if (adapterViewPager.getPageTitle(vpPager.getCurrentItem()).toString().equalsIgnoreCase(mContext.getString(R.string.presetsManager_TitleAccount))) {
            MainActivity.visibleFragment = "PresetsManagerAccount";
            if (LoginFragment.loginFragmentMode.equalsIgnoreCase("login")) {
                MainActivity.actionbar.setButton(mContext.getString(R.string.login_Title), R.drawable.ic_action_import, LoginFragment.loginOnClickListener);
            } else if (LoginFragment.loginFragmentMode.equalsIgnoreCase("register")) {
                MainActivity.actionbar.setButton(mContext.getString(R.string.login_TitleRegister), R.drawable.ic_action_import, LoginFragment.registerOnClickListener);
            } else if (MainActivity.loggedIn) {
                MainActivity.actionbar.setButton(mContext.getString(R.string.login_TitleLogout), R.drawable.ic_action_export, LoginFragment.logoutOnClickListener);
            } else if (LoginFragment.loginFragmentMode.equalsIgnoreCase("recover")) {
                MainActivity.actionbar.setButton(mContext.getString(R.string.login_Recover), R.drawable.ic_action_settings_backup_restore, LoginFragment.recoverOnClickListener);
            }
            if (onlineSearch.getVisibility() == View.VISIBLE) {
                onlineSearch.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_out_top));
                onlineSearch.setVisibility(View.GONE);
            }
            if (onlineSearchBar.getVisibility() == View.VISIBLE) {
                onlineSearchBar.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_out_top));
                onlineSearchBar.setVisibility(View.GONE);
                //onlineSearchEdit.setText("");
                try {
                    InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    IBinder windowToken = onlineSearchEdit.getWindowToken();
                    inputManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (Throwable t) {
                }
            }
            if (onlineOrder.getVisibility() == View.VISIBLE) {
                onlineOrder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_out_top));
                onlineOrder.setVisibility(View.GONE);
            }
            LoginFragment.checkState();
        } else {
            MainActivity.visibleFragment = "PresetsManager";
            MainActivity.actionbar.setButton(mContext.getString(R.string.PreviewPowerMenu), R.drawable.ic_action_launch, MainActivity.previewOnClickListener);
            if (onlineSearch.getVisibility() == View.VISIBLE) {
                onlineSearch.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_out_top));
                onlineSearch.setVisibility(View.GONE);
            }
            if (onlineSearchBar.getVisibility() == View.VISIBLE) {
                onlineSearchBar.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_out_top));
                onlineSearchBar.setVisibility(View.GONE);
                //onlineSearchEdit.setText("");
                try {
                    InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    IBinder windowToken = onlineSearchEdit.getWindowToken();
                    inputManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (Throwable t) {
                }
            }
            if (onlineOrder.getVisibility() == View.VISIBLE) {
                onlineOrder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_out_top));
                onlineOrder.setVisibility(View.GONE);
            }
        }
    }

    public static void hideBars() {
        if (onlineSearchBar.getVisibility() == View.VISIBLE) {
            onlineSearchBar.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_out_top));
            onlineSearchBar.setVisibility(View.GONE);
        }
        onlineSearchTerm = "";
        onlineSearch.setVisibility(View.VISIBLE);
        onlineSearch.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_in_top));
        onlineOrder.setVisibility(View.VISIBLE);
        onlineOrder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.abc_slide_in_top));
    }

    public static boolean ImportPreset(final String surl, final PresetsAdapter adapter, String name, final String creator) {
        Importcancled = false;
        sCreator = "< unknown >";
        final boolean isZip;
        try {
            //Log.i("NPM","Showing import dialog for: "+surl);
            String fUrl = surl;
            if (!surl.endsWith(".nps")) {
                MainActivity.ImportUrl = null;
                Toast.makeText(mContext, "Import failed...\nUnknown file type!", Toast.LENGTH_LONG).show();
                return false;
            }
            if (surl.startsWith("file:")) {
                fUrl = surl.replace("file:", "");
            }
            newUrl = fUrl;
            File prefile = new File(newUrl);
            Filename = (name != null && !name.isEmpty()) ? name + ".nps" : prefile.getName();
            if (helper.isValidZip(prefile.getPath(), null)) {
                if (helper.unzipFile(prefile.getPath(), mContext.getFilesDir().getPath() + "/temp/", Filename, null) == null) {
                    prefile = new File(mContext.getFilesDir().getPath() + "/temp/" + Filename);
                } else {
                    Toast.makeText(mContext, "Import failed...\nCorrupted or invalid preset!", Toast.LENGTH_LONG).show();
                    return false;
                }
                isZip = true;
            } else {
                isZip = false;
            }
            FileInputStream fIn = new FileInputStream(prefile);
            BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
            String aDataRow = "";
            //final String[] presetInfo = new String[4];
						final PresetsHolder preset = new PresetsHolder();
						preset.setType(PresetsHolder.TYPE_INTERNAL);
            preset.setName(Filename.split(".nps")[0]);
            preset.setDescription("< unknown >");
            while ((aDataRow = myReader.readLine()) != null) {
                //aBuffer += aDataRow + "\n";
                if(!aDataRow.equalsIgnoreCase("[INFO]") && !aDataRow.equalsIgnoreCase("[COLORS]")) {
                    String[] aData = aDataRow.split("=");
                    if (aData.length < 2) {
                        MainActivity.ImportUrl = null;
                        Toast.makeText(mContext, "Import failed...\nCorrupted or invalid preset!\nCause by: Unknown string split length: "+aData.length, Toast.LENGTH_LONG).show();
                        return false;//presetInfo[1] = mContext.getString(R.string.presetsManager_Creator).replace("[CREATORNAME]",aData[1]);
                    }
                    if (aData[0].equalsIgnoreCase("Creator")) {
                        preset.setDescription(aData[1]);
                    }
                }
            }
            final slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
            dialogFragment.setContext(mContext);
            dialogFragment.setFragmentManager(MainActivity.fragmentManager);
            dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

                @Override
                public void onListItemClick(int position, String text) {

                }

                @Override
                public void onNegativeClick() {

                    Importcancled = true;
                }

                @Override
                public void onNeutralClick() {

                }

                @Override
                public void onPositiveClick(Bundle resultBundle) {

                    File prefile = new File(newUrl);
                    String newFilename = resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0") + ".nps";
                    preset.setName(newFilename.replace(".nps", ""));
                    final boolean newPresetAdded;
                    newPresetAdded = !new File(mContext.getFilesDir().getPath() + "/presets/" + newFilename).exists();
                    String FilePath = "";
                    if (isZip) {
                        helper.unzipFile(newUrl, mContext.getFilesDir().getPath() + "/temp/", Filename, null);
                        FilePath = newUrl;
                    } else {
                        FilePath = prefile.getPath();
                    }
                    if (helper.copyFile(FilePath, mContext.getFilesDir().getPath() + "/presets/" + newFilename)) {
                        new File(mContext.getFilesDir().getPath() + "/temp/" + Filename).renameTo(new File(mContext.getFilesDir().getPath() + "/temp/" + newFilename));
                        if (isZip) {
                            helper.removeFromZip(mContext.getFilesDir().getPath() + "/presets/" + newFilename, Filename, null);
                            helper.zipFile(mContext.getFilesDir().getPath() + "/temp/" + newFilename, mContext.getFilesDir().getPath() + "/presets/" + newFilename, null);
                        }
                        File presetsFolder = new File(mContext.getFilesDir().getPath() + "/temp/");
                        File[] presetsFiles = presetsFolder.listFiles(new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                return true;
                            }
                        });
                        for (int i = 0; i < presetsFiles.length; i++) {
                            presetsFiles[i].delete();
                        }
                        if (newPresetAdded) {
                            adapter.insert(preset);
                        }
                        Toast.makeText(mContext, mContext.getString(R.string.presetsManager_ImportSuccess).replace("[PRESETNAME]", newFilename.replace(".nps", "")), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("NPM", "Import failed!\nCan't move file.");
                    }
                    //new ImportPreset().execute(newUrl, Filename, adapter);
                }

                @Override
                public void onTouchOutside() {

                    Importcancled = true;
                }
            });
            dialogFragment.setText(mContext.getString(R.string.presetsManager_Creator).replace("[CREATORNAME]", (creator != null && !creator.isEmpty()) ? creator : preset.getDescription()) + "\n\n" + mContext.getString(R.string.presetsManager_ImportMsg));
            dialogFragment.addInput(mContext.getString(R.string.presetSaveDialog_InfoText), Filename.replace(".nps", ""), false, new TextWatcher() {

                        @Override
                        public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {

                        }

                        @Override
                        public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {

                            if (!p1.toString().equalsIgnoreCase("")) {
                                File checkFile = new File(mContext.getFilesDir() + "/presets/" + p1.toString().replace("/", "") + ".nps");
                                if (!checkFile.exists()) {
                                    dialogFragment.showAssistInfo(false);
                                    //dialogFragment.setDialogText("");
                                } else {
                                    dialogFragment.showAssistInfo(true);
                                    //dialogFragment.setDialogText(context.getString(R.string.presetSaveDialog_OverwriteText));
                                }
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable p1) {

                        }
                    }
            );
            dialogFragment.setInputAssistInfo(mContext.getString(R.string.presetSaveDialog_OverwriteText));
            dialogFragment.setNegativeButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[4]);
            dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[7]);
            dialogFragment.showDialog(R.id.dialog_container);

        } catch (Throwable e) {
            Log.e("NPM", "Import failed!\n" + e.toString());
        }
        MainActivity.ImportUrl = null;
        return !Importcancled;
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
            PresetsPage page = new PresetsPage();
            Bundle pageBundle = new Bundle();
            switch (position) {
                case 0:
                    return new LoginFragment();
                case 1: // Fragment # 0 - This will show FirstFragment
                    pageBundle.putInt("page", 0);
                    pageBundle.putString("title", "Local");
                    page.setArguments(pageBundle);
                    return page;
                case 2: // Fragment # 0 - This will show FirstFragment different title
                    pageBundle.putInt("page", 1);
                    pageBundle.putString("title", "Online");
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

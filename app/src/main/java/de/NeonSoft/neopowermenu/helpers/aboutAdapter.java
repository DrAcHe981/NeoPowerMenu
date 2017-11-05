package de.NeonSoft.neopowermenu.helpers;

import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.app.FragmentTransaction;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import de.NeonSoft.neopowermenu.*;

import java.util.*;

import com.nostra13.universalimageloader.utils.*;

import java.io.*;

import de.NeonSoft.neopowermenu.Preferences.*;
import de.NeonSoft.neopowermenu.tour.tourFragment;

import android.view.animation.*;

public class aboutAdapter extends ArrayAdapter<String> {
    private Activity mContext;

    private ArrayList<String> itemsTitles;
    private ArrayList<String> itemsTexts;

    private int IdClickCount = 20;
    private Toast lastToast;
    private Timer timer = new Timer();
    private boolean timerRunning = false;

    public aboutAdapter(Activity context, ArrayList<String> itemTitles, ArrayList<String> itemTexts) {
        super(context, R.layout.aboutlistitem, itemTitles);
        this.mContext = context;
        this.itemsTitles = itemTitles;
        this.itemsTexts = itemTexts;
    }

    @Override
    public View getView(final int p1, View p2, ViewGroup p3) {

        View InflatedView = mContext.getLayoutInflater().inflate(R.layout.aboutlistitem, p3, false);

        LinearLayout root = (LinearLayout) InflatedView.findViewById(R.id.aboutlistitemLinearLayout);
        TextView Title = (TextView) InflatedView.findViewById(R.id.aboutlistitemTextView1);
        TextView Text = (TextView) InflatedView.findViewById(R.id.aboutlistitemTextView2);

        Title.setText(this.itemsTitles.get(p1));
        Text.setText(this.itemsTexts.get(p1));
        if (this.itemsTitles.get(p1).contains(mContext.getString(R.string.tourReview_Title))) {
            Text.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.changePrefPage(new PreferencesPartFragment(), false);
                    MainActivity.fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).add(R.id.dialog_container, new tourFragment(), "tour").commit();
                }
            });
        } else if (this.itemsTitles.get(p1).equalsIgnoreCase("Force Language")) {
            Text.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ArrayList<String> languages = new ArrayList<String>();
                    languages.add("Default (System)");
                    Locale myLocale = new Locale("en");
                    languages.add(myLocale.getDisplayName() + " (" + myLocale.getLanguage() + (myLocale.getCountry().isEmpty() ? "" : "_" + myLocale.getCountry().toUpperCase()) + ")");
                    int currentLanguageSel = -1;
                    DisplayMetrics metrics = new DisplayMetrics();
                    mContext.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    Resources r = mContext.getResources();
                    Configuration c = r.getConfiguration();
                    Locale oldLocale = c.locale;
                    Locale[] loc = Locale.getAvailableLocales();
                    //String[] loc = r.getAssets().getLocales();
                    for (int i = 0; i < loc.length; i++) {
                        try {
                            c.locale = new Locale(loc[i].getLanguage() + (loc[i].getCountry().isEmpty() ? "" : "_" + loc[i].getCountry().toUpperCase()));
                            Resources res = new Resources(mContext.getAssets(), metrics, c);
                            String s1 = res.getString(R.string.powerMenuMain_Shutdown);

                            c.locale = new Locale("");
                            Resources res2 = new Resources(mContext.getAssets(), metrics, c);
                            String s2 = res2.getString(R.string.powerMenuMain_Shutdown);

                            if (!s1.equals(s2)) {
                                //Locale locale = new Locale(loc[i].getLanguage());
                                languages.add(loc[i].getDisplayName() + " (" + loc[i].getLanguage() + (loc[i].getCountry().isEmpty() ? "" : "_" + loc[i].getCountry().toUpperCase()) + ")");
                            }
                        } catch (Throwable t) {
                        }
                    }
                    Resources res = mContext.getResources();
                    DisplayMetrics dm = res.getDisplayMetrics();
                    Configuration conf = res.getConfiguration();
                    conf.locale = oldLocale;
                    res.updateConfiguration(conf, dm);
                    Collections.sort(languages, new Comparator<String>() {
                        @Override
                        public int compare(String p1, String p2) {
                            return p1.compareToIgnoreCase(p2);
                        }
                    });
                    for (int i = 0; i < languages.size(); i++) {
                        if (languages.get(i).split("\\(")[1].split("\\)")[0].equals(MainActivity.ForcedLanguage)) {
                            currentLanguageSel = i;
                        }
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
                            if (resultBundle != null) {
                                MainActivity.preferences.edit().putString("ForcedLanguage", languages.get(resultBundle.getInt(slideDownDialogFragment.RESULT_LIST)).split("\\(")[1].split("\\)")[0]).commit();
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
                                        AboutFragment.LoadingLayout.setVisibility(View.VISIBLE);
                                        AboutFragment.LoadingLayout.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
                                        new Handler().postDelayed(new Runnable() {

                                            @Override
                                            public void run() {
                                                mContext.runOnUiThread(new Runnable() {

                                                    @Override
                                                    public void run() {

                                                        mContext.recreate();
                                                    }
                                                });
                                            }
                                        }, 1000L);

                                    }

                                    @Override
                                    public void onTouchOutside() {

                                    }
                                });
                                dialogFragment.setCloseOnTouchOutside(false);
                                dialogFragment.setText("Restart the app to let the change take effect.");
                                dialogFragment.setPositiveButton("Restart");
                                dialogFragment.showDialog(R.id.dialog_container);
                            }
                        }

                        @Override
                        public void onTouchOutside() {

                        }
                    });
                    dialogFragment.setList(ListView.CHOICE_MODE_SINGLE, languages, currentLanguageSel, false);
                    dialogFragment.setNegativeButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                    dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_OK]);
                    dialogFragment.showDialog(R.id.dialog_container);
                }
            });
        } else if (this.itemsTexts.get(p1).contains("This Project uses some public libraries")) {
            Text.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View p1) {

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
                            if (resultBundle != null) {
                                MainActivity.preferences.edit().putBoolean("useLocalServer", resultBundle.getBoolean(slideDownDialogFragment.RESULT_CHECKBOX)).commit();
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
                                        AboutFragment.LoadingLayout.setVisibility(View.VISIBLE);
                                        AboutFragment.LoadingLayout.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
                                        new Handler().postDelayed(new Runnable() {

                                            @Override
                                            public void run() {
                                                mContext.runOnUiThread(new Runnable() {

                                                    @Override
                                                    public void run() {

                                                        mContext.recreate();
                                                    }
                                                });
                                            }
                                        }, 1000L);

                                    }

                                    @Override
                                    public void onTouchOutside() {

                                    }
                                });
                                dialogFragment.setCloseOnTouchOutside(false);
                                dialogFragment.setText("Restart the app to let the change take effect.");
                                dialogFragment.setPositiveButton("Restart");
                                dialogFragment.showDialog(R.id.dialog_container);
                            }
                        }

                        @Override
                        public void onTouchOutside() {

                        }
                    });
                    dialogFragment.setText("Disable or enable the use of the local server via the checkbox below.\nLocal means 127.0.0.1, online means www.Neon-Soft.de");
                    dialogFragment.setCheckBox("Use local server", MainActivity.preferences.getBoolean("useLocalServer", false));
                    dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_SAVE]);
                    dialogFragment.showDialog(R.id.dialog_container);
                }
            });

        } else if (this.itemsTexts.get(p1).contains("Your Device Id")) {
            Text.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (IdClickCount > 1) {
                        if (lastToast != null) lastToast.cancel();
                        IdClickCount--;
                        if (IdClickCount > 10) {
                            try {
                                String cpm = "";
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                    ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(mContext.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("NPM_deviceId", MainActivity.deviceUniqeId);
                                    clipboard.setPrimaryClip(clip);
                                    cpm = clipboard.getPrimaryClip().getItemAt(0).getText().toString();
                                } else {
                                    //noinspection deprecation
                                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) mContext.getSystemService(mContext.CLIPBOARD_SERVICE);
                                    clipboard.setText(MainActivity.deviceUniqeId);
                                    cpm = clipboard.getText().toString();
                                }
                                if (cpm.equals(MainActivity.deviceUniqeId)) {
                                    lastToast = Toast.makeText(mContext, MainActivity.deviceUniqeId + " copied to clipboard.", Toast.LENGTH_SHORT);
                                    lastToast.show();
                                } else {
                                    lastToast = Toast.makeText(mContext, "Failed to put to clipboard...", Toast.LENGTH_SHORT);
                                    lastToast.show();
                                }
                            } catch (Throwable t) {
                                Log.e("NPM", "Failed to put in clipboard: " + t.toString());
                                lastToast = Toast.makeText(mContext, "Failed to put to clipboard...", Toast.LENGTH_SHORT);
                                lastToast.show();
                            }
                        } else {
                            lastToast = Toast.makeText(mContext, "Click " + IdClickCount + " times to edit your deviceId.\nBe carefull with this!", Toast.LENGTH_LONG);
                            lastToast.show();
                        }
                                                        /*if(timerRunning) timer.cancel();
                                                        timerRunning = true;
														timer.schedule(new TimerTask() {

																		@Override
																		public void run()
																		{
																				timerRunning = false;
																				IdClickCount = 0;
																				//Toast.makeText(mContext,"idClickCount reset",Toast.LENGTH_SHORT).show();
																		}
																}, 1000);*/
                    } else {
                        IdClickCount = 20;
                        final slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
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
                                MainActivity.deviceUniqeId = (resultBundle.getBoolean(slideDownDialogFragment.RESULT_CHECKBOX) ? helper.md5Crypto(resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0")) : resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0"));
                                MainActivity.preferences.edit().putString("userUniqeId", MainActivity.deviceUniqeId).commit();
                                itemsTexts.set(p1, "Your Device Id:\n" + ((MainActivity.deviceUniqeId.isEmpty() || MainActivity.deviceUniqeId.equalsIgnoreCase("none")) ? "Not generated. (this is not normal...)" : MainActivity.deviceUniqeId) + "\nYour Account Id:\n" + ((MainActivity.accountUniqeId.isEmpty() || MainActivity.accountUniqeId.equalsIgnoreCase("none")) ? "Not logged in." : MainActivity.accountUniqeId) + "\nThe Id's are used by the Preset Sever to verify your identity.");
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onTouchOutside() {

                            }
                        });
                        dialogFragment.setCloseOnTouchOutside(false);
                        dialogFragment.setText("Use this at your own risk!\nThis is to change your device Id, when changing this keep in mind that your uploaded presets won't be 'yours' anymore, if not logged in with an account!");
                        dialogFragment.addInput("Device Id:", MainActivity.deviceUniqeId, false, null);
                        dialogFragment.setCheckBox("Encrypt with md5", true);
                        dialogFragment.setNegativeButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[4]);
                        //dialogFragment.setDialogNeutralButton("Reset");
                        dialogFragment.setPositiveButton(mContext.getString(R.string.Dialog_Buttons).split("\\|")[7]);
                        dialogFragment.showDialog(R.id.dialog_container);
                    }
                }
            });
            Text.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View p1) {

                    try {
                        String cpm = "";
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(mContext.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("NPM_deviceId", MainActivity.accountUniqeId);
                            clipboard.setPrimaryClip(clip);
                            cpm = clipboard.getPrimaryClip().getItemAt(0).getText().toString();
                        } else {
                            //noinspection deprecation
                            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) mContext.getSystemService(mContext.CLIPBOARD_SERVICE);
                            clipboard.setText(MainActivity.accountUniqeId);
                            cpm = clipboard.getText().toString();
                        }
                        if (cpm.equals(MainActivity.accountUniqeId)) {
                            Toast.makeText(mContext, MainActivity.accountUniqeId + " copied to clipboard.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "Failed to put to clipboard...", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Throwable t) {
                        Log.e("NPM", "Failed to put in clipboard: " + t.toString());
                        Toast.makeText(mContext, "Failed to put to clipboard...", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
        } else {
            root.setClickable(false);
            root.setEnabled(false);
        }

        return InflatedView;
    }

}

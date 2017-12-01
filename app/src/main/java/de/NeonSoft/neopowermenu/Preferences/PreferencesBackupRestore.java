package de.NeonSoft.neopowermenu.Preferences;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ThemeInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipFile;

import de.NeonSoft.neopowermenu.MainActivity;
import de.NeonSoft.neopowermenu.R;
import de.NeonSoft.neopowermenu.helpers.SettingsManager;
import de.NeonSoft.neopowermenu.helpers.helper;
import de.NeonSoft.neopowermenu.helpers.slideDownDialogFragment;

public class PreferencesBackupRestore extends Fragment {

    Activity mActivity;

    ArrayList<String> backupRestoreOptions = new ArrayList<>();

    LinearLayout LinearLayout_NewBackup;

    LinearLayout LinearLayout_Restore;

    LinearLayout LinearLayout_Delete;

    private int SELECT_BACKUP_RESULT = 1998;

    RelativeLayout RelativeLayout_Progress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mActivity = getActivity();
        MainActivity.visibleFragment = "BackupRestore";

        backupRestoreOptions.add(getString(R.string.backupRestoreOptions_MainPreferences));
        backupRestoreOptions.add(getString(R.string.backupRestoreOptions_Colors));
        backupRestoreOptions.add(getString(R.string.backupRestoreOptions_Presets));
        backupRestoreOptions.add(getString(R.string.backupRestoreOptions_Graphics));
        backupRestoreOptions.add(getString(R.string.backupRestoreOptions_VisibilityAndOrder));
        backupRestoreOptions.add(getString(R.string.backupRestoreOptions_Animations));

        View InflatedView = inflater.inflate(R.layout.activity_backuprestore, container, false);

        LinearLayout_NewBackup = (LinearLayout) InflatedView.findViewById(R.id.activitybackuprestoreLinearLayout_NewBackup);

        LinearLayout_NewBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                dialogFragment.setContext(mActivity);
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
                        Calendar date = Calendar.getInstance();
                        String backupPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
                        String backupName = resultBundle.getString(slideDownDialogFragment.RESULT_INPUT + "0", "");
                        if (backupName.isEmpty())
                            backupName = "NPM_Backup_" + String.format(Locale.getDefault(), "%02d-%02d-%4d", date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.MONTH), date.get(Calendar.YEAR)) + "_" + String.format(Locale.getDefault(), "%02d-%02d-%02d", date.get(Calendar.HOUR), date.get(Calendar.MINUTE), date.get(Calendar.SECOND));
                        backupName += ".npmb";
                        String backupOptions = resultBundle.getString(slideDownDialogFragment.RESULT_LIST, "");

                        helper.startAsyncTask(new createBackup(),backupPath, backupName, backupOptions);
                    }

                    @Override
                    public void onTouchOutside() {

                    }
                });
                dialogFragment.setText(getString(R.string.backupRestore_ChooseBackupOptions));
                dialogFragment.addInput(getString(R.string.backupRestore_ChooseBackupName), "", true, null);
                dialogFragment.setList(ListView.CHOICE_MODE_MULTIPLE, backupRestoreOptions, -1, false);
                dialogFragment.setNegativeButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_OK]);
                dialogFragment.showDialog(R.id.dialog_container);
            }
        });

        LinearLayout_Restore = (LinearLayout) InflatedView.findViewById(R.id.activitybackuprestoreLinearLayout_Restore);

        LinearLayout_Restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Backup"), SELECT_BACKUP_RESULT);
            }
        });

        LinearLayout_Delete = (LinearLayout) InflatedView.findViewById(R.id.activitybackuprestoreLinearLayout_Delete);

        LinearLayout_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                dialogFragment.setContext(mActivity);
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
                        String deleteOptions = resultBundle.getString(slideDownDialogFragment.RESULT_LIST, "");

                        helper.startAsyncTask(new deleteFiles(),deleteOptions);
                    }

                    @Override
                    public void onTouchOutside() {

                    }
                });
                dialogFragment.setText(getString(R.string.backupRestore_ChooseDeleteOptions));
                dialogFragment.setList(ListView.CHOICE_MODE_MULTIPLE, backupRestoreOptions, -1, false);
                dialogFragment.setNegativeButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_DELETE]);
                dialogFragment.showDialog(R.id.dialog_container);
            }
        });
        RelativeLayout_Progress = (RelativeLayout) InflatedView.findViewById(R.id.activitybackuprestoreRelativeLayout_Progress);
        RelativeLayout_Progress.setVisibility(View.GONE);

        return InflatedView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_BACKUP_RESULT) {
            if (resultCode == mActivity.RESULT_OK) {
                String restorePath = data.getData().getLastPathSegment();
                if (restorePath.startsWith("primary:"))
                    restorePath = restorePath.replace("primary:", Environment.getExternalStorageDirectory().getAbsolutePath() + "/");
                final String finalPath = restorePath;
                if (MainActivity.DeepLogging)
                    Log.d("NPM", "Checking file: " + restorePath);
                if (restorePath.endsWith(".npmb") && helper.isValidZip(restorePath, null)) {
                    final slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                    dialogFragment.setContext(mActivity);
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
                            String restoreOptions = resultBundle.getString(slideDownDialogFragment.RESULT_LIST, "");

                            helper.startAsyncTask(new restoreBackup(),finalPath, restoreOptions);
                        }

                        @Override
                        public void onTouchOutside() {

                        }
                    });
                    dialogFragment.setText(getString(R.string.backupRestore_ChooseRestoreOptions));
                    dialogFragment.setList(ListView.CHOICE_MODE_MULTIPLE, backupRestoreOptions, -1, false);
                    dialogFragment.setNegativeButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_CANCEL]);
                    dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_OK]);
                    dialogFragment.showDialog(R.id.dialog_container);
                } else {
                    final slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                    dialogFragment.setContext(mActivity);
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
                        }

                        @Override
                        public void onTouchOutside() {

                        }
                    });
                    dialogFragment.setText(getString(R.string.backupRestore_RestoreFailedUnknownFile));
                    dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_OK]);
                    dialogFragment.showDialog(R.id.dialog_container);
                }
            }
        }
    }

    class createBackup extends AsyncTask<Object, String, String> {

        private String backupPath;
        private String backupName;
        private String[] backupOptions;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            RelativeLayout_Progress.setVisibility(View.VISIBLE);
            RelativeLayout_Progress.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.fade_in));
        }

        @Override
        protected String doInBackground(Object... params) {
            backupPath = params[0].toString();
            backupName = params[1].toString();
            backupOptions = params[2].toString().split(",");

            PackageManager m = mActivity.getPackageManager();
            String s = MainActivity.class.getPackage().getName();
            try {
                PackageInfo p = m.getPackageInfo(s, 0);
                s = p.applicationInfo.dataDir;
            } catch (PackageManager.NameNotFoundException e) {
                Log.w("NPM", "Error Package name not found ", e);
            }

            if (MainActivity.DeepLogging) {
                Log.d("NPM", "Backing up to: " + backupPath + backupName);
                Log.d("NPM", "Backing up parts: " + params[2]);
            }

            try {
                for (String backupOption : backupOptions) {
                    if (Integer.parseInt(backupOption) == 0) {
                        if (MainActivity.DeepLogging)
                            Log.d("NPM", "Adding main settings...");
                        helper.zipFile(SettingsManager.getSettingsFile(MainActivity.context), backupPath + backupName, null);
                    } else if (Integer.parseInt(backupOption) == 1) {
                        if (MainActivity.DeepLogging)
                            Log.d("NPM", "Adding color settings...");
                        helper.zipFile(s + "/shared_prefs/colors.xml", backupPath + backupName, null);
                    } else if (Integer.parseInt(backupOption) == 2) {
                        if (MainActivity.DeepLogging) Log.d("NPM", "Adding presets...");
                        helper.zipAll(mActivity.getFilesDir().getPath() + "/presets/", backupPath + backupName, null);
                    } else if (Integer.parseInt(backupOption) == 3) {
                        if (MainActivity.DeepLogging) Log.d("NPM", "Adding images...");
                        helper.zipAll(mActivity.getFilesDir().getPath() + "/images/", backupPath + backupName, null);
                    } else if (Integer.parseInt(backupOption) == 4) {
                        if (MainActivity.DeepLogging)
                            Log.d("NPM", "Adding visibilityOrder settings...");
                        helper.zipFile(s + "/shared_prefs/visibilityOrder.xml", backupPath + backupName, null);
                    } else if (Integer.parseInt(backupOption) == 5) {
                        if (MainActivity.DeepLogging)
                            Log.d("NPM", "Adding animation settings...");
                        helper.zipFile(s + "/shared_prefs/animations.xml", backupPath + backupName, null);
                    }
                }
            } catch (Throwable t) {
                return t.toString();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            RelativeLayout_Progress.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.fade_out));
            RelativeLayout_Progress.setVisibility(View.GONE);
            if (MainActivity.DeepLogging) Log.d("NPM", "Checking file...");
            if (s == null && helper.isValidZip(backupPath + backupName, null)) {
                Toast.makeText(mActivity, getString(R.string.backupRestore_BackupComplete), Toast.LENGTH_SHORT).show();
            } else {
                Log.e("NPM", "Failed to backup: " + s);
                Toast.makeText(mActivity, getString(R.string.backupRestore_BackupFailed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    class restoreBackup extends AsyncTask<Object, String, String> {

        String path = "";
        String[] restoreOptions;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            RelativeLayout_Progress.setVisibility(View.VISIBLE);
            RelativeLayout_Progress.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.fade_in));
        }

        @Override
        protected String doInBackground(Object... params) {
            path = params[0].toString();
            restoreOptions = params[1].toString().split(",");
            PackageManager m = mActivity.getPackageManager();
            String s = MainActivity.class.getPackage().getName();
            try {
                PackageInfo p = m.getPackageInfo(s, 0);
                s = p.applicationInfo.dataDir;
            } catch (PackageManager.NameNotFoundException e) {
                Log.w("NPM", "Error Package name not found ", e);
            }

            if (MainActivity.DeepLogging) {
                Log.d("NPM", "Restoring from: " + path);
                Log.d("NPM", "Restoring to: " + SettingsManager.getSettingsFile(MainActivity.context));
                Log.d("NPM", "Restoring parts: " + params[1]);
            }

            try {
                File folder = new File(mActivity.getFilesDir().getPath() + "/temp/");

                File[] oldTempFiles = folder.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return true;
                    }
                });
                for (java.io.File file : oldTempFiles) {
                    file.delete();
                }

                helper.unzipAll(path, mActivity.getFilesDir() + "/temp/", null);

                for (String option : restoreOptions) {
                    if (Integer.parseInt(option) == 0) {
                        if (MainActivity.DeepLogging) Log.d("NPM", "Restoring main settings...");
                        new File(SettingsManager.getSettingsFile(MainActivity.context)).delete();
                        if (!new File(mActivity.getFilesDir().getPath() + "/temp/" + MainActivity.class.getPackage().getName() + "_preferences.xml").renameTo(new File(SettingsManager.getSettingsFile(MainActivity.context)))) {
                            Log.e("NPM", "Failed to restore main settings!");
                        }
                    } else if (Integer.parseInt(option) == 1) {
                        if (MainActivity.DeepLogging)
                            Log.d("NPM", "Restoring color settings...");
                        new File(s + "/shared_prefs/colors.xml").delete();
                        if (!new File(mActivity.getFilesDir().getPath() + "/temp/colors.xml").renameTo(new File(s + "/shared_prefs/colors.xml"))) {
                            Log.e("NPM", "Failed to restore color settings!");
                        }
                    } else if (Integer.parseInt(option) == 2) {
                        if (MainActivity.DeepLogging) Log.d("NPM", "Restoring presets...");
                        File[] oldPresetFiles = new File(mActivity.getFilesDir().getPath() + "/presets/").listFiles(new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                return name.endsWith(".nps");
                            }
                        });
                        for (java.io.File file : oldPresetFiles) {
                            file.delete();
                        }

                        File[] presetFiles = folder.listFiles(new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                return name.endsWith(".nps");
                            }
                        });
                        for (java.io.File file : presetFiles) {
                            file.renameTo(new File(mActivity.getFilesDir().getPath() + "/presets/" + file.getName()));
                        }
                    } else if (Integer.parseInt(option) == 3) {
                        if (MainActivity.DeepLogging) Log.d("NPM", "Restoring graphics...");
                        MainActivity.imageLoader.clearMemoryCache();
                        MainActivity.imageLoader.clearDiskCache();
                        File[] oldGraphicsFiles = new File(mActivity.getFilesDir().getPath() + "/images/").listFiles(new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                return name.endsWith(".png");
                            }
                        });
                        for (java.io.File file : oldGraphicsFiles) {
                            file.delete();
                        }

                        File[] graphicFiles = folder.listFiles(new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                return name.endsWith(".png");
                            }
                        });
                        for (java.io.File file : graphicFiles) {
                            file.renameTo(new File(mActivity.getFilesDir().getPath() + "/images/" + file.getName()));
                        }
                    } else if (Integer.parseInt(option) == 4) {
                        if (MainActivity.DeepLogging)
                            Log.d("NPM", "Restoring visibilityOrder settings...");
                        new File(s + "/shared_prefs/visibilityOrder.xml").delete();
                        if (!new File(mActivity.getFilesDir().getPath() + "/temp/visibilityOrder.xml").renameTo(new File(s + "/shared_prefs/visibilityOrder.xml"))) {
                            Log.e("NPM", "Failed to restore visibilityOrder settings!");
                        }
                    } else if (Integer.parseInt(option) == 5) {
                        if (MainActivity.DeepLogging)
                            Log.d("NPM", "Restoring animation settings...");
                        new File(s + "/shared_prefs/animations.xml").delete();
                        if (!new File(mActivity.getFilesDir().getPath() + "/temp/animations.xml").renameTo(new File(s + "/shared_prefs/animations.xml"))) {
                            Log.e("NPM", "Failed to restore animation settings!");
                        }
                    }
                }

                File[] tempFiles = folder.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return true;
                    }
                });
                for (java.io.File file : tempFiles) {
                    file.delete();
                }

            } catch (Throwable t) {
                return t.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            RelativeLayout_Progress.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.fade_out));
            RelativeLayout_Progress.setVisibility(View.GONE);
            if (s == null) {
                slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                dialogFragment.setContext(mActivity);
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
                        RelativeLayout_Progress.setVisibility(View.VISIBLE);
                        RelativeLayout_Progress.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.fade_in));
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                mActivity.runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        Intent mStartActivity = new Intent(mActivity, MainActivity.class);
                                        int mPendingIntentId = 123456;
                                        PendingIntent mPendingIntent = PendingIntent.getActivity(mActivity, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                                        AlarmManager mgr = (AlarmManager) mActivity.getSystemService(mActivity.ALARM_SERVICE);
                                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 10, mPendingIntent);
                                        System.exit(0);
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
                dialogFragment.setText(getString(R.string.backupRestore_RestoreComplete));
                dialogFragment.setPositiveButton(getString(R.string.backupRestore_Restart));
                dialogFragment.showDialog(R.id.dialog_container);
            } else {
                Log.e("NPM", "Failed to restore: " + s);
                slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                dialogFragment.setContext(mActivity);
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
                    }

                    @Override
                    public void onTouchOutside() {

                    }
                });
                dialogFragment.setCloseOnTouchOutside(false);
                dialogFragment.setText(getString(R.string.backupRestore_RestoreFailed));
                dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_OK]);
                dialogFragment.showDialog(R.id.dialog_container);
            }
        }
    }

    class deleteFiles extends AsyncTask<Object, String, String> {

        String[] deleteOptions;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            RelativeLayout_Progress.setVisibility(View.VISIBLE);
            RelativeLayout_Progress.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.fade_in));
        }

        @Override
        protected String doInBackground(Object... params) {
            deleteOptions = params[0].toString().split(",");

            PackageManager m = mActivity.getPackageManager();
            String s = MainActivity.class.getPackage().getName();
            try {
                PackageInfo p = m.getPackageInfo(s, 0);
                s = p.applicationInfo.dataDir;
            } catch (PackageManager.NameNotFoundException e) {
                Log.w("NPM", "Error Package name not found ", e);
            }

            if (MainActivity.DeepLogging) {
                Log.d("NPM", "Deleting parts: " + params[0]);
            }
            try {
                for (String option : deleteOptions) {
                    if (Integer.parseInt(option) == 0) {
                        if (MainActivity.DeepLogging) Log.d("NPM", "Deleting main settings...");
                        new File(s + "/shared_prefs/" + MainActivity.class.getPackage().getName() + "_preferences.xml").delete();
                        new File(SettingsManager.getSettingsFile(MainActivity.context)).delete();
                    } else if (Integer.parseInt(option) == 1) {
                        if (MainActivity.DeepLogging)
                            Log.d("NPM", "Deleting color settings...");
                        new File(s + "/shared_prefs/colors.xml").delete();
                    } else if (Integer.parseInt(option) == 2) {
                        if (MainActivity.DeepLogging) Log.d("NPM", "Deleting presets...");
                        File[] oldPresetFiles = new File(mActivity.getFilesDir().getPath() + "/presets/").listFiles(new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                return name.endsWith(".nps");
                            }
                        });
                        for (java.io.File file : oldPresetFiles) {
                            file.delete();
                        }
                    } else if (Integer.parseInt(option) == 3) {
                        if (MainActivity.DeepLogging) Log.d("NPM", "Deleting graphics...");
                        MainActivity.imageLoader.clearMemoryCache();
                        MainActivity.imageLoader.clearDiskCache();
                        File[] oldGraphicsFiles = new File(mActivity.getFilesDir().getPath() + "/images/").listFiles(new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                return name.endsWith(".png");
                            }
                        });
                        for (java.io.File file : oldGraphicsFiles) {
                            file.delete();
                        }
                    } else if (Integer.parseInt(option) == 4) {
                        if (MainActivity.DeepLogging)
                            Log.d("NPM", "Deleting visibilityOrder settings...");
                        new File(s + "/shared_prefs/visibilityOrder.xml").delete();
                    } else if (Integer.parseInt(option) == 5) {
                        if (MainActivity.DeepLogging)
                            Log.d("NPM", "Deleting animation settings...");
                        new File(s + "/shared_prefs/animations.xml").delete();
                    }
                }
            } catch (Throwable t) {
                return t.toString();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            RelativeLayout_Progress.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.fade_out));
            RelativeLayout_Progress.setVisibility(View.GONE);
            if (s == null) {
                slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                dialogFragment.setContext(mActivity);
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
                        RelativeLayout_Progress.setVisibility(View.VISIBLE);
                        RelativeLayout_Progress.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.fade_in));
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                mActivity.runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        Intent mStartActivity = new Intent(mActivity, MainActivity.class);
                                        int mPendingIntentId = 123456;
                                        PendingIntent mPendingIntent = PendingIntent.getActivity(mActivity, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                                        AlarmManager mgr = (AlarmManager) mActivity.getSystemService(mActivity.ALARM_SERVICE);
                                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 10, mPendingIntent);
                                        System.exit(0);
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
                dialogFragment.setText(getString(R.string.backupRestore_DeleteComplete));
                dialogFragment.setPositiveButton(getString(R.string.backupRestore_Restart));
                dialogFragment.showDialog(R.id.dialog_container);
            } else {
                Log.e("NPM", "Failed to delete: " + s);
                slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                dialogFragment.setContext(mActivity);
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
                    }

                    @Override
                    public void onTouchOutside() {

                    }
                });
                dialogFragment.setCloseOnTouchOutside(false);
                dialogFragment.setText(getString(R.string.backupRestore_DeleteFailed));
                dialogFragment.setPositiveButton(getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_OK]);
                dialogFragment.showDialog(R.id.dialog_container);
            }
        }
    }

}

package de.NeonSoft.neopowermenu;

import android.app.*;
import android.content.*;
import android.os.*;
import android.text.InputType;
import android.util.Log;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.widget.*;

import cat.ereza.customactivityoncrash.*;
import de.NeonSoft.neopowermenu.helpers.*;

import android.support.v4.app.Fragment;

import org.acra.ACRA;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;

public class errorFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View InflatedView = inflater.inflate(de.NeonSoft.neopowermenu.R.layout.customactivityoncrash_error_fragment, container, false);

        //Close/restart button logic:
        //If a class if set, use restart.
        //Else, use close and just finish the app.
        //It is recommended that you follow this logic if implementing a custom error activity.
        Button restartButton = (Button) InflatedView.findViewById(de.NeonSoft.neopowermenu.R.id.customactivityoncrash_error_activity_restart_button);

        final Class<? extends Activity> restartActivityClass = CustomActivityOnCrash.getRestartActivityClassFromIntent(ErrorActivity.thisActivity.getIntent());
        final CustomActivityOnCrash.EventListener eventListener = CustomActivityOnCrash.getEventListenerFromIntent(ErrorActivity.thisActivity.getIntent());

        if (restartActivityClass != null) {
            restartButton.setText(R.string.errorActivity_RestartApp);
            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ACRA.getErrorReporter().putCustomData("User_Info",UserInput.getText().toString());
                    //ACRA.getErrorReporter().handleException(new Throwable(CustomActivityOnCrash.getStackTraceFromIntent(ErrorActivity.thisActivity.getIntent())),false);
                    Intent intent = new Intent(ErrorActivity.thisActivity, restartActivityClass);
                    CustomActivityOnCrash.restartApplicationWithIntent(ErrorActivity.thisActivity, intent, eventListener);
                }
            });
        } else {
            restartButton.setText(R.string.errorActivity_CloseApp);
            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ACRA.getErrorReporter().putCustomData("User_Info",UserInput.getText().toString());
                    //ACRA.getErrorReporter().handleException(new Throwable(CustomActivityOnCrash.getStackTraceFromIntent(ErrorActivity.thisActivity.getIntent())),false);
                    CustomActivityOnCrash.closeApplication(ErrorActivity.thisActivity, eventListener);
                }
            });
        }

        final Button moreInfoButton = (Button) InflatedView.findViewById(de.NeonSoft.neopowermenu.R.id.customactivityoncrash_error_activity_more_info_button);

        if (CustomActivityOnCrash.isShowErrorDetailsFromIntent(ErrorActivity.thisActivity.getIntent())) {
            moreInfoButton.setText(getString(R.string.errorActivity_MoreInfo));
            moreInfoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //We retrieve all the error data and show it

                    slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                    dialogFragment.setContext(getActivity());
                    dialogFragment.setFragmentManager(ErrorActivity.fragmentManager);
                    dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

                        @Override
                        public void onListItemClick(int position, String text) {
                        }

                        @Override
                        public void onNegativeClick() {
                        }

                        @Override
                        public void onNeutralClick() {
                            copyErrorToClipboard();
                            Toast.makeText(ErrorActivity.thisActivity, R.string.errorActivity_Copyied, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPositiveClick(Bundle resultBundle) {
                        }

                        @Override
                        public void onTouchOutside() {
                        }
                    });
                    dialogFragment.setText(CustomActivityOnCrash.getStackTraceFromIntent(ErrorActivity.thisActivity.getIntent()));
                    dialogFragment.setNeutralButton(getString(R.string.errorActivity_CopyToClip));
                    dialogFragment.setPositiveButton(getString(R.string.errorActivity_Close));
                    dialogFragment.showDialog(de.NeonSoft.neopowermenu.R.id.dialog_container);
                }
            });
        } else {
            moreInfoButton.setVisibility(View.GONE);
        }

        final RelativeLayout loadingBar = (RelativeLayout) InflatedView.findViewById(R.id.loadingProgressHolder);
        loadingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        final Button submitMoreInfo = (Button) InflatedView.findViewById(R.id.customactivityoncrash_error_activity_submit_more_details);
        submitMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //We retrieve all the error data and show it

                slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                dialogFragment.setContext(getActivity());
                dialogFragment.setFragmentManager(ErrorActivity.fragmentManager);
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

                        final File crashOut = new File(ErrorActivity.thisActivity.getFilesDir().getPath() + "/crashDetails.txt");
                        try {
                            crashOut.createNewFile();
                            FileWriter fw = new FileWriter(crashOut);
                            fw.append(resultBundle.getString(slideDownDialogFragment.RESULT_INPUT+"0"));
                            fw.close();
                            uploadHelper uploadDetails = new uploadHelper(ErrorActivity.thisActivity);
                            uploadDetails.setInterface(new uploadHelper.uploadHelperInterface() {
                                @Override
                                public void onStateChanged(int state) {
                                }

                                @Override
                                public void onPublishUploadProgress(long nowSize, long totalSize) {

                                }

                                @Override
                                public void onUploadComplete(String response) {
                                    crashOut.delete();
                                    loadingBar.startAnimation(AnimationUtils.loadAnimation(ErrorActivity.thisActivity, R.anim.fade_out));
                                    loadingBar.setVisibility(View.GONE);
                                    if (response.contains("success")) {
                                        Toast.makeText(ErrorActivity.thisActivity, R.string.errorActivity_SubmitSuccessful, Toast.LENGTH_LONG).show();
                                        submitMoreInfo.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onUploadFailed(String reason) {
                                    crashOut.delete();
                                    loadingBar.startAnimation(AnimationUtils.loadAnimation(ErrorActivity.thisActivity, R.anim.fade_out));
                                    loadingBar.setVisibility(View.GONE);
                                    if (reason.contains("no_report_for_id")) {
                                        Toast.makeText(ErrorActivity.thisActivity, "We could not locate a report for your crash on our server.", Toast.LENGTH_LONG).show();
                                        submitMoreInfo.setEnabled(false);
                                    } else {
                                        Toast.makeText(ErrorActivity.thisActivity, R.string.errorActivity_FailedToSubmit, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            loadingBar.setVisibility(View.VISIBLE);
                            loadingBar.startAnimation(AnimationUtils.loadAnimation(ErrorActivity.thisActivity, R.anim.fade_in));
                            uploadDetails.setLocalUrl(ErrorActivity.thisActivity.getFilesDir().getPath() + "/crashDetails.txt");
                            uploadDetails.setServerUrl("https://neon-soft.de/inc/acra/acra.php");
                            uploadDetails.setAdditionalUploadPosts(new String[][] {{"type", "details"}, {"stacktrace", "" + CustomActivityOnCrash.getStackTraceFromIntent(ErrorActivity.thisActivity.getIntent())}, {"package", MainActivity.class.getPackage().getName()}});
                            uploadDetails.startUpload();
                        } catch (Throwable e) {
                            Log.e("NPM","[errorActivity] Failed to save crash details.",e);
                        }
                    }

                    @Override
                    public void onTouchOutside() {
                    }
                });
                dialogFragment.addInput(getString(R.string.errorActivity_EnterMoreDetails), "", false, null);
                dialogFragment.setInputSingleLine(0, false);
                dialogFragment.setNeutralButton(getString(R.string.errorActivity_Close));
                dialogFragment.setPositiveButton(getString(R.string.errorActivity_Submit));
                dialogFragment.showDialog(de.NeonSoft.neopowermenu.R.id.dialog_container);
            }
        });

        return InflatedView;
    }

    private void copyErrorToClipboard() {
        String errorInformation =
                CustomActivityOnCrash.getStackTraceFromIntent(ErrorActivity.thisActivity.getIntent());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ClipboardManager clipboard = (ClipboardManager) ErrorActivity.thisActivity.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(getString(R.string.customactivityoncrash_error_activity_error_details_clipboard_label), errorInformation);
            clipboard.setPrimaryClip(clip);
        } else {
            //noinspection deprecation
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) ErrorActivity.thisActivity.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(errorInformation);
        }
    }

}

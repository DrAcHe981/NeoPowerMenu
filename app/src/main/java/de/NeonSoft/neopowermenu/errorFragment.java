package de.NeonSoft.neopowermenu;
import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;
import cat.ereza.customactivityoncrash.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.*;
import java.util.*;

import android.support.v4.app.Fragment;
import org.acra.*;

public class errorFragment extends Fragment
{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO: Implement this method
		View InflatedView = inflater.inflate(de.NeonSoft.neopowermenu.R.layout.customactivityoncrash_error_fragment, container, false);
		
		final EditText UserInput = (EditText) InflatedView.findViewById(de.NeonSoft.neopowermenu.R.id.customactivityoncrash_error_activity_info_input);
		
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

        Button moreInfoButton = (Button) InflatedView.findViewById(de.NeonSoft.neopowermenu.R.id.customactivityoncrash_error_activity_more_info_button);

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
								public void onListItemClick(int position, String text)
								{
									// TODO: Implement this method
								}

								@Override
								public void onNegativeClick()
								{
									// TODO: Implement this method
								}

								@Override
								public void onNeutralClick()
								{
									// TODO: Implement this method
									copyErrorToClipboard();
									Toast.makeText(ErrorActivity.thisActivity, R.string.errorActivity_Copyied, Toast.LENGTH_SHORT).show();
								}

								@Override
								public void onPositiveClick(Bundle resultBundle)
								{
									// TODO: Implement this method
								}

								@Override
								public void onTouchOutside()
								{
									// TODO: Implement this method
								}
							});
						dialogFragment.setText(CustomActivityOnCrash.getAllErrorDetailsFromIntent(ErrorActivity.thisActivity, ErrorActivity.thisActivity.getIntent()));
						dialogFragment.setNeutralButton(getString(R.string.errorActivity_CopyToClip));
						dialogFragment.setPositiveButton(getString(R.string.errorActivity_Close));
						dialogFragment.showDialog(de.NeonSoft.neopowermenu.R.id.dialog_container);
					}
				});
        } else {
            moreInfoButton.setVisibility(View.GONE);
        }

        int defaultErrorActivityDrawableId = CustomActivityOnCrash.getDefaultErrorActivityDrawableIdFromIntent(ErrorActivity.thisActivity.getIntent());
        ImageView errorImageView = ((ImageView) InflatedView.findViewById(cat.ereza.customactivityoncrash.R.id.customactivityoncrash_error_activity_image));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            errorImageView.setImageDrawable(getResources().getDrawable(defaultErrorActivityDrawableId, ErrorActivity.thisActivity.getTheme()));
        } else {
            //noinspection deprecation
            errorImageView.setImageDrawable(getResources().getDrawable(defaultErrorActivityDrawableId));
        }
		
		return InflatedView;
	}
	
    private void copyErrorToClipboard() {
        String errorInformation =
			CustomActivityOnCrash.getAllErrorDetailsFromIntent(ErrorActivity.thisActivity, ErrorActivity.thisActivity.getIntent());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ClipboardManager clipboard = (ClipboardManager) ErrorActivity.thisActivity.getSystemService(ErrorActivity.thisActivity.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(getString(R.string.customactivityoncrash_error_activity_error_details_clipboard_label), errorInformation);
            clipboard.setPrimaryClip(clip);
        } else {
            //noinspection deprecation
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) ErrorActivity.thisActivity.getSystemService(ErrorActivity.thisActivity.CLIPBOARD_SERVICE);
            clipboard.setText(errorInformation);
        }
    }
	
}

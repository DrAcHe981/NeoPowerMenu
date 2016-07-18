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
import cat.ereza.customactivityoncrash.R;

public class errorFragment extends Fragment
{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO: Implement this method
		View InflatedView = inflater.inflate(de.NeonSoft.neopowermenu.R.layout.customactivityoncrash_error_fragment, container, false);
		
		//Close/restart button logic:
        //If a class if set, use restart.
        //Else, use close and just finish the app.
        //It is recommended that you follow this logic if implementing a custom error activity.
        Button restartButton = (Button) InflatedView.findViewById(de.NeonSoft.neopowermenu.R.id.customactivityoncrash_error_activity_restart_button);

        final Class<? extends Activity> restartActivityClass = CustomActivityOnCrash.getRestartActivityClassFromIntent(ErrorActivity.thisActivity.getIntent());
        final CustomActivityOnCrash.EventListener eventListener = CustomActivityOnCrash.getEventListenerFromIntent(ErrorActivity.thisActivity.getIntent());

        if (restartActivityClass != null) {
            restartButton.setText(R.string.customactivityoncrash_error_activity_restart_app);
            restartButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(ErrorActivity.thisActivity, restartActivityClass);
						CustomActivityOnCrash.restartApplicationWithIntent(ErrorActivity.thisActivity, intent, eventListener);
					}
				});
        } else {
            restartButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						CustomActivityOnCrash.closeApplication(ErrorActivity.thisActivity, eventListener);
					}
				});
        }

        Button moreInfoButton = (Button) InflatedView.findViewById(de.NeonSoft.neopowermenu.R.id.customactivityoncrash_error_activity_more_info_button);

        if (CustomActivityOnCrash.isShowErrorDetailsFromIntent(ErrorActivity.thisActivity.getIntent())) {

            moreInfoButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//We retrieve all the error data and show it

						slideDownDialogFragment dialogFragment = new slideDownDialogFragment(getActivity(), ErrorActivity.fragmentManager);
						dialogFragment.setDialogListener(new slideDownDialogFragment.slideDownDialogInterface() {

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
									Toast.makeText(ErrorActivity.thisActivity, R.string.customactivityoncrash_error_activity_error_details_copied, Toast.LENGTH_SHORT).show();
								}

								@Override
								public void onPositiveClick(ArrayList<String> resultData)
								{
									// TODO: Implement this method
								}

								@Override
								public void onTouchOutside()
								{
									// TODO: Implement this method
								}
							});
						dialogFragment.setDialogText(CustomActivityOnCrash.getAllErrorDetailsFromIntent(ErrorActivity.thisActivity, ErrorActivity.thisActivity.getIntent()));
						dialogFragment.setDialogNeutralButton(getString(R.string.customactivityoncrash_error_activity_error_details_copy));
						dialogFragment.setDialogPositiveButton(getString(R.string.customactivityoncrash_error_activity_error_details_close));
						dialogFragment.showDialog(de.NeonSoft.neopowermenu.R.id.dialog_container);
						/*AlertDialog dialog = new AlertDialog.Builder(ErrorActivity.this)
						 .setTitle(R.string.customactivityoncrash_error_activity_error_details_title)
						 .setMessage(CustomActivityOnCrash.getAllErrorDetailsFromIntent(ErrorActivity.this, getIntent()))
						 .setPositiveButton(R.string.customactivityoncrash_error_activity_error_details_close, null)
						 .setNeutralButton(R.string.customactivityoncrash_error_activity_error_details_copy,
						 new DialogInterface.OnClickListener() {
						 @Override
						 public void onClick(DialogInterface dialog, int which) {
						 copyErrorToClipboard();
						 Toast.makeText(ErrorActivity.this, R.string.customactivityoncrash_error_activity_error_details_copied, Toast.LENGTH_SHORT).show();
						 }
						 })
						 .show();
						 TextView textView = (TextView) dialog.findViewById(android.R.id.message);
						 textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.customactivityoncrash_error_activity_error_details_text_size));*/
					}
				});
        } else {
            moreInfoButton.setVisibility(View.GONE);
        }

        int defaultErrorActivityDrawableId = CustomActivityOnCrash.getDefaultErrorActivityDrawableIdFromIntent(ErrorActivity.thisActivity.getIntent());
        ImageView errorImageView = ((ImageView) InflatedView.findViewById(R.id.customactivityoncrash_error_activity_image));
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

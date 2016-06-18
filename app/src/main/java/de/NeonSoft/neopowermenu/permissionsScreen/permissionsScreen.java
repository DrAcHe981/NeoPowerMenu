package de.NeonSoft.neopowermenu.permissionsScreen;

import android.*;
import android.app.*;
import android.content.pm.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.CompoundButton.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.Preferences.*;
import de.NeonSoft.neopowermenu.helpers.*;
import java.util.*;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View.OnClickListener;
import de.NeonSoft.neopowermenu.R;

public class permissionsScreen extends Fragment
{

		private static Activity mActivity;
		private static FragmentActivity mFragment;
		public static final int MY_PERMISSIONS_REQUEST = 101;
		private static AlertDialog.Builder PermissionsDialog;
		
		private static ListView ListView_Permissions;
		public static PermissionsAdapter adapter;
		private static CheckBox CheckBox_DontAskAgain;
		
		public static String[] permissions = {Manifest.permission.INTERNET,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.WAKE_LOCK,Manifest.permission.ACCESS_SURFACE_FLINGER,Manifest.permission.RECORD_AUDIO,Manifest.permission.SYSTEM_ALERT_WINDOW};
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
				// TODO: Implement this method
				if(getArguments() != null && !getArguments().isEmpty() && getArguments().getBoolean("AutoStart",true)) {
						MainActivity.visibleFragment = "permissionsAutoStart";
				} else {
						MainActivity.visibleFragment = "permissions";
				}
				View InflatedView = inflater.inflate(R.layout.permissionsscreen,null);
				
				mActivity = getActivity();
				mFragment = getActivity();
				
				ListView_Permissions = (ListView) InflatedView.findViewById(R.id.permissionsscreenListView_permissions);
				adapter = new PermissionsAdapter(mActivity,permissions);
				ListView_Permissions.setAdapter(adapter);
				
				CheckBox_DontAskAgain = (CheckBox) InflatedView.findViewById(R.id.permissionsscreenCheckBox_DontAskAgain);
				
				if(getArguments() != null && !getArguments().isEmpty() && !getArguments().getBoolean("AutoStart",true)) {
						CheckBox_DontAskAgain.setVisibility(View.GONE);
				}
				
				MainActivity.setActionBarButton(getString(R.string.Dialog_Ok), R.drawable.ic_content_send, new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										if(!adapter.isAllChecked()) {
												slideDownDialogFragment dialogFragment = new slideDownDialogFragment(getActivity(), new slideDownDialogFragment.slideDownDialogInterface() {

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
																}

																@Override
																public void onPositiveClick(ArrayList<String> resultData)
																{
																		// TODO: Implement this method
																		MainActivity.fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
																}

																@Override
																public void onTouchOutside()
																{
																		// TODO: Implement this method
																}
														});
														dialogFragment.setDialogText(getActivity().getString(R.string.permissionsScreen_NotAllGranted));
												dialogFragment.setDialogPositiveButton(getActivity().getString(R.string.Dialog_Ok));
												MainActivity.fragmentManager.beginTransaction().add(R.id.dialog_container,dialogFragment,slideDownDialogFragment.dialogTag).commit();
										} else {
												MainActivity.fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container,new PreferencesPartFragment()).commit();
										}
								}
						});
				
				CheckBox_DontAskAgain.setOnCheckedChangeListener(new OnCheckedChangeListener() {

								@Override
								public void onCheckedChanged(CompoundButton p1, boolean p2)
								{
										// TODO: Implement this method
										MainActivity.preferences.edit().putBoolean("DontAskPermissionsAgain",p2).commit();
								}
								
						});
						
				return InflatedView;
		}
		
		public static boolean checkPermissions(Activity mActivity,String[] permissions) {
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
				{
						for(int i = 0;i<permissions.length;i++) {
								if(!permissions[i].equalsIgnoreCase(Manifest.permission.ACCESS_SURFACE_FLINGER) && !permissions[i].equalsIgnoreCase(Manifest.permission.SYSTEM_ALERT_WINDOW) && ContextCompat.checkSelfPermission(mActivity,permissions[i]) != PackageManager.PERMISSION_GRANTED) {
										return false;
								}
						}
						return true;
				} else {
						return true;
				}
		}
		

}

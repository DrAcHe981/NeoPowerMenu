package de.NeonSoft.neopowermenu.permissionsScreen;

import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.view.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import de.NeonSoft.neopowermenu.R;
import android.view.View.*;
import de.NeonSoft.neopowermenu.Preferences.*;
import android.widget.CompoundButton.*;

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
												PermissionsDialog = new AlertDialog.Builder(getActivity());
												PermissionsDialog.setMessage(R.string.permissionsScreen_NotAllGranted);
												PermissionsDialog.setPositiveButton(R.string.Dialog_Ok, new DialogInterface.OnClickListener() {

																@Override
																public void onClick(DialogInterface p1, int p2)
																{
																		// TODO: Implement this method
																		MainActivity.fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.pref_container,new PreferencesPartFragment()).commit();
																}
														});
												PermissionsDialog.setNegativeButton(R.string.Dialog_Cancel, new DialogInterface.OnClickListener() {

																@Override
																public void onClick(DialogInterface p1, int p2)
																{
																		// TODO: Implement this method
																}
														});
												PermissionsDialog.show();
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
								if(!permissions[i].equalsIgnoreCase(Manifest.permission.ACCESS_SURFACE_FLINGER) && ContextCompat.checkSelfPermission(mActivity,permissions[i]) != PackageManager.PERMISSION_GRANTED) {
										return false;
								}
						}
						return true;
				} else {
						return true;
				}
		}
		

}

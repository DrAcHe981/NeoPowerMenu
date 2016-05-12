package de.NeonSoft.neopowermenu.permissionsScreen;

import android.app.*;
import android.content.pm.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;

import android.support.v4.app.Fragment;

public class PermissionsAdapter extends ArrayAdapter<String>
{

		private int MY_PERMISSIONS_REQUEST = 101;
		
		private static Activity mContext;
		private static LayoutInflater mInflater;
		private static String[] mPermissions;
		private static boolean[] mChecks;
		
		public PermissionsAdapter(Activity context,String[] permissions) {
				super(context,R.layout.permissionslistitem,permissions);
				this.mContext = context;
				this.mInflater = mContext.getLayoutInflater();
				this.mPermissions = permissions;
				this.mChecks = new boolean[permissions.length];
		}

		@Override
		public View getView(final int p1, View p2, ViewGroup p3)
		{
				// TODO: Implement this method
				View InflatedView = mInflater.inflate(R.layout.permissionslistitem,null);
				
				LinearLayout root = (LinearLayout) InflatedView.findViewById(R.id.root);
				CheckBox CheckBox = (CheckBox) InflatedView.findViewById(R.id.Checkbox);
				TextView Title = (TextView) InflatedView.findViewById(R.id.Title);
				TextView Desc = (TextView) InflatedView.findViewById(R.id.Desc);
			
				CheckBox.setClickable(false);
				CheckBox.setFocusable(false);
				mChecks[p1] = false;
				if(permissionsScreen.checkPermissions(mContext,new String[] {mPermissions[p1]})) {
						CheckBox.setChecked(true);
						mChecks[p1] = true;
				} else {
						root.setOnClickListener(new OnClickListener() {
								
										@Override
										public void onClick(View view)
										{
												// TODO: Implement this method
												if (ContextCompat.checkSelfPermission(mContext,mPermissions[p1]) != PackageManager.PERMISSION_GRANTED) {
														mContext.requestPermissions(new String[] {mPermissions[p1]},MY_PERMISSIONS_REQUEST);
												}
										}
								});
				}

				try {
						String title = mContext.getResources().getString(mContext.getResources().getIdentifier("permissionsScreenTitle_"+mPermissions[p1],"string",MainActivity.class.getPackage().getName()));
						Title.setText(title);
				} catch (Throwable t) {
						Title.setText("String Resource for permissionsScreenTitle_"+ mPermissions[p1]+" not found.");
				}
				try {
						String Description = mContext.getResources().getString(mContext.getResources().getIdentifier("permissionsScreenDesc_"+mPermissions[p1],"string",MainActivity.class.getPackage().getName()));
						Desc.setText(Description + (mChecks[p1] ? "" : ("\n\n" + mContext.getString(R.string.permissionsScreen_ClickToRequest))));
				} catch (Throwable t) {
						Desc.setText("String Resource for permissionsScreenDesc_" + mPermissions[p1]+" not found.");
				}
				
				return InflatedView;
		}
	
		public boolean isAllChecked() {
				for(int i = 0;i<mPermissions.length;i++) {
						if(!mChecks[i]) {
								return false;
						}
				}
				return true;
		}
		
}

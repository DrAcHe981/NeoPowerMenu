package de.NeonSoft.neopowermenu.permissionsScreen;

import android.annotation.TargetApi;
import android.app.*;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.*;
import android.os.Build;
import android.os.TransactionTooLargeException;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.slideDownDialogFragment;

public class PermissionsAdapter extends ArrayAdapter<String> {

    final private int MY_PERMISSIONS_REQUEST = 101;

    private static Activity mContext;
    private static LayoutInflater mInflater;
    private static String[] mPermissions;
    private static boolean[] mChecks;

    public PermissionsAdapter(Activity context, String[] permissions) {
        super(context, R.layout.permissionslistitem, permissions);
        this.mContext = context;
        this.mInflater = mContext.getLayoutInflater();
        this.mPermissions = permissions;
        this.mChecks = new boolean[permissions.length];
    }

    @Override
    public View getView(final int p1, View p2, ViewGroup p3) {
        View InflatedView = mInflater.inflate(R.layout.permissionslistitem, null);

        LinearLayout root = (LinearLayout) InflatedView.findViewById(R.id.root);
        CheckBox CheckBox = (CheckBox) InflatedView.findViewById(R.id.Checkbox);
        TextView Title = (TextView) InflatedView.findViewById(R.id.Title);
        TextView Desc = (TextView) InflatedView.findViewById(R.id.Desc);

        CheckBox.setClickable(false);
        CheckBox.setFocusable(false);
        mChecks[p1] = false;
        if (mPermissions[p1].equalsIgnoreCase("android.permission.ACCESS_NOTIFICATION_POLICY")) {
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted()) {
                root.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                            mContext.startActivityForResult(intent, permissionsScreen.MY_PERMISSIONS_REQUEST);
                        } catch (Throwable t) {
                            slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                            dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                            dialogFragment.setContext(mContext);
                            dialogFragment.setText("Your device does not seem to support notification policy access settings.");
                            dialogFragment.showDialog(R.id.dialog_container);
                        }
                    }

                });
            } else {
                CheckBox.setChecked(true);
                mChecks[p1] = true;
            }
        } else if (mPermissions[p1].equalsIgnoreCase("DeviceAdmin")) {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) mContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
            if (devicePolicyManager.isAdminActive(new ComponentName(mContext, deviceAdmin.class))) {
                CheckBox.setChecked(true);
                mChecks[p1] = true;
            } else {
                root.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        try {
                            Intent intent = new Intent(DevicePolicyManager
                                    .ACTION_ADD_DEVICE_ADMIN);
                            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                                    new ComponentName(mContext, deviceAdmin.class));
                            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                                    mContext.getString(R.string.permissionsScreenDesc_DeviceAdmin));
                            mContext.startActivityForResult(intent, permissionsScreen.RESULT_ENABLE_ADMIN);
                        } catch (Throwable t) {
                            slideDownDialogFragment dialogFragment = new slideDownDialogFragment();
                            dialogFragment.setFragmentManager(MainActivity.fragmentManager);
                            dialogFragment.setContext(mContext);
                            dialogFragment.setText("Your device does not seem to support device admins.");
                            dialogFragment.showDialog(R.id.dialog_container);
                        }
                    }
                });
            }
        } else {
            if (permissionsScreen.checkPermissions(mContext, new String[]{mPermissions[p1]})) {
                CheckBox.setChecked(true);
                mChecks[p1] = true;
            } else {
                root.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (ContextCompat.checkSelfPermission(mContext, mPermissions[p1]) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(mContext, new String[]{mPermissions[p1]}, MY_PERMISSIONS_REQUEST);
                        }
                    }
                });
            }
        }

        try {
            String title = mContext.getResources().getString(mContext.getResources().getIdentifier("permissionsScreenTitle_" + mPermissions[p1], "string", MainActivity.class.getPackage().getName()));
            Title.setText(title);
        } catch (Throwable t) {
            Title.setText("String Resource for permissionsScreenTitle_" + mPermissions[p1] + " not found.");
        }
        try {
            String Description = mContext.getResources().getString(mContext.getResources().getIdentifier("permissionsScreenDesc_" + mPermissions[p1], "string", MainActivity.class.getPackage().getName()));
            Desc.setText(Description + (mChecks[p1] ? "" : ("\n\n" + mContext.getString(R.string.permissionsScreen_ClickToRequest))));
        } catch (Throwable t) {
            Desc.setText("String Resource for permissionsScreenDesc_" + mPermissions[p1] + " not found.");
        }

        return InflatedView;
    }

    public boolean isAllChecked() {
        for (int i = 0; i < mPermissions.length; i++) {
            if (!mChecks[i]) {
                return false;
            }
        }
        return true;
    }

}

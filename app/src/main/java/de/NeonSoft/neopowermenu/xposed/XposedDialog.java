package de.NeonSoft.neopowermenu.xposed;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.provider.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.ViewGroup.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.*;
import de.NeonSoft.neopowermenu.services.*;
import eu.chainfire.libsuperuser.*;

import android.view.View.OnClickListener;
import de.NeonSoft.neopowermenu.Preferences.*;
import android.media.*;

/**
 * Created by naman on 20/03/15.
 */
public class XposedDialog extends DialogFragment
{

    public XposedDialog()
		{

    }

		public static Context mContext;
		public static NotificationManager nfm;
		public static Notification.Builder notifyb;

		View singleTouch = null;
		boolean doubleToConfirm = false;
		
		private boolean HookShutdownThread = false;

		View.OnClickListener powerOnClickListener,rebootOnClickListener,soft_rebootOnClickListener,
				screenshotOnClickListener,screenrecordOnClickListener,flashlightOnClickListener,
				expandedDesktopOnClickListener,airplaneModeOnClickListener,restartUIOnClickListener,
				soundModeOnClickListener;

		FrameLayout dialogMain;
		LinearLayout ListContainer;

    LinearLayout recovery, bootloader, safemode;
		TextView recoveryText, bootloaderText, safemodeText;
    FrameLayout frame, frame2;
		View seperator1;
    private CircularRevealView revealView;
    private View selectedView;
    private int backgroundColor;
		ImageView progressbg;
    ProgressBar progress;
    TextView status, status_detail;

    private static final String SHUTDOWN_BROADCAST
		= "am broadcast android.intent.action.ACTION_SHUTDOWN";
    private static final String SHUTDOWN = "reboot -p";
    private static final String REBOOT_CMD = "reboot";
    private static final String REBOOT_SOFT_REBOOT_CMD = "setprop ctl.restart zygote";
    private static final String REBOOT_RECOVERY_CMD = "reboot recovery";
    private static final String REBOOT_BOOTLOADER_CMD = "reboot bootloader";
    private static final String[] REBOOT_SAFE_MODE
		= new String[]{"setprop persist.sys.safemode 1", REBOOT_SOFT_REBOOT_CMD};
		private static final String RESTARTSYSTEMUI = "am startservice -n com.android.systemui/.SystemUIService";
		
    private static final int BG_PRIO = android.os.Process.THREAD_PRIORITY_BACKGROUND;
    private static final int RUNNABLE_DELAY_MS = 5000;

		public static boolean canDismiss = true;

		boolean boolean_DialogGravityTop = false;
		boolean boolean_DialogGravityLeft = false;
		boolean boolean_DialogGravityRight = false;
		boolean boolean_DialogGravityBottom = false;
		
		AudioManager am;
		int amRingerMode;
		
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
		{

				//doubleToConfirm = XposedMainActivity.preferences.getBoolean("DoubleTouchToConfirm",true);

        View view = inflater.inflate(R.layout.fragment_power,container,false);

				mContext = getDialog().getContext();

				am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
				amRingerMode = am.getRingerMode();
				
				if(!XposedMainActivity.sStyleName.equalsIgnoreCase("Material (Fullscreen)")) {
				boolean_DialogGravityTop = XposedMainActivity.preferences.getBoolean("DialogGravityTop",false);
				boolean_DialogGravityLeft = XposedMainActivity.preferences.getBoolean("DialogGravityLeft",false);
				boolean_DialogGravityRight = XposedMainActivity.preferences.getBoolean("DialogGravityRight",false);
				boolean_DialogGravityBottom = XposedMainActivity.preferences.getBoolean("DialogGravityBottom",false);
				}
				dialogMain = (FrameLayout) view.findViewById(R.id.fragmentpowerFrameLayout_Main);
        revealView = (CircularRevealView) view.findViewById(R.id.reveal);
				backgroundColor = Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Backgroundcolor", "#ffffff"));
				ListContainer = (LinearLayout) view.findViewById(R.id.ListContainer);
        seperator1 = view.findViewById(R.id.seperator1);
        recovery = (LinearLayout) view.findViewById(R.id.recovery);
				recoveryText = (TextView) view.findViewById(R.id.recoveryText);
        bootloader = (LinearLayout) view.findViewById(R.id.bootloader);
				bootloaderText = (TextView) view.findViewById(R.id.bootloaderText);
        safemode = (LinearLayout) view.findViewById(R.id.safemode);
				safemodeText = (TextView) view.findViewById(R.id.safemodeText);

        frame = (FrameLayout) view.findViewById(R.id.frame);
				frame.setBackgroundColor(backgroundColor);
        frame2 = (FrameLayout) view.findViewById(R.id.frame2);

        status = (TextView) view.findViewById(R.id.status);
        status_detail = (TextView) view.findViewById(R.id.status_detail);

				progressbg = (ImageView) view.findViewById(R.id.progressbg);
        progress = (ProgressBar) view.findViewById(R.id.progress);

				seperator1.setBackgroundColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor", "#000000")));
				recoveryText.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor", "#000000")));
				bootloaderText.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor", "#000000")));
				safemodeText.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor", "#000000")));

				if(XposedMainActivity.sStyleName.equalsIgnoreCase("Material (Fullscreen)")) {
						LayoutParams fllp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
						dialogMain.setLayoutParams(fllp);
				}
				//TextDrawable progressbgd = TextDrawable.builder().buildRound("",Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Backgroundcolor","#ffffff")));
				//progressbg.setImageDrawable(progressbgd);
        progress.getIndeterminateDrawable().setColorFilter(
						Color.parseColor("#ffffff"),
						android.graphics.PorterDuff.Mode.SRC_IN);

						
        powerOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v)
						{
								canDismiss = false;

                final int color = Color.parseColor(XposedMainActivity.preferences.getString("DialogShutdown_Backgroundcolor", "#d32f2f"));
                final Point p = getLocationInView(revealView, v);

                if (selectedView == v)
								{
                    revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
                    selectedView = null;
                }
								else
								{
                    revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
                    selectedView = v;
                }

                ((XposedMainActivity) getActivity()).revealFromTop();
                frame.setVisibility(View.GONE);
                frame2.setVisibility(View.VISIBLE);

                status.setText(R.string.powerMenuMain_Shutdown);
                status_detail.setText(R.string.powerMenu_Shuttingdown);
								progress.getIndeterminateDrawable().setColorFilter(
										Color.parseColor(XposedMainActivity.preferences.getString("DialogShutdown_Textcolor", "#ffffff")),
										android.graphics.PorterDuff.Mode.SRC_IN);
								status.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogShutdown_Textcolor", "#ffffff")));
								status_detail.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogShutdown_Textcolor", "#ffffff")));

								if (!XposedMainActivity.previewMode)
								{
										getDialog().setCanceledOnTouchOutside(false);
										getDialog().setCancelable(false);
										if(HookShutdownThread) {
												XposedUtils.doShutdown(getActivity(),0);
										} else {
												new BackgroundThread(SHUTDOWN).start();
										}
								}
            }
        };
        rebootOnClickListener = new View.OnClickListener() {
						@Override
						public void onClick(View v)
						{
								canDismiss = false;

								final int color = Color.parseColor(XposedMainActivity.preferences.getString("DialogReboot_Backgroundcolor", "#3f51b5"));
								final Point p = getLocationInView(revealView, v);

								if (selectedView == v)
								{
										revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
										selectedView = null;
								}
								else
								{
										revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
										selectedView = v;
								}

								((XposedMainActivity) getActivity()).revealFromTop();
								frame.setVisibility(View.GONE);
								frame2.setVisibility(View.VISIBLE);

								status.setText(R.string.powerMenuMain_Reboot);
								status_detail.setText(R.string.powerMenu_Rebooting);
								progress.getIndeterminateDrawable().setColorFilter(
										Color.parseColor(XposedMainActivity.preferences.getString("DialogReboot_Textcolor", "#ffffff")),
										android.graphics.PorterDuff.Mode.SRC_IN);
								status.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogReboot_Textcolor", "#ffffff")));
								status_detail.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogReboot_Textcolor", "#ffffff")));

								if (!XposedMainActivity.previewMode)
								{
										getDialog().setCanceledOnTouchOutside(false);
										getDialog().setCancelable(false);
										if(HookShutdownThread) {
												XposedUtils.doReboot(getActivity(),0);
										} else {
												new BackgroundThread(REBOOT_CMD).start();
										}
								}
						}
				};
        soft_rebootOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v)
						{
								canDismiss = false;

                final int color = Color.parseColor(XposedMainActivity.preferences.getString("DialogSoftReboot_Backgroundcolor", "#e91e63"));
                final Point p = getLocationInView(revealView, v);

                if (selectedView == v)
								{
                    revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
                    selectedView = null;
                }
								else
								{
                    revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
                    selectedView = v;
                }

                ((XposedMainActivity) getActivity()).revealFromTop();
                frame.setVisibility(View.GONE);
                frame2.setVisibility(View.VISIBLE);

                status.setText(R.string.powerMenuMain_SoftReboot);
                status_detail.setText(R.string.powerMenu_Rebooting);
								progress.getIndeterminateDrawable().setColorFilter(
										Color.parseColor(XposedMainActivity.preferences.getString("DialogSoftReboot_Textcolor", "#ffffff")),
										android.graphics.PorterDuff.Mode.SRC_IN);
								status.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogSoftReboot_Textcolor", "#ffffff")));
								status_detail.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogSoftReboot_Textcolor", "#ffffff")));

								if (!XposedMainActivity.previewMode)
								{
										getDialog().setCanceledOnTouchOutside(false);
										getDialog().setCancelable(false);
										if(HookShutdownThread) {
												XposedUtils.doReboot(getActivity(),1);
										} else {
												new BackgroundThread(REBOOT_SOFT_REBOOT_CMD).start();
										}
								}
            }
        };

				screenshotOnClickListener = new View.OnClickListener() {

						@Override
						public void onClick(View p1)
						{
								// TODO: Implement this method
								if (!XposedMainActivity.previewMode)
								{
										dismiss();
										Handler handler = new Handler();
										handler.postDelayed(new Runnable() {

														@Override
														public void run()
														{
																// TODO: Implement this method
																Intent takeScreenshotBC = new Intent();
																takeScreenshotBC.setAction(XposedMain.NPM_ACTION_BROADCAST_SCREENSHOT);
																XposedMainActivity.mContext.sendBroadcast(takeScreenshotBC);
														}
												}, XposedMainActivity.preferences.getLong("ScreenshotDelay", 1000));
								}
						}
				};
				
				screenrecordOnClickListener = new View.OnClickListener() {

						@Override
						public void onClick(View p1)
						{
								if (!XposedMainActivity.previewMode)
								{
										dismiss();
										Handler handler = new Handler();
										handler.postDelayed(new Runnable() {

														@Override
														public void run()
														{
																// TODO: Implement this method
																Intent takeScreenrecordBC = new Intent();
																takeScreenrecordBC.setAction(XposedMain.NPM_ACTION_BROADCAST_SCREENRECORD);
																XposedMainActivity.mContext.sendBroadcast(takeScreenrecordBC);
														}
												}, 1000);
										}
						}
				};
				
				flashlightOnClickListener = new OnClickListener() {

						@Override
						public void onClick(View p1)
						{
								if(!XposedMainActivity.previewMode) {
										dismiss();
										final Handler handler = new Handler();
										new Thread() {
												@Override
												public void run() {
														handler.post(new Runnable() {

																		@Override
																		public void run()
																		{
																				toggleTorch(false);
																		}
																});
												}
										}.start();
								}
						}
				};
				expandedDesktopOnClickListener = new OnClickListener() {

						@Override
						public void onClick(View p1)
						{
								if(!XposedMainActivity.previewMode) {
										dismiss();
										Intent launchIntent = new Intent("gravitybox.intent.action.TOGGLE_EXPANDED_DESKTOP");
										mContext.sendBroadcast(launchIntent);
								}
						}
				};
				airplaneModeOnClickListener = new OnClickListener() {

						@Override
						public void onClick(View p1)
						{
								if(!XposedMainActivity.previewMode) {
										dismiss();
										Intent launchIntent = new Intent(XposedMain.NPM_ACTION_BROADCAST_TOGGLEAIRPLANEMODE);
										mContext.sendBroadcast(launchIntent);
								}
						}
						
				};
				restartUIOnClickListener = new OnClickListener() {

						@Override
						public void onClick(View v)
						{
								if (!XposedMainActivity.previewMode)
								{
										dismiss();
										Intent launchIntent = new Intent(XposedMain.NPM_ACTION_BROADCAST_KILLSYSTEMUI);
										mContext.sendBroadcast(launchIntent);
								}
						}
				};
				soundModeOnClickListener = new OnClickListener() {

						@Override
						public void onClick(View p1)
						{
								if (!XposedMainActivity.previewMode) {
										dismiss();
										if(amRingerMode==AudioManager.RINGER_MODE_NORMAL) {
												am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
										} else if (amRingerMode==AudioManager.RINGER_MODE_SILENT) {
												am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
										} else if (amRingerMode==AudioManager.RINGER_MODE_VIBRATE) {
												am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
										}
								}
						}
				};
				
        recovery.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v)
								{
										canDismiss = false;

										final int color = Color.parseColor(XposedMainActivity.preferences.getString("DialogRecovery_Backgroundcolor", "#8bc34a"));
										final Point p = getLocationInView(revealView, v);

										if (selectedView == v)
										{
												revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
												selectedView = null;
										}
										else
										{
												revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
												selectedView = v;
										}

										((XposedMainActivity) getActivity()).revealFromTop();
										frame.setVisibility(View.GONE);
										frame2.setVisibility(View.VISIBLE);

										status.setText(R.string.powerMenuBottom_Recovery);
										status_detail.setText(R.string.powerMenu_Rebooting);
										progress.getIndeterminateDrawable().setColorFilter(
												Color.parseColor(XposedMainActivity.preferences.getString("DialogRecovery_Textcolor", "#ffffff")),
												android.graphics.PorterDuff.Mode.SRC_IN);
										status.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogRecovery_Textcolor", "#ffffff")));
										status_detail.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogRecovery_Textcolor", "#ffffff")));

										if (!XposedMainActivity.previewMode)
										{
												getDialog().setCanceledOnTouchOutside(false);
												getDialog().setCancelable(false);
												if(HookShutdownThread) {
														XposedUtils.doReboot(getActivity(),2);
												} else {
														new BackgroundThread(REBOOT_RECOVERY_CMD).start();
												}
										}
								}
						});
        bootloader.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v)
								{
										canDismiss = false;

										final int color = Color.parseColor(XposedMainActivity.preferences.getString("DialogBootloader_Backgroundcolor", "#277b71"));
										final Point p = getLocationInView(revealView, v);

										if (selectedView == v)
										{
												revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
												selectedView = null;
										}
										else
										{
												revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
												selectedView = v;
										}

										((XposedMainActivity) getActivity()).revealFromTop();
										frame.setVisibility(View.GONE);
										frame2.setVisibility(View.VISIBLE);

										status.setText(R.string.powerMenuBottom_Bootloader);
										status_detail.setText(R.string.powerMenu_Rebooting);
										progress.getIndeterminateDrawable().setColorFilter(
												Color.parseColor(XposedMainActivity.preferences.getString("DialogBootloader_Textcolor", "#ffffff")),
												android.graphics.PorterDuff.Mode.SRC_IN);
										status.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogBootloader_Textcolor", "#ffffff")));
										status_detail.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogBootloader_Textcolor", "#ffffff")));

										if (!XposedMainActivity.previewMode)
										{
												getDialog().setCanceledOnTouchOutside(false);
												getDialog().setCancelable(false);
												if(HookShutdownThread) {
														XposedUtils.doReboot(getActivity(),3);
												} else {
														new BackgroundThread(REBOOT_BOOTLOADER_CMD).start();
												}
										}
								}
						});
        safemode.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v)
								{
										canDismiss = false;

										final int color = Color.parseColor(XposedMainActivity.preferences.getString("DialogSafeMode_Backgroundcolor", "#009688"));
										final Point p = getLocationInView(revealView, v);

										if (selectedView == v)
										{
												revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
												selectedView = null;
										}
										else
										{
												revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
												selectedView = v;
										}

										((XposedMainActivity) getActivity()).revealFromTop();
										frame.setVisibility(View.GONE);
										frame2.setVisibility(View.VISIBLE);

										status.setText(R.string.powerMenuBottom_SafeMode);
										status_detail.setText(R.string.powerMenu_Rebooting);
										progress.getIndeterminateDrawable().setColorFilter(
												Color.parseColor(XposedMainActivity.preferences.getString("DialogSafeMode_Textcolor", "#ffffff")),
												android.graphics.PorterDuff.Mode.SRC_IN);
										status.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogSafeMode_Textcolor", "#ffffff")));
										status_detail.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogSafeMode_Textcolor", "#ffffff")));

										if (!XposedMainActivity.previewMode)
										{
												getDialog().setCanceledOnTouchOutside(false);
												getDialog().setCancelable(false);
												new BackgroundThread(REBOOT_SAFE_MODE).start();
										}
								}
						});
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

				for (int position = 0;position < 10;position++)
				{
						View InflatedItem = inflater.inflate(R.layout.powermenu_listitem, null);
						LinearLayout root = (LinearLayout) InflatedItem.findViewById(R.id.powermenuitemRoot);
						ImageView icon = (ImageView) InflatedItem.findViewById(R.id.powermenuIcon);
						ImageView icon2 = (ImageView) InflatedItem.findViewById(R.id.powermenuIcon2);
						icon2.setVisibility(View.GONE);
						TextView text = (TextView) InflatedItem.findViewById(R.id.powermenuText);
						TextView desc = (TextView) InflatedItem.findViewById(R.id.powermenuDesc);
						desc.setVisibility(View.GONE);

						if (position == XposedMainActivity.preferences.getInt("ShutdownPosition", 0) && XposedMainActivity.preferences.getBoolean("ShutdownEnabled", true))
						{
								root.setOnClickListener(powerOnClickListener);
								text.setText(getString(R.string.powerMenuMain_Shutdown));
								text.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor", "#000000")));
								createCircleIcon(icon,icon2,getString(R.string.powerMenuMain_Shutdown),getResources().getDrawable(R.drawable.poweroff1),XposedMainActivity.preferences.getString("DialogShutdown_Backgroundcolor","#ffd32f2f"),XposedMainActivity.preferences.getString("DialogShutdown_Textcolor","#ffffff"));
						}
						else if (position == XposedMainActivity.preferences.getInt("RebootPosition", 1) && XposedMainActivity.preferences.getBoolean("RebootEnabled", true))
						{
								root.setOnClickListener(rebootOnClickListener);
								text.setText(getString(R.string.powerMenuMain_Reboot));
								text.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor", "#000000")));
								createCircleIcon(icon,icon2,getString(R.string.powerMenuMain_Reboot),getResources().getDrawable(R.drawable.ic_av_loop),XposedMainActivity.preferences.getString("DialogReboot_Backgroundcolor","#ff3f51b5"),XposedMainActivity.preferences.getString("DialogReboot_Textcolor","#ffffff"));
						}
						else if (position == XposedMainActivity.preferences.getInt("SoftRebootPosition", 2) && XposedMainActivity.preferences.getBoolean("SoftRebootEnabled", true))
						{
								root.setOnClickListener(soft_rebootOnClickListener);
								text.setText(getString(R.string.powerMenuMain_SoftReboot));
								text.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor", "#000000")));
								if(!XposedMainActivity.preferences.getBoolean("SoftReboot_HideDesc",false)) {
										desc.setVisibility(View.VISIBLE);
										desc.setText(R.string.powerMenuMain_SoftRebootDesc);
										desc.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor", "#000000")));
								}
								createCircleIcon(icon,icon2,getString(R.string.powerMenuMain_SoftReboot),getResources().getDrawable(R.drawable.ic_image_rotate_left),XposedMainActivity.preferences.getString("DialogSoftReboot_Backgroundcolor","#ffe91e63"),XposedMainActivity.preferences.getString("DialogSoftReboot_Textcolor","#ffffff"));
						}
						else if (position == XposedMainActivity.preferences.getInt("ScreenshotPosition", 3) && XposedMainActivity.preferences.getBoolean("ScreenshotEnabled", false))
						{
								root.setOnClickListener(screenshotOnClickListener);
								text.setText(getString(R.string.powerMenuMain_Screenshot));
								text.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor", "#000000")));
								if(!XposedMainActivity.preferences.getBoolean("Screenshot_HideDesc",false)) {
								desc.setVisibility(View.VISIBLE);
								if (XposedMainActivity.preferences.getLong("ScreenshotDelay", 1000) == 0)
								{
										desc.setText(getString(R.string.powerMenuMain_ScreenshotDesc).replace("[SCREENSHOTDELAY]", getString(R.string.advancedPrefs_DelayZero)));
								}
								else
								{
										desc.setText(getString(R.string.powerMenuMain_ScreenshotDesc).replace("[SCREENSHOTDELAY]", helper.getTimeString(XposedMainActivity.preferences.getLong("ScreenshotDelay", 1000), true)));
								}
								desc.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor", "#000000")));
								}
								createCircleIcon(icon,icon2,getString(R.string.powerMenuMain_Screenshot),getResources().getDrawable(R.drawable.ic_device_now_wallpaper),XposedMainActivity.preferences.getString("DialogScreenshot_Backgroundcolor","#ff3f51b5"),XposedMainActivity.preferences.getString("DialogScreenshot_Textcolor","#ffffff"));
						}
						else if (position == XposedMainActivity.preferences.getInt("ScreenrecordPosition", 4) && XposedMainActivity.preferences.getBoolean("ScreenrecordEnabled", false))
						{
								root.setOnClickListener(screenrecordOnClickListener);
								text.setText(getString(R.string.powerMenuMain_Screenrecord));
								text.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor", "#000000")));
								createCircleIcon(icon,icon2,getString(R.string.powerMenuMain_Screenrecord),getResources().getDrawable(R.drawable.ic_image_center_focus_weak),XposedMainActivity.preferences.getString("DialogScreenrecord_Backgroundcolor","#ff3f51b5"),XposedMainActivity.preferences.getString("DialogScreenrecord_Textcolor","#ffffff"));
						}
						else if (position == XposedMainActivity.preferences.getInt("FlashlightPosition", 5) && XposedMainActivity.preferences.getBoolean("FlashlightEnabled", false))
						{
								root.setOnClickListener(flashlightOnClickListener);
								text.setText((TorchService.getTorchState()==TorchService.TORCH_STATUS_OFF) ? getString(R.string.powerMenuMain_Flashlight) : getString(R.string.powerMenuMain_FlashlightOff));
								text.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor", "#000000")));
								if(!XposedMainActivity.preferences.getBoolean("Flashlight_HideDesc",false)) {
								desc.setVisibility(View.VISIBLE);
								if (XposedMainActivity.preferences.getLong("FlashlightAutoOff", 1000*60*10) == 0)
								{
										desc.setText(getString(R.string.powerMenuMain_FlashlightDescDisabled));
								}
								else
								{
										desc.setText(getString(R.string.powerMenuMain_FlashlightDesc).replace("[AUTOOFF]", helper.getTimeString(XposedMainActivity.preferences.getLong("FlashlightAutoOff", 1000*60*10), true)));
								}
								desc.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor", "#000000")));
								}
								createCircleIcon(icon,icon2,(TorchService.getTorchState()==TorchService.TORCH_STATUS_OFF) ? getString(R.string.powerMenuMain_Flashlight) : getString(R.string.powerMenuMain_FlashlightOff),getResources().getDrawable((TorchService.getTorchState()==TorchService.TORCH_STATUS_OFF) ? R.drawable.ic_qs_torch_on : R.drawable.ic_qs_torch_off),XposedMainActivity.preferences.getString("DialogFlashlight_Backgroundcolor","#ff3f51b5"),XposedMainActivity.preferences.getString("DialogFlashlight_Textcolor","#ffffff"));
						}
						else if (position == XposedMainActivity.preferences.getInt("ExpandedDesktopPosition", 6) && XposedMainActivity.preferences.getBoolean("ExpandedDesktopEnabled", false))
						{
								root.setOnClickListener(expandedDesktopOnClickListener);
								text.setText(getString(R.string.powerMenuMain_ExpandedDesktop));
								text.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor", "#000000")));
								createCircleIcon(icon,icon2,getString(R.string.powerMenuMain_ExpandedDesktop),getResources().getDrawable(R.drawable.ic_device_developer_mode),XposedMainActivity.preferences.getString("DialogExpandedDesktop_Backgroundcolor","#ff3f51b5"),XposedMainActivity.preferences.getString("DialogExpandedDesktop_Textcolor","#ffffff"));
						} 
						else if (position == XposedMainActivity.preferences.getInt("AirplaneModePosition",7) && XposedMainActivity.preferences.getBoolean("AirplaneModeEnabled",false))
						{
								root.setOnClickListener(airplaneModeOnClickListener);
								text.setText(getString(R.string.powerMenuMain_AirplaneMode));
								text.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor", "#000000")));
								int drawId = R.drawable.ic_device_airplanemode_on;
								try
								{
										drawId = android.provider.Settings.Global.getInt(mContext.getContentResolver(), android.provider.Settings.Global.AIRPLANE_MODE_ON) == 0 ? R.drawable.ic_device_airplanemode_on : R.drawable.ic_device_airplanemode_off;
								}
								catch (Settings.SettingNotFoundException e)
								{
										Log.w("NPM","Airplane mode Setting not found!\n"+e);
								}
								catch (Resources.NotFoundException e)
								{
										Log.w("NPM","Airplane mode not found!\n"+e);
								}
								createCircleIcon(icon, icon2, getString(R.string.powerMenuMain_AirplaneMode), getResources().getDrawable(drawId), XposedMainActivity.preferences.getString("DialogAirplaneMode_Backgroundcolor", "#ff3f51b5"), XposedMainActivity.preferences.getString("DialogAirplaneMode_Textcolor", "#ffffff"));
						}
						else if (position == XposedMainActivity.preferences.getInt("RestartUIPosition",8) && XposedMainActivity.preferences.getBoolean("RestartUIEnabled",false))
						{
								root.setOnClickListener(restartUIOnClickListener);
								text.setText(getString(R.string.powerMenuMain_RestartUI));
								text.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor", "#000000")));
								createCircleIcon(icon,icon2,getString(R.string.powerMenuMain_RestartUI),getResources().getDrawable(R.drawable.ic_alert_error),XposedMainActivity.preferences.getString("DialogRestartUI_Backgroundcolor","#ff3f51b5"),XposedMainActivity.preferences.getString("DialogRestartUI_Textcolor","#ffffff"));
						}
						else if (position == XposedMainActivity.preferences.getInt("SoundModePosition",9) && XposedMainActivity.preferences.getBoolean("SoundModeEnabled",false)) {
								root.setOnClickListener(soundModeOnClickListener);
								text.setText(getString(R.string.powerMenuMain_SoundMode));
								text.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor", "#000000")));
								int drawId = R.drawable.ic_av_volume_up;
								if(amRingerMode==AudioManager.RINGER_MODE_VIBRATE) {
										drawId = R.drawable.ic_notification_vibration;
								} else if (amRingerMode==AudioManager.RINGER_MODE_SILENT) {
										drawId = R.drawable.ic_av_volume_off;
								}
								if(!XposedMainActivity.preferences.getBoolean("SoundMode_HideDesc",false)) {
										desc.setVisibility(View.VISIBLE);
										String descText = getString(R.string.SoundMode_Normal);
										if (amRingerMode==AudioManager.RINGER_MODE_SILENT) {
												descText=getString(R.string.SoundMode_Silent);
										} else if (amRingerMode==AudioManager.RINGER_MODE_VIBRATE) {
												descText=getString(R.string.SoundMode_Vibrate);
										}
										desc.setText(mContext.getString(R.string.powerMenuMain_SoundModeDesc).replace("[SOUNDMODE]",descText));
										desc.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor", "#000000")));
								}
								createCircleIcon(icon,icon2,getString(R.string.powerMenuMain_SoundMode),getResources().getDrawable(drawId),XposedMainActivity.preferences.getString("DialogSoundMode_Backgroundcolor","#ff3f51b5"),XposedMainActivity.preferences.getString("DialogSoundMode_Textcolor","#ffffff"));
						}
						if (!text.getText().toString().equalsIgnoreCase("Text"))
						{
								ListContainer.addView(InflatedItem);
						}
				}
				
				ImageView icon,icon2;
				icon = (ImageView) view.findViewById(R.id.irecovery);
				icon2 = (ImageView) view.findViewById(R.id.irecovery2);
				createCircleIcon(icon,icon2,getString(R.string.powerMenuBottom_Recovery),getResources().getDrawable(R.drawable.ic_hardware_memory),XposedMainActivity.preferences.getString("DialogRecovery_Backgroundcolor","#ff8bc34a"),XposedMainActivity.preferences.getString("DialogRecovery_Textcolor","#ffffff"));
				
				icon = (ImageView) view.findViewById(R.id.ibootloader);
				icon2 = (ImageView) view.findViewById(R.id.ibootloader2);
				createCircleIcon(icon,icon2,getString(R.string.powerMenuBottom_Bootloader),getResources().getDrawable(R.drawable.ic_action_settings_backup_restore),XposedMainActivity.preferences.getString("DialogBootloader_Backgroundcolor","#ff277b71"),XposedMainActivity.preferences.getString("DialogBootloader_Textcolor","#ffffff"));

				icon = (ImageView) view.findViewById(R.id.isafe);
				icon2 = (ImageView) view.findViewById(R.id.isafe2);
				createCircleIcon(icon,icon2,getString(R.string.powerMenuBottom_SafeMode),getResources().getDrawable(R.drawable.ic_notification_sync_problem),XposedMainActivity.preferences.getString("DialogSafeMode_Backgroundcolor","#ff009688"),XposedMainActivity.preferences.getString("DialogSafeMode_Textcolor","#ffffff"));

        return view;

    }

		public static void createCircleIcon(ImageView background,ImageView foreground,String text,Drawable icon,String color1,String color2) {
				if (XposedMainActivity.preferences.getBoolean("UseGraphics",false)) {
						GraphicDrawable drawable = GraphicDrawable.builder().buildRound(null, Color.parseColor(color1));
						background.setImageDrawable(drawable);
						foreground.setColorFilter(Color.parseColor(color2),
																 android.graphics.PorterDuff.Mode.SRC_IN);
						foreground.setVisibility(View.VISIBLE);
						foreground.setImageDrawable(icon);
				} else {
						TextDrawable drawable = TextDrawable.builder().beginConfig().textColor(Color.parseColor(color2)).endConfig()
								.buildRound(text.substring(0, 1), Color.parseColor(color1));
						background.setImageDrawable(drawable);
						foreground.setVisibility(View.GONE);
				}
		}
		
    private static void setThreadPrio(int prio)
		{
        android.os.Process.setThreadPriority(prio);
    }

    private static class BackgroundThread extends Thread
		{
        private Object sCmd;

        private BackgroundThread(Object cmd)
				{
            this.sCmd = cmd;
        }

        @Override
        public void run()
				{
            super.run();
						if (!XposedMainActivity.previewMode)
						{
								setThreadPrio(BG_PRIO);

								if (sCmd == null)
										return;
										/**
										 * Sending a system broadcast to notify apps and the system that we're going down
										 * so that they write any outstanding data that might need to be flushed
										 */
										Shell.SU.run(SHUTDOWN_BROADCAST);
								

								new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
												@Override
												public void run()
												{
														if (sCmd instanceof String)
														{
																		if (sCmd.toString().equalsIgnoreCase(SHUTDOWN + "notusedjet"))
																		{
																				Intent broadcast =new Intent();
																				broadcast.setAction("de.NeonSoft.neopowermenu.Broadcast.poweroff");
																				mContext.sendBroadcast(broadcast);
																		}
																		else
																		{
																				Shell.SU.run((String) sCmd);
																		}
														}
														else if (sCmd instanceof String[])
														{
																Shell.SU.run((String[]) sCmd);
														}
												}
										}, RUNNABLE_DELAY_MS);
						}
        }
    }

    @Override
    public void onStart()
		{
        super.onStart();

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.0f;
				//windowParams.type = windowParams.TYPE_KEYGUARD_DIALOG;

        window.setAttributes(windowParams);
    }
		
    @Override
    public void onDismiss(final DialogInterface dialog)
		{
				if (canDismiss || XposedMainActivity.previewMode)
				{
						super.onDismiss(dialog);
						final Activity activity = getActivity();
        		if (activity != null && activity instanceof DialogInterface.OnDismissListener)
						{
           			((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        		}
				}
    }

		@Override
		public void onCancel(DialogInterface dialog)
		{
				// TODO: Implement this method
				if (canDismiss || XposedMainActivity.previewMode)
				{
						super.onCancel(dialog);
						final Activity activity = getActivity();
        		if (activity != null && activity instanceof DialogInterface.OnDismissListener)
						{
           			((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        		}
				}
		}

    private Point getLocationInView(View src, View target)
		{
        final int[] l0 = new int[2];
        src.getLocationOnScreen(l0);

        final int[] l1 = new int[2];
        target.getLocationOnScreen(l1);

        l1[0] = l1[0] - l0[0] + target.getWidth() / 2;
        l1[1] = l1[1] - l0[1] + target.getHeight() / 2;

        return new Point(l1[0], l1[1]);
    }

    @Override
    public void onActivityCreated(Bundle arg0)
		{
        super.onActivityCreated(arg0);
				int gravity = 0;
				if(boolean_DialogGravityTop) {
						gravity |= Gravity.TOP;
				} else if(boolean_DialogGravityBottom) {
						gravity |= Gravity.BOTTOM;
				} else {
						gravity |= Gravity.CENTER_VERTICAL;
				}
				if(boolean_DialogGravityLeft) {
						gravity |= Gravity.LEFT;
				} else if(boolean_DialogGravityRight) {
						gravity |= Gravity.RIGHT;
				} else {
						gravity |= Gravity.CENTER_HORIZONTAL;
				}
        getDialog().getWindow()
						.getAttributes().windowAnimations = R.style.DialogAnimation;
				getDialog().getWindow()
						.getAttributes().gravity = gravity;
    }
		
    private static void toggleTorch(boolean goToSleep) {
        try {
            Intent intent = new Intent(mContext, TorchService.class);
            intent.setAction(TorchService.ACTION_TOGGLE_TORCH);
            intent.putExtra(TorchService.EXTRA_GO_TO_SLEEP, goToSleep);
            mContext.startService(intent);
        } catch (Throwable t) {
            Log.e("TorchService","Error toggling Torch: " + t.getMessage());
        }
    }
		
}


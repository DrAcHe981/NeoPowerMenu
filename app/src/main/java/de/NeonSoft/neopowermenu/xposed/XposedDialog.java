package de.NeonSoft.neopowermenu.xposed;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.WindowManager.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.*;
import eu.chainfire.libsuperuser.*;
import java.util.*;
import android.view.View.*;

/**
 * Created by naman on 20/03/15.
 */
public class XposedDialog extends DialogFragment {

    public XposedDialog() {

    }
		
    private static final String CLASS_PHONE_WINDOW_MANAGER = "com.android.server.policy.PhoneWindowManager";
    private static Object mPhoneWindowManager;

		public static Context mContext;
		public static NotificationManager nfm;
		public static Notification.Builder notifyb;
		
		View singleTouch = null;
		boolean doubleToConfirm = false;
	
		View.OnClickListener powerOnClickListener,rebootOnClickListener,soft_rebootOnClickListener,screenshotOnClickListener;
		
		LinearLayout ListContainer;
		
    LinearLayout recovery, bootloader, safemode;
		TextView recoveryText, bootloaderText, safemodeText;
    FrameLayout frame, frame2;
		View seperator1;
    private CircularRevealView revealView;
    private View selectedView;
    private int backgroundColor;
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
						
		private static final String SCREENSHOT_CMD = "screencap -p /sdcard1/[FILENAME].png";

    private static final int BG_PRIO = android.os.Process.THREAD_PRIORITY_BACKGROUND;
    private static final int RUNNABLE_DELAY_MS = 5000;

		public static boolean canDismiss = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

				//doubleToConfirm = XposedMainActivity.preferences.getBoolean("DoubleTouchToConfirm",true);

        View view = inflater.inflate(R.layout.fragment_power, container, false);
				
				mContext = getDialog().getContext();
				nfm = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
				notifyb = new Notification.Builder(getActivity());
				
        revealView = (CircularRevealView) view.findViewById(R.id.reveal);
				backgroundColor = Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Backgroundcolor","#ffffff"));
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

        progress = (ProgressBar) view.findViewById(R.id.progress);

				seperator1.setBackgroundColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor","#000000")));
				recoveryText.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor","#000000")));
				bootloaderText.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor","#000000")));
				safemodeText.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor","#000000")));
				
        progress.getIndeterminateDrawable().setColorFilter(
                Color.parseColor("#ffffff"),
                android.graphics.PorterDuff.Mode.SRC_IN);
						
        powerOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
								canDismiss = false;
								
                final int color = Color.parseColor(XposedMainActivity.preferences.getString("DialogShutdown_Backgroundcolor","#d32f2f"));
                final Point p = getLocationInView(revealView, v);

                if (selectedView == v) {
                    revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
                    selectedView = null;
                } else {
                    revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
                    selectedView = v;
                }

                ((XposedMainActivity) getActivity()).revealFromTop();
                frame.setVisibility(View.GONE);
                frame2.setVisibility(View.VISIBLE);

                status.setText(R.string.powerMenuMain_Shutdown);
                status_detail.setText(R.string.powerMenu_Shuttingdown);
								progress.getIndeterminateDrawable().setColorFilter(
										Color.parseColor(XposedMainActivity.preferences.getString("DialogShutdown_Textcolor","#ffffff")),
										android.graphics.PorterDuff.Mode.SRC_IN);
								status.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogShutdown_Textcolor","#ffffff")));
								status_detail.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogShutdown_Textcolor","#ffffff")));

								if(!XposedMainActivity.previewMode) {
										getDialog().setCanceledOnTouchOutside(false);
										getDialog().setCancelable(false);
                new BackgroundThread(SHUTDOWN).start();
								}
            }
        };
        rebootOnClickListener = new View.OnClickListener() {
								@Override
								public void onClick(View v) {
												canDismiss = false;

												final int color = Color.parseColor(XposedMainActivity.preferences.getString("DialogReboot_Backgroundcolor","#3f51b5"));
												final Point p = getLocationInView(revealView, v);

												if (selectedView == v) {
														revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
														selectedView = null;
												} else {
														revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
														selectedView = v;
												}

												((XposedMainActivity) getActivity()).revealFromTop();
												frame.setVisibility(View.GONE);
												frame2.setVisibility(View.VISIBLE);

												status.setText(R.string.powerMenuMain_Reboot);
												status_detail.setText(R.string.powerMenu_Rebooting);
												progress.getIndeterminateDrawable().setColorFilter(
														Color.parseColor(XposedMainActivity.preferences.getString("DialogReboot_Textcolor","#ffffff")),
														android.graphics.PorterDuff.Mode.SRC_IN);
												status.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogReboot_Textcolor","#ffffff")));
												status_detail.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogReboot_Textcolor","#ffffff")));

										if(!XposedMainActivity.previewMode) {
												getDialog().setCanceledOnTouchOutside(false);
														getDialog().setCancelable(false);
												Intent takeScreenshotBC = new Intent();
												takeScreenshotBC.setAction("de.NeonSoft.neopowermenu.action.Reboot");
												XposedMainActivity.mContext.sendBroadcast(takeScreenshotBC);
														//new BackgroundThread(REBOOT_CMD).start();
												}
										}
						};
        soft_rebootOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
								canDismiss = false;
								
                final int color = Color.parseColor(XposedMainActivity.preferences.getString("DialogSoftReboot_Backgroundcolor","#e91e63"));
                final Point p = getLocationInView(revealView, v);

                if (selectedView == v) {
                    revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
                    selectedView = null;
                } else {
                    revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
                    selectedView = v;
                }

                ((XposedMainActivity) getActivity()).revealFromTop();
                frame.setVisibility(View.GONE);
                frame2.setVisibility(View.VISIBLE);

                status.setText(R.string.powerMenuMain_SoftReboot);
                status_detail.setText(R.string.powerMenu_Rebooting);
								progress.getIndeterminateDrawable().setColorFilter(
										Color.parseColor(XposedMainActivity.preferences.getString("DialogSoftReboot_Textcolor","#ffffff")),
										android.graphics.PorterDuff.Mode.SRC_IN);
								status.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogSoftReboot_Textcolor","#ffffff")));
								status_detail.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogSoftReboot_Textcolor","#ffffff")));

								if(!XposedMainActivity.previewMode) {
										getDialog().setCanceledOnTouchOutside(false);
										getDialog().setCancelable(false);
                new BackgroundThread(REBOOT_SOFT_REBOOT_CMD).start();
								}
            }
        };
				
				screenshotOnClickListener = new View.OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										if(!XposedMainActivity.previewMode) {
												dismiss();
												Handler handler = new Handler();
												handler.postDelayed(new Runnable() {

																@Override
																public void run()
																{
																		// TODO: Implement this method
																		Intent takeScreenshotBC = new Intent();
																		takeScreenshotBC.setAction("de.NeonSoft.neopowermenu.action.takeScreenshot");
																		XposedMainActivity.mContext.sendBroadcast(takeScreenshotBC);
																}
														}, XposedMainActivity.preferences.getLong("ScreenshotDelay", 1000));
												//takeScreenshot();
												//new BackgroundThread(SCREENSHOT_CMD).start();
										}
								}
						};
				
        recovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
								canDismiss = false;
								
                final int color = Color.parseColor(XposedMainActivity.preferences.getString("DialogRecovery_Backgroundcolor","#8bc34a"));
                final Point p = getLocationInView(revealView, v);

                if (selectedView == v) {
                    revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
                    selectedView = null;
                } else {
                    revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
                    selectedView = v;
                }

                ((XposedMainActivity) getActivity()).revealFromTop();
                frame.setVisibility(View.GONE);
                frame2.setVisibility(View.VISIBLE);

                status.setText(R.string.powerMenuBottom_Recovery);
                status_detail.setText(R.string.powerMenu_Rebooting);
								progress.getIndeterminateDrawable().setColorFilter(
										Color.parseColor(XposedMainActivity.preferences.getString("DialogRecovery_Textcolor","#ffffff")),
										android.graphics.PorterDuff.Mode.SRC_IN);
								status.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogRecovery_Textcolor","#ffffff")));
								status_detail.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogRecovery_Textcolor","#ffffff")));

								if(!XposedMainActivity.previewMode) {
										getDialog().setCanceledOnTouchOutside(false);
										getDialog().setCancelable(false);
										Intent takeScreenshotBC = new Intent();
										takeScreenshotBC.setAction("de.NeonSoft.neopowermenu.action.RebootRecovery");
										XposedMainActivity.mContext.sendBroadcast(takeScreenshotBC);
                //new BackgroundThread(REBOOT_RECOVERY_CMD).start();
								}
            }
        });
        bootloader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
								canDismiss = false;
								
                final int color = Color.parseColor(XposedMainActivity.preferences.getString("DialogBootloader_Backgroundcolor","#277b71"));
                final Point p = getLocationInView(revealView, v);

                if (selectedView == v) {
                    revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
                    selectedView = null;
                } else {
                    revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
                    selectedView = v;
                }

                ((XposedMainActivity) getActivity()).revealFromTop();
                frame.setVisibility(View.GONE);
                frame2.setVisibility(View.VISIBLE);

                status.setText(R.string.powerMenuBottom_Bootloader);
                status_detail.setText(R.string.powerMenu_Rebooting);
								progress.getIndeterminateDrawable().setColorFilter(
										Color.parseColor(XposedMainActivity.preferences.getString("DialogBootloader_Textcolor","#ffffff")),
										android.graphics.PorterDuff.Mode.SRC_IN);
								status.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogBootloader_Textcolor","#ffffff")));
								status_detail.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogBootloader_Textcolor","#ffffff")));

								if(!XposedMainActivity.previewMode) {
										getDialog().setCanceledOnTouchOutside(false);
										getDialog().setCancelable(false);
										Intent takeScreenshotBC = new Intent();
										takeScreenshotBC.setAction("de.NeonSoft.neopowermenu.action.RebootBootloader");
										XposedMainActivity.mContext.sendBroadcast(takeScreenshotBC);
                //new BackgroundThread(REBOOT_BOOTLOADER_CMD).start();
								}
            }
        });
        safemode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
								canDismiss = false;
								
                final int color = Color.parseColor(XposedMainActivity.preferences.getString("DialogSafeMode_Backgroundcolor","#009688"));
                final Point p = getLocationInView(revealView, v);

                if (selectedView == v) {
                    revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
                    selectedView = null;
                } else {
                    revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
                    selectedView = v;
                }

                ((XposedMainActivity) getActivity()).revealFromTop();
                frame.setVisibility(View.GONE);
                frame2.setVisibility(View.VISIBLE);

                status.setText(R.string.powerMenuBottom_SafeMode);
                status_detail.setText(R.string.powerMenu_Rebooting);
								progress.getIndeterminateDrawable().setColorFilter(
										Color.parseColor(XposedMainActivity.preferences.getString("DialogSafeMode_Textcolor","#ffffff")),
										android.graphics.PorterDuff.Mode.SRC_IN);
								status.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogSafeMode_Textcolor","#ffffff")));
								status_detail.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogSafeMode_Textcolor","#ffffff")));

								if(!XposedMainActivity.previewMode) {
										getDialog().setCanceledOnTouchOutside(false);
										getDialog().setCancelable(false);
                new BackgroundThread(REBOOT_SAFE_MODE).start();
								}
            }
        });
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
				
				for (int position = 0;position < 4;position++) {
						View InflatedItem = inflater.inflate(R.layout.powermenu_listitem,null);
						LinearLayout root = (LinearLayout) InflatedItem.findViewById(R.id.powermenuitemRoot);
						ImageView icon = (ImageView) InflatedItem.findViewById(R.id.powermenuIcon);
						TextView text = (TextView) InflatedItem.findViewById(R.id.powermenuText);
						TextView desc = (TextView) InflatedItem.findViewById(R.id.powermenuDesc);
						desc.setVisibility(View.GONE);

						if (position == XposedMainActivity.preferences.getInt("ShutdownPosition",0) && XposedMainActivity.preferences.getBoolean("ShutdownEnabled",true) ) {
								root.setOnClickListener(powerOnClickListener);
								text.setText(getString(R.string.powerMenuMain_Shutdown));
								text.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor","#000000")));
								TextDrawable drawable = TextDrawable.builder().beginConfig().textColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogShutdown_Textcolor","#ffffff"))).endConfig()
										.buildRound(getString(R.string.powerMenuMain_Shutdown).substring(0,1), Color.parseColor(XposedMainActivity.preferences.getString("DialogShutdown_Backgroundcolor","#ffd32f2f")));
								icon.setImageDrawable(drawable);
						} else if (position == XposedMainActivity.preferences.getInt("RebootPosition",1) && XposedMainActivity.preferences.getBoolean("RebootEnabled",true) ) {
								root.setOnClickListener(rebootOnClickListener);
								text.setText(getString(R.string.powerMenuMain_Reboot));
								text.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor","#000000")));
								TextDrawable drawable = TextDrawable.builder().beginConfig().textColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogReboot_Textcolor","#ffffff"))).endConfig()
										.buildRound(getString(R.string.powerMenuMain_Reboot).substring(0,1), Color.parseColor(XposedMainActivity.preferences.getString("DialogReboot_Backgroundcolor","#ff3f51b5")));
								icon.setImageDrawable(drawable);
						} else if (position == XposedMainActivity.preferences.getInt("SoftRebootPosition",2) && XposedMainActivity.preferences.getBoolean("SoftRebootEnabled",true) ) {
								root.setOnClickListener(soft_rebootOnClickListener);
								text.setText(getString(R.string.powerMenuMain_SoftReboot));
								text.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor","#000000")));
								desc.setVisibility(View.VISIBLE);
								desc.setText(R.string.powerMenuMain_SoftRebootDesc);
								desc.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor","#000000")));
								TextDrawable drawable = TextDrawable.builder().beginConfig().textColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogSoftReboot_Textcolor","#ffffff"))).endConfig()
										.buildRound(getString(R.string.powerMenuMain_SoftReboot).substring(0,1), Color.parseColor(XposedMainActivity.preferences.getString("DialogSoftReboot_Backgroundcolor","#ffe91e63")));
								icon.setImageDrawable(drawable);
						} else if (position == XposedMainActivity.preferences.getInt("ScreenshotPosition",4) && XposedMainActivity.preferences.getBoolean("ScreenshotEnabled",true) ) {
								root.setOnClickListener(screenshotOnClickListener);
								text.setText(getString(R.string.powerMenuMain_Screenshot));
								text.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor","#000000")));
								desc.setVisibility(View.VISIBLE);
								if(XposedMainActivity.preferences.getLong("ScreenshotDelay",1000)==0) {
										desc.setText(getString(R.string.powerMenuMain_ScreenshotDesc).replace("[SCREENSHOTDELAY]",getString(R.string.advancedPrefs_DelayZero)));
								} else {
										desc.setText(getString(R.string.powerMenuMain_ScreenshotDesc).replace("[SCREENSHOTDELAY]",helper.getTimeString(XposedMainActivity.preferences.getLong("ScreenshotDelay",1000),false))+" (m:s)");
								}
								desc.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor","#000000")));
								TextDrawable drawable = TextDrawable.builder().beginConfig().textColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor","#ffffff"))).endConfig()
										.buildRound(getString(R.string.powerMenuMain_Screenshot).substring(0,1), Color.parseColor(XposedMainActivity.preferences.getString("RevealBackground","#ffffff")));
								icon.setImageDrawable(drawable);
						}
						if (!text.getText().toString().equalsIgnoreCase("Text")) {
								ListContainer.addView(InflatedItem);
						}
				}
				
        TextDrawable drawableRecovery = TextDrawable.builder().beginConfig().textColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogRecovery_Textcolor","#ffffff"))).endConfig()
						.buildRound(getString(R.string.powerMenuBottom_Recovery).substring(0,1), Color.parseColor(XposedMainActivity.preferences.getString("DialogRecovery_Backgroundcolor","#ff009688")));
        ((ImageView) view.findViewById(R.id.irecovery)).setImageDrawable(drawableRecovery);
				
        TextDrawable drawableBootloader = TextDrawable.builder().beginConfig().textColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogBootloader_Textcolor","#ffffff"))).endConfig()
                .buildRound(getString(R.string.powerMenuBottom_Bootloader).substring(0,1), Color.parseColor(XposedMainActivity.preferences.getString("DialogBootloader_Backgroundcolor","#009688")));
        ((ImageView) view.findViewById(R.id.ibootloader)).setImageDrawable(drawableBootloader);

        TextDrawable drawableSafeMode = TextDrawable.builder().beginConfig().textColor(Color.parseColor(XposedMainActivity.preferences.getString("DialogSafeMode_Textcolor","#ffffff"))).endConfig()
						.buildRound(getString(R.string.powerMenuBottom_SafeMode).substring(0,1), Color.parseColor(XposedMainActivity.preferences.getString("DialogSafeMode_Backgroundcolor","#009688")));
        ((ImageView) view.findViewById(R.id.isafe)).setImageDrawable(drawableSafeMode);
				

        return view;

    }

    private static void setThreadPrio(int prio) {
        android.os.Process.setThreadPriority(prio);
    }

    private static class BackgroundThread extends Thread {
        private Object sCmd;

        private BackgroundThread(Object cmd) {
            this.sCmd = cmd;
        }

        @Override
        public void run() {
            super.run();
						if(!XposedMainActivity.previewMode) {
            setThreadPrio(BG_PRIO);

            if (sCmd == null)
                return;
						if (!sCmd.toString().equalsIgnoreCase(SCREENSHOT_CMD)) {
            /**
             * Sending a system broadcast to notify apps and the system that we're going down
             * so that they write any outstanding data that might need to be flushed
             */
            Shell.SU.run(SHUTDOWN_BROADCAST);
						}

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
										if (sCmd instanceof String) {
												Date now = new Date();
												if (sCmd.toString().equalsIgnoreCase(SCREENSHOT_CMD)) {
														try {
																takeScreenshot();
														} catch (Throwable t) {
																Log.e("NeoPowerMenu",""+t);
														}
												} else { 
														if(sCmd.toString().equalsIgnoreCase(SHUTDOWN+"notusedjet")) {
																Intent broadcast =new Intent();
																broadcast.setAction("de.NeonSoft.neopowermenu.Broadcast.poweroff");
																mContext.sendBroadcast(broadcast);
														}
														else {
																Shell.SU.run((String) sCmd);
														}
												}
										}
                   	else if (sCmd instanceof String[]) {
                    		Shell.SU.run((String[]) sCmd);
										}
                }
            }, RUNNABLE_DELAY_MS);
						}
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.0f;

        window.setAttributes(windowParams);
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
				if(canDismiss || XposedMainActivity.previewMode) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        		if (activity != null && activity instanceof DialogInterface.OnDismissListener) {
           			((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        		}
				}
    }
		
		@Override
		public void onCancel(DialogInterface dialog)
		{
				// TODO: Implement this method
				if(canDismiss || XposedMainActivity.previewMode) {
						super.onCancel(dialog);
						final Activity activity = getActivity();
        		if (activity != null && activity instanceof DialogInterface.OnDismissListener) {
           			((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        		}
				}
		}

    private Point getLocationInView(View src, View target) {
        final int[] l0 = new int[2];
        src.getLocationOnScreen(l0);

        final int[] l1 = new int[2];
        target.getLocationOnScreen(l1);

        l1[0] = l1[0] - l0[0] + target.getWidth() / 2;
        l1[1] = l1[1] - l0[1] + target.getHeight() / 2;

        return new Point(l1[0], l1[1]);
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }
		

    private static final Object mScreenshotLock = new Object();
    private static ServiceConnection mScreenshotConnection = null;  
    private static void takeScreenshot() {
				return;
    }
		
}


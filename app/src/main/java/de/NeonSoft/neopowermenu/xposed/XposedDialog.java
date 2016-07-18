package de.NeonSoft.neopowermenu.xposed;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.os.*;
import android.provider.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.android.internal.os.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.*;
import de.NeonSoft.neopowermenu.services.*;
import de.NeonSoft.neopowermenu.xposed.*;
import eu.chainfire.libsuperuser.*;

import de.NeonSoft.neopowermenu.R;
import de.NeonSoft.neopowermenu.Preferences.*;
import android.provider.Settings.*;
import android.content.res.Resources.*;

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

		boolean firstTouch = false;
		View firstTouchOn = null;
		boolean doubleToConfirm = false;
		
		private boolean HookShutdownThread = false;

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
		public static int amRingerMode;
		
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
		{

				//doubleToConfirm = XposedMainActivity.preferences.getBoolean("DoubleTouchToConfirm",true);

        View view;
				if(XposedMainActivity.sStyleName.equalsIgnoreCase("Material (Fullscreen)")) {
						view = inflater.inflate(R.layout.fragment_powerfullscreen,container,false);
				} else {
						view = inflater.inflate(R.layout.fragment_power,container,false);
				}

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

				/*if(XposedMainActivity.sStyleName.equalsIgnoreCase("Material (Fullscreen)")) {
						LayoutParams fllp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
						dialogMain.setLayoutParams(fllp);
				}*/
				//TextDrawable progressbgd = TextDrawable.builder().buildRound("",Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Backgroundcolor","#ffffff")));
				//progressbg.setImageDrawable(progressbgd);
        progress.getIndeterminateDrawable().setColorFilter(
						Color.parseColor("#ffffff"),
						android.graphics.PorterDuff.Mode.SRC_IN);
				
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

				int i = 0;
				while (XposedMainActivity.orderPrefs.getInt(i+"_item_type",-1) != -1) {
						i++;
				}
				if(i==0) {
						XposedMainActivity.orderPrefs.edit().putInt("0_item_type",visibilityOrderNew_ListAdapter.TYPE_NORMAL).commit();
						XposedMainActivity.orderPrefs.edit().putString("0_item_title","Shutdown").commit();
						XposedMainActivity.orderPrefs.edit().putInt("1_item_type",visibilityOrderNew_ListAdapter.TYPE_NORMAL).commit();
						XposedMainActivity.orderPrefs.edit().putString("1_item_title","Reboot").commit();
						XposedMainActivity.orderPrefs.edit().putInt("2_item_type",visibilityOrderNew_ListAdapter.TYPE_NORMAL).commit();
						XposedMainActivity.orderPrefs.edit().putString("2_item_title","SoftReboot").commit();
						XposedMainActivity.orderPrefs.edit().putInt("3_item_type",visibilityOrderNew_ListAdapter.TYPE_MULTI).commit();
						XposedMainActivity.orderPrefs.edit().putString("3_item1_title","Recovery").commit();
						XposedMainActivity.orderPrefs.edit().putString("3_item2_title","Bootloader").commit();
						XposedMainActivity.orderPrefs.edit().putString("3_item3_title","SafeMode").commit();
				}
				i = 0;
				while (XposedMainActivity.orderPrefs.getInt(i+"_item_type",-1) != -1) {
						View inflated = null;
						int type = XposedMainActivity.orderPrefs.getInt(i+"_item_type",visibilityOrderNew_ListAdapter.TYPE_NORMAL);
						if(type == visibilityOrderNew_ListAdapter.TYPE_NORMAL) {
								final String title = XposedMainActivity.orderPrefs.getString(i+"_item_title","null");
								inflated = inflater.inflate(R.layout.powermenu_normal, null, false);

								LinearLayout root = (LinearLayout) inflated.findViewById(R.id.powermenunormal_root);
								ImageView icon = (ImageView) inflated.findViewById(R.id.powermenunormal_icon);
								ImageView icon2 = (ImageView) inflated.findViewById(R.id.powermenunormal_icon2);
								icon2.setVisibility(View.GONE);
								TextView text = (TextView) inflated.findViewById(R.id.powermenunormal_text1);
								TextView desc = (TextView) inflated.findViewById(R.id.powermenunormal_text2);
								desc.setVisibility(View.GONE);
								
								if(!title.equalsIgnoreCase("Empty")) {
								String string = "Failed to get String resource for "+ title;
								try {
										string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_"+title,"string",MainActivity.class.getPackage().getName()));
								}
								catch (Throwable t) {
										try {
												string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_"+title,"string",MainActivity.class.getPackage().getName()));
										}
										catch (Throwable t1) {
										}
								}
								text.setText(string);
								text.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor", "#000000")));
								
								if(!XposedMainActivity.preferences.getBoolean(title+"_HideDesc",false)) {
								String descString = "Failed to get String resource for "+ title;
								try {
										descString = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_"+title+"Desc","string",MainActivity.class.getPackage().getName()));
										desc.setVisibility(View.VISIBLE);
								}
								catch (Throwable t) {
										try {
												descString = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_"+title+"Desc","string",MainActivity.class.getPackage().getName()));
												desc.setVisibility(View.VISIBLE);
										}
										catch (Throwable t1) {
										}
								}
								if (XposedMainActivity.preferences.getLong("ScreenshotDelay", 1000) == 0)
								{
										descString = descString.replace("[SCREENSHOTDELAY]", getString(R.string.advancedPrefs_DelayZero));
								}
								else
								{
										descString = descString.replace("[SCREENSHOTDELAY]", helper.getTimeString(XposedMainActivity.preferences.getLong("ScreenshotDelay", 1000), true));
								}
								descString = descString.replace("[AUTOOFF]", helper.getTimeString(XposedMainActivity.preferences.getLong("FlashlightAutoOff", 1000*60*10), true));
								String descText = getString(R.string.SoundMode_Normal);
								if (amRingerMode==AudioManager.RINGER_MODE_SILENT) {
										descText=getString(R.string.SoundMode_Silent);
								} else if (amRingerMode==AudioManager.RINGER_MODE_VIBRATE) {
										descText=getString(R.string.SoundMode_Vibrate);
								}
								descString = descString.replace("[SOUNDMODE]",descText);
								desc.setText(descString);
								}
								
								desc.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor", "#000000")));
								
								createCircleIcon(icon,icon2,string,XposedMainActivity.preferences.getString("Dialog"+title+"_Backgroundcolor","#ff000000"),XposedMainActivity.preferences.getString("Dialog"+title+"_Textcolor","#ffffff"));
								
								root.setOnClickListener(new OnClickListener() {

												@Override
												public void onClick(View p1)
												{
														// TODO: Implement this method
														performMenuClick(title,p1);
												}
										});
								} else {
										root.setVisibility(View.INVISIBLE);
								}
						} else if (type == visibilityOrderNew_ListAdapter.TYPE_MULTI) {
								inflated = inflater.inflate(R.layout.powermenu_multi, null, false);
								
										final String title = XposedMainActivity.orderPrefs.getString(i+"_item1_title","null");
										LinearLayout root = (LinearLayout) inflated.findViewById(R.id.powermenumulti_item1);
										ImageView icon = (ImageView) inflated.findViewById(R.id.powermenumulti_item1icon);
										ImageView icon2 = (ImageView) inflated.findViewById(R.id.powermenumulti_item1icon2);
										icon2.setVisibility(View.GONE);
										TextView text = (TextView) inflated.findViewById(R.id.powermenumulti_item1text);

										if(!title.equalsIgnoreCase("Empty")) {
										String string = "Failed to get String resource for "+ title;
										try {
												string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_"+title,"string",MainActivity.class.getPackage().getName()));
										}
										catch (Throwable t) {
												try {
														string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_"+title,"string",MainActivity.class.getPackage().getName()));
												}
												catch (Throwable t1) {
												}
										}
								text.setText(string);
								text.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor", "#000000")));
										
								createCircleIcon(icon,icon2,string,XposedMainActivity.preferences.getString("Dialog"+title+"_Backgroundcolor","#ff000000"),XposedMainActivity.preferences.getString("Dialog"+title+"_Textcolor","#ffffff"));

								root.setOnClickListener(new OnClickListener() {

												@Override
												public void onClick(View p1)
												{
														// TODO: Implement this method
														performMenuClick(title,p1);
												}
										});
						} else {
								root.setVisibility(View.GONE);
						}
										
								final String title2 = XposedMainActivity.orderPrefs.getString(i+"_item2_title","null");
								LinearLayout root2 = (LinearLayout) inflated.findViewById(R.id.powermenumulti_item2);
								ImageView iconitem2 = (ImageView) inflated.findViewById(R.id.powermenumulti_item2icon);
								ImageView icon2item2 = (ImageView) inflated.findViewById(R.id.powermenumulti_item2icon2);
								icon2item2.setVisibility(View.GONE);
								TextView text2 = (TextView) inflated.findViewById(R.id.powermenumulti_item2text);
								if(!title2.equalsIgnoreCase("Empty")) {
								String string2 = "Failed to get String resource for "+ title2;
								try {
										string2 = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_"+title2,"string",MainActivity.class.getPackage().getName()));
								}
								catch (Throwable t) {
										try {
												string2 = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_"+title2,"string",MainActivity.class.getPackage().getName()));
										}
										catch (Throwable t1) {
										}
								}
								text2.setText(string2);
								text2.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor", "#000000")));

								createCircleIcon(iconitem2,icon2item2,string2,XposedMainActivity.preferences.getString("Dialog"+title2+"_Backgroundcolor","#ff000000"),XposedMainActivity.preferences.getString("Dialog"+title2+"_Textcolor","#ffffff"));

								root2.setOnClickListener(new OnClickListener() {

												@Override
												public void onClick(View p1)
												{
														// TODO: Implement this method
														performMenuClick(title2,p1);
												}
										});
								} else {
										root2.setVisibility(View.GONE);
								}
								
								final String title3 = XposedMainActivity.orderPrefs.getString(i+"_item3_title","null");
								LinearLayout root3 = (LinearLayout) inflated.findViewById(R.id.powermenumulti_item3);
								ImageView iconitem3 = (ImageView) inflated.findViewById(R.id.powermenumulti_item3icon);
								ImageView icon2item3 = (ImageView) inflated.findViewById(R.id.powermenumulti_item3icon2);
								icon2item3.setVisibility(View.GONE);
								TextView text3 = (TextView) inflated.findViewById(R.id.powermenumulti_item3text);

								if(!title3.equalsIgnoreCase("Empty")) {
								String string3 = "Failed to get String resource for "+ title3;
								try {
										string3 = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_"+title3,"string",MainActivity.class.getPackage().getName()));
								}
								catch (Throwable t) {
										try {
												string3 = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_"+title3,"string",MainActivity.class.getPackage().getName()));
										}
										catch (Throwable t1) {
										}
								}
								text3.setText(string3);
								text3.setTextColor(Color.parseColor(XposedMainActivity.preferences.getString("Dialog_Textcolor", "#000000")));

								createCircleIcon(iconitem3,icon2item3,string3,XposedMainActivity.preferences.getString("Dialog"+title3+"_Backgroundcolor","#ff000000"),XposedMainActivity.preferences.getString("Dialog"+title3+"_Textcolor","#ffffff"));

								root3.setOnClickListener(new OnClickListener() {

												@Override
												public void onClick(View p1)
												{
														// TODO: Implement this method
														performMenuClick(title3,p1);
												}
												});
								} else {
										root3.setVisibility(View.GONE);
								}
						}
						ListContainer.addView(inflated);
						i++;
				}
				
        return view;

    }

		public static void createCircleIcon(ImageView background,ImageView foreground,String text,String color1,String color2) {
				try {
				if (XposedMainActivity.preferences.getBoolean("UseGraphics",false)) {
						GraphicDrawable drawable = GraphicDrawable.builder().buildRound(null, Color.parseColor(color1));
						background.setImageDrawable(drawable);
						foreground.setColorFilter(Color.parseColor(color2),
																 android.graphics.PorterDuff.Mode.SRC_IN);
						foreground.setVisibility(View.VISIBLE);
						if(text.equalsIgnoreCase(mContext.getString(R.string.powerMenuMain_Shutdown))) {
								foreground.setImageDrawable(mContext.getResources().getDrawable(R.drawable.poweroff1));
						} else if(text.equalsIgnoreCase(mContext.getString(R.string.powerMenuMain_Reboot))) {
								foreground.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_av_loop));
						} else if(text.equalsIgnoreCase(mContext.getString(R.string.powerMenuMain_SoftReboot))) {
								foreground.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_image_rotate_left));
						} else if(text.equalsIgnoreCase(mContext.getString(R.string.powerMenuMain_Screenshot))) {
								foreground.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_device_now_wallpaper));
						} else if(text.equalsIgnoreCase(mContext.getString(R.string.powerMenuMain_Screenrecord))) {
								foreground.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_image_center_focus_weak));
						} else if(text.equalsIgnoreCase(mContext.getString(R.string.powerMenuMain_Flashlight))) {
								foreground.setImageDrawable(mContext.getResources().getDrawable((TorchService.getTorchState()==TorchService.TORCH_STATUS_OFF) ? R.drawable.ic_qs_torch_on : R.drawable.ic_qs_torch_off));
						} else if(text.equalsIgnoreCase(mContext.getString(R.string.powerMenuMain_ExpandedDesktop))) {
								foreground.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_device_developer_mode));
						} else if(text.equalsIgnoreCase(mContext.getString(R.string.powerMenuMain_AirplaneMode))) {
								try
								{
										foreground.setImageDrawable(mContext.getResources().getDrawable(android.provider.Settings.Global.getInt(mContext.getContentResolver(), android.provider.Settings.Global.AIRPLANE_MODE_ON) == 0 ? R.drawable.ic_device_airplanemode_on : R.drawable.ic_device_airplanemode_off));
								}
								catch (Throwable e)
								{
										foreground.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_device_airplanemode_on));
								}
						} else if (text.equalsIgnoreCase(mContext.getString(R.string.powerMenuMain_RestartUI))) {
								foreground.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_alert_error));
						} else if(text.equalsIgnoreCase(mContext.getString(R.string.powerMenuMain_SoundMode))) {
								foreground.setImageDrawable(mContext.getResources().getDrawable(amRingerMode==AudioManager.RINGER_MODE_VIBRATE ? R.drawable.ic_notification_vibration : (amRingerMode==AudioManager.RINGER_MODE_SILENT ? R.drawable.ic_av_volume_off : R.drawable.ic_av_volume_up)));
						} else if(text.equalsIgnoreCase(mContext.getString(R.string.powerMenuBottom_Recovery))) {
								foreground.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_hardware_memory));
						} else if(text.equalsIgnoreCase(mContext.getString(R.string.powerMenuBottom_Bootloader))) {
								foreground.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_action_settings_backup_restore));
						} else if(text.equalsIgnoreCase(mContext.getString(R.string.powerMenuBottom_SafeMode))) {
								foreground.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_notification_sync_problem));
						}
				} else {
						TextDrawable drawable = TextDrawable.builder().beginConfig().textColor(Color.parseColor(color2)).endConfig()
								.buildRound(text.substring(0, 1), Color.parseColor(color1));
						background.setImageDrawable(drawable);
						foreground.setVisibility(View.GONE);
				}
				} catch (Throwable t) {
						Log.e("NPM","Failed to create Circke Icon.",t);
				}
		}
		
		private void performMenuClick(String name,View v) {
				if(name.equalsIgnoreCase("Shutdown")) {
						if(doubleToConfirm) {
								if(!firstTouch && firstTouchOn != v) {
										firstTouch = true;
										firstTouchOn = v;
										for(int i =0;i<ListContainer.getChildCount();i++) {
												View child = ListContainer.getChildAt(i);
												if(child!=v) {
														child.setAlpha((float) .3);
														//child.setEnabled(false);
												}
										}
								} else if (firstTouch && firstTouchOn!=v) {
										firstTouch = false;
										firstTouchOn = null;
										for(int i =0;i<ListContainer.getChildCount();i++) {
												View child = ListContainer.getChildAt(i);
												//if(child!=v) {
												child.setAlpha((float) 1);
												//child.setEnabled(true);
												//}
										}
								}
								return;
						} else {
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
				} else if(name.equalsIgnoreCase("Reboot")) {
						if(doubleToConfirm) {
								if(!firstTouch && firstTouchOn != v) {
										firstTouch = true;
										firstTouchOn = v;
										for(int i =0;i<ListContainer.getChildCount();i++) {
												View child = ListContainer.getChildAt(i);
												if(child!=v) {
														child.setAlpha((float) .3);
														//child.setEnabled(false);
												}
										}
								} else if (firstTouch && firstTouchOn!=v) {
										firstTouch = false;
										firstTouchOn = null;
										for(int i =0;i<ListContainer.getChildCount();i++) {
												View child = ListContainer.getChildAt(i);
												//if(child!=v) {
												child.setAlpha((float) 1);
												//child.setEnabled(true);
												//}
										}
								}
								return;
						} else {
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
				} else if (name.equalsIgnoreCase("SoftReboot")) {
						if(doubleToConfirm) {
								if(!firstTouch && firstTouchOn != v) {
										firstTouch = true;
										firstTouchOn = v;
										for(int i =0;i<ListContainer.getChildCount();i++) {
												View child = ListContainer.getChildAt(i);
												if(child!=v) {
														child.setAlpha((float) .3);
														//child.setEnabled(false);
												}
										}
								} else if (firstTouch && firstTouchOn!=v) {
										firstTouch = false;
										firstTouchOn = null;
										for(int i =0;i<ListContainer.getChildCount();i++) {
												View child = ListContainer.getChildAt(i);
												//if(child!=v) {
												child.setAlpha((float) 1);
												//child.setEnabled(true);
												//}
										}
								}
								return;
						} else {
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
				} else if(name.equalsIgnoreCase("Screenshot")) {
						if (firstTouch && firstTouchOn!=v) {
								firstTouch = false;
								firstTouchOn = null;
								for(int i =0;i<ListContainer.getChildCount();i++) {
										View child = ListContainer.getChildAt(i);
										//if(child!=v) {
										child.setAlpha((float) 1);
										//child.setEnabled(true);
										//}
								}
						} else {
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
																//Toast.makeText(mContext, "Taking screenshot...",Toast.LENGTH_SHORT).show();
														}
												}, XposedMainActivity.preferences.getLong("ScreenshotDelay", 1000));
								}
						}
				} else if(name.equalsIgnoreCase("Screenrecord")) {
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
														//Toast.makeText(mContext, "Taking screenrecord...",Toast.LENGTH_SHORT).show();
												}
										}, 1000);
						}
				} else if(name.equalsIgnoreCase("Flashlight")) {
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
				} else if(name.equalsIgnoreCase("ExpandedDesktop")) {
						if(!XposedMainActivity.previewMode) {
								dismiss();
								Intent launchIntent = new Intent("gravitybox.intent.action.TOGGLE_EXPANDED_DESKTOP");
								mContext.sendBroadcast(launchIntent);
						}
				} else if(name.equalsIgnoreCase("AirplaneMode")) {
						if(!XposedMainActivity.previewMode) {
								dismiss();
								Intent launchIntent = new Intent(XposedMain.NPM_ACTION_BROADCAST_TOGGLEAIRPLANEMODE);
								mContext.sendBroadcast(launchIntent);
						}
				} else if(name.equalsIgnoreCase("RestartUI")) {
						if (!XposedMainActivity.previewMode)
						{
								dismiss();
								Intent launchIntent = new Intent(XposedMain.NPM_ACTION_BROADCAST_KILLSYSTEMUI);
								mContext.sendBroadcast(launchIntent);
						}
				} else if(name.equalsIgnoreCase("SoundMode")) {
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
				} else if(name.equalsIgnoreCase("Recovery")) {
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
				} else if(name.equalsIgnoreCase("Bootlooder")) {
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
				} else if(name.equalsIgnoreCase("SafeMode")) {
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
						try {
								mContext.unregisterReceiver(XposedMainActivity.mReceiver);
						} catch (Throwable t) {}
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
						try {
								mContext.unregisterReceiver(XposedMainActivity.mReceiver);
						} catch (Throwable t) {}
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


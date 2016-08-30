package de.NeonSoft.neopowermenu.xposed;

import android.animation.*;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.widget.*;
import com.nostra13.universalimageloader.core.assist.*;
import com.nostra13.universalimageloader.core.listener.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.Preferences.*;
import de.NeonSoft.neopowermenu.helpers.*;
import de.NeonSoft.neopowermenu.services.*;
import eu.chainfire.libsuperuser.*;
import java.io.*;
import java.util.*;

/**
 * Created by naman on 20/03/15.
 */
public class XposedDialog extends DialogFragment
{

    public XposedDialog()
		{

    }

		public static Context mContext;
		public static Handler mHandler;
		public static LayoutInflater mInflater;
		public static NotificationManager nfm;
		public static Notification.Builder notifyb;

		boolean firstTouch = false;
		View firstTouchOn = null;
		boolean doubleToConfirm = false;
		boolean RequireConfirmation = false;
		String confirmDialog = "";
		static boolean isDismissing = false;
		public static String SubDialogParent = "root";
		public static String inSubDialog = null;
		
		boolean GraphicBehindProgress = false;
		
		private boolean HookShutdownThread = false;

		static LinearLayout dialogMain;
		FrameLayout dialogContent;
		LinearLayout ListContainer, ListContainer2;
		
    FrameLayout frame, frame2, frame3, frameConfirm;
		LinearLayout frameLinear, frame2Linear, frame3Linear, frameConfirmLinear;
    private CircularRevealView revealView;
    private View selectedView;
    private int backgroundColor;
		ImageView progressbg;
    ProgressBar progress;
    TextView status, status_detail, confirmAction, confirmNo, confirmYes;

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
		
		Runnable mRun;
		Object[] soundModeIcon = {null,""};
		int airplaneMode = 0;
		Object[] airplaneModeIcon = {null,""};
		boolean flashlightOn = false;
		Object[] flashlightIcon = {null,""};
		
		ArrayList<Integer> types;
		ArrayList<String> names;
		ArrayList<String> items;
		
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
		{

				//doubleToConfirm = XposedMainActivity.preferences.getBoolean("DoubleTouchToConfirm",true);
				mInflater = inflater;
				isDismissing = false;
				canDismiss = true;
				mHandler = new Handler();
				try
				{
						airplaneMode = android.provider.Settings.Global.getInt(mContext.getContentResolver(), android.provider.Settings.Global.AIRPLANE_MODE_ON);
				}
				catch (Throwable e)
				{}
        View view = inflater.inflate(R.layout.fragment_power,container,false);
				

				mContext = getActivity();

				am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
				amRingerMode = am.getRingerMode();
			
				RequireConfirmation = XposedMainActivity.preferences.getBoolean("RequireConfirmation",false);
				
				boolean_DialogGravityTop = XposedMainActivity.preferences.getBoolean("DialogGravityTop",false);
				boolean_DialogGravityLeft = XposedMainActivity.preferences.getBoolean("DialogGravityLeft",false);
				boolean_DialogGravityRight = XposedMainActivity.preferences.getBoolean("DialogGravityRight",false);
				boolean_DialogGravityBottom = XposedMainActivity.preferences.getBoolean("DialogGravityBottom",false);
				
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
				dialogMain = (LinearLayout) view.findViewById(R.id.fragmentpowerFrameLayout_Main);
				
				dialogContent = (FrameLayout) view.findViewById(R.id.fragmentpowerFrameLayout1);
				
        revealView = (CircularRevealView) view.findViewById(R.id.reveal);
				backgroundColor = Color.parseColor(XposedMainActivity.colorPrefs.getString("Dialog_Backgroundcolor", "#ffffff"));
				ListContainer = (LinearLayout) view.findViewById(R.id.ListContainer);
				
        frame = (FrameLayout) view.findViewById(R.id.frame);
				dialogContent.setBackgroundColor(backgroundColor);
				frameLinear = (LinearLayout) view.findViewById(R.id.frameLinear);
        frame2 = (FrameLayout) view.findViewById(R.id.frame2);
				frame2.setVisibility(View.GONE);
				frame2Linear = (LinearLayout) view.findViewById(R.id.frame2Linear);
				frame3 = (FrameLayout) view.findViewById(R.id.frame3);
				frame3.setVisibility(View.GONE);
				frame3Linear = (LinearLayout) view.findViewById(R.id.frame3Linear);
				ListContainer2 = (LinearLayout) view.findViewById(R.id.ListContainer2);
				
				frameConfirm = (FrameLayout) view.findViewById(R.id.frameConfirm);
				frameConfirm.setVisibility(View.GONE);
				frameConfirmLinear = (LinearLayout) view.findViewById(R.id.frameConfirmLinear);
				confirmAction = (TextView) view.findViewById(R.id.fragmentpowerTextView_ConfirmAction);
				confirmAction.setTextColor(Color.parseColor(XposedMainActivity.colorPrefs.getString("Dialog_Textcolor", "#000000")));
				confirmNo = (TextView) view.findViewById(R.id.fragmentpowerTextView_ConfirmNo);
				confirmNo.setTextColor(Color.parseColor(XposedMainActivity.colorPrefs.getString("Dialog_Textcolor", "#000000")));
				confirmYes = (TextView) view.findViewById(R.id.fragmentpowerTextView_ConfirmYes);
				confirmYes.setTextColor(Color.parseColor(XposedMainActivity.colorPrefs.getString("Dialog_Textcolor", "#000000")));

				if(XposedMainActivity.sStyleName.equalsIgnoreCase("Material (Fullscreen)")) {
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dialogContent.getLayoutParams());
						params.width = LinearLayout.LayoutParams.MATCH_PARENT;
						params.height = LinearLayout.LayoutParams.MATCH_PARENT;
						dialogContent.setLayoutParams(params);
						FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(frame.getLayoutParams());
						params2.width = FrameLayout.LayoutParams.MATCH_PARENT;
						params2.topMargin = helper.getStatusBarHeight(mContext);
						if(!helper.isDeviceHorizontal(mContext)) {
								params2.bottomMargin = helper.getNavigationBarSize(mContext).y;
						}
						else if (helper.isDeviceHorizontal(mContext)) {
								params2.rightMargin = helper.getNavigationBarSize(mContext).x;
						}
						params2.height = FrameLayout.LayoutParams.MATCH_PARENT;
						frame.setLayoutParams(params2);
						frame3.setLayoutParams(params2);
						//params2.height = (int) helper.convertDpToPixel(150f, mContext);
						//frameConfirm.setLayoutParams(params2);
						FrameLayout.LayoutParams crparams = new FrameLayout.LayoutParams(revealView.getLayoutParams());
						crparams.width = FrameLayout.LayoutParams.MATCH_PARENT;
						crparams.height = FrameLayout.LayoutParams.MATCH_PARENT;
						revealView.setLayoutParams(crparams);
				} else if(XposedMainActivity.sStyleName.equalsIgnoreCase("Material (Full horizontal)")) {
						FrameLayout.LayoutParams crparams = new FrameLayout.LayoutParams(revealView.getLayoutParams());
						crparams.width = FrameLayout.LayoutParams.MATCH_PARENT;
						crparams.height = ((int) helper.convertDpToPixel(150, mContext)) + (boolean_DialogGravityBottom ? helper.getNavigationBarSize(mContext).y : (boolean_DialogGravityTop ? helper.getStatusBarHeight(mContext) : 0));
						revealView.setLayoutParams(crparams);
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dialogContent.getLayoutParams());
						params.width = LinearLayout.LayoutParams.MATCH_PARENT;
						dialogContent.setLayoutParams(params);
						FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(frame.getLayoutParams());
						params2.width = FrameLayout.LayoutParams.MATCH_PARENT;
						if(boolean_DialogGravityTop) {
								params2.topMargin = helper.getStatusBarHeight(mContext);
						}
						if(boolean_DialogGravityBottom) {
								if(!helper.isDeviceHorizontal(mContext)) {
										params2.bottomMargin = helper.getNavigationBarSize(mContext).y;
								}
						}
						if(boolean_DialogGravityRight) {
								if (helper.isDeviceHorizontal(mContext)) {
										params2.rightMargin = helper.getNavigationBarSize(mContext).x;
								}
						}
						frame.setLayoutParams(params2);
						frame3.setLayoutParams(params2);
				} else {
						FrameLayout.LayoutParams crparams = new FrameLayout.LayoutParams(revealView.getLayoutParams());
						crparams.width = ((int) helper.convertDpToPixel(340, mContext)) + (boolean_DialogGravityRight ? helper.getNavigationBarSize(mContext).x : 0);
						crparams.height = ((int) helper.convertDpToPixel(150, mContext)) + (boolean_DialogGravityBottom ? helper.getNavigationBarSize(mContext).y : (boolean_DialogGravityTop ? helper.getStatusBarHeight(mContext) : 0));
						revealView.setLayoutParams(crparams);
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dialogContent.getLayoutParams());
						params.width = ((int) helper.convertDpToPixel(340, mContext)) + (boolean_DialogGravityRight ? helper.getNavigationBarSize(mContext).x : 0);
						dialogContent.setLayoutParams(params);
						FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(frame.getLayoutParams());
						params2.width = FrameLayout.LayoutParams.MATCH_PARENT;
						if(boolean_DialogGravityTop) {
								params2.topMargin = helper.getStatusBarHeight(mContext);
						}
						if(boolean_DialogGravityBottom) {
								if(!helper.isDeviceHorizontal(mContext)) {
										params2.bottomMargin = helper.getNavigationBarSize(mContext).y;
								}
						}
						if(boolean_DialogGravityRight) {
								if (helper.isDeviceHorizontal(mContext)) {
										params2.rightMargin = helper.getNavigationBarSize(mContext).x;
								}
						}
						frame.setLayoutParams(params2);
						frame3.setLayoutParams(params2);
						//params2.height = (int) helper.convertDpToPixel(150f, mContext);
						//frameConfirm.setLayoutParams(params2);
				}
				FrameLayout.LayoutParams params3 = new FrameLayout.LayoutParams(frame.getLayoutParams());
				params3.width = FrameLayout.LayoutParams.MATCH_PARENT;
				params3.height = (int) helper.convertDpToPixel(150, mContext);
				if(boolean_DialogGravityTop) {
						params3.topMargin = helper.getStatusBarHeight(mContext);
				}
				if(boolean_DialogGravityBottom) {
						if(!helper.isDeviceHorizontal(mContext)) {
								params3.bottomMargin = helper.getNavigationBarSize(mContext).y;
						}
				}
				if(boolean_DialogGravityRight) {
						if (helper.isDeviceHorizontal(mContext)) {
								params3.rightMargin = helper.getNavigationBarSize(mContext).x;
						}
				}
				frame2.setLayoutParams(params3);
				frameConfirm.setLayoutParams(params3);
				
				if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[1]+"_type",PreferencesAnimationsFragment.defaultTypes[1]) < mContext.getString(R.string.animations_Types).split("\\|").length-1) {
						LayoutTransition lt = new LayoutTransition();
						dialogMain.setLayoutTransition(lt);
						dialogContent.setLayoutTransition(lt);
				}
				
				dialogMain.setGravity(gravity);
				frameLinear.setGravity(gravity);
				frame2Linear.setGravity(Gravity.CENTER);
				frame3Linear.setGravity(gravity);
				frameConfirmLinear.setGravity(gravity);
				
        status = (TextView) view.findViewById(R.id.status);
        status_detail = (TextView) view.findViewById(R.id.status_detail);

				progressbg = (ImageView) view.findViewById(R.id.progressbg);
				progressbg.setVisibility((GraphicBehindProgress ? View.VISIBLE : View.INVISIBLE));
        progress = (ProgressBar) view.findViewById(R.id.progress);

        progress.getIndeterminateDrawable().setColorFilter(
						Color.parseColor("#ffffff"),
						android.graphics.PorterDuff.Mode.SRC_IN);
				
				if(XposedMainActivity.orderPrefs.getAll().isEmpty()) {
						XposedMainActivity.orderPrefs.edit().putInt("0_item_type",visibilityOrder_ListAdapter.TYPE_NORMAL).commit();
						XposedMainActivity.orderPrefs.edit().putString("0_item_title","Shutdown").commit();
						XposedMainActivity.orderPrefs.edit().putInt("1_item_type",visibilityOrder_ListAdapter.TYPE_NORMAL).commit();
						XposedMainActivity.orderPrefs.edit().putString("1_item_title","Reboot").commit();
						XposedMainActivity.orderPrefs.edit().putInt("2_item_type",visibilityOrder_ListAdapter.TYPE_NORMAL).commit();
						XposedMainActivity.orderPrefs.edit().putString("2_item_title","SoftReboot").commit();
						XposedMainActivity.orderPrefs.edit().putInt("3_item_type",visibilityOrder_ListAdapter.TYPE_MULTI).commit();
						XposedMainActivity.orderPrefs.edit().putString("3_item1_title","Recovery").commit();
						XposedMainActivity.orderPrefs.edit().putString("3_item2_title","Bootloader").commit();
						XposedMainActivity.orderPrefs.edit().putString("3_item3_title","SafeMode").commit();
				}
				
				types = new ArrayList<Integer>(Arrays.asList(new Integer[] {}));
				items = new ArrayList<String>(Arrays.asList(new String[] {}));
				names = new ArrayList<String>(Arrays.asList(new String[] {}));
				final ArrayList<String> MultiPage = new ArrayList<String>();
				boolean firstItemDrawn = false;
				for(int i = 0; i < XposedMainActivity.orderPrefs.getAll().size(); i++) {
						if(XposedMainActivity.orderPrefs.getInt((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+ i+"_item_type",-1)!=-1) {
								names.add((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+i);
								types.add(XposedMainActivity.orderPrefs.getInt((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+ i+"_item_type",visibilityOrder_ListAdapter.TYPE_NORMAL));
								if(types.get(i) == visibilityOrder_ListAdapter.TYPE_NORMAL) {
										items.add(XposedMainActivity.orderPrefs.getString((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+ i+"_item_title","null"));
										if(MultiPage.size()==0 || (MultiPage.size()==1 && !firstItemDrawn)) {
												ListContainer.addView(createNormalItem(items.get(i),(MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1) : null)));
												firstItemDrawn = true;
										}
								} else if(XposedMainActivity.orderPrefs.getInt((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+ i+"_item_type",visibilityOrder_ListAdapter.TYPE_NORMAL) == visibilityOrder_ListAdapter.TYPE_MULTI) {
										items.add(XposedMainActivity.orderPrefs.getString((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+ i+"_item1_title","null") + "|" +
															XposedMainActivity.orderPrefs.getString((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+ i+"_item2_title","null") + "|" +
															XposedMainActivity.orderPrefs.getString((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+ i+"_item3_title","null"));
										if(MultiPage.size()==0) {
												ListContainer.addView(createMultiItem(items.get(i)));
										}
								} else if (XposedMainActivity.orderPrefs.getInt((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+i+"_item_type",-1)==visibilityOrder_ListAdapter.TYPE_MULTIPAGE_START) {
										items.add(XposedMainActivity.orderPrefs.getString((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+i+"_item_title","null"));
										MultiPage.add(XposedMainActivity.orderPrefs.getString((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+i+"_item_title","null"));
										firstItemDrawn = false;
								} else if (XposedMainActivity.orderPrefs.getInt((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+i+"_item_type",-1)==visibilityOrder_ListAdapter.TYPE_MULTIPAGE_END) {
										items.add(XposedMainActivity.orderPrefs.getString((MultiPage.size()>0 ? MultiPage.get(MultiPage.size()-1)+"_" : "")+i+"_item_title","null"));
										if(MultiPage.size() > 0) MultiPage.remove(MultiPage.size()-1);
								}
						}	
				}
				
				mRun = new Runnable() {

						@Override
						public void run()
						{
								// TODO: Implement this method
								refreshIcons();
						}
				};
				mHandler.postDelayed(mRun, 250L);

				if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[1]+"_type",PreferencesAnimationsFragment.defaultTypes[1]) != mContext.getString(R.string.animations_Types).split("\\|").length-1) {
						dialogMain.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 1, false));
				}
				
        return view;

    }
		
		private View createNormalItem(String title, String pageItem) {
						final String mTitle = title;
						View inflated = mInflater.inflate(R.layout.powermenu_normal, null, false);

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
								text.setTextColor(Color.parseColor(XposedMainActivity.colorPrefs.getString("Dialog_Textcolor", "#000000")));

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

								desc.setTextColor(Color.parseColor(XposedMainActivity.colorPrefs.getString("Dialog_Textcolor", "#000000")));

								createCircleIcon(icon,icon2,string,XposedMainActivity.colorPrefs.getString("Dialog"+title+"_Backgroundcolor","#ff000000"),XposedMainActivity.colorPrefs.getString("Dialog"+title+"_Textcolor","#ffffff"));

								if(pageItem == null || pageItem.isEmpty()) {
										root.setOnClickListener(new OnClickListener() {

														@Override
														public void onClick(View p1)
														{
																// TODO: Implement this method
																performMenuClick(mTitle,p1);
														}
												});
								} else {
										final String thisGroup = pageItem;
										root.setOnClickListener(new OnClickListener() {

														@Override
														public void onClick(View p1)
														{
																// TODO: Implement this method
																performMenuClick("multipage:"+thisGroup,p1);
														}
												});
								}
						} else {
								root.setVisibility(View.INVISIBLE);
						}
						if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[3]+"_type",PreferencesAnimationsFragment.defaultTypes[3]) != mContext.getString(R.string.animations_Types).split("\\|").length-1) {
								root.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 3, false));
						}
				return inflated;
		}
		
		private View createMultiItem(String title)
		{
				View inflated = mInflater.inflate(R.layout.powermenu_multi, null, false);

				final String[] titles = title.split("\\|");
				LinearLayout root = (LinearLayout) inflated.findViewById(R.id.powermenumulti_item1);
				ImageView icon = (ImageView) inflated.findViewById(R.id.powermenumulti_item1icon);
				ImageView icon2 = (ImageView) inflated.findViewById(R.id.powermenumulti_item1icon2);
				icon2.setVisibility(View.GONE);
				TextView text = (TextView) inflated.findViewById(R.id.powermenumulti_item1text);
				
				if (!titles[0].equalsIgnoreCase("Empty"))
				{
						String string = "Failed to get String resource for powerMenuMain_" + titles[0];
						try
						{
								string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + titles[0], "string",MainActivity.class.getPackage().getName()));
						}
						catch (Throwable t)
						{
								try
								{
										string = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_" + titles[0], "string",MainActivity.class.getPackage().getName()));
								}
								catch (Throwable t1)
								{
										string = "Failed to get String resource for powerMenuBottom_" + titles[0];
										Log.e("NPM","Failed to get String resource for powerMenuBottom_"+titles[0],t);
								}
						}
						text.setText(string);
						text.setTextColor(Color.parseColor(XposedMainActivity.colorPrefs.getString("Dialog_Textcolor", "#000000")));

						createCircleIcon(icon, icon2, string, XposedMainActivity.colorPrefs.getString("Dialog" + titles[0] + "_Backgroundcolor", "#ff000000"), XposedMainActivity.colorPrefs.getString("Dialog" + titles[0] + "_Textcolor", "#ffffff"));

						root.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View p1)
										{
												// TODO: Implement this method
												performMenuClick(titles[0], p1);
										}
								});
				}
				else
				{
						root.setVisibility(View.GONE);
				}
				if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[4]+"_type",PreferencesAnimationsFragment.defaultTypes[4]) != mContext.getString(R.string.animations_Types).split("\\|").length-1) {
						root.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 4, false));
				}
				
				LinearLayout root2 = (LinearLayout) inflated.findViewById(R.id.powermenumulti_item2);
				ImageView iconitem2 = (ImageView) inflated.findViewById(R.id.powermenumulti_item2icon);
				ImageView icon2item2 = (ImageView) inflated.findViewById(R.id.powermenumulti_item2icon2);
				icon2item2.setVisibility(View.GONE);
				TextView text2 = (TextView) inflated.findViewById(R.id.powermenumulti_item2text);
				if (!titles[1].equalsIgnoreCase("Empty"))
				{
						String string2 = "Failed to get String resource for powerMenuMain_" + titles[1];
						try
						{
								string2 = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + titles[1], "string",MainActivity.class.getPackage().getName()));
						}
						catch (Throwable t)
						{
								try
								{
										string2 = "Failed to get String resource for powerMenuBottom_" + titles[1];
										string2 = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_" + titles[1], "string",MainActivity.class.getPackage().getName()));
								}
								catch (Throwable t1)
								{
										Log.e("NPM","Failed to get String resource for powerMenuBottom_"+titles[1],t);
								}
						}
						text2.setText(string2);
						text2.setTextColor(Color.parseColor(XposedMainActivity.colorPrefs.getString("Dialog_Textcolor", "#000000")));

						createCircleIcon(iconitem2, icon2item2, string2, XposedMainActivity.colorPrefs.getString("Dialog" + titles[1] + "_Backgroundcolor", "#ff000000"), XposedMainActivity.colorPrefs.getString("Dialog" + titles[1] + "_Textcolor", "#ffffff"));

						root2.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View p1)
										{
												// TODO: Implement this method
												performMenuClick(titles[1], p1);
										}
								});
				}
				else
				{
						root2.setVisibility(View.GONE);
				}
				if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[4]+"_type",PreferencesAnimationsFragment.defaultTypes[4]) != mContext.getString(R.string.animations_Types).split("\\|").length-1) {
						Animation anim = helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 4, false);
						anim.setStartOffset((anim.getDuration()/30)*3);
						root2.startAnimation(anim);
				}

				LinearLayout root3 = (LinearLayout) inflated.findViewById(R.id.powermenumulti_item3);
				ImageView iconitem3 = (ImageView) inflated.findViewById(R.id.powermenumulti_item3icon);
				ImageView icon2item3 = (ImageView) inflated.findViewById(R.id.powermenumulti_item3icon2);
				icon2item3.setVisibility(View.GONE);
				TextView text3 = (TextView) inflated.findViewById(R.id.powermenumulti_item3text);

				if (!titles[2].equalsIgnoreCase("Empty"))
				{
						String string3 = "Failed to get String resource for powerMenuMain_" + titles[2];
						try
						{
								string3 = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuMain_" + titles[2], "string",MainActivity.class.getPackage().getName()));
						}
						catch (Throwable t)
						{
								try
								{
										string3 = "Failed to get String resource for powerMenuBottom_" + titles[2];
										string3 = mContext.getResources().getString(mContext.getResources().getIdentifier("powerMenuBottom_" + titles[2], "string",MainActivity.class.getPackage().getName()));
								}
								catch (Throwable t1)
								{
										Log.e("NPM","Failed to get String resource for powerMenuBottom_"+titles[2],t);
								}
						}
						text3.setText(string3);
						text3.setTextColor(Color.parseColor(XposedMainActivity.colorPrefs.getString("Dialog_Textcolor", "#000000")));

						createCircleIcon(iconitem3, icon2item3, string3, XposedMainActivity.colorPrefs.getString("Dialog" + titles[2] + "_Backgroundcolor", "#ff000000"), XposedMainActivity.colorPrefs.getString("Dialog" + titles[2] + "_Textcolor", "#ffffff"));

						root3.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View p1)
										{
												// TODO: Implement this method
												performMenuClick(titles[2], p1);
										}
								});
				}
				else
				{
						root3.setVisibility(View.GONE);
				}
				if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[4]+"_type",PreferencesAnimationsFragment.defaultTypes[4]) != mContext.getString(R.string.animations_Types).split("\\|").length-1) {
						Animation anim = helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 4, false);
						anim.setStartOffset((anim.getDuration()/30)*6);
						root3.startAnimation(anim);
				}
				
				return inflated;
		}
		
		public void refreshIcons() {
				if(soundModeIcon[0] != null && amRingerMode != am.getRingerMode()) {
						if(amRingerMode==AudioManager.RINGER_MODE_VIBRATE) {
								loadImage((ImageView) soundModeIcon[0], 14,soundModeIcon[1].toString());
						} else if(amRingerMode==AudioManager.RINGER_MODE_SILENT) {
								loadImage((ImageView) soundModeIcon[0], 13,soundModeIcon[1].toString());
						} else {
								loadImage((ImageView) soundModeIcon[0], 12,soundModeIcon[1].toString());
						}
						amRingerMode = am.getRingerMode();
				}
				try
				{
						if (airplaneModeIcon[0] != null && airplaneMode != android.provider.Settings.Global.getInt(mContext.getContentResolver(), android.provider.Settings.Global.AIRPLANE_MODE_ON))
						{
								try
								{
										if (android.provider.Settings.Global.getInt(mContext.getContentResolver(), android.provider.Settings.Global.AIRPLANE_MODE_ON) == 0)
										{
												loadImage((ImageView) airplaneModeIcon[0], 10, airplaneModeIcon[1].toString());
										}
										else
										{
												loadImage((ImageView) airplaneModeIcon[0], 9, airplaneModeIcon[1].toString());
										}
								}
								catch (Throwable e)
								{
										loadImage((ImageView) airplaneModeIcon[0], 10, airplaneModeIcon[1].toString());
								}
								airplaneMode = android.provider.Settings.Global.getInt(mContext.getContentResolver(), android.provider.Settings.Global.AIRPLANE_MODE_ON);
						}
				}
				catch (Throwable e)
				{}
				try {
						if(flashlightIcon[0] != null && flashlightOn != (TorchService.getTorchState() == TorchService.TORCH_STATUS_ON)) {
								if(TorchService.getTorchState()==TorchService.TORCH_STATUS_ON) {
										loadImage((ImageView) flashlightIcon[0], 6,flashlightIcon[1].toString());
								} else {
										loadImage((ImageView) flashlightIcon[0], 7,flashlightIcon[1].toString());
								}
								flashlightOn = TorchService.getTorchState() == TorchService.TORCH_STATUS_ON;
						}
				} catch (Throwable t) {
						
				}
				mHandler.postDelayed(mRun, 250L);
		}
		
		public void createCircleIcon(ImageView background,final ImageView foreground,String text,String color1,String color2) {
				try {
				if (XposedMainActivity.preferences.getBoolean("UseGraphics",false)) {
						GraphicDrawable drawable = GraphicDrawable.builder().buildRound((Bitmap) null, Color.parseColor(color1));
						background.setImageDrawable(drawable);
						foreground.setVisibility(View.VISIBLE);
						if(text.equalsIgnoreCase(mContext.getString(R.string.powerMenuMain_Shutdown))) {
								loadImage(foreground, 1,color2);
						} else if(text.equalsIgnoreCase(mContext.getString(R.string.powerMenuMain_Reboot))) {
								loadImage(foreground, 2,color2);
						} else if(text.equalsIgnoreCase(mContext.getString(R.string.powerMenuMain_SoftReboot))) {
								loadImage(foreground, 3,color2);
						} else if(text.equalsIgnoreCase(mContext.getString(R.string.powerMenuMain_Screenshot))) {
								loadImage(foreground, 4,color2);
						} else if(text.equalsIgnoreCase(mContext.getString(R.string.powerMenuMain_Screenrecord))) {
								loadImage(foreground, 5,color2);
						} else if(text.equalsIgnoreCase(mContext.getString(R.string.powerMenuMain_Flashlight))) {
								if(TorchService.getTorchState()==TorchService.TORCH_STATUS_OFF) {
										loadImage(foreground, 7,color2);
								} else {
										loadImage(foreground, 6,color2);
								}
								flashlightIcon[0] = foreground;
								flashlightIcon[1] = color2;
						} else if(text.equalsIgnoreCase(mContext.getString(R.string.powerMenuMain_ExpandedDesktop))) {
								loadImage(foreground, 8,color2);
						} else if(text.equalsIgnoreCase(mContext.getString(R.string.powerMenuMain_AirplaneMode))) {
								try
								{
										if(android.provider.Settings.Global.getInt(mContext.getContentResolver(), android.provider.Settings.Global.AIRPLANE_MODE_ON) == 0) {
												loadImage(foreground, 10,color2);
										} else {
												loadImage(foreground, 9,color2);
										}
								}
								catch (Throwable e)
								{
										loadImage(foreground, 10,color2);
								}
								airplaneModeIcon[0] = foreground;
								airplaneModeIcon[1] = color2;
						} else if (text.equalsIgnoreCase(mContext.getString(R.string.powerMenuMain_RestartUI))) {
								loadImage(foreground, 11,color2);
						} else if(text.equalsIgnoreCase(mContext.getString(R.string.powerMenuMain_SoundMode))) {
								if(amRingerMode==AudioManager.RINGER_MODE_VIBRATE) {
										loadImage(foreground, 14,color2);
								} else if(amRingerMode==AudioManager.RINGER_MODE_SILENT) {
										loadImage(foreground, 13,color2);
								} else {
										loadImage(foreground, 12,color2);
								}
								soundModeIcon[0] = foreground;
								soundModeIcon[1] = color2;
						} else if(text.equalsIgnoreCase(mContext.getString(R.string.powerMenuBottom_Recovery))) {
								loadImage(foreground, 15,color2);
						} else if(text.equalsIgnoreCase(mContext.getString(R.string.powerMenuBottom_Bootloader))) {
								loadImage(foreground, 16,color2);
						} else if(text.equalsIgnoreCase(mContext.getString(R.string.powerMenuBottom_SafeMode))) {
								loadImage(foreground, 17,color2);
						} else if(text.equalsIgnoreCase(mContext.getString(R.string.powerMenuMain_SoundVibrate))) {
								loadImage(foreground, 14,color2);
						} else if(text.equalsIgnoreCase(mContext.getString(R.string.powerMenuMain_SoundNormal))) {
								loadImage(foreground, 12,color2);
						} else if(text.equalsIgnoreCase(mContext.getString(R.string.powerMenuMain_SoundSilent))) {
								loadImage(foreground, 13,color2);
						}
				} else {
						TextDrawable drawable = TextDrawable.builder().beginConfig().textColor(Color.parseColor(color2)).endConfig()
								.buildRound(text.substring(0, 1), Color.parseColor(color1));
						background.setImageDrawable(drawable);
						foreground.setVisibility(View.GONE);
				}
				} catch (Throwable t) {
						Log.e("NPM","Failed to create Circle Icon.",t);
				}
		}
		
		private static void loadImage(final ImageView image, final int id, final String color) {
				if(new File(mContext.getFilesDir().getPath()+"/images/"+PreferencesGraphicsFragment.defaultGraphics[id][2]+".png").exists()) {
				XposedMainActivity.imageLoader.displayImage("file://"+mContext.getFilesDir().getPath()+"/images/"+PreferencesGraphicsFragment.defaultGraphics[id][2]+".png",
						image, new SimpleImageLoadingListener() {
								@Override
								public void onLoadingStarted(String imageUri, View view) {
										image.setImageBitmap(null);
										image.setPadding(5,5,5,5);
										image.setColorFilter(Color.parseColor("#ffffff"),
																				 android.graphics.PorterDuff.Mode.DST);
										if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[2]+"_type",PreferencesAnimationsFragment.defaultTypes[2]) != mContext.getString(R.string.animations_Types).split("\\|").length-1) {
												image.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 2, true));
										}
										image.setVisibility(View.INVISIBLE);
										super.onLoadingStarted(imageUri, view);
								}
								@Override
								public void onLoadingComplete(final String imageUri, final View view, Bitmap loadedImage) {
										image.setImageBitmap(loadedImage);
										image.setPadding(0,0,0,0);
										image.setVisibility(View.VISIBLE);
										if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[2]+"_type",PreferencesAnimationsFragment.defaultTypes[2]) != mContext.getString(R.string.animations_Types).split("\\|").length-1) {
												image.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 2, false));
										}
								}
								@Override
								public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
										Log.e("NPM:xposedDialog","Failed to load image '"+imageUri+"': "+failReason.getCause().toString());
										image.setImageDrawable(mContext.getResources().getDrawable(PreferencesGraphicsFragment.defaultGraphics[id][1]));
										image.setColorFilter(Color.parseColor(color),
																							android.graphics.PorterDuff.Mode.MULTIPLY);
										image.setVisibility(View.VISIBLE);
										if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[2]+"_type",PreferencesAnimationsFragment.defaultTypes[2]) != mContext.getString(R.string.animations_Types).split("\\|").length-1) {
												image.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 2, false));
										}
								}
						});
				} else {
						image.setImageDrawable(mContext.getResources().getDrawable(PreferencesGraphicsFragment.defaultGraphics[id][1]));
						image.setColorFilter(Color.parseColor(color),
																 android.graphics.PorterDuff.Mode.MULTIPLY);
						image.setVisibility(View.VISIBLE);
						if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[2]+"_type",PreferencesAnimationsFragment.defaultTypes[2]) != mContext.getString(R.string.animations_Types).split("\\|").length-1) {
								image.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 2, false));
						}
				}
		}
		
		private void performMenuClick(final String name,final View v) {
				if(name.contains("multipage:")) {
						if(frame3.getVisibility()==View.GONE) {
								ListContainer2.removeAllViews();
						} else {
								ListContainer.removeAllViews();
						}
						SubDialogParent = inSubDialog;
						String page = name.split("\\:")[1];
						inSubDialog = page;
						boolean firstItemDrawn = false;
						boolean inRightSpot = false;
						ArrayList<String> MultiPage = new ArrayList<String>();
						//Log.i("NPM:itemLoader","Searching for multi item with the code "+page);
						for(int i = 0; i < items.size(); i++) {
										if(types.get(i)==visibilityOrder_ListAdapter.TYPE_MULTIPAGE_START) {
												if(items.get(i).equals(page)) {
														firstItemDrawn = false;
														//MultiPage.add(items.get(i));
														inRightSpot = true;
														//Log.i("NPM:itemLoader","Got the right spot!");
												} else {
														if(inRightSpot && MultiPage.size() == 0) {
																firstItemDrawn = false;
																MultiPage.add(items.get(i));
																page = items.get(i);
														}
														//Log.i("NPM:itemLoader","Found multi item with another code"+items.get(i)+(inRightSpot ? "" : " but ignoring."));
												}
										} else if (types.get(i)==visibilityOrder_ListAdapter.TYPE_MULTIPAGE_END) {
												if(items.get(i).equals(page)) {
														inRightSpot = false;
														if(MultiPage.size()>0) {
																//page = MultiPage.get(MultiPage.size()-1);
																MultiPage.remove(MultiPage.size()-1);
														}
														//Log.i("NPM:itemLoader","Left the right spot.");
												}
										} else if(inRightSpot) {
												if(types.get(i)==visibilityOrder_ListAdapter.TYPE_NORMAL) {
														if(names.get(i).contains(page)) {
																if(MultiPage.size()==0 || !firstItemDrawn) {
																		if(frame3.getVisibility()==View.GONE) {
																				ListContainer2.addView(createNormalItem(items.get(i),(MultiPage.size()>0 ? page : null)));
																		} else {
																				ListContainer.addView(createNormalItem(items.get(i),(MultiPage.size()>0 ? page : null)));
																		}
																}
																if(!firstItemDrawn) {
																		firstItemDrawn = true;
																		if(MultiPage.size()>0) {
																				page = MultiPage.get(MultiPage.size()-1);
																				//MultiPage.remove(MultiPage.size()-1);
																		}
																}
																//Log.i("NPM:itemLoader","Added "+items.get(i)+" in "+page);
																//if(MultiPage.size()>0) MultiPage.remove(MultiPage.size()-1);
														}
												} else if(types.get(i)==visibilityOrder_ListAdapter.TYPE_MULTI) {
														if(MultiPage.size() == 0 && names.get(i).contains(page)) {
																if(frame3.getVisibility()==View.GONE) {
																		ListContainer2.addView(createMultiItem(items.get(i)));
																} else {
																		ListContainer.addView(createMultiItem(items.get(i)));
																}
																//Log.i("NPM:itemLoader","Added "+items.get(i) +" in "+page);
														}
												}
										}
						}
						if(frame3.getVisibility()==View.GONE) {
								frame3.setVisibility(View.VISIBLE);
								if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[1]+"_type",PreferencesAnimationsFragment.defaultTypes[1]) != mContext.getString(R.string.animations_Types).split("\\|").length-1) {
										frame.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 1, true));
										frame3.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 1, false));
								}
								frame.setVisibility(View.GONE);
						} else {
								frame.setVisibility(View.VISIBLE);
								if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[1]+"_type",PreferencesAnimationsFragment.defaultTypes[1]) != mContext.getString(R.string.animations_Types).split("\\|").length-1) {
										frame3.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 1, true));
										frame.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 1, false));
								}
								frame3.setVisibility(View.GONE);
						}
				} else {
						if(RequireConfirmation && (name.equalsIgnoreCase("Shutdown") || name.equalsIgnoreCase("Reboot") || name.equalsIgnoreCase("SoftReboot") || name.equalsIgnoreCase("Recovery") || name.equalsIgnoreCase("Bootloader") || name.equalsIgnoreCase("SafeMode"))) {
								if(!confirmDialog.equalsIgnoreCase(name)) {
										confirmDialog = name;
										confirmAction.setText(mContext.getString(R.string.powerMenu_SureToRebootPowerOff).split("\\|")[(name.equalsIgnoreCase("Shutdown") ? 1 : 0)]);
										confirmNo.setText(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_NO]);
										confirmNo.setOnClickListener(new OnClickListener() {

														@Override
														public void onClick(View p1)
														{
																// TODO: Implement this method
																dismissThis();
														}
												});
										confirmYes.setText(mContext.getString(R.string.Dialog_Buttons).split("\\|")[slideDownDialogFragment.BUTTON_YES]);
										confirmYes.setOnClickListener(new OnClickListener() {

														@Override
														public void onClick(View p1)
														{
																// TODO: Implement this method
																performMenuClick(name,v);
														}
												});
										if(frame3.getVisibility()==View.GONE) {
												frameConfirm.setVisibility(View.VISIBLE);
												if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[1]+"_type",PreferencesAnimationsFragment.defaultTypes[1]) != mContext.getString(R.string.animations_Types).split("\\|").length-1) {
														frame.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 1, true));
														frameConfirm.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 1, false));
												}
												frame.setVisibility(View.GONE);
										} else {
												frameConfirm.setVisibility(View.VISIBLE);
												if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[1]+"_type",PreferencesAnimationsFragment.defaultTypes[1]) != mContext.getString(R.string.animations_Types).split("\\|").length-1) {
														frame3.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 1, true));
														frameConfirm.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 1, false));
												}
												frame3.setVisibility(View.GONE);
										}
										return;
								}
						}
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

								//revealView.setVisibility(View.VISIBLE);
                final int color = Color.parseColor(XposedMainActivity.colorPrefs.getString("DialogShutdown_Backgroundcolor", "#d32f2f"));
                final Point p = getLocationInView(revealView, v);
								if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[0]+"_type",PreferencesAnimationsFragment.defaultTypes[0]) != mContext.getString(R.string.animations_Types).split("\\|").length-1) {
										Animation anim = helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 0, false);
										if (XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[0]+"_type",PreferencesAnimationsFragment.defaultTypes[0]) == 1) {
												revealView.reveal(p.x, p.y, color, 0, anim.getDuration(), null);
										} else {
												revealView.reveal(p.x, p.y, color, 0, 0, null);
												if(frame3.getVisibility()==View.VISIBLE) {
														frame3.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 0, true));
												} else if (frame.getVisibility() == View.VISIBLE) {
														frame.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 0, true));
												} else {
														frameConfirm.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 0, true));
												}
												revealView.startAnimation(anim);
												frame2.startAnimation(anim);
										}
								} else {
										revealView.reveal(p.x, p.y, color, 0, 0, null);
								}

                if (selectedView == v)
								{
                    //revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
                    selectedView = null;
                }
								else
								{
                    //revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
                    selectedView = v;
                }

                ((XposedMainActivity) getActivity()).revealFromTop();
                frame.setVisibility(View.GONE);
								frame3.setVisibility(View.GONE);
								frameConfirm.setVisibility(View.GONE);
                frame2.setVisibility(View.VISIBLE);
								
								status.setText(R.string.powerMenuMain_Shutdown);
								status_detail.setText(R.string.powerMenu_Shuttingdown);

								setProgressScreen("Shutdown");
								if (!XposedMainActivity.previewMode)
								{
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

								//revealView.setVisibility(View.VISIBLE);
								final int color = Color.parseColor(XposedMainActivity.colorPrefs.getString("DialogReboot_Backgroundcolor", "#3f51b5"));
								final Point p = getLocationInView(revealView, v);
								if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[0]+"_type",PreferencesAnimationsFragment.defaultTypes[0]) != mContext.getString(R.string.animations_Types).split("\\|").length-1) {
										Animation anim = helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 0, false);
										if (XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[0]+"_type",PreferencesAnimationsFragment.defaultTypes[0]) == 1) {
												revealView.reveal(p.x, p.y, color, 0, anim.getDuration(), null);
										} else {
												revealView.reveal(p.x, p.y, color, 0, 0, null);
												if(frame3.getVisibility()==View.VISIBLE) {
														frame3.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 0, true));
												} else if (frame.getVisibility() == View.VISIBLE) {
														frame.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 0, true));
												} else {
														frameConfirm.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 0, true));
												}
												revealView.startAnimation(anim);
												frame2.startAnimation(anim);
										}
								} else {
										revealView.reveal(p.x, p.y, color, 0, 0, null);
								}

								if (selectedView == v)
								{
										//revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
										selectedView = null;
								}
								else
								{
										//revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
										selectedView = v;
								}

								((XposedMainActivity) getActivity()).revealFromTop();
								frame.setVisibility(View.GONE);
								frame3.setVisibility(View.GONE);
								frameConfirm.setVisibility(View.GONE);
								frame2.setVisibility(View.VISIBLE);

								status.setText(R.string.powerMenuMain_Reboot);
								status_detail.setText(R.string.powerMenu_Rebooting);

								setProgressScreen("Reboot");
								if (!XposedMainActivity.previewMode)
								{
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

								//revealView.setVisibility(View.VISIBLE);
                final int color = Color.parseColor(XposedMainActivity.colorPrefs.getString("DialogSoftReboot_Backgroundcolor", "#e91e63"));
                final Point p = getLocationInView(revealView, v);
								if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[0]+"_type",PreferencesAnimationsFragment.defaultTypes[0]) != mContext.getString(R.string.animations_Types).split("\\|").length-1) {
										Animation anim = helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 0, false);
										if (XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[0]+"_type",PreferencesAnimationsFragment.defaultTypes[0]) == 1) {
												revealView.reveal(p.x, p.y, color, 0, anim.getDuration(), null);
										} else {
												revealView.reveal(p.x, p.y, color, 0, 0, null);
												if(frame3.getVisibility()==View.VISIBLE) {
														frame3.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 0, true));
												} else if (frame.getVisibility() == View.VISIBLE) {
														frame.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 0, true));
												} else {
														frameConfirm.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 0, true));
												}
												revealView.startAnimation(anim);
												frame2.startAnimation(anim);
										}
								} else {
										revealView.reveal(p.x, p.y, color, 0, 0, null);
								}

                if (selectedView == v)
								{
                    //revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
                    selectedView = null;
                }
								else
								{
                    //revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
                    selectedView = v;
                }

                ((XposedMainActivity) getActivity()).revealFromTop();
                frame.setVisibility(View.GONE);
								frame3.setVisibility(View.GONE);
								frameConfirm.setVisibility(View.GONE);
                frame2.setVisibility(View.VISIBLE);

                status.setText(R.string.powerMenuMain_SoftReboot);
                status_detail.setText(R.string.powerMenu_Rebooting);

								setProgressScreen("SoftReboot");
								if (!XposedMainActivity.previewMode)
								{
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
										dismissThis();
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
								dismissThis();
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
								//dismiss();
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
								dismissThis();
								Intent launchIntent = new Intent("gravitybox.intent.action.TOGGLE_EXPANDED_DESKTOP");
								mContext.sendBroadcast(launchIntent);
						}
				} else if(name.equalsIgnoreCase("AirplaneMode")) {
						if(!XposedMainActivity.previewMode) {
								dismissThis();
								Intent launchIntent = new Intent(XposedMain.NPM_ACTION_BROADCAST_TOGGLEAIRPLANEMODE);
								mContext.sendBroadcast(launchIntent);
						}
				} else if(name.equalsIgnoreCase("RestartUI")) {
						if (!XposedMainActivity.previewMode)
						{
								dismissThis();
								Intent launchIntent = new Intent(XposedMain.NPM_ACTION_BROADCAST_KILLSYSTEMUI);
								mContext.sendBroadcast(launchIntent);
						}
				} else if(name.equalsIgnoreCase("SoundMode")) {
						if (!XposedMainActivity.previewMode) {
								//dismiss();
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

						//revealView.setVisibility(View.VISIBLE);
						final int color = Color.parseColor(XposedMainActivity.colorPrefs.getString("DialogRecovery_Backgroundcolor", "#8bc34a"));
						final Point p = getLocationInView(revealView, v);
						if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[0]+"_type",PreferencesAnimationsFragment.defaultTypes[0]) != mContext.getString(R.string.animations_Types).split("\\|").length-1) {
								Animation anim = helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 0, false);
								if (XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[0]+"_type",PreferencesAnimationsFragment.defaultTypes[0]) == 1) {
										revealView.reveal(p.x, p.y, color, 0, anim.getDuration(), null);
								} else {
										revealView.reveal(p.x, p.y, color, 0, 0, null);
										if(frame3.getVisibility()==View.VISIBLE) {
												frame3.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 0, true));
										} else if (frame.getVisibility() == View.VISIBLE) {
												frame.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 0, true));
										} else {
												frameConfirm.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 0, true));
										}
										revealView.startAnimation(anim);
										frame2.startAnimation(anim);
								}
						} else {
								revealView.reveal(p.x, p.y, color, 0, 0, null);
						}

						if (selectedView == v)
						{
								//revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
								selectedView = null;
						}
						else
						{
								//revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
								selectedView = v;
						}

						((XposedMainActivity) getActivity()).revealFromTop();
						frame.setVisibility(View.GONE);
						frame3.setVisibility(View.GONE);
						frameConfirm.setVisibility(View.GONE);
						frame2.setVisibility(View.VISIBLE);

						status.setText(R.string.powerMenuBottom_Recovery);
						status_detail.setText(R.string.powerMenu_Rebooting);

						setProgressScreen("Recovery");
						if (!XposedMainActivity.previewMode)
						{
								if(HookShutdownThread) {
										XposedUtils.doReboot(getActivity(),2);
								} else {
										new BackgroundThread(REBOOT_RECOVERY_CMD).start();
								}
						}
				} else if(name.equalsIgnoreCase("Bootloader")) {
						canDismiss = false;

						//revealView.setVisibility(View.VISIBLE);
						final int color = Color.parseColor(XposedMainActivity.colorPrefs.getString("DialogBootloader_Backgroundcolor", "#277b71"));
						final Point p = getLocationInView(revealView, v);
						if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[0]+"_type",PreferencesAnimationsFragment.defaultTypes[0]) != mContext.getString(R.string.animations_Types).split("\\|").length-1) {
								Animation anim = helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 0, false);
								if (XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[0]+"_type",PreferencesAnimationsFragment.defaultTypes[0]) == 1) {
										revealView.reveal(p.x, p.y, color, 0, anim.getDuration(), null);
								} else {
										revealView.reveal(p.x, p.y, color, 0, 0, null);
										if(frame3.getVisibility()==View.VISIBLE) {
												frame3.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 0, true));
										} else if (frame.getVisibility() == View.VISIBLE) {
												frame.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 0, true));
										} else {
												frameConfirm.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 0, true));
										}
										revealView.startAnimation(anim);
										frame2.startAnimation(anim);
								}
						} else {
								revealView.reveal(p.x, p.y, color, 0, 0, null);
						}

						if (selectedView == v)
						{
								//revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
								selectedView = null;
						}
						else
						{
								//revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
								selectedView = v;
						}

						((XposedMainActivity) getActivity()).revealFromTop();
						frame.setVisibility(View.GONE);
						frame3.setVisibility(View.GONE);
						frameConfirm.setVisibility(View.GONE);
						frame2.setVisibility(View.VISIBLE);

						status.setText(R.string.powerMenuBottom_Bootloader);
						status_detail.setText(R.string.powerMenu_Rebooting);

						setProgressScreen("Bootloader");
						if (!XposedMainActivity.previewMode)
						{
								if(HookShutdownThread) {
										XposedUtils.doReboot(getActivity(),3);
								} else {
										new BackgroundThread(REBOOT_BOOTLOADER_CMD).start();
								}
						}
				} else if(name.equalsIgnoreCase("SafeMode")) {
						canDismiss = false;

						//revealView.setVisibility(View.VISIBLE);
						final int color = Color.parseColor(XposedMainActivity.colorPrefs.getString("DialogSafeMode_Backgroundcolor", "#009688"));
						final Point p = getLocationInView(revealView, v);
						if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[0]+"_type",PreferencesAnimationsFragment.defaultTypes[0]) != mContext.getString(R.string.animations_Types).split("\\|").length-1) {
								Animation anim = helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 0, false);
								if (XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[0]+"_type",PreferencesAnimationsFragment.defaultTypes[0]) == 1) {
										revealView.reveal(p.x, p.y, color, 0, anim.getDuration(), null);
								} else {
										revealView.reveal(p.x, p.y, color, 0, 0, null);
										if(frame3.getVisibility()==View.VISIBLE) {
												frame3.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 0, true));
										} else if (frame.getVisibility() == View.VISIBLE) {
												frame.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 0, true));
										} else {
												frameConfirm.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 0, true));
										}
										revealView.startAnimation(anim);
										frame2.startAnimation(anim);
								}
						} else {
								revealView.reveal(p.x, p.y, color, 0, 0, null);
						}

						if (selectedView == v)
						{
								//revealView.hide(p.x, p.y, backgroundColor, 0, 330, null);
								selectedView = null;
						}
						else
						{
								//revealView.reveal(p.x / 2, p.y / 2, color, v.getHeight() / 2, 440, null);
								selectedView = v;
						}

						((XposedMainActivity) getActivity()).revealFromTop();
						frame.setVisibility(View.GONE);
						frame3.setVisibility(View.GONE);
						frameConfirm.setVisibility(View.GONE);
						frame2.setVisibility(View.VISIBLE);

						status.setText(R.string.powerMenuBottom_SafeMode);
						status_detail.setText(R.string.powerMenu_Rebooting);

						setProgressScreen("SafeMode");
						if (!XposedMainActivity.previewMode)
						{
								new BackgroundThread(REBOOT_SAFE_MODE).start();
						}
				} else if (name.equalsIgnoreCase("SoundVibrate")) {
						if (!XposedMainActivity.previewMode) {
								am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
						}
				} else if (name.equalsIgnoreCase("SoundNormal")) {
						if (!XposedMainActivity.previewMode) {
								am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
						}
				} else if (name.equalsIgnoreCase("SoundSilent")) {
						if (!XposedMainActivity.previewMode) {
								am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
						}
				}
				}
		}
		
		public void setProgressScreen(final String showingFor) {
				status.setTextColor(Color.parseColor(XposedMainActivity.colorPrefs.getString("Dialog"+showingFor+"_Textcolor", "#ffffff")));
				status_detail.setTextColor(Color.parseColor(XposedMainActivity.colorPrefs.getString("Dialog"+showingFor+"_Textcolor", "#ffffff")));
				
				//if(GraphicBehindProgress) loadImage(progressbg, 0, XposedMainActivity.colorPrefs.getString("DialogShutdown_Textcolor", "#ffffff"));
				if(XposedMainActivity.preferences.getString("ProgressDrawable","Stock").equalsIgnoreCase("file")) {
						if(new File(mContext.getFilesDir().getPath()+"/images/"+PreferencesGraphicsFragment.defaultGraphics[0][2]+".png").exists()) {
								XposedMainActivity.imageLoader.displayImage("file://"+mContext.getFilesDir().getPath()+"/images/"+PreferencesGraphicsFragment.defaultGraphics[0][2]+".png",
										progressbg, new SimpleImageLoadingListener() {
												@Override
												public void onLoadingStarted(String imageUri, View view) {
														progress.setVisibility(View.INVISIBLE);
												}
												@Override
												public void onLoadingComplete(final String imageUri, final View view, Bitmap loadedImage) {
														progressbg.setVisibility(View.VISIBLE);
														progressbg.setImageBitmap(loadedImage);
														if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[2]+"_type",PreferencesAnimationsFragment.defaultTypes[2]) != mContext.getString(R.string.animations_Types).split("\\|").length-1) {
														Animation blendIn = helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 2, false);
																blendIn.setAnimationListener(new Animation.AnimationListener() {

																				@Override
																				public void onAnimationEnd(Animation p1)
																				{
																						// TODO: Implement this method
																						Animation progressAnim = AnimationUtils.loadAnimation(mContext, R.anim.rotate_right);
																						progressAnim.setRepeatMode(Animation.RESTART);
																						progressAnim.setRepeatCount(Animation.INFINITE);
																						progressbg.startAnimation(progressAnim);
																				}

																				@Override
																				public void onAnimationRepeat(Animation p1)
																				{
																						// TODO: Implement this method
																				}

																				@Override
																				public void onAnimationStart(Animation p1)
																				{
																						// TODO: Implement this method
																				}
																});
																progressbg.startAnimation(blendIn);
														}
												}
												@Override
												public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
														Log.e("NPM:xposedDialog","Failed to load image '"+imageUri+"': "+failReason.getCause().toString());
														progress.getIndeterminateDrawable().setColorFilter(Color.parseColor(XposedMainActivity.colorPrefs.getString("Dialog"+showingFor+"_Textcolor", "#ffffff")),android.graphics.PorterDuff.Mode.MULTIPLY);
														progress.setVisibility(View.VISIBLE);
														if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[2]+"_type",PreferencesAnimationsFragment.defaultTypes[2]) != mContext.getString(R.string.animations_Types).split("\\|").length-1) {
																progress.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 2, false));
														}
												}
										});
						}
				} else {
						if(XposedMainActivity.preferences.getString("ProgressDrawable","Stock").equalsIgnoreCase("Stock")) {
								progress.getIndeterminateDrawable().setColorFilter(Color.parseColor(XposedMainActivity.colorPrefs.getString("Dialog"+showingFor+"_Textcolor", "#ffffff")),android.graphics.PorterDuff.Mode.MULTIPLY);
								if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[2]+"_type",PreferencesAnimationsFragment.defaultTypes[2]) != mContext.getString(R.string.animations_Types).split("\\|").length-1) {
										progress.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 2, false));
								}
						} else {
								progress.setVisibility(View.GONE);
								progressbg.setColorFilter(Color.parseColor(XposedMainActivity.colorPrefs.getString("Dialog"+showingFor+"_Textcolor", "#ffffff")),android.graphics.PorterDuff.Mode.MULTIPLY);
								new loadProgressResource().execute(showingFor);
						}
				}
		}
		
		class loadProgressResource extends AsyncTask<Object, String, String>
		{

				Drawable image = null;
				String showingFor = "";
				
				@Override
				protected String doInBackground(Object[] p1)
				{
						// TODO: Implement this method
						showingFor = p1[0].toString();
						try {
						if(XposedMainActivity.preferences.getString("ProgressDrawable","Stock").equalsIgnoreCase("pb/dr")) {
								image = mContext.getResources().getDrawable(R.drawable.progress_pitchblack_darkred_cm13);
						} else if (XposedMainActivity.preferences.getString("ProgressDrawable","Stock").equalsIgnoreCase("WeaReOne")) {
								image = mContext.getResources().getDrawable(R.drawable.progress_weareone);
						}
						} catch (OutOfMemoryError t) {
								return t.toString();
						}
						return null;
				}

				@Override
				protected void onPostExecute(String p1)
				{
						// TODO: Implement this method
						super.onPostExecute(p1);
						if(p1 == null) {
						progressbg.setImageDrawable(image);
						progressbg.setVisibility(View.VISIBLE);
						if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[2]+"_type",PreferencesAnimationsFragment.defaultTypes[2]) != mContext.getString(R.string.animations_Types).split("\\|").length-1) {
								progressbg.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 2, false));
						}
						((AnimationDrawable) progressbg.getDrawable()).start();
						} else {
								progressbg.setVisibility(View.INVISIBLE);
								progress.setVisibility(View.VISIBLE);
								progress.getIndeterminateDrawable().setColorFilter(Color.parseColor(XposedMainActivity.colorPrefs.getString("Dialog"+showingFor+"_Textcolor", "#ffffff")),android.graphics.PorterDuff.Mode.MULTIPLY);
								if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[2]+"_type",PreferencesAnimationsFragment.defaultTypes[2]) != mContext.getString(R.string.animations_Types).split("\\|").length-1) {
										progress.startAnimation(helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 2, false));
								}
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
				if(getShowsDialog()) {
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.0f;
				//windowParams.type = windowParams.TYPE_KEYGUARD_DIALOG;

        window.setAttributes(windowParams);
				}
    }
		
		public static void dismissThis() {
				if(canDismiss || XposedMainActivity.previewMode) {
				if(!isDismissing) {
						isDismissing = true;
				final int speed;
				speed = 0;
				if(XposedMainActivity.animationPrefs.getInt(PreferencesAnimationsFragment.names[1]+"_type",PreferencesAnimationsFragment.defaultTypes[1]) != mContext.getString(R.string.animations_Types).split("\\|").length-1) {
						Animation anim = helper.getAnimation(mContext, XposedMainActivity.animationPrefs, 1, true);
						dialogMain.startAnimation(anim);
						speed = (int) anim.getDuration();
				}
				new Handler().postDelayed(new Runnable() {

								@Override
								public void run()
								{
										// TODO: Implement this method
										dialogMain.setVisibility(View.GONE);
										XposedMainActivity.dismissThis();
								}
						}, Math.max(speed-100,0));
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
				if(getShowsDialog()) {
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
        //getDialog().getWindow()
				//		.getAttributes().windowAnimations = R.style.DialogAnimation;
				getDialog().getWindow()
						.getAttributes().gravity = gravity;
				}
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


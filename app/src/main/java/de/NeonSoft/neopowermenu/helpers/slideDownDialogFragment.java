package de.NeonSoft.neopowermenu.helpers;
import android.animation.*;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.support.v4.app.*;
import android.text.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.larswerkman.holocolorpicker.*;
import de.NeonSoft.neopowermenu.*;
import java.util.*;

import android.support.v4.app.Fragment;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import de.NeonSoft.neopowermenu.R;

public class slideDownDialogFragment extends Fragment
{

		public static String dialogTag = "slideDownDialog";
	
		 private Activity mContext;
		private slideDownDialogInterface mInterface;
		private Fragment mFragment;
		BroadcastReceiver br;
		
		 private String dialogText = "";
	
		 private int dialogListMode;
		 private int dialogListLimit = 0;
		 private boolean dialogListLimitMin = false;
		 private String[] dialogListItems;
		 private int dialogListDefault = 0;
		 private boolean dialogListClose = true;
		 
		 private String dialogInput1descText;
		 private String dialogInput1defaultText;
		 private boolean dialogInput1allowEmpty;
		private TextWatcher dialogInput1textWatcher;

		private String dialogInput2descText;
		private String dialogInput2defaultText;
		private boolean dialogInput2allowEmpty;
		private TextWatcher dialogInput2textWatcher;
		 
		private String dialogColorPickerdefaultValue;
		private boolean dialogColorPickershowOpacityBar;
		
		private TextView TextView_OverwriteInfo;
		
		 private String negativeButtonText = null;
		 private String neutralButtonText = null;
		 private String positiveButtonText = "Ok";

		 private boolean closeOnTouchOutside = true;
		

		TextView TextView_DialogBg;
		LinearLayout LinearLayout_DialogRoot;
		LinearLayout LinearLayout_MainContainer;
		TextView TextView_DialogText;

		LinearLayout LinearLayout_DialogListView;
		ListView ListView_DialogListView;

		LinearLayout LinearLayout_DialogInput1;
		TextView TextView_DialogInput1Text;
		EditText EditText_DialogInput1;
		TextWatcher TextWatcher_DialogInput1;

		LinearLayout LinearLayout_DialogInput2;
		TextView TextView_DialogInput2Text;
		EditText EditText_DialogInput2;
		TextWatcher TextWatcher_DialogInput2;

		boolean DialogColorPicker_HexChangeViaWheel = false;

		LinearLayout LinearLayout_DialogColorPicker;
		ColorPicker ColorPicker_DialogColorPicker;
		ValueBar ValueBar_DialogValueBar;
		SaturationBar SaturationBar_DialogSaturationBar;
		OpacityBar OpacityBar_DialogOpacityBar;
		EditText EditText_DialogHexInput;

		LinearLayout LinearLayout_Buttons;
		
		LinearLayout LinearLayout_DialogNegativeButton;
		TextView TextView_DialogNegativeButtonText;

		LinearLayout LinearLayout_DialogNeutralButton;
		TextView TextView_DialogNeutralButtonText;

		LinearLayout LinearLayout_DialogPositiveButton;
		TextView TextView_DialogPositiveButtonText;

		TextView TextView_DialogTouchOutside;
		
		 
		public slideDownDialogFragment(Activity context,slideDownDialogInterface listener) {
				this.mContext = context;
				this.mInterface = listener;
		}
		
		public void setDialogText(String text) {
				dialogText = text;
				if(TextView_DialogText!=null) {
						TextView_DialogText.setText(text);
						TextView_DialogText.setVisibility(text.isEmpty() ? View.GONE : View.VISIBLE);
				}
		}
		
		public void setDialogList(int mode, String[] items, int defaultsel, boolean closeonsel) {
				dialogListMode = mode;
				dialogListItems = items;
				dialogListDefault = defaultsel;
				dialogListClose = closeonsel;
		}
		public void setDialogListLimit(int limit, boolean alsoMin) {
				dialogListLimit = limit;
				dialogListLimitMin = alsoMin;
		}
		
		public void setDialogInput1(String descText,String defaultText,boolean allowEmpty,TextWatcher watcher) {
				dialogInput1descText = descText;
				dialogInput1defaultText = defaultText;
				dialogInput1allowEmpty = allowEmpty;
				dialogInput1textWatcher = watcher;
		}
		public void setDialogInput2(String descText,String defaultText,boolean allowEmpty,TextWatcher watcher) {
				dialogInput2descText = descText;
				dialogInput2defaultText = defaultText;
				dialogInput2allowEmpty = allowEmpty;
				dialogInput2textWatcher = watcher;
		}
		
		public void setDialogColorPicker(String defaultColor,boolean showOpacityBar) {
				dialogColorPickerdefaultValue = defaultColor;
				dialogColorPickershowOpacityBar = showOpacityBar;
		}
		
		public void setOverwriteInfo(boolean enabled) {
				TextView_OverwriteInfo.setVisibility(enabled ? View.VISIBLE : View.GONE);
		}
		
		public void setDialogNegativeButton(String text) {
				negativeButtonText = text;
		}
		public void setDialogNeutralButton(String text) {
				neutralButtonText = text;
		}
		public void setDialogPositiveButton(String text) {
				positiveButtonText = text;
		}
		
		public void setDialogCloseOnTouchOutside(boolean enabled) {
				closeOnTouchOutside = enabled;
		}
		
		public interface slideDownDialogInterface {
				public void onListItemClick(int position, String text);
				public void onNegativeClick();
				public void onNeutralClick();
				public void onPositiveClick(ArrayList<String> resultData);
				public void onTouchOutside();
		}
		
				@Override
				public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
				{
						// TODO: Implement this method
						mFragment = this;
						View InflatedView = inflater.inflate(R.layout.slidedowndialogfragment,container,false);

						final LayoutTransition transitioner = new LayoutTransition();
						ObjectAnimator customAppearingAnim = ObjectAnimator.ofFloat(null, "rotationY", 0f, 0f).
								setDuration(transitioner.getDuration(LayoutTransition.APPEARING));
						customAppearingAnim.addListener(new AnimatorListenerAdapter() {
										public void onAnimationEnd(Animator anim) {
												View view = (View) ((ObjectAnimator) anim).getTarget();
												view.setRotationY(0f);
										}
								});
						ObjectAnimator customDisappearingAnim = ObjectAnimator.ofFloat(null, "rotationX", 0f, 0f).
								setDuration(transitioner.getDuration(LayoutTransition.DISAPPEARING));
						customDisappearingAnim.addListener(new AnimatorListenerAdapter() {
										public void onAnimationEnd(Animator anim) {
												View view = (View) ((ObjectAnimator) anim).getTarget();
												view.setRotationX(0f);
										}
								});
								
						TextView_DialogBg = (TextView) InflatedView.findViewById(R.id.slidedowndialogfragmentTextView_DialogBg);
						LinearLayout_DialogRoot = (LinearLayout) InflatedView.findViewById(R.id.slidedowndialogfragmentLinearLayout_DialogRoot);
						LinearLayout_MainContainer = (LinearLayout) InflatedView.findViewById(R.id.slidedowndialogfragmentLinearLayout_MainContainer);
						//LinearLayout_DialogRoot.setLayoutTransition(transitioner);
						//LinearLayout_MainContainer.setLayoutTransition(transitioner);
						TextView_DialogText = (TextView) InflatedView.findViewById(R.id.slidedowndialogfragmentTextView_DialogText);
						TextView_DialogBg.setVisibility(View.GONE);
						TextView_DialogBg.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View p1)
										{
												// Just to prevent touch trough
										}
								});
						LinearLayout_DialogRoot.setVisibility(View.GONE);

						LinearLayout_DialogListView = (LinearLayout) InflatedView.findViewById(R.id.slidedowndialogfragmentLinearLayout_DialogList);
						ListView_DialogListView = (ListView) InflatedView.findViewById(R.id.slidedowndialogfragmentListView_DialogList);
						ListView_DialogListView.setFastScrollEnabled(true);
						LinearLayout_DialogListView.setVisibility(View.GONE);

						LinearLayout_DialogInput1 = (LinearLayout) InflatedView.findViewById(R.id.slidedowndialogfragmentLinearLayout_DialogInput1);
						TextView_DialogInput1Text = (TextView) InflatedView.findViewById(R.id.slidedowndialogfragmentTextView_DialogInput1Text);
						EditText_DialogInput1 = (EditText) InflatedView.findViewById(R.id.slidedowndialogfragmentEditText_DialogInput1);
						LinearLayout_DialogInput1.setVisibility(View.GONE);

						LinearLayout_DialogInput2 = (LinearLayout) InflatedView.findViewById(R.id.slidedowndialogfragmentLinearLayout_DialogInput2);
						TextView_DialogInput2Text = (TextView) InflatedView.findViewById(R.id.slidedowndialogfragmentTextView_DialogInput2Text);
						EditText_DialogInput2 = (EditText) InflatedView.findViewById(R.id.slidedowndialogfragmentEditText_DialogInput2);
						LinearLayout_DialogInput2.setVisibility(View.GONE);

						LinearLayout_DialogColorPicker = (LinearLayout) InflatedView.findViewById(R.id.slidedowndialogfragmentLinearLayout_DialogColorPicker);
						ColorPicker_DialogColorPicker = (ColorPicker) InflatedView.findViewById(R.id.slidedowndialogfragmentColorPicker_DialogColorPicker);
						ValueBar_DialogValueBar = (ValueBar) InflatedView.findViewById(R.id.slidedowndialogfragmentValueBar_DialogValueBar);
						SaturationBar_DialogSaturationBar = (SaturationBar) InflatedView.findViewById(R.id.slidedowndialogfragmentSaturationBar_DialogSaturationBar);
						OpacityBar_DialogOpacityBar = (OpacityBar) InflatedView.findViewById(R.id.slidedowndialogfragmentOpacityBar_DialogOpacityBar);
						EditText_DialogHexInput = (EditText) InflatedView.findViewById(R.id.slidedowndialogfragmentEditText_DialogHexInput);
						LinearLayout_DialogColorPicker.setVisibility(View.GONE);

						TextView_OverwriteInfo = (TextView) InflatedView.findViewById(R.id.slidedowndialogfragmentTextView_OverwriteInfo);
						TextView_OverwriteInfo.setVisibility(View.GONE);
						
						LinearLayout_Buttons = (LinearLayout) InflatedView.findViewById(R.id.slidedowndialogfragmentLinearLayout_DialogButtons);
						
						LinearLayout_DialogNegativeButton = (LinearLayout) InflatedView.findViewById(R.id.slidedowndialogfragmentLinearLayout_DialogButtonNegative);
						TextView_DialogNegativeButtonText = (TextView) InflatedView.findViewById(R.id.slidedowndialogfragmentTextView_DialogButtonNegativeText);
						LinearLayout_DialogNegativeButton.setVisibility(View.GONE);

						LinearLayout_DialogNeutralButton = (LinearLayout) InflatedView.findViewById(R.id.slidedowndialogfragmentLinearLayout_DialogButtonNeutral);
						TextView_DialogNeutralButtonText = (TextView) InflatedView.findViewById(R.id.slidedowndialogfragmentTextView_DialogButtonNeutralText);
						LinearLayout_DialogNeutralButton.setVisibility(View.GONE);

						LinearLayout_DialogPositiveButton = (LinearLayout) InflatedView.findViewById(R.id.slidedowndialogfragmentLinearLayout_DialogButtonPositive);
						TextView_DialogPositiveButtonText = (TextView) InflatedView.findViewById(R.id.slidedowndialogfragmentTextView_DialogButtonPositiveText);

						TextView_DialogTouchOutside = (TextView) InflatedView.findViewById(R.id.slidedowndialogfragmentTextView_DialogTouchOutside);
						TextView_DialogTouchOutside.setVisibility(View.GONE);


						TextView_DialogText.setText(dialogText);
						TextView_DialogText.setVisibility(dialogText.isEmpty() ? View.GONE : View.VISIBLE);
						
						br = new BroadcastReceiver() {

								@Override
								public void onReceive(Context p1, Intent p2)
								{
										// TODO: Implement this method
										if(p2.getAction().equalsIgnoreCase("de.NeonSoft.neopowermenu.closeDialogs")) {
												if(negativeButtonText!=null) {
														mInterface.onNegativeClick();
												} else if (positiveButtonText!=null) {
														mInterface.onPositiveClick(null);
												}
												closeDialog();
										}
								}
						};
						IntentFilter filter = new IntentFilter();
						filter.addAction("de.NeonSoft.neopowermenu.closeDialogs");
						filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
						mContext.registerReceiver(br, filter);
						
						if(dialogListItems != null && dialogListItems.length > 0) {
								ListView_DialogListView.setAdapter(new ArrayAdapter<String>(mContext,(dialogListMode==ListView.CHOICE_MODE_NONE ? android.R.layout.simple_list_item_1 : (dialogListMode==ListView.CHOICE_MODE_MULTIPLE ? android.R.layout.simple_list_item_multiple_choice : android.R.layout.simple_list_item_single_choice)),dialogListItems));
								ListView_DialogListView.setChoiceMode(dialogListMode);
								if(dialogListDefault>-1) {
										ListView_DialogListView.setItemChecked(dialogListDefault,true);
										ListView_DialogListView.setSelection(dialogListDefault);
								}
								ListView_DialogListView.setOnItemClickListener(new OnItemClickListener() {

												@Override
												public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
												{
														// TODO: Implement this method
														if(dialogListLimit > 0) {
																if(ListView_DialogListView.getCheckedItemCount()>dialogListLimit) {
																		ListView_DialogListView.setItemChecked(p3,false);
																		return;
																}
														}
														mInterface.onListItemClick(p3,ListView_DialogListView.getItemAtPosition(p3).toString());
														if(dialogListClose) {
																closeDialog();
														}
												}
										});
								LinearLayout_DialogListView.setVisibility(View.VISIBLE);
						}
						
						if(dialogInput1descText!=null) {
								TextView_DialogInput1Text.setText(dialogInput1descText);
								if(dialogInput1textWatcher!=null) EditText_DialogInput1.addTextChangedListener(dialogInput1textWatcher);
								EditText_DialogInput1.setText(dialogInput1defaultText);
								LinearLayout_DialogInput1.setVisibility(View.VISIBLE);
						}
						if(dialogInput2descText!=null) {
								TextView_DialogInput2Text.setText(dialogInput2descText);
								if(dialogInput2textWatcher!=null) EditText_DialogInput2.addTextChangedListener(dialogInput2textWatcher);
								EditText_DialogInput2.setText(dialogInput2defaultText);
								LinearLayout_DialogInput2.setVisibility(View.VISIBLE);
						}
						
						if(dialogColorPickerdefaultValue!=null) {
								ColorPicker_DialogColorPicker.addValueBar(ValueBar_DialogValueBar);
								ColorPicker_DialogColorPicker.addSaturationBar(SaturationBar_DialogSaturationBar);
								ColorPicker_DialogColorPicker.addOpacityBar(OpacityBar_DialogOpacityBar);
								ColorPicker_DialogColorPicker.setOldCenterColor(Color.parseColor(dialogColorPickerdefaultValue));
								ColorPicker_DialogColorPicker.setColor(Color.parseColor(dialogColorPickerdefaultValue));
								OpacityBar_DialogOpacityBar.setVisibility(dialogColorPickershowOpacityBar ? View.VISIBLE : View.GONE);
								if(dialogColorPickershowOpacityBar) {
										EditText_DialogHexInput.setText(""+String.format("#%08X", (0xFFFFFFFF & Color.parseColor(dialogColorPickerdefaultValue))));
								} else {
										EditText_DialogHexInput.setText(""+String.format("#%06X", (0xFFFFFF & Color.parseColor(dialogColorPickerdefaultValue))));
								}
								ColorPicker_DialogColorPicker.setOnTouchListener(new OnTouchListener() {

												@Override
												public boolean onTouch(View p1, MotionEvent p2)
												{
														// TODO: Implement this method
														DialogColorPicker_HexChangeViaWheel = true;
														if(dialogColorPickershowOpacityBar) {
																EditText_DialogHexInput.setText(""+String.format("#%08X", (0xFFFFFFFF & ColorPicker_DialogColorPicker.getColor())));
														} else {
																EditText_DialogHexInput.setText(""+String.format("#%06X", (0xFFFFFF & ColorPicker_DialogColorPicker.getColor())));
														}
														return false;
												}
										});
								ValueBar_DialogValueBar.setOnTouchListener(new OnTouchListener() {

												@Override
												public boolean onTouch(View p1, MotionEvent p2)
												{
														// TODO: Implement this method
														DialogColorPicker_HexChangeViaWheel = true;
														if(dialogColorPickershowOpacityBar) {
																EditText_DialogHexInput.setText(""+String.format("#%08X", (0xFFFFFFFF & ColorPicker_DialogColorPicker.getColor())));
														} else {
																EditText_DialogHexInput.setText(""+String.format("#%06X", (0xFFFFFF & ColorPicker_DialogColorPicker.getColor())));
														}
														return false;
												}
										});
								SaturationBar_DialogSaturationBar.setOnTouchListener(new OnTouchListener() {

												@Override
												public boolean onTouch(View p1, MotionEvent p2)
												{
														// TODO: Implement this method
														DialogColorPicker_HexChangeViaWheel = true;
														if(dialogColorPickershowOpacityBar) {
																EditText_DialogHexInput.setText(""+String.format("#%08X", (0xFFFFFFFF & ColorPicker_DialogColorPicker.getColor())));
														} else {
																EditText_DialogHexInput.setText(""+String.format("#%06X", (0xFFFFFF & ColorPicker_DialogColorPicker.getColor())));
														}
														return false;
												}
										});
								OpacityBar_DialogOpacityBar.setOnTouchListener(new OnTouchListener() {

												@Override
												public boolean onTouch(View p1, MotionEvent p2)
												{
														// TODO: Implement this method
														DialogColorPicker_HexChangeViaWheel = true;
														if(dialogColorPickershowOpacityBar) {
																EditText_DialogHexInput.setText(""+String.format("#%08X", (0xFFFFFFFF & ColorPicker_DialogColorPicker.getColor())));
														} else {
																EditText_DialogHexInput.setText(""+String.format("#%06X", (0xFFFFFF & ColorPicker_DialogColorPicker.getColor())));
														}
														return false;
												}
										});
								EditText_DialogHexInput.setOnTouchListener(new OnTouchListener() {

												@Override
												public boolean onTouch(View p1, MotionEvent p2)
												{
														// TODO: Implement this method
														DialogColorPicker_HexChangeViaWheel = false;
														return false;
												}
										});
								EditText_DialogHexInput.addTextChangedListener(new TextWatcher() {

												@Override
												public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
												{
														// TODO: Implement this method
												}

												@Override
												public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
												{
														// TODO: Implement this method
												}

												@Override
												public void afterTextChanged(Editable p1)
												{
														// TODO: Implement this method
														if(!DialogColorPicker_HexChangeViaWheel) {
																try {
																		EditText_DialogHexInput.setTextColor(Color.parseColor("#FFFFFF"));
																		ColorPicker_DialogColorPicker.setColor(Color.parseColor(EditText_DialogHexInput.getText().toString()));
																		//picker.invalidate();
																} catch (Throwable e) {
																		EditText_DialogHexInput.setTextColor(Color.parseColor("#FF0000"));
																}
														} else {
																//DialogColorPicker_HexChangeViaWheel = false;
														}
												}
										});
										LinearLayout_DialogColorPicker.setVisibility(View.VISIBLE);
						}
						
						if(negativeButtonText != null && !negativeButtonText.isEmpty()) {
								TextView_DialogNegativeButtonText.setText(negativeButtonText);
								LinearLayout_DialogNegativeButton.setOnClickListener(new OnClickListener() {

												@Override
												public void onClick(View p1)
												{
														// TODO: Implement this method
														mInterface.onNegativeClick();
														closeDialog();
												}
										});
								LinearLayout_DialogNegativeButton.setVisibility(View.VISIBLE);
						}
						if(neutralButtonText != null && !neutralButtonText.isEmpty()) {
								TextView_DialogNeutralButtonText.setText(neutralButtonText);
								LinearLayout_DialogNeutralButton.setOnClickListener(new OnClickListener() {

												@Override
												public void onClick(View p1)
												{
														// TODO: Implement this method
														mInterface.onNeutralClick();
														closeDialog();
												}
										});
								LinearLayout_DialogNeutralButton.setVisibility(View.VISIBLE);
						}
						TextView_DialogPositiveButtonText.setText(positiveButtonText);
						LinearLayout_DialogPositiveButton.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View p1)
										{
												// TODO: Implement this method
												ArrayList<String> resultData = new ArrayList<String>();
												if(dialogListItems!=null) {
														if(ListView_DialogListView.getCheckedItemCount() < 1) {
																return;
														}
														if(dialogListLimitMin && dialogListLimit>0 && ListView_DialogListView.getCheckedItemCount() < dialogListLimit) {
																return;
														}
														if(dialogListMode==ListView.CHOICE_MODE_SINGLE) {
																resultData.add(dialogListItems[ListView_DialogListView.getCheckedItemPosition()]);
														} else if(dialogListMode==ListView.CHOICE_MODE_MULTIPLE) {
																String string = "";
																int checked = 0;
																for(int i = 0;i<dialogListItems.length;i++) {
																		if(ListView_DialogListView.isItemChecked(i)) {
																				checked++;
																				string = string + i + ((checked>=ListView_DialogListView.getCheckedItemCount()) ? "" : ",");
																		}
																}
																resultData.add(string);
														}
												}
												if(dialogInput1descText!=null) {
														if(!dialogInput1allowEmpty&&EditText_DialogInput1.getText().toString().isEmpty()) {
																return;
														}
														resultData.add(EditText_DialogInput1.getText().toString());
												}
												if(dialogInput2descText!=null) {
														if(!dialogInput2allowEmpty&&EditText_DialogInput2.getText().toString().isEmpty()) {
																return;
														}
														resultData.add(EditText_DialogInput2.getText().toString());
												}
												if(dialogColorPickerdefaultValue!=null) {
														resultData.add(EditText_DialogHexInput.getText().toString());
												}
												mInterface.onPositiveClick(resultData);
												closeDialog();
										}
								});
						if(closeOnTouchOutside) {
								TextView_DialogTouchOutside.setOnClickListener(new OnClickListener() {

												@Override
												public void onClick(View p1)
												{
														// TODO: Implement this method
														mInterface.onTouchOutside();
														closeDialog();
												}
										});
						}

						TextView_DialogTouchOutside.setVisibility(View.VISIBLE);
						TextView_DialogBg.setVisibility(View.VISIBLE);
						TextView_DialogBg.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_in));
						LinearLayout_DialogRoot.setVisibility(View.VISIBLE);
						LinearLayout_DialogRoot.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.abc_slide_in_top));

						if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.LOLLIPOP) {
								//mContext.getWindow().setNavigationBarColor(Color.parseColor(String.format("#%08X", (0xCC000000 & mContext.getResources().getColor(R.color.window_background_dark)))));
						}
						
						return InflatedView;
				}
		
		public void closeDialog() {
				InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE); 
				IBinder windowToken = null;
				if(EditText_DialogInput1.isFocused()) {
						windowToken = EditText_DialogInput1.getWindowToken();
				} else if (EditText_DialogInput2.isFocused()) {
						windowToken = EditText_DialogInput2.getWindowToken();
				} else if (EditText_DialogHexInput.isFocused()) {
						windowToken = EditText_DialogHexInput.getWindowToken();
				}
				if(windowToken!=null) inputManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);
				TextView_DialogBg.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_out));
				TextView_DialogBg.setVisibility(View.GONE);
				Animation hideAnim = AnimationUtils.loadAnimation(mContext,R.anim.abc_slide_out_top);
				hideAnim.setAnimationListener(new Animation.AnimationListener() {

								@Override
								public void onAnimationEnd(Animation p1)
								{
										// TODO: Implement this method
										mContext.unregisterReceiver(br);
										MainActivity.fragmentManager.beginTransaction().remove(mFragment).commit();
										if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.LOLLIPOP) {
												//mContext.getWindow().setNavigationBarColor(mContext.getResources().getColor(R.color.window_background_dark));
										}
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
				LinearLayout_DialogRoot.startAnimation(hideAnim);
				LinearLayout_DialogRoot.setVisibility(View.GONE);
		}
		
}

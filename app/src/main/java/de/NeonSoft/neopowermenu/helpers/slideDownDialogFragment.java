package de.NeonSoft.neopowermenu.helpers;
import android.animation.*;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.view.inputmethod.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.larswerkman.holocolorpicker.*;
import de.NeonSoft.neopowermenu.*;
import java.util.*;

import android.content.ClipboardManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import de.NeonSoft.neopowermenu.R;

public class slideDownDialogFragment extends android.support.v4.app.DialogFragment
{

		public static String RESULT_LIST = "listResult";
		public static String RESULT_INPUT = "inputResult_";
		public static String RESULT_COLORPICKER = "colorpickerResult";
		public static String RESULT_CHECKBOX = "checkboxResult";

		public static int BUTTON_OK = 0;
		public static int BUTTON_YES = 1;
		public static int BUTTON_NO = 2;
		public static int BUTTON_RENAME = 3;
		public static int BUTTON_CANCEL = 4;
		public static int BUTTON_DELETE = 5;
		public static int BUTTON_LOAD = 6;
		public static int BUTTON_SAVE = 7;
		public static int BUTTON_IGNORE = 8;
		
		public static String dialogTag = "slideDownDialog";
		public static String dialogCloseCall = "de.NeonSoft.slideDownDialog.close";
		public static ArrayList<slideDownDialogFragment> dialogs = new ArrayList<slideDownDialogFragment>();
		
		private Activity mContext;
		Handler handler;
		private slideDownDialogInterface mInterface;
		private android.support.v4.app.Fragment mFragment;
		private android.support.v4.app.FragmentManager mFragmentmanager;
		BroadcastReceiver br;
		
		private boolean dialogCloseOnButtonClick = true;
		
		 private String dialogText = "";
		 private float dialogTextSize = -1;
	
		 private ArrayAdapter<String> dialogListAdapter = null;
		 private int dialogListMode;
		 private int dialogListLimit = 0;
		 private boolean dialogListLimitMin = false;
		 private String[] dialogListItems;
		 private int dialogListDefault = 0;
		 private boolean dialogListClose = true;
		 
		 private ArrayList<EditText> dialogInputs = new ArrayList<EditText>();
		private ArrayList<String> dialogInputDescText = new ArrayList<String>();
		private ArrayList<String> dialogInputDefaultText = new ArrayList<String>();
		private ArrayList<Boolean> dialogInputAllowEmpty = new ArrayList<Boolean>();
		private ArrayList<TextWatcher> dialogInputTextWatcher = new ArrayList<TextWatcher>();
		private ArrayList<Integer> dialogInputMode = new ArrayList<Integer>();
		private ArrayList<Boolean> dialogInputSingleLine = new ArrayList<Boolean>();
		 
		private LinearLayout LinearLayout_InputHolder;
		
		/*
		 private String dialogInput1descText;
		 private String dialogInput1defaultText;
		 private boolean dialogInput1allowEmpty;
		private TextWatcher dialogInput1textWatcher;
		private int dialogInput1mode = InputType.TYPE_TEXT_VARIATION_NORMAL;
		private boolean dialogInput1singleline = true;

		private String dialogInput2descText;
		private String dialogInput2defaultText;
		private boolean dialogInput2allowEmpty;
		private TextWatcher dialogInput2textWatcher;
		private int dialogInput2mode = InputType.TYPE_TEXT_VARIATION_NORMAL;
		private boolean dialogInput2singleline = true;
		*/
		
		private TextView TextView_InputAssistInfo;
		private String dialogInputAssistInfoString;
		
		private String dialogColorPickerdefaultValue;
		private boolean dialogColorPickershowOpacityBar;
		
		private String dialogCheckBoxtext;
		
		private boolean dialogShowProgressBar = false;
		private boolean dialogProgressBlink = false;
		private boolean dialogProgressShowText = true;
		
		 private String negativeButtonText = null;
		 private String neutralButtonText = null;
		 private String positiveButtonText = "Ok";

		 private boolean closeOnTouchOutside = true;
		
		 private boolean hideActive = false;
		 
		TextView TextView_DialogBg;
		LinearLayout LinearLayout_DialogRoot;
		LinearLayout LinearLayout_MainContainer;
		TextView TextView_DialogText;

		LinearLayout LinearLayout_DialogListView;
		ListView ListView_DialogListView;

		/*
		LinearLayout LinearLayout_DialogInput1;
		TextView TextView_DialogInput1Text;
		EditText EditText_DialogInput1;
		TextWatcher TextWatcher_DialogInput1;

		LinearLayout LinearLayout_DialogInput2;
		TextView TextView_DialogInput2Text;
		EditText EditText_DialogInput2;
		TextWatcher TextWatcher_DialogInput2;
		*/
		
		boolean DialogColorPicker_HexChangeViaWheel = false;

		LinearLayout LinearLayout_DialogColorPicker;
		ColorPicker ColorPicker_DialogColorPicker;
		ValueBar ValueBar_DialogValueBar;
		SaturationBar SaturationBar_DialogSaturationBar;
		OpacityBar OpacityBar_DialogOpacityBar;
		EditText EditText_DialogHexInput;

		LinearLayout LinearLayout_DialogCheckBox;
		CheckBox CheckBox_DialogCheckBox;
		
		RelativeLayout RelativeLayout_Progress;
		ProgressBar ProgressBar_Progress;
		RelativeLayout RelativeLayout_ProgressText;
		TextView TextView_ProgressText;
		
		LinearLayout LinearLayout_Buttons;
		
		LinearLayout LinearLayout_DialogNegativeButton;
		TextView TextView_DialogNegativeButtonText;

		LinearLayout LinearLayout_DialogNeutralButton;
		TextView TextView_DialogNeutralButtonText;

		LinearLayout LinearLayout_DialogPositiveButton;
		TextView TextView_DialogPositiveButtonText;

		TextView TextView_DialogTouchOutside;
		
		public slideDownDialogFragment() {
				this.mContext = null;
				this.mInterface = new slideDownDialogInterface() {

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
						public void onPositiveClick(Bundle resultBundle)
						{
								// TODO: Implement this method
						}

						@Override
						public void onTouchOutside()
						{
								// TODO: Implement this method
						}
				};
				this.mFragmentmanager = null;
		}

		public slideDownDialogFragment(Activity context,android.support.v4.app.FragmentManager fragmentmanager) {
				this.mContext = context;
				this.mInterface = new slideDownDialogInterface() {

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
						public void onPositiveClick(Bundle resultBundle)
						{
								// TODO: Implement this method
						}

						@Override
						public void onTouchOutside()
						{
								// TODO: Implement this method
						}
				};
				this.mFragmentmanager = fragmentmanager;
		}
		public slideDownDialogFragment(Activity context, android.support.v4.app.FragmentManager fragmentmanager, slideDownDialogInterface listener) {
				this.mContext = context;
				this.mInterface = listener;
				this.mFragmentmanager = fragmentmanager;
		}
		
		public void setListener(slideDownDialogInterface listener) {
				this.mInterface = listener;
		}
		
		public void setCloseOnButtonClick(boolean close) {
				this.dialogCloseOnButtonClick = close;
		}
		
		public void setText(String text) {
				dialogText = text;
				if(TextView_DialogText!=null) {
						TextView_DialogText.setText(text);
						TextView_DialogText.setVisibility(text.isEmpty() ? View.GONE : View.VISIBLE);
				}
		}
		public String getText() {
				return dialogText;
		}
		public void setTextSize(float size) {
				if(TextView_DialogText!=null) {
						TextView_DialogText.setTextSize(size);
				}
				dialogTextSize = size;
		}
		
		public void setList(int mode, ArrayList<String> items, int defaultsel, boolean closeonsel) {
				dialogListMode = mode;
				dialogListItems = (String[]) items.toArray();
				dialogListDefault = defaultsel;
				dialogListClose = closeonsel;
		}
		public void setList(int mode, String[] items, int defaultsel, boolean closeonsel) {
				dialogListMode = mode;
				dialogListItems = items;
				dialogListDefault = defaultsel;
				dialogListClose = closeonsel;
		}
		public void setListLimit(int limit, boolean alsoMin) {
				dialogListLimit = limit;
				dialogListLimitMin = alsoMin;
		}
		
		public void addInput(String descText, String defaultText, boolean allowEmpty, TextWatcher watcher) {
				dialogInputs.add(null);
				dialogInputDescText.add(descText);
				dialogInputDefaultText.add(defaultText);
				dialogInputAllowEmpty.add(allowEmpty);
				dialogInputTextWatcher.add(watcher);
				dialogInputMode.add(InputType.TYPE_CLASS_TEXT);
				dialogInputSingleLine.add(true);
		}
		
		/*public void setInput1(String descText,String defaultText,boolean allowEmpty,TextWatcher watcher) {
				dialogInput1descText = descText;
				dialogInput1defaultText = defaultText;
				dialogInput1allowEmpty = allowEmpty;
				dialogInput1textWatcher = watcher;
		}
		public void setInput2(String descText,String defaultText,boolean allowEmpty,TextWatcher watcher) {
				dialogInput2descText = descText;
				dialogInput2defaultText = defaultText;
				dialogInput2allowEmpty = allowEmpty;
				dialogInput2textWatcher = watcher;
		}*/

		public void showAssistInfo(boolean enabled) {
				TextView_InputAssistInfo.setVisibility(enabled ? View.VISIBLE : View.GONE);
		}
		
		public void setInputAssistInfo(String text) {
				if(TextView_InputAssistInfo != null) {
						TextView_InputAssistInfo.setText(text);
				}
				dialogInputAssistInfoString = text;
		}
		
		public void setInputMode(int input, int mode) {
				dialogInputMode.set(input,InputType.TYPE_CLASS_TEXT | mode);
				if(dialogInputs.get(input) != null) dialogInputs.get(input).setInputType(InputType.TYPE_CLASS_TEXT | mode);
				/*if (input == 1) {
						dialogInput1mode = mode;
						if(EditText_DialogInput1 != null) EditText_DialogInput1.setInputType(InputType.TYPE_CLASS_TEXT | mode);
				} else if (input == 2) {
						dialogInput2mode = mode;
						if(EditText_DialogInput2 != null) EditText_DialogInput2.setInputType(InputType.TYPE_CLASS_TEXT | mode);
				}*/
		}

		public void setInputSingleLine(int input, boolean mode) {
				dialogInputSingleLine.set(input,mode);
				if(dialogInputs.get(input) != null) dialogInputs.get(input).setSingleLine(mode);
				/*if (input == 1) {
						dialogInput1singleline = mode;
						if(EditText_DialogInput1 != null) EditText_DialogInput1.setSingleLine(mode);
				} else if (input == 2) {
						dialogInput2singleline = mode;
						if(EditText_DialogInput2 != null) EditText_DialogInput2.setSingleLine(mode);
				}*/
		}
		
		public void setColorPicker(String defaultColor,boolean showOpacityBar) {
				dialogColorPickerdefaultValue = defaultColor;
				dialogColorPickershowOpacityBar = showOpacityBar;
		}
		
		public void setCheckBox(final String text) {
				dialogCheckBoxtext = text;
				if(LinearLayout_DialogCheckBox != null && CheckBox_DialogCheckBox.getText().toString().equals(text)) {
						Animation fadeOut = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
						fadeOut.setAnimationListener(new Animation.AnimationListener() {

										@Override
										public void onAnimationEnd(Animation p1)
										{
												// TODO: Implement this method
												CheckBox_DialogCheckBox.setText(text);
												LinearLayout_DialogCheckBox.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
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
						LinearLayout_DialogCheckBox.startAnimation(fadeOut);
				}
		}
		
		public void addProgressBar(boolean showText, boolean blink) {
				dialogShowProgressBar = true;
				dialogProgressShowText = showText;
				dialogProgressBlink = blink;
				if(RelativeLayout_Progress!=null && dialogShowProgressBar == false) {
						if(showText && !blink) {
								RelativeLayout_ProgressText.setVisibility(View.VISIBLE);
								RelativeLayout_ProgressText.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
						} else {
								RelativeLayout_ProgressText.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
								RelativeLayout_ProgressText.setVisibility(View.INVISIBLE);
						}
						RelativeLayout_Progress.setVisibility(View.VISIBLE);
						RelativeLayout_Progress.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
						if(blink) {
								ProgressBar_Progress.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.progress_blink));
						} else {
								ProgressBar_Progress.setAlpha((float) 0.2);
								ProgressBar_Progress.clearAnimation();
						}
				}
		}
		public void setProgressBarBlink(boolean blink) {
				if(blink != dialogProgressBlink) {
				dialogProgressBlink = blink;
				if(blink) {
						if(dialogProgressShowText) {
								RelativeLayout_ProgressText.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
								RelativeLayout_ProgressText.setVisibility(View.INVISIBLE);
						}
						ProgressBar_Progress.setAlpha((float) 1);
						ProgressBar_Progress.setProgress(100);
						ProgressBar_Progress.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.progress_blink));
				} else {
						if(dialogProgressShowText) {
								RelativeLayout_ProgressText.setVisibility(View.VISIBLE);
								RelativeLayout_ProgressText.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
						}
						ProgressBar_Progress.setAlpha((float) 0.2);
						ProgressBar_Progress.setProgress(0);
						ProgressBar_Progress.clearAnimation();
				}
				}
		}
		public void setProgressBarShowText(boolean showText) {
				if(showText != dialogProgressShowText) {
				dialogProgressShowText = showText;
				if(!showText) {
						RelativeLayout_ProgressText.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
						RelativeLayout_ProgressText.setVisibility(View.INVISIBLE);
						if(dialogProgressBlink) {
								ProgressBar_Progress.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.progress_blink));
						}
				} else {
						RelativeLayout_ProgressText.setVisibility(View.VISIBLE);
						RelativeLayout_ProgressText.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
						if(dialogProgressBlink) {
								ProgressBar_Progress.clearAnimation();
						}
				}
				}
		}
		public void setProgressBar(int percent) {
				if(RelativeLayout_Progress!=null) {
						ProgressBar_Progress.setProgress(percent);
						TextView_ProgressText.setText(percent+"%");
				}
		}
		
		public void setNegativeButton(final String text) {
				if(text != null && !text.isEmpty()) {
				negativeButtonText = text;
				if(LinearLayout_DialogNegativeButton != null && TextView_DialogNegativeButtonText.getText().toString().equals(text)) {
						Animation fadeOut = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
						fadeOut.setAnimationListener(new Animation.AnimationListener() {

										@Override
										public void onAnimationEnd(Animation p1)
										{
												// TODO: Implement this method
												TextView_DialogNegativeButtonText.setText(text);
												LinearLayout_DialogNegativeButton.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
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
						LinearLayout_DialogNegativeButton.startAnimation(fadeOut);
				}
		} else {
				negativeButtonText = null;
				if(LinearLayout_DialogNegativeButton != null) {
						LinearLayout_DialogNegativeButton.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
						LinearLayout_DialogNegativeButton.setVisibility(View.GONE);
				}
		}
		}
		public void setNeutralButton(final String text) {
				if(text != null && !text.isEmpty()) {
				neutralButtonText = text;
				if(LinearLayout_DialogNeutralButton != null && TextView_DialogNeutralButtonText.getText().toString().equals(text)) {
						Animation fadeOut = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
						fadeOut.setAnimationListener(new Animation.AnimationListener() {

										@Override
										public void onAnimationEnd(Animation p1)
										{
												// TODO: Implement this method
												TextView_DialogNeutralButtonText.setText(text);
												LinearLayout_DialogNeutralButton.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
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
						TextView_DialogNeutralButtonText.startAnimation(fadeOut);
				}
				} else {
						neutralButtonText = null;
						if(LinearLayout_DialogNeutralButton != null) {
								LinearLayout_DialogNeutralButton.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
								LinearLayout_DialogNeutralButton.setVisibility(View.GONE);
						}
				}
		}
		public void setPositiveButton(final String text) {
				if(text != null && !text.isEmpty()) {
				positiveButtonText = text;
				if(LinearLayout_DialogPositiveButton != null && TextView_DialogPositiveButtonText.getText().toString().equals(text)) {
						Animation fadeOut = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
						fadeOut.setAnimationListener(new Animation.AnimationListener() {

										@Override
										public void onAnimationEnd(Animation p1)
										{
												// TODO: Implement this method
												TextView_DialogPositiveButtonText.setText(text);
												LinearLayout_DialogPositiveButton.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
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
						LinearLayout_DialogPositiveButton.startAnimation(fadeOut);
				}
		} else {
				positiveButtonText = null;
				if(LinearLayout_DialogPositiveButton != null) {
				LinearLayout_DialogPositiveButton.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
				LinearLayout_DialogPositiveButton.setVisibility(View.GONE);
				}
		}
		}
		
		public void setCloseOnTouchOutside(boolean enabled) {
				closeOnTouchOutside = enabled;
				if(TextView_DialogTouchOutside != null && enabled) {
						TextView_DialogTouchOutside.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View p1)
										{
												// TODO: Implement this method
												mInterface.onTouchOutside();
												closeDialog();
										}
								});
				} else if(TextView_DialogTouchOutside != null && !enabled) {
						TextView_DialogTouchOutside.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View p1)
										{
												// TODO: Implement this method
										}
								});
				}
		}
		
		public void showDialog(int mDialogContainer) {
				try {
						if(mFragmentmanager==null) {
								throw new Throwable("mFragmentmanager is null!");
						}
						if(mDialogContainer > -1) {
								slideDownDialogFragment.dialogs.add(this);
								mFragmentmanager.beginTransaction().add(mDialogContainer, this, slideDownDialogFragment.dialogTag).commitAllowingStateLoss();
						} else {
								slideDownDialogFragment.dialogs.add(this);
								setStyle(R.style.slideDownDialogFragment,R.style.slideDownDialogFragment);
								show(mFragmentmanager,dialogTag);
						}
				} catch (final Throwable t) {
						AlertDialog.Builder adb = new AlertDialog.Builder(mContext);
						adb.setMessage("Failed to show slideDownDialogFragment:\n"+t);
						adb.setNeutralButton("Copy to clipboard", new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface p1, int p2)
										{
												try {
														ClipboardManager cpm = new ClipboardManager(mContext, new Handler());
														cpm.setText(t.toString());
														if(cpm.getText().toString().equals(t.toString())) {
																Toast.makeText(mContext,"Copied to clipboard.",Toast.LENGTH_SHORT).show();
														} else {
																Toast.makeText(mContext,"Failed to put in clipboard...",Toast.LENGTH_SHORT).show();
														}
												}
												catch (Throwable t) {
														Log.e("NPM","Failed to put in clipboard: "+t.toString());
														Toast.makeText(mContext,"Failed to put in clipboard...",Toast.LENGTH_SHORT).show();
												}
										}
								});
						adb.setPositiveButton("Ok",null);
						adb.show();
				}
		}
		
		public interface slideDownDialogInterface {
				public void onListItemClick(int position, String text);
				public void onNegativeClick();
				public void onNeutralClick();
				public void onPositiveClick(Bundle resultBundle);
				public void onTouchOutside();
		}
		
				@Override
				public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
				{
						// TODO: Implement this method
						mFragment = this;
						handler = new Handler();
						View InflatedView = inflater.inflate(R.layout.slidedowndialogfragment,container,false);
						
						TextView_DialogBg = (TextView) InflatedView.findViewById(R.id.slidedowndialogfragmentTextView_DialogBg);
						LinearLayout_DialogRoot = (LinearLayout) InflatedView.findViewById(R.id.slidedowndialogfragmentLinearLayout_DialogRoot);
						TextView_DialogBg.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View p1)
										{
												// Just to prevent touch trough
										}
								});
								
						LinearLayout_MainContainer = (LinearLayout) InflatedView.findViewById(R.id.slidedowndialogfragmentLinearLayout_MainContainer);
						//LinearLayout_DialogRoot.setLayoutTransition(transitioner);
						//LinearLayout_MainContainer.setLayoutTransition(transitioner);
						TextView_DialogText = (TextView) InflatedView.findViewById(R.id.slidedowndialogfragmentTextView_DialogText);
						//TextView_DialogText.setMovementMethod(new ScrollingMovementMethod());
						TextView_DialogBg.setVisibility(View.GONE);
						LinearLayout_DialogRoot.setVisibility(View.GONE);

						LinearLayout_DialogListView = (LinearLayout) InflatedView.findViewById(R.id.slidedowndialogfragmentLinearLayout_DialogList);
						ListView_DialogListView = (ListView) InflatedView.findViewById(R.id.slidedowndialogfragmentListView_DialogList);
						ListView_DialogListView.setFastScrollEnabled(true);
						LinearLayout_DialogListView.setVisibility(View.GONE);

						LinearLayout_InputHolder = (LinearLayout) InflatedView.findViewById(R.id.slidedowndialogfragmentLinearLayout_InputHolder);
						
						/*
						LinearLayout_DialogInput1 = (LinearLayout) InflatedView.findViewById(R.id.slidedowndialogfragmentLinearLayout_DialogInput1);
						TextView_DialogInput1Text = (TextView) InflatedView.findViewById(R.id.slidedowndialogfragmentTextView_DialogInput1Text);
						EditText_DialogInput1 = (EditText) InflatedView.findViewById(R.id.slidedowndialogfragmentEditText_DialogInput1);
						LinearLayout_DialogInput1.setVisibility(View.GONE);

						LinearLayout_DialogInput2 = (LinearLayout) InflatedView.findViewById(R.id.slidedowndialogfragmentLinearLayout_DialogInput2);
						TextView_DialogInput2Text = (TextView) InflatedView.findViewById(R.id.slidedowndialogfragmentTextView_DialogInput2Text);
						EditText_DialogInput2 = (EditText) InflatedView.findViewById(R.id.slidedowndialogfragmentEditText_DialogInput2);
						LinearLayout_DialogInput2.setVisibility(View.GONE);
						*/
						
						LinearLayout_DialogColorPicker = (LinearLayout) InflatedView.findViewById(R.id.slidedowndialogfragmentLinearLayout_DialogColorPicker);
						ColorPicker_DialogColorPicker = (ColorPicker) InflatedView.findViewById(R.id.slidedowndialogfragmentColorPicker_DialogColorPicker);
						ValueBar_DialogValueBar = (ValueBar) InflatedView.findViewById(R.id.slidedowndialogfragmentValueBar_DialogValueBar);
						SaturationBar_DialogSaturationBar = (SaturationBar) InflatedView.findViewById(R.id.slidedowndialogfragmentSaturationBar_DialogSaturationBar);
						OpacityBar_DialogOpacityBar = (OpacityBar) InflatedView.findViewById(R.id.slidedowndialogfragmentOpacityBar_DialogOpacityBar);
						EditText_DialogHexInput = (EditText) InflatedView.findViewById(R.id.slidedowndialogfragmentEditText_DialogHexInput);
						LinearLayout_DialogColorPicker.setVisibility(View.GONE);

						TextView_InputAssistInfo = (TextView) InflatedView.findViewById(R.id.slidedowndialogfragmentTextView_OverwriteInfo);
						TextView_InputAssistInfo.setVisibility(View.GONE);
						
						LinearLayout_DialogCheckBox = (LinearLayout) InflatedView.findViewById(R.id.slidedowndialogfragmentLinearLayout_CheckBox);
						CheckBox_DialogCheckBox = (CheckBox) InflatedView.findViewById(R.id.slidedowndialogfragmentCheckBox_CheckBox);
						LinearLayout_DialogCheckBox.setVisibility(View.GONE);
						
						RelativeLayout_Progress = (RelativeLayout) InflatedView.findViewById(R.id.slidedowndialogfragmentRelativeLayout_Progressbar);
						ProgressBar_Progress = (ProgressBar) InflatedView.findViewById(R.id.slidedowndialogfragmentProgressBar_Progressbar);
						RelativeLayout_ProgressText = (RelativeLayout) InflatedView.findViewById(R.id.slidedowndialogfragmentRelativeLayout_ProgressText);
						TextView_ProgressText = (TextView) InflatedView.findViewById(R.id.slidedowndialogfragmentTextView_Progressbar);
						ProgressBar_Progress.setProgress(dialogProgressBlink ? 100 : 0);
						ProgressBar_Progress.setAlpha(dialogProgressBlink ? (float) 1 : (float) 0.2);
						TextView_ProgressText.setText("0%");
						RelativeLayout_Progress.setVisibility(dialogShowProgressBar ? View.VISIBLE : View.GONE);
						RelativeLayout_ProgressText.setVisibility(dialogProgressShowText ? View.VISIBLE : View.INVISIBLE);
						
						LinearLayout_Buttons = (LinearLayout) InflatedView.findViewById(R.id.slidedowndialogfragmentLinearLayout_DialogButtons);
						
						LinearLayout_DialogNegativeButton = (LinearLayout) InflatedView.findViewById(R.id.slidedowndialogfragmentLinearLayout_DialogButtonNegative);
						TextView_DialogNegativeButtonText = (TextView) InflatedView.findViewById(R.id.slidedowndialogfragmentTextView_DialogButtonNegativeText);
						LinearLayout_DialogNegativeButton.setVisibility(View.GONE);

						LinearLayout_DialogNeutralButton = (LinearLayout) InflatedView.findViewById(R.id.slidedowndialogfragmentLinearLayout_DialogButtonNeutral);
						TextView_DialogNeutralButtonText = (TextView) InflatedView.findViewById(R.id.slidedowndialogfragmentTextView_DialogButtonNeutralText);
						LinearLayout_DialogNeutralButton.setVisibility(View.GONE);

						LinearLayout_DialogPositiveButton = (LinearLayout) InflatedView.findViewById(R.id.slidedowndialogfragmentLinearLayout_DialogButtonPositive);
						TextView_DialogPositiveButtonText = (TextView) InflatedView.findViewById(R.id.slidedowndialogfragmentTextView_DialogButtonPositiveText);
						if(positiveButtonText == null || positiveButtonText.isEmpty()) LinearLayout_DialogPositiveButton.setVisibility(View.GONE);
						
						TextView_DialogTouchOutside = (TextView) InflatedView.findViewById(R.id.slidedowndialogfragmentTextView_DialogTouchOutside);
						TextView_DialogTouchOutside.setVisibility(View.GONE);


						TextView_DialogText.setText(dialogText);
						if(dialogTextSize>0) TextView_DialogText.setTextSize(dialogTextSize);
						TextView_DialogText.setVisibility(dialogText.isEmpty() ? View.GONE : View.VISIBLE);
						
						br = new BroadcastReceiver() {

								@Override
								public void onReceive(Context p1, Intent p2)
								{
										// TODO: Implement this method
										if(p2.getAction().equalsIgnoreCase(slideDownDialogFragment.dialogCloseCall)) {
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
						filter.addAction(slideDownDialogFragment.dialogCloseCall);
						filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
						
						if(mContext!=null) mContext.registerReceiver(br, filter);
						
						if(dialogListItems != null && dialogListItems.length > 0) {
								dialogListAdapter = new ArrayAdapter<String>(mContext,(dialogListMode==ListView.CHOICE_MODE_NONE ? android.R.layout.simple_list_item_1 : (dialogListMode==ListView.CHOICE_MODE_MULTIPLE ? android.R.layout.simple_list_item_multiple_choice : android.R.layout.simple_list_item_single_choice)),dialogListItems);
								ListView_DialogListView.setAdapter(dialogListAdapter);
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
						
						if(!dialogInputs.isEmpty()) {
								for(int i = 0; i < dialogInputs.size(); i++) {
										View InputView = inflater.inflate(R.layout.slidedowndialogfragment_input, null, false);
										TextView InputText = (TextView) InputView.findViewById(R.id.slidedowndialogfragmentTextView_DialogInputText);
										EditText Input = (EditText) InputView.findViewById(R.id.slidedowndialogfragmentEditText_DialogInput);
										InputText.setText(dialogInputDescText.get(i));
										if(dialogInputTextWatcher.get(i)!=null) Input.addTextChangedListener(dialogInputTextWatcher.get(i));
										Input.setText(dialogInputDefaultText.get(i));
										Input.setInputType(dialogInputMode.get(i));
										Input.setSingleLine(dialogInputSingleLine.get(i));
										dialogInputs.set(i,Input);
										LinearLayout_InputHolder.addView(InputView);
								}
						}
						
						/*if(dialogInput1descText!=null) {
								TextView_DialogInput1Text.setText(dialogInput1descText);
								if(dialogInput1textWatcher!=null) EditText_DialogInput1.addTextChangedListener(dialogInput1textWatcher);
								EditText_DialogInput1.setText(dialogInput1defaultText);
								EditText_DialogInput1.setInputType(InputType.TYPE_CLASS_TEXT | dialogInput1mode);
								EditText_DialogInput1.setSingleLine(dialogInput1singleline);
								LinearLayout_DialogInput1.setVisibility(View.VISIBLE);
						}
						if(dialogInput2descText!=null) {
								TextView_DialogInput2Text.setText(dialogInput2descText);
								if(dialogInput2textWatcher!=null) EditText_DialogInput2.addTextChangedListener(dialogInput2textWatcher);
								EditText_DialogInput2.setText(dialogInput2defaultText);
								EditText_DialogInput2.setInputType(InputType.TYPE_CLASS_TEXT | dialogInput2mode);
								EditText_DialogInput1.setSingleLine(dialogInput1singleline);
								LinearLayout_DialogInput2.setVisibility(View.VISIBLE);
						}*/
						if(dialogInputAssistInfoString!= null) {
								TextView_InputAssistInfo.setText(dialogInputAssistInfoString);
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
						
						if(dialogCheckBoxtext != null && !dialogCheckBoxtext.isEmpty()) {
								LinearLayout_DialogCheckBox.setVisibility(View.VISIBLE);
								CheckBox_DialogCheckBox.setText(dialogCheckBoxtext);
						}
						
						if(dialogProgressBlink) {
								RelativeLayout_ProgressText.setVisibility(View.INVISIBLE);
								ProgressBar_Progress.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.progress_blink));
						}
						
						if(negativeButtonText != null && !negativeButtonText.isEmpty()) {
								TextView_DialogNegativeButtonText.setText(negativeButtonText);
								LinearLayout_DialogNegativeButton.setOnClickListener(new OnClickListener() {

												@Override
												public void onClick(View p1)
												{
														// TODO: Implement this method
														mInterface.onNegativeClick();
														if(dialogCloseOnButtonClick) closeDialog();
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
														if(dialogCloseOnButtonClick) closeDialog();
												}
										});
								LinearLayout_DialogNeutralButton.setVisibility(View.VISIBLE);
						}
						if(positiveButtonText != null && !positiveButtonText.isEmpty()) {
						TextView_DialogPositiveButtonText.setText(positiveButtonText);
						LinearLayout_DialogPositiveButton.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View p1)
										{
												// TODO: Implement this method
												Bundle resultBundle = new Bundle();
												//ArrayList<String> resultData = new ArrayList<String>();
												if(dialogListItems!=null) {
														if(dialogListMode != ListView.CHOICE_MODE_NONE) {
																if(ListView_DialogListView.getCheckedItemCount() < 1) {
																		return;
																} else {
														if(dialogListLimitMin && dialogListLimit>0 && ListView_DialogListView.getCheckedItemCount() < dialogListLimit) {
																return;
														}
														if(dialogListMode==ListView.CHOICE_MODE_SINGLE) {
																resultBundle.putString(RESULT_LIST,dialogListItems[ListView_DialogListView.getCheckedItemPosition()]);
																resultBundle.putInt(RESULT_LIST,ListView_DialogListView.getCheckedItemPosition());
																//resultData.add(dialogListItems[ListView_DialogListView.getCheckedItemPosition()]);
														} else if(dialogListMode==ListView.CHOICE_MODE_MULTIPLE) {
																String string = "";
																int checked = 0;
																for(int i = 0;i<dialogListItems.length;i++) {
																		if(ListView_DialogListView.isItemChecked(i)) {
																				checked++;
																				string = string + i + ((checked>=ListView_DialogListView.getCheckedItemCount()) ? "" : ",");
																		}
																}
																resultBundle.putString(RESULT_LIST,string);
																//resultData.add(string);
														}
														}
														}
												}
												if(!dialogInputs.isEmpty()) {
														for(int i = 0; i < dialogInputs.size(); i++) {
																if(!dialogInputAllowEmpty.get(i) && dialogInputs.get(i).getText().toString().isEmpty()) {
																		return;
																}
																resultBundle.putString(RESULT_INPUT+i,dialogInputs.get(i).getText().toString().trim());
																//resultData.add(dialogInputs.get(i).getText().toString());
														}
												}
												/*
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
												*/
												if(dialogColorPickerdefaultValue!=null) {
														resultBundle.putString(RESULT_COLORPICKER,String.format((dialogColorPickershowOpacityBar ? "#%08X" : "#%06X"), ((dialogColorPickershowOpacityBar ? 0xFFFFFFFF : 0xFFFFFF) & ColorPicker_DialogColorPicker.getColor())));
														//resultData.add(String.format((dialogColorPickershowOpacityBar ? "#%08X" : "#%06X"), ((dialogColorPickershowOpacityBar ? 0xFFFFFFFF : 0xFFFFFF) & ColorPicker_DialogColorPicker.getColor())));
												}
												if(dialogCheckBoxtext != null) {
														resultBundle.putBoolean(RESULT_CHECKBOX,CheckBox_DialogCheckBox.isChecked());
														//resultData.add(CheckBox_DialogCheckBox.isChecked() ? "true" : "false");
												}
												mInterface.onPositiveClick(resultBundle);
												if(dialogCloseOnButtonClick) closeDialog();
										}
								});
						}
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
						Animation anim = AnimationUtils.loadAnimation(mContext,R.anim.slidedowndialogfragment_in);
						//anim.setDuration(800);
						LinearLayout_DialogRoot.startAnimation(anim);

						if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.LOLLIPOP) {
								//mContext.getWindow().setNavigationBarColor(Color.parseColor(String.format("#%08X", (0xCC000000 & mContext.getResources().getColor(R.color.window_background_dark)))));
						}
						
						return InflatedView;
				}
		
		public String cancelDialog() {
				if(!hideActive) {
				if(negativeButtonText!=null) {
						mInterface.onNegativeClick();
				} else if (positiveButtonText!=null) {
						mInterface.onPositiveClick(null);
				}
				closeDialog();
				return "closing";
				} else if (hideActive) {
						return "hideactive";
				}
				return null;
		}
				
		public void closeDialog() {
				if(!hideActive) {
						hideActive = true;
						mFragment.getActivity().runOnUiThread(new Runnable() {

										@Override
										public void run()
										{
												// TODO: Implement this method
				mContext.unregisterReceiver(br);
				InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE); 
				IBinder windowToken = null;
				if(!dialogInputs.isEmpty()) {
						for(int i = 0; i < dialogInputs.size(); i++) {
								if(dialogInputs.get(i).isFocused()) {
										windowToken = dialogInputs.get(i).getWindowToken();
										break;
								}
						}
				}
				/*
				if(EditText_DialogInput1.isFocused()) {
						windowToken = EditText_DialogInput1.getWindowToken();
				} else if (EditText_DialogInput2.isFocused()) {
						windowToken = EditText_DialogInput2.getWindowToken();
				} else if (EditText_DialogHexInput.isFocused()) {
						windowToken = EditText_DialogHexInput.getWindowToken();
				}*/
						if(windowToken!=null) inputManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);
						TextView_DialogBg.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_out));
				TextView_DialogBg.setVisibility(View.GONE);
				TextView_DialogTouchOutside.setVisibility(View.GONE);
				Animation hideAnim = AnimationUtils.loadAnimation(mContext,R.anim.slidedowndialogfragment_out);
				/*hideAnim.setAnimationListener(new Animation.AnimationListener() {

								@Override
								public void onAnimationEnd(Animation p1)
								{
										// TODO: Implement this method
										if(slideDownDialogFragment.dialogs.get(slideDownDialogFragment.dialogs.size()-1).getShowsDialog()) {
												slideDownDialogFragment.dialogs.get(slideDownDialogFragment.dialogs.size()-1).dismiss();
										} else {
												mFragmentmanager.beginTransaction().remove(mFragment).commit();
										}
										slideDownDialogFragment.dialogs.remove(slideDownDialogFragment.dialogs.size()-1);
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
						});*/
						//hideAnim.setDuration(800);
				LinearLayout_DialogRoot.startAnimation(hideAnim);
						handler.postDelayed(new Runnable() {

										@Override
										public void run()
										{
												// TODO: Implement this method
																		try {
																				if(slideDownDialogFragment.dialogs.get(slideDownDialogFragment.dialogs.size()-1).getShowsDialog()) {
																						slideDownDialogFragment.dialogs.get(slideDownDialogFragment.dialogs.size()-1).dismiss();
																				} else {
																						mFragmentmanager.beginTransaction().remove(mFragment).commitAllowingStateLoss();
																				}
																				slideDownDialogFragment.dialogs.remove(slideDownDialogFragment.dialogs.size()-1);
																		} catch (Throwable t) {
																		}
										}
								}, hideAnim.getDuration());
				LinearLayout_DialogRoot.setVisibility(View.GONE);
				}
				});
				}
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState)
		{
				super.onActivityCreated(savedInstanceState);
				if(getShowsDialog()) {
						//getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
						//getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
						getDialog().getWindow().getAttributes().windowAnimations = R.style.PopUpDialogAnimation_Window;
				}
		}
		
}

package de.NeonSoft.neopowermenu.helpers;

import android.app.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;
import android.view.animation.*;

public class actionBar
{
		
		private Activity mContext;
		private LayoutInflater mInflater;
		
		private TextView TextView_Title;
		private String TextView_TitleText = "null";
		private TextView TextView_SubTitle;
		private String TextView_SubTitleText = "null";
		
		private LinearLayout LinearLayout_Button;
		private TextView TextView_ButtonText;
		private String TextView_ButtonText_Text = "null";
		private ImageView ImageView_ButtonIcon;
		private int ImageView_ButtonIcon_Icon = 0;
		
		public actionBar(Activity context) {
				this.mContext = context;
				this.mInflater = context.getLayoutInflater();
		}
		
		public void addActionBar(LinearLayout root) {
				View InflatedView = mInflater.inflate(R.layout.actionbar, root, false);
				
				TextView_Title = (TextView) InflatedView.findViewById(R.id.actionBar_Title);
				TextView_SubTitle = (TextView) InflatedView.findViewById(R.id.actionBar_SubTitle);
				
				LinearLayout_Button = (LinearLayout) InflatedView.findViewById(R.id.actionBar_Button);
				TextView_ButtonText = (TextView) InflatedView.findViewById(R.id.actionBar_ButtonText);
				ImageView_ButtonIcon = (ImageView) InflatedView.findViewById(R.id.actionBar_ButtonIcon);
				
				TextView_Title.setText(mContext.getString(R.string.app_name));
				TextView_Title.setSelected(true);
				TextView_SubTitle.setVisibility(View.GONE);
				TextView_SubTitle.setSelected(true);
				
				LinearLayout_Button.setVisibility(View.GONE);
				
				root.addView(InflatedView);
				
		}

		public void setActionBarTitle(String title) {
				if(!TextView_TitleText.equalsIgnoreCase(title)) {
				if(TextView_Title.getVisibility() == View.VISIBLE) {
						TextView_Title.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_out));
						TextView_Title.setVisibility(View.INVISIBLE);
				}
				TextView_Title.setText(title);
				TextView_TitleText = title;
				TextView_Title.setVisibility(View.VISIBLE);
				TextView_Title.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_in));
				}
		}

		public void setActionBarSubTitle(String subtitle) {
				if(!TextView_SubTitleText.equalsIgnoreCase(subtitle)) {
						if(TextView_SubTitle.getVisibility() == View.VISIBLE) {
								TextView_SubTitle.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_out));
								TextView_SubTitle.setVisibility(View.INVISIBLE);
						}
				TextView_SubTitle.setText(subtitle);
				TextView_SubTitleText = subtitle;
						TextView_SubTitle.setVisibility(View.VISIBLE);
						TextView_SubTitle.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_in));
				}
		}
		
		public void hideActionBarSubTitle() {
				if(TextView_SubTitle.getVisibility() == View.VISIBLE) {
						TextView_SubTitle.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_out));
						TextView_SubTitle.setVisibility(View.GONE);
						TextView_SubTitleText = "null";
				}
		}
		
		public void setActionBarButton(String sText,int iImgResId,OnClickListener onclkl) {
				if(LinearLayout_Button.getVisibility()==View.GONE || (!sText.equalsIgnoreCase(TextView_ButtonText_Text) && ImageView_ButtonIcon_Icon!=iImgResId)) {
						if(LinearLayout_Button.getVisibility()==View.VISIBLE) {
								LinearLayout_Button.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_out));
								LinearLayout_Button.setVisibility(View.INVISIBLE);
						}
						TextView_ButtonText.setText(sText);
						TextView_ButtonText_Text = sText;
						//Toast.makeText(context,"Showing ActionBarButton:\nIcon: "+iImgResId+"\nText: "+sText,Toast.LENGTH_SHORT).show();
						if (iImgResId>-1) {
								ImageView_ButtonIcon_Icon = iImgResId;
								ImageView_ButtonIcon.setImageResource(iImgResId);
								ImageView_ButtonIcon.setVisibility(View.VISIBLE);
						} else {
								ImageView_ButtonIcon_Icon = -1;
								ImageView_ButtonIcon.setVisibility(View.GONE);
						}
						LinearLayout_Button.setOnClickListener(onclkl);
						if(LinearLayout_Button.getVisibility()==View.INVISIBLE) {
								LinearLayout_Button.setVisibility(View.VISIBLE);
								LinearLayout_Button.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_in));
						} else {
								LinearLayout_Button.setVisibility(View.VISIBLE);
								LinearLayout_Button.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.anim_fade_slide_in_right));
						}
				} else {
						//Toast.makeText(context,"ActionBarButton already set with this data.",Toast.LENGTH_SHORT).show();
				}
		}

		public void setActionBarButtonListener(OnClickListener listener) {
				LinearLayout_Button.setOnClickListener(listener);
		}

		public void setActionBarButtonText(String sText) {
				if(!sText.equalsIgnoreCase(TextView_ButtonText.getText().toString())) {
						if(LinearLayout_Button.getVisibility()==View.VISIBLE) {
								LinearLayout_Button.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_out));
								LinearLayout_Button.setVisibility(View.INVISIBLE);
						}
						TextView_ButtonText.setText(sText);
						TextView_ButtonText_Text = sText;
						if(LinearLayout_Button.getVisibility()==View.INVISIBLE) {
								LinearLayout_Button.setVisibility(View.VISIBLE);
								LinearLayout_Button.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_in));
						}
				}
		}

		public void setActionBarButtonIcon(int iImgResId) {
				if(iImgResId!=ImageView_ButtonIcon_Icon) {
						if(LinearLayout_Button.getVisibility()==View.VISIBLE && ImageView_ButtonIcon.getVisibility()==View.VISIBLE) {
								ImageView_ButtonIcon.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_out));
						}
						if(iImgResId>-1) {
								ImageView_ButtonIcon_Icon = iImgResId;
								ImageView_ButtonIcon.setImageResource(iImgResId);
								ImageView_ButtonIcon.setVisibility(View.VISIBLE);
								ImageView_ButtonIcon.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fade_in));
						} else {
								ImageView_ButtonIcon.setVisibility(View.GONE);
						}
				}
		}

		public void hideActionBarButton() {
				if(LinearLayout_Button.getVisibility()==View.VISIBLE) {
						LinearLayout_Button.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.anim_fade_slide_out_right));
						LinearLayout_Button.setVisibility(View.GONE);
						ImageView_ButtonIcon_Icon = 0;
						TextView_ButtonText_Text = "none";
				}
		}
		
}

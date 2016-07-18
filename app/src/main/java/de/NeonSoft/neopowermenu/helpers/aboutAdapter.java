package de.NeonSoft.neopowermenu.helpers;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;
import java.util.*;

public class aboutAdapter extends ArrayAdapter<String>
{
		private Activity mContext;
		
		private ArrayList<String> itemsTitles;
		private ArrayList<String> itemsTexts;
		
		public aboutAdapter(Activity context,ArrayList<String> itemTitles,ArrayList<String> itemTexts) {
				super(context,R.layout.aboutlistitem,itemTitles);
				this.mContext = context;
				this.itemsTitles = itemTitles;
				this.itemsTexts = itemTexts;
		}

		@Override
		public View getView(int p1, View p2, ViewGroup p3)
		{
				// TODO: Implement this method
				View InflatedView = mContext.getLayoutInflater().inflate(R.layout.aboutlistitem,p3,false);
				
				LinearLayout root = (LinearLayout) InflatedView.findViewById(R.id.aboutlistitemLinearLayout);
				TextView Title = (TextView) InflatedView.findViewById(R.id.aboutlistitemTextView1);
				TextView Text = (TextView) InflatedView.findViewById(R.id.aboutlistitemTextView2);
				
				Title.setText(this.itemsTitles.get(p1));
				Text.setText(this.itemsTexts.get(p1));
				
				if(this.itemsTexts.get(p1).contains("Your Device Id")) {
						Text.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View p1)
										{
												try {
												ClipboardManager cpm = new ClipboardManager(mContext, new Handler());
												cpm.setText(MainActivity.deviceUniqeId);
												if(cpm.getText().toString().equals(MainActivity.deviceUniqeId)) {
														Toast.makeText(mContext,MainActivity.deviceUniqeId+" copied to clipboard.",Toast.LENGTH_SHORT).show();
												} else {
														Toast.makeText(mContext,"Failed to put to clipboard...",Toast.LENGTH_SHORT).show();
												}
												}
												catch (Throwable t) {
														Log.e("NPM","Failed to put in clipboard: "+t.toString());
														Toast.makeText(mContext,"Failed to put to clipboard...",Toast.LENGTH_SHORT).show();
												}
										}
								});
						Text.setOnLongClickListener(new OnLongClickListener() {

										@Override
										public boolean onLongClick(View p1)
										{
												// TODO: Implement this method
												try {
														ClipboardManager cpm = new ClipboardManager(mContext, new Handler());
														cpm.setText(MainActivity.accountUniqeId);
														if(cpm.getText().toString().equals(MainActivity.accountUniqeId)) {
																Toast.makeText(mContext,MainActivity.accountUniqeId+" copied to clipboard.",Toast.LENGTH_SHORT).show();
														} else {
																Toast.makeText(mContext,"Failed to put to clipboard...",Toast.LENGTH_SHORT).show();
														}
												}
												catch (Throwable t) {
														Log.e("NPM","Failed to put in clipboard: "+t.toString());
														Toast.makeText(mContext,"Failed to put to clipboard...",Toast.LENGTH_SHORT).show();
												}
												return true;
										}
								});
				} else {
						root.setClickable(false);
						root.setEnabled(false);
				}
				
				return InflatedView;
		}
		
}

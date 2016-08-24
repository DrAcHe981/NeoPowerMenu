package de.NeonSoft.neopowermenu.helpers;

import android.app.*;
import android.view.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;
import android.text.*;

public class animationsCustomAdapter extends ArrayAdapter<String>
{
		
		Activity mContext;
		String mItem;
		String[] mPrefItems;
		
		public animationsCustomAdapter(Activity context, String item, String[] prefNames) {
				super(context, R.layout.animationscustom, prefNames);
				
				this.mItem = item;
				this.mContext = context;
				this.mPrefItems = prefNames;
		}

		@Override
		public View getView(int position, View p2, ViewGroup p3)
		{
				// TODO: Implement this method
				View InflatedView = mContext.getLayoutInflater().inflate(R.layout.animationscustom, p3, false);
				
				LinearLayout pref1 = (LinearLayout) InflatedView.findViewById(R.id.animationscustomLinearLayout_Pref1);
				TextView pref1Text = (TextView) InflatedView.findViewById(R.id.animationscustomTextView_Pref1);
				final EditText pref1Edit = (EditText) InflatedView.findViewById(R.id.animationscustomEditText_Pref1);
				Spinner pref1Spinn = (Spinner) InflatedView.findViewById(R.id.animationscustomSpinner_Pref1);
				
				LinearLayout pref2 = (LinearLayout) InflatedView.findViewById(R.id.animationscustomLinearLayout_Pref2);
				pref2.setVisibility(View.GONE);
				TextView pref2Text = (TextView) InflatedView.findViewById(R.id.animationscustomTextView_Pref2);
				final EditText pref2Edit = (EditText) InflatedView.findViewById(R.id.animationscustomEditText_Pref2);
				Spinner pref2Spinn = (Spinner) InflatedView.findViewById(R.id.animationscustomSpinner_Pref2);
				//pref2Spinn.setAdapter(new SpinnerAdapter(mContext, ));
				
				//pref1Text.setText((mPrefItems[position].contains("Pivot") || mPrefItems[position].contains("Duration") ? mPrefItems[position] : mContext.getString(R.string.animations_Custom).split("\\|")[0]).replace("[PREFNAME]",mPrefItems[position]));
				/*pref1Edit.addTextChangedListener(new TextWatcher() {

								CharSequence beforeChange;
						
								@Override
								public void afterTextChanged(Editable p1)
								{
										// TODO: Implement this method
								}

								@Override
								public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
								{
										// TODO: Implement this method
										beforeChange = p1;
								}

								@Override
								public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
								{
										// TODO: Implement this method
										if(!p1.toString().matches("[0-9][%p?]")) {
												pref1Edit.setText(beforeChange);
										}
								}
						});*/
						pref1Edit.setText(MainActivity.animationPrefs.getString(mItem+"_from"+mPrefItems[position],"0")+
								((mPrefItems[position].contains("Alpha") || mPrefItems[position].contains("Duration")) ? "" : ((mPrefItems[position].contains("Pivot") || mPrefItems[position].contains("Rotation")) ? "%" : "%p")));
						
				if(!mPrefItems[position].contains("Pivot") && !mPrefItems[position].contains("Duration")) {
						pref2.setVisibility(View.VISIBLE);
						//pref2Text.setText(mContext.getString(R.string.animations_Custom).split("\\|")[1].replace("[PREFNAME]",mPrefItems[position]));
						/*pref2Edit.addTextChangedListener(new TextWatcher() {

										CharSequence beforeChange;

										@Override
										public void afterTextChanged(Editable p1)
										{
												// TODO: Implement this method
										}

										@Override
										public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
										{
												// TODO: Implement this method
												beforeChange = p1;
										}

										@Override
										public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
										{
												// TODO: Implement this method
												if(!p1.toString().matches("[0-9][%p?]")) {
														pref2Edit.setText(beforeChange);
												}
										}
						});*/
				pref2Edit.setText(MainActivity.animationPrefs.getString(mItem+"_to"+mPrefItems[position],"100")+
						((mPrefItems[position].contains("Alpha") || mPrefItems[position].contains("Duration")) ? "" : ((mPrefItems[position].contains("Pivot") || mPrefItems[position].contains("Rotation")) ? "%" : "%p")));
				}
				
				return InflatedView;
		}
		
		public void saveData() {
				
		}
		
}

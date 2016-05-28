package de.NeonSoft.neopowermenu.helpers;

import android.app.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;
import java.util.*;
import android.view.*;

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
				
				root.setClickable(false);
				root.setEnabled(false);
				Title.setText(this.itemsTitles.get(p1));
				Text.setText(this.itemsTexts.get(p1));
				
				return InflatedView;
		}
		
}

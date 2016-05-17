package de.NeonSoft.neopowermenu.helpers;
import android.app.*;
import android.view.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;
import java.util.*;
import java.io.*;
import android.widget.CompoundButton.*;

public class visibilityOrder_ListAdapter extends ArrayAdapter<String>
{

		private final Activity context;
		private final ArrayList<String> itemsTitle;
		private final ArrayList<String> itemsEnabled;
		
		public visibilityOrder_ListAdapter(Activity context,
															 ArrayList<String> itemsTitle,
															 ArrayList<String> itemsEnabled){
				super(context, R.layout.list_item_handle_left, itemsTitle);
				this.context = context;
				this.itemsTitle = itemsTitle;
				this.itemsEnabled = itemsEnabled;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
				// TODO: Implement this method
				LayoutInflater inflater = context.getLayoutInflater();
				View rowView;
				final String prefname = this.itemsTitle.get(position)+"Enabled";
				rowView = inflater.inflate(R.layout.list_item_handle_left, null, true);
				
				TextView ItemTitle = (TextView) rowView.findViewById(R.id.title);
				TextView ItemDesc = (TextView) rowView.findViewById(R.id.text);
				LinearLayout ItemCheckBoxHolder = (LinearLayout) rowView.findViewById(R.id.listitemhandleleftLinearLayout_HideDesc);
				final CheckBox ItemCheckBox = (CheckBox) rowView.findViewById(R.id.hideDescCheckBox);
				ItemCheckBox.setClickable(false);
				ItemCheckBox.setFocusable(false);
				Switch ItemSwitch = (Switch) rowView.findViewById(R.id.Switch);

				if (this.itemsTitle.get(position).equalsIgnoreCase("SoftReboot") || this.itemsTitle.get(position).equalsIgnoreCase("Screenshot") || this.itemsTitle.get(position).equalsIgnoreCase("Flashlight")) {
						ItemCheckBox.setChecked(MainActivity.preferences.getBoolean(this.itemsTitle.get(position)+"_HideDesc",false));
						ItemCheckBoxHolder.setVisibility(View.VISIBLE);
						ItemCheckBoxHolder.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View p1)
										{
												ItemCheckBox.setChecked(!ItemCheckBox.isChecked());
												MainActivity.preferences.edit().putBoolean(itemsTitle.get(position)+"_HideDesc",ItemCheckBox.isChecked()).commit();
										}
								});
				}
				
				try {
						String titleStr = context.getResources().getString(context.getResources().getIdentifier("powerMenuMain_"+this.itemsTitle.get(position),"string",MainActivity.class.getPackage().getName()));
						ItemTitle.setText(titleStr);
				} catch (Throwable t) {
						ItemTitle.setText("Failed to get String resource for powerMenuMain_"+this.itemsTitle.get(position));
				}
				try {
						String descStr = context.getResources().getString(context.getResources().getIdentifier("visibilityOrderDesc_"+this.itemsTitle.get(position),"string",MainActivity.class.getPackage().getName()));
						ItemDesc.setText(descStr);
				} catch (Throwable t) {
						ItemDesc.setText("Failed to get String resource for visibilityOrderDesc_"+this.itemsTitle.get(position));
				}
				//ItemTitle.setText(this.itemsTitle.get(position));
				//ItemDesc.setText(this.itemsDesc.get(position));
				if (this.itemsEnabled.get(position).equalsIgnoreCase("false")) {
						ItemSwitch.setEnabled(false);
						ItemSwitch.setChecked(false);
						MainActivity.preferences.edit().putBoolean(prefname,false).commit();
				} else {
						ItemSwitch.setEnabled(true);
						ItemSwitch.setChecked(MainActivity.preferences.getBoolean(prefname,false));
				}
				ItemSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

								@Override
								public void onCheckedChanged(CompoundButton p1, boolean p2)
								{
										// TODO: Implement this method
										MainActivity.preferences.edit().putBoolean(prefname,p2).commit();
								}
						});
					
				return rowView;
		}
		
		public String[] getItemAt(int position) {
				String[] string = new String[2];
				
				string[0] = this.itemsTitle.get(position);
				string[1] = this.itemsEnabled.get(position);
				//string[4] = this.itemsPrefPositions.get(position);
				
				return string;
		}
		
		public void insertAt(String[] string,int to) {
				itemsTitle.add(to,string[0]);
				itemsEnabled.add(to,string[1]);
				//itemsEnabled.add(to,string[1]);
				//itemsPrefPositions.add(to,string[4]);
		}
		
		public void removeAt(int position) {
				itemsTitle.remove(position);
				itemsEnabled.remove(position);
				//itemsEnabled.remove(position);
				//itemsPrefPositions.remove(position);
		}
		
		public void OutputSorting() {
				try
				{
						for(int i=0;i<this.itemsTitle.size();i++) {
								MainActivity.preferences.edit().putInt(this.itemsTitle.get(i)+"Position",i).commit();
						}
				}
				catch (Throwable e)
				{}
		}
		
}

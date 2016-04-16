package de.NeonSoft.neopowermenu.helpers;
import android.app.*;
import android.view.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;
import java.util.*;
import java.io.*;

public class visibilityOrder_ListAdapter extends ArrayAdapter<String>
{

		private final Activity context;
		private final ArrayList<String> itemsTitle;
		private final ArrayList<String> itemsPrefPositions;
		private final ArrayList<String> itemsDesc;
		private final ArrayList<String> itemsSwitchPrefName;
		private final ArrayList<String> itemsEnabled;
		
		public visibilityOrder_ListAdapter(Activity context,
															 ArrayList<String> itemsTitle,
															 ArrayList<String> itemsPrefPositions,
															 ArrayList<String> itemsDesc,
															 ArrayList<String> itemsSwitchPrefName,
															 ArrayList<String> itemsEnabled){
				super(context, R.layout.list_item_handle_left, itemsTitle);
				this.context = context;
				this.itemsTitle = itemsTitle;
				this.itemsPrefPositions = itemsPrefPositions;
				this.itemsDesc = itemsDesc;
				this.itemsSwitchPrefName = itemsSwitchPrefName;
				this.itemsEnabled = itemsEnabled;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
				// TODO: Implement this method
				LayoutInflater inflater = context.getLayoutInflater();
				View rowView;
				final String prefname = this.itemsSwitchPrefName.get(position);
				rowView = inflater.inflate(R.layout.list_item_handle_left, null, true);
				
				TextView ItemTitle = (TextView) rowView.findViewById(R.id.title);
				TextView ItemDesc = (TextView) rowView.findViewById(R.id.text);
				Switch ItemSwitch = (Switch) rowView.findViewById(R.id.Switch);
				
				ItemTitle.setText(this.itemsTitle.get(position));
				ItemDesc.setText(this.itemsDesc.get(position));
				if (this.itemsEnabled.get(position).equalsIgnoreCase("false")) {
						ItemSwitch.setEnabled(false);
						ItemSwitch.setChecked(false);
						MainActivity.preferences.edit().putBoolean(itemsSwitchPrefName.get(position),false).commit();
				} else {
						ItemSwitch.setChecked(MainActivity.preferences.getBoolean(itemsSwitchPrefName.get(position),true));
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
				String[] string = new String[5];
				
				string[0] = this.itemsTitle.get(position);
				string[1] = this.itemsDesc.get(position);
				string[2] = this.itemsSwitchPrefName.get(position);
				string[3] = this.itemsEnabled.get(position);
				string[4] = this.itemsPrefPositions.get(position);
				
				return string;
		}
		
		public void insertAt(String[] string,int to) {
				itemsTitle.add(to,string[0]);
				itemsDesc.add(to,string[1]);
				itemsSwitchPrefName.add(to,string[2]);
				itemsEnabled.add(to,string[3]);
				itemsPrefPositions.add(to,string[4]);
		}
		
		public void removeAt(int position) {
				itemsTitle.remove(position);
				itemsDesc.remove(position);
				itemsSwitchPrefName.remove(position);
				itemsEnabled.remove(position);
				itemsPrefPositions.remove(position);
		}
		
		public void OutputSorting() {
				try
				{
						for(int i=0;i<this.itemsPrefPositions.size();i++) {
								MainActivity.preferences.edit().putInt(this.itemsPrefPositions.get(i),i).commit();
						}
				}
				catch (Throwable e)
				{}
		}
		
}

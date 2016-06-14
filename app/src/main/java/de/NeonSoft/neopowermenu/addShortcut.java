package de.NeonSoft.neopowermenu;
import android.app.*;
import android.content.*;
import android.os.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.xposed.*;

public class addShortcut extends Activity
{

		@Override
		protected void onCreate(Bundle p1)
		{
				// TODO: Implement this method
				super.onCreate(p1);
				addShortcut();
				this.finish();
		}


		private void addShortcut()
		{

				//Adding shortcut for MainActivity
				//on Home screen

				Intent shortcutIntent = new Intent(getApplicationContext(), XposedMainActivity.class);
				shortcutIntent.setAction(Intent.ACTION_MAIN);
				Intent addIntent = new Intent();
				addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
				addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
				addIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				addIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
													 Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher));
				addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

				getApplicationContext().sendBroadcast(addIntent);

				//Toast.makeText(getApplicationContext(), "Added Home Screen Shortcurt", Toast.LENGTH_SHORT).show();
				this.finish();
		}


}

package de.NeonSoft.neopowermenu;
import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.xposed.*;

public class addShortcut extends Activity
{

		private String TAG = "NPM:shortcut";
		
		@Override
		protected void onCreate(Bundle p1)
		{
				// TODO: Implement this method
				super.onCreate(p1);
				Log.i(TAG,"Adding shortcut...");
			createShortcut(new CreateShortcutListener() {

					@Override
					public void onShortcutCreated(Intent intent)
					{
							// TODO: Implement this method
							Log.i(TAG,"Shortcut added.");
							if(intent != null) {
								addShortcut.this.setResult(RESULT_OK,intent);
							} else {
								addShortcut.this.setResult(RESULT_OK);
						}
						addShortcut.this.finish();
					}
					
					@Override
					public void onShortcutFailed(String reason) {
							Log.e(TAG,"Failed to create Shortcut: "+reason);
							Toast.makeText(getApplicationContext(),"Failed to create Shortcut, more info in the logs under the tag 'NPM:shortcut'.",Toast.LENGTH_LONG).show();
							addShortcut.this.setResult(RESULT_CANCELED);
							addShortcut.this.finish();
					}
					
				});
		}

    public interface CreateShortcutListener {
        public void onShortcutCreated(Intent intent);
				public void onShortcutFailed(String reason);
    }
	
    protected void createShortcut(CreateShortcutListener listener) {
				try {
        Intent launchIntent = new Intent(getApplicationContext(), XposedMainActivity.class);
        //launchIntent.setAction(ShortcutActivity.ACTION_LAUNCH_ACTION);
        //launchIntent.putExtra(ShortcutActivity.EXTRA_ACTION, getAction());
        //launchIntent.putExtra(ShortcutActivity.EXTRA_ACTION_TYPE, getActionType());
		launchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		launchIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launchIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "NeoPowerMenu");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(),R.mipmap.ic_launcher));

        // descendants can override this to supply additional data
            listener.onShortcutCreated(intent);
				} catch (Throwable t) {
						listener.onShortcutFailed(t.toString());
				}
    }

}

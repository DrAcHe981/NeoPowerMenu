package de.NeonSoft.neopowermenu.helpers;

import android.content.*;
import android.graphics.drawable.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import de.NeonSoft.neopowermenu.*;

public class helper
{


    @SuppressWarnings("deprecation")
    public static void setBackground(View view, Drawable d) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(d);
        } else {
            view.setBackgroundDrawable(d);
        }
    }

    public static void postInvalidateOnAnimation(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.postInvalidateOnAnimation();
        } else {
            view.invalidate();
        }
    }
		
    public static void setThreadPrio(int prio) {
        android.os.Process.setThreadPriority(prio);
    }

		public static String ModuleState() {
				String active = "no";
				return active;
		}
		
		public static String getTimeString(long InputMilliSeconds,boolean withMs)
		{
				long OutputMiliSeconds = 0;
				long OutputSeconds = 0;
				long OutputMinutes = 0;
				long OutputHours = 0;
				for (int i=0;i < InputMilliSeconds;i += 1000)
				{
						if (OutputSeconds >= 59)
						{
								OutputSeconds = 0;
								OutputMinutes++;
						}
						else if (OutputMinutes >= 59)
						{
								OutputMinutes = 0;
								OutputHours++;
						}
						else
						{
								OutputSeconds++;
						}
				}
				String duration_string = "00:00";
				if (withMs) {
						OutputHours = OutputMinutes;
						OutputMinutes = OutputSeconds;
						OutputSeconds = Long.parseLong((""+InputMilliSeconds));
				}
				if (OutputHours > 0)
				{
						duration_string =  String.format("%02d:%02d:%02d", OutputHours, OutputMinutes, OutputSeconds);
				}
				else
				{
						duration_string =  String.format("%02d:%02d", OutputMinutes, OutputSeconds);
				}
				return duration_string;
		}
}

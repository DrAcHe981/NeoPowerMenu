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

		public static int ModuleState() {
				int active = -1;
				return active;
		}
		
		public static String getTimeString(long InputMilliSeconds,boolean withTxt)
		{
				//long OutputMiliSeconds = 0;
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
				/*if (withMs) {
						OutputHours = OutputMinutes;
						OutputMinutes = OutputSeconds;
						OutputSeconds = Long.parseLong((""+InputMilliSeconds));
				}*/
				if (OutputHours > 0)
				{
						duration_string =  String.format("%02d:%02d:%02d", OutputHours, OutputMinutes, OutputSeconds);
				}
				else if (OutputMinutes > 0)
				{
						duration_string =  String.format("%02d:%02d", OutputMinutes, OutputSeconds);
				} else if (OutputSeconds > 0) {
						duration_string =  String.format("%02d", OutputSeconds);
				} else {
						duration_string =  String.format("%03d", InputMilliSeconds);
				}
				if (withTxt) {
						if (OutputHours > 0)
						{
								duration_string +=  "(H:M:S)";
						}
						else if (OutputMinutes > 0)
						{
								duration_string +=  "(M:S)";
						} else if (OutputSeconds > 0) {
								duration_string +=  "(S)";
						} else {
								duration_string += "(MS)";
						}
				}
				return duration_string;
		}
}

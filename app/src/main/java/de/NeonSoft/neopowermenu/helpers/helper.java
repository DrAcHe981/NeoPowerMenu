package de.NeonSoft.neopowermenu.helpers;

import android.app.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.text.*;
import android.util.*;
import android.view.*;
import java.lang.reflect.*;
import java.security.*;
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
		
		public static String activeParts() {
				String activeParts = MainActivity.preferences.getString("activeParts","none");
				return activeParts;
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

		public static String getSizeString(long bytes, boolean si)
		{
				if (bytes <= 0) return "0b";
				int unit = si ? 1024 : 1000;
				if (bytes < unit) return bytes + " B";
				int exp = (int) (Math.log(bytes) / Math.log(unit));
				String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
				return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
		}
		
		public static String md5Crypto(String input) {
				try {
						MessageDigest digest = MessageDigest.getInstance("MD5");
						digest.update(input.getBytes());
						byte messageDigest[] = digest.digest();
						
						StringBuffer hexString = new StringBuffer();
						for(int i=0;i< messageDigest.length;i++) {
								String h = Integer.toHexString(0xFF & messageDigest[i]);
								while (h.length() < 2) {
										h = "0" + h;
								}
								hexString.append(h);
						}
						return hexString.toString();
				} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
				}
				return "null";
		}
		
		public static boolean isValidEmail(String input) {
				return !TextUtils.isEmpty(input) && android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches();
		}

}

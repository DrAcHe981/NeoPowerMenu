package de.NeonSoft.neopowermenu.helpers;

import android.graphics.drawable.*;
import android.os.*;
import android.view.*;

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
}

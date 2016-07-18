package de.NeonSoft.neopowermenu;
import android.app.*;
import org.acra.*;
import org.acra.annotation.*;
import cat.ereza.customactivityoncrash.*;

@ReportsCrashes(
		formUri = "http://www.Neon-Soft.de/inc/acra/acra.php",
		formUriBasicAuthLogin = "acra",
		formUriBasicAuthPassword = "acraerrormailer",
		reportType = org.acra.sender.HttpSender.Type.FORM,
		mode = ReportingInteractionMode.SILENT,
		sendReportsAtShutdown = true,
		socketTimeout = 1000 * 20
)

public class neopowermenu extends Application
{

		@Override
		public void onCreate()
		{
				// TODO: Implement this method
				super.onCreate();
		}
		
}

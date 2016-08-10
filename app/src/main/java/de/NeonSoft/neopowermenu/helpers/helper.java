package de.NeonSoft.neopowermenu.helpers;

import android.content.*;
import android.content.res.*;
import android.graphics.drawable.*;
import android.os.*;
import android.text.*;
import android.util.*;
import android.view.*;
import de.NeonSoft.neopowermenu.*;
import java.io.*;
import java.security.*;
import net.lingala.zip4j.core.*;
import net.lingala.zip4j.model.*;
import net.lingala.zip4j.util.*;

public class helper
{

		private static boolean zipLogging = true;
		
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
						duration_string =  String.format("%2d"+(withTxt ? "h " : ":")+"%2d"+(withTxt ? "m " : ":")+"%2d"+(withTxt ? "s" : ""), OutputHours, OutputMinutes, OutputSeconds);
				}
				else if (OutputMinutes > 0)
				{
						duration_string =  String.format("%2d"+(withTxt ? "m " : ":")+"%2d"+(withTxt ? "s" : ""), OutputMinutes, OutputSeconds);
				} else if (OutputSeconds > 0) {
						duration_string =  String.format("%2d"+(withTxt ? "s" : ""), OutputSeconds);
				} else {
						duration_string =  String.format("%3d"+(withTxt ? "ms" : ""), InputMilliSeconds);
				}
				/*if (withTxt) {
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
				}*/
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

		public static int getStatusBarHeight(Context context) {
				int result = 0;
				int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
				if (resourceId > 0) {
						result = context.getResources().getDimensionPixelSize(resourceId);
				}
				return result;
		}
		
		public static int getNavigationBarHeight(Context context) {
				int result = 0;
				int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
				if (resourceId > 0) {
						result = context.getResources().getDimensionPixelSize(resourceId);
				}
				return result;
		}
		
		/**
		 * This method converts dp unit to equivalent pixels, depending on device density. 
		 * 
		 * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
		 * @param context Context to get resources and device specific display metrics
		 * @return A float value to represent px equivalent to dp depending on device density
		 */
		public static float convertDpToPixel(float dp, Context context){
				if(context==null) return dp;
				Resources resources = context.getResources();
				DisplayMetrics metrics = resources.getDisplayMetrics();
				float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
				return px;
		}

		/**
		 * This method converts device specific pixels to density independent pixels.
		 * 
		 * @param px A value in px (pixels) unit. Which we need to convert into db
		 * @param context Context to get resources and device specific display metrics
		 * @return A float value to represent dp equivalent to px value
		 */
		public static float convertPixelsToDp(float px, Context context){
				if(context==null) return px;
				Resources resources = context.getResources();
				DisplayMetrics metrics = resources.getDisplayMetrics();
				float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
				return dp;
		}

		
		public static void zipLogging(boolean enabled) {
				zipLogging = enabled;
				//Log.d("NPM:zip","Logging "+(zipLogging ? "enabled" : "disabled")+"...");
		}
		
		public static String zipFile(String fileToZip, String zipFile, String password) {
				try {
						ZipFile zip = new ZipFile(zipFile);
						ZipParameters params = new ZipParameters();

						params.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
						params.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
						params.setIncludeRootFolder(false);
						
						if(password != null && !password.isEmpty()) {
								params.setPassword(password);
						}

						zip.addFile(new File(fileToZip),params);
				} catch (Throwable t) {
						if(zipLogging) Log.e("NPM:zipFile","Failed to zip: "+t.toString());
						return t.toString();
				}
				return null;
		}
		
		public static String zipAll(String toZipFolder, String zipFile, String password) {
				try {
						ZipFile zip = new ZipFile(zipFile);
						ZipParameters params = new ZipParameters();
						
						params.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
						params.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
						params.setIncludeRootFolder(false);
						
						if(password != null && !password.isEmpty()) {
								params.setPassword(password);
						}
						
						zip.addFolder(toZipFolder,params);
				} catch (Throwable t) {
						if(zipLogging) Log.e("NPM:zipAll","Failed to zip: "+t.toString());
						return t.toString();
				}
				return null;
		}
		
		public static String unzipFile(String zipFile, String outputFolder, String fileToUnzip, String password) {
				try {
						ZipFile zip = new ZipFile(zipFile);
						UnzipParameters params = new UnzipParameters();
						
						if(password != null && !password.isEmpty()) {
								zip.setPassword(password);
						}
						
						zip.extractFile(fileToUnzip, outputFolder, params, fileToUnzip);
				} catch (Throwable t) {
						if(zipLogging) Log.e("NPM:unzipFile","Failed to unzip: "+t.toString());
						return t.toString();
				}
				return null;
		}
		
		public static String unzipAll(String zipFile, String outputFolder, String password) {
				try {
						ZipFile zip = new ZipFile(zipFile);
						
						if(password != null && !password.isEmpty()) {
								zip.setPassword(password);
						}
						
						zip.extractAll(outputFolder);
				} catch (Throwable t) {
						if(zipLogging) Log.e("NPM:unzipAll","Failed to unzip: "+t.toString());
						return t.toString();
				}
				return null;
		}
		
		public static boolean isValidZip(String zipFile, String password) {
				try {
						ZipFile zip = new ZipFile(zipFile);
						
						if(password != null && !password.isEmpty()) {
								zip.setPassword(password);
						}
						
						return zip.isValidZipFile();
						
				} catch (Throwable t) {
						if(zipLogging) Log.e("NPM:isValidZip","Failed to validate: "+t.toString());
						return false;
				}
		}
		
		public static String removeFromZip(String zipFile, String fileToRemove, String password) {
				try {
						ZipFile zip = new ZipFile(zipFile);

						if(password != null && !password.isEmpty()) {
								zip.setPassword(password);
						}
						
						zip.removeFile(fileToRemove);
						
				} catch (Throwable t) {
						if(zipLogging) Log.e("NPM:removeFromZip","Failed to remove: "+t.toString());
						return t.toString();
				}
				return null;
		}
		
		public static boolean copyFile(String inputPath, String outputPath) {

				InputStream in = null;
				OutputStream out = null;
				try {

						//create output directory if it doesn't exist
						File dir = new File (outputPath.replace("/"+outputPath.split("/")[outputPath.split("/").length-1],"")); 
						if (!dir.exists())
						{
								dir.mkdirs();
						}


						in = new FileInputStream(inputPath);        
						out = new FileOutputStream(outputPath);

						byte[] buffer = new byte[1024];
						int read;
						while ((read = in.read(buffer)) != -1) {
								out.write(buffer, 0, read);
						}
						in.close();
						in = null;

            // write the output file (You have now copied the file)
            out.flush();
						out.close();
						out = null;        

				}  catch (FileNotFoundException fnfe1) {
						Log.e("NPM:copyFile", fnfe1.getMessage());
						return false;
				}
				catch (Exception e) {
						Log.e("NPM:copyFile", e.getMessage());
						return false;
				} finally 		
				{
						return true;
				}

		}
}

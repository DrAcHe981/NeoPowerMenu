package de.NeonSoft.neopowermenu.helpers;

import android.content.*;
import android.util.*;
import java.io.*;
import java.math.*;
import java.security.*;
import java.util.*;
import org.acra.*;
import org.acra.collector.*;
import org.acra.sender.*;
import org.apache.http.*;
import org.apache.http.client.entity.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.message.*;
import org.apache.http.protocol.*;


public class ACRAHelper implements ReportSender {
		private final static String BASE_URL = "http://www.Neon-Soft.de/inc/acra/acra.php";
		private final static String SHARED_SECRET = "acraerrormailer";
		private Map<String, String> custom_data = null;

		public ACRAHelper() {
				Log.i("ACRAHelper","Initializing...");
		}	
		public ACRAHelper(HashMap<String, String> custom_data) {
				this.custom_data = custom_data;
				Log.i("ACRAHelper","Initializing with data: "+custom_data+"...");
		}

		@Override
		public void send(Context mContext, CrashReportData report) throws ReportSenderException
		{
				//String url = getUrl();
				Log.i("ACRAHelper","Preparing to send: "+ report.get(ReportField.REPORT_ID));
				
				uploadHelper uH = new uploadHelper(mContext);
				uH.setInterface(new uploadHelper.uploadHelperInterface() {

								@Override
								public void onUploadStarted(boolean state)
								{
										// TODO: Implement this method
										Log.i("ACRAHelper","Starting upload...");
								}

								@Override
								public void onPublishUploadProgress(long nowSize, long totalSize)
								{
										// TODO: Implement this method
								}

								@Override
								public void onUploadComplete(String response)
								{
										// TODO: Implement this method
										Log.i("ACRAHelper","Upload complete!");
								}

								@Override
								public void onUploadFailed(String reason)
								{
										// TODO: Implement this method
										Log.e("ACRAHelper","Upload failed: "+reason);
								}
						});
				uH.setServerUrl(BASE_URL);
				String token= getToken();
				String[][] uploadPosts = {
						{"token",token},
						{"key",getKey(token)},
						{"DATE",new Date().toString()},
						{"REPORT_ID",report.get(ReportField.REPORT_ID)},
						{"APP_VERSION_CODE",report.get(ReportField.APP_VERSION_CODE)},
						{"APP_VERSION_NAME",report.get(ReportField.APP_VERSION_NAME)},
						{"PACKAGE_NAME",report.get(ReportField.PACKAGE_NAME)},
						{"FILE_PATH",report.get(ReportField.FILE_PATH)},
						{"PHONE_MODEL",report.get(ReportField.PHONE_MODEL)},
						{"ANDROID_VERSION",report.get(ReportField.ANDROID_VERSION)},
						{"BUILD",report.get(ReportField.BUILD)},
						{"BRAND",report.get(ReportField.BRAND)},
						{"PRODUCT",report.get(ReportField.PRODUCT)},
						{"TOTAL_MEM_SIZE",report.get(ReportField.TOTAL_MEM_SIZE)},
						{"AVAILABLE_MEM_SIZE",report.get(ReportField.AVAILABLE_MEM_SIZE)},
						{"CUSTOM_DATA",report.get(ReportField.CUSTOM_DATA)},
						{"STACK_TRACE",report.get(ReportField.STACK_TRACE)},
						{"INITIAL_CONFIGURATION",report.get(ReportField.INITIAL_CONFIGURATION)},
						{"CRASH_CONFIGURATION",report.get(ReportField.CRASH_CONFIGURATION)},
						{"DISPLAY",report.get(ReportField.DISPLAY)},
						{"USER_COMMENT",report.get(ReportField.USER_COMMENT)},
						{"USER_APP_START_DATE",report.get(ReportField.USER_APP_START_DATE)},
						{"USER_CRASH_DATE",report.get(ReportField.USER_CRASH_DATE)},
						{"DUMPSYS_MEMINFO",report.get(ReportField.DUMPSYS_MEMINFO)},
						{"DROPBOX",report.get(ReportField.DROPBOX)},
						{"LOGCAT",report.get(ReportField.LOGCAT)},
						{"EVENTSLOG",report.get(ReportField.EVENTSLOG)},
						{"RADIOLOG",report.get(ReportField.RADIOLOG)},
						{"IS_SILENT",report.get(ReportField.IS_SILENT)},
						{"DEVICE_ID",report.get(ReportField.DEVICE_ID)},
						{"INSTALLATION_ID",report.get(ReportField.INSTALLATION_ID)},
						{"USER_EMAIL",report.get(ReportField.USER_EMAIL)},
						{"DEVICE_FEATURES",report.get(ReportField.DEVICE_FEATURES)},
						{"ENVIRONMENT",report.get(ReportField.ENVIRONMENT)},
						{"SETTINGS_SYSTEM",report.get(ReportField.SETTINGS_SYSTEM)},
						{"SETTINGS_SECURE",report.get(ReportField.SETTINGS_SECURE)},
						{"SHARED_PREFERENCES",report.get(ReportField.SHARED_PREFERENCES)},
						{"APPLICATION_LOG",report.get(ReportField.APPLICATION_LOG)},
						{"MEDIA_CODEC_LIST",report.get(ReportField.MEDIA_CODEC_LIST)},
						{"THREAD_DETAILS",report.get(ReportField.THREAD_DETAILS)}
				};
				uH.setAdditionalUploadPosts(uploadPosts);
				try
				{
						new File(mContext.getFilesDir().getPath() + "/tmp").createNewFile();
				}
				catch (IOException e)
				{}
				uH.setLocalUrl(mContext.getFilesDir().getPath() + "/tmp");
						
				uH.startUpload();
		}
		
		public void sendOld(Context context, CrashReportData report) throws ReportSenderException {

				String url = getUrl();
				Log.i("ACRAHelper", url);

				try {
						DefaultHttpClient httpClient = new DefaultHttpClient();
						HttpPost httpPost = new HttpPost(url);

						List<NameValuePair> parameters = new ArrayList<NameValuePair>();

						if (custom_data != null) {
								for (Map.Entry<String, String> entry : custom_data.entrySet()) {
										parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
								}
						}			
						parameters.add(new BasicNameValuePair("DATE", new Date().toString()));
						parameters.add(new BasicNameValuePair("REPORT_ID", report.get(ReportField.REPORT_ID)));
						parameters.add(new BasicNameValuePair("APP_VERSION_CODE", report.get(ReportField.APP_VERSION_CODE)));
						parameters.add(new BasicNameValuePair("APP_VERSION_NAME", report.get(ReportField.APP_VERSION_NAME)));
						parameters.add(new BasicNameValuePair("PACKAGE_NAME", report.get(ReportField.PACKAGE_NAME)));
						parameters.add(new BasicNameValuePair("FILE_PATH", report.get(ReportField.FILE_PATH)));
						parameters.add(new BasicNameValuePair("PHONE_MODEL", report.get(ReportField.PHONE_MODEL)));
						parameters.add(new BasicNameValuePair("ANDROID_VERSION", report.get(ReportField.ANDROID_VERSION)));
						parameters.add(new BasicNameValuePair("BUILD", report.get(ReportField.BUILD)));
						parameters.add(new BasicNameValuePair("BRAND", report.get(ReportField.BRAND)));
						parameters.add(new BasicNameValuePair("PRODUCT", report.get(ReportField.PRODUCT)));
						parameters.add(new BasicNameValuePair("TOTAL_MEM_SIZE", report.get(ReportField.TOTAL_MEM_SIZE)));
						parameters.add(new BasicNameValuePair("AVAILABLE_MEM_SIZE", report.get(ReportField.AVAILABLE_MEM_SIZE)));
						parameters.add(new BasicNameValuePair("CUSTOM_DATA", report.get(ReportField.CUSTOM_DATA)));
						parameters.add(new BasicNameValuePair("STACK_TRACE", report.get(ReportField.STACK_TRACE)));
						parameters.add(new BasicNameValuePair("INITIAL_CONFIGURATION", report.get(ReportField.INITIAL_CONFIGURATION)));
						parameters.add(new BasicNameValuePair("CRASH_CONFIGURATION", report.get(ReportField.CRASH_CONFIGURATION)));
						parameters.add(new BasicNameValuePair("DISPLAY", report.get(ReportField.DISPLAY)));
						parameters.add(new BasicNameValuePair("USER_COMMENT", report.get(ReportField.USER_COMMENT)));
						parameters.add(new BasicNameValuePair("USER_APP_START_DATE", report.get(ReportField.USER_APP_START_DATE)));
						parameters.add(new BasicNameValuePair("USER_CRASH_DATE", report.get(ReportField.USER_CRASH_DATE)));
						parameters.add(new BasicNameValuePair("DUMPSYS_MEMINFO", report.get(ReportField.DUMPSYS_MEMINFO)));
						parameters.add(new BasicNameValuePair("DROPBOX", report.get(ReportField.DROPBOX)));
						parameters.add(new BasicNameValuePair("LOGCAT", report.get(ReportField.LOGCAT)));
						parameters.add(new BasicNameValuePair("EVENTSLOG", report.get(ReportField.EVENTSLOG)));
						parameters.add(new BasicNameValuePair("RADIOLOG", report.get(ReportField.RADIOLOG)));
						parameters.add(new BasicNameValuePair("IS_SILENT", report.get(ReportField.IS_SILENT)));
						parameters.add(new BasicNameValuePair("DEVICE_ID", report.get(ReportField.DEVICE_ID)));
						parameters.add(new BasicNameValuePair("INSTALLATION_ID", report.get(ReportField.INSTALLATION_ID)));
						parameters.add(new BasicNameValuePair("USER_EMAIL", report.get(ReportField.USER_EMAIL)));
						parameters.add(new BasicNameValuePair("DEVICE_FEATURES", report.get(ReportField.DEVICE_FEATURES)));
						parameters.add(new BasicNameValuePair("ENVIRONMENT", report.get(ReportField.ENVIRONMENT)));
						parameters.add(new BasicNameValuePair("SETTINGS_SYSTEM", report.get(ReportField.SETTINGS_SYSTEM)));
						parameters.add(new BasicNameValuePair("SETTINGS_SECURE", report.get(ReportField.SETTINGS_SECURE)));
						parameters.add(new BasicNameValuePair("SHARED_PREFERENCES", report.get(ReportField.SHARED_PREFERENCES)));
						parameters.add(new BasicNameValuePair("APPLICATION_LOG", report.get(ReportField.APPLICATION_LOG)));
						parameters.add(new BasicNameValuePair("MEDIA_CODEC_LIST", report.get(ReportField.MEDIA_CODEC_LIST)));
						parameters.add(new BasicNameValuePair("THREAD_DETAILS", report.get(ReportField.THREAD_DETAILS)));			

						httpPost.setEntity(new UrlEncodedFormEntity(parameters, HTTP.UTF_8));
						httpClient.execute(httpPost);
				} catch (Throwable e) {
						Log.e("ACRAHelper",e.toString());
				}
		}

		private String getUrl() {
				String token = getToken();
				String key = getKey(token);
				return String.format("%s&token=%s&key=%s&", BASE_URL, token, key);
		}

		private String getKey(String token) {
				return md5(String.format("%s+%s", SHARED_SECRET, token));
		}

		private String getToken() {
				return md5(UUID.randomUUID().toString());
		}

		public static String md5(String s) {
				MessageDigest m = null;
				try {
						m = MessageDigest.getInstance("MD5");
				} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
				}
				m.update(s.getBytes(), 0, s.length());
				String hash = new BigInteger(1, m.digest()).toString(16);
				return hash;
		}
		
		/*public class ACRAHelperFactory implements ReportSenderFactory {

				// NB requires a no arg constructor.
				public ACRAHelperFactory () {}
				
				public ReportSender create() {
						return new ACRAHelper();
				}
				public ReportSender create(Context context, ACRAConfiguration config) {
						return new ACRAHelper();
				}
		}*/
}


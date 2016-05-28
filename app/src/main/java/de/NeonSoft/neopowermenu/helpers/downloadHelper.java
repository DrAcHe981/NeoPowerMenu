package de.NeonSoft.neopowermenu.helpers;

import android.content.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import java.io.*;
import java.net.*;

public class downloadHelper
{
		
		private Context mContext;
		private downloadHelperInterface mInterface;
		
		private String mUrl;
		private String mLocalUrl;
		
		public downloadHelper(Context context,downloadHelperInterface listener) {
				this.mContext = context;
				this.mInterface = listener;
				this.mLocalUrl = context.getFilesDir().getPath()+"/download";
				new File(this.mLocalUrl).mkdirs();
		}
		
		void setUrl(String url) {
				this.mUrl = url;
		}
		
		void setLocalUrl(String url) {
				if(url.endsWith("/")) {
						url = url.substring(0,url.length()-1);
				}
				this.mLocalUrl = url;
				new File(this.mLocalUrl).mkdirs();
		}
		
		void startDownload() {
				if (!this.mUrl.isEmpty() && !this.mLocalUrl.isEmpty()) {
						new downloadAsync().execute(this.mUrl,this.mLocalUrl);
				} else {
						Toast.makeText(this.mContext,"Cant start download without url...",Toast.LENGTH_LONG).show();
				}
		}
		
		interface downloadHelperInterface {
				void onDownloadStarted(boolean state);
				void onPublishDownloadProgress(long nowSize,long totalSize);
				void onDownloadComplete();
				void onDownloadFailed(String reason);
		}
		
		class downloadAsync extends AsyncTask<String, String, String>
		{
				
				URLConnection connection;
				int CONNECT_TIMEOUT = 5000;
				int READ_TIMEOUT = 5000;
				long dltotalsize;
				long dlnowsize;
				int oldPercent;
				String file;
				BufferedInputStream dlinput;
				FileOutputStream dloutput;

				@Override
				protected void onPreExecute()
				{
						// TODO: Implement this method
						super.onPreExecute();
						mInterface.onDownloadStarted(true);
				}
				
				@Override
				protected String doInBackground(String[] p1)
				{
						// TODO: Implement this method
						int count;
						try
						{
								try
								{
										try
										{
												String name = p1[0].split("/")[p1[0].split("/").length-1];
												URL url = new URL(p1[0].replace(" ", "%20"));
												file = p1[1]+"/"+name;
												if(new File(file).exists()) {
														new File(file).delete();
												}
												connection = url.openConnection();
												connection.setConnectTimeout(CONNECT_TIMEOUT);
												connection.setReadTimeout(READ_TIMEOUT);
												connection.connect();
												Log.i("Starting download", url.toString());
												int lenghtOfFile = connection.getContentLength();
												if (lenghtOfFile > 0)
												{
														dltotalsize = ((Number) lenghtOfFile).longValue();
														dlinput = new BufferedInputStream(url.openStream(), 8192);
														dloutput = new FileOutputStream(file);
														byte data[] = new byte[1024];
														long total = 0;
														while ((count = dlinput.read(data)) != -1)
														{	
																total += count;
																dlnowsize = total;
																//if (((total * 100) / lenghtOfFile) >= oldPercent+1) {
																//		oldPercent = (int) ((total * 100) / lenghtOfFile);
																		publishProgress("" + ((total * 100) / lenghtOfFile), "" + dlnowsize, "" + dltotalsize);
																//}
																dloutput.write(data, 0, count);
														}
												}
										}
										catch (ConnectException ce)
										{
												//return "Download Failed";
												Log.e("Download Error: ", ce.toString());
												return "Connect Exception: "+ce.toString();
										}
								}
								catch (IOException ioe)
								{
										//return "Download Failed";
										Log.e("Download Error: ", ioe.toString());
										return "IO Exception: " + ioe.toString();
								}
						}
						catch (Throwable e)
						{
								//return "Download Failed";
								Log.e("Download Error: ", e.toString());
								return "Download failed: " + e.toString();
						}
						return null;
				}

				@Override
				protected void onProgressUpdate(String[] p1)
				{
						// TODO: Implement this method
						super.onProgressUpdate(p1);
						mInterface.onPublishDownloadProgress(Integer.parseInt(p1[1]),Integer.parseInt(p1[2]));
				}

				@Override
				protected void onPostExecute(String p1)
				{
						// TODO: Implement this method
						super.onPostExecute(p1);
						try
						{
								dloutput.flush();
								dloutput.close();
								dlinput.close();
						}
						catch (Throwable t)
						{
								Log.e("NPM",t.toString());
						}
						if(p1 == null) {
								File sizeCheck = new File(file);
								if (!sizeCheck.exists() || sizeCheck.length()<dltotalsize) {
										mInterface.onDownloadFailed("Downloaded file is corrupted!");
										sizeCheck.delete();
								} else {
										mInterface.onDownloadComplete();
								}
						} else {
								mInterface.onDownloadFailed(p1);
						}
				}
				
		}
	
}

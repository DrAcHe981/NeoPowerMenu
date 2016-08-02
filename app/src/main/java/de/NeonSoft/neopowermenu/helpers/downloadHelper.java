package de.NeonSoft.neopowermenu.helpers;

import android.content.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class downloadHelper
{
		
		private Context mContext;
		private downloadHelperInterface mInterface;
		
		private String mUrl;
		private String mLocalUrl;
		
		private boolean isRunning = false;
		private AsyncTask dlTask;

		URLConnection connection;
		int CONNECT_TIMEOUT = 5000;
		int READ_TIMEOUT = 5000;
		long total = 0;
		long dltotalsize = 0;
		long dlnowsize = 0;
		String file;
		BufferedInputStream dlinput;
		FileOutputStream dloutput;
		Timer timer = new Timer();
		
		long mAvgSpeed;
		long mSpeed;
		long mETA;
		int mProgress;
		/** How much was downloaded last time. */
		long iMLastDownloadedSize;
		/** The nanoTime last time. */
		long iMLastTime;
		long iMFirstTime;
		
		public downloadHelper(Context context) {
				this.mContext = context;
				this.mInterface = new downloadHelperInterface() {

						@Override
						public void onDownloadStarted(boolean state)
						{
								// TODO: Implement this method
						}

						@Override
						public void onPublishDownloadProgress(long nowSize, long totalSize)
						{
								// TODO: Implement this method
						}

						@Override
						public void onDownloadComplete()
						{
								// TODO: Implement this method
						}

						@Override
						public void onDownloadFailed(String reason)
						{
								// TODO: Implement this method
						}
				};
				this.mLocalUrl = context.getFilesDir().getPath()+"/download";
				new File(this.mLocalUrl).mkdirs();
		}
		
		public void setInterface(downloadHelperInterface listener) {
				this.mInterface = listener;
		}
		
		public void setUrl(String url) {
				this.mUrl = url;
		}
		
		public void setLocalUrl(String url) {
				if(url.endsWith("/")) {
						url = url.substring(0,url.length()-1);
				}
				this.mLocalUrl = url;
				new File(this.mLocalUrl).mkdirs();
		}
		
		public void startDownload() {
				if (!this.mUrl.isEmpty() && !this.mLocalUrl.isEmpty()) {
						dlTask = new downloadAsync().execute(this.mUrl,this.mLocalUrl);
				} else {
						Toast.makeText(this.mContext,"Cant start download without url...",Toast.LENGTH_LONG).show();
				}
		}
		
		public boolean stopDownload(boolean force) {
				return dlTask.cancel(force);
		}
		
		public boolean isRunning() {
				return isRunning;
		}
		
		public long[] getSizes() {
				return new long[] {dlnowsize,dltotalsize};
		}
		
		public int getProgress() {
				return mProgress;
		}
		
		public long getSpeed() {
				return mSpeed;
		}
		
		public long getAvgSpeed() {
				return mAvgSpeed;
		}
		
		public long getETA() {
				return mETA;
		}
		
		interface downloadHelperInterface {
				void onDownloadStarted(boolean state);
				void onPublishDownloadProgress(long nowSize,long totalSize);
				void onDownloadComplete();
				void onDownloadFailed(String reason);
		}
		
		class downloadAsync extends AsyncTask<String, String, String>
		{

				@Override
				protected void onPreExecute()
				{
						// TODO: Implement this method
						super.onPreExecute();
						mInterface.onDownloadStarted(true);
						isRunning = true;
						iMLastDownloadedSize = 0;
						iMLastTime = System.currentTimeMillis();
						iMFirstTime = iMLastTime;
						timer.scheduleAtFixedRate(new TimerTask() {

										@Override
										public void run()
										{
												// TODO: Implement this method
												if(dlnowsize > 0 && dltotalsize > 0) {
														mInterface.onPublishDownloadProgress(dlnowsize,dltotalsize);
												}
										}
								}, 0, 150L);
						timer.scheduleAtFixedRate(new TimerTask() {

										@Override
										public void run()
										{
												// TODO: Implement this method
												try {
														long mReaminingSize = dltotalsize - dlnowsize;
														long mDownloadedSize = dlnowsize;
														mProgress = (int) ((dlnowsize * 100) / dltotalsize);

														long timeElapsedSinceLastTime = System.currentTimeMillis() - iMLastTime;
														long timeElapsed = System.currentTimeMillis() - iMFirstTime;
														iMLastTime = System.currentTimeMillis();
														// Difference between last time and this time = how much was downloaded since last run.
														long downloadedSinceLastTime = mDownloadedSize - iMLastDownloadedSize;
														iMLastDownloadedSize = mDownloadedSize;
														if (timeElapsedSinceLastTime > 0 && timeElapsed > 0) {
																// Speed (bytes per second) = downloaded bytes / time in seconds (nanoseconds / 1000000000)
																mAvgSpeed = (mDownloadedSize) * 1000 / timeElapsed;
																mSpeed = downloadedSinceLastTime * 1000 / timeElapsedSinceLastTime;
														}

														if (mAvgSpeed > 0) {
																// ETA (milliseconds) = remaining byte size / bytes per millisecond (bytes per second * 1000)
																mETA = (mReaminingSize) * 1000 / mAvgSpeed;
														}
												} catch (Throwable t) {}
										}
								}, 1000L, 1000L);
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
												Log.i("NPM","Initializing download: \nServer: "+p1[0]+"\nFile Name: "+name);
												URL url = new URL(p1[0].replace(" ", "%20"));
												file = p1[1]+"/"+name;
												if(new File(file).exists()) {
														new File(file).delete();
												}
												connection = url.openConnection();
												connection.setConnectTimeout(CONNECT_TIMEOUT);
												connection.setReadTimeout(READ_TIMEOUT);
												connection.connect();
												//Log.i("Starting download", url.toString());
												dltotalsize = connection.getContentLength();
												if (dltotalsize > 0)
												{
														dlinput = new BufferedInputStream(url.openStream(), 8192);
														dloutput = new FileOutputStream(file);
														byte data[] = new byte[1024];
														total = 0;
														while ((count = dlinput.read(data)) != -1)
														{	
																total += count;
																dlnowsize = total;
																dloutput.write(data, 0, count);
														}
												}
										}
										catch (ConnectException ce)
										{
												//return "Download Failed";
												Log.e("NPM","Download Error: "+ce.toString());
												return "Connect Exception: "+ce.toString();
										}
								}
								catch (IOException ioe)
								{
										//return "Download Failed";
										Log.e("NPM","Download Error: "+ioe.toString());
										return "IO Exception: " + ioe.toString();
								}
						}
						catch (Throwable e)
						{
								//return "Download Failed";
								Log.e("NPM","Download Error: "+e.toString());
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
				protected void onCancelled(String p1)
				{
						// TODO: Implement this method
						super.onCancelled(p1);
						timer.cancel();
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
						mInterface.onDownloadFailed("canceled");
						isRunning = false;
				}

				@Override
				protected void onPostExecute(String p1)
				{
						// TODO: Implement this method
						super.onPostExecute(p1);
						timer.cancel();
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
						isRunning = false;
				}
				
		}
	
}

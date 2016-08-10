package de.NeonSoft.neopowermenu.helpers;
import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class uploadHelper
{
		
		private Activity mActivity;
		private uploadHelperInterface mInterface;
		
		private String mUrl;
		private String mLocalUrl;
		private String mAlias;
		private String[][] mParams;
		
		private FileInputStream fileInputStream;
		private DataOutputStream dos;
		
		private boolean isRunning = false;
		private boolean isCanceled = false;
		private AsyncTask ulTask;

		long total = 0;
		long dltotalsize = 0;
		long dlnowsize = 0;
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
		
		public uploadHelper(Activity context) {
				this.mActivity = context;
				this.mInterface = new uploadHelperInterface() {

						@Override
						public void onUploadStarted(boolean state)
						{
								// TODO: Implement this method
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
						}

						@Override
						public void onUploadFailed(String reason)
						{
								// TODO: Implement this method
						}
				};
		}
		
		public void setInterface(uploadHelperInterface listener) {
				this.mInterface = listener;
		}
		
		public void setServerUrl(String url) {
				this.mUrl = url;
		}
		
		public void setLocalUrl(String url) {
				this.mLocalUrl = url;
		}
		
		public void uploadAs(String alias) {
				this.mAlias = alias;
		}
		
		public void setAdditionalUploadPosts(String[][] params) {
				this.mParams = params;
		}
		
		public void startUpload() {
				if(!this.mUrl.isEmpty() || !this.mLocalUrl.isEmpty()) {
						if(this.mAlias != null && this.mAlias.isEmpty()) {
								new uploadAsync().execute(this.mUrl,this.mLocalUrl,this.mAlias);
						} else {
								new uploadAsync().execute(this.mUrl,this.mLocalUrl);
						}
				} else {
						Toast.makeText(this.mActivity,"Cant upload without server or local file...",Toast.LENGTH_LONG).show();
				}
		}
		
		public boolean stopUpload(boolean force) {
				isCanceled = true;
				return ulTask.cancel(force);
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
		
		public interface uploadHelperInterface {
				void onUploadStarted(boolean state);
				void onPublishUploadProgress(long nowSize,long totalSize);
				void onUploadComplete(String response);
				void onUploadFailed(String reason);
		}
		
		class uploadAsync extends AsyncTask<String, String, String>
		{
				
				@Override
				protected void onPreExecute()
				{
						// TODO: Implement this method
						super.onPreExecute();
						mInterface.onUploadStarted(true);
						isRunning = true;
						iMLastDownloadedSize = 0;
						iMLastTime = System.currentTimeMillis();
						iMFirstTime = iMLastTime;
						timer.scheduleAtFixedRate(new TimerTask() {

										@Override
										public void run()
										{
												mActivity.runOnUiThread(new Runnable() {

																@Override
																public void run()
																{
																		// TODO: Implement this method
																		if(dlnowsize > 0 && dltotalsize > 0) {
																				mInterface.onPublishUploadProgress(dlnowsize,dltotalsize);
																		} else {
																				//Log.d("NPM:uH","dlnowsize = "+dlnowsize+", dltotalsize = "+dltotalsize);
																		}
																}
														});
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
						String uploadFileName;
						if(p1.length>=3) {
								uploadFileName = p1[2];
						} else {
								uploadFileName = p1[1].split("/")[p1[1].split("/").length-1];
						}
						HttpURLConnection conn = null;
						String lineEnd = "\r\n";
						String twoHyphens = "--";
						String boundary = "*****";
						int bytesRead, bytesAvailable, bufferSize;
						byte[] buffer;
						int maxBufferSize = 1 * 1024 * 1024;
						Log.i("NPM:uH","Initializing upload: \nServer: "+p1[0]+"\nFile Name: "+uploadFileName+"\nSource file: "+p1[1]);
						File sourceFile = new File(p1[1]);
						if (!sourceFile.isFile()) {
								Log.e("NPM:uH", "Source File not exist :" +p1[1]);
								return "file not found";
						} else {
								try {
										// open a URL connection to the Servlet
										fileInputStream = new FileInputStream(sourceFile);
										URL url = new URL(p1[0].replace(" ","%20")+(mParams!=null ? getQuery(mParams) : ""));
										//Log.i("NPM:uH","Parsed url: "+url.toString());
										// Open a HTTP connection to the URL
										conn = (HttpURLConnection) url.openConnection();
										conn.setDoInput(true); // Allow Inputs
										conn.setDoOutput(true); // Allow Outputs
										conn.setUseCaches(false); // Don't use a Cached Copy
										conn.setRequestMethod("POST");
										conn.setRequestProperty("Connection", "Keep-Alive");
										conn.setRequestProperty("ENCTYPE", "multipart/form-data");
										conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
										conn.setRequestProperty("uploaded_file", p1[1]);
										dos = new DataOutputStream(conn.getOutputStream());
										dos.writeBytes(twoHyphens + boundary + lineEnd);
										dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ p1[1] + "\"" + lineEnd);
										dos.writeBytes(lineEnd);
										// create a buffer of maximum size
										bytesAvailable = fileInputStream.available();
										bufferSize = Math.min(bytesAvailable, maxBufferSize);
										buffer = new byte[bufferSize];
										// read file and write it into form...
										bytesRead = fileInputStream.read(buffer, 0, bufferSize);
										dltotalsize = sourceFile.length();
										total = 0;
										while ((count = bytesRead) > 0) {
												if(!isCancelled()) {
														dos.write(buffer, 0, bufferSize);
														bytesAvailable = fileInputStream.available();
														bufferSize = Math.min(bytesAvailable, maxBufferSize);
														bytesRead = fileInputStream.read(buffer, 0, bufferSize);
														total += bytesRead;
														dlnowsize = total;
												} else {
														onCancelled("");
												}
										}
										// send multipart form data necesssary after file data...
										dos.writeBytes(lineEnd);
										dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
										// Responses from the server (code and message)
										//serverResponseCode = conn.getResponseCode();
										//String serverResponseMessage = conn.getResponseMessage();
										//if(serverResponseCode==200) {
												InputStream serverErrorMessage = conn.getInputStream();
												BufferedReader br = new BufferedReader(new InputStreamReader(serverErrorMessage));
												String errorMsgB = "";
												String errorMsg = "";
												while ((errorMsgB = br.readLine()) != null) {
														errorMsg += errorMsgB+"\n";
												}
												Log.i("NPM:uH", "HTTP Message is : "+ errorMsg);
												return errorMsg;
										//}
										//Log.i("NPM:uH", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
										//if(serverResponseCode==200) {
										//		return null;
										//}
								} catch (MalformedURLException ex) {
										Log.e("NPM:uH",ex.toString());
										return "error: " + ex.toString();
								} catch (Throwable e) {
										Log.e("NPM:uH",e.toString());
										return "Exception : " + e.toString();
								}
						} // End else block
				}
				
								@Override
								protected void onProgressUpdate(String[] p1)
								{
										// TODO: Implement this method
										super.onProgressUpdate(p1);
										mInterface.onPublishUploadProgress(Integer.parseInt(p1[1]),Integer.parseInt(p1[2]));
								}

								@Override
								protected void onCancelled(String p1)
								{
										// TODO: Implement this method
										super.onCancelled(p1);
										timer.cancel();
										try {
												//close the streams //
												fileInputStream.close();
												dos.flush();
												dos.close();
										} catch (Throwable t) {
										}
										mInterface.onUploadFailed("canceled");
										isRunning = false;
								}

								@Override
								protected void onPostExecute(String p1)
								{
										// TODO: Implement this method
										super.onPostExecute(p1);
										timer.cancel();
										try {
												//close the streams //
												fileInputStream.close();
												dos.flush();
												dos.close();
										} catch (Throwable t) {
										}
										if(p1.contains("success")) {
												mInterface.onUploadComplete(p1);
										} else {
												mInterface.onUploadFailed(p1);
										}
										isRunning = false;
								}
		}
		
		private String getQuery(String[][] params) throws UnsupportedEncodingException
		{
				StringBuilder result = new StringBuilder();
				boolean first = true;

				for (int x=0;x<params.length;x++)
				{
						if (first) {
								first = false;
								result.append("?");
						} else {
								result.append("&");
						}

						result.append(URLEncoder.encode(params[x][0].replace("'","\\'").replace("\"","\\\"").replace("\\","\\\\").replace("/",""), "UTF-8"));
						result.append("=");
						result.append(URLEncoder.encode(params[x][1].replace("'","\\'").replace("\"","\\\"").replace("\\","\\\\").replace("/",""), "UTF-8"));
				}

				return result.toString();
		}
		
}

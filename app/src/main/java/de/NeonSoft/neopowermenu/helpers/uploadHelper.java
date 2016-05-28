package de.NeonSoft.neopowermenu.helpers;
import android.content.*;
import android.os.*;
import android.util.*;
import java.io.*;
import java.net.*;
import android.widget.*;

public class uploadHelper
{
		
		private Context mContext;
		private uploadHelperInterface mInterface;
		
		private String mUrl;
		private String mLocalUrl;
		private String mAlias;
		private String[][] mParams;
		
		private FileInputStream fileInputStream;
		private DataOutputStream dos;
		
		public uploadHelper(Context context,uploadHelperInterface listener) {
				this.mContext = context;
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
						Toast.makeText(this.mContext,"Cant upload without server or local file...",Toast.LENGTH_LONG).show();
				}
		}
		
		public interface uploadHelperInterface {
				void onUploadStarted(boolean state);
				void onPublishUploadProgress(long nowSize,long totalSize);
				void onUploadComplete();
				void onUploadFailed(String reason);
		}
		
		class uploadAsync extends AsyncTask<String, String, String>
		{
				
				long dltotalsize;
				long dlnowsize;
				int oldPercent;
				
				@Override
				protected void onPreExecute()
				{
						// TODO: Implement this method
						super.onPreExecute();
						mInterface.onUploadStarted(true);
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
						Log.i("NPM","Initializing upload: \nServer: "+p1[0]+"\nFile Name: "+uploadFileName);
						File sourceFile = new File(p1[1]);
						if (!sourceFile.isFile()) {
								Log.e("uploadFile", "Source File not exist :" +p1[1] + "" + uploadFileName);
								return "file not found";
						} else {
								try {
										// open a URL connection to the Servlet
										fileInputStream = new FileInputStream(sourceFile);
										URL url = new URL(p1[0].replace(" ","%20")+(mParams!=null ? getQuery(mParams) : ""));
										//Log.i("NPM","Parsed url: "+url.toString());
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
										long total = 0;
										while ((count = bytesRead) > 0) {
												total += count;
												dlnowsize = total;
												publishProgress("" + ((total * 100) / sourceFile.length()),""+dlnowsize,""+dltotalsize);
												dos.write(buffer, 0, bufferSize);
												bytesAvailable = fileInputStream.available();
												bufferSize = Math.min(bytesAvailable, maxBufferSize);
												bytesRead = fileInputStream.read(buffer, 0, bufferSize);
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
												Log.i("NPM", "HTTP Error Message is : "+ errorMsg);
												return errorMsg;
										//}
										//Log.i("NPM", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
										//if(serverResponseCode==200) {
										//		return null;
										//}
								} catch (MalformedURLException ex) {
										ex.printStackTrace();
										return "error: " + ex.toString();
								} catch (Exception e) {
										e.printStackTrace();
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
								protected void onPostExecute(String p1)
								{
										// TODO: Implement this method
										super.onPostExecute(p1);
										try {
												//close the streams //
												fileInputStream.close();
												dos.flush();
												dos.close();
										} catch (Throwable t) {
										}
										if(p1.contains("success")) {
												mInterface.onUploadComplete();
										} else {
												mInterface.onUploadFailed(p1);
										}
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

						result.append(URLEncoder.encode(params[x][0].replace("'","\\'").replace("\"","\\\""), "UTF-8"));
						result.append("=");
						result.append(URLEncoder.encode(params[x][1].replace("'","\\'").replace("\"","\\\""), "UTF-8"));
				}

				return result.toString();
		}
		
}

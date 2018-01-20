package de.NeonSoft.neopowermenu.helpers;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.widget.*;

import java.io.*;
import java.net.*;
import java.util.*;

import de.NeonSoft.neopowermenu.*;

import android.os.PowerManager.WakeLock;

public class uploadHelper {

    public static String[] STATE_NAMES = {"STATE_WAITING","STATE_CONNECTING","STATE_REQUESTINGINFO","STATE_UPLOADING","STATE_CANCELLING"};
    public static int STATE_WAITING = 0;
    public static int STATE_CONNECTING = 1;
    public static int STATE_REQUESTINGINFO = 2;
    public static int STATE_UPLOADING = 3;
    public static int STATE_CANCELLING = 4;

    private PowerManager pm;
    private WakeLock mPartialWakeLock;

    private Context mContext;
    private Activity mActivity;
    private uploadHelperInterface mInterface;

    private String mUrl;
    private String mLocalUrl;
    private String mAlias;
    private String[][] mParams;

    private FileInputStream fileInputStream;
    private DataOutputStream dos;

    private String instanceName = "";

    private boolean isRunning = false;
    private boolean isCanceled = false;
    private AsyncTask ulTask;

    boolean mAllowMultiple = false;

    int CONNECT_TIMEOUT = 20000;
    int READ_TIMEOUT = 20000;

    long total = 0;
    long dltotalsize = 0;
    long dlnowsize = 0;
    Timer timer = new Timer();

    long mAvgSpeed;
    long mSpeed;
    long mETA;
    int mState = this.STATE_WAITING;
    int mProgress;
    /**
     * How much was downloaded last time.
     */
    long iMLastDownloadedSize;
    /**
     * The nanoTime last time.
     */
    long iMLastTime;
    long iMFirstTime;

    public uploadHelper(Activity context) {
        this.mActivity = context;
        this.mInterface = new uploadHelperInterface() {

            @Override
            public void onStateChanged(int state) {

            }

            @Override
            public void onPublishUploadProgress(long nowSize, long totalSize) {

            }

            @Override
            public void onUploadComplete(String response) {

            }

            @Override
            public void onUploadFailed(String reason) {

            }
        };
    }
    public uploadHelper(Context context) {
        this.mContext = context;
        this.mInterface = new uploadHelperInterface() {

            @Override
            public void onStateChanged(int state) {

            }

            @Override
            public void onPublishUploadProgress(long nowSize, long totalSize) {

            }

            @Override
            public void onUploadComplete(String response) {

            }

            @Override
            public void onUploadFailed(String reason) {

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

    public void setAllowMultiple(boolean mode) {
        this.mAllowMultiple = mode;
    }

    public void startUpload() {
        if (!this.mUrl.isEmpty() || !this.mLocalUrl.isEmpty()) {
            instanceName = String.format(Locale.getDefault(), "%04d",Math.round(Math.random() * 1000));
            if (this.mAlias != null && this.mAlias.isEmpty()) {
                ulTask = startAsyncTask(new uploadAsync(),this.mUrl, this.mLocalUrl, this.mAlias);
            } else {
                ulTask = startAsyncTask(new uploadAsync(),this.mUrl, this.mLocalUrl);
            }
        } else {
            Toast.makeText(this.mActivity == null ? this.mContext : this.mActivity, "Cant upload without server or local file...", Toast.LENGTH_LONG).show();
        }
    }

    public boolean stopUpload(boolean force) {
        isCanceled = true;
        setState(STATE_CANCELLING);
        return ulTask.cancel(force);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public long[] getSizes() {
        return new long[]{dlnowsize, dltotalsize};
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

    public int getState() {
        return mState;
    }

    private void setState(final int state) {
        mState = state;
        Log.i("NPM", "[uploadHelper] " + instanceName + "> State changed to " + STATE_NAMES[state] + "(" + state + ")");
        if(mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    mInterface.onStateChanged(state);
                }
            });
        }
    }

    public interface uploadHelperInterface {
        void onStateChanged(int state);

        void onPublishUploadProgress(long nowSize, long totalSize);

        void onUploadComplete(String response);

        void onUploadFailed(String reason);
    }

    class uploadAsync extends AsyncTask<Object, String, String> {

        HttpURLConnection conn = null;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            setState(STATE_WAITING);
            isRunning = true;
            iMLastDownloadedSize = 0;
            iMLastTime = System.currentTimeMillis();
            iMFirstTime = iMLastTime;
            pm = (PowerManager) (mActivity == null ? mContext : mActivity).getSystemService(Context.POWER_SERVICE);
            mPartialWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "NPM");
            mPartialWakeLock.acquire();

            timer.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    if(mActivity != null) {
                        mActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                if (dlnowsize > 0 && dltotalsize > 0) {
                                    if (getState() != STATE_CANCELLING) {
                                        mInterface.onPublishUploadProgress(dlnowsize, dltotalsize);
                                    }
                                }
                            }
                        });
                    }
                }
            }, 0, 150L);
            timer.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {

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
                        //Log.d("NPM:uH","dlnowsize = "+dlnowsize+", dltotalsize = "+dltotalsize);
                    } catch (Throwable t) {
                    }
                }
            }, 1000L, 1000L);
        }

        @Override
        protected String doInBackground(Object... p1) {

            int count;
            String uploadFileName;
            if (p1.length >= 3) {
                uploadFileName = p1[2].toString();
            } else {
                uploadFileName = p1[1].toString().split("/")[p1[1].toString().split("/").length - 1];
            }
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1024;
            Log.i("NPM", "[uploadHelper] Initializing upload: \nServer: " + p1[0] + "\nFile Name: " + uploadFileName + "\nSource file: " + p1[1]);
            File sourceFile = new File(p1[1].toString());
            if (!sourceFile.isFile()) {
                Log.e("NPM", "[uploadHelper] " + instanceName + "> Source File not exist :" + p1[1]);
                return "file not found";
            } else {
                try {
                    Log.i("NPM", "[uploadHelper] Uploading " + helper.getSizeString(sourceFile.length(), true) + "(" + sourceFile.length() + ")");
                    // open a URL connection to the Servlet
                    fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(p1[0].toString().replace(" ", "%20") + (mParams != null ? getQuery(mParams) : ""));
                    //Log.i("NPM:uH","Parsed url: "+url.toString());
                    // Open a HTTP connection to the URL
                    setState(STATE_CONNECTING);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(CONNECT_TIMEOUT);
                    conn.setReadTimeout(READ_TIMEOUT);
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    //System.setProperty("http.keepAlive","false");
                    conn.setRequestProperty("connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", p1[1].toString());
                    dltotalsize = sourceFile.length() +
                            (twoHyphens + boundary + lineEnd).getBytes().length +
                            ("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + p1[1] + "\"" + lineEnd).getBytes().length +
                            (lineEnd).getBytes().length +
                            (lineEnd).getBytes().length +
                            (twoHyphens + boundary + twoHyphens + lineEnd).getBytes().length;
                    if(Build.VERSION.SDK_INT >= 19) {
                        conn.setFixedLengthStreamingMode(dltotalsize);
                    }
                    //conn.setChunkedStreamingMode(maxBufferSize);
                    dos = new DataOutputStream(conn.getOutputStream());
                    setState(STATE_REQUESTINGINFO);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + p1[1] + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    // create a buffer of maximum size
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];
                    // read file and write it into form...
                    setState(STATE_UPLOADING);
                    bytesRead = 0;//fileInputStream.read(buffer, 0, bufferSize);
                    dlnowsize = bytesRead;
                    iMLastDownloadedSize = bytesRead;
                    total = bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer, 0, bufferSize)) > 0) {
                        //for(int i = 0; i < buffer.length; i+=bufferSize) {
                        if (getState()!=STATE_CANCELLING) {
                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            //bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                            total += bytesRead;
                            dlnowsize = total;
                        } else {
                            bytesRead = 0;
                        }
                        //}
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
                        errorMsg += errorMsgB + "\n";
                    }
                    Log.i("NPM", "[uploadHelper] HTTP Message is : " + errorMsg.split(",")[0]);
                    return errorMsg;
                    //}
                    //Log.i("NPM:uH", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
                    //if(serverResponseCode==200) {
                    //		return null;
                    //}
                } catch (MalformedURLException ex) {
                    Log.e("NPM", "[uploadHelper] " + instanceName + "> "+ ex.toString());
                    return "Failed at: "+STATE_NAMES[getState()];
                } catch (Throwable e) {
                    Log.e("NPM", "[uploadHelper] " + instanceName + "> "+ e.toString());
                    return "Failed at: "+STATE_NAMES[getState()];
                }
            } // End else block
        }

        @Override
        protected void onProgressUpdate(String[] p1) {

            super.onProgressUpdate(p1);
            mInterface.onPublishUploadProgress(Integer.parseInt(p1[1]), Integer.parseInt(p1[2]));
        }

        @Override
        protected void onCancelled(String p1) {

            super.onCancelled(p1);
            if (mPartialWakeLock != null && mPartialWakeLock.isHeld()) {
                mPartialWakeLock.release();
                mPartialWakeLock = null;
            }
            System.setProperty("http.keepAlive", "true");
            timer.cancel();
            try {
                //close the streams //
                conn.disconnect();
                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (Throwable t) {
            }
            mInterface.onUploadFailed("canceled");
            isRunning = false;
        }

        @Override
        protected void onPostExecute(String p1) {

            super.onPostExecute(p1);
            if (mPartialWakeLock != null && mPartialWakeLock.isHeld()) {
                mPartialWakeLock.release();
                mPartialWakeLock = null;
            }
            System.setProperty("http.keepAlive", "true");
            timer.cancel();
            try {
                //close the streams //
                conn.disconnect();
                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (Throwable ignored) {
            }
            if (p1.contains("success")) {
                mInterface.onUploadComplete(p1);
            } else {
                switch (getState()) {
                    case 1:
                        mInterface.onUploadFailed((mActivity == null ? mContext : mActivity).getString(R.string.uploadHelper_FailedAt1));
                        break;
                    case 2:
                        mInterface.onUploadFailed((mActivity == null ? mContext : mActivity).getString(R.string.uploadHelper_FailedAt2));
                        break;
                    case 3:
                        mInterface.onUploadFailed((mActivity == null ? mContext : mActivity).getString(R.string.uploadHelper_FailedAt3));
                        break;
                    default:
                        mInterface.onUploadFailed((mActivity == null ? mContext : mActivity).getString(R.string.uploadHelper_UnknownError));
                        break;
                }
            }
            isRunning = false;
        }
    }

    private String getQuery(String[][] params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (String[] param : params) {
            if (first) {
                first = false;
                result.append("?");
            } else {
                result.append("&");
            }

            result.append(URLEncoder.encode(param[0].replace("'", "\\'").replace("\"", "\\\"").replace("\\", "\\\\").replace("/", ""), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(param[1].replace("'", "\\'").replace("\"", "\\\"").replace("\\", "\\\\").replace("/", ""), "UTF-8"));
        }

        return result.toString();
    }

    @SafeVarargs
    private final <T> AsyncTask startAsyncTask(AsyncTask<T, ?, ?> asyncTask, T... params) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && mAllowMultiple)
            return asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        else
            return asyncTask.execute(params);
    }

}

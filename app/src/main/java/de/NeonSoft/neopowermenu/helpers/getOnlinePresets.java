package de.NeonSoft.neopowermenu.helpers;


import android.os.*;
import android.util.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.Preferences.*;
import java.io.*;
import java.util.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.params.*;
import org.json.*;
import android.view.View.*;

public class getOnlinePresets extends AsyncTask<String, String, String>
{

		private String[] onlineTitles;
		private String[] onlineCreator;
		private String[] onlineEnabled;
		private String[] onlineLocal;
		
		@Override
		protected void onPreExecute()
		{
				// TODO: Implement this method
				if(PreferencesPresetsFragment.onlineRequestIsRunning) {
						cancel(true);
						return;
				}
				PreferencesPresetsFragment.onlineRequestIsRunning = true;
				PreferencesPresetsFragment.onlineList.startAnimation(AnimationUtils.loadAnimation(PreferencesPresetsFragment.mContext,R.anim.fade_out));
				PreferencesPresetsFragment.onlineList.setVisibility(View.GONE);
				if(PreferencesPresetsFragment.onlineAdapter!=null) {
						PreferencesPresetsFragment.onlineAdapter.removeAll();
				}
				PreferencesPresetsFragment.onlineMSG.setText("Loading...");
				PreferencesPresetsFragment.onlineMSG.setVisibility(View.VISIBLE);
				PreferencesPresetsFragment.onlineMSG.startAnimation(AnimationUtils.loadAnimation(PreferencesPresetsFragment.mContext,R.anim.fade_in));
				super.onPreExecute();
		}

		@Override
		protected String doInBackground(String[] p1)
		{
				// TODO: Implement this method
				String orderBy = MainActivity.context.getString(R.string.presetsManager_OrderNames).split("/")[0]+" ("+MainActivity.context.getString(R.string.presetsManager_OrderAscDesc).split("/")[0]+")";
				String searchFor = "";
				if(p1!=null && p1.length>0) {
						for(int i = 0;i<p1.length;i++) {
						if(p1[i].contains("order=")) {
								orderBy = p1[i];
						} else if (p1[i].contains("search=")) {
								searchFor = p1[i];
						}
						}
				}
				String searchTerm = "";
				if(!searchFor.isEmpty()) {
						searchTerm = searchFor.split("ch=")[1];
				}
				String orderName = "_presetName";
				String orderDirection = "ASC";
				if(orderBy.contains("("+MainActivity.context.getString(R.string.presetsManager_OrderAscDesc).split("/")[1]+")")) {
						orderDirection = "DESC";
				}
				if(orderBy.contains(MainActivity.context.getString(R.string.presetsManager_OrderNames).split("/")[1])) {
						orderName = "_presetStars";
				} else if (orderBy.contains(MainActivity.context.getString(R.string.presetsManager_OrderNames).split("/")[2])) {
						orderName = "_presetCreator";
				} else if (orderBy.contains(MainActivity.context.getString(R.string.presetsManager_OrderNames).split("/")[3])) {
						orderName = "own";
				}
				
				try
				{
						HttpParams httpParams = new BasicHttpParams();
						HttpConnectionParams.setConnectionTimeout(httpParams,
																											MainActivity.TIMEOUT_MILLISEC);
						HttpConnectionParams.setSoTimeout(httpParams, MainActivity.TIMEOUT_MILLISEC);

						HttpParams p = new BasicHttpParams();
						p.setParameter("user", "1");

						HttpClient httpclient = new DefaultHttpClient(p);
						String url = "http://"+(MainActivity.LOCALTESTSERVER ? "127.0.0.1:8080" : "www.Neon-Soft.de")+"/page/NeoPowerMenu/phpWebservice/webservice1.php?action=presets&format=json&userId="+MainActivity.preferences.getString("userUniqeId","null")+"&sortBy="+orderName+"&sortDir="+orderDirection+(searchTerm.isEmpty() ? "" : "&searchFor="+searchTerm);
						HttpPost httppost = new HttpPost(url);

						try
						{
								Log.i(getClass().getSimpleName(), "send  task - start");

								//List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
								//		2);
								//nameValuePairs.add(new BasicNameValuePair("user", "1"));
								//httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
								ResponseHandler<String> responseHandler = new BasicResponseHandler();
								String responseBody = httpclient.execute(httppost, responseHandler);
								if(responseBody.contains("Cannot connect to the DB")) {
										return "Request failed: Cannot connect to the DB";
								}
								JSONObject json = new JSONObject(responseBody);
								JSONArray jArray = json.getJSONArray("presets");
								ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();

								onlineTitles = new String[jArray.length()];
								onlineCreator = new String[jArray.length()];
								onlineEnabled = new String[jArray.length()];
								onlineLocal = new String[jArray.length()];
								PresetsPage.onlineIds = new String[jArray.length()];

								for (int i = 0; i < jArray.length(); i++)
								{

										HashMap<String, String> map = new HashMap<String, String>();
										JSONObject e = jArray.getJSONObject(i);
										String s = e.getString("preset");
										JSONObject jObject = new JSONObject(s);

										map.put("Id", jObject.getString("_id"));
										map.put("Name", jObject.getString("_presetName"));
										map.put("Creator", jObject.getString("_presetCreator"));
										map.put("AppVersion", jObject.getString("_presetAppVersion"));
										map.put("CreatorId",jObject.getString("_creatorUniqeId"));

										mylist.add(map);
										onlineTitles[i] = jObject.getString("_presetName");
										onlineCreator[i] = jObject.getString("_presetCreator")+",=,"+jObject.getString("_presetAppVersion")+",=,"+jObject.getString("_presetStars");
										onlineEnabled[i] = "false";
										onlineLocal[i] = "false";
										PresetsPage.onlineIds[i] = jObject.getString("_creatorUniqeId");

								}

						}
						catch (ClientProtocolException e)
						{
								// TODO Auto-generated catch block
								return e.toString();
								//return Errorstring;
						}
						catch (IOException e)
						{
								// TODO Auto-generated catch block
								return e.toString();
								//return Errorstring;
						}

				}
				catch (Throwable t)
				{
						return t.toString();
						//return Errorstring;
				}
				return  null;
		}

		@Override
		protected void onPostExecute(String result)
		{
				// TODO: Implement this method
				super.onPostExecute(result);
				if (result != null)
				{
								Log.e("NPM",result);
								if((result.contains("Connection to") && result.contains("refused")) || result.contains("Unable to resolve host")) {
										PreferencesPresetsFragment.onlineMSG.setText(PreferencesPresetsFragment.mContext.getString(R.string.presetsManager_CantConnecttoServer));
								} else if (result.contains("Cannot connect to the DB")) {
										PreferencesPresetsFragment.onlineMSG.setText(PreferencesPresetsFragment.mContext.getString(R.string.presetsManager_CantConnecttoDB));
								} else {
										slideDownDialogFragment dialogFragment = new slideDownDialogFragment(PreferencesPresetsFragment.mContext, new slideDownDialogFragment.slideDownDialogInterface() {

														@Override
														public void onListItemClick(int position, String text)
														{
																// TODO: Implement this method
														}

														@Override
														public void onNegativeClick()
														{
																// TODO: Implement this method
														}

														@Override
														public void onNeutralClick()
														{
																// TODO: Implement this method
														}

														@Override
														public void onPositiveClick(ArrayList<String> resultData)
														{
																// TODO: Implement this method
														}

														@Override
														public void onTouchOutside()
														{
																// TODO: Implement this method
														}
												});
										dialogFragment.setDialogText(result);
										dialogFragment.setDialogPositiveButton(PreferencesPresetsFragment.mContext.getString(R.string.Dialog_Ok));
										MainActivity.fragmentManager.beginTransaction().add(R.id.pref_container,dialogFragment,"slideDownDialog").commit();
										PreferencesPresetsFragment.onlineMSG.setText(result);
								}
						PreferencesPresetsFragment.onlineMSG.setVisibility(View.VISIBLE);
						PreferencesPresetsFragment.onlineMSG.startAnimation(AnimationUtils.loadAnimation(PreferencesPresetsFragment.mContext,R.anim.fade_in));
						//Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
				}
				else
				{
						ArrayList<String> ListTitles = new ArrayList<String>(Arrays.asList(onlineTitles));
						ArrayList<String> ListDescs = new ArrayList<String>(Arrays.asList(onlineCreator));
						ArrayList<String> ListEnabled = new ArrayList<String>(Arrays.asList(onlineEnabled));
						ArrayList<String> ListLocal = new ArrayList<String>(Arrays.asList(onlineLocal));
						PreferencesPresetsFragment.onlineAdapter = new PresetsAdapter(PreferencesPresetsFragment.mContext, ListTitles, ListDescs, ListEnabled, ListLocal);
						PreferencesPresetsFragment.onlineList.setAdapter(PreferencesPresetsFragment.onlineAdapter);
						PreferencesPresetsFragment.onlineList.setFastScrollEnabled(true);
						PreferencesPresetsFragment.onlineList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
						PreferencesPresetsFragment.onlineMSG.startAnimation(AnimationUtils.loadAnimation(PreferencesPresetsFragment.mContext,R.anim.fade_out));
						PreferencesPresetsFragment.onlineMSG.setVisibility(View.GONE);
						PreferencesPresetsFragment.onlineList.setVisibility(View.VISIBLE);
						PreferencesPresetsFragment.onlineList.startAnimation(AnimationUtils.loadAnimation(PreferencesPresetsFragment.mContext,R.anim.fade_in));
				}
				PreferencesPresetsFragment.onlineRequestIsRunning = false;
		}

}

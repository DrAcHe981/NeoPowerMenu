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
import android.graphics.*;

public class getOnlinePresets extends AsyncTask<String, String, String>
{
		public static int MODE_ALL = 0;
		public static int MODE_OFFSET = 1;

		private int refreshMode = this.MODE_ALL;

		private String[] onlineTitles;
		private String[] onlineCreator;
		private String[] onlineEnabled;
		private String[] onlineLocal;

		public getOnlinePresets()
		{
		}

		public getOnlinePresets(int refreshMode)
		{
				this.refreshMode = refreshMode;
		}

		@Override
		protected void onPreExecute()
		{
				// TODO: Implement this method
				if (PreferencesPresetsFragment.onlineRequestIsRunning)
				{
						PreferencesPresetsFragment.onlineMSG.setText(MainActivity.context.getString(R.string.presetsManager_LoadMore).split("\\|")[0]);
						if (PreferencesPresetsFragment.onlineMSG.getVisibility() == View.GONE)
						{
								PreferencesPresetsFragment.onlineMSG.setVisibility(View.VISIBLE);
								PreferencesPresetsFragment.onlineMSG.startAnimation(AnimationUtils.loadAnimation(PreferencesPresetsFragment.mContext, R.anim.fade_in));
						}
						cancel(true);
						return;
				}
				PreferencesPresetsFragment.onlineRequestIsRunning = true;
				if (refreshMode == MODE_ALL && PreferencesPresetsFragment.onlineList.getVisibility() == View.VISIBLE)
				{
						PreferencesPresetsFragment.onlineList.startAnimation(AnimationUtils.loadAnimation(PreferencesPresetsFragment.mContext, R.anim.fade_out));
						PreferencesPresetsFragment.onlineList.setVisibility(View.GONE);
				}
				/*if(PreferencesPresetsFragment.onlineAdapter!=null) {
				 PreferencesPresetsFragment.onlineAdapter.removeAll();
				 }*/
				if (refreshMode == MODE_ALL)
				{
						PresetsPage.onlineIds.clear();
						PreferencesPresetsFragment.DownloadingActiveForRoot.clear();
						PreferencesPresetsFragment.DownloadingActiveForHelper.clear();
						PreferencesPresetsFragment.DownloadingActiveForLayout.clear();
						PreferencesPresetsFragment.DownloadingActiveForImageView.clear();
						PreferencesPresetsFragment.DownloadingActiveForOldText.clear();
						PreferencesPresetsFragment.DownloadingActiveForLabel.clear();
						PreferencesPresetsFragment.DownloadingActiveForProgress.clear();
						PreferencesPresetsFragment.OnlineListTitles.clear();
						PreferencesPresetsFragment.OnlineListDescs.clear();
						PreferencesPresetsFragment.OnlineListEnabled.clear();
						PreferencesPresetsFragment.OnlineListLocal.clear();
						PreferencesPresetsFragment.OnlineHasGraphics.clear();
						PreferencesPresetsFragment.onlineAdapter.notifyDataSetChanged();
						PreferencesPresetsFragment.onlineMSG.setText(MainActivity.context.getString(R.string.presetsManager_LoadMore).split("\\|")[0]);
						PreferencesPresetsFragment.onlineMSGHolder.setVisibility(View.VISIBLE);
						PreferencesPresetsFragment.onlineMSGHolder.startAnimation(AnimationUtils.loadAnimation(PreferencesPresetsFragment.mContext, R.anim.fade_in));
				}
				super.onPreExecute();
		}

		@Override
		protected String doInBackground(String[] p1)
		{
				// TODO: Implement this method
				String orderBy = MainActivity.context.getString(R.string.presetsManager_OrderNames).split("\\|")[0] + " (" + MainActivity.context.getString(R.string.presetsManager_OrderAscDesc).split("/")[0] + ")";
				String searchFor = "";
				String offset = "";
				if (p1 != null && p1.length > 0)
				{
						for (int i = 0;i < p1.length;i++)
						{
								if (!p1[i].isEmpty())
								{
										if (p1[i].contains("order="))
										{
												orderBy = p1[i];
										}
										else if (p1[i].contains("search="))
										{
												searchFor = p1[i];
										}
										else if (p1[i].contains("offset="))
										{
												offset = p1[i].split("set=")[1];
										}
								}
						}
				}
				String searchTerm = "";
				if (!searchFor.isEmpty())
				{
						searchTerm = searchFor.split("ch=")[1];
				}
				String orderName = "_presetName";
				String orderDirection = "ASC";
				if (orderBy.contains("(" + MainActivity.context.getString(R.string.presetsManager_OrderAscDesc).split("\\|")[1] + ")"))
				{
						orderDirection = "DESC";
				}
				if (orderBy.contains(MainActivity.context.getString(R.string.presetsManager_OrderNames).split("\\|")[1]))
				{
						orderName = "_presetTimestamp";
				}
				else if (orderBy.contains(MainActivity.context.getString(R.string.presetsManager_OrderNames).split("\\|")[2]))
				{
						orderName = "_presetStars";
				}
				else if (orderBy.contains(MainActivity.context.getString(R.string.presetsManager_OrderNames).split("\\|")[3]))
				{
						orderName = "_presetCreator";
				}
				else if (orderBy.contains(MainActivity.context.getString(R.string.presetsManager_OrderNames).split("\\|")[4]))
				{
						orderName = "own";
				}

				try
				{
						HttpParams httpParams = new BasicHttpParams();
						HttpConnectionParams.setConnectionTimeout(httpParams,
																											MainActivity.TIMEOUT_MILLISEC);
						HttpConnectionParams.setSoTimeout(httpParams, MainActivity.TIMEOUT_MILLISEC);

						HttpParams p = new BasicHttpParams();
						p.setParameter("user", MainActivity.deviceUniqeId);

						HttpClient httpclient = new DefaultHttpClient(p);
						String url = "http://" + (MainActivity.LOCALTESTSERVER ? "127.0.0.1:8080" : "www.Neon-Soft.de") + "/page/NeoPowerMenu/phpWebservice/webservice1.php?action=presets&appversion=" + MainActivity.versionCode + "&format=json&userId=" + MainActivity.deviceUniqeId + "" + ((MainActivity.accountUniqeId.isEmpty() || MainActivity.accountUniqeId.equalsIgnoreCase("none")) ? "" : "&accountId=" + MainActivity.accountUniqeId) + "&sortBy=" + orderName + "&sortDir=" + orderDirection + (searchTerm.isEmpty() ? "" : "&searchFor=" + searchTerm) + (offset.isEmpty() ? "" : "&offset=" + offset);
						Log.i("NPM:getOnlinePresets", "Trying to fetch from " + url);
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
								if (responseBody.contains("Cannot connect to the DB"))
								{
										return "Request failed: Cannot connect to the DB";
								}
								JSONObject json = new JSONObject(responseBody);
								JSONArray jArray = json.getJSONArray("presets");
								ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();

								onlineTitles = new String[jArray.length()];
								onlineCreator = new String[jArray.length()];
								onlineEnabled = new String[jArray.length()];
								onlineLocal = new String[jArray.length()];

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
										map.put("CreatorId", jObject.getString("_creatorUniqeId"));

										mylist.add(map);
										onlineTitles[i] = jObject.getString("_presetName");
										onlineCreator[i] = jObject.getString("_presetCreator") + ",=," + jObject.getString("_presetAppVersion") + ",=," + jObject.getString("_presetStars");
										onlineEnabled[i] = "false";
										onlineLocal[i] = "false";
										PresetsPage.onlineIds.add(jObject.getString("_creatorUniqeId"));
										PreferencesPresetsFragment.OnlineHasGraphics.add(jObject.getString("_presetHasGraphics").equalsIgnoreCase("true"));
										
										PreferencesPresetsFragment.DownloadingActiveForRoot.add(null);
										PreferencesPresetsFragment.DownloadingActiveForHelper.add(null);
										PreferencesPresetsFragment.DownloadingActiveForLayout.add(null);
										PreferencesPresetsFragment.DownloadingActiveForImageView.add(null);
										PreferencesPresetsFragment.DownloadingActiveForOldText.add("");
										PreferencesPresetsFragment.DownloadingActiveForLabel.add(null);
										PreferencesPresetsFragment.DownloadingActiveForProgress.add(null);
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
						Log.e("NPM", result);
						if (PreferencesPresetsFragment.onlineMSGHolder.getVisibility() == View.VISIBLE)
						{
								PreferencesPresetsFragment.onlineMSGHolder.startAnimation(AnimationUtils.loadAnimation(PreferencesPresetsFragment.mContext, R.anim.fade_out));
								PreferencesPresetsFragment.onlineMSGHolder.setVisibility(View.GONE);
						}
						String errorTitle = "";
						if ((result.contains("Connection to") && result.contains("refused")) || result.contains("Unable to resolve host"))
						{
								errorTitle = PreferencesPresetsFragment.mContext.getString(R.string.presetsManager_CantConnecttoServer);
								if (refreshMode == MODE_ALL)
								{
										PreferencesPresetsFragment.onlineMSG.setText(PreferencesPresetsFragment.mContext.getString(R.string.presetsManager_CantConnecttoServer));
										PreferencesPresetsFragment.onlineMSGHolder.setVisibility(View.VISIBLE);
										PreferencesPresetsFragment.onlineMSGHolder.startAnimation(AnimationUtils.loadAnimation(PreferencesPresetsFragment.mContext, R.anim.fade_in));
								}
						}
						else if (result.contains("Cannot connect to the DB"))
						{
								errorTitle = PreferencesPresetsFragment.mContext.getString(R.string.presetsManager_CantConnecttoDB);
								if (refreshMode == MODE_ALL)
								{
										PreferencesPresetsFragment.onlineMSG.setText(PreferencesPresetsFragment.mContext.getString(R.string.presetsManager_CantConnecttoDB));
										PreferencesPresetsFragment.onlineMSGHolder.setVisibility(View.VISIBLE);
										PreferencesPresetsFragment.onlineMSGHolder.startAnimation(AnimationUtils.loadAnimation(PreferencesPresetsFragment.mContext, R.anim.fade_in));
								}
						}
						else
						{
								errorTitle = result;
								if (refreshMode == MODE_ALL)
								{
										slideDownDialogFragment dialogFragment = new slideDownDialogFragment(PreferencesPresetsFragment.mContext, MainActivity.fragmentManager);
										dialogFragment.setListener(new slideDownDialogFragment.slideDownDialogInterface() {

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
														public void onPositiveClick(Bundle resultBundle)
														{
																// TODO: Implement this method
														}

														@Override
														public void onTouchOutside()
														{
																// TODO: Implement this method
														}
												});
										dialogFragment.setText(result);
										dialogFragment.setPositiveButton(PreferencesPresetsFragment.mContext.getString(R.string.Dialog_Buttons).split("\\|")[0]);
										dialogFragment.showDialog(R.id.dialog_container);
										PreferencesPresetsFragment.onlineMSG.setText(result);
								}
						}
						if (refreshMode == MODE_OFFSET)
						{
								//PresetsPage.onlineIds.remove(PresetsPage.onlineIds.size()-1);
								PreferencesPresetsFragment.OnlineListTitles.set(PreferencesPresetsFragment.OnlineListTitles.size() - 1, PreferencesPresetsFragment.mContext.getString(R.string.presetsManager_LoadMore).split("\\|")[6]);
								PreferencesPresetsFragment.OnlineListDescs.set(PreferencesPresetsFragment.OnlineListDescs.size() - 1, errorTitle);
								//PreferencesPresetsFragment.OnlineListEnabled.set(PreferencesPresetsFragment.OnlineListEnabled.size()-1);
								PreferencesPresetsFragment.OnlineListLocal.set(PreferencesPresetsFragment.OnlineListLocal.size() - 1, "error");
								PreferencesPresetsFragment.onlineAdapter.notifyDataSetChanged();
						}
						//Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
				}
				else
				{
						//ArrayList<String> ListTitles = new ArrayList<String>(Arrays.asList(onlineTitles));
						//ArrayList<String> ListDescs = new ArrayList<String>(Arrays.asList(onlineCreator));
						//ArrayList<String> ListEnabled = new ArrayList<String>(Arrays.asList(onlineEnabled));
						//ArrayList<String> ListLocal = new ArrayList<String>(Arrays.asList(onlineLocal));

						if (refreshMode == MODE_OFFSET)
						{
								PresetsPage.onlineIds.remove(PresetsPage.onlineIds.size() - 1);
								PreferencesPresetsFragment.OnlineListTitles.remove(PreferencesPresetsFragment.OnlineListTitles.size() - 1);
								PreferencesPresetsFragment.OnlineListDescs.remove(PreferencesPresetsFragment.OnlineListDescs.size() - 1);
								PreferencesPresetsFragment.OnlineListEnabled.remove(PreferencesPresetsFragment.OnlineListEnabled.size() - 1);
								PreferencesPresetsFragment.OnlineListLocal.remove(PreferencesPresetsFragment.OnlineListLocal.size() - 1);
								PreferencesPresetsFragment.OnlineHasGraphics.remove(PreferencesPresetsFragment.OnlineHasGraphics.size() - 1);
						}

						PreferencesPresetsFragment.OnlineListTitles.addAll(Arrays.asList(onlineTitles));
						PreferencesPresetsFragment.OnlineListDescs.addAll(Arrays.asList(onlineCreator));
						PreferencesPresetsFragment.OnlineListEnabled.addAll(Arrays.asList(onlineEnabled));
						PreferencesPresetsFragment.OnlineListLocal.addAll(Arrays.asList(onlineLocal));

						if (onlineTitles.length == 0 && refreshMode == MODE_OFFSET)
						{
								PresetsPage.onlineIds.add("xyz");
								PreferencesPresetsFragment.OnlineListTitles.add(MainActivity.context.getString(R.string.presetsManager_LoadMore).split("\\|")[4]);
								PreferencesPresetsFragment.OnlineListDescs.add(MainActivity.context.getString(R.string.presetsManager_LoadMore).split("\\|")[5]);
								PreferencesPresetsFragment.OnlineListEnabled.add("false");
								PreferencesPresetsFragment.OnlineListLocal.add("NoMore");
								PreferencesPresetsFragment.OnlineHasGraphics.add(false);
						}

						if (onlineTitles.length == 30)
						{
								PresetsPage.onlineIds.add("xyz");
								PreferencesPresetsFragment.OnlineListTitles.add(MainActivity.context.getString(R.string.presetsManager_LoadMore).split("\\|")[2]);
								PreferencesPresetsFragment.OnlineListDescs.add(MainActivity.context.getString(R.string.presetsManager_LoadMore).split("\\|")[3]);
								PreferencesPresetsFragment.OnlineListEnabled.add("false");
								PreferencesPresetsFragment.OnlineListLocal.add("LoadMore");
								PreferencesPresetsFragment.OnlineHasGraphics.add(false);
						}

						PreferencesPresetsFragment.onlineAdapter.notifyDataSetChanged();
						if (PreferencesPresetsFragment.onlineMSGHolder.getVisibility() == View.VISIBLE)
						{
								PreferencesPresetsFragment.onlineMSGHolder.startAnimation(AnimationUtils.loadAnimation(PreferencesPresetsFragment.mContext, R.anim.fade_out));
								PreferencesPresetsFragment.onlineMSGHolder.setVisibility(View.GONE);
						}
						if (refreshMode == MODE_ALL)
						{
								PreferencesPresetsFragment.onlineList.setSelection(0);
								PreferencesPresetsFragment.onlineList.scrollTo(0, 0);
						}
						if (PreferencesPresetsFragment.onlineList.getVisibility() == View.GONE)
						{
								PreferencesPresetsFragment.onlineList.setVisibility(View.VISIBLE);
								PreferencesPresetsFragment.onlineList.startAnimation(AnimationUtils.loadAnimation(PreferencesPresetsFragment.mContext, R.anim.fade_in));
						}
				}
				PreferencesPresetsFragment.onlineRequestIsRunning = false;
		}

}

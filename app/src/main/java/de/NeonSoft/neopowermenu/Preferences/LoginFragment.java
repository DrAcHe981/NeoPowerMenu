package de.NeonSoft.neopowermenu.Preferences;

import android.app.*;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import de.NeonSoft.neopowermenu.*;
import de.NeonSoft.neopowermenu.helpers.*;
import java.io.*;

import android.support.v4.app.Fragment;
import android.view.animation.*;

public class LoginFragment extends Fragment
{

		static Activity mContext;
		public static String loginFragmentMode = "login";

		// LoginContainer
		static LinearLayout LinearLayout_LoginContainer;
		static EditText EditText_UsernameEmail, EditText_Password;
		static CheckBox CheckBox_KeepLogin;
		static LinearLayout LinearLayout_CreateAccount;

		// RegisterContainer
		static LinearLayout LinearLayout_RegisterContainer;
		static EditText EditText_Username, EditText_Email, EditText_RetypeEmail;

		// LoggedInContainer
		static LinearLayout LinearLayout_LoggedInContainer;
		static TextView TextView_TitleStatistics, TextView_Statistics, TextView_ReloadStatistics;

		public static OnClickListener loginOnClickListener, registerOnClickListener, logoutOnClickListener;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
				// TODO: Implement this method
				mContext = getActivity();

				//loginFragmentMode = "login";
				View InflatedView = inflater.inflate(R.layout.activity_login, container, false);

				// LoginContainer
				LinearLayout_LoginContainer = (LinearLayout) InflatedView.findViewById(R.id.activityloginLinearLayout_LoginContainer);
				LinearLayout_LoginContainer.setVisibility(View.VISIBLE);
				EditText_UsernameEmail = (EditText) InflatedView.findViewById(R.id.activityloginEditText_UsernameEmail);
				EditText_Password = (EditText) InflatedView.findViewById(R.id.activityloginEditText_Password);
				CheckBox_KeepLogin = (CheckBox) InflatedView.findViewById(R.id.activityloginCheckBox_KeepLogin);
				LinearLayout_CreateAccount = (LinearLayout) InflatedView.findViewById(R.id.activityloginLinearLayout_CreateAccount);

				// RegisterContainer
				LinearLayout_RegisterContainer = (LinearLayout) InflatedView.findViewById(R.id.activityloginLinearLayout_RegisterContainer);
				LinearLayout_RegisterContainer.setVisibility(View.GONE);
				EditText_Username = (EditText) InflatedView.findViewById(R.id.activityloginEditText_Username);
				EditText_Email = (EditText) InflatedView.findViewById(R.id.activityloginEditText_Email);
				EditText_RetypeEmail = (EditText) InflatedView.findViewById(R.id.activityloginEditText_RetypeEmail);

				// LoggedInContainer
				LinearLayout_LoggedInContainer = (LinearLayout) InflatedView.findViewById(R.id.activityloginLinearLayout_LoggedInContainer);
				LinearLayout_LoggedInContainer.setVisibility(View.GONE);
				TextView_TitleStatistics = (TextView) InflatedView.findViewById(R.id.activityloginTextView_TitleStatistics);
				TextView_Statistics = (TextView) InflatedView.findViewById(R.id.activityloginTextView_Statistics);
				TextView_ReloadStatistics = (TextView) InflatedView.findViewById(R.id.activityloginTextView_ReloadStatistics);
				
				loginOnClickListener = new OnClickListener() {

						@Override
						public void onClick(View p1)
						{
								// TODO: Implement this method
								if (EditText_UsernameEmail.getText().toString().isEmpty() || EditText_Password.getText().toString().isEmpty())
								{
										Toast.makeText(mContext, getString(R.string.login_pleaseCheckInput), Toast.LENGTH_SHORT).show();
										return;
								}
								if (EditText_UsernameEmail.getText().toString().contains("@"))
								{
										if (!helper.isValidEmail(EditText_UsernameEmail.getText().toString()))
										{
												Toast.makeText(mContext, getString(R.string.login_pleaseCheckInput), Toast.LENGTH_SHORT).show();
												return;
										}
								}
								uploadHelper uH = new uploadHelper(getActivity(), new uploadHelper.uploadHelperInterface() {

												@Override
												public void onUploadStarted(boolean state)
												{
														// TODO: Implement this method
														PreferencesPresetsFragment.LoadingMsg.setText(getString(R.string.login_Processing));
														PreferencesPresetsFragment.progressHolder.setVisibility(View.VISIBLE);
														PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
												}

												@Override
												public void onPublishUploadProgress(long nowSize, long totalSize)
												{
														// TODO: Implement this method
												}

												@Override
												public void onUploadComplete()
												{
														// TODO: Implement this method
														PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
														PreferencesPresetsFragment.progressHolder.setVisibility(View.GONE);
														Toast.makeText(mContext, getString(R.string.login_LoggedIn), Toast.LENGTH_SHORT).show();
														MainActivity.loggedIn = true;
														MainActivity.usernameemail = EditText_UsernameEmail.getText().toString();
														MainActivity.password = helper.md5Crypto(EditText_Password.getText().toString());
														if (CheckBox_KeepLogin.isChecked())
														{
																MainActivity.preferences.edit().putBoolean("autoLogin", true)
																		.putString("ueel", MainActivity.usernameemail)
																		.putString("pd", MainActivity.password).commit();
														}
														LinearLayout_LoginContainer.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
														LinearLayout_LoginContainer.setVisibility(View.GONE);
														TextView_TitleStatistics.setText(getString(R.string.login_TitleStatistics).replace("[USERNAMEEMAIL]", MainActivity.usernameemail));
														getStatistics();
														LinearLayout_LoggedInContainer.setVisibility(View.VISIBLE);
														LinearLayout_LoggedInContainer.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
														MainActivity.setActionBarButtonText(getString(R.string.login_TitleLogout));
														MainActivity.setActionBarButtonListener(logoutOnClickListener);
												}

												@Override
												public void onUploadFailed(String reason)
												{
														// TODO: Implement this method
														PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
														PreferencesPresetsFragment.progressHolder.setVisibility(View.GONE);
														if (reason.contains("user with this data not found"))
														{
																Toast.makeText(mContext, getString(R.string.login_LoginFailedWrongData), Toast.LENGTH_LONG).show();
														}
														else if (reason.contains("Cannot connect to the DB"))
														{
																Toast.makeText(mContext, getString(R.string.presetsManager_CantConnecttoServer), Toast.LENGTH_LONG).show();
														}
														else if (reason.contains("Connection refused"))
														{
																Toast.makeText(mContext, getString(R.string.presetsManager_CantConnecttoServer), Toast.LENGTH_LONG).show();
														}
														else
														{
																Toast.makeText(mContext, getString(R.string.login_LoginFailedWithReason)+"\n" + reason, Toast.LENGTH_LONG).show();
														}
												}
										});
								uH.setServerUrl("http://" + (MainActivity.LOCALTESTSERVER ? "127.0.0.1:8080" : "www.Neon-Soft.de") + "/page/NeoPowerMenu/phpWebservice/webservice3.php");
								uH.setAdditionalUploadPosts(new String[][] {{"action","login"},{(EditText_UsernameEmail.getText().toString().contains("@") ? "email" : "name"),EditText_UsernameEmail.getText().toString()},{"password",helper.md5Crypto(EditText_Password.getText().toString())}});
								try
								{
										new File(mContext.getFilesDir().getPath() + "/tmp").createNewFile();
								}
								catch (IOException e)
								{}
								uH.setLocalUrl(mContext.getFilesDir().getPath() + "/tmp");
								uH.startUpload();
						}
				};

				registerOnClickListener = new OnClickListener() {

						@Override
						public void onClick(View p1)
						{
								// TODO: Implement this method
								if (EditText_Username.getText().toString().isEmpty() || (!helper.isValidEmail(EditText_Email.getText().toString()) || !helper.isValidEmail(EditText_RetypeEmail.getText().toString())) || !EditText_Email.getText().toString().equals(EditText_RetypeEmail.getText().toString()))
								{
										Toast.makeText(getActivity(), getString(R.string.login_pleaseCheckInput), Toast.LENGTH_SHORT).show();
										return;
								}
								uploadHelper uH = new uploadHelper(getActivity(), new uploadHelper.uploadHelperInterface() {

												@Override
												public void onUploadStarted(boolean state)
												{
														// TODO: Implement this method
														PreferencesPresetsFragment.LoadingMsg.setText(getString(R.string.login_Processing));
														PreferencesPresetsFragment.progressHolder.setVisibility(View.VISIBLE);
														PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
												}

												@Override
												public void onPublishUploadProgress(long nowSize, long totalSize)
												{
														// TODO: Implement this method
												}

												@Override
												public void onUploadComplete()
												{
														// TODO: Implement this method
														PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
														PreferencesPresetsFragment.progressHolder.setVisibility(View.GONE);
														Toast.makeText(mContext, getString(R.string.login_RegisterSuccess), Toast.LENGTH_SHORT).show();
														LinearLayout_RegisterContainer.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
														LinearLayout_RegisterContainer.setVisibility(View.GONE);
														EditText_UsernameEmail.setText(EditText_Email.getText().toString());
														LinearLayout_LoginContainer.setVisibility(View.VISIBLE);
														LinearLayout_LoginContainer.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
														MainActivity.setActionBarButtonText(getString(R.string.login_Title));
														MainActivity.setActionBarButtonListener(loginOnClickListener);
														loginFragmentMode = "login";
												}

												@Override
												public void onUploadFailed(String reason)
												{
														// TODO: Implement this method
														PreferencesPresetsFragment.progressHolder.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
														PreferencesPresetsFragment.progressHolder.setVisibility(View.GONE);
														if (reason.contains("this username or email is already in use"))
														{
																Toast.makeText(mContext, getString(R.string.login_RegisterFailedNameUsed), Toast.LENGTH_LONG).show();
														}
														else if (reason.contains("Cannot connect to the DB"))
														{
																Toast.makeText(mContext, getString(R.string.presetsManager_CantConnecttoServer), Toast.LENGTH_LONG).show();
														}
														else if (reason.contains("Connection refused"))
														{
																Toast.makeText(mContext, getString(R.string.presetsManager_CantConnecttoServer), Toast.LENGTH_LONG).show();
														}
														else
														{
																Toast.makeText(mContext, "Register failed:\n" + reason, Toast.LENGTH_LONG).show();
														}
												}
										});
								uH.setServerUrl("http://" + (MainActivity.LOCALTESTSERVER ? "127.0.0.1:8080" : "www.Neon-Soft.de") + "/page/NeoPowerMenu/phpWebservice/webservice3.php");
								uH.setAdditionalUploadPosts(new String[][] {{"action","register"},{"name",EditText_Username.getText().toString()},{"email",EditText_Email.getText().toString()}});
								try
								{
										new File(mContext.getFilesDir().getPath() + "/tmp").createNewFile();
								}
								catch (IOException e)
								{}
								uH.setLocalUrl(mContext.getFilesDir().getPath() + "/tmp");
								uH.startUpload();
						}
				};

				logoutOnClickListener = new OnClickListener() {

						@Override
						public void onClick(View p1)
						{
								// TODO: Implement this method
								MainActivity.loggedIn = false;
								MainActivity.usernameemail = "null";
								MainActivity.password = "null";
								MainActivity.preferences.edit().putBoolean("autoLogin", false)
										.remove("ueel")
										.remove("pd").commit();
								LinearLayout_LoggedInContainer.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
								LinearLayout_LoggedInContainer.setVisibility(View.GONE);
								LinearLayout_LoginContainer.setVisibility(View.VISIBLE);
								LinearLayout_LoginContainer.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
								MainActivity.setActionBarButtonText(getString(R.string.login_Title));
								MainActivity.setActionBarButtonListener(loginOnClickListener);
								loginFragmentMode = "login";
						}
				};

				if (MainActivity.loggedIn)
				{
						loginFragmentMode = "logout";
						LinearLayout_LoginContainer.setVisibility(View.GONE);
						TextView_TitleStatistics.setText(getString(R.string.login_TitleStatistics).replace("[USERNAMEEMAIL]", MainActivity.usernameemail));
						getStatistics();
						LinearLayout_LoggedInContainer.setVisibility(View.VISIBLE);
						//MainActivity.setActionBarButton(getString(R.string.login_TitleLogout), R.drawable.ic_content_send, logoutOnClickListener);
				}
				else
				{
						loginFragmentMode = "login";
						//MainActivity.setActionBarButton(getString(R.string.login_Title), R.drawable.ic_content_send, loginOnClickListener);
				}
				
				if(MainActivity.preferences.getBoolean("autoLogin",false)) {
						EditText_UsernameEmail.setText(MainActivity.preferences.getString("ueel","null"));
						CheckBox_KeepLogin.setChecked(true);
				}

				LinearLayout_CreateAccount.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										loginFragmentMode = "register";
										MainActivity.setActionBarButtonText(getString(R.string.login_TitleRegister));
										MainActivity.setActionBarButtonListener(registerOnClickListener);
										LinearLayout_LoginContainer.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
										LinearLayout_LoginContainer.setVisibility(View.GONE);
										if (!EditText_UsernameEmail.getText().toString().isEmpty() && EditText_UsernameEmail.getText().toString().contains("@"))
										{
												EditText_Email.setText(EditText_UsernameEmail.getText().toString());
										}
										LinearLayout_RegisterContainer.setVisibility(View.VISIBLE);
										LinearLayout_RegisterContainer.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
								}
						});
						
				TextView_ReloadStatistics.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View p1)
								{
										// TODO: Implement this method
										getStatistics();
								}
						});

				return InflatedView;
		}

		public void getStatistics() {
				uploadHelper uH = new uploadHelper(mContext, new uploadHelper.uploadHelperInterface() {

								@Override
								public void onUploadStarted(boolean state)
								{
										// TODO: Implement this method
										TextView_Statistics.setText(getString(R.string.login_Processing));
								}

								@Override
								public void onPublishUploadProgress(long nowSize, long totalSize)
								{
										// TODO: Implement this method
								}

								@Override
								public void onUploadComplete()
								{
										// TODO: Implement this method
								}

								@Override
								public void onUploadFailed(String reason)
								{
										// TODO: Implement this method
										if(reason.contains("Statistics:")) {
												String[] stats = reason.split(",");
												TextView_Statistics.setText(getString(R.string.login_Statistics).replace("[UPLOADCOUNT]",stats[1]).replace("[STARSGIVEN]",stats[2]).replace("[STARSRECEIVED]",stats[3]).replace("[TOP5PRESETS]",stats[4]));
										} else {
												TextView_Statistics.setText(getString(R.string.login_StatisticsFailed));
										}
								}
						});
				uH.setServerUrl("http://" + (MainActivity.LOCALTESTSERVER ? "127.0.0.1:8080" : "www.Neon-Soft.de") + "/page/NeoPowerMenu/phpWebservice/webservice3.php");
				uH.setAdditionalUploadPosts(new String[][] {{"action","statistics"},{(MainActivity.usernameemail.contains("@") ? "email" : "name"),MainActivity.usernameemail},{"id",MainActivity.preferences.getString("userUniqeId","null")}});
				try
				{
						new File(mContext.getFilesDir().getPath() + "/tmp").createNewFile();
				}
				catch (IOException e)
				{}
				uH.setLocalUrl(mContext.getFilesDir().getPath() + "/tmp");
				uH.startUpload();
		}
		
		public static void returnToLogin()
		{
				loginFragmentMode = "login";
				MainActivity.setActionBarButtonText(mContext.getString(R.string.login_Title));
				MainActivity.setActionBarButtonListener(loginOnClickListener);
				LinearLayout_RegisterContainer.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
				LinearLayout_RegisterContainer.setVisibility(View.GONE);
				if (!EditText_Email.getText().toString().isEmpty())
				{
						EditText_UsernameEmail.setText(EditText_Email.getText().toString());
				}
				LinearLayout_LoginContainer.setVisibility(View.VISIBLE);
				LinearLayout_LoginContainer.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
		}

}

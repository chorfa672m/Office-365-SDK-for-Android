/*******************************************************************************
 * Copyright (c) Microsoft Open Technologies, Inc.
 * All Rights Reserved
 * See License.txt in the project root for license information. 
 ******************************************************************************/
package com.microsoft.assetmanagement;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.microsoft.assetmanagement.files.SharepointListsClientWithFiles;
import com.microsoft.office365.Action;
import com.microsoft.office365.Credentials;
import com.microsoft.office365.LogLevel;
import com.microsoft.office365.Logger;
import com.microsoft.office365.OfficeFuture;
import com.microsoft.office365.http.BasicAuthenticationCredentials;
import com.microsoft.office365.http.CookieCredentials;
import com.microsoft.office365.http.SharepointCookieCredentials;
import com.microsoft.office365.lists.SharepointListsClient;

public class AssetApplication extends Application {

	private static Context appContext;
	private AssetPreferences mPreferences;
	private Credentials mCredentials;
	private SharepointListsClient mSharepointListsClient;

	@Override
	public void onCreate() {

		Log.d("Asset Management", "onCreate");
		super.onCreate();
		AssetApplication.appContext = getApplicationContext();

		mPreferences = new AssetPreferences(appContext,
				PreferenceManager.getDefaultSharedPreferences(this));
	}

	public Credentials getCredentials() {
		return mCredentials;
	}

	public void setCredentials(Credentials credentials) {
		mCredentials = credentials;
	}

	public void handleError(Throwable throwable) {
		Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
		Log.e("Asset", throwable.toString());
	}

	public OfficeFuture<Credentials> authenticate(Activity activity) {
		final OfficeFuture<Credentials> result = new OfficeFuture<Credentials>();

		String method = mPreferences.getAuthenticationMethod();
		if (method.equals("COOKIES")) {
			OfficeFuture<CookieCredentials> future = SharepointCookieCredentials
					.requestCredentials(mPreferences.getSharepointServer(), activity);

			future.done(new Action<CookieCredentials>() {

				@Override
				public void run(CookieCredentials credentials) throws Exception {
					mCredentials = credentials;
					result.setResult(credentials);
				}
			});

		} else {
			String userName = mPreferences.getNTLMUser();
			String password = mPreferences.getNTLMPassword();
			mCredentials = new BasicAuthenticationCredentials(userName, password);
			result.setResult(mCredentials);
		}
		return result;
	}

	public boolean hasConfigurationSettings() {

		String authenticationMethod = mPreferences.getAuthenticationMethod();
		if (isNullOrEmpty(authenticationMethod))
			return false;

		if (isNullOrEmpty(mPreferences.getLibraryName()))
			return false;
		if (authenticationMethod.equals("NTLM")) {
			String server = mPreferences.getSharepointServer();
			String username = mPreferences.getNTLMUser();
			String password = mPreferences.getNTLMPassword();

			boolean result = (!isNullOrEmpty(server)) && (!isNullOrEmpty(username))
					&& (!isNullOrEmpty(password));
			return result;
		} else if (authenticationMethod.equals("COOKIES")) {
			return (!isNullOrEmpty(mPreferences.getSharepointServer()) && (!isNullOrEmpty(mPreferences
					.getSiteRelativeUrl())));
		} else {
			String authorityUrl = mPreferences.getAuthorityUrl();
			String clientId = mPreferences.getClientId();
			String resourceUrl = mPreferences.getResourceUrl();
			String userHint = mPreferences.getUserHint();
			boolean result = (!isNullOrEmpty(authorityUrl)) && (!isNullOrEmpty(clientId))
					&& (!isNullOrEmpty(resourceUrl)) && (!isNullOrEmpty(userHint));
			return result;
		}
	}

	private boolean isNullOrEmpty(String value) {

		return value == null || value.length() == 0;
	}

	public Boolean storeSiteUrl(String url) {
		mPreferences.storeSharepointListUrl(url);
		return true;
	}

	public ArrayList<String> getStoredLists() {
		return mPreferences.getSharepointListNames();
	}

	public boolean hasDefaultList() {
		return mPreferences.getLibraryName() != null;
	}

	public AssetPreferences getPreferences() {
		return mPreferences;
	}

	public void clearPreferences() {
		// mPreferences.clear();
		CookieSyncManager syncManager = CookieSyncManager.createInstance(this);
		if (syncManager != null) {
			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.removeAllCookie();
		}
	}

	public SharepointListsClient getCurrentListClient() {
		String serverUrl = mPreferences.getSharepointServer();
		String siteRelativeUrl = mPreferences.getSiteRelativeUrl();
		Credentials credentials = getCredentials();
		mSharepointListsClient = new SharepointListsClientWithFiles(serverUrl, siteRelativeUrl,
				credentials, new Logger() {

					@Override
					public void log(String message, LogLevel level) {
						Log.d("Asset", message);
					}
				});
		return mSharepointListsClient;
	}

	public String getAccountInfo() {
		SharepointListsClient client = getCurrentListClient();
		try {
			return client.getUserProperties().get();
		} catch (Throwable t) {
			Log.d("Asset", t.getMessage());
		}
		return "";
	}
}

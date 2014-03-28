package com.mbrite.blobphoto.common;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.app.*;
import android.content.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.message.BasicNameValuePair;
import org.json.*;

import java.util.*;

import com.mbrite.blobphoto.connection.*;
/**
 * Utility class
 */
public class Utils {
    public static boolean isValidUsernameAndPassword(String username, String password, Activity activity)
            throws URISyntaxException, JSONException, IOException {
        if (TextUtils.isEmpty(password)) {
            return false;
        }

        List<BasicNameValuePair> payload = new ArrayList<BasicNameValuePair>(2);
        payload.add(new BasicNameValuePair(Constants.USER_NAME, username));
        payload.add(new BasicNameValuePair(Constants.PASSWORD, password));
        HttpResponse response = RestClient.INSTANCE.post(activity, Constants.LOGIN, payload);
        int statusCode = response.getStatusLine().getStatusCode();
        switch (statusCode) {
            case 200:
                RestClient.INSTANCE.setCookie(response);
                break;
            default:
                throw new HttpResponseException(statusCode, "Error occurred for Login request");
        }
        return true;
    }

    public static void savedUsernameAndPassword(Activity activity, String username, String password) {
        SharedPreferences sharedPref = activity.getSharedPreferences(Constants.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor
                .putString(Constants.USER_NAME, username)
                .putString(Constants.PASSWORD, password);
        editor.commit();
    }

    public static void clearUsernameAndPassword(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(Constants.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor
                .remove(Constants.USER_NAME)
                .remove(Constants.PASSWORD);
        editor.commit();
    }

    /**
     * @return String array of two elements representing username and password.
     * If either username or password is not found, null is returned.
     */
    public static String[] getSavedUsernameAndPassword(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(Constants.PREFERENCE_FILE_KEY,Context.MODE_PRIVATE);
        String username = sharedPref.getString(Constants.USER_NAME, null);
        String password = sharedPref.getString(Constants.PASSWORD, null);
        if (username == null || password == null) {
            return null;
        }

        return new String[] { username, password };
    }

    public static String getSiteURI(Activity activity) {
        return PreferenceManager
                .getDefaultSharedPreferences(activity)
                .getString(Constants.SITE_URL, "");
    }

    public static String convertStreamToString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            char[] buf = new char[1024];
            int numRead = 0;

            while((numRead=reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                sb.append(readData);
            }
        } finally {
            if (is != null) {
                is.close();
            }
        }

        return sb.toString();
    }
}

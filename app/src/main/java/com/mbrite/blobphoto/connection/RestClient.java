package com.mbrite.blobphoto.connection;

import android.app.Activity;
import android.text.TextUtils;
import android.os.*;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.entity.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.net.*;
import java.util.*;

import com.mbrite.blobphoto.common.*;
import com.mbrite.blobphoto.app.*;

public enum RestClient {

    INSTANCE;

    private URI siteURI;

    private String cookie;

    {
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private URI getSiteURI(Activity activity)
            throws URISyntaxException {
        if (this.siteURI != null) {
            return this.siteURI;
        }

        String site = Utils.getSiteURI(activity);
        if (TextUtils.isEmpty(site)) {
            throw new IllegalStateException(activity.getString(R.string.error_site_url_missing));
        }
        this.siteURI = new URI(site);
        return this.siteURI;
    }

    public HttpResponse get(String relativeURI)
            throws IOException {
        return get(relativeURI, null);
    }

    public HttpResponse get(String relativeURI, Map<String, String> headers)
            throws IOException {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(this.siteURI.resolve(relativeURI));
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                request.setHeader(header.getKey(), header.getValue());
            }
        }
        if (cookie != null) {
            request.addHeader(Constants.COOKIE, cookie);
        }
        return client.execute(request);
    }

    public HttpResponse post(Activity activity, String relativeURI, List<BasicNameValuePair> payload)
            throws IOException, URISyntaxException {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(getSiteURI(activity).resolve(relativeURI));
        post.setEntity(new UrlEncodedFormEntity(payload));
        if (cookie != null) {
            post.addHeader(Constants.COOKIE, cookie);
        }
        return client.execute(post);
    }

    public void setCookie(HttpResponse response) {
        for (Header header : response.getAllHeaders()) {
            if (header.getName().equalsIgnoreCase(Constants.COOKIES_HEADER)) {
                cookie = header.getValue();
                return;
            }
        }
    }
}


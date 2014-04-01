package com.mbrite.blobphoto.connection;

import android.app.Activity;
import android.text.TextUtils;
import android.os.*;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.entity.*;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

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

    public URI getSiteURI(Activity activity)
            throws URISyntaxException {
        if (this.siteURI != null) {
            return this.siteURI;
        }

        String site = Utils.getSiteURI(activity);
        if (TextUtils.isEmpty(site)) {
            site = "http://photobox.cfapps.io";
        }
        this.siteURI = new URI(site);
        return this.siteURI;
    }

    public HttpResponse get(Activity activity, String relativeURI)
            throws IOException, URISyntaxException {
        return get(activity, relativeURI, null);
    }

    public HttpResponse get(Activity activity, String relativeURI, Map<String, String> headers)
            throws IOException, URISyntaxException {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(getSiteURI(activity).resolve(relativeURI));
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

    public HttpResponse post(Activity activity, String relativeURI, MultipartEntity entity)
            throws IOException, URISyntaxException {
        HttpContext localContext = new BasicHttpContext();
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(getSiteURI(activity).resolve(relativeURI));
        post.setEntity(entity);
        if (cookie != null) {
            post.addHeader(Constants.COOKIE, cookie);
        }
        return client.execute(post);
    }

    public void setCookie(HttpResponse response) {
        for (Header header : response.getAllHeaders()) {
            if (header.getName().equalsIgnoreCase(Constants.COOKIES_HEADER)) {
                cookie = header.getValue();
            }
        }
    }
}


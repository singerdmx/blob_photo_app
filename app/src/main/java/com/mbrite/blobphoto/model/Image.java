package com.mbrite.blobphoto.model;

import android.app.Activity;

import com.mbrite.blobphoto.common.Constants;
import com.mbrite.blobphoto.connection.RestClient;

import java.net.URISyntaxException;

public class Image {
    final public int id;
    final public String url;
    final public String description;

    public Image(Activity activity, int id, String description)
        throws URISyntaxException {
        this.id = id;
        String relativeURI = String.format("%s/%d?thumb=true", Constants.IMAGE, id);
        this.url = RestClient.INSTANCE.getSiteURI(activity).resolve(relativeURI).toString();
        this.description = description;
    }
}

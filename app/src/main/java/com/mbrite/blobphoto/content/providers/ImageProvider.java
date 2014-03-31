package com.mbrite.blobphoto.content.providers;

import android.app.Activity;
import android.os.StrictMode;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import com.mbrite.blobphoto.common.Constants;
import com.mbrite.blobphoto.common.Utils;
import com.mbrite.blobphoto.connection.RestClient;
import com.mbrite.blobphoto.model.*;

public class ImageProvider {

    {
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private Activity activity;

    public ImageProvider(Activity activity) {
        this.activity = activity;
    }

    public ArrayList<Image> getImages()
            throws JSONException, IOException, URISyntaxException {
        ArrayList<Image> images = new ArrayList<Image>();
        HttpResponse response = RestClient.INSTANCE.get(activity, Constants.IMAGES);
        String data = Utils.convertStreamToString(response.getEntity().getContent());
        JSONObject imagesJSON = new JSONObject(data);
        Iterator iter = imagesJSON.keys();
        while (iter.hasNext()) {
            String key = iter.next().toString();
            int id = Integer.parseInt(key);
            String description = imagesJSON.getJSONObject(key).getString(Constants.DESC);
            images.add(new Image(activity, id, description));
        }

        return images;
    }
}


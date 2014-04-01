package com.mbrite.blobphoto.app;

import android.app.*;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.mbrite.blobphoto.content.providers.*;
import com.mbrite.blobphoto.model.*;
import com.mbrite.blobphoto.widget.ImageAdapter;

import org.json.JSONException;

import java.util.*;

public class MainFragment extends ListFragment {
    private static final String TAG = MainFragment.class.getSimpleName();

    private ImageProvider imageProvider;
    private ArrayList<Image> images = new ArrayList<Image>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            this.imageProvider = new ImageProvider(getActivity());
            this.images = imageProvider.getImages();
        }catch (JSONException ex) {
            Toast.makeText(
                    getActivity(),
                    String.format("JSONException: %s", ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        } catch (Exception ex) {
            Toast.makeText(
                    getActivity(),
                    String.format("Error: %s", ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        }

        ImageAdapter adapter = new ImageAdapter(
                getActivity(),
                images);
        setListAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            this.imageProvider = new ImageProvider(getActivity());
            this.images = imageProvider.getImages();
        }catch (JSONException ex) {
            Toast.makeText(
                    getActivity(),
                    String.format("JSONException: %s", ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        } catch (Exception ex) {
            Toast.makeText(
                    getActivity(),
                    String.format("Error: %s", ex.getLocalizedMessage()),
                    Toast.LENGTH_LONG)
                    .show();
        }

        ImageAdapter adapter = new ImageAdapter(
                getActivity(),
                images);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, final int position, long id) {
        Log.d(TAG, "ROW ID: " + id);
        final Image image = (Image) getListAdapter().getItem(position);
    }
}


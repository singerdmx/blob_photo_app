package com.mbrite.blobphoto.widget;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.mbrite.blobphoto.app.*;
import com.mbrite.blobphoto.model.*;

import com.nostra13.universalimageloader.core.*;

import java.util.*;

public class ImageAdapter extends ArrayAdapter<Image> {

    private final Context context;
    private final ArrayList<Image> itemsArrayList;

    public ImageAdapter(Context context, ArrayList<Image> itemsArrayList) {

        super(context, R.layout.activity_list_item_image, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Get rowView from inflater
        View rowView = inflater.inflate(R.layout.activity_list_item_image, parent, false);

        if (position % 2 == 0){
            rowView.setBackgroundResource(R.drawable.alterselector1);
        } else {
            rowView.setBackgroundResource(R.drawable.alterselector2);
        }

        Image image = itemsArrayList.get(position);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.image);
        ImageLoader.getInstance().displayImage(image.url, imageView);
        TextView textView = (TextView) rowView.findViewById(R.id.description);
        textView.setText(image.description);
        return rowView;
    }
}



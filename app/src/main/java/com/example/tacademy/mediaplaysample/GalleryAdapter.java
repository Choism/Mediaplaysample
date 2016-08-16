package com.example.tacademy.mediaplaysample;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016-08-16.
 */
public class GalleryAdapter extends ArrayAdapter<Bitmap> {
    public GalleryAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(parent.getContext());
            imageView.setLayoutParams(new Gallery.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        } else {
            imageView = (ImageView)convertView;
        }
        imageView.setImageBitmap(getItem(position));
        return imageView;
    }
}

package com.emotionalgoods.flickrinstantapp.feature.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.emotionalgoods.flickrinstantapp.feature.R;

/**
 * Created by jacob on 11/16/17.
 */

public class PhotoViewHolder extends RecyclerView.ViewHolder {

    private ImageView photo;
    private TextView title;

    public PhotoViewHolder(@NonNull View view) {
        super(view);
        photo = view.findViewById(R.id.photo);
        title = view.findViewById(R.id.title);
    }

    public ImageView getPhoto() {
        return photo;
    }

    public TextView getTitle() {
        return title;
    }
}

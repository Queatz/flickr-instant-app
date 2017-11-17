package com.emotionalgoods.flickrinstantapp.feature.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.emotionalgoods.flickrinstantapp.feature.R;
import com.emotionalgoods.flickrinstantapp.feature.api.PhotoModel;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jacob on 11/16/17.
 */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoViewHolder> {

    private final List<PhotoModel> photos;
    private Context context;
    private int orientation;

    public PhotoAdapter(@NonNull List<PhotoModel> photos) {
        this.photos = photos;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        return new PhotoViewHolder(LayoutInflater.from(
                context).inflate(orientation == GridLayoutManager.HORIZONTAL ?
                        R.layout.photo_grid_item_horizontal :
                        R.layout.photo_grid_item,
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        PhotoModel photo = photos.get(position);

        Picasso.with(context).cancelRequest(holder.getPhoto());
        Picasso.with(context)
                .load(photo.getUrl())
                .placeholder(R.color.placeholder)
                .into(holder.getPhoto());

        holder.getTitle().setText(photo.getTitle());
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public int getOrientation() {
        return orientation;
    }

    public PhotoAdapter setOrientation(int orientation) {
        this.orientation = orientation;
        return this;
    }
}

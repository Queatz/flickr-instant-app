package com.emotionalgoods.flickrinstantapp.feature.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emotionalgoods.flickrinstantapp.feature.R;
import com.emotionalgoods.flickrinstantapp.feature.api.PhotoModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by jacob on 11/16/17.
 */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoViewHolder> {

    private final List<PhotoModel> photos;
    private Context context;
    private int orientation;
    private PublishSubject<PhotoModel> photoClicked;

    public PhotoAdapter(@NonNull List<PhotoModel> photos) {
        this.photos = photos;
        photoClicked = PublishSubject.create();
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
    public void onBindViewHolder(final PhotoViewHolder holder, int position) {
        final PhotoModel photo = photos.get(position);

        Picasso.with(context).cancelRequest(holder.getPhoto());
        Picasso.with(context)
                .load(photo.getUrl())
                .placeholder(R.color.placeholder)
                .into(holder.getPhoto());

        holder.getTitle().setText(photo.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoClicked.onNext(photo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public Observable<PhotoModel> onPhotoClicked() {
        return photoClicked;
    }

    public int getOrientation() {
        return orientation;
    }

    public PhotoAdapter setOrientation(int orientation) {
        this.orientation = orientation;
        return this;
    }
}

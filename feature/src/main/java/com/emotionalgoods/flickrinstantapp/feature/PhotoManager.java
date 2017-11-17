package com.emotionalgoods.flickrinstantapp.feature;

import android.content.Context;

import com.emotionalgoods.flickrinstantapp.feature.api.FlickrApi;
import com.emotionalgoods.flickrinstantapp.feature.api.PhotoModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by jacob on 11/16/17.
 */

public class PhotoManager {
    private final FlickrApi flickrApi;
    private final List<PhotoModel> photoList;
    private final PublishSubject<Integer> photosAddedPublisher;
    private final Context context;

    private String searchTerm;
    private int page;
    private boolean loading;

    public PhotoManager(Context context) {
        this.context = context;
        page = 1;
        flickrApi = new FlickrApi();
        photoList = new ArrayList<>();
        photosAddedPublisher = PublishSubject.create();
    }

    public void loadMorePhotos() {
        if (loading) {
            return;
        }

        loading = true;

        flickrApi.searchPhotos(context.getString(R.string.flickrApiKey), searchTerm, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<PhotoModel>>() {
                    @Override
                    public void accept(List<PhotoModel> photos) throws Exception {
                        page++;
                        loading = false;

                        PhotoManager.this.photoList.addAll(photos);
                        photosAddedPublisher.onNext(page);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        loading = false;
                    }
                });
    }

    public Observable<Integer> onPhotosAdded() {
        return photosAddedPublisher;
    }

    public List<PhotoModel> getPhotoList() {
        return photoList;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public PhotoManager setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
        return this;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public void reset() {
        photoList.clear();
        page = 1;
        photosAddedPublisher.onNext(page);
    }
}

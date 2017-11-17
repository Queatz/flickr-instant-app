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

    public PhotoManager(Context context) {
        this.context = context;
        page = 0;
        flickrApi = new FlickrApi();
        photoList = new ArrayList<>();
        photosAddedPublisher = PublishSubject.create();
    }

    public void loadMorePhotos() {
        flickrApi.searchPhotos(context.getString(R.string.flickrApiKey), searchTerm)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<PhotoModel>>() {
                    @Override
                    public void accept(List<PhotoModel> photos) throws Exception {
                        page++;

                        PhotoManager.this.photoList.addAll(photos);
                        photosAddedPublisher.onNext(page);
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
}

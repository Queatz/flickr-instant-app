package com.emotionalgoods.flickrinstantapp.feature.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.emotionalgoods.flickrinstantapp.feature.PhotoManager;
import com.emotionalgoods.flickrinstantapp.feature.R;
import com.emotionalgoods.flickrinstantapp.feature.api.PhotoModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    private static final int PHOTO_GRID_COLUMNS = 2;

    private RecyclerView photoGridView;
    private PhotoManager photoManager;
    private PhotoAdapter photoAdapter;
    private ProgressBar progressBar;
    private Disposable onPhotosAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        photoManager = new PhotoManager(this);

        setContentView(R.layout.activity_main);
        photoGridView = findViewById(R.id.photoGridView);
        progressBar = findViewById(R.id.progressBar);

        photoAdapter = new PhotoAdapter(photoManager.getPhotoList());
        photoGridView.setAdapter(photoAdapter);

        photoGridView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (photoManager.isLoading() || photoManager.getPhotoList().isEmpty()) {
                    return;
                }

                int offset = isHorizontal() ?
                        recyclerView.computeHorizontalScrollOffset() :
                        recyclerView.computeVerticalScrollOffset();

                int extent = isHorizontal() ?
                        recyclerView.computeHorizontalScrollExtent() :
                        recyclerView.computeVerticalScrollExtent();

                int range = isHorizontal() ?
                        recyclerView.computeHorizontalScrollRange() :
                        recyclerView.computeVerticalScrollRange();

                if (offset + extent * 2 >= range) {
                    photoManager.loadMorePhotos();
                }
            }
        });

        photoAdapter.onPhotoClicked().subscribe(new Consumer<PhotoModel>() {
            @Override
            public void accept(PhotoModel photo) throws Exception {
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://emotionalgoods.com/flickr/photo"));
                intent.setPackage(getPackageName());
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.putExtra(Extras.PHOTO_URL, photo.getUrl());
                intent.putExtra(Extras.PHOTO_TITLE, photo.getTitle());
                startActivity(intent);
            }
        });

        updateLayoutManager();

        onPhotosAdded = photoManager.onPhotosAdded()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                photoAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        });

        photoManager.setSearchTerm("tomato");
        photoManager.loadMorePhotos();
    }

    @Override
    protected void onDestroy() {
        onPhotosAdded.dispose();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateLayoutManager();
    }

    private void updateLayoutManager() {
        int orientation = isHorizontal() ? GridLayoutManager.HORIZONTAL : GridLayoutManager.VERTICAL;

        photoAdapter.setOrientation(orientation);

        photoGridView.setVerticalScrollBarEnabled(orientation == GridLayoutManager.VERTICAL);
        photoGridView.setHorizontalScrollBarEnabled(orientation == GridLayoutManager.HORIZONTAL);

        GridLayoutManager layoutManager;
        layoutManager = new GridLayoutManager(this, PHOTO_GRID_COLUMNS, orientation, false);
        photoGridView.setLayoutManager(layoutManager);

        photoGridView.getRecycledViewPool().clear();
    }

    private boolean isHorizontal() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
}

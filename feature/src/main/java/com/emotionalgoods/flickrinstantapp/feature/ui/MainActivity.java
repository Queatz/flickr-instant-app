package com.emotionalgoods.flickrinstantapp.feature.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.emotionalgoods.flickrinstantapp.feature.PhotoManager;
import com.emotionalgoods.flickrinstantapp.feature.R;

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

                int offset = recyclerView.computeVerticalScrollOffset();
                int extent = recyclerView.computeVerticalScrollExtent();
                int range = recyclerView.computeVerticalScrollRange();

                if (offset + extent * 2 >= range) {
                    photoManager.loadMorePhotos();
                }
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
        boolean landscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        int orientation = landscape ? GridLayoutManager.HORIZONTAL : GridLayoutManager.VERTICAL;

        photoAdapter.setOrientation(orientation);

        GridLayoutManager layoutManager;
        layoutManager = new GridLayoutManager(this, PHOTO_GRID_COLUMNS, orientation, false);
        photoGridView.setLayoutManager(layoutManager);

        photoGridView.getRecycledViewPool().clear();
    }
}

package com.emotionalgoods.flickrinstantapp.feature.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.emotionalgoods.flickrinstantapp.feature.R;
import com.squareup.picasso.Picasso;

/**
 * Created by jacob on 11/17/17.
 */

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);

        setContentView(R.layout.detail_activity);

        if (getIntent() != null) {
            show(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        show(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void show(Intent intent) {
        if (intent.hasExtra(Extras.PHOTO_URL)) {
            ImageView photo = findViewById(R.id.photo);

            Picasso.with(this)
                    .load(intent.getStringExtra(Extras.PHOTO_URL))
                    .into(photo);
        }

        if(intent.hasExtra(Extras.PHOTO_TITLE)) {
            TextView title = findViewById(R.id.title);
            title.setText(intent.getStringExtra(Extras.PHOTO_TITLE));
        }
    }
}

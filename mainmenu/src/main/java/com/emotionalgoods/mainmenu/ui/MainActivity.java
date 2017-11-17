package com.emotionalgoods.mainmenu.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.emotionalgoods.mainmenu.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.showPhotos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPhotos(false);
            }
        });

        findViewById(R.id.searchPhotos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPhotos(true);
            }
        });
    }

    private void showPhotos(boolean search) {
        String url = "https://emotionalgoods.com/flickr" + (search ? "/search" : "");

        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setPackage(getPackageName());
        intent.addCategory(Intent.CATEGORY_BROWSABLE);

        startActivity(intent);
    }
}

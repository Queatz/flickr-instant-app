package com.emotionalgoods.flickrinstantapp.feature.api;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jacob on 11/16/17.
 */

public class FlickrApi {

    private static final String FLICKR_API = "https://api.flickr.com/services/rest/";
    private static final String FLICKR_PHOTO_URL = "https://farm%s.staticflickr.com/%s/%s_%s.jpg";

    private final OkHttpClient client;
    private final Gson gson;

    private String baseUrl = FLICKR_API;

    public FlickrApi() {
        client = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        gson = new GsonBuilder().create();
    }

    public Single<List<PhotoModel>> searchPhotos(final String apiKey, final String search, final int page) {
        return Single.create(new SingleOnSubscribe<List<PhotoModel>>() {
            @Override
            public void subscribe(final SingleEmitter<List<PhotoModel>> emitter) throws Exception {
                HttpUrl.Builder httpBuilder = HttpUrl.parse(baseUrl).newBuilder();
                httpBuilder.addQueryParameter("method", "flickr.photos.search");
                httpBuilder.addQueryParameter("api_key", apiKey);
                httpBuilder.addQueryParameter("text", search);
                httpBuilder.addQueryParameter("per_page", "30");
                httpBuilder.addQueryParameter("page", String.valueOf(page));
                httpBuilder.addQueryParameter("format", "json");
                httpBuilder.addQueryParameter("nojsoncallback", "1");

                final Request request = new Request.Builder().url(httpBuilder.build()).build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        emitter.onError(e);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (!response.isSuccessful() || response.body() == null) {
                            emitter.onError(new Throwable("Invalid response"));
                            return;
                        }

                        List<PhotoModel> photos = new ArrayList<>();

                        try {
                            JsonArray results = gson.fromJson(
                                    response.body().charStream(),
                                    JsonObject.class
                            ).getAsJsonObject("photos").getAsJsonArray("photo");


                            for (JsonElement result : results) {
                                String url = makePhotoUrl(result.getAsJsonObject());
                                String title = result.getAsJsonObject().get("title").getAsString();
                                photos.add(new PhotoModel(url, title));
                            }
                        } catch (NullPointerException | JsonSyntaxException e) {
                            // ignored
                        }

                        emitter.onSuccess(photos);
                    }
                });
            }
        });
    }

    private String makePhotoUrl(JsonObject photo) {
        String farm = photo.get("farm").getAsString();
        String server = photo.get("server").getAsString();
        String id = photo.get("id").getAsString();
        String secret = photo.get("secret").getAsString();

        return String.format(FLICKR_PHOTO_URL, farm, server, id, secret);
    }

    @VisibleForTesting
    protected void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
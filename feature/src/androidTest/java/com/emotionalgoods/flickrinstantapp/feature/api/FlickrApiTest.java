package com.emotionalgoods.flickrinstantapp.feature.api;

import com.emotionalgoods.flickrinstantapp.feature.MockResponses;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import io.reactivex.Single;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.junit.Assert.assertEquals;

/**
 * Created by jacob on 11/16/17.
 */
public class FlickrApiTest {

    private FlickrApi flickrApi;
    private MockWebServer mockWebServer;

    @Before
    public void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse().setBody(MockResponses.getMockTomatoSearchResponse()));
        mockWebServer.start();

        flickrApi = new FlickrApi();
        flickrApi.setBaseUrl(mockWebServer.url("/services/rest/").toString());
    }

    @After
    public void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    public void searchPhotos() throws Exception {

        Single<List<PhotoModel>> search = flickrApi.searchPhotos("test", "tomato", 1);

        List<PhotoModel> photos = search.blockingGet();

        assertEquals(30, photos.size());
        assertEquals("20171017-IMG_5071 Final Days Barcelona 11", photos.get(0).getTitle());
        assertEquals("https://farm5.staticflickr.com/4541/37755167834_0688bce4c6.jpg", photos.get(0).getUrl());
    }
}
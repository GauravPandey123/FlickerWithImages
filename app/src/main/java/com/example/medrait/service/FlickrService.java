package com.example.medrait.service;

import com.App;
import com.example.medrait.model.PhotoInfoModel;
import com.example.medrait.model.PhotoModel;
import com.example.medrait.model.event.DetailEvent;
import com.example.medrait.model.event.SearchEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;


public final class FlickrService {

    public static final FlickrService INSTANCE = new FlickrService();
    public static final int PAGE_SIZE = 15;
    private static final long CACHE_SIZE_IN_MB = 10 * 1024 * 1024;
    private static final String API_KEY = "3189212285dcb4cf5b2f044edcb0544e";
    private static final String URL_BASE = "https://api.flickr.com/services/rest/"
            + "?method=flickr.photos.getRecent&nojsoncallback=1&format=json&api_key=" + API_KEY;
    private static final String URL_SEARCH = "&method=flickr.photos.search&tags=mode&per_page=" + PAGE_SIZE + "&page=";
    private static final String URL_DETAIL = "&method=flickr.photos.getInfo&photo_id=";
//    private static final String CACHE_PATH = App.getContext().getCacheDir().getAbsolutePath();
    private static final String COLUMN_PHOTO = "photo";
    private static final String COLUMN_PHOTOS = "photos";

    private FlickrService() {
        // no instances
    }

    public void searchAsync(final int page) {
        new Thread(() -> {
            try {
                List<PhotoModel> items = search(page);
                EventBus.getDefault().post(new SearchEvent(items, null));
            } catch (IOException | JSONException e) {
                Timber.e(e);
                EventBus.getDefault().post(new SearchEvent(null, e));
            }
        }).start();
    }

    public void getDetailAsync(final long id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PhotoInfoModel item = getDetail(id);
                    EventBus.getDefault().post(new DetailEvent(item, null));
                } catch (IOException | JSONException e) {
                    Timber.e(e);
                    EventBus.getDefault().post(new DetailEvent(null, e));
                }
            }
        }).start();
    }

    private PhotoInfoModel getDetail(long id) throws IOException, JSONException {
        Request request = new Request.Builder()
                .url(URL_BASE + URL_DETAIL + id)
                .build();

        Response response = getClient().newCall(request).execute();
        String json = response.body().string();
        JSONObject jsonObject = new JSONObject(json).getJSONObject(COLUMN_PHOTO);
        return getGson().fromJson(jsonObject.toString(), PhotoInfoModel.class);
    }

    private List<PhotoModel> search(int page) throws IOException, JSONException {
        Request request = new Request.Builder()
                .url(URL_BASE + URL_SEARCH + page)
                .build();

        Response response = getClient().newCall(request).execute();
        assert response.body() != null;
        String json = response.body().string();
        JSONArray jsonArray = new JSONObject(json).getJSONObject(COLUMN_PHOTOS).getJSONArray(COLUMN_PHOTO);

        Type listType = new TypeToken<List<PhotoModel>>() {
        }.getType();
        return getGson().fromJson(jsonArray.toString(), listType);

    }

    private OkHttpClient getClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
    }

    private Gson getGson() {
        return new GsonBuilder().create();
    }

}
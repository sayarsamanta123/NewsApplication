package com.example.sayarsamanta.newsapplication.volleyClass;

import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sayarsamanta.newsapplication.model.Post;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;

public class RXPostConnector {
    private static final String ENDPOINT_GET_POSTS = "https://newsapi.org/v2/top-headlines?country=in&apiKey=3ada68c0a34c46c0b6d4340a19289425&category=technology";

    private static RXPostConnector instance = new RXPostConnector();

    public static RXPostConnector getInstance() {
        return instance;
    }

    private RXPostConnector() {
    }


    private Post getPost(JSONObject current) throws JSONException {
        JSONObject jsonObjectSource=current.getJSONObject("source");
        Post result = new Post(current.getString("urlToImage"),jsonObjectSource.getString("name"),current.getString("content"),current.getString("title"),current.getString("url"));
//        result.iamgeUrl = current.getString("urlToImage");
//        result.newsContent = current.getString("content");
//        result.newsSource = current.getString("author");
//        result.newsTitle = current.getString("title");
//        result.newsUrl=current.getString("url");
        Log.d("title",result.iamgeUrl);
        return result;
    }

    public Single<List<Post>> getPosts() {
        return Single.create(new SingleOnSubscribe<List<Post>>() {
            @Override
            public void subscribe(@NonNull final SingleEmitter<List<Post>> e) {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ENDPOINT_GET_POSTS, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if (response != null) {
                                    Log.d("response",response.toString());
                                    Prefs.putString("response",response.toString());
                                    ArrayList<Post> result = new ArrayList<>();
                                    try {
                                        JSONArray jsonArray=response.getJSONArray("articles");
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            result.add(getPost(jsonArray.getJSONObject(i)));
                                        }
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }

                                    e.onSuccess(result);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                e.onError(error);
                            }
                        }
                ) {
                    @Override
                    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                        try {
                            Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                            if (cacheEntry == null) {
                                cacheEntry = new Cache.Entry();
                            }
                            final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
                            final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
                            long now = System.currentTimeMillis();
                            final long softExpire = now + cacheHitButRefreshed;
                            final long ttl = now + cacheExpired;
                            cacheEntry.data = response.data;
                            cacheEntry.softTtl = softExpire;
                            cacheEntry.ttl = ttl;
                            String headerValue;
                            headerValue = response.headers.get("Date");
                            if (headerValue != null) {
                                cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                            }
                            headerValue = response.headers.get("Last-Modified");
                            if (headerValue != null) {
                                cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                            }
                            cacheEntry.responseHeaders = response.headers;
                            final String jsonString = new String(response.data,
                                    HttpHeaderParser.parseCharset(response.headers));
                            return Response.success(new JSONObject(jsonString), cacheEntry);
                        } catch (UnsupportedEncodingException e) {
                            return Response.error(new ParseError(e));
                        } catch (JSONException e) {
                            return Response.error(new ParseError(e));
                        }
                    }

                    @Override
                    protected void deliverResponse(JSONObject response) {
                        super.deliverResponse(response);
                    }

                    @Override
                    public void deliverError(VolleyError error) {
                        super.deliverError(error);
                    }

                    @Override
                    protected VolleyError parseNetworkError(VolleyError volleyError) {
                        return super.parseNetworkError(volleyError);
                    }
                };



                VolleyDispatcher.getInstance().addToQueue(jsonObjectRequest);
            }
        });
    }
}

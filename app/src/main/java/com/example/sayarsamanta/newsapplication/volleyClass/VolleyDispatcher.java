package com.example.sayarsamanta.newsapplication.volleyClass;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyDispatcher {

    private VolleyDispatcher() {
    }

    private static VolleyDispatcher instance = new VolleyDispatcher();

    public static VolleyDispatcher getInstance() {
        return instance;
    }

    // application's main queue
    private RequestQueue requestQueue;

    public void init(Context context){
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(context);
    }

    // since multiple threads may be accessing this, let's make it synchronized
    public synchronized void addToQueue(Request request) {
        requestQueue.add(request);
    }
}

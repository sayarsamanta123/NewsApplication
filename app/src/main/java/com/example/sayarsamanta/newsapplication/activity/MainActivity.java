package com.example.sayarsamanta.newsapplication.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.android.volley.Cache;
import com.example.sayarsamanta.newsapplication.adapter.NewsListAdapter;
import com.example.sayarsamanta.newsapplication.R;
import com.example.sayarsamanta.newsapplication.volleyClass.RXPostConnector;
import com.example.sayarsamanta.newsapplication.volleyClass.VolleyDispatcher;
import com.example.sayarsamanta.newsapplication.model.Post;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private CompositeDisposable disposable = new CompositeDisposable();

    private RecyclerView postsRecylerview;

    private ArrayList<Post> postTitles = new ArrayList<>();
    NewsListAdapter newsListAdapter;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setMessage(getString(R.string.please_wait));
        Window window = MainActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.mainActivityTopBar));
        VolleyDispatcher.getInstance().init(this);
        getSupportActionBar().hide();
        postsRecylerview = (RecyclerView) findViewById(R.id.posts_list);



        if (isNetworkAvailable(MainActivity.this)) {
            Log.d("makingTheCall", "true");
            progressDialog.show();
            downloadPosts();

        }


        try {
            if (Prefs.getString("response", null).equals("")) {

            } else {
                String response = Prefs.getString("response", null);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    ArrayList<Post> result = new ArrayList<>();
                    JSONArray jsonArray = jsonObject.getJSONArray("articles");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        result.add(getPost(jsonArray.getJSONObject(i)));
                    }
                    postsRecylerview.setHasFixedSize(false);
                    final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    postsRecylerview.setLayoutManager(linearLayoutManager);
                    newsListAdapter = new NewsListAdapter(result, getApplicationContext());
                    postsRecylerview.setAdapter(newsListAdapter);
                    newsListAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }catch (NullPointerException e){
            Log.d("cameInException","true");
            e.printStackTrace();
        }





    }
    public boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
    private void downloadPosts() {
        Disposable subscription = RXPostConnector.getInstance().getPosts()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(getObserver());

        // add the subscription to the list to avoid a possible leak of references
        disposable.add(subscription);
    }

    private Post getPost(JSONObject current) throws JSONException {
        Post result = new Post(current.getString("urlToImage"),current.getString("author"),current.getString("content"),current.getString("title"),current.getString("url"));
        Log.d("title",result.iamgeUrl);
        return result;
    }

    private DisposableSingleObserver<List<Post>> getObserver(){
        return new DisposableSingleObserver<List<Post>>() {
            @Override
            public void onSuccess(@NonNull List<Post> posts) {
                progressDialog.dismiss();
                postTitles.clear();
                for (Post current : posts) {
                    Post post=new Post(current.iamgeUrl,current.newsSource,current.newsContent,current.newsTitle,current.newsUrl);
                    postTitles.add(post);
                }
                postsRecylerview.setHasFixedSize(false);
                final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                postsRecylerview.setLayoutManager(linearLayoutManager);
                newsListAdapter = new NewsListAdapter(postTitles,getApplicationContext());
                postsRecylerview.setAdapter(newsListAdapter);
                newsListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}

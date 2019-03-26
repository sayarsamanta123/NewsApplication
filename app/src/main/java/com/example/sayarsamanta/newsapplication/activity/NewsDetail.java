package com.example.sayarsamanta.newsapplication.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sayarsamanta.newsapplication.R;


public class NewsDetail extends AppCompatActivity {
    private Context mContext;
    private Activity mActivity;
    WebView webView;
    ImageView imageView;
    TextView textView;
    Toolbar toolbar;
    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        Window window = NewsDetail.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(NewsDetail.this, R.color.mainActivityTopBar));
        getSupportActionBar().hide();
        progress=new ProgressDialog(NewsDetail.this);
        mContext = getApplicationContext();
        // Get the activity
        mActivity = NewsDetail.this;
        toolbar = findViewById(R.id.toolbarNewsDetail);
        webView = findViewById(R.id.newsDetail);
        imageView = findViewById(R.id.imageViewBack);
        textView = toolbar.findViewById(R.id.newsTitle);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewsDetail.this, MainActivity.class);
                startActivity(intent);
            }
        });
        final Intent intent = getIntent();
        String titleNews = intent.getStringExtra("title");
        String url = intent.getStringExtra("url");
        textView.setText(titleNews);
        webView.setWebViewClient(new myWebViewClient());
        webView.getSettings().setAppCacheMaxSize( 5 * 1024 * 1024 ); // 5MB
        webView.getSettings().setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath() );
        webView.getSettings().setAllowFileAccess( true );
        webView.getSettings().setAppCacheEnabled( true );
        webView.getSettings().setJavaScriptEnabled( true );
        webView.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT );// load online by default
        if ( !isNetworkAvailable() ) { // loading offline
            webView.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
        }

        webView.loadUrl( url );

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        progress.dismiss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        progress.dismiss();
    }

    private class myWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap facIcon) {
            //SHOW LOADING IF IT ISNT ALREADY VISIBLE
            progress = ProgressDialog.show(NewsDetail.this, null, getResources().getString(R.string.please_wait), true);
            progress.setCanceledOnTouchOutside(false);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progress.dismiss();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

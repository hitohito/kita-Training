package com.example.tsunehitokita.mybrowserapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.view.View;

import static com.example.tsunehitokita.mybrowserapp.R.styleable.View;

public class MainActivity extends AppCompatActivity {
    private WebView myWebView;
    private EditText urlText;
    private static final String INITIAL_WEBSITE = "http://dotinstall.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        myWebView = (WebView) findViewById(R.id.myWebView);
        urlText = (EditText) findViewById(R.id.urlText);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                getSupportActionBar().setSubtitle(view.getTitle());
                urlText.setText(url);
            }
        });

        myWebView.loadUrl(INITIAL_WEBSITE);


    }
    public void clearUrl(View view) {
        urlText.setText("");
    }


    public void showWebsite(View view) {
        String url = urlText.getText().toString().trim();
        myWebView.loadUrl(url);

        if (!Patterns.WEB_URL.matcher(url).matches()) {
            urlText.setError("Invalid URL");
        } else

        {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            myWebView.loadUrl(url);
        }
    }



    public void onBackPressed() {
            if (myWebView.canGoBack()) {
                myWebView.goBack();
                return;
            }
            super.onBackPressed();
        }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myWebView != null) {
            myWebView.stopLoading();
            myWebView.setWebViewClient(null);
            myWebView.destroy();
        }
        myWebView = null;
    }

}

package com.lotustechnologicalsolution.chiri.ActivitiesFragments.OrderCreate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.lotustechnologicalsolution.R;

public class PaymentWebViewA extends AppCompatActivity {
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_web_view);
        String url = getIntent().getStringExtra("url");
        webView = findViewById(R.id.webview);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.addJavascriptInterface(new MyJavascriptInterface(),"interface");
                webView.loadUrl(url);
                return true;
            }
        });
        webView.addJavascriptInterface(new MyJavascriptInterface(),"interface");
        webView.loadUrl(url);




    }
    class MyJavascriptInterface
    {
        @android.webkit.JavascriptInterface
        public void callback(String authCode,String refNo)
        {
            //Toast.makeText(PaymentWebViewA.this, authCode+","+refNo, Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.putExtra("auth",authCode);
            intent.putExtra("ref",refNo);
            setResult(RESULT_OK,intent);
            finish();
        }
    }
}
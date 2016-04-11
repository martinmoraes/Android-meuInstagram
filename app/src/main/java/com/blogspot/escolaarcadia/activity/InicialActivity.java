package com.blogspot.escolaarcadia.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TabHost;

import br.com.escolaarcadia.meusfilmes.R;

@SuppressWarnings("deprecation")
public class InicialActivity extends TabActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityinicial);

        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);

        TabHost.TabSpec tab1 = tabHost.newTabSpec("TAB_1");
        tab1.setIndicator("", getResources().getDrawable(R.mipmap.listacinza));
        tab1.setContent(new Intent(this, RecebidosActivity.class));

        TabHost.TabSpec tab2 = tabHost.newTabSpec("TAB_2");
        tab2.setIndicator("", getResources().getDrawable(R.mipmap.cameracinza));
        tab2.setContent(new Intent(this, EnviarActivity.class));

        TabHost.TabSpec tab3 = tabHost.newTabSpec("TAB_3");
        tab3.setIndicator("", getResources().getDrawable(R.mipmap.perfilcinza));
        tab3.setContent(R.id.llweb);

        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);


        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equals("TAB_3")) {
                    WebView mWebView = (WebView) findViewById(R.id.webview);
                    mWebView.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            view.loadUrl(url);
                            return false;
                        }
                    });
                    mWebView.loadUrl("http://appviral.com.br/listar.php");
                }
            }
        });
    }
}
package com.blogspot.escolaarcadia.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TabHost;

import br.com.escolaarcadia.meusfilmes.R;

@SuppressWarnings("deprecation")
public class InicialActivity extends TabActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityinicial);

        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec tab1 = tabHost.newTabSpec("TAB_1");
        tab1.setIndicator("", getResources().getDrawable(R.mipmap.lista));
        tab1.setContent(new Intent(this, RecebidosActivity.class));

        TabHost.TabSpec tab2 = tabHost.newTabSpec("TAB_2");
        tab2.setIndicator("", getResources().getDrawable(R.mipmap.camera));
        tab2.setContent(new Intent(this, EnviarActivity.class));

        TabHost.TabSpec tab3 = tabHost.newTabSpec("TAB_3");
        tab3.setIndicator("", getResources().getDrawable(R.mipmap.perfil));
        tab3.setContent(R.id.perfil);

        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);


        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                switch (tabId) {
                    case "TAB_1":
                        //
                        break;
                    case "TAB_3":
                        WebView mWebView = (WebView) findViewById(R.id.webview);
                        mWebView.loadUrl("http://www.google.com.br/");
                        break;
                }
            }
        });
    }
}
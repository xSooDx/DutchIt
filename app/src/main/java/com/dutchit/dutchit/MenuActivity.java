package com.dutchit.dutchit;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        WebView mWebView = (WebView) findViewById(R.id.menu_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(Globals.getMenuUrl());
        mWebView.addJavascriptInterface(new DutchInterface(this),"DI");
    }
    class DutchInterface {
        int Pid;
        int Tid;
        String name;
        Context context;
        public DutchInterface(Context con){
            Pid=Globals.getPId();
            Tid=Globals.getTableN();
            name=Globals.getUName();
            context=con;
        }
        @android.webkit.JavascriptInterface
        public int getPId() {
            return Pid;
        }
        @android.webkit.JavascriptInterface
        public int getTId() {
            return Tid;
        }
        @android.webkit.JavascriptInterface
        public String getName() {
            return name;
        }
        @android.webkit.JavascriptInterface
        public void genBill()
        {
            Intent I = new Intent(context,BillActivity.class);
            startActivity(I);
        }
    }
}

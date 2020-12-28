package com.codemao.land.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.codemao.land.R;
import com.codemao.land.api.BridgeWebViewClient;
import com.codemao.land.api.JsApi;

import java.lang.reflect.Method;
import wendu.dsbridge.DWebView;
import wendu.dsbridge.OnReturnValue;

public class SelfJavaScriptActivity extends AppCompatActivity {
    public static final String WEB_VIEW_URL = "WEB_VIEW_URL";
    public static final String WEB_JSON_PATH_URL = "WEB_JSON_PATH_URL";
    private static final String TAG = "SelfJavaScriptActivity";
    DWebView webView;
    String mWebUrl;
    String mLocalJsonPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_java_script);
        findViewById();
        initData();
        setListener();
    }

    private void findViewById() {
        webView = findViewById(R.id.web_view);
        webView.addJavascriptObject(new JsApi(), null);
        //webView.loadUrl("file:///android_asset/js-native-transform.html");
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initData() {
        mWebUrl = getIntent().getStringExtra(WEB_VIEW_URL);
        mLocalJsonPath = getIntent().getStringExtra(WEB_JSON_PATH_URL);
        Log.e(TAG,mLocalJsonPath);
        try {//本地HTML里面有跨域的请求 原生webview需要设置之后才能实现跨域请求
            if (Build.VERSION.SDK_INT >= 16) {
                Class<?> clazz = webView.getSettings().getClass();
                Method method = clazz.getMethod(
                        "setAllowUniversalAccessFromFileURLs", boolean.class);
                if (method != null) {
                    method.invoke(webView.getSettings(), true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        webView.loadUrl(mWebUrl);

        webView.setWebViewClient(new BridgeWebViewClient(this));
    }

    private void setListener(){
       ImageButton  btnRefreshLoad = findViewById(R.id.btnRefreshLoad);
        btnRefreshLoad.setOnClickListener(v -> {
            webView.loadUrl(mWebUrl);
        });
        ImageButton  btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> {
            webView.callHandler("addValue", new Object[]{3, 4}, new OnReturnValue<Integer>() {
                @Override
                public void onValue(Integer retValue) {
                    Toast.makeText(SelfJavaScriptActivity.this, "答案等于" + retValue, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
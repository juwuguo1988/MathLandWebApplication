package com.codemao.land.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.codemao.land.R;
import com.codemao.land.api.BridgeWebViewClient;

import java.lang.reflect.Method;

import wendu.dsbridge.CompletionHandler;
import wendu.dsbridge.DWebView;
import wendu.dsbridge.OnReturnValue;

public class SelfJavaScriptActivity extends AppCompatActivity {
    public static final String WEB_VIEW_URL = "WEB_VIEW_URL";
    public static final String WEB_JSON_PATH_URL = "WEB_JSON_PATH_URL";
    private static final String TAG = "SelfJavaScriptActivity";
    private DWebView webView;
    private String mWebUrl;
    private String mLocalJsonPath;
    private DevicePowerReceiver mDevicePowerReceiver;

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
        // webView.loadUrl("file:///android_asset/js-native-transform.html");
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initData() {
        mWebUrl = getIntent().getStringExtra(WEB_VIEW_URL);
        mLocalJsonPath = getIntent().getStringExtra(WEB_JSON_PATH_URL);
        webView.addJavascriptObject(new JsCallNativeApi(mLocalJsonPath), null);
        Log.e(TAG, mLocalJsonPath);
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
        registerSearchReceiver();
    }

    //动态注册receiver;
    private void registerSearchReceiver() {
        mDevicePowerReceiver = new DevicePowerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mDevicePowerReceiver, filter);
    }

    class DevicePowerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                webView.callHandler("gamePause", new OnReturnValue() {
                    @Override
                    public void onValue(Object retValue) {
                        Log.i("============", "gamePause");
                    }
                });
            } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                webView.callHandler("gameResume", new OnReturnValue() {
                    @Override
                    public void onValue(Object retValue) {
                        Log.i("============", "gameResume");
                    }
                });
            }
        }

    }

    private void setListener() {
        ImageButton btnRefreshLoad = findViewById(R.id.btnRefreshLoad);
        btnRefreshLoad.setOnClickListener(v -> {
            webView.loadUrl(mWebUrl);
        });
        ImageButton btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> {
            webView.callHandler("addValue", new Object[]{3, 4}, new OnReturnValue<Integer>() {
                @Override
                public void onValue(Integer retValue) {
                    Toast.makeText(SelfJavaScriptActivity.this, "答案等于" + retValue, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    class JsCallNativeApi {
        private String filePath;

        JsCallNativeApi(String path) {
            this.filePath = path;
        }
//
//        @JavascriptInterface
//        public void getConfigPath(Object msg, CompletionHandler<String> handler) {
//            handler.complete(filePath);
//        }

        @JavascriptInterface
        public void onLoadSuccess(Object msg, CompletionHandler<String> handler) {
            webView.callHandler("getConfigPath", new Object[]{filePath}, new OnReturnValue() {
                @Override
                public void onValue(Object retValue) {

                }
            });
        }
    }
}

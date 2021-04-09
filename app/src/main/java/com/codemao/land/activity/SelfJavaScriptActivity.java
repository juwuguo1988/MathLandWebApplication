package com.codemao.land.activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
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
import java.util.List;

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
    public boolean isForeground = false;
    public boolean isFirstComeApp = false;
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

    protected void onResume() {
        if (!isForeground) {
            //由后台切换到前台
            isForeground = true;
            if (isFirstComeApp) {
                webView.callHandler("resume", new OnReturnValue() {
                    @Override
                    public void onValue(Object retValue) {
                        Log.i("============", "gameResume");
                    }
                });
            }
        }
        super.onResume();
    }


    @Override
    protected void onStop() {
        if (!isAppOnForeground()) {
            //app 进入后台
            isForeground = false;//记录当前已经进入后台
            isFirstComeApp = true;
            webView.callHandler("pause", new OnReturnValue() {
                @Override
                public void onValue(Object retValue) {
                    Log.i("============", "gamePause");
                }
            });
        }
        super.onStop();
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
                webView.callHandler("pause", new OnReturnValue() {
                    @Override
                    public void onValue(Object retValue) {
                        Log.i("============", "gamePause");
                    }
                });
            } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                webView.callHandler("resume", new OnReturnValue() {
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

    /**
     * 判断app是否处于前台
     *
     * @return
     */
    public boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();
        /**
         * 获取Android设备中所有正在运行的App
         */
        List<RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }
}

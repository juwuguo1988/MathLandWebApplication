package com.codemao.land.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.codemao.land.utils.loading.LoadingDialog;

/**
 * Created by Juwuguo on 2017/5/22.
 */

public class BridgeWebViewClient extends WebViewClient {
    private Context mContext;
    private LoadingDialog mLoadingDialog;

    public BridgeWebViewClient(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        showProgressDialog();
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        dismissProgressDialog();
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed();
    }

    public void showProgressDialog() {
        try {
            dismissProgressDialog();
            mLoadingDialog = new LoadingDialog(mContext, false);
            mLoadingDialog.show();
        } catch (Exception e) {
        }
    }

    public void dismissProgressDialog() {
        try {
            if (mLoadingDialog != null) {
                mLoadingDialog.dismiss();
            }
        } catch (Exception e) {
        }
    }

}

package com.codemao.land.utils.okhttp;

import android.text.TextUtils;
import android.util.Log;

import com.codemao.land.utils.okhttp.builder.GetStringBuilder;
import com.codemao.land.utils.okhttp.callback.Callback;
import com.codemao.land.utils.okhttp.request.RequestCall;
import com.codemao.land.utils.okhttp.utils.Platform;
import com.codemao.land.utils.okhttp.utils.ResponseException;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.Executor;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSource;

public class OkHttpUtils {

    public static final long DEFAULT_MILLISECONDS = 10000L;
    private volatile static OkHttpUtils mInstance;
    private OkHttpClient mOkHttpClient;
    private Platform mPlatform;
    private static RequestCall localRequestCall;

    public OkHttpUtils(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            mOkHttpClient = new OkHttpClient();
        } else {
            mOkHttpClient = okHttpClient;
        }

        mPlatform = Platform.get();
    }

    public static OkHttpUtils initClient(OkHttpClient okHttpClient) {
        if (mInstance == null) {
            synchronized (OkHttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtils(okHttpClient);
                }
            }
        }
        return mInstance;
    }

    public static OkHttpUtils getInstance() {
        return initClient(null);
    }

    public Executor getDelivery() {
        return mPlatform.defaultCallbackExecutor();
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public static GetStringBuilder get() {
        return new GetStringBuilder();
    }

    public static GetStringBuilder getWithTimeHeader() {
        return new GetStringBuilder(System.currentTimeMillis());
    }

    public void execute(final RequestCall requestCall, Callback callback) {
        if (callback == null) {
            callback = Callback.CALLBACK_DEFAULT;
        }
        final Callback finalCallback = callback;
        localRequestCall = requestCall;
//        if(NetUtils.cheackNet())
        final int id = requestCall.getOkHttpRequest().getId();
        requestCall.getCall().enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                try {
                    Log.i("OkHttp", e.getMessage().toString());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                sendFailResultCallback(call, e, finalCallback, id);
            }

            @Override
            public void onResponse(final Call call, final Response response) {
                Log.i("OkHttpLog", mOkHttpClient.toString());
                try {
                    if (call.isCanceled()) {
                        sendFailResultCallback(call, new IOException("Canceled!"), finalCallback, id);
                        return;
                    }

                    if (!finalCallback.validateResponse(response, id)) {
                        ResponseException responseException = new ResponseException(response);
                        String errorBody = getResponseSource(response);
                        if (!TextUtils.isEmpty(errorBody)) {
                            responseException.setErrorJson(errorBody);
                        }
                        sendFailResultCallback(call, responseException, finalCallback, id);
                        return;
                    }

                    Object o = finalCallback.parseNetworkResponse(response, id);
                    sendSuccessResultCallback(o, finalCallback, id);
                } catch (Exception e) {
                    sendFailResultCallback(call, e, finalCallback, id);
                } finally {
                    if (response.body() != null) {
                        response.body().close();
                    }
                }

            }
        });
    }

    private String getResponseSource(Response response) {
        BufferedSource source = response.body().source();
        try {
            source.request(9223372036854775807L);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Buffer buffer = source.buffer();
        Charset charset = Charset.forName("UTF-8");
        MediaType contentType = response.body().contentType();
        if (contentType != null) {
            try {
                charset = contentType.charset(Charset.forName("UTF-8"));
            } catch (UnsupportedCharsetException var26) {
            }
        }

        if (isPlaintext(buffer)) {
            return buffer.readString(charset);
        }
        return null;
    }

    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64L ? buffer.size() : 64L;
            buffer.copyTo(prefix, 0L, byteCount);

            for (int i = 0; i < 16 && !prefix.exhausted(); ++i) {
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }

            return true;
        } catch (EOFException var6) {
            return false;
        }
    }

    public Response excute(RequestCall requestCall) {
        Response response = null;
        try {
            response = requestCall.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public void sendFailResultCallback(final Call call, final Exception e, final Callback callback, final int id) {
        if (callback == null) {
            return;
        }
        mPlatform.execute(new Runnable() {
            @Override
            public void run() {
                callback.onError(call, e, id);
                callback.onAfter(id);
            }
        });
    }

    public void sendSuccessResultCallback(final Object object, final Callback callback, final int id) {
        if (callback == null) {
            return;
        }
        mPlatform.execute(new Runnable() {
            @Override
            public void run() {
                callback.onCompleted(object, id);
                callback.onAfter(id);
            }
        });
    }

    public void cancelTag(Object tag) {
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    //取消所有網絡請求
    public void cancelAll(){
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            call.cancel();
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            call.cancel();
        }
    }

    public static void finishRequestTask() {
        if (null != localRequestCall) {
            if (null != localRequestCall.getCall() && localRequestCall.getCall().isExecuted()) {
                localRequestCall.cancel();
            }
        }
    }

    public static class METHOD {

        public static final String HEAD = "HEAD";
        public static final String DELETE = "DELETE";
        public static final String PUT = "PUT";
        public static final String PATCH = "PATCH";
    }
}

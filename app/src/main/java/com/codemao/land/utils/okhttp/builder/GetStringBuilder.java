package com.codemao.land.utils.okhttp.builder;

import android.net.Uri;

import com.codemao.land.utils.okhttp.request.GetStringRequest;
import com.codemao.land.utils.okhttp.utils.HasParamsable;
import com.codemao.land.utils.okhttp.utils.OkHttpConstant;
import com.codemao.land.utils.okhttp.request.RequestCall;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


public class GetStringBuilder extends OkHttpRequestBuilder<GetStringBuilder> implements HasParamsable {

    protected String appendParams(String url, Map<String, String> params) {
        if (url == null || params == null || params.isEmpty()) {
            return url;
        }
        Uri.Builder builder = Uri.parse(url).buildUpon();
        Set<String> keys = params.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            builder.appendQueryParameter(key, params.get(key));
        }
        return builder.build().toString();
    }

    public GetStringBuilder() {
    }

    public GetStringBuilder(long timestamp) {
        addHeader(OkHttpConstant.HTTP_KEY_TIMESTAMP, String.valueOf(timestamp));
    }

    @Override
    public GetStringBuilder params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    public GetStringBuilder params(Object params) {
        try {
            this.params = (Map<String, String>) params;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public GetStringBuilder addParams(String key, String val) {
        if (this.params == null) {
            params = new LinkedHashMap<>();
        }
        params.put(key, val);
        return this;
    }

    @Override
    public RequestCall build() {
        return new GetStringRequest(url, tag, params, headers, id).build();
    }

}

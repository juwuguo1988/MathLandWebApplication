package com.codemao.land.utils.okhttp.utils;

import com.codemao.land.utils.okhttp.builder.OkHttpRequestBuilder;

import java.util.Map;

public interface HasParamsable {

    OkHttpRequestBuilder params(Map<String, String> params);

    OkHttpRequestBuilder addParams(String key, String val);
}

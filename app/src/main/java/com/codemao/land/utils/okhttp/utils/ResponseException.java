package com.codemao.land.utils.okhttp.utils;

import okhttp3.Response;

public class ResponseException extends RuntimeException {

    private Response mResponse;
    private String errorJson;

    public ResponseException(Response response) {
        mResponse = response;
    }

    public ResponseException(String detailMessage, Response response) {
        super(detailMessage);
        mResponse = response;
    }

    public ResponseException(String detailMessage, Throwable throwable, Response response) {
        super(detailMessage, throwable);
        mResponse = response;
    }

    public ResponseException(Throwable throwable, Response response) {
        super(throwable);
        mResponse = response;
    }

    public Response getResponse() {
        return mResponse;
    }

    public String getErrorJson() {
        return errorJson;
    }

    public void setErrorJson(String errorJson) {
        this.errorJson = errorJson;
    }
}

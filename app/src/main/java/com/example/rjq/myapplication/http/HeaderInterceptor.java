package com.example.rjq.myapplication.http;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        //为所有请求添加请求头
        Request requestOverwrite = request.newBuilder().header("User-Agent", "Android").build();
        return chain.proceed(requestOverwrite);

        //为所有请求添加响应头
        //Request request = chain.request();
        //okhttp3.Response originalResponse = chain.proceed(request);
        //return originalResponse.newBuilder().header("Cache-Control","max-age=100").build();
    }
}

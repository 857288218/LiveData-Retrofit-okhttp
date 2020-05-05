package com.example.rjq.myapplication.http;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.example.rjq.myapplication.entity.HttpResult;
import com.example.rjq.myapplication.entity.Subject;
import com.example.rjq.myapplication.entity.User;
import com.example.rjq.myapplication.entity.WanResponse;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 相当于内核,调用接口返回一个liveData
 */
public class HttpMethods {

    public static final String BASE_URL = "https://www.wanandroid.com/";

    private static final int DEFAULT_TIMEOUT = 8;

    private Retrofit retrofit;
    private MovieService movieService;

    //构造方法私有
    private HttpMethods() {
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        //为所有请求都加个该header,比如用户的id
        builder.addInterceptor(new HeaderInterceptor());
        //添加不同baseUrl
        builder.addInterceptor(new MoreBaseUrlInterceptor());
        retrofit = new Retrofit.Builder()
                .client(builder.build())
                //modify by zqikai 20160317 for 对http请求结果进行统一的预处理 GosnResponseBodyConvert
                .addConverterFactory(GsonConverterFactory.create())
//                .addConverterFactory(ResponseConvertFactory.create())
                .baseUrl(BASE_URL)
                .build();

        movieService = retrofit.create(MovieService.class);
    }

    //在访问HttpMethods时创建单例
    private static class SingletonHolder {
        private static final HttpMethods INSTANCE = new HttpMethods();
    }

    //获取单例
    public static HttpMethods getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 用于获取豆瓣电影Top250的数据
     *
     * @param start 起始位置
     * @param count 获取长度
     */
    public LiveData<List<Subject>> getTopMovie(int start, int count) {
        final MutableLiveData<List<Subject>> liveData = new MutableLiveData<>();
        movieService.getTopMovie(start, count).enqueue(new Callback<HttpResult<List<Subject>>>() {
            @Override
            public void onResponse(Call<HttpResult<List<Subject>>> call, Response<HttpResult<List<Subject>>> response) {
                liveData.postValue(response.body().getSubjects());
            }

            @Override
            public void onFailure(Call<HttpResult<List<Subject>>> call, Throwable t) {
                liveData.postValue(null);
            }
        });
        return liveData;
    }

    public LiveData<WanResponse<User>> login(String name, String pwd) {
        final MutableLiveData<WanResponse<User>> liveData = new MutableLiveData<>();
        movieService.loginAsync(name, pwd).enqueue(new Callback<WanResponse<User>>() {
            @Override
            public void onResponse(Call<WanResponse<User>> call, Response<WanResponse<User>> response) {
                //读取response header
                okhttp3.Response okRes = response.raw(); //Retrofit的Response转换为原生的OkHttp当中的Response
                okRes.header("Cache-Control");
                //获取了本次请求所有的响应头
                response.headers();  //等于okRes.headers();
                Log.d("current thread", Thread.currentThread().getName());
                liveData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<WanResponse<User>> call, Throwable t) {
                liveData.setValue(null);
            }
        });
        return liveData;
    }

    //图文混传
    public void uploadFile() {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        File f = new File("本地url");
        builder.addFormDataPart("img", f.getName(), RequestBody.create(MediaType.parse("image/*"), f));
        //builder.addFormDataPart("json",json字符串); 也可以这样传递json字符串,一般json字符串是用new Gson().toJson(obj)把对象转换成的，也可以解析list，这样就解析成jsonArray字符串了
        builder.addFormDataPart("param", "value");
        movieService.uploadFileAndString(builder.build()).enqueue(new Callback<WanResponse>() {
            @Override
            public void onResponse(Call<WanResponse> call, Response<WanResponse> response) {

            }

            @Override
            public void onFailure(Call<WanResponse> call, Throwable t) {

            }
        });
    }
}

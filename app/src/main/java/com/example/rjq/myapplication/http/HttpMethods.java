package com.example.rjq.myapplication.http;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.example.rjq.myapplication.entity.User;
import com.example.rjq.myapplication.entity.WanResponse;

import java.io.File;
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

    private String TAG = this.getClass().getSimpleName() + ">>>>";

    public static final String BASE_URL = "https://www.wanandroid.com/";

    private static final int DEFAULT_TIMEOUT = 8;

    private Retrofit retrofit;
    private MovieService movieService;
    //在viewModel中的onCleared()回调中cancel
    public Call<WanResponse<User>> wanResponseCallback;

    //构造方法私有
    private HttpMethods() {
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        //为所有请求都加个该header,比如用户的id
        builder.addInterceptor(new HeaderInterceptor());
        //添加不同baseUrl
//        builder.addInterceptor(new MoreBaseUrlInterceptor());
        retrofit = new Retrofit.Builder()
                .client(builder.build())
                //对http请求结果进行统一的预处理 GosnResponseBodyConvert
                .addConverterFactory(GsonConverterFactory.create())
//                .addConverterFactory(ResponseConvertFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
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

    public LiveData<WanResponse<User>> login(String name, String pwd) {
        final MutableLiveData<WanResponse<User>> liveData = new MutableLiveData<>();
        wanResponseCallback = movieService.loginAsync(name, pwd);
        wanResponseCallback.enqueue(new Callback<WanResponse<User>>() {
            @Override
            public void onResponse(Call<WanResponse<User>> call, Response<WanResponse<User>> response) {
                //response.isSuccessful()方法的HTTP状态码是[200..300)之间，在这之间都算是请求成功
                //并且在正常情况下只有200的时候后台才会返回数据，其他是没有数据的
                if (response.isSuccessful()) {
                    //对后台返回的数据进行处理
                    //读取response header
                    okhttp3.Response okRes = response.raw(); //Retrofit的Response转换为原生的OkHttp当中的Response
                    okRes.header("Cache-Control");
                    //获取了本次请求所有的响应头
                    response.headers();  //等于okRes.headers();
                    Log.d("current thread", Thread.currentThread().getName());
                    liveData.setValue(response.body());
                } else {
                    //对后台返回不在[200..300)之间的错误码进行处理，即unsuccessful
                    //errorBody is unsuccessful response; if {@link #code()} is in the range [200..300) is isSuccessful
                    //可以只弹toast
                    liveData.setValue(new WanResponse<User>(response.code(), "服务器状态码异常：" + response.code(), null));
                }
            }

            @Override
            public void onFailure(Call<WanResponse<User>> call, Throwable t) {
                //对当前网络情况差或者请求超时等网络请求延迟等一些错误处理。可以只弹toast
                liveData.setValue(new WanResponse<User>(-2, "当前网络不给力,请确认网络已连接" + t.getMessage(), null));
            }
        });
        return liveData;
    }

    public LiveData<WanResponse<User>> login2(String name, String pwd) {
        final MutableLiveData<WanResponse<User>> liveData = new MutableLiveData<>();
        wanResponseCallback = movieService.loginAsync(name, pwd);
        wanResponseCallback.enqueue(new RJQCallback<WanResponse<User>>() {
            @Override
            public void onSuccessful(Call<WanResponse<User>> call, Response<WanResponse<User>> response) {
                liveData.setValue(response.body());
            }
            
            //如果对某些错误特殊处理，重写onFail
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

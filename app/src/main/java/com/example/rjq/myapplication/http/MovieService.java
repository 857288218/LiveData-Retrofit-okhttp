package com.example.rjq.myapplication.http;

import com.example.rjq.myapplication.entity.HttpResult;
import com.example.rjq.myapplication.entity.Subject;
import com.example.rjq.myapplication.entity.User;
import com.example.rjq.myapplication.entity.WanResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by liukun on 16/3/9.
 */
public interface MovieService {

    @GET("top250")
    Call<HttpResult<List<Subject>>> getTopMovie(@Query("start") int start, @Query("count") int count);

    @Headers("Content-type:application/x-www-form-urlencoded;charset=UTF-8") //添加请求头
    @FormUrlEncoded
    @POST("user/login")
    //可以使用最原始的方法返回Call<WanResponse<User>>
    Call<WanResponse<User>> loginAsync(@Field("username")String username, @Field("password")String password);

    @Multipart()
    @POST("api/files")
    Call<WanResponse> uploadFileAndString(@Part("file")MultipartBody multipartBody);

    @FormUrlEncoded
    @POST("api/users") //动态添加请求头
    Call<WanResponse> uploadNewUser(@Header("Content-Type") String contentType, @Field("username") String username, @Field("gender") String male, @Field("age") int age);
}

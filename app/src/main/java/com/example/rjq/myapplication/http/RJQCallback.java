package com.example.rjq.myapplication.http;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class RJQCallback<T> implements Callback<T> {

    private String TAG = this.getClass().getSimpleName() + ">>>>";

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            onSuccessful(call, response);
        } else {
            onFail(call, null, response);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        onFail(call, t, null);
    }

    public abstract void onSuccessful(Call<T> call, Response<T> response);

    protected void onFail(Call<T> call, Throwable t, Response<T> response) {
        if (response == null) {
            //弹toast:"当前网络不给力,请确认网络已连接" + t.getMessage()
        } else {
            //对后台返回不在[200..300)之间的错误码进行处理，即unsuccessful
            //弹toast:response.errorBody().string() 或者 服务器状态码异常:response.code()
        }
    }
}

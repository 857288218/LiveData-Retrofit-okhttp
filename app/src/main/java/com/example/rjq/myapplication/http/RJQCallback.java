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

    }
}

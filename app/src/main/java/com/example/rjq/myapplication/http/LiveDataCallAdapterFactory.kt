package com.example.rjq.myapplication.http

import android.arch.lifecycle.LiveData
import com.example.rjq.myapplication.entity.WanResponse
import retrofit2.*
import retrofit2.CallAdapter.Factory
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

class LiveDataCallAdapterFactory : Factory() {
    override fun get(
            returnType: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != LiveData::class.java) {
            return null
        }
        val observableType = getParameterUpperBound(0, returnType as ParameterizedType)
        val rawObservableType = getRawType(observableType)
        if (rawObservableType != WanResponse::class.java) {
            throw IllegalArgumentException("type must be a resource")
        }
        if (observableType !is ParameterizedType) {
            throw IllegalArgumentException("resource must be parameterized")
        }
        val bodyType = getParameterUpperBound(0, observableType)
        return LiveDataCallAdapter<Any>(bodyType)
    }

    /**
     * 请求适配器
     */
    class LiveDataCallAdapter<R>(var type: Type) : CallAdapter<R, LiveData<R>> {
        override fun adapt(call: Call<R>?): LiveData<R> {
            return object : LiveData<R>() {
                //这个作用是业务在多线程中,业务处理的线程安全问题,确保单一线程作业
                val flag = AtomicBoolean(false)

                override fun onActive() {
                    super.onActive()
                    if (flag.compareAndSet(false, true)) {
                        call!!.enqueue(object : Callback<R> {
                            override fun onFailure(call: Call<R>?, t: Throwable?) {
                                //对当前网络情况差或者请求超时等网络请求延迟等一些错误处理。可以只弹toast
                                postValue(null)
                            }

                            override fun onResponse(call: Call<R>?, response: Response<R>?) {
                                if (response?.isSuccessful == true) {
                                    //The raw response body of an {@linkplain #isSuccessful() successful} response
                                    value = response.body()
                                } else {
                                    //The raw response body of an {@linkplain #isSuccessful() unsuccessful} response
                                    response?.errorBody()
                                    //可以弹个toast
                                }
                            }
                        })
                    }
                }
            }
        }

        override fun responseType(): Type {
            return type
        }
    }
}
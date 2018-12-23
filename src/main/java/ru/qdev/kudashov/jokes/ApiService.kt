package ru.qdev.kudashov.jokes

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.schedulers.Schedulers


interface AnecdoticaRuService {
    companion object {
        val baseUrl: String
            get() = "http://anecdotica.ru/"

        fun create() : AnecdoticaRuService {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor()
                    .setLevel(
                        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                        else HttpLoggingInterceptor.Level.NONE)
                )
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()
            return retrofit.create(AnecdoticaRuService::class.java)
        }
    }

    class Result {
        var error: Int = -1
        var errMsg: String = ""
    }

    class Joke {
        var text: String = ""
        var note: String = ""
    }

    class JokeResponse {
        var result: Result? = null
        var item: Joke? = null
    }

    @GET("api?pid=221122&method=getRandItem&format=json&charset=utf-8&")
    fun randomJoke() : Observable<JokeResponse>

    @GET("api?pid=221122&method=getRandItem&format=json&charset=utf-8&")
    fun randomJokeCall() : Call<JokeResponse>
}
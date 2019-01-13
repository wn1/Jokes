package ru.qdev.kudashov.jokes

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Path
import rx.schedulers.Schedulers

interface UmoriliService {
    companion object {
        val baseUrl: String
            get() = "http://umorili.herokuapp.com/api/"

        fun create() : UmoriliService {
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
            return retrofit.create(UmoriliService::class.java)
        }
    }

    class Joke {
        var site: String? = ""
        var name: String? = ""
        var desc: String? = ""
        var link: String? = ""
        var elementPureHtml: String? = ""
    }

    class JokeResponse : ArrayList<Joke>()

    @GET("random?num={count}")
    fun randomJoke(count: Int) : Observable<JokeResponse>

    @GET("random?num=5")
    fun randomJokeCall() : Call<JokeResponse>
}
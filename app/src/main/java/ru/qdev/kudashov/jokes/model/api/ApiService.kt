package ru.qdev.kudashov.jokes.model.api

import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.http.GET
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Query
import ru.qdev.kudashov.jokes.BuildConfig

interface UmoriliService {
    companion object {
        val baseUrl: String
            get() = "http://umorili.herokuapp.com/api/"

        val wwwUrl: String
            get() = "http://umori.li"

        private var service: UmoriliService? = null

        fun instance(): UmoriliService {
            if (service == null) {
                service = create()
            }
            return service!!
        }

        fun create() : UmoriliService {
            val okHttpClientBuilder = OkHttpClient.Builder()
            okHttpClientBuilder.addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(
                        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                        else HttpLoggingInterceptor.Level.NONE)
                )
            if (BuildConfig.DEBUG) {
                okHttpClientBuilder.addInterceptor(OkHttpProfilerInterceptor())
            }
            val okHttpClient = okHttpClientBuilder.build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))
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

    @GET("random")
    fun randomJokeList(
        @Query("num") count: Int,
        @Query("name") name: String?
    ) : Single<JokeResponse>

    @GET("random?num=10")
    fun randomJokeCall() : Call<JokeResponse>
}
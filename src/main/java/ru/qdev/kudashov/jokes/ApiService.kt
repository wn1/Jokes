package ru.qdev.kudashov.jokes

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.Retrofit

interface AnecdoticaRuService {
    companion object {
        val baseUrl: String
            get() = "https://api.github.com"

        fun create() : AnecdoticaRuService {
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
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

    @GET("/users/{user}/repos")
    fun randomJoke() : Observable<JokeResponse>
}
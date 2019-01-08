package ru.qdev.kudashov.jokes.main.ui

import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.qdev.kudashov.jokes.UmoriliService

class JokeRepository {
    var jokeObservable: Observable<UmoriliService.JokeResponse>? = null
    fun getNewJoke() : Observable<UmoriliService.JokeResponse> {
        jokeObservable = Observable.defer {
            val observable: Observable<UmoriliService.JokeResponse>? = Observable.create {
                UmoriliService.create().randomJokeCall().enqueue(object : Callback<UmoriliService.JokeResponse> {
                    override fun onFailure(call: Call<UmoriliService.JokeResponse>, t: Throwable) {
                        it.onError(t)
                    }

                    override fun onResponse(
                        call: Call<UmoriliService.JokeResponse>,
                        response: Response<UmoriliService.JokeResponse>
                    ) {
                        it.onNext(response.body()!!)
                    }
                })
            }
            return@defer observable
        }
        return jokeObservable!!
    }

}
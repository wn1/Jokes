package ru.qdev.kudashov.jokes.main.ui

import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.qdev.kudashov.jokes.AnecdoticaRuService

class JokeRepository {
    var jokeObservable: Observable<AnecdoticaRuService.JokeResponse>? = null

    fun getNewJoke() : Observable<AnecdoticaRuService.JokeResponse> {
        jokeObservable = Observable.create {
//            Observable.defer({
                AnecdoticaRuService.create().randomJokeCall().enqueue( object : Callback<AnecdoticaRuService.JokeResponse> {
                    override fun onFailure(call: Call<AnecdoticaRuService.JokeResponse>, t: Throwable) {
                        it.onError(t)
                    }

                    override fun onResponse(
                        call: Call<AnecdoticaRuService.JokeResponse>,
                        response: Response<AnecdoticaRuService.JokeResponse>
                    ) {
                        it.onNext(response.body()!!)
                    }
                }
                )
//            }
        }
        return jokeObservable!!

//        jokeObservable = AnecdoticaRuService.create().randomJoke()
//        return jokeObservable!!
    }

}
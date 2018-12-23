package ru.qdev.kudashov.jokes.main.ui

import io.reactivex.Observable
import ru.qdev.kudashov.jokes.AnecdoticaRuService

class JokeRepository {
    var jokeObservable: Observable<AnecdoticaRuService.JokeResponse>? = null

    fun getNewJoke() : Observable<AnecdoticaRuService.JokeResponse> {
        jokeObservable = AnecdoticaRuService.create().randomJoke()
        return jokeObservable!!
    }
}
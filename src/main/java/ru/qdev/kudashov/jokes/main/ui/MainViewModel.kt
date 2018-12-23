package ru.qdev.kudashov.jokes.main.ui

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import ru.qdev.kudashov.jokes.AnecdoticaRuService.*

class MainViewModel : ViewModel() {
    val jokeRepository: JokeRepository = JokeRepository()
    fun getNewJoke() : Observable<JokeResponse> {
        return jokeRepository.getNewJoke()
    }
}

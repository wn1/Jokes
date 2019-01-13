package ru.qdev.kudashov.jokes.main.ui

import android.content.Context
import androidx.databinding.ObservableBoolean
import io.reactivex.Flowable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.qdev.kudashov.jokes.AppSettings
import ru.qdev.kudashov.jokes.db.*
import ru.qdev.kudashov.jokes.UmoriliService
import java.util.*

class JokeRepository(val context: Context) {
    val updateInProgress  = ObservableBoolean(false)

    // From Api to Db mapping function
    fun UmoriliService.Joke.toDb() : Joke {
        val result = Joke()
        result.content = this.desc
        result.dateUTC = Date().time
        result.link = this.link
        result.nonUniqueId = this.link
        return result
    }

    fun getNewJoke() : Flowable<JokeList> {
        return JokeDb.get(context).jokeDao().getLastUnreadJoke()
    }

    fun setJokeIsReaded(joke: Joke) {
        joke.isReading = true
        JokeDb.get(context).jokeDao().update(joke)
    }

    fun updateLocal() {
        updateInProgress.set(true)
        UmoriliService.create().randomJokeCall(AppSettings.jokeCountInRequest)
            .enqueue(object : Callback<UmoriliService.JokeResponse> {
                override fun onFailure(call: Call<UmoriliService.JokeResponse>, t: Throwable) {
                    updateInProgress.set(false)
                   //TODO error
                }

                override fun onResponse(
                    call: Call<UmoriliService.JokeResponse>,
                    response: Response<UmoriliService.JokeResponse>
                ) {
                    updateInProgress.set(false)
                    if (response.body() != null) {
                        val dbJokes = response.body()!!.map { it.toDb() }
                        JokeDb.get(context).jokeDao().insert(dbJokes)
                    }
                }
        })
    }
}
package ru.qdev.kudashov.jokes.model.repository

import android.content.Context
import android.os.AsyncTask
import androidx.databinding.ObservableBoolean
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import ru.qdev.kudashov.jokes.model.api.UmoriliService
import ru.qdev.kudashov.jokes.model.db.Joke
import ru.qdev.kudashov.jokes.model.db.JokeDb
import ru.qdev.kudashov.jokes.model.db.JokeList
import ru.qdev.kudashov.jokes.model.db.SQLQuery
import ru.qdev.kudashov.jokes.model.db.RoomQueryAdapter
import java.util.*

class JokeRepository(val context: Context) {
    val updateInProgress  = ObservableBoolean(false)

    private val jokeDb = JokeDb.get(context)
    private val jokeDbDao = jokeDb.jokeDao()
    private var randomJokeListDisposable: Disposable? = null

    private val filterSite = "anekdot.ru"
    private val filterNameChanel = "new anekdot"

    // From Api to Db mapping function
    fun UmoriliService.Joke.toDb() : Joke {
        val result = Joke()
//        result.content = this.desc ?: "А нет тут ничего!" //ToDo need description field add
        result.content = this.elementPureHtml ?: "А нет тут ничего!"
        result.dateUTC = Date().time
        result.link = this.link ?: ""
        result.nonUniqueId = this.link ?: ""
        return result
    }

    fun getNewJoke() : Flowable<JokeList> {
        return jokeDbDao.getLastUnreadJoke()
    }

    fun createAllJokesQueryAdapter(): RoomQueryAdapter<Joke> {
        return RoomQueryAdapter<Joke>(
            jokeDb, SQLQuery.allJokes.sql,
            SQLQuery.allJokes.useTables,
            SQLQuery.allJokes.convertToEntity
        )
    }

    fun setJokeIsReaded(joke: Joke) {
        AsyncTask.execute {
            joke.isReading = true
            jokeDbDao.update(joke)
        }
    }

    fun updateLocalFromApi() {
        updateInProgress.set(true)

        randomJokeListDisposable?.dispose()
        randomJokeListDisposable =
            UmoriliService.instance().randomJokeList(
                10,
                filterSite,
                filterNameChanel
            ).map {
                    response ->
                val filtered = response.filter {
                    it.site == filterSite && it.name == filterNameChanel
                }
                val jokes = filtered.map { it.toDb() }
                jokeDbDao.insertNew(jokes)
        }.subscribe { result, error ->
            updateInProgress.set(false)
        }
    }

    fun clear() {
        randomJokeListDisposable?.dispose()
        randomJokeListDisposable = null
    }
}
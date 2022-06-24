package ru.qdev.kudashov.jokes.repository

import android.content.Context
import android.os.AsyncTask
import androidx.databinding.ObservableBoolean
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import ru.qdev.kudashov.jokes.api.UmoriliService
import ru.qdev.kudashov.jokes.mapping.toDb
import ru.qdev.kudashov.jokes.db.JokeDb
import ru.qdev.kudashov.jokes.db.JokeList
import ru.qdev.kudashov.jokes.db.SQLQuery
import ru.qdev.kudashov.jokes.db.RoomQueryAdapter
import ru.qdev.kudashov.jokes.db.entry.JokeDbEntry

class JokeRepository(val context: Context) {
    val updateInProgress  = ObservableBoolean(false)

    private val jokeDb = JokeDb.get(context)
    private val jokeDbDao = jokeDb.jokeDao()
    private var randomJokeListDisposable: Disposable? = null

    private val filterSite = "anekdot.ru"
    private val filterNameChanel = "new anekdot"

    fun getNewJoke() : Flowable<JokeList> {
        return jokeDbDao.getLastUnreadJoke()
    }

    fun createAllJokesQueryAdapter(): RoomQueryAdapter<JokeDbEntry> {
        return RoomQueryAdapter<JokeDbEntry>(
            jokeDb, SQLQuery.allJokes.sql,
            SQLQuery.allJokes.useTables,
            SQLQuery.allJokes.convertToEntity
        )
    }

    fun setJokeIsReaded(joke: JokeDbEntry) {
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
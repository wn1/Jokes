package ru.qdev.kudashov.jokes.repository

import android.content.Context
import android.os.AsyncTask
import androidx.databinding.ObservableBoolean
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import ru.qdev.kudashov.jokes.api.UmoriliService
import ru.qdev.kudashov.jokes.mapping.toDb
import ru.qdev.kudashov.jokes.db.JokeDb
import ru.qdev.kudashov.jokes.db.RoomQueryAdapter
import ru.qdev.kudashov.jokes.db.dao.JokeDbEntryList
import ru.qdev.kudashov.jokes.db.entry.JokeDbEntry
import ru.qdev.kudashov.jokes.db.query.AllJokes

class JokeRepository(val context: Context) {
    val updateInProgress  = ObservableBoolean(false)

    private val jokeDb = JokeDb.get(context)
    private val jokeDbDao = jokeDb.jokeDao()
    private var randomJokeListDisposable: Disposable? = null

    private val filterSite = "anekdot.ru"
    private val filterNameChanel = "new anekdot"

    fun getNewJoke() : Flowable<JokeDbEntryList> {
        return jokeDbDao.getLastUnreadJoke()
    }

    fun createAllJokesQueryAdapter(): RoomQueryAdapter<JokeDbEntry> {
        return RoomQueryAdapter(
            jokeDb, AllJokes.query.sql,
            AllJokes.query.useTables,
            AllJokes.query.convertToEntity
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
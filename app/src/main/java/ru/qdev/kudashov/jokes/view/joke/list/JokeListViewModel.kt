package ru.qdev.kudashov.jokes.view.joke.list

import android.app.Application
import android.database.Cursor
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import ru.qdev.kudashov.jokes.repository.JokeRepository
import ru.qdev.kudashov.jokes.db.cursor.CursorQueryAdapter
import ru.qdev.kudashov.jokes.utils.WeakSubscriberArray
import ru.qdev.kudashov.jokes.view.AlertMessageSubscriber

interface JokeListViewSubscriber : AlertMessageSubscriber {
    fun onJokeListChanged()
}

class JokeListViewModel(application: Application) : AndroidViewModel(application), CursorQueryAdapter.ChangeNotify {

    val viewSubscribers = WeakSubscriberArray<JokeListViewSubscriber>()

    private val jokeRepository: JokeRepository =
        JokeRepository(application)

    val allJokesQueryAdapter = jokeRepository.createAllJokesQueryAdapter()

    init {
        allJokesQueryAdapter.changeSubscribers.addSubscriber(this)
    }

    override fun onChanged(cursor: Cursor?) {
        Handler(Looper.getMainLooper()).post {
            viewSubscribers.forEachSubscribers { it.onJokeListChanged() }
        }
    }

    override fun onCleared() {
        allJokesQueryAdapter.changeSubscribers.removeSubscriber(this)
        jokeRepository.clear()
        super.onCleared()
    }
}

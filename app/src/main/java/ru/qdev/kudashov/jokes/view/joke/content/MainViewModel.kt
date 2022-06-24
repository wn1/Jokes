package ru.qdev.kudashov.jokes.view.joke.content

import android.app.Application
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.view.View
import androidx.databinding.Observable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.qdev.kudashov.jokes.api.UmoriliService
import ru.qdev.kudashov.jokes.utils.AlertMessage
import ru.qdev.kudashov.jokes.utils.WeakSubscriberArray
import ru.qdev.kudashov.jokes.model.db.JokeList
import ru.qdev.kudashov.jokes.model.db.entry.JokeDbEntry
import ru.qdev.kudashov.jokes.repository.JokeRepository
import ru.qdev.kudashov.jokes.view.AlertMessageSubscriber

interface MainViewSubscriber : AlertMessageSubscriber {
    fun openUri(uri: Uri)
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application
    val viewSubscribers = WeakSubscriberArray<MainViewSubscriber>()
    private val jokeRepository: JokeRepository =
        JokeRepository(application)

    private val jokeContentEmpty : Spanned
        get() = SpannableString ("И это будет что-то!")

    val jokeContent = MutableLiveData<Spanned> (jokeContentEmpty)
    private var lastJoke: JokeDbEntry? = null
    private var newJokeDisposable: Disposable? = null

    private var updateInProgressCallback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            if (jokeRepository.updateInProgress.get()) {
                jokeContent.postValue(SpannableString("Ищу что-то интересненькое..."))
            }
            else
            {
                setContent(lastJoke?.content)
            }
        }
    }

    init {
        jokeRepository.updateInProgress.addOnPropertyChangedCallback(updateInProgressCallback)

        val newJoke = getNewJoke()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
        newJokeDisposable = newJoke
            .subscribe (::onJokeResponse, ::onErrorResponse)
    }

    override fun onCleared() {
        newJokeDisposable?.dispose()

        jokeRepository.updateInProgress.removeOnPropertyChangedCallback(updateInProgressCallback)
        jokeRepository.clear()
        super.onCleared()
    }

    fun openWebUrl() {
        if (!lastJoke?.link.isNullOrEmpty()) {
            viewSubscribers.forEachSubscribers {
                it.openUri(Uri.parse(lastJoke?.link))
            }
        } else {
            viewSubscribers.forEachSubscribers {
                it.alertMessage(AlertMessage("Страница потерялась, почитайте что-то ещё"))
            }
        }
    }

    fun openUmorili() {
        viewSubscribers.forEachSubscribers {
            it.openUri(Uri.parse(UmoriliService.wwwUrl))
        }
    }

    fun getNewJoke() : Flowable<JokeList> {
        return jokeRepository.getNewJoke()
    }

    fun onClickFloatingActionButton(view: View) {
        if (lastJoke!=null) {
            jokeRepository.setJokeIsReaded(lastJoke!!)
        }
        else{
            jokeRepository.updateLocalFromApi()
        }
    }

    private fun setContent(content: String?) {
        val contentPureHtml =
            content ?: "<p>Пока нет.</p><p>Мы уже что-то придумаваем, приходите ещё )</p>"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jokeContent.postValue(Html.fromHtml(contentPureHtml, Html.FROM_HTML_MODE_COMPACT))
        } else {
            jokeContent.postValue(Html.fromHtml(contentPureHtml))
        }
    }

    private fun onJokeResponse(jokeList: JokeList) {
        lastJoke = jokeList.firstOrNull()
        if (lastJoke == null) {
            jokeRepository.updateLocalFromApi()
        }

        if (!jokeRepository.updateInProgress.get()) {
            setContent(lastJoke?.content)
        }
    }

    private fun onErrorResponse(throwable: Throwable) {
        jokeContent.postValue(jokeContentEmpty)
        viewSubscribers.forEachSubscribers {
            it.alertMessage(
                AlertMessage(
                    "Ошибка: ${throwable.localizedMessage}",
                    throwable
                )
            )
        }
    }
}

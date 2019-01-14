package ru.qdev.kudashov.jokes.main.ui

import android.app.Application
import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.view.View
import androidx.databinding.Observable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.qdev.kudashov.jokes.AlertMessage
import ru.qdev.kudashov.jokes.db.Joke
import ru.qdev.kudashov.jokes.db.JokeList

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val alertObservable : PublishSubject<AlertMessage> = PublishSubject.create()

    private val jokeRepository: JokeRepository = JokeRepository(application)

    private val jokeContentEmpty : Spanned
        get() = SpannableString ("И это будет что-то!")

    val jokeContent = MutableLiveData<Spanned> (jokeContentEmpty)
    private var lastJoke: Joke? = null
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
        super.onCleared()
    }

    fun getNewJoke() : Flowable<JokeList> {
        return jokeRepository.getNewJoke()
    }

    fun onClickFloatingActionButton(view: View) {
        if (lastJoke!=null) {
            jokeRepository.setJokeIsReaded(lastJoke!!)
        }
        else{
            jokeRepository.updateLocal()
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
            jokeRepository.updateLocal()
        }

        if (!jokeRepository.updateInProgress.get()) { //ToDo to progress indicator change
            setContent(lastJoke?.content)
        }
    }

    private fun onErrorResponse(throwable: Throwable) {
        jokeContent.postValue(jokeContentEmpty)
        alertObservable.onNext (AlertMessage("Ошибка: ${throwable.localizedMessage}", throwable))
    }
}

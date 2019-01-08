package ru.qdev.kudashov.jokes.main.ui

import android.app.AlertDialog
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding3.view.clicks
import com.trello.rxlifecycle3.android.lifecycle.kotlin.bindToLifecycle
import com.trello.rxlifecycle3.components.support.RxFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.main_fragment.*
import ru.qdev.kudashov.jokes.UmoriliService
import ru.qdev.kudashov.jokes.R
import java.util.concurrent.TimeUnit

class MainFragment : RxFragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        floatingActionButton.clicks().subscribe {
            val newJoke = viewModel.getNewJoke()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .delaySubscription(2000, TimeUnit.MILLISECONDS)
                .bindToLifecycle(this)

            newJoke
//                .filter { it.items?.get(0) != null }
                .subscribe (::onJokeResponse, ::onErrorResponse)

//            newJoke
//                .filter {it.items?.get(0) == null}
//                .subscribe {
//                    var errorMessage = "Ошибка: пустой ответ"
//                    onErrorResponse(Throwable(errorMessage))
//                }

        }
    }

    private fun onJokeResponse(jokeResponse: UmoriliService.JokeResponse) {
        val contentPureHtml = jokeResponse[0].elementPureHtml
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            contentView.text = Html.fromHtml(contentPureHtml, Html.FROM_HTML_MODE_COMPACT)
        } else {
            contentView.text = Html.fromHtml(contentPureHtml)
        }
    }

    private fun onErrorResponse(throwable: Throwable) {
        AlertDialog.Builder(context)
            .setMessage("Ошибка: ${throwable.localizedMessage}")
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

}

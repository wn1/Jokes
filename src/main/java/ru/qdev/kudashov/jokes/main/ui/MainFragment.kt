package ru.qdev.kudashov.jokes.main.ui

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding3.view.clicks
import com.trello.rxlifecycle3.android.lifecycle.kotlin.bindToLifecycle
import com.trello.rxlifecycle3.components.support.RxFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.main_fragment.*
import ru.qdev.kudashov.jokes.AnecdoticaRuService
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
                .filter {it.result?.error == 0}
                .subscribe (::onJokeResponse, ::onErrorResponse)

            newJoke
                .filter {it.result?.error != 0}
                .subscribe {
                    var errorMessage = it.result?.errMsg
                    if (errorMessage==null || errorMessage.isEmpty()) {
                        errorMessage = "код ${it.result?.error}"
                    }
                    onErrorResponse(Throwable(errorMessage))
                }

        }
    }


    private fun onJokeResponse(jokeResponse: AnecdoticaRuService.JokeResponse){
        contentView.text = jokeResponse.item?.text
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

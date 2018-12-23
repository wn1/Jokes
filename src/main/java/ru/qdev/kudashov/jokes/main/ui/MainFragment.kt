package ru.qdev.kudashov.jokes.main.ui

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding3.view.clicks
import com.trello.rxlifecycle3.components.support.RxFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.main_fragment.*
import ru.qdev.kudashov.jokes.AnecdoticaRuService
import ru.qdev.kudashov.jokes.R
import java.util.*

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
        floatingActionButton.clicks().compose(bindToLifecycle()).subscribe {
            viewModel.getNewJoke()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .filter {it.result?.error == 0}
//                .subscribe (::onJokeResponse, ::onErrorResponse)
        }
    }


    fun onJokeResponse(jokeResponse: AnecdoticaRuService.JokeResponse){
        contentView.text = jokeResponse.item?.text
    }

    fun onErrorResponse(throwable: Throwable) {
        AlertDialog.Builder(context)
            .setNegativeButton("Ошибка: ${throwable.localizedMessage}", null)
            .show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

}

package ru.qdev.kudashov.jokes.main.ui

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.databinding.DataBindingUtil
import com.trello.rxlifecycle3.android.lifecycle.kotlin.bindToLifecycle
import com.trello.rxlifecycle3.components.support.RxFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.main_fragment.*
import ru.qdev.kudashov.jokes.R
import ru.qdev.kudashov.jokes.databinding.MainFragmentBinding
import ru.qdev.kudashov.jokes.ui.ScrollViewExtended

class MainFragment : RxFragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    lateinit var alertObservable: Disposable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: MainFragmentBinding = DataBindingUtil.inflate(layoutInflater, R.layout.main_fragment, container, false)
        val viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        binding.jokeViewModel = viewModel
        binding.setLifecycleOwner(this)

        alertObservable = viewModel.alertObservable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .bindToLifecycle(this)
            .subscribe {
                AlertDialog.Builder(context)
                    .setMessage(it.message)
                    .setNegativeButton("Отмена", null)
                    .show()
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scrollView.setOnScrollChangeSupportedListener(object : ScrollViewExtended.OnScrollChangeSupportListener {
            override fun onScrollChange(v: View, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
                val scrollV = v as ScrollView
                if (oldScrollY > scrollV.getChildAt(0).height - scrollV.height || oldScrollY < 0) return

                val deltaY = scrollY - oldScrollY
                if (deltaY > 0 && floatingActionButton.visibility == View.VISIBLE) {
                    floatingActionButton.hide()
                } else if (deltaY < 0 && floatingActionButton.visibility != View.VISIBLE) {
                    floatingActionButton.show()
                }
            }
        })
    }

    override fun onDestroyView() {
        alertObservable.dispose()
        super.onDestroyView()
    }
}

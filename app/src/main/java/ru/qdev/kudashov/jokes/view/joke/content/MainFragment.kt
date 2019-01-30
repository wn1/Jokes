package ru.qdev.kudashov.jokes.view.joke.content

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.databinding.DataBindingUtil
import com.trello.rxlifecycle3.components.support.RxFragment
import kotlinx.android.synthetic.main.main_fragment.*
import ru.qdev.kudashov.jokes.utils.AlertMessage
import ru.qdev.kudashov.jokes.R
import ru.qdev.kudashov.jokes.databinding.MainFragmentBinding
import ru.qdev.kudashov.jokes.ui.widget.ScrollViewExtended

class MainFragment : RxFragment() , MainViewSubscriber {
    companion object {
        fun newInstance() = MainFragment()
    }

    lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: MainFragmentBinding = DataBindingUtil.inflate(layoutInflater, R.layout.main_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        binding.jokeViewModel = viewModel
        binding.setLifecycleOwner(this)

        viewModel.viewSubscribers.addSubscriber(this)
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

    override fun alertMessage(alertMessage: AlertMessage) {
        AlertDialog.Builder(context)
            .setMessage(alertMessage.message)
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onDestroyView() {
        viewModel.viewSubscribers.removeSubscriber(this)
        super.onDestroyView()
    }
}

package ru.qdev.kudashov.jokes.view.joke.content

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import android.widget.ScrollView
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.main_fragment.*
import ru.qdev.kudashov.jokes.R
import ru.qdev.kudashov.jokes.databinding.MainFragmentBinding
import ru.qdev.kudashov.jokes.ui.widget.ScrollViewExtended
import ru.qdev.kudashov.jokes.view.BaseFragment
import ru.qdev.kudashov.jokes.view.joke.list.JokeListFragment
import ru.qdev.kudashov.jokes.view.joke.settings.SettingsFragment

class MainFragment : BaseFragment(), MainViewSubscriber {
    companion object {
        fun newInstance() = MainFragment()
    }

    lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId ) {
            R.id.jokesList -> fragmentManager?.beginTransaction()
                ?.addToBackStack(null)
                ?.replace(R.id.container, JokeListFragment.newInstance())
                ?.commit()
            R.id.settings -> fragmentManager?.beginTransaction()
                ?.addToBackStack(null)
                ?.replace(R.id.container, SettingsFragment.newInstance())
                ?.commit()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        viewModel.viewSubscribers.removeSubscriber(this)
        super.onDestroyView()
    }
}

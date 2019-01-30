package ru.qdev.kudashov.jokes.view.joke.list

import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.joke_list_fragment.*
import kotlinx.android.synthetic.main.joke_list_item.view.*
import ru.qdev.kudashov.jokes.R
import ru.qdev.kudashov.jokes.databinding.JokeListFragmentBinding
import ru.qdev.kudashov.jokes.view.BaseFragment

class JokeListFragment : BaseFragment() , JokeListViewSubscriber {
    companion object {
        fun newInstance() = JokeListFragment()
    }

    private lateinit var viewModel: JokeListViewModel

    class JokeListViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        val content = itemView.content
    }

    val adapter = object : RecyclerView.Adapter<JokeListViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JokeListViewHolder {
            return JokeListViewHolder(layoutInflater.inflate(R.layout.joke_list_item, parent, false))
        }

        override fun getItemCount(): Int {
            return viewModel.allJokesQueryAdapter.count
        }

        override fun onBindViewHolder(holder: JokeListViewHolder, position: Int) {
            val joke = viewModel.allJokesQueryAdapter.getItem(position)
            val contentPureHtml = joke?.content ?: "<p>Ничего нет</p>"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.content.text = Html.fromHtml(contentPureHtml, Html.FROM_HTML_MODE_COMPACT)
            } else {
                holder.content.text = Html.fromHtml(contentPureHtml)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: JokeListFragmentBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.joke_list_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(JokeListViewModel::class.java)
        binding.jokeViewModel = viewModel
        binding.setLifecycleOwner(this)
        viewModel.viewSubscribers.addSubscriber(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    override fun onJokeListChanged() {
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        viewModel.viewSubscribers.removeSubscriber(this)
        super.onDestroyView()
    }
}

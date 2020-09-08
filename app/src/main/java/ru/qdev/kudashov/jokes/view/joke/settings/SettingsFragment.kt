package ru.qdev.kudashov.jokes.view.joke.settings

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import ru.qdev.kudashov.jokes.R
import ru.qdev.kudashov.jokes.databinding.SettingsFragmentBinding

class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: SettingsFragmentBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.settings_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        binding.settingsViewModel = viewModel
        binding.setLifecycleOwner(this)
        return binding.root
}

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}

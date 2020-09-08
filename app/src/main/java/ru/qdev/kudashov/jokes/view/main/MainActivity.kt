package ru.qdev.kudashov.jokes.view.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.qdev.kudashov.jokes.R
import ru.qdev.kudashov.jokes.view.joke.content.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }

}

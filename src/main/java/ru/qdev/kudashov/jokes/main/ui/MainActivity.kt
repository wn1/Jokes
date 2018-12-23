package ru.qdev.kudashov.jokes.main.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.qdev.kudashov.jokes.R

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

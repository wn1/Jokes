package ru.qdev.kudashov.jokes.db

import android.content.Context
import android.database.Cursor
import androidx.room.*
import androidx.room.Database
import io.reactivex.rxjava3.core.Flowable
import ru.qdev.kudashov.jokes.db.dao.JokeDao
import ru.qdev.kudashov.jokes.db.entry.JokeDbEntry

@Database(entities = [JokeDbEntry::class], version = 1)
abstract class JokeDb : RoomDatabase() {

    companion object {
        private const val databaseName = "jokeBase"

        fun get (context: Context): JokeDb {
            return Room.databaseBuilder(context, JokeDb::class.java,
                databaseName
            ).build()
        }
    }

    abstract fun jokeDao(): JokeDao
}







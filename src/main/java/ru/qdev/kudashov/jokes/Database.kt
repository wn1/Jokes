package ru.qdev.kudashov.jokes

import androidx.room.*
import androidx.room.Database
import io.reactivex.Flowable

@Database(entities = arrayOf(Joke::class), version = 1)
abstract class Database : RoomDatabase() {
    abstract fun jokeDao(): JokeDao
}

@Entity
class Joke {
    @PrimaryKey
    var id: Long = 0
    var content: String = ""
    var dateUTC: Long = 0
}

typealias JokeList = List<Joke>

@Dao
interface JokeDao {
    @Query("SELECT * FROM joke")
    fun allJokes() : Flowable<JokeList>

    @Query("SELECT * FROM joke WHERE dateUTC = (SELECT max(dateUTC) FROM joke) LIMIT 1")
    fun getLastJoke() : Flowable<Joke>

    @Insert
    fun insert(joke: Joke)

    @Query("DELETE FROM joke WHERE dateUTC < :dateUTC")
    fun deleteBefore(dateUTC: Long)
}




package ru.qdev.kudashov.jokes.db

import android.content.Context
import androidx.room.*
import androidx.room.Database
import io.reactivex.Flowable

@Database(entities = [Joke::class], version = 1)
abstract class JokeDb : RoomDatabase() {
    companion object {
        val databaseName = "jokeBase"

        fun get (context: Context): JokeDb {
            return Room.databaseBuilder(context, JokeDb::class.java, databaseName).build()
        }
    }
    abstract fun jokeDao(): JokeDao
}

@Entity
class Joke {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var content: String = ""
    var dateUTC: Long = 0
    var link: String = ""
    var nonUniqueId: String = ""
    var isReading: Boolean = false
}

typealias JokeList = List<Joke>

@Dao
interface JokeDao {
    @Query("SELECT * FROM joke")
    fun allJokes() : Flowable<JokeList>

    @Query("SELECT * FROM joke WHERE dateUTC = (SELECT max(dateUTC) FROM joke) AND NOT isReading LIMIT 1")
    fun getLastUnreadJoke() : Flowable<JokeList> //JokeList to maybe return empty result

    @Insert
    fun insert(joke: Joke)

    @Insert
    fun insert(jokes: List<Joke>)

    @Update
    fun update(joke: Joke)

    @Query("DELETE FROM joke WHERE dateUTC < :dateUTC")
    fun deleteBefore(dateUTC: Long)
}




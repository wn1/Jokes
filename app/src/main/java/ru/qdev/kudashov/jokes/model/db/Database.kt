package ru.qdev.kudashov.jokes.model.db

import android.content.Context
import android.database.Cursor
import androidx.room.*
import androidx.room.Database
import io.reactivex.rxjava3.core.Flowable
import ru.qdev.kudashov.jokes.model.db.entry.JokeDbEntry

@Database(entities = [JokeDbEntry::class], version = 1)
abstract class JokeDb : RoomDatabase() {

    companion object {
        val databaseName = "jokeBase"

        fun get (context: Context): JokeDb {
            return Room.databaseBuilder(context, JokeDb::class.java,
                databaseName
            ).build()
        }
    }

    abstract fun jokeDao(): JokeDao
}



typealias JokeList = List<JokeDbEntry>


class SQLQuery (val sql: String, val useTables : Array<String>, val convertToEntity: (Cursor) -> JokeDbEntry){
    companion object {
        val allJokes = SQLQuery(
            "SELECT id, content, dateUTC, link, nonUniqueId, isReading FROM joke",
            arrayOf("joke")
        ) {
            val joke = JokeDbEntry()
            joke.id = it.getLong(0)
            joke.content = it.getString(1)
            joke.dateUTC = it.getLong(2)
            joke.nonUniqueId = it.getString(4)
            joke.isReading = it.getInt(5) != 0
            joke
        }
    }

}


@Dao
interface JokeDao {
    @Query("SELECT * FROM joke")
    fun allJokes() : Flowable<JokeList>

    @Query("SELECT * FROM joke WHERE dateUTC = (SELECT max(dateUTC) FROM joke) AND NOT isReading LIMIT 1")
    fun getLastUnreadJoke() : Flowable<JokeList> //JokeList to maybe return empty result

    @Query("SELECT * FROM joke WHERE nonUniqueId = :nonUniqueID AND content = :content LIMIT 1")
    fun getRepeatJoke(nonUniqueID: String, content: String) : JokeList //JokeList to maybe return empty result

    @Insert
    fun insert(joke: JokeDbEntry)

    @Insert()
    fun insert(jokes: List<JokeDbEntry>)

    @Transaction
    fun insertNew(jokes: List<JokeDbEntry>) {
        for (joke in jokes) {
            if (getRepeatJoke(joke.nonUniqueId, joke.content).isEmpty()) {
                insert(joke)
            }
        }
    }

    @Update
    fun update(joke: JokeDbEntry)

    @Query("DELETE FROM joke WHERE dateUTC < :dateUTC")
    fun deleteBefore(dateUTC: Long)
}




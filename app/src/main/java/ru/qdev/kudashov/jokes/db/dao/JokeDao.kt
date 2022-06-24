package ru.qdev.kudashov.jokes.db.dao

import androidx.room.*
import io.reactivex.rxjava3.core.Flowable
import ru.qdev.kudashov.jokes.db.entry.JokeDbEntry

typealias JokeDbEntryList = List<JokeDbEntry>

@Dao
interface JokeDao {

    @Query("SELECT * FROM joke")
    fun allJokes() : Flowable<JokeDbEntryList>

    @Query("SELECT * FROM joke WHERE dateUTC = (SELECT max(dateUTC) FROM joke) AND NOT isReading LIMIT 1")
    fun getLastUnreadJoke() : Flowable<JokeDbEntryList> //JokeList to maybe return empty result

    @Query("SELECT * FROM joke WHERE nonUniqueId = :nonUniqueID AND content = :content LIMIT 1")
    fun getRepeatJoke(nonUniqueID: String, content: String) : JokeDbEntryList //JokeList to maybe return empty result

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
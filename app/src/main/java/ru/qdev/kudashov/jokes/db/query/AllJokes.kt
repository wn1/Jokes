package ru.qdev.kudashov.jokes.db.query

import ru.qdev.kudashov.jokes.db.entry.JokeDbEntry

class AllJokes {
    companion object {
        val query = SQLQuery(
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
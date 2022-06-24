package ru.qdev.kudashov.jokes.db.entry

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "joke")
class JokeDbEntry {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var content: String = ""
    var dateUTC: Long = 0
    var link: String = ""
    var nonUniqueId: String = ""
    var isReading: Boolean = false
}
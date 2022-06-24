package ru.qdev.kudashov.jokes.db.query

import android.database.Cursor
import ru.qdev.kudashov.jokes.db.entry.JokeDbEntry

class SQLQuery (val sql: String,
                val useTables : Array<String>,
                val convertToEntity: (Cursor) -> JokeDbEntry)
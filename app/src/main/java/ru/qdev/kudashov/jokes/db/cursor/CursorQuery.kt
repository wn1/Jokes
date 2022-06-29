package ru.qdev.kudashov.jokes.db.cursor

import android.database.Cursor

class CursorQuery<T_entity> (val sql: String,
                   val args: Array<Any>? = null,
                   val useTables : Array<String>,
                   val convertToEntity: (Cursor) -> T_entity)
package ru.qdev.kudashov.jokes.mapping

import ru.qdev.kudashov.jokes.api.model.JokeApiModel
import ru.qdev.kudashov.jokes.model.db.entry.JokeDbEntry
import java.util.*

// From Api to Db mapping function
fun JokeApiModel.toDb() : JokeDbEntry {
    val result = JokeDbEntry()
//        result.content = this.desc ?: "А нет тут ничего!" //ToDo need description field add
    result.content = this.elementPureHtml ?: "А нет тут ничего!"
    result.dateUTC = Date().time
    result.link = this.link ?: ""
    result.nonUniqueId = this.link ?: ""
    return result
}


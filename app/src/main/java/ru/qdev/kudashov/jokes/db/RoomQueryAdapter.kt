package ru.qdev.kudashov.jokes.db

import android.database.Cursor
import android.database.DataSetObserver
import android.os.AsyncTask
import androidx.room.InvalidationTracker
import androidx.room.RoomDatabase
import ru.qdev.kudashov.jokes.utils.WeakSubscriberArray

class RoomQueryAdapter<T_entity> (val database: RoomDatabase, val query: String, val useTables: Array<String>,
                                           val convertToEntity: (Cursor) -> T_entity, val args: Array<Any>? = null) {

    interface ChangeNotify {
        fun onChanged(cursor: Cursor?)
    }

    private var cursor: Cursor? = null

    val changeSubscribers =
        WeakSubscriberArray<ChangeNotify>()

    val count: Int
        get() = cursor?.count ?: 0

    private val tablesObserver = object : InvalidationTracker.Observer( useTables) {
        override fun onInvalidated(tables: MutableSet<String>) {
            updateCursor()
        }
    }

    private val dataSetObserver = object : DataSetObserver() {
        override fun onChanged() {
            super.onChanged()
            changeSubscribers.forEachSubscribers { it.onChanged(cursor) }
        }

        override fun onInvalidated() {
            super.onInvalidated()
            changeSubscribers.forEachSubscribers { it.onChanged(cursor) }
        }
    }

    fun updateCursor(){
        AsyncTask.execute {
            cursor?.unregisterDataSetObserver(dataSetObserver)
            database.invalidationTracker.addObserver(tablesObserver)
            cursor = database.query(query, args)
            cursor?.registerDataSetObserver(dataSetObserver)
            changeSubscribers.forEachSubscribers { it.onChanged(cursor) }
        }
    }

    init {
        updateCursor()
    }

    fun getItem(position: Int): T_entity? {
        if (cursor==null || cursor!!.isClosed) {
            return null
        }

        cursor!!.moveToPosition(position)

        if (cursor!!.isAfterLast || cursor!!.isBeforeFirst) {
            return null
        }

        return convertToEntity(cursor!!)
    }


    protected fun finalize() {
        unregisterDataSetObserver()
    }

    fun unregisterDataSetObserver() {
        cursor?.unregisterDataSetObserver(dataSetObserver)
        database.invalidationTracker.removeObserver(tablesObserver)
    }

}
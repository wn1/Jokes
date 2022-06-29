package ru.qdev.kudashov.jokes.db.cursor

import android.database.Cursor
import android.database.DataSetObserver
import android.util.Log
import androidx.room.InvalidationTracker
import androidx.room.RoomDatabase
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.qdev.kudashov.jokes.utils.WeakSubscriberArray

class CursorQueryAdapter<T_entity> (
    private val database: RoomDatabase,
    private val query: CursorQuery<T_entity>
    ) {

    interface ChangeNotify {
        fun onChanged(cursor: Cursor?)
    }

    private var cursor: Cursor? = null

    val changeSubscribers =
        WeakSubscriberArray<ChangeNotify>()

    val count: Int
        get() = cursor?.count ?: 0

    private val tablesObserver = object : InvalidationTracker.Observer(query.useTables) {
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
        Single
            .fromCallable {
                cursor?.unregisterDataSetObserver(dataSetObserver)
                database.invalidationTracker.addObserver(tablesObserver)
                cursor = database.query(query.sql, query.args)
                cursor?.registerDataSetObserver(dataSetObserver)
                changeSubscribers.forEachSubscribers { it.onChanged(cursor) }
            }
            .subscribeOn(Schedulers.io())
            .subscribe { _, e ->
                if (e != null) {
                    Log.e(javaClass.simpleName, "Error: ${e.message}")
                }
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

        return query.convertToEntity(cursor!!)
    }


    protected fun finalize() {
        unregisterDataSetObserver()
    }

    fun unregisterDataSetObserver() {
        cursor?.unregisterDataSetObserver(dataSetObserver)
        database.invalidationTracker.removeObserver(tablesObserver)
    }
}
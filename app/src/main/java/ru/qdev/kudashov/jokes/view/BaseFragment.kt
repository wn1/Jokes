package ru.qdev.kudashov.jokes.view

import android.app.AlertDialog
import com.trello.rxlifecycle3.components.support.RxFragment
import ru.qdev.kudashov.jokes.utils.AlertMessage

open class BaseFragment : RxFragment(), AlertMessageSubscriber {
    override fun alertMessage(alertMessage: AlertMessage) {
        AlertDialog.Builder(context)
            .setMessage(alertMessage.message)
            .setNegativeButton("Отмена", null)
            .show()
    }
}

package ru.qdev.kudashov.jokes.view

import android.app.AlertDialog
import androidx.fragment.app.Fragment
import ru.qdev.kudashov.jokes.utils.AlertMessage

open class BaseFragment : Fragment(), AlertMessageSubscriber {
    override fun alertMessage(alertMessage: AlertMessage) {
        AlertDialog.Builder(context)
            .setMessage(alertMessage.message)
            .setNegativeButton("Отмена", null)
            .show()
    }
}

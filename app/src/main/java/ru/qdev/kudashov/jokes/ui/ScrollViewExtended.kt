package ru.qdev.kudashov.jokes.ui

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.ScrollView

class ScrollViewExtended : ScrollView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int, defStyleRes: Int)
            : super(context, attrs, defStyle, defStyleRes)

    interface OnScrollChangeSupportListener {
        fun onScrollChange(v: View, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int)
    }

    var onScrollChangeSupportListener: OnScrollChangeSupportListener? = null

    fun setOnScrollChangeSupportedListener(l: OnScrollChangeSupportListener?) {
        onScrollChangeSupportListener = l
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        onScrollChangeSupportListener?.onScrollChange(this, l, t, oldl, oldt)
    }
}
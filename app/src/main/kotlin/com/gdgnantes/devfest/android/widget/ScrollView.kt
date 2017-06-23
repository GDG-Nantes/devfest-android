package com.gdgnantes.devfest.android.widget

import android.content.Context
import android.support.v4.widget.NestedScrollView
import android.util.AttributeSet

class ScrollView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr) {

    var onScrollChangeListener: ((NestedScrollView, Int, Int) -> Unit)? = null

    override fun onScrollChanged(left: Int, top: Int, oldLeft: Int, oldTop: Int) {
        super.onScrollChanged(left, top, oldLeft, oldTop)
        onScrollChangeListener?.invoke(this, top, oldTop)
    }

}
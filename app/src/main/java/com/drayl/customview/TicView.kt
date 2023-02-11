package com.drayl.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View

class TicView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attributeSet, defStyleAttr, defStyleRes) {

    init {
        if (attributeSet != null) {
            initAttribute(attributeSet, defStyleAttr, defStyleRes)
        }
    }

    private fun initAttribute(attributeSet: AttributeSet, defStyleAttr: Int, defStyleRes: Int) {

    }
}
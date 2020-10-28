package com.duoshine.douyin.widget

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.widget.TextView

/**
 *Created by chen on 2020
 */
class CTextView : TextView {

    private val TAG = "CTextView"

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {}

    constructor(context: Context, attrs: AttributeSet?, defAttr: Int) : super(
        context,
        attrs,
        defAttr
    )

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val top = IntArray(2)
        this.getLocationInWindow(top)
        val height = this.height
        val y = top[1]
        Log.d(TAG, "duo_shine--:${top[0]}-${top[1]}")
        Log.d(TAG, "duo_shine--:$height")
    }
}
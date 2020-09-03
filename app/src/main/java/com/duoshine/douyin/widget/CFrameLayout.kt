package com.duoshine.douyin.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout

/**
 *Created by chen on 2020
 */
class CFrameLayout : FrameLayout {

    private var paint: Paint? = null

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {
        paint = Paint()
        paint?.color = Color.BLACK
        paint?.style =  Paint.Style.FILL
//        paint?.style = Paint.Style.STROKE;
//        paint?.strokeWidth = 5f
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
//        canvas.drawColor(Color.GREEN)
        canvas.drawRoundRect(RectF(0f, height.toFloat() - 40f, width.toFloat(), height.toFloat()), 20f, 20f, paint!!)
        canvas.drawRect(RectF(0f, 0f, width.toFloat(), height.toFloat()-20f), paint!!)
    }
}
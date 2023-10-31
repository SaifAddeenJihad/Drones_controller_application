package com.example.mydrone

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.*
import android.graphics.Paint
import android.view.View

class CircleCanvas(context: Context, private val centerX: Float, private val centerY: Float, private val radius: Float) : View(context) {
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        var paint=Paint()
        paint.color = BLACK
        paint.style=Paint.Style.STROKE
        paint.strokeWidth = 5f
        canvas?.drawCircle(centerX,centerY,radius,paint)
    }
}
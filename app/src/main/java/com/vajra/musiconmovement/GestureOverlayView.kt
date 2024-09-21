package com.vajra.musiconmovement

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View


class GestureOverlayView : View {
    private var gesturePoints: List<PointF>? = null
    private var paint: Paint? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        paint = Paint()
        paint!!.color = Color.RED
        paint!!.strokeWidth = 10f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (gesturePoints != null) {
            for (point in gesturePoints!!) {
                canvas.drawCircle(point.x, point.y, 10f, paint!!)
            }
        }
    }

    fun setGesturePoints(points: List<PointF>?) {
        this.gesturePoints = points
        invalidate()
    }
}

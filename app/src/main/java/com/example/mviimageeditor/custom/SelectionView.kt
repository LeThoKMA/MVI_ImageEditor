package com.example.mviimageeditor.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.max
import kotlin.math.min


class SelectionView : View {
    private val selectionRect: Rect = Rect()
    private val paint: Paint = Paint()
    private var isSelecting = false
    private val isDraggingCorner = false
    private val cornerSize = 30 // Kích thước vùng kiểm tra góc

    private var startX = 0f
    private var startY = 0f
    private var dragStartX = 0f
    private var dragStartY = 0f

    private enum class DraggingCorner {
        NONE, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    private var draggingCorner = DraggingCorner.NONE

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
        paint.setStyle(Paint.Style.STROKE)
        paint.setColor(android.graphics.Color.RED)
        paint.strokeWidth = 5f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (selectionRect.width() > 0 && selectionRect.height() > 0) {
            canvas.drawRect(selectionRect, paint)
            drawCorners(canvas)
        }
    }

    private fun drawCorners(canvas: Canvas) {
        // Vẽ các góc nhỏ để người dùng có thể kéo
        paint.setStyle(Paint.Style.FILL)
        paint.setColor(android.graphics.Color.RED)
        canvas.drawRect(
            selectionRect.left - cornerSize / 2f,
            selectionRect.top - cornerSize / 2f,
            selectionRect.left + cornerSize / 2f,
            selectionRect.top + cornerSize / 2f,
            paint
        ) // TOP_LEFT
        canvas.drawRect(
            selectionRect.right - cornerSize / 2f,
            selectionRect.top - cornerSize / 2f,
            selectionRect.right + cornerSize / 2f,
            selectionRect.top + cornerSize / 2f,
            paint
        ) // TOP_RIGHT
        canvas.drawRect(
            selectionRect.left - cornerSize / 2f,
            selectionRect.bottom - cornerSize / 2f,
            selectionRect.left + cornerSize / 2f,
            selectionRect.bottom + cornerSize / 2f,
            paint
        ) // BOTTOM_LEFT
        canvas.drawRect(
            selectionRect.right - cornerSize / 2f,
            selectionRect.bottom - cornerSize / 2f,
            selectionRect.right + cornerSize / 2f,
            selectionRect.bottom + cornerSize / 2f,
            paint
        ) // BOTTOM_RIGHT
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (isCornerTouched(x, y)) {
                draggingCorner = getDraggingCorner(x, y)
                dragStartX = x
                dragStartY = y
            } else {
                startX = x
                startY = y
                isSelecting = true
            }

            MotionEvent.ACTION_MOVE -> if (draggingCorner != DraggingCorner.NONE) {
                adjustRect(x, y)
            } else if (isSelecting) {
                val left = min(startX.toDouble(), x.toDouble()).toFloat()
                val top = min(startY.toDouble(), y.toDouble()).toFloat()
                val right = max(startX.toDouble(), x.toDouble()).toFloat()
                val bottom = max(startY.toDouble(), y.toDouble()).toFloat()
                selectionRect.set(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                isSelecting = false
                draggingCorner = DraggingCorner.NONE
            }
        }
        return true
    }

    private fun isCornerTouched(x: Float, y: Float): Boolean {
        return (Math.abs(x - selectionRect.left) < cornerSize && Math.abs(y - selectionRect.top) < cornerSize) ||
                (Math.abs(x - selectionRect.right) < cornerSize && Math.abs(y - selectionRect.top) < cornerSize) ||
                (Math.abs(x - selectionRect.left) < cornerSize && Math.abs(y - selectionRect.bottom) < cornerSize) ||
                (Math.abs(x - selectionRect.right) < cornerSize && Math.abs(y - selectionRect.bottom) < cornerSize)
    }

    private fun getDraggingCorner(x: Float, y: Float): DraggingCorner {
        if (Math.abs(x - selectionRect.left) < cornerSize && Math.abs(y - selectionRect.top) < cornerSize) {
            return DraggingCorner.TOP_LEFT
        } else if (Math.abs(x - selectionRect.right) < cornerSize && Math.abs(y - selectionRect.top) < cornerSize) {
            return DraggingCorner.TOP_RIGHT
        } else if (Math.abs(x - selectionRect.left) < cornerSize && Math.abs(y - selectionRect.bottom) < cornerSize) {
            return DraggingCorner.BOTTOM_LEFT
        } else if (Math.abs(x - selectionRect.right) < cornerSize && Math.abs(y - selectionRect.bottom) < cornerSize) {
            return DraggingCorner.BOTTOM_RIGHT
        }
        return DraggingCorner.NONE
    }

    private fun adjustRect(x: Float, y: Float) {
        when (draggingCorner) {
            DraggingCorner.TOP_LEFT -> {
                selectionRect.left = Math.min(x, selectionRect.right.toFloat()).toInt()
                selectionRect.top = Math.min(y, selectionRect.bottom.toFloat()).toInt()
            }

            DraggingCorner.TOP_RIGHT -> {
                selectionRect.right = Math.max(x, selectionRect.left.toFloat()).toInt()
                selectionRect.top = Math.min(y, selectionRect.bottom.toFloat()).toInt()
            }

            DraggingCorner.BOTTOM_LEFT -> {
                selectionRect.left = Math.min(x, selectionRect.right.toFloat()).toInt()
                selectionRect.bottom = Math.max(y, selectionRect.top.toFloat()).toInt()
            }

            DraggingCorner.BOTTOM_RIGHT -> {
                selectionRect.right = Math.max(x, selectionRect.left.toFloat()).toInt()
                selectionRect.bottom = Math.max(y, selectionRect.top.toFloat()).toInt()
            }

            DraggingCorner.NONE -> TODO()
        }
        invalidate()
    }

    fun getSelectionRect(): Rect {
        return selectionRect
    }
}

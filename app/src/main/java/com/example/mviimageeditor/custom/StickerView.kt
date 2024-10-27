//package com.example.mviimageeditor.custom
//
//import android.content.Context
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.Paint
//import android.graphics.Rect
//import android.util.AttributeSet
//import android.view.Gravity
//import android.view.MotionEvent
//import android.view.View
//import android.view.View.OnTouchListener
//import android.view.ViewGroup
//import android.widget.FrameLayout
//import android.widget.ImageView
//import android.widget.TextView
//import kotlin.math.abs
//import kotlin.math.max
//import kotlin.math.round
//import kotlin.math.roundToInt
//
//class StickerView : FrameLayout {
//
//    companion object {
//        const val TAG = "StickerView"
//        fun convertDpToPixel(dp: Float, context: Context): Int {
//            val resources = context.resources
//            val metrics = resources.displayMetrics
//            val px = dp * (metrics.densityDpi / 160f)
//            return px.toInt()
//        }
//    }
//
//    private var ivBorder: BorderView? = null
//    private var ivActiveBorder: ActiveBorderView? = null
//    private var ivDelete: ImageView? = null
//    private var ivVerticalScaleBottom: ImageView? = null
//    private var ivVerticalScaleTop: ImageView? = null
//    private var ivHorizontalScaleRight: ImageView? = null
//    private var ivHorizontalScaleLeft: ImageView? = null
//    private var ivScaleTopRight: ImageView? = null
//    private var ivScaleTopLeft: ImageView? = null
//    private var ivScaleBottomRight: ImageView? = null
//    private var ivScaleBottomLeft: ImageView? = null
//    private var tvFrameOrder: TextView? = null
//
//    private var color = 0
//
//    // For scalling
//    private var orgX = -1f
//    private var orgY = -1f
//    private var scaleOrgX = -1f
//    private var scaleOrgY = -1f
//
//    // For moving
//    private var moveOrgX = -1f
//    private var moveOrgY = -1f
//    private var borderPaint: Paint? = null
//    private var toggleActive = false
//    var frameOrder: Int? = null
//    private var margin = convertDpToPixel(AppConstants.STICKER_VIEW_MARGIN, context)
//    private val colorRed = context.resources.getColor(R.color.colorRedFrame, null)
//    private val colorGreen = context.resources.getColor(R.color.colorGreenFrame, null)
//    var isButtonSwitch = false
//    private var myCanvas = Canvas()
//
//    constructor(context: Context, color: Int, toggleActive: Boolean, frameOrder: Int) : super(
//        context
//    ) {
//        init(context, frameOrder, margin)
//        this.color = color
//        this.toggleActive = toggleActive
//        this.frameOrder = frameOrder
//    }
//
//    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)
//
//    constructor(
//        context: Context?,
//        attrs: AttributeSet?,
//        defStyle: Int
//    ) : super(context!!, attrs, defStyle)
//
//    private fun init(context: Context, frameOrder: Int, margin: Int) {
//        ivBorder = BorderView(context)
//        ivDelete = ImageView(context)
//        ivActiveBorder = ActiveBorderView(context)
//        ivVerticalScaleTop = ImageView(context)
//        ivVerticalScaleBottom = ImageView(context)
//        ivHorizontalScaleRight = ImageView(context)
//        ivHorizontalScaleLeft = ImageView(context)
//        ivScaleTopRight = ImageView(context)
//        ivScaleTopLeft = ImageView(context)
//        ivScaleBottomLeft = ImageView(context)
//        ivScaleBottomRight = ImageView(context)
//        tvFrameOrder = TextView(context)
//        ivScaleTopRight!!.setImageResource(R.drawable.corner_right_top)
//        ivScaleTopLeft!!.setImageResource(R.drawable.corner_left_top)
//        ivScaleBottomLeft!!.setImageResource(R.drawable.corner_left_bot)
//        ivScaleBottomRight!!.setImageResource(R.drawable.corner_right_bot)
//        ivDelete!!.setImageResource(R.drawable.remove)
//        this.tag = "DraggableViewGroup"
//        ivActiveBorder!!.tag = "ivActiveBorder"
//        ivBorder!!.tag = "ivBorder"
//        ivDelete!!.tag = "ivDelete"
//        ivHorizontalScaleRight!!.tag = "ivHorizontalScale"
//        ivHorizontalScaleLeft!!.tag = "ivHorizontalScaleLeft"
//        ivVerticalScaleTop!!.tag = "ivVerticalScaleTop"
//        ivVerticalScaleBottom!!.tag = "ivVerticalScale"
//        ivScaleTopRight!!.tag = "ivScaleTopRight"
//        ivScaleTopLeft!!.tag = "ivScaleTopLeft"
//        ivScaleBottomLeft!!.tag = "ivScaleBottomLeft"
//        ivScaleBottomRight!!.tag = "ivScaleBottomRight"
//        val borderSize = convertDpToPixel(AppConstants.MIN_VIEW_FRAME_SIZE, context) / 2
//        val size = margin
//        val thisParams = LayoutParams(size, size)
//        thisParams.gravity = Gravity.CENTER
//
//        val ivMainParams = LayoutParams(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.MATCH_PARENT
//        )
//        ivMainParams.setMargins(margin, margin, margin, margin)
//
//        val ivBorderParams = LayoutParams(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.MATCH_PARENT
//        )
//        ivBorderParams.setMargins(margin, margin, margin, margin)
//
//        val ivScaleHorizontalParams = LayoutParams(
//            margin, ViewGroup.LayoutParams.MATCH_PARENT
//        )
//        ivScaleHorizontalParams.gravity = Gravity.END or Gravity.CENTER
//
//        val ivScaleHorizontalLeftParams = LayoutParams(
//            margin, ViewGroup.LayoutParams.MATCH_PARENT
//        )
//        ivScaleHorizontalLeftParams.gravity = Gravity.START or Gravity.CENTER
//
//        val ivScaleVerticalParams = LayoutParams(
//            ViewGroup.LayoutParams.MATCH_PARENT, margin
//        )
//        ivScaleVerticalParams.gravity = Gravity.BOTTOM or Gravity.CENTER
//
//        val ivScaleVerticalTopParams = LayoutParams(
//            ViewGroup.LayoutParams.MATCH_PARENT, margin
//        )
//        ivScaleVerticalTopParams.gravity = Gravity.TOP or Gravity.CENTER
//
//        val ivScaleTopRightParam = LayoutParams(
//            borderSize, borderSize
//        )
//        ivScaleTopRightParam.gravity = Gravity.TOP or Gravity.END
//
//        val ivScaleTopLeftParam = LayoutParams(
//            borderSize, borderSize
//        )
//        ivScaleTopLeftParam.gravity = Gravity.TOP or Gravity.START
//
//        val ivScaleBottomLeftParam = LayoutParams(
//            borderSize, borderSize
//        )
//        ivScaleBottomLeftParam.gravity = Gravity.BOTTOM or Gravity.START
//
//        val ivScaleBottomRightParam = LayoutParams(
//            borderSize, borderSize
//        )
//        ivScaleBottomRightParam.gravity = Gravity.BOTTOM or Gravity.END
//
//        val tvFrameOrderLayoutParams = LayoutParams(margin - 1, margin - 1)
//        tvFrameOrderLayoutParams.gravity = Gravity.TOP or Gravity.START
//        tvFrameOrderLayoutParams.setMargins(margin, 0, margin, margin)
//        tvFrameOrder!!.gravity = Gravity.CENTER
//        tvFrameOrder!!.textSize = AppConstants.FRAME_VIEW_TEXT_SIZE
//        tvFrameOrder!!.setTextColor(Color.WHITE)
//        tvFrameOrder!!.setBackgroundColor(colorRed)
//        tvFrameOrder!!.includeFontPadding = false
//        this.layoutParams = thisParams
//        this.addView(ivBorder, ivBorderParams)
//        this.addView(ivActiveBorder, ivBorderParams)
//        this.addView(ivHorizontalScaleRight, ivScaleHorizontalParams)
//        this.addView(ivHorizontalScaleLeft, ivScaleHorizontalLeftParams)
//        this.addView(ivVerticalScaleBottom, ivScaleVerticalParams)
//        this.addView(ivVerticalScaleTop, ivScaleVerticalTopParams)
//        this.addView(tvFrameOrder, tvFrameOrderLayoutParams)
//        this.addView(ivScaleTopRight, ivScaleTopRightParam)
//        this.addView(ivScaleTopLeft, ivScaleTopLeftParam)
//        this.addView(ivScaleBottomLeft, ivScaleBottomLeftParam)
//        this.addView(ivScaleBottomRight, ivScaleBottomRightParam)
//        setOnTouchListener(mTouchListener)
////        ivHorizontalScaleRight!!.setOnTouchListener(mTouchListener)
////        ivHorizontalScaleLeft!!.setOnTouchListener(mTouchListener)
////        ivVerticalScaleBottom!!.setOnTouchListener(mTouchListener)
////        ivVerticalScaleTop!!.setOnTouchListener(mTouchListener)
//        ivScaleTopRight!!.setOnTouchListener(mTouchListener)
//        ivScaleTopLeft!!.setOnTouchListener(mTouchListener)
//        ivScaleBottomLeft!!.setOnTouchListener(mTouchListener)
//        ivScaleBottomRight!!.setOnTouchListener(mTouchListener)
//
//        ivActiveBorder!!.visibility = View.GONE
//        if (frameOrder != 99) {
//            tvFrameOrder!!.text = frameOrder.toString()
//            tvFrameOrder!!.visibility = View.VISIBLE
//        } else {
//            tvFrameOrder!!.visibility = View.GONE
//        }
//    }
//
//    fun toggleEdit(isEditable: Boolean) {
//        isActive = isEditable
//        if (isEditable) {
//            EventBusHelper.post(FrameActiveBus(frameOrder!!))
//            ivActiveBorder!!.visibility = View.VISIBLE
//            ivHorizontalScaleRight!!.visibility = View.VISIBLE
//            ivHorizontalScaleLeft!!.visibility = View.VISIBLE
//            ivVerticalScaleBottom!!.visibility = View.VISIBLE
//            ivVerticalScaleTop!!.visibility = View.VISIBLE
//            ivScaleTopRight!!.visibility = View.VISIBLE
//            ivScaleTopLeft!!.visibility = View.VISIBLE
//            ivScaleBottomRight!!.visibility = View.VISIBLE
//            ivScaleBottomLeft!!.visibility = View.VISIBLE
//        } else {
//            setOnTouchListener(null)
//            ivActiveBorder!!.visibility = View.GONE
//            ivHorizontalScaleRight!!.visibility = View.GONE
//            ivHorizontalScaleLeft!!.visibility = View.GONE
//            ivVerticalScaleBottom!!.visibility = View.GONE
//            ivVerticalScaleTop!!.visibility = View.GONE
//            ivScaleTopRight!!.visibility = View.GONE
//            ivScaleTopLeft!!.visibility = View.GONE
//            ivScaleBottomRight!!.visibility = View.GONE
//            ivScaleBottomLeft!!.visibility = View.GONE
//
//            if (frameOrder != 99) {
//                tvFrameOrder!!.visibility = View.VISIBLE
//            }
//        }
//        invalidate()
//        requestLayout()
//    }
//
//    var isActive: Boolean = false
//        set(value) {
//            if (value) {
//                field = true
//                ivActiveBorder!!.visibility = View.VISIBLE
//
//                hideAnchor(false)
//            } else {
//                field = false
//                ivActiveBorder!!.visibility = View.GONE
//                hideAnchor(true)
//            }
//
//            if (frameOrder != 99) {
//                tvFrameOrder!!.visibility = View.VISIBLE
//            }
//            field = value
//        }
//
//    fun setIsSwitch(isCheck: Boolean) {
//        isButtonSwitch = isCheck
//
//        if (frameOrder != 99) {
//            if (isCheck)
//                this.color = colorGreen
//            else
//                this.color = colorRed
//
//            tvFrameOrder!!.setBackgroundColor(color)
//        }
//        borderPaint?.color = color
//        if (isCheck)
//            this@StickerView.layoutParams.width = (this@StickerView.layoutParams.height - this.margin) * 2
//        postInvalidate()
//        requestLayout()
//
//        this.ivBorder?.invalidate()
//    }
//
//    private fun hideAnchor(isHide: Boolean) {
//        if (isHide) {
//            ivHorizontalScaleRight!!.visibility = View.GONE
//            ivHorizontalScaleLeft!!.visibility = View.GONE
//            ivVerticalScaleBottom!!.visibility = View.GONE
//            ivVerticalScaleTop!!.visibility = View.GONE
//            ivScaleTopRight!!.visibility = View.GONE
//            ivScaleTopLeft!!.visibility = View.GONE
//            ivScaleBottomRight!!.visibility = View.GONE
//            ivScaleBottomLeft!!.visibility = View.GONE
//        } else {
//            ivHorizontalScaleRight!!.visibility = View.VISIBLE
//            ivHorizontalScaleLeft!!.visibility = View.VISIBLE
//            ivVerticalScaleBottom!!.visibility = View.VISIBLE
//            ivVerticalScaleTop!!.visibility = View.VISIBLE
//            ivScaleTopRight!!.visibility = View.VISIBLE
//            ivScaleTopLeft!!.visibility = View.VISIBLE
//            ivScaleBottomRight!!.visibility = View.VISIBLE
//            ivScaleBottomLeft!!.visibility = View.VISIBLE
//        }
//    }
//
//    fun hideBalls() {
//        this.ivActiveBorder?.visibility = View.GONE
//        this.ivHorizontalScaleLeft?.visibility = View.GONE
//        this.ivHorizontalScaleRight?.visibility = View.GONE
//        this.ivVerticalScaleBottom?.visibility = View.GONE
//        this.ivVerticalScaleTop?.visibility = View.GONE
//        this.ivScaleTopRight?.visibility = View.GONE
//        this.ivScaleTopLeft?.visibility = View.GONE
//        this.ivScaleBottomRight?.visibility = View.GONE
//        this.ivScaleBottomLeft?.visibility = View.GONE
//    }
//
//    fun setFrameOrder(frameOrder: Int) {
//        this.frameOrder = frameOrder
//        tvFrameOrder?.text = frameOrder.toString()
//    }
//
//    protected fun onScaling(scaleUp: Boolean) {
//        Timber.e(scaleUp.toString())
//    }
//
//    private inner class BorderView : View {
//        constructor(context: Context?) : super(context)
//        constructor(context: Context?, attrs: AttributeSet?) : super(
//            context,
//            attrs
//        )
//
//        constructor(
//            context: Context?,
//            attrs: AttributeSet?,
//            defStyle: Int
//        ) : super(context, attrs, defStyle)
//
//        override fun onDraw(canvas: Canvas) {
//            super.onDraw(canvas)
//            // Draw sticker border
//            val params =
//                this.layoutParams as LayoutParams
//            val border = Rect()
//            border.left = this.left - params.leftMargin
//            border.top = this.top - params.topMargin
//            border.right = this.right - params.rightMargin
//            border.bottom = this.bottom - params.bottomMargin
//            borderPaint = Paint()
//            borderPaint!!.strokeWidth = 6f
//            borderPaint!!.color = color
//            tvFrameOrder!!.setBackgroundColor(color)
//            borderPaint!!.style = Paint.Style.STROKE
//            canvas.drawRect(border, borderPaint!!)
//            myCanvas = canvas
//        }
//    }
//
//    private inner class ActiveBorderView : View {
//        constructor(context: Context?) : super(context)
//        constructor(context: Context?, attrs: AttributeSet?) : super(
//            context,
//            attrs
//        )
//
//        constructor(
//            context: Context?,
//            attrs: AttributeSet?,
//            defStyle: Int
//        ) : super(context, attrs, defStyle)
//
//        override fun onDraw(canvas: Canvas) {
//            super.onDraw(canvas)
//            // Draw sticker border
//            val params =
//                this.layoutParams as LayoutParams
//            val border = Rect()
//            border.left = this.left - params.leftMargin
//            border.top = this.top - params.topMargin
//            border.right = this.right - params.rightMargin
//            border.bottom = this.bottom - params.bottomMargin
//            borderPaint = Paint()
//            borderPaint!!.strokeWidth = 6f
//            val color = resources.getColor(R.color.color_frame_active_border, null)
//            borderPaint!!.color = color
//            borderPaint!!.style = Paint.Style.STROKE
//            canvas.drawRect(border, borderPaint!!)
//        }
//    }
//
//    private val mTouchListener = OnTouchListener { view, event ->
//        when (view.tag) {
//            "DraggableViewGroup" -> {
//                draggableViewGroup(event)
//            }
//            "ivHorizontalScale" -> {
//                ivHorizontalScale(event)
//            }
//            "ivVerticalScale" -> {
//                ivVerticalScale(event)
//            }
//            "ivHorizontalScaleLeft" -> {
//                ivHorizontalScaleLeft(event)
//            }
//            "ivVerticalScaleTop" -> {
//                ivVerticalScaleTop(event)
//            }
//            "ivScaleTopRight" -> {
//                ivScaleTopRight(event)
//            }
//            "ivScaleTopLeft" -> {
//                ivScaleTopLeft(event)
//            }
//            "ivScaleBottomLeft" -> {
//                ivScaleBottomLeft(event)
//            }
//            "ivScaleBottomRight" -> {
//                ivScaleBottomRight(event)
//            }
//        }
//        performClick()
//        true
//    }
//
//    private fun ivScaleBottomRight(event: MotionEvent) {
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                orgX = this@StickerView.x
//                orgY = this@StickerView.y
//                scaleOrgX = event.rawX
//                scaleOrgY = event.rawY
//            }
//            MotionEvent.ACTION_MOVE -> {
//                val diffX = (event.rawX - scaleOrgX)
//                val diffY = (event.rawY - scaleOrgY)
//
//                if (diffX > 0) {
//                    this@StickerView.layoutParams.width += abs(diffX.toInt())
//
//                } else if (diffX < 0 && this@StickerView.layoutParams.width > convertDpToPixel(
//                        AppConstants.MIN_VIEW_FRAME_SIZE, context
//                    )
//                ) {
//                    this@StickerView.layoutParams.width -= abs(diffX.toInt())
//                }
//                if (diffY > 0) {
//                    this@StickerView.layoutParams.height += abs(diffY.toInt())
//                } else if (diffY < 0 && this@StickerView.layoutParams.height > convertDpToPixel(
//                        AppConstants.MIN_VIEW_FRAME_SIZE, context
//                    )
//                ) {
//                    this@StickerView.layoutParams.height -= abs(diffY.toInt())
//                }
//                if (isButtonSwitch)
//                    this@StickerView.layoutParams.width = (this@StickerView.layoutParams.height - this.margin) * 2
//
//                scaleOrgX = event.rawX
//                scaleOrgY = event.rawY
//                postInvalidate()
//                requestLayout()
//            }
//            MotionEvent.ACTION_UP -> {
//                postInvalidate()
//                requestLayout()
//            }
//        }
//    }
//
//    private fun ivScaleBottomLeft(event: MotionEvent) {
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                orgX = this@StickerView.x
//                orgY = this@StickerView.y
//                scaleOrgX = event.rawX
//                scaleOrgY = event.rawY
//            }
//            MotionEvent.ACTION_MOVE -> {
//                val diffX = (event.rawX - scaleOrgX)
//                val diffY = (event.rawY - scaleOrgY)
//
//                if (diffX > 0 && this@StickerView.layoutParams.width > convertDpToPixel(
//                        AppConstants.MIN_VIEW_FRAME_SIZE, context
//                    )
//                ) {
//                    this@StickerView.x += abs(diffX.toInt())
//                    this@StickerView.layoutParams.width -= abs(diffX.toInt())
//                } else if (diffX < 0) {
//                    this@StickerView.x -= abs(diffX.toInt())
//                    this@StickerView.layoutParams.width += abs(diffX.toInt())
//                }
//                if (diffY > 0) {
//                    this@StickerView.layoutParams.height += abs(diffY.toInt())
//                } else if (diffY < 0 && this@StickerView.layoutParams.height > convertDpToPixel(
//                        AppConstants.MIN_VIEW_FRAME_SIZE, context
//                    )
//                ) {
//                    this@StickerView.layoutParams.height -= abs(diffY.toInt())
//                }
//                if (isButtonSwitch)
//                    this@StickerView.layoutParams.width = (this@StickerView.layoutParams.height - this.margin) * 2
//
//                scaleOrgX = event.rawX
//                scaleOrgY = event.rawY
//                postInvalidate()
//                requestLayout()
//            }
//            MotionEvent.ACTION_UP -> {
//                postInvalidate()
//                requestLayout()
//            }
//        }
//    }
//
//    private fun ivScaleTopLeft(event: MotionEvent) {
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                orgX = this@StickerView.x
//                orgY = this@StickerView.y
//                scaleOrgX = event.rawX
//                scaleOrgY = event.rawY
//            }
//            MotionEvent.ACTION_MOVE -> {
//                val diffX = (event.rawX - scaleOrgX)
//                val diffY = (event.rawY - scaleOrgY)
//
//                if (diffX > 0 && this@StickerView.layoutParams.width > convertDpToPixel(
//                        AppConstants.MIN_VIEW_FRAME_SIZE, context
//                    )
//                ) {
//                    this@StickerView.x += abs(diffX.toInt())
//                    this@StickerView.layoutParams.width -= abs(diffX.toInt())
//                } else if (diffX < 0) {
//                    this@StickerView.x -= abs(diffX.toInt())
//                    this@StickerView.layoutParams.width += abs(diffX.toInt())
//                }
//                if (diffY > 0 && this@StickerView.layoutParams.height > convertDpToPixel(
//                        AppConstants.MIN_VIEW_FRAME_SIZE, context
//                    )
//                ) {
//                    this@StickerView.y += abs(diffY.toInt())
//                    this@StickerView.layoutParams.height -= abs(diffY.toInt())
//                } else if (diffY < 0) {
//                    this@StickerView.y -= abs(diffY.toInt())
//                    this@StickerView.layoutParams.height += abs(diffY.toInt())
//                }
//                if (isButtonSwitch)
//                    this@StickerView.layoutParams.width = (this@StickerView.layoutParams.height - this.margin) * 2
//
//                scaleOrgX = event.rawX
//                scaleOrgY = event.rawY
//                postInvalidate()
//                requestLayout()
//            }
//            MotionEvent.ACTION_UP -> {
//                postInvalidate()
//                requestLayout()
//            }
//        }
//    }
//
//    private fun ivScaleTopRight(event: MotionEvent) {
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                orgX = this@StickerView.x
//                orgY = this@StickerView.y
//                scaleOrgX = event.rawX
//                scaleOrgY = event.rawY
//            }
//            MotionEvent.ACTION_MOVE -> {
//                val diffX = (event.rawX - scaleOrgX)
//                val diffY = (event.rawY - scaleOrgY)
//
//                if (diffX > 0) {
//                    this@StickerView.layoutParams.width += abs(diffX.toInt())
//                } else if (diffX < 0 && this@StickerView.layoutParams.width > convertDpToPixel(
//                        AppConstants.MIN_VIEW_FRAME_SIZE, context
//                    )
//                ) {
//                    this@StickerView.layoutParams.width -= abs(diffX.toInt())
//                }
//                if (diffY > 0 && this@StickerView.layoutParams.height > convertDpToPixel(
//                        AppConstants.MIN_VIEW_FRAME_SIZE, context
//                    )
//                ) {
//                    this@StickerView.y += abs(diffY.toInt())
//                    this@StickerView.layoutParams.height -= abs(diffY.toInt())
//                } else if (diffY < 0) {
//                    this@StickerView.y -= abs(diffY.toInt())
//                    this@StickerView.layoutParams.height += abs(diffY.toInt())
//                }
//                if (isButtonSwitch)
//                    this@StickerView.layoutParams.width = (this@StickerView.layoutParams.height - this.margin) * 2
//                scaleOrgX = event.rawX
//                scaleOrgY = event.rawY
//                postInvalidate()
//                requestLayout()
//            }
//            MotionEvent.ACTION_UP -> {
//                postInvalidate()
//                requestLayout()
//            }
//        }
//    }
//
//    private fun ivHorizontalScaleLeft(event: MotionEvent) {
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                orgX = this@StickerView.x
//                orgY = this@StickerView.y
//                scaleOrgX = event.rawX
//                scaleOrgY = event.rawY
//            }
//            MotionEvent.ACTION_MOVE -> {
//                if (event.rawY - scaleOrgY < 0
//                ) {
//                    //scale up
//                    val offsetX = abs(event.rawX - scaleOrgX).toDouble()
//                    val offsetY = abs(event.rawY - scaleOrgY).toDouble()
//                    var offset = max(offsetX, offsetY)
//                    offset = offset.roundToInt().toDouble()
//                    this@StickerView.y -= offsetY.toFloat()
//                    this@StickerView.layoutParams.height += offset.toInt()
//                    onScaling(true)
//                } else if (event.rawY - scaleOrgY > 0
//                    &&
//                    this@StickerView.layoutParams.height > convertDpToPixel(
//                        AppConstants.MIN_VIEW_FRAME_SIZE, context
//                    )
//                ) {
//                    //scale down
//                    val offsetX = abs(event.rawX - scaleOrgX).toDouble()
//                    val offsetY = abs(event.rawY - scaleOrgY).toDouble()
//                    var offset = offsetX.coerceAtLeast(offsetY)
//                    offset = offset.roundToInt().toDouble()
//                    this@StickerView.y += offsetY.toFloat()
//                    this@StickerView.layoutParams.height -= offset.toInt()
//                    onScaling(false)
//                }
//                scaleOrgX = event.rawX
//                scaleOrgY = event.rawY
//                postInvalidate()
//                requestLayout()
//            }
//            MotionEvent.ACTION_UP -> {
//                postInvalidate()
//                requestLayout()
//            }
//        }
//    }
//
//    private fun ivVerticalScaleTop(event: MotionEvent) {
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                orgX = this@StickerView.x
//                orgY = this@StickerView.y
//                scaleOrgX = event.rawX
//                scaleOrgY = event.rawY
//            }
//            MotionEvent.ACTION_MOVE -> {
//                if (event.rawX - scaleOrgX < 0
//                ) {
//                    //lock horizontal left to right
//                    //scale up
//                    val offsetX = abs(event.rawX - scaleOrgX).toDouble()
//                    val offsetY = abs(event.rawY - scaleOrgY).toDouble()
//                    var offset = max(offsetX, offsetY)
//                    offset = offset.roundToInt().toDouble()
//                    this@StickerView.x -= offsetX.toFloat()
//                    this@StickerView.layoutParams.width += offset.toInt()
//                    onScaling(true)
//                } else if (event.rawX - scaleOrgX > 0 && this@StickerView.layoutParams.width > convertDpToPixel(
//                        AppConstants.MIN_VIEW_FRAME_SIZE,
//                        context
//                    )
//                ) {
//                    //scale down
//                    val offsetX = abs(event.rawX - scaleOrgX).toDouble()
//                    val offsetY = abs(event.rawY - scaleOrgY).toDouble()
//                    var offset = max(offsetX, offsetY)
//                    offset = round(offset)
//                    this@StickerView.x += offsetX.toFloat()
//                    this@StickerView.layoutParams.width -= offset.toInt()
//                    onScaling(false)
//                }
//                scaleOrgX = event.rawX
//                scaleOrgY = event.rawY
//                postInvalidate()
//                requestLayout()
//            }
//            MotionEvent.ACTION_UP -> {
//                postInvalidate()
//                requestLayout()
//            }
//        }
//    }
//
//    private fun ivVerticalScale(event: MotionEvent) {
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                orgX = this@StickerView.x
//                orgY = this@StickerView.y
//                scaleOrgX = event.rawX
//                scaleOrgY = event.rawY
//            }
//            MotionEvent.ACTION_MOVE -> {
//                if (
//                    event.rawY.toDouble() - scaleOrgY > 0
//                ) {
//                    //scale up
//                    val offsetX =
//                        abs(event.rawX - scaleOrgX).toDouble()
//                    val offsetY =
//                        abs(event.rawY - scaleOrgY).toDouble()
//                    var offset = max(offsetX, offsetY)
//                    offset = offset.roundToInt().toDouble()
//                    this@StickerView.layoutParams.height += offset.toInt()
//                    onScaling(true)
//                } else if (
//                    event.rawY.toDouble() - scaleOrgY < 0 && this@StickerView.layoutParams.height > convertDpToPixel(
//                        AppConstants.MIN_VIEW_FRAME_SIZE,
//                        context
//                    )
//                ) {
//                    //scale down
//                    val offsetX =
//                        abs(event.rawX - scaleOrgX).toDouble()
//                    val offsetY =
//                        abs(event.rawY - scaleOrgY).toDouble()
//                    var offset = max(offsetX, offsetY)
//                    offset = offset.roundToInt().toDouble()
//                    this@StickerView.layoutParams.height -= offset.toInt()
//                    onScaling(false)
//                }
//                scaleOrgX = event.rawX
//                scaleOrgY = event.rawY
//                postInvalidate()
//                requestLayout()
//            }
//            MotionEvent.ACTION_UP -> {
//                postInvalidate()
//                requestLayout()
//            }
//        }
//    }
//
//    private fun draggableViewGroup(event: MotionEvent) {
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                moveOrgX = event.rawX
//                moveOrgY = event.rawY
//                EventBusHelper.post(FrameActiveBus(frameOrder!!))
//            }
//            MotionEvent.ACTION_MOVE -> {
//                val offsetX = event.rawX - moveOrgX
//                val offsetY = event.rawY - moveOrgY
//                this@StickerView.x = this@StickerView.x + offsetX
//                this@StickerView.y = this@StickerView.y + offsetY
//                moveOrgX = event.rawX
//                moveOrgY = event.rawY
//                postInvalidate()
//                requestLayout()
//            }
//            MotionEvent.ACTION_UP -> {
//                postInvalidate()
//                requestLayout()
//            }
//        }
//    }
//
//    private fun ivHorizontalScale(event: MotionEvent) {
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                orgX = this@StickerView.x
//                orgY = this@StickerView.y
//                scaleOrgX = event.rawX
//                scaleOrgY = event.rawY
//            }
//            MotionEvent.ACTION_MOVE -> {
//                if (
//                    event.rawX.toDouble() - scaleOrgX < 0 && this@StickerView.layoutParams.width > convertDpToPixel(
//                        AppConstants.MIN_VIEW_FRAME_SIZE, context
//                    )
//                ) {
//                    //scale down
//                    val offsetX =
//                        abs(event.rawX - scaleOrgX).toDouble()
//                    val offsetY =
//                        abs(event.rawY - scaleOrgY).toDouble()
//                    var offset = max(offsetX, offsetY)
//                    offset = offset.roundToInt().toDouble()
//                    this@StickerView.layoutParams.width -= offset.toInt()
//                    onScaling(true)
//                } else if (
//                    event.rawX.toDouble() - scaleOrgX > 0
//                ) {
//                    //scale up
//                    val offsetX =
//                        abs(event.rawX - scaleOrgX).toDouble()
//                    val offsetY =
//                        abs(event.rawY - scaleOrgY).toDouble()
//                    var offset = max(offsetX, offsetY)
//                    offset = offset.roundToInt().toDouble()
//                    this@StickerView.layoutParams.width += offset.toInt()
//                    onScaling(false)
//                }
//                scaleOrgX = event.rawX
//                scaleOrgY = event.rawY
//                postInvalidate()
//                requestLayout()
//            }
//            MotionEvent.ACTION_UP -> {
//                postInvalidate()
//                requestLayout()
//            }
//        }
//    }
//}
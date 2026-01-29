package com.bcasekuritas.mybest.widget.imageview

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import android.content.res.ColorStateList
import android.graphics.*
import com.bcasekuritas.mybest.R
import android.graphics.drawable.Drawable
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet

class CircleImageView : AppCompatImageView {
    private val mDrawableRect = RectF()
    private val mBorderRect = RectF()
    private val mShaderMatrix = Matrix()
    private val mBitmapPaint = Paint()
    private val mBorderPaint = Paint()
    private var mBorderColor: ColorStateList? = ColorStateList.valueOf(DEFAULT_BORDER_COLOR)
    private var mBorderWidth = DEFAULT_BORDER_WIDTH
    private var mBitmap: Bitmap? = null
    private var mBitmapShader: BitmapShader? = null
    private var mBitmapWidth = 0
    private var mBitmapHeight = 0
    private var mDrawableRadius = 0f
    private var mBorderRadius = 0f
    private var mReady = false
    private var mSetupPending = false

    constructor(context: Context?) : super(context!!)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int = 0) : super(
        context, attrs, defStyle) {
        super.setScaleType(SCALE_TYPE)
        val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyle, 0)
        mBorderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_border_width, 0)
        mBorderColor = a.getColorStateList(R.styleable.CircleImageView_border_color)
        a.recycle()
        if (mBorderColor == null) {
            mBorderColor = ColorStateList.valueOf(DEFAULT_BORDER_COLOR)
        }
        mReady = true
        if (mSetupPending) {
            setup()
            mSetupPending = false
        }
    }

    override fun getScaleType(): ScaleType {
        return SCALE_TYPE
    }

    override fun onDraw(canvas: Canvas) {
        if (drawable == null) {
            return
        }
        canvas.drawCircle(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            mDrawableRadius,
            mBitmapPaint
        )
        canvas.drawCircle(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            mBorderRadius,
            mBorderPaint
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setup()
    }

    override fun setImageBitmap(bm: Bitmap) {
        super.setImageBitmap(bm)
        mBitmap = bm
        setup()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        mBitmap = getBitmapFromDrawable(drawable)
        setup()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        mBitmap = getBitmapFromDrawable(drawable)
        setup()
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else try {
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: OutOfMemoryError) {
            null
        }
    }

    private fun setup() {
        if (!mReady) {
            mSetupPending = true
            return
        }
        if (mBitmap == null) {
            return
        }
        mBitmapShader = BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        mBitmapPaint.isAntiAlias = true
        mBitmapPaint.shader = mBitmapShader
        mBorderPaint.style = Paint.Style.STROKE
        mBorderPaint.isAntiAlias = true
        mBorderPaint.color = mBorderColor!!.defaultColor
        mBorderPaint.strokeWidth = mBorderWidth.toFloat()
        mBitmapHeight = mBitmap!!.height
        mBitmapWidth = mBitmap!!.width
        mBorderRect[0f, 0f, width.toFloat()] = height.toFloat()
        mBorderRadius = Math.min(
            (mBorderRect.height() - mBorderWidth) / 2,
            (mBorderRect.width() - mBorderWidth) / 2
        )
        mDrawableRect[mBorderWidth.toFloat(), mBorderWidth.toFloat(), mBorderRect.width() - mBorderWidth] =
            mBorderRect.height() - mBorderWidth
        mDrawableRadius = Math.min(mDrawableRect.height() / 2, mDrawableRect.width() / 2)
        updateShaderMatrix()
        invalidate()
    }

    private fun updateShaderMatrix() {
        val scale: Float
        var dx = 0f
        var dy = 0f
        mShaderMatrix.set(null)
        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / mBitmapHeight.toFloat()
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * A_HALF_FLOAT
        } else {
            scale = mDrawableRect.width() / mBitmapWidth.toFloat()
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * A_HALF_FLOAT
        }
        mShaderMatrix.setScale(scale, scale)
        mShaderMatrix.postTranslate(
            ((dx + A_HALF_FLOAT).toInt() + mBorderWidth).toFloat(),
            ((dy + A_HALF_FLOAT).toInt() + mBorderWidth).toFloat()
        )
        mBitmapShader!!.setLocalMatrix(mShaderMatrix)
    }

    companion object {
        private val SCALE_TYPE = ScaleType.CENTER_CROP
        private const val DEFAULT_BORDER_WIDTH = 0
        private const val A_HALF_FLOAT = 0.5f
        private const val DEFAULT_BORDER_COLOR = Color.BLACK
    }
}
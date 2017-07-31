package com.gdgnantes.devfest.android.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.gdgnantes.devfest.android.R
import com.gdgnantes.devfest.android.view.forEach

/**
 * A very basic layout that lay out its children based on a "flow logic" i.e. each child is positioned at the right of
 * the previous one and so on. Currently it only support horizontal layout and hence can't break on several lines.
 * Please note that this behavior may change in the future (canBreakLine will return true, by default).
 */
class FlowLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0) : ViewGroup(context, attrs, defStyle) {

    var spacingHorizontal: Int = 0
        set(spacingHorizontal) {
            if (this.spacingHorizontal != spacingHorizontal) {
                field = spacingHorizontal
                requestLayout()
                invalidate()
            }
        }
    var spacingVertical: Int = 0
        set(spacingVertical) {
            if (this.spacingVertical != spacingVertical) {
                field = spacingVertical
                requestLayout()
                invalidate()
            }
        }
    var canBreakLine: Boolean = false
        set(canBreakLine) {
            if (this.canBreakLine != canBreakLine) {
                field = canBreakLine
                requestLayout()
                invalidate()
            }
        }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.FlowLayout, defStyle, 0)?.let {
            canBreakLine = it.getBoolean(R.styleable.FlowLayout_canBreakLine, false)
            spacingHorizontal = it.getDimensionPixelSize(R.styleable.FlowLayout_spacingHorizontal, 0)
            spacingVertical = it.getDimensionPixelSize(R.styleable.FlowLayout_spacingVertical, 0)
            it.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec) - paddingRight
        val hSpacing = spacingHorizontal
        val vSpacing = spacingVertical

        var width = 0
        var height = 0

        var currentX = paddingLeft
        var currentY = paddingTop

        var currentLineHeight = 0

        forEach { child ->
            if (child.visibility == View.GONE) {
                return@forEach
            }

            measureChild(child, widthMeasureSpec, heightMeasureSpec)

            val lp = child.layoutParams as LayoutParams

            val canFitCut = currentX < widthSize
            if (!canFitCut && !canBreakLine) {
                lp.skip = true
                return@forEach
            }

            val canFit = currentX + child.measuredWidth < widthSize
            if (!canFit && canBreakLine) {
                width = Math.max(currentX, width)
                height += currentLineHeight + vSpacing

                currentX = paddingLeft
                currentY += currentLineHeight + vSpacing

                currentLineHeight = 0
            }

            lp.skip = false
            lp.x = currentX
            lp.y = currentY

            currentX += child.measuredWidth + hSpacing
            currentLineHeight = Math.max(currentLineHeight, child.measuredHeight)
        }

        width = Math.max(currentX, width)

        height += currentLineHeight
        height += paddingTop + paddingBottom

        setMeasuredDimension(View.resolveSize(width, widthMeasureSpec), View.resolveSize(height, heightMeasureSpec))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        forEach {
            if (it.visibility != View.GONE) {
                val lp = it.layoutParams as LayoutParams
                if (!lp.skip) {
                    it.layout(lp.x, lp.y, lp.x + it.measuredWidth, lp.y + it.measuredHeight)
                }
            }
        }
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        val lp = child.layoutParams as LayoutParams
        if (!lp.skip) {
            return super.drawChild(canvas, child, drawingTime)
        }
        return false
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is LayoutParams
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): LayoutParams {
        return LayoutParams(p.width, p.height)
    }

    class LayoutParams : ViewGroup.LayoutParams {

        internal var skip: Boolean = false
        internal var x: Int = 0
        internal var y: Int = 0

        constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

        constructor(w: Int, h: Int) : super(w, h)
    }
}

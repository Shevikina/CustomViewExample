package com.example.customviewexample

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.View
import android.widget.Toast
import androidx.core.graphics.toColorInt
import kotlin.random.Random

class ShapeView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr) {

    private var defaultColor = DEFAULT_SHAPE_COLOR
    private var shapeColors: List<Int> = emptyList()
    private val shapes = mutableListOf<ShapeInfo>()

    private val shapePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = TEXT_SIZE
        color = TEXT_COLOR
        typeface = Typeface.DEFAULT_BOLD
    }

    private val restartMessage: String by lazy { context.getString(R.string.game_over) }

    init {
        val typeArray = context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.ShapeView,
            defStyleAttr, 0
        )
        defaultColor = typeArray.getColor(
            R.styleable.ShapeView_shv_defaultColor,
            DEFAULT_SHAPE_COLOR
        )
        typeArray.recycle()
    }

    fun setIntColors(colors: List<Int>) {
        shapeColors = colors
    }

    fun setHexColors(colors: List<String>) {
        shapeColors = colors.map { it.toColorInt() }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == ACTION_DOWN) {
            addRandomShape(event.x, event.y)
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun addRandomShape(x: Float, y: Float) {
        shapes.add(
            ShapeInfo(
                type = ShapeType.entries.toTypedArray().random(),
                color = shapeColors.randomOrNull() ?: defaultColor,
                size = Random.nextDouble(MIN_SHAPE_SIZE, MAX_SHAPE_SIZE).toFloat(),
                cx = x,
                cy = y
            )
        )

        if (shapes.count() >= MAX_SHAPE_COUNT) clearShapes()

        invalidate()
    }

    private fun clearShapes() {
        shapes.clear()
        Toast.makeText(context, restartMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawText("Фигур: ${shapes.count()}", TEXT_HEIGHT, TEXT_WIDTH, textPaint)

        shapes.forEach {
            shapePaint.color = it.color
            canvas.drawShape(it)
        }
    }

    private fun Canvas.drawShape(shape: ShapeInfo) {
        when (shape.type) {
            ShapeType.CIRCLE -> {
                drawCircle(
                    shape.cx,
                    shape.cy,
                    shape.size / 2,
                    shapePaint
                )
            }

            ShapeType.SQUARE -> {
                drawRect(
                    shape.rect(),
                    shapePaint
                )
            }

            ShapeType.ROUNDED_SQUARE -> {
                drawRoundRect(
                    shape.rect(),
                    RECT_RADIUS,
                    RECT_RADIUS,
                    shapePaint
                )
            }
        }
    }

    private fun ShapeInfo.rect(): RectF {
        val halfSize = size / 2

        return RectF(
            cx - halfSize,
            cy - halfSize,
            cx + halfSize,
            cy + halfSize
        )
    }

    companion object {
        const val DEFAULT_SHAPE_COLOR = Color.GREEN
        const val MAX_SHAPE_COUNT = 10
        const val MIN_SHAPE_SIZE = 20.0
        const val MAX_SHAPE_SIZE = 50.0
        const val RECT_RADIUS = 15f

        const val TEXT_COLOR = Color.BLACK
        const val TEXT_SIZE = 48f
        const val TEXT_HEIGHT = 20f
        const val TEXT_WIDTH = 60f
    }
}
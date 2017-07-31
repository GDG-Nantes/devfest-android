package com.gdgnantes.devfest.android.graphics

import android.graphics.*
import com.squareup.picasso.Transformation


class RoundedTransformation : Transformation {

    private val paint: Paint = Paint()

    override fun transform(source: Bitmap): Bitmap {
        paint.isAntiAlias = true
        paint.shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        val radius = Math.min(source.width, source.height) / 2

        val output = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        canvas.drawRoundRect(0f, 0f, source.width.toFloat(), source.height.toFloat(), radius.toFloat(), radius.toFloat(), paint)

        if (source != output) {
            source.recycle()
        }

        return output
    }

    override fun key(): String {
        return "rounded"
    }
}

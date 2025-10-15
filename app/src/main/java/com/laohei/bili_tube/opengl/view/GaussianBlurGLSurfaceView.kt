package com.laohei.bili_tube.opengl.view

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.laohei.bili_tube.opengl.glsl.GaussianBlurRenderer

class GaussianBlurGLSurfaceView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {

    companion object {
        private val TAG = GaussianBlurGLSurfaceView::class.simpleName
    }

    private val renderer = GaussianBlurRenderer(context, this)

    init {
        setEGLContextClientVersion(3)
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    fun setBitmap(bitmap: Bitmap?) {
        renderer.setBitmap(bitmap)
        requestRender()
    }

    fun setBlurRadius(radius: Int) {
        renderer.setBlurRadius(radius)
        requestRender()
    }

    fun setBlurSize(size: Float) {
        renderer.setBlurSize(size)
        requestRender()
    }
}

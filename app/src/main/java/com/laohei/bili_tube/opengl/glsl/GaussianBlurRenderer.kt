package com.laohei.bili_tube.opengl.glsl


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.opengl.GLES32
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import com.laohei.bili_tube.R
import com.laohei.bili_tube.opengl.util.ShaderUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GaussianBlurRenderer(
    private val context: Context,
    private val glSurfaceView: GLSurfaceView
) : GLSurfaceView.Renderer {

    companion object {
        private val TAG = GaussianBlurRenderer::class.simpleName
    }

    private var originalTexture = 0
    private var fboTexture = 0
    private var fbo = 0
    private var width = 0
    private var height = 0
    private var bitmap: Bitmap? = null

    private var programH = 0
    private var programV = 0

    private var blurRadius = 8
    private var blurSize = 1.0f

    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var texBuffer: FloatBuffer

    private val vertexCoords = floatArrayOf(-1f, 1f, -1f, -1f, 1f, 1f, 1f, -1f)
    private val texCoords = floatArrayOf(0f, 0f, 0f, 1f, 1f, 0f, 1f, 1f) // Y翻转

    override fun onSurfaceCreated(unused: GL10?, config: EGLConfig?) {
        GLES32.glClearColor(0f, 0f, 0f, 1f)

        programH =
            ShaderUtils.createProgramFromRaw(context, R.raw.vertex_shader, R.raw.blur_horizontal)
        programV =
            ShaderUtils.createProgramFromRaw(context, R.raw.vertex_shader, R.raw.blur_vertical)

        vertexBuffer = ByteBuffer.allocateDirect(vertexCoords.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertexCoords)
        vertexBuffer.position(0)

        texBuffer = ByteBuffer.allocateDirect(texCoords.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer().put(texCoords)
        texBuffer.position(0)
    }

    override fun onSurfaceChanged(unused: GL10?, w: Int, h: Int) {
        width = w
        height = h
        GLES32.glViewport(0, 0, w, h)
        setupFrameBuffer(w, h)
    }

    override fun onDrawFrame(unused: GL10?) {
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT)
        bitmap?.let {
            if (originalTexture == 0) {
                originalTexture = loadTexture(it)
            }

            // Pass 1: 横向模糊 → FBO
            GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, fbo)
            GLES32.glViewport(0, 0, width, height)
            drawBlur(programH, originalTexture)

            // Pass 2: 纵向模糊 → 屏幕
            GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, 0)
            GLES32.glViewport(0, 0, width, height)
            drawBlur(programV, fboTexture)
        }
    }

    fun setBitmap(bmp: Bitmap?) {
        if (bitmap == null || !bitmap!!.sameAs(bmp)) {
            bitmap = bmp
            originalTexture = 0
        }
    }

    fun setBlurRadius(radius: Int) {
        blurRadius = radius
    }

    fun setBlurSize(size: Float) {
        blurSize = size
    }

    private fun setupFrameBuffer(w: Int, h: Int) {
        val fboIds = IntArray(1)
        val texIds = IntArray(1)
        GLES32.glGenFramebuffers(1, fboIds, 0)
        GLES32.glGenTextures(1, texIds, 0)

        fbo = fboIds[0]
        fboTexture = texIds[0]

        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, fboTexture)
        GLES32.glTexImage2D(
            GLES32.GL_TEXTURE_2D, 0, GLES32.GL_RGBA, w, h, 0, GLES32.GL_RGBA,
            GLES32.GL_UNSIGNED_BYTE, null
        )
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_LINEAR)
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_LINEAR)
        GLES32.glTexParameteri(
            GLES32.GL_TEXTURE_2D,
            GLES32.GL_TEXTURE_WRAP_S,
            GLES32.GL_CLAMP_TO_EDGE
        )
        GLES32.glTexParameteri(
            GLES32.GL_TEXTURE_2D,
            GLES32.GL_TEXTURE_WRAP_T,
            GLES32.GL_CLAMP_TO_EDGE
        )

        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, fbo)
        GLES32.glFramebufferTexture2D(
            GLES32.GL_FRAMEBUFFER,
            GLES32.GL_COLOR_ATTACHMENT0,
            GLES32.GL_TEXTURE_2D,
            fboTexture,
            0
        )
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, 0)
    }

    private fun drawBlur(program: Int, texture: Int) {
        GLES32.glUseProgram(program)
        val posHandle = GLES32.glGetAttribLocation(program, "aPosition")
        val texHandle = GLES32.glGetAttribLocation(program, "aTexCoord")
        val texelHandle = GLES32.glGetUniformLocation(program, "uTexelSize")
        val blurHandle = GLES32.glGetUniformLocation(program, "uBlurSize")
        val samplerHandle = GLES32.glGetUniformLocation(program, "uTexture")
        val radiusHandle = GLES32.glGetUniformLocation(program, "uRadius")

        GLES32.glEnableVertexAttribArray(posHandle)
        GLES32.glVertexAttribPointer(posHandle, 2, GLES32.GL_FLOAT, false, 0, vertexBuffer)
        GLES32.glEnableVertexAttribArray(texHandle)
        GLES32.glVertexAttribPointer(texHandle, 2, GLES32.GL_FLOAT, false, 0, texBuffer)

        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, texture)
        GLES32.glUniform1i(samplerHandle, 0)
        GLES32.glUniform2f(texelHandle, 1f / width, 1f / height)
        GLES32.glUniform1f(blurHandle, blurSize)
        GLES32.glUniform1i(radiusHandle, blurRadius)

        GLES32.glDrawArrays(GLES32.GL_TRIANGLE_STRIP, 0, 4)
        GLES32.glDisableVertexAttribArray(posHandle)
        GLES32.glDisableVertexAttribArray(texHandle)
    }

    fun loadTexture(bitmap: Bitmap): Int {
        val flipped = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height,
            Matrix().apply { preScale(1f, -1f) }, false
        )
        val textures = IntArray(1)
        GLES32.glGenTextures(1, textures, 0)
        val textureId = textures[0]
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textureId)
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_LINEAR)
        GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_LINEAR)
        GLES32.glTexParameteri(
            GLES32.GL_TEXTURE_2D,
            GLES32.GL_TEXTURE_WRAP_S,
            GLES32.GL_CLAMP_TO_EDGE
        )
        GLES32.glTexParameteri(
            GLES32.GL_TEXTURE_2D,
            GLES32.GL_TEXTURE_WRAP_T,
            GLES32.GL_CLAMP_TO_EDGE
        )
        GLUtils.texImage2D(GLES32.GL_TEXTURE_2D, 0, flipped, 0)
        return textureId
    }
}
package com.laohei.bili_tube.opengl.util

import android.content.Context
import android.opengl.GLES32
import java.io.BufferedReader
import java.io.InputStreamReader

object ShaderUtils {

    /** 从 raw 资源加载 shader 字符串 */
    fun loadRawString(context: Context, resId: Int): String {
        val inputStream = context.resources.openRawResource(resId)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val sb = StringBuilder()
        reader.useLines { lines ->
            lines.forEach { sb.append(it).append("\n") }
        }
        return sb.toString()
    }

    /** 编译 shader */
    fun loadShader(type: Int, source: String): Int {
        val shader = GLES32.glCreateShader(type)
        GLES32.glShaderSource(shader, source)
        GLES32.glCompileShader(shader)
        val compiled = IntArray(1)
        GLES32.glGetShaderiv(shader, GLES32.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] == 0) {
            val info = GLES32.glGetShaderInfoLog(shader)
            GLES32.glDeleteShader(shader)
            throw RuntimeException("Could not compile shader $type: $info")
        }
        return shader
    }

    /** 从顶点 shader + 片元 shader raw 资源创建 program */
    fun createProgramFromRaw(context: Context, vertexResId: Int, fragmentResId: Int): Int {
        val vertexSource = loadRawString(context, vertexResId)
        val fragmentSource = loadRawString(context, fragmentResId)
        return createProgram(vertexSource, fragmentSource)
    }

    /** 从顶点 shader + 片元 shader 字符串创建 program */
    fun createProgram(vertexSource: String, fragmentSource: String): Int {
        val vertexShader = loadShader(GLES32.GL_VERTEX_SHADER, vertexSource)
        val fragmentShader = loadShader(GLES32.GL_FRAGMENT_SHADER, fragmentSource)

        val program = GLES32.glCreateProgram()
        GLES32.glAttachShader(program, vertexShader)
        GLES32.glAttachShader(program, fragmentShader)
        GLES32.glLinkProgram(program)

        val linkStatus = IntArray(1)
        GLES32.glGetProgramiv(program, GLES32.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            val info = GLES32.glGetProgramInfoLog(program)
            GLES32.glDeleteProgram(program)
            throw RuntimeException("Could not link program: $info")
        }
        return program
    }
}

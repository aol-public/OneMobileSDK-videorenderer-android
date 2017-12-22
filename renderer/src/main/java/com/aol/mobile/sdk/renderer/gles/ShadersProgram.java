/*
 * Copyright (c) 2017. Oath.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.aol.mobile.sdk.renderer.gles;

import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glDetachShader;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glUseProgram;

public final class ShadersProgram {
    private final int program;
    private final int vertexShader;
    private final int fragmentShader;

    private final int aPosition;
    private final int aTextureCoord;
    private final int uTextureMatrix;
    private final int uMVPMatrix;

    public ShadersProgram() {
        vertexShader = loadShader(GL_VERTEX_SHADER, "uniform mat4 uMVPMatrix;\n" +
                "uniform mat4 uTextureMatrix;\n" +
                "attribute vec4 aPosition;\n" +
                "attribute vec4 aTextureCoord;\n" +
                "varying vec2 vTextureCoord;\n" +
                "\n" +
                "void main() {\n" +
                "    gl_Position = uMVPMatrix * aPosition;\n" +
                "    vTextureCoord = (uTextureMatrix * aTextureCoord).xy;\n" +
                "}\n");

        fragmentShader = loadShader(GL_FRAGMENT_SHADER, "#extension GL_OES_EGL_image_external : require\n" +
                "\n" +
                "precision mediump float;\n" +
                "varying vec2 vTextureCoord;\n" +
                "uniform samplerExternalOES sTexture;\n" +
                "\n" +
                "void main() {\n" +
                "    vec4 color = texture2D(sTexture, vTextureCoord);\n" +
                "    gl_FragColor = color;\n" +
                "}\n");

        program = glCreateProgram();

        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);
        glUseProgram(program);

        aPosition = glGetAttribLocation(program, "aPosition");
        aTextureCoord = glGetAttribLocation(program, "aTextureCoord");

        uTextureMatrix = glGetUniformLocation(program, "uTextureMatrix");
        uMVPMatrix = glGetUniformLocation(program, "uMVPMatrix");
    }

    public int getAttributePosition() {
        return aPosition;
    }

    public int getAttributeTextureCoord() {
        return aTextureCoord;
    }

    public int getUniformTextureMatrix() {
        return uTextureMatrix;
    }

    public int getUniformMVPMatrix() {
        return uMVPMatrix;
    }

    public void release() {
        glDetachShader(program, vertexShader);
        glDetachShader(program, fragmentShader);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        glDeleteProgram(program);
    }

    private int loadShader(int shaderType, String source) {
        int shader = glCreateShader(shaderType);

        glShaderSource(shader, source);
        glCompileShader(shader);

        return shader;
    }
}

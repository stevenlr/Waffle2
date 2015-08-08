package com.stevenlr.waffle2.graphics;

import java.nio.FloatBuffer;

import com.stevenlr.waffle2.graphics.opengl.GLStates;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL31.*;

public class Quad {

	private int _vbo;

	public Quad() {
		_vbo = glGenBuffers();

		FloatBuffer buffer = BufferUtils.createFloatBuffer(12);
		float[] bufferData = {0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 0, 0};

		buffer.put(bufferData).flip();
		GLStates.bindBufferArray(_vbo);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		GLStates.bindBufferArray(0);
	}

	public void bindVertexAttrib(int index) {
		GLStates.bindBufferArray(_vbo);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0L);
		GLStates.bindBufferArray(0);
	}

	public void draw() {
		glDrawArrays(GL_TRIANGLES, 0, 6);
	}

	public void drawInstanced(int count) {
		glDrawArraysInstanced(GL_TRIANGLES, 0, 6, count);
	}
}

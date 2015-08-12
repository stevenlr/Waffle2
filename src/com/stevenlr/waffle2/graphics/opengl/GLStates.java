package com.stevenlr.waffle2.graphics.opengl;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;

public class GLStates {

	private static int _drawFramebuffer = 0;
	private static int _readFramebuffer = 0;
	private static int _vao = 0;
	private static int _vboArray = 0;
	private static int _vboTexture = 0;
	private static int _program = 0;
	private static int _activeTexture = 0;

	private static int[] _textureType = new int[8];
	private static int[] _texture = new int[8];

	public static void bindFramebuffer(int f) {
		if (_drawFramebuffer != f || _readFramebuffer != f) {
			_drawFramebuffer = f;
			_readFramebuffer = f;
			glBindFramebuffer(GL_FRAMEBUFFER, f);
		}
	}

	public static void bindDrawFramebuffer(int f) {
		if (_drawFramebuffer != f) {
			_drawFramebuffer = f;
			glBindFramebuffer(GL_DRAW_FRAMEBUFFER, f);
		}
	}

	public static void bindReadFramebuffer(int f) {
		if (_readFramebuffer != f) {
			_readFramebuffer = f;
			glBindFramebuffer(GL_READ_FRAMEBUFFER, f);
		}
	}

	public static void activeTexture(int t) {
		if (_activeTexture != t) {
			_activeTexture = t;
			glActiveTexture(GL_TEXTURE0 + t);
		}
	}

	public static void bindTexture(int type, int unit, int texture) {
		if (_textureType[unit] != type && _textureType[unit] != 0) {
			activeTexture(unit);
			glBindTexture(_textureType[unit], 0);
			_textureType[unit] = type;
		}

		if (_texture[unit] != texture) {
			activeTexture(unit);
			_texture[unit] = texture;
			glBindTexture(type, texture);
		}
	}

	public static void bindVertexArray(int vao) {
		if (_vao != vao) {
			_vao = vao;
			glBindVertexArray(vao);
		}
	}

	public static void bindBufferArray(int vbo) {
		if (_vboArray != vbo) {
			_vboArray = vbo;
			glBindBuffer(GL_ARRAY_BUFFER, vbo);
		}
	}

	public static void bindBufferTexture(int vbo) {
		if (_vboTexture != vbo) {
			_vboTexture = vbo;
			glBindBuffer(GL_TEXTURE_BUFFER, vbo);
		}
	}

	public static void useProgram(int p) {
		if (_program != p) {
			_program = p;
			glUseProgram(p);
		}
	}
}

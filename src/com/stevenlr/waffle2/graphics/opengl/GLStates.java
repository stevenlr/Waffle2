package com.stevenlr.waffle2.graphics.opengl;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;

public class GLStates {

	private static int _drawFramebuffer = 0;
	private static int _readFramebuffer = 0;
	private static int _texture2d = 0;
	private static int _vao = 0;
	private static int _vboArray = 0;
	private static int _vboTexture = 0;
	private static int _program = 0;
	private static int _textureBuffer = 0;

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

	public static void bindTexture2d(int t) {
		if (_texture2d != t) {
			_texture2d = t;
			glBindTexture(GL_TEXTURE_2D, t);
		}
	}

	public static void bindTextureBuffer(int t) {
		if (_textureBuffer != t) {
			_textureBuffer = t;
			glBindTexture(GL_TEXTURE_BUFFER, t);
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

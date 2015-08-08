package com.stevenlr.waffle2.graphics;

import java.nio.IntBuffer;

import com.stevenlr.waffle2.graphics.opengl.GLStates;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Canvas {

	private int _width;
	private int _height;
	private int _framebuffer;
	private int _renderbufferDepthStencil;
	private int _texture;
	private Renderer _renderer;

	private static IntBuffer _drawBuffer = null;

	public Canvas(int width, int height) {
		if (_drawBuffer == null) {
			_drawBuffer = BufferUtils.createIntBuffer(1);
			_drawBuffer.put(0, GL_COLOR_ATTACHMENT0);
		}

		_width = width;
		_height = height;

		_framebuffer = glGenFramebuffers();
		GLStates.bindFramebuffer(_framebuffer);

		_renderbufferDepthStencil = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, _renderbufferDepthStencil);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, _width, _height);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, _renderbufferDepthStencil);
		glBindRenderbuffer(GL_RENDERBUFFER, 0);

		_texture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, _texture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB8, _width, _height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, _texture, 0);
		glBindTexture(GL_TEXTURE_2D, 0);

		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
			throw new RuntimeException("Failed to create framebuffer");
		}

		GLStates.bindFramebuffer(0);

		_renderer = new Renderer(this);
	}

	public int getWidth() {
		return _width;
	}

	public int getHeight() {
		return _height;
	}

	public void bindDraw() {
		GLStates.bindDrawFramebuffer(_framebuffer);
		glDrawBuffers(_drawBuffer);
		glViewport(0, 0, _width, _height);
	}

	public void bindRead() {
		GLStates.bindReadFramebuffer(_framebuffer);
		glReadBuffer(GL_COLOR_ATTACHMENT0);
	}

	public Renderer getRenderer() {
		return _renderer;
	}
}

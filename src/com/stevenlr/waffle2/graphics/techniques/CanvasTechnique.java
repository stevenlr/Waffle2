package com.stevenlr.waffle2.graphics.techniques;

import com.stevenlr.waffle2.graphics.Quad;
import com.stevenlr.waffle2.graphics.opengl.GLStates;
import com.stevenlr.waffle2.graphics.opengl.Shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.*;

public class CanvasTechnique {

	private static Shader _shader;
	private static final int POSITION_LOCATION = 0;

	private Quad _quad;
	private int _vao;

	public static void init() {
		_shader = new Shader("/waffle2/shaders/canvas.vert", "/waffle2/shaders/canvas.frag");
		_shader.bindAttribLocation("in_Position", POSITION_LOCATION);
		_shader.link();

		_shader.bind();
		_shader.setUniform("u_TextureCanvas", 2);
		_shader.unbind();
	}

	public CanvasTechnique(Quad quad) {
		_quad = quad;

		_vao = glGenVertexArrays();
		GLStates.bindVertexArray(_vao);

		glEnableVertexAttribArray(POSITION_LOCATION);
		_quad.bindVertexAttrib(POSITION_LOCATION);

		GLStates.bindBufferArray(0);
		GLStates.bindVertexArray(0);
	}

	public void blit(int texture, float sx0, float sy0, float sx1, float sy1, float dx0, float dy0, float dx1, float dy1, float r, float g, float b, float a) {
		GLStates.bindTexture(GL_TEXTURE_2D, 2, texture);

		_shader.bind();

		_shader.setUniform("u_S0", sx0, sy0);
		_shader.setUniform("u_S1", sx1, sy1);
		_shader.setUniform("u_D0", dx0, dy0);
		_shader.setUniform("u_D1", dx1, dy1);
		_shader.setUniform("u_Color", r, g, b, a);

		GLStates.bindVertexArray(_vao);
		_quad.draw();
	}
}

package com.stevenlr.waffle2.graphics.techniques;

import com.stevenlr.waffle2.graphics.Quad;
import com.stevenlr.waffle2.graphics.opengl.GLStates;
import com.stevenlr.waffle2.graphics.opengl.Shader;
import com.stevenlr.waffle2.utils.buffers.MultiFloatBuffer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.*;

public class ColoredTechnique {

	private static Shader _coloredShader;
	private static final int POSITION_LOCATION = 0;
	private static final int TRANSFORM_LOCATION = 1;
	private static final int COLOR_LOCATION = 4;
	private static final int BUFFER_SIZE = 1024;
	private static final int ELEMENT_SIZE = 13;

	private MultiFloatBuffer _buffer = new MultiFloatBuffer(ELEMENT_SIZE * BUFFER_SIZE);
	private Quad _quad;

	private int _vao;
	private int _vbo;

	public static void init() {
		_coloredShader = new Shader("/shaders/colored.vert", "/shaders/colored.frag");
		_coloredShader.bindAttribLocation("in_Position", POSITION_LOCATION);
		_coloredShader.bindAttribLocation("in_Transform", TRANSFORM_LOCATION);
		_coloredShader.bindAttribLocation("in_Color", COLOR_LOCATION);
		_coloredShader.link();
	}
	public ColoredTechnique(Quad quad) {
		_quad = quad;

		_vao = glGenVertexArrays();

		GLStates.bindVertexArray(_vao);

		glEnableVertexAttribArray(POSITION_LOCATION);
		quad.bindVertexAttrib(POSITION_LOCATION);

		_vbo = glGenBuffers();
		GLStates.bindBufferArray(_vbo);
		glBufferData(GL_ARRAY_BUFFER, _buffer.getByteSize(), GL_DYNAMIC_DRAW);

		glEnableVertexAttribArray(TRANSFORM_LOCATION);
		glEnableVertexAttribArray(TRANSFORM_LOCATION + 1);
		glEnableVertexAttribArray(TRANSFORM_LOCATION + 2);
		glVertexAttribPointer(TRANSFORM_LOCATION, 3, GL_FLOAT, false, ELEMENT_SIZE * 4, 0);
		glVertexAttribPointer(TRANSFORM_LOCATION + 1, 3, GL_FLOAT, false, ELEMENT_SIZE * 4, 3 * 4);
		glVertexAttribPointer(TRANSFORM_LOCATION + 2, 3, GL_FLOAT, false, ELEMENT_SIZE * 4, 6 * 4);
		glVertexAttribDivisor(TRANSFORM_LOCATION, 1);
		glVertexAttribDivisor(TRANSFORM_LOCATION + 1, 1);
		glVertexAttribDivisor(TRANSFORM_LOCATION + 2, 1);

		glEnableVertexAttribArray(COLOR_LOCATION);
		glVertexAttribPointer(COLOR_LOCATION, 4, GL_FLOAT, false, ELEMENT_SIZE * 4, 9 * 4);
		glVertexAttribDivisor(COLOR_LOCATION, 1);

		GLStates.bindBufferArray(0);
		GLStates.bindVertexArray(0);
	}

	public void add(Matrix3f transform, float r, float g, float b, float a) {
		_buffer.put(transform);
		_buffer.put(r);
		_buffer.put(g);
		_buffer.put(b);
		_buffer.put(a);
	}

	public void doRenderPass(Matrix4f projection) {
		_coloredShader.bind();
		_coloredShader.setUniform("u_ProjectionMatrix", projection);
		GLStates.bindVertexArray(_vao);

		while (true) {
			MultiFloatBuffer.ReadInfo readBuffer = _buffer.read();

			if (readBuffer == null) {
				break;
			}

			GLStates.bindBufferArray(_vbo);
			glBufferSubData(GL_ARRAY_BUFFER, 0, readBuffer.buffer);
			GLStates.bindBufferArray(0);

			_quad.drawInstanced(readBuffer.size / ELEMENT_SIZE);
		}
	}

	public void reset() {
		_buffer.reset();
	}
}

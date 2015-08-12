package com.stevenlr.waffle2.graphics.techniques;

import com.stevenlr.waffle2.Waffle2;
import com.stevenlr.waffle2.graphics.Quad;
import com.stevenlr.waffle2.graphics.opengl.GLStates;
import com.stevenlr.waffle2.graphics.opengl.Shader;
import com.stevenlr.waffle2.utils.buffers.MultiFloatBuffer;
import com.stevenlr.waffle2.utils.buffers.MultiIntBuffer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.*;

public class SpriteTechnique {

	private static Shader _shader;
	private static final int POSITION_LOCATION = 0;
	private static final int TRANSFORM_LOCATION = 1;
	private static final int COLOR_LOCATION = 4;
	private static final int TEXTURE_ID_LOCATION = 5;

	private static final int BUFFER_SIZE = 1024;
	private static final int FLOAT_ELEMENT_SIZE = 13;
	private static final int INT_ELEMENT_SIZE = 1;

	private Quad _quad;
	private int _vao;
	private int _vboFloat;
	private int _vboInt;
	private MultiFloatBuffer _floatBuffer = new MultiFloatBuffer(BUFFER_SIZE * FLOAT_ELEMENT_SIZE);
	private MultiIntBuffer _intBuffer = new MultiIntBuffer(BUFFER_SIZE * INT_ELEMENT_SIZE);

	public static void init() {
		_shader = new Shader("/waffle2/shaders/sprite.vert", "/waffle2/shaders/sprite.frag");
		_shader.bindAttribLocation("in_Position", POSITION_LOCATION);
		_shader.bindAttribLocation("in_Transform", TRANSFORM_LOCATION);
		_shader.bindAttribLocation("in_Color", COLOR_LOCATION);
		_shader.bindAttribLocation("in_TextureId", TEXTURE_ID_LOCATION);
		_shader.link();

		_shader.bind();
		_shader.setUniform("u_TextureAtlas", 0);
		_shader.setUniform("u_TexturesData", 1);
		_shader.unbind();
	}

	public SpriteTechnique(Quad quad) {
		_quad = quad;

		_vao = glGenVertexArrays();
		GLStates.bindVertexArray(_vao);

		glEnableVertexAttribArray(POSITION_LOCATION);
		_quad.bindVertexAttrib(POSITION_LOCATION);

		_vboFloat = glGenBuffers();
		GLStates.bindBufferArray(_vboFloat);
		glBufferData(GL_ARRAY_BUFFER, _floatBuffer.getByteSize(), GL_DYNAMIC_DRAW);

		glEnableVertexAttribArray(TRANSFORM_LOCATION);
		glEnableVertexAttribArray(TRANSFORM_LOCATION + 1);
		glEnableVertexAttribArray(TRANSFORM_LOCATION + 2);
		glVertexAttribPointer(TRANSFORM_LOCATION, 3, GL_FLOAT, false, FLOAT_ELEMENT_SIZE * 4, 0);
		glVertexAttribPointer(TRANSFORM_LOCATION + 1, 3, GL_FLOAT, false, FLOAT_ELEMENT_SIZE * 4, 3 * 4);
		glVertexAttribPointer(TRANSFORM_LOCATION + 2, 3, GL_FLOAT, false, FLOAT_ELEMENT_SIZE * 4, 6 * 4);
		glVertexAttribDivisor(TRANSFORM_LOCATION, 1);
		glVertexAttribDivisor(TRANSFORM_LOCATION + 1, 1);
		glVertexAttribDivisor(TRANSFORM_LOCATION + 2, 1);

		glEnableVertexAttribArray(COLOR_LOCATION);
		glVertexAttribPointer(COLOR_LOCATION, 4, GL_FLOAT, false, FLOAT_ELEMENT_SIZE * 4, 9 * 4);
		glVertexAttribDivisor(COLOR_LOCATION, 1);

		_vboInt = glGenBuffers();
		GLStates.bindBufferArray(_vboInt);
		glBufferData(GL_ARRAY_BUFFER, _intBuffer.getByteSize(), GL_DYNAMIC_DRAW);

		glEnableVertexAttribArray(TEXTURE_ID_LOCATION);
		glVertexAttribIPointer(TEXTURE_ID_LOCATION, 1, GL_INT, 0, 0);
		glVertexAttribDivisor(TEXTURE_ID_LOCATION, 1);

		GLStates.bindBufferArray(0);
		GLStates.bindVertexArray(0);
	}

	public void add(Matrix3f transform, float r, float g, float b, float a, int textureId) {
		_floatBuffer.put(transform);
		_floatBuffer.put(r);
		_floatBuffer.put(g);
		_floatBuffer.put(b);
		_floatBuffer.put(a);
		_intBuffer.put(textureId);
	}

	public void doRenderPass(Matrix4f projection) {
		Waffle2.getInstance().bindTextureRegistry(0, 1);

		_shader.bind();
		_shader.setUniform("u_ProjectionMatrix", projection);
		GLStates.bindVertexArray(_vao);

		while (true) {
			MultiFloatBuffer.ReadInfo readFloatBuffer = _floatBuffer.read();
			MultiIntBuffer.ReadInfo readIntBuffer = _intBuffer.read();

			if (readFloatBuffer == null || readIntBuffer == null) {
				break;
			}

			GLStates.bindBufferArray(_vboFloat);
			glBufferSubData(GL_ARRAY_BUFFER, 0, readFloatBuffer.buffer);

			GLStates.bindBufferArray(_vboInt);
			glBufferSubData(GL_ARRAY_BUFFER, 0, readIntBuffer.buffer);

			GLStates.bindBufferArray(0);

			_quad.drawInstanced(readFloatBuffer.size / FLOAT_ELEMENT_SIZE);
		}
	}

	public void reset() {
		_floatBuffer.reset();
		_intBuffer.reset();
	}
}

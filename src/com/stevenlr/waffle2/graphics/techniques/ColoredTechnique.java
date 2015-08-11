package com.stevenlr.waffle2.graphics.techniques;

import com.stevenlr.waffle2.graphics.Quad;
import com.stevenlr.waffle2.graphics.opengl.GLStates;
import com.stevenlr.waffle2.graphics.opengl.Shader;
import com.stevenlr.waffle2.utils.buffers.MultiFloatBuffer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL33.*;

public class ColoredTechnique {

	private static Shader _coloredShader;
	private static final int POSITION_LOCATION = 0;
	private static final int TRANSFORM_LOCATION = 1;
	private static final int COLOR_LOCATION = 4;
	private static final int BUFFER_SIZE = 1024;

	private MultiFloatBuffer _transforms = new MultiFloatBuffer(9 * BUFFER_SIZE);
	private MultiFloatBuffer _colors = new MultiFloatBuffer(4 * BUFFER_SIZE);
	private Quad _quad;

	private int _vao;
	private int _vboTransforms;
	private int _vboColors;

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

		_vboTransforms = glGenBuffers();
		GLStates.bindBufferArray(_vboTransforms);
		glBufferData(GL_ARRAY_BUFFER, _transforms.getByteSize(), GL_DYNAMIC_DRAW);
		glEnableVertexAttribArray(TRANSFORM_LOCATION);
		glEnableVertexAttribArray(TRANSFORM_LOCATION + 1);
		glEnableVertexAttribArray(TRANSFORM_LOCATION + 2);
		glVertexAttribPointer(TRANSFORM_LOCATION, 3, GL_FLOAT, false, 9 * 4, 0);
		glVertexAttribPointer(TRANSFORM_LOCATION + 1, 3, GL_FLOAT, false, 9 * 4, 3 * 4);
		glVertexAttribPointer(TRANSFORM_LOCATION + 2, 3, GL_FLOAT, false, 9 * 4, 6 * 4);
		glVertexAttribDivisor(TRANSFORM_LOCATION, 1);
		glVertexAttribDivisor(TRANSFORM_LOCATION + 1, 1);
		glVertexAttribDivisor(TRANSFORM_LOCATION + 2, 1);

		_vboColors = glGenBuffers();
		GLStates.bindBufferArray(_vboColors);
		glBufferData(GL_ARRAY_BUFFER, _colors.getByteSize(), GL_DYNAMIC_DRAW);
		glEnableVertexAttribArray(COLOR_LOCATION);
		glVertexAttribPointer(COLOR_LOCATION, 4, GL_FLOAT, false, 0, 0);
		glVertexAttribDivisor(COLOR_LOCATION, 1);

		GLStates.bindBufferArray(0);
		GLStates.bindVertexArray(0);
	}

	public void add(Matrix3f transform, float r, float g, float b, float a) {
		_transforms.put(transform);
		_colors.put(r);
		_colors.put(g);
		_colors.put(b);
		_colors.put(a);
	}

	public void doRenderPass(Matrix4f projection) {
		_coloredShader.bind();
		_coloredShader.setUniform("u_ProjectionMatrix", projection);
		GLStates.bindVertexArray(_vao);

		while (true) {
			MultiFloatBuffer.ReadInfo readTransforms = _transforms.read();
			MultiFloatBuffer.ReadInfo readColors = _colors.read();

			if (readTransforms == null || readColors == null) {
				break;
			}

			GLStates.bindBufferArray(_vboTransforms);
			glBufferSubData(GL_ARRAY_BUFFER, 0, readTransforms.buffer);
			GLStates.bindBufferArray(_vboColors);
			glBufferSubData(GL_ARRAY_BUFFER, 0, readColors.buffer);
			GLStates.bindBufferArray(0);

			_quad.drawInstanced(readColors.size / 4);
		}
	}

	public void reset() {
		_transforms.reset();
		_colors.reset();
	}
}

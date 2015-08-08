package com.stevenlr.waffle2.graphics.techniques;

import com.stevenlr.waffle2.graphics.Quad;
import com.stevenlr.waffle2.graphics.opengl.GLStates;
import com.stevenlr.waffle2.graphics.opengl.Shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;

public class ColoredTechnique {

	private static Shader _coloredShader;
	private static final int POSITION_LOCATION = 0;
	private static final int TRANSFORM_LOCATION = 1;
	private static final int COLOR_LOCATION = 4;

	private int _vao;

	public static void init() {
		_coloredShader = new Shader("/shaders/colored.vert", "/shaders/colored.frag");
		_coloredShader.bindAttribLocation("in_Position", POSITION_LOCATION);
		_coloredShader.bindAttribLocation("in_Transform", TRANSFORM_LOCATION);
		_coloredShader.bindAttribLocation("in_Color", COLOR_LOCATION);
		_coloredShader.link();
	}

	public ColoredTechnique(Quad quad) {
		_vao = glGenVertexArrays();

		GLStates.bindVertexArray(_vao);
		glEnableVertexAttribArray(POSITION_LOCATION);
		quad.bindVertexAttrib(POSITION_LOCATION);
		GLStates.bindVertexArray(0);
	}
}

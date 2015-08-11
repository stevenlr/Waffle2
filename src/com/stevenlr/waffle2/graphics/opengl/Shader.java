package com.stevenlr.waffle2.graphics.opengl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Shader {

	private Map<String, Integer> _uniformCache = new HashMap<String, Integer>();
	private FloatBuffer _mat4fBuffer = BufferUtils.createFloatBuffer(16);
	private int _program = 0;

	public Shader(String vertFilename, String fragFilename) {
		int vertShader = makeShader(vertFilename, GL_VERTEX_SHADER);
		int fragShader = makeShader(fragFilename, GL_FRAGMENT_SHADER);

		_program = glCreateProgram();
		glAttachShader(_program, vertShader);
		glAttachShader(_program, fragShader);
		glBindFragDataLocation(_program, 0, "out_Color");
	}

	private int makeShader(String filename, int type) {
		int shader = glCreateShader(type);
		String source = "";
		String line;
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new InputStreamReader(Shader.class.getResourceAsStream(filename)));

			while ((line = reader.readLine()) != null) {
				source += line + "\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (reader != null) {
				reader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		glShaderSource(shader, source);
		glCompileShader(shader);

		if (glGetShaderi(shader, GL_COMPILE_STATUS) != GL_TRUE) {
			throw new RuntimeException("Error when compiling " + filename + ":\n" + glGetShaderInfoLog(shader));
		}

		return shader;
	}

	public int getUniformLocation(String name) {
		int location = -1;

		if (_uniformCache.containsKey(name)) {
			location = _uniformCache.get(name);
		} else {
			location = glGetUniformLocation(_program, name);
			_uniformCache.put(name, location);
		}

		return location;
	}

	public void setUniform(String name, int value) {
		int location = getUniformLocation(name);

		if (location >= 0) {
			glUniform1i(location, value);
		}
	}

	public void setUniform(String name, Matrix4f value) {
		int location = getUniformLocation(name);

		if (location >= 0) {
			value.get(_mat4fBuffer);
			glUniformMatrix4fv(location, false, _mat4fBuffer);
			_mat4fBuffer.clear();
		}
	}

	public void bindAttribLocation(String name, int location) {
		glBindAttribLocation(_program, location, name);
	}

	public void link() {
		glLinkProgram(_program);

		if (glGetProgrami(_program, GL_LINK_STATUS) != GL_TRUE) {
			throw new RuntimeException("Error when linking program:\n" + glGetProgramInfoLog(_program));
		}
	}

	public void bind() {
		GLStates.useProgram(_program);
	}

	public void unbind() {
		GLStates.useProgram(0);
	}
}

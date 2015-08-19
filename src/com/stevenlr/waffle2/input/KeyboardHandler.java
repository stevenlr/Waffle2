package com.stevenlr.waffle2.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.*;

public class KeyboardHandler {

	private KeyCallback _keyCallback = new KeyCallback();

	private Button[] _keys = new Button[GLFW_KEY_LAST + 1];
	private int[] _keysUpdated = new int[GLFW_KEY_LAST + 1];
	private int _nbKeysUpdated = 0;

	public class KeyCallback extends GLFWKeyCallback {

		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
			if (key < 0 || key > GLFW_KEY_LAST) {
				return;
			}

			switch (action) {
			case GLFW.GLFW_PRESS:
				_keys[key].press();
				break;
			case GLFW.GLFW_RELEASE:
				_keys[key].release();
				break;
			default:
				return;
			}

			_keysUpdated[_nbKeysUpdated++] = key;
		}
	}

	public KeyboardHandler() {
		for (int i = 0; i <= GLFW_KEY_LAST; ++i) {
			_keys[i] = new Button();
		}
	}

	public void setCallbacks(long window) {
		glfwSetKeyCallback(window, _keyCallback);
	}

	public void update() {
		for (int i = 0; i < _nbKeysUpdated; ++i) {
			_keys[_keysUpdated[i]].update();
		}

		_nbKeysUpdated = 0;
	}

	public boolean isDown(int key) {
		if (key < 0 || key > GLFW_KEY_LAST) {
			throw new RuntimeException("Unknown key " + key);
		}

		return _keys[key].isDown();
	}

	public boolean wasPressed(int key) {
		if (key < 0 || key > GLFW_KEY_LAST) {
			throw new RuntimeException("Unknown key " + key);
		}

		return _keys[key].wasPressed();
	}

	public boolean wasReleased(int key) {
		if (key < 0 || key > GLFW_KEY_LAST) {
			throw new RuntimeException("Unknown key " + key);
		}

		return _keys[key].wasReleased();
	}
}

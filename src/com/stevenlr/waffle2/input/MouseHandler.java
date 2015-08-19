package com.stevenlr.waffle2.input;

import com.stevenlr.waffle2.Waffle2;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import static org.lwjgl.glfw.GLFW.*;

public class MouseHandler {

	private MouseButtonCallback _mouseButtonCallback = new MouseButtonCallback();
	private CursorPosCallback _cursorPosCallback = new CursorPosCallback();

	private Button[] _buttons = new Button[GLFW_MOUSE_BUTTON_LAST + 1];
	private float _mouseX = 0;
	private float _mouseY = 0;

	private int _viewportWidth = Waffle2.getInstance().getViewportWidth();
	private int _viewportHeight = Waffle2.getInstance().getViewportHeight();
	private int _resolution = Waffle2.getInstance().getResolution();

	public class MouseButtonCallback extends GLFWMouseButtonCallback {

		@Override
		public void invoke(long window, int button, int action, int mods) {
			if (button < 0 || button > GLFW_MOUSE_BUTTON_LAST) {
				return;
			}

			switch (action) {
			case GLFW.GLFW_PRESS:
				_buttons[button].press();
				break;
			case GLFW.GLFW_RELEASE:
				_buttons[button].release();
				break;
			}
		}
	}

	public class CursorPosCallback extends GLFWCursorPosCallback {

		@Override
		public void invoke(long window, double x, double y) {
			_mouseX = (float) x / _resolution;
			_mouseY = (float) (_viewportHeight - (y / _resolution));
		}
	}

	public MouseHandler() {
		for (int i = 0; i <= GLFW_MOUSE_BUTTON_LAST; ++i) {
			_buttons[i] = new Button();
		}
	}

	public void setCallbacks(long window) {
		glfwSetMouseButtonCallback(window, _mouseButtonCallback);
		glfwSetCursorPosCallback(window, _cursorPosCallback);
	}

	public void update() {
		for (int i = 0; i <= GLFW_MOUSE_BUTTON_LAST; ++i) {
			_buttons[i].update();
		}
	}

	public boolean isDown(int button) {
		if (button < 0 || button > GLFW_MOUSE_BUTTON_LAST) {
			throw new RuntimeException("Unknown mouse button " + button);
		}

		return _buttons[button].isDown();
	}

	public boolean wasPressed(int button) {
		if (button < 0 || button > GLFW_MOUSE_BUTTON_LAST) {
			throw new RuntimeException("Unknown mouse button " + button);
		}

		return _buttons[button].wasPressed();
	}

	public boolean wasReleased(int button) {
		if (button < 0 || button > GLFW_MOUSE_BUTTON_LAST) {
			throw new RuntimeException("Unknown mouse button " + button);
		}

		return _buttons[button].wasReleased();
	}

	public Vector2f getMousePosition() {
		return new Vector2f(_mouseX, _mouseY);
	}

	public void getMousePosition(Vector2f v) {
		v.set(_mouseX, _mouseY);
	}

	public float getMouseX() {
		return _mouseX;
	}

	public float getMouseY() {
		return _mouseY;
	}
}

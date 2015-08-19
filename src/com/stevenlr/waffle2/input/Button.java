package com.stevenlr.waffle2.input;

public class Button {

	private boolean _currentState = false;
	private boolean _oldState = false;

	public void press() {
		_currentState = true;
	}

	public void release() {
		_currentState = false;
	}

	public void update() {
		_oldState = _currentState;
	}

	public boolean isDown() {
		return _currentState;
	}

	public boolean wasPressed() {
		return _currentState && !_oldState;
	}

	public boolean wasReleased() {
		return !_currentState && _oldState;
	}
}

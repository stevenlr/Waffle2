package com.stevenlr.waffle2.graphics;

import java.util.Deque;
import java.util.LinkedList;

import org.joml.Matrix4f;
import org.joml.Vector2f;

public class Camera {

	private float _aspectRatio;
	private float _aspectRatioModifier = 1;
	private float _radius = 10;
	private float _rotation = 0;
	private Matrix4f _transform = new Matrix4f();
	private boolean _isDirty = true;
	private Vector2f _center = new Vector2f(0, 0);

	private Deque<Camera> _stack = new LinkedList<Camera>();

	public Camera(float aspectRatio) {
		_aspectRatio = aspectRatio;
	}

	public Camera(Camera c) {
		copyFrom(c);
	}

	public void copyFrom(Camera c) {
		_aspectRatio = c._aspectRatio;
		_aspectRatioModifier = c._aspectRatioModifier;
		_radius = c._radius;
		_rotation = c._rotation;
		_transform.set(c._transform);
		_isDirty = true;
		_center.set(c._center);
	}

	void push() {
		_stack.push(new Camera(this));
	}

	void pop() {
		if (_stack.isEmpty()) {
			throw new RuntimeException("Popping empty transform stack");
		}

		copyFrom(_stack.pop());
	}

	public float getAspectRatioModifier() {
		return _aspectRatioModifier;
	}

	public void setAspectRatioModifier(float aspectRatioModifier) {
		_isDirty = true;
		_aspectRatioModifier = aspectRatioModifier;
	}

	public float getRadius() {
		return _radius;
	}

	public void setRadius(float radius) {
		_isDirty = true;
		_radius = radius;
	}

	public float getRotation() {
		return _rotation;
	}

	public void setRotation(float rotation) {
		_isDirty = true;
		_rotation = rotation;
	}

	public Vector2f getCenter() {
		return _center;
	}

	public void setCenter(Vector2f center) {
		_isDirty = true;
		_center = center;
	}

	public void setCenter(float x, float y) {
		_isDirty = true;
		_center.x = x;
		_center.y = y;
	}

	public Matrix4f getTransform() {
		if (_isDirty) {
			_transform.identity();
			_transform.ortho2D(-_radius, _radius,
					-_radius / (_aspectRatio * _aspectRatioModifier),
					_radius / (_aspectRatio * _aspectRatioModifier));
			_transform.rotateZ(_rotation);
			_transform.translate(-_center.x, -_center.y, 0);
			_isDirty = false;
		}

		return _transform;
	}
}

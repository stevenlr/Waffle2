package com.stevenlr.waffle2.graphics;

import java.util.Deque;
import java.util.LinkedList;

import com.stevenlr.waffle2.graphics.techniques.ColoredTechnique;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Renderer {

	private Canvas _canvas;
	private Matrix3f _transform;
	private Deque<Matrix3f> _stack = new LinkedList<Matrix3f>();
	private Matrix4f _projection;

	private static ColoredTechnique _coloredTechnique;
	private static Quad _quad;

	public static void init() {
		_quad = new Quad();

		ColoredTechnique.init();
		_coloredTechnique = new ColoredTechnique(_quad);
	}

	public Renderer(Canvas canvas) {
		_canvas = canvas;
		_transform = new Matrix3f();
		_transform.identity();
		_projection = new Matrix4f();
		_projection.ortho2D(0, _canvas.getWidth(), 0, _canvas.getHeight());
	}

	public void pop() {
		if (_stack.isEmpty()) {
			throw new RuntimeException("Popping empty transform stack");
		}

		_transform = _stack.pop();
	}

	public void push() {
		_stack.push(new Matrix3f(_transform));
	}

	public void identity() {
		_transform.identity();
	}

	public void translate(Vector2f offset) {
		_transform.translation(offset);
	}

	public void translate(float dx, float dy) {
		_transform.translation(dx, dy);
	}

	public void scale(Vector2f scale) {
		_transform.scale(scale.x, scale.y, 1);
	}

	public void scale(float sx, float sy) {
		_transform.scale(sx, sy, 1);
	}

	public void scale(float scale) {
		_transform.scale(scale, scale, 1);
	}

	public void rotate(float angle) {
		_transform.rotate(angle, 0, 0, 1);
	}

	public Matrix3f getTransform() {
		return _transform;
	}

	public Matrix4f getProjection() {
		return _projection;
	}

	public void fill(float r, float g, float b) {
		_canvas.bindDraw();
		glClearColor(r, g, b, 1);
		glClear(GL_COLOR_BUFFER_BIT);
	}

	public void blitCanvas(Canvas canvas, int x, int y) {
		_canvas.bindDraw();
		canvas.bindRead();

		glBlitFramebuffer(0, 0, canvas.getWidth(), canvas.getHeight(),
				x, y, x + canvas.getWidth(), y + canvas.getHeight(),
				GL_COLOR_BUFFER_BIT, GL_NEAREST);
	}
}

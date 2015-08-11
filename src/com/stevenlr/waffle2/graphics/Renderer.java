package com.stevenlr.waffle2.graphics;

import java.util.Deque;
import java.util.LinkedList;

import com.stevenlr.waffle2.graphics.techniques.ColoredTechnique;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class Renderer {

	private Canvas _canvas;
	private Matrix3f _transform;
	private Matrix3f _translation = new Matrix3f();
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
		_translation.identity();
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
		translate(offset.x, offset.y);
	}

	public void translate(float dx, float dy) {
		_translation.m20 = dx;
		_translation.m21 = dy;

		_transform.mul(_translation);
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
		_transform.rotateZ(angle);
	}

	public void rotate(float angle, float centerX, float centerY) {
		translate(centerX, centerY);
		_transform.rotateZ(angle);
		translate(-centerX, -centerY);
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

	public void fillRect(float x, float y, float sx, float sy, float r, float g, float b, float a) {
		push();
		translate(x, y);
		scale(sx, sy);
		_coloredTechnique.add(_transform, r, g, b, a);
		pop();
	}

	public void fillRect(Vector2f position, Vector2f size, float r, float g, float b, float a) {
		fillRect(position.x, position.y, size.x, size.y, r, g, b, a);
	}

	public void doRenderPass() {
		_canvas.bindDraw();

		// Alpha blending
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		_coloredTechnique.doRenderPass(_projection);
		_coloredTechnique.reset();

		// Additive blending
		glBlendFunc(GL_ONE, GL_ONE);

		// Multiplicative blending
		glBlendFunc(GL_DST_COLOR, GL_ZERO);
	}
}

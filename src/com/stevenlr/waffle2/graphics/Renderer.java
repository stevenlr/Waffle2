package com.stevenlr.waffle2.graphics;

import java.util.Deque;
import java.util.LinkedList;

import com.stevenlr.waffle2.Waffle2;
import com.stevenlr.waffle2.graphics.techniques.SpriteTechnique;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class Renderer {

	public static final int ALPHA = 0;
	public static final int ADDITIVE = 1;
	public static final int MULTIPLICATIVE = 2;

	private Canvas _canvas;
	private Matrix3f _transform;
	private Matrix3f _translation = new Matrix3f();
	private Deque<Matrix3f> _stack = new LinkedList<Matrix3f>();
	private Matrix4f _projection;

	private static int[][] _blendingModes;
	private static SpriteTechnique _spriteTechnique;
	private static Quad _quad;
	private int _blendingMode = ALPHA;
	private int _whiteTexture = -1;

	public static void init() {
		_quad = new Quad();

		SpriteTechnique.init();
		_spriteTechnique = new SpriteTechnique(_quad);

		_blendingModes = new int[3][2];
		_blendingModes[ALPHA][0] = GL_SRC_ALPHA;
		_blendingModes[ALPHA][1] = GL_ONE_MINUS_SRC_ALPHA;
		_blendingModes[ADDITIVE][0] = GL_ONE;
		_blendingModes[ADDITIVE][1] = GL_ONE;
		_blendingModes[MULTIPLICATIVE][0] = GL_DST_COLOR;
		_blendingModes[MULTIPLICATIVE][1] = GL_ZERO;
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
		if (_whiteTexture < 0) {
			_whiteTexture = Waffle2.getInstance().getTextureId("waffle2:white");
		}

		push();
		translate(x, y);
		scale(sx, sy);
		_spriteTechnique.add(_transform, r, g, b, a, _whiteTexture);
		pop();
	}

	public void fillRect(Vector2f position, Vector2f size, float r, float g, float b, float a) {
		fillRect(position.x, position.y, size.x, size.y, r, g, b, a);
	}

	public void setBlending(int mode) {
		if (mode < 0 || mode > _blendingModes.length) {
			throw new RuntimeException("Invalid blending mode");
		}

		if (mode != _blendingMode) {
			doRenderPass();
			_blendingMode = mode;
		}
	}

	public void doRenderPass() {
		_canvas.bindDraw();
		glBlendFunc(_blendingModes[_blendingMode][0], _blendingModes[_blendingMode][1]);
		_spriteTechnique.doRenderPass(_projection);
		_spriteTechnique.reset();
	}

	public void drawTile(Vector2f pos, Vector2f size, float r, float g, float b, float a, int textureId) {
		drawTile(pos.x, pos.y, size.x, size.y, r, g, b, a, textureId);
	}

	public void drawTile(float x, float y, float sx, float sy, float r, float g, float b, float a, int textureId) {
		push();
		translate(x, y);
		scale(sx, sy);
		_spriteTechnique.add(_transform, r, g, b, a, textureId);
		pop();
	}
}

package com.stevenlr.waffle2.graphics;

import java.util.Deque;
import java.util.LinkedList;

import com.stevenlr.waffle2.Waffle2;
import com.stevenlr.waffle2.graphics.techniques.SpriteTechnique;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

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

	private static int[][] _blendingModes;
	private SpriteTechnique _spriteTechnique;
	private static Quad _quad;
	private int _blendingMode = ALPHA;
	private int _whiteTexture = -1;
	private Camera _camera;

	private boolean _mirrorX = false;
	private boolean _mirrorY = false;
	private boolean _center = false;

	public static void init() {
		_quad = new Quad();

		SpriteTechnique.init();

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
		_translation.identity();
		_spriteTechnique = new SpriteTechnique(_quad);
		_camera = new Camera((float) canvas.getWidth() / canvas.getHeight());
	}

	public Camera getCamera() {
		return _camera;
	}

	public void pop() {
		if (_stack.isEmpty()) {
			throw new RuntimeException("Popping empty transform stack");
		}

		_transform = _stack.pop();
		_camera.pop();
	}

	public void push() {
		_stack.push(new Matrix3f(_transform));
		_camera.push();
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
		return _camera.getTransform();
	}

	public void fill(float r, float g, float b) {
		_canvas.bindDraw();
		glClearColor(r, g, b, 1);
		glClear(GL_COLOR_BUFFER_BIT);
	}

	public void setDrawParameters(boolean mirrorX, boolean mirrorY, boolean center) {
		_mirrorX = mirrorX;
		_mirrorY = mirrorY;
		_center = center;
	}

	public void resetDrawParameters() {
		_mirrorX = false;
		_mirrorY = false;
		_center = false;
	}

	public void blitCanvas(Canvas canvas, int x, int y) {
		canvas.getRenderer().doRenderPass();
		_canvas.getRenderer().doRenderPass();

		_canvas.bindDraw();
		canvas.bindRead();

		int sx0 = 0, sx1 = canvas.getWidth(), sy0 = 0, sy1 = canvas.getHeight();
		int dx0 = x, dx1 = x + canvas.getWidth(), dy0 = y, dy1 = y + canvas.getHeight();

		if (_mirrorX) {
			sx0 = canvas.getWidth();
			sx1 = 0;
		}

		if (_mirrorY) {
			sy0 = canvas.getHeight();
			sy1 = 0;
		}

		if (_center) {
			dx0 -= canvas.getWidth() / 2;
			dx1 -= canvas.getWidth() / 2;
			dy0 -= canvas.getHeight() / 2;
			dy1 -= canvas.getHeight() / 2;
		}

		glBlitFramebuffer(sx0, sy0, sx1, sy1, dx0, dy0, dx1, dy1, GL_COLOR_BUFFER_BIT, GL_NEAREST);
	}

	public void fillRect(float x, float y, float sx, float sy, float r, float g, float b, float a) {
		if (_whiteTexture < 0) {
			_whiteTexture = Waffle2.getInstance().getTextureRegistry().getTexture("waffle2:white");
		}

		push();
		translate(x, y);
		scale(sx, sy);

		if (_center) {
			translate(-0.5f, -0.5f);
		}

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
		_spriteTechnique.doRenderPass(_camera.getTransform());
		_spriteTechnique.reset();
	}

	public void drawText(String identifier, String text, Vector2f pos, float size, float r, float g, float b, float a, Font.Alignment align) {
		drawText(identifier, text, pos.x, pos.y, size, r, g, b, a, align);
	}

	public void drawText(String identifier, String text, float x, float y, float size, float r, float g, float b, float a, Font.Alignment align) {
		Waffle2.getInstance().getFontRegistry().getFont(identifier).drawText(this, text, x, y, size, r, g, b, a, align);
	}

	public void drawTile(Vector2f pos, Vector2f size, float r, float g, float b, float a, int textureId) {
		drawTile(pos.x, pos.y, size.x, size.y, r, g, b, a, textureId);
	}

	public void drawTile(Vector2f pos, Vector2f size, int textureId) {
		drawTile(pos.x, pos.y, size.x, size.y, 1, 1, 1, 1, textureId);
	}

	public void drawTile(float x, float y, float sx, float sy, int textureId) {
		drawTile(x, y, sx, sy, 1, 1, 1, 1, textureId);
	}

	public void drawTile(float x, float y, float sx, float sy, float r, float g, float b, float a, int textureId) {
		push();
		translate(x, y);
		scale(sx, sy);

		if ((_mirrorX || _mirrorY) && !_center) {
			translate(0.5f, 0.5f);
		}

		if (_mirrorX || _mirrorY) {
			scale((_mirrorX) ? -1 : 1, (_mirrorY) ? -1 : 1);
		}

		if (_mirrorX || _mirrorY || _center) {
			translate(-0.5f, -0.5f);
		}

		_spriteTechnique.add(_transform, r, g, b, a, textureId);
		pop();
	}

	public Vector2f toWorldCoords(Vector2f screenCoords) {
		Matrix4f p = new Matrix4f(_camera.getTransform());

		p.invert();

		Vector4f v4 = new Vector4f((screenCoords.x / Waffle2.getInstance().getViewportWidth()) * 2 - 1,
				(screenCoords.y / Waffle2.getInstance().getViewportHeight()) * 2 - 1,
				0, 1);

		v4.mul(p);

		return new Vector2f(v4.x / v4.w, v4.y / v4.w);
	}
}

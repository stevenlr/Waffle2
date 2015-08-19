package com.stevenlr.waffle2;

import java.awt.image.BufferedImage;

import com.stevenlr.waffle2.graphics.Canvas;
import com.stevenlr.waffle2.graphics.FontRegistry;
import com.stevenlr.waffle2.graphics.opengl.GLStates;
import com.stevenlr.waffle2.graphics.Renderer;
import com.stevenlr.waffle2.graphics.TextureRegistry;
import com.stevenlr.waffle2.input.KeyboardHandler;
import com.stevenlr.waffle2.input.MouseHandler;
import org.lwjgl.opengl.GLContext;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;

public class Waffle2 {

	private static Waffle2 _instance = null;

	private int _viewportWidth = 1280;
	private int _viewportHeight = 720;
	private int _resolution = 1;
	private String _title = "Waffle 2 Game";
	private boolean _showFps = false;

	private Canvas _canvas;

	private TextureRegistry _textureRegistry = new TextureRegistry();
	private FontRegistry _fontRegistry = new FontRegistry();
	private MouseHandler _mouseHandler;
	private KeyboardHandler _keyboardHandler;

	public static Waffle2 getInstance() {
		if (_instance == null) {
			_instance = new Waffle2();
		}

		return _instance;
	}

	private Waffle2() {
	}

	private void preInit() {
		_mouseHandler = new MouseHandler();
		_keyboardHandler = new KeyboardHandler();

		_textureRegistry.registerTexture("/waffle2/textures/white.png", "waffle2:white");
	}

	private void init() {
		Renderer.init();
	}

	public void launchGame(IWaffle2Game game) {
		getAnnotationData(game);

		long window;

		if (glfwInit() != GL_TRUE) {
			throw new RuntimeException("Couldn't init GLFW");
		}

		glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		window = glfwCreateWindow(_viewportWidth * _resolution, _viewportHeight * _resolution, _title, 0, 0);

		if (window == 0) {
			throw new RuntimeException("Couldn't create GLFW window");
		}

		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		glfwShowWindow(window);
		GLContext.createFromCurrent();

		preInit();
		_mouseHandler.setCallbacks(window);
		_keyboardHandler.setCallbacks(window);
		game.preInit();

		_textureRegistry.buildAtlas();
		_fontRegistry.buildFonts();

		init();
		game.init();

		_canvas = new Canvas(_viewportWidth, _viewportHeight);

		glDisable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		glBlendEquation(GL_FUNC_ADD);

		double frameTimeExpected = 1.0 / 60;
		double previousTime = glfwGetTime();
		double currentTime, updateTime ;
		double totalTime = 0, frameTime;
		int totalFrames = 0;

		while (glfwWindowShouldClose(window) == GL_FALSE) {
			currentTime = glfwGetTime();
			updateTime = currentTime - previousTime;
			totalTime += currentTime - previousTime;
			previousTime = currentTime;

			updateTime = Math.min(updateTime, 4 * frameTimeExpected);

			game.update((float) updateTime);
			_mouseHandler.update();
			_keyboardHandler.update();

			totalFrames++;

			_canvas.bindDraw();
			glClearColor(0, 0, 0, 1);
			glClear(GL_COLOR_BUFFER_BIT);
			game.draw(_canvas.getRenderer());
			_canvas.getRenderer().doRenderPass();

			GLStates.bindDrawFramebuffer(0);
			glDrawBuffer(GL_FRONT_LEFT);
			_canvas.bindRead();

			glViewport(0, 0, _viewportWidth * _resolution, _viewportHeight * _resolution);
			glBlitFramebuffer(0, 0, _viewportWidth, _viewportHeight,
					0, 0, _viewportWidth * _resolution, _viewportHeight * _resolution,
					GL_COLOR_BUFFER_BIT, GL_NEAREST);

			glfwSwapBuffers(window);
			glfwPollEvents();

			if (totalTime >= 1) {
				if (_showFps) {
					System.out.println(totalFrames + " fps");
				}

				totalFrames = 0;
				totalTime = 0;
			}

			frameTime = glfwGetTime() - previousTime;

			if (frameTime < frameTimeExpected) {
				try {
					Thread.sleep((long) ((frameTimeExpected - frameTime) * 1000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		glfwDestroyWindow(window);
	}

	private void getAnnotationData(IWaffle2Game game) {
		Waffle2Game annotation = game.getClass().getAnnotation(Waffle2Game.class);

		if (annotation != null) {
			_viewportWidth = annotation.viewportWidth();
			_viewportHeight = annotation.viewportHeight();
			_resolution = annotation.resolution();
			_title = annotation.title();
			_showFps = annotation.showFps();
		}
	}

	public int getViewportWidth() {
		return _viewportWidth;
	}

	public int getViewportHeight() {
		return _viewportHeight;
	}

	public int getResolution() {
		return _resolution;
	}

	public TextureRegistry getTextureRegistry() {
		return _textureRegistry;
	}

	public FontRegistry getFontRegistry() {
		return _fontRegistry;
	}

	public MouseHandler getMouseHandler() {
		return _mouseHandler;
	}

	public KeyboardHandler getKeyboardHandler() {
		return _keyboardHandler;
	}
}

package com.stevenlr.waffle2;

import com.stevenlr.waffle2.graphics.Renderer;

public interface IWaffle2Game {

	void preInit();
	void init();
	void update(float dt);
	void draw(Renderer r);
}

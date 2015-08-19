package com.stevenlr.waffle2.particles;

public abstract class Particle {

	public float x = 0;
	public float y = 0;
	public float dx = 0;
	public float dy = 0;
	public float age = 0;

	public abstract void update(double dt);
	public abstract int getTexture();
}

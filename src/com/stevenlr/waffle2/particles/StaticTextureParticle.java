package com.stevenlr.waffle2.particles;

public class StaticTextureParticle extends Particle {

	private int _texture;

	public StaticTextureParticle(int texture) {
		_texture = texture;
	}

	@Override
	public void update(double dt) {
	}

	@Override
	public int getTexture() {
		return _texture;
	}
}

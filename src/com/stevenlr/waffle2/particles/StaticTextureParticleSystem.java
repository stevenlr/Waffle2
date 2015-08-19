package com.stevenlr.waffle2.particles;

public class StaticTextureParticleSystem extends ParticleSystem {

	private int _textureId;

	public StaticTextureParticleSystem(int textureId, int maxParticles) {
		super(maxParticles);

		_textureId = textureId;
	}

	@Override
	public Particle newParticle() {
		return new StaticTextureParticle(_textureId);
	}
}

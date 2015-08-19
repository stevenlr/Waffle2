package com.stevenlr.waffle2.particles;

import com.stevenlr.waffle2.graphics.Animation;

public class AnimatedTextureParticleSystem extends ParticleSystem {

	private Animation _animation;

	public AnimatedTextureParticleSystem(Animation animation, int maxParticles) {
		super(maxParticles);

		_animation = animation;
	}

	@Override
	public Particle newParticle() {
		return new AnimatedTextureParticle(_animation.makeInstance());
	}
}

package com.stevenlr.waffle2.particles;

import com.stevenlr.waffle2.graphics.Animation;

public class AnimatedTextureParticle extends Particle {

	private Animation.Instance _animation;

	public AnimatedTextureParticle(Animation.Instance animation) {
		_animation = animation;
	}

	@Override
	public void update(double dt) {
		_animation.update(dt);
	}

	@Override
	public int getTexture() {
		return _animation.getTextureId();
	}
}

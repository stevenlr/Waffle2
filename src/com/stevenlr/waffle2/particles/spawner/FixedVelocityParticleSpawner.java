package com.stevenlr.waffle2.particles.spawner;

import com.stevenlr.waffle2.particles.Particle;

public class FixedVelocityParticleSpawner extends ParticleSpawner {

	private float _dx;
	private float _dy;

	public FixedVelocityParticleSpawner(float dx, float dy, ParticleSpawner subSpawner) {
		super(subSpawner);
		_dx = dx;
		_dy = dy;
	}

	@Override
	public Particle spawnParticle() {
		if (_subSpawner != null) {
			Particle p = _subSpawner.spawnParticle();

			p.dx = _dx;
			p.dy = _dy;

			return p;
		}

		return null;
	}
}

package com.stevenlr.waffle2.particles.spawner;

import com.stevenlr.waffle2.particles.Particle;

public class PointParticleSpawner extends ParticleSpawner {

	private float _x;
	private float _y;

	public PointParticleSpawner(float x, float y, ParticleSpawner subSpawner) {
		super(subSpawner);
		_x = x;
		_y = y;
	}

	@Override
	public Particle spawnParticle() {
		if (_subSpawner != null) {
			Particle p = _subSpawner.spawnParticle();

			p.x = _x;
			p.y = _y;

			return p;
		}

		return null;
	}
}

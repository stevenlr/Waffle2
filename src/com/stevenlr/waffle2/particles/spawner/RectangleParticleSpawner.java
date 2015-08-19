package com.stevenlr.waffle2.particles.spawner;

import java.util.Random;

import com.stevenlr.waffle2.particles.Particle;

public class RectangleParticleSpawner extends ParticleSpawner {

	private static Random rand = new Random();

	private float _x1;
	private float _y1;
	private float _x2;
	private float _y2;

	public RectangleParticleSpawner(float x1, float y1, float x2, float y2, ParticleSpawner subSpawner) {
		super(subSpawner);
		_x1 = x1;
		_y1 = y1;
		_x2 = x2;
		_y2 = y2;
	}

	@Override
	public Particle spawnParticle() {
		if (_subSpawner != null) {
			Particle p = _subSpawner.spawnParticle();

			float x = (rand.nextFloat() - 0.5f) * 2;
			float y = (rand.nextFloat() - 0.5f) * 2;

			p.x = _x1 + (_x2 - _x1) * x;
			p.y = _y1 + (_y2 - _y1) * y;

			return p;
		}

		return null;
	}
}

package com.stevenlr.waffle2.particles.spawner;

import java.util.Random;

import com.stevenlr.waffle2.particles.Particle;

public class DiscParticleSpawner extends ParticleSpawner {

	private static Random rand = new Random();

	private float _x;
	private float _y;
	private float _radius;

	public DiscParticleSpawner(float x, float y, float radius, ParticleSpawner subSpawner) {
		super(subSpawner);
		_radius = radius;
		_x = x;
		_y = y;
	}

	@Override
	public Particle spawnParticle() {
		if (_subSpawner != null) {
			Particle p = _subSpawner.spawnParticle();

			float radius = rand.nextFloat() * _radius;
			float angle = rand.nextFloat() * (float) Math.PI * 2;

			p.x = _x + radius * (float) Math.cos(angle);
			p.y = _y + radius * (float) Math.sin(angle);

			return p;
		}

		return null;
	}
}

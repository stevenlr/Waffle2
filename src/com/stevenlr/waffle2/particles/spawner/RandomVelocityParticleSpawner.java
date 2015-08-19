package com.stevenlr.waffle2.particles.spawner;

import java.util.Random;

import com.stevenlr.waffle2.particles.Particle;

public class RandomVelocityParticleSpawner extends ParticleSpawner {

	private static Random rand = new Random();
	private float _minVelocity;
	private float _maxVelocity;
	private float _deviation;
	private float _angle;

	public RandomVelocityParticleSpawner(float minVelocity, float maxVelocity, float dx, float dy, float deviation, ParticleSpawner subSpawner) {
		super(subSpawner);
		_minVelocity = minVelocity;
		_maxVelocity = maxVelocity;
		_angle = (float) Math.atan2(dy, dx);
		_deviation = deviation;
	}

	public RandomVelocityParticleSpawner(float minVelocity, float maxVelocity, ParticleSpawner subSpawner) {
		super(subSpawner);
		_minVelocity = minVelocity;
		_maxVelocity = maxVelocity;
		_angle = 0;
		_deviation = (float) Math.PI;
	}

	@Override
	public Particle spawnParticle() {
		if (_subSpawner != null) {
			Particle p = _subSpawner.spawnParticle();

			float angle = (rand.nextFloat() - 0.5f) * 2 * _deviation + _angle;
			float velocity = _minVelocity + (_maxVelocity - _minVelocity) * rand.nextFloat();

			p.dx = (float) Math.cos(angle) * velocity;
			p.dy = (float) Math.sin(angle) * velocity;

			return p;
		}

		return null;
	}
}

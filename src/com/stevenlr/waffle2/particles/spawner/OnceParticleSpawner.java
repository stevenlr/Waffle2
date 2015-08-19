package com.stevenlr.waffle2.particles.spawner;

import com.stevenlr.waffle2.particles.Particle;
import com.stevenlr.waffle2.particles.ParticleSystem;

public class OnceParticleSpawner extends ParticleSpawner {

	private int _numToSpawn;
	private float _delay;
	private ParticleSystem _system;
	private int _numSpawned = 0;

	public OnceParticleSpawner(int numToSpawn, float delay) {
		super(null);
		_numToSpawn = numToSpawn;
	}

	@Override
	public void update(double dt) {
		_delay -= dt;
	}

	@Override
	public boolean canSpawnParticle() {
		return _delay <= 0 && _numSpawned < _numToSpawn;
	}

	@Override
	public boolean isDoneSpawning() {
		return _delay <= 0 && _numSpawned >= _numToSpawn;
	}

	@Override
	public Particle spawnParticle() {
		if (_system == null) {
			throw new RuntimeException("No particle system specified");
		}

		_numSpawned++;

		return _system.addParticle();
	}

	@Override
	public void setParticleSystem(ParticleSystem system) {
		_system = system;
	}
}

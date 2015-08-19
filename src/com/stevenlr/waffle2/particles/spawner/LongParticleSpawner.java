package com.stevenlr.waffle2.particles.spawner;

import com.stevenlr.waffle2.particles.Particle;
import com.stevenlr.waffle2.particles.ParticleSystem;

public class LongParticleSpawner extends ParticleSpawner {

	private int _numToSpawn;
	private float _delay;
	private ParticleSystem _system;
	private float _timeSinceLastSpawned = 0;
	private int _numSpawned = 0;

	public LongParticleSpawner(int numToSpawn, float duration) {
		super(null);
		_numToSpawn = numToSpawn;
		_delay = duration / numToSpawn;
	}

	@Override
	public boolean canSpawnParticle() {
		return _timeSinceLastSpawned >= _delay && _numSpawned < _numToSpawn;
	}

	@Override
	public boolean isDoneSpawning() {
		return _numSpawned >= _numToSpawn;
	}

	@Override
	public Particle spawnParticle() {
		if (_system == null) {
			throw new RuntimeException("No particle system specified");
		}

		_numSpawned++;
		_timeSinceLastSpawned = 0;

		return _system.addParticle();
	}

	@Override
	public void update(double dt) {
		_timeSinceLastSpawned += dt;
	}

	@Override
	public void setParticleSystem(ParticleSystem system) {
		_system = system;
	}
}

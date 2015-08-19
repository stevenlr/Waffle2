package com.stevenlr.waffle2.particles.spawner;

import com.stevenlr.waffle2.particles.ParticleSystem;

public abstract class ParticleSpawner implements IParticleSpawnerDecorator {

	protected ParticleSpawner _subSpawner;

	public ParticleSpawner(ParticleSpawner subSpawner) {
		_subSpawner = subSpawner;
	}

	@Override
	public boolean canSpawnParticle() {
		if (_subSpawner != null) {
			return _subSpawner.canSpawnParticle();
		}

		return false;
	}

	@Override
	public boolean isDoneSpawning() {
		if (_subSpawner != null) {
			return _subSpawner.isDoneSpawning();
		}

		return true;
	}

	public void update(double dt) {
		if (_subSpawner != null) {
			_subSpawner.update(dt);
		}
	}

	@Override
	public void setParticleSystem(ParticleSystem system) {
		if (_subSpawner != null) {
			_subSpawner.setParticleSystem(system);
		}
	}
}

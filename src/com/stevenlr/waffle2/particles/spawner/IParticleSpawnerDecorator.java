package com.stevenlr.waffle2.particles.spawner;

import com.stevenlr.waffle2.particles.Particle;
import com.stevenlr.waffle2.particles.ParticleSystem;

public interface IParticleSpawnerDecorator {

	boolean canSpawnParticle();
	boolean isDoneSpawning();
	Particle spawnParticle();
	void setParticleSystem(ParticleSystem system);
}

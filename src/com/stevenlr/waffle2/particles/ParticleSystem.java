package com.stevenlr.waffle2.particles;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.stevenlr.waffle2.graphics.Renderer;
import com.stevenlr.waffle2.particles.spawner.ParticleSpawner;

public abstract class ParticleSystem {

	private float _linearDamping = 0;
	private float _maxAge = 1;
	private float _linearAccelerationX = 0;
	private float _linearAccelerationY = 0;
	private float _r = 1, _g = 1, _b = 1, _a = 1;
	private float _sizeX = 1;
	private float _sizeY = 1;

	private int _maxParticles;
	private int _circularBufferBottom = 0;
	private int _circularBufferTop = 0;
	private Particle[] _particles;
	private boolean _renderAdditive = false;
	private List<ParticleSpawner> _spawners = new LinkedList<ParticleSpawner>();

	public ParticleSystem(int maxParticles) {
		_maxParticles = maxParticles + 1;
		_particles = new Particle[_maxParticles];
	}

	public void setSize(float x, float y) {
		_sizeX = x;
		_sizeY = y;
	}

	public void setColor(float r, float g, float b, float a) {
		_r = r;
		_g = g;
		_b = b;
		_a = a;
	}

	public void setDuration(float duration) {
		_maxAge = duration;
	}

	public void setLinearDamping(float damping) {
		_linearDamping = damping;
	}

	public void setLinearAcceleration(float ax, float ay) {
		_linearAccelerationX = ax;
		_linearAccelerationY = ay;
	}

	public void setRenderAdditive(boolean additive) {
		_renderAdditive = additive;
	}

	public abstract Particle newParticle();

	public Particle addParticle() {
		Particle particle = newParticle();

		if ((_circularBufferTop + 1) % _maxParticles == _circularBufferBottom) {
			_circularBufferBottom = (_circularBufferBottom + 1) % _maxParticles;
		}

		_particles[_circularBufferTop] = particle;
		_circularBufferTop = (_circularBufferTop + 1) % _maxParticles;

		return particle;
	}

	public void update(double dt) {
		Iterator<ParticleSpawner> it = _spawners.iterator();

		while (it.hasNext()) {
			ParticleSpawner spawner = it.next();

			spawner.update(dt);

			while (spawner.canSpawnParticle()) {
				spawner.spawnParticle();
			}

			if (spawner.isDoneSpawning()) {
				it.remove();
			}
		}

		for (int i = _circularBufferBottom; i != _circularBufferTop; i = (i + 1) % _maxParticles) {
			Particle p = _particles[i];
			boolean _isDead = false;

			if (p != null && p.age >= _maxAge) {
				_isDead = true;
			} else if (p != null) {
				p.dx += _linearAccelerationX * dt;
				p.dy += _linearAccelerationY * dt;
				p.dx *= 1 - _linearDamping;
				p.dy *= 1 - _linearDamping;
				p.x += p.dx * dt;
				p.y += p.dy * dt;
				p.age += dt;
				p.update(dt);
			}

			if (_isDead) {
				_particles[i] = null;
				_circularBufferBottom = (_circularBufferBottom + 1) % _maxParticles;
			}
		}
	}

	public void draw(Renderer r) {
		if (_renderAdditive) {
			r.setBlending(Renderer.ADDITIVE);
		} else {
			r.setBlending(Renderer.ALPHA);
		}

		for (int i = _circularBufferBottom; i != _circularBufferTop; i = (i + 1) % _maxParticles) {
			Particle p = _particles[i];

			 if (p != null) {
				 r.drawTile(p.x - _sizeX / 2, p.y - _sizeY / 2, _sizeX, _sizeY, _r, _g, _b, _a, p.getTexture());
			 }
		}
	}

	public void addSpawner(ParticleSpawner spawner) {
		spawner.setParticleSystem(this);
		_spawners.add(spawner);
	}
}

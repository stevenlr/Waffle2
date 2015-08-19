package com.stevenlr.waffle2.audio;

import static org.lwjgl.openal.AL10.*;

public class SoundSource {

	private float _defaultGain;
	private int _nbSources;
	private int[] _sources;
	private int _nextSource = 0;

	public SoundSource(Sound sound, boolean repeat, float gain, int nbSources) {
		_nbSources = nbSources;
		_sources = new int[_nbSources];
		_defaultGain = gain;

		for (int i = 0; i < _nbSources; ++i) {
			int s = alGenSources();

			alSourcei(s, AL_BUFFER, sound.getBuffer());
			alSourcei(s, AL_LOOPING, (repeat) ? AL_TRUE : AL_FALSE);
			alSourcef(s, AL_GAIN, gain);

			_sources[i] = s;
		}
	}

	public void destroy() {
		for (int i = 0; i < _nbSources; ++i) {
			alDeleteSources(_sources[i]);
		}
	}

	public void setGain(float gain) {
		_defaultGain = gain;

		for (int i = 0; i < _nbSources; ++i) {
			alSourcef(_sources[i], AL_GAIN, gain);
		}
	}

	public void play() {
		play(_defaultGain, 1);
	}

	public void play(float gain, float pitch) {
		int s = _sources[_nextSource];

		if (alGetSourcei(s, AL_PLAYING) == AL_TRUE) {
			alSourceRewind(s);
		}

		alSourcef(s, AL_GAIN, gain * _defaultGain);
		alSourcef(s, AL_PITCH, pitch);
		alSourcePlay(s);

		_nextSource = (_nextSource + 1) % _nbSources;
	}

	public void pause() {
		if (_nbSources > 1) {
			return;
		}

		alSourcePause(_sources[0]);
	}

	public void stop() {
		if (_nbSources > 1) {
			return;
		}

		alSourceStop(_sources[0]);
	}
}

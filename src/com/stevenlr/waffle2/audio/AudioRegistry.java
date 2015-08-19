package com.stevenlr.waffle2.audio;

import java.util.HashMap;
import java.util.Map;

public class AudioRegistry {

	private Map<String, Sound> _sounds = new HashMap<String, Sound>();
	private Map<String, SoundSource> _sources = new HashMap<String, SoundSource>();

	public void registerSound(String id, String filename) {
		_sounds.put(id, new Sound(filename));
	}

	public void registerSource(String id, String sound, boolean repeat, float gain, int nbSource) {
		if (!_sounds.containsKey(sound)) {
			throw new RuntimeException("Sound " + sound + " does not exist");
		}

		_sources.put(id, new SoundSource(_sounds.get(sound), repeat, gain, nbSource));
	}

	public SoundSource getSource(String id) {
		if (!_sources.containsKey(id)) {
			throw new RuntimeException("Source " + id + " does not exist");
		}

		return _sources.get(id);
	}
}

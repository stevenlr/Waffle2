package com.stevenlr.waffle2.graphics;

import java.util.ArrayList;
import java.util.List;

import com.stevenlr.waffle2.Waffle2;

public class Animation {

	public class Frame {

		public int textureId;
		public float duration;
	}

	public class Instance {

		private float _frameTime = 0;
		private int _frameId = 0;

		public void update(double dt) {
			Frame frame;
			int nbFrames = getNbFrames();

			_frameTime += dt;

			while (_frameTime >= (frame = getFrame(_frameId)).duration) {
				_frameTime -= frame.duration;
				_frameId++;

				if (_frameId >= nbFrames) {
					_frameId = 0;
				}
			}
		}

		public int getTextureId() {
			return getFrame(_frameId).textureId;
		}
	}

	private String _tilesetIdentifier;
	private List<Frame> _frames = new ArrayList<Frame>();

	public Animation(String tilesetIdentifier) {
		_tilesetIdentifier = tilesetIdentifier;
	}

	public void addFrame(int tileId, float duration) {
		Frame frame = new Frame();

		frame.textureId = Waffle2.getInstance().getTextureRegistry().getTexture(_tilesetIdentifier, tileId);
		frame.duration = duration;

		_frames.add(frame);
	}

	public Instance makeInstance() {
		return new Instance();
	}

	public Frame getFrame(int id) {
		if (id < 0 || id > _frames.size()) {
			throw new RuntimeException("Invalid frame id");
		}

		return _frames.get(id);
	}

	public int getNbFrames() {
		return _frames.size();
	}
}

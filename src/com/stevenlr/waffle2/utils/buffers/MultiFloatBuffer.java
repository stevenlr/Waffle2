package com.stevenlr.waffle2.utils.buffers;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix3f;
import org.lwjgl.BufferUtils;

public class MultiFloatBuffer {

	private int _size;
	private int _used = 0;
	private int _bufferUsed = 0;
	private List<FloatBuffer> _buffers = new ArrayList<FloatBuffer>();
	private int _nextRead = 0;
	private boolean _isReading = false;

	public MultiFloatBuffer(int size) {
		_size = size;
		_buffers.add(BufferUtils.createFloatBuffer(_size));
	}

	public void reset() {
		_used = 0;
		_nextRead = 0;
		_isReading = false;
		_bufferUsed = 0;

		for (FloatBuffer b : _buffers) {
			b.clear();
		}
	}

	private void growIfNeeded(int size) {
		if (_isReading) {
			throw new RuntimeException("Cannot write in multi-buffer while reading");
		}

		if (size > _size) {
			throw new RuntimeException("Trying to insert array larger that buffer size");
		}

		if (_used + size > _size * _buffers.size()) {
			allocateNewBuffer();
		}

		if (_used + size > _size * (_bufferUsed + 1)) {
			_bufferUsed++;
		}
	}

	public void put(float f) {
		growIfNeeded(1);

		_buffers.get(_bufferUsed).put(f);
		_used++;
	}

	public void put(float[] f) {
		growIfNeeded(f.length);

		_buffers.get(_bufferUsed).put(f);
		_used += f.length;
	}

	public void put(Matrix3f m) {
		growIfNeeded(9);

		FloatBuffer buffer = _buffers.get(_bufferUsed);

		m.get(buffer);
		buffer.position(buffer.position() + 9);
		_used += 9;
	}

	private void allocateNewBuffer() {
		_used = _buffers.size() * _size;
		_buffers.add(BufferUtils.createFloatBuffer(_size));
	}

	public class ReadInfo {
		public FloatBuffer buffer;
		public int size;
	}

	public ReadInfo read() {
		ReadInfo info = new ReadInfo();

		if (_nextRead >= _buffers.size()) {
			return null;
		}

		info.buffer = _buffers.get(_nextRead++);
		info.buffer.flip();

		if (_used >= _size) {
			info.size = _size;
			_used -= _size;
		} else {
			info.size = _used;
			_used = 0;
		}

		return info;
	}

	public long getByteSize() {
		return _size * 4;
	}
}

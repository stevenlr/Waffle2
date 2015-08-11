package com.stevenlr.waffle2.utils.buffers;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;

public class MultiIntBuffer {

	private int _size;
	private int _used = 0;
	private List<IntBuffer> _buffers = new ArrayList<IntBuffer>();
	private int _nextRead = 0;
	private boolean _isReading = false;

	public MultiIntBuffer(int size) {
		_size = size;
		_buffers.add(BufferUtils.createIntBuffer(_size));
	}

	public void reset() {
		_used = 0;
		_nextRead = 0;
		_isReading = false;

		for (IntBuffer b : _buffers) {
			b.clear();
		}
	}

	public void put(int i) {
		if (_isReading) {
			throw new RuntimeException("Cannot write in multi-buffer while reading");
		}

		if ((_used + 1) % _size > _size) {
			allocateNewBuffer();
		}

		_buffers.get(_buffers.size() - 1).put(i);
	}

	public void put(int[] i) {
		if (_isReading) {
			throw new RuntimeException("Cannot write in multi-buffer while reading");
		}

		if (i.length > _size) {
			throw new RuntimeException("Trying to insert array larger that buffer size");
		}

		if ((_used + i.length) % _size > _size) {
			allocateNewBuffer();
		}

		_buffers.get(_buffers.size() - 1).put(i);
	}

	private void allocateNewBuffer() {
		_used = _buffers.size() * _size;
		_buffers.add(BufferUtils.createIntBuffer(_size));
	}

	public class ReadInfo {
		public IntBuffer buffer;
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

	public IntBuffer writeIn(int size) {
		if (_isReading) {
			throw new RuntimeException("Cannot write in multi-buffer while reading");
		}

		if (size > _size) {
			throw new RuntimeException("Trying to insert array larger that buffer size");
		}

		if ((_used + size) % _size > _size) {
			allocateNewBuffer();
		}

		return _buffers.get(_buffers.size() - 1);
	}
}
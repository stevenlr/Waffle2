package com.stevenlr.waffle2.audio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBVorbisInfo;

import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.openal.AL10.*;

public class Sound {

	private int _buffer = -1;

	public Sound(String filename) {
		STBVorbisInfo info = new STBVorbisInfo();
		ByteBuffer buffer = readVorbis(filename, info);

		int format;

		switch (info.getChannels()) {
		case 1:
			format = AL_FORMAT_MONO16;
			break;
		case 2:
			format = AL_FORMAT_STEREO16;
			break;
		default:
			throw new RuntimeException("Unsupported audio format for " + filename);
		}

		_buffer = alGenBuffers();
		alBufferData(_buffer, format, buffer, info.getSampleRate());
	}

	public void destroy() {
		alDeleteBuffers(_buffer);
		_buffer = -1;
	}

	static ByteBuffer readVorbis(String resource, STBVorbisInfo info) {
		ByteBuffer vorbis;

		try {
			vorbis = ioResourceToByteBuffer(resource);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		IntBuffer error = BufferUtils.createIntBuffer(1);
		long decoder = stb_vorbis_open_memory(vorbis, error, null);

		if (decoder == NULL) {
			throw new RuntimeException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
		}

		stb_vorbis_get_info(decoder, info.buffer());

		int channels = info.getChannels();
		int lengthSamples = stb_vorbis_stream_length_in_samples(decoder);
		ByteBuffer pcm = BufferUtils.createByteBuffer(lengthSamples * 2 * channels);

		stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm, lengthSamples * channels);
		stb_vorbis_close(decoder);

		return pcm;
	}

	public static ByteBuffer ioResourceToByteBuffer(String resource) throws IOException {
		InputStream is = Sound.class.getResourceAsStream(resource);
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[16384];

		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		buffer.flush();

		ByteBuffer b = BufferUtils.createByteBuffer(buffer.size());

		b.put(buffer.toByteArray());
		b.flip();

		return b;
	}

	int getBuffer() {
		return _buffer;
	}
}

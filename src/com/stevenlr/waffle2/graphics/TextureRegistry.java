package com.stevenlr.waffle2.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.stevenlr.waffle2.graphics.opengl.GLStates;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;

public class TextureRegistry {

	private boolean _atlasBuilt = false;
	private TextureAtlas _atlas = new TextureAtlas();
	private List<String> _identifiers = new ArrayList<String>();
	private Map<String, Texture> _textures = new HashMap<String, Texture>();

	private int _buffer;
	private int _bufferTexture;

	public void registerTexture(BufferedImage img, String identifier) {
		registerTexture(img, identifier, 0, 0);
	}

	public void registerTexture(BufferedImage img, String identifier, int tileWidth, int tileHeight) {
		if (_atlasBuilt) {
			throw new RuntimeException("Cannot register textures after pre-init");
		}

		_atlas.registerTexture(img);
		_identifiers.add(identifier);

		Texture texture = new Texture();

		texture.width = img.getWidth();
		texture.height = img.getHeight();

		if (tileWidth < 1 || tileHeight < 1) {
			tileWidth = texture.width;
			tileHeight = texture.height;
		}

		texture.tileWidth = tileWidth;
		texture.tileHeight = tileHeight;
		texture.nbTilesX = Math.floorDiv(texture.width, texture.tileWidth);
		texture.nbTilesY = Math.floorDiv(texture.height, texture.tileHeight);
		_textures.put(identifier, texture);
	}

	public void registerTexture(String filename, String identifier) {
		registerTexture(filename, identifier, 0, 0);
	}

	public void registerTexture(String filename, String identifier, int tileWidth, int tileHeight) {
		try {
			BufferedImage img = ImageIO.read(TextureRegistry.class.getResourceAsStream(filename));

			registerTexture(img, identifier, tileWidth, tileHeight);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void bind(int atlasUnit, int dataUnit) {
		_atlas.bind(atlasUnit);
		GLStates.bindTexture(GL_TEXTURE_BUFFER, dataUnit, _bufferTexture);
	}

	public void buildAtlas() {
		_atlas.buildAtlas();
		_atlasBuilt = true;

		int nbTilesTotal = 0;

		for (Texture texture : _textures.values()) {
			nbTilesTotal += texture.nbTilesX * texture.nbTilesY;
		}

		int tileId = 0;
		FloatBuffer buffer = BufferUtils.createFloatBuffer(4 * nbTilesTotal);
		float atlasWidth = _atlas.getWidth();
		float atlasHeight = _atlas.getHeight();

		for (int i = 0; i < _identifiers.size(); ++i) {
			String identifier = _identifiers.get(i);
			Texture texture = _textures.get(identifier);
			TextureAtlas.Node region = _atlas.getRegion(i);
			int nbTiles = texture.nbTilesX * texture.nbTilesY;

			texture.tileIdOffset = tileId;
			tileId += nbTiles;

			for (int tile = 0; tile < nbTiles; ++tile) {
				float tileWidth = (float) texture.tileWidth / atlasWidth;
				float tileHeight = (float) texture.tileHeight / atlasHeight;
				float tileX = (float) (region.x + (tile % texture.nbTilesX) * texture.tileWidth) / atlasWidth;
				float tileY = (float) (region.y + Math.floorDiv(tile, texture.nbTilesX) * texture.tileHeight) / atlasHeight;

				buffer.put(tileX).put(tileY).put(tileWidth).put(tileHeight);
			}
		}

		buffer.flip();

		_buffer = glGenBuffers();
		_bufferTexture = glGenTextures();

		GLStates.bindBufferTexture(_buffer);
		GLStates.bindTexture(GL_TEXTURE_BUFFER, 0, _bufferTexture);

		glBufferData(GL_TEXTURE_BUFFER, buffer, GL_STATIC_DRAW);
		glTexBuffer(GL_TEXTURE_BUFFER, GL_RGBA32F, _buffer);

		GLStates.bindTexture(GL_TEXTURE_BUFFER, 0, 0);
		GLStates.bindBufferTexture(0);
	}

	public int getTexture(String identifier) {
		return getTexture(identifier, 0);
	}

	public int getTexture(String identifier, int tileId) {
		if (!_atlasBuilt) {
			throw new RuntimeException("Trying to access texture before atlas building");
		}

		Texture texture = _textures.get(identifier);

		if (texture == null) {
			throw new RuntimeException("Requested texture does not exist");
		}

		if (tileId < 0 || tileId >= texture.nbTilesX * texture.nbTilesY) {
			throw new RuntimeException("Invalid tile id");
		}

		return texture.tileIdOffset + tileId;
	}
}

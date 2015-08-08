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

	public void registerTexture(String filename, String identifier, int tileWidth, int tileHeight) {
		try {
			BufferedImage img = ImageIO.read(TextureRegistry.class.getResourceAsStream(filename));
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void bind() {
		_atlas.bind();
		glActiveTexture(GL_TEXTURE1);
		GLStates.bindTextureBuffer(_bufferTexture);
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
				float tileWidth = texture.tileWidth / atlasWidth;
				float tileHeight = texture.tileHeight / atlasHeight;
				float tileX = (region.x + (tile % texture.nbTilesX) * texture.tileWidth) / atlasWidth;
				float tileY = (region.y + Math.floorDiv(tile, texture.nbTilesY) * texture.tileHeight) / atlasHeight;

				buffer.put(tileX).put(tileY).put(tileWidth).put(tileHeight);
			}

			buffer.flip();
		}

		_buffer = glGenBuffers();
		GLStates.bindBufferTexture(_buffer);
		_bufferTexture = glGenTextures();
		GLStates.bindTextureBuffer(_bufferTexture);

		glBufferData(GL_TEXTURE_BUFFER, _buffer, GL_STATIC_DRAW);
		glTexBuffer(GL_TEXTURE_BUFFER, GL_RGBA32F, _buffer);

		GLStates.bindTextureBuffer(0);
		GLStates.bindBufferTexture(0);
	}

	public int getTexture(String identifier, int tileId) {
		if (!_atlasBuilt) {
			throw new RuntimeException("Trying to access texture before atlas building");
		}

		return _textures.get(identifier).tileIdOffset + tileId;
	}
}

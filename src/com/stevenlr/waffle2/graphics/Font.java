package com.stevenlr.waffle2.graphics;

import java.awt.Color;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.stevenlr.waffle2.Waffle2;

public class Font {

	public enum Alignment {
		LEFT,
		MIDDLE,
		RIGHT
	}

	private java.awt.Font _font;

	int _maxWidth;
	int _maxHeight;
	int _ascent;
	int[] _widths;
	int[] _textureIds;
	String _identifier;

	public Font(String identifier, String filename, float size, boolean bold, boolean italic) {
		_identifier = identifier;

		try {
			int style = 0;

			if (bold) {
				style = style | java.awt.Font.BOLD;
			}

			if (italic) {
				style = style | java.awt.Font.ITALIC;
			}

			_font = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, Font.class.getResourceAsStream(filename));
			_font = _font.deriveFont(style, size);

			BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			FontMetrics metrics = image.getGraphics().getFontMetrics(_font);

			int[] widths = metrics.getWidths();
			int maxAscent = metrics.getMaxAscent();
			int maxDescent = metrics.getMaxDescent();
			int maxWidth = 0;

			_widths = new int[96];

			for (int i = 32; i < 128; ++i) {
				maxWidth = Math.max(maxWidth, widths[i]);
				_widths[i - 32] = widths[i];
			}

			int tileHeight = maxAscent + maxDescent;
			int tileWidth = maxWidth;
			Graphics2D g;

			_maxWidth = tileWidth;
			_maxHeight = tileHeight;
			_ascent = maxAscent;

			image = new BufferedImage(tileWidth * 12, tileHeight * 8, BufferedImage.TYPE_INT_ARGB);
			g = (Graphics2D) image.getGraphics();

			g.setFont(_font);
			g.setColor(Color.WHITE);
			g.setBackground(new Color(0, 0, 0, 0));
			g.clearRect(0, 0, image.getWidth(), image.getHeight());

			for (int y = 0; y < 8; ++y) {
				for (int x = 0; x < 12; ++x) {
					int charId = y * 12 + x;
					String charChar = Character.toString((char) (charId + 32));

					g.drawString(charChar, x * tileWidth, y * tileHeight + maxAscent);
				}
			}

			Waffle2.getInstance().getTextureRegistry().registerTexture(image, "waffle2:font:" + _identifier, tileWidth, tileHeight);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void build() {
		TextureRegistry textureRegistry = Waffle2.getInstance().getTextureRegistry();
		String identifier = "waffle2:font:" + _identifier;

		_textureIds = new int[96];

		for (int i = 0; i < 96; ++i) {
			_textureIds[i] = textureRegistry.getTexture(identifier, i);
		}
	}

	public void drawText(Renderer rd, String text, float x, float y, float size, float r, float g, float b, float a, Alignment align) {
		int length = text.length();
		char[] chars = text.toCharArray();
		float width = 0;

		for (int i = 0; i < length; ++i) {
			if (chars[i] >= 128 || chars[i] < 32) {
				chars[i] = 127;
			}

			chars[i] -= 32;
			width += (float) _widths[chars[i]] / _maxWidth;
		}

		width *= size;

		switch (align) {
		case MIDDLE:
			x -= width / 2;
			break;
		case RIGHT:
			x -= width;
			break;
		}

		float sizeY = (float) _maxHeight / _ascent * size;
		float sizeX = (float) _maxWidth / _maxHeight * size;

		y -= (float) (_maxHeight - _ascent) / _ascent * size;

		for (int i = 0; i < length; ++i) {
			rd.drawTile(x, y, sizeX, sizeY, r, g, b, a, _textureIds[chars[i]]);
			x += (float) _widths[chars[i]] / _maxWidth * size;
		}
	}
}

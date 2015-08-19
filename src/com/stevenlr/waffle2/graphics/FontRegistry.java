package com.stevenlr.waffle2.graphics;

import java.util.HashMap;
import java.util.Map;

public class FontRegistry {

	private boolean _hasBuiltFont = false;
	private Map<String, Font> _fonts = new HashMap<String, Font>();

	public void registerFont(String identifier, String filename, float size) {
		registerFont(identifier, filename, size, false, false);
	}

	public void registerFont(String identifier, String filename, float size, boolean bold, boolean italic) {
		if (_hasBuiltFont) {
			throw new RuntimeException("Cannot register font after pre-init");
		}

		Font font = new Font(identifier, filename, size, bold, italic);

		_fonts.put(identifier, font);
	}

	public void buildFonts() {
		for (Map.Entry<String, Font> entry : _fonts.entrySet()) {
			entry.getValue().build();
		}
	}

	public Font getFont(String identifier) {
		if (!_fonts.containsKey(identifier)) {
			throw new RuntimeException("Font " + identifier + " does not exist");
		}

		return _fonts.get(identifier);
	}
}

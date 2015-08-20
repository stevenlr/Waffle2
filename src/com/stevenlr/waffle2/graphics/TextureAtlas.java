package com.stevenlr.waffle2.graphics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.stevenlr.waffle2.graphics.opengl.GLStates;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

public class TextureAtlas {

	private static final int STARTING_SIZE = 4096;

	private List<BufferedImage> _images = new ArrayList<BufferedImage>();
	private List<Node> _regions = new ArrayList<Node>();
	private Node _root = new Node(0, 0, STARTING_SIZE, STARTING_SIZE);
	private int _width = 0;
	private int _height = 0;
	private int _texture = 0;

	public class Node {

		public int width;
		public int height;
		public int x;
		public int y;

		public boolean occupied = false;
		public Node child0 = null;
		public Node child1 = null;

		private Node() {
			x = 0;
			y = 0;
			width = 0;
			height = 0;
		}

		private Node(int x, int y, int width, int height) {
			this.width = width;
			this.height = height;
			this.x = x;
			this.y = y;
		}

		private Node insert(int w, int h) {
			if (child0 != null && child1 != null) {
				Node node = child0.insert(w, h);

				if (node != null) {
					return node;
				}

				return child1.insert(w, h);
			}

			if (occupied) {
				return null;
			}

			if (width == w && height == h) {
				return this;
			}

			if (width < w || height < h) {
				return null;
			}

			int dw = width - w;
			int dh = height - h;

			if (dw > dh) {
				child0 = new Node(x, y, w, height);
				child1 = new Node(x + w, y, width - w, height);
			} else {
				child0 = new Node(x, y, width, h);
				child1 = new Node(x, y + h, width, height - h);
			}

			return child0.insert(w, h);
		}
	}

	public void registerTexture(BufferedImage img) {
		_images.add(img);
	}

	public void buildAtlas() {
		List<Integer> indices = new ArrayList<Integer>();

		for (int i = 0; i < _images.size(); ++i) {
			indices.add(i);
		}

		indices.sort(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				BufferedImage i1 = _images.get(o1);
				BufferedImage i2 = _images.get(o2);

				return i2.getWidth() * i2.getHeight() - i1.getWidth() * i1.getHeight();
			}
		});

		Node[] nodes = new Node[_images.size()];

		for (Integer index : indices) {
			Node node;
			BufferedImage img = _images.get(index);

			if ((node = _root.insert(img.getWidth(), img.getHeight())) == null) {
				throw new RuntimeException("Couldn't insert new texture in texture atlas");
			}

			node.occupied = true;
			nodes[index] = node;
		}

		for (Node node : nodes) {
			_regions.add(node);
		}

		for (Node node : _regions) {
			_width = Math.max(_width, node.x + node.width);
			_height = Math.max(_height, node.y + node.height);
		}

		_width = (int) Math.pow(2, Math.ceil(Math.log(_width) / Math.log(2)));
		_height = (int) Math.pow(2, Math.ceil(Math.log(_height) / Math.log(2)));

		BufferedImage atlas = new BufferedImage(_width, _height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = atlas.getGraphics();

		for (int i = 0; i < _images.size(); ++i) {
			g.drawImage(_images.get(i), _regions.get(i).x, _regions.get(i).y, null);
		}

		g.dispose();
		_images.clear();

		int[] data = ((DataBufferInt) atlas.getRaster().getDataBuffer()).getData();
		ByteBuffer buffer = BufferUtils.createByteBuffer(_width * _height * 4);
		IntBuffer bufferInt = buffer.asIntBuffer();

		bufferInt.put(data, 0, _width * _height);

		_texture = glGenTextures();
		GLStates.bindTexture(GL_TEXTURE_2D, 0, _texture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, _width, _height, 0, GL_BGRA, GL_UNSIGNED_BYTE, buffer);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		GLStates.bindTexture(GL_TEXTURE_2D, 0, 0);
	}

	public Node getRegion(int i) {
		return _regions.get(i);
	}

	public void bind(int unit) {
		GLStates.bindTexture(GL_TEXTURE_2D, unit, _texture);
	}

	public int getWidth() {
		return _width;
	}

	public int getHeight() {
		return _height;
	}
}

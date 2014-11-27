package elements;

import java.awt.image.BufferedImage;

import assets.Assets;

public class Grass extends MapElement implements DrawableElement {

	public Grass(int x, int y) {
		super(x, y);
	}

	@Override
	public BufferedImage getImg() {
		return Assets.grass;
	}

	@Override
	public Grass copy() {
		return this;
	}
}

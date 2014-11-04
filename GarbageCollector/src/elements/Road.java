package elements;

import java.awt.image.BufferedImage;

import assets.Assets;

public class Road extends MapElement implements DrawableElement {
	boolean twoWay;

	public Road(boolean twoWay) {
		this.twoWay = twoWay;
	}

	@Override
	public BufferedImage getImg() {
		return Assets.asphalt;
	}
}

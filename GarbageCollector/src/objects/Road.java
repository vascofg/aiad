package objects;

import java.awt.image.BufferedImage;

public class Road extends Element {
	boolean twoWay;

	public Road(boolean twoWay) {
		this.twoWay = twoWay;
	}

	@Override
	public BufferedImage getImg() {
		return Element.asphalt;
	}
}

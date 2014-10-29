package elements;

import java.awt.image.BufferedImage;

public class PlasticContainer extends Container {

	public PlasticContainer(int capacity) {
		super(capacity);
	}
	
	@Override
	public BufferedImage getImg() {
		return Element.plasticContainer;
	}
}
package elements;

import java.awt.image.BufferedImage;

public class GarbageContainer extends Container  {

	public GarbageContainer(int capacity) {
		super(capacity);
	}
	
	@Override
	public BufferedImage getImg() {
		return Element.garbageContainer;
	}
}
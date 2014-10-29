package elements;

import java.awt.image.BufferedImage;

public class GlassContainer extends Container  {

	public GlassContainer(int capacity) {
		super(capacity);
	}
	
	@Override
	public BufferedImage getImg() {
		return Element.glassContainer;
	}
}
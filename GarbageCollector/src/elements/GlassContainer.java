package elements;

import java.awt.image.BufferedImage;

public class GlassContainer extends Container  {

	public GlassContainer(int capacity) {
		super(capacity);
	}
	
	@Override
	public BufferedImage getImg() {
		if(this.isEmpty())
			return Element.glassContainer;
		else
			return Element.glassContainerFull;
	}
}
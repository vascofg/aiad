package elements;

import java.awt.image.BufferedImage;

public class PlasticContainer extends Container {

	public PlasticContainer(int capacity) {
		super(capacity);
	}
	
	@Override
	public BufferedImage getImg() {
		if(this.isEmpty())
			return Element.plasticContainer;
		else
			return Element.plasticContainerFull;
	}
}
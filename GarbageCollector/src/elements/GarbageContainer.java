package elements;

import java.awt.image.BufferedImage;

public class GarbageContainer extends Container  {

	public GarbageContainer(int capacity) {
		super(capacity);
	}
	
	@Override
	public BufferedImage getImg() {
		if(this.isEmpty())
			return Element.garbageContainer;
		else
			return Element.garbageContainerFull;
	}
}
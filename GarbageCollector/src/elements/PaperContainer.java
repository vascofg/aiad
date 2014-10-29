package elements;

import java.awt.image.BufferedImage;

public class PaperContainer extends Container  {

	public PaperContainer(int capacity) {
		super(capacity);
	}
	
	@Override
	public BufferedImage getImg() {
		if(this.isEmpty())
			return Element.paperContainer;
		else
			return Element.paperContainerFull;
	}
}
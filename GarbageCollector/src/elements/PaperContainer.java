package elements;

import java.awt.image.BufferedImage;

public class PaperContainer extends Container  {

	public PaperContainer(int capacity) {
		super(capacity);
	}
	
	@Override
	public BufferedImage getImg() {
		return Element.paperContainer;
	}
}
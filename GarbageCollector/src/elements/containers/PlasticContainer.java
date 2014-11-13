package elements.containers;

import java.awt.image.BufferedImage;

import assets.Assets;
import elements.DrawableElement;

public class PlasticContainer extends Container implements DrawableElement {

	public PlasticContainer(int x, int y, int capacity) {
		super(x, y, capacity);
	}
	
	@Override
	public BufferedImage getImg() {
		if(this.isEmpty())
			return Assets.plasticContainer;
		else
			return Assets.plasticContainerFull;
	}
	
	@Override
	public int getType() {
		return Assets.PLASTIC;
	}
}
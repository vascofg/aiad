package elements;

import java.awt.image.BufferedImage;

import assets.Assets;

public class PaperContainer extends Container implements DrawableElement  {

	public PaperContainer(int capacity) {
		super(capacity);
	}
	
	@Override
	public BufferedImage getImg() {
		if(this.isEmpty())
			return Assets.paperContainer;
		else
			return Assets.paperContainerFull;
	}

	@Override
	public boolean truckCompatible(Truck truck) {
		return truck instanceof PaperTruck;
	}
}
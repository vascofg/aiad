package elements;

import java.awt.image.BufferedImage;

import assets.Assets;

public class PlasticContainer extends Container implements DrawableElement {

	public PlasticContainer(int capacity) {
		super(capacity);
	}
	
	@Override
	public BufferedImage getImg() {
		if(this.isEmpty())
			return Assets.plasticContainer;
		else
			return Assets.plasticContainerFull;
	}

	@Override
	public boolean truckCompatible(Truck truck) {
		return truck instanceof PlasticTruck;
	}
}
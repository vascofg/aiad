package elements;

import java.awt.image.BufferedImage;

import assets.Assets;

public class GarbageContainer extends Container implements DrawableElement {

	public GarbageContainer(int capacity) {
		super(capacity);
	}
	
	@Override
	public BufferedImage getImg() {
		if(this.isEmpty())
			return Assets.garbageContainer;
		else
			return Assets.garbageContainerFull;
	}

	@Override
	public boolean truckCompatible(Truck truck) {
		return truck instanceof GarbageTruck;
	}
}
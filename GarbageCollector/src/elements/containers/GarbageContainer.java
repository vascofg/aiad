package elements.containers;

import java.awt.image.BufferedImage;

import assets.Assets;
import elements.DrawableElement;
import elements.trucks.GarbageTruck;
import elements.trucks.Truck;

public class GarbageContainer extends Container implements DrawableElement {

	public GarbageContainer(int x, int y, int capacity) {
		super(x, y, capacity);
	}

	@Override
	public BufferedImage getImg() {
		if (this.isEmpty())
			return Assets.garbageContainer;
		else
			return Assets.garbageContainerFull;
	}

	@Override
	public boolean truckCompatible(Truck truck) {
		return truck instanceof GarbageTruck;
	}
}
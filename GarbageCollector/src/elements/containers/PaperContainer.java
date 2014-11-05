package elements.containers;

import java.awt.image.BufferedImage;

import assets.Assets;
import elements.DrawableElement;
import elements.trucks.PaperTruck;
import elements.trucks.Truck;

public class PaperContainer extends Container implements DrawableElement  {

	public PaperContainer(int x, int y, int capacity) {
		super(x, y, capacity);
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
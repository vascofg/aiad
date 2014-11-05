package elements.containers;

import java.awt.image.BufferedImage;

import assets.Assets;
import elements.DrawableElement;
import elements.trucks.GlassTruck;
import elements.trucks.Truck;

public class GlassContainer extends Container implements DrawableElement {

	public GlassContainer(int x, int y, int capacity) {
		super(x, y, capacity);
	}
	
	@Override
	public BufferedImage getImg() {
		if(this.isEmpty())
			return Assets.glassContainer;
		else
			return Assets.glassContainerFull;
	}

	@Override
	public boolean truckCompatible(Truck truck) {
		return truck instanceof GlassTruck;
	}
}
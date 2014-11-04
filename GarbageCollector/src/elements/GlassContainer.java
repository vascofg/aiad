package elements;

import java.awt.image.BufferedImage;

import assets.Assets;

public class GlassContainer extends Container implements DrawableElement {

	public GlassContainer(int capacity) {
		super(capacity);
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
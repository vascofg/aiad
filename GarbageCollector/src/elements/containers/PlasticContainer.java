package elements.containers;

import java.awt.image.BufferedImage;

import assets.Assets;
import elements.DrawableElement;

public class PlasticContainer extends Container implements DrawableElement {

	public PlasticContainer(int x, int y, int capacity) {
		super(x, y, capacity);
	}

	public PlasticContainer(PlasticContainer other) {
		super(other.x, other.y, null);
	}

	@Override
	public BufferedImage getImg() {
		if (this.isEmpty())
			return Assets.plasticContainer;
		else
			return Assets.plasticContainerFull;
	}

	@Override
	public int getType() {
		return Assets.PLASTIC;
	}

	@Override
	public PlasticContainer copy() {
		return new PlasticContainer(this);
	}
}
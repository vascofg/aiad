package elements.containers;

import java.awt.image.BufferedImage;

import assets.Assets;
import elements.DrawableElement;

public class GarbageContainer extends Container implements DrawableElement {

	public GarbageContainer(int x, int y, int capacity) {
		super(x, y, capacity);
	}

	public GarbageContainer(GarbageContainer other) {
		super(other.x, other.y, null);
	}

	@Override
	public BufferedImage getImg() {
		if (this.isEmpty())
			return Assets.garbageContainer;
		else
			return Assets.garbageContainerFull;
	}

	@Override
	public int getType() {
		return Assets.GARBAGE;
	}

	@Override
	public GarbageContainer copy() {
		return new GarbageContainer(this);
	}
}
package elements.containers;

import java.awt.image.BufferedImage;

import assets.Assets;
import elements.DrawableElement;

public class PaperContainer extends Container implements DrawableElement {

	public PaperContainer(int x, int y, int capacity) {
		super(x, y, capacity);
	}

	public PaperContainer(PaperContainer other) {
		super(other.x, other.y, null);
	}

	@Override
	public BufferedImage getImg() {
		if (this.isEmpty())
			return Assets.paperContainer;
		else
			return Assets.paperContainerFull;
	}

	@Override
	public int getType() {
		return Assets.PAPER;
	}

	@Override
	public PaperContainer copy() {
		return new PaperContainer(this);
	}
}
package elements;

import java.awt.image.BufferedImage;

import assets.Assets;

public class Deposit extends MapElement implements DrawableElement {
	public Deposit(int x, int y) {
		super(x, y);
	}

	@Override
	public BufferedImage getImg() {
		return Assets.deposit;
	}

	@Override
	public Deposit copy() {
		return this;
	}
}

package elements.trucks;

import java.awt.image.BufferedImage;

import assets.Assets;
import elements.DrawableElement;
import elements.Road;

public class GarbageTruck extends Truck implements DrawableElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GarbageTruck(Road initialLocation, int capacity) {
		super(initialLocation, capacity);
		// TODO Auto-generated constructor stub
	}

	@Override
	public BufferedImage getImg() {
		return Assets.garbageTruck;
	}

}

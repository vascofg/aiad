package elements.trucks;

import java.awt.image.BufferedImage;

import assets.Assets;
import elements.DrawableElement;
import elements.Road;

public class PaperTruck extends Truck implements DrawableElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PaperTruck(Road initialLocation) {
		super(initialLocation);
		// TODO Auto-generated constructor stub
	}

	@Override
	public BufferedImage getImg() {
		return Assets.paperTruck;
	}

}

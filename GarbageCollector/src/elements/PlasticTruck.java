package elements;

import java.awt.image.BufferedImage;

import assets.Assets;

public class PlasticTruck extends Truck implements DrawableElement {

	@Override
	public BufferedImage getImg() {
		return Assets.plasticTruck;
	}

}

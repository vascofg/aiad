package elements;

import java.awt.image.BufferedImage;

import assets.Assets;

public class GlassTruck extends Truck implements DrawableElement {

	@Override
	public BufferedImage getImg() {
		return Assets.glassTruck;
	}

}

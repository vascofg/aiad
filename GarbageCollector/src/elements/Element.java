package elements;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

//generic map element class
public abstract class Element {
	public static BufferedImage grass, asphalt,
	glassContainer, glassContainerFull,
	paperContainer, paperContainerFull,
	plasticContainer, plasticContainerFull,
	garbageContainer, garbageContainerFull;
	public static Dimension imgDim;

	public BufferedImage getImg() {
		return null;
	}
}

package elements;

import java.awt.image.BufferedImage;

import algorithms.Dijkstra;
import assets.Assets;

public class Road extends MapElement implements DrawableElement {
	boolean twoWay;
	public Dijkstra dijkstra;

	public Road(int x, int y, boolean twoWay) {
		super(x,y);
		this.twoWay = twoWay;
	}
	
	public String getID() {
		return (this.getX() + "|" + this.getY());
	}

	@Override
	public BufferedImage getImg() {
		return Assets.asphalt;
	}
}

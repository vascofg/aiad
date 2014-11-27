package elements;

import java.awt.image.BufferedImage;

import algorithms.Dijkstra;
import assets.Assets;

public class Road extends MapElement implements DrawableElement {
	boolean twoWay;
	String id;
	public Dijkstra dijkstra;

	public Road(int x, int y, boolean twoWay) {
		this.twoWay = twoWay;
		this.id = x + "|" + y;
	}

	public String getID() {
		return id;
	}

	@Override
	public BufferedImage getImg() {
		return Assets.asphalt;
	}

	@Override
	public Road copy() {
		return this;
	}
}

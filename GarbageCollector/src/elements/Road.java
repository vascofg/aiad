package elements;

import java.awt.image.BufferedImage;

import algorithms.Dijkstra;
import assets.Assets;
import elements.trucks.Truck;

public class Road extends MapElement implements DrawableElement {
	private boolean twoWay;
	private String id;
	private Truck truck;
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
		if (truck == null)
			return Assets.asphalt;
		else
			return truck.getImg();
	}

	@Override
	public Road copy() {
		return this;
	}

	public Truck getTruck() {
		return truck;
	}

	public void setTruck(Truck truck) {
		this.truck = truck;
	}

	public void removeTruck() {
		this.truck = null;
	}
}

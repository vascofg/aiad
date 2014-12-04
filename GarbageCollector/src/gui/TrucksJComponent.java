package gui;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import map.Map;
import assets.Assets;
import elements.Road;
import elements.trucks.Truck;

public class TrucksJComponent extends JComponent {

	/**
	 * 
	 */
	private Map map;
	private static final long serialVersionUID = 1L;

	public TrucksJComponent(Map map) {
		this.map = map;
	}

	@Override
	protected void paintComponent(Graphics g) {
		for (Truck truck : map.trucks) {
			BufferedImage img = truck.getImg();
			if (img != null) {
				g.drawImage(img, truck.getLocation().x * Assets.imgDim.width,
						truck.getLocation().y * Assets.imgDim.height,
						Assets.imgDim.width, Assets.imgDim.height, null);
			}
		}
	}

	public void repaintTruck(Point from, Truck truck) { //TODO: Should we use this?
		Graphics g = this.getGraphics();
		g.drawImage(Assets.asphalt, from.x * Assets.imgDim.width, from.y
				* Assets.imgDim.height, Assets.imgDim.width,
				Assets.imgDim.height, null);
		g.drawImage(truck.getImg(),
				truck.getLocation().x * Assets.imgDim.width,
				truck.getLocation().y * Assets.imgDim.height,
				Assets.imgDim.width, Assets.imgDim.height, null);
	}
}

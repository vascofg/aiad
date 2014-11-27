package gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import map.Map;
import assets.Assets;
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
						truck.getLocation().y * Assets.imgDim.height, null);
			}
		}
		super.paintComponent(g);
	}
}

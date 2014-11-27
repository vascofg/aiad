package gui;

import jade.core.NotFoundException;

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
				try {
					int[] indexes = Map.getElementIndexes(truck.getLocation(),
							map.mapMatrix);
					int x = indexes[0], y = indexes[1];
					g.drawImage(img, x * Assets.imgDim.width, y
							* Assets.imgDim.height, null);
				} catch (NotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		super.paintComponent(g);
	}
}

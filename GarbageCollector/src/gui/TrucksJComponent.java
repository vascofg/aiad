package gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import main.GarbageCollector;
import assets.Assets;
import elements.trucks.Truck;

public class TrucksJComponent extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void paintComponent(Graphics g) {
		for (Truck truck : GarbageCollector.map.trucks) {
			BufferedImage img = truck.getImg();
			if (img != null) {
				g.drawImage(img, truck.getLocation().getX()
						* Assets.imgDim.width, truck.getLocation().getY()
						* Assets.imgDim.height, null);
			}
		}
		super.paintComponent(g);
	}
}

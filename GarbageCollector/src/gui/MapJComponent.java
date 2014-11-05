package gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JComponent;

import main.GarbageCollector;
import assets.Assets;
import elements.MapElement;

public class MapJComponent extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void paintComponent(Graphics g) {
		for (ArrayList<MapElement> line : GarbageCollector.map.mapMatrix) {
			for (MapElement element : line) {
				BufferedImage img = element.getImg();
				if (img != null)
					g.drawImage(img, element.getX() * Assets.imgDim.width,
							element.getY() * Assets.imgDim.height, null);
			}
		}
		super.paintComponent(g);
	}
}

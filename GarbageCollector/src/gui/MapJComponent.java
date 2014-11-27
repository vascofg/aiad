package gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JComponent;

import map.Map;
import assets.Assets;
import elements.MapElement;

public class MapJComponent extends JComponent {

	/**
	 * 
	 */
	private Map map;
	private static final long serialVersionUID = 1L;

	public MapJComponent(Map map) {
		this.map = map;
	}

	@Override
	protected void paintComponent(Graphics g) {
		for (int y = 0; y < map.mapMatrix.size(); y++) {
			ArrayList<MapElement> line = map.mapMatrix.get(y);
			for (int x = 0; x < line.size(); x++) {
				MapElement element = line.get(x);
				BufferedImage img = element.getImg();
				if (img != null)
					g.drawImage(img, x * Assets.imgDim.width, y
							* Assets.imgDim.height, null);
			}
		}
		super.paintComponent(g);
	}
}

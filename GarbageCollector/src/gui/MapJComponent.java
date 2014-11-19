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
		for (ArrayList<MapElement> line : map.mapMatrix) {
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

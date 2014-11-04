package gui;

import java.awt.Dimension;
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

	private static Dimension mapDim;

	public MapJComponent() {
		mapDim = new Dimension(Assets.imgDim.width
				* GarbageCollector.mapMatrix.get(0).size(),
				Assets.imgDim.height * GarbageCollector.mapMatrix.size());
		this.setPreferredSize(mapDim);
	}

	@Override
	protected void paintComponent(Graphics g) {
		int x = 0, y = 0;
		for (ArrayList<MapElement> line : GarbageCollector.mapMatrix) {
			for (MapElement element : line) {
				BufferedImage img = element.getImg();
				if (img != null)
					g.drawImage(element.getImg(), x * Assets.imgDim.width, y
							* Assets.imgDim.height, null);
				x++;
			}
			x = 0;
			y++;
		}
		super.paintComponent(g);
	}
}

package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JComponent;

import main.GarbageCollector;
import objects.Element;

public class MapJComponent extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Dimension mapDim;

	public MapJComponent() {
		mapDim = new Dimension(Element.imgDim.width
				* GarbageCollector.mapMatrix.get(0).size(),
				Element.imgDim.height * GarbageCollector.mapMatrix.size());
		this.setPreferredSize(mapDim);
	}

	@Override
	protected void paintComponent(Graphics g) {
		int x = 0, y = 0;
		for (ArrayList<Element> line : GarbageCollector.mapMatrix) {
			for (Element element : line) {
				BufferedImage img = element.getImg();
				if (img != null)
					g.drawImage(element.getImg(), x * Element.imgDim.width, y
							* Element.imgDim.height, null);
				x++;
			}
			x = 0;
			y++;
		}
		super.paintComponent(g);
	}
}

package gui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;

import map.Map;
import assets.Assets;

public class MapJFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Dimension mapDim;
	public MapJComponent mapComponent;
	public TrucksJComponent trucksComponent;

	public MapJFrame(String title, Map map) {
		super(title);

		mapDim = new Dimension(Assets.imgDim.width
				* map.mapMatrix.get(0).size(), Assets.imgDim.height
				* map.mapMatrix.size());

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setPreferredSize(mapDim);
		this.add(layeredPane);

		mapComponent = new MapJComponent(map);
		mapComponent.setSize(mapDim);
		layeredPane.add(mapComponent, 1, 0);

		trucksComponent = new TrucksJComponent(map);
		trucksComponent.setSize(mapDim);
		layeredPane.add(trucksComponent, 2, 0);

		this.setResizable(false);
		// Display the window.
		this.pack();
		this.setVisible(true);
	}
}

package gui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;

import main.GarbageCollector;
import assets.Assets;

public class MapJFrame extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Dimension mapDim;
	public MapJComponent mapComponent;
	public TrucksJComponent trucksComponent;
	
	public MapJFrame(String title) {
		super(title);
		
		mapDim = new Dimension(Assets.imgDim.width
				* GarbageCollector.map.mapMatrix.get(0).size(),
				Assets.imgDim.height * GarbageCollector.map.mapMatrix.size());
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setPreferredSize(mapDim);
		this.add(layeredPane);
		
		mapComponent = new MapJComponent();
		mapComponent.setSize(mapDim);
		layeredPane.add(mapComponent, 1, 0);
		
		trucksComponent = new TrucksJComponent();
		trucksComponent.setSize(mapDim);
		layeredPane.add(trucksComponent, 2, 0);

		this.setResizable(false);
		// Display the window.
		this.pack();
		this.setVisible(true);
	}
}

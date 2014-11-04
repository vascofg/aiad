package main;

import java.util.ArrayList;

import javax.swing.JFrame;

import assets.Assets;
import elements.Container;
import elements.MapElement;
import exceptions.ContainerFullException;
import files.FileParser;
import gui.MapJComponent;

public class GarbageCollector {

	public static ArrayList<ArrayList<MapElement>> mapMatrix = new ArrayList<ArrayList<MapElement>>();
	private static JFrame frame;
	private static MapJComponent mapComponent;
	
	private static void createAndShowGUI() {
		// Create and set up the window.
		frame = new JFrame("GarbageCollector");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		mapComponent = new MapJComponent();
		frame.getContentPane().add(mapComponent);

		frame.setResizable(false);
		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		Assets.loadAssets();
		FileParser.parseFile("maps/simple.txt");
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
		try {
			Thread.sleep(2000);
			((Container) mapMatrix.get(6).get(0)).addToContainer(20);
			mapComponent.repaint();
		} catch (InterruptedException | ContainerFullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

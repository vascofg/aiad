package main;

import files.FileParser;
import gui.MapJFrame;
import map.Map;
import assets.Assets;

public class GarbageCollector {

	public static Map map;
	public static MapJFrame frame;
	private static TruckThread truckThread;

	public static void main(String[] args) {
		map = new Map();
		Assets.loadAssets();
		FileParser.parseFile("maps/simple.txt");
		map.initTrucks();

		truckThread = new TruckThread();
		
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new MapJFrame("Garbage Collector");
				truckThread.start();
			}
		});
	}

}

package main;

import files.FileParser;
import gui.MapJFrame;
import jade.core.ProfileImpl;
import jade.wrapper.ContainerController;
import map.Map;
import algorithms.Dijkstra;
import algorithms.Graph;
import assets.Assets;

public class GarbageCollector {

	public static Map map;
	public static MapJFrame frame;
	private static TruckThread truckThread;
	private static ContainerThread containerThread;
	public static ContainerController containerController;

	public static void main(String[] args) {
		containerController = jade.core.Runtime.instance().createMainContainer(
				new ProfileImpl(true));
		map = new Map();
		Assets.loadAssets();
		FileParser.parseFile("maps/simple.txt");
		Graph graph = new Graph(map.roads);
		map.initRoads(graph);
		map.initTrucks();

		truckThread = new TruckThread();
		containerThread = new ContainerThread();

		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new MapJFrame("Garbage Collector");
				truckThread.start();
				containerThread.start();
			}
		});
	}

}

package main;

import files.FileParser;
import gui.MapJFrame;
import jade.core.ProfileImpl;
import jade.wrapper.ContainerController;

import java.util.Random;

import map.Map;
import assets.Assets;

public class GarbageCollector {

	private static Map map;
	private static MapJFrame frame;
	private static TruckThread truckThread;
	private static ContainerThread containerThread;
	private static ContainerController containerController;
	public static Random randGenerator = new Random();

	/* private agente mundo */

	public static void main(String[] args) {
		containerController = jade.core.Runtime.instance().createMainContainer(
				new ProfileImpl(true));
		Assets.loadAssets();
		map = FileParser.parseFile("maps/simple.txt");
		// map.initRoads(graph);
		map.initTrucks(containerController);

		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new MapJFrame("Garbage Collector", map);
				truckThread = new TruckThread(map.trucks,
						frame.trucksComponent, frame.mapComponent);
				truckThread.start();
				containerThread = new ContainerThread(map.containers,
						frame.mapComponent);
				containerThread.start();
			}
		});
	}

}

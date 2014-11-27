package main;

import files.FileParser;
import gui.MapJFrame;
import jade.core.ProfileImpl;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

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

	private static ContainerController startJADE() {
		ContainerController c = jade.core.Runtime.instance()
				.createMainContainer(new ProfileImpl(true));
		try {
			c.createNewAgent("rma", "jade.tools.rma.rma", new Object[0])
					.start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
	}

	public static void main(String[] args) {
		System.out.println("Starting JADE...");
		containerController = startJADE();
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

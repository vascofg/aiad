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

	public static MapJFrame frame;
	private static ContainerThread containerThread;
	private static ContainerController containerController;
	public static final Random randGenerator = new Random();

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
		FileParser.parseFile("maps/simple.txt", containerController);
		// map.initRoads(graph);

		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new MapJFrame("Garbage Collector");
				containerThread = new ContainerThread();
				containerThread.start();
				Map.INSTANCE.initTrucks(containerController);
			}
		});
	}

}

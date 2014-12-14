package main;

import files.FileParser;
import gui.JGraphFrame;
import gui.MapJFrame;
import jade.core.ProfileImpl;
import jade.wrapper.ContainerController;

import java.util.Random;

import map.Map;
import assets.Assets;

public class GarbageCollector {

	public static MapJFrame frame;
	private static ContainerThread containerThread;
	private static ContainerController containerController;
	private static JGraphFrame jgraphFrame;
	public static final Random randGenerator = new Random();
	// flag (add informed container permanently to the trucks route)
	public static final boolean addToRoute = true;

	private static ContainerController startJADE() {
		ContainerController c = jade.core.Runtime.instance()
				.createMainContainer(new ProfileImpl(true));
		/*
		 * try { c.createNewAgent("rma", "jade.tools.rma.rma", new Object[0])
		 * .start(); } catch (StaleProxyException e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); }
		 */
		return c;
	}

	public static void main(String[] args) {
		System.out.println("Starting JADE...");
		containerController = startJADE();
		Assets.loadAssets();
		FileParser.parseMapFile("maps/big.txt", containerController);

		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame = new MapJFrame("Garbage Collector");
				Map.INSTANCE.initTrucks(containerController);
				frame.menu.addTrucks(Map.INSTANCE.trucks);
				frame.pack();
				// jgraphFrame = new JGraphFrame(Map.INSTANCE.graph);
				containerThread = new ContainerThread();
				containerThread.start();
				frame.mapComponent.repaint(); // repaint all after initing
												// trucks
			}
		});
	}

}

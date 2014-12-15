package main;

import files.FileParser;
import gui.JGraphFrame;
import gui.MapJFrame;
import jade.core.ProfileImpl;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.util.Random;

import javax.swing.JOptionPane;

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
	public static boolean local = true;
	public static String remotePlatformName, remoteMTP;

	private static ContainerController startJADE() {
		ContainerController c = jade.core.Runtime.instance()
				.createMainContainer(new ProfileImpl(true));

		try {
			c.createNewAgent("rma", "jade.tools.rma.rma", new Object[0])
					.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}

		return c;
	}

	public static void main(String[] args) {
		int reply = JOptionPane.showConfirmDialog(null,
				"Is this a remote instance?", "Remote?",
				JOptionPane.YES_NO_OPTION);
		if (reply == JOptionPane.YES_OPTION) {
			GarbageCollector.local = false;
			//GarbageCollector.remotePlatformName = JOptionPane.showInputDialog("Platform Name:");
			//GarbageCollector.remoteMTP = JOptionPane.showInputDialog("MTP:");
			int reply2 = JOptionPane.showConfirmDialog(null,
					"PORTATIL?", "Remote?",
					JOptionPane.YES_NO_OPTION);
			if (reply2 == JOptionPane.YES_OPTION) {
				GarbageCollector.remotePlatformName = "192.168.56.1:1099/JADE";
				GarbageCollector.remoteMTP = "http://192.168.108.47:7778/acc";
			}
			else {
				GarbageCollector.remotePlatformName = "192.168.108.201:1099/JADE";
				GarbageCollector.remoteMTP = "http://192.168.108.201:7778/acc";
			}
		}

		System.out.println("Starting JADE...");
		containerController = startJADE();
		Assets.loadAssets();
		FileParser.parseMapFile("maps/big.txt", containerController);

		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		if (local) {
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					frame = new MapJFrame("Garbage Collector");
					Map.INSTANCE.initTrucks(containerController);
					frame.pack();
					// jgraphFrame = new JGraphFrame(Map.INSTANCE.graph);
					containerThread = new ContainerThread();
					containerThread.start();
					frame.mapComponent.repaint(); // repaint all after initing
													// trucks
				}
			});
		}
		else
			Map.INSTANCE.initTrucks(containerController);
	}

}

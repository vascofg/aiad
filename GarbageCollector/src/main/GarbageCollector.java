package main;

import elements.Container;
import elements.Element;
import exceptions.ContainerFullException;
import files.FileParser;
import gui.MapJComponent;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class GarbageCollector {

	public static ArrayList<ArrayList<Element>> mapMatrix = new ArrayList<ArrayList<Element>>();
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
	
	private static void loadAssets() {
		try {
			Element.grass = ImageIO.read(new File("img/grass.png"));
			Element.asphalt = ImageIO.read(new File("img/asphalt.png"));
			Element.glassContainer = ImageIO.read(new File("img/glasscontainer.png"));
			Element.paperContainer = ImageIO.read(new File("img/papercontainer.png"));
			Element.plasticContainer = ImageIO.read(new File("img/plasticcontainer.png"));
			Element.garbageContainer = ImageIO.read(new File("img/garbagecontainer.png"));
			Element.glassContainerFull = ImageIO.read(new File("img/glasscontainerfull.png"));
			Element.paperContainerFull = ImageIO.read(new File("img/papercontainerfull.png"));
			Element.plasticContainerFull = ImageIO.read(new File("img/plasticcontainerfull.png"));
			Element.garbageContainerFull = ImageIO.read(new File("img/garbagecontainerfull.png"));
			Element.imgDim = new Dimension(Element.grass.getWidth(),
					Element.grass.getHeight());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		loadAssets();
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

package main;

import files.FileParser;
import gui.MapJComponent;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import objects.Element;

public class GarbageCollector {

	public static ArrayList<ArrayList<Element>> mapMatrix = new ArrayList<ArrayList<Element>>();

	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("GarbageCollector");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		MapJComponent component = new MapJComponent();
		frame.getContentPane().add(component);

		frame.setResizable(false);
		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}
	
	private static void loadAssets() {
		try {
			Element.grass = ImageIO.read(new File("img/grass.png"));
			Element.asphalt = ImageIO.read(new File("img/asphalt.png"));
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
	}

}

package files;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import main.GarbageCollector;
import objects.Container;
import objects.Element;
import objects.GarbageContainer;
import objects.GlassContainer;
import objects.PaperContainer;
import objects.PlasticContainer;
import objects.Road;
import objects.Grass;

public class FileParser {
	public static void parseFile(String name) {
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(name));
			String line;
			while ((line = in.readLine()) != null) {
				if (line.isEmpty() || line.charAt(0) == '#')
					continue;
				ArrayList<Element> lineList = new ArrayList<Element>();
				for (char c : line.toCharArray()) {
					switch (c) {
					case '-': // wall
						lineList.add(new Grass());
						break;
					case 'r': // one way road
						lineList.add(new Road(false));
						break;
					case 'R': // two way road
						lineList.add(new Road(true));
						break;
					case 'V': // glass container
						lineList.add(new GlassContainer(
								Container.defaultCapacity));
						break;
					case 'E': // plastic container
						lineList.add(new PlasticContainer(
								Container.defaultCapacity));
						break;
					case 'P': // paper container
						lineList.add(new PaperContainer(
								Container.defaultCapacity));
						break;
					case 'I': // garbage container
						lineList.add(new GarbageContainer(
								Container.defaultCapacity));
						break;
					default:
						System.out.println("Character>" + c);
						throw new IllegalArgumentException();
					}
				}
				GarbageCollector.mapMatrix.add(lineList);
			}
			in.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

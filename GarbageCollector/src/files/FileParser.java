package files;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import map.Map;
import algorithms.Graph;
import elements.Deposit;
import elements.Grass;
import elements.MapElement;
import elements.Road;
import elements.containers.Container;
import elements.containers.GarbageContainer;
import elements.containers.GlassContainer;
import elements.containers.PaperContainer;
import elements.containers.PlasticContainer;

public class FileParser {
	public static Map parseFile(String name) {
		System.out.println("Parsing map file...");
		Map map = new Map();
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(name));
			String line;
			Road road;
			Container container;
			Grass grass = new Grass();
			Deposit deposit = new Deposit();
			// they are all the same
			long count = 0;
			int x = 0, y = 0;
			while ((line = in.readLine()) != null) {
				if (line.isEmpty() || line.charAt(0) == '#')
					continue;
				ArrayList<MapElement> lineList = new ArrayList<MapElement>();
				for (char c : line.toCharArray()) {
					switch (c) {
					case '-': // wall
						lineList.add(grass);
						break;
					case 'r': // one way road
						road = new Road(x, y, false);
						if (map.initialLocation == null)
							map.initialLocation = new Point(x, y);
						lineList.add(road);
						map.roads.add(road);
						break;
					case 'R': // two way road
						road = new Road(x, y, true);
						if (map.initialLocation == null)
							map.initialLocation = new Point(x, y);
						lineList.add(road);
						map.roads.add(road);
						break;
					case 'V': // glass container
						container = new GlassContainer(
								Container.defaultCapacity);
						lineList.add(container);
						map.containers.add(container);
						break;
					case 'E': // plastic container
						container = new PlasticContainer(
								Container.defaultCapacity);
						lineList.add(container);
						map.containers.add(container);
						break;
					case 'P': // paper container
						container = new PaperContainer(
								Container.defaultCapacity);
						lineList.add(container);
						map.containers.add(container);
						break;
					case 'I': // garbage container
						container = new GarbageContainer(
								Container.defaultCapacity);
						lineList.add(container);
						map.containers.add(container);
						break;
					case 'D': // deposit
						lineList.add(deposit);
						break;
					default:
						System.out.println("Character>" + c);
						throw new IllegalArgumentException();
					}
					x++;
				}
				count += x;
				x = 0;
				y++;
				map.mapMatrix.add(lineList);
			}
			in.close();
			System.out.println("Parsed " + count + " elements");
			map.graph = new Graph(map);
			return map;
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}

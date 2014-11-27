package files;

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
		Map map = new Map();
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(name));
			String line;
			Road road;
			Container container;
			int x = 0, y = 0;
			while ((line = in.readLine()) != null) {
				if (line.isEmpty() || line.charAt(0) == '#')
					continue;
				ArrayList<MapElement> lineList = new ArrayList<MapElement>();
				for (char c : line.toCharArray()) {
					switch (c) {
					case '-': // wall
						lineList.add(new Grass(x, y));
						break;
					case 'r': // one way road
						road = new Road(x, y, false);
						if (map.initialRoad == null)
							map.initialRoad = road;
						lineList.add(road);
						map.roads.add(road);
						break;
					case 'R': // two way road
						road = new Road(x, y, true);
						if (map.initialRoad == null)
							map.initialRoad = road;
						lineList.add(road);
						map.roads.add(road);
						break;
					case 'V': // glass container
						container = new GlassContainer(x, y,
								Container.defaultCapacity);
						lineList.add(container);
						map.containers.add(container);
						break;
					case 'E': // plastic container
						container = new PlasticContainer(x, y,
								Container.defaultCapacity);
						lineList.add(container);
						map.containers.add(container);
						break;
					case 'P': // paper container
						container = new PaperContainer(x, y,
								Container.defaultCapacity);
						lineList.add(container);
						map.containers.add(container);
						break;
					case 'I': // garbage container
						container = new GarbageContainer(x, y,
								Container.defaultCapacity);
						lineList.add(container);
						map.containers.add(container);
						break;
					case 'D': // deposit
						lineList.add(new Deposit(x, y));
						break;
					default:
						System.out.println("Character>" + c);
						throw new IllegalArgumentException();
					}
					x++;
				}
				x = 0;
				y++;
				map.mapMatrix.add(lineList);
			}
			in.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		map.graph = new Graph(map);
		return map;
	}
}

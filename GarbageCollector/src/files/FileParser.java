package files;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import main.GarbageCollector;
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
	public static void parseFile(String name) {
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(name));
			String line;
			Road r;
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
						r = new Road(x, y, false);
						if (GarbageCollector.map.initialRoad == null)
							GarbageCollector.map.initialRoad = r;
						lineList.add(r);
						break;
					case 'R': // two way road
						r = new Road(x, y, true);
						if (GarbageCollector.map.initialRoad == null)
							GarbageCollector.map.initialRoad = r;
						lineList.add(r);
						break;
					case 'V': // glass container
						lineList.add(new GlassContainer(x, y,
								Container.defaultCapacity));
						break;
					case 'E': // plastic container
						lineList.add(new PlasticContainer(x, y,
								Container.defaultCapacity));
						break;
					case 'P': // paper container
						lineList.add(new PaperContainer(x, y,
								Container.defaultCapacity));
						break;
					case 'I': // garbage container
						lineList.add(new GarbageContainer(x, y,
								Container.defaultCapacity));
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
				x=0;
				y++;
				GarbageCollector.map.mapMatrix.add(lineList);
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

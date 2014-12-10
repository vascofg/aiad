package files;

import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import elements.trucks.GarbageTruck;
import elements.trucks.GlassTruck;
import elements.trucks.PaperTruck;
import elements.trucks.PlasticTruck;
import elements.trucks.Truck;

public class FileParser {
	public static void parseMapFile(String fileName,
			ContainerController containerController) {
		System.out.println("Parsing map file...");
		Map map = Map.INSTANCE;
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(fileName));
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
			map.worldAgent = containerController.createNewAgent("World",
					"agents.WorldAgent", new Object[0]);
			map.worldAgent.start();
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static List<Truck> parseTrucksFile(String fileName,
			ContainerController containerController,
			ArrayList<ArrayList<MapElement>> mapMatrix) {
		System.out.println("Parsing route file...");
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(fileName));
			ArrayList<Point> route = new ArrayList<Point>();
			ArrayList<Truck> trucks = new ArrayList<Truck>();
			Truck currentTruck = null;
			String line, truckName = null;
			long count = 0;
			int y = 0, capacity = 0;
			Point initialLocation = null;
			/* truck name,truck type,capacity,initialLocation(x,y),route */
			/*
			 * Quim V 20 1,1
			 */
			while ((line = in.readLine()) != null) {
				if (line.isEmpty() || line.charAt(0) == '#')
					continue;
				new ArrayList<MapElement>();
				for (int i = 0; i < line.length(); i++) {
					char c = line.charAt(i);
					if (c == 'n') {
						truckName = line.substring(i + 1, line.length());
						break;
					} else if (c == 'c') {
						capacity = Integer.parseInt(line.substring(i + 1,
								line.length()));
						break;
					} else if (c == 'i') {
						String[] splitted = line
								.substring(i + 1, line.length()).split(",");
						initialLocation = new Point(
								Integer.parseInt(splitted[0]),
								Integer.parseInt(splitted[1]));
					} else if (c == 't') {
						char type = line.charAt(i + 1);
						switch (type) {
						case 'V':
							currentTruck = new GlassTruck(initialLocation,
									capacity, containerController, truckName,
									mapMatrix);
							trucks.add(currentTruck);
							break;
						case 'P':
							currentTruck = new PaperTruck(initialLocation,
									capacity, containerController, truckName,
									mapMatrix);
							trucks.add(currentTruck);
							break;
						case 'E':
							currentTruck = new PlasticTruck(initialLocation,
									capacity, containerController, truckName,
									mapMatrix);
							trucks.add(currentTruck);
							break;
						case 'I':
							currentTruck = new GarbageTruck(initialLocation,
									capacity, containerController, truckName,
									mapMatrix);
							trucks.add(currentTruck);
							break;
						}
						break;
					} else if (c == 'T') {
						route.add(new Point(i, y));
						count++;
					} else if (c == '/') {
						currentTruck.setRoute(route);
						route = new ArrayList<Point>();
						y = -1;
						break;
					}
				}
				y++;
			}
			in.close();
			System.out.println("Parsed " + count + " route elements");
			return trucks;
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}

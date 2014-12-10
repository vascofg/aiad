package map;

import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import algorithms.Dijkstra;
import algorithms.Graph;
import assets.Assets;
import elements.MapElement;
import elements.Road;
import elements.containers.Container;
import elements.trucks.Truck;
import files.FileParser;

public class Map {
	public ArrayList<ArrayList<MapElement>> mapMatrix;
	public ArrayList<Truck> trucks;
	public ArrayList<Container> containers;
	public ArrayList<Road> roads;
	public Point initialLocation;
	public Graph graph;
	public AgentController worldAgent;

	public static final Map INSTANCE = new Map();

	private Map() {
		this.mapMatrix = new ArrayList<ArrayList<MapElement>>();
		this.trucks = new ArrayList<Truck>();
		this.containers = new ArrayList<Container>();
		this.roads = new ArrayList<Road>();
	}

	public static <T extends MapElement> T getElement(Class<T> clazz,
			Point point, ArrayList<ArrayList<MapElement>> mapMatrix) {
		try {
			return clazz.cast(mapMatrix.get(point.y).get(point.x));
		} catch (ClassCastException | IndexOutOfBoundsException e) {
			return null;
		}
	}

	private static Point getAdjacentPoint(Point location, int direction) {
		switch (direction) {
		case Assets.TOP:
			return new Point(location.x, location.y - 1);
		case Assets.BOTTOM:
			return new Point(location.x, location.y + 1);
		case Assets.LEFT:
			return new Point(location.x - 1, location.y);
		case Assets.RIGHT:
			return new Point(location.x + 1, location.y);
		default:
			// should never happen
			return null;
		}
	}

	public static <T extends MapElement> Point findElement(Class<T> clazz,
			T toFind, ArrayList<ArrayList<MapElement>> mapMatrix) {
		Point point = new Point();
		for (int y = 0; y < mapMatrix.size(); y++) {
			point.y = y;
			for (int x = 0; x < mapMatrix.get(0).size(); x++) {
				point.x = x;
				T element = Map.<T> getElement(clazz, point, mapMatrix);
				try {
					if (element.equals(toFind))
						return point;
				} catch (NullPointerException e) {

				}
			}
		}
		return null;
	}

	public static <T extends MapElement> List<Point> getAllAdjacentPoints(
			Class<T> clazz, Point location,
			ArrayList<ArrayList<MapElement>> mapMatrix) {
		List<Point> list = new LinkedList<>();

		Point topPoint = getAdjacentPoint(location, Assets.TOP);
		Point bottomPoint = getAdjacentPoint(location, Assets.BOTTOM);
		Point leftPoint = getAdjacentPoint(location, Assets.LEFT);
		Point rightPoint = getAdjacentPoint(location, Assets.RIGHT);

		T topElement = getElement(clazz, topPoint, mapMatrix);
		T bottomElement = getElement(clazz, bottomPoint, mapMatrix);
		T leftElement = getElement(clazz, leftPoint, mapMatrix);
		T rightElement = getElement(clazz, rightPoint, mapMatrix);
		if (topElement != null)
			list.add(topPoint);
		else
			list.add(null);
		if (bottomElement != null)
			list.add(bottomPoint);
		else
			list.add(null);
		if (leftElement != null)
			list.add(leftPoint);
		else
			list.add(null);
		if (rightElement != null)
			list.add(rightPoint);
		else
			list.add(null);
		return list;
	}

	public static <T extends MapElement> List<T> getAllAdjacentElements(
			Class<T> clazz, Point location,
			ArrayList<ArrayList<MapElement>> mapMatrix) {
		List<Point> points = getAllAdjacentPoints(clazz, location, mapMatrix);
		List<T> elements = new LinkedList<>();
		for (Point point : points) {
			if (point != null)
				elements.add(Map.<T> getElement(clazz, point, mapMatrix));
			else
				elements.add(null);
		}
		return elements;
	}

	public void initTrucks(ContainerController containerController) {
		System.out.println("Initializing trucks...");

		List<Truck> parsed = FileParser.parseTrucksFile("maps/big_route.txt",
				containerController, mapMatrix);
		for (Truck truck : parsed) {
			trucks.add(truck);
			truck.start();
		}
	}

	public static ArrayList<ArrayList<MapElement>> cloneMapMatrix(
			ArrayList<ArrayList<MapElement>> mapMatrix) {
		ArrayList<ArrayList<MapElement>> returnMatrix = new ArrayList<ArrayList<MapElement>>();
		for (ArrayList<MapElement> line : mapMatrix) {
			ArrayList<MapElement> returnLine = new ArrayList<MapElement>();
			for (MapElement element : line) {
				returnLine.add(element.copy());
			}
			returnMatrix.add(returnLine);
		}
		return returnMatrix;
	}

	public static Truck getTruckByAgentName(String agentName,
			ArrayList<Truck> trucks) {
		for (Truck truck : trucks)
			if (truck.getAgentName().equals(agentName))
				return truck;
		return null;
	}

	public void initRoads(Graph graph) {
		System.out.println("Initializing roads...");
		for (Road road : roads) {
			road.dijkstra = new Dijkstra(graph);
			road.dijkstra.execute(graph.getVertexByID(road.getID()));
		}
		System.out.println("Done processing roads");
	}
}

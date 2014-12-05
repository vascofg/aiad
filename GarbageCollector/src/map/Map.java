package map;

import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import algorithms.Graph;
import assets.Assets;
import elements.MapElement;
import elements.Road;
import elements.containers.Container;
import elements.trucks.GarbageTruck;
import elements.trucks.GlassTruck;
import elements.trucks.PaperTruck;
import elements.trucks.PlasticTruck;
import elements.trucks.Truck;

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
		if (bottomElement != null)
			list.add(bottomPoint);
		if (leftElement != null)
			list.add(leftPoint);
		if (rightElement != null)
			list.add(rightPoint);
		return list;
	}

	public static <T extends MapElement> List<T> getAllAdjacentElements(
			Class<T> clazz, Point location,
			ArrayList<ArrayList<MapElement>> mapMatrix) {
		List<Point> points = getAllAdjacentPoints(clazz, location, mapMatrix);
		List<T> elements = new LinkedList<>();
		for (Point point : points) {
			elements.add(Map.<T> getElement(clazz, point, mapMatrix));
		}
		return elements;
	}

	public void initTrucks(ContainerController containerController) {
		System.out.println("Initializing trucks...");
		Road initialRoad = Map.getElement(Road.class, initialLocation,
				mapMatrix);
		try {

			PaperTruck paper = new PaperTruck(initialLocation,
					Truck.defaultCapacity, containerController, "Papel",
					this.mapMatrix);
			trucks.add(paper);
			// put one of the trucks on the initial road to draw
			initialRoad.setTruck(paper);
			paper.start();

			PlasticTruck plastic = new PlasticTruck(initialLocation,
					Truck.defaultCapacity, containerController, "Plastico",
					this.mapMatrix);
			trucks.add(plastic);
			plastic.start();

			GlassTruck glass = new GlassTruck(initialLocation,
					Truck.defaultCapacity, containerController, "Vidro",
					this.mapMatrix);
			trucks.add(glass);
			glass.start();

			GarbageTruck garbage = new GarbageTruck(initialLocation,
					Truck.defaultCapacity, containerController, "Lixo",
					this.mapMatrix);
			trucks.add(garbage);
			garbage.start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	/*
	 * public void initRoads(Graph graph) {
	 * System.out.println("Initializing roads..."); for (Road road : roads) {
	 * road.dijkstra = new Dijkstra(graph);
	 * road.dijkstra.execute(graph.getVertexByID(road.getID())); } }
	 */
}

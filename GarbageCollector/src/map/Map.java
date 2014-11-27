package map;

import jade.core.NotFoundException;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

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
	public Road initialRoad;
	public Graph graph;

	public Map() {
		this.mapMatrix = new ArrayList<ArrayList<MapElement>>();
		this.trucks = new ArrayList<Truck>();
		this.containers = new ArrayList<Container>();
		this.roads = new ArrayList<Road>();
	}

	public static int[] getElementIndexes(MapElement toFind,
			ArrayList<ArrayList<MapElement>> mapMatrix)
			throws NotFoundException {
		int[] indexes = new int[2];
		for (int y = 0; y < mapMatrix.size(); y++) {
			indexes[1] = y;
			ArrayList<MapElement> line = mapMatrix.get(y);
			for (int x = 0; x < line.size(); x++) {
				indexes[0] = x;
				if (line.get(x).equals(toFind))
					return indexes;
			}
		}
		throw new NotFoundException();
	}

	public static MapElement getAdjacentElement(MapElement element,
			int direction, ArrayList<ArrayList<MapElement>> mapMatrix) {
		try {
			int[] indexes = Map.getElementIndexes(element, mapMatrix);
			int x = indexes[0], y = indexes[1];

			switch (direction) {
			case Assets.TOP:
				return mapMatrix.get(y - 1).get(x);
			case Assets.BOTTOM:
				return mapMatrix.get(y + 1).get(x);
			case Assets.LEFT:
				return mapMatrix.get(y).get(x - 1);
			case Assets.RIGHT:
				return mapMatrix.get(y).get(x + 1);
			default:
				return null;
			}
		} catch (NotFoundException e) {
			return null;
		}
	}

	public static Road getAdjacentRoad(MapElement element, int direction,
			ArrayList<ArrayList<MapElement>> mapMatrix) {
		try {
			return (Road) getAdjacentElement(element, direction, mapMatrix);
		} catch (ClassCastException e) {
			return null;
		}
	}

	public static List<Road> getAllAdjacentRoads(MapElement element,
			ArrayList<ArrayList<MapElement>> mapMatrix) {
		List<Road> list = new LinkedList<Road>();
		Road topRoad = getAdjacentRoad(element, Assets.TOP, mapMatrix);
		Road bottomRoad = getAdjacentRoad(element, Assets.BOTTOM, mapMatrix);
		Road leftRoad = getAdjacentRoad(element, Assets.LEFT, mapMatrix);
		Road rightRoad = getAdjacentRoad(element, Assets.RIGHT, mapMatrix);
		if (topRoad != null)
			list.add(topRoad);
		if (bottomRoad != null)
			list.add(bottomRoad);
		if (leftRoad != null)
			list.add(leftRoad);
		if (rightRoad != null)
			list.add(rightRoad);
		return list;
	}

	public static Container getAdjacentContainer(MapElement element,
			int direction, ArrayList<ArrayList<MapElement>> mapMatrix) {
		try {
			return (Container) getAdjacentElement(element, direction, mapMatrix);
		} catch (ClassCastException e) {
			return null;
		}
	}

	public static List<Container> getAllAdjacentContainers(MapElement element,
			ArrayList<ArrayList<MapElement>> mapMatrix) {
		List<Container> list = new LinkedList<Container>();
		Container topContainer = getAdjacentContainer(element, Assets.TOP,
				mapMatrix);
		Container bottomContainer = getAdjacentContainer(element,
				Assets.BOTTOM, mapMatrix);
		Container leftContainer = getAdjacentContainer(element, Assets.LEFT,
				mapMatrix);
		Container rightContainer = getAdjacentContainer(element, Assets.RIGHT,
				mapMatrix);
		if (topContainer != null)
			list.add(topContainer);
		if (bottomContainer != null)
			list.add(bottomContainer);
		if (leftContainer != null)
			list.add(leftContainer);
		if (rightContainer != null)
			list.add(rightContainer);
		return list;
	}

	public void initTrucks(ContainerController containerController) {
		System.out.println("Initializing trucks...");
		try {
			trucks.add(new PlasticTruck(initialRoad, Truck.defaultCapacity,
					containerController, "Plastico", this.mapMatrix));
			trucks.add(new PaperTruck(initialRoad, Truck.defaultCapacity,
					containerController, "Papel", this.mapMatrix));
			trucks.add(new GlassTruck(initialRoad, Truck.defaultCapacity,
					containerController, "Vidro", this.mapMatrix));
			trucks.add(new GarbageTruck(initialRoad, Truck.defaultCapacity,
					containerController, "Lixo", this.mapMatrix));
			containerController.createNewAgent("rma", "jade.tools.rma.rma",
					new Object[0]).start();
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
	/*
	 * public void initRoads(Graph graph) {
	 * System.out.println("Initializing roads..."); for (Road road : roads) {
	 * road.dijkstra = new Dijkstra(graph);
	 * road.dijkstra.execute(graph.getVertexByID(road.getID())); } }
	 */
}

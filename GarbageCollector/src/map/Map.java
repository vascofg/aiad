package map;

import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import algorithms.Dijkstra;
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

	public Map() {
		this.mapMatrix = new ArrayList<ArrayList<MapElement>>();
		this.trucks = new ArrayList<Truck>();
		this.containers = new ArrayList<Container>();
		this.roads = new ArrayList<Road>();
	}

	public MapElement getElement(int x, int y) throws IndexOutOfBoundsException {
		return mapMatrix.get(y).get(x);
	}

	public MapElement getAdjacentElement(MapElement element, int direction) {
		try {
			switch (direction) {
			case Assets.TOP:
				return getElement(element.getX(), element.getY() - 1);
			case Assets.BOTTOM:
				return getElement(element.getX(), element.getY() + 1);
			case Assets.LEFT:
				return getElement(element.getX() - 1, element.getY());
			case Assets.RIGHT:
				return getElement(element.getX() + 1, element.getY());
			default:
				return null;
			}
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public Road getAdjacentRoad(MapElement element, int direction) {
		try {
			return (Road) getAdjacentElement(element, direction);
		} catch (ClassCastException e) {
			return null;
		}
	}

	public List<Road> getAllAdjacentRoads(MapElement element) {
		List<Road> list = new LinkedList<Road>();
		Road topRoad = getAdjacentRoad(element, Assets.TOP);
		Road bottomRoad = getAdjacentRoad(element, Assets.BOTTOM);
		Road leftRoad = getAdjacentRoad(element, Assets.LEFT);
		Road rightRoad = getAdjacentRoad(element, Assets.RIGHT);
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

	public Container getAdjacentContainer(MapElement element, int direction) {
		try {
			return (Container) getAdjacentElement(element, direction);
		} catch (ClassCastException e) {
			return null;
		}
	}

	public List<Container> getAllAdjacentContainers(MapElement element) {
		List<Container> list = new LinkedList<Container>();
		Container topContainer = getAdjacentContainer(element, Assets.TOP);
		Container bottomContainer = getAdjacentContainer(element, Assets.BOTTOM);
		Container leftContainer = getAdjacentContainer(element, Assets.LEFT);
		Container rightContainer = getAdjacentContainer(element, Assets.RIGHT);
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
					containerController, "Plastico"));
			trucks.add(new PaperTruck(initialRoad, Truck.defaultCapacity,
					containerController, "Papel"));
			trucks.add(new GlassTruck(initialRoad, Truck.defaultCapacity,
					containerController, "Vidro"));
			trucks.add(new GarbageTruck(initialRoad, Truck.defaultCapacity,
					containerController, "Lixo"));
			containerController.createNewAgent("rma", "jade.tools.rma.rma",
					new Object[0]).start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void initRoads(Graph graph) {
		System.out.println("Initializing roads...");
		for (Road road : roads) {
			road.dijkstra = new Dijkstra(graph);
			road.dijkstra.execute(graph.getVertexByID(road.getID()));
		}
	}
}

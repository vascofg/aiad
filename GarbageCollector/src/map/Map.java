package map;

import java.util.ArrayList;

import assets.Assets;
import elements.MapElement;
import elements.Road;
import elements.trucks.GarbageTruck;
import elements.trucks.GlassTruck;
import elements.trucks.PaperTruck;
import elements.trucks.PlasticTruck;
import elements.trucks.Truck;

public class Map {
	public ArrayList<ArrayList<MapElement>> mapMatrix;
	public ArrayList<Truck> trucks;
	public Road initialRoad;

	public Map() {
		this.mapMatrix = new ArrayList<ArrayList<MapElement>>();
		this.trucks = new ArrayList<Truck>();
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

	public void initTrucks() {
		trucks.add(new PlasticTruck(initialRoad));
		trucks.add(new PaperTruck(initialRoad));
		trucks.add(new GlassTruck(initialRoad));
		trucks.add(new GarbageTruck(initialRoad));
	}
}

package main;

import java.util.List;
import java.util.Random;

import javax.swing.JComponent;

import map.Map;
import elements.Road;
import elements.trucks.Truck;

public class TruckThread extends Thread {
	private boolean go = true;
	private static final int tickTime = 500; // in ms
	private static Random randGenerator = new Random();
	private Map map;
	private JComponent component;

	public TruckThread(Map map, JComponent component) {
		this.map = map;
		this.component = component;
	}

	@Override
	public void run() {
		while (go) {
			try {
				for (Truck truck : map.trucks) {
					moveRandomDirection(truck);
				}
				component.repaint();
				Thread.sleep(tickTime);
			} catch (InterruptedException e) {
				System.out.println("Thread interrupted, exiting");
			}
		}
	}

	// temporary
	private void moveRandomDirection(Truck truck) {
		List<Road> adjacentRoads = map.getAllAdjacentRoads(truck.getLocation());
		truck.moveTruck(adjacentRoads.get(randGenerator.nextInt(adjacentRoads
				.size())));
		if (truck.emptyAdjacentContainers(map.getAllAdjacentContainers(truck
				.getLocation()))) // if emptied any, have to repaint
			component.repaint();
	}

	@Override
	public void interrupt() {
		this.go = false;
		super.interrupt();
	}
}

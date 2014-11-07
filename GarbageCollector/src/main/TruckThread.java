package main;

import java.util.Random;

import assets.Assets;
import elements.trucks.Truck;

public class TruckThread extends Thread {
	private boolean go = true;
	private static final int tickTime = 500; // in ms
	private static Random randGenerator = new Random();

	@Override
	public void run() {
		while (go) {
			try {
				for (Truck truck : GarbageCollector.map.trucks) {
					moveRandomDirection(truck);
				}
				GarbageCollector.frame.trucksComponent.repaint();
				Thread.sleep(tickTime);
			} catch (InterruptedException e) {
				System.out.println("Thread interrupted, exiting");
			}
		}
	}

	// temporary
	private void moveRandomDirection(Truck truck) {
		while (!truck.moveTruck(randGenerator.nextInt(4))) {
		}
		if(truck.emptyAdjacentContainers()) //if emptied any, have to repaint
			GarbageCollector.frame.mapComponent.repaint();
	}

	@Override
	public void interrupt() {
		this.go = false;
		super.interrupt();
	}
}

package main;

import java.util.ArrayList;

import javax.swing.JComponent;

import elements.trucks.Truck;

public class TruckThread extends Thread {
	private boolean go = true;
	private static final int tickTime = 500; // in ms
	private ArrayList<Truck> trucks;
	private JComponent truckComponent, mapComponent;

	public TruckThread(ArrayList<Truck> trucks, JComponent truckComponent,
			JComponent containerComponent) {
		this.trucks = trucks;
		this.truckComponent = truckComponent;
		this.mapComponent = containerComponent;
	}

	@Override
	public void run() {
		boolean changed = false;
		while (go) {
			try {
				for (Truck truck : trucks) {
					if (truck.moveRandomDirection())
						changed = true;
				}
				if (changed) {
					mapComponent.repaint();
					changed = false;
				}
				truckComponent.repaint();
				Thread.sleep(tickTime);
			} catch (InterruptedException e) {
				System.out.println("Thread interrupted, exiting");
			}
		}
	}

	@Override
	public void interrupt() {
		this.go = false;
		super.interrupt();
	}
}

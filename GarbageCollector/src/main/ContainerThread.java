package main;

import java.util.Random;

import elements.containers.Container;
import exceptions.ContainerFullException;

public class ContainerThread extends Thread {
	private boolean go = true;
	private static final int tickTime = 10000; // in ms
	private static Random randGenerator = new Random();

	@Override
	public void run() {
		while (go) {
			try {
				for (Container c : GarbageCollector.map.containers) {
					addRandomToContainer(c);
				}
				GarbageCollector.frame.mapComponent.repaint();
				Thread.sleep(tickTime);
			} catch (InterruptedException e) {
				System.out.println("Thread interrupted, exiting");
			}
		}
	}

	private void addRandomToContainer(Container c) {
		try {
			c.addToContainer(randGenerator.nextInt(2));
		} catch (ContainerFullException e) {
			//do nothing
		}
	}

	@Override
	public void interrupt() {
		this.go = false;
		super.interrupt();
	}
}

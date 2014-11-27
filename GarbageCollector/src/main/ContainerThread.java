package main;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.JComponent;

import elements.containers.Container;
import exceptions.ContainerFullException;

public class ContainerThread extends Thread {
	private boolean go = true;
	private static final int tickTime = 10000; // in ms
	private ArrayList<Container> containers;
	private JComponent component;

	public ContainerThread(ArrayList<Container> containers, JComponent component) {
		this.containers = containers;
		this.component = component;
	}

	@Override
	public void run() {
		while (go) {
			try {
				for (Container c : containers) {
					addRandomToContainer(c);
				}
				component.repaint();
				Thread.sleep(tickTime);
			} catch (InterruptedException e) {
				System.out.println("Thread interrupted, exiting");
			}
		}
	}

	private void addRandomToContainer(Container c) {
		try {
			c.addToContainer(GarbageCollector.randGenerator.nextInt(2));
		} catch (ContainerFullException e) {
			// do nothing
		}
	}

	@Override
	public void interrupt() {
		this.go = false;
		super.interrupt();
	}
}

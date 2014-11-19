package main;

import java.util.Random;

import javax.swing.JComponent;

import map.Map;
import elements.containers.Container;
import exceptions.ContainerFullException;

public class ContainerThread extends Thread {
	private boolean go = true;
	private static final int tickTime = 10000; // in ms
	private static Random randGenerator = new Random();
	private Map map;
	private JComponent component;

	public ContainerThread(Map map, JComponent component) {
		this.map = map;
		this.component = component;
	}
	
	@Override
	public void run() {
		while (go) {
			try {
				for (Container c : map.containers) {
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

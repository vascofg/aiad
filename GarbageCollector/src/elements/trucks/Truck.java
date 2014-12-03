package elements.trucks;

import jade.util.Event;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import main.GarbageCollector;
import map.Map;
import agents.TruckAgent;
import elements.DrawableElement;
import elements.MapElement;
import elements.Road;
import elements.containers.Container;
import exceptions.TruckFullException;

public abstract class Truck extends Thread implements DrawableElement {

	public static int defaultCapacity = 20;

	private Point currentLocation;
	private ArrayList<ArrayList<MapElement>> mapMatrix;
	private String agentName;
	public AgentController agentController;
	int capacity, usedCapacity;

	private boolean go = true;
	private static final int tickTime = 500; // in ms

	public Truck(Point initialLocation, int capacity,
			ContainerController containerController, String agentName,
			int agentType, ArrayList<ArrayList<MapElement>> mapMatrix)
			throws StaleProxyException {
		this.currentLocation = initialLocation;
		this.capacity = capacity;
		this.usedCapacity = 0;
		this.mapMatrix = Map.cloneMapMatrix(mapMatrix);
		this.agentController = containerController.createNewAgent(agentName,
				TruckAgent.class.getName(), new Object[] { agentType });
		this.agentName = agentName;
		this.agentController.start();
	}

	public abstract int getType();

	public Point getLocation() {
		return this.currentLocation;
	}

	public void addToTruck(int ammount) throws TruckFullException {
		if ((this.usedCapacity + ammount) > this.capacity)
			throw new TruckFullException();
		else
			this.usedCapacity += ammount;
	}

	public void emptyTruck() {
		this.usedCapacity = 0;
	}

	public boolean moveTruck(Point destination) throws StaleProxyException,
			InterruptedException {
		Event event = moveRequest(destination);
		boolean result = (boolean) event.waitUntilProcessed(); // TODO:
																// acrescentar
																// timeout?
		if (result) {
			this.currentLocation = destination;
			return true;
		}
		return false;
	}

	public boolean emptyAdjacentContainers(List<Container> adjacentContainers,
			List<Point> adjacentContainerPoints) {
		boolean emptiedAny = false;
		try {
			for (int i = 0; i < adjacentContainers.size(); i++) {
				Container c = adjacentContainers.get(i);
				Point p = adjacentContainerPoints.get(i);

				// pergunta usedCapacity
				// if (c.getUsedCapacity() > 0) {
				if (c.truckCompatible(this)) { // my type
					containerRequest(p.x, p.y);
					// esvazia
					/*
					 * try { this.addToTruck(c.getUsedCapacity());
					 * c.emptyContainer(); emptiedAny = true; } catch
					 * (TruckFullException e) { // TODO: inform trucks of same
					 * // type //avisa outros do mm tipo break; }
					 */
				} else
					containerInform(c.getType(), p.x, p.y);
			}
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// }
		return emptiedAny;
	}

	public void containerInform(int garbageType, int X, int Y)
			throws StaleProxyException {
		this.agentController.putO2AObject(new String(
				TruckAgent.INFORMOTHERTRUCKS + " " + garbageType + " " + X
						+ " " + Y), false);
	}

	public Event containerRequest(int X, int Y) throws StaleProxyException {
		this.agentController.putO2AObject(new String(
				TruckAgent.CONTAINERCAPACITY + " " + X + " " + Y), false);
		Event event = new Event(TruckAgent.CONTAINERCAPACITY, this);
		this.agentController.putO2AObject(event, false);
		return event;
	}

	public Event moveRequest(Point destination) throws StaleProxyException {
		this.agentController.putO2AObject(new String(TruckAgent.MOVE + " "
				+ this.agentName + " " + destination.x + " " + destination.y), false);
		Event event = new Event(TruckAgent.MOVE, this);
		this.agentController.putO2AObject(event, false);
		return event;
	}

	// temporary
	public boolean moveRandomDirection() {
		try {
			List<Point> possibleMoves = Map.getAllAdjacentPoints(Road.class,
					getLocation(), mapMatrix);
			return moveTruck(possibleMoves.get(GarbageCollector.randGenerator
					.nextInt(possibleMoves.size())));
		} catch (StaleProxyException | InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	/* THREAD */

	@Override
	public synchronized void start() {
		System.out.println("Starting truck thread...");
		super.start();
	}

	@Override
	public void run() {
		while (go) {
			try {
				this.moveRandomDirection();
				Thread.sleep(tickTime);
			} catch (InterruptedException e) {
				System.out.println("Truck thread interrupted, exiting");
			}
		}
	}

	@Override
	public void interrupt() {
		this.go = false;
		super.interrupt();
	}
}

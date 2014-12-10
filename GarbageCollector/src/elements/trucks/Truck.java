package elements.trucks;

import jade.util.Event;
import jade.wrapper.AgentController;
import jade.wrapper.AgentState;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import main.GarbageCollector;
import map.Map;
import agents.TruckAgent;
import assets.Assets;
import elements.DrawableElement;
import elements.MapElement;
import elements.Road;
import elements.containers.Container;
import exceptions.TruckFullException;

public abstract class Truck extends Thread implements DrawableElement {

	public static int defaultCapacity = 20;

	private int from;
	private Point currentLocation;
	private ArrayList<Point> route;
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

	public String getAgentName() {
		return agentName;
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

	public void moveTruck(Point destination) {
		this.currentLocation = destination;
	}

	public ArrayList<Point> getRoute() {
		return route;
	}

	public void setRoute(ArrayList<Point> route) {
		this.route = route;
	}

	public boolean emptyAdjacentContainers(List<Container> adjacentContainers,
			List<Point> adjacentContainerPoints) {
		boolean emptiedAny = false;
		try {
			for (int i = 0; i < adjacentContainers.size(); i++) {
				Container c = adjacentContainers.get(i);
				if (c == null)
					continue;
				Point p = adjacentContainerPoints.get(i);
				if (c.truckCompatible(this)) {
					if (containerRequest(p.x, p.y))
						emptiedAny = true;
				} else
					containerInform(c.getType(), p.x, p.y);

			}
		} catch (StaleProxyException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return emptiedAny;
	}

	public void containerInform(int garbageType, int X, int Y)
			throws StaleProxyException {
		Event event = new Event(TruckAgent.INFORM_OTHER_TRUCKS, this);
		event.addParameter(new String(TruckAgent.INFORM_OTHER_TRUCKS + " "
				+ garbageType + " " + X + " " + Y));
		this.agentController.putO2AObject(event, false);
	}

	public void emptiedContainerInform(int X, int Y) throws StaleProxyException {
		Event event = new Event(TruckAgent.INFORM_EMPTIED_CONTAINER, this);
		event.addParameter(new String(TruckAgent.INFORM_EMPTIED_CONTAINER + " "
				+ X + " " + Y));
		this.agentController.putO2AObject(event, false);
	}

	public boolean containerRequest(int X, int Y) throws StaleProxyException,
			InterruptedException {
		Event event = new Event(TruckAgent.REQUEST_CONTAINER_CAPACITY, this);
		event.addParameter(new String(TruckAgent.REQUEST_CONTAINER_CAPACITY
				+ " " + X + " " + Y));
		event.addParameter(new Point(X, Y));
		this.agentController.putO2AObject(event, false);

		int usedCapacity = (int) event.waitUntilProcessed();
		if (usedCapacity > 0) {
			try {
				this.addToTruck(usedCapacity);
				emptiedContainerInform(X, Y);
				return true;
			} catch (TruckFullException e) {
				containerInform(this.getType(), X, Y);
			}
		}
		return false;
	}

	public boolean moveRequest(Point destination) throws StaleProxyException,
			InterruptedException {
		Event event = new Event(TruckAgent.REQUEST_MOVE, this);
		event.addParameter(new String(TruckAgent.REQUEST_MOVE + " "
				+ this.agentName + " " + destination.x + " " + destination.y));
		this.agentController.putO2AObject(event, false);

		boolean result = (boolean) event.waitUntilProcessed(); // TODO:
																// acrescentar
																// timeout?
		if (result)
			moveTruck(destination);

		return result;
	}

	// temporary
	public boolean moveRandomDirection(List<Point> possibleMoves) {
		try {
			return moveRequest(possibleMoves.get(GarbageCollector.randGenerator
					.nextInt(possibleMoves.size())));
		} catch (StaleProxyException | InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean goRoute() throws StaleProxyException, InterruptedException {
		List<Point> possibleMoves = Map.getAllAdjacentPoints(Road.class,
				getLocation(), mapMatrix);
		for (int i = 0; i < 4; i++) {
			Point p = possibleMoves.get(i);
			// TODO: se so tiver um possibleMove, ignora from (anda para trás)
			// TODO: REFACTOR (usar Assets)
			if (p != null && i != this.from && route.contains(p)) {
				this.from = Assets.reverseDirection(i);
				return this.moveRequest(p);
			}
		}
		return false;
	}

	/* THREAD */

	@Override
	public synchronized void start() {
		System.out.println("Starting " + agentName + " truck thread...");
		super.start();
	}

	@Override
	public void run() {
		while (go) {
			try {
				if (agentController.getState().getCode() == AgentState.cAGENT_STATE_IDLE) {
					if (this.goRoute())
						emptyAdjacentContainers(Map.getAllAdjacentElements(
								Container.class, getLocation(), mapMatrix),
								Map.getAllAdjacentPoints(Container.class,
										getLocation(), mapMatrix));
				}
				Thread.sleep(tickTime);
			} catch (InterruptedException e) {
				System.out.println("Truck thread interrupted, exiting");
			} catch (StaleProxyException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void interrupt() {
		this.go = false;
		super.interrupt();
	}
}

package elements.trucks;

import jade.util.Event;
import jade.wrapper.AgentController;
import jade.wrapper.AgentState;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.GarbageCollector;
import map.Map;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;

import agents.TruckAgent;
import assets.Assets;
import elements.Deposit;
import elements.DrawableElement;
import elements.MapElement;
import elements.Road;
import elements.containers.Container;
import exceptions.TruckFullException;

public abstract class Truck extends Thread implements DrawableElement {

	public static int defaultCapacity = 20;

	Point currentLocation;

	private Point currentDestination;
	ArrayList<Point> pointsToVisit;
	private int pointIndex, moveDirection;
	private ArrayList<DefaultEdge> currentDestinationRoute;
	private HashMap<ArrayList<Point>, List<DefaultEdge>> routes;
	ArrayList<ArrayList<MapElement>> mapMatrix;
	String agentName;
	public AgentController agentController;
	private int capacity, usedCapacity;
	Event gotInformEvent;
	private Thread waitInformThread;

	private boolean go = true;
	private static final int tickTime = 50; // in ms

	public Truck(Point initialLocation, int capacity,
			ContainerController containerController, String name,
			int agentType, ArrayList<ArrayList<MapElement>> matrix)
			throws StaleProxyException {
		super(name + " truck");
		this.currentLocation = initialLocation;
		this.capacity = capacity;
		this.usedCapacity = 0;
		this.mapMatrix = Map.cloneMapMatrix(matrix);
		this.agentController = containerController.createNewAgent(name,
				TruckAgent.class.getName(), new Object[] { agentType });
		this.agentController.start();
		// wait until agent is started
		while (agentController.getState().getCode() != AgentState.cAGENT_STATE_IDLE) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.gotInformEvent = new Event(TruckAgent.GOT_INFORM_EVENT, this);
		this.agentController.putO2AObject(this.gotInformEvent, true);
		this.agentName = name;
		this.waitInformThread = new TruckInform(this);
		this.waitInformThread.start();
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
		int moveDirection = Assets.getMoveDirection(this.getLocation(),
				destination);
		if (moveDirection > -1) // actual move
			this.setMoveDirection(moveDirection);
		this.currentLocation = destination;
	}

	public void setPointsToVisit(ArrayList<Point> route) {
		this.pointsToVisit = route;
		this.currentDestinationRoute = new ArrayList<>();
		this.routes = new HashMap<ArrayList<Point>, List<DefaultEdge>>();
		this.pointIndex = 0;
	}

	public boolean emptyAdjacentContainers(List<Container> adjacentContainers,
			List<Point> adjacentContainerPoints) {
		boolean emptiedAny = false;
		try {
			for (int i = 0; i < adjacentContainers.size(); i++) {
				Container c = adjacentContainers.get(i);
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

	public boolean emptyInDeposit(List<Point> adjacentDeposits) {
		if (adjacentDeposits.size() > 0) {
			this.emptyTruck();
			return true;
		}
		return false;
	}

	public void containerInform(int garbageType, int X, int Y)
			throws StaleProxyException {
		Event event = new Event(TruckAgent.INFORM_OTHER_TRUCKS, this);
		event.addParameter(new String(garbageType + " " + X + " " + Y));
		this.agentController.putO2AObject(event, false);
	}

	public void emptiedContainerInform(int X, int Y) throws StaleProxyException {
		Event event = new Event(TruckAgent.INFORM_EMPTIED_CONTAINER, this);
		event.addParameter(new String(X + " " + Y));
		this.agentController.putO2AObject(event, false);
	}

	public boolean containerRequest(int X, int Y) throws StaleProxyException,
			InterruptedException {
		Event event = new Event(TruckAgent.REQUEST_CONTAINER_CAPACITY, this);
		event.addParameter(new String(X + " " + Y));
		event.addParameter(new Point(X, Y));
		this.agentController.putO2AObject(event, false);

		int usedContainerCapacity = (int) event.waitUntilProcessed();
		if (usedContainerCapacity > 0) {
			try {
				this.addToTruck(usedContainerCapacity);
				emptiedContainerInform(X, Y);
				return true;
				// TODO: esvaziar o que puder (tem de avisar mundo)
				// TODO: ir esvaziar quando acima de x capacidade usada
			} catch (TruckFullException e) {
				containerInform(this.getType(), X, Y);
				this.currentDestination = getClosestDeposit();
				System.out.println(getAgentName() + " is full, going to "
						+ this.currentDestination.x + "|"
						+ this.currentDestination.y + " to empty...");
			}
		}
		return false;
	}

	private Point getClosestDeposit() {
		int minDistance = Integer.MAX_VALUE;
		Point closestDeposit = null;
		for (Point deposit : Map.INSTANCE.depositPoints) {
			// TODO: optimizar estrada a escolher
			Point road = Map.getAllAdjacentPoints(Road.class, deposit,
					mapMatrix).get(0);
			ArrayList<Point> points = new ArrayList<Point>(2);
			points.add(currentLocation);
			points.add(road);
			List<DefaultEdge> path;
			if (!this.routes.containsKey(points)) {
				path = DijkstraShortestPath.findPathBetween(Map.INSTANCE.graph,
						currentLocation, road);
				this.routes.put(points, path);
			} else
				path = this.routes.get(points);

			if (path.size() < minDistance)
				closestDeposit = road;
		}
		return closestDeposit;
	}

	public boolean moveRequest(Point destination) throws StaleProxyException,
			InterruptedException {
		Event event = new Event(TruckAgent.REQUEST_MOVE, this);
		event.addParameter(new String(this.agentName + " " + destination.x
				+ " " + destination.y + " "
				+ Assets.getMoveDirection(this.getLocation(), destination)));
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
		if (pointsToVisit.size() < 1)
			return false;
		if (pointIndex == pointsToVisit.size())
			pointIndex = 0; // TODO: ESTRATEGIAS
		if (currentDestination == null)
			currentDestination = pointsToVisit.get(pointIndex);
		if (currentDestinationRoute.isEmpty()) {
			ArrayList<Point> points = new ArrayList<Point>(2);
			points.add(currentLocation);
			points.add(currentDestination);
			if (!routes.containsKey(points))
				routes.put(points, DijkstraShortestPath
						.findPathBetween(Map.INSTANCE.graph, currentLocation,
								currentDestination));
			currentDestinationRoute = new ArrayList<DefaultEdge>(
					routes.get(points));
		}
		if (!currentDestinationRoute.isEmpty()) {
			DefaultEdge currentEdge = currentDestinationRoute.get(0);
			if (this.moveRequest(Map.INSTANCE.graph.getEdgeTarget(currentEdge))) {
				currentDestinationRoute.remove(0);
				if (currentLocation.equals(currentDestination)) {
					pointIndex++;
					currentDestination = null;
				}
				return true;
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
				if (this.goRoute()) {
					if (this.usedCapacity < this.capacity)
						emptyAdjacentContainers(Map.getAllAdjacentElements(
								Container.class, getLocation(), mapMatrix),
								Map.getAllAdjacentPoints(Container.class,
										getLocation(), mapMatrix));
					//TODO: esvaziar só acima de X???
					if (emptyInDeposit(Map.getAllAdjacentPoints(Deposit.class,
							getLocation(), mapMatrix))) {
						/*
						 * System.out.println(getAgentName() +
						 * " emptied the truck at: " + getLocation().x + "|" +
						 * getLocation().y);
						 */
					}
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

	public int getMoveDirection() {
		return moveDirection;
	}

	public void setMoveDirection(int moveDirection) {
		this.moveDirection = moveDirection;
	}
}

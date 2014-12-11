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

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;

import agents.TruckAgent;
import assets.Assets;
import elements.DrawableElement;
import elements.MapElement;
import elements.Road;
import elements.containers.Container;
import exceptions.TruckFullException;

public abstract class Truck extends Thread implements DrawableElement {

	public static int defaultCapacity = 20;

	private Point currentLocation, currentDestination;
	private ArrayList<Point> pointsToVisit;
	private int pointIndex, moveDirection;
	private List<DefaultEdge> currentDestinationRoute;
	private ArrayList<ArrayList<MapElement>> mapMatrix;
	private String agentName;
	public AgentController agentController;
	private int capacity, usedCapacity;
	private Event gotInformEvent;
	private Thread waitInformThread;

	private boolean go = true;
	private static final int tickTime = 250; // in ms

	public Truck(Point initialLocation, int capacity,
			ContainerController containerController, String name,
			int agentType, ArrayList<ArrayList<MapElement>> matrix)
			throws StaleProxyException {
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
		createInformThread();
		this.waitInformThread.start();
		this.agentName = name;
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
		if (pointsToVisit.size() <= 1)
			return false;
		if (pointIndex == pointsToVisit.size())
			pointIndex = 0; // TODO: ESTRATEGIAS
		if (currentDestination == null)
			currentDestination = pointsToVisit.get(pointIndex);
		if (currentDestinationRoute.isEmpty()) {
			// TODO: guardar rota entre 2 containers
			currentDestinationRoute = DijkstraShortestPath.findPathBetween(
					Map.INSTANCE.graph, currentLocation, currentDestination);
		}
		DefaultEdge currentEdge = currentDestinationRoute.get(0);
		if (this.moveRequest(Map.INSTANCE.graph.getEdgeTarget(currentEdge))) {
			currentDestinationRoute.remove(0);
			if (currentLocation.equals(currentDestination)) {
				pointIndex++;
				currentDestination = null;
			}
			return true;
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
				if (this.goRoute())
					emptyAdjacentContainers(Map.getAllAdjacentElements(
							Container.class, getLocation(), mapMatrix),
							Map.getAllAdjacentPoints(Container.class,
									getLocation(), mapMatrix));
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

	private void createInformThread() {
		this.waitInformThread = new Thread(new Runnable() {
			private boolean go = true;

			@Override
			public void run() {
				try {
					while (go) {
						Point container = (Point) gotInformEvent
								.waitUntilProcessed();
						// TODO: processo de decisão (qual dos trucks vai lá)
						List<Point> adjacentRoads = Map.getAllAdjacentPoints(
								Road.class, container, mapMatrix);
						boolean contains = false;
						for (Point road : adjacentRoads)
							if (pointsToVisit.contains(road)) {
								contains = true;
								break;
							}
						if (!contains) {
							// TODO: optimizar qual das estradas escolher
							Point toVisit = adjacentRoads.get(0);
							// TODO: interface para opções
							pointsToVisit.add(toVisit); // permanente (no final
														// da
														// rota actual)
							// currentDestination = toVisit; //imediato
							System.out.println(agentName
									+ " added point to visit: " + toVisit.x
									+ "|" + toVisit.y);
						}
						gotInformEvent.reset();
					}
				} catch (InterruptedException e) {
					System.out.println("Inform thread interrupted!");
					this.go = false;
				}
			}
		});
	}

	public int getMoveDirection() {
		return moveDirection;
	}

	public void setMoveDirection(int moveDirection) {
		this.moveDirection = moveDirection;
	}
}

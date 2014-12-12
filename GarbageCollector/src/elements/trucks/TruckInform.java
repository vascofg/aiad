package elements.trucks;

import jade.util.Event;
import jade.wrapper.StaleProxyException;

import java.awt.Point;
import java.util.List;

import map.Map;

import org.jgrapht.alg.DijkstraShortestPath;

import agents.TruckAgent;
import elements.Road;

public class TruckInform extends Thread {
	private boolean go = true;
	private static final int waitTime = 500;
	private Truck truck;

	public TruckInform(Truck truck) {
		super(truck.agentName + " inform");
		this.truck = truck;
	}

	@Override
	public void run() {
		try {
			while (go) {
				Point container = (Point) truck.gotInformEvent
						.waitUntilProcessed();
				// TODO: processo de decisão (qual dos trucks vai lá)
				List<Point> adjacentRoads = Map.getAllAdjacentPoints(
						Road.class, container, truck.mapMatrix);
				boolean contains = false;
				for (Point road : adjacentRoads)
					if (truck.pointsToVisit.contains(road)) {
						contains = true;
						break;
					}
				Event sendInformDist = new Event(TruckAgent.INFORM_DISTANCE,
						this);
				if (!contains) {
					// TODO: optimizar qual das estradas escolher
					Point toVisit = adjacentRoads.get(0);
					int distance = DijkstraShortestPath.findPathBetween(
							Map.INSTANCE.graph, truck.currentLocation, toVisit)
							.size();
					Event getInformDist = new Event(
							TruckAgent.GOT_INFORM_DISTANCE_EVENT, this);
					truck.agentController.putO2AObject(getInformDist, true);
					sendInformDist.addParameter(truck.getType() + " "
							+ container.x + " " + container.y + " " + distance);
					truck.agentController.putO2AObject(sendInformDist, true);
					try {
						getInformDist.waitUntilProcessed(TruckInform.waitTime);
					} catch (InterruptedException e) {
						// timeout
						boolean mineIsCloser = true;
						try {
							int[] parameter;
							for (int i = 0; i < Integer.MAX_VALUE; i++) {
								parameter = (int[]) getInformDist
										.getParameter(i);
								Point receivedContainer = new Point(
										parameter[0], parameter[1]);
								if (receivedContainer.equals(container)) {
									int theirDistance = parameter[2];
									if (theirDistance < distance) {
										mineIsCloser = false;
										break;
									}
								} else
									System.out
											.println(truck.getAgentName()
													+ " got INFORM_DISTANCE for a different container!!!");
							}
						} catch (IndexOutOfBoundsException e2) {
							// acabou, n faz nada
						}
						if (mineIsCloser) {
							// TODO: interface para opções
							truck.pointsToVisit.add(toVisit); // permanente
							// (no final
							// da
							// rota
							// actual)
							// currentDestination = toVisit;
							// //imediato
							System.out.println(truck.agentName
									+ " added point to visit: " + toVisit.x
									+ "|" + toVisit.y);
						}
					}
				} else { // already on my route
					sendInformDist.addParameter(truck.getType() + " "
							+ container.x + " " + container.y + " -1");
					truck.agentController.putO2AObject(sendInformDist, false);
				}
				truck.gotInformEvent.reset();
			}
		} catch (InterruptedException e) {
			System.out.println("Inform thread interrupted!");
			this.go = false;
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
};

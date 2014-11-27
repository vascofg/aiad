package elements.trucks;

import jade.core.NotFoundException;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

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

public abstract class Truck implements DrawableElement {

	public static int defaultCapacity = 20;

	private Road currentLocation;
	private ArrayList<ArrayList<MapElement>> mapMatrix;
	public AgentController agentController;
	int capacity, usedCapacity;

	public Truck(Road initialLocation, int capacity,
			ContainerController containerController, String agentName,
			int agentType, ArrayList<ArrayList<MapElement>> mapMatrix)
			throws StaleProxyException {
		this.currentLocation = initialLocation;
		this.capacity = capacity;
		this.usedCapacity = 0;
		this.mapMatrix = Map.cloneMapMatrix(mapMatrix);
		this.agentController = containerController.createNewAgent(agentName,
				TruckAgent.class.getName(), new Object[] { agentType });
		this.agentController.start();
	}

	public abstract int getType();

	public Road getLocation() {
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

	public boolean moveTruck(Road destination) {
		if (true) { // TODO: verificar se há algum truck no caminho
			// TODO: notificar agente mundo
			this.currentLocation = destination;
			return true;
		}
		return false;
	}

	public boolean emptyAdjacentContainers(List<Container> adjacentContainers) {
		boolean emptiedAny = false;
		for (Container c : adjacentContainers) {
			// pergunta usedCapacity
			// if (c.getUsedCapacity() > 0) {
			if (c.truckCompatible(this)) {
				// esvazia
				/*
				 * try { this.addToTruck(c.getUsedCapacity());
				 * c.emptyContainer(); emptiedAny = true; } catch
				 * (TruckFullException e) { // TODO: inform trucks of same //
				 * type //avisa outros do mm tipo break; }
				 */
			} else { // inform other trucks
				// TODO: Send used capacity?
				try {
					int[] indexes = Map.getElementIndexes(c, mapMatrix);
					int x = indexes[0], y = indexes[1];
					informGarbage(c.getType(), x, y);
				} catch (StaleProxyException | NotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		// }
		return emptiedAny;
	}

	public void informGarbage(int garbageType, int X, int Y)
			throws StaleProxyException {
		StringBuilder sb = new StringBuilder(garbageType + " " + X + " " + Y);
		this.agentController.putO2AObject(sb.toString(), false);
	}

	// temporary
	public boolean moveRandomDirection() {
		List<Road> adjacentRoads = Map.getAllAdjacentRoads(getLocation(),
				mapMatrix);
		moveTruck(adjacentRoads.get(GarbageCollector.randGenerator
				.nextInt(adjacentRoads.size())));
		// if emptied any, return to repaint...
		return emptyAdjacentContainers(Map.getAllAdjacentContainers(
				getLocation(), mapMatrix));
	}
}

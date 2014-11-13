package elements.trucks;

import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import main.GarbageCollector;
import agents.TruckAgent;
import elements.DrawableElement;
import elements.Road;
import elements.containers.Container;
import exceptions.TruckFullException;

public abstract class Truck implements DrawableElement {

	public static int defaultCapacity = 20;

	private Road currentLocation;
	public AgentController agentController;
	int capacity, usedCapacity;

	public Truck(Road initialLocation, int capacity,
			ContainerController containerController, String agentName,
			int agentType) throws StaleProxyException {
		this.currentLocation = initialLocation;
		this.capacity = capacity;
		this.usedCapacity = 0;
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

	public boolean moveTruck(int direction) {
		Road destination = GarbageCollector.map.getAdjacentRoad(
				this.currentLocation, direction);
		if (destination != null) {
			this.currentLocation = destination;
			return true;
		}
		return false;
	}

	public boolean emptyAdjacentContainers() {
		boolean emptiedAny = false;
		for (int i = 1; i <= 4; i++) {
			Container c = GarbageCollector.map.getAdjacentContainer(
					this.currentLocation, i);
			if (c != null) {
				if (c.truckCompatible(this)) {
					try {
						this.addToTruck(c.getUsedCapacity());
						c.emptyContainer();
						emptiedAny = true;
					} catch (TruckFullException e) { //TODO: inform trucks of same type
						return false;
					}
				}
				else if(c.getUsedCapacity()>0) { // inform other trucks
					//TODO: Send used capacity?
					try {
						informGarbage(c.getType(), c.getX(), c.getY());
					} catch (StaleProxyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return emptiedAny;
	}

	public void informGarbage(int garbageType, int X, int Y)
			throws StaleProxyException {
		StringBuilder sb = new StringBuilder(garbageType + " " + X + " " + Y);
		this.agentController.putO2AObject(sb.toString(), false);
	}
}

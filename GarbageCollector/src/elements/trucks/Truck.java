package elements.trucks;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import main.GarbageCollector;
import elements.DrawableElement;
import elements.Road;
import elements.containers.Container;
import exceptions.TruckFullException;

public abstract class Truck extends Agent implements DrawableElement {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static int defaultCapacity = 20;

	private Road currentLocation;
	int capacity, usedCapacity;

	public Truck(Road initialLocation, int capacity) {
		super();
		this.currentLocation = initialLocation;
		this.capacity = capacity;
		this.usedCapacity = 0;
	}

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
			if (c != null && c.truckCompatible(this)) {
				try {
					this.addToTruck(c.getUsedCapacity());
					c.emptyContainer();
					emptiedAny = true;
				} catch (TruckFullException e) {
					return false;
				}
			}
		}
		return emptiedAny;
	}

	class TruckBehaviour extends SimpleBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			ACLMessage msg = blockingReceive();
			if (msg.getPerformative() == ACLMessage.INFORM) {
				System.out.println("Recebi uma mensagem do tipo INFORM");
			}
		}

		@Override
		public boolean done() {
			// always running
			return false;
		}

	}

	@Override
	protected void setup() {
		// TODO Auto-generated method stub
		super.setup();
	}
}

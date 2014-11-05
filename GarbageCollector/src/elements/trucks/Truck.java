package elements.trucks;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import main.GarbageCollector;
import elements.DrawableElement;
import elements.Road;

public abstract class Truck extends Agent implements DrawableElement {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Road currentLocation;

	public Truck(Road initialLocation) {
		super();
		this.currentLocation = initialLocation;
	}

	public Road getLocation() {
		return this.currentLocation;
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

package elements;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

public abstract class Truck extends Agent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	class TruckBehaviour extends SimpleBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			ACLMessage msg = blockingReceive();
				if(msg.getPerformative() == ACLMessage.INFORM) {
					System.out.println("Recebi uma mensagem do tipo INFORM");
				}
		}

		@Override
		public boolean done() {
			//always running
			return false;
		}
		
	}
	
	@Override
	protected void setup() {
		// TODO Auto-generated method stub
		super.setup();
	}
}

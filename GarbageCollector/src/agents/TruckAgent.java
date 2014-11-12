package agents;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class TruckAgent extends Agent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	class TruckBehaviour extends SimpleBehaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public TruckBehaviour(Agent a) {
			super(a);
		}
		
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

	// método setup
	protected void setup() {
		// regista agente no DF
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getName());
		sd.setType("a tua velha");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}

		// cria behaviour
		TruckBehaviour b = new TruckBehaviour(this);
		addBehaviour(b);

	}

	// método takeDown
	protected void takeDown() {
		// retira registo no DF
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
	
	public void informGarbage() {
		// pesquisa DF por agentes "ping"
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd1 = new ServiceDescription();
		sd1.setType("a tua velha");
		template.addServices(sd1);
		try {
			DFAgentDescription[] result = DFService.search(this, template);
			// envia mensagem "pong" inicial a todos os agentes "ping"
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			for (int i = 0; i < result.length; ++i)
				msg.addReceiver(result[i].getName());
			msg.setContent("pong");
			send(msg);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
}

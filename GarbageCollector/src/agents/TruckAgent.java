package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
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

	// método setup
	protected void setup() {
		// regista agente no DF
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getName());
		sd.setType(String.valueOf(this.getArguments()[0]));
		setEnabledO2ACommunication(true, 0);
		System.out.println("Created TruckAgent " + getName() + " with type: "
				+ sd.getType());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}

		// adiciona behaviour ciclico (ler mensagem)
		addBehaviour(new CyclicBehaviour(this) {
			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				ACLMessage msg = receive();
				if (msg != null) {
					if (msg.getPerformative() == ACLMessage.INFORM) {
						System.out
								.println(myAgent.getName() + " got INFORM from "
										+ msg.getSender().getName()
										+ ": "
										+ msg.getContent());
					}
				}
				else {
					block();
				}
			}
		});

		// adiciona behaviour ciclico (enviar mensagem)
		addBehaviour(new CyclicBehaviour(this) {
			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				// get an object from the O2A mailbox
				String messageContent = (String) myAgent.getO2AObject();

				// if we actually got one
				if (messageContent != null) {
					String[] args = messageContent.split("\\s+");
					String toSend = new String(args[1] + " " + args[2]);

					// pesquisa DF por agentes "ping"
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription sd1 = new ServiceDescription();
					sd1.setType(args[0]); // agentes que recebem mensagem s�o do
											// mesmo tipo que o lixo
					template.addServices(sd1);
					try {
						DFAgentDescription[] result = DFService.search(myAgent,
								template);
						ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
						for (int i = 0; i < result.length; ++i)
							msg.addReceiver(result[i].getName());
						msg.setContent(toSend);
						send(msg);
					} catch (FIPAException e) {
						e.printStackTrace();
					}
				} else {
					block();
				}
			}
		});

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
}

package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.util.Event;

public class TruckAgent extends Agent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Event event;
	public static final int CONTAINERCAPACITY = 1, MOVE = 2,
			INFORMOTHERTRUCKS = 3;

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

		// adiciona behaviour ciclico (ler mensagens)
		addBehaviour(new CyclicBehaviour(this) {
			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				ACLMessage msg = receive();
				if (msg != null) {
					switch (msg.getPerformative()) {
					case ACLMessage.INFORM:
						System.out.println(myAgent.getName()
								+ " got INFORM from "
								+ msg.getSender().getName() + ": "
								+ msg.getContent());
						break;
					case ACLMessage.CONFIRM:
						event.notifyProcessed(true);
						break;
					case ACLMessage.REFUSE:
						event.notifyProcessed(false);
						break;
					}
				} else {
					block();
				}
			}
		});

		// adiciona behaviour ciclico (enviar mensagens)
		addBehaviour(new CyclicBehaviour(this) {
			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				// get an object from the O2A mailbox
				String messageContent = (String) myAgent.getO2AObject();

				// if we actually got one
				if (messageContent != null) {
					String[] args = messageContent.split("\\s+");
					int requestType = Integer.parseInt(args[0]);
					String toSend = null;

					// pesquisa DF por agentes do tipo de lixo respectivo
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription sd1 = new ServiceDescription();
					TruckAgent myTruckAgent = (TruckAgent) this.myAgent;

					ACLMessage msg;

					switch (requestType) {
					case TruckAgent.CONTAINERCAPACITY:
						myTruckAgent.event = (Event) myAgent.getO2AObject();
						sd1.setType("World");
						msg = new ACLMessage(ACLMessage.REQUEST);
						// REQUEST_TYPE + X + Y
						toSend = args[0] + " " + args[1] + " " + args[2];
						break;
					case TruckAgent.INFORMOTHERTRUCKS:
						sd1.setType(args[1]); // agentes que recebem mensagem
												// s�o
												// do
												// mesmo tipo que o lixo
						msg = new ACLMessage(ACLMessage.INFORM);
						// X + Y
						toSend = args[2] + " " + args[3];
						break;
					case TruckAgent.MOVE:
						myTruckAgent.event = (Event) myAgent.getO2AObject();
						sd1.setType("World");
						msg = new ACLMessage(ACLMessage.REQUEST);
						// REQUEST_TYPE + TRUCK_NAME + X + Y
						toSend = args[0] + " " + args[1] + " " + args[2] + " "
								+ args[3];
						break;
					default:
						System.out
								.println("(TruckAgent) INVALID MESSAGE TYPE!");
						return;

					}
					template.addServices(sd1);
					try {
						DFAgentDescription[] result = DFService.search(myAgent,
								template);
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

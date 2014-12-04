package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.util.Event;

import java.awt.Point;

public class TruckAgent extends Agent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Event event;
	public static final int REQUEST_CONTAINER_CAPACITY = 1, REQUEST_MOVE = 2,
			INFORM_OTHER_TRUCKS = 3, INFORM_EMPTIED_CONTAINER = 4;

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
					String[] args = msg.getContent().split("\\s+");
					int informType = Integer.parseInt(args[0]);
					switch (informType) {
					case TruckAgent.INFORM_OTHER_TRUCKS:
						//disregard messages from self
						if(!msg.getSender().equals(myAgent.getAID()))
							System.out.println(myAgent.getName()
									+ " got INFORM from "
									+ msg.getSender().getName() + ": "
									+ msg.getContent());
						break;
					case WorldAgent.INFORM_CONTAINER_CAPACITY:
						Point expectedContainer = (Point) event.getParameter(1);
						if (expectedContainer.x == Integer.parseInt(args[1])
								&& expectedContainer.y == Integer
										.parseInt(args[2]))
							event.notifyProcessed(Integer.parseInt(args[3]));
						else
							System.out
									.println("(TruckAgent) GOT CAPACITY OF AN UNEXPECTED CONTAINER");
						event = null;
						break;
					case WorldAgent.CONFIRM_REFUSE_MOVE:
						if (msg.getPerformative() == ACLMessage.CONFIRM)
							event.notifyProcessed(true);
						else if (msg.getPerformative() == ACLMessage.REFUSE)
							event.notifyProcessed(false);
						event = null;
						break;
					default:
						System.out
								.println("(TruckAgent) GOT UNEXPECTED MESSAGE TYPE!");
						return;
					}
				} else {
					block();
				}
			}
		});

		// adiciona behaviour ciclico (enviar mensagens)
		addBehaviour(new CyclicBehaviour(this) {
			private static final long serialVersionUID = 1L;
			TruckAgent myTruckAgent = (TruckAgent) this.myAgent;

			@Override
			public void action() {
				// get an object from the O2A mailbox
				Event event = (Event) myAgent.getO2AObject();

				// if we actually got one
				if (event != null) {
					String messageContent = (String) event.getParameter(0);
					String[] args = messageContent.split("\\s+");
					int requestType = Integer.parseInt(args[0]);
					String toSend = null;

					// pesquisa DF por agentes do tipo de lixo respectivo
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription sd1 = new ServiceDescription();

					ACLMessage msg;

					switch (requestType) {
					case TruckAgent.REQUEST_CONTAINER_CAPACITY:
						myTruckAgent.event = event;
						sd1.setType("World");
						msg = new ACLMessage(ACLMessage.REQUEST);
						// REQUEST_TYPE + X + Y
						toSend = args[0] + " " + args[1] + " " + args[2];
						break;
					case TruckAgent.INFORM_OTHER_TRUCKS:
						sd1.setType(args[1]); // agentes que recebem mensagem
												// s�o
												// do
												// mesmo tipo que o lixo
						msg = new ACLMessage(ACLMessage.INFORM);
						// INFORM_TYPE + X + Y
						toSend = args[0] + " " + args[2] + " " + args[3];
						break;
					case TruckAgent.REQUEST_MOVE:
						myTruckAgent.event = event;
						sd1.setType("World");
						msg = new ACLMessage(ACLMessage.REQUEST);
						// REQUEST_TYPE + TRUCK_NAME + X + Y
						toSend = args[0] + " " + args[1] + " " + args[2] + " "
								+ args[3];
						break;
					case TruckAgent.INFORM_EMPTIED_CONTAINER:
						sd1.setType("World");
						msg = new ACLMessage(ACLMessage.INFORM);
						// REQUEST_TYPE + X + Y
						toSend = args[0] + " " + args[1] + " " + args[2];
						break;
					default:
						System.out
								.println("(TruckAgent) GOT UNEXPECTED MESSAGE TYPE!");
						return;

					}
					template.addServices(sd1);
					try {
						DFAgentDescription[] result = DFService.search(myAgent,
								template);
						for (int i = 0; i < result.length; ++i) {
							// TODO: nao mandar para proprio agente (caso do
							// inform com truck cheio)
							msg.addReceiver(result[i].getName());
						}
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

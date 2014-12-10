package agents;

import jade.core.AID;
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
	protected Event event, gotInformEvent;
	private AID worldAgent;
	public static final int REQUEST_CONTAINER_CAPACITY = 1, REQUEST_MOVE = 2,
			INFORM_OTHER_TRUCKS = 3, INFORM_EMPTIED_CONTAINER = 4,
			GOT_INFORM_EVENT = 7;

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
			// find world agent
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd1 = new ServiceDescription();
			sd1.setType("World");
			template.addServices(sd1);
			DFService.search(this, template);
			this.worldAgent = DFService.search(this, template)[0].getName();
		} catch (FIPAException e) {
			e.printStackTrace();
		}

		// adiciona behaviour ciclico (ler mensagens)
		addBehaviour(new CyclicBehaviour(this) {
			private static final long serialVersionUID = 1L;
			TruckAgent myTruckAgent = (TruckAgent) this.myAgent;

			@Override
			public void action() {
				ACLMessage msg = receive();
				if (msg != null) {
					String[] args = msg.getContent().split("\\s+");
					int informType = Integer.parseInt(args[0]);
					switch (informType) {
					case TruckAgent.INFORM_OTHER_TRUCKS:
						System.out.println(myAgent.getName()
								+ " got INFORM from "
								+ msg.getSender().getName() + ": "
								+ msg.getContent());
						myTruckAgent.gotInformEvent.notifyProcessed(new Point(
								Integer.parseInt(args[1]), Integer
										.parseInt(args[2])));
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
					try {
						String messageContent = (String) event.getParameter(0);
						String[] args = messageContent.split("\\s+");
						String toSend = null;

						ACLMessage msg = null;

						switch (event.getType()) {
						case TruckAgent.REQUEST_CONTAINER_CAPACITY:
							myTruckAgent.event = event;
							msg = new ACLMessage(ACLMessage.REQUEST);
							msg.addReceiver(worldAgent);
							// REQUEST_TYPE + X + Y
							toSend = event.getType() + " " + args[0] + " " + args[1];
							break;
						case TruckAgent.INFORM_OTHER_TRUCKS:
							// pesquisa DF por agentes do tipo de lixo
							// respectivo
							try {
								msg = new ACLMessage(ACLMessage.INFORM);
								DFAgentDescription template = new DFAgentDescription();
								ServiceDescription sd1 = new ServiceDescription();
								sd1.setType(args[0]);
								template.addServices(sd1);
								DFAgentDescription[] result = DFService.search(
										myAgent, template);
								for (int i = 0; i < result.length; ++i) {
									// dont send messages to self
									if (!myAgent.getAID().equals(
											result[i].getName()))
										msg.addReceiver(result[i].getName());
								}
								// INFORM_TYPE + X + Y
								toSend = event.getType() + " " + args[1] + " "
										+ args[2];
								break;
							} catch (FIPAException e) {
								e.printStackTrace();
								return;
							}
						case TruckAgent.REQUEST_MOVE:
							myTruckAgent.event = event;
							msg = new ACLMessage(ACLMessage.REQUEST);
							msg.addReceiver(worldAgent);
							// REQUEST_TYPE + TRUCK_NAME + X + Y
							toSend = event.getType() + " " + args[0] + " " + args[1]
									+ " " + args[2];
							break;
						case TruckAgent.INFORM_EMPTIED_CONTAINER:
							msg = new ACLMessage(ACLMessage.INFORM);
							msg.addReceiver(worldAgent);
							// REQUEST_TYPE + X + Y
							toSend = event.getType() + " " + args[0] + " " + args[1];
							break;
						default:
							System.out
									.println("(TruckAgent) GOT UNEXPECTED MESSAGE TYPE!");
							return;

						}
						msg.setContent(toSend);
						send(msg);
					} catch (IndexOutOfBoundsException e) {
						if (event.getType() == TruckAgent.GOT_INFORM_EVENT) { // regista
							// evento
							// (espera
							// por
							// informs)
							myTruckAgent.gotInformEvent = event;
							return;
						} else {
							e.printStackTrace();
						}
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

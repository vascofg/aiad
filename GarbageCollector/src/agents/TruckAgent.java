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
	public static final int REQUESTCONTAINERCAPACITY = 1, REQUESTMOVE = 2,
			INFORMOTHERTRUCKS = 3, INFORMEMPTIEDCONTAINER = 4;

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
					switch (msg.getPerformative()) {
					case ACLMessage.INFORM:
						switch (informType) {
						case TruckAgent.INFORMOTHERTRUCKS:
							break;
						case WorldAgent.INFORMCONTAINERCAPACITY:
							Point expectedContainer = (Point) event
									.getParameter(1);
							if (expectedContainer.x == Integer
									.parseInt(args[1])
									&& expectedContainer.y == Integer
											.parseInt(args[2]))
								event.notifyProcessed(Integer.parseInt(args[3]));
							else
								System.out
										.println("(TruckAgent) GOT CAPACITY OF AN UNEXPECTED CONTAINER");
							break;
						}
						break;
					case ACLMessage.CONFIRM:
						if (informType == WorldAgent.CONFIRMREFUSEMOVE)
							event.notifyProcessed(true);
						break;
					case ACLMessage.REFUSE:
						if (informType == WorldAgent.CONFIRMREFUSEMOVE)
							event.notifyProcessed(false);
						break;
					default:
						System.out
								.println("(TruckAgent) GOT UNEXPECTED MESSAGE!");
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

			@Override
			public void action() {
				TruckAgent myTruckAgent = (TruckAgent) this.myAgent;
				// get an object from the O2A mailbox
				myTruckAgent.event = (Event) myAgent.getO2AObject();
				String messageContent = (String) event.getParameter(0);

				// if we actually got one
				if (messageContent != null) {
					String[] args = messageContent.split("\\s+");
					int requestType = Integer.parseInt(args[0]);
					String toSend = null;

					// pesquisa DF por agentes do tipo de lixo respectivo
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription sd1 = new ServiceDescription();

					ACLMessage msg;

					switch (requestType) {
					case TruckAgent.REQUESTCONTAINERCAPACITY:
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
						// INFORM_TYPE + X + Y
						toSend = args[0] + " " + args[2] + " " + args[3];
						break;
					case TruckAgent.REQUESTMOVE:
						sd1.setType("World");
						msg = new ACLMessage(ACLMessage.REQUEST);
						// REQUEST_TYPE + TRUCK_NAME + X + Y
						toSend = args[0] + " " + args[1] + " " + args[2] + " "
								+ args[3];
						break;
					case TruckAgent.INFORMEMPTIEDCONTAINER:
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
						for (int i = 0; i < result.length; ++i)
						{
							//TODO: nao mandar para proprio agente (caso do inform com truck cheio)
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

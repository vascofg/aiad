package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

import java.awt.Point;

import main.GarbageCollector;
import map.Map;
import elements.containers.Container;
import elements.trucks.Truck;

public class WorldAgent extends Agent {
	private static final long serialVersionUID = 1L;
	public static final int INFORM_CONTAINER_CAPACITY = 5, CONFIRM_REFUSE_MOVE = 6;

	// método setup
	protected void setup() {
		// regista agente no DF
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getName());
		sd.setType("World");
		setEnabledO2ACommunication(true, 0);
		System.out.println("Created WorldAgent " + getName());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}

		// adiciona behaviour ciclico (ler request lixo)
		addBehaviour(new CyclicBehaviour(this) {
			private static final long serialVersionUID = 1L;

			@Override
			public void action() {
				ACLMessage msg = receive();
				if (msg != null) {
					String[] args = msg.getContent().split("\\s+");
					int requestType = Integer.parseInt(args[0]);
					Point point;
					Container c;
					ACLMessage sendMsg = null;
					String toSend = null;
					switch (requestType) {
					case TruckAgent.REQUEST_CONTAINER_CAPACITY:
						point = new Point(Integer.parseInt(args[1]),
								Integer.parseInt(args[2]));
						c = Map.getElement(Container.class, point,
								GarbageCollector.map.mapMatrix);
						sendMsg = new ACLMessage(ACLMessage.INFORM);
						// REQUEST_TYPE + X + Y + CAPACITY
						toSend = new String(WorldAgent.INFORM_CONTAINER_CAPACITY
								+ " " + point.x + " " + point.y + " "
								+ c.getUsedCapacity());
						break;
					case TruckAgent.REQUEST_MOVE:
						Truck truck = Map.getTruckByAgentName(args[1],
								GarbageCollector.map.trucks);
						point = new Point(Integer.parseInt(args[2]),
								Integer.parseInt(args[3]));
						boolean canMove = true;
						for (Truck t : GarbageCollector.map.trucks)
							if (t.getLocation().equals(point))
								canMove = false;
						if (canMove) {
							sendMsg = new ACLMessage(ACLMessage.CONFIRM);
							truck.moveTruck(point);
							GarbageCollector.frame.trucksComponent.repaint();
						} else {
							sendMsg = new ACLMessage(ACLMessage.REFUSE);
						}

						// REQUEST_TYPE
						toSend = Integer.toString(WorldAgent.CONFIRM_REFUSE_MOVE);
						break;
					case TruckAgent.INFORM_EMPTIED_CONTAINER:
						point = new Point(Integer.parseInt(args[1]),
								Integer.parseInt(args[2]));
						c = Map.getElement(Container.class, point,
								GarbageCollector.map.mapMatrix);
						c.emptyContainer();
						GarbageCollector.frame.mapComponent.repaint();
						return; // no response to send
					default:
						System.out
								.println("(WorldAgent) GOT UNEXPECTED MESSAGE TYPE!");
						return;
					}
					sendMsg.addReceiver(msg.getSender());
					sendMsg.setContent(toSend);
					send(sendMsg);
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

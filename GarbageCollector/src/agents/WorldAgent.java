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
import elements.Road;
import elements.containers.Container;
import elements.trucks.Truck;

public class WorldAgent extends Agent {
	private static final long serialVersionUID = 1L;
	public static final int INFORM_CONTAINER_CAPACITY = 5,
			CONFIRM_REFUSE_MOVE = 6;

	// método setup
	@Override
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
					Map map = Map.INSTANCE;
					Point point;
					Container c;
					ACLMessage sendMsg = null;
					String toSend = null;
					switch (requestType) {
					case TruckAgent.REQUEST_CONTAINER_CAPACITY:
						point = new Point(Integer.parseInt(args[1]),
								Integer.parseInt(args[2]));
						c = Map.getElement(Container.class, point,
								map.mapMatrix);
						sendMsg = new ACLMessage(ACLMessage.INFORM);
						// REQUEST_TYPE + X + Y + CAPACITY
						toSend = new String(
								WorldAgent.INFORM_CONTAINER_CAPACITY + " "
										+ point.x + " " + point.y + " "
										+ c.getUsedCapacity());
						break;
					case TruckAgent.REQUEST_MOVE:
						Truck truck = Map.getTruckByAgentName(args[1],
								map.trucks);
						point = new Point(Integer.parseInt(args[2]),
								Integer.parseInt(args[3]));
						int moveDir = Integer.parseInt(args[4]);
						boolean canMove = true;
						for (Truck t : map.trucks) {
							Road road = Map.getElement(Road.class,
									t.getLocation(), map.mapMatrix);
							if (t.getLocation().equals(point)
									&& (t.getMoveDirection() == moveDir || !road
											.isTwoWay())) {
								canMove = false;
								/*
								 * System.out.println(args[1] +
								 * " couldn't move! " + moveDir + "|" +
								 * t.getMoveDirection() + " Trying to go from "
								 * + truck.getLocation() + " to " + point);
								 */
							}
						}
						if (canMove) {
							sendMsg = new ACLMessage(ACLMessage.CONFIRM);
							Point from = truck.getLocation();
							truck.moveTruck(point);
							Point to = truck.getLocation();
							Road roadFrom = Map.getElement(Road.class, from,
									map.mapMatrix);
							Road roadTo = Map.getElement(Road.class, to,
									map.mapMatrix);
							roadTo.setTruck(truck);
							// TODO: evitar que apague no inicio (v�rios na
							// mesma road)
							roadFrom.removeTruck();
							GarbageCollector.frame.mapComponent.repaintTruck(
									from, to);
						} else {
							sendMsg = new ACLMessage(ACLMessage.REFUSE);
						}

						// REQUEST_TYPE
						toSend = Integer
								.toString(WorldAgent.CONFIRM_REFUSE_MOVE);
						break;
					case TruckAgent.INFORM_EMPTIED_CONTAINER:
						point = new Point(Integer.parseInt(args[1]),
								Integer.parseInt(args[2]));
						c = Map.getElement(Container.class, point,
								map.mapMatrix);
						c.emptyContainer();
						GarbageCollector.frame.mapComponent.repaintElement(c,
								point);
						return; // no response to send
					default:
						System.out
								.println("(WorldAgent) GOT UNEXPECTED MESSAGE TYPE ("
										+ requestType + ")!");
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
	@Override
	protected void takeDown() {
		// retira registo no DF
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
}

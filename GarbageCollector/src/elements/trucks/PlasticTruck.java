package elements.trucks;

import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.awt.image.BufferedImage;

import assets.Assets;
import elements.DrawableElement;
import elements.Road;

public class PlasticTruck extends Truck implements DrawableElement {

	public PlasticTruck(Road initialLocation, int capacity, ContainerController containerController, String agentName) throws StaleProxyException {
		super(initialLocation, capacity, containerController, agentName, Assets.PLASTIC);
	}

	@Override
	public BufferedImage getImg() {
		return Assets.plasticTruck;
	}

	@Override
	public int getType() {
		return Assets.PLASTIC;
	}
}

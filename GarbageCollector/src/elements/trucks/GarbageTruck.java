package elements.trucks;

import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.awt.image.BufferedImage;

import assets.Assets;
import elements.DrawableElement;
import elements.Road;

public class GarbageTruck extends Truck implements DrawableElement {

	public GarbageTruck(Road initialLocation, int capacity, ContainerController containerController, String agentName) throws StaleProxyException {
		super(initialLocation, capacity, containerController, agentName, Assets.GARBAGE);
	}

	@Override
	public BufferedImage getImg() {
		return Assets.garbageTruck;
	}
	
	@Override
	public int getType() {
		return Assets.GARBAGE;
	}

}

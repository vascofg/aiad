package elements.trucks;

import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.awt.image.BufferedImage;

import assets.Assets;
import elements.DrawableElement;
import elements.Road;

public class PaperTruck extends Truck implements DrawableElement {

	public PaperTruck(Road initialLocation, int capacity, ContainerController containerController, String agentName) throws StaleProxyException {
		super(initialLocation, capacity, containerController, agentName, Assets.PAPER);
	}

	@Override
	public BufferedImage getImg() {
		return Assets.paperTruck;
	}

	@Override
	public int getType() {
		return Assets.PAPER;
	}
}

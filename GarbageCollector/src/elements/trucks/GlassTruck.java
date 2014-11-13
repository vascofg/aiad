package elements.trucks;

import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.awt.image.BufferedImage;

import assets.Assets;
import elements.DrawableElement;
import elements.Road;

public class GlassTruck extends Truck implements DrawableElement {

	public GlassTruck(Road initialLocation, int capacity, ContainerController containerController, String agentName) throws StaleProxyException {
		super(initialLocation, capacity, containerController, agentName, Assets.GLASS);
	}

	@Override
	public BufferedImage getImg() {
		return Assets.glassTruck;
	}

	@Override
	public int getType() {
		return Assets.GLASS;
	}
}

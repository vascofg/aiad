package elements.trucks;

import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import assets.Assets;
import elements.DrawableElement;
import elements.MapElement;
import elements.Road;

public class PaperTruck extends Truck implements DrawableElement {

	public PaperTruck(Road initialLocation, int capacity,
			ContainerController containerController, String agentName,
			ArrayList<ArrayList<MapElement>> mapMatrix)
			throws StaleProxyException {
		super(initialLocation, capacity, containerController, agentName,
				Assets.PAPER, mapMatrix);
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

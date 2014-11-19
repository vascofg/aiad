package algorithms;

import java.util.ArrayList;
import java.util.List;

import assets.Assets;
import main.GarbageCollector;
import elements.Road;

public class Graph {
	private final List<Vertex> vertexes;
	private final List<Edge> edges;

	public Graph(List<Vertex> vertexes, List<Edge> edges) {
		this.vertexes = vertexes;
		this.edges = edges;
	}

	public Graph(List<Road> roads) {
		this.vertexes = new ArrayList<Vertex>();
		this.edges = new ArrayList<Edge>();
		for (Road road : roads) {
			this.vertexes.add(new Vertex(road.getID()));
		}
		for (Road road : roads) {
			Vertex currentVertex = getVertexByID(road.getID());
			List<Road> adjacentRoads = GarbageCollector.map
					.getAllAdjacentRoads(road);
			for (Road adjacent : adjacentRoads) {
				Vertex adjacentVertex = getVertexByID(adjacent.getID());
				this.edges.add(new Edge(road.getID() + "->" + adjacent.getID(),
						currentVertex, adjacentVertex, 1));
			}
		}
	}

	public List<Vertex> getVertexes() {
		return vertexes;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public Vertex getVertexByID(String ID) {
		for (Vertex vertex : vertexes) {
			if (vertex.getId().equals(ID))
				return vertex;
		}
		return null;
	}
}
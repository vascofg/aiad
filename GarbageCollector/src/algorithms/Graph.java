package algorithms;

import java.util.ArrayList;
import java.util.List;

import map.Map;
import elements.Road;

public class Graph {
	private final List<Vertex> vertexes;
	private final List<Edge> edges;

	public Graph(List<Vertex> vertexes, List<Edge> edges) {
		this.vertexes = vertexes;
		this.edges = edges;
	}

	public Graph(Map map) {
		this.vertexes = new ArrayList<Vertex>();
		this.edges = new ArrayList<Edge>();
		for (Road road : map.roads) {
			this.vertexes.add(new Vertex(road.getID()));
		}
		for (Road road : map.roads) {
			Vertex currentVertex = getVertexByID(road.getID());
			List<Road> adjacentRoads = Map.getAllAdjacentElements(Road.class,
					Map.findElement(Road.class, road, map.mapMatrix),
					map.mapMatrix);
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
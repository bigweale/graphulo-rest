package edu.mit.ll.graphulo.rest.objects;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author ti26350
 *
 */
public class Graph {

    private final long id;
    private final List<Vertex> vertices;
    private final List<LinkOLD> edges;

    public Graph(long id, List<Vertex> vertices, List<LinkOLD> edges) {
        this.id = id;
        this.vertices = vertices;
        this.edges = edges;
    }

	public long getId() {
        return id;
    }

    public List<Vertex> getNodes() {
        return vertices;
    }

    public List<LinkOLD> getLinks() {
        return edges;
    }

}

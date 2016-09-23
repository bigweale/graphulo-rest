package edu.mit.ll.graphulo.rest.objects;

import java.util.List;

/**
 * 
 * @author ti26350
 *
 */
public class Vertices {

    private final long id;
    private final List vertexList;

    public Vertices(long id, List vertexList) {
    	this.id = id;
    	this.vertexList = vertexList;
    }

    public List getLinks() {
        return vertexList;
    }

}

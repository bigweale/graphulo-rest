package edu.mit.ll.graphulo.rest.objects;

import java.util.List;

/**
 * 
 * @author ti26350
 *
 */
public class Edges {

    private final long id;
    private final List links;

    public Edges(long id, List links) {
    	this.id = id;
    	this.links = links;
    }

    public List getLinks() {
        return links;
    }

}

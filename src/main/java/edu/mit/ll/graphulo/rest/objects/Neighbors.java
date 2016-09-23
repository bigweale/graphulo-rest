package edu.mit.ll.graphulo.rest.objects;

import java.util.List;

/**
 * 
 * @author ti26350
 *
 */
public class Neighbors {


    private final long id;
    private final Vertex node;
    private final List parentList;
    private final List childrenList;
	    
    public Neighbors(long id, Vertex node, List parentList, List childrenList) {
    	this.id = id;
    	this.node = node;
    	this.parentList = parentList;
    	this.childrenList = childrenList;
    }

    public long getId() {
        return id;
    }
    
    public Vertex getVertex() {
        return node;
    }

    public List getParents() {
        return parentList;
    }
    
    public List getChildren() {
        return childrenList;
    }
	    
}


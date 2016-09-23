package edu.mit.ll.graphulo.rest.objects;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author ti26350
 *
 */
public class GraphSummary {

    private final long id;
    private final Long vertexCount;
    private final Long edgeCount;

    public GraphSummary(long id, long l, long m) {
        this.id = id;
        if(l >= 0) {
        	this.vertexCount = l;
        } else {
        	this.vertexCount = null;
        }
        if(m >= 0) {
        	this.edgeCount = m;
        } else {
        	this.edgeCount = null;
        }
    }
    

	public long getId() {
        return id;
    }

    public Long getVertexCount() {
        return vertexCount;
    }

    public Long getEdgeCount() {
        return edgeCount;
    }

}

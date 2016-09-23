package edu.mit.ll.graphulo.rest.objects;

/**
 * 
 * @author ti26350
 *
 */
public class Vertex {

    private final String name;
    private final String group;

    public Vertex(String name, String group) {
        this.name = name;
        this.group = group;
    }
    
    public Vertex(String name) {
        this.name = name;
        this.group = "";
    }
    
    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

}

package edu.mit.ll.graphulo.rest.objects;

public class StringLink {

    private final String source;
    private final String target;
    private final int weight;

    public StringLink(String source, String target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public int getWeight() {
        return weight;
    }

}

package edu.mit.ll.graphulo.rest.objects;

/**
 * 
 * @author ti26350
 *
 * @param <T>
 */
public class LinkOLD<T> {

    private final T source;
    private final T target;
    private final int weight;

    public LinkOLD(T source, T target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

    public T getSource() {
        return source;
    }

    public T getTarget() {
        return target;
    }

    public int getWeight() {
        return weight;
    }

}

package net.itransformers.topologyviewer.api.models;

/**
 * Created by pod on 10/15/16.
 */
public class Edge {
    String id;
    String label;
    Color color;
    Stroke stroke;

    String fromVertex;
    String toVertex;

    public Edge(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Stroke getStroke() {
        return stroke;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    public String getFromVertex() {
        return fromVertex;
    }

    public void setFromVertex(String fromVertex) {
        this.fromVertex = fromVertex;
    }

    public String getToVertex() {
        return toVertex;
    }

    public void setToVertex(String toVertex) {
        this.toVertex = toVertex;
    }
}

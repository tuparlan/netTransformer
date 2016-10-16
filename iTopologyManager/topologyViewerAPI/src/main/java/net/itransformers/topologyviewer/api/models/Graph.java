package net.itransformers.topologyviewer.api.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pod on 10/14/16.
 */
public class Graph {
    List<Vertex> vertices = new ArrayList<>();
    List<Edge> edges = new ArrayList<>();

    public List<Vertex> getVertices() {
        return vertices;
    }

    public void setVertices(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }
}

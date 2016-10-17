package net.itransformers.topologyviewer.discoveryresult;

import net.itransformers.idiscover.api.DiscoveryResult;
import net.itransformers.idiscover.api.models.graphml.GraphmlEdge;
import net.itransformers.idiscover.api.models.graphml.GraphmlGraph;
import net.itransformers.idiscover.api.models.graphml.GraphmlNode;
import net.itransformers.topologyviewer.api.TopologyViewer;
import net.itransformers.topologyviewer.api.models.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pod on 10/15/16.
 */
public class DiscoveryResultTopologyViewer implements TopologyViewer {
    private DiscoveryResult discoveryResult;
    private IconMapLoader iconMapLoader;
    private EdgeStrokeMapLoader edgeStrokeMapLoader;
    private EdgeColorMapLoader edgeColorMapLoader;

    public DiscoveryResultTopologyViewer(DiscoveryResult discoveryResult, IconMapLoader iconMapLoader, EdgeStrokeMapLoader edgeStrokeMapLoader, EdgeColorMapLoader edgeColorMapLoader) {
        this.discoveryResult = discoveryResult;
        this.iconMapLoader = iconMapLoader;
        this.edgeStrokeMapLoader = edgeStrokeMapLoader;
        this.edgeColorMapLoader = edgeColorMapLoader;
    }

    @Override
    public Graph getGraph(String version) {
        Graph graph = new Graph();
        GraphmlGraph graphmlGraph = discoveryResult.getNetwork(version);
        createVertices(graphmlGraph, graph);
        createEdges(graphmlGraph, graph);
        return graph;
    }

    private Graph createVertices(GraphmlGraph graphmlGraph, Graph graph) {
        List<GraphmlNode> verteces = graphmlGraph.getGraphmlNodes();
        Map<String, GraphmlNode> vertexMap = new HashMap<>();
        for (GraphmlNode graphmlNode: verteces){
            vertexMap.put(graphmlNode.getId(), graphmlNode);
        }
        Map<String, List<Icon>> iconMap = iconMapLoader.getIconsMap(vertexMap);
        graph.getVertices();
        for (GraphmlNode graphmlNode: graphmlGraph.getGraphmlNodes()){
            Vertex vertex = new Vertex(graphmlNode.getId());
            vertex.setLabel(graphmlNode.getLabel());
            vertex.setIcons(iconMap.get(graphmlNode.getId()));
            graph.getVertices().add(vertex);
        }
        return graph;
    }

    private void createEdges(GraphmlGraph graphmlGraph, Graph graph) {
        Map<String, GraphmlEdge> edgeMap = new HashMap<>();
        List<GraphmlEdge> edges = graphmlGraph.getGraphmlEdges();
        for (GraphmlEdge graphmlEdge: edges){
            edgeMap.put(graphmlEdge.getId(), graphmlEdge);
        }
        Map<String, Stroke> edgeStrokeMap = edgeStrokeMapLoader.getEdgeStrokeMap(edgeMap);
        Map<String, Color> edgeColorMap = edgeColorMapLoader.getEdgeColorMap(edgeMap);
        for (GraphmlEdge graphmlEdge: graphmlGraph.getGraphmlEdges()){
            Edge edge = new Edge(graphmlEdge.getId());
            edge.setLabel(graphmlEdge.getLabel());
            edge.setColor(edgeColorMap.get(graphmlEdge.getId()));
            edge.setStroke(edgeStrokeMap.get(graphmlEdge.getId()));
            edge.setFromVertex(graphmlEdge.getFromNode());
            edge.setToVertex(graphmlEdge.getToNode());
            graph.getEdges().add(edge);
        }
    }
}

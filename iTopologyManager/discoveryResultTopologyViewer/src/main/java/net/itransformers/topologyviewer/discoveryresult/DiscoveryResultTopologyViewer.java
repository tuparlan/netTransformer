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
    protected DiscoveryResult discoveryResult;
    protected IconMapLoader iconMapLoader;
    protected EdgeStrokeMapLoader edgeStrokeMapLoader;
    protected EdgeColorMapLoader edgeColorMapLoader;
    protected VertexFilter vertexFilter;
    protected EdgeFilter edgeFilter;

    public DiscoveryResultTopologyViewer(DiscoveryResult discoveryResult,
                                         IconMapLoader iconMapLoader,
                                         EdgeStrokeMapLoader edgeStrokeMapLoader,
                                         EdgeColorMapLoader edgeColorMapLoader,
                                         VertexFilter vertexFilter,
                                         EdgeFilter edgeFilter) {
        this.discoveryResult = discoveryResult;
        this.iconMapLoader = iconMapLoader;
        this.edgeStrokeMapLoader = edgeStrokeMapLoader;
        this.edgeColorMapLoader = edgeColorMapLoader;
        this.vertexFilter = vertexFilter;
        this.edgeFilter = edgeFilter;
    }

    @Override
    public Graph getGraph(String version) {
        return this.getGraph(version, null, null);
    }
    @Override
    public Graph getGraph(String version, String vertexFilterName, String edgeFilterName) {
        Graph graph = new Graph();
        GraphmlGraph graphmlGraph = discoveryResult.getNetwork(version);
        Map<String, GraphmlNode> graphmlNodeMap = createGraphmlNodeMap(graphmlGraph);
        Map<String, GraphmlEdge> graphmlEdgeMap = createGraphmlEdgeMap(graphmlGraph);
        graphmlNodeMap = vertexFilter.filter(vertexFilterName, graphmlNodeMap);
        graphmlEdgeMap = edgeFilter.filter(edgeFilterName, graphmlEdgeMap);
        createVertices(graphmlNodeMap, graph);
        createEdges(graphmlEdgeMap, graph);
        return graph;
    }

    protected Map<String, GraphmlNode> createGraphmlNodeMap(GraphmlGraph graphmlGraph) {
        List<GraphmlNode> verteces = graphmlGraph.getGraphmlNodes();
        Map<String, GraphmlNode> nodeMap = new HashMap<>();
        for (GraphmlNode graphmlNode: verteces){
            nodeMap.put(graphmlNode.getId(), graphmlNode);
        }
        return nodeMap;
    }

    protected Map<String, GraphmlEdge> createGraphmlEdgeMap(GraphmlGraph graphmlGraph) {
        List<GraphmlEdge> verteces = graphmlGraph.getGraphmlEdges();
        Map<String, GraphmlEdge> edgeMap = new HashMap<>();
        for (GraphmlEdge graphmlNode: verteces){
            edgeMap.put(graphmlNode.getId(), graphmlNode);
        }
        return edgeMap;
    }

    protected Graph createVertices(Map<String, GraphmlNode> graphmlNodeMap, Graph graph) {
        Map<String, List<Icon>> iconMap = iconMapLoader.getIconsMap(graphmlNodeMap);
        graph.getVertices();
        for (GraphmlNode graphmlNode: graphmlNodeMap.values()){
            Vertex vertex = new Vertex(graphmlNode.getId());
            vertex.setLabel(graphmlNode.getLabel());
            vertex.setIcons(iconMap.get(graphmlNode.getId()));
            graph.getVertices().add(vertex);
        }
        return graph;
    }

    protected void createEdges(Map<String, GraphmlEdge> graphmlEdgeMap, Graph graph) {
        Map<String, Stroke> edgeStrokeMap = edgeStrokeMapLoader.getEdgeStrokeMap(graphmlEdgeMap);
        Map<String, Color> edgeColorMap = edgeColorMapLoader.getEdgeColorMap(graphmlEdgeMap);
        for (GraphmlEdge graphmlEdge: graphmlEdgeMap.values()){
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

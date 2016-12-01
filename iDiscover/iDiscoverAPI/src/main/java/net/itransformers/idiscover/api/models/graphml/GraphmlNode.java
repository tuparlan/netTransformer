package net.itransformers.idiscover.api.models.graphml;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by niau on 8/17/16.
 */
public class GraphmlNode {

    private String id;



    private String label;

    private Map<String, String> graphmlNodeData = new HashMap<>();

    private Map<String, String> graphmlNodePorts = new HashMap<>();

    public GraphmlNode(String id) {
        this.id = id;
    }

    public GraphmlNode(String id, String label) {
        this.id = id;
        this.label = label;
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

    public Map<String, String> getGraphmlNodeData() {
        return graphmlNodeData;
    }

    public void setGraphmlNodeData(Map<String, String> graphmlNodeData) {
        this.graphmlNodeData = graphmlNodeData;
    }

    public Map<String, String> getGraphmlNodePorts() {
        return graphmlNodePorts;
    }

    public void setGraphmlNodePorts(Map<String, String> graphmlNodePorts) {
        this.graphmlNodePorts = graphmlNodePorts;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GraphmlNode node = (GraphmlNode) o;

        return id.equals(node.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}

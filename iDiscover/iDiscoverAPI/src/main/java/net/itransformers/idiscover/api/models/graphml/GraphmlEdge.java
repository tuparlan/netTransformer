package net.itransformers.idiscover.api.models.graphml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by niau on 8/22/16.
 */
public class GraphmlEdge {

        private String id;
        private String label;
        private String fromNode;
        private String toNode;

        private Map<String, String> graphmlEdgeData = new HashMap<>();

    public GraphmlEdge(String id) {
        this.id = id;
    }

    public GraphmlEdge(String id, String label, String fromNode, String toNode) {
            this.id = id;
            this.label = label;
            this.fromNode = fromNode;
            this.toNode = toNode;
        }

    public String getFromNode() {
        return fromNode;
    }

    public void setFromNode(String fromNode) {
        this.fromNode = fromNode;
    }

    public String getToNode() {
        return toNode;
    }

    public void setToNode(String toNode) {
        this.toNode = toNode;
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

    public Map<String, String> getGraphmlEdgeData() {
        return graphmlEdgeData;
    }

    public void setGraphmlEdgeData(Map<String, String> graphmlEdgeData) {
        this.graphmlEdgeData = graphmlEdgeData;
    }

    @Override
    public String toString() {
        return "GraphmlEdge{" +
                "id='" + id + '\'' +
                ", label='" + label + '\'' +
                ", fromNode='" + fromNode + '\'' +
                ", toNode='" + toNode + '\'' +
                ", graphmlEdgeData=" + graphmlEdgeData +
                '}';
    }
}

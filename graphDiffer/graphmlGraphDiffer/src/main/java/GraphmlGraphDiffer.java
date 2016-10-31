import net.itransformers.idiscover.api.models.graphml.GraphmlEdge;
import net.itransformers.idiscover.api.models.graphml.GraphmlGraph;
import net.itransformers.idiscover.api.models.graphml.GraphmlNode;

import java.util.*;

/**
 * Created by niau on 10/19/16.
 */
public class GraphmlGraphDiffer implements GraphmlDiffer {

    HashSet<String> ignoredGraphmlNodeData;
    HashSet<String> ignoredGraphmlEdgeData;

    public GraphmlGraphDiffer(HashSet<String> ignoredGraphmlNodeData, HashSet<String> ignoredGraphmlEdgeData) {
        this.ignoredGraphmlNodeData = ignoredGraphmlNodeData;
        this.ignoredGraphmlEdgeData = ignoredGraphmlEdgeData;
    }

    @Override
    public GraphmlGraph doDiff(GraphmlGraph graphA, GraphmlGraph graphB) {

        Map<String,GraphmlNode> graphANodes = createGraphmlNodeMap(graphA);

        Map<String,GraphmlNode> graphBNodes = createGraphmlNodeMap(graphB);

        Map<String,GraphmlEdge> graphAedges = createGraphmlEdgeMap(graphA);
        Map<String,GraphmlEdge> graphBedges = createGraphmlEdgeMap(graphB);

        GraphmlGraph graphC = new GraphmlGraph();

        graphC.setGraphmlNodes(diffNodes(graphANodes,graphBNodes));
        graphC.setGraphmlEdges(diffEdges(graphAedges,graphBedges));


        return graphC;
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


    protected ArrayList<GraphmlNode> diffNodes(Map<String,GraphmlNode> graphANodes,Map<String,GraphmlNode> graphBNodes){


        HashMap<String,GraphmlNode> allNodes = new HashMap<>();

        allNodes.putAll(graphANodes);
        allNodes.putAll(graphBNodes);



        ArrayList<GraphmlNode> graphmlCNodes = new ArrayList<>();

        for (Map.Entry<String,GraphmlNode> nodeEntry : allNodes.entrySet()) {

            Map<String,String> graphmlNodeData = nodeEntry.getValue().getGraphmlNodeData();
            String nodeId = nodeEntry.getValue().getId();

            GraphmlNode node1 = new GraphmlNode(nodeEntry.getValue().getId(),nodeEntry.getValue().getLabel());


           //Node is present in A but not in B
            if (graphANodes.get(nodeId)!=null && graphBNodes.get(nodeId)==null){
                graphmlNodeData.put("diff","removed");
                node1.setGraphmlNodeData(graphmlNodeData);
            //Node is present in B but not in A
            }else if (graphANodes.get(nodeId)==null && graphBNodes.get(nodeId)!=null ){
                graphmlNodeData.put("diff","added");
                node1.setGraphmlNodeData(graphmlNodeData);
            //Node is present in A and in B
            }else if (graphANodes.get(nodeId)!=null && graphBNodes.get(nodeId)!=null) {

                GraphmlNode graphANode = graphANodes.get(nodeId);
                GraphmlNode graphBNode = graphBNodes.get(nodeId);

                Map<String,String> graphCnodeMetadata = diffMetaData(graphANode.getGraphmlNodeData(), graphBNode.getGraphmlNodeData());

                node1.setGraphmlNodeData(graphCnodeMetadata);

            //Node is not present anywhere?
            } else {
                System.out.println("Strange case node "+nodeId+" has not been found in A nodes and B nodes");
            }
            graphmlCNodes.add(node1);

        }

       return graphmlCNodes;
    }



    protected ArrayList<GraphmlEdge> diffEdges(Map<String,GraphmlEdge> graphAEdges,Map<String,GraphmlEdge> graphBedges){


        HashMap<String,GraphmlEdge> alledges = new HashMap<>();

        alledges.putAll(graphAEdges);
        alledges.putAll(graphBedges);



        ArrayList<GraphmlEdge> graphmlCNodes = new ArrayList<>();

        for (Map.Entry<String,GraphmlEdge> edgeEntry : alledges.entrySet()) {

            Map<String,String> graphmlEdgeData = edgeEntry.getValue().getGraphmlEdgeData();
            String edgeId = edgeEntry.getValue().getId();
            String from = edgeEntry.getValue().getFromNode();
            String to = edgeEntry.getValue().getToNode();

            GraphmlEdge graphmlEdge = new GraphmlEdge(edgeEntry.getValue().getId(),edgeEntry.getValue().getLabel(),from,to);


            //Node is present in A but not in B
            if (graphAEdges.get(edgeId)!=null && graphBedges.get(edgeId)==null){
                graphmlEdgeData.put("diff", "removed");
                graphmlEdge.setGraphmlEdgeData(graphmlEdgeData);
                //Node is present in B but not in A
            }else if (graphAEdges.get(edgeId)==null && graphBedges.get(edgeId)!=null ){
                graphmlEdgeData.put("diff", "added");
                graphmlEdge.setGraphmlEdgeData(graphmlEdgeData);
                //Node is present in A and in B
            }else if (graphAEdges.get(edgeId)!=null && graphBedges.get(edgeId)!=null) {

                GraphmlEdge graphAEdge = graphAEdges.get(edgeId);
                GraphmlEdge graphBEdge = graphBedges.get(edgeId);

                Map<String,String> graphCEdgeMetadata = diffMetaData(graphAEdge.getGraphmlEdgeData(), graphBEdge.getGraphmlEdgeData());

                graphmlEdge.setGraphmlEdgeData(graphCEdgeMetadata);

                //Node is not present anywhere?
            } else {
                System.out.println("Strange case node "+edgeId+" has not been found in A nodes and B nodes");
            }
            graphmlCNodes.add(graphmlEdge);

        }

        return graphmlCNodes;
    }



    protected Map<String,String> diffMetaData(Map<String, String> graphAMetaData, Map<String, String> graphBMetaData){


        Map<String,String> graphCMetaData = new HashMap<>();

        
        Map<String, String> graphCAddedMetaData = new HashMap<>();
        Map<String, String> graphCRemovedMetaData = new HashMap<>();
        Map<String, String> graphCUnchangedMetaData = new HashMap<>();
        Map<String, String> graphCChangedMetaData = new HashMap<>();

        StringBuilder diffs = new StringBuilder();

        for (Map.Entry<String, String> entry : graphAMetaData.entrySet()) {
            String keyA = entry.getKey();
            String valueA = entry.getValue();

            String valueB = graphBMetaData.get(keyA);

            //Exists in graphANode but does not in graphBNode
            if (valueB == null) {
                graphCRemovedMetaData.put(keyA, valueA);

            }
            //Exists in graphANode and in graphBNode and the value is the same!
            else if (valueA.equals(valueB)) {
                graphCUnchangedMetaData.put(keyA, valueB);
            } else {
                //Exists in graphANode and in graphBNode, value has become valueB
                graphCChangedMetaData.put(keyA, valueB);
            }

        }

        for (Map.Entry<String, String> entry : graphBMetaData.entrySet()) {
            String keyB = entry.getKey();
            String valueB = entry.getValue();
            String valueA = graphAMetaData.get(keyB);
            //Exists in graphBNode but does not in graphANode

            if (valueA==null){
                graphCAddedMetaData.put(keyB,valueB);
            }

        }
        boolean changedFlag = false;
        if (graphCRemovedMetaData.size()!=0){
            graphCMetaData.putAll(graphCRemovedMetaData);
            diffs.append("Properties removed:\n");
            for (Map.Entry<String, String> stringStringEntry : graphCRemovedMetaData.entrySet()) {
                diffs.append(stringStringEntry.getKey()).append(": ").append(stringStringEntry.getValue()).append("\n");
            }
            changedFlag=true;

        }

        if (graphCAddedMetaData.size()!=0) {
            graphCMetaData.putAll(graphCAddedMetaData);
            diffs.append("Properties added:\n");
            for (Map.Entry<String, String> stringStringEntry : graphCAddedMetaData.entrySet()) {
                diffs.append(stringStringEntry.getKey()).append(": ").append(stringStringEntry.getValue()).append("\n");
            }
            changedFlag=true;
        }

        if (graphCChangedMetaData.size()!=0){
            graphCMetaData.putAll(graphCChangedMetaData);
            diffs.append("Properties changed:\n");
            for (Map.Entry<String, String> stringStringEntry : graphCChangedMetaData.entrySet()) {
                diffs.append(stringStringEntry.getKey()).append(": ").append(stringStringEntry.getValue()).append("\n");
            }
            changedFlag=true;
        }


        if (changedFlag) {
            graphCMetaData.put("diff", "changed");
            graphCMetaData.put("diffs", diffs.toString());
        }

        graphCMetaData.putAll(graphCUnchangedMetaData);

       return graphCMetaData;
    }

}

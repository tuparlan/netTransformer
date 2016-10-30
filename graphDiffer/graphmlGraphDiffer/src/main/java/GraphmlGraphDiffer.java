import net.itransformers.idiscover.api.models.graphml.GraphmlGraph;

import java.util.HashSet;

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
        GraphmlGraph graphC = new GraphmlGraph();

        return graphC;
    }
}

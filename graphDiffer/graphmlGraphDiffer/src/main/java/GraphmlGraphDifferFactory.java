import java.util.HashSet;
import java.util.Map;

/**
 * Created by niau on 10/19/16.
 */
public class GraphmlGraphDifferFactory implements GraphmlDifferFactory {


    @Override
    public GraphmlGraphDiffer createGraphmlDiffer(Map<String,Object> properties) {

        HashSet<String> ignoredGraphmlNodeData = (HashSet<String>) properties.get("ignoredGraphmlNodeData");
        HashSet<String> ignoredGraphmlEdgeData = (HashSet<String>) properties.get("ignoredGraphmlEdgeData");

        return new GraphmlGraphDiffer(ignoredGraphmlNodeData,ignoredGraphmlEdgeData);
    }
}

package net.itransformers.ws.versionDiff;

import net.itransformers.graphmlDiffAPI.GraphmlDiffer;
import net.itransformers.graphmlDiffAPI.GraphmlDifferFactory;
import net.itransformers.idiscover.api.VersionManager;
import net.itransformers.idiscover.api.VersionManagerFactory;
import net.itransformers.idiscover.api.models.graphml.GraphmlGraph;
import net.itransformers.utils.ProjectConstants;
import net.itransformers.utils.graphmlRenderer.GraphmlRenderer;
import net.itransformers.xmlNodeDataProvider.XmlNodeDataProvider;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by niau on 1/5/17.
 */

@Controller
@RequestMapping(value="/diff")

public class VersionDiffController {
    final Logger logger = LoggerFactory.getLogger(VersionDiffController.class);

    @Resource(name="graphmlDifferFactory")
    private GraphmlDifferFactory graphmlDifferFactory;

    @Resource(name="xmlNodeDataProvider")
    private XmlNodeDataProvider xmlNodeDataProvider;


    @Resource(name="versionManagerFactory")
    private VersionManagerFactory versionManagerFactory;


    @Resource(name="projectPath")
    private String projectPath;

    @RequestMapping(value = "/", method= RequestMethod.POST)
    @ResponseBody
    public void doDiff(@RequestParam(value="versionA", required=true) String versionA,
        @RequestParam(value="versionB", required = true) String versionB) {

        HashMap<String,Object> props = new HashMap<>();

        HashSet<String>  ignoredEdgeMetaData= new HashSet<>();
        HashSet<String> ignoredNodeMetaData= new HashSet<>();

        props.put("ignoredGraphmlEdgeData",ignoredEdgeMetaData);
        props.put("ignoredGraphmlNodeData",ignoredNodeMetaData);

        GraphmlDiffer graphmlDiffer = graphmlDifferFactory.createGraphmlDiffer(props);


        GraphmlGraph graphA =   xmlNodeDataProvider.getNetwork(versionA);
        GraphmlGraph graphB = xmlNodeDataProvider.getNetwork(versionB);

        GraphmlGraph graphC = graphmlDiffer.doDiff(graphA, graphB);

        HashMap<String,Object> params = new HashMap<>();
        params.put("version",versionA+"-"+versionB);
        params.put("nodes",graphC.getGraphmlNodes());
        params.put("edges",graphC.getGraphmlEdges());
        params.put("graphDirection","undirected");
        params.put("projectPath",projectPath);
        params.put("velocityTemplate", "graphmlGraphDiffer/conf/velocity/snmpGraphmlTemplate.vm");
        params.put("networkGraphmlPath", ProjectConstants.undirectedGraphmlDirName);

        createNewDiffVersion(params);



    }

    private void  createNewDiffVersion(HashMap<String,Object> params){

        Map<String, String> props = new HashMap<>();
        props.put("projectPath", projectPath);
        VersionManager versionManager = versionManagerFactory.createVersionManager("dir", props);

        versionManager.createVersion((String) params.get("version"));

        String graphmlGraph = null;
        try {
            graphmlGraph = renderGraphmlGraph(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            File graphmlDir= new File(params.get("projectPath")+File.separator+params.get("version")+File.separator+params.get("networkGraphmlPath"));
            if (!graphmlDir.exists()) {
                graphmlDir.mkdir();
            }
            FileUtils.writeStringToFile(new File(graphmlDir + File.separator + ProjectConstants.networkGraphmlFileName), graphmlGraph);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private String renderGraphmlGraph(HashMap<String,Object> params) throws Exception {
        String velocityTemplate = (String) params.get("velocityTemplate");
        GraphmlRenderer graphmlRenderer = new GraphmlRenderer();
        return graphmlRenderer.render(velocityTemplate, params);


    }


}

package net.itransformers.graphmlGraphDiffer;

import net.itransformers.idiscover.api.models.graphml.GraphmlGraph;
import net.itransformers.utils.graphmlRenderer.GraphmlRenderer;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by niau on 12/24/16.
 */
public class VersionDiffCreator {

    GraphmlRenderer graphmlRenderer;
    String velocityTemplate;
    String versionPath;
    HashMap<String,Object> params;

    static Logger logger = Logger.getLogger(VersionDiffCreator.class);

    private static VersionDiffCreator ourInstance = new VersionDiffCreator();

    public static VersionDiffCreator getInstance() {
        return ourInstance;
    }

    private VersionDiffCreator() {
    }

    public VersionDiffCreator(GraphmlRenderer graphmlRenderer, String velocityTemplate, HashMap<String,Object> params) {
        this.graphmlRenderer = graphmlRenderer;
        this.velocityTemplate = velocityTemplate;
        this.params = params;
    }

    public void  createNewVersion(GraphmlGraph graph){

        String graphmlGraph = null;
        try {
             graphmlGraph = renderGraphmlGraph(params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FileUtils.writeStringToFile(new File(versionPath), graphmlGraph);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private String renderGraphmlGraph(HashMap<String,Object> params) throws Exception {

        return graphmlRenderer.render(velocityTemplate, params);


    }
}

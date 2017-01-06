package net.itransformers.graphmlGraphDiffer;

import net.itransformers.idiscover.api.VersionManager;
import net.itransformers.idiscover.api.VersionManagerFactory;
import net.itransformers.utils.ProjectConstants;
import net.itransformers.utils.graphmlRenderer.GraphmlRenderer;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by niau on 12/24/16.
 */
public class VersionCreator {

    VersionManagerFactory versionManagerFactory;

    HashMap<String,Object> params;

    static Logger logger = Logger.getLogger(VersionCreator.class);



    private static VersionCreator ourInstance = new VersionCreator();

    public static VersionCreator getInstance() {
        return ourInstance;
    }

    private VersionCreator() {
    }

    public VersionCreator(VersionManagerFactory versionManagerFactory, HashMap<String, Object> params) {
        this.params = params;
        this.versionManagerFactory = versionManagerFactory;
    }

    public void  createNewDiffVersion(){

        Map<String, String> props = new HashMap<>();
        props.put("projectPath", (String) params.get("projectPath"));
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
            FileUtils.writeStringToFile(new File(graphmlDir+File.separator+ ProjectConstants.networkGraphmlFileName), graphmlGraph);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private String renderGraphmlGraph(HashMap<String,Object> params) throws Exception {
        String velocityTemplate = (String) params.get("velocityTemplate");
        GraphmlRenderer graphmlRenderer = new GraphmlRenderer();
        return graphmlRenderer.render(velocityTemplate, params);


    }

    public void  deleteDiffedVersion(){

        Map<String, String> props = new HashMap<>();
        props.put("projectPath", (String) params.get("projectPath"));
        VersionManager versionManager = versionManagerFactory.createVersionManager("dir", props);
        versionManager.deleteVersion((String) params.get("version"));

    }
}

package net.itransformers.topologyviewer.config.xml;

import net.itransformers.topologyviewer.config.TopologyViewerConfigManager;
import net.itransformers.topologyviewer.config.TopologyViewerConfigManagerFactory;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by vasko on 10/13/2016.
 */
public class XmlTopologyViewerConfigManagerFactory implements TopologyViewerConfigManagerFactory {
    Logger logger = Logger.getLogger(XmlTopologyViewerConfigManagerFactory.class);

    @Override
    public TopologyViewerConfigManager createTopologyViewerConfigManager(String type, Map<String, String> properties) {
        String projectPath = properties.get("projectPath");
        if (projectPath == null) {
            throw new IllegalArgumentException("Missing projectPath parameter");
        }
        String configName = properties.get("name");
        if (configName == null) {
            throw new IllegalArgumentException("Missing configName parameter");
        }
        String configRelFileName;
        if (configName.equals("bgpPeeringMap")) {
            configRelFileName = "topologyViewer/conf/bgpPeeringMap/viewer-config.xml";
        } else if (configName.equals("freeGraph")){
            configRelFileName = "topologyViewer/conf/freeGraph/viewer-config.xml";
        } else if (configName.equals("discovery")){
            configRelFileName = "topologyViewer/discovery/viewer-config.xml";
        } else {
            throw new IllegalArgumentException("Invalid config name: "+ configName);
        }
        File file = new File(projectPath + File.separator + configRelFileName);

        XmlTopologyViewerConfigManager xmlTopologyViewerConfigManager = new XmlTopologyViewerConfigManager(file);
        try {
            xmlTopologyViewerConfigManager.load();
        } catch (IOException | JAXBException e ) {
            logger.error(e.getMessage(), e);
        }
        return xmlTopologyViewerConfigManager;
    }
}

package net.itransformers.topologyviewer.config.xml;

import net.itransformers.topologyviewer.config.TopologyViewerConfigManager;
import net.itransformers.topologyviewer.config.TopologyViewerConfigManagerFactory;
import net.itransformers.utils.ProjectConstants;
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
        String projectConfigType = properties.get("projectType");
        //TODO it always is equal to null. Has to be fixed.
        if (projectConfigType == null) {
            projectConfigType = ProjectConstants.snmpProjectType;
            //throw new IllegalArgumentException("Missing projectConfigType parameter");
        }

        String configRelFileName;
//        if (projectConfigType.equals(ProjectConstants.bgpDiscovererProjectType)) {
//            configRelFileName = "xmlTopologyViewerConfig/conf/xml/bgpPeeringMap/viewer-config.xml";
//        } else if (projectConfigType.equals(ProjectConstants.freeGraphProjectType)){
//            configRelFileName = "xmlTopologyViewerConfig/conf/xml/freeGraph/viewer-config.xml";
//        } else if (projectConfigType.equals(ProjectConstants.snmpProjectType)){
//            configRelFileName = "xmlTopologyViewerConfig/conf/xml/ipNetworkDiscovery/viewer-config.xml";
//        } else {
//            throw new IllegalArgumentException("Invalid config name: "+ projectConfigType);
//        }

        configRelFileName = "xmlTopologyViewerConfig/conf/xml/"+ projectConfigType+"/viewer-config.xml";
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

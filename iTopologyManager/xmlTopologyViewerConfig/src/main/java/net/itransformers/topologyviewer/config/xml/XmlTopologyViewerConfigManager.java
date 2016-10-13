package net.itransformers.topologyviewer.config.xml;

import net.itransformers.topologyviewer.config.TopologyViewerConfigManager;
import net.itransformers.topologyviewer.config.models.EdgeColorType;
import net.itransformers.topologyviewer.config.models.EdgeStrokeType;
import net.itransformers.topologyviewer.config.models.IconType;
import net.itransformers.topologyviewer.config.models.TopologyViewerConfType;
import net.itransformers.utils.JaxbMarshalar;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.List;

/**
 * Created by vasko on 10/13/2016.
 */
public class XmlTopologyViewerConfigManager implements TopologyViewerConfigManager {
    static Logger logger = Logger.getLogger(XmlTopologyViewerConfigManager.class);
    private TopologyViewerConfType topologyViewerConfType;
    private File file;

    public XmlTopologyViewerConfigManager(File file) {
        this.file = file;
    }

    public void load() throws IOException, JAXBException {
        FileInputStream is = null;
        try {
            logger.info("Reading resource config from: "+file.getAbsoluteFile());
            is = new FileInputStream(file);
            this.topologyViewerConfType = JaxbMarshalar.unmarshal(TopologyViewerConfType.class, is);
        } finally {
            if (is != null) is.close();
        }
    }

    public void save() {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            JaxbMarshalar.marshal(this.topologyViewerConfType, os, "topology-viewer-conf");
        } catch (FileNotFoundException e) {
            throw new TopologyViewerConfigManagerException(e.getMessage(), e);
        } catch (JAXBException e) {
            e.printStackTrace();
        } finally {
            if (os != null) try {
                os.close();
            } catch (IOException e) {}
        }
    }


    @Override
    public TopologyViewerConfType getTopologyViewerConfType(){
        // TODO make a clone
        return this.topologyViewerConfType;
    }

    @Override
    public List<IconType> getIcons() {
        return this.topologyViewerConfType.getIcon();
    }

    @Override
    public List<EdgeStrokeType> getEdgeStrokes() {
        return this.topologyViewerConfType.getEdgeStroke();
    }

    @Override
    public List<EdgeColorType> getEdgeColors() {
        return this.topologyViewerConfType.getEdgeColor();
    }
}
